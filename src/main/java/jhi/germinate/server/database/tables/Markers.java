/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables;


import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import jhi.germinate.server.database.GerminateTemplate_4_0_0;
import jhi.germinate.server.database.tables.records.MarkersRecord;

import org.jooq.Field;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;
import org.jooq.impl.TableImpl;


// @formatter:off
/**
 * Defines genetic markers within the database and assigns a type (markertypes).
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Markers extends TableImpl<MarkersRecord> {

    private static final long serialVersionUID = -1305004946;

    /**
     * The reference instance of <code>germinate_template_4_0_0.markers</code>
     */
    public static final Markers MARKERS = new Markers();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MarkersRecord> getRecordType() {
        return MarkersRecord.class;
    }

    /**
     * The column <code>germinate_template_4_0_0.markers.id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public final TableField<MarkersRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "Primary id for this table. This uniquely identifies the row.");

    /**
     * The column <code>germinate_template_4_0_0.markers.markertype_id</code>. Foreign key to locations (locations.id).
     */
    public final TableField<MarkersRecord, Integer> MARKERTYPE_ID = createField("markertype_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "Foreign key to locations (locations.id).");

    /**
     * The column <code>germinate_template_4_0_0.markers.marker_name</code>. The name of the marker. This should be a unique name which identifies the marker.
     */
    public final TableField<MarkersRecord, String> MARKER_NAME = createField("marker_name", org.jooq.impl.SQLDataType.VARCHAR(45).nullable(false), this, "The name of the marker. This should be a unique name which identifies the marker.");

    /**
     * The column <code>germinate_template_4_0_0.markers.created_on</code>. When the record was created.

     */
    public final TableField<MarkersRecord, Timestamp> CREATED_ON = createField("created_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "When the record was created.\n");

    /**
     * The column <code>germinate_template_4_0_0.markers.updated_on</code>. When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.
     */
    public final TableField<MarkersRecord, Timestamp> UPDATED_ON = createField("updated_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.");

    /**
     * Create a <code>germinate_template_4_0_0.markers</code> table reference
     */
    public Markers() {
        this(DSL.name("markers"), null);
    }

    /**
     * Create an aliased <code>germinate_template_4_0_0.markers</code> table reference
     */
    public Markers(String alias) {
        this(DSL.name(alias), MARKERS);
    }

    /**
     * Create an aliased <code>germinate_template_4_0_0.markers</code> table reference
     */
    public Markers(Name alias) {
        this(alias, MARKERS);
    }

    private Markers(Name alias, Table<MarkersRecord> aliased) {
        this(alias, aliased, null);
    }

    private Markers(Name alias, Table<MarkersRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("Defines genetic markers within the database and assigns a type (markertypes)."));
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
    public Identity<MarkersRecord, Integer> getIdentity() {
        return Internal.createIdentity(jhi.germinate.server.database.tables.Markers.MARKERS, jhi.germinate.server.database.tables.Markers.MARKERS.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<MarkersRecord> getPrimaryKey() {
        return Internal.createUniqueKey(jhi.germinate.server.database.tables.Markers.MARKERS, "KEY_markers_PRIMARY", jhi.germinate.server.database.tables.Markers.MARKERS.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<MarkersRecord>> getKeys() {
        return Arrays.<UniqueKey<MarkersRecord>>asList(
              Internal.createUniqueKey(jhi.germinate.server.database.tables.Markers.MARKERS, "KEY_markers_PRIMARY", jhi.germinate.server.database.tables.Markers.MARKERS.ID)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Markers as(String alias) {
        return new Markers(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Markers as(Name alias) {
        return new Markers(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Markers rename(String name) {
        return new Markers(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Markers rename(Name name) {
        return new Markers(name, null);
    }
// @formatter:on
}
