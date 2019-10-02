/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables.records;


import javax.annotation.Generated;

import jhi.germinate.server.database.tables.ViewTableEntities;

import org.jooq.Field;
import org.jooq.Record8;
import org.jooq.Row8;
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
public class ViewTableEntitiesRecord extends TableRecordImpl<ViewTableEntitiesRecord> implements Record8<Integer, String, String, String, Integer, String, String, String> {

    private static final long serialVersionUID = -1933263080;

    /**
     * Setter for <code>germinate_template_3_7_0.view_table_entities.entity_parent_id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public void setEntityParentId(Integer value) {
        set(0, value);
    }

    /**
     * Create a detached ViewTableEntitiesRecord
     */
    public ViewTableEntitiesRecord() {
        super(ViewTableEntities.VIEW_TABLE_ENTITIES);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.view_table_entities.entity_parent_gid</code>. A unique identifier.
     */
    public void setEntityParentGid(String value) {
        set(1, value);
    }

    /**
     * Create a detached, initialised ViewTableEntitiesRecord
     */
    public ViewTableEntitiesRecord(Integer entityParentId, String entityParentGid, String entityParentName, String entityParentType, Integer entityChildId, String entityChildGid, String entityChildName, String entityChildType) {
        super(ViewTableEntities.VIEW_TABLE_ENTITIES);

        set(0, entityParentId);
        set(1, entityParentGid);
        set(2, entityParentName);
        set(3, entityParentType);
        set(4, entityChildId);
        set(5, entityChildGid);
        set(6, entityChildName);
        set(7, entityChildType);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.view_table_entities.entity_parent_name</code>. A unique name which defines an entry in the germinatbase table.
     */
    public void setEntityParentName(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.view_table_entities.entity_parent_id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public Integer getEntityParentId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.view_table_entities.entity_parent_type</code>. The name of the entity type.
     */
    public void setEntityParentType(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.view_table_entities.entity_parent_gid</code>. A unique identifier.
     */
    public String getEntityParentGid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.view_table_entities.entity_child_id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public void setEntityChildId(Integer value) {
        set(4, value);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.view_table_entities.entity_parent_name</code>. A unique name which defines an entry in the germinatbase table.
     */
    public String getEntityParentName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.view_table_entities.entity_child_gid</code>. A unique identifier.
     */
    public void setEntityChildGid(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.view_table_entities.entity_parent_type</code>. The name of the entity type.
     */
    public String getEntityParentType() {
        return (String) get(3);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.view_table_entities.entity_child_name</code>. A unique name which defines an entry in the germinatbase table.
     */
    public void setEntityChildName(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.view_table_entities.entity_child_id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public Integer getEntityChildId() {
        return (Integer) get(4);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.view_table_entities.entity_child_gid</code>. A unique identifier.
     */
    public String getEntityChildGid() {
        return (String) get(5);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.view_table_entities.entity_child_type</code>. The name of the entity type.
     */
    public String getEntityChildType() {
        return (String) get(7);
    }

    // -------------------------------------------------------------------------
    // Record8 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row8<Integer, String, String, String, Integer, String, String, String> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row8<Integer, String, String, String, Integer, String, String, String> valuesRow() {
        return (Row8) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return ViewTableEntities.VIEW_TABLE_ENTITIES.ENTITY_PARENT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return ViewTableEntities.VIEW_TABLE_ENTITIES.ENTITY_PARENT_GID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return ViewTableEntities.VIEW_TABLE_ENTITIES.ENTITY_PARENT_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return ViewTableEntities.VIEW_TABLE_ENTITIES.ENTITY_PARENT_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field5() {
        return ViewTableEntities.VIEW_TABLE_ENTITIES.ENTITY_CHILD_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return ViewTableEntities.VIEW_TABLE_ENTITIES.ENTITY_CHILD_GID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field7() {
        return ViewTableEntities.VIEW_TABLE_ENTITIES.ENTITY_CHILD_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return ViewTableEntities.VIEW_TABLE_ENTITIES.ENTITY_CHILD_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component1() {
        return getEntityParentId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getEntityParentGid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getEntityParentName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component4() {
        return getEntityParentType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component5() {
        return getEntityChildId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component6() {
        return getEntityChildGid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component7() {
        return getEntityChildName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component8() {
        return getEntityChildType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getEntityParentId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getEntityParentGid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getEntityParentName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getEntityParentType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value5() {
        return getEntityChildId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getEntityChildGid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value7() {
        return getEntityChildName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value8() {
        return getEntityChildType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableEntitiesRecord value1(Integer value) {
        setEntityParentId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableEntitiesRecord value2(String value) {
        setEntityParentGid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableEntitiesRecord value3(String value) {
        setEntityParentName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableEntitiesRecord value4(String value) {
        setEntityParentType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableEntitiesRecord value5(Integer value) {
        setEntityChildId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableEntitiesRecord value6(String value) {
        setEntityChildGid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableEntitiesRecord value7(String value) {
        setEntityChildName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableEntitiesRecord value8(String value) {
        setEntityChildType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableEntitiesRecord values(Integer value1, String value2, String value3, String value4, Integer value5, String value6, String value7, String value8) {
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
     * Getter for <code>germinate_template_3_7_0.view_table_entities.entity_child_name</code>. A unique name which defines an entry in the germinatbase table.
     */
    public String getEntityChildName() {
        return (String) get(6);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.view_table_entities.entity_child_type</code>. The name of the entity type.
     */
    public void setEntityChildType(String value) {
        set(7, value);
    }
// @formatter:on
}
