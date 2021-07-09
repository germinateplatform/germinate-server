package jhi.germinate.server.resource.germplasm;

import jhi.germinate.resource.GermplasmUnificationRequest;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.Germinatebase;
import jhi.germinate.server.database.codegen.tables.records.*;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.*;
import jhi.germinate.server.util.hdf5.Hdf5ToFJTabbedConverter;
import org.jooq.DSLContext;

import javax.ws.rs.Path;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static jhi.germinate.server.database.codegen.tables.Attributedata.*;
import static jhi.germinate.server.database.codegen.tables.Attributes.*;
import static jhi.germinate.server.database.codegen.tables.Comments.*;
import static jhi.germinate.server.database.codegen.tables.Commenttypes.*;
import static jhi.germinate.server.database.codegen.tables.Compounddata.*;
import static jhi.germinate.server.database.codegen.tables.Datasetmembers.*;
import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;
import static jhi.germinate.server.database.codegen.tables.Groupmembers.*;
import static jhi.germinate.server.database.codegen.tables.Groups.*;
import static jhi.germinate.server.database.codegen.tables.Images.*;
import static jhi.germinate.server.database.codegen.tables.Imagetypes.*;
import static jhi.germinate.server.database.codegen.tables.Links.*;
import static jhi.germinate.server.database.codegen.tables.Linktypes.*;
import static jhi.germinate.server.database.codegen.tables.Pedigreedefinitions.*;
import static jhi.germinate.server.database.codegen.tables.Pedigrees.*;
import static jhi.germinate.server.database.codegen.tables.Phenotypedata.*;
import static jhi.germinate.server.database.codegen.tables.Storagedata.*;
import static jhi.germinate.server.database.codegen.tables.Synonyms.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableDatasets.*;

@Path("germplasm/unify")
@Secured(UserType.DATA_CURATOR)
public class GermplasmUnifierResource extends ContextResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean postGermplasmUnifier(GermplasmUnificationRequest request)
		throws SQLException, IOException
	{
		if (request == null || request.getPreferredGermplasmId() == null || CollectionUtils.isEmpty(request.getOtherGermplasmIds()))
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return false;
		}

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		// Remove the preferred id from the list just in case it was added
		List<Integer> ids = new ArrayList<>(Arrays.asList(request.getOtherGermplasmIds()));
		ids.remove(request.getPreferredGermplasmId());

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			// Get the database entries matching the requested ids
			Germinatebase preferred = context.selectFrom(GERMINATEBASE).where(GERMINATEBASE.ID.eq(request.getPreferredGermplasmId())).fetchAnyInto(Germinatebase.class);
			Integer preferredId = preferred.getId();
			List<Germinatebase> others = context.selectFrom(GERMINATEBASE).where(GERMINATEBASE.ID.in(ids)).fetchInto(Germinatebase.class);
			List<Integer> otherIds = others.stream().map(Germinatebase::getId).collect(Collectors.toList());

			// If there's no preferred one or the others are empty or the only other one is the preferred one, return
			if (preferredId == null || CollectionUtils.isEmpty(otherIds) || (otherIds.size() == 1 && Objects.equals(otherIds.get(0), preferredId)))
			{
				resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
				return false;
			}

			// Update all references to the old ids
			context.update(DATASETMEMBERS).set(DATASETMEMBERS.FOREIGN_ID, preferredId).where(DATASETMEMBERS.DATASETMEMBERTYPE_ID.eq(2).and(DATASETMEMBERS.FOREIGN_ID.in(otherIds))).execute();
			context.update(SYNONYMS).set(SYNONYMS.FOREIGN_ID, preferredId).where(SYNONYMS.SYNONYMTYPE_ID.eq(1).and(SYNONYMS.FOREIGN_ID.in(otherIds))).execute();
			context.update(PHENOTYPEDATA).set(PHENOTYPEDATA.GERMINATEBASE_ID, preferredId).where(PHENOTYPEDATA.GERMINATEBASE_ID.in(otherIds)).execute();
			context.update(COMPOUNDDATA).set(COMPOUNDDATA.GERMINATEBASE_ID, preferredId).where(COMPOUNDDATA.GERMINATEBASE_ID.in(otherIds)).execute();
			context.update(PEDIGREEDEFINITIONS).set(PEDIGREEDEFINITIONS.GERMINATEBASE_ID, preferredId).where(PEDIGREEDEFINITIONS.GERMINATEBASE_ID.in(otherIds)).execute();
			context.update(PEDIGREES).set(PEDIGREES.GERMINATEBASE_ID, preferredId).where(PEDIGREES.GERMINATEBASE_ID.in(otherIds)).execute();
			context.update(PEDIGREES).set(PEDIGREES.PARENT_ID, preferredId).where(PEDIGREES.PARENT_ID.in(otherIds)).execute();
			context.update(STORAGEDATA).set(STORAGEDATA.GERMINATEBASE_ID, preferredId).where(STORAGEDATA.GERMINATEBASE_ID.in(otherIds)).execute();
			context.update(GERMINATEBASE).set(GERMINATEBASE.ENTITYPARENT_ID, preferredId).where(GERMINATEBASE.ENTITYPARENT_ID.in(otherIds)).execute();
			context.update(ATTRIBUTEDATA.leftJoin(ATTRIBUTES).on(ATTRIBUTES.ID.eq(ATTRIBUTEDATA.ATTRIBUTE_ID))).set(ATTRIBUTEDATA.FOREIGN_ID, preferredId).where(ATTRIBUTES.TARGET_TABLE.eq("germinatebase").and(ATTRIBUTEDATA.FOREIGN_ID.in(otherIds))).execute();
			context.update(COMMENTS.leftJoin(COMMENTTYPES).on(COMMENTTYPES.ID.eq(COMMENTS.COMMENTTYPE_ID))).set(COMMENTS.REFERENCE_ID, preferredId).where(COMMENTTYPES.REFERENCE_TABLE.eq("germinatebase").and(COMMENTS.REFERENCE_ID.in(otherIds))).execute();
			context.update(GROUPMEMBERS.leftJoin(GROUPS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID))).set(GROUPMEMBERS.FOREIGN_ID, preferredId).where(GROUPS.GROUPTYPE_ID.eq(3).and(GROUPMEMBERS.FOREIGN_ID.in(otherIds))).execute();
			context.update(IMAGES.leftJoin(IMAGETYPES).on(IMAGETYPES.ID.eq(IMAGES.IMAGETYPE_ID))).set(IMAGES.FOREIGN_ID, preferredId).where(IMAGETYPES.REFERENCE_TABLE.eq("germinatebase").and(IMAGES.FOREIGN_ID.in(otherIds))).execute();
			context.update(LINKS.leftJoin(LINKTYPES).on(LINKTYPES.ID.eq(LINKS.LINKTYPE_ID))).set(LINKS.FOREIGN_ID, preferredId).where(LINKTYPES.TARGET_TABLE.eq("germinatebase").and(LINKS.FOREIGN_ID.in(otherIds))).execute();

			// Delete the old ids
			context.deleteFrom(GERMINATEBASE).where(GERMINATEBASE.ID.in(otherIds)).execute();

			// Add a comment to the preferred germplasm to remember this event happened
			String commentContent = "Germplasm unification of (" + otherIds + ") into " + preferredId;
			if (!StringUtils.isEmpty(request.getExplanation()))
				commentContent += " " + request.getExplanation();

			CommentsRecord comment = context.newRecord(COMMENTS);
			comment.setUserId(userDetails.getId());
			comment.setReferenceId(preferredId);
			comment.setDescription(commentContent);
			comment.setCommenttypeId(1);
			comment.setVisibility(true);
			comment.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			comment.store();

			List<String> otherNames = others.stream().map(Germinatebase::getName).collect(Collectors.toList());

			// Check the synonyms of the preferred germplasm
			SynonymsRecord synonymsRecord = context.selectFrom(SYNONYMS).where(SYNONYMS.SYNONYMTYPE_ID.eq(1).and(SYNONYMS.FOREIGN_ID.eq(preferredId))).fetchAny();
			// Create if it doesn't exist
			if (synonymsRecord == null)
			{
				synonymsRecord = context.newRecord(SYNONYMS);
				synonymsRecord.setForeignId(preferredId);
				synonymsRecord.setSynonymtypeId(1);
				synonymsRecord.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			}
			// Update the synonyms to include the ones that have just been removed.
			Set<String> synonyms = new HashSet<>();
			if (!CollectionUtils.isEmpty(synonymsRecord.getSynonyms()))
				synonyms.addAll(Arrays.asList(synonymsRecord.getSynonyms()));
			synonyms.addAll(otherNames);
			synonymsRecord.setSynonyms(synonyms.toArray(new String[0]));
			// Store back to the database
			synonymsRecord.store();

			// Update the names in the genotype files
			context.select(VIEW_TABLE_DATASETS.SOURCE_FILE).from(VIEW_TABLE_DATASETS).where(VIEW_TABLE_DATASETS.IS_EXTERNAL.eq(false).and(VIEW_TABLE_DATASETS.SOURCE_FILE.isNotNull()).and(VIEW_TABLE_DATASETS.DATASET_TYPE.eq("genotype"))).fetchInto(String.class)
				   .stream()
				   .map(f -> ResourceUtils.getFromExternal(f, "data", "genotypes"))
				   .forEach(gf -> Hdf5ToFJTabbedConverter.updateGermplasmNames(gf, preferred.getName(), otherNames));

			// Update the names in the allele frequency files
			context.select(VIEW_TABLE_DATASETS.SOURCE_FILE).from(VIEW_TABLE_DATASETS).where(VIEW_TABLE_DATASETS.IS_EXTERNAL.eq(false).and(VIEW_TABLE_DATASETS.SOURCE_FILE.isNotNull()).and(VIEW_TABLE_DATASETS.DATASET_TYPE.eq("allelefreq"))).fetchInto(String.class)
				   .stream()
				   .map(f -> ResourceUtils.getFromExternal(f, "data", "allelefreq"))
				   .forEach(af -> updateAllelefreqFile(af, preferred.getName(), otherNames));

			return true;
		}
	}

	private synchronized void updateAllelefreqFile(File file, String preferredName, List<String> otherNames)
	{
		File target = new File(file.getParentFile(), file.getName() + ".temp");

		try
		{
			// Read the original and write to the temp file
			try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
				 BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(target), StandardCharsets.UTF_8)))
			{
				String line;

				while ((line = br.readLine()) != null)
				{
					// Find the first tab
					int tabIndex = line.indexOf("\t");

					// If there is one
					if (tabIndex != -1)
					{
						// Get the germplasm name
						String name = line.substring(0, tabIndex);
						// If it's one of the ones to replace, do so
						if (otherNames.contains(name))
							line = line.replaceFirst(name, preferredName);
					}

					// Write the line back into the temp target
					bw.write(line);
					bw.newLine();
				}
			}

			// Overwrite the original file with the temp file. Do this outside of the inner try so that all files are closed at this point.
			Files.move(target.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Logger.getLogger("").severe(e.getLocalizedMessage());
		}
	}
}
