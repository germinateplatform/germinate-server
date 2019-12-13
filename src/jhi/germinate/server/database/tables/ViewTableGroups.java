/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables;


import java.sql.Timestamp;

import javax.annotation.Generated;

import jhi.germinate.server.database.GerminateTemplate_3_7_0;
import jhi.germinate.server.database.tables.records.ViewTableGroupsRecord;

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
public class ViewTableGroups extends TableImpl<ViewTableGroupsRecord> {

    private static final long serialVersionUID = 122816937;

    /**
     * The reference instance of <code>germinate_template_3_7_0.view_table_groups</code>
     */
    public static final ViewTableGroups VIEW_TABLE_GROUPS = new ViewTableGroups();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ViewTableGroupsRecord> getRecordType() {
        return ViewTableGroupsRecord.class;
    }

    /**
     * The column <code>germinate_template_3_7_0.view_table_groups.group_id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public final TableField<ViewTableGroupsRecord, Integer> GROUP_ID = createField("group_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "Primary id for this table. This uniquely identifies the row.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_groups.group_name</code>. The name of the group which can be used to identify it.
     */
    public final TableField<ViewTableGroupsRecord, String> GROUP_NAME = createField("group_name", org.jooq.impl.SQLDataType.VARCHAR(255), this, "The name of the group which can be used to identify it.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_groups.group_description</code>. A free text description of the group. This has no length limitations.
     */
    public final TableField<ViewTableGroupsRecord, String> GROUP_DESCRIPTION = createField("group_description", org.jooq.impl.SQLDataType.CLOB, this, "A free text description of the group. This has no length limitations.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_groups.group_type_id</code>.
     */
    public final TableField<ViewTableGroupsRecord, Integer> GROUP_TYPE_ID = createField("group_type_id", org.jooq.impl.SQLDataType.INTEGER.defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>germinate_template_3_7_0.view_table_groups.group_type</code>.
     */
    public final TableField<ViewTableGroupsRecord, String> GROUP_TYPE = createField("group_type", org.jooq.impl.SQLDataType.VARCHAR(255).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "");

    /**
     * The column <code>germinate_template_3_7_0.view_table_groups.user_name</code>.
     */
    public final TableField<ViewTableGroupsRecord, String> USER_NAME = createField("user_name", org.jooq.impl.SQLDataType.CHAR.nullable(false).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.CHAR)), this, "");

    /**
     * The column <code>germinate_template_3_7_0.view_table_groups.user_id</code>. Defines who created the group. Foreign key to Gatekeeper users (Gatekeeper users.id).
     */
    public final TableField<ViewTableGroupsRecord, Integer> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.INTEGER, this, "Defines who created the group. Foreign key to Gatekeeper users (Gatekeeper users.id).");

    /**
     * The column <code>germinate_template_3_7_0.view_table_groups.group_visibility</code>. Defines if the group is visuble or hidden from the Germinate user interface.
     */
    public final TableField<ViewTableGroupsRecord, Boolean> GROUP_VISIBILITY = createField("group_visibility", org.jooq.impl.SQLDataType.BOOLEAN, this, "Defines if the group is visuble or hidden from the Germinate user interface.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_groups.created_on</code>. Foreign key to locations (locations.id).
     */
    public final TableField<ViewTableGroupsRecord, Timestamp> CREATED_ON = createField("created_on", org.jooq.impl.SQLDataType.TIMESTAMP, this, "Foreign key to locations (locations.id).");

    /**
     * The column <code>germinate_template_3_7_0.view_table_groups.updated_on</code>. When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.
     */
    public final TableField<ViewTableGroupsRecord, Timestamp> UPDATED_ON = createField("updated_on", org.jooq.impl.SQLDataType.TIMESTAMP, this, "When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_groups.count</code>.
     */
    public final TableField<ViewTableGroupsRecord, Long> COUNT = createField("count", org.jooq.impl.SQLDataType.BIGINT.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.BIGINT)), this, "");

    /**
     * Create a <code>germinate_template_3_7_0.view_table_groups</code> table reference
     */
    public ViewTableGroups() {
        this(DSL.name("view_table_groups"), null);
    }

    /**
     * Create an aliased <code>germinate_template_3_7_0.view_table_groups</code> table reference
     */
    public ViewTableGroups(String alias) {
        this(DSL.name(alias), VIEW_TABLE_GROUPS);
    }

    /**
     * Create an aliased <code>germinate_template_3_7_0.view_table_groups</code> table reference
     */
    public ViewTableGroups(Name alias) {
        this(alias, VIEW_TABLE_GROUPS);
    }

    private ViewTableGroups(Name alias, Table<ViewTableGroupsRecord> aliased) {
        this(alias, aliased, null);
    }

    private ViewTableGroups(Name alias, Table<ViewTableGroupsRecord> aliased, Field<?>[] parameters) {
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
    public ViewTableGroups as(String alias) {
        return new ViewTableGroups(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableGroups as(Name alias) {
        return new ViewTableGroups(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewTableGroups rename(String name) {
        return new ViewTableGroups(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewTableGroups rename(Name name) {
        return new ViewTableGroups(name, null);
    }
// @formatter:on
}
