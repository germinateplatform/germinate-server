/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables.pojos;


import java.io.*;
import java.sql.*;

import javax.annotation.*;


/**
 * Allows additional supporting data to be associated with a pedigree definition 
 * such as the contributing data source.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Pedigreenotations implements Serializable {

    private static final long serialVersionUID = -1535558270;

    private Integer   id;
    private String    name;
    private String    description;
    private String    referenceUrl;
    private Timestamp createdOn;
    private Timestamp updatedOn;

    public Pedigreenotations() {}

    public Pedigreenotations(Pedigreenotations value) {
        this.id = value.id;
        this.name = value.name;
        this.description = value.description;
        this.referenceUrl = value.referenceUrl;
        this.createdOn = value.createdOn;
        this.updatedOn = value.updatedOn;
    }

    public Pedigreenotations(
        Integer   id,
        String    name,
        String    description,
        String    referenceUrl,
        Timestamp createdOn,
        Timestamp updatedOn
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.referenceUrl = referenceUrl;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReferenceUrl() {
        return this.referenceUrl;
    }

    public void setReferenceUrl(String referenceUrl) {
        this.referenceUrl = referenceUrl;
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
        StringBuilder sb = new StringBuilder("Pedigreenotations (");

        sb.append(id);
        sb.append(", ").append(name);
        sb.append(", ").append(description);
        sb.append(", ").append(referenceUrl);
        sb.append(", ").append(createdOn);
        sb.append(", ").append(updatedOn);

        sb.append(")");
        return sb.toString();
    }
}
