package jhi.germinate.server.resource.projects;

import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jhi.germinate.resource.ProjectStats;
import jhi.germinate.resource.enums.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.Groups;
import jhi.germinate.server.database.codegen.tables.records.*;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.resource.images.ImageResource;
import jhi.germinate.server.util.*;
import org.glassfish.jersey.media.multipart.*;
import org.jooq.*;

import java.io.*;
import java.io.File;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Datasets.DATASETS;
import static jhi.germinate.server.database.codegen.tables.Experiments.EXPERIMENTS;
import static jhi.germinate.server.database.codegen.tables.Groups.GROUPS;
import static jhi.germinate.server.database.codegen.tables.Images.IMAGES;
import static jhi.germinate.server.database.codegen.tables.Imagetypes.IMAGETYPES;
import static jhi.germinate.server.database.codegen.tables.Projectcollaborators.PROJECTCOLLABORATORS;
import static jhi.germinate.server.database.codegen.tables.Projectgroups.PROJECTGROUPS;
import static jhi.germinate.server.database.codegen.tables.Projectpublications.PROJECTPUBLICATIONS;
import static jhi.germinate.server.database.codegen.tables.Projects.PROJECTS;

@Path("project")
public class ProjectResource extends ContextResource
{
	@GET
	@Path("/{projectId:\\d+}/stats")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ProjectStats getProjectStats(@PathParam("projectId") Integer projectId)
			throws SQLException
	{
		if (projectId == null)
			throw new BadRequestException();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			ProjectsRecord project = context.selectFrom(PROJECTS).where(PROJECTS.ID.eq(projectId)).fetchAny();

			if (project == null)
				throw new NotFoundException();

			ProjectStats result = new ProjectStats();
			result.setGroupCount(context.selectCount().from(PROJECTGROUPS).where(PROJECTGROUPS.PROJECT_ID.eq(projectId)).fetchOneInto(Integer.class));
			result.setDatasetCount(context.selectCount().from(DATASETS).leftJoin(EXPERIMENTS).on(EXPERIMENTS.ID.eq(DATASETS.EXPERIMENT_ID)).where(EXPERIMENTS.PROJECT_ID.eq(projectId)).fetchOneInto(Integer.class));
			result.setPublicationCount(context.selectCount().from(PROJECTPUBLICATIONS).where(PROJECTPUBLICATIONS.PROJECT_ID.eq(projectId)).fetchOneInto(Integer.class));
			result.setCollaboratorCount(context.selectCount().from(PROJECTCOLLABORATORS).where(PROJECTCOLLABORATORS.PROJECT_ID.eq(projectId)).fetchOneInto(Integer.class));

			return result;
		}
	}

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.DATA_CURATOR)
	public boolean postProject(@FormDataParam("name") String name,
	                           @FormDataParam("description") String description,
	                           @FormDataParam("pageContent") String pageContent,
	                           @FormDataParam("externalUrl") String externalUrl,
	                           @FormDataParam("startDate") String startDate,
	                           @FormDataParam("endDate") String endDate,
	                           @FormDataParam("image") InputStream image,
	                           @FormDataParam("image") FormDataContentDisposition fileDetails)
			throws SQLException, IOException
	{
		if (StringUtils.isEmpty(name))
			throw new BadRequestException();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			ProjectsRecord project = context.newRecord(PROJECTS);
			project.setName(name);
			project.setDescription(description);
			project.setExternalUrl(externalUrl);
			project.setPageContent(pageContent);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			if (!StringUtils.isEmpty(startDate))
			{
				try
				{
					project.setStartDate(new Timestamp(sdf.parse(startDate).getTime()));
				}
				catch (Exception e)
				{
				}
			}
			if (!StringUtils.isEmpty(startDate))
			{
				try
				{
					project.setEndDate(new Timestamp(sdf.parse(endDate).getTime()));
				}
				catch (Exception e)
				{
				}
			}

			project.store();

			if (image != null)
			{
				ImagetypesRecord imageType = context.selectFrom(IMAGETYPES)
				                                    .where(IMAGETYPES.REFERENCE_TABLE.eq("projects"))
				                                    .fetchAny();

				File folder = new File(new File(new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images"), ImageResource.ImageType.projects.name()), "upload");
				folder.mkdirs();

				String itemName = fileDetails.getFileName();
				String uuid = UUID.randomUUID().toString();
				String extension = itemName.substring(itemName.lastIndexOf(".") + 1);
				File targetFile = new File(folder, uuid + "." + extension);

				if (!FileUtils.isSubDirectory(folder, targetFile))
					throw new BadRequestException();

				java.nio.file.Files.copy(image, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

				ImagesRecord imagesRecord = context.newRecord(IMAGES);
				imagesRecord.setForeignId(project.getId());
				imagesRecord.setImagetypeId(imageType.getId());
				imagesRecord.setDescription(targetFile.getName());
				imagesRecord.setPath("upload/" + targetFile.getName());
				imagesRecord.store();

				project.setImageId(imagesRecord.getId());
				project.store(PROJECTS.IMAGE_ID);
			}

			return true;
		}
	}

	@PATCH
	@Path("/{projectId:\\d+}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.DATA_CURATOR)
	public boolean patchProject(@PathParam("projectId") Integer projectId,
	                            @FormDataParam("name") String name,
	                            @FormDataParam("description") String description,
	                            @FormDataParam("pageContent") String pageContent,
	                            @FormDataParam("externalUrl") String externalUrl,
	                            @FormDataParam("startDate") String startDate,
	                            @FormDataParam("endDate") String endDate,
	                            @FormDataParam("image") InputStream image,
	                            @FormDataParam("image") FormDataContentDisposition fileDetails)
			throws SQLException, IOException
	{
		if (projectId == null || StringUtils.isEmpty(name))
			throw new BadRequestException();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			ProjectsRecord project = context.selectFrom(PROJECTS).where(PROJECTS.ID.eq(projectId)).fetchAny();

			if (project == null)
				throw new NotFoundException();

			project.setName(name);
			project.setDescription(description);
			project.setExternalUrl(externalUrl);
			project.setPageContent(pageContent);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

			if (!StringUtils.isEmpty(startDate))
			{
				try
				{
					project.setStartDate(new Timestamp(sdf.parse(startDate).getTime()));
				}
				catch (Exception e)
				{
					project.setStartDate(null);
				}
			}
			else
			{
				project.setStartDate(null);
			}
			if (!StringUtils.isEmpty(endDate))
			{
				try
				{
					project.setEndDate(new Timestamp(sdf.parse(endDate).getTime()));
				}
				catch (Exception e)
				{
					project.setEndDate(null);
				}
			}
			else
			{
				project.setEndDate(null);
			}

			if (image != null)
			{
				ImagetypesRecord imageType = context.selectFrom(IMAGETYPES)
				                                    .where(IMAGETYPES.REFERENCE_TABLE.eq("projects"))
				                                    .fetchAny();

				File folder = new File(new File(new File(PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL), "images"), ImageResource.ImageType.projects.name()), "upload");
				folder.mkdirs();

				String itemName = fileDetails.getFileName();
				String uuid = UUID.randomUUID().toString();
				String extension = itemName.substring(itemName.lastIndexOf(".") + 1);
				File targetFile = new File(folder, uuid + "." + extension);

				if (!FileUtils.isSubDirectory(folder, targetFile))
					throw new BadRequestException();

				java.nio.file.Files.copy(image, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

				ImagesRecord imagesRecord = context.newRecord(IMAGES);
				imagesRecord.setForeignId(project.getId());
				imagesRecord.setImagetypeId(imageType.getId());
				imagesRecord.setDescription(targetFile.getName());
				imagesRecord.setPath("upload/" + targetFile.getName());
				imagesRecord.store();

				project.setImageId(imagesRecord.getId());
			}
			else
			{
				project.setImageId(null);
			}

			project.store();

			return true;
		}
	}

	@DELETE
	@Path("/{projectId:\\d+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.DATA_CURATOR)
	public boolean deleteProject(@PathParam("projectId") Integer projectId)
			throws SQLException
	{
		if (projectId == null)
			throw new BadRequestException();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			return context.deleteFrom(PROJECTS).where(PROJECTS.ID.eq(projectId)).execute() > 0;
		}
	}

	@POST
	@Path("/{projectId:\\d+}/group")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.DATA_CURATOR)
	public boolean postProjectGroups(@PathParam("projectId") Integer projectId, List<Integer> groupIds)
			throws SQLException
	{
		if (projectId == null || CollectionUtils.isEmpty(groupIds))
			throw new BadRequestException();

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			// Only leave group ids they have access to
			groupIds.retainAll(context.select(GROUPS.ID).from(GROUPS).where(GROUPS.VISIBILITY.eq(true).or(GROUPS.CREATED_BY.eq(userDetails.getId()))).fetchInto(Integer.class));

			InsertValuesStep2<ProjectgroupsRecord, Integer, Integer> insertStep = context.insertInto(PROJECTGROUPS, PROJECTGROUPS.PROJECT_ID, PROJECTGROUPS.GROUP_ID);

			for (Integer groupId : groupIds)
				insertStep.values(projectId, groupId);

			return insertStep.onDuplicateKeyIgnore()
			                 .execute() > 0;
		}
	}

	@POST
	@Path("/{projectId:\\d+}/experiment")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.DATA_CURATOR)
	public boolean postProjectExperiments(@PathParam("projectId") Integer projectId, List<Integer> experimentIds)
			throws SQLException
	{
		if (projectId == null || CollectionUtils.isEmpty(experimentIds))
			throw new BadRequestException();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			return context.update(EXPERIMENTS).set(EXPERIMENTS.PROJECT_ID, projectId).where(EXPERIMENTS.ID.in(experimentIds)).execute() > 0;
		}
	}

	@DELETE
	@Path("/{projectId:\\d+}/group/{groupId:\\d+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.DATA_CURATOR)
	public boolean deleteProjectGroup(@PathParam("projectId") Integer projectId, @PathParam("groupId") Integer groupId)
			throws SQLException
	{
		if (projectId == null || groupId == null)
			throw new BadRequestException();

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			// Check they have permissions to use this group
			Groups group = context.selectFrom(GROUPS).where(GROUPS.VISIBILITY.eq(true).or(GROUPS.CREATED_BY.eq(userDetails.getId()))).fetchAnyInto(Groups.class);

			if (group != null)
				return context.deleteFrom(PROJECTGROUPS).where(PROJECTGROUPS.PROJECT_ID.eq(projectId).and(PROJECTGROUPS.GROUP_ID.eq(groupId))).execute() > 0;
			else
				return false;
		}
	}

	@DELETE
	@Path("/{projectId:\\d+}/experiment/{experimentId:\\d+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.DATA_CURATOR)
	public boolean deleteProjectExperiment(@PathParam("projectId") Integer projectId, @PathParam("experimentId") Integer experimentId)
			throws SQLException
	{
		if (projectId == null || experimentId == null)
			throw new BadRequestException();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			return context.update(EXPERIMENTS).setNull(EXPERIMENTS.PROJECT_ID).where(EXPERIMENTS.ID.eq(experimentId)).execute() > 0;
		}
	}
}
