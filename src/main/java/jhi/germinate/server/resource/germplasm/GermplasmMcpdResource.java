package jhi.germinate.server.resource.germplasm;

import org.jooq.DSLContext;
import org.jooq.impl.*;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;

import jhi.germinate.resource.ViewMcpd;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.tables.Germinatebase;

import static jhi.germinate.server.database.tables.Attributedata.*;
import static jhi.germinate.server.database.tables.Attributes.*;
import static jhi.germinate.server.database.tables.Countries.*;
import static jhi.germinate.server.database.tables.Entitytypes.*;
import static jhi.germinate.server.database.tables.Germinatebase.*;
import static jhi.germinate.server.database.tables.Institutions.*;
import static jhi.germinate.server.database.tables.Locations.*;
import static jhi.germinate.server.database.tables.Pedigreedefinitions.*;
import static jhi.germinate.server.database.tables.Storage.*;
import static jhi.germinate.server.database.tables.Storagedata.*;
import static jhi.germinate.server.database.tables.Taxonomies.*;

/**
 * @author Sebastian Raubach
 */
public class GermplasmMcpdResource extends ServerResource
{
	private Integer germplasmId;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			this.germplasmId = Integer.parseInt(getRequestAttributes().get("germplasmId").toString());
		}
		catch (NullPointerException | NumberFormatException e)
		{
		}
	}

	@Get("json")
	public ViewMcpd getJson()
	{
		if (germplasmId == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
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
						  .fetchOneInto(ViewMcpd.class);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
