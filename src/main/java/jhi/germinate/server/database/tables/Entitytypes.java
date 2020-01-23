/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables;


import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import jhi.germinate.server.database.GerminateTemplate_4_0_0;
import jhi.germinate.server.database.tables.records.EntitytypesRecord;

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
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Entitytypes extends TableImpl<EntitytypesRecord> {

    private static final long serialVersionUID = -1848545153;

    /**
     * The reference instance of <code>germinate_template_4_0_0.entitytypes</code>
     */
    public static final Entitytypes ENTITYTYPES = new Entitytypes();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<EntitytypesRecord> getRecordType() {
        return EntitytypesRecord.class;
    }

    /**
     * The column <code>germinate_template_4_0_0.entitytypes.id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public final TableField<EntitytypesRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "Primary id for this table. This uniquely identifies the row.");

    /**
     * The column <code>germinate_template_4_0_0.entitytypes.name</code>. The name of the entity type.
     */
    public final TableField<EntitytypesRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR(255).nullable(false), this, "The name of the entity type.");

    /**
     * The column <code>germinate_template_4_0_0.entitytypes.description</code>. Describes the entity type.
     */
    public final TableField<EntitytypesRecord, String> DESCRIPTION = createField("description", org.jooq.impl.SQLDataType.CLOB, this, "Describes the entity type.");

    /**
     * The column <code>germinate_template_4_0_0.entitytypes.created_on</code>. When the record was created.
     */
    public final TableField<EntitytypesRecord, Timestamp> CREATED_ON = createField("created_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "When the record was created.");

    /**
     * The column <code>germinate_template_4_0_0.entitytypes.updated_on</code>. When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.
     */
    public final TableField<EntitytypesRecord, Timestamp> UPDATED_ON = createField("updated_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.");

    /**
     * Create a <code>germinate_template_4_0_0.entitytypes</code> table reference
     */
    public Entitytypes() {
        this(DSL.name("entitytypes"), null);
    }

    /**
     * Create an aliased <code>germinate_template_4_0_0.entitytypes</code> table reference
     */
    public Entitytypes(String alias) {
        this(DSL.name(alias), ENTITYTYPES);
    }

    /**
     * Create an aliased <code>germinate_template_4_0_0.entitytypes</code> table reference
     */
    public Entitytypes(Name alias) {
        this(alias, ENTITYTYPES);
    }

    private Entitytypes(Name alias, Table<EntitytypesRecord> aliased) {
        this(alias, aliased, null);
    }

    private Entitytypes(Name alias, Table<EntitytypesRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
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
    public Identity<EntitytypesRecord, Integer> getIdentity() {
        return Internal.createIdentity(jhi.germinate.server.database.tables.Entitytypes.ENTITYTYPES, jhi.germinate.server.database.tables.Entitytypes.ENTITYTYPES.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<EntitytypesRecord> getPrimaryKey() {
        return Internal.createUniqueKey(jhi.germinate.server.database.tables.Entitytypes.ENTITYTYPES, "KEY_entitytypes_PRIMARY", jhi.germinate.server.database.tables.Entitytypes.ENTITYTYPES.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<EntitytypesRecord>> getKeys() {
        return Arrays.<UniqueKey<EntitytypesRecord>>asList(
              Internal.createUniqueKey(jhi.germinate.server.database.tables.Entitytypes.ENTITYTYPES, "KEY_entitytypes_PRIMARY", jhi.germinate.server.database.tables.Entitytypes.ENTITYTYPES.ID)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Entitytypes as(String alias) {
        return new Entitytypes(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Entitytypes as(Name alias) {
        return new Entitytypes(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Entitytypes rename(String name) {
        return new Entitytypes(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Entitytypes rename(Name name) {
        return new Entitytypes(name, null);
    }
// @formatter:on
}
