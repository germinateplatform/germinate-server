/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables;


import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import jhi.germinate.server.database.GerminateTemplate_4_0_0;
import jhi.germinate.server.database.tables.records.MapdefinitionsRecord;

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
 * Relates genetic markers to a map and assigns a position (if known). Maps 
 * are made up of lists of markers and positions (genetic or physiscal and 
 * chromosome/linkage group assignation). In the case of QTL the definition_start 
 * and definition_end columns can be used to specify a range across a linkage 
 * group.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Mapdefinitions extends TableImpl<MapdefinitionsRecord> {

    private static final long serialVersionUID = -1051328097;

    /**
     * The reference instance of <code>germinate_template_4_0_0.mapdefinitions</code>
     */
    public static final Mapdefinitions MAPDEFINITIONS = new Mapdefinitions();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MapdefinitionsRecord> getRecordType() {
        return MapdefinitionsRecord.class;
    }

    /**
     * The column <code>germinate_template_4_0_0.mapdefinitions.id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public final TableField<MapdefinitionsRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "Primary id for this table. This uniquely identifies the row.");

    /**
     * The column <code>germinate_template_4_0_0.mapdefinitions.mapfeaturetype_id</code>. Foreign key to mapfeaturetypes (mapfeaturetypes.id).
     */
    public final TableField<MapdefinitionsRecord, Integer> MAPFEATURETYPE_ID = createField("mapfeaturetype_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "Foreign key to mapfeaturetypes (mapfeaturetypes.id).");

    /**
     * The column <code>germinate_template_4_0_0.mapdefinitions.marker_id</code>. Foreign key to markers (markers.id).
     */
    public final TableField<MapdefinitionsRecord, Integer> MARKER_ID = createField("marker_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "Foreign key to markers (markers.id).");

    /**
     * The column <code>germinate_template_4_0_0.mapdefinitions.map_id</code>. Foreign key to maps (maps.id).
     */
    public final TableField<MapdefinitionsRecord, Integer> MAP_ID = createField("map_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "Foreign key to maps (maps.id).");

    /**
     * The column <code>germinate_template_4_0_0.mapdefinitions.definition_start</code>. Used if the markers location spans over an area more than a single point on the maps. Determines the marker start location.
     */
    public final TableField<MapdefinitionsRecord, Double> DEFINITION_START = createField("definition_start", org.jooq.impl.SQLDataType.DOUBLE.nullable(false), this, "Used if the markers location spans over an area more than a single point on the maps. Determines the marker start location.");

    /**
     * The column <code>germinate_template_4_0_0.mapdefinitions.definition_end</code>. Used if the markers location spans over an area more than a single point on the maps. Determines the marker end location.
     */
    public final TableField<MapdefinitionsRecord, Double> DEFINITION_END = createField("definition_end", org.jooq.impl.SQLDataType.DOUBLE, this, "Used if the markers location spans over an area more than a single point on the maps. Determines the marker end location.");

    /**
     * The column <code>germinate_template_4_0_0.mapdefinitions.chromosome</code>. The chromosome/linkage group that this marker is found on.
     */
    public final TableField<MapdefinitionsRecord, String> CHROMOSOME = createField("chromosome", org.jooq.impl.SQLDataType.VARCHAR(255).nullable(false), this, "The chromosome/linkage group that this marker is found on.");

    /**
     * The column <code>germinate_template_4_0_0.mapdefinitions.arm_impute</code>. If a chromosome arm is available then this can be entered here.
     */
    public final TableField<MapdefinitionsRecord, String> ARM_IMPUTE = createField("arm_impute", org.jooq.impl.SQLDataType.VARCHAR(255), this, "If a chromosome arm is available then this can be entered here.");

    /**
     * The column <code>germinate_template_4_0_0.mapdefinitions.created_on</code>. When the record was created.
     */
    public final TableField<MapdefinitionsRecord, Timestamp> CREATED_ON = createField("created_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "When the record was created.");

    /**
     * The column <code>germinate_template_4_0_0.mapdefinitions.updated_on</code>. When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.
     */
    public final TableField<MapdefinitionsRecord, Timestamp> UPDATED_ON = createField("updated_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.");

    /**
     * Create a <code>germinate_template_4_0_0.mapdefinitions</code> table reference
     */
    public Mapdefinitions() {
        this(DSL.name("mapdefinitions"), null);
    }

    /**
     * Create an aliased <code>germinate_template_4_0_0.mapdefinitions</code> table reference
     */
    public Mapdefinitions(String alias) {
        this(DSL.name(alias), MAPDEFINITIONS);
    }

    /**
     * Create an aliased <code>germinate_template_4_0_0.mapdefinitions</code> table reference
     */
    public Mapdefinitions(Name alias) {
        this(alias, MAPDEFINITIONS);
    }

    private Mapdefinitions(Name alias, Table<MapdefinitionsRecord> aliased) {
        this(alias, aliased, null);
    }

    private Mapdefinitions(Name alias, Table<MapdefinitionsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("Relates genetic markers to a map and assigns a position (if known). Maps are made up of lists of markers and positions (genetic or physiscal and chromosome/linkage group assignation). In the case of QTL the definition_start and definition_end columns can be used to specify a range across a linkage group."));
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
    public Identity<MapdefinitionsRecord, Integer> getIdentity() {
        return Internal.createIdentity(jhi.germinate.server.database.tables.Mapdefinitions.MAPDEFINITIONS, jhi.germinate.server.database.tables.Mapdefinitions.MAPDEFINITIONS.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<MapdefinitionsRecord> getPrimaryKey() {
        return Internal.createUniqueKey(jhi.germinate.server.database.tables.Mapdefinitions.MAPDEFINITIONS, "KEY_mapdefinitions_PRIMARY", jhi.germinate.server.database.tables.Mapdefinitions.MAPDEFINITIONS.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<MapdefinitionsRecord>> getKeys() {
        return Arrays.<UniqueKey<MapdefinitionsRecord>>asList(
              Internal.createUniqueKey(jhi.germinate.server.database.tables.Mapdefinitions.MAPDEFINITIONS, "KEY_mapdefinitions_PRIMARY", jhi.germinate.server.database.tables.Mapdefinitions.MAPDEFINITIONS.ID)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mapdefinitions as(String alias) {
        return new Mapdefinitions(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mapdefinitions as(Name alias) {
        return new Mapdefinitions(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Mapdefinitions rename(String name) {
        return new Mapdefinitions(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Mapdefinitions rename(Name name) {
        return new Mapdefinitions(name, null);
    }
// @formatter:on
}
