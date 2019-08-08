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
 * Defines attributes data. Attributes which are defined in attributes can 
 * have values associated with them. Data which does not warrant new column 
 * in the germinatebase table can be added here. Examples include small amounts 
 * of data defining germplasm which only exists for a small sub-group of the 
 * total database.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AttributedataRecord extends UpdatableRecordImpl<AttributedataRecord> implements Record6<Integer, Integer, Integer, String, Timestamp, Timestamp> {

    private static final long serialVersionUID = 1904195261;

    /**
     * Create a detached AttributedataRecord
     */
    public AttributedataRecord() {
        super(Attributedata.ATTRIBUTEDATA);
    }

    /**
     * Create a detached, initialised AttributedataRecord
     */
    public AttributedataRecord(Integer id, Integer attributeId, Integer foreignId, String value, Timestamp createdOn, Timestamp updatedOn) {
        super(Attributedata.ATTRIBUTEDATA);

        set(0, id);
        set(1, attributeId);
        set(2, foreignId);
        set(3, value);
        set(4, createdOn);
        set(5, updatedOn);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.attributedata.id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.attributedata.id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.attributedata.attribute_id</code>. Foreign key to attributes (attributes.id).
     */
    public Integer getAttributeId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.attributedata.attribute_id</code>. Foreign key to attributes (attributes.id).
     */
    public void setAttributeId(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.attributedata.foreign_id</code>. Foreign key to germinatebase (germinatebase.id).
     */
    public Integer getForeignId() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.attributedata.foreign_id</code>. Foreign key to germinatebase (germinatebase.id).
     */
    public void setForeignId(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.attributedata.value</code>. The value of the attribute.
     */
    public String getValue() {
        return (String) get(3);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.attributedata.value</code>. The value of the attribute.
     */
    public void setValue(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.attributedata.created_on</code>. When the record was created.
     */
    public Timestamp getCreatedOn() {
        return (Timestamp) get(4);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.attributedata.created_on</code>. When the record was created.
     */
    public void setCreatedOn(Timestamp value) {
        set(4, value);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * Getter for <code>germinate_template_3_6_0.attributedata.updated_on</code>. When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.
     */
    public Timestamp getUpdatedOn() {
        return (Timestamp) get(5);
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    /**
     * Setter for <code>germinate_template_3_6_0.attributedata.updated_on</code>. When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.
     */
    public void setUpdatedOn(Timestamp value) {
        set(5, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row6<Integer, Integer, Integer, String, Timestamp, Timestamp> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row6<Integer, Integer, Integer, String, Timestamp, Timestamp> valuesRow() {
        return (Row6) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return Attributedata.ATTRIBUTEDATA.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field2() {
        return Attributedata.ATTRIBUTEDATA.ATTRIBUTE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field3() {
        return Attributedata.ATTRIBUTEDATA.FOREIGN_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return Attributedata.ATTRIBUTEDATA.VALUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field5() {
        return Attributedata.ATTRIBUTEDATA.CREATED_ON;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field6() {
        return Attributedata.ATTRIBUTEDATA.UPDATED_ON;
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
        return getAttributeId();
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
    public String component4() {
        return getValue();
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
        return getAttributeId();
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
    public String value4() {
        return getValue();
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
    public AttributedataRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AttributedataRecord value2(Integer value) {
        setAttributeId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AttributedataRecord value3(Integer value) {
        setForeignId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AttributedataRecord value4(String value) {
        setValue(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AttributedataRecord value5(Timestamp value) {
        setCreatedOn(value);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public AttributedataRecord value6(Timestamp value) {
        setUpdatedOn(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AttributedataRecord values(Integer value1, Integer value2, Integer value3, String value4, Timestamp value5, Timestamp value6) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        return this;
    }
}
