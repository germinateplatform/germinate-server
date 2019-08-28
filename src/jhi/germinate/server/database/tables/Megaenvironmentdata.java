/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables;


import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import jhi.germinate.server.database.GerminateTemplate_3_7_0;
import jhi.germinate.server.database.tables.records.MegaenvironmentdataRecord;

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
 * Describes mega environment data by grouping collection sites (locations) 
 * into mega environments. Mega environments in this context are collections 
 * of sites which meet the mega environment definition criteria.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Megaenvironmentdata extends TableImpl<MegaenvironmentdataRecord> {

    private static final long serialVersionUID = -2096238972;

    /**
     * The reference instance of <code>germinate_template_3_7_0.megaenvironmentdata</code>
     */
    public static final Megaenvironmentdata MEGAENVIRONMENTDATA = new Megaenvironmentdata();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MegaenvironmentdataRecord> getRecordType() {
        return MegaenvironmentdataRecord.class;
    }

    /**
     * The column <code>germinate_template_3_7_0.megaenvironmentdata.id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public final TableField<MegaenvironmentdataRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "Primary id for this table. This uniquely identifies the row.");

    /**
     * The column <code>germinate_template_3_7_0.megaenvironmentdata.location_id</code>. Foreign key to locations (locations.id).
     */
    public final TableField<MegaenvironmentdataRecord, Integer> LOCATION_ID = createField("location_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "Foreign key to locations (locations.id).");

    /**
     * The column <code>germinate_template_3_7_0.megaenvironmentdata.source_id</code>. Source ID
     */
    public final TableField<MegaenvironmentdataRecord, Integer> SOURCE_ID = createField("source_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "Source ID");

    /**
     * The column <code>germinate_template_3_7_0.megaenvironmentdata.megaenvironment_id</code>. Foreign key to megaenvironments (megaenvironments.id).
     */
    public final TableField<MegaenvironmentdataRecord, Integer> MEGAENVIRONMENT_ID = createField("megaenvironment_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "Foreign key to megaenvironments (megaenvironments.id).");

    /**
     * The column <code>germinate_template_3_7_0.megaenvironmentdata.is_final</code>. The source that was used to determine the megaenvironment data.
     */
    public final TableField<MegaenvironmentdataRecord, Byte> IS_FINAL = createField("is_final", org.jooq.impl.SQLDataType.TINYINT, this, "The source that was used to determine the megaenvironment data.");

    /**
     * The column <code>germinate_template_3_7_0.megaenvironmentdata.created_on</code>. When the record was created.
     */
    public final TableField<MegaenvironmentdataRecord, Timestamp> CREATED_ON = createField("created_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "When the record was created.");

    /**
     * The column <code>germinate_template_3_7_0.megaenvironmentdata.updated_on</code>. When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.
     */
    public final TableField<MegaenvironmentdataRecord, Timestamp> UPDATED_ON = createField("updated_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.");

    /**
     * Create a <code>germinate_template_3_7_0.megaenvironmentdata</code> table reference
     */
    public Megaenvironmentdata() {
        this(DSL.name("megaenvironmentdata"), null);
    }

    /**
     * Create an aliased <code>germinate_template_3_7_0.megaenvironmentdata</code> table reference
     */
    public Megaenvironmentdata(String alias) {
        this(DSL.name(alias), MEGAENVIRONMENTDATA);
    }

    /**
     * Create an aliased <code>germinate_template_3_7_0.megaenvironmentdata</code> table reference
     */
    public Megaenvironmentdata(Name alias) {
        this(alias, MEGAENVIRONMENTDATA);
    }

    private Megaenvironmentdata(Name alias, Table<MegaenvironmentdataRecord> aliased) {
        this(alias, aliased, null);
    }

    private Megaenvironmentdata(Name alias, Table<MegaenvironmentdataRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("Describes mega environment data by grouping collection sites (locations) into mega environments. Mega environments in this context are collections of sites which meet the mega environment definition criteria."));
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
    public Identity<MegaenvironmentdataRecord, Integer> getIdentity() {
        return Internal.createIdentity(jhi.germinate.server.database.tables.Megaenvironmentdata.MEGAENVIRONMENTDATA, jhi.germinate.server.database.tables.Megaenvironmentdata.MEGAENVIRONMENTDATA.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<MegaenvironmentdataRecord> getPrimaryKey() {
        return Internal.createUniqueKey(jhi.germinate.server.database.tables.Megaenvironmentdata.MEGAENVIRONMENTDATA, "KEY_megaenvironmentdata_PRIMARY", jhi.germinate.server.database.tables.Megaenvironmentdata.MEGAENVIRONMENTDATA.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<MegaenvironmentdataRecord>> getKeys() {
        return Arrays.<UniqueKey<MegaenvironmentdataRecord>>asList(
              Internal.createUniqueKey(jhi.germinate.server.database.tables.Megaenvironmentdata.MEGAENVIRONMENTDATA, "KEY_megaenvironmentdata_PRIMARY", jhi.germinate.server.database.tables.Megaenvironmentdata.MEGAENVIRONMENTDATA.ID)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Megaenvironmentdata as(String alias) {
        return new Megaenvironmentdata(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Megaenvironmentdata as(Name alias) {
        return new Megaenvironmentdata(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Megaenvironmentdata rename(String name) {
        return new Megaenvironmentdata(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Megaenvironmentdata rename(Name name) {
        return new Megaenvironmentdata(name, null);
    }
// @formatter:on
}
