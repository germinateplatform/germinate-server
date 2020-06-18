/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables.records;


import java.sql.Timestamp;

import javax.annotation.Generated;

import jhi.germinate.server.database.tables.ViewTableFileresources;

import org.jooq.Field;
import org.jooq.Record10;
import org.jooq.Row10;
import org.jooq.impl.TableRecordImpl;


// @formatter:off
/**
 * VIEW
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ViewTableFileresourcesRecord extends TableRecordImpl<ViewTableFileresourcesRecord> implements Record10<Integer, String, String, String, Long, Timestamp, Timestamp, Integer, String, String> {

    private static final long serialVersionUID = 823962289;

    /**
     * Setter for <code>germinate_template_4_20_06_15.view_table_fileresources.fileresource_id</code>. The primary id.
     */
    public void setFileresourceId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>germinate_template_4_20_06_15.view_table_fileresources.fileresource_id</code>. The primary id.
     */
    public Integer getFileresourceId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>germinate_template_4_20_06_15.view_table_fileresources.fileresource_name</code>. The name of the file resource.
     */
    public void setFileresourceName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>germinate_template_4_20_06_15.view_table_fileresources.fileresource_name</code>. The name of the file resource.
     */
    public String getFileresourceName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>germinate_template_4_20_06_15.view_table_fileresources.fileresource_path</code>. The file name of the actual data file.
     */
    public void setFileresourcePath(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>germinate_template_4_20_06_15.view_table_fileresources.fileresource_path</code>. The file name of the actual data file.
     */
    public String getFileresourcePath() {
        return (String) get(2);
    }

    /**
     * Setter for <code>germinate_template_4_20_06_15.view_table_fileresources.fileresource_description</code>. A description of the file contents.
     */
    public void setFileresourceDescription(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>germinate_template_4_20_06_15.view_table_fileresources.fileresource_description</code>. A description of the file contents.
     */
    public String getFileresourceDescription() {
        return (String) get(3);
    }

    /**
     * Setter for <code>germinate_template_4_20_06_15.view_table_fileresources.fileresource_size</code>. The file size in bytes.
     */
    public void setFileresourceSize(Long value) {
        set(4, value);
    }

    /**
     * Getter for <code>germinate_template_4_20_06_15.view_table_fileresources.fileresource_size</code>. The file size in bytes.
     */
    public Long getFileresourceSize() {
        return (Long) get(4);
    }

    /**
     * Setter for <code>germinate_template_4_20_06_15.view_table_fileresources.fileresource_created_on</code>. When this record was created.
     */
    public void setFileresourceCreatedOn(Timestamp value) {
        set(5, value);
    }

    /**
     * Getter for <code>germinate_template_4_20_06_15.view_table_fileresources.fileresource_created_on</code>. When this record was created.
     */
    public Timestamp getFileresourceCreatedOn() {
        return (Timestamp) get(5);
    }

    /**
     * Setter for <code>germinate_template_4_20_06_15.view_table_fileresources.fileresource_updated_on</code>. When this record was last updated.
     */
    public void setFileresourceUpdatedOn(Timestamp value) {
        set(6, value);
    }

    /**
     * Getter for <code>germinate_template_4_20_06_15.view_table_fileresources.fileresource_updated_on</code>. When this record was last updated.
     */
    public Timestamp getFileresourceUpdatedOn() {
        return (Timestamp) get(6);
    }

    /**
     * Setter for <code>germinate_template_4_20_06_15.view_table_fileresources.fileresourcetype_id</code>. The primary id.
     */
    public void setFileresourcetypeId(Integer value) {
        set(7, value);
    }

    /**
     * Getter for <code>germinate_template_4_20_06_15.view_table_fileresources.fileresourcetype_id</code>. The primary id.
     */
    public Integer getFileresourcetypeId() {
        return (Integer) get(7);
    }

    /**
     * Setter for <code>germinate_template_4_20_06_15.view_table_fileresources.fileresourcetype_name</code>. The name of the file type.
     */
    public void setFileresourcetypeName(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>germinate_template_4_20_06_15.view_table_fileresources.fileresourcetype_name</code>. The name of the file type.
     */
    public String getFileresourcetypeName() {
        return (String) get(8);
    }

    /**
     * Setter for <code>germinate_template_4_20_06_15.view_table_fileresources.fileresourcetype_description</code>. The description of the file type.
     */
    public void setFileresourcetypeDescription(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>germinate_template_4_20_06_15.view_table_fileresources.fileresourcetype_description</code>. The description of the file type.
     */
    public String getFileresourcetypeDescription() {
        return (String) get(9);
    }

    // -------------------------------------------------------------------------
    // Record10 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row10<Integer, String, String, String, Long, Timestamp, Timestamp, Integer, String, String> fieldsRow() {
        return (Row10) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row10<Integer, String, String, String, Long, Timestamp, Timestamp, Integer, String, String> valuesRow() {
        return (Row10) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return ViewTableFileresources.VIEW_TABLE_FILERESOURCES.FILERESOURCE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return ViewTableFileresources.VIEW_TABLE_FILERESOURCES.FILERESOURCE_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return ViewTableFileresources.VIEW_TABLE_FILERESOURCES.FILERESOURCE_PATH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return ViewTableFileresources.VIEW_TABLE_FILERESOURCES.FILERESOURCE_DESCRIPTION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field5() {
        return ViewTableFileresources.VIEW_TABLE_FILERESOURCES.FILERESOURCE_SIZE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field6() {
        return ViewTableFileresources.VIEW_TABLE_FILERESOURCES.FILERESOURCE_CREATED_ON;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field7() {
        return ViewTableFileresources.VIEW_TABLE_FILERESOURCES.FILERESOURCE_UPDATED_ON;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field8() {
        return ViewTableFileresources.VIEW_TABLE_FILERESOURCES.FILERESOURCETYPE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return ViewTableFileresources.VIEW_TABLE_FILERESOURCES.FILERESOURCETYPE_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field10() {
        return ViewTableFileresources.VIEW_TABLE_FILERESOURCES.FILERESOURCETYPE_DESCRIPTION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component1() {
        return getFileresourceId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getFileresourceName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getFileresourcePath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component4() {
        return getFileresourceDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component5() {
        return getFileresourceSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component6() {
        return getFileresourceCreatedOn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component7() {
        return getFileresourceUpdatedOn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component8() {
        return getFileresourcetypeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component9() {
        return getFileresourcetypeName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component10() {
        return getFileresourcetypeDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getFileresourceId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getFileresourceName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getFileresourcePath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getFileresourceDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value5() {
        return getFileresourceSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value6() {
        return getFileresourceCreatedOn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value7() {
        return getFileresourceUpdatedOn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value8() {
        return getFileresourcetypeId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value9() {
        return getFileresourcetypeName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value10() {
        return getFileresourcetypeDescription();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableFileresourcesRecord value1(Integer value) {
        setFileresourceId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableFileresourcesRecord value2(String value) {
        setFileresourceName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableFileresourcesRecord value3(String value) {
        setFileresourcePath(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableFileresourcesRecord value4(String value) {
        setFileresourceDescription(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableFileresourcesRecord value5(Long value) {
        setFileresourceSize(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableFileresourcesRecord value6(Timestamp value) {
        setFileresourceCreatedOn(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableFileresourcesRecord value7(Timestamp value) {
        setFileresourceUpdatedOn(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableFileresourcesRecord value8(Integer value) {
        setFileresourcetypeId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableFileresourcesRecord value9(String value) {
        setFileresourcetypeName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableFileresourcesRecord value10(String value) {
        setFileresourcetypeDescription(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableFileresourcesRecord values(Integer value1, String value2, String value3, String value4, Long value5, Timestamp value6, Timestamp value7, Integer value8, String value9, String value10) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ViewTableFileresourcesRecord
     */
    public ViewTableFileresourcesRecord() {
        super(ViewTableFileresources.VIEW_TABLE_FILERESOURCES);
    }

    /**
     * Create a detached, initialised ViewTableFileresourcesRecord
     */
    public ViewTableFileresourcesRecord(Integer fileresourceId, String fileresourceName, String fileresourcePath, String fileresourceDescription, Long fileresourceSize, Timestamp fileresourceCreatedOn, Timestamp fileresourceUpdatedOn, Integer fileresourcetypeId, String fileresourcetypeName, String fileresourcetypeDescription) {
        super(ViewTableFileresources.VIEW_TABLE_FILERESOURCES);

        set(0, fileresourceId);
        set(1, fileresourceName);
        set(2, fileresourcePath);
        set(3, fileresourceDescription);
        set(4, fileresourceSize);
        set(5, fileresourceCreatedOn);
        set(6, fileresourceUpdatedOn);
        set(7, fileresourcetypeId);
        set(8, fileresourcetypeName);
        set(9, fileresourcetypeDescription);
    }
// @formatter:on
}