package jhi.germinate.server.resource.projects;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.ProjectStats;
import jhi.germinate.resource.enums.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.records.*;
import jhi.germinate.server.resource.images.ImageResource;
import jhi.germinate.server.util.*;
import org.glassfish.jersey.media.multipart.*;
import org.jooq.DSLContext;

import java.io.*;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.UUID;

import static jhi.germinate.server.database.codegen.tables.Datasets.DATASETS;
import static jhi.germinate.server.database.codegen.tables.Experiments.EXPERIMENTS;
import static jhi.germinate.server.database.codegen.tables.Images.IMAGES;
import static jhi.germinate.server.database.codegen.tables.Imagetypes.IMAGETYPES;
import static jhi.germinate.server.database.codegen.tables.Projectcollaborators.PROJECTCOLLABORATORS;
import static jhi.germinate.server.database.codegen.tables.Projectgroups.PROJECTGROUPS;
import static jhi.germinate.server.database.codegen.tables.Projectpublications.PROJECTPUBLICATIONS;
import static jhi.germinate.server.database.codegen.tables.Projects.PROJECTS;

@Path("project")
public class ProjectResource
{
	@GET
	@Path("/{projectId:\\d+}/stats")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProjectStats(@PathParam("projectId") Integer projectId)
			throws SQLException
	{
		if (projectId == null)
			return Response.status(Response.Status.BAD_REQUEST).build();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			ProjectsRecord project = context.selectFrom(PROJECTS).where(PROJECTS.ID.eq(projectId)).fetchAny();

			if (project == null)
				return Response.status(Response.Status.NOT_FOUND).build();

			ProjectStats result = new ProjectStats();
			result.setGroupCount(context.selectCount().from(PROJECTGROUPS).where(PROJECTGROUPS.PROJECT_ID.eq(projectId)).fetchOneInto(Integer.class));
			result.setDatasetCount(context.selectCount().from(DATASETS).leftJoin(EXPERIMENTS).on(EXPERIMENTS.ID.eq(DATASETS.EXPERIMENT_ID)).where(EXPERIMENTS.PROJECT_ID.eq(projectId)).fetchOneInto(Integer.class));
			result.setPublicationCount(context.selectCount().from(PROJECTPUBLICATIONS).where(PROJECTPUBLICATIONS.PROJECT_ID.eq(projectId)).fetchOneInto(Integer.class));
			result.setCollaboratorCount(context.selectCount().from(PROJECTCOLLABORATORS).where(PROJECTCOLLABORATORS.PROJECT_ID.eq(projectId)).fetchOneInto(Integer.class));

			return Response.ok(result).build();
		}
	}

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.DATA_CURATOR)
	public Response postProject(@FormDataParam("name") String name,
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
			return Response.status(Response.Status.BAD_REQUEST).build();

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
					return Response.status(Response.Status.BAD_REQUEST).build();

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

			return Response.status(Response.Status.OK).build();
		}
	}

	@PATCH
	@Path("/{projectId:\\d+}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.DATA_CURATOR)
	public Response patchProject(@PathParam("projectId") Integer projectId,
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
			return Response.status(Response.Status.BAD_REQUEST).build();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			ProjectsRecord project = context.selectFrom(PROJECTS).where(PROJECTS.ID.eq(projectId)).fetchAny();

			if (project == null)
				return Response.status(Response.Status.NOT_FOUND).build();

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
					return Response.status(Response.Status.BAD_REQUEST).build();

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

			return Response.status(Response.Status.OK).build();
		}
	}

	@DELETE
	@Path("/{projectId:\\d+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.DATA_CURATOR)
	public Response deleteProject(@PathParam("projectId") Integer projectId)
			throws SQLException
	{
		if (projectId == null)
			return Response.status(Response.Status.BAD_REQUEST).build();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			return Response.ok(context.deleteFrom(PROJECTS).where(PROJECTS.ID.eq(projectId)).execute() > 0).build();
		}
	}
}
