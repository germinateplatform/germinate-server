package jhi.germinate.server.resource.comment;

import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.Comments;
import jhi.germinate.server.database.codegen.tables.records.CommentsRecord;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
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
	public Integer putComment(Comments comment)
		throws IOException, SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		if (comment.getUserId() == null || !Objects.equals(comment.getUserId(), userDetails.getId()))
		{
			resp.sendError(Response.Status.FORBIDDEN.getStatusCode());
			return null;
		}
		if (StringUtils.isEmpty(comment.getDescription()) || comment.getCommenttypeId() == null || comment.getReferenceId() == null || comment.getId() != null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			comment.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			comment.setUpdatedOn(new Timestamp(System.currentTimeMillis()));

			CommentsRecord record = context.newRecord(COMMENTS, comment);
			record.store();
			return record.getId();
		}
	}

	@DELETE
	@Path("/{commentId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean deleteComment(@PathParam("commentId") Integer commentId)
		throws IOException, SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		if (commentId == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return false;
		}

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
				return false;
			}
			else
			{
				return dbRecord.delete() == 1;
			}
		}
	}
}
