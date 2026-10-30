package jhi.germinate.server.resource.publications;

import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.enums.PublicationdataReferenceType;
import jhi.germinate.server.database.codegen.tables.pojos.*;
import jhi.germinate.server.database.codegen.tables.records.*;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.resource.groups.GroupResource;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Experiments.EXPERIMENTS;
import static jhi.germinate.server.database.codegen.tables.Germinatebase.GERMINATEBASE;
import static jhi.germinate.server.database.codegen.tables.Projectpublications.PROJECTPUBLICATIONS;
import static jhi.germinate.server.database.codegen.tables.Projects.PROJECTS;
import static jhi.germinate.server.database.codegen.tables.Publicationdata.PUBLICATIONDATA;
import static jhi.germinate.server.database.codegen.tables.Publications.PUBLICATIONS;

@Path("publication")
@Secured(UserType.DATA_CURATOR)
public class PublicationResource extends ContextResource
{
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Integer putPublication(Publications publication)
			throws SQLException
	{
		if (publication == null || StringUtils.isEmpty(publication.getDoi()) || publication.getId() != null)
			throw new BadRequestException();

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
	public boolean putPublicationReference(@PathParam("publicationId") Integer publicationId, Publicationdata data, @QueryParam("isProject") Boolean isProject)
			throws SQLException
	{
		if (data == null || data.getPublicationId() == null || publicationId == null || !publicationId.equals(data.getPublicationId()))
			throw new BadRequestException();

		if (isProject == null && (data.getReferenceType() == null || (data.getReferenceType() != PublicationdataReferenceType.database && data.getForeignId() == null)))
			throw new BadRequestException();

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			PublicationsRecord publication = context.selectFrom(PUBLICATIONS).where(PUBLICATIONS.ID.eq(data.getPublicationId())).fetchAny();

			if (publication == null)
				throw new NotFoundException();

			if (isProject != null && isProject)
			{
				ProjectsRecord project = context.selectFrom(PROJECTS).where(PROJECTS.ID.eq(data.getForeignId())).fetchAny();

				if (project == null)
					throw new NotFoundException();

				ProjectpublicationsRecord ppRecord = context.selectFrom(PROJECTPUBLICATIONS)
															.where(PROJECTPUBLICATIONS.PROJECT_ID.eq(project.getId()))
															.and(PROJECTPUBLICATIONS.PUBLICATION_ID.eq(publication.getId()))
															.fetchAny();

				if (ppRecord == null)
				{
					ppRecord = context.newRecord(PROJECTPUBLICATIONS);
					ppRecord.setProjectId(project.getId());
					ppRecord.setPublicationId(publication.getId());
					return ppRecord.store() > 0;
				}
				else
				{
					return true;
				}
			}
			else
			{
				boolean exists = switch (data.getReferenceType())
				{
					case database -> true;
					case dataset ->
					{
						List<Integer> availableIds = AuthorizationFilter.getDatasetIds(req, userDetails, null, false);
						yield availableIds.contains(data.getForeignId());
					}
					case group ->
					{
						GroupResource.checkGroupVisibility(context, userDetails, data.getForeignId());
						yield true;
					}
					case experiment ->
							context.selectFrom(EXPERIMENTS).where(EXPERIMENTS.ID.eq(data.getForeignId())).fetchAny() != null;
					case germplasm ->
							context.selectFrom(GERMINATEBASE).where(GERMINATEBASE.ID.eq(data.getForeignId())).fetchAny() != null;
				};

				if (!exists)
					throw new NotFoundException();

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
	}

	@PUT
	@Path("/{publicationId}/references")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean putPublicationReferences(@PathParam("publicationId") Integer publicationId, Publicationdata[] data)
			throws SQLException
	{
		if (CollectionUtils.isEmpty(data))
			throw new BadRequestException();

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			Set<Integer> publicationIds = context.select(PUBLICATIONS.ID).from(PUBLICATIONS).fetchSet(PUBLICATIONS.ID);
			Set<Integer> germplasmIds = context.select(GERMINATEBASE.ID).from(GERMINATEBASE).fetchSet(GERMINATEBASE.ID);
			Set<Integer> experimentId = context.select(EXPERIMENTS.ID).from(EXPERIMENTS).fetchSet(EXPERIMENTS.ID);
			List<Integer> availableIds = AuthorizationFilter.getDatasetIds(req, userDetails, null, false);
			Field<String> concat = DSL.concat(PUBLICATIONDATA.PUBLICATION_ID, DSL.val("|"), PUBLICATIONDATA.REFERENCE_TYPE, DSL.val("|"), PUBLICATIONDATA.FOREIGN_ID);
			Set<String> existingEntries = context.select(concat).from(PUBLICATIONDATA).fetchSet(concat);

			List<Publicationdata> filtered = Arrays.stream(data)
												   .filter(d -> {
													   // Remove any invalid configurations
													   if (d.getPublicationId() == null || d.getReferenceType() == null || (d.getReferenceType() != PublicationdataReferenceType.database && d.getForeignId() == null) || !publicationId.equals(d.getPublicationId()) || !publicationIds.contains(d.getPublicationId()))
														   return false;

													   if (d.getReferenceType() != PublicationdataReferenceType.database)
													   {
														   // Remove all the entries that already exist
														   if (existingEntries.contains(d.getPublicationId() + "|" + d.getReferenceType() + "|" + d.getForeignId()))
															   return false;
													   }

													   return switch (d.getReferenceType())
													   {
														   case group ->
														   {
															   // Check group permissions
															   try
															   {
																   GroupResource.checkGroupVisibility(context, userDetails, d.getForeignId());
																   yield true;
															   }
															   catch (Exception e)
															   {
																   yield false;
															   }
														   }
														   // Check dataset permissions
														   case dataset -> availableIds.contains(d.getForeignId());
														   // Check availability of referenced items
														   case germplasm -> germplasmIds.contains(d.getForeignId());
														   case experiment -> experimentId.contains(d.getForeignId());
														   default -> true;
													   };
												   }).toList();

			if (CollectionUtils.isEmpty(filtered))
				throw new NotFoundException();

			for (Publicationdata publication : filtered)
			{
				PublicationdataRecord record = context.newRecord(PUBLICATIONDATA, publication);
				record.store();
			}

			return true;
		}
	}

	@DELETE
	@Path("/{publicationId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean deletePublication(@PathParam("publicationId") Integer publicationId)
			throws SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			return context.deleteFrom(PUBLICATIONS)
						  .where(PUBLICATIONS.ID.eq(publicationId))
						  .execute() > 0;
		}
	}

	@DELETE
	@Path("/{publicationId}/reference/database")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean deletePublicationReferenceDatabase(@PathParam("publicationId") Integer publicationId)
			throws SQLException
	{
		return delete(publicationId, PublicationdataReferenceType.database, null);
	}

	@DELETE
	@Path("/{publicationId:\\d+}/reference/{referenceType}/{referenceId:\\d+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean deletePublicationReferenceById(@PathParam("publicationId") Integer publicationId, @PathParam("referenceType") String referenceType, @PathParam("referenceId") Integer referenceId)
			throws SQLException
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
			throws SQLException
	{
		if (publicationId == null || (referenceType != PublicationdataReferenceType.database && referenceId == null))
			throw new BadRequestException();

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
