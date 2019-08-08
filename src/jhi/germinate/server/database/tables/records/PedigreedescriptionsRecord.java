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
 * Description of pedigrees. Pedigrees can have a description which details 
 * additional information about the pedigree, how it was constructed and who 
 * the contact is for the pedigree.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class PedigreedescriptionsRecord extends UpdatableRecordImpl<PedigreedescriptionsRecord> implements Record6<Integer, String, String, String, Timestamp, Timestamp> {

    private static final long serialVersionUID = 329447828;

    /**
     * Create a detached PedigreedescriptionsRecord
     */
    public PedigreedescriptionsRecord() {
        super(Pedigreedescriptions.PEDIGREEDESCRIPTIONS);
    }

    /**
     * Create a detached, initialised PedigreedescriptionsRecord
     */
    public PedigreedescriptionsRecord(Integer id, String name, String description, String author, Timestamp createdOn, Timestamp updatedOn) {
        super(Pedigreedescriptions.PEDIGREEDESCRIPTIONS);

        set(0, id);
        set(1, name);
        set(2, description);
        set(3, author);
        set(4, createdOn);
        set(5, updatedOn);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.pedigreedescriptions.id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.pedigreedescriptions.id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.pedigreedescriptions.name</code>. The name of the pedigree.
     */
    public String getName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.pedigreedescriptions.name</code>. The name of the pedigree.
     */
    public void setName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.pedigreedescriptions.description</code>. Describes the pedigree in more detail.
     */
    public String getDescription() {
        return (String) get(2);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.pedigreedescriptions.description</code>. Describes the pedigree in more detail.
     */
    public void setDescription(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.pedigreedescriptions.author</code>. Who is responsible for the creation of the pedigree. Attribution should be included in here for pedigree sources.
     */
    public String getAuthor() {
        return (String) get(3);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.pedigreedescriptions.author</code>. Who is responsible for the creation of the pedigree. Attribution should be included in here for pedigree sources.
     */
    public void setAuthor(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.pedigreedescriptions.created_on</code>. When the record was created.
     */
    public Timestamp getCreatedOn() {
        return (Timestamp) get(4);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.pedigreedescriptions.created_on</code>. When the record was created.
     */
    public void setCreatedOn(Timestamp value) {
        set(4, value);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * Getter for <code>germinate_template_3_6_0.pedigreedescriptions.updated_on</code>. When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.
     */
    public Timestamp getUpdatedOn() {
        return (Timestamp) get(5);
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    /**
     * Setter for <code>germinate_template_3_6_0.pedigreedescriptions.updated_on</code>. When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.
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
    public Row6<Integer, String, String, String, Timestamp, Timestamp> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row6<Integer, String, String, String, Timestamp, Timestamp> valuesRow() {
        return (Row6) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return Pedigreedescriptions.PEDIGREEDESCRIPTIONS.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return Pedigreedescriptions.PEDIGREEDESCRIPTIONS.NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return Pedigreedescriptions.PEDIGREEDESCRIPTIONS.DESCRIPTION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return Pedigreedescriptions.PEDIGREEDESCRIPTIONS.AUTHOR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field5() {
        return Pedigreedescriptions.PEDIGREEDESCRIPTIONS.CREATED_ON;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field6() {
        return Pedigreedescriptions.PEDIGREEDESCRIPTIONS.UPDATED_ON;
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
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component4() {
        return getAuthor();
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
    public String value2() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getAuthor();
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
    public PedigreedescriptionsRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PedigreedescriptionsRecord value2(String value) {
        setName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PedigreedescriptionsRecord value3(String value) {
        setDescription(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PedigreedescriptionsRecord value4(String value) {
        setAuthor(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PedigreedescriptionsRecord value5(Timestamp value) {
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
    public PedigreedescriptionsRecord value6(Timestamp value) {
        setUpdatedOn(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PedigreedescriptionsRecord values(Integer value1, String value2, String value3, String value4, Timestamp value5, Timestamp value6) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        return this;
    }
}
