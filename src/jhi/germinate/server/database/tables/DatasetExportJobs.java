/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables;


import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import jhi.germinate.server.database.GerminateTemplate_3_7_0;
import jhi.germinate.server.database.enums.DatasetExportJobsStatus;
import jhi.germinate.server.database.tables.records.DatasetExportJobsRecord;

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
public class DatasetExportJobs extends TableImpl<DatasetExportJobsRecord> {

    private static final long serialVersionUID = -987473141;

    /**
     * The reference instance of <code>germinate_template_3_7_0.dataset_export_jobs</code>
     */
    public static final DatasetExportJobs DATASET_EXPORT_JOBS = new DatasetExportJobs();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DatasetExportJobsRecord> getRecordType() {
        return DatasetExportJobsRecord.class;
    }

    /**
     * The column <code>germinate_template_3_7_0.dataset_export_jobs.id</code>.
     */
    public final TableField<DatasetExportJobsRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>germinate_template_3_7_0.dataset_export_jobs.uuid</code>.
     */
    public final TableField<DatasetExportJobsRecord, String> UUID = createField("uuid", org.jooq.impl.SQLDataType.VARCHAR(36).nullable(false), this, "");

    /**
     * The column <code>germinate_template_3_7_0.dataset_export_jobs.job_id</code>.
     */
    public final TableField<DatasetExportJobsRecord, String> JOB_ID = createField("job_id", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>germinate_template_3_7_0.dataset_export_jobs.user_id</code>.
     */
    public final TableField<DatasetExportJobsRecord, Integer> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>germinate_template_3_7_0.dataset_export_jobs.status</code>.
     */
    public final TableField<DatasetExportJobsRecord, DatasetExportJobsStatus> STATUS = createField("status", org.jooq.impl.SQLDataType.VARCHAR(9).nullable(false).defaultValue(org.jooq.impl.DSL.inline("waiting", org.jooq.impl.SQLDataType.VARCHAR)).asEnumDataType(jhi.germinate.server.database.enums.DatasetExportJobsStatus.class), this, "");

    /**
     * The column <code>germinate_template_3_7_0.dataset_export_jobs.visibility</code>.
     */
    public final TableField<DatasetExportJobsRecord, Boolean> VISIBILITY = createField("visibility", org.jooq.impl.SQLDataType.BOOLEAN.nullable(false).defaultValue(org.jooq.impl.DSL.inline("1", org.jooq.impl.SQLDataType.BOOLEAN)), this, "");

    /**
     * The column <code>germinate_template_3_7_0.dataset_export_jobs.experiment_type_id</code>.
     */
    public final TableField<DatasetExportJobsRecord, Integer> EXPERIMENT_TYPE_ID = createField("experiment_type_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * @deprecated Unknown data type. Please define an explicit {@link org.jooq.Binding} to specify how this type should be handled. Deprecation can be turned off using {@literal <deprecationOnUnknownTypes/>} in your code generator configuration.
     */
    @java.lang.Deprecated
    public final TableField<DatasetExportJobsRecord, Object> METADATA = createField("metadata", org.jooq.impl.DefaultDataType.getDefaultDataType("\"germinate_template_3_7_0\".\"dataset_export_jobs_metadata\""), this, "");

    /**
     * The column <code>germinate_template_3_7_0.dataset_export_jobs.created_on</code>.
     */
    public final TableField<DatasetExportJobsRecord, Timestamp> CREATED_ON = createField("created_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * The column <code>germinate_template_3_7_0.dataset_export_jobs.updated_on</code>.
     */
    public final TableField<DatasetExportJobsRecord, Timestamp> UPDATED_ON = createField("updated_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "");

    /**
     * Create a <code>germinate_template_3_7_0.dataset_export_jobs</code> table reference
     */
    public DatasetExportJobs() {
        this(DSL.name("dataset_export_jobs"), null);
    }

    /**
     * Create an aliased <code>germinate_template_3_7_0.dataset_export_jobs</code> table reference
     */
    public DatasetExportJobs(String alias) {
        this(DSL.name(alias), DATASET_EXPORT_JOBS);
    }

    /**
     * Create an aliased <code>germinate_template_3_7_0.dataset_export_jobs</code> table reference
     */
    public DatasetExportJobs(Name alias) {
        this(alias, DATASET_EXPORT_JOBS);
    }

    private DatasetExportJobs(Name alias, Table<DatasetExportJobsRecord> aliased) {
        this(alias, aliased, null);
    }

    private DatasetExportJobs(Name alias, Table<DatasetExportJobsRecord> aliased, Field<?>[] parameters) {
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
    public Identity<DatasetExportJobsRecord, Integer> getIdentity() {
        return Internal.createIdentity(jhi.germinate.server.database.tables.DatasetExportJobs.DATASET_EXPORT_JOBS, jhi.germinate.server.database.tables.DatasetExportJobs.DATASET_EXPORT_JOBS.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<DatasetExportJobsRecord> getPrimaryKey() {
        return Internal.createUniqueKey(jhi.germinate.server.database.tables.DatasetExportJobs.DATASET_EXPORT_JOBS, "KEY_dataset_export_jobs_PRIMARY", jhi.germinate.server.database.tables.DatasetExportJobs.DATASET_EXPORT_JOBS.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<DatasetExportJobsRecord>> getKeys() {
        return Arrays.<UniqueKey<DatasetExportJobsRecord>>asList(
              Internal.createUniqueKey(jhi.germinate.server.database.tables.DatasetExportJobs.DATASET_EXPORT_JOBS, "KEY_dataset_export_jobs_PRIMARY", jhi.germinate.server.database.tables.DatasetExportJobs.DATASET_EXPORT_JOBS.ID)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetExportJobs as(String alias) {
        return new DatasetExportJobs(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetExportJobs as(Name alias) {
        return new DatasetExportJobs(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public DatasetExportJobs rename(String name) {
        return new DatasetExportJobs(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public DatasetExportJobs rename(Name name) {
        return new DatasetExportJobs(name, null);
    }
// @formatter:on
}
