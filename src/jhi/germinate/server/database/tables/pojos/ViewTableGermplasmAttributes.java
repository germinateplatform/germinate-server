/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;

import jhi.germinate.server.database.enums.ViewTableGermplasmAttributesAttributeType;


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
public class ViewTableGermplasmAttributes implements Serializable {

    private static final long serialVersionUID = -325944854;

    private Integer                                   germplasmId;
    private String                                    germplasmGid;
    private String                                    germplasmName;
    private Integer                                   attributeId;
    private String                                    attributeName;
    private String                                    attributeDescription;
    private ViewTableGermplasmAttributesAttributeType attributeType;
    private String                                    targetTable;
    private Integer                                   foreignId;
    private String                                    attributeValue;

    public ViewTableGermplasmAttributes() {}

    public ViewTableGermplasmAttributes(ViewTableGermplasmAttributes value) {
        this.germplasmId = value.germplasmId;
        this.germplasmGid = value.germplasmGid;
        this.germplasmName = value.germplasmName;
        this.attributeId = value.attributeId;
        this.attributeName = value.attributeName;
        this.attributeDescription = value.attributeDescription;
        this.attributeType = value.attributeType;
        this.targetTable = value.targetTable;
        this.foreignId = value.foreignId;
        this.attributeValue = value.attributeValue;
    }

    public ViewTableGermplasmAttributes(
        Integer                                   germplasmId,
        String                                    germplasmGid,
        String                                    germplasmName,
        Integer                                   attributeId,
        String                                    attributeName,
        String                                    attributeDescription,
        ViewTableGermplasmAttributesAttributeType attributeType,
        String                                    targetTable,
        Integer                                   foreignId,
        String                                    attributeValue
    ) {
        this.germplasmId = germplasmId;
        this.germplasmGid = germplasmGid;
        this.germplasmName = germplasmName;
        this.attributeId = attributeId;
        this.attributeName = attributeName;
        this.attributeDescription = attributeDescription;
        this.attributeType = attributeType;
        this.targetTable = targetTable;
        this.foreignId = foreignId;
        this.attributeValue = attributeValue;
    }

    public Integer getGermplasmId() {
        return this.germplasmId;
    }

    public void setGermplasmId(Integer germplasmId) {
        this.germplasmId = germplasmId;
    }

    public String getGermplasmGid() {
        return this.germplasmGid;
    }

    public void setGermplasmGid(String germplasmGid) {
        this.germplasmGid = germplasmGid;
    }

    public String getGermplasmName() {
        return this.germplasmName;
    }

    public void setGermplasmName(String germplasmName) {
        this.germplasmName = germplasmName;
    }

    public Integer getAttributeId() {
        return this.attributeId;
    }

    public void setAttributeId(Integer attributeId) {
        this.attributeId = attributeId;
    }

    public String getAttributeName() {
        return this.attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeDescription() {
        return this.attributeDescription;
    }

    public void setAttributeDescription(String attributeDescription) {
        this.attributeDescription = attributeDescription;
    }

    public ViewTableGermplasmAttributesAttributeType getAttributeType() {
        return this.attributeType;
    }

    public void setAttributeType(ViewTableGermplasmAttributesAttributeType attributeType) {
        this.attributeType = attributeType;
    }

    public String getTargetTable() {
        return this.targetTable;
    }

    public void setTargetTable(String targetTable) {
        this.targetTable = targetTable;
    }

    public Integer getForeignId() {
        return this.foreignId;
    }

    public void setForeignId(Integer foreignId) {
        this.foreignId = foreignId;
    }

    public String getAttributeValue() {
        return this.attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ViewTableGermplasmAttributes (");

        sb.append(germplasmId);
        sb.append(", ").append(germplasmGid);
        sb.append(", ").append(germplasmName);
        sb.append(", ").append(attributeId);
        sb.append(", ").append(attributeName);
        sb.append(", ").append(attributeDescription);
        sb.append(", ").append(attributeType);
        sb.append(", ").append(targetTable);
        sb.append(", ").append(foreignId);
        sb.append(", ").append(attributeValue);

        sb.append(")");
        return sb.toString();
    }
// @formatter:on
}
