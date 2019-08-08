/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables.pojos;


import java.io.*;
import java.sql.*;

import javax.annotation.*;

import jhi.germinate.server.database.enums.*;


/**
 * Defines climates. Climates are measureable weather type characteristics 
 * such as temperature or cloud cover.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Climates implements Serializable {

    private static final long serialVersionUID = 1580649967;

    private Integer          id;
    private String           name;
    private String           shortName;
    private String           description;
    private ClimatesDatatype datatype;
    private Integer          unitId;
    private Timestamp        createdOn;
    private Timestamp        updatedOn;

    public Climates() {}

    public Climates(Climates value) {
        this.id = value.id;
        this.name = value.name;
        this.shortName = value.shortName;
        this.description = value.description;
        this.datatype = value.datatype;
        this.unitId = value.unitId;
        this.createdOn = value.createdOn;
        this.updatedOn = value.updatedOn;
    }

    public Climates(
        Integer          id,
        String           name,
        String           shortName,
        String           description,
        ClimatesDatatype datatype,
        Integer          unitId,
        Timestamp        createdOn,
        Timestamp        updatedOn
    ) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.description = description;
        this.datatype = datatype;
        this.unitId = unitId;
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

    public String getShortName() {
        return this.shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ClimatesDatatype getDatatype() {
        return this.datatype;
    }

    public void setDatatype(ClimatesDatatype datatype) {
        this.datatype = datatype;
    }

    public Integer getUnitId() {
        return this.unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
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
        StringBuilder sb = new StringBuilder("Climates (");

        sb.append(id);
        sb.append(", ").append(name);
        sb.append(", ").append(shortName);
        sb.append(", ").append(description);
        sb.append(", ").append(datatype);
        sb.append(", ").append(unitId);
        sb.append(", ").append(createdOn);
        sb.append(", ").append(updatedOn);

        sb.append(")");
        return sb.toString();
    }
}
