package jhi.germinate.server.resource.traits;

import jhi.germinate.server.AuthenticationFilter;
import jhi.germinate.server.database.codegen.tables.Germinatebase;
import jhi.germinate.server.resource.ExportResource;
import jhi.germinate.server.util.CollectionUtils;
import org.jooq.*;
import org.jooq.impl.*;

import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Countries.COUNTRIES;
import static jhi.germinate.server.database.codegen.tables.Datasets.DATASETS;
import static jhi.germinate.server.database.codegen.tables.Entitytypes.ENTITYTYPES;
import static jhi.germinate.server.database.codegen.tables.Germinatebase.GERMINATEBASE;
import static jhi.germinate.server.database.codegen.tables.Locations.LOCATIONS;
import static jhi.germinate.server.database.codegen.tables.Phenotypedata.PHENOTYPEDATA;
import static jhi.germinate.server.database.codegen.tables.Phenotypes.PHENOTYPES;
import static jhi.germinate.server.database.codegen.tables.Synonyms.SYNONYMS;
import static jhi.germinate.server.database.codegen.tables.Treatments.TREATMENTS;
import static jhi.germinate.server.database.codegen.tables.Trialsetup.TRIALSETUP;
import static jhi.germinate.server.database.codegen.tables.Units.UNITS;

public class TrialsDataBaseResource extends ExportResource
{
	public static String GERMPLASM_ID                     = "germplasm_id";
	public static String GERMPLASM_GID                    = "germplasm_gid";
	public static String GERMPLASM_NAME                   = "germplasm_name";
	public static String GERMPLASM_SYNONYMS               = "germplasm_synonyms";
	public static String ENTITY_PARENT_NAME               = "entity_parent_name";
	public static String ENTITY_PARENT_GENERAL_IDENTIFIER = "entity_parent_general_identifier";
	public static String ENTITY_TYPE                      = "entity_type";
	public static String DATASET_ID                       = "dataset_id";
	public static String DATASET_NAME                     = "dataset_name";
	public static String DATASET_DESCRIPTION              = "dataset_description";
	public static String LOCATION_NAME                    = "location_name";
	public static String COUNTRY_NAME                     = "country_name";
	public static String COUNTRY_CODE2                    = "country_code2";
	public static String TRAIT_ID                         = "trait_id";
	public static String TRAIT_NAME                       = "trait_name";
	public static String TRAIT_NAME_SHORT                 = "trait_name_short";
	public static String TRAIT_DATA_TYPE                  = "trait_data_type";
	public static String TRAIT_RESTRICTIONS               = "trait_restrictions";
	public static String UNIT_NAME                        = "unit_name";
	public static String TREATMENT                        = "treatment";
	public static String TRIALSETUP_ID                    = "trialsetup_id";
	public static String REP                              = "rep";
	public static String BLOCK                            = "block";
	public static String TRIAL_ROW                        = "trial_row";
	public static String TRIAL_COLUMN                     = "trial_column";
	public static String GROUPS_FIELD                     = "groups";
	public static String LATITUDE                         = "latitude";
	public static String LONGITUDE                        = "longitude";
	public static String ELEVATION                        = "elevation";
	public static String RECORDING_DATE                   = "recording_date";
	public static String TRAIT_VALUE                      = "trait_value";

	private List<Field<?>> getFields(Germinatebase g, boolean minimal)
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		if (minimal)
		{
			return Arrays.asList(
					GERMINATEBASE.ID.as(GERMPLASM_ID),
					GERMINATEBASE.NAME.as(GERMPLASM_NAME),
					DATASETS.ID.as(DATASET_ID),
					DATASETS.NAME.as(DATASET_NAME),
					PHENOTYPES.ID.as(TRAIT_ID),
					PHENOTYPES.NAME.as(TRAIT_NAME),
					TREATMENTS.NAME.as(TREATMENT),
					PHENOTYPEDATA.RECORDING_DATE.as(RECORDING_DATE),
					PHENOTYPEDATA.PHENOTYPE_VALUE.as(TRAIT_VALUE));
		}
		else
		{
			return Arrays.asList(
					GERMINATEBASE.ID.as(GERMPLASM_ID),
					GERMINATEBASE.GENERAL_IDENTIFIER.as(GERMPLASM_GID),
					GERMINATEBASE.NAME.as(GERMPLASM_NAME),
					SYNONYMS.SYNONYMS_.as(GERMPLASM_SYNONYMS),
					g.NAME.as(ENTITY_PARENT_NAME),
					g.GENERAL_IDENTIFIER.as(ENTITY_PARENT_GENERAL_IDENTIFIER),
					ENTITYTYPES.NAME.as(ENTITY_TYPE),
					DATASETS.ID.as(DATASET_ID),
					DATASETS.NAME.as(DATASET_NAME),
					DATASETS.DESCRIPTION.as(DATASET_DESCRIPTION),
					LOCATIONS.SITE_NAME.as(LOCATION_NAME),
					COUNTRIES.COUNTRY_NAME.as(COUNTRY_NAME),
					COUNTRIES.COUNTRY_CODE2.as(COUNTRY_CODE2),
					PHENOTYPES.ID.as(TRAIT_ID),
					PHENOTYPES.NAME.as(TRAIT_NAME),
					PHENOTYPES.SHORT_NAME.as(TRAIT_NAME_SHORT),
					PHENOTYPES.RESTRICTIONS.as(TRAIT_RESTRICTIONS),
					PHENOTYPES.DATATYPE.as(TRAIT_DATA_TYPE),
					UNITS.UNIT_NAME.as(UNIT_NAME),
					TREATMENTS.NAME.as(TREATMENT),
					TRIALSETUP.ID.as(TRIALSETUP_ID),
					TRIALSETUP.REP.as(REP),
					TRIALSETUP.BLOCK.as(BLOCK),
					TRIALSETUP.TRIAL_ROW.as(TRIAL_ROW),
					TRIALSETUP.TRIAL_COLUMN.as(TRIAL_COLUMN),
					TRIALSETUP.LATITUDE.as(LATITUDE),
					TRIALSETUP.LONGITUDE.as(LONGITUDE),
					TRIALSETUP.ELEVATION.as(ELEVATION),
					PHENOTYPEDATA.RECORDING_DATE.as(RECORDING_DATE),
					PHENOTYPEDATA.PHENOTYPE_VALUE.as(TRAIT_VALUE));
		}
	}

	protected <A> SelectJoinStep<Record1<Integer>> getTrialsDataIdQueryWrapped(DSLContext context, boolean minimal, List<Join<A>> joins, Field<?>... additionalFields)
	{
		Germinatebase g = GERMINATEBASE.as("g");

		List<Field<?>> fields = new ArrayList<>(getFields(g, minimal));

		if (additionalFields != null)
			fields.addAll(Arrays.asList(additionalFields));

		SelectJoinStep<?> inner = context.select(fields)
										 .from(PHENOTYPEDATA)
										 .leftJoin(TRIALSETUP).on(TRIALSETUP.ID.eq(PHENOTYPEDATA.TRIALSETUP_ID))
										 .leftJoin(GERMINATEBASE).on(GERMINATEBASE.ID.eq(TRIALSETUP.GERMINATEBASE_ID))
										 .leftJoin(g).on(g.ID.eq(GERMINATEBASE.ENTITYPARENT_ID))
										 .leftJoin(SYNONYMS).on(SYNONYMS.FOREIGN_ID.eq(GERMINATEBASE.ID).and(SYNONYMS.SYNONYMTYPE_ID.eq(1)))
										 .leftJoin(ENTITYTYPES).on(ENTITYTYPES.ID.eq(GERMINATEBASE.ENTITYTYPE_ID))
										 .leftJoin(PHENOTYPES).on(PHENOTYPES.ID.eq(PHENOTYPEDATA.PHENOTYPE_ID))
										 .leftJoin(UNITS).on(UNITS.ID.eq(PHENOTYPES.UNIT_ID))
										 .leftJoin(DATASETS).on(DATASETS.ID.eq(TRIALSETUP.DATASET_ID))
										 .leftJoin(LOCATIONS).on(LOCATIONS.ID.eq(TRIALSETUP.LOCATION_ID))
										 .leftJoin(COUNTRIES).on(COUNTRIES.ID.eq(LOCATIONS.COUNTRY_ID))
										 .leftJoin(TREATMENTS).on(TREATMENTS.ID.eq(TRIALSETUP.TREATMENT_ID));

		if (!CollectionUtils.isEmpty(joins))
		{
			for (Join<A> join : joins)
				inner = inner.leftJoin(join.table).on(join.left.eq(join.right));
		}

		return context.selectDistinct(DSL.field(GERMPLASM_ID, Integer.class)).from(inner);
	}

	protected <A> SelectJoinStep<?> getTrialsGermplasmDistinctNameQueryWrapped(DSLContext context, List<Join<A>> joins, Field<?>... additionalFields)
	{
		List<Field<?>> fields = new ArrayList<>(Arrays.asList(
				GERMINATEBASE.ID.as(GERMPLASM_ID),
				GERMINATEBASE.GENERAL_IDENTIFIER.as(GERMPLASM_GID),
				GERMINATEBASE.NAME.as(GERMPLASM_NAME),
				DATASETS.ID.as(DATASET_ID)));

		if (additionalFields != null)
			fields.addAll(Arrays.asList(additionalFields));

		SelectSelectStep<?> select = context.select(DSL.asterisk());

		if (previousCount == -1)
			select.hint("SQL_CALC_FOUND_ROWS");

		SelectJoinStep<?> inner = context.selectDistinct(fields)
										 .from(PHENOTYPEDATA)
										 .leftJoin(TRIALSETUP).on(TRIALSETUP.ID.eq(PHENOTYPEDATA.TRIALSETUP_ID))
										 .leftJoin(GERMINATEBASE).on(GERMINATEBASE.ID.eq(TRIALSETUP.GERMINATEBASE_ID))
										 .leftJoin(DATASETS).on(DATASETS.ID.eq(TRIALSETUP.DATASET_ID));

		if (!CollectionUtils.isEmpty(joins))
		{
			for (Join<A> join : joins)
				inner = inner.leftJoin(join.table).on(join.left.eq(join.right));
		}

		return select.from(inner);
	}

	protected <A> SelectJoinStep<?> getTrialsGermplasmQueryWrapped(DSLContext context, List<Join<A>> joins, Field<?>... additionalFields)
	{
		List<Field<?>> fields = new ArrayList<>(Arrays.asList(
				GERMINATEBASE.ID.as(GERMPLASM_ID),
				GERMINATEBASE.GENERAL_IDENTIFIER.as(GERMPLASM_GID),
				GERMINATEBASE.NAME.as(GERMPLASM_NAME),
				DATASETS.ID.as(DATASET_ID),
				TREATMENTS.NAME.as(TREATMENT),
				TRIALSETUP.ID.as(TRIALSETUP_ID),
				TRIALSETUP.REP.as(REP),
				TRIALSETUP.BLOCK.as(BLOCK),
				TRIALSETUP.TRIAL_ROW.as(TRIAL_ROW),
				TRIALSETUP.TRIAL_COLUMN.as(TRIAL_COLUMN)));

		if (additionalFields != null)
			fields.addAll(Arrays.asList(additionalFields));

		SelectSelectStep<?> select = context.select(DSL.asterisk());

		if (previousCount == -1)
			select.hint("SQL_CALC_FOUND_ROWS");

		SelectJoinStep<?> inner = context.selectDistinct(fields)
										 .from(PHENOTYPEDATA)
										 .leftJoin(TRIALSETUP).on(TRIALSETUP.ID.eq(PHENOTYPEDATA.TRIALSETUP_ID))
										 .leftJoin(TREATMENTS).on(TREATMENTS.ID.eq(TRIALSETUP.TREATMENT_ID))
										 .leftJoin(GERMINATEBASE).on(GERMINATEBASE.ID.eq(TRIALSETUP.GERMINATEBASE_ID))
										 .leftJoin(DATASETS).on(DATASETS.ID.eq(TRIALSETUP.DATASET_ID));

		if (!CollectionUtils.isEmpty(joins))
		{
			for (Join<A> join : joins)
				inner = inner.leftJoin(join.table).on(join.left.eq(join.right));
		}

		return select.from(inner);
	}

	protected <A> SelectJoinStep<?> getTrialsDataQueryWrapped(DSLContext context, boolean minimal, List<Join<A>> joins, Field<?>... additionalFields)
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();
		Germinatebase g = GERMINATEBASE.as("g");

		List<Field<?>> fields = new ArrayList<>(getFields(g, minimal));

		if (additionalFields != null)
			fields.addAll(Arrays.asList(additionalFields));

		SelectSelectStep<?> select = context.select(DSL.asterisk());

		if (previousCount == -1)
			select.hint("SQL_CALC_FOUND_ROWS");

		SelectJoinStep<?> inner = context.select(fields)
										 .from(PHENOTYPEDATA)
										 .leftJoin(TRIALSETUP).on(TRIALSETUP.ID.eq(PHENOTYPEDATA.TRIALSETUP_ID))
										 .leftJoin(GERMINATEBASE).on(GERMINATEBASE.ID.eq(TRIALSETUP.GERMINATEBASE_ID))
										 .leftJoin(g).on(g.ID.eq(GERMINATEBASE.ENTITYPARENT_ID))
										 .leftJoin(SYNONYMS).on(SYNONYMS.FOREIGN_ID.eq(GERMINATEBASE.ID).and(SYNONYMS.SYNONYMTYPE_ID.eq(1)))
										 .leftJoin(ENTITYTYPES).on(ENTITYTYPES.ID.eq(GERMINATEBASE.ENTITYTYPE_ID))
										 .leftJoin(PHENOTYPES).on(PHENOTYPES.ID.eq(PHENOTYPEDATA.PHENOTYPE_ID))
										 .leftJoin(UNITS).on(UNITS.ID.eq(PHENOTYPES.UNIT_ID))
										 .leftJoin(DATASETS).on(DATASETS.ID.eq(TRIALSETUP.DATASET_ID))
										 .leftJoin(LOCATIONS).on(LOCATIONS.ID.eq(TRIALSETUP.LOCATION_ID))
										 .leftJoin(COUNTRIES).on(COUNTRIES.ID.eq(LOCATIONS.COUNTRY_ID))
										 .leftJoin(TREATMENTS).on(TREATMENTS.ID.eq(TRIALSETUP.TREATMENT_ID));

		if (!CollectionUtils.isEmpty(joins))
		{
			for (Join<A> join : joins)
				inner = inner.leftJoin(join.table).on(join.left.eq(join.right));
		}

		return select.from(inner);
	}

	public static class Join<A>
	{
		private TableImpl<?>     table;
		private TableField<?, A> left;
		private TableField<?, A> right;

		public Join(TableImpl<?> table, TableField<?, A> left, TableField<?, A> right)
		{
			this.table = table;
			this.left = left;
			this.right = right;
		}
	}
}
