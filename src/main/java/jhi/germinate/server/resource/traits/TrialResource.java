package jhi.germinate.server.resource.traits;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.TrialCreationDetails;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableDatasets;
import jhi.germinate.server.database.codegen.tables.records.TrialsetupRecord;
import jhi.germinate.server.database.pojo.ImportStatus;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static jhi.germinate.server.database.codegen.tables.Germinatebase.GERMINATEBASE;
import static jhi.germinate.server.database.codegen.tables.Trialsetup.TRIALSETUP;

@Path("dataset/data/trial")
@Secured(UserType.DATA_CURATOR)
public class TrialResource extends ContextResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postTrial(TrialCreationDetails details)
			throws SQLException
	{
		if (details == null || details.getDatasetId() == null || CollectionUtils.isEmpty(details.getPlots()))
			return Response.status(Response.Status.BAD_REQUEST).build();

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			ViewTableDatasets dataset = DatasetTableResource.getDatasetForId(details.getDatasetId(), req, userDetails, false);

			if (dataset == null)
				return Response.status(Response.Status.FORBIDDEN).build();

			Set<String> germplasmNames = new HashSet<>();
			Set<String> rowColumn = new HashSet<>();
			boolean[] validIndices = {true};
			details.getPlots().stream().forEach(p -> {
				germplasmNames.add(p.getGermplasm());
				if (p.getRow() != null && p.getColumn() != null)
				{
					String concat = p.getRow() + "|" + p.getColumn();

					if (rowColumn.contains(concat))
					{
						validIndices[0] = false;
					}

					rowColumn.add(concat);
				}
			});

			if (!validIndices[0])
				return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), ImportStatus.TRIALS_ROW_COL_MISMATCH.name()).build();

			Map<String, Integer> germplasmMap = new HashMap<>();
			context.selectFrom(GERMINATEBASE).where(GERMINATEBASE.NAME.in(germplasmNames))
				   .forEach(g -> germplasmMap.put(g.getName(), g.getId()));

			if (germplasmMap.size() != germplasmNames.size())
				return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), ImportStatus.GENERIC_INVALID_GERMPLASM.name()).build();

			List<TrialsetupRecord> entries = details.getPlots().stream().map(p -> {
				TrialsetupRecord r = context.newRecord(TRIALSETUP);
				r.setGerminatebaseId(germplasmMap.get(p.getGermplasm()));
				if (!StringUtils.isEmpty(p.getRep()))
					r.setRep(p.getRep());
				else
					r.setRep("1");
				r.setTrialRow(p.getRow());
				r.setTrialColumn(p.getColumn());
				r.setDatasetId(dataset.getDatasetId());
				return r;
			}).collect(Collectors.toList());

			context.batchStore(entries)
				   .execute();

			return Response.ok().build();
		}
	}
}
