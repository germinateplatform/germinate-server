package jhi.germinate.server.resource.datasets;

import org.jooq.*;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.List;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.tables.pojos.*;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.CollectionUtils;

import static jhi.germinate.server.database.tables.ViewTableCollaborators.*;

/**
 * @author Sebastian Raubach
 */
public class CollaboratorTableResource extends PaginatedServerResource implements FilteredResource
{
	private Integer datasetId = null;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			this.datasetId = Integer.parseInt(getRequestAttributes().get("datasetId").toString());
		}
		catch (NullPointerException | NumberFormatException e)
		{
		}
	}

	@Post("json")
	public PaginatedResult<List<ViewTableCollaborators>> getJson(PaginatedRequest request)
	{
		processRequest(request);
		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			List<ViewTableDatasets> datasets = DatasetTableResource.getDatasetForId(datasetId, getRequest(), getResponse(), false);

			if (!CollectionUtils.isEmpty(datasets))
			{
				SelectSelectStep<Record> select = context.select();

				if (previousCount == -1)
					select.hint("SQL_CALC_FOUND_ROWS");

				SelectJoinStep<Record> from = select.from(VIEW_TABLE_COLLABORATORS);

				// Filter here!
				filter(from, filters);

				from.where(VIEW_TABLE_COLLABORATORS.DATASET_ID.eq(datasetId));

				List<ViewTableCollaborators> result = setPaginationAndOrderBy(from)
					.fetch()
					.into(ViewTableCollaborators.class);

				long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

				return new PaginatedResult<>(result, count);
			}
			else
			{
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}