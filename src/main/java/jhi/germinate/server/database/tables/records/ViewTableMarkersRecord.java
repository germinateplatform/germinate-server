/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables.records;


import com.google.gson.JsonArray;

import javax.annotation.Generated;

import jhi.germinate.server.database.tables.ViewTableMarkers;

import org.jooq.Field;
import org.jooq.Record4;
import org.jooq.Row4;
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
public class ViewTableMarkersRecord extends TableRecordImpl<ViewTableMarkersRecord> implements Record4<Integer, String, String, JsonArray> {

    private static final long serialVersionUID = 1000096958;

    /**
     * Setter for <code>germinate_template_4_0_0.view_table_markers.marker_id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public void setMarkerId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>germinate_template_4_0_0.view_table_markers.marker_id</code>. Primary id for this table. This uniquely identifies the row.
     */
    public Integer getMarkerId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>germinate_template_4_0_0.view_table_markers.marker_name</code>. The name of the marker. This should be a unique name which identifies the marker.
     */
    public void setMarkerName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>germinate_template_4_0_0.view_table_markers.marker_name</code>. The name of the marker. This should be a unique name which identifies the marker.
     */
    public String getMarkerName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>germinate_template_4_0_0.view_table_markers.marker_type</code>. Describes the marker type. Markers (markers) have a defined type. This could be AFLP, MicroSat, SNP and so on.
     */
    public void setMarkerType(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>germinate_template_4_0_0.view_table_markers.marker_type</code>. Describes the marker type. Markers (markers) have a defined type. This could be AFLP, MicroSat, SNP and so on.
     */
    public String getMarkerType() {
        return (String) get(2);
    }

    /**
     * Setter for <code>germinate_template_4_0_0.view_table_markers.marker_synonyms</code>. The synonyms as a json array.
     */
    public void setMarkerSynonyms(JsonArray value) {
        set(3, value);
    }

    /**
     * Getter for <code>germinate_template_4_0_0.view_table_markers.marker_synonyms</code>. The synonyms as a json array.
     */
    public JsonArray getMarkerSynonyms() {
        return (JsonArray) get(3);
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row4<Integer, String, String, JsonArray> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row4<Integer, String, String, JsonArray> valuesRow() {
        return (Row4) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return ViewTableMarkers.VIEW_TABLE_MARKERS.MARKER_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return ViewTableMarkers.VIEW_TABLE_MARKERS.MARKER_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return ViewTableMarkers.VIEW_TABLE_MARKERS.MARKER_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<JsonArray> field4() {
        return ViewTableMarkers.VIEW_TABLE_MARKERS.MARKER_SYNONYMS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component1() {
        return getMarkerId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getMarkerName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getMarkerType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonArray component4() {
        return getMarkerSynonyms();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getMarkerId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getMarkerName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getMarkerType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonArray value4() {
        return getMarkerSynonyms();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableMarkersRecord value1(Integer value) {
        setMarkerId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableMarkersRecord value2(String value) {
        setMarkerName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableMarkersRecord value3(String value) {
        setMarkerType(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableMarkersRecord value4(JsonArray value) {
        setMarkerSynonyms(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableMarkersRecord values(Integer value1, String value2, String value3, JsonArray value4) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ViewTableMarkersRecord
     */
    public ViewTableMarkersRecord() {
        super(ViewTableMarkers.VIEW_TABLE_MARKERS);
    }

    /**
     * Create a detached, initialised ViewTableMarkersRecord
     */
    public ViewTableMarkersRecord(Integer markerId, String markerName, String markerType, JsonArray markerSynonyms) {
        super(ViewTableMarkers.VIEW_TABLE_MARKERS);

        set(0, markerId);
        set(1, markerName);
        set(2, markerType);
        set(3, markerSynonyms);
    }
// @formatter:on
}
