/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables;


import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import jhi.germinate.server.database.GerminateTemplate_4_0_0;
import jhi.germinate.server.database.tables.records.InstitutionsRecord;

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
 * Defines institutions within Germinate. Accessions may be associated with 
 * an institute and this can be defined here.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Institutions extends TableImpl<InstitutionsRecord> {

    private static final long serialVersionUID = -2043451490;

    /**
     * The reference instance of <code>germinate_template_4_0_0.institutions</code>
     */
    public static final Institutions INSTITUTIONS = new Institutions();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<InstitutionsRecord> getRecordType() {
        return InstitutionsRecord.class;
    }

    /**
     * The column <code>germinate_template_4_0_0.institutions.id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public final TableField<InstitutionsRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "Primary id for this table. This uniquely identifies the row.");

    /**
     * The column <code>germinate_template_4_0_0.institutions.code</code>. If there is a defined ISO code for the institute this should be used here.
     */
    public final TableField<InstitutionsRecord, String> CODE = createField("code", org.jooq.impl.SQLDataType.VARCHAR(255), this, "If there is a defined ISO code for the institute this should be used here.");

    /**
     * The column <code>germinate_template_4_0_0.institutions.name</code>. The institute name.
     */
    public final TableField<InstitutionsRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR(255).nullable(false).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "The institute name.");

    /**
     * The column <code>germinate_template_4_0_0.institutions.acronym</code>. If there is an acronym for the institute.
     */
    public final TableField<InstitutionsRecord, String> ACRONYM = createField("acronym", org.jooq.impl.SQLDataType.VARCHAR(20), this, "If there is an acronym for the institute.");

    /**
     * The column <code>germinate_template_4_0_0.institutions.country_id</code>. Foreign key to countries.id.
     */
    public final TableField<InstitutionsRecord, Integer> COUNTRY_ID = createField("country_id", org.jooq.impl.SQLDataType.INTEGER, this, "Foreign key to countries.id.");

    /**
     * The column <code>germinate_template_4_0_0.institutions.contact</code>. The contact at the institute which should be used for correspondence.
     */
    public final TableField<InstitutionsRecord, String> CONTACT = createField("contact", org.jooq.impl.SQLDataType.VARCHAR(255), this, "The contact at the institute which should be used for correspondence.");

    /**
     * The column <code>germinate_template_4_0_0.institutions.phone</code>. The telephone number for the institute.
     */
    public final TableField<InstitutionsRecord, String> PHONE = createField("phone", org.jooq.impl.SQLDataType.VARCHAR(255), this, "The telephone number for the institute.");

    /**
     * The column <code>germinate_template_4_0_0.institutions.email</code>. The email address to contact the institute.
     */
    public final TableField<InstitutionsRecord, String> EMAIL = createField("email", org.jooq.impl.SQLDataType.VARCHAR(255), this, "The email address to contact the institute.");

    /**
     * The column <code>germinate_template_4_0_0.institutions.address</code>. The postal address of the institute.
     */
    public final TableField<InstitutionsRecord, String> ADDRESS = createField("address", org.jooq.impl.SQLDataType.CLOB, this, "The postal address of the institute.");

    /**
     * The column <code>germinate_template_4_0_0.institutions.created_on</code>. When the record was created.
     */
    public final TableField<InstitutionsRecord, Timestamp> CREATED_ON = createField("created_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "When the record was created.");

    /**
     * The column <code>germinate_template_4_0_0.institutions.updated_on</code>. When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.
     */
    public final TableField<InstitutionsRecord, Timestamp> UPDATED_ON = createField("updated_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.");

    /**
     * Create a <code>germinate_template_4_0_0.institutions</code> table reference
     */
    public Institutions() {
        this(DSL.name("institutions"), null);
    }

    /**
     * Create an aliased <code>germinate_template_4_0_0.institutions</code> table reference
     */
    public Institutions(String alias) {
        this(DSL.name(alias), INSTITUTIONS);
    }

    /**
     * Create an aliased <code>germinate_template_4_0_0.institutions</code> table reference
     */
    public Institutions(Name alias) {
        this(alias, INSTITUTIONS);
    }

    private Institutions(Name alias, Table<InstitutionsRecord> aliased) {
        this(alias, aliased, null);
    }

    private Institutions(Name alias, Table<InstitutionsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("Defines institutions within Germinate. Accessions may be associated with an institute and this can be defined here."));
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
    public Identity<InstitutionsRecord, Integer> getIdentity() {
        return Internal.createIdentity(jhi.germinate.server.database.tables.Institutions.INSTITUTIONS, jhi.germinate.server.database.tables.Institutions.INSTITUTIONS.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<InstitutionsRecord> getPrimaryKey() {
        return Internal.createUniqueKey(jhi.germinate.server.database.tables.Institutions.INSTITUTIONS, "KEY_institutions_PRIMARY", jhi.germinate.server.database.tables.Institutions.INSTITUTIONS.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<InstitutionsRecord>> getKeys() {
        return Arrays.<UniqueKey<InstitutionsRecord>>asList(
              Internal.createUniqueKey(jhi.germinate.server.database.tables.Institutions.INSTITUTIONS, "KEY_institutions_PRIMARY", jhi.germinate.server.database.tables.Institutions.INSTITUTIONS.ID)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Institutions as(String alias) {
        return new Institutions(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Institutions as(Name alias) {
        return new Institutions(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Institutions rename(String name) {
        return new Institutions(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Institutions rename(Name name) {
        return new Institutions(name, null);
    }
// @formatter:on
}
