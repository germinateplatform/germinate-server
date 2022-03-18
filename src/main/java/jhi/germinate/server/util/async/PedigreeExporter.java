package jhi.germinate.server.util.async;

import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.routines.ExportPassportData;
import jhi.germinate.server.database.codegen.tables.records.ViewTablePedigreesRecord;
import jhi.germinate.server.resource.ResourceUtils;
import jhi.germinate.server.resource.pedigrees.PedigreeResource;
import jhi.germinate.server.util.CollectionUtils;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;

import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Date;
import java.util.*;
import java.util.stream.Collectors;

import static jhi.germinate.server.database.codegen.tables.ViewTablePedigrees.*;

public class PedigreeExporter
{
	private static final String           CRLF              = "\r\n";
	private static final SimpleDateFormat SDF               = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	private              File             folder;
	private              Boolean          includeAttributes = false;
	private              File             zipFile;
	private              File             germplasmFile;
	private              Set<String>      germplasm;
	private              List<Integer>    datasetIds;

	private final Instant start;

	public PedigreeExporter()
	{
		start = Instant.now();
	}

	public static void main(String[] args)
		throws IOException, SQLException
	{
		Database.init(args[0], args[1], args[2], args[3], args[4], false);
		PedigreeExporter exporter = new PedigreeExporter();
		exporter.datasetIds = Arrays.stream(args[5].split(",")).map(id -> Integer.parseInt(id)).collect(Collectors.toList());
		exporter.folder = new File(args[6]);
		exporter.includeAttributes = Boolean.parseBoolean(args[7]);
		exporter.zipFile = new File(exporter.folder, exporter.folder.getName() + "-" + SDF.format(new Date()) + ".zip");
		File germplasmFile = new File(exporter.folder, exporter.folder.getName() + ".germplasm");

		if (germplasmFile.exists() && germplasmFile.isFile())
			exporter.germplasmFile = germplasmFile;

		exporter.run();
	}

	private void init()
		throws IOException
	{
		if (germplasmFile != null)
		{
			germplasm = new LinkedHashSet<>(Files.readAllLines(germplasmFile.toPath()));
			germplasmFile.delete();
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
					   .where(VIEW_TABLE_PEDIGREES.DATASET_ID.in(datasetIds))
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
