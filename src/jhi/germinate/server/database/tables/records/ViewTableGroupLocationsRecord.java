/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables.records;


import java.math.BigDecimal;

import javax.annotation.Generated;

import jhi.germinate.server.database.tables.ViewTableGroupLocations;

import org.jooq.Field;
import org.jooq.Record12;
import org.jooq.Row12;
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
public class ViewTableGroupLocationsRecord extends TableRecordImpl<ViewTableGroupLocationsRecord> implements Record12<Integer, String, String, String, String, BigDecimal, BigDecimal, BigDecimal, String, String, String, Integer> {

    private static final long serialVersionUID = 717120914;

    /**
     * Create a detached ViewTableGroupLocationsRecord
     */
    public ViewTableGroupLocationsRecord() {
        super(ViewTableGroupLocations.VIEW_TABLE_GROUP_LOCATIONS);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.view_table_group_locations.location_id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public Integer getLocationId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.view_table_group_locations.location_name</code>. The site name where the location is.
     */
    public void setLocationName(String value) {
        set(1, value);
    }

    /**
     * Create a detached, initialised ViewTableGroupLocationsRecord
     */
    public ViewTableGroupLocationsRecord(Integer locationId, String locationName, String locationRegion, String locationState, String locationType, BigDecimal locationLatitude, BigDecimal locationLongitude, BigDecimal locationElevation, String countryName, String countryCode2, String countryCode3, Integer groupId) {
        super(ViewTableGroupLocations.VIEW_TABLE_GROUP_LOCATIONS);

        set(0, locationId);
        set(1, locationName);
        set(2, locationRegion);
        set(3, locationState);
        set(4, locationType);
        set(5, locationLatitude);
        set(6, locationLongitude);
        set(7, locationElevation);
        set(8, countryName);
        set(9, countryCode2);
        set(10, countryCode3);
        set(11, groupId);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.view_table_group_locations.location_region</code>. The region where the location is if this exists.
     */
    public void setLocationRegion(String value) {
        set(2, value);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.view_table_group_locations.location_id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public void setLocationId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.view_table_group_locations.location_name</code>. The site name where the location is.
     */
    public String getLocationName() {
        return (String) get(1);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.view_table_group_locations.location_state</code>. The state where the location is if this exists.
     */
    public String getLocationState() {
        return (String) get(3);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.view_table_group_locations.location_region</code>. The region where the location is if this exists.
     */
    public String getLocationRegion() {
        return (String) get(2);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.view_table_group_locations.location_state</code>. The state where the location is if this exists.
     */
    public void setLocationState(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.view_table_group_locations.location_type</code>. The name of the location type.
     */
    public String getLocationType() {
        return (String) get(4);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.view_table_group_locations.location_latitude</code>. Latitude of the location.
     */
    public BigDecimal getLocationLatitude() {
        return (BigDecimal) get(5);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.view_table_group_locations.location_longitude</code>. Longitude of the location.
     */
    public void setLocationLongitude(BigDecimal value) {
        set(6, value);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.view_table_group_locations.location_type</code>. The name of the location type.
     */
    public void setLocationType(String value) {
        set(4, value);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.view_table_group_locations.location_elevation</code>. The elevation of the site in metres.
     */
    public void setLocationElevation(BigDecimal value) {
        set(7, value);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.view_table_group_locations.location_latitude</code>. Latitude of the location.
     */
    public void setLocationLatitude(BigDecimal value) {
        set(5, value);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.view_table_group_locations.location_longitude</code>. Longitude of the location.
     */
    public BigDecimal getLocationLongitude() {
        return (BigDecimal) get(6);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.view_table_group_locations.country_name</code>. Country name.
     */
    public String getCountryName() {
        return (String) get(8);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.view_table_group_locations.country_code2</code>. ISO 2 Code for country.
     */
    public void setCountryCode2(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.view_table_group_locations.location_elevation</code>. The elevation of the site in metres.
     */
    public BigDecimal getLocationElevation() {
        return (BigDecimal) get(7);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.view_table_group_locations.country_name</code>. Country name.
     */
    public void setCountryName(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.view_table_group_locations.country_code3</code>. ISO 3 Code for country.
     */
    public String getCountryCode3() {
        return (String) get(10);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.view_table_group_locations.group_id</code>. Foreign key to groups (groups.id).
     */
    public void setGroupId(Integer value) {
        set(11, value);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.view_table_group_locations.country_code2</code>. ISO 2 Code for country.
     */
    public String getCountryCode2() {
        return (String) get(9);
    }

    // -------------------------------------------------------------------------
    // Record12 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row12<Integer, String, String, String, String, BigDecimal, BigDecimal, BigDecimal, String, String, String, Integer> fieldsRow() {
        return (Row12) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row12<Integer, String, String, String, String, BigDecimal, BigDecimal, BigDecimal, String, String, String, Integer> valuesRow() {
        return (Row12) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return ViewTableGroupLocations.VIEW_TABLE_GROUP_LOCATIONS.LOCATION_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return ViewTableGroupLocations.VIEW_TABLE_GROUP_LOCATIONS.LOCATION_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return ViewTableGroupLocations.VIEW_TABLE_GROUP_LOCATIONS.LOCATION_REGION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return ViewTableGroupLocations.VIEW_TABLE_GROUP_LOCATIONS.LOCATION_STATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return ViewTableGroupLocations.VIEW_TABLE_GROUP_LOCATIONS.LOCATION_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<BigDecimal> field6() {
        return ViewTableGroupLocations.VIEW_TABLE_GROUP_LOCATIONS.LOCATION_LATITUDE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<BigDecimal> field7() {
        return ViewTableGroupLocations.VIEW_TABLE_GROUP_LOCATIONS.LOCATION_LONGITUDE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<BigDecimal> field8() {
        return ViewTableGroupLocations.VIEW_TABLE_GROUP_LOCATIONS.LOCATION_ELEVATION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field9() {
        return ViewTableGroupLocations.VIEW_TABLE_GROUP_LOCATIONS.COUNTRY_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field10() {
        return ViewTableGroupLocations.VIEW_TABLE_GROUP_LOCATIONS.COUNTRY_CODE2;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field11() {
        return ViewTableGroupLocations.VIEW_TABLE_GROUP_LOCATIONS.COUNTRY_CODE3;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field12() {
        return ViewTableGroupLocations.VIEW_TABLE_GROUP_LOCATIONS.GROUP_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component1() {
        return getLocationId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getLocationName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getLocationRegion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component4() {
        return getLocationState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component5() {
        return getLocationType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal component6() {
        return getLocationLatitude();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal component7() {
        return getLocationLongitude();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal component8() {
        return getLocationElevation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component9() {
        return getCountryName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component10() {
        return getCountryCode2();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component11() {
        return getCountryCode3();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component12() {
        return getGroupId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getLocationId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getLocationName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getLocationRegion();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getLocationState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getLocationType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal value6() {
        return getLocationLatitude();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal value7() {
        return getLocationLongitude();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal value8() {
        return getLocationElevation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value9() {
        return getCountryName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value10() {
        return getCountryCode2();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value11() {
        return getCountryCode3();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value12() {
        return getGroupId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableGroupLocationsRecord value1(Integer value) {
        setLocationId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableGroupLocationsRecord value2(String value) {
        setLocationName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableGroupLocationsRecord value3(String value) {
        setLocationRegion(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableGroupLocationsRecord value4(String value) {
        setLocationState(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableGroupLocationsRecord value5(String value) {
        setLocationType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableGroupLocationsRecord value6(BigDecimal value) {
        setLocationLatitude(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableGroupLocationsRecord value7(BigDecimal value) {
        setLocationLongitude(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableGroupLocationsRecord value8(BigDecimal value) {
        setLocationElevation(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableGroupLocationsRecord value9(String value) {
        setCountryName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableGroupLocationsRecord value10(String value) {
        setCountryCode2(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableGroupLocationsRecord value11(String value) {
        setCountryCode3(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableGroupLocationsRecord value12(Integer value) {
        setGroupId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableGroupLocationsRecord values(Integer value1, String value2, String value3, String value4, String value5, BigDecimal value6, BigDecimal value7, BigDecimal value8, String value9, String value10, String value11, Integer value12) {
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
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Setter for <code>germinate_template_3_7_0.view_table_group_locations.country_code3</code>. ISO 3 Code for country.
     */
    public void setCountryCode3(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.view_table_group_locations.group_id</code>. Foreign key to groups (groups.id).
     */
    public Integer getGroupId() {
        return (Integer) get(11);
    }
// @formatter:on
}
