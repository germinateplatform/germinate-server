package jhi.germinate.server.resource.feedback;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.enums.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.enums.*;
import jhi.germinate.server.database.codegen.tables.pojos.Userfeedback;
import jhi.germinate.server.database.codegen.tables.records.UserfeedbackRecord;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.*;
import net.coobird.thumbnailator.Thumbnails;
import net.logicsquad.nanocaptcha.content.LatinContentProducer;
import net.logicsquad.nanocaptcha.image.ImageCaptcha;
import net.logicsquad.nanocaptcha.image.backgrounds.FlatColorBackgroundProducer;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.jooq.DSLContext;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

import static jhi.germinate.server.database.codegen.tables.Userfeedback.*;

@Path("feedback")
public class FeedbackResource extends ContextResource
{
	private static final Map<String, String> captchaMap = Collections.synchronizedMap(new LinkedHashMap<>(1000)
	{
		@Override
		protected synchronized boolean removeEldestEntry(Map.Entry<String, String> entry)
		{
			return size() > 1000;
		}
	});

	@GET
	@Path("/{id:\\d+}/mark")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured(UserType.ADMIN)
	public Response getFeedbackMarked(@PathParam("id") Integer id)
		throws SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			UserfeedbackRecord uf = context.selectFrom(USERFEEDBACK)
										   .where(USERFEEDBACK.ID.eq(id))
										   .and(USERFEEDBACK.IS_NEW.eq(true))
										   .fetchAny();

			if (uf == null)
				return Response.status(Response.Status.NOT_FOUND).build();

			uf.setIsNew(false);
			uf.store(USERFEEDBACK.IS_NEW);

			return Response.ok().build();
		}
	}

	@GET
	@Path("/{id:\\d+}/img")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({"image/png", "image/jpeg", "image/*"})
	@Secured
	@PermitAll
	public Response getFeedbackImage(@PathParam("id") Integer id, @QueryParam("size") String size, @QueryParam("token") String token)
		throws SQLException, IOException
	{
		if (id == null || StringUtils.isEmpty(token))
			return Response.status(Response.Status.BAD_REQUEST).build();

		AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);

		// If it's not a template image, check the image token
		if (mode == AuthenticationMode.FULL)
		{
			if (StringUtils.isEmpty(token) || !AuthenticationFilter.isValidImageToken(token))
			{
				resp.sendError(Response.Status.FORBIDDEN.getStatusCode());
				return null;
			}
		}

		AuthenticationFilter.UserDetails userDetails = AuthenticationFilter.getDetailsFromImageToken(token);

		if (userDetails == null || !userDetails.isAtLeast(UserType.ADMIN))
			return Response.status(Response.Status.FORBIDDEN).build();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			Userfeedback uf = context.selectFrom(USERFEEDBACK).where(USERFEEDBACK.ID.eq(id)).fetchAnyInto(Userfeedback.class);

			if (uf == null || uf.getImage() == null)
				return Response.status(Response.Status.NOT_FOUND).build();

			if (Objects.equals(size, "small"))
			{
				try (InputStream is = new ByteArrayInputStream(uf.getImage()))
				{
					BufferedImage bi = ImageIO.read(is);

					return Response.ok((StreamingOutput) output -> Thumbnails.of(bi)
																			 .height(500)
																			 .addFilter(new NoScaleUpResizer(bi.getWidth(), bi.getHeight()))
																			 .keepAspectRatio(true)
																			 .outputFormat("png")
																			 .toOutputStream(output))
								   .type("image/png")
								   .build();

				}
				catch (IOException e)
				{
					e.printStackTrace();
					Logger.getLogger("").severe(e.getLocalizedMessage());
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
				}
			}
			else
			{
				return Response.ok(new ByteArrayInputStream(uf.getImage()))
							   .type("image/png")
							   .build();
			}
		}
	}

	@POST
	@Path("/{uuid}")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured
	@PermitAll
	public Response postFeedback(@PathParam("uuid") String uuid,
								 @FormDataParam("captcha") String captcha,
								 @FormDataParam("content") String content,
								 @FormDataParam("image") InputStream image,
								 @FormDataParam("pageUrl") String pageUrl,
								 @FormDataParam("contactEmail") String contactEmail,
								 @FormDataParam("feedbackType") UserfeedbackFeedbackType feedbackType,
								 @FormDataParam("severity") UserfeedbackSeverity severity)
		throws SQLException, IOException
	{
		// Synchronize on the map to be sure
		synchronized (captchaMap)
		{
			if (StringUtils.isEmpty(uuid) || StringUtils.isEmpty(captcha) || StringUtils.isEmpty(contactEmail) || StringUtils.isEmpty(content) || StringUtils.isEmpty(pageUrl) || image == null || severity == null || feedbackType == null)
				return Response.status(Response.Status.BAD_REQUEST).build();

			String mapCaptcha = captchaMap.get(uuid);

			AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

			// Check if the captcha is correct
			if (StringUtils.isEmpty(mapCaptcha) || !Objects.equals(mapCaptcha, captcha))
				return Response.status(Response.Status.NOT_FOUND).build();

			try (Connection conn = Database.getConnection())
			{
				// Convert Base64 to byte[]
				byte[] imageData = IOUtils.toByteArray(image);

				DSLContext context = Database.getContext(conn);
				UserfeedbackRecord record = context.newRecord(USERFEEDBACK);
				record.setContent(content);
				record.setImage(imageData);
				record.setPageUrl(pageUrl);
				if (userDetails != null && userDetails.getId() != null)
					record.setUserId(userDetails.getId());
				record.setContactEmail(contactEmail);
				record.setSeverity(severity);
				record.setFeedbackType(feedbackType);
				record.setIsNew(true);
				record.setCreatedOn(new Timestamp(System.currentTimeMillis()));
				record.setUpdatedOn(new Timestamp(System.currentTimeMillis()));

				captchaMap.remove(uuid);

				if (record.store() > 0)
					return Response.status(Response.Status.OK).build();
				else
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
			}
		}
	}

	@GET
	@Path("/{uuid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({"image/png"})
	@Secured
	@PermitAll
	public Response getFeedbackCaptcha(@PathParam("uuid") String uuid)
	{
		try
		{
			// Create a captcha with noise
			ImageCaptcha imageCaptcha = new ImageCaptcha.Builder(200, 50)
				.addNoise()
				.addContent(new LatinContentProducer(7))
				.addBackground(new FlatColorBackgroundProducer(Color.WHITE))
				.build();

			// Write to image
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(imageCaptcha.getImage(), "png", baos);
			byte[] imageData = baos.toByteArray();

			// Remember captcha mapped to the uuid
			captchaMap.put(uuid, imageCaptcha.getContent());

			// Send image
			return Response.ok(new ByteArrayInputStream(imageData)).build();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Logger.getLogger("").severe(e.getLocalizedMessage());
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						   .build();
		}
	}
}
