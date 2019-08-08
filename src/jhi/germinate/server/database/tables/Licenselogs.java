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
public class Licenselogs extends TableImpl<LicenselogsRecord> {

    /**
     * The reference instance of <code>germinate_template_3_6_0.licenselogs</code>
     */
    public static final Licenselogs LICENSELOGS = new Licenselogs();
    private static final long serialVersionUID = 77719584;
    /**
     * The column <code>germinate_template_3_6_0.licenselogs.id</code>.
     */
    public final TableField<LicenselogsRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");
    /**
     * The column <code>germinate_template_3_6_0.licenselogs.license_id</code>.
     */
    public final TableField<LicenselogsRecord, Integer> LICENSE_ID = createField("license_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");
    /**
     * The column <code>germinate_template_3_6_0.licenselogs.user_id</code>.
     */
    public final TableField<LicenselogsRecord, Integer> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");
    /**
     * The column <code>germinate_template_3_6_0.licenselogs.accepted_on</code>.
     */
    public final TableField<LicenselogsRecord, Timestamp> ACCEPTED_ON = createField("accepted_on", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * Create a <code>germinate_template_3_6_0.licenselogs</code> table reference
     */
    public Licenselogs() {
        this(DSL.name("licenselogs"), null);
    }

    /**
     * Create an aliased <code>germinate_template_3_6_0.licenselogs</code> table reference
     */
    public Licenselogs(String alias) {
        this(DSL.name(alias), LICENSELOGS);
    }

    /**
     * Create an aliased <code>germinate_template_3_6_0.licenselogs</code> table reference
     */
    public Licenselogs(Name alias) {
        this(alias, LICENSELOGS);
    }

    private Licenselogs(Name alias, Table<LicenselogsRecord> aliased) {
        this(alias, aliased, null);
    }

    private Licenselogs(Name alias, Table<LicenselogsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> Licenselogs(Table<O> child, ForeignKey<O, LicenselogsRecord> key) {
        super(child, key, LICENSELOGS);
    }

    /**
     * The class holding records for this type
     */
    @Override
    public Class<LicenselogsRecord> getRecordType() {
        return LicenselogsRecord.class;
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
        return Arrays.<Index>asList(Indexes.LICENSELOGS_LICENSE_ID, Indexes.LICENSELOGS_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<LicenselogsRecord, Integer> getIdentity() {
        return Keys.IDENTITY_LICENSELOGS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<LicenselogsRecord> getPrimaryKey() {
        return Keys.KEY_LICENSELOGS_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<LicenselogsRecord>> getKeys() {
        return Arrays.<UniqueKey<LicenselogsRecord>>asList(Keys.KEY_LICENSELOGS_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<LicenselogsRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<LicenselogsRecord, ?>>asList(Keys.LICENSELOGS_IBFK_1);
    }

    public Licenses licenses() {
        return new Licenses(this, Keys.LICENSELOGS_IBFK_1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Licenselogs as(String alias) {
        return new Licenselogs(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Licenselogs as(Name alias) {
        return new Licenselogs(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Licenselogs rename(String name) {
        return new Licenselogs(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Licenselogs rename(Name name) {
        return new Licenselogs(name, null);
    }
}
