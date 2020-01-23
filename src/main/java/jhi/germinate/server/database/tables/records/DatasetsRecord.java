/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables.records;


import java.sql.Date;
import java.sql.Timestamp;

import javax.annotation.Generated;

import jhi.germinate.resource.DublinCore;
import jhi.germinate.server.database.tables.Datasets;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record19;
import org.jooq.Row19;
import org.jooq.impl.UpdatableRecordImpl;


// @formatter:off
/**
 * Datasets which are defined within Germinate although there can be external 
 * datasets which are links out to external data sources most will be held 
 * within Germinate.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DatasetsRecord extends UpdatableRecordImpl<DatasetsRecord> implements Record19<Integer, Integer, Integer, String, String, Date, Date, String, String, DublinCore, String, Integer, Integer, Integer, Boolean, String, Timestamp, Timestamp, String> {

    private static final long serialVersionUID = 2096540385;

    /**
     * Setter for <code>germinate_template_4_0_0.datasets.id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>germinate_template_4_0_0.datasets.id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>germinate_template_4_0_0.datasets.experiment_id</code>. Foreign key to experiments (experiments.id).
     */
    public void setExperimentId(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>germinate_template_4_0_0.datasets.experiment_id</code>. Foreign key to experiments (experiments.id).
     */
    public Integer getExperimentId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>germinate_template_4_0_0.datasets.location_id</code>. Foreign key to locations (locations.id).
     */
    public void setLocationId(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>germinate_template_4_0_0.datasets.location_id</code>. Foreign key to locations (locations.id).
     */
    public Integer getLocationId() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>germinate_template_4_0_0.datasets.name</code>. Describes the dataset.
     */
    public void setName(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>germinate_template_4_0_0.datasets.name</code>. Describes the dataset.
     */
    public String getName() {
        return (String) get(3);
    }

    /**
     * Setter for <code>germinate_template_4_0_0.datasets.description</code>. The name of this dataset.
     */
    public void setDescription(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>germinate_template_4_0_0.datasets.description</code>. The name of this dataset.
     */
    public String getDescription() {
        return (String) get(4);
    }

    /**
     * Setter for <code>germinate_template_4_0_0.datasets.date_start</code>. Date that the dataset was generated.
     */
    public void setDateStart(Date value) {
        set(5, value);
    }

    /**
     * Getter for <code>germinate_template_4_0_0.datasets.date_start</code>. Date that the dataset was generated.
     */
    public Date getDateStart() {
        return (Date) get(5);
    }

    /**
     * Setter for <code>germinate_template_4_0_0.datasets.date_end</code>. Date at which the dataset recording ended.
     */
    public void setDateEnd(Date value) {
        set(6, value);
    }

    /**
     * Getter for <code>germinate_template_4_0_0.datasets.date_end</code>. Date at which the dataset recording ended.
     */
    public Date getDateEnd() {
        return (Date) get(6);
    }

    /**
     * Setter for <code>germinate_template_4_0_0.datasets.source_file</code>.
     */
    public void setSourceFile(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>germinate_template_4_0_0.datasets.source_file</code>.
     */
    public String getSourceFile() {
        return (String) get(7);
    }

    /**
     * Setter for <code>germinate_template_4_0_0.datasets.datatype</code>. A description of the data type of the contained data. Examples might be: "raw data", "BLUPs", etc.
     */
    public void setDatatype(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>germinate_template_4_0_0.datasets.datatype</code>. A description of the data type of the contained data. Examples might be: "raw data", "BLUPs", etc.
     */
    public String getDatatype() {
        return (String) get(8);
    }

    /**
     * Setter for <code>germinate_template_4_0_0.datasets.dublin_core</code>.
     */
    public void setDublinCore(DublinCore value) {
        set(9, value);
    }

    /**
     * Getter for <code>germinate_template_4_0_0.datasets.dublin_core</code>.
     */
    public DublinCore getDublinCore() {
        return (DublinCore) get(9);
    }

    /**
     * Setter for <code>germinate_template_4_0_0.datasets.version</code>. Dataset version if this exists.
     */
    public void setVersion(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>germinate_template_4_0_0.datasets.version</code>. Dataset version if this exists.
     */
    public String getVersion() {
        return (String) get(10);
    }

    /**
     * Setter for <code>germinate_template_4_0_0.datasets.created_by</code>. Defines who created the dataset. This is a FK in Gatekeeper users table. Foreign key to Gatekeeper users (users.id).
     */
    public void setCreatedBy(Integer value) {
        set(11, value);
    }

    /**
     * Getter for <code>germinate_template_4_0_0.datasets.created_by</code>. Defines who created the dataset. This is a FK in Gatekeeper users table. Foreign key to Gatekeeper users (users.id).
     */
    public Integer getCreatedBy() {
        return (Integer) get(11);
    }

    /**
     * Setter for <code>germinate_template_4_0_0.datasets.dataset_state_id</code>. Foreign key to datasetstates (datasetstates.id).
     */
    public void setDatasetStateId(Integer value) {
        set(12, value);
    }

    /**
     * Getter for <code>germinate_template_4_0_0.datasets.dataset_state_id</code>. Foreign key to datasetstates (datasetstates.id).
     */
    public Integer getDatasetStateId() {
        return (Integer) get(12);
    }

    /**
     * Setter for <code>germinate_template_4_0_0.datasets.license_id</code>.
     */
    public void setLicenseId(Integer value) {
        set(13, value);
    }

    /**
     * Getter for <code>germinate_template_4_0_0.datasets.license_id</code>.
     */
    public Integer getLicenseId() {
        return (Integer) get(13);
    }

    /**
     * Setter for <code>germinate_template_4_0_0.datasets.is_external</code>. Defines if the dataset is contained within Germinate or from an external source and not stored in the database.
     */
    public void setIsExternal(Boolean value) {
        set(14, value);
    }

    /**
     * Getter for <code>germinate_template_4_0_0.datasets.is_external</code>. Defines if the dataset is contained within Germinate or from an external source and not stored in the database.
     */
    public Boolean getIsExternal() {
        return (Boolean) get(14);
    }

    /**
     * Setter for <code>germinate_template_4_0_0.datasets.hyperlink</code>. Link to access the external dasets.
     */
    public void setHyperlink(String value) {
        set(15, value);
    }

    /**
     * Getter for <code>germinate_template_4_0_0.datasets.hyperlink</code>. Link to access the external dasets.
     */
    public String getHyperlink() {
        return (String) get(15);
    }

    /**
     * Setter for <code>germinate_template_4_0_0.datasets.created_on</code>. When the record was created.

     */
    public void setCreatedOn(Timestamp value) {
        set(16, value);
    }

    /**
     * Getter for <code>germinate_template_4_0_0.datasets.created_on</code>. When the record was created.

     */
    public Timestamp getCreatedOn() {
        return (Timestamp) get(16);
    }

    /**
     * Setter for <code>germinate_template_4_0_0.datasets.updated_on</code>. When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.
     */
    public void setUpdatedOn(Timestamp value) {
        set(17, value);
    }

    /**
     * Getter for <code>germinate_template_4_0_0.datasets.updated_on</code>. When the record was updated. This may be different from the created on date if subsequent changes have been made to the underlying record.
     */
    public Timestamp getUpdatedOn() {
        return (Timestamp) get(17);
    }

    /**
     * Setter for <code>germinate_template_4_0_0.datasets.contact</code>. The contact to get more information about this dataset.
     */
    public void setContact(String value) {
        set(18, value);
    }

    /**
     * Getter for <code>germinate_template_4_0_0.datasets.contact</code>. The contact to get more information about this dataset.
     */
    public String getContact() {
        return (String) get(18);
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
    // Record19 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row19<Integer, Integer, Integer, String, String, Date, Date, String, String, DublinCore, String, Integer, Integer, Integer, Boolean, String, Timestamp, Timestamp, String> fieldsRow() {
        return (Row19) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row19<Integer, Integer, Integer, String, String, Date, Date, String, String, DublinCore, String, Integer, Integer, Integer, Boolean, String, Timestamp, Timestamp, String> valuesRow() {
        return (Row19) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return Datasets.DATASETS.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field2() {
        return Datasets.DATASETS.EXPERIMENT_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field3() {
        return Datasets.DATASETS.LOCATION_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return Datasets.DATASETS.NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return Datasets.DATASETS.DESCRIPTION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Date> field6() {
        return Datasets.DATASETS.DATE_START;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Date> field7() {
        return Datasets.DATASETS.DATE_END;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return Datasets.DATASETS.SOURCE_FILE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return Datasets.DATASETS.DATATYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<DublinCore> field10() {
        return Datasets.DATASETS.DUBLIN_CORE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field11() {
        return Datasets.DATASETS.VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field12() {
        return Datasets.DATASETS.CREATED_BY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field13() {
        return Datasets.DATASETS.DATASET_STATE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field14() {
        return Datasets.DATASETS.LICENSE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field15() {
        return Datasets.DATASETS.IS_EXTERNAL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field16() {
        return Datasets.DATASETS.HYPERLINK;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field17() {
        return Datasets.DATASETS.CREATED_ON;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field18() {
        return Datasets.DATASETS.UPDATED_ON;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field19() {
        return Datasets.DATASETS.CONTACT;
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
        return getExperimentId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component3() {
        return getLocationId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component4() {
        return getName();
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
    public Date component6() {
        return getDateStart();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date component7() {
        return getDateEnd();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component8() {
        return getSourceFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component9() {
        return getDatatype();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DublinCore component10() {
        return getDublinCore();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component11() {
        return getVersion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component12() {
        return getCreatedBy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component13() {
        return getDatasetStateId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component14() {
        return getLicenseId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean component15() {
        return getIsExternal();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component16() {
        return getHyperlink();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component17() {
        return getCreatedOn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component18() {
        return getUpdatedOn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component19() {
        return getContact();
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
        return getExperimentId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value3() {
        return getLocationId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getName();
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
    public Date value6() {
        return getDateStart();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date value7() {
        return getDateEnd();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value8() {
        return getSourceFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value9() {
        return getDatatype();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DublinCore value10() {
        return getDublinCore();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value11() {
        return getVersion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value12() {
        return getCreatedBy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value13() {
        return getDatasetStateId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value14() {
        return getLicenseId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value15() {
        return getIsExternal();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value16() {
        return getHyperlink();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value17() {
        return getCreatedOn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value18() {
        return getUpdatedOn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value19() {
        return getContact();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetsRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetsRecord value2(Integer value) {
        setExperimentId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetsRecord value3(Integer value) {
        setLocationId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetsRecord value4(String value) {
        setName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetsRecord value5(String value) {
        setDescription(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetsRecord value6(Date value) {
        setDateStart(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetsRecord value7(Date value) {
        setDateEnd(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetsRecord value8(String value) {
        setSourceFile(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetsRecord value9(String value) {
        setDatatype(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetsRecord value10(DublinCore value) {
        setDublinCore(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetsRecord value11(String value) {
        setVersion(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetsRecord value12(Integer value) {
        setCreatedBy(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetsRecord value13(Integer value) {
        setDatasetStateId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetsRecord value14(Integer value) {
        setLicenseId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetsRecord value15(Boolean value) {
        setIsExternal(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetsRecord value16(String value) {
        setHyperlink(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetsRecord value17(Timestamp value) {
        setCreatedOn(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetsRecord value18(Timestamp value) {
        setUpdatedOn(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetsRecord value19(String value) {
        setContact(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DatasetsRecord values(Integer value1, Integer value2, Integer value3, String value4, String value5, Date value6, Date value7, String value8, String value9, DublinCore value10, String value11, Integer value12, Integer value13, Integer value14, Boolean value15, String value16, Timestamp value17, Timestamp value18, String value19) {
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
        value11(value11);
        value12(value12);
        value13(value13);
        value14(value14);
        value15(value15);
        value16(value16);
        value17(value17);
        value18(value18);
        value19(value19);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached DatasetsRecord
     */
    public DatasetsRecord() {
        super(Datasets.DATASETS);
    }

    /**
     * Create a detached, initialised DatasetsRecord
     */
    public DatasetsRecord(Integer id, Integer experimentId, Integer locationId, String name, String description, Date dateStart, Date dateEnd, String sourceFile, String datatype, DublinCore dublinCore, String version, Integer createdBy, Integer datasetStateId, Integer licenseId, Boolean isExternal, String hyperlink, Timestamp createdOn, Timestamp updatedOn, String contact) {
        super(Datasets.DATASETS);

        set(0, id);
        set(1, experimentId);
        set(2, locationId);
        set(3, name);
        set(4, description);
        set(5, dateStart);
        set(6, dateEnd);
        set(7, sourceFile);
        set(8, datatype);
        set(9, dublinCore);
        set(10, version);
        set(11, createdBy);
        set(12, datasetStateId);
        set(13, licenseId);
        set(14, isExternal);
        set(15, hyperlink);
        set(16, createdOn);
        set(17, updatedOn);
        set(18, contact);
    }
// @formatter:on
}
