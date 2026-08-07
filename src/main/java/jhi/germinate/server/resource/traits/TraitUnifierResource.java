package jhi.germinate.server.resource.traits;

import jhi.germinate.resource.UnificationRequest;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.Variables;
import jhi.germinate.server.database.codegen.tables.records.*;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static jhi.germinate.server.database.codegen.tables.Images.*;
import static jhi.germinate.server.database.codegen.tables.Imagetypes.*;
import static jhi.germinate.server.database.codegen.tables.Phenotypedata.*;
import static jhi.germinate.server.database.codegen.tables.Synonyms.*;
import static jhi.germinate.server.database.codegen.tables.Synonymtypes.SYNONYMTYPES;
import static jhi.germinate.server.database.codegen.tables.Variables.VARIABLES;

@Path("trait/unify")
@Secured(UserType.DATA_CURATOR)
public class TraitUnifierResource extends ContextResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean postTraitUnifier(UnificationRequest request)
		throws SQLException, IOException
	{
		if (request == null || request.getPreferredId() == null || CollectionUtils.isEmpty(request.getOtherIds()))
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return false;
		}

		// Remove the preferred id from the list just in case it was added
		List<Integer> ids = new ArrayList<>(Arrays.asList(request.getOtherIds()));
		ids.remove(request.getPreferredId());

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			// Get the database entries matching the requested ids
			Variables preferred = context.selectFrom(VARIABLES).where(VARIABLES.ID.eq(request.getPreferredId())).fetchAnyInto(Variables.class);
			Integer preferredId = preferred.getId();
			List<Variables> others = context.selectFrom(VARIABLES).where(VARIABLES.ID.in(ids)).fetchInto(Variables.class);
			List<Integer> otherIds = others.stream().map(Variables::getId).collect(Collectors.toList());

			// If there's no preferred one or the others are empty or the only other one is the preferred one, return
			if (preferredId == null || CollectionUtils.isEmpty(otherIds) || (otherIds.size() == 1 && Objects.equals(otherIds.get(0), preferredId)))
			{
				resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
				return false;
			}

			context.update(IMAGES.leftJoin(IMAGETYPES).on(IMAGETYPES.ID.eq(IMAGES.IMAGETYPE_ID))).set(IMAGES.FOREIGN_ID, preferredId).where(IMAGETYPES.REFERENCE_TABLE.eq("phenotypes").and(IMAGES.FOREIGN_ID.in(otherIds))).execute();
			context.update(PHENOTYPEDATA).set(PHENOTYPEDATA.VARIABLE_ID, preferredId).where(PHENOTYPEDATA.VARIABLE_ID.in(otherIds)).execute();

			List<String> otherNames = others.stream().map(Variables::getName).toList();

			SynonymtypesRecord type = context.selectFrom(SYNONYMTYPES).where(SYNONYMTYPES.TARGET_TABLE.eq("variables")).fetchAny();
			if (type == null) {
				type = context.newRecord(SYNONYMTYPES);
				type.setName("Variables");
				type.setDescription("Synonyms for variables");
				type.setTargetTable("variables");
				type.store();
			}

			// Check the synonyms of the preferred trait
			SynonymsRecord synonymsRecord = context.selectFrom(SYNONYMS).where(SYNONYMS.SYNONYMTYPE_ID.eq(type.getId()).and(SYNONYMS.FOREIGN_ID.eq(preferredId))).fetchAny();
			// Create if it doesn't exist
			if (synonymsRecord == null)
			{
				synonymsRecord = context.newRecord(SYNONYMS);
				synonymsRecord.setForeignId(preferredId);
				synonymsRecord.setSynonymtypeId(type.getId());
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
			context.deleteFrom(VARIABLES).where(VARIABLES.ID.in(otherIds)).execute();

			return true;
		}
	}
}
