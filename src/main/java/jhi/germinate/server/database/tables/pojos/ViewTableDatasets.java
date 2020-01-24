/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables.pojos;


import com.google.gson.JsonArray;

import java.io.Serializable;
import java.sql.Date;

import javax.annotation.Generated;

import jhi.germinate.resource.DublinCore;

import org.jooq.types.ULong;


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
public class ViewTableDatasets implements Serializable {

    private static final long serialVersionUID = -2063351927;

    private Integer    datasetId;
    private String     datasetName;
    private String     datasetDescription;
    private String     hyperlink;
    private String     sourceFile;
    private String     experimentType;
    private Integer    experimentId;
    private String     experimentName;
    private String     datatype;
    private String     datasetState;
    private JsonArray  locationIds;
    private JsonArray  countryCodes;
    private Integer    licenseId;
    private String     licenseName;
    private String     contact;
    private Date       startDate;
    private Date       endDate;
    private DublinCore dublinCore;
    private ULong      dataObjectCount;
    private ULong      dataPointCount;
    private Boolean    isExternal;
    private Long       collaborators;
    private Long       attributes;
    private JsonArray  acceptedBy;

    public ViewTableDatasets() {}

    public ViewTableDatasets(ViewTableDatasets value) {
        this.datasetId = value.datasetId;
        this.datasetName = value.datasetName;
        this.datasetDescription = value.datasetDescription;
        this.hyperlink = value.hyperlink;
        this.sourceFile = value.sourceFile;
        this.experimentType = value.experimentType;
        this.experimentId = value.experimentId;
        this.experimentName = value.experimentName;
        this.datatype = value.datatype;
        this.datasetState = value.datasetState;
        this.locationIds = value.locationIds;
        this.countryCodes = value.countryCodes;
        this.licenseId = value.licenseId;
        this.licenseName = value.licenseName;
        this.contact = value.contact;
        this.startDate = value.startDate;
        this.endDate = value.endDate;
        this.dublinCore = value.dublinCore;
        this.dataObjectCount = value.dataObjectCount;
        this.dataPointCount = value.dataPointCount;
        this.isExternal = value.isExternal;
        this.collaborators = value.collaborators;
        this.attributes = value.attributes;
        this.acceptedBy = value.acceptedBy;
    }

    public ViewTableDatasets(
        Integer    datasetId,
        String     datasetName,
        String     datasetDescription,
        String     hyperlink,
        String     sourceFile,
        String     experimentType,
        Integer    experimentId,
        String     experimentName,
        String     datatype,
        String     datasetState,
        JsonArray  locationIds,
        JsonArray  countryCodes,
        Integer    licenseId,
        String     licenseName,
        String     contact,
        Date       startDate,
        Date       endDate,
        DublinCore dublinCore,
        ULong      dataObjectCount,
        ULong      dataPointCount,
        Boolean    isExternal,
        Long       collaborators,
        Long       attributes,
        JsonArray  acceptedBy
    ) {
        this.datasetId = datasetId;
        this.datasetName = datasetName;
        this.datasetDescription = datasetDescription;
        this.hyperlink = hyperlink;
        this.sourceFile = sourceFile;
        this.experimentType = experimentType;
        this.experimentId = experimentId;
        this.experimentName = experimentName;
        this.datatype = datatype;
        this.datasetState = datasetState;
        this.locationIds = locationIds;
        this.countryCodes = countryCodes;
        this.licenseId = licenseId;
        this.licenseName = licenseName;
        this.contact = contact;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dublinCore = dublinCore;
        this.dataObjectCount = dataObjectCount;
        this.dataPointCount = dataPointCount;
        this.isExternal = isExternal;
        this.collaborators = collaborators;
        this.attributes = attributes;
        this.acceptedBy = acceptedBy;
    }

    public Integer getDatasetId() {
        return this.datasetId;
    }

    public void setDatasetId(Integer datasetId) {
        this.datasetId = datasetId;
    }

    public String getDatasetName() {
        return this.datasetName;
    }

    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    public String getDatasetDescription() {
        return this.datasetDescription;
    }

    public void setDatasetDescription(String datasetDescription) {
        this.datasetDescription = datasetDescription;
    }

    public String getHyperlink() {
        return this.hyperlink;
    }

    public void setHyperlink(String hyperlink) {
        this.hyperlink = hyperlink;
    }

    public String getSourceFile() {
        return this.sourceFile;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public String getExperimentType() {
        return this.experimentType;
    }

    public void setExperimentType(String experimentType) {
        this.experimentType = experimentType;
    }

    public Integer getExperimentId() {
        return this.experimentId;
    }

    public void setExperimentId(Integer experimentId) {
        this.experimentId = experimentId;
    }

    public String getExperimentName() {
        return this.experimentName;
    }

    public void setExperimentName(String experimentName) {
        this.experimentName = experimentName;
    }

    public String getDatatype() {
        return this.datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public String getDatasetState() {
        return this.datasetState;
    }

    public void setDatasetState(String datasetState) {
        this.datasetState = datasetState;
    }

    public JsonArray getLocationIds() {
        return this.locationIds;
    }

    public void setLocationIds(JsonArray locationIds) {
        this.locationIds = locationIds;
    }

    public JsonArray getCountryCodes() {
        return this.countryCodes;
    }

    public void setCountryCodes(JsonArray countryCodes) {
        this.countryCodes = countryCodes;
    }

    public Integer getLicenseId() {
        return this.licenseId;
    }

    public void setLicenseId(Integer licenseId) {
        this.licenseId = licenseId;
    }

    public String getLicenseName() {
        return this.licenseName;
    }

    public void setLicenseName(String licenseName) {
        this.licenseName = licenseName;
    }

    public String getContact() {
        return this.contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public DublinCore getDublinCore() {
        return this.dublinCore;
    }

    public void setDublinCore(DublinCore dublinCore) {
        this.dublinCore = dublinCore;
    }

    public ULong getDataObjectCount() {
        return this.dataObjectCount;
    }

    public void setDataObjectCount(ULong dataObjectCount) {
        this.dataObjectCount = dataObjectCount;
    }

    public ULong getDataPointCount() {
        return this.dataPointCount;
    }

    public void setDataPointCount(ULong dataPointCount) {
        this.dataPointCount = dataPointCount;
    }

    public Boolean getIsExternal() {
        return this.isExternal;
    }

    public void setIsExternal(Boolean isExternal) {
        this.isExternal = isExternal;
    }

    public Long getCollaborators() {
        return this.collaborators;
    }

    public void setCollaborators(Long collaborators) {
        this.collaborators = collaborators;
    }

    public Long getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Long attributes) {
        this.attributes = attributes;
    }

    public JsonArray getAcceptedBy() {
        return this.acceptedBy;
    }

    public void setAcceptedBy(JsonArray acceptedBy) {
        this.acceptedBy = acceptedBy;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ViewTableDatasets (");

        sb.append(datasetId);
        sb.append(", ").append(datasetName);
        sb.append(", ").append(datasetDescription);
        sb.append(", ").append(hyperlink);
        sb.append(", ").append(sourceFile);
        sb.append(", ").append(experimentType);
        sb.append(", ").append(experimentId);
        sb.append(", ").append(experimentName);
        sb.append(", ").append(datatype);
        sb.append(", ").append(datasetState);
        sb.append(", ").append(locationIds);
        sb.append(", ").append(countryCodes);
        sb.append(", ").append(licenseId);
        sb.append(", ").append(licenseName);
        sb.append(", ").append(contact);
        sb.append(", ").append(startDate);
        sb.append(", ").append(endDate);
        sb.append(", ").append(dublinCore);
        sb.append(", ").append(dataObjectCount);
        sb.append(", ").append(dataPointCount);
        sb.append(", ").append(isExternal);
        sb.append(", ").append(collaborators);
        sb.append(", ").append(attributes);
        sb.append(", ").append(acceptedBy);

        sb.append(")");
        return sb.toString();
    }
// @formatter:on
}
