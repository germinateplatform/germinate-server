/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables;


import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import jhi.germinate.server.database.GerminateTemplate_4_0_0;
import jhi.germinate.server.database.tables.records.DatasetpermissionsRecord;

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
 * This defines which users can view which datasets. Requires Germinate Gatekeeper. 
 * This overrides the datasets state.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Datasetpermissions extends TableImpl<DatasetpermissionsRecord> {

    private static final long serialVersionUID = 142624031;

    /**
     * The reference instance of <code>germinate_template_4_0_0.datasetpermissions</code>
     */
    public static final Datasetpermissions DATASETPERMISSIONS = new Datasetpermissions();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DatasetpermissionsRecord> getRecordType() {
        return DatasetpermissionsRecord.class;
    }

    /**
     * The column <code>germinate_template_4_0_0.datasetpermissions.id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public final TableField<DatasetpermissionsRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "Primary id for this table. This uniquely identifies the row.");

    /**
     * The column <code>germinate_template_4_0_0.datasetpermissions.dataset_id</code>. Foreign key to datasets (datasets.id).
     */
    public final TableField<DatasetpermissionsRecord, Integer> DATASET_ID = createField("dataset_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "Foreign key to datasets (datasets.id).");

    /**
     * The column <code>germinate_template_4_0_0.datasetpermissions.user_id</code>. Foreign key to Gatekeeper users (Gatekeeper usersid).
     */
    public final TableField<DatasetpermissionsRecord, Integer> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.INTEGER, this, "Foreign key to Gatekeeper users (Gatekeeper usersid).");

    /**
     * The column <code>germinate_template_4_0_0.datasetpermissions.group_id</code>. Foreign key to usergroups table.
     */
    public final TableField<DatasetpermissionsRecord, Integer> GROUP_ID = createField("group_id", org.jooq.impl.SQLDataType.INTEGER, this, "Foreign key to usergroups table.");

    /**
     * The column <code>germinate_template_4_0_0.datasetpermissions.created_on</code>. When the record was created.
     */
    public final TableField<DatasetpermissionsRecord, Timestamp> CREATED_ON = createField("created_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "When the record was created.");

    /**
     * The column <code>germinate_template_4_0_0.datasetpermissions.updated_on</code>. When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.
     */
    public final TableField<DatasetpermissionsRecord, Timestamp> UPDATED_ON = createField("updated_on", org.jooq.impl.SQLDataType.TIMESTAMP.defaultValue(org.jooq.impl.DSL.field("CURRENT_TIMESTAMP", org.jooq.impl.SQLDataType.TIMESTAMP)), this, "When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.");

    /**
     * Create a <code>germinate_template_4_0_0.datasetpermissions</code> table reference
     */
    public Datasetpermissions() {
        this(DSL.name("datasetpermissions"), null);
    }

    /**
     * Create an aliased <code>germinate_template_4_0_0.datasetpermissions</code> table reference
     */
    public Datasetpermissions(String alias) {
        this(DSL.name(alias), DATASETPERMISSIONS);
    }

    /**
     * Create an aliased <code>germinate_template_4_0_0.datasetpermissions</code> table reference
     */
    public Datasetpermissions(Name alias) {
        this(alias, DATASETPERMISSIONS);
    }

    private Datasetpermissions(Name alias, Table<DatasetpermissionsRecord> aliased) {
        this(alias, aliased, null);
    }

    private Datasetpermissions(Name alias, Table<DatasetpermissionsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("This defines which users can view which datasets. Requires Germinate Gatekeeper. This overrides the datasets state."));
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
    public Identity<DatasetpermissionsRecord, Integer> getIdentity() {
        return Internal.createIdentity(jhi.germinate.server.database.tables.Datasetpermissions.DATASETPERMISSIONS, jhi.germinate.server.database.tables.Datasetpermissions.DATASETPERMISSIONS.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<DatasetpermissionsRecord> getPrimaryKey() {
        return Internal.createUniqueKey(jhi.germinate.server.database.tables.Datasetpermissions.DATASETPERMISSIONS, "KEY_datasetpermissions_PRIMARY", jhi.germinate.server.database.tables.Datasetpermissions.DATASETPERMISSIONS.ID);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<DatasetpermissionsRecord>> getKeys() {
        return Arrays.<UniqueKey<DatasetpermissionsRecord>>asList(
              Internal.createUniqueKey(jhi.germinate.server.database.tables.Datasetpermissions.DATASETPERMISSIONS, "KEY_datasetpermissions_PRIMARY", jhi.germinate.server.database.tables.Datasetpermissions.DATASETPERMISSIONS.ID)
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Datasetpermissions as(String alias) {
        return new Datasetpermissions(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Datasetpermissions as(Name alias) {
        return new Datasetpermissions(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Datasetpermissions rename(String name) {
        return new Datasetpermissions(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Datasetpermissions rename(Name name) {
        return new Datasetpermissions(name, null);
    }
// @formatter:on
}
