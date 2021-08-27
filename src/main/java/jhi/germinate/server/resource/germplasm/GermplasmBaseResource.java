package jhi.germinate.server.resource.germplasm;

import jhi.germinate.server.database.codegen.tables.Germinatebase;
import jhi.germinate.server.resource.ExportResource;
import jhi.germinate.server.util.CollectionUtils;
import org.jooq.*;
import org.jooq.impl.*;

import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Biologicalstatus.*;
import static jhi.germinate.server.database.codegen.tables.Compounddata.*;
import static jhi.germinate.server.database.codegen.tables.Countries.*;
import static jhi.germinate.server.database.codegen.tables.Datasetmembers.*;
import static jhi.germinate.server.database.codegen.tables.Datasets.*;
import static jhi.germinate.server.database.codegen.tables.Entitytypes.*;
import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;
import static jhi.germinate.server.database.codegen.tables.Images.*;
import static jhi.germinate.server.database.codegen.tables.Imagetypes.*;
import static jhi.germinate.server.database.codegen.tables.Institutions.*;
import static jhi.germinate.server.database.codegen.tables.Locations.*;
import static jhi.germinate.server.database.codegen.tables.Phenotypedata.*;
import static jhi.germinate.server.database.codegen.tables.Synonyms.*;
import static jhi.germinate.server.database.codegen.tables.Taxonomies.*;

public class GermplasmBaseResource extends ExportResource
{
	public static String GERMPLASM_ID                     = "germplasm_id";
	public static String GERMPLASM_NAME                   = "germplasm_name";
	public static String GERMPLASM_GID                    = "germplasm_gid";
	public static String GERMPLASM_NUMBER                 = "germplasm_number";
	public static String GERMPLASM_PUID                   = "germplasm_puid";
	public static String ENTITY_TYPE_ID                   = "entity_type_id";
	public static String ENTITY_TYPE_NAME                 = "entity_type_name";
	public static String ENTITY_PARENT_ID                 = "entity_parent_id";
	public static String ENTITY_PARENT_NAME               = "entity_parent_name";
	public static String ENTITY_PARENT_GENERAL_IDENTIFIER = "entity_parent_general_identifier";
	public static String BIOLOGICAL_STATUS_ID             = "biological_status_id";
	public static String BIOLOGICAL_STATUS_NAME           = "biological_status_name";
	public static String SSYNONYMS                        = "synonyms";
	public static String COLLECTOR_NUMBER                 = "collector_number";
	public static String GENUS                            = "genus";
	public static String SPECIES                          = "species";
	public static String SUBTAXA                          = "subtaxa";
	public static String INSTITUTION_ID                   = "institution_id";
	public static String INSTITUTION_NAME                 = "institution_name";
	public static String LOCATION_ID                      = "location_id";
	public static String LOCATION                         = "location";
	public static String LATITUDE                         = "latitude";
	public static String LONGITUDE                        = "longitude";
	public static String ELEVATION                        = "elevation";
	public static String COUNTRY_NAME                     = "country_name";
	public static String COUNTRY_CODE                     = "country_code";
	public static String COLL_DATE                        = "coll_date";
	public static String PDCI                             = "pdci";
	public static String IMAGE_COUNT                      = "image_count";
	public static String FIRST_IMAGE_PATH                 = "first_image_path";
	public static String HAS_TRIALS_DATA                  = "has_trials_data";
	public static String HAS_GENOTYPIC_DATA               = "has_genotypic_data";
	public static String HAS_ALLELEFREQ_DATA              = "has_allelefreq_data";
	public static String HAS_COMPOUND_DATA                = "has_compound_data";

	protected <A> SelectJoinStep<Record1<Integer>> getGermplasmIdQueryWrapped(DSLContext context, List<Join<A>> joins, Field<?>... additionalFields)
	{
		Germinatebase g = GERMINATEBASE.as("g");

		List<Field<?>> fields = new ArrayList<>(Arrays.asList(GERMINATEBASE.NAME.as(GERMPLASM_NAME),
			GERMINATEBASE.ID.as(GERMPLASM_ID),
			GERMINATEBASE.GENERAL_IDENTIFIER.as(GERMPLASM_GID),
			GERMINATEBASE.NUMBER.as(GERMPLASM_NUMBER),
			GERMINATEBASE.PUID.as(GERMPLASM_PUID),
			ENTITYTYPES.ID.as(ENTITY_TYPE_ID),
			ENTITYTYPES.NAME.as(ENTITY_TYPE_NAME),
			GERMINATEBASE.ENTITYPARENT_ID.as(ENTITY_PARENT_ID),
			g.NAME.as(ENTITY_PARENT_NAME),
			g.GENERAL_IDENTIFIER.as(ENTITY_PARENT_GENERAL_IDENTIFIER),
			BIOLOGICALSTATUS.ID.as(BIOLOGICAL_STATUS_ID),
			BIOLOGICALSTATUS.SAMPSTAT.as(BIOLOGICAL_STATUS_NAME),
			SYNONYMS.SYNONYMS_.as(SSYNONYMS),
			GERMINATEBASE.COLLNUMB.as(COLLECTOR_NUMBER),
			TAXONOMIES.GENUS.as(GENUS),
			TAXONOMIES.SPECIES.as(SPECIES),
			TAXONOMIES.SUBTAXA.as(SUBTAXA),
			INSTITUTIONS.ID.as(INSTITUTION_ID),
			INSTITUTIONS.NAME.as(INSTITUTION_NAME),
			LOCATIONS.ID.as(LOCATION_ID),
			LOCATIONS.SITE_NAME.as(LOCATION),
			LOCATIONS.LATITUDE.as(LATITUDE),
			LOCATIONS.LONGITUDE.as(LONGITUDE),
			LOCATIONS.ELEVATION.as(ELEVATION),
			COUNTRIES.COUNTRY_NAME.as(COUNTRY_NAME),
			COUNTRIES.COUNTRY_CODE2.as(COUNTRY_CODE),
			GERMINATEBASE.COLLDATE.as(COLL_DATE),
			GERMINATEBASE.PDCI.as(PDCI),
			DSL.selectCount()
			   .from(IMAGES)
			   .leftJoin(IMAGETYPES).on(IMAGES.IMAGETYPE_ID.eq(IMAGETYPES.ID))
			   .where(IMAGETYPES.REFERENCE_TABLE.eq("germinatebase"))
			   .and(IMAGES.FOREIGN_ID.eq(GERMINATEBASE.ID))
			   .asField(IMAGE_COUNT),
			DSL.select(IMAGES.PATH)
			   .from(IMAGES)
			   .leftJoin(IMAGETYPES).on(IMAGES.IMAGETYPE_ID.eq(IMAGETYPES.ID))
			   .where(IMAGETYPES.REFERENCE_TABLE.eq("germinatebase"))
			   .and(IMAGES.FOREIGN_ID.eq(GERMINATEBASE.ID))
			   .limit(1)
			   .asField(FIRST_IMAGE_PATH),
			DSL.selectOne()
			   .from(PHENOTYPEDATA)
			   .where(PHENOTYPEDATA.GERMINATEBASE_ID.eq(GERMINATEBASE.ID))
			   .limit(1)
			   .asField(HAS_TRIALS_DATA),
			DSL.selectOne()
			   .from(DATASETMEMBERS)
			   .leftJoin(DATASETS).on(DATASETS.ID.eq(DATASETMEMBERS.DATASET_ID))
			   .where(DATASETMEMBERS.FOREIGN_ID.eq(GERMINATEBASE.ID))
			   .and(DATASETMEMBERS.DATASETMEMBERTYPE_ID.eq(2))
			   .and(DATASETS.DATASETTYPE_ID.eq(1))
			   .limit(1)
			   .asField(HAS_GENOTYPIC_DATA),
			DSL.selectOne()
			   .from(DATASETMEMBERS)
			   .leftJoin(DATASETS).on(DATASETS.ID.eq(DATASETMEMBERS.DATASET_ID))
			   .where(DATASETMEMBERS.FOREIGN_ID.eq(GERMINATEBASE.ID))
			   .and(DATASETMEMBERS.DATASETMEMBERTYPE_ID.eq(2))
			   .and(DATASETS.DATASETTYPE_ID.eq(4))
			   .limit(1)
			   .asField(HAS_ALLELEFREQ_DATA),
			DSL.selectOne()
			   .from(COMPOUNDDATA)
			   .where(COMPOUNDDATA.GERMINATEBASE_ID.eq(GERMINATEBASE.ID))
			   .limit(1)
			   .asField(HAS_COMPOUND_DATA)));

		if (additionalFields != null)
			fields.addAll(Arrays.asList(additionalFields));

		SelectJoinStep<?> inner = context.select(fields)
										 .from(GERMINATEBASE)
										 .leftJoin(g).on(g.ID.eq(GERMINATEBASE.ENTITYPARENT_ID))
										 .leftJoin(INSTITUTIONS).on(INSTITUTIONS.ID.eq(GERMINATEBASE.INSTITUTION_ID))
										 .leftJoin(ENTITYTYPES).on(ENTITYTYPES.ID.eq(GERMINATEBASE.ENTITYTYPE_ID))
										 .leftJoin(TAXONOMIES).on(TAXONOMIES.ID.eq(GERMINATEBASE.TAXONOMY_ID))
										 .leftJoin(LOCATIONS).on(LOCATIONS.ID.eq(GERMINATEBASE.LOCATION_ID))
										 .leftJoin(COUNTRIES).on(COUNTRIES.ID.eq(LOCATIONS.COUNTRY_ID))
										 .leftJoin(BIOLOGICALSTATUS).on(BIOLOGICALSTATUS.ID.eq(GERMINATEBASE.BIOLOGICALSTATUS_ID))
										 .leftJoin(SYNONYMS).on(SYNONYMS.SYNONYMTYPE_ID.eq(1).and(SYNONYMS.FOREIGN_ID.eq(GERMINATEBASE.ID)));

		if (!CollectionUtils.isEmpty(joins))
		{
			for (Join<A> join : joins)
				inner = inner.leftJoin(join.table).on(join.left.eq(join.right));
		}

		return context.selectDistinct(DSL.field(GERMPLASM_ID, Integer.class)).from(inner);
	}

	protected <A> SelectJoinStep<?> getGermplasmQueryWrapped(DSLContext context, List<Join<A>> joins, Field<?>... additionalFields)
	{
		Germinatebase g = GERMINATEBASE.as("g");

		List<Field<?>> fields = new ArrayList<>(Arrays.asList(GERMINATEBASE.NAME.as(GERMPLASM_NAME),
			GERMINATEBASE.ID.as(GERMPLASM_ID),
			GERMINATEBASE.GENERAL_IDENTIFIER.as(GERMPLASM_GID),
			GERMINATEBASE.NUMBER.as(GERMPLASM_NUMBER),
			GERMINATEBASE.PUID.as(GERMPLASM_PUID),
			ENTITYTYPES.ID.as(ENTITY_TYPE_ID),
			ENTITYTYPES.NAME.as(ENTITY_TYPE_NAME),
			GERMINATEBASE.ENTITYPARENT_ID.as(ENTITY_PARENT_ID),
			g.NAME.as(ENTITY_PARENT_NAME),
			g.GENERAL_IDENTIFIER.as(ENTITY_PARENT_GENERAL_IDENTIFIER),
			BIOLOGICALSTATUS.ID.as(BIOLOGICAL_STATUS_ID),
			BIOLOGICALSTATUS.SAMPSTAT.as(BIOLOGICAL_STATUS_NAME),
			SYNONYMS.SYNONYMS_.as(SSYNONYMS),
			GERMINATEBASE.COLLNUMB.as(COLLECTOR_NUMBER),
			TAXONOMIES.GENUS.as(GENUS),
			TAXONOMIES.SPECIES.as(SPECIES),
			TAXONOMIES.SUBTAXA.as(SUBTAXA),
			INSTITUTIONS.ID.as(INSTITUTION_ID),
			INSTITUTIONS.NAME.as(INSTITUTION_NAME),
			LOCATIONS.ID.as(LOCATION_ID),
			LOCATIONS.SITE_NAME.as(LOCATION),
			LOCATIONS.LATITUDE.as(LATITUDE),
			LOCATIONS.LONGITUDE.as(LONGITUDE),
			LOCATIONS.ELEVATION.as(ELEVATION),
			COUNTRIES.COUNTRY_NAME.as(COUNTRY_NAME),
			COUNTRIES.COUNTRY_CODE2.as(COUNTRY_CODE),
			GERMINATEBASE.COLLDATE.as(COLL_DATE),
			GERMINATEBASE.PDCI.as(PDCI),
			DSL.selectCount()
			   .from(IMAGES)
			   .leftJoin(IMAGETYPES).on(IMAGES.IMAGETYPE_ID.eq(IMAGETYPES.ID))
			   .where(IMAGETYPES.REFERENCE_TABLE.eq("germinatebase"))
			   .and(IMAGES.FOREIGN_ID.eq(GERMINATEBASE.ID))
			   .asField(IMAGE_COUNT),
			DSL.select(IMAGES.PATH)
			   .from(IMAGES)
			   .leftJoin(IMAGETYPES).on(IMAGES.IMAGETYPE_ID.eq(IMAGETYPES.ID))
			   .where(IMAGETYPES.REFERENCE_TABLE.eq("germinatebase"))
			   .and(IMAGES.FOREIGN_ID.eq(GERMINATEBASE.ID))
			   .limit(1)
			   .asField(FIRST_IMAGE_PATH),
			DSL.selectOne()
			   .from(PHENOTYPEDATA)
			   .where(PHENOTYPEDATA.GERMINATEBASE_ID.eq(GERMINATEBASE.ID))
			   .limit(1)
			   .asField(HAS_TRIALS_DATA),
			DSL.selectOne()
			   .from(DATASETMEMBERS)
			   .leftJoin(DATASETS).on(DATASETS.ID.eq(DATASETMEMBERS.DATASET_ID))
			   .where(DATASETMEMBERS.FOREIGN_ID.eq(GERMINATEBASE.ID))
			   .and(DATASETMEMBERS.DATASETMEMBERTYPE_ID.eq(2))
			   .and(DATASETS.DATASETTYPE_ID.eq(1))
			   .limit(1)
			   .asField(HAS_GENOTYPIC_DATA),
			DSL.selectOne()
			   .from(DATASETMEMBERS)
			   .leftJoin(DATASETS).on(DATASETS.ID.eq(DATASETMEMBERS.DATASET_ID))
			   .where(DATASETMEMBERS.FOREIGN_ID.eq(GERMINATEBASE.ID))
			   .and(DATASETMEMBERS.DATASETMEMBERTYPE_ID.eq(2))
			   .and(DATASETS.DATASETTYPE_ID.eq(4))
			   .limit(1)
			   .asField(HAS_ALLELEFREQ_DATA),
			DSL.selectOne()
			   .from(COMPOUNDDATA)
			   .where(COMPOUNDDATA.GERMINATEBASE_ID.eq(GERMINATEBASE.ID))
			   .limit(1)
			   .asField(HAS_COMPOUND_DATA)));

		if (additionalFields != null)
			fields.addAll(Arrays.asList(additionalFields));

		SelectSelectStep<?> select = context.select(DSL.asterisk());

		if (previousCount == -1)
			select.hint("SQL_CALC_FOUND_ROWS");

		SelectJoinStep<?> inner = context.select(fields).from(GERMINATEBASE)
										 .leftJoin(g).on(g.ID.eq(GERMINATEBASE.ENTITYPARENT_ID))
										 .leftJoin(INSTITUTIONS).on(INSTITUTIONS.ID.eq(GERMINATEBASE.INSTITUTION_ID))
										 .leftJoin(ENTITYTYPES).on(ENTITYTYPES.ID.eq(GERMINATEBASE.ENTITYTYPE_ID))
										 .leftJoin(TAXONOMIES).on(TAXONOMIES.ID.eq(GERMINATEBASE.TAXONOMY_ID))
										 .leftJoin(LOCATIONS).on(LOCATIONS.ID.eq(GERMINATEBASE.LOCATION_ID))
										 .leftJoin(COUNTRIES).on(COUNTRIES.ID.eq(LOCATIONS.COUNTRY_ID))
										 .leftJoin(BIOLOGICALSTATUS).on(BIOLOGICALSTATUS.ID.eq(GERMINATEBASE.BIOLOGICALSTATUS_ID))
										 .leftJoin(SYNONYMS).on(SYNONYMS.SYNONYMTYPE_ID.eq(1).and(SYNONYMS.FOREIGN_ID.eq(GERMINATEBASE.ID)));

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
