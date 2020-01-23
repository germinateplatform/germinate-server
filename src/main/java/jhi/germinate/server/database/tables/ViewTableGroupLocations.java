/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables;


import java.math.BigDecimal;

import javax.annotation.Generated;

import jhi.germinate.server.database.GerminateTemplate_4_0_0;
import jhi.germinate.server.database.tables.records.ViewTableGroupLocationsRecord;

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
public class ViewTableGroupLocations extends TableImpl<ViewTableGroupLocationsRecord> {

    private static final long serialVersionUID = 1340802859;

    /**
     * The reference instance of <code>germinate_template_4_0_0.view_table_group_locations</code>
     */
    public static final ViewTableGroupLocations VIEW_TABLE_GROUP_LOCATIONS = new ViewTableGroupLocations();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ViewTableGroupLocationsRecord> getRecordType() {
        return ViewTableGroupLocationsRecord.class;
    }

    /**
     * The column <code>germinate_template_4_0_0.view_table_group_locations.location_id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public final TableField<ViewTableGroupLocationsRecord, Integer> LOCATION_ID = createField("location_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "Primary id for this table. This uniquely identifies the row.");

    /**
     * The column <code>germinate_template_4_0_0.view_table_group_locations.location_name</code>. The site name where the location is.
     */
    public final TableField<ViewTableGroupLocationsRecord, String> LOCATION_NAME = createField("location_name", org.jooq.impl.SQLDataType.VARCHAR(255).nullable(false).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "The site name where the location is.");

    /**
     * The column <code>germinate_template_4_0_0.view_table_group_locations.location_region</code>. The region where the location is if this exists.
     */
    public final TableField<ViewTableGroupLocationsRecord, String> LOCATION_REGION = createField("location_region", org.jooq.impl.SQLDataType.VARCHAR(255), this, "The region where the location is if this exists.");

    /**
     * The column <code>germinate_template_4_0_0.view_table_group_locations.location_state</code>. The state where the location is if this exists.
     */
    public final TableField<ViewTableGroupLocationsRecord, String> LOCATION_STATE = createField("location_state", org.jooq.impl.SQLDataType.VARCHAR(255), this, "The state where the location is if this exists.");

    /**
     * The column <code>germinate_template_4_0_0.view_table_group_locations.location_type</code>. The name of the location type. 
     */
    public final TableField<ViewTableGroupLocationsRecord, String> LOCATION_TYPE = createField("location_type", org.jooq.impl.SQLDataType.VARCHAR(255), this, "The name of the location type. ");

    /**
     * The column <code>germinate_template_4_0_0.view_table_group_locations.location_latitude</code>. Latitude of the location.
     */
    public final TableField<ViewTableGroupLocationsRecord, BigDecimal> LOCATION_LATITUDE = createField("location_latitude", org.jooq.impl.SQLDataType.DECIMAL(64, 10), this, "Latitude of the location.");

    /**
     * The column <code>germinate_template_4_0_0.view_table_group_locations.location_longitude</code>. Longitude of the location.
     */
    public final TableField<ViewTableGroupLocationsRecord, BigDecimal> LOCATION_LONGITUDE = createField("location_longitude", org.jooq.impl.SQLDataType.DECIMAL(64, 10), this, "Longitude of the location.");

    /**
     * The column <code>germinate_template_4_0_0.view_table_group_locations.location_elevation</code>. The elevation of the site in metres.
     */
    public final TableField<ViewTableGroupLocationsRecord, BigDecimal> LOCATION_ELEVATION = createField("location_elevation", org.jooq.impl.SQLDataType.DECIMAL(64, 10), this, "The elevation of the site in metres.");

    /**
     * The column <code>germinate_template_4_0_0.view_table_group_locations.country_name</code>. Country name.
     */
    public final TableField<ViewTableGroupLocationsRecord, String> COUNTRY_NAME = createField("country_name", org.jooq.impl.SQLDataType.VARCHAR(255).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "Country name.");

    /**
     * The column <code>germinate_template_4_0_0.view_table_group_locations.country_code2</code>. ISO 2 Code for country.
     */
    public final TableField<ViewTableGroupLocationsRecord, String> COUNTRY_CODE2 = createField("country_code2", org.jooq.impl.SQLDataType.CHAR(2).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.CHAR)), this, "ISO 2 Code for country.");

    /**
     * The column <code>germinate_template_4_0_0.view_table_group_locations.country_code3</code>. ISO 3 Code for country.
     */
    public final TableField<ViewTableGroupLocationsRecord, String> COUNTRY_CODE3 = createField("country_code3", org.jooq.impl.SQLDataType.CHAR(3).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.CHAR)), this, "ISO 3 Code for country.");

    /**
     * The column <code>germinate_template_4_0_0.view_table_group_locations.group_id</code>. Foreign key to groups (groups.id).
     */
    public final TableField<ViewTableGroupLocationsRecord, Integer> GROUP_ID = createField("group_id", org.jooq.impl.SQLDataType.INTEGER, this, "Foreign key to groups (groups.id).");

    /**
     * Create a <code>germinate_template_4_0_0.view_table_group_locations</code> table reference
     */
    public ViewTableGroupLocations() {
        this(DSL.name("view_table_group_locations"), null);
    }

    /**
     * Create an aliased <code>germinate_template_4_0_0.view_table_group_locations</code> table reference
     */
    public ViewTableGroupLocations(String alias) {
        this(DSL.name(alias), VIEW_TABLE_GROUP_LOCATIONS);
    }

    /**
     * Create an aliased <code>germinate_template_4_0_0.view_table_group_locations</code> table reference
     */
    public ViewTableGroupLocations(Name alias) {
        this(alias, VIEW_TABLE_GROUP_LOCATIONS);
    }

    private ViewTableGroupLocations(Name alias, Table<ViewTableGroupLocationsRecord> aliased) {
        this(alias, aliased, null);
    }

    private ViewTableGroupLocations(Name alias, Table<ViewTableGroupLocationsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("VIEW"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return GerminateTemplate_4_0_0.GERMINATE_TEMPLATE_4_0_0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableGroupLocations as(String alias) {
        return new ViewTableGroupLocations(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableGroupLocations as(Name alias) {
        return new ViewTableGroupLocations(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewTableGroupLocations rename(String name) {
        return new ViewTableGroupLocations(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewTableGroupLocations rename(Name name) {
        return new ViewTableGroupLocations(name, null);
    }
// @formatter:on
}
