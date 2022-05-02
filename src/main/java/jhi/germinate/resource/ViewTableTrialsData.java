package jhi.germinate.resource;

import jhi.germinate.server.database.pojo.TraitRestrictions;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class ViewTableTrialsData
{
	private Integer           germplasmId;
	private String            germplasmGid;
	private String            germplasmName;
	private String[]          germplasmSynonyms;
	private String            entityParentName;
	private String            entityParentGeneralIdentifier;
	private String            entityType;
	private Integer           datasetId;
	private String            datasetName;
	private String            datasetDescription;
	private String            locationName;
	private String            countryName;
	private String            countryCode2;
	private Integer           traitId;
	private String            traitName;
	private String            traitNameShort;
	private TraitRestrictions traitRestrictions;
	private String            unitName;
	private String            treatment;
	private String            rep;
	private String            block;
	private BigDecimal        latitude;
	private BigDecimal        longitude;
	private BigDecimal        elevation;
	private Timestamp         recordingDate;
	private String            traitValue;

	public Integer getGermplasmId()
	{
		return germplasmId;
	}

	public ViewTableTrialsData setGermplasmId(Integer germplasmId)
	{
		this.germplasmId = germplasmId;
		return this;
	}

	public String getGermplasmGid()
	{
		return germplasmGid;
	}

	public ViewTableTrialsData setGermplasmGid(String germplasmGid)
	{
		this.germplasmGid = germplasmGid;
		return this;
	}

	public String getGermplasmName()
	{
		return germplasmName;
	}

	public ViewTableTrialsData setGermplasmName(String germplasmName)
	{
		this.germplasmName = germplasmName;
		return this;
	}

	public String[] getGermplasmSynonyms()
	{
		return germplasmSynonyms;
	}

	public ViewTableTrialsData setGermplasmSynonyms(String[] germplasmSynonyms)
	{
		this.germplasmSynonyms = germplasmSynonyms;
		return this;
	}

	public String getEntityParentName()
	{
		return entityParentName;
	}

	public ViewTableTrialsData setEntityParentName(String entityParentName)
	{
		this.entityParentName = entityParentName;
		return this;
	}

	public String getEntityParentGeneralIdentifier()
	{
		return entityParentGeneralIdentifier;
	}

	public ViewTableTrialsData setEntityParentGeneralIdentifier(String entityParentGeneralIdentifier)
	{
		this.entityParentGeneralIdentifier = entityParentGeneralIdentifier;
		return this;
	}

	public String getEntityType()
	{
		return entityType;
	}

	public ViewTableTrialsData setEntityType(String entityType)
	{
		this.entityType = entityType;
		return this;
	}

	public Integer getDatasetId()
	{
		return datasetId;
	}

	public ViewTableTrialsData setDatasetId(Integer datasetId)
	{
		this.datasetId = datasetId;
		return this;
	}

	public String getDatasetName()
	{
		return datasetName;
	}

	public ViewTableTrialsData setDatasetName(String datasetName)
	{
		this.datasetName = datasetName;
		return this;
	}

	public String getDatasetDescription()
	{
		return datasetDescription;
	}

	public ViewTableTrialsData setDatasetDescription(String datasetDescription)
	{
		this.datasetDescription = datasetDescription;
		return this;
	}

	public String getLocationName()
	{
		return locationName;
	}

	public ViewTableTrialsData setLocationName(String locationName)
	{
		this.locationName = locationName;
		return this;
	}

	public String getCountryName()
	{
		return countryName;
	}

	public ViewTableTrialsData setCountryName(String countryName)
	{
		this.countryName = countryName;
		return this;
	}

	public String getCountryCode2()
	{
		return countryCode2;
	}

	public ViewTableTrialsData setCountryCode2(String countryCode2)
	{
		this.countryCode2 = countryCode2;
		return this;
	}

	public Integer getTraitId()
	{
		return traitId;
	}

	public ViewTableTrialsData setTraitId(Integer traitId)
	{
		this.traitId = traitId;
		return this;
	}

	public String getTraitName()
	{
		return traitName;
	}

	public ViewTableTrialsData setTraitName(String traitName)
	{
		this.traitName = traitName;
		return this;
	}

	public String getTraitNameShort()
	{
		return traitNameShort;
	}

	public ViewTableTrialsData setTraitNameShort(String traitNameShort)
	{
		this.traitNameShort = traitNameShort;
		return this;
	}

	public TraitRestrictions getTraitRestrictions()
	{
		return traitRestrictions;
	}

	public ViewTableTrialsData setTraitRestrictions(TraitRestrictions traitRestrictions)
	{
		this.traitRestrictions = traitRestrictions;
		return this;
	}

	public String getUnitName()
	{
		return unitName;
	}

	public ViewTableTrialsData setUnitName(String unitName)
	{
		this.unitName = unitName;
		return this;
	}

	public String getTreatment()
	{
		return treatment;
	}

	public ViewTableTrialsData setTreatment(String treatment)
	{
		this.treatment = treatment;
		return this;
	}

	public String getRep()
	{
		return rep;
	}

	public ViewTableTrialsData setRep(String rep)
	{
		this.rep = rep;
		return this;
	}

	public Timestamp getRecordingDate()
	{
		return recordingDate;
	}

	public ViewTableTrialsData setRecordingDate(Timestamp recordingDate)
	{
		this.recordingDate = recordingDate;
		return this;
	}

	public String getTraitValue()
	{
		return traitValue;
	}

	public ViewTableTrialsData setTraitValue(String traitValue)
	{
		this.traitValue = traitValue;
		return this;
	}

	public String getBlock()
	{
		return block;
	}

	public ViewTableTrialsData setBlock(String block)
	{
		this.block = block;
		return this;
	}

	public BigDecimal getLatitude()
	{
		return latitude;
	}

	public ViewTableTrialsData setLatitude(BigDecimal latitude)
	{
		this.latitude = latitude;
		return this;
	}

	public BigDecimal getLongitude()
	{
		return longitude;
	}

	public ViewTableTrialsData setLongitude(BigDecimal longitude)
	{
		this.longitude = longitude;
		return this;
	}

	public BigDecimal getElevation()
	{
		return elevation;
	}

	public ViewTableTrialsData setElevation(BigDecimal elevation)
	{
		this.elevation = elevation;
		return this;
	}
}
