package jhi.germinate.server.util.async;

import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.enums.DataExportJobsStatus;
import jhi.germinate.server.database.codegen.routines.ExportPassportData;
import jhi.germinate.server.database.codegen.tables.pojos.*;
import jhi.germinate.server.database.codegen.tables.records.*;
import jhi.germinate.server.database.pojo.AdditionalExportFormat;
import jhi.germinate.server.resource.ResourceUtils;
import jhi.germinate.server.resource.pedigrees.PedigreeResource;
import jhi.germinate.server.util.CollectionUtils;
import org.jooq.*;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import java.io.*;
import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.nio.file.Files;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static jhi.germinate.server.database.codegen.tables.DataExportJobs.*;
import static jhi.germinate.server.database.codegen.tables.Datasetmembers.*;
import static jhi.germinate.server.database.codegen.tables.Datasets.*;
import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;
import static jhi.germinate.server.database.codegen.tables.Groupmembers.*;
import static jhi.germinate.server.database.codegen.tables.Mapdefinitions.*;
import static jhi.germinate.server.database.codegen.tables.Markers.*;
import static jhi.germinate.server.database.codegen.tables.Synonyms.*;
import static jhi.germinate.server.database.codegen.tables.ViewTablePedigrees.*;

public class PedigreeExporter
{
	private static final String            CRLF              = "\r\n";
	private static final SimpleDateFormat  SDF               = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	private              File              folder;
	private              Boolean           includeAttributes = false;
	private              File              zipFile;
	private              Set<String>       germplasm;
	private              DataExportJobs exportJob;
	private              Datasets          dataset;

	private final Instant start;

	public PedigreeExporter()
	{
		start = Instant.now();
	}

	public static void main(String[] args)
		throws IOException, SQLException
	{
		PedigreeExporter exporter = new PedigreeExporter();
		Database.init(args[0], args[1], args[2], args[3], args[4], false);
		Integer jobId = Integer.parseInt(args[5]);

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			DataExportJobsRecord job = context.selectFrom(DATA_EXPORT_JOBS).where(DATA_EXPORT_JOBS.ID.eq(jobId)).fetchAny();
			job.setStatus(DataExportJobsStatus.running);
			job.store(DATA_EXPORT_JOBS.STATUS);
			exporter.exportJob = job.into(DataExportJobs.class);
			exporter.dataset = context.selectFrom(DATASETS).where(DATASETS.ID.eq(exporter.exportJob.getDatasetIds()[0])).fetchAnyInto(Datasets.class);

			exporter.folder = new File(new File(exporter.exportJob.getJobConfig().getBaseFolder(), "async"), exporter.exportJob.getUuid());
			String[] params = exporter.exportJob.getJobConfig().getExportParams();
			exporter.includeAttributes = params != null && Arrays.asList(params).contains("includeAttributes");

			exporter.zipFile = new File(exporter.folder, exporter.folder.getName() + "-" + SDF.format(new Date()) + ".zip");

			exporter.run();

			DataExportJobsRecord record = context.selectFrom(DATA_EXPORT_JOBS).where(DATA_EXPORT_JOBS.ID.eq(jobId)).fetchAny();
			record.setStatus(DataExportJobsStatus.completed);
			record.setResultSize(exporter.zipFile.length());
			record.store(DATA_EXPORT_JOBS.STATUS, DATA_EXPORT_JOBS.RESULT_SIZE);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			try (Connection conn = Database.getConnection())
			{
				DSLContext context = Database.getContext(conn);

				DataExportJobsRecord record = context.selectFrom(DATA_EXPORT_JOBS).where(DATA_EXPORT_JOBS.ID.eq(jobId)).fetchAny();
				record.setStatus(DataExportJobsStatus.failed);
				record.store(DATA_EXPORT_JOBS.STATUS);
			}
			catch (Exception ee)
			{
				ee.printStackTrace();
				Logger.getLogger("").severe(ee.getMessage());
			}
		}
	}

	private void init()
		throws IOException, SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			// Get the germplasm
			if (exportJob.getJobConfig().getyGroupIds() != null || exportJob.getJobConfig().getyIds() != null)
			{
				Set<String> result = new LinkedHashSet<>();

				if (!CollectionUtils.isEmpty(exportJob.getJobConfig().getyIds()))
				{
					result.addAll(context.selectDistinct(GERMINATEBASE.NAME)
										 .from(GERMINATEBASE)
										 .where(GERMINATEBASE.ID.in(exportJob.getJobConfig().getyIds()))
										 .fetchInto(String.class));
				}
				if (!CollectionUtils.isEmpty(exportJob.getJobConfig().getyGroupIds()))
				{
					result.addAll(context.selectDistinct(GERMINATEBASE.NAME)
										 .from(GERMINATEBASE)
										 .leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.FOREIGN_ID.eq(GERMINATEBASE.ID))
										 .where(GROUPMEMBERS.GROUP_ID.in(exportJob.getJobConfig().getyGroupIds()))
										 .fetchInto(String.class));
				}

				this.germplasm = result;
			}
		}
	}

	private void run()
		throws IOException, DataAccessException, SQLException
	{
		init();

		// Make sure it doesn't exist
		if (zipFile.exists())
			zipFile.delete();

		String prefix = zipFile.getAbsolutePath().replace("\\", "/");
		if (prefix.startsWith("/"))
			prefix = prefix.substring(1);
		URI uri = URI.create("jar:file:/" + prefix);
		Map<String, String> env = new HashMap<>();
		env.put("create", "true");
		env.put("encoding", "UTF-8");

		try (Connection conn = Database.getConnection();
			 FileSystem fs = FileSystems.newFileSystem(uri, env, null))
		{
			DSLContext context = Database.getContext(conn);
			try (BufferedWriter bwH = Files.newBufferedWriter(fs.getPath("/pedigree.helium"), StandardCharsets.UTF_8))
			{
				Map<String, List<ViewTablePedigreesRecord>> parentToChildren = new HashMap<>();
				Map<String, List<ViewTablePedigreesRecord>> childrenToParents = new HashMap<>();
				context.selectFrom(VIEW_TABLE_PEDIGREES)
					   .where(VIEW_TABLE_PEDIGREES.DATASET_ID.in(dataset.getId()))
					   .forEach(r -> {
						   String child = r.getChildName();
						   String parent = r.getParentName();

						   List<ViewTablePedigreesRecord> childList = childrenToParents.get(child);
						   List<ViewTablePedigreesRecord> parentList = parentToChildren.get(parent);

						   if (childList == null)
							   childList = new ArrayList<>();
						   if (parentList == null)
							   parentList = new ArrayList<>();

						   childList.add(r);
						   parentList.add(r);

						   childrenToParents.put(child, childList);
						   parentToChildren.put(parent, parentList);
					   });

				bwH.write("# heliumInput = PEDIGREE" + CRLF);
				bwH.write("LineName\tParent\tParentType" + CRLF);

				if (CollectionUtils.isEmpty(germplasm))
				{
					parentToChildren.forEach((p, cs) -> cs.forEach(c -> {
						try
						{
							bwH.write(c.getChildName() + "\t" + c.getParentName() + "\t" + c.getRelationshipType().getLiteral() + CRLF);
						}
						catch (IOException e)
						{
						}
					}));
				}
				else
				{
					int upLimit = germplasm.size() == 1 ? 2 : 3;
					int downLimit = germplasm.size() == 1 ? 1 : 3;

					PedigreeResource.PedigreeWriter downWriter = new PedigreeResource.PedigreeWriter(bwH, parentToChildren, false, downLimit);
					PedigreeResource.PedigreeWriter upWriter = new PedigreeResource.PedigreeWriter(bwH, childrenToParents, true, upLimit);

					for (String requested : germplasm)
					{
						downWriter.run(requested, 0);
						upWriter.run(requested, 0);
					}
				}

				if (includeAttributes)
				{
					try (BufferedWriter bwA = Files.newBufferedWriter(fs.getPath("/pedigree-attributes.helium"), StandardCharsets.UTF_8))
					{
						ExportPassportData procedure = new ExportPassportData();

						procedure.execute(context.configuration());

						ResourceUtils.exportToFile(bwA, procedure.getResults().get(0), true, null, "#heliumInput = PHENOTYPE");
					}
				}
			}
		}

		Duration duration = Duration.between(start, Instant.now());
		System.out.println("DURATION: " + duration);
	}
}
