package jhi.germinate.server.resource.markers;

import jhi.germinate.server.resource.ExportResource;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Mapdefinitions.MAPDEFINITIONS;
import static jhi.germinate.server.database.codegen.tables.Mapfeaturetypes.MAPFEATURETYPES;
import static jhi.germinate.server.database.codegen.tables.Maps.MAPS;
import static jhi.germinate.server.database.codegen.tables.Markers.MARKERS;
import static jhi.germinate.server.database.codegen.tables.Synonyms.SYNONYMS;

public class MapdefinitionBaseResource extends ExportResource
{
	public static String MARKER_ID        = "marker_id";
	public static String MARKER_NAME      = "marker_name";
	public static String SSYNONYMS        = "synonyms";
	public static String MAP_FEATURE_TYPE = "map_feature_type";
	public static String MAP_ID           = "map_id";
	public static String USER_ID          = "user_id";
	public static String VISIBILITY       = "visibility";
	public static String MAP_NAME         = "map_name";
	public static String CHROMOSOME       = "chromosome";
	public static String POSITION         = "position";

	protected <A> SelectJoinStep<?> getMapdefinitionQuery(DSLContext context)
	{
		List<Field<?>> fields = new ArrayList<>(Arrays.asList(
				MARKERS.ID.as(MARKER_ID),
				MARKERS.MARKER_NAME.as(MARKER_NAME),
				SYNONYMS.SYNONYMS_.as(SSYNONYMS),
				MAPFEATURETYPES.DESCRIPTION.as(MAP_FEATURE_TYPE),
				MAPS.ID.as(MAP_ID),
				MAPS.USER_ID.as(USER_ID),
				MAPS.VISIBILITY.as(VISIBILITY),
				MAPS.NAME.as(MAP_NAME),
				MAPDEFINITIONS.CHROMOSOME.as(CHROMOSOME),
				MAPDEFINITIONS.DEFINITION_START.as(POSITION)
		));

		SelectSelectStep<?> select = context.select(fields);

		if (previousCount == -1)
			select.hint("SQL_CALC_FOUND_ROWS");

		SelectJoinStep<?> inner = select.from(MARKERS)
										.leftJoin(MAPDEFINITIONS).on(MAPDEFINITIONS.MARKER_ID.eq(MARKERS.ID))
										.leftJoin(MAPFEATURETYPES).on(MAPFEATURETYPES.ID.eq(MAPDEFINITIONS.MAPFEATURETYPE_ID))
										.leftJoin(MAPS).on(MAPS.ID.eq(MAPDEFINITIONS.MAP_ID))
										.leftJoin(SYNONYMS).on(SYNONYMS.SYNONYMTYPE_ID.eq(2).and(SYNONYMS.FOREIGN_ID.eq(MARKERS.ID)));

		return inner;
	}

	protected <A> SelectJoinStep<?> getMapDefinitionIdQuery(DSLContext context) {
		List<Field<?>> fields = new ArrayList<>(Arrays.asList(
				MARKERS.ID.as(MARKER_ID),
				MARKERS.MARKER_NAME.as(MARKER_NAME),
				SYNONYMS.SYNONYMS_.as(SSYNONYMS),
				MAPFEATURETYPES.DESCRIPTION.as(MAP_FEATURE_TYPE),
				MAPS.ID.as(MAP_ID),
				MAPS.USER_ID.as(USER_ID),
				MAPS.VISIBILITY.as(VISIBILITY),
				MAPS.NAME.as(MAP_NAME),
				MAPDEFINITIONS.CHROMOSOME.as(CHROMOSOME),
				MAPDEFINITIONS.DEFINITION_START.as(POSITION)
		));

		SelectSelectStep<?> select = context.select(fields);

		if (previousCount == -1)
			select.hint("SQL_CALC_FOUND_ROWS");

		SelectJoinStep<?> inner = select.from(MARKERS)
										.leftJoin(MAPDEFINITIONS).on(MAPDEFINITIONS.MARKER_ID.eq(MARKERS.ID))
										.leftJoin(MAPFEATURETYPES).on(MAPFEATURETYPES.ID.eq(MAPDEFINITIONS.MAPFEATURETYPE_ID))
										.leftJoin(MAPS).on(MAPS.ID.eq(MAPDEFINITIONS.MAP_ID))
										.leftJoin(SYNONYMS).on(SYNONYMS.SYNONYMTYPE_ID.eq(2).and(SYNONYMS.FOREIGN_ID.eq(MARKERS.ID)));

		return context.selectDistinct(DSL.field(MARKER_ID, Integer.class)).from(inner);
	}
}
