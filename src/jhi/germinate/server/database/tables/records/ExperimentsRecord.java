/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables.records;


import org.jooq.*;
import org.jooq.impl.*;

import java.sql.*;

import javax.annotation.*;

import jhi.germinate.server.database.tables.*;


/**
 * Defines ecperiments that are held in Germinate.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ExperimentsRecord extends UpdatableRecordImpl<ExperimentsRecord> implements Record8<Integer, String, Integer, String, Date, Integer, Timestamp, Timestamp> {

    private static final long serialVersionUID = 623075167;

    /**
     * Create a detached ExperimentsRecord
     */
    public ExperimentsRecord() {
        super(Experiments.EXPERIMENTS);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.experiments.id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Create a detached, initialised ExperimentsRecord
     */
    public ExperimentsRecord(Integer id, String experimentName, Integer userId, String description, Date experimentDate, Integer experimentTypeId, Timestamp createdOn, Timestamp updatedOn) {
        super(Experiments.EXPERIMENTS);

        set(0, id);
        set(1, experimentName);
        set(2, userId);
        set(3, description);
        set(4, experimentDate);
        set(5, experimentTypeId);
        set(6, createdOn);
        set(7, updatedOn);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.experiments.experiment_name</code>. The name of the experiment.
     */
    public String getExperimentName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.experiments.user_id</code>. Foreign key to Gatekeeper users (Gatekeeper users.id).

     */
    public void setUserId(Integer value) {
        set(2, value);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.experiments.id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.experiments.description</code>. Describes the experiment.
     */
    public void setDescription(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.experiments.description</code>. Describes the experiment.
     */
    public String getDescription() {
        return (String) get(3);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.experiments.experiment_name</code>. The name of the experiment.
     */
    public void setExperimentName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.experiments.experiment_date</code>. The date that the experiment was carried out.
     */
    public Date getExperimentDate() {
        return (Date) get(4);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.experiments.experiment_type_id</code>. Foreign key to experimenttypes (experimenttypes.id).

     */
    public void setExperimentTypeId(Integer value) {
        set(5, value);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.experiments.user_id</code>. Foreign key to Gatekeeper users (Gatekeeper users.id).

     */
    public Integer getUserId() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.experiments.created_on</code>. When the record was created.
     */
    public void setCreatedOn(Timestamp value) {
        set(6, value);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.experiments.experiment_date</code>. The date that the experiment was carried out.
     */
    public void setExperimentDate(Date value) {
        set(4, value);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.experiments.experiment_type_id</code>. Foreign key to experimenttypes (experimenttypes.id).

     */
    public Integer getExperimentTypeId() {
        return (Integer) get(5);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.experiments.created_on</code>. When the record was created.
     */
    public Timestamp getCreatedOn() {
        return (Timestamp) get(6);
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
    // Record8 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row8<Integer, String, Integer, String, Date, Integer, Timestamp, Timestamp> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row8<Integer, String, Integer, String, Date, Integer, Timestamp, Timestamp> valuesRow() {
        return (Row8) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return Experiments.EXPERIMENTS.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return Experiments.EXPERIMENTS.EXPERIMENT_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field3() {
        return Experiments.EXPERIMENTS.USER_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return Experiments.EXPERIMENTS.DESCRIPTION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Date> field5() {
        return Experiments.EXPERIMENTS.EXPERIMENT_DATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field6() {
        return Experiments.EXPERIMENTS.EXPERIMENT_TYPE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field7() {
        return Experiments.EXPERIMENTS.CREATED_ON;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field8() {
        return Experiments.EXPERIMENTS.UPDATED_ON;
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
    public String component2() {
        return getExperimentName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component3() {
        return getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component4() {
        return getDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date component5() {
        return getExperimentDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component6() {
        return getExperimentTypeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component7() {
        return getCreatedOn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component8() {
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
    public String value2() {
        return getExperimentName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value3() {
        return getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date value5() {
        return getExperimentDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value6() {
        return getExperimentTypeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value7() {
        return getCreatedOn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value8() {
        return getUpdatedOn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExperimentsRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExperimentsRecord value2(String value) {
        setExperimentName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExperimentsRecord value3(Integer value) {
        setUserId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExperimentsRecord value4(String value) {
        setDescription(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExperimentsRecord value5(Date value) {
        setExperimentDate(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExperimentsRecord value6(Integer value) {
        setExperimentTypeId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExperimentsRecord value7(Timestamp value) {
        setCreatedOn(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExperimentsRecord value8(Timestamp value) {
        setUpdatedOn(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExperimentsRecord values(Integer value1, String value2, Integer value3, String value4, Date value5, Integer value6, Timestamp value7, Timestamp value8) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Getter for <code>germinate_template_3_7_0.experiments.updated_on</code>. When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.
     */
    public Timestamp getUpdatedOn() {
        return (Timestamp) get(7);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.experiments.updated_on</code>. When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.
     */
    public void setUpdatedOn(Timestamp value) {
        set(7, value);
    }
}
