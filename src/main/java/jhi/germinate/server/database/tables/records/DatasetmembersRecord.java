/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables.records;


import java.sql.Timestamp;

import javax.annotation.Generated;

import jhi.germinate.server.database.tables.Datasetmembers;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;


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
public class DatasetmembersRecord extends UpdatableRecordImpl<DatasetmembersRecord> implements Record6<Integer, Integer, Integer, Integer, Timestamp, Timestamp> {

    private static final long serialVersionUID = -2040409800;

    /**
     * Setter for <code>germinate_template_4_0_0.datasetmembers.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>germinate_template_4_0_0.datasetmembers.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>germinate_template_4_0_0.datasetmembers.dataset_id</code>.
     */
    public void setDatasetId(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>germinate_template_4_0_0.datasetmembers.dataset_id</code>.
     */
    public Integer getDatasetId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>germinate_template_4_0_0.datasetmembers.foreign_id</code>.
     */
    public void setForeignId(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>germinate_template_4_0_0.datasetmembers.foreign_id</code>.
     */
    public Integer getForeignId() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>germinate_template_4_0_0.datasetmembers.datasetmembertype_id</code>.
     */
    public void setDatasetmembertypeId(Integer value) {
        set(3, value);
    }

    /**
     * Getter for <code>germinate_template_4_0_0.datasetmembers.datasetmembertype_id</code>.
     */
    public Integer getDatasetmembertypeId() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>germinate_template_4_0_0.datasetmembers.created_on</code>. When the record was created.
     */
    public void setCreatedOn(Timestamp value) {
        set(4, value);
    }

    /**
     * Getter for <code>germinate_template_4_0_0.datasetmembers.created_on</code>. When the record was created.
     */
    public Timestamp getCreatedOn() {
        return (Timestamp) get(4);
    }

    /**
     * Setter for <code>germinate_template_4_0_0.datasetmembers.updated_on</code>. When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.
     */
    public void setUpdatedOn(Timestamp value) {
        set(5, value);
    }

    /**
     * Getter for <code>germinate_template_4_0_0.datasetmembers.updated_on</code>. When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.
     */
    public Timestamp getUpdatedOn() {
        return (Timestamp) get(5);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row6<Integer, Integer, Integer, Integer, Timestamp, Timestamp> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row6<Integer, Integer, Integer, Integer, Timestamp, Timestamp> valuesRow() {
        return (Row6) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return Datasetmembers.DATASETMEMBERS.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field2() {
        return Datasetmembers.DATASETMEMBERS.DATASET_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field3() {
        return Datasetmembers.DATASETMEMBERS.FOREIGN_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field4() {
        return Datasetmembers.DATASETMEMBERS.DATASETMEMBERTYPE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field5() {
        return Datasetmembers.DATASETMEMBERS.CREATED_ON;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field6() {
        return Datasetmembers.DATASETMEMBERS.UPDATED_ON;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component2() {
        return getDatasetId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component3() {
        return getForeignId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component4() {
        return getDatasetmembertypeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component5() {
        return getCreatedOn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component6() {
        return getUpdatedOn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value2() {
        return getDatasetId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value3() {
        return getForeignId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value4() {
        return getDatasetmembertypeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value5() {
        return getCreatedOn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value6() {
        return getUpdatedOn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetmembersRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetmembersRecord value2(Integer value) {
        setDatasetId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetmembersRecord value3(Integer value) {
        setForeignId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetmembersRecord value4(Integer value) {
        setDatasetmembertypeId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetmembersRecord value5(Timestamp value) {
        setCreatedOn(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetmembersRecord value6(Timestamp value) {
        setUpdatedOn(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetmembersRecord values(Integer value1, Integer value2, Integer value3, Integer value4, Timestamp value5, Timestamp value6) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached DatasetmembersRecord
     */
    public DatasetmembersRecord() {
        super(Datasetmembers.DATASETMEMBERS);
    }

    /**
     * Create a detached, initialised DatasetmembersRecord
     */
    public DatasetmembersRecord(Integer id, Integer datasetId, Integer foreignId, Integer datasetmembertypeId, Timestamp createdOn, Timestamp updatedOn) {
        super(Datasetmembers.DATASETMEMBERS);

        set(0, id);
        set(1, datasetId);
        set(2, foreignId);
        set(3, datasetmembertypeId);
        set(4, createdOn);
        set(5, updatedOn);
    }
// @formatter:on
}
