/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables;


import com.google.gson.JsonArray;

import javax.annotation.Generated;

import jhi.germinate.server.database.GerminateTemplate_3_7_0;
import jhi.germinate.server.database.tables.records.ViewTableMarkersGroupsRecord;
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
public class ViewTableMarkersGroups extends TableImpl<ViewTableMarkersGroupsRecord> {

    /**
     * The reference instance of <code>germinate_template_3_7_0.view_table_markers_groups</code>
     */
    public static final ViewTableMarkersGroups VIEW_TABLE_MARKERS_GROUPS = new ViewTableMarkersGroups();
    private static final long serialVersionUID = -1467940269;
    /**
     * The column <code>germinate_template_3_7_0.view_table_markers_groups.marker_id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public final TableField<ViewTableMarkersGroupsRecord, Integer> MARKER_ID = createField("marker_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "Primary id for this table. This uniquely identifies the row.");
    /**
     * The column <code>germinate_template_3_7_0.view_table_markers_groups.marker_name</code>. The name of the marker. This should be a unique name which identifies the marker.
     */
    public final TableField<ViewTableMarkersGroupsRecord, String> MARKER_NAME = createField("marker_name", org.jooq.impl.SQLDataType.VARCHAR(45).nullable(false), this, "The name of the marker. This should be a unique name which identifies the marker.");
    /**
     * The column <code>germinate_template_3_7_0.view_table_markers_groups.marker_type</code>. Describes the marker type. Markers (markers) have a defined type. This could be AFLP, MicroSat, SNP and so on.
     */
    public final TableField<ViewTableMarkersGroupsRecord, String> MARKER_TYPE = createField("marker_type", org.jooq.impl.SQLDataType.VARCHAR(255).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "Describes the marker type. Markers (markers) have a defined type. This could be AFLP, MicroSat, SNP and so on.");
    /**
     * The column <code>germinate_template_3_7_0.view_table_markers_groups.marker_synonyms</code>. The synonyms as a json array.
     */
    public final TableField<ViewTableMarkersGroupsRecord, JsonArray> MARKER_SYNONYMS = createField("marker_synonyms", org.jooq.impl.DefaultDataType.getDefaultDataType("\"germinate_template_3_7_0\".\"view_table_markers_groups_marker_synonyms\""), this, "The synonyms as a json array.", new SynonymBinding());
    /**
     * The column <code>germinate_template_3_7_0.view_table_markers_groups.group_id</code>. Foreign key to groups (groups.id).
     */
    public final TableField<ViewTableMarkersGroupsRecord, Integer> GROUP_ID = createField("group_id", org.jooq.impl.SQLDataType.INTEGER, this, "Foreign key to groups (groups.id).");

    /**
     * Create a <code>germinate_template_3_7_0.view_table_markers_groups</code> table reference
     */
    public ViewTableMarkersGroups() {
        this(DSL.name("view_table_markers_groups"), null);
    }

    /**
     * Create an aliased <code>germinate_template_3_7_0.view_table_markers_groups</code> table reference
     */
    public ViewTableMarkersGroups(String alias) {
        this(DSL.name(alias), VIEW_TABLE_MARKERS_GROUPS);
    }

    /**
     * Create an aliased <code>germinate_template_3_7_0.view_table_markers_groups</code> table reference
     */
    public ViewTableMarkersGroups(Name alias) {
        this(alias, VIEW_TABLE_MARKERS_GROUPS);
    }

    private ViewTableMarkersGroups(Name alias, Table<ViewTableMarkersGroupsRecord> aliased) {
        this(alias, aliased, null);
    }

    private ViewTableMarkersGroups(Name alias, Table<ViewTableMarkersGroupsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("VIEW"));
    }

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ViewTableMarkersGroupsRecord> getRecordType() {
        return ViewTableMarkersGroupsRecord.class;
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
    public ViewTableMarkersGroups as(String alias) {
        return new ViewTableMarkersGroups(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableMarkersGroups as(Name alias) {
        return new ViewTableMarkersGroups(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewTableMarkersGroups rename(String name) {
        return new ViewTableMarkersGroups(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewTableMarkersGroups rename(Name name) {
        return new ViewTableMarkersGroups(name, null);
    }
// @formatter:on
}
