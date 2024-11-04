package jhi.germinate.server.resource.maps;

import jhi.germinate.resource.MapExportRequest;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.Maps;
import jhi.germinate.server.database.codegen.tables.records.MapdefinitionsRecord;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.resource.maps.writer.*;
import jhi.germinate.server.util.*;
import org.jooq.*;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.jooq.Record;

import java.io.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static jhi.germinate.server.database.codegen.tables.Mapdefinitions.*;
import static jhi.germinate.server.database.codegen.tables.Mapfeaturetypes.*;
import static jhi.germinate.server.database.codegen.tables.Maps.*;
import static jhi.germinate.server.database.codegen.tables.Markers.*;

@Path("map/{mapId}/export")
@Secured
@PermitAll
public class MapExportResource extends ContextResource
{
	@PathParam("mapId")
	private Integer mapId;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response postMapFile(MapExportRequest request)
		throws IOException, SQLException
	{
		if (request == null || StringUtils.isEmpty(request.getFormat()) || mapId == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		try
		{
			File file = ResourceUtils.createTempFile("map-" + mapId, ".tsv");

			try (Connection conn = Database.getConnection();
				 BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)))
			{
				DSLContext context = Database.getContext(conn);
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

//					step.orderBy(MAPDEFINITIONS.CHROMOSOME, MAPDEFINITIONS.DEFINITION_START, MARKERS.MARKER_NAME)
					step.stream()
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
			catch (IOException e)
			{
				e.printStackTrace();
				resp.sendError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
				return null;
			}

			java.nio.file.Path filePath = file.toPath();
			return Response.ok((StreamingOutput) output -> {
				Files.copy(filePath, output);
				Files.deleteIfExists(filePath);
			})
						   .header("content-disposition", "attachment; filename=\"" + file.getName() + "\"")
						   .header("content-length", file.length())
						   .build();
		}
		catch (IOException e)
		{
			e.printStackTrace();
			resp.sendError(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}

		return null;
	}

	private void filter(DSLContext context, SelectConditionStep<? extends Record> step, MapExportRequest request)
		throws IOException
	{
		switch (request.getMethod().toLowerCase())
		{
			case "chromosomes":
				step.and(MAPDEFINITIONS.CHROMOSOME.in(request.getChromosomes()));
				break;
			case "regions":
				if (CollectionUtils.isEmpty(request.getRegions()))
				{
					resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
					return;
				}

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
				{
					resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
					return;
				}

				MapdefinitionsRecord one = context.selectFrom(MAPDEFINITIONS).where(MAPDEFINITIONS.MAP_ID.eq(mapId).and(MAPDEFINITIONS.MARKER_ID.eq(request.getMarkerIdInterval()[0]))).fetchAny();
				MapdefinitionsRecord two = context.selectFrom(MAPDEFINITIONS).where(MAPDEFINITIONS.MAP_ID.eq(mapId).and(MAPDEFINITIONS.MARKER_ID.eq(request.getMarkerIdInterval()[1]))).fetchAny();

				if (one == null || two == null)
				{
					resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
					return;
				}

				step.and(MAPDEFINITIONS.DEFINITION_START.greaterOrEqual(one.getDefinitionEnd())
														.and(MAPDEFINITIONS.DEFINITION_END.lessOrEqual(two.getDefinitionStart()))
														.and(MAPDEFINITIONS.CHROMOSOME.eq(one.getChromosome()))
														.and(MAPDEFINITIONS.CHROMOSOME.eq(two.getChromosome())));
				break;
			case "radius":
				if (request.getRadius() == null || request.getRadius().getMarkerId() == null || request.getRadius().getLeft() == null || request.getRadius().getRight() == null)
				{
					resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
					return;
				}

				MapdefinitionsRecord marker = context.selectFrom(MAPDEFINITIONS).where(MAPDEFINITIONS.MAP_ID.eq(mapId).and(MAPDEFINITIONS.MARKER_ID.eq(request.getRadius().getMarkerId()))).fetchAny();

				if (marker == null)
				{
					resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
					return;
				}

				step.and(MAPDEFINITIONS.DEFINITION_START.greaterOrEqual(marker.getDefinitionStart() - request.getRadius().getLeft())
														.and(MAPDEFINITIONS.DEFINITION_END.lessOrEqual(marker.getDefinitionEnd() + request.getRadius().getRight()))
														.and(MAPDEFINITIONS.CHROMOSOME.eq(marker.getChromosome())));
				break;
		}
	}
}
