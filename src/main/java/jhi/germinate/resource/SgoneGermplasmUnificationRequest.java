package jhi.germinate.resource;

import java.util.List;

public class SgoneGermplasmUnificationRequest
{
	private List<SgoneGermplasmUnification> unifications;

	public List<SgoneGermplasmUnification> getUnifications()
	{
		return unifications;
	}

	public SgoneGermplasmUnificationRequest setUnifications(List<SgoneGermplasmUnification> unifications)
	{
		this.unifications = unifications;
		return this;
	}

	public static class SgoneGermplasmUnification
	{
		private SgonePojo preferred;
		private List<SgonePojo> others;

		public SgonePojo getPreferred()
		{
			return preferred;
		}

		public SgoneGermplasmUnification setPreferred(SgonePojo preferred)
		{
			this.preferred = preferred;
			return this;
		}

		public List<SgonePojo> getOthers()
		{
			return others;
		}

		public SgoneGermplasmUnification setOthers(List<SgonePojo> others)
		{
			this.others = others;
			return this;
		}
	}

	public static class SgonePojo
	{
		private String id;
		private String name;

		public String getId()
		{
			return id;
		}

		public SgonePojo setId(String id)
		{
			this.id = id;
			return this;
		}

		public String getName()
		{
			return name;
		}

		public SgonePojo setName(String name)
		{
			this.name = name;
			return this;
		}
	}
}
