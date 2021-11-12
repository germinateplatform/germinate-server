package jhi.germinate.server.resource.traits;

import jhi.germinate.resource.TraitUnificationRequest;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.Phenotypes;
import jhi.germinate.server.database.codegen.tables.records.SynonymsRecord;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static jhi.germinate.server.database.codegen.tables.Images.*;
import static jhi.germinate.server.database.codegen.tables.Imagetypes.*;
import static jhi.germinate.server.database.codegen.tables.Phenotypedata.*;
import static jhi.germinate.server.database.codegen.tables.Phenotypes.*;
import static jhi.germinate.server.database.codegen.tables.Synonyms.*;

@Path("trait/unify")
@Secured(UserType.DATA_CURATOR)
public class TraitUnifierResource extends ContextResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean postTraitUnifier(TraitUnificationRequest request)
		throws SQLException, IOException
	{
		if (request == null || request.getPreferredTraitId() == null || CollectionUtils.isEmpty(request.getOtherTraitIds()))
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return false;
		}

		// Remove the preferred id from the list just in case it was added
		List<Integer> ids = new ArrayList<>(Arrays.asList(request.getOtherTraitIds()));
		ids.remove(request.getPreferredTraitId());

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			// Get the database entries matching the requested ids
			Phenotypes preferred = context.selectFrom(PHENOTYPES).where(PHENOTYPES.ID.eq(request.getPreferredTraitId())).fetchAnyInto(Phenotypes.class);
			Integer preferredId = preferred.getId();
			List<Phenotypes> others = context.selectFrom(PHENOTYPES).where(PHENOTYPES.ID.in(ids)).fetchInto(Phenotypes.class);
			List<Integer> otherIds = others.stream().map(Phenotypes::getId).collect(Collectors.toList());

			// If there's no preferred one or the others are empty or the only other one is the preferred one, return
			if (preferredId == null || CollectionUtils.isEmpty(otherIds) || (otherIds.size() == 1 && Objects.equals(otherIds.get(0), preferredId)))
			{
				resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
				return false;
			}

			context.update(IMAGES.leftJoin(IMAGETYPES).on(IMAGETYPES.ID.eq(IMAGES.IMAGETYPE_ID))).set(IMAGES.FOREIGN_ID, preferredId).where(IMAGETYPES.REFERENCE_TABLE.eq("phenotypes").and(IMAGES.FOREIGN_ID.in(otherIds))).execute();
			context.update(PHENOTYPEDATA).set(PHENOTYPEDATA.PHENOTYPE_ID, preferredId).where(PHENOTYPEDATA.PHENOTYPE_ID.in(otherIds)).execute();

			List<String> otherNames = others.stream().map(Phenotypes::getName).collect(Collectors.toList());

			// Check the synonyms of the preferred trait
			SynonymsRecord synonymsRecord = context.selectFrom(SYNONYMS).where(SYNONYMS.SYNONYMTYPE_ID.eq(4).and(SYNONYMS.FOREIGN_ID.eq(preferredId))).fetchAny();
			// Create if it doesn't exist
			if (synonymsRecord == null)
			{
				synonymsRecord = context.newRecord(SYNONYMS);
				synonymsRecord.setForeignId(preferredId);
				synonymsRecord.setSynonymtypeId(4);
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

			// Delete the old ids
			context.deleteFrom(PHENOTYPES).where(PHENOTYPES.ID.in(otherIds)).execute();

			return true;
		}
	}
}
