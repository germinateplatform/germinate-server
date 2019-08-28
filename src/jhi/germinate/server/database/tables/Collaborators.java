/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables;


import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import jhi.germinate.server.database.GerminateTemplate_3_7_0;
import jhi.germinate.server.database.tables.records.CollaboratorsRecord;

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
public class Collaborators extends TableImpl<CollaboratorsRecord> {

    private static final long serialVersionUID = -729660969;

    /**
     * The reference instance of <code>germinate_template_3_7_0.collaborators</code>
     */
    public static final Collaborators COLLABORATORS = new Collaborators();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CollaboratorsRecord> getRecordType() {
        return CollaboratorsRecord.class;
    }

    /**
     * The column <code>germinate_template_3_7_0.collaborators.id</code>.
     */
    public final TableField<CollaboratorsRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>germinate_template_3_7_0.collaborators.first_name</code>. Last name (surname) of the author(s), researcher(s), scientist(s), student(s) responsible for producing the information product.
     */
    public final TableField<CollaboratorsRecord, String> FIRST_NAME = createField("first_name", org.jooq.impl.SQLDataType.VARCHAR(255).nullable(false), this, "Last name (surname) of the author(s), researcher(s), scientist(s), student(s) responsible for producing the information product.");

    /**
     * The column <code>germinate_template_3_7_0.collaborators.last_name</code>. First name (and middle name if available) of the author(s), researcher(s), scientist(s), student(s) responsible for producing the information product.
     */
    public final TableField<CollaboratorsRecord, String> LAST_NAME = createField("last_name", org.jooq.impl.SQLDataType.VARCHAR(255).nullable(false), this, "First name (and middle name if available) of the author(s), researcher(s), scientist(s), student(s) responsible for producing the information product.");

    /**
     * The column <code>germinate_template_3_7_0.collaborators.email</code>. E-mail address of the author(s), researcher(s), scientist(s), student(s) responsible for producing the information product.
     */
    public final TableField<CollaboratorsRecord, String> EMAIL = createField("email", org.jooq.impl.SQLDataType.VARCHAR(255), this, "E-mail address of the author(s), researcher(s), scientist(s), student(s) responsible for producing the information product.");

    /**
     * The column <code>germinate_template_3_7_0.collaborators.phone</code>. Phone number of the author(s), researcher(s), scientist(s), student(s) responsible for producing the information product.
     */
    public final TableField<CollaboratorsRecord, String> PHONE = createField("phone", org.jooq.impl.SQLDataType.VARCHAR(255), this, "Phone number of the author(s), researcher(s), scientist(s), student(s) responsible for producing the information product.");

    /**
     * The column <code>germinate_template_3_7_0.collaborators.institution_id</code>. Author's affiliation when the resource was created. Foreign key to 'institutions'
     */
    public final TableField<CollaboratorsRecord, Integer> INSTITUTION_ID = createField("institution_id", org.jooq.impl.SQLDataType.INTEGER, this, "Author's affiliation when the resource was created. Foreign key to 'institutions'");

    /**
     * The column <code>germinate_template_3_7_0.collaborators.created_on</code>. When the record was created.
     */
    public final TableField<CollaboratorsRecord, Timestamp> CREATED_ON = createField("created_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "When the record was created.");

    /**
     * The column <code>germinate_template_3_7_0.collaborators.updated_on</code>. When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.
     */
    public final TableField<CollaboratorsRecord, Timestamp> UPDATED_ON = createField("updated_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.");

    /**
     * Create a <code>germinate_template_3_7_0.collaborators</code> table reference
     */
    public Collaborators() {
        this(DSL.name("collaborators"), null);
    }

    /**
     * Create an aliased <code>germinate_template_3_7_0.collaborators</code> table reference
     */
    public Collaborators(String alias) {
        this(DSL.name(alias), COLLABORATORS);
    }

    /**
     * Create an aliased <code>germinate_template_3_7_0.collaborators</code> table reference
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
    public Identity<CollaboratorsRecord, Integer> getIdentity() {
        return Internal.createIdentity(jhi.germinate.server.database.tables.Collaborators.COLLABORATORS, jhi.germinate.server.database.tables.Collaborators.COLLABORATORS.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<CollaboratorsRecord> getPrimaryKey() {
        return Internal.createUniqueKey(jhi.germinate.server.database.tables.Collaborators.COLLABORATORS, "KEY_collaborators_PRIMARY", jhi.germinate.server.database.tables.Collaborators.COLLABORATORS.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<CollaboratorsRecord>> getKeys() {
        return Arrays.<UniqueKey<CollaboratorsRecord>>asList(
              Internal.createUniqueKey(jhi.germinate.server.database.tables.Collaborators.COLLABORATORS, "KEY_collaborators_PRIMARY", jhi.germinate.server.database.tables.Collaborators.COLLABORATORS.ID)
        );
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
// @formatter:on
}
