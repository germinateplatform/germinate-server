/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables;


import javax.annotation.Generated;

import jhi.germinate.server.database.GerminateTemplate_4_0_0;
import jhi.germinate.server.database.tables.records.ViewTableClimateoverlaysRecord;

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
public class ViewTableClimateoverlays extends TableImpl<ViewTableClimateoverlaysRecord> {

    private static final long serialVersionUID = -455536730;

    /**
     * The reference instance of <code>germinate_template_4_0_0.view_table_climateoverlays</code>
     */
    public static final ViewTableClimateoverlays VIEW_TABLE_CLIMATEOVERLAYS = new ViewTableClimateoverlays();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ViewTableClimateoverlaysRecord> getRecordType() {
        return ViewTableClimateoverlaysRecord.class;
    }

    /**
     * The column <code>germinate_template_4_0_0.view_table_climateoverlays.climate_overlay_id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public final TableField<ViewTableClimateoverlaysRecord, Integer> CLIMATE_OVERLAY_ID = createField("climate_overlay_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "Primary id for this table. This uniquely identifies the row.");

    /**
     * The column <code>germinate_template_4_0_0.view_table_climateoverlays.climate_id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public final TableField<ViewTableClimateoverlaysRecord, Integer> CLIMATE_ID = createField("climate_id", org.jooq.impl.SQLDataType.INTEGER.defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "Primary id for this table. This uniquely identifies the row.");

    /**
     * The column <code>germinate_template_4_0_0.view_table_climateoverlays.climate_name</code>. Describes the climate.
     */
    public final TableField<ViewTableClimateoverlaysRecord, String> CLIMATE_NAME = createField("climate_name", org.jooq.impl.SQLDataType.VARCHAR(255).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "Describes the climate.");

    /**
     * The column <code>germinate_template_4_0_0.view_table_climateoverlays.is_legend</code>. The legend for the image. What colours represent in the overlays. This is not required but used if present. 
     */
    public final TableField<ViewTableClimateoverlaysRecord, Boolean> IS_LEGEND = createField("is_legend", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.BOOLEAN)), this, "The legend for the image. What colours represent in the overlays. This is not required but used if present. ");

    /**
     * The column <code>germinate_template_4_0_0.view_table_climateoverlays.bottom_left_latitude</code>. Allows the allignment of images against OpenStreetMap API.
     */
    public final TableField<ViewTableClimateoverlaysRecord, Double> BOTTOM_LEFT_LATITUDE = createField("bottom_left_latitude", org.jooq.impl.SQLDataType.DOUBLE, this, "Allows the allignment of images against OpenStreetMap API.");

    /**
     * The column <code>germinate_template_4_0_0.view_table_climateoverlays.bottom_left_longitude</code>. Allows the allignment of images against OpenStreetMap API.
     */
    public final TableField<ViewTableClimateoverlaysRecord, Double> BOTTOM_LEFT_LONGITUDE = createField("bottom_left_longitude", org.jooq.impl.SQLDataType.DOUBLE, this, "Allows the allignment of images against OpenStreetMap API.");

    /**
     * The column <code>germinate_template_4_0_0.view_table_climateoverlays.top_right_latitude</code>. Allows the allignment of images against OpenStreetMap API.
     */
    public final TableField<ViewTableClimateoverlaysRecord, Double> TOP_RIGHT_LATITUDE = createField("top_right_latitude", org.jooq.impl.SQLDataType.DOUBLE, this, "Allows the allignment of images against OpenStreetMap API.");

    /**
     * The column <code>germinate_template_4_0_0.view_table_climateoverlays.top_right_longitude</code>. Allows the allignment of images against OpenStreetMap API.
     */
    public final TableField<ViewTableClimateoverlaysRecord, Double> TOP_RIGHT_LONGITUDE = createField("top_right_longitude", org.jooq.impl.SQLDataType.DOUBLE, this, "Allows the allignment of images against OpenStreetMap API.");

    /**
     * The column <code>germinate_template_4_0_0.view_table_climateoverlays.filename</code>. This is the path for holding images which can be used as overlays for the Google Maps representation in Germinate. The path is relative.
     */
    public final TableField<ViewTableClimateoverlaysRecord, String> FILENAME = createField("filename", org.jooq.impl.SQLDataType.VARCHAR(255).nullable(false), this, "This is the path for holding images which can be used as overlays for the Google Maps representation in Germinate. The path is relative.");

    /**
     * Create a <code>germinate_template_4_0_0.view_table_climateoverlays</code> table reference
     */
    public ViewTableClimateoverlays() {
        this(DSL.name("view_table_climateoverlays"), null);
    }

    /**
     * Create an aliased <code>germinate_template_4_0_0.view_table_climateoverlays</code> table reference
     */
    public ViewTableClimateoverlays(String alias) {
        this(DSL.name(alias), VIEW_TABLE_CLIMATEOVERLAYS);
    }

    /**
     * Create an aliased <code>germinate_template_4_0_0.view_table_climateoverlays</code> table reference
     */
    public ViewTableClimateoverlays(Name alias) {
        this(alias, VIEW_TABLE_CLIMATEOVERLAYS);
    }

    private ViewTableClimateoverlays(Name alias, Table<ViewTableClimateoverlaysRecord> aliased) {
        this(alias, aliased, null);
    }

    private ViewTableClimateoverlays(Name alias, Table<ViewTableClimateoverlaysRecord> aliased, Field<?>[] parameters) {
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
    public ViewTableClimateoverlays as(String alias) {
        return new ViewTableClimateoverlays(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableClimateoverlays as(Name alias) {
        return new ViewTableClimateoverlays(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewTableClimateoverlays rename(String name) {
        return new ViewTableClimateoverlays(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewTableClimateoverlays rename(Name name) {
        return new ViewTableClimateoverlays(name, null);
    }
// @formatter:on
}