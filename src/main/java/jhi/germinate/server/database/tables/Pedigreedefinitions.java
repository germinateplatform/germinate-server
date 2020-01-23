/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables;


import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import jhi.germinate.server.database.GerminateTemplate_4_0_0;
import jhi.germinate.server.database.tables.records.PedigreedefinitionsRecord;

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
 * This table holds the actual pedigree definition data.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Pedigreedefinitions extends TableImpl<PedigreedefinitionsRecord> {

    private static final long serialVersionUID = -126475518;

    /**
     * The reference instance of <code>germinate_template_4_0_0.pedigreedefinitions</code>
     */
    public static final Pedigreedefinitions PEDIGREEDEFINITIONS = new Pedigreedefinitions();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PedigreedefinitionsRecord> getRecordType() {
        return PedigreedefinitionsRecord.class;
    }

    /**
     * The column <code>germinate_template_4_0_0.pedigreedefinitions.id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public final TableField<PedigreedefinitionsRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "Primary id for this table. This uniquely identifies the row.");

    /**
     * The column <code>germinate_template_4_0_0.pedigreedefinitions.germinatebase_id</code>. Foreign key to germinatebase (germinatebase.id).
     */
    public final TableField<PedigreedefinitionsRecord, Integer> GERMINATEBASE_ID = createField("germinatebase_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "Foreign key to germinatebase (germinatebase.id).");

    /**
     * The column <code>germinate_template_4_0_0.pedigreedefinitions.pedigreenotation_id</code>. Foreign key to pedigreenotations (pedigreenotations.id).
     */
    public final TableField<PedigreedefinitionsRecord, Integer> PEDIGREENOTATION_ID = createField("pedigreenotation_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "Foreign key to pedigreenotations (pedigreenotations.id).");

    /**
     * The column <code>germinate_template_4_0_0.pedigreedefinitions.pedigreedescription_id</code>.
     */
    public final TableField<PedigreedefinitionsRecord, Integer> PEDIGREEDESCRIPTION_ID = createField("pedigreedescription_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>germinate_template_4_0_0.pedigreedefinitions.definition</code>. The pedigree string which is used to represent the germinatebase entry.
     */
    public final TableField<PedigreedefinitionsRecord, String> DEFINITION = createField("definition", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "The pedigree string which is used to represent the germinatebase entry.");

    /**
     * The column <code>germinate_template_4_0_0.pedigreedefinitions.created_on</code>. When the record was created.
     */
    public final TableField<PedigreedefinitionsRecord, Timestamp> CREATED_ON = createField("created_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "When the record was created.");

    /**
     * The column <code>germinate_template_4_0_0.pedigreedefinitions.updated_on</code>. When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.
     */
    public final TableField<PedigreedefinitionsRecord, Timestamp> UPDATED_ON = createField("updated_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.");

    /**
     * Create a <code>germinate_template_4_0_0.pedigreedefinitions</code> table reference
     */
    public Pedigreedefinitions() {
        this(DSL.name("pedigreedefinitions"), null);
    }

    /**
     * Create an aliased <code>germinate_template_4_0_0.pedigreedefinitions</code> table reference
     */
    public Pedigreedefinitions(String alias) {
        this(DSL.name(alias), PEDIGREEDEFINITIONS);
    }

    /**
     * Create an aliased <code>germinate_template_4_0_0.pedigreedefinitions</code> table reference
     */
    public Pedigreedefinitions(Name alias) {
        this(alias, PEDIGREEDEFINITIONS);
    }

    private Pedigreedefinitions(Name alias, Table<PedigreedefinitionsRecord> aliased) {
        this(alias, aliased, null);
    }

    private Pedigreedefinitions(Name alias, Table<PedigreedefinitionsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("This table holds the actual pedigree definition data."));
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
    public Identity<PedigreedefinitionsRecord, Integer> getIdentity() {
        return Internal.createIdentity(jhi.germinate.server.database.tables.Pedigreedefinitions.PEDIGREEDEFINITIONS, jhi.germinate.server.database.tables.Pedigreedefinitions.PEDIGREEDEFINITIONS.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<PedigreedefinitionsRecord> getPrimaryKey() {
        return Internal.createUniqueKey(jhi.germinate.server.database.tables.Pedigreedefinitions.PEDIGREEDEFINITIONS, "KEY_pedigreedefinitions_PRIMARY", jhi.germinate.server.database.tables.Pedigreedefinitions.PEDIGREEDEFINITIONS.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<PedigreedefinitionsRecord>> getKeys() {
        return Arrays.<UniqueKey<PedigreedefinitionsRecord>>asList(
              Internal.createUniqueKey(jhi.germinate.server.database.tables.Pedigreedefinitions.PEDIGREEDEFINITIONS, "KEY_pedigreedefinitions_PRIMARY", jhi.germinate.server.database.tables.Pedigreedefinitions.PEDIGREEDEFINITIONS.ID)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pedigreedefinitions as(String alias) {
        return new Pedigreedefinitions(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pedigreedefinitions as(Name alias) {
        return new Pedigreedefinitions(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Pedigreedefinitions rename(String name) {
        return new Pedigreedefinitions(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Pedigreedefinitions rename(Name name) {
        return new Pedigreedefinitions(name, null);
    }
// @formatter:on
}
