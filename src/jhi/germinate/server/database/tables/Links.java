/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables;


import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import jhi.germinate.server.database.GerminateTemplate_3_7_0;
import jhi.germinate.server.database.tables.records.LinksRecord;

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
 * Germinate allows to define external links for different types of data. 
 * With this feature you can
 * define links to external resources.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Links extends TableImpl<LinksRecord> {

    private static final long serialVersionUID = -1167149632;

    /**
     * The reference instance of <code>germinate_template_3_7_0.links</code>
     */
    public static final Links LINKS = new Links();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<LinksRecord> getRecordType() {
        return LinksRecord.class;
    }

    /**
     * The column <code>germinate_template_3_7_0.links.id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public final TableField<LinksRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "Primary id for this table. This uniquely identifies the row.");

    /**
     * The column <code>germinate_template_3_7_0.links.linktype_id</code>. Foreign key to linktypes (linktypes.id).
     */
    public final TableField<LinksRecord, Integer> LINKTYPE_ID = createField("linktype_id", org.jooq.impl.SQLDataType.INTEGER, this, "Foreign key to linktypes (linktypes.id).");

    /**
     * The column <code>germinate_template_3_7_0.links.foreign_id</code>.
     */
    public final TableField<LinksRecord, Integer> FOREIGN_ID = createField("foreign_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>germinate_template_3_7_0.links.hyperlink</code>. The actual hyperlink.
     */
    public final TableField<LinksRecord, String> HYPERLINK = createField("hyperlink", org.jooq.impl.SQLDataType.VARCHAR(255), this, "The actual hyperlink.");

    /**
     * The column <code>germinate_template_3_7_0.links.description</code>. A description of the link.
     */
    public final TableField<LinksRecord, String> DESCRIPTION = createField("description", org.jooq.impl.SQLDataType.VARCHAR(255), this, "A description of the link.");

    /**
     * The column <code>germinate_template_3_7_0.links.visibility</code>. Determines if the link is visible or not: {0, 1}
     */
    public final TableField<LinksRecord, Boolean> VISIBILITY = createField("visibility", org.jooq.impl.SQLDataType.BOOLEAN.defaultValue(org.jooq.impl.DSL.inline("1", org.jooq.impl.SQLDataType.BOOLEAN)), this, "Determines if the link is visible or not: {0, 1}");

    /**
     * The column <code>germinate_template_3_7_0.links.created_on</code>. When the record was created.
     */
    public final TableField<LinksRecord, Timestamp> CREATED_ON = createField("created_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "When the record was created.");

    /**
     * The column <code>germinate_template_3_7_0.links.updated_on</code>. When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.
     */
    public final TableField<LinksRecord, Timestamp> UPDATED_ON = createField("updated_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.");

    /**
     * Create a <code>germinate_template_3_7_0.links</code> table reference
     */
    public Links() {
        this(DSL.name("links"), null);
    }

    /**
     * Create an aliased <code>germinate_template_3_7_0.links</code> table reference
     */
    public Links(String alias) {
        this(DSL.name(alias), LINKS);
    }

    /**
     * Create an aliased <code>germinate_template_3_7_0.links</code> table reference
     */
    public Links(Name alias) {
        this(alias, LINKS);
    }

    private Links(Name alias, Table<LinksRecord> aliased) {
        this(alias, aliased, null);
    }

    private Links(Name alias, Table<LinksRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("Germinate allows to define external links for different types of data. With this feature you can\r\ndefine links to external resources."));
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
    public Identity<LinksRecord, Integer> getIdentity() {
        return Internal.createIdentity(jhi.germinate.server.database.tables.Links.LINKS, jhi.germinate.server.database.tables.Links.LINKS.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<LinksRecord> getPrimaryKey() {
        return Internal.createUniqueKey(jhi.germinate.server.database.tables.Links.LINKS, "KEY_links_PRIMARY", jhi.germinate.server.database.tables.Links.LINKS.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<LinksRecord>> getKeys() {
        return Arrays.<UniqueKey<LinksRecord>>asList(
              Internal.createUniqueKey(jhi.germinate.server.database.tables.Links.LINKS, "KEY_links_PRIMARY", jhi.germinate.server.database.tables.Links.LINKS.ID)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Links as(String alias) {
        return new Links(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Links as(Name alias) {
        return new Links(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Links rename(String name) {
        return new Links(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Links rename(Name name) {
        return new Links(name, null);
    }
// @formatter:on
}
