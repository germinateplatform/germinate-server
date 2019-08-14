/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables.pojos;


import org.jooq.types.*;

import java.io.*;
import java.sql.*;

import javax.annotation.*;


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
public class ViewTableDatasets implements Serializable {

    private static final long serialVersionUID = 1321537355;

    private Integer datasetid;
    private String  datasetname;
    private String  datesetdescription;
    private String  experimenttype;
    private String  experimentname;
    private String  datatype;
    private String  datasetstate;
    private String  location;
    private String  countrycode;
    private String  countryname;
    private Integer licenseid;
    private String  licensename;
    private String  contact;
    private Date    startdate;
    private Date    enddate;
    private ULong   dataobjectcount;
    private ULong   datapointcount;
    private Byte    isexternal;
    private String  acceptedby;

    public ViewTableDatasets() {}

    public ViewTableDatasets(ViewTableDatasets value) {
        this.datasetid = value.datasetid;
        this.datasetname = value.datasetname;
        this.datesetdescription = value.datesetdescription;
        this.experimenttype = value.experimenttype;
        this.experimentname = value.experimentname;
        this.datatype = value.datatype;
        this.datasetstate = value.datasetstate;
        this.location = value.location;
        this.countrycode = value.countrycode;
        this.countryname = value.countryname;
        this.licenseid = value.licenseid;
        this.licensename = value.licensename;
        this.contact = value.contact;
        this.startdate = value.startdate;
        this.enddate = value.enddate;
        this.dataobjectcount = value.dataobjectcount;
        this.datapointcount = value.datapointcount;
        this.isexternal = value.isexternal;
        this.acceptedby = value.acceptedby;
    }

    public ViewTableDatasets(
        Integer datasetid,
        String  datasetname,
        String  datesetdescription,
        String  experimenttype,
        String  experimentname,
        String  datatype,
        String  datasetstate,
        String  location,
        String  countrycode,
        String  countryname,
        Integer licenseid,
        String  licensename,
        String  contact,
        Date    startdate,
        Date    enddate,
        ULong   dataobjectcount,
        ULong   datapointcount,
        Byte    isexternal,
        String  acceptedby
    ) {
        this.datasetid = datasetid;
        this.datasetname = datasetname;
        this.datesetdescription = datesetdescription;
        this.experimenttype = experimenttype;
        this.experimentname = experimentname;
        this.datatype = datatype;
        this.datasetstate = datasetstate;
        this.location = location;
        this.countrycode = countrycode;
        this.countryname = countryname;
        this.licenseid = licenseid;
        this.licensename = licensename;
        this.contact = contact;
        this.startdate = startdate;
        this.enddate = enddate;
        this.dataobjectcount = dataobjectcount;
        this.datapointcount = datapointcount;
        this.isexternal = isexternal;
        this.acceptedby = acceptedby;
    }

    public Integer getDatasetid() {
        return this.datasetid;
    }

    public void setDatasetid(Integer datasetid) {
        this.datasetid = datasetid;
    }

    public String getDatasetname() {
        return this.datasetname;
    }

    public void setDatasetname(String datasetname) {
        this.datasetname = datasetname;
    }

    public String getDatesetdescription() {
        return this.datesetdescription;
    }

    public void setDatesetdescription(String datesetdescription) {
        this.datesetdescription = datesetdescription;
    }

    public String getExperimenttype() {
        return this.experimenttype;
    }

    public void setExperimenttype(String experimenttype) {
        this.experimenttype = experimenttype;
    }

    public String getExperimentname() {
        return this.experimentname;
    }

    public void setExperimentname(String experimentname) {
        this.experimentname = experimentname;
    }

    public String getDatatype() {
        return this.datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getDatasetstate() {
        return this.datasetstate;
    }

    public void setDatasetstate(String datasetstate) {
        this.datasetstate = datasetstate;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCountrycode() {
        return this.countrycode;
    }

    public void setCountrycode(String countrycode) {
        this.countrycode = countrycode;
    }

    public String getCountryname() {
        return this.countryname;
    }

    public void setCountryname(String countryname) {
        this.countryname = countryname;
    }

    public Integer getLicenseid() {
        return this.licenseid;
    }

    public void setLicenseid(Integer licenseid) {
        this.licenseid = licenseid;
    }

    public String getLicensename() {
        return this.licensename;
    }

    public void setLicensename(String licensename) {
        this.licensename = licensename;
    }

    public String getContact() {
        return this.contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Date getStartdate() {
        return this.startdate;
    }

    public void setStartdate(Date startdate) {
        this.startdate = startdate;
    }

    public Date getEnddate() {
        return this.enddate;
    }

    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }

    public ULong getDataobjectcount() {
        return this.dataobjectcount;
    }

    public void setDataobjectcount(ULong dataobjectcount) {
        this.dataobjectcount = dataobjectcount;
    }

    public ULong getDatapointcount() {
        return this.datapointcount;
    }

    public void setDatapointcount(ULong datapointcount) {
        this.datapointcount = datapointcount;
    }

    public Byte getIsexternal() {
        return this.isexternal;
    }

    public void setIsexternal(Byte isexternal) {
        this.isexternal = isexternal;
    }

    public String getAcceptedby() {
        return this.acceptedby;
    }

    public void setAcceptedby(String acceptedby) {
        this.acceptedby = acceptedby;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ViewTableDatasets (");

        sb.append(datasetid);
        sb.append(", ").append(datasetname);
        sb.append(", ").append(datesetdescription);
        sb.append(", ").append(experimenttype);
        sb.append(", ").append(experimentname);
        sb.append(", ").append(datatype);
        sb.append(", ").append(datasetstate);
        sb.append(", ").append(location);
        sb.append(", ").append(countrycode);
        sb.append(", ").append(countryname);
        sb.append(", ").append(licenseid);
        sb.append(", ").append(licensename);
        sb.append(", ").append(contact);
        sb.append(", ").append(startdate);
        sb.append(", ").append(enddate);
        sb.append(", ").append(dataobjectcount);
        sb.append(", ").append(datapointcount);
        sb.append(", ").append(isexternal);
        sb.append(", ").append(acceptedby);

        sb.append(")");
        return sb.toString();
    }
}
