/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables;


import org.jooq.*;
import org.jooq.impl.*;

import java.math.*;

import javax.annotation.*;

import jhi.germinate.server.database.*;
import jhi.germinate.server.database.tables.records.*;


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
public class ViewMcpd extends TableImpl<ViewMcpdRecord> {

    /**
     * The reference instance of <code>germinate_template_3_6_0.view_mcpd</code>
     */
    public static final ViewMcpd VIEW_MCPD = new ViewMcpd();
    private static final long serialVersionUID = -1702296225;
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.PUID</code>. Any persistent, unique identifier assigned to the accession so it can be unambiguously referenced at the global level and the information associated with it harvested through automated means. Report one PUID for each accession.
     */
    public final TableField<ViewMcpdRecord, String> PUID = createField("PUID", org.jooq.impl.SQLDataType.VARCHAR(255), this, "Any persistent, unique identifier assigned to the accession so it can be unambiguously referenced at the global level and the information associated with it harvested through automated means. Report one PUID for each accession.");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.INSTCODE</code>. If there is a defined ISO code for the institute this should be used here.
     */
    public final TableField<ViewMcpdRecord, String> INSTCODE = createField("INSTCODE", org.jooq.impl.SQLDataType.VARCHAR(255), this, "If there is a defined ISO code for the institute this should be used here.");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.ACCENUMB</code>. A unique identifier.
     */
    public final TableField<ViewMcpdRecord, String> ACCENUMB = createField("ACCENUMB", org.jooq.impl.SQLDataType.VARCHAR(255).nullable(false), this, "A unique identifier.");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.COLLNUMB</code>. Original identifier assigned by the collector(s) of the sample, normally composed of the name or
initials of the collector(s) followed by a number (e.g. ‘FM9909’). This identifier is essential for
identifying duplicates held in different collections.
     */
    public final TableField<ViewMcpdRecord, String> COLLNUMB = createField("COLLNUMB", org.jooq.impl.SQLDataType.VARCHAR(255), this, "Original identifier assigned by the collector(s) of the sample, normally composed of the name or\ninitials of the collector(s) followed by a number (e.g. ‘FM9909’). This identifier is essential for\nidentifying duplicates held in different collections.");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.COLLCODE</code>. FAO WIEWS code of the institute collecting the sample. If the holding institute has collected the
material, the collecting institute code (COLLCODE) should be the same as the holding institute
code (INSTCODE). Follows INSTCODE standard. Multiple values are separated by a semicolon
without space.
     */
    public final TableField<ViewMcpdRecord, String> COLLCODE = createField("COLLCODE", org.jooq.impl.SQLDataType.VARCHAR(255), this, "FAO WIEWS code of the institute collecting the sample. If the holding institute has collected the\nmaterial, the collecting institute code (COLLCODE) should be the same as the holding institute\ncode (INSTCODE). Follows INSTCODE standard. Multiple values are separated by a semicolon\nwithout space.");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.COLLNAME</code>. Name of the institute collecting the sample. This descriptor should be used only if COLLCODE cannot be filled because the FAO WIEWS code for this institute is not available. Multiple values are separated by a semicolon without space.
     */
    public final TableField<ViewMcpdRecord, String> COLLNAME = createField("COLLNAME", org.jooq.impl.SQLDataType.VARCHAR(255), this, "Name of the institute collecting the sample. This descriptor should be used only if COLLCODE cannot be filled because the FAO WIEWS code for this institute is not available. Multiple values are separated by a semicolon without space.");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.COLLINSTADDRESS</code>. The postal address of the institute.
     */
    public final TableField<ViewMcpdRecord, String> COLLINSTADDRESS = createField("COLLINSTADDRESS", org.jooq.impl.SQLDataType.CLOB, this, "The postal address of the institute.");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.COLLMISSID</code>. Identifier of the collecting mission used by the Collecting Institute (4 or 4.1) (e.g. 'CIATFOR-052', 'CN426').
     */
    public final TableField<ViewMcpdRecord, String> COLLMISSID = createField("COLLMISSID", org.jooq.impl.SQLDataType.VARCHAR(255), this, "Identifier of the collecting mission used by the Collecting Institute (4 or 4.1) (e.g. 'CIATFOR-052', 'CN426').");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.GENUS</code>. Genus name for the species.
     */
    public final TableField<ViewMcpdRecord, String> GENUS = createField("GENUS", org.jooq.impl.SQLDataType.VARCHAR(255).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "Genus name for the species.");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.SPECIES</code>. Species name in lowercase.
     */
    public final TableField<ViewMcpdRecord, String> SPECIES = createField("SPECIES", org.jooq.impl.SQLDataType.VARCHAR(255).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "Species name in lowercase.");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.SPAUTHOR</code>. also known as spauthor in the Multi Crop Passport Descriptors (MCPD V2 2012). Describes the authority for the species name.
     */
    public final TableField<ViewMcpdRecord, String> SPAUTHOR = createField("SPAUTHOR", org.jooq.impl.SQLDataType.VARCHAR(255), this, "also known as spauthor in the Multi Crop Passport Descriptors (MCPD V2 2012). Describes the authority for the species name.");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.SUBTAXA</code>. Subtaxa name.
     */
    public final TableField<ViewMcpdRecord, String> SUBTAXA = createField("SUBTAXA", org.jooq.impl.SQLDataType.VARCHAR(255), this, "Subtaxa name.");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.SUBTAUTHOR</code>. also known as subtauthor in the Multi Crop Passport Descriptors (MCPD V2 2012).
     */
    public final TableField<ViewMcpdRecord, String> SUBTAUTHOR = createField("SUBTAUTHOR", org.jooq.impl.SQLDataType.VARCHAR(255), this, "also known as subtauthor in the Multi Crop Passport Descriptors (MCPD V2 2012).");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.CROPNAME</code>. The name of the crop. This should be the common name. Examples would include barley, maize, wheat, rice and so on.
     */
    public final TableField<ViewMcpdRecord, String> CROPNAME = createField("CROPNAME", org.jooq.impl.SQLDataType.VARCHAR(255), this, "The name of the crop. This should be the common name. Examples would include barley, maize, wheat, rice and so on.");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.ACCENAME</code>. A unique name which defines an entry in the germinatbase table.
     */
    public final TableField<ViewMcpdRecord, String> ACCENAME = createField("ACCENAME", org.jooq.impl.SQLDataType.VARCHAR(255).nullable(false), this, "A unique name which defines an entry in the germinatbase table.");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.ACQDATE</code>.
     */
    public final TableField<ViewMcpdRecord, String> ACQDATE = createField("ACQDATE", org.jooq.impl.SQLDataType.VARCHAR(255), this, "");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.ORIGCTY</code>. ISO 3 Code for country.
     */
    public final TableField<ViewMcpdRecord, String> ORIGCTY = createField("ORIGCTY", org.jooq.impl.SQLDataType.CHAR(3).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.CHAR)), this, "ISO 3 Code for country.");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.COLLSITE</code>. The site name where the location is.
     */
    public final TableField<ViewMcpdRecord, String> COLLSITE = createField("COLLSITE", org.jooq.impl.SQLDataType.VARCHAR(255).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "The site name where the location is.");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.DECLATITUDE</code>. Latitude of the location.
     */
    public final TableField<ViewMcpdRecord, BigDecimal> DECLATITUDE = createField("DECLATITUDE", org.jooq.impl.SQLDataType.DECIMAL(64, 10), this, "Latitude of the location.");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.LATITUDE</code>.
     */
    public final TableField<ViewMcpdRecord, byte[]> LATITUDE = createField("LATITUDE", org.jooq.impl.SQLDataType.BINARY, this, "");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.DECLONGITUDE</code>. Longitude of the location.
     */
    public final TableField<ViewMcpdRecord, BigDecimal> DECLONGITUDE = createField("DECLONGITUDE", org.jooq.impl.SQLDataType.DECIMAL(64, 10), this, "Longitude of the location.");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.LONGITUDE</code>.
     */
    public final TableField<ViewMcpdRecord, byte[]> LONGITUDE = createField("LONGITUDE", org.jooq.impl.SQLDataType.BINARY, this, "");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.COORDUNCERT</code>. Uncertainty associated with the coordinates in metres. Leave the value empty if the uncertainty is unknown.
     */
    public final TableField<ViewMcpdRecord, Integer> COORDUNCERT = createField("COORDUNCERT", org.jooq.impl.SQLDataType.INTEGER, this, "Uncertainty associated with the coordinates in metres. Leave the value empty if the uncertainty is unknown. ");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.COORDDATUM</code>. The geodetic datum or spatial reference system upon which the coordinates given in decimal latitude and decimal longitude are based (e.g. WGS84, ETRS89, NAD83). The GPS uses the WGS84 datum.
     */
    public final TableField<ViewMcpdRecord, String> COORDDATUM = createField("COORDDATUM", org.jooq.impl.SQLDataType.VARCHAR(255), this, "The geodetic datum or spatial reference system upon which the coordinates given in decimal latitude and decimal longitude are based (e.g. WGS84, ETRS89, NAD83). The GPS uses the WGS84 datum.");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.GEOREFMETH</code>. The georeferencing method used (GPS, determined from map, gazetteer, or estimated using software). Leave the value empty if georeferencing method is not known.
     */
    public final TableField<ViewMcpdRecord, String> GEOREFMETH = createField("GEOREFMETH", org.jooq.impl.SQLDataType.VARCHAR(255), this, "The georeferencing method used (GPS, determined from map, gazetteer, or estimated using software). Leave the value empty if georeferencing method is not known.");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.ELEVATION</code>. The elevation of the site in metres.
     */
    public final TableField<ViewMcpdRecord, BigDecimal> ELEVATION = createField("ELEVATION", org.jooq.impl.SQLDataType.DECIMAL(64, 10), this, "The elevation of the site in metres.");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.COLLDATE</code>.
     */
    public final TableField<ViewMcpdRecord, String> COLLDATE = createField("COLLDATE", org.jooq.impl.SQLDataType.VARCHAR(10), this, "");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.BREDCODE</code>. FAO WIEWS code of the institute that has bred the material. If the holding institute has bred the material, the breeding institute code (BREDCODE) should be the same as the holding institute code (INSTCODE). Follows INSTCODE standard. Multiple values are separated by a semicolon without space.
     */
    public final TableField<ViewMcpdRecord, String> BREDCODE = createField("BREDCODE", org.jooq.impl.SQLDataType.CHAR(50), this, "FAO WIEWS code of the institute that has bred the material. If the holding institute has bred the material, the breeding institute code (BREDCODE) should be the same as the holding institute code (INSTCODE). Follows INSTCODE standard. Multiple values are separated by a semicolon without space.");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.BREDNAME</code>. Name of the institute (or person) that bred the material. This descriptor should be used only if BREDCODE cannot be filled because the FAO WIEWS code for this institute is not available. Multiple names are separated by a semicolon without space.
     */
    public final TableField<ViewMcpdRecord, String> BREDNAME = createField("BREDNAME", org.jooq.impl.SQLDataType.VARCHAR(255), this, "Name of the institute (or person) that bred the material. This descriptor should be used only if BREDCODE cannot be filled because the FAO WIEWS code for this institute is not available. Multiple names are separated by a semicolon without space.");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.SAMPSTAT</code>. Foreign key to biologicalstatus (biologicalstaus.id).
     */
    public final TableField<ViewMcpdRecord, Integer> SAMPSTAT = createField("SAMPSTAT", org.jooq.impl.SQLDataType.INTEGER, this, "Foreign key to biologicalstatus (biologicalstaus.id).");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.ANCEST</code>. The pedigree string which is used to represent the germinatebase entry.
     */
    public final TableField<ViewMcpdRecord, String> ANCEST = createField("ANCEST", org.jooq.impl.SQLDataType.CLOB, this, "The pedigree string which is used to represent the germinatebase entry.");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.COLLSRC</code>. Foreign key to collectionsources (collectionsources.id).
     */
    public final TableField<ViewMcpdRecord, Integer> COLLSRC = createField("COLLSRC", org.jooq.impl.SQLDataType.INTEGER, this, "Foreign key to collectionsources (collectionsources.id).");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.DONORCODE</code>. FAO WIEWS code of the donor institute. Follows INSTCODE standard.
     */
    public final TableField<ViewMcpdRecord, String> DONORCODE = createField("DONORCODE", org.jooq.impl.SQLDataType.VARCHAR(255), this, "FAO WIEWS code of the donor institute. Follows INSTCODE standard.");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.DONORNAME</code>. Name of the donor institute (or person). This descriptor should be used only if DONORCODE cannot be filled because the FAO WIEWS code for this institute is not available.
     */
    public final TableField<ViewMcpdRecord, String> DONORNAME = createField("DONORNAME", org.jooq.impl.SQLDataType.VARCHAR(255), this, "Name of the donor institute (or person). This descriptor should be used only if DONORCODE cannot be filled because the FAO WIEWS code for this institute is not available.");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.DONORNUMB</code>. Identifier assigned to an accession by the donor. Follows ACCENUMB standard.
     */
    public final TableField<ViewMcpdRecord, String> DONORNUMB = createField("DONORNUMB", org.jooq.impl.SQLDataType.VARCHAR(255), this, "Identifier assigned to an accession by the donor. Follows ACCENUMB standard.");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.OTHERNUMB</code>. Any other identifiers known to exist in other collections for this accession. Use the following format: INSTCODE:ACCENUMB;INSTCODE:identifier;… INSTCODE and identifier are separated by a colon without space. Pairs of INSTCODE and identifier are separated by a semicolon without space. When the institute is not known, the identifier should be preceded by a colon.
     */
    public final TableField<ViewMcpdRecord, String> OTHERNUMB = createField("OTHERNUMB", org.jooq.impl.SQLDataType.CLOB, this, "Any other identifiers known to exist in other collections for this accession. Use the following format: INSTCODE:ACCENUMB;INSTCODE:identifier;… INSTCODE and identifier are separated by a colon without space. Pairs of INSTCODE and identifier are separated by a semicolon without space. When the institute is not known, the identifier should be preceded by a colon.");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.DUPLSITE</code>. FAO WIEWS code of the institute(s) where a safety duplicate of the accession is maintained.
Multiple values are separated by a semicolon without space. Follows INSTCODE standard.
     */
    public final TableField<ViewMcpdRecord, String> DUPLSITE = createField("DUPLSITE", org.jooq.impl.SQLDataType.VARCHAR(255), this, "FAO WIEWS code of the institute(s) where a safety duplicate of the accession is maintained.\nMultiple values are separated by a semicolon without space. Follows INSTCODE standard.");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.DUPLINSTNAME</code>. Name of the institute where a safety duplicate of the accession is maintained. Multiple values are separated by a semicolon without space.
     */
    public final TableField<ViewMcpdRecord, String> DUPLINSTNAME = createField("DUPLINSTNAME", org.jooq.impl.SQLDataType.VARCHAR(255), this, "Name of the institute where a safety duplicate of the accession is maintained. Multiple values are separated by a semicolon without space.");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.STORAGE</code>.
     */
    public final TableField<ViewMcpdRecord, String> STORAGE = createField("STORAGE", org.jooq.impl.SQLDataType.CLOB, this, "");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.MLSSTAT</code>. Foreign key to mlsstatus (mlsstatus.id).
     */
    public final TableField<ViewMcpdRecord, Integer> MLSSTAT = createField("MLSSTAT", org.jooq.impl.SQLDataType.INTEGER, this, "Foreign key to mlsstatus (mlsstatus.id).");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.REMARKS</code>.
     */
    public final TableField<ViewMcpdRecord, String> REMARKS = createField("REMARKS", org.jooq.impl.SQLDataType.CLOB, this, "");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.Entity Type</code>.
     */
    public final TableField<ViewMcpdRecord, String> ENTITY_TYPE = createField("Entity Type", org.jooq.impl.SQLDataType.VARCHAR(9).nullable(false).defaultValue(org.jooq.impl.DSL.inline("", org.jooq.impl.SQLDataType.VARCHAR)), this, "");
    /**
     * The column <code>germinate_template_3_6_0.view_mcpd.Entity parent ACCENUMB</code>.
     */
    public final TableField<ViewMcpdRecord, byte[]> ENTITY_PARENT_ACCENUMB = createField("Entity parent ACCENUMB", org.jooq.impl.SQLDataType.BINARY, this, "");

    /**
     * Create a <code>germinate_template_3_6_0.view_mcpd</code> table reference
     */
    public ViewMcpd() {
        this(DSL.name("view_mcpd"), null);
    }

    /**
     * Create an aliased <code>germinate_template_3_6_0.view_mcpd</code> table reference
     */
    public ViewMcpd(String alias) {
        this(DSL.name(alias), VIEW_MCPD);
    }

    /**
     * Create an aliased <code>germinate_template_3_6_0.view_mcpd</code> table reference
     */
    public ViewMcpd(Name alias) {
        this(alias, VIEW_MCPD);
    }

    private ViewMcpd(Name alias, Table<ViewMcpdRecord> aliased) {
        this(alias, aliased, null);
    }

    private ViewMcpd(Name alias, Table<ViewMcpdRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("VIEW"));
    }

    public <O extends Record> ViewMcpd(Table<O> child, ForeignKey<O, ViewMcpdRecord> key) {
        super(child, key, VIEW_MCPD);
    }

    /**
     * The class holding records for this type
     */
    @Override
    public Class<ViewMcpdRecord> getRecordType() {
        return ViewMcpdRecord.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return GerminateTemplate_3_6_0.GERMINATE_TEMPLATE_3_6_0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewMcpd as(String alias) {
        return new ViewMcpd(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewMcpd as(Name alias) {
        return new ViewMcpd(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewMcpd rename(String name) {
        return new ViewMcpd(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public ViewMcpd rename(Name name) {
        return new ViewMcpd(name, null);
    }
}
