/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables.records;


import org.jooq.impl.*;

import java.math.*;

import javax.annotation.*;

import jhi.germinate.server.database.tables.*;


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
public class ViewMcpdRecord extends TableRecordImpl<ViewMcpdRecord> {

    private static final long serialVersionUID = -953224741;

    /**
     * Create a detached ViewMcpdRecord
     */
    public ViewMcpdRecord() {
        super(ViewMcpd.VIEW_MCPD);
    }

    /**
     * Create a detached, initialised ViewMcpdRecord
     */
    public ViewMcpdRecord(String puid, String instcode, String accenumb, String collnumb, String collcode, String collname, String collinstaddress, String collmissid, String genus, String species, String spauthor, String subtaxa, String subtauthor, String cropname, String accename, String acqdate, String origcty, String collsite, BigDecimal declatitude, byte[] latitude, BigDecimal declongitude, byte[] longitude, Integer coorduncert, String coorddatum, String georefmeth, BigDecimal elevation, String colldate, String bredcode, String bredname, Integer sampstat, String ancest, Integer collsrc, String donorcode, String donorname, String donornumb, String othernumb, String duplsite, String duplinstname, String storage, Integer mlsstat, String remarks, String entityType, byte[] entityParentAccenumb) {
        super(ViewMcpd.VIEW_MCPD);

        set(0, puid);
        set(1, instcode);
        set(2, accenumb);
        set(3, collnumb);
        set(4, collcode);
        set(5, collname);
        set(6, collinstaddress);
        set(7, collmissid);
        set(8, genus);
        set(9, species);
        set(10, spauthor);
        set(11, subtaxa);
        set(12, subtauthor);
        set(13, cropname);
        set(14, accename);
        set(15, acqdate);
        set(16, origcty);
        set(17, collsite);
        set(18, declatitude);
        set(19, latitude);
        set(20, declongitude);
        set(21, longitude);
        set(22, coorduncert);
        set(23, coorddatum);
        set(24, georefmeth);
        set(25, elevation);
        set(26, colldate);
        set(27, bredcode);
        set(28, bredname);
        set(29, sampstat);
        set(30, ancest);
        set(31, collsrc);
        set(32, donorcode);
        set(33, donorname);
        set(34, donornumb);
        set(35, othernumb);
        set(36, duplsite);
        set(37, duplinstname);
        set(38, storage);
        set(39, mlsstat);
        set(40, remarks);
        set(41, entityType);
        set(42, entityParentAccenumb);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.PUID</code>. Any persistent, unique identifier assigned to the accession so it can be unambiguously referenced at the global level and the information associated with it harvested through automated means. Report one PUID for each accession.
     */
    public String getPuid() {
        return (String) get(0);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.PUID</code>. Any persistent, unique identifier assigned to the accession so it can be unambiguously referenced at the global level and the information associated with it harvested through automated means. Report one PUID for each accession.
     */
    public void setPuid(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.INSTCODE</code>. If there is a defined ISO code for the institute this should be used here.
     */
    public String getInstcode() {
        return (String) get(1);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.INSTCODE</code>. If there is a defined ISO code for the institute this should be used here.
     */
    public void setInstcode(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.ACCENUMB</code>. A unique identifier.
     */
    public String getAccenumb() {
        return (String) get(2);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.ACCENUMB</code>. A unique identifier.
     */
    public void setAccenumb(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.COLLNUMB</code>. Original identifier assigned by the collector(s) of the sample, normally composed of the name or
initials of the collector(s) followed by a number (e.g. ‘FM9909’). This identifier is essential for
identifying duplicates held in different collections.
     */
    public String getCollnumb() {
        return (String) get(3);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.COLLNUMB</code>. Original identifier assigned by the collector(s) of the sample, normally composed of the name or
initials of the collector(s) followed by a number (e.g. ‘FM9909’). This identifier is essential for
identifying duplicates held in different collections.
     */
    public void setCollnumb(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.COLLCODE</code>. FAO WIEWS code of the institute collecting the sample. If the holding institute has collected the
material, the collecting institute code (COLLCODE) should be the same as the holding institute
code (INSTCODE). Follows INSTCODE standard. Multiple values are separated by a semicolon
without space.
     */
    public String getCollcode() {
        return (String) get(4);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.COLLCODE</code>. FAO WIEWS code of the institute collecting the sample. If the holding institute has collected the
material, the collecting institute code (COLLCODE) should be the same as the holding institute
code (INSTCODE). Follows INSTCODE standard. Multiple values are separated by a semicolon
without space.
     */
    public void setCollcode(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.COLLNAME</code>. Name of the institute collecting the sample. This descriptor should be used only if COLLCODE cannot be filled because the FAO WIEWS code for this institute is not available. Multiple values are separated by a semicolon without space.
     */
    public String getCollname() {
        return (String) get(5);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.COLLNAME</code>. Name of the institute collecting the sample. This descriptor should be used only if COLLCODE cannot be filled because the FAO WIEWS code for this institute is not available. Multiple values are separated by a semicolon without space.
     */
    public void setCollname(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.COLLINSTADDRESS</code>. The postal address of the institute.
     */
    public String getCollinstaddress() {
        return (String) get(6);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.COLLINSTADDRESS</code>. The postal address of the institute.
     */
    public void setCollinstaddress(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.COLLMISSID</code>. Identifier of the collecting mission used by the Collecting Institute (4 or 4.1) (e.g. 'CIATFOR-052', 'CN426').
     */
    public String getCollmissid() {
        return (String) get(7);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.COLLMISSID</code>. Identifier of the collecting mission used by the Collecting Institute (4 or 4.1) (e.g. 'CIATFOR-052', 'CN426').
     */
    public void setCollmissid(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.GENUS</code>. Genus name for the species.
     */
    public String getGenus() {
        return (String) get(8);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.GENUS</code>. Genus name for the species.
     */
    public void setGenus(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.SPECIES</code>. Species name in lowercase.
     */
    public String getSpecies() {
        return (String) get(9);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.SPECIES</code>. Species name in lowercase.
     */
    public void setSpecies(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.SPAUTHOR</code>. also known as spauthor in the Multi Crop Passport Descriptors (MCPD V2 2012). Describes the authority for the species name.
     */
    public String getSpauthor() {
        return (String) get(10);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.SPAUTHOR</code>. also known as spauthor in the Multi Crop Passport Descriptors (MCPD V2 2012). Describes the authority for the species name.
     */
    public void setSpauthor(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.SUBTAXA</code>. Subtaxa name.
     */
    public String getSubtaxa() {
        return (String) get(11);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.SUBTAXA</code>. Subtaxa name.
     */
    public void setSubtaxa(String value) {
        set(11, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.SUBTAUTHOR</code>. also known as subtauthor in the Multi Crop Passport Descriptors (MCPD V2 2012).
     */
    public String getSubtauthor() {
        return (String) get(12);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.SUBTAUTHOR</code>. also known as subtauthor in the Multi Crop Passport Descriptors (MCPD V2 2012).
     */
    public void setSubtauthor(String value) {
        set(12, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.CROPNAME</code>. The name of the crop. This should be the common name. Examples would include barley, maize, wheat, rice and so on.
     */
    public String getCropname() {
        return (String) get(13);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.CROPNAME</code>. The name of the crop. This should be the common name. Examples would include barley, maize, wheat, rice and so on.
     */
    public void setCropname(String value) {
        set(13, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.ACCENAME</code>. A unique name which defines an entry in the germinatbase table.
     */
    public String getAccename() {
        return (String) get(14);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.ACCENAME</code>. A unique name which defines an entry in the germinatbase table.
     */
    public void setAccename(String value) {
        set(14, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.ACQDATE</code>.
     */
    public String getAcqdate() {
        return (String) get(15);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.ACQDATE</code>.
     */
    public void setAcqdate(String value) {
        set(15, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.ORIGCTY</code>. ISO 3 Code for country.
     */
    public String getOrigcty() {
        return (String) get(16);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.ORIGCTY</code>. ISO 3 Code for country.
     */
    public void setOrigcty(String value) {
        set(16, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.COLLSITE</code>. The site name where the location is.
     */
    public String getCollsite() {
        return (String) get(17);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.COLLSITE</code>. The site name where the location is.
     */
    public void setCollsite(String value) {
        set(17, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.DECLATITUDE</code>. Latitude of the location.
     */
    public BigDecimal getDeclatitude() {
        return (BigDecimal) get(18);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.DECLATITUDE</code>. Latitude of the location.
     */
    public void setDeclatitude(BigDecimal value) {
        set(18, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.LATITUDE</code>.
     */
    public byte[] getLatitude() {
        return (byte[]) get(19);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.LATITUDE</code>.
     */
    public void setLatitude(byte... value) {
        set(19, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.DECLONGITUDE</code>. Longitude of the location.
     */
    public BigDecimal getDeclongitude() {
        return (BigDecimal) get(20);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.DECLONGITUDE</code>. Longitude of the location.
     */
    public void setDeclongitude(BigDecimal value) {
        set(20, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.LONGITUDE</code>.
     */
    public byte[] getLongitude() {
        return (byte[]) get(21);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.LONGITUDE</code>.
     */
    public void setLongitude(byte... value) {
        set(21, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.COORDUNCERT</code>. Uncertainty associated with the coordinates in metres. Leave the value empty if the uncertainty is unknown.
     */
    public Integer getCoorduncert() {
        return (Integer) get(22);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.COORDUNCERT</code>. Uncertainty associated with the coordinates in metres. Leave the value empty if the uncertainty is unknown.
     */
    public void setCoorduncert(Integer value) {
        set(22, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.COORDDATUM</code>. The geodetic datum or spatial reference system upon which the coordinates given in decimal latitude and decimal longitude are based (e.g. WGS84, ETRS89, NAD83). The GPS uses the WGS84 datum.
     */
    public String getCoorddatum() {
        return (String) get(23);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.COORDDATUM</code>. The geodetic datum or spatial reference system upon which the coordinates given in decimal latitude and decimal longitude are based (e.g. WGS84, ETRS89, NAD83). The GPS uses the WGS84 datum.
     */
    public void setCoorddatum(String value) {
        set(23, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.GEOREFMETH</code>. The georeferencing method used (GPS, determined from map, gazetteer, or estimated using software). Leave the value empty if georeferencing method is not known.
     */
    public String getGeorefmeth() {
        return (String) get(24);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.GEOREFMETH</code>. The georeferencing method used (GPS, determined from map, gazetteer, or estimated using software). Leave the value empty if georeferencing method is not known.
     */
    public void setGeorefmeth(String value) {
        set(24, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.ELEVATION</code>. The elevation of the site in metres.
     */
    public BigDecimal getElevation() {
        return (BigDecimal) get(25);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.ELEVATION</code>. The elevation of the site in metres.
     */
    public void setElevation(BigDecimal value) {
        set(25, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.COLLDATE</code>.
     */
    public String getColldate() {
        return (String) get(26);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.COLLDATE</code>.
     */
    public void setColldate(String value) {
        set(26, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.BREDCODE</code>. FAO WIEWS code of the institute that has bred the material. If the holding institute has bred the material, the breeding institute code (BREDCODE) should be the same as the holding institute code (INSTCODE). Follows INSTCODE standard. Multiple values are separated by a semicolon without space.
     */
    public String getBredcode() {
        return (String) get(27);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.BREDCODE</code>. FAO WIEWS code of the institute that has bred the material. If the holding institute has bred the material, the breeding institute code (BREDCODE) should be the same as the holding institute code (INSTCODE). Follows INSTCODE standard. Multiple values are separated by a semicolon without space.
     */
    public void setBredcode(String value) {
        set(27, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.BREDNAME</code>. Name of the institute (or person) that bred the material. This descriptor should be used only if BREDCODE cannot be filled because the FAO WIEWS code for this institute is not available. Multiple names are separated by a semicolon without space.
     */
    public String getBredname() {
        return (String) get(28);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.BREDNAME</code>. Name of the institute (or person) that bred the material. This descriptor should be used only if BREDCODE cannot be filled because the FAO WIEWS code for this institute is not available. Multiple names are separated by a semicolon without space.
     */
    public void setBredname(String value) {
        set(28, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.SAMPSTAT</code>. Foreign key to biologicalstatus (biologicalstaus.id).
     */
    public Integer getSampstat() {
        return (Integer) get(29);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.SAMPSTAT</code>. Foreign key to biologicalstatus (biologicalstaus.id).
     */
    public void setSampstat(Integer value) {
        set(29, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.ANCEST</code>. The pedigree string which is used to represent the germinatebase entry.
     */
    public String getAncest() {
        return (String) get(30);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.ANCEST</code>. The pedigree string which is used to represent the germinatebase entry.
     */
    public void setAncest(String value) {
        set(30, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.COLLSRC</code>. Foreign key to collectionsources (collectionsources.id).
     */
    public Integer getCollsrc() {
        return (Integer) get(31);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.COLLSRC</code>. Foreign key to collectionsources (collectionsources.id).
     */
    public void setCollsrc(Integer value) {
        set(31, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.DONORCODE</code>. FAO WIEWS code of the donor institute. Follows INSTCODE standard.
     */
    public String getDonorcode() {
        return (String) get(32);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.DONORCODE</code>. FAO WIEWS code of the donor institute. Follows INSTCODE standard.
     */
    public void setDonorcode(String value) {
        set(32, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.DONORNAME</code>. Name of the donor institute (or person). This descriptor should be used only if DONORCODE cannot be filled because the FAO WIEWS code for this institute is not available.
     */
    public String getDonorname() {
        return (String) get(33);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.DONORNAME</code>. Name of the donor institute (or person). This descriptor should be used only if DONORCODE cannot be filled because the FAO WIEWS code for this institute is not available.
     */
    public void setDonorname(String value) {
        set(33, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.DONORNUMB</code>. Identifier assigned to an accession by the donor. Follows ACCENUMB standard.
     */
    public String getDonornumb() {
        return (String) get(34);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.DONORNUMB</code>. Identifier assigned to an accession by the donor. Follows ACCENUMB standard.
     */
    public void setDonornumb(String value) {
        set(34, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.OTHERNUMB</code>. Any other identifiers known to exist in other collections for this accession. Use the following format: INSTCODE:ACCENUMB;INSTCODE:identifier;… INSTCODE and identifier are separated by a colon without space. Pairs of INSTCODE and identifier are separated by a semicolon without space. When the institute is not known, the identifier should be preceded by a colon.
     */
    public String getOthernumb() {
        return (String) get(35);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.OTHERNUMB</code>. Any other identifiers known to exist in other collections for this accession. Use the following format: INSTCODE:ACCENUMB;INSTCODE:identifier;… INSTCODE and identifier are separated by a colon without space. Pairs of INSTCODE and identifier are separated by a semicolon without space. When the institute is not known, the identifier should be preceded by a colon.
     */
    public void setOthernumb(String value) {
        set(35, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.DUPLSITE</code>. FAO WIEWS code of the institute(s) where a safety duplicate of the accession is maintained.
Multiple values are separated by a semicolon without space. Follows INSTCODE standard.
     */
    public String getDuplsite() {
        return (String) get(36);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.DUPLSITE</code>. FAO WIEWS code of the institute(s) where a safety duplicate of the accession is maintained.
Multiple values are separated by a semicolon without space. Follows INSTCODE standard.
     */
    public void setDuplsite(String value) {
        set(36, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.DUPLINSTNAME</code>. Name of the institute where a safety duplicate of the accession is maintained. Multiple values are separated by a semicolon without space.
     */
    public String getDuplinstname() {
        return (String) get(37);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.DUPLINSTNAME</code>. Name of the institute where a safety duplicate of the accession is maintained. Multiple values are separated by a semicolon without space.
     */
    public void setDuplinstname(String value) {
        set(37, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.STORAGE</code>.
     */
    public String getStorage() {
        return (String) get(38);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.STORAGE</code>.
     */
    public void setStorage(String value) {
        set(38, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.MLSSTAT</code>. Foreign key to mlsstatus (mlsstatus.id).
     */
    public Integer getMlsstat() {
        return (Integer) get(39);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.MLSSTAT</code>. Foreign key to mlsstatus (mlsstatus.id).
     */
    public void setMlsstat(Integer value) {
        set(39, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.REMARKS</code>.
     */
    public String getRemarks() {
        return (String) get(40);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.REMARKS</code>.
     */
    public void setRemarks(String value) {
        set(40, value);
    }

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.Entity Type</code>.
     */
    public String getEntityType() {
        return (String) get(41);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.Entity Type</code>.
     */
    public void setEntityType(String value) {
        set(41, value);
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Getter for <code>germinate_template_3_6_0.view_mcpd.Entity parent ACCENUMB</code>.
     */
    public byte[] getEntityParentAccenumb() {
        return (byte[]) get(42);
    }

    /**
     * Setter for <code>germinate_template_3_6_0.view_mcpd.Entity parent ACCENUMB</code>.
     */
    public void setEntityParentAccenumb(byte... value) {
        set(42, value);
    }
}
