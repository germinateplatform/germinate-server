/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables.pojos;


import java.io.*;
import java.sql.*;

import javax.annotation.*;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Mlsstatus implements Serializable {

    private static final long serialVersionUID = -1015756940;

    private Integer   id;
    private String    description;
    private Timestamp createdOn;
    private Timestamp updatedOn;

    public Mlsstatus() {}

    public Mlsstatus(Mlsstatus value) {
        this.id = value.id;
        this.description = value.description;
        this.createdOn = value.createdOn;
        this.updatedOn = value.updatedOn;
    }

    public Mlsstatus(
        Integer   id,
        String    description,
        Timestamp createdOn,
        Timestamp updatedOn
    ) {
        this.id = id;
        this.description = description;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        StringBuilder sb = new StringBuilder("Mlsstatus (");

        sb.append(id);
        sb.append(", ").append(description);
        sb.append(", ").append(createdOn);
        sb.append(", ").append(updatedOn);

        sb.append(")");
        return sb.toString();
    }
}
