package jhi.germinate.server.resource.markers;

import jhi.germinate.server.resource.ExportResource;
import jhi.germinate.server.resource.germplasm.GermplasmBaseResource;
import jhi.germinate.server.util.CollectionUtils;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Markers.MARKERS;
import static jhi.germinate.server.database.codegen.tables.Markertypes.MARKERTYPES;
import static jhi.germinate.server.database.codegen.tables.Synonyms.SYNONYMS;

public class MarkerBaseResource extends ExportResource
{
	public static final String MARKER_ID       = "marker_id";
	public static final String MARKER_NAME     = "marker_name";
	public static final String MARKER_TYPE     = "marker_type";
	public static final String MARKER_SYNONYMS = "marker_synonyms";
	public static final String CREATED_ON      = "created_on";
	public static final String UPDATED_ON      = "updated_on";

	protected <A> SelectJoinStep<?> getMarkerQuery(DSLContext context, List<GermplasmBaseResource.Join<A>> joins, Field<?>... additionalFields)
	{
		List<Field<?>> fields = new ArrayList<>(Arrays.asList(
				MARKERS.ID.as(MARKER_ID),
				MARKERS.MARKER_NAME.as(MARKER_NAME),
				MARKERTYPES.DESCRIPTION.as(MARKER_TYPE),
				SYNONYMS.SYNONYMS_.as(MARKER_SYNONYMS),
				MARKERS.CREATED_ON.as(CREATED_ON),
				MARKERS.UPDATED_ON.as(UPDATED_ON)
		));

		if (additionalFields != null)
			fields.addAll(Arrays.asList(additionalFields));

		SelectSelectStep<?> select = context.select(fields);

		if (previousCount == -1)
			select.hint("SQL_CALC_FOUND_ROWS");

		SelectOnConditionStep<?> inner = select.from(MARKERS)
											.leftJoin(MARKERTYPES).on(MARKERTYPES.ID.eq(MARKERS.MARKERTYPE_ID))
											.leftJoin(SYNONYMS).on(SYNONYMS.SYNONYMTYPE_ID.eq(2).and(SYNONYMS.FOREIGN_ID.eq(MARKERS.ID)));

		if (!CollectionUtils.isEmpty(joins))
		{
			for (GermplasmBaseResource.Join<A> join : joins)
				inner = inner.leftJoin(join.table).on(join.left.eq(join.right));
		}

		return inner;
	}

	protected <A> SelectJoinStep<?> getMarkerIdQuery(DSLContext context, List<GermplasmBaseResource.Join<A>> joins, Field<?>... additionalFields)
	{
		List<Field<?>> fields = new ArrayList<>(Arrays.asList(
				MARKERS.ID.as(MARKER_ID),
				MARKERS.MARKER_NAME.as(MARKER_NAME),
				MARKERTYPES.DESCRIPTION.as(MARKER_TYPE),
				SYNONYMS.SYNONYMS_.as(MARKER_SYNONYMS),
				MARKERS.CREATED_ON.as(CREATED_ON),
				MARKERS.UPDATED_ON.as(UPDATED_ON)
		));

		if (additionalFields != null)
			fields.addAll(Arrays.asList(additionalFields));

		SelectSelectStep<?> select = context.select(fields);

		if (previousCount == -1)
			select.hint("SQL_CALC_FOUND_ROWS");

		SelectJoinStep<?> inner = select.from(MARKERS)
										.leftJoin(MARKERTYPES).on(MARKERTYPES.ID.eq(MARKERS.MARKERTYPE_ID))
										.leftJoin(SYNONYMS).on(SYNONYMS.SYNONYMTYPE_ID.eq(2).and(SYNONYMS.FOREIGN_ID.eq(MARKERS.ID)));

		if (!CollectionUtils.isEmpty(joins))
		{
			for (GermplasmBaseResource.Join<A> join : joins)
				inner = inner.leftJoin(join.table).on(join.left.eq(join.right));
		}

		return context.selectDistinct(DSL.field(MARKER_ID, Integer.class)).from(inner);
	}
}
