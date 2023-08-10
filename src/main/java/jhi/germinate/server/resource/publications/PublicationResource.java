package jhi.germinate.server.resource.publications;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.enums.PublicationdataReferenceType;
import jhi.germinate.server.database.codegen.tables.pojos.*;
import jhi.germinate.server.database.codegen.tables.records.*;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.resource.groups.GroupResource;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.sql.*;

import static jhi.germinate.server.database.codegen.tables.Experiments.EXPERIMENTS;
import static jhi.germinate.server.database.codegen.tables.Germinatebase.GERMINATEBASE;
import static jhi.germinate.server.database.codegen.tables.Publicationdata.PUBLICATIONDATA;
import static jhi.germinate.server.database.codegen.tables.Publications.PUBLICATIONS;
import static jhi.germinate.server.database.codegen.tables.ViewTablePublications.VIEW_TABLE_PUBLICATIONS;

@Path("publication")
@Secured(UserType.DATA_CURATOR)
public class PublicationResource extends ContextResource
{
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Integer putPublication(Publications publication)
			throws SQLException, IOException
	{
		if (publication == null || StringUtils.isEmpty(publication.getDoi()) || publication.getId() != null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		publication.setDoi(publication.getDoi().trim());

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			PublicationsRecord record = context.selectFrom(PUBLICATIONS).where(PUBLICATIONS.DOI.eq(publication.getDoi())).fetchAny();

			if (record == null)
			{
				record = context.newRecord(PUBLICATIONS, publication);
				record.store();
			}

			return record.getId();
		}
	}

	@PUT
	@Path("/{publicationId}/reference")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean putPublicationReference(@PathParam("publicationId") Integer publicationId, Publicationdata data)
			throws SQLException, IOException
	{
		if (data == null || data.getPublicationId() == null || data.getReferenceType() == null || (data.getReferenceType() != PublicationdataReferenceType.database && data.getForeignId() == null) || publicationId == null || publicationId != data.getPublicationId())
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return false;
		}

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			PublicationsRecord publication = context.selectFrom(PUBLICATIONS).where(PUBLICATIONS.ID.eq(data.getPublicationId())).fetchAny();

			if (publication == null)
			{
				resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
				return false;
			}

			boolean exists = false;
			switch (data.getReferenceType())
			{
				case database:
					exists = true;
					break;
				case dataset:
					ViewTableDatasets dataset = DatasetTableResource.getDatasetForId(data.getForeignId(), req, userDetails, false);
					exists = dataset != null;
					break;
				case group:
					try
					{
						GroupResource.checkGroupVisibility(context, userDetails, data.getForeignId());
						exists = true;
					}
					catch (GerminateException e)
					{
						exists = false;
					}
					break;
				case experiment:
					exists = context.selectFrom(EXPERIMENTS).where(EXPERIMENTS.ID.eq(data.getForeignId())).fetchAny() != null;
					break;
				case germplasm:
					exists = context.selectFrom(GERMINATEBASE).where(GERMINATEBASE.ID.eq(data.getForeignId())).fetchAny() != null;
					break;
			}

			if (!exists)
			{
				resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
				return false;
			}

			PublicationdataRecord record = context.selectFrom(PUBLICATIONDATA)
												  .where(PUBLICATIONDATA.PUBLICATION_ID.eq(data.getPublicationId()))
												  .and(PUBLICATIONDATA.FOREIGN_ID.isNotDistinctFrom(data.getForeignId()))
												  .and(PUBLICATIONDATA.REFERENCE_TYPE.eq(data.getReferenceType()))
												  .fetchAny();

			if (record == null)
			{
				record = context.newRecord(PUBLICATIONDATA, data);
				return record.store() > 0;
			}
			else
			{
				return true;
			}
		}
	}


	@DELETE
	@Path("/{publicationId}/reference/database")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean deletePublicationReferenceDatabase(@PathParam("publicationId") Integer publicationId)
			throws SQLException, IOException
	{
		return delete(publicationId, PublicationdataReferenceType.database, null);
	}

	@DELETE
	@Path("/{publicationId:\\d+}/reference/{referenceType}/{referenceId:\\d+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean deletePublicationReferenceById(@PathParam("publicationId") Integer publicationId, @PathParam("referenceType") String referenceType, @PathParam("referenceId") Integer referenceId)
			throws SQLException, IOException
	{
		PublicationdataReferenceType type = null;
		try
		{
			type = PublicationdataReferenceType.valueOf(referenceType);
		}
		catch (IllegalArgumentException e)
		{
		}

		return delete(publicationId, type, referenceId);
	}

	private boolean delete(Integer publicationId, PublicationdataReferenceType referenceType, Integer referenceId)
			throws IOException, SQLException
	{
		if (publicationId == null || (referenceType != PublicationdataReferenceType.database && referenceId == null))
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return false;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			boolean result = context.deleteFrom(PUBLICATIONDATA)
									.where(PUBLICATIONDATA.PUBLICATION_ID.eq(publicationId))
									.and(PUBLICATIONDATA.REFERENCE_TYPE.eq(referenceType))
									.and(PUBLICATIONDATA.FOREIGN_ID.isNotDistinctFrom(referenceId)).execute() > 0;

			// Delete all no longer referenced publications
			context.deleteFrom(PUBLICATIONS).whereNotExists(DSL.selectOne().from(PUBLICATIONDATA).where(PUBLICATIONDATA.PUBLICATION_ID.eq(PUBLICATIONS.ID))).execute();

			return result;
		}
	}
}
