package jhi.germinate.server.resource.germplasm;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.util.List;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.util.*;

import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;

/**
 * @author Sebastian Raubach
 */
public class GermplasmTableIdResource extends GermplasmBaseResource
{
	public static final String PARAM_NAMES_FROM_FILE = "namesFromFile";

	private String namesFromFile;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		this.namesFromFile = getQueryValue(PARAM_NAMES_FROM_FILE);
	}

	@Post("json")
	public PaginatedResult<List<Integer>> getJson(PaginatedRequest request)
	{
		processRequest(request);
		currentPage = 0;
		pageSize = Integer.MAX_VALUE;
		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			SelectJoinStep<Record1<Integer>> from = getGermplasmIdQuery(context);

			// Add an additional filter based on the names in the file uploaded from CurlyWhirly
			if (!StringUtils.isEmpty(namesFromFile))
			{
				try
				{
					List<String> names = Files.readAllLines(getTempDir(namesFromFile).toPath());

					if (!CollectionUtils.isEmpty(names))
						from.where(DSL.field(GERMINATEBASE.getName() + "." + GERMINATEBASE.ID.getName()).in(names));
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}

			// Filter here!
			filter(from, adjustFilter(filters));

			List<Integer> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(Integer.class);

			return new PaginatedResult<>(result, result.size());
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
