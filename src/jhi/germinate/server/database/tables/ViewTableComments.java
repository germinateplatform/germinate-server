/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables;


import javax.annotation.Generated;

import jhi.germinate.server.database.GerminateTemplate_3_7_0;
import jhi.germinate.server.database.tables.records.ViewTableCommentsRecord;

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
public class ViewTableComments extends TableImpl<ViewTableCommentsRecord> {

    private static final long serialVersionUID = -1263630692;

    /**
     * The reference instance of <code>germinate_template_3_7_0.view_table_comments</code>
     */
    public static final ViewTableComments VIEW_TABLE_COMMENTS = new ViewTableComments();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ViewTableCommentsRecord> getRecordType() {
        return ViewTableCommentsRecord.class;
    }

    /**
     * The column <code>germinate_template_3_7_0.view_table_comments.comment_id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public final TableField<ViewTableCommentsRecord, Integer> COMMENT_ID = createField("comment_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "Primary id for this table. This uniquely identifies the row.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_comments.comment_type</code>. This could include 'germinatebase' or 'markers' to define the table that the comment relates to.
     */
    public final TableField<ViewTableCommentsRecord, String> COMMENT_TYPE = createField("comment_type", org.jooq.impl.SQLDataType.VARCHAR(50).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "This could include 'germinatebase' or 'markers' to define the table that the comment relates to.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_comments.comment_foreign_id</code>. Relates to the UID of the table to which the comment relates
     */
    public final TableField<ViewTableCommentsRecord, Integer> COMMENT_FOREIGN_ID = createField("comment_foreign_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "Relates to the UID of the table to which the comment relates");

    /**
     * The column <code>germinate_template_3_7_0.view_table_comments.user_id</code>. Foreign key to Gatekeeper users (Gatekeeper users.id).
     */
    public final TableField<ViewTableCommentsRecord, Integer> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.INTEGER, this, "Foreign key to Gatekeeper users (Gatekeeper users.id).");

    /**
     * The column <code>germinate_template_3_7_0.view_table_comments.user_name</code>.
     */
    public final TableField<ViewTableCommentsRecord, String> USER_NAME = createField("user_name", org.jooq.impl.SQLDataType.CHAR.nullable(false).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.CHAR)), this, "");

    /**
     * The column <code>germinate_template_3_7_0.view_table_comments.comment_content</code>. The comment content.
     */
    public final TableField<ViewTableCommentsRecord, String> COMMENT_CONTENT = createField("comment_content", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "The comment content.");

    /**
     * Create a <code>germinate_template_3_7_0.view_table_comments</code> table reference
     */
    public ViewTableComments() {
        this(DSL.name("view_table_comments"), null);
    }

    /**
     * Create an aliased <code>germinate_template_3_7_0.view_table_comments</code> table reference
     */
    public ViewTableComments(String alias) {
        this(DSL.name(alias), VIEW_TABLE_COMMENTS);
    }

    /**
     * Create an aliased <code>germinate_template_3_7_0.view_table_comments</code> table reference
     */
    public ViewTableComments(Name alias) {
        this(alias, VIEW_TABLE_COMMENTS);
    }

    private ViewTableComments(Name alias, Table<ViewTableCommentsRecord> aliased) {
        this(alias, aliased, null);
    }

    private ViewTableComments(Name alias, Table<ViewTableCommentsRecord> aliased, Field<?>[] parameters) {
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
    public ViewTableComments as(String alias) {
        return new ViewTableComments(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableComments as(Name alias) {
        return new ViewTableComments(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewTableComments rename(String name) {
        return new ViewTableComments(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewTableComments rename(Name name) {
        return new ViewTableComments(name, null);
    }
// @formatter:on
}
