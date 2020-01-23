/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables;


import javax.annotation.Generated;

import jhi.germinate.server.database.GerminateTemplate_4_0_0;
import jhi.germinate.server.database.tables.records.ViewTableEntitiesRecord;

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
public class ViewTableEntities extends TableImpl<ViewTableEntitiesRecord> {

    private static final long serialVersionUID = -891408160;

    /**
     * The reference instance of <code>germinate_template_4_0_0.view_table_entities</code>
     */
    public static final ViewTableEntities VIEW_TABLE_ENTITIES = new ViewTableEntities();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ViewTableEntitiesRecord> getRecordType() {
        return ViewTableEntitiesRecord.class;
    }

    /**
     * The column <code>germinate_template_4_0_0.view_table_entities.entity_parent_id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public final TableField<ViewTableEntitiesRecord, Integer> ENTITY_PARENT_ID = createField("entity_parent_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "Primary id for this table. This uniquely identifies the row.");

    /**
     * The column <code>germinate_template_4_0_0.view_table_entities.entity_parent_gid</code>. A unique identifier.
     */
    public final TableField<ViewTableEntitiesRecord, String> ENTITY_PARENT_GID = createField("entity_parent_gid", org.jooq.impl.SQLDataType.VARCHAR(255).nullable(false), this, "A unique identifier.");

    /**
     * The column <code>germinate_template_4_0_0.view_table_entities.entity_parent_name</code>. A unique name which defines an entry in the germinatbase table.
     */
    public final TableField<ViewTableEntitiesRecord, String> ENTITY_PARENT_NAME = createField("entity_parent_name", org.jooq.impl.SQLDataType.VARCHAR(255).nullable(false), this, "A unique name which defines an entry in the germinatbase table.");

    /**
     * The column <code>germinate_template_4_0_0.view_table_entities.entity_parent_type</code>. The name of the entity type.
     */
    public final TableField<ViewTableEntitiesRecord, String> ENTITY_PARENT_TYPE = createField("entity_parent_type", org.jooq.impl.SQLDataType.VARCHAR(255), this, "The name of the entity type.");

    /**
     * The column <code>germinate_template_4_0_0.view_table_entities.entity_child_id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public final TableField<ViewTableEntitiesRecord, Integer> ENTITY_CHILD_ID = createField("entity_child_id", org.jooq.impl.SQLDataType.INTEGER.defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "Primary id for this table. This uniquely identifies the row.");

    /**
     * The column <code>germinate_template_4_0_0.view_table_entities.entity_child_gid</code>. A unique identifier.
     */
    public final TableField<ViewTableEntitiesRecord, String> ENTITY_CHILD_GID = createField("entity_child_gid", org.jooq.impl.SQLDataType.VARCHAR(255), this, "A unique identifier.");

    /**
     * The column <code>germinate_template_4_0_0.view_table_entities.entity_child_name</code>. A unique name which defines an entry in the germinatbase table.
     */
    public final TableField<ViewTableEntitiesRecord, String> ENTITY_CHILD_NAME = createField("entity_child_name", org.jooq.impl.SQLDataType.VARCHAR(255), this, "A unique name which defines an entry in the germinatbase table.");

    /**
     * The column <code>germinate_template_4_0_0.view_table_entities.entity_child_type</code>. The name of the entity type.
     */
    public final TableField<ViewTableEntitiesRecord, String> ENTITY_CHILD_TYPE = createField("entity_child_type", org.jooq.impl.SQLDataType.VARCHAR(255), this, "The name of the entity type.");

    /**
     * Create a <code>germinate_template_4_0_0.view_table_entities</code> table reference
     */
    public ViewTableEntities() {
        this(DSL.name("view_table_entities"), null);
    }

    /**
     * Create an aliased <code>germinate_template_4_0_0.view_table_entities</code> table reference
     */
    public ViewTableEntities(String alias) {
        this(DSL.name(alias), VIEW_TABLE_ENTITIES);
    }

    /**
     * Create an aliased <code>germinate_template_4_0_0.view_table_entities</code> table reference
     */
    public ViewTableEntities(Name alias) {
        this(alias, VIEW_TABLE_ENTITIES);
    }

    private ViewTableEntities(Name alias, Table<ViewTableEntitiesRecord> aliased) {
        this(alias, aliased, null);
    }

    private ViewTableEntities(Name alias, Table<ViewTableEntitiesRecord> aliased, Field<?>[] parameters) {
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
    public ViewTableEntities as(String alias) {
        return new ViewTableEntities(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableEntities as(Name alias) {
        return new ViewTableEntities(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewTableEntities rename(String name) {
        return new ViewTableEntities(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewTableEntities rename(Name name) {
        return new ViewTableEntities(name, null);
    }
// @formatter:on
}
