package jhi.germinate.server.resource.datawarning;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.Datawarnings;
import jhi.germinate.server.database.codegen.tables.records.*;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Datawarnings.DATAWARNINGS;
import static jhi.germinate.server.database.codegen.tables.Germplasmdatawarnings.GERMPLASMDATAWARNINGS;

@Path("datawarning/germplasm/{germplasmId:\\d+}")
public class GermplasmDataWarningResource
{
	@PathParam("germplasmId")
	Integer germplasmId;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Secured
	@PermitAll
	public List<Datawarnings> getGermplasmDataWarning()
			throws SQLException
	{
		if (germplasmId == null)
			throw new NotFoundException();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			return context.select(DATAWARNINGS.fields())
						  .from(GERMPLASMDATAWARNINGS)
						  .leftJoin(DATAWARNINGS).on(DATAWARNINGS.ID.eq(GERMPLASMDATAWARNINGS.DATAWARNING_ID))
						  .where(GERMPLASMDATAWARNINGS.GERMINATEBASE_ID.eq(germplasmId))
						  .fetchInto(Datawarnings.class);
		}
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured(UserType.DATA_CURATOR)
	public Datawarnings putGermplasmDataWarning(Datawarnings data)
			throws SQLException
	{
		if (germplasmId == null || data == null)
			throw new BadRequestException();

		if (data.getId() == null && (StringUtils.isEmpty(data.getDescription()) || data.getCategory() == null))
			throw new BadRequestException();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			// Create the new data warning if it doesn't exist yet
			Integer id = data.getId();
			if (id == null)
			{
				DatawarningsRecord record = context.newRecord(DATAWARNINGS, data);
				record.store();
				id = record.getId();
			}

			// Create the new mapping
			GermplasmdatawarningsRecord newRecord = context.newRecord(GERMPLASMDATAWARNINGS);
			newRecord.setDatawarningId(id);
			newRecord.setGerminatebaseId(germplasmId);
			newRecord.store();

			return newRecord.into(Datawarnings.class);
		}
	}

	@Path("/{dataWarningId:\\d+}")
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.DATA_CURATOR)
	public boolean deleteGermplasmDataWarning(@PathParam("dataWarningId") Integer dataWarningId)
			throws SQLException
	{
		if (germplasmId == null || dataWarningId == null)
			throw new NotFoundException();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			// Remove the connection between germplasm and data warning
			boolean result = context.deleteFrom(GERMPLASMDATAWARNINGS)
									.where(GERMPLASMDATAWARNINGS.DATAWARNING_ID.eq(dataWarningId))
									.and(GERMPLASMDATAWARNINGS.GERMINATEBASE_ID.eq(germplasmId))
									.execute() > 0;

			// Delete any data warnings that no longer have any germplasm using it
			context.deleteFrom(DATAWARNINGS)
				   .whereNotExists(DSL.selectOne().from(GERMPLASMDATAWARNINGS).where(GERMPLASMDATAWARNINGS.DATAWARNING_ID.eq(DATAWARNINGS.ID)))
				   .execute();

			return result;
		}
	}
}
