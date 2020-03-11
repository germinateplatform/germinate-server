package jhi.germinate.resource;

import java.math.BigDecimal;

/**
 * @author Sebastian Raubach
 */
public class ViewMcpd
{
	private Integer    id;
	private String     puid;
	private String     instcode;
	private String     accenumb;
	private String     collnumb;
	private String     collcode;
	private String     collname;
	private String     collinstaddress;
	private String     collmissid;
	private String     genus;
	private String     species;
	private String     spauthor;
	private String     subtaxa;
	private String     subtauthor;
	private String     cropname;
	private String     accename;
	private String     acqdate;
	private String     origcty;
	private String     collsite;
	private BigDecimal declatitude;
	private byte[]     latitude;
	private BigDecimal declongitude;
	private byte[]     longitude;
	private Integer    coorduncert;
	private String     coorddatum;
	private String     georefmeth;
	private BigDecimal elevation;
	private String     colldate;
	private String     bredcode;
	private String     bredname;
	private Integer    sampstat;
	private String     ancest;
	private Integer    collsrc;
	private String     donorcode;
	private String     donorname;
	private String     donornumb;
	private String     othernumb;
	private String     duplsite;
	private String     duplinstname;
	private String     storage;
	private Integer    mlsstat;
	private String     remarks;
	private String     entitytype;
	private Long       entityparentid;
	private String     entityparentaccenumb;

	public ViewMcpd()
	{
	}

	public Integer getId()
	{
		return id;
	}

	public ViewMcpd setId(Integer id)
	{
		this.id = id;
		return this;
	}

	public String getPuid()
	{
		return puid;
	}

	public ViewMcpd setPuid(String puid)
	{
		this.puid = puid;
		return this;
	}

	public String getInstcode()
	{
		return instcode;
	}

	public ViewMcpd setInstcode(String instcode)
	{
		this.instcode = instcode;
		return this;
	}

	public String getAccenumb()
	{
		return accenumb;
	}

	public ViewMcpd setAccenumb(String accenumb)
	{
		this.accenumb = accenumb;
		return this;
	}

	public String getCollnumb()
	{
		return collnumb;
	}

	public ViewMcpd setCollnumb(String collnumb)
	{
		this.collnumb = collnumb;
		return this;
	}

	public String getCollcode()
	{
		return collcode;
	}

	public ViewMcpd setCollcode(String collcode)
	{
		this.collcode = collcode;
		return this;
	}

	public String getCollname()
	{
		return collname;
	}

	public ViewMcpd setCollname(String collname)
	{
		this.collname = collname;
		return this;
	}

	public String getCollinstaddress()
	{
		return collinstaddress;
	}

	public ViewMcpd setCollinstaddress(String collinstaddress)
	{
		this.collinstaddress = collinstaddress;
		return this;
	}

	public String getCollmissid()
	{
		return collmissid;
	}

	public ViewMcpd setCollmissid(String collmissid)
	{
		this.collmissid = collmissid;
		return this;
	}

	public String getGenus()
	{
		return genus;
	}

	public ViewMcpd setGenus(String genus)
	{
		this.genus = genus;
		return this;
	}

	public String getSpecies()
	{
		return species;
	}

	public ViewMcpd setSpecies(String species)
	{
		this.species = species;
		return this;
	}

	public String getSpauthor()
	{
		return spauthor;
	}

	public ViewMcpd setSpauthor(String spauthor)
	{
		this.spauthor = spauthor;
		return this;
	}

	public String getSubtaxa()
	{
		return subtaxa;
	}

	public ViewMcpd setSubtaxa(String subtaxa)
	{
		this.subtaxa = subtaxa;
		return this;
	}

	public String getSubtauthor()
	{
		return subtauthor;
	}

	public ViewMcpd setSubtauthor(String subtauthor)
	{
		this.subtauthor = subtauthor;
		return this;
	}

	public String getCropname()
	{
		return cropname;
	}

	public ViewMcpd setCropname(String cropname)
	{
		this.cropname = cropname;
		return this;
	}

	public String getAccename()
	{
		return accename;
	}

	public ViewMcpd setAccename(String accename)
	{
		this.accename = accename;
		return this;
	}

	public String getAcqdate()
	{
		return acqdate;
	}

	public ViewMcpd setAcqdate(String acqdate)
	{
		this.acqdate = acqdate;
		return this;
	}

	public String getOrigcty()
	{
		return origcty;
	}

	public ViewMcpd setOrigcty(String origcty)
	{
		this.origcty = origcty;
		return this;
	}

	public String getCollsite()
	{
		return collsite;
	}

	public ViewMcpd setCollsite(String collsite)
	{
		this.collsite = collsite;
		return this;
	}

	public BigDecimal getDeclatitude()
	{
		return declatitude;
	}

	public ViewMcpd setDeclatitude(BigDecimal declatitude)
	{
		this.declatitude = declatitude;
		return this;
	}

	public byte[] getLatitude()
	{
		return latitude;
	}

	public ViewMcpd setLatitude(byte[] latitude)
	{
		this.latitude = latitude;
		return this;
	}

	public BigDecimal getDeclongitude()
	{
		return declongitude;
	}

	public ViewMcpd setDeclongitude(BigDecimal declongitude)
	{
		this.declongitude = declongitude;
		return this;
	}

	public byte[] getLongitude()
	{
		return longitude;
	}

	public ViewMcpd setLongitude(byte[] longitude)
	{
		this.longitude = longitude;
		return this;
	}

	public Integer getCoorduncert()
	{
		return coorduncert;
	}

	public ViewMcpd setCoorduncert(Integer coorduncert)
	{
		this.coorduncert = coorduncert;
		return this;
	}

	public String getCoorddatum()
	{
		return coorddatum;
	}

	public ViewMcpd setCoorddatum(String coorddatum)
	{
		this.coorddatum = coorddatum;
		return this;
	}

	public String getGeorefmeth()
	{
		return georefmeth;
	}

	public ViewMcpd setGeorefmeth(String georefmeth)
	{
		this.georefmeth = georefmeth;
		return this;
	}

	public BigDecimal getElevation()
	{
		return elevation;
	}

	public ViewMcpd setElevation(BigDecimal elevation)
	{
		this.elevation = elevation;
		return this;
	}

	public String getColldate()
	{
		return colldate;
	}

	public ViewMcpd setColldate(String colldate)
	{
		this.colldate = colldate;
		return this;
	}

	public String getBredcode()
	{
		return bredcode;
	}

	public ViewMcpd setBredcode(String bredcode)
	{
		this.bredcode = bredcode;
		return this;
	}

	public String getBredname()
	{
		return bredname;
	}

	public ViewMcpd setBredname(String bredname)
	{
		this.bredname = bredname;
		return this;
	}

	public Integer getSampstat()
	{
		return sampstat;
	}

	public ViewMcpd setSampstat(Integer sampstat)
	{
		this.sampstat = sampstat;
		return this;
	}

	public String getAncest()
	{
		return ancest;
	}

	public ViewMcpd setAncest(String ancest)
	{
		this.ancest = ancest;
		return this;
	}

	public Integer getCollsrc()
	{
		return collsrc;
	}

	public ViewMcpd setCollsrc(Integer collsrc)
	{
		this.collsrc = collsrc;
		return this;
	}

	public String getDonorcode()
	{
		return donorcode;
	}

	public ViewMcpd setDonorcode(String donorcode)
	{
		this.donorcode = donorcode;
		return this;
	}

	public String getDonorname()
	{
		return donorname;
	}

	public ViewMcpd setDonorname(String donorname)
	{
		this.donorname = donorname;
		return this;
	}

	public String getDonornumb()
	{
		return donornumb;
	}

	public ViewMcpd setDonornumb(String donornumb)
	{
		this.donornumb = donornumb;
		return this;
	}

	public String getOthernumb()
	{
		return othernumb;
	}

	public ViewMcpd setOthernumb(String othernumb)
	{
		this.othernumb = othernumb;
		return this;
	}

	public String getDuplsite()
	{
		return duplsite;
	}

	public ViewMcpd setDuplsite(String duplsite)
	{
		this.duplsite = duplsite;
		return this;
	}

	public String getDuplinstname()
	{
		return duplinstname;
	}

	public ViewMcpd setDuplinstname(String duplinstname)
	{
		this.duplinstname = duplinstname;
		return this;
	}

	public String getStorage()
	{
		return storage;
	}

	public ViewMcpd setStorage(String storage)
	{
		this.storage = storage;
		return this;
	}

	public Integer getMlsstat()
	{
		return mlsstat;
	}

	public ViewMcpd setMlsstat(Integer mlsstat)
	{
		this.mlsstat = mlsstat;
		return this;
	}

	public String getRemarks()
	{
		return remarks;
	}

	public ViewMcpd setRemarks(String remarks)
	{
		this.remarks = remarks;
		return this;
	}

	public String getEntitytype()
	{
		return entitytype;
	}

	public ViewMcpd setEntitytype(String entitytype)
	{
		this.entitytype = entitytype;
		return this;
	}

	public Long getEntityparentid()
	{
		return entityparentid;
	}

	public ViewMcpd setEntityparentid(Long entityparentid)
	{
		this.entityparentid = entityparentid;
		return this;
	}

	public String getEntityparentaccenumb()
	{
		return entityparentaccenumb;
	}

	public ViewMcpd setEntityparentaccenumb(String entityparentaccenumb)
	{
		this.entityparentaccenumb = entityparentaccenumb;
		return this;
	}
}
