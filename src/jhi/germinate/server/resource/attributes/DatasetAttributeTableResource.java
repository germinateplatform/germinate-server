package jhi.germinate.server.resource.attributes;

import org.jooq.*;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.List;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.CustomVerifier;
import jhi.germinate.server.resource.*;

import static jhi.germinate.server.database.tables.Datasets.*;
import static jhi.germinate.server.database.tables.ViewTableAttributes.*;

/**
 * @author Sebastian Raubach
 */
public class DatasetAttributeTableResource extends PaginatedServerResource implements FilteredResource
{
	private Integer datasetId;

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
	public PaginatedResult<List<DatasetAttributeData>> getJson(PaginatedRequest request)
	{
		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());

		if (datasetId == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		processRequest(request);
		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			SelectSelectStep<Record> select = context.select(
				DATASETS.ID.as("dataset_id"),
				DATASETS.NAME.as("dataset_name"),
				DATASETS.DESCRIPTION.as("dataset_description"),
				VIEW_TABLE_ATTRIBUTES.asterisk()
			);

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(DATASETS)
												.leftJoin(VIEW_TABLE_ATTRIBUTES).on(VIEW_TABLE_ATTRIBUTES.TARGET_TABLE.eq("datasets").and(DATASETS.ID.eq(VIEW_TABLE_ATTRIBUTES.FOREIGN_ID)));

			from.where(DATASETS.ID.eq(datasetId));

			// Filter here!
			filter(from, filters);

			List<DatasetAttributeData> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(DatasetAttributeData.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
