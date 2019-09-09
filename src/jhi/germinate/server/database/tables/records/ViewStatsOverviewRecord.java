/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables.records;


import javax.annotation.Generated;

import jhi.germinate.server.database.tables.ViewStatsOverview;

import org.jooq.Field;
import org.jooq.Record6;
import org.jooq.Row6;
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
public class ViewStatsOverviewRecord extends TableRecordImpl<ViewStatsOverviewRecord> implements Record6<Long, Long, Long, Long, Long, Long> {

    private static final long serialVersionUID = 554633676;

    /**
     * Create a detached ViewStatsOverviewRecord
     */
    public ViewStatsOverviewRecord() {
        super(ViewStatsOverview.VIEW_STATS_OVERVIEW);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.view_stats_overview.germplasm</code>.
     */
    public Long getGermplasm() {
        return (Long) get(0);
    }

    /**
     * Create a detached, initialised ViewStatsOverviewRecord
     */
    public ViewStatsOverviewRecord(Long germplasm, Long markers, Long traits, Long compounds, Long locations, Long groups) {
        super(ViewStatsOverview.VIEW_STATS_OVERVIEW);

        set(0, germplasm);
        set(1, markers);
        set(2, traits);
        set(3, compounds);
        set(4, locations);
        set(5, groups);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.view_stats_overview.markers</code>.
     */
    public Long getMarkers() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.view_stats_overview.traits</code>.
     */
    public void setTraits(Long value) {
        set(2, value);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.view_stats_overview.traits</code>.
     */
    public Long getTraits() {
        return (Long) get(2);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.view_stats_overview.compounds</code>.
     */
    public void setCompounds(Long value) {
        set(3, value);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.view_stats_overview.compounds</code>.
     */
    public Long getCompounds() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.view_stats_overview.locations</code>.
     */
    public void setLocations(Long value) {
        set(4, value);
    }

    /**
     * Getter for <code>germinate_template_3_7_0.view_stats_overview.locations</code>.
     */
    public Long getLocations() {
        return (Long) get(4);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.view_stats_overview.germplasm</code>.
     */
    public void setGermplasm(Long value) {
        set(0, value);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.view_stats_overview.markers</code>.
     */
    public void setMarkers(Long value) {
        set(1, value);
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row6<Long, Long, Long, Long, Long, Long> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row6<Long, Long, Long, Long, Long, Long> valuesRow() {
        return (Row6) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field1() {
        return ViewStatsOverview.VIEW_STATS_OVERVIEW.GERMPLASM;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field2() {
        return ViewStatsOverview.VIEW_STATS_OVERVIEW.MARKERS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field3() {
        return ViewStatsOverview.VIEW_STATS_OVERVIEW.TRAITS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field4() {
        return ViewStatsOverview.VIEW_STATS_OVERVIEW.COMPOUNDS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field5() {
        return ViewStatsOverview.VIEW_STATS_OVERVIEW.LOCATIONS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Long> field6() {
        return ViewStatsOverview.VIEW_STATS_OVERVIEW.GROUPS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component1() {
        return getGermplasm();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component2() {
        return getMarkers();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component3() {
        return getTraits();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component4() {
        return getCompounds();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component5() {
        return getLocations();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long component6() {
        return getGroups();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value1() {
        return getGermplasm();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value2() {
        return getMarkers();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value3() {
        return getTraits();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value4() {
        return getCompounds();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value5() {
        return getLocations();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long value6() {
        return getGroups();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewStatsOverviewRecord value1(Long value) {
        setGermplasm(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewStatsOverviewRecord value2(Long value) {
        setMarkers(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewStatsOverviewRecord value3(Long value) {
        setTraits(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewStatsOverviewRecord value4(Long value) {
        setCompounds(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewStatsOverviewRecord value5(Long value) {
        setLocations(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewStatsOverviewRecord value6(Long value) {
        setGroups(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewStatsOverviewRecord values(Long value1, Long value2, Long value3, Long value4, Long value5, Long value6) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Getter for <code>germinate_template_3_7_0.view_stats_overview.groups</code>.
     */
    public Long getGroups() {
        return (Long) get(5);
    }

    /**
     * Setter for <code>germinate_template_3_7_0.view_stats_overview.groups</code>.
     */
    public void setGroups(Long value) {
        set(5, value);
    }
// @formatter:on
}
