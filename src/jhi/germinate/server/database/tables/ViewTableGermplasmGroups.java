/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables;


import com.google.gson.JsonArray;

import java.math.BigDecimal;
import java.sql.Date;

import javax.annotation.Generated;

import jhi.germinate.server.database.GerminateTemplate_3_7_0;
import jhi.germinate.server.database.tables.records.ViewTableGermplasmGroupsRecord;
import jhi.germinate.server.util.SynonymBinding;

import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


// @formatter:off
/**
 * VIEW
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ViewTableGermplasmGroups extends TableImpl<ViewTableGermplasmGroupsRecord> {

    private static final long serialVersionUID = 799481700;

    /**
     * The reference instance of <code>germinate_template_3_7_0.view_table_germplasm_groups</code>
     */
    public static final ViewTableGermplasmGroups VIEW_TABLE_GERMPLASM_GROUPS = new ViewTableGermplasmGroups();
    /**
     * The column <code>germinate_template_3_7_0.view_table_germplasm_groups.entity_parent_id</code>. Foreign key to germinatebase (germinatebase.id).
     */
    public final TableField<ViewTableGermplasmGroupsRecord, Integer> ENTITY_PARENT_ID = createField("entity_parent_id", org.jooq.impl.SQLDataType.INTEGER, this, "Foreign key to germinatebase (germinatebase.id).");

    /**
     * The column <code>germinate_template_3_7_0.view_table_germplasm_groups.germplasm_id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public final TableField<ViewTableGermplasmGroupsRecord, Integer> GERMPLASM_ID = createField("germplasm_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "Primary id for this table. This uniquely identifies the row.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_germplasm_groups.germplasm_gid</code>. A unique identifier.
     */
    public final TableField<ViewTableGermplasmGroupsRecord, String> GERMPLASM_GID = createField("germplasm_gid", org.jooq.impl.SQLDataType.VARCHAR(255).nullable(false), this, "A unique identifier.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_germplasm_groups.germplasm_name</code>. A unique name which defines an entry in the germinatbase table.
     */
    public final TableField<ViewTableGermplasmGroupsRecord, String> GERMPLASM_NAME = createField("germplasm_name", org.jooq.impl.SQLDataType.VARCHAR(255).nullable(false), this, "A unique name which defines an entry in the germinatbase table.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_germplasm_groups.germplasm_number</code>. This is the unique identifier for accessions within a genebank, and is assigned when a sample is
entered into the genebank collection (e.g. ‘PI 113869’).
     */
    public final TableField<ViewTableGermplasmGroupsRecord, String> GERMPLASM_NUMBER = createField("germplasm_number", org.jooq.impl.SQLDataType.VARCHAR(255), this, "This is the unique identifier for accessions within a genebank, and is assigned when a sample is\nentered into the genebank collection (e.g. ‘PI 113869’).");

    /**
     * The column <code>germinate_template_3_7_0.view_table_germplasm_groups.germplasm_puid</code>. Any persistent, unique identifier assigned to the accession so it can be unambiguously referenced at the global level and the information associated with it harvested through automated means. Report one PUID for each accession.
     */
    public final TableField<ViewTableGermplasmGroupsRecord, String> GERMPLASM_PUID = createField("germplasm_puid", org.jooq.impl.SQLDataType.VARCHAR(255), this, "Any persistent, unique identifier assigned to the accession so it can be unambiguously referenced at the global level and the information associated with it harvested through automated means. Report one PUID for each accession.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_germplasm_groups.entity_type_id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public final TableField<ViewTableGermplasmGroupsRecord, Integer> ENTITY_TYPE_ID = createField("entity_type_id", org.jooq.impl.SQLDataType.INTEGER.defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "Primary id for this table. This uniquely identifies the row.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_germplasm_groups.entity_type_name</code>. The name of the entity type.
     */
    public final TableField<ViewTableGermplasmGroupsRecord, String> ENTITY_TYPE_NAME = createField("entity_type_name", org.jooq.impl.SQLDataType.VARCHAR(255), this, "The name of the entity type.");
    /**
     * The column <code>germinate_template_3_7_0.view_table_germplasm_groups.location</code>. The site name where the location is.
     */
    public final TableField<ViewTableGermplasmGroupsRecord, String> LOCATION = createField("location", org.jooq.impl.SQLDataType.VARCHAR(255).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "The site name where the location is.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_germplasm_groups.biological_status_id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public final TableField<ViewTableGermplasmGroupsRecord, Integer> BIOLOGICAL_STATUS_ID = createField("biological_status_id", org.jooq.impl.SQLDataType.INTEGER.defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "Primary id for this table. This uniquely identifies the row.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_germplasm_groups.biological_status_name</code>. Previoulsy known as sampstat.
     */
    public final TableField<ViewTableGermplasmGroupsRecord, String> BIOLOGICAL_STATUS_NAME = createField("biological_status_name", org.jooq.impl.SQLDataType.VARCHAR(255), this, "Previoulsy known as sampstat.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_germplasm_groups.synonyms</code>. The synonyms as a json array.
     */
    public final TableField<ViewTableGermplasmGroupsRecord, JsonArray> SYNONYMS = createField("synonyms", org.jooq.impl.DefaultDataType.getDefaultDataType("\"germinate_template_3_7_0\".\"view_table_germplasm_groups_synonyms\""), this, "The synonyms as a json array.", new SynonymBinding());

    /**
     * The column <code>germinate_template_3_7_0.view_table_germplasm_groups.collector_number</code>. Original identifier assigned by the collector(s) of the sample, normally composed of the name or
initials of the collector(s) followed by a number (e.g. ‘FM9909’). This identifier is essential for
identifying duplicates held in different collections.
     */
    public final TableField<ViewTableGermplasmGroupsRecord, String> COLLECTOR_NUMBER = createField("collector_number", org.jooq.impl.SQLDataType.VARCHAR(255), this, "Original identifier assigned by the collector(s) of the sample, normally composed of the name or\ninitials of the collector(s) followed by a number (e.g. ‘FM9909’). This identifier is essential for\nidentifying duplicates held in different collections.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_germplasm_groups.genus</code>. Genus name for the species.
     */
    public final TableField<ViewTableGermplasmGroupsRecord, String> GENUS = createField("genus", org.jooq.impl.SQLDataType.VARCHAR(255).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "Genus name for the species.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_germplasm_groups.species</code>. Species name in lowercase.
     */
    public final TableField<ViewTableGermplasmGroupsRecord, String> SPECIES = createField("species", org.jooq.impl.SQLDataType.VARCHAR(255).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "Species name in lowercase.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_germplasm_groups.subtaxa</code>. Subtaxa name.
     */
    public final TableField<ViewTableGermplasmGroupsRecord, String> SUBTAXA = createField("subtaxa", org.jooq.impl.SQLDataType.VARCHAR(255), this, "Subtaxa name.");
    /**
     * The column <code>germinate_template_3_7_0.view_table_germplasm_groups.coll_date</code>. Collecting date of the sample, where YYYY is the year, MM is the month and DD is the day.
Missing data (MM or DD) should be indicated with hyphens or ‘00’ [double zero].
     */
    public final TableField<ViewTableGermplasmGroupsRecord, Date> COLL_DATE = createField("coll_date", org.jooq.impl.SQLDataType.DATE, this, "Collecting date of the sample, where YYYY is the year, MM is the month and DD is the day.\nMissing data (MM or DD) should be indicated with hyphens or ‘00’ [double zero]. ");

    /**
     * The column <code>germinate_template_3_7_0.view_table_germplasm_groups.elevation</code>. The elevation of the site in metres.
     */
    public final TableField<ViewTableGermplasmGroupsRecord, BigDecimal> ELEVATION = createField("elevation", org.jooq.impl.SQLDataType.DECIMAL(64, 10), this, "The elevation of the site in metres.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_germplasm_groups.country_name</code>. Country name.
     */
    public final TableField<ViewTableGermplasmGroupsRecord, String> COUNTRY_NAME = createField("country_name", org.jooq.impl.SQLDataType.VARCHAR(255).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "Country name.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_germplasm_groups.country_code</code>. ISO 2 Code for country.
     */
    public final TableField<ViewTableGermplasmGroupsRecord, String> COUNTRY_CODE = createField("country_code", org.jooq.impl.SQLDataType.CHAR(2).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.CHAR)), this, "ISO 2 Code for country.");

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ViewTableGermplasmGroupsRecord> getRecordType() {
        return ViewTableGermplasmGroupsRecord.class;
    }

    /**
     * The column <code>germinate_template_3_7_0.view_table_germplasm_groups.pdci</code>. Passport Data Completeness Index. This is calculated by Germinate. Manual editing of this field will be overwritten.
     */
    public final TableField<ViewTableGermplasmGroupsRecord, Double> PDCI = createField("pdci", org.jooq.impl.SQLDataType.FLOAT, this, "Passport Data Completeness Index. This is calculated by Germinate. Manual editing of this field will be overwritten.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_germplasm_groups.image_count</code>.
     */
    public final TableField<ViewTableGermplasmGroupsRecord, Long> IMAGE_COUNT = createField("image_count", org.jooq.impl.SQLDataType.BIGINT, this, "");

    /**
     * The column <code>germinate_template_3_7_0.view_table_germplasm_groups.first_image_path</code>.
     */
    public final TableField<ViewTableGermplasmGroupsRecord, String> FIRST_IMAGE_PATH = createField("first_image_path", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>germinate_template_3_7_0.view_table_germplasm_groups.group_id</code>. Foreign key to groups (groups.id).
     */
    public final TableField<ViewTableGermplasmGroupsRecord, Integer> GROUP_ID = createField("group_id", org.jooq.impl.SQLDataType.INTEGER, this, "Foreign key to groups (groups.id).");

    /**
     * Create a <code>germinate_template_3_7_0.view_table_germplasm_groups</code> table reference
     */
    public ViewTableGermplasmGroups() {
        this(DSL.name("view_table_germplasm_groups"), null);
    }

    /**
     * Create an aliased <code>germinate_template_3_7_0.view_table_germplasm_groups</code> table reference
     */
    public ViewTableGermplasmGroups(String alias) {
        this(DSL.name(alias), VIEW_TABLE_GERMPLASM_GROUPS);
    }

    /**
     * Create an aliased <code>germinate_template_3_7_0.view_table_germplasm_groups</code> table reference
     */
    public ViewTableGermplasmGroups(Name alias) {
        this(alias, VIEW_TABLE_GERMPLASM_GROUPS);
    }

    private ViewTableGermplasmGroups(Name alias, Table<ViewTableGermplasmGroupsRecord> aliased) {
        this(alias, aliased, null);
    }

    private ViewTableGermplasmGroups(Name alias, Table<ViewTableGermplasmGroupsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("VIEW"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return GerminateTemplate_3_7_0.GERMINATE_TEMPLATE_3_7_0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableGermplasmGroups as(String alias) {
        return new ViewTableGermplasmGroups(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableGermplasmGroups as(Name alias) {
        return new ViewTableGermplasmGroups(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewTableGermplasmGroups rename(String name) {
        return new ViewTableGermplasmGroups(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewTableGermplasmGroups rename(Name name) {
        return new ViewTableGermplasmGroups(name, null);
    }
// @formatter:on
}
