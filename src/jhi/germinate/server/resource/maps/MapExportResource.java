package jhi.germinate.server.resource.maps;

import org.jooq.*;
import org.restlet.data.Status;
import org.restlet.data.*;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import jhi.germinate.resource.MapExportRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.CustomVerifier;
import jhi.germinate.server.database.tables.pojos.Maps;
import jhi.germinate.server.database.tables.records.MapdefinitionsRecord;
import jhi.germinate.server.resource.BaseServerResource;
import jhi.germinate.server.resource.maps.writer.*;
import jhi.germinate.server.util.*;

import static jhi.germinate.server.database.tables.Mapdefinitions.*;
import static jhi.germinate.server.database.tables.Mapfeaturetypes.*;
import static jhi.germinate.server.database.tables.Maps.*;
import static jhi.germinate.server.database.tables.Markers.*;

/**
 * @author Sebastian Raubach
 */
public class MapExportResource extends BaseServerResource
{
	// TODO: Export format!

	private Integer mapId;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			this.mapId = Integer.parseInt(getRequestAttributes().get("mapId").toString());
		}
		catch (NullPointerException | NumberFormatException e)
		{
		}
	}

	@Post
	public FileRepresentation postFile(MapExportRequest request)
	{
		if (request == null || StringUtils.isEmpty(request.getFormat()))
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());

		FileRepresentation representation;
		try
		{
			File file = createTempFile("map-" + mapId, ".tsv");

			try (Connection conn = Database.getConnection();
				 DSLContext context = Database.getContext(conn);
				 BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)))
			{
				Maps map = context.selectFrom(MAPS).where(MAPS.ID.eq(mapId)).fetchAnyInto(Maps.class);

				if (map != null)
				{
					AbstractMapWriter writer;

					switch (request.getFormat().toLowerCase())
					{
						case "strudel":
							writer = new StrudelMapWriter(bw);
							break;
						case "mapchart":
							writer = new MapChartWriter(bw);
							break;
						case "flapjack":
						default:
							writer = new FlapjackMapWriter(bw);
							break;
					}

					writer.writeHeader(map);

					SelectConditionStep<? extends Record> step = context.selectFrom(
						MAPDEFINITIONS.leftJoin(MAPS).on(MAPS.ID.eq(MAPDEFINITIONS.MAP_ID))
									  .leftJoin(MARKERS).on(MARKERS.ID.eq(MAPDEFINITIONS.MARKER_ID))
									  .leftJoin(MAPFEATURETYPES).on(MAPFEATURETYPES.ID.eq(MAPDEFINITIONS.MAPFEATURETYPE_ID))
					)
																		.where(MAPS.VISIBILITY.eq(true)
																							  .or(MAPS.USER_ID.eq(userDetails.getId())))
																		.and(MAPS.ID.eq(mapId));

					if (!StringUtils.isEmpty(request.getMethod()))
						filter(context, step, request);

					step.orderBy(MAPDEFINITIONS.CHROMOSOME, MAPDEFINITIONS.DEFINITION_START, MARKERS.MARKER_NAME)
						.stream()
						.forEachOrdered(m -> {
							try
							{
								writer.writeRow(m);
							}
							catch (IOException e)
							{
								e.printStackTrace();
							}
						});

					writer.writeFooter();
				}
			}
			catch (SQLException | IOException e)
			{
				e.printStackTrace();
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
			}

			representation = new FileRepresentation(file, MediaType.TEXT_PLAIN);
			representation.setSize(file.length());
			representation.setDisposition(new Disposition(Disposition.TYPE_ATTACHMENT));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}

		return representation;
	}

	private void filter(DSLContext context, SelectConditionStep<? extends Record> step, MapExportRequest request)
	{
		switch (request.getMethod().toLowerCase())
		{
			case "chromosomes":
				step.and(MAPDEFINITIONS.CHROMOSOME.in(request.getChromosomes()));
				break;
			case "regions":
				if (CollectionUtils.isEmpty(request.getRegions()))
					throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

				List<Condition> conditions = Arrays.stream(request.getRegions())
												   .map(r -> MAPDEFINITIONS.CHROMOSOME.eq(r.getChromosome())
																					  .and(MAPDEFINITIONS.DEFINITION_START.greaterOrEqual(r.getStart()))
																					  .and(MAPDEFINITIONS.DEFINITION_END.lessOrEqual(r.getEnd())))
												   .collect(Collectors.toList());

				if (conditions.size() > 0)
				{
					Condition overall = conditions.get(0);

					for (int i = 1; i < conditions.size(); i++)
						overall = overall.or(conditions.get(i));

					step.and(overall);
				}
				break;
			case "markeridinterval":
				if (request.getMarkerIdInterval() == null || request.getMarkerIdInterval().length != 2)
					throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

				MapdefinitionsRecord one = context.selectFrom(MAPDEFINITIONS).where(MAPDEFINITIONS.MAP_ID.eq(mapId).and(MAPDEFINITIONS.MARKER_ID.eq(request.getMarkerIdInterval()[0]))).fetchAny();
				MapdefinitionsRecord two = context.selectFrom(MAPDEFINITIONS).where(MAPDEFINITIONS.MAP_ID.eq(mapId).and(MAPDEFINITIONS.MARKER_ID.eq(request.getMarkerIdInterval()[1]))).fetchAny();

				if (one == null || two == null)
					throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

				step.and(MAPDEFINITIONS.DEFINITION_START.greaterOrEqual(one.getDefinitionEnd())
														.and(MAPDEFINITIONS.DEFINITION_END.lessOrEqual(two.getDefinitionStart()))
														.and(MAPDEFINITIONS.CHROMOSOME.eq(one.getChromosome()))
														.and(MAPDEFINITIONS.CHROMOSOME.eq(two.getChromosome())));
				break;
			case "radius":
				if (request.getRadius() == null || request.getRadius().getMarkerId() == null || request.getRadius().getLeft() == null || request.getRadius().getRight() == null)
					throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

				MapdefinitionsRecord marker = context.selectFrom(MAPDEFINITIONS).where(MAPDEFINITIONS.MAP_ID.eq(mapId).and(MAPDEFINITIONS.MARKER_ID.eq(request.getRadius().getMarkerId()))).fetchAny();

				if (marker == null)
					throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

				step.and(MAPDEFINITIONS.DEFINITION_START.greaterOrEqual(marker.getDefinitionStart() - request.getRadius().getLeft())
														.and(MAPDEFINITIONS.DEFINITION_END.lessOrEqual(marker.getDefinitionEnd() + request.getRadius().getRight()))
														.and(MAPDEFINITIONS.CHROMOSOME.eq(marker.getChromosome())));
				break;
		}
	}
}
