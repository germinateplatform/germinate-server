package jhi.germinate.server.resource.datasets;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.*;
import jhi.germinate.server.resource.PaginatedServerResource;
import org.jooq.*;
import org.restlet.*;
import org.restlet.resource.*;

import java.util.*;

import static jhi.germinate.server.database.codegen.tables.ViewTableCollaborators.*;

/**
 * @author Sebastian Raubach
 */
public class CollaboratorTableResource extends PaginatedServerResource
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
		try (DSLContext context = Database.getContext())
		{
			ViewTableDatasets dataset = DatasetTableResource.getDatasetForId(datasetId, getRequest(), getResponse(), false);

			if (dataset != null)
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
				return new PaginatedResult<>(new ArrayList<>(), 0);
			}
		}
	}

	public static List<ViewTableCollaborators> getCollaboratorsForDataset(int datasetId, Request req, Response resp)
	{
		try (DSLContext context = Database.getContext())
		{
			ViewTableDatasets dataset = DatasetTableResource.getDatasetForId(datasetId, req, resp, false);

			if (dataset != null)
			{
				return context.select()
							  .from(VIEW_TABLE_COLLABORATORS)
							  .where(VIEW_TABLE_COLLABORATORS.DATASET_ID.eq(datasetId)).fetch()
							  .into(ViewTableCollaborators.class);
			}
			else
			{
				return null;
			}
		}
	}
}