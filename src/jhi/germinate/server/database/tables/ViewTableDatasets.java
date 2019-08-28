/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables;


import java.sql.Date;

import javax.annotation.Generated;

import jhi.germinate.server.database.GerminateTemplate_3_7_0;
import jhi.germinate.server.database.tables.records.ViewTableDatasetsRecord;

import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
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
public class ViewTableDatasets extends TableImpl<ViewTableDatasetsRecord> {

    private static final long serialVersionUID = -335867897;

    /**
     * The reference instance of <code>germinate_template_3_7_0.view_table_datasets</code>
     */
    public static final ViewTableDatasets VIEW_TABLE_DATASETS = new ViewTableDatasets();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ViewTableDatasetsRecord> getRecordType() {
        return ViewTableDatasetsRecord.class;
    }

    /**
     * The column <code>germinate_template_3_7_0.view_table_datasets.datasetId</code>. Primary id for this table. This uniquely identifies the row.
     */
    public final TableField<ViewTableDatasetsRecord, Integer> DATASETID = createField("datasetId", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "Primary id for this table. This uniquely identifies the row.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_datasets.datasetName</code>. Describes the dataset.
     */
    public final TableField<ViewTableDatasetsRecord, String> DATASETNAME = createField("datasetName", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "Describes the dataset.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_datasets.datesetDescription</code>. The name of this dataset.
     */
    public final TableField<ViewTableDatasetsRecord, String> DATESETDESCRIPTION = createField("datesetDescription", org.jooq.impl.SQLDataType.CLOB, this, "The name of this dataset.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_datasets.experimentType</code>. Describes the experiment type.
     */
    public final TableField<ViewTableDatasetsRecord, String> EXPERIMENTTYPE = createField("experimentType", org.jooq.impl.SQLDataType.VARCHAR(255), this, "Describes the experiment type.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_datasets.experimentName</code>. Describes the experiment.
     */
    public final TableField<ViewTableDatasetsRecord, String> EXPERIMENTNAME = createField("experimentName", org.jooq.impl.SQLDataType.CLOB, this, "Describes the experiment.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_datasets.datatype</code>. A description of the data type of the contained data. Examples might be: "raw data", "BLUPs", etc.
     */
    public final TableField<ViewTableDatasetsRecord, String> DATATYPE = createField("datatype", org.jooq.impl.SQLDataType.VARCHAR(255), this, "A description of the data type of the contained data. Examples might be: \"raw data\", \"BLUPs\", etc.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_datasets.datasetState</code>. Defines the datasetstate.
     */
    public final TableField<ViewTableDatasetsRecord, String> DATASETSTATE = createField("datasetState", org.jooq.impl.SQLDataType.VARCHAR(255), this, "Defines the datasetstate.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_datasets.location</code>. The site name where the location is.
     */
    public final TableField<ViewTableDatasetsRecord, String> LOCATION = createField("location", org.jooq.impl.SQLDataType.VARCHAR(255).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "The site name where the location is.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_datasets.countryCode</code>. ISO 2 Code for country.
     */
    public final TableField<ViewTableDatasetsRecord, String> COUNTRYCODE = createField("countryCode", org.jooq.impl.SQLDataType.CHAR(2).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.CHAR)), this, "ISO 2 Code for country.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_datasets.countryName</code>. Country name.
     */
    public final TableField<ViewTableDatasetsRecord, String> COUNTRYNAME = createField("countryName", org.jooq.impl.SQLDataType.VARCHAR(255).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "Country name.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_datasets.licenseId</code>.
     */
    public final TableField<ViewTableDatasetsRecord, Integer> LICENSEID = createField("licenseId", org.jooq.impl.SQLDataType.INTEGER.defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>germinate_template_3_7_0.view_table_datasets.licenseName</code>.
     */
    public final TableField<ViewTableDatasetsRecord, String> LICENSENAME = createField("licenseName", org.jooq.impl.SQLDataType.VARCHAR(255), this, "");

    /**
     * The column <code>germinate_template_3_7_0.view_table_datasets.contact</code>. The contact to get more information about this dataset.
     */
    public final TableField<ViewTableDatasetsRecord, String> CONTACT = createField("contact", org.jooq.impl.SQLDataType.VARCHAR(255), this, "The contact to get more information about this dataset.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_datasets.startDate</code>. Date that the dataset was generated.
     */
    public final TableField<ViewTableDatasetsRecord, Date> STARTDATE = createField("startDate", org.jooq.impl.SQLDataType.DATE, this, "Date that the dataset was generated.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_datasets.endDate</code>. Date at which the dataset recording ended.
     */
    public final TableField<ViewTableDatasetsRecord, Date> ENDDATE = createField("endDate", org.jooq.impl.SQLDataType.DATE, this, "Date at which the dataset recording ended.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_datasets.dataObjectCount</code>. The number of data objects contained in this dataset.
     */
    public final TableField<ViewTableDatasetsRecord, ULong> DATAOBJECTCOUNT = createField("dataObjectCount", org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "The number of data objects contained in this dataset.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_datasets.dataPointCount</code>. The number of individual data points contained in this dataset.
     */
    public final TableField<ViewTableDatasetsRecord, ULong> DATAPOINTCOUNT = createField("dataPointCount", org.jooq.impl.SQLDataType.BIGINTUNSIGNED, this, "The number of individual data points contained in this dataset.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_datasets.isExternal</code>. Defines if the dataset is contained within Germinate or from an external source and not stored in the database.
     */
    public final TableField<ViewTableDatasetsRecord, Byte> ISEXTERNAL = createField("isExternal", org.jooq.impl.SQLDataType.TINYINT.defaultValue(org.jooq.impl.DSL.inline("0", org.jooq.impl.SQLDataType.TINYINT)), this, "Defines if the dataset is contained within Germinate or from an external source and not stored in the database.");

    /**
     * The column <code>germinate_template_3_7_0.view_table_datasets.acceptedBy</code>.
     */
    public final TableField<ViewTableDatasetsRecord, String> ACCEPTEDBY = createField("acceptedBy", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * Create a <code>germinate_template_3_7_0.view_table_datasets</code> table reference
     */
    public ViewTableDatasets() {
        this(DSL.name("view_table_datasets"), null);
    }

    /**
     * Create an aliased <code>germinate_template_3_7_0.view_table_datasets</code> table reference
     */
    public ViewTableDatasets(String alias) {
        this(DSL.name(alias), VIEW_TABLE_DATASETS);
    }

    /**
     * Create an aliased <code>germinate_template_3_7_0.view_table_datasets</code> table reference
     */
    public ViewTableDatasets(Name alias) {
        this(alias, VIEW_TABLE_DATASETS);
    }

    private ViewTableDatasets(Name alias, Table<ViewTableDatasetsRecord> aliased) {
        this(alias, aliased, null);
    }

    private ViewTableDatasets(Name alias, Table<ViewTableDatasetsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("VIEW"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return GerminateTemplate_3_7_0.GERMINATE_TEMPLATE_3_7_0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableDatasets as(String alias) {
        return new ViewTableDatasets(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewTableDatasets as(Name alias) {
        return new ViewTableDatasets(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewTableDatasets rename(String name) {
        return new ViewTableDatasets(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewTableDatasets rename(Name name) {
        return new ViewTableDatasets(name, null);
    }
// @formatter:on
}
