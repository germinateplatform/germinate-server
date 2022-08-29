package jhi.germinate.server.resource.germplasm;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableInstitutions;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.util.Secured;
import org.jooq.*;

import java.io.IOException;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Countries.*;
import static jhi.germinate.server.database.codegen.tables.Germplasminstitutions.*;
import static jhi.germinate.server.database.codegen.tables.Institutions.*;

@Path("germplasm/{germplasmId}/institution/table")
@Secured
@PermitAll
public class GermplasmInstitutionTableResource extends BaseResource
{
	@PathParam("germplasmId")
	Integer germplasmId;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableInstitutions>> postInstitutionTable(PaginatedRequest request)
		throws SQLException, IOException
	{
		if (germplasmId == null) {
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectSelectStep<?> select = context.select(
				INSTITUTIONS.ID.as("institution_id"),
				INSTITUTIONS.NAME.as("institution_name"),
				INSTITUTIONS.CODE.as("institution_code"),
				INSTITUTIONS.ACRONYM.as("institution_acronym"),
				INSTITUTIONS.ADDRESS.as("institution_address"),
				INSTITUTIONS.EMAIL.as("institution_email"),
				INSTITUTIONS.CONTACT.as("institution_contact"),
				INSTITUTIONS.PHONE.as("institution_phone"),
				COUNTRIES.ID.as("country_id"),
				COUNTRIES.COUNTRY_NAME.as("country_name"),
				COUNTRIES.COUNTRY_CODE2.as("country_code"),
				GERMPLASMINSTITUTIONS.TYPE.as("institution_type")
			);

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectConditionStep<?> from = select.from(INSTITUTIONS)
				.leftJoin(COUNTRIES).on(COUNTRIES.ID.eq(INSTITUTIONS.COUNTRY_ID))
				.leftJoin(GERMPLASMINSTITUTIONS).on(GERMPLASMINSTITUTIONS.INSTITUTION_ID.eq(INSTITUTIONS.ID))
				.where(GERMPLASMINSTITUTIONS.GERMINATEBASE_ID.eq(germplasmId));

			// Filter here!
			filter(from, filters);

			List<ViewTableInstitutions> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableInstitutions.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}
}
