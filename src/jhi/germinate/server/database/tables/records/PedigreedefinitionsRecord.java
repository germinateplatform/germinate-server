/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables.records;


import java.sql.Timestamp;

import javax.annotation.Generated;

import jhi.germinate.server.database.tables.Pedigreedefinitions;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record7;
import org.jooq.Row7;
import org.jooq.impl.UpdatableRecordImpl;


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
public class PedigreedefinitionsRecord extends UpdatableRecordImpl<PedigreedefinitionsRecord> implements Record7<Integer, Integer, Integer, Integer, String, Timestamp, Timestamp> {

    private static final long serialVersionUID = 147623474;

    /**
     * Create a detached PedigreedefinitionsRecord
     */
    public PedigreedefinitionsRecord() {
        super(Pedigreedefinitions.PEDIGREEDEFINITIONS);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.pedigreedefinitions.id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Create a detached, initialised PedigreedefinitionsRecord
     */
    public PedigreedefinitionsRecord(Integer id, Integer germinatebaseId, Integer pedigreenotationId, Integer pedigreedescriptionId, String definition, Timestamp createdOn, Timestamp updatedOn) {
        super(Pedigreedefinitions.PEDIGREEDEFINITIONS);

        set(0, id);
        set(1, germinatebaseId);
        set(2, pedigreenotationId);
        set(3, pedigreedescriptionId);
        set(4, definition);
        set(5, createdOn);
        set(6, updatedOn);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.pedigreedefinitions.germinatebase_id</code>. Foreign key to germinatebase (germinatebase.id).
     */
    public Integer getGerminatebaseId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.pedigreedefinitions.pedigreenotation_id</code>. Foreign key to pedigreenotations (pedigreenotations.id).
     */
    public void setPedigreenotationId(Integer value) {
        set(2, value);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.pedigreedefinitions.id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.pedigreedefinitions.pedigreedescription_id</code>.
     */
    public void setPedigreedescriptionId(Integer value) {
        set(3, value);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.pedigreedefinitions.pedigreedescription_id</code>.
     */
    public Integer getPedigreedescriptionId() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.pedigreedefinitions.germinatebase_id</code>. Foreign key to germinatebase (germinatebase.id).
     */
    public void setGerminatebaseId(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.pedigreedefinitions.definition</code>. The pedigree string which is used to represent the germinatebase entry.
     */
    public String getDefinition() {
        return (String) get(4);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.pedigreedefinitions.created_on</code>. When the record was created.
     */
    public void setCreatedOn(Timestamp value) {
        set(5, value);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.pedigreedefinitions.created_on</code>. When the record was created.
     */
    public Timestamp getCreatedOn() {
        return (Timestamp) get(5);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.pedigreedefinitions.pedigreenotation_id</code>. Foreign key to pedigreenotations (pedigreenotations.id).
     */
    public Integer getPedigreenotationId() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.pedigreedefinitions.definition</code>. The pedigree string which is used to represent the germinatebase entry.
     */
    public void setDefinition(String value) {
        set(4, value);
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
    // Record7 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row7<Integer, Integer, Integer, Integer, String, Timestamp, Timestamp> fieldsRow() {
        return (Row7) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row7<Integer, Integer, Integer, Integer, String, Timestamp, Timestamp> valuesRow() {
        return (Row7) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return Pedigreedefinitions.PEDIGREEDEFINITIONS.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field2() {
        return Pedigreedefinitions.PEDIGREEDEFINITIONS.GERMINATEBASE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field3() {
        return Pedigreedefinitions.PEDIGREEDEFINITIONS.PEDIGREENOTATION_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field4() {
        return Pedigreedefinitions.PEDIGREEDEFINITIONS.PEDIGREEDESCRIPTION_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return Pedigreedefinitions.PEDIGREEDEFINITIONS.DEFINITION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field6() {
        return Pedigreedefinitions.PEDIGREEDEFINITIONS.CREATED_ON;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field7() {
        return Pedigreedefinitions.PEDIGREEDEFINITIONS.UPDATED_ON;
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
        return getGerminatebaseId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component3() {
        return getPedigreenotationId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component4() {
        return getPedigreedescriptionId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component5() {
        return getDefinition();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component6() {
        return getCreatedOn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component7() {
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
        return getGerminatebaseId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value3() {
        return getPedigreenotationId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value4() {
        return getPedigreedescriptionId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getDefinition();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value6() {
        return getCreatedOn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value7() {
        return getUpdatedOn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PedigreedefinitionsRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PedigreedefinitionsRecord value2(Integer value) {
        setGerminatebaseId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PedigreedefinitionsRecord value3(Integer value) {
        setPedigreenotationId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PedigreedefinitionsRecord value4(Integer value) {
        setPedigreedescriptionId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PedigreedefinitionsRecord value5(String value) {
        setDefinition(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PedigreedefinitionsRecord value6(Timestamp value) {
        setCreatedOn(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PedigreedefinitionsRecord value7(Timestamp value) {
        setUpdatedOn(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PedigreedefinitionsRecord values(Integer value1, Integer value2, Integer value3, Integer value4, String value5, Timestamp value6, Timestamp value7) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Getter for <code>germinate_template_3_7_0.pedigreedefinitions.updated_on</code>. When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.
     */
    public Timestamp getUpdatedOn() {
        return (Timestamp) get(6);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.pedigreedefinitions.updated_on</code>. When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.
     */
    public void setUpdatedOn(Timestamp value) {
        set(6, value);
    }
// @formatter:on
}
