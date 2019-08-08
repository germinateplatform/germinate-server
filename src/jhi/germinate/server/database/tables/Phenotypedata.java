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
 * Contains phenotypic data which has been collected.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Phenotypedata extends TableImpl<PhenotypedataRecord> {

    /**
     * The reference instance of <code>germinate_template_3_6_0.phenotypedata</code>
     */
    public static final Phenotypedata PHENOTYPEDATA = new Phenotypedata();
    private static final long serialVersionUID = -1063629531;
    /**
     * The column <code>germinate_template_3_6_0.phenotypedata.id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public final TableField<PhenotypedataRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "Primary id for this table. This uniquely identifies the row.");
    /**
     * The column <code>germinate_template_3_6_0.phenotypedata.phenotype_id</code>. Foreign key phenotypes (phenotype.id).
     */
    public final TableField<PhenotypedataRecord, Integer> PHENOTYPE_ID = createField("phenotype_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "Foreign key phenotypes (phenotype.id).");
    /**
     * The column <code>germinate_template_3_6_0.phenotypedata.germinatebase_id</code>. Foreign key germinatebase (germinatebase.id).
     */
    public final TableField<PhenotypedataRecord, Integer> GERMINATEBASE_ID = createField("germinatebase_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "Foreign key germinatebase (germinatebase.id).");
    /**
     * The column <code>germinate_template_3_6_0.phenotypedata.phenotype_value</code>. The phenotype value for this phenotype_id and germinatebase_id combination.
     */
    public final TableField<PhenotypedataRecord, String> PHENOTYPE_VALUE = createField("phenotype_value", org.jooq.impl.SQLDataType.VARCHAR(255), this, "The phenotype value for this phenotype_id and germinatebase_id combination.");
    /**
     * The column <code>germinate_template_3_6_0.phenotypedata.dataset_id</code>. Foreign key datasets (datasets.id).
     */
    public final TableField<PhenotypedataRecord, Integer> DATASET_ID = createField("dataset_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "Foreign key datasets (datasets.id).");
    /**
     * The column <code>germinate_template_3_6_0.phenotypedata.recording_date</code>. Date when the phenotypic result was recorded. Should be formatted 'YYYY-MM-DD HH:MM:SS' or just 'YYYY-MM-DD' where a timestamp is not available.
     */
    public final TableField<PhenotypedataRecord, Timestamp> RECORDING_DATE = createField("recording_date", org.jooq.impl.SQLDataType.TIMESTAMP, this, "Date when the phenotypic result was recorded. Should be formatted 'YYYY-MM-DD HH:MM:SS' or just 'YYYY-MM-DD' where a timestamp is not available.");
    /**
     * The column <code>germinate_template_3_6_0.phenotypedata.created_on</code>. When the record was created.
     */
    public final TableField<PhenotypedataRecord, Timestamp> CREATED_ON = createField("created_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "When the record was created.");
    /**
     * The column <code>germinate_template_3_6_0.phenotypedata.updated_on</code>. When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.
     */
    public final TableField<PhenotypedataRecord, Timestamp> UPDATED_ON = createField("updated_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.");
    /**
     * The column <code>germinate_template_3_6_0.phenotypedata.location_id</code>. Foreign key to locations (locations.id).
     */
    public final TableField<PhenotypedataRecord, Integer> LOCATION_ID = createField("location_id", org.jooq.impl.SQLDataType.INTEGER, this, "Foreign key to locations (locations.id).");
    /**
     * The column <code>germinate_template_3_6_0.phenotypedata.treatment_id</code>. Foreign key to treatments (treatments.id).
     */
    public final TableField<PhenotypedataRecord, Integer> TREATMENT_ID = createField("treatment_id", org.jooq.impl.SQLDataType.INTEGER, this, "Foreign key to treatments (treatments.id).");
    /**
     * The column <code>germinate_template_3_6_0.phenotypedata.trialseries_id</code>. Foreign key to trialseries (trialseries.id).
     */
    public final TableField<PhenotypedataRecord, Integer> TRIALSERIES_ID = createField("trialseries_id", org.jooq.impl.SQLDataType.INTEGER, this, "Foreign key to trialseries (trialseries.id).");

    /**
     * Create a <code>germinate_template_3_6_0.phenotypedata</code> table reference
     */
    public Phenotypedata() {
        this(DSL.name("phenotypedata"), null);
    }

    /**
     * Create an aliased <code>germinate_template_3_6_0.phenotypedata</code> table reference
     */
    public Phenotypedata(String alias) {
        this(DSL.name(alias), PHENOTYPEDATA);
    }

    /**
     * Create an aliased <code>germinate_template_3_6_0.phenotypedata</code> table reference
     */
    public Phenotypedata(Name alias) {
        this(alias, PHENOTYPEDATA);
    }

    private Phenotypedata(Name alias, Table<PhenotypedataRecord> aliased) {
        this(alias, aliased, null);
    }

    private Phenotypedata(Name alias, Table<PhenotypedataRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("Contains phenotypic data which has been collected."));
    }

    public <O extends Record> Phenotypedata(Table<O> child, ForeignKey<O, PhenotypedataRecord> key) {
        super(child, key, PHENOTYPEDATA);
    }

    /**
     * The class holding records for this type
     */
    @Override
    public Class<PhenotypedataRecord> getRecordType() {
        return PhenotypedataRecord.class;
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
        return Arrays.<Index>asList(Indexes.PHENOTYPEDATA_DATASET_ID, Indexes.PHENOTYPEDATA_DATASET_ID_2, Indexes.PHENOTYPEDATA_GERMINATEBASE_ID, Indexes.PHENOTYPEDATA_PHENOTYPEDATA_RECORDING_DATE, Indexes.PHENOTYPEDATA_PHENOTYPES_IBFK_LOCATIONS, Indexes.PHENOTYPEDATA_PHENOTYPES_IBFK_TREATMENT, Indexes.PHENOTYPEDATA_PHENOTYPES_IBFK_TRIALSERIES, Indexes.PHENOTYPEDATA_PHENOTYPE_ID, Indexes.PHENOTYPEDATA_PRIMARY, Indexes.PHENOTYPEDATA_TRIALS_QUERY_INDEX);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<PhenotypedataRecord, Integer> getIdentity() {
        return Keys.IDENTITY_PHENOTYPEDATA;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<PhenotypedataRecord> getPrimaryKey() {
        return Keys.KEY_PHENOTYPEDATA_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<PhenotypedataRecord>> getKeys() {
        return Arrays.<UniqueKey<PhenotypedataRecord>>asList(Keys.KEY_PHENOTYPEDATA_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<PhenotypedataRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<PhenotypedataRecord, ?>>asList(Keys.PHENOTYPEDATA_IBFK_2, Keys.PHENOTYPEDATA_IBFK_3, Keys.PHENOTYPEDATA_IBFK_1, Keys.PHENOTYPEDATA_IBFK_4, Keys.PHENOTYPEDATA_IBFK_5, Keys.PHENOTYPEDATA_IBFK_6);
    }

    public Phenotypes phenotypes() {
        return new Phenotypes(this, Keys.PHENOTYPEDATA_IBFK_2);
    }

    public Germinatebase germinatebase() {
        return new Germinatebase(this, Keys.PHENOTYPEDATA_IBFK_3);
    }

    public Datasets datasets() {
        return new Datasets(this, Keys.PHENOTYPEDATA_IBFK_1);
    }

    public Locations locations() {
        return new Locations(this, Keys.PHENOTYPEDATA_IBFK_4);
    }

    public Treatments treatments() {
        return new Treatments(this, Keys.PHENOTYPEDATA_IBFK_5);
    }

    public Trialseries trialseries() {
        return new Trialseries(this, Keys.PHENOTYPEDATA_IBFK_6);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Phenotypedata as(String alias) {
        return new Phenotypedata(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Phenotypedata as(Name alias) {
        return new Phenotypedata(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Phenotypedata rename(String name) {
        return new Phenotypedata(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Phenotypedata rename(Name name) {
        return new Phenotypedata(name, null);
    }
}
