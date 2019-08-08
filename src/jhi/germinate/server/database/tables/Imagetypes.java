/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables;


import org.jooq.*;
import org.jooq.impl.*;

import java.sql.*;
import java.util.*;

import javax.annotation.*;

import jhi.germinate.server.database.*;
import jhi.germinate.server.database.tables.records.*;


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
public class Imagetypes extends TableImpl<ImagetypesRecord> {

    /**
     * The reference instance of <code>germinate_template_3_6_0.imagetypes</code>
     */
    public static final Imagetypes IMAGETYPES = new Imagetypes();
    private static final long serialVersionUID = 453775536;
    /**
     * The column <code>germinate_template_3_6_0.imagetypes.id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public final TableField<ImagetypesRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "Primary id for this table. This uniquely identifies the row.");
    /**
     * The column <code>germinate_template_3_6_0.imagetypes.description</code>. A description of the image type. This would usually be a description of what the image was showing in general terms such as 'field image' or 'insitu hybridisation images'.
     */
    public final TableField<ImagetypesRecord, String> DESCRIPTION = createField("description", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "A description of the image type. This would usually be a description of what the image was showing in general terms such as 'field image' or 'insitu hybridisation images'.");
    /**
     * The column <code>germinate_template_3_6_0.imagetypes.reference_table</code>. The table which the image type relates to.
     */
    public final TableField<ImagetypesRecord, String> REFERENCE_TABLE = createField("reference_table", org.jooq.impl.SQLDataType.VARCHAR(50).nullable(false).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "The table which the image type relates to.");
    /**
     * The column <code>germinate_template_3_6_0.imagetypes.created_on</code>. When the record was created.
     */
    public final TableField<ImagetypesRecord, Timestamp> CREATED_ON = createField("created_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "When the record was created.");
    /**
     * The column <code>germinate_template_3_6_0.imagetypes.updated_on</code>. When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.
     */
    public final TableField<ImagetypesRecord, Timestamp> UPDATED_ON = createField("updated_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.");

    /**
     * Create a <code>germinate_template_3_6_0.imagetypes</code> table reference
     */
    public Imagetypes() {
        this(DSL.name("imagetypes"), null);
    }

    /**
     * Create an aliased <code>germinate_template_3_6_0.imagetypes</code> table reference
     */
    public Imagetypes(String alias) {
        this(DSL.name(alias), IMAGETYPES);
    }

    /**
     * Create an aliased <code>germinate_template_3_6_0.imagetypes</code> table reference
     */
    public Imagetypes(Name alias) {
        this(alias, IMAGETYPES);
    }

    private Imagetypes(Name alias, Table<ImagetypesRecord> aliased) {
        this(alias, aliased, null);
    }

    private Imagetypes(Name alias, Table<ImagetypesRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> Imagetypes(Table<O> child, ForeignKey<O, ImagetypesRecord> key) {
        super(child, key, IMAGETYPES);
    }

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ImagetypesRecord> getRecordType() {
        return ImagetypesRecord.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return GerminateTemplate_3_6_0.GERMINATE_TEMPLATE_3_6_0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.IMAGETYPES_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<ImagetypesRecord, Integer> getIdentity() {
        return Keys.IDENTITY_IMAGETYPES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<ImagetypesRecord> getPrimaryKey() {
        return Keys.KEY_IMAGETYPES_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<ImagetypesRecord>> getKeys() {
        return Arrays.<UniqueKey<ImagetypesRecord>>asList(Keys.KEY_IMAGETYPES_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Imagetypes as(String alias) {
        return new Imagetypes(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Imagetypes as(Name alias) {
        return new Imagetypes(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Imagetypes rename(String name) {
        return new Imagetypes(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Imagetypes rename(Name name) {
        return new Imagetypes(name, null);
    }
}
