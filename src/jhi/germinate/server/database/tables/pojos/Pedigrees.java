/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables.pojos;


import java.io.*;
import java.sql.*;

import javax.annotation.*;

import jhi.germinate.server.database.enums.*;


/**
 * Holds pedigree definitions. A pedigree is constructed from a series of 
 * individial-&gt;parent records. This gives a great deal of flexibility in 
 * how pedigree networks can be constructed. This table is required for operation 
 * with the Helium pedigree viewer.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Pedigrees implements Serializable {

    private static final long serialVersionUID = -1029545626;

    private Integer                   id;
    private Integer                   germinatebaseId;
    private Integer                   parentId;
    private PedigreesRelationshipType relationshipType;
    private Integer                   pedigreedescriptionId;
    private String                    relationshipDescription;
    private Timestamp                 createdOn;
    private Timestamp                 updatedOn;

    public Pedigrees() {}

    public Pedigrees(Pedigrees value) {
        this.id = value.id;
        this.germinatebaseId = value.germinatebaseId;
        this.parentId = value.parentId;
        this.relationshipType = value.relationshipType;
        this.pedigreedescriptionId = value.pedigreedescriptionId;
        this.relationshipDescription = value.relationshipDescription;
        this.createdOn = value.createdOn;
        this.updatedOn = value.updatedOn;
    }

    public Pedigrees(
        Integer                   id,
        Integer                   germinatebaseId,
        Integer                   parentId,
        PedigreesRelationshipType relationshipType,
        Integer                   pedigreedescriptionId,
        String                    relationshipDescription,
        Timestamp                 createdOn,
        Timestamp                 updatedOn
    ) {
        this.id = id;
        this.germinatebaseId = germinatebaseId;
        this.parentId = parentId;
        this.relationshipType = relationshipType;
        this.pedigreedescriptionId = pedigreedescriptionId;
        this.relationshipDescription = relationshipDescription;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGerminatebaseId() {
        return this.germinatebaseId;
    }

    public void setGerminatebaseId(Integer germinatebaseId) {
        this.germinatebaseId = germinatebaseId;
    }

    public Integer getParentId() {
        return this.parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public PedigreesRelationshipType getRelationshipType() {
        return this.relationshipType;
    }

    public void setRelationshipType(PedigreesRelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }

    public Integer getPedigreedescriptionId() {
        return this.pedigreedescriptionId;
    }

    public void setPedigreedescriptionId(Integer pedigreedescriptionId) {
        this.pedigreedescriptionId = pedigreedescriptionId;
    }

    public String getRelationshipDescription() {
        return this.relationshipDescription;
    }

    public void setRelationshipDescription(String relationshipDescription) {
        this.relationshipDescription = relationshipDescription;
    }

    public Timestamp getCreatedOn() {
        return this.createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public Timestamp getUpdatedOn() {
        return this.updatedOn;
    }

    public void setUpdatedOn(Timestamp updatedOn) {
        this.updatedOn = updatedOn;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Pedigrees (");

        sb.append(id);
        sb.append(", ").append(germinatebaseId);
        sb.append(", ").append(parentId);
        sb.append(", ").append(relationshipType);
        sb.append(", ").append(pedigreedescriptionId);
        sb.append(", ").append(relationshipDescription);
        sb.append(", ").append(createdOn);
        sb.append(", ").append(updatedOn);

        sb.append(")");
        return sb.toString();
    }
}
