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
 * Based on Multi Crop Passport Descriptors (MCPD V2 2012) - The coding scheme 
 * proposed can be used at 3 different levels of detail: either by using the
 * general codes (in boldface) such as 100, 200, 300, 400, or by using the 
 * more specific codes
 * such as 110, 120, etc.
 * 100) Wild
 * 110) Natural
 * 120) Semi-natural/wild
 * 130) Semi-natural/sown
 * 200) Weedy
 * 300) Traditional cultivar/landrace
 * 400) Breeding/research material
 *  410) Breeder's line
 *  411) Synthetic population
 *  412) Hybrid
 *  413) Founder stock/base population
 *  414) Inbred line (parent of hybrid cultivar)
 *  415) Segregating population
 *  416) Clonal selection
 *  420) Genetic stock
 *  421) Mutant (e.g. induced/insertion mutants, tilling populations)
 *  422) Cytogenetic stocks (e.g. chromosome addition/substitution, aneuploids,
 * amphiploids)
 *  423) Other genetic stocks (e.g. mapping populations)
 * 500) Advanced or improved cultivar (conventional breeding methods)
 * 600) GMO (by genetic engineering)
 *  999) Other 
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class BiologicalstatusRecord extends UpdatableRecordImpl<BiologicalstatusRecord> implements Record4<Integer, String, Timestamp, Timestamp> {

    private static final long serialVersionUID = -1858115278;

    /**
     * Create a detached BiologicalstatusRecord
     */
    public BiologicalstatusRecord() {
        super(Biologicalstatus.BIOLOGICALSTATUS);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.biologicalstatus.id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.biologicalstatus.sampstat</code>. Previoulsy known as sampstat.
     */
    public void setSampstat(String value) {
        set(1, value);
    }

    /**
     * Create a detached, initialised BiologicalstatusRecord
     */
    public BiologicalstatusRecord(Integer id, String sampstat, Timestamp createdOn, Timestamp updatedOn) {
        super(Biologicalstatus.BIOLOGICALSTATUS);

        set(0, id);
        set(1, sampstat);
        set(2, createdOn);
        set(3, updatedOn);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.biologicalstatus.created_on</code>. When the record was created.
     */
    public void setCreatedOn(Timestamp value) {
        set(2, value);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.biologicalstatus.created_on</code>. When the record was created.
     */
    public Timestamp getCreatedOn() {
        return (Timestamp) get(2);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.biologicalstatus.id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.biologicalstatus.sampstat</code>. Previoulsy known as sampstat.
     */
    public String getSampstat() {
        return (String) get(1);
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
    // Record4 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row4<Integer, String, Timestamp, Timestamp> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row4<Integer, String, Timestamp, Timestamp> valuesRow() {
        return (Row4) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return Biologicalstatus.BIOLOGICALSTATUS.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return Biologicalstatus.BIOLOGICALSTATUS.SAMPSTAT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field3() {
        return Biologicalstatus.BIOLOGICALSTATUS.CREATED_ON;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field4() {
        return Biologicalstatus.BIOLOGICALSTATUS.UPDATED_ON;
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
        return getSampstat();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component3() {
        return getCreatedOn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component4() {
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
        return getSampstat();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value3() {
        return getCreatedOn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value4() {
        return getUpdatedOn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BiologicalstatusRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BiologicalstatusRecord value2(String value) {
        setSampstat(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BiologicalstatusRecord value3(Timestamp value) {
        setCreatedOn(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BiologicalstatusRecord value4(Timestamp value) {
        setUpdatedOn(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BiologicalstatusRecord values(Integer value1, String value2, Timestamp value3, Timestamp value4) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Getter for <code>germinate_template_3_7_0.biologicalstatus.updated_on</code>. When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.
     */
    public Timestamp getUpdatedOn() {
        return (Timestamp) get(3);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.biologicalstatus.updated_on</code>. When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.
     */
    public void setUpdatedOn(Timestamp value) {
        set(3, value);
    }
}
