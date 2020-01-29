/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables;


import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import jhi.germinate.server.database.GerminateTemplate_4_0_0;
import jhi.germinate.server.database.tables.records.DatasetlocationsRecord;

import org.jooq.Field;
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
public class Datasetlocations extends TableImpl<DatasetlocationsRecord> {

    private static final long serialVersionUID = -1220217492;

    /**
     * The reference instance of <code>germinate_template_4_0_0.datasetlocations</code>
     */
    public static final Datasetlocations DATASETLOCATIONS = new Datasetlocations();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DatasetlocationsRecord> getRecordType() {
        return DatasetlocationsRecord.class;
    }

    /**
     * The column <code>germinate_template_4_0_0.datasetlocations.dataset_id</code>.
     */
    public final TableField<DatasetlocationsRecord, Integer> DATASET_ID = createField("dataset_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>germinate_template_4_0_0.datasetlocations.location_id</code>.
     */
    public final TableField<DatasetlocationsRecord, Integer> LOCATION_ID = createField("location_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>germinate_template_4_0_0.datasetlocations.created_on</code>.
     */
    public final TableField<DatasetlocationsRecord, Timestamp> CREATED_ON = createField("created_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * The column <code>germinate_template_4_0_0.datasetlocations.updated_on</code>.
     */
    public final TableField<DatasetlocationsRecord, Timestamp> UPDATED_ON = createField("updated_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * Create a <code>germinate_template_4_0_0.datasetlocations</code> table reference
     */
    public Datasetlocations() {
        this(DSL.name("datasetlocations"), null);
    }

    /**
     * Create an aliased <code>germinate_template_4_0_0.datasetlocations</code> table reference
     */
    public Datasetlocations(String alias) {
        this(DSL.name(alias), DATASETLOCATIONS);
    }

    /**
     * Create an aliased <code>germinate_template_4_0_0.datasetlocations</code> table reference
     */
    public Datasetlocations(Name alias) {
        this(alias, DATASETLOCATIONS);
    }

    private Datasetlocations(Name alias, Table<DatasetlocationsRecord> aliased) {
        this(alias, aliased, null);
    }

    private Datasetlocations(Name alias, Table<DatasetlocationsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
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
    public UniqueKey<DatasetlocationsRecord> getPrimaryKey() {
        return Internal.createUniqueKey(jhi.germinate.server.database.tables.Datasetlocations.DATASETLOCATIONS, "KEY_datasetlocations_PRIMARY", jhi.germinate.server.database.tables.Datasetlocations.DATASETLOCATIONS.DATASET_ID, jhi.germinate.server.database.tables.Datasetlocations.DATASETLOCATIONS.LOCATION_ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<DatasetlocationsRecord>> getKeys() {
        return Arrays.<UniqueKey<DatasetlocationsRecord>>asList(
              Internal.createUniqueKey(jhi.germinate.server.database.tables.Datasetlocations.DATASETLOCATIONS, "KEY_datasetlocations_PRIMARY", jhi.germinate.server.database.tables.Datasetlocations.DATASETLOCATIONS.DATASET_ID, jhi.germinate.server.database.tables.Datasetlocations.DATASETLOCATIONS.LOCATION_ID)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Datasetlocations as(String alias) {
        return new Datasetlocations(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Datasetlocations as(Name alias) {
        return new Datasetlocations(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Datasetlocations rename(String name) {
        return new Datasetlocations(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Datasetlocations rename(Name name) {
        return new Datasetlocations(name, null);
    }
// @formatter:on
}