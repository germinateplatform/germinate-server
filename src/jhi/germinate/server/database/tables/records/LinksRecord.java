/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables.records;


import java.sql.Timestamp;

import javax.annotation.Generated;

import jhi.germinate.server.database.tables.Links;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record8;
import org.jooq.Row8;
import org.jooq.impl.UpdatableRecordImpl;


// @formatter:off
/**
 * Germinate allows to define external links for different types of data. 
 * With this feature you can
 * define links to external resources.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class LinksRecord extends UpdatableRecordImpl<LinksRecord> implements Record8<Integer, Integer, Integer, String, String, Byte, Timestamp, Timestamp> {

    private static final long serialVersionUID = -871998449;

    /**
     * Create a detached LinksRecord
     */
    public LinksRecord() {
        super(Links.LINKS);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.links.id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Create a detached, initialised LinksRecord
     */
    public LinksRecord(Integer id, Integer linktypeId, Integer foreignId, String hyperlink, String description, Byte visibility, Timestamp createdOn, Timestamp updatedOn) {
        super(Links.LINKS);

        set(0, id);
        set(1, linktypeId);
        set(2, foreignId);
        set(3, hyperlink);
        set(4, description);
        set(5, visibility);
        set(6, createdOn);
        set(7, updatedOn);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.links.linktype_id</code>. Foreign key to linktypes (linktypes.id).
     */
    public Integer getLinktypeId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.links.foreign_id</code>.
     */
    public void setForeignId(Integer value) {
        set(2, value);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.links.id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.links.hyperlink</code>. The actual hyperlink.
     */
    public void setHyperlink(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.links.hyperlink</code>. The actual hyperlink.
     */
    public String getHyperlink() {
        return (String) get(3);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.links.linktype_id</code>. Foreign key to linktypes (linktypes.id).
     */
    public void setLinktypeId(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.links.description</code>. A description of the link.
     */
    public String getDescription() {
        return (String) get(4);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.links.visibility</code>. Determines if the link is visible or not: {0, 1}
     */
    public void setVisibility(Byte value) {
        set(5, value);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.links.foreign_id</code>.
     */
    public Integer getForeignId() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.links.created_on</code>. When the record was created.
     */
    public void setCreatedOn(Timestamp value) {
        set(6, value);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.links.created_on</code>. When the record was created.
     */
    public Timestamp getCreatedOn() {
        return (Timestamp) get(6);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.links.description</code>. A description of the link.
     */
    public void setDescription(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.links.visibility</code>. Determines if the link is visible or not: {0, 1}
     */
    public Byte getVisibility() {
        return (Byte) get(5);
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
    public Row8<Integer, Integer, Integer, String, String, Byte, Timestamp, Timestamp> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row8<Integer, Integer, Integer, String, String, Byte, Timestamp, Timestamp> valuesRow() {
        return (Row8) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return Links.LINKS.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field2() {
        return Links.LINKS.LINKTYPE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field3() {
        return Links.LINKS.FOREIGN_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return Links.LINKS.HYPERLINK;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return Links.LINKS.DESCRIPTION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Byte> field6() {
        return Links.LINKS.VISIBILITY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field7() {
        return Links.LINKS.CREATED_ON;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field8() {
        return Links.LINKS.UPDATED_ON;
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
        return getLinktypeId();
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
        return getHyperlink();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component5() {
        return getDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte component6() {
        return getVisibility();
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
    public Integer value2() {
        return getLinktypeId();
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
        return getHyperlink();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Byte value6() {
        return getVisibility();
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
    public LinksRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LinksRecord value2(Integer value) {
        setLinktypeId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LinksRecord value3(Integer value) {
        setForeignId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LinksRecord value4(String value) {
        setHyperlink(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LinksRecord value5(String value) {
        setDescription(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LinksRecord value6(Byte value) {
        setVisibility(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LinksRecord value7(Timestamp value) {
        setCreatedOn(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LinksRecord value8(Timestamp value) {
        setUpdatedOn(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LinksRecord values(Integer value1, Integer value2, Integer value3, String value4, String value5, Byte value6, Timestamp value7, Timestamp value8) {
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
     * Getter for <code>germinate_template_3_7_0.links.updated_on</code>. When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.
     */
    public Timestamp getUpdatedOn() {
        return (Timestamp) get(7);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.links.updated_on</code>. When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.
     */
    public void setUpdatedOn(Timestamp value) {
        set(7, value);
    }
// @formatter:on
}
