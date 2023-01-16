package jhi.germinate.server.resource.datasets;

import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.*;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.impl.DSL;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.*;
import java.util.Date;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Climates.*;
import static jhi.germinate.server.database.codegen.tables.Datasetlocations.*;
import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;
import static jhi.germinate.server.database.codegen.tables.Groupmembers.*;
import static jhi.germinate.server.database.codegen.tables.Groups.*;
import static jhi.germinate.server.database.codegen.tables.Locations.*;
import static jhi.germinate.server.database.codegen.tables.Phenotypes.*;
import static jhi.germinate.server.database.codegen.tables.Units.*;

@Path("dataset/crosscomparison")
@Secured
@PermitAll
public class DatasetCrossDataTypeResource extends ContextResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response postDatasetCrossDataType(DatasetCrossDataTypeRequest request)
		throws IOException, SQLException
	{
		if (request == null || request.getFirst() == null || request.getSecond() == null || request.getFirst().getType() == null || request.getSecond().getType() == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		DatasetCrossDataTypeRequest.Config first = request.getFirst();
		DatasetCrossDataTypeRequest.Config second = request.getSecond();

		// Get the datasets the user has access to
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();
		List<Integer> allDatasetIds = DatasetTableResource.getDatasetIdsForUser(req, userDetails, null);
		List<Integer> firstDatasetIds = new ArrayList<>();
		List<Integer> secondDatasetIds = new ArrayList<>();

		if (!CollectionUtils.isEmpty(first.getDatasetIds()))
		{
			// If datasets were requested, restrict them to the available ones
			firstDatasetIds.addAll(Arrays.asList(first.getDatasetIds()));
			firstDatasetIds.retainAll(allDatasetIds);
		}
		else
		{
			// Else use all available datasets
			firstDatasetIds.addAll(allDatasetIds);
		}

		if (!CollectionUtils.isEmpty(second.getDatasetIds()))
		{
			// If datasets were requested, restrict them to the available ones
			secondDatasetIds.addAll(Arrays.asList(second.getDatasetIds()));
			secondDatasetIds.retainAll(allDatasetIds);
		}
		else
		{
			// Else use all available datasets
			secondDatasetIds.addAll(allDatasetIds);
		}

		// Set them back into the request so that we can access them from there
		first.setDatasetIds(firstDatasetIds.toArray(new Integer[0]));
		second.setDatasetIds(secondDatasetIds.toArray(new Integer[0]));

		File resultFile = ResourceUtils.createTempFile(null, "comparison-" + DateTimeUtils.getFormattedDateTime(new Date()), ".txt", false);

		try (Connection conn = Database.getConnection(true);
			 BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultFile), StandardCharsets.UTF_8)))
		{
			DSLContext context = Database.getContext(conn);
			SelectConditionStep<?> step = null;

			DatasetCrossDataTypeRequest.DataType f = first.getType();
			DatasetCrossDataTypeRequest.DataType s = second.getType();

			if (f == DatasetCrossDataTypeRequest.DataType.GERMPLASM_COLUMN && s == DatasetCrossDataTypeRequest.DataType.GERMPLASM_COLUMN)
			{
				if (StringUtils.isEmpty(first.getColumnName()) || StringUtils.isEmpty(second.getColumnName()))
				{
					resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
					return null;
				}

				String firstColumnName = Filter.getSafeColumn(first.getColumnName());
				String secondColumnName = Filter.getSafeColumn(second.getColumnName());

				ViewTableGermplasmDeprecated x = ViewTableGermplasmDeprecated.VIEW_TABLE_GERMPLASM_DEPRECATED.as("x");
				ViewTableGermplasmDeprecated y = ViewTableGermplasmDeprecated.VIEW_TABLE_GERMPLASM_DEPRECATED.as("y");

				Optional<Field<?>> firstField = x.fieldStream().filter(tf -> Objects.equals(tf.getName(), firstColumnName))
												  .findAny();
				Optional<Field<?>> secondField = y.fieldStream().filter(tf -> Objects.equals(tf.getName(), secondColumnName))
											.findAny();

				if (firstField.isEmpty() || secondField.isEmpty())
				{
					resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
					return null;
				}

				String firstDisplayName = firstColumnName.replace("_", " ");
				firstDisplayName = firstDisplayName.substring(0, 1).toUpperCase() + firstDisplayName.substring(1);
				String secondDisplayName = secondColumnName.replace("_", " ");
				secondDisplayName = secondDisplayName.substring(0, 1).toUpperCase() + secondDisplayName.substring(1);

				step = context.select(
					GERMINATEBASE.NAME.as("name"),
					GERMINATEBASE.ID.as("dbId"),
					GERMINATEBASE.GENERAL_IDENTIFIER.as("general_identifier"),
					firstField.get().as(firstDisplayName),
					secondField.get().as(secondDisplayName)
				).from(GERMINATEBASE)
							  .leftJoin(x).on(x.GERMPLASM_ID.eq(GERMINATEBASE.ID))
							  .leftJoin(y).on(y.GERMPLASM_ID.eq(GERMINATEBASE.ID))
				.where(DSL.val(1).eq(1));

				addFiltering(step, userDetails, first, second, x.GERMPLASM_ID, y.GERMPLASM_ID, null, null);
			}
			else if ((f == DatasetCrossDataTypeRequest.DataType.GERMPLASM_COLUMN && s == DatasetCrossDataTypeRequest.DataType.CLIMATE)
				|| (f == DatasetCrossDataTypeRequest.DataType.CLIMATE && s == DatasetCrossDataTypeRequest.DataType.GERMPLASM_COLUMN))
			{
				DatasetCrossDataTypeRequest.Config climate = first.getType() == DatasetCrossDataTypeRequest.DataType.CLIMATE ? first : second;
				DatasetCrossDataTypeRequest.Config germplasm = first.getType() == DatasetCrossDataTypeRequest.DataType.GERMPLASM_COLUMN ? first : second;

				if (climate.getId() == null || StringUtils.isEmpty(germplasm.getColumnName()))
				{
					resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
					return null;
				}

				String germplasmColumn = Filter.getSafeColumn(germplasm.getColumnName());

				Map<Integer, String> climates = getClimateMapping(context, climate.getId());

				Climatedata x = Climatedata.CLIMATEDATA.as("x");
				ViewTableGermplasmDeprecated y = ViewTableGermplasmDeprecated.VIEW_TABLE_GERMPLASM_DEPRECATED.as("y");

				Optional<Field<?>> field = y.fieldStream().filter(tf -> Objects.equals(tf.getName(), germplasmColumn))
											.findAny();

				if (field.isEmpty())
				{
					resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
					return null;
				}

				String displayName = germplasmColumn.replace("_", " ");
				displayName = displayName.substring(0, 1).toUpperCase() + displayName.substring(1);

				step = context.select(
					GERMINATEBASE.NAME.as("name"),
					GERMINATEBASE.ID.as("dbId"),
					GERMINATEBASE.GENERAL_IDENTIFIER.as("general_identifier"),
					x.CLIMATE_VALUE.as(climates.get(climate.getId())),
					field.get().as(displayName)
				).from(GERMINATEBASE)
							  .leftJoin(x).on(x.LOCATION_ID.eq(GERMINATEBASE.LOCATION_ID))
							  .leftJoin(y).on(y.GERMPLASM_ID.eq(GERMINATEBASE.ID))
							  .where(x.CLIMATE_ID.eq(climate.getId()));

				addFiltering(step, userDetails, climate, germplasm, x.LOCATION_ID, y.GERMPLASM_ID, x.DATASET_ID, x.DATASET_ID);
			}
			else if ((f == DatasetCrossDataTypeRequest.DataType.GERMPLASM_COLUMN && s == DatasetCrossDataTypeRequest.DataType.TRAIT)
				|| (f == DatasetCrossDataTypeRequest.DataType.TRAIT && s == DatasetCrossDataTypeRequest.DataType.GERMPLASM_COLUMN))
			{
				DatasetCrossDataTypeRequest.Config trait = first.getType() == DatasetCrossDataTypeRequest.DataType.TRAIT ? first : second;
				DatasetCrossDataTypeRequest.Config germplasm = first.getType() == DatasetCrossDataTypeRequest.DataType.GERMPLASM_COLUMN ? first : second;

				if (trait.getId() == null || StringUtils.isEmpty(germplasm.getColumnName()))
				{
					resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
					return null;
				}

				String germplasmColumn = Filter.getSafeColumn(germplasm.getColumnName());

				Map<Integer, String> traits = getTraitMapping(context, trait.getId());

				Phenotypedata x = Phenotypedata.PHENOTYPEDATA.as("x");
				ViewTableGermplasmDeprecated y = ViewTableGermplasmDeprecated.VIEW_TABLE_GERMPLASM_DEPRECATED.as("y");

				Optional<Field<?>> field = y.fieldStream().filter(tf -> Objects.equals(tf.getName(), germplasmColumn))
											.findAny();

				if (field.isEmpty())
				{
					resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
					return null;
				}

				String displayName = germplasmColumn.replace("_", " ");
				displayName = displayName.substring(0, 1).toUpperCase() + displayName.substring(1);

				step = context.select(
					GERMINATEBASE.NAME.as("name"),
					GERMINATEBASE.ID.as("dbId"),
					GERMINATEBASE.GENERAL_IDENTIFIER.as("general_identifier"),
					x.PHENOTYPE_VALUE.as(traits.get(trait.getId())),
					field.get().as(displayName)
				).from(GERMINATEBASE)
							  .leftJoin(x).on(x.GERMINATEBASE_ID.eq(GERMINATEBASE.ID))
							  .leftJoin(y).on(y.GERMPLASM_ID.eq(GERMINATEBASE.ID))
							  .where(x.PHENOTYPE_ID.eq(trait.getId()));

				addFiltering(step, userDetails, trait, germplasm, x.GERMINATEBASE_ID, y.GERMPLASM_ID, x.DATASET_ID, x.DATASET_ID);
			}
			else if (f == DatasetCrossDataTypeRequest.DataType.TRAIT && s == DatasetCrossDataTypeRequest.DataType.TRAIT)
			{
				if (first.getId() == null || second.getId() == null)
				{
					resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
					return null;
				}

				Map<Integer, String> traits = getTraitMapping(context, first.getId(), second.getId());

				// Get the actual data.
				Phenotypedata x = Phenotypedata.PHENOTYPEDATA.as("x");
				Phenotypedata y = Phenotypedata.PHENOTYPEDATA.as("y");
				step = context.select(
					GERMINATEBASE.NAME.as("name"),
					GERMINATEBASE.ID.as("dbId"),
					GERMINATEBASE.GENERAL_IDENTIFIER.as("general_identifier"),
					x.PHENOTYPE_VALUE.as(traits.get(first.getId())),
					y.PHENOTYPE_VALUE.as(traits.get(second.getId()))
				).from(GERMINATEBASE)
							  .leftJoin(x).on(x.GERMINATEBASE_ID.eq(GERMINATEBASE.ID))
							  .leftJoin(y).on(y.GERMINATEBASE_ID.eq(GERMINATEBASE.ID))
							  .where(x.PHENOTYPE_ID.eq(first.getId()))
							  .and(y.PHENOTYPE_ID.eq(second.getId()));

				addFiltering(step, userDetails, first, second, x.GERMINATEBASE_ID, y.GERMINATEBASE_ID, x.DATASET_ID, y.DATASET_ID);
			}
			else if ((f == DatasetCrossDataTypeRequest.DataType.TRAIT && s == DatasetCrossDataTypeRequest.DataType.CLIMATE)
				|| (f == DatasetCrossDataTypeRequest.DataType.CLIMATE && s == DatasetCrossDataTypeRequest.DataType.TRAIT))
			{
				DatasetCrossDataTypeRequest.Config trait = first.getType() == DatasetCrossDataTypeRequest.DataType.TRAIT ? first : second;
				DatasetCrossDataTypeRequest.Config climate = first.getType() == DatasetCrossDataTypeRequest.DataType.CLIMATE ? first : second;

				if (trait.getId() == null || climate.getId() == null)
				{
					resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
					return null;
				}

				Map<Integer, String> traits = getTraitMapping(context, trait.getId());
				Map<Integer, String> climates = getClimateMapping(context, climate.getId());

				// Get the actual data.
				Phenotypedata x = Phenotypedata.PHENOTYPEDATA.as("x");
				Climatedata y = Climatedata.CLIMATEDATA.as("y");
				step = context.select(
					GERMINATEBASE.NAME.as("name"),
					GERMINATEBASE.ID.as("dbId"),
					GERMINATEBASE.GENERAL_IDENTIFIER.as("general_identifier"),
					x.PHENOTYPE_VALUE.as(traits.get(trait.getId())),
					y.CLIMATE_VALUE.as(climates.get(climate.getId()))
				).from(GERMINATEBASE)
							  .leftJoin(x).on(x.GERMINATEBASE_ID.eq(GERMINATEBASE.ID))
							  .leftJoin(y).on(y.LOCATION_ID.in(DSL.select(DATASETLOCATIONS.LOCATION_ID).from(DATASETLOCATIONS).where(DATASETLOCATIONS.DATASET_ID.in(trait.getDatasetIds()))))
							  .where(x.PHENOTYPE_ID.eq(trait.getId()))
							  .and(y.CLIMATE_ID.eq(climate.getId()));

				addFiltering(step, userDetails, trait, climate, x.GERMINATEBASE_ID, y.LOCATION_ID, x.DATASET_ID, y.DATASET_ID);
			}
			else if (f == DatasetCrossDataTypeRequest.DataType.CLIMATE && s == DatasetCrossDataTypeRequest.DataType.CLIMATE)
			{
				if (first.getId() == null || second.getId() == null)
				{
					resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
					return null;
				}

				Map<Integer, String> climates = getClimateMapping(context, first.getId(), second.getId());

				// Get the actual data.
				Climatedata x = Climatedata.CLIMATEDATA.as("x");
				Climatedata y = Climatedata.CLIMATEDATA.as("y");
				step = context.select(
					LOCATIONS.SITE_NAME.as("name"),
					LOCATIONS.ID.as("dbId"),
					DSL.val("").as("general_identifier"),
					x.CLIMATE_VALUE.as(climates.get(first.getId())),
					y.CLIMATE_VALUE.as(climates.get(second.getId()))
				).from(LOCATIONS)
							  .leftJoin(x).on(x.LOCATION_ID.eq(LOCATIONS.ID))
							  .leftJoin(y).on(y.LOCATION_ID.eq(LOCATIONS.ID))
							  .where(x.CLIMATE_ID.eq(first.getId()))
							  .and(y.CLIMATE_ID.eq(second.getId()));

				addFiltering(step, userDetails, first, second, x.LOCATION_ID, y.LOCATION_ID, x.DATASET_ID, y.DATASET_ID);
			}

			if (step != null)
			{
				bw.write("#input=PHENOTYPE" + ResourceUtils.CRLF);

				step.limit(100000);
				step.fetchSize(100);

				try (Cursor<? extends Record> cursor = step.fetchLazy())
				{
					ResourceUtils.exportToFileStreamed(bw, cursor, true, null);
				}
			}
		}

		java.nio.file.Path target = resultFile.toPath();
		return Response.ok((StreamingOutput) output -> {
			Files.copy(target, output);
			Files.deleteIfExists(target);
		})
					   .type(MediaType.TEXT_PLAIN)
					   .header("content-disposition", "attachment;filename= \"" + resultFile.getName() + "\"")
					   .header("content-length", resultFile.length())
					   .build();
	}

	private void addFiltering(SelectConditionStep<?> step, AuthenticationFilter.UserDetails userDetails, DatasetCrossDataTypeRequest.Config first, DatasetCrossDataTypeRequest.Config second, TableField<?, Integer> firstItemId, TableField<?, Integer> secondItemId, TableField<?, Integer> firstDatasetId, TableField<?, Integer> secondDatasetId)
	{
		// Add marked item filtering per dimension
		if (!CollectionUtils.isEmpty(first.getMarkedIds()))
			step.and(firstItemId.in(first.getMarkedIds()));
		if (!CollectionUtils.isEmpty(second.getMarkedIds()))
			step.and(secondItemId.in(second.getMarkedIds()));

		// Add group id filtering per dimension
		if (!CollectionUtils.isEmpty(first.getGroupIds()))
			step.and(firstItemId.in(DSL.select(GROUPMEMBERS.FOREIGN_ID).from(GROUPMEMBERS).leftJoin(GROUPS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID)).where(GROUPS.CREATED_BY.eq(userDetails.getId()).or(GROUPS.VISIBILITY.eq(true))).and(GROUPS.ID.in(first.getGroupIds()))));
		if (!CollectionUtils.isEmpty(second.getGroupIds()))
			step.and(secondItemId.in(DSL.select(GROUPMEMBERS.FOREIGN_ID).from(GROUPMEMBERS).leftJoin(GROUPS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID)).where(GROUPS.CREATED_BY.eq(userDetails.getId()).or(GROUPS.VISIBILITY.eq(true))).and(GROUPS.ID.in(second.getGroupIds()))));

		// Add dataset filtering per dimension
		if (firstDatasetId != null && !CollectionUtils.isEmpty(first.getDatasetIds()))
			step.and(firstDatasetId.in(first.getDatasetIds()));
		if (secondDatasetId != null && !CollectionUtils.isEmpty(second.getDatasetIds()))
			step.and(secondDatasetId.in(second.getDatasetIds()));
	}

	private Map<Integer, String> getClimateMapping(DSLContext context, Integer... ids)
	{
		// Get the climate names including units. Map id to concat name.
		Field<String> climateUnit = DSL.concat(CLIMATES.NAME, DSL.iif(CLIMATES.UNIT_ID.isNull().or(UNITS.UNIT_ABBREVIATION.isNull()), "", DSL.concat(" [", UNITS.UNIT_ABBREVIATION).concat("]")));
		return context.select(CLIMATES.ID, climateUnit)
					  .from(CLIMATES)
					  .leftJoin(UNITS).on(UNITS.ID.eq(CLIMATES.UNIT_ID))
					  .where(CLIMATES.ID.in(ids))
					  .fetchMap(CLIMATES.ID, climateUnit);
	}

	private Map<Integer, String> getTraitMapping(DSLContext context, Integer... ids)
	{
		// Get the trait names including units. Map id to concat name.
		Field<String> traitUnit = DSL.concat(PHENOTYPES.NAME, DSL.iif(PHENOTYPES.UNIT_ID.isNull().or(UNITS.UNIT_ABBREVIATION.isNull()), "", DSL.concat(" [", UNITS.UNIT_ABBREVIATION).concat("]")));
		return context.select(PHENOTYPES.ID, traitUnit)
					  .from(PHENOTYPES)
					  .leftJoin(UNITS).on(UNITS.ID.eq(PHENOTYPES.UNIT_ID))
					  .where(PHENOTYPES.ID.in(ids))
					  .fetchMap(PHENOTYPES.ID, traitUnit);
	}
}
