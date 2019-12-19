package jhi.germinate.server.resource.comment;

import org.jooq.DSLContext;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.Objects;

import jhi.germinate.server.Database;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.database.tables.pojos.Comments;
import jhi.germinate.server.database.tables.records.CommentsRecord;
import jhi.germinate.server.resource.BaseServerResource;
import jhi.germinate.server.util.StringUtils;

import static jhi.germinate.server.database.tables.Comments.*;

/**
 * @author Sebastian Raubach
 */
public class CommentResource extends BaseServerResource
{
	private Integer commentId;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			this.commentId = Integer.parseInt(getRequestAttributes().get("commentId").toString());
		}
		catch (NullPointerException | NumberFormatException e)
		{
		}
	}

	@Delete("json")
	@MinUserType(UserType.AUTH_USER)
	public boolean deleteJson()
	{
		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());

		if (commentId == null)
			throw new ResourceException(org.restlet.data.Status.CLIENT_ERROR_BAD_REQUEST, "Missing id");

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			CommentsRecord dbRecord = context.selectFrom(COMMENTS)
											 .where(COMMENTS.ID.eq(commentId))
											 .and(COMMENTS.USER_ID.eq(userDetails.getId()))
											 .fetchOneInto(CommentsRecord.class);

			// If it's null, then the id doesn't exist or the user doesn't have access
			if (dbRecord == null)
				throw new ResourceException(org.restlet.data.Status.CLIENT_ERROR_NOT_FOUND);
			else
				return dbRecord.delete() == 1;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}

	@Put("json")
	@MinUserType(UserType.AUTH_USER)
	public Integer putJson(Comments comment)
	{
		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());

		if (comment.getUserId() == null || !Objects.equals(comment.getUserId(), userDetails.getId()))
			throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN);
		if (StringUtils.isEmpty(comment.getDescription()) || comment.getCommenttypeId() == null || comment.getReferenceId() == null || comment.getId() != null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			comment.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			comment.setUpdatedOn(new Timestamp(System.currentTimeMillis()));

			CommentsRecord record = context.newRecord(COMMENTS, comment);
			record.store();
			return record.getId();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
