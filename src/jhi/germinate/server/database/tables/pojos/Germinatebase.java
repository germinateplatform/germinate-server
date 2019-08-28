/*
 * This file is generated by jOOQ.
 */
package jhi.germinate.server.database.tables.pojos;


import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

import javax.annotation.Generated;


// @formatter:off
/**
 * Germinatebase is the Germinate base table which contains passport and other 
 * germplasm definition data.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Germinatebase implements Serializable {

    private static final long serialVersionUID = -602147695;

    private Integer   id;
    private String    generalIdentifier;
    private String    number;
    private String    name;
    private String    bankNumber;
    private String    breedersCode;
    private String    breedersName;
    private Integer   taxonomyId;
    private Integer   institutionId;
    private String    plantPassport;
    private String    donorCode;
    private String    donorName;
    private String    donorNumber;
    private String    acqdate;
    private String    collnumb;
    private Date      colldate;
    private String    collcode;
    private String    collname;
    private String    collmissid;
    private String    othernumb;
    private String    duplsite;
    private String    duplinstname;
    private Integer   mlsstatusId;
    private String    puid;
    private Integer   biologicalstatusId;
    private Integer   collsrcId;
    private Integer   locationId;
    private Integer   entitytypeId;
    private Integer   entityparentId;
    private Double    pdci;
    private Timestamp createdOn;
    private Timestamp updatedOn;

    public Germinatebase() {}

    public Germinatebase(Germinatebase value) {
        this.id = value.id;
        this.generalIdentifier = value.generalIdentifier;
        this.number = value.number;
        this.name = value.name;
        this.bankNumber = value.bankNumber;
        this.breedersCode = value.breedersCode;
        this.breedersName = value.breedersName;
        this.taxonomyId = value.taxonomyId;
        this.institutionId = value.institutionId;
        this.plantPassport = value.plantPassport;
        this.donorCode = value.donorCode;
        this.donorName = value.donorName;
        this.donorNumber = value.donorNumber;
        this.acqdate = value.acqdate;
        this.collnumb = value.collnumb;
        this.colldate = value.colldate;
        this.collcode = value.collcode;
        this.collname = value.collname;
        this.collmissid = value.collmissid;
        this.othernumb = value.othernumb;
        this.duplsite = value.duplsite;
        this.duplinstname = value.duplinstname;
        this.mlsstatusId = value.mlsstatusId;
        this.puid = value.puid;
        this.biologicalstatusId = value.biologicalstatusId;
        this.collsrcId = value.collsrcId;
        this.locationId = value.locationId;
        this.entitytypeId = value.entitytypeId;
        this.entityparentId = value.entityparentId;
        this.pdci = value.pdci;
        this.createdOn = value.createdOn;
        this.updatedOn = value.updatedOn;
    }

    public Germinatebase(
        Integer   id,
        String    generalIdentifier,
        String    number,
        String    name,
        String    bankNumber,
        String    breedersCode,
        String    breedersName,
        Integer   taxonomyId,
        Integer   institutionId,
        String    plantPassport,
        String    donorCode,
        String    donorName,
        String    donorNumber,
        String    acqdate,
        String    collnumb,
        Date      colldate,
        String    collcode,
        String    collname,
        String    collmissid,
        String    othernumb,
        String    duplsite,
        String    duplinstname,
        Integer   mlsstatusId,
        String    puid,
        Integer   biologicalstatusId,
        Integer   collsrcId,
        Integer   locationId,
        Integer   entitytypeId,
        Integer   entityparentId,
        Double    pdci,
        Timestamp createdOn,
        Timestamp updatedOn
    ) {
        this.id = id;
        this.generalIdentifier = generalIdentifier;
        this.number = number;
        this.name = name;
        this.bankNumber = bankNumber;
        this.breedersCode = breedersCode;
        this.breedersName = breedersName;
        this.taxonomyId = taxonomyId;
        this.institutionId = institutionId;
        this.plantPassport = plantPassport;
        this.donorCode = donorCode;
        this.donorName = donorName;
        this.donorNumber = donorNumber;
        this.acqdate = acqdate;
        this.collnumb = collnumb;
        this.colldate = colldate;
        this.collcode = collcode;
        this.collname = collname;
        this.collmissid = collmissid;
        this.othernumb = othernumb;
        this.duplsite = duplsite;
        this.duplinstname = duplinstname;
        this.mlsstatusId = mlsstatusId;
        this.puid = puid;
        this.biologicalstatusId = biologicalstatusId;
        this.collsrcId = collsrcId;
        this.locationId = locationId;
        this.entitytypeId = entitytypeId;
        this.entityparentId = entityparentId;
        this.pdci = pdci;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGeneralIdentifier() {
        return this.generalIdentifier;
    }

    public void setGeneralIdentifier(String generalIdentifier) {
        this.generalIdentifier = generalIdentifier;
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBankNumber() {
        return this.bankNumber;
    }

    public void setBankNumber(String bankNumber) {
        this.bankNumber = bankNumber;
    }

    public String getBreedersCode() {
        return this.breedersCode;
    }

    public void setBreedersCode(String breedersCode) {
        this.breedersCode = breedersCode;
    }

    public String getBreedersName() {
        return this.breedersName;
    }

    public void setBreedersName(String breedersName) {
        this.breedersName = breedersName;
    }

    public Integer getTaxonomyId() {
        return this.taxonomyId;
    }

    public void setTaxonomyId(Integer taxonomyId) {
        this.taxonomyId = taxonomyId;
    }

    public Integer getInstitutionId() {
        return this.institutionId;
    }

    public void setInstitutionId(Integer institutionId) {
        this.institutionId = institutionId;
    }

    public String getPlantPassport() {
        return this.plantPassport;
    }

    public void setPlantPassport(String plantPassport) {
        this.plantPassport = plantPassport;
    }

    public String getDonorCode() {
        return this.donorCode;
    }

    public void setDonorCode(String donorCode) {
        this.donorCode = donorCode;
    }

    public String getDonorName() {
        return this.donorName;
    }

    public void setDonorName(String donorName) {
        this.donorName = donorName;
    }

    public String getDonorNumber() {
        return this.donorNumber;
    }

    public void setDonorNumber(String donorNumber) {
        this.donorNumber = donorNumber;
    }

    public String getAcqdate() {
        return this.acqdate;
    }

    public void setAcqdate(String acqdate) {
        this.acqdate = acqdate;
    }

    public String getCollnumb() {
        return this.collnumb;
    }

    public void setCollnumb(String collnumb) {
        this.collnumb = collnumb;
    }

    public Date getColldate() {
        return this.colldate;
    }

    public void setColldate(Date colldate) {
        this.colldate = colldate;
    }

    public String getCollcode() {
        return this.collcode;
    }

    public void setCollcode(String collcode) {
        this.collcode = collcode;
    }

    public String getCollname() {
        return this.collname;
    }

    public void setCollname(String collname) {
        this.collname = collname;
    }

    public String getCollmissid() {
        return this.collmissid;
    }

    public void setCollmissid(String collmissid) {
        this.collmissid = collmissid;
    }

    public String getOthernumb() {
        return this.othernumb;
    }

    public void setOthernumb(String othernumb) {
        this.othernumb = othernumb;
    }

    public String getDuplsite() {
        return this.duplsite;
    }

    public void setDuplsite(String duplsite) {
        this.duplsite = duplsite;
    }

    public String getDuplinstname() {
        return this.duplinstname;
    }

    public void setDuplinstname(String duplinstname) {
        this.duplinstname = duplinstname;
    }

    public Integer getMlsstatusId() {
        return this.mlsstatusId;
    }

    public void setMlsstatusId(Integer mlsstatusId) {
        this.mlsstatusId = mlsstatusId;
    }

    public String getPuid() {
        return this.puid;
    }

    public void setPuid(String puid) {
        this.puid = puid;
    }

    public Integer getBiologicalstatusId() {
        return this.biologicalstatusId;
    }

    public void setBiologicalstatusId(Integer biologicalstatusId) {
        this.biologicalstatusId = biologicalstatusId;
    }

    public Integer getCollsrcId() {
        return this.collsrcId;
    }

    public void setCollsrcId(Integer collsrcId) {
        this.collsrcId = collsrcId;
    }

    public Integer getLocationId() {
        return this.locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getEntitytypeId() {
        return this.entitytypeId;
    }

    public void setEntitytypeId(Integer entitytypeId) {
        this.entitytypeId = entitytypeId;
    }

    public Integer getEntityparentId() {
        return this.entityparentId;
    }

    public void setEntityparentId(Integer entityparentId) {
        this.entityparentId = entityparentId;
    }

    public Double getPdci() {
        return this.pdci;
    }

    public void setPdci(Double pdci) {
        this.pdci = pdci;
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
        StringBuilder sb = new StringBuilder("Germinatebase (");

        sb.append(id);
        sb.append(", ").append(generalIdentifier);
        sb.append(", ").append(number);
        sb.append(", ").append(name);
        sb.append(", ").append(bankNumber);
        sb.append(", ").append(breedersCode);
        sb.append(", ").append(breedersName);
        sb.append(", ").append(taxonomyId);
        sb.append(", ").append(institutionId);
        sb.append(", ").append(plantPassport);
        sb.append(", ").append(donorCode);
        sb.append(", ").append(donorName);
        sb.append(", ").append(donorNumber);
        sb.append(", ").append(acqdate);
        sb.append(", ").append(collnumb);
        sb.append(", ").append(colldate);
        sb.append(", ").append(collcode);
        sb.append(", ").append(collname);
        sb.append(", ").append(collmissid);
        sb.append(", ").append(othernumb);
        sb.append(", ").append(duplsite);
        sb.append(", ").append(duplinstname);
        sb.append(", ").append(mlsstatusId);
        sb.append(", ").append(puid);
        sb.append(", ").append(biologicalstatusId);
        sb.append(", ").append(collsrcId);
        sb.append(", ").append(locationId);
        sb.append(", ").append(entitytypeId);
        sb.append(", ").append(entityparentId);
        sb.append(", ").append(pdci);
        sb.append(", ").append(createdOn);
        sb.append(", ").append(updatedOn);

        sb.append(")");
        return sb.toString();
    }
// @formatter:on
}
