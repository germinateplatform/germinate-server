/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables;


import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import jhi.germinate.server.database.GerminateTemplate_4_0_0;
import jhi.germinate.server.database.tables.records.UnitsRecord;

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
 * The 'units' table holds descriptions of the various units that are used 
 * in the Germinate database. Examples of these would include International 
 * System of Units (SI) base units: kilogram, meter, second, ampere, kelvin, 
 * candela and mole but can include any units that are required.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Units extends TableImpl<UnitsRecord> {

    private static final long serialVersionUID = 1457614771;

    /**
     * The reference instance of <code>germinate_template_4_0_0.units</code>
     */
    public static final Units UNITS = new Units();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<UnitsRecord> getRecordType() {
        return UnitsRecord.class;
    }

    /**
     * The column <code>germinate_template_4_0_0.units.id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public final TableField<UnitsRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "Primary id for this table. This uniquely identifies the row.");

    /**
     * The column <code>germinate_template_4_0_0.units.unit_name</code>. The name of the unit. This should be the name of the unit in full.
     */
    public final TableField<UnitsRecord, String> UNIT_NAME = createField("unit_name", org.jooq.impl.SQLDataType.VARCHAR(255).nullable(false).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "The name of the unit. This should be the name of the unit in full.");

    /**
     * The column <code>germinate_template_4_0_0.units.unit_abbreviation</code>. This should be the unit abbreviation.
     */
    public final TableField<UnitsRecord, String> UNIT_ABBREVIATION = createField("unit_abbreviation", org.jooq.impl.SQLDataType.CHAR(10), this, "This should be the unit abbreviation.");

    /**
     * The column <code>germinate_template_4_0_0.units.unit_description</code>. A description of the unit. If the unit is not a standard SI unit then it is beneficial to have a description which explains what the unit it, how it is derived and any other information which would help identifiy it.
     */
    public final TableField<UnitsRecord, String> UNIT_DESCRIPTION = createField("unit_description", org.jooq.impl.SQLDataType.CLOB, this, "A description of the unit. If the unit is not a standard SI unit then it is beneficial to have a description which explains what the unit it, how it is derived and any other information which would help identifiy it.");

    /**
     * The column <code>germinate_template_4_0_0.units.created_on</code>. When the record was created.
     */
    public final TableField<UnitsRecord, Timestamp> CREATED_ON = createField("created_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "When the record was created.");

    /**
     * The column <code>germinate_template_4_0_0.units.updated_on</code>. When the record was updated. This may be different from the created on date if changes have been made subsequently to the underlying record.
     */
    public final TableField<UnitsRecord, Timestamp> UPDATED_ON = createField("updated_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "When the record was updated. This may be different from the created on date if changes have been made subsequently to the underlying record.");

    /**
     * Create a <code>germinate_template_4_0_0.units</code> table reference
     */
    public Units() {
        this(DSL.name("units"), null);
    }

    /**
     * Create an aliased <code>germinate_template_4_0_0.units</code> table reference
     */
    public Units(String alias) {
        this(DSL.name(alias), UNITS);
    }

    /**
     * Create an aliased <code>germinate_template_4_0_0.units</code> table reference
     */
    public Units(Name alias) {
        this(alias, UNITS);
    }

    private Units(Name alias, Table<UnitsRecord> aliased) {
        this(alias, aliased, null);
    }

    private Units(Name alias, Table<UnitsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("The 'units' table holds descriptions of the various units that are used in the Germinate database. Examples of these would include International System of Units (SI) base units: kilogram, meter, second, ampere, kelvin, candela and mole but can include any units that are required."));
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
    public Identity<UnitsRecord, Integer> getIdentity() {
        return Internal.createIdentity(jhi.germinate.server.database.tables.Units.UNITS, jhi.germinate.server.database.tables.Units.UNITS.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<UnitsRecord> getPrimaryKey() {
        return Internal.createUniqueKey(jhi.germinate.server.database.tables.Units.UNITS, "KEY_units_PRIMARY", jhi.germinate.server.database.tables.Units.UNITS.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<UnitsRecord>> getKeys() {
        return Arrays.<UniqueKey<UnitsRecord>>asList(
              Internal.createUniqueKey(jhi.germinate.server.database.tables.Units.UNITS, "KEY_units_PRIMARY", jhi.germinate.server.database.tables.Units.UNITS.ID)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Units as(String alias) {
        return new Units(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Units as(Name alias) {
        return new Units(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Units rename(String name) {
        return new Units(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Units rename(Name name) {
        return new Units(name, null);
    }
// @formatter:on
}
