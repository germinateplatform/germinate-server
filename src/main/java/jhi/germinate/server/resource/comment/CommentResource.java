package jhi.germinate.server.resource.comment;

import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.Comments;
import jhi.germinate.server.database.codegen.tables.records.CommentsRecord;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Objects;

import static jhi.germinate.server.database.codegen.tables.Comments.*;

@Path("comment")
@Secured({UserType.AUTH_USER})
public class CommentResource
{
	@Context
	protected SecurityContext     securityContext;
	@Context
	protected HttpServletResponse resp;

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response putComment(Comments comment)
		throws IOException, SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		if (comment.getUserId() == null || !Objects.equals(comment.getUserId(), userDetails.getId()))
			return Response.status(Response.Status.FORBIDDEN.getStatusCode()).build();
		if (StringUtils.isEmpty(comment.getDescription()) || comment.getCommenttypeId() == null || comment.getReferenceId() == null || comment.getId() != null)
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			comment.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			comment.setUpdatedOn(new Timestamp(System.currentTimeMillis()));

			CommentsRecord record = context.newRecord(COMMENTS, comment);
			record.store();
			return Response.ok(record.getId()).build();
		}
	}

	@DELETE
	@Path("/{commentId:\\d+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteComment(@PathParam("commentId") Integer commentId)
		throws IOException, SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		if (commentId == null)
			return Response.status(Response.Status.BAD_REQUEST).build();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			CommentsRecord dbRecord = context.selectFrom(COMMENTS)
											 .where(COMMENTS.ID.eq(commentId))
											 .and(COMMENTS.USER_ID.eq(userDetails.getId()))
											 .fetchAnyInto(CommentsRecord.class);

			// If it's null, then the id doesn't exist or the user doesn't have access
			if (dbRecord == null)
			{
				resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
				return Response.ok(false).build();
			}
			else
			{
				return Response.ok(dbRecord.delete() == 1).build();
			}
		}
	}
}
