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
public class Collaborators extends TableImpl<CollaboratorsRecord> {

    /**
     * The reference instance of <code>germinate_template_3_6_0.collaborators</code>
     */
    public static final Collaborators COLLABORATORS = new Collaborators();
    private static final long serialVersionUID = -1503859364;
    /**
     * The column <code>germinate_template_3_6_0.collaborators.id</code>.
     */
    public final TableField<CollaboratorsRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");
    /**
     * The column <code>germinate_template_3_6_0.collaborators.first_name</code>. Last name (surname) of the author(s), researcher(s), scientist(s), student(s) responsible for producing the information product.
     */
    public final TableField<CollaboratorsRecord, String> FIRST_NAME = createField("first_name", org.jooq.impl.SQLDataType.VARCHAR(255).nullable(false), this, "Last name (surname) of the author(s), researcher(s), scientist(s), student(s) responsible for producing the information product.");
    /**
     * The column <code>germinate_template_3_6_0.collaborators.last_name</code>. First name (and middle name if available) of the author(s), researcher(s), scientist(s), student(s) responsible for producing the information product.
     */
    public final TableField<CollaboratorsRecord, String> LAST_NAME = createField("last_name", org.jooq.impl.SQLDataType.VARCHAR(255).nullable(false), this, "First name (and middle name if available) of the author(s), researcher(s), scientist(s), student(s) responsible for producing the information product.");
    /**
     * The column <code>germinate_template_3_6_0.collaborators.email</code>. E-mail address of the author(s), researcher(s), scientist(s), student(s) responsible for producing the information product.
     */
    public final TableField<CollaboratorsRecord, String> EMAIL = createField("email", org.jooq.impl.SQLDataType.VARCHAR(255), this, "E-mail address of the author(s), researcher(s), scientist(s), student(s) responsible for producing the information product.");
    /**
     * The column <code>germinate_template_3_6_0.collaborators.phone</code>. Phone number of the author(s), researcher(s), scientist(s), student(s) responsible for producing the information product.
     */
    public final TableField<CollaboratorsRecord, String> PHONE = createField("phone", org.jooq.impl.SQLDataType.VARCHAR(255), this, "Phone number of the author(s), researcher(s), scientist(s), student(s) responsible for producing the information product.");
    /**
     * The column <code>germinate_template_3_6_0.collaborators.institution_id</code>. Author's affiliation when the resource was created. Foreign key to 'institutions'
     */
    public final TableField<CollaboratorsRecord, Integer> INSTITUTION_ID = createField("institution_id", org.jooq.impl.SQLDataType.INTEGER, this, "Author's affiliation when the resource was created. Foreign key to 'institutions'");
    /**
     * The column <code>germinate_template_3_6_0.collaborators.created_on</code>. When the record was created.
     */
    public final TableField<CollaboratorsRecord, Timestamp> CREATED_ON = createField("created_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "When the record was created.");
    /**
     * The column <code>germinate_template_3_6_0.collaborators.updated_on</code>. When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.
     */
    public final TableField<CollaboratorsRecord, Timestamp> UPDATED_ON = createField("updated_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.");

    /**
     * Create a <code>germinate_template_3_6_0.collaborators</code> table reference
     */
    public Collaborators() {
        this(DSL.name("collaborators"), null);
    }

    /**
     * Create an aliased <code>germinate_template_3_6_0.collaborators</code> table reference
     */
    public Collaborators(String alias) {
        this(DSL.name(alias), COLLABORATORS);
    }

    /**
     * Create an aliased <code>germinate_template_3_6_0.collaborators</code> table reference
     */
    public Collaborators(Name alias) {
        this(alias, COLLABORATORS);
    }

    private Collaborators(Name alias, Table<CollaboratorsRecord> aliased) {
        this(alias, aliased, null);
    }

    private Collaborators(Name alias, Table<CollaboratorsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> Collaborators(Table<O> child, ForeignKey<O, CollaboratorsRecord> key) {
        super(child, key, COLLABORATORS);
    }

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CollaboratorsRecord> getRecordType() {
        return CollaboratorsRecord.class;
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
        return Arrays.<Index>asList(Indexes.COLLABORATORS_INSTITUTION_ID, Indexes.COLLABORATORS_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<CollaboratorsRecord, Integer> getIdentity() {
        return Keys.IDENTITY_COLLABORATORS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<CollaboratorsRecord> getPrimaryKey() {
        return Keys.KEY_COLLABORATORS_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<CollaboratorsRecord>> getKeys() {
        return Arrays.<UniqueKey<CollaboratorsRecord>>asList(Keys.KEY_COLLABORATORS_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<CollaboratorsRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<CollaboratorsRecord, ?>>asList(Keys.COLLABORATORS_IBFK_1);
    }

    public Institutions institutions() {
        return new Institutions(this, Keys.COLLABORATORS_IBFK_1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collaborators as(String alias) {
        return new Collaborators(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collaborators as(Name alias) {
        return new Collaborators(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Collaborators rename(String name) {
        return new Collaborators(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Collaborators rename(Name name) {
        return new Collaborators(name, null);
    }
}
