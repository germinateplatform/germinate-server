package jhi.germinate.server.resource.germplasm;

import jhi.germinate.resource.ViewMcpd;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.Germinatebase;
import jhi.germinate.server.util.Secured;
import org.jooq.DSLContext;
import org.jooq.impl.*;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.sql.*;

import static jhi.germinate.server.database.codegen.tables.Attributedata.*;
import static jhi.germinate.server.database.codegen.tables.Attributes.*;
import static jhi.germinate.server.database.codegen.tables.Countries.*;
import static jhi.germinate.server.database.codegen.tables.Entitytypes.*;
import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;
import static jhi.germinate.server.database.codegen.tables.Institutions.*;
import static jhi.germinate.server.database.codegen.tables.Locations.*;
import static jhi.germinate.server.database.codegen.tables.Pedigreedefinitions.*;
import static jhi.germinate.server.database.codegen.tables.Storage.*;
import static jhi.germinate.server.database.codegen.tables.Storagedata.*;
import static jhi.germinate.server.database.codegen.tables.Taxonomies.*;

@Path("germplasm/{germplasmId}/mcpd")
@Secured
@PermitAll
public class GermplasmMcpdResource
{
	@Context
	protected HttpServletResponse resp;

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ViewMcpd getGermplasmMcpd(@PathParam("germplasmId") Integer germplasmId)
		throws IOException, SQLException
	{
		if (germplasmId == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			Germinatebase g = GERMINATEBASE.as("g");
			Germinatebase p = GERMINATEBASE.as("p");

			return context.select(
				g.ID.as("ID"),
				g.PUID.as("PUID"),
				INSTITUTIONS.CODE.as("INSTCODE)"),
				g.GENERAL_IDENTIFIER.as("ACCENUMB"),
				g.COLLNUMB.as("COLLNUMN"),
				g.COLLCODE.as("COLLCODE"),
				g.COLLNAME.as("COLLNAME"),
				INSTITUTIONS.ADDRESS.as("COLLINSTADDRESS"),
				g.COLLMISSID.as("COLLMISSID"),
				TAXONOMIES.GENUS.as("GENUS"),
				TAXONOMIES.SPECIES.as("SPECIES"),
				TAXONOMIES.SPECIES_AUTHOR.as("SPAUTHOR"),
				TAXONOMIES.SUBTAXA.as("SUBTAXA"),
				TAXONOMIES.SUBTAXA_AUTHOR.as("SUBTAUTHOR"),
				TAXONOMIES.CROPNAME.as("CROPNAME"),
				g.NUMBER.as("ACCENAME"),
				DSL.replace(g.ACQDATE, "-", "").as("ACQDATE"),
				COUNTRIES.COUNTRY_CODE3.as("ORIGCTY"),
				LOCATIONS.SITE_NAME.as("COLLSITE"),
				LOCATIONS.LATITUDE.as("DECLATITUDE"),
				DSL.inline(null, SQLDataType.VARCHAR).as("LATITUDE"),
				LOCATIONS.LONGITUDE.as("DECLONGITUDE"),
				DSL.inline(null, SQLDataType.VARCHAR).as("LONGITUDE"),
				LOCATIONS.COORDINATE_UNCERTAINTY.as("COORDUNCERT"),
				LOCATIONS.COORDINATE_DATUM.as("COORDDATUM"),
				LOCATIONS.GEOREFERENCING_METHOD.as("GEOREFMETH"),
				LOCATIONS.ELEVATION.as("ELEVATION"),
				DSL.replace(DSL.cast(g.COLLDATE, String.class), "-", "").as("COLLDATE"),
				g.BREEDERS_CODE.as("BREDCODE"),
				g.BREEDERS_NAME.as("BREDNAME"),
				g.BIOLOGICALSTATUS_ID.as("SAMPSTAT"),
				PEDIGREEDEFINITIONS.DEFINITION.as("ANCEST"),
				g.COLLSRC_ID.as("COLLSRC"),
				g.DONOR_CODE.as("DONORCODE"),
				g.DONOR_NAME.as("DONORNAME"),
				g.DONOR_NUMBER.as("DONORNUMB"),
				g.OTHERNUMB.as("OTHERNUMB"),
				g.DUPLSITE.as("DUPLSITE"),
				g.DUPLINSTNAME.as("DUPLINSTNAME"),
				DSL.groupConcat(STORAGE.DESCRIPTION).separator(",").as("STORATE"),
				g.MLSSTATUS_ID.as("MLSSTAT"),
				DSL.select(ATTRIBUTEDATA.VALUE)
				   .from(ATTRIBUTEDATA).leftJoin(ATTRIBUTES).on(ATTRIBUTES.ID.eq(ATTRIBUTEDATA.ATTRIBUTE_ID))
				   .where(ATTRIBUTES.TARGET_TABLE.eq("germinatebase"))
				   .and(ATTRIBUTES.NAME.eq("Remarks"))
				   .and(ATTRIBUTEDATA.FOREIGN_ID.eq(g.ID))
				   .limit(1)
				   .asField("REMARKS"),
				ENTITYTYPES.NAME.as("ENTITYTYPE"),
				DSL.select(p.ID).from(p).where(p.ID.eq(g.ENTITYPARENT_ID)).asField("ENTITYPARENTID"),
				DSL.select(p.GENERAL_IDENTIFIER).from(p).where(p.ID.eq(g.ENTITYPARENT_ID)).asField("ENTITYPARENTACCENUMB")
			)
						  .from(g)
						  .leftJoin(TAXONOMIES).on(TAXONOMIES.ID.eq(g.TAXONOMY_ID))
						  .leftJoin(LOCATIONS).on(LOCATIONS.ID.eq(g.LOCATION_ID))
						  .leftJoin(COUNTRIES).on(COUNTRIES.ID.eq(LOCATIONS.COUNTRY_ID))
						  .leftJoin(INSTITUTIONS).on(INSTITUTIONS.ID.eq(g.INSTITUTION_ID))
						  .leftJoin(PEDIGREEDEFINITIONS).on(PEDIGREEDEFINITIONS.GERMINATEBASE_ID.eq(g.ID))
						  .leftJoin(STORAGEDATA).on(STORAGEDATA.GERMINATEBASE_ID.eq(g.ID))
						  .leftJoin(STORAGE).on(STORAGE.ID.eq(STORAGEDATA.STORAGE_ID))
						  .leftJoin(ATTRIBUTEDATA).on(ATTRIBUTEDATA.FOREIGN_ID.eq(g.ID))
						  .leftJoin(ENTITYTYPES).on(ENTITYTYPES.ID.eq(g.ENTITYTYPE_ID))
						  .where(g.ID.eq(germplasmId))
						  .groupBy(g.ID, PEDIGREEDEFINITIONS.ID)
						  .fetchAnyInto(ViewMcpd.class);
		}
	}
}
