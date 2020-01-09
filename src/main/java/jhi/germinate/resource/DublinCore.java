package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class DublinCore
{
	private String[] title;
	private String[] creator;
	private String[] subject;
	private String[] description;
	private String[] publisher;
	private String[] contributor;
	private String[] date;
	private String[] type;
	private String[] format;
	private String[] identifier;
	private String[] source;
	private String[] language;
	private String[] relation;
	private String[] coverage;
	private String[] rights;

	public String[] getTitle()
	{
		return title;
	}

	public DublinCore setTitle(String[] title)
	{
		this.title = title;
		return this;
	}

	public String[] getCreator()
	{
		return creator;
	}

	public DublinCore setCreator(String[] creator)
	{
		this.creator = creator;
		return this;
	}

	public String[] getSubject()
	{
		return subject;
	}

	public DublinCore setSubject(String[] subject)
	{
		this.subject = subject;
		return this;
	}

	public String[] getDescription()
	{
		return description;
	}

	public DublinCore setDescription(String[] description)
	{
		this.description = description;
		return this;
	}

	public String[] getPublisher()
	{
		return publisher;
	}

	public DublinCore setPublisher(String[] publisher)
	{
		this.publisher = publisher;
		return this;
	}

	public String[] getContributor()
	{
		return contributor;
	}

	public DublinCore setContributor(String[] contributor)
	{
		this.contributor = contributor;
		return this;
	}

	public String[] getDate()
	{
		return date;
	}

	public DublinCore setDate(String[] date)
	{
		this.date = date;
		return this;
	}

	public String[] getType()
	{
		return type;
	}

	public DublinCore setType(String[] type)
	{
		this.type = type;
		return this;
	}

	public String[] getFormat()
	{
		return format;
	}

	public DublinCore setFormat(String[] format)
	{
		this.format = format;
		return this;
	}

	public String[] getIdentifier()
	{
		return identifier;
	}

	public DublinCore setIdentifier(String[] identifier)
	{
		this.identifier = identifier;
		return this;
	}

	public String[] getSource()
	{
		return source;
	}

	public DublinCore setSource(String[] source)
	{
		this.source = source;
		return this;
	}

	public String[] getLanguage()
	{
		return language;
	}

	public DublinCore setLanguage(String[] language)
	{
		this.language = language;
		return this;
	}

	public String[] getRelation()
	{
		return relation;
	}

	public DublinCore setRelation(String[] relation)
	{
		this.relation = relation;
		return this;
	}

	public String[] getCoverage()
	{
		return coverage;
	}

	public DublinCore setCoverage(String[] coverage)
	{
		this.coverage = coverage;
		return this;
	}

	public String[] getRights()
	{
		return rights;
	}

	public DublinCore setRights(String[] rights)
	{
		this.rights = rights;
		return this;
	}
}
