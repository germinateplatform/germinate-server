package jhi.germinate.resource;

import jhi.germinate.server.database.pojo.GermplasmInstitution;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * @author Sebastian Raubach
 */
public class ViewTableGermplasm
{
	private String                 germplasmName;
	private Integer                germplasmId;
	private String                 germplasmGid;
	private String                 germplasmNumber;
	private String                 germplasmPuid;
	private Integer                entityTypeId;
	private String                 entityTypeName;
	private Integer                entityParentId;
	private String                 entityParentName;
	private String                 entityParentGeneralIdentifier;
	private Integer                biologicalStatusId;
	private String                 biologicalStatusName;
	private String[]               synonyms;
	private String                 collectorNumber;
	private String                 genus;
	private String                 species;
	private String                 subtaxa;
	private GermplasmInstitution[] institutions;
	private Integer                locationId;
	private String                 location;
	private BigDecimal             latitude;
	private BigDecimal             longitude;
	private BigDecimal             elevation;
	private String                 countryName;
	private String                 countryCode;
	private Date                   collDate;
	private Double                 pdci;
	private Long                   imageCount;
	private String                 firstImagePath;
	private Integer                hasTrialsData;
	private Integer                hasGenotypicData;
	private Integer                hasAllelefreqData;
	private Integer                hasCompoundData;
	private Integer                hasPedigreeData;

	public ViewTableGermplasm()
	{
	}

	public String getGermplasmName()
	{
		return germplasmName;
	}

	public ViewTableGermplasm setGermplasmName(String germplasmName)
	{
		this.germplasmName = germplasmName;
		return this;
	}

	public Integer getGermplasmId()
	{
		return germplasmId;
	}

	public ViewTableGermplasm setGermplasmId(Integer germplasmId)
	{
		this.germplasmId = germplasmId;
		return this;
	}

	public String getGermplasmGid()
	{
		return germplasmGid;
	}

	public ViewTableGermplasm setGermplasmGid(String germplasmGid)
	{
		this.germplasmGid = germplasmGid;
		return this;
	}

	public String getGermplasmNumber()
	{
		return germplasmNumber;
	}

	public ViewTableGermplasm setGermplasmNumber(String germplasmNumber)
	{
		this.germplasmNumber = germplasmNumber;
		return this;
	}

	public String getGermplasmPuid()
	{
		return germplasmPuid;
	}

	public ViewTableGermplasm setGermplasmPuid(String germplasmPuid)
	{
		this.germplasmPuid = germplasmPuid;
		return this;
	}

	public Integer getEntityTypeId()
	{
		return entityTypeId;
	}

	public ViewTableGermplasm setEntityTypeId(Integer entityTypeId)
	{
		this.entityTypeId = entityTypeId;
		return this;
	}

	public String getEntityTypeName()
	{
		return entityTypeName;
	}

	public ViewTableGermplasm setEntityTypeName(String entityTypeName)
	{
		this.entityTypeName = entityTypeName;
		return this;
	}

	public Integer getEntityParentId()
	{
		return entityParentId;
	}

	public ViewTableGermplasm setEntityParentId(Integer entityParentId)
	{
		this.entityParentId = entityParentId;
		return this;
	}

	public String getEntityParentName()
	{
		return entityParentName;
	}

	public ViewTableGermplasm setEntityParentName(String entityParentName)
	{
		this.entityParentName = entityParentName;
		return this;
	}

	public String getEntityParentGeneralIdentifier()
	{
		return entityParentGeneralIdentifier;
	}

	public ViewTableGermplasm setEntityParentGeneralIdentifier(String entityParentGeneralIdentifier)
	{
		this.entityParentGeneralIdentifier = entityParentGeneralIdentifier;
		return this;
	}

	public Integer getBiologicalStatusId()
	{
		return biologicalStatusId;
	}

	public ViewTableGermplasm setBiologicalStatusId(Integer biologicalStatusId)
	{
		this.biologicalStatusId = biologicalStatusId;
		return this;
	}

	public String getBiologicalStatusName()
	{
		return biologicalStatusName;
	}

	public ViewTableGermplasm setBiologicalStatusName(String biologicalStatusName)
	{
		this.biologicalStatusName = biologicalStatusName;
		return this;
	}

	public String[] getSynonyms()
	{
		return synonyms;
	}

	public ViewTableGermplasm setSynonyms(String[] synonyms)
	{
		this.synonyms = synonyms;
		return this;
	}

	public String getCollectorNumber()
	{
		return collectorNumber;
	}

	public ViewTableGermplasm setCollectorNumber(String collectorNumber)
	{
		this.collectorNumber = collectorNumber;
		return this;
	}

	public String getGenus()
	{
		return genus;
	}

	public ViewTableGermplasm setGenus(String genus)
	{
		this.genus = genus;
		return this;
	}

	public String getSpecies()
	{
		return species;
	}

	public ViewTableGermplasm setSpecies(String species)
	{
		this.species = species;
		return this;
	}

	public String getSubtaxa()
	{
		return subtaxa;
	}

	public ViewTableGermplasm setSubtaxa(String subtaxa)
	{
		this.subtaxa = subtaxa;
		return this;
	}

	public GermplasmInstitution[] getInstitutions()
	{
		return institutions;
	}

	public ViewTableGermplasm setInstitutions(GermplasmInstitution[] institutions)
	{
		this.institutions = institutions;
		return this;
	}

	public Integer getLocationId()
	{
		return locationId;
	}

	public ViewTableGermplasm setLocationId(Integer locationId)
	{
		this.locationId = locationId;
		return this;
	}

	public String getLocation()
	{
		return location;
	}

	public ViewTableGermplasm setLocation(String location)
	{
		this.location = location;
		return this;
	}

	public BigDecimal getLatitude()
	{
		return latitude;
	}

	public ViewTableGermplasm setLatitude(BigDecimal latitude)
	{
		this.latitude = latitude;
		return this;
	}

	public BigDecimal getLongitude()
	{
		return longitude;
	}

	public ViewTableGermplasm setLongitude(BigDecimal longitude)
	{
		this.longitude = longitude;
		return this;
	}

	public BigDecimal getElevation()
	{
		return elevation;
	}

	public ViewTableGermplasm setElevation(BigDecimal elevation)
	{
		this.elevation = elevation;
		return this;
	}

	public String getCountryName()
	{
		return countryName;
	}

	public ViewTableGermplasm setCountryName(String countryName)
	{
		this.countryName = countryName;
		return this;
	}

	public String getCountryCode()
	{
		return countryCode;
	}

	public ViewTableGermplasm setCountryCode(String countryCode)
	{
		this.countryCode = countryCode;
		return this;
	}

	public Date getCollDate()
	{
		return collDate;
	}

	public ViewTableGermplasm setCollDate(Date collDate)
	{
		this.collDate = collDate;
		return this;
	}

	public Double getPdci()
	{
		return pdci;
	}

	public ViewTableGermplasm setPdci(Double pdci)
	{
		this.pdci = pdci;
		return this;
	}

	public Long getImageCount()
	{
		return imageCount;
	}

	public ViewTableGermplasm setImageCount(Long imageCount)
	{
		this.imageCount = imageCount;
		return this;
	}

	public String getFirstImagePath()
	{
		return firstImagePath;
	}

	public ViewTableGermplasm setFirstImagePath(String firstImagePath)
	{
		this.firstImagePath = firstImagePath;
		return this;
	}

	public Integer getHasTrialsData()
	{
		return hasTrialsData;
	}

	public ViewTableGermplasm setHasTrialsData(Integer hasTrialsData)
	{
		this.hasTrialsData = hasTrialsData;
		return this;
	}

	public Integer getHasGenotypicData()
	{
		return hasGenotypicData;
	}

	public ViewTableGermplasm setHasGenotypicData(Integer hasGenotypicData)
	{
		this.hasGenotypicData = hasGenotypicData;
		return this;
	}

	public Integer getHasAllelefreqData()
	{
		return hasAllelefreqData;
	}

	public ViewTableGermplasm setHasAllelefreqData(Integer hasAllelefreqData)
	{
		this.hasAllelefreqData = hasAllelefreqData;
		return this;
	}

	public Integer getHasCompoundData()
	{
		return hasCompoundData;
	}

	public ViewTableGermplasm setHasCompoundData(Integer hasCompoundData)
	{
		this.hasCompoundData = hasCompoundData;
		return this;
	}

	public Integer getHasPedigreeData()
	{
		return hasPedigreeData;
	}

	public ViewTableGermplasm setHasPedigreeData(Integer hasPedigreeData)
	{
		this.hasPedigreeData = hasPedigreeData;
		return this;
	}
}
