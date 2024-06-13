package jhi.germinate.resource;

import java.util.*;

public class AboutConfig extends ArrayList<AboutConfig.AboutInfo>
{
	public static class AboutInfo {
		private String name;
		private String description;
		private String group;
		private String url;
		private String image;

		public String getName()
		{
			return name;
		}

		public AboutInfo setName(String name)
		{
			this.name = name;
			return this;
		}

		public String getDescription()
		{
			return description;
		}

		public AboutInfo setDescription(String description)
		{
			this.description = description;
			return this;
		}

		public String getGroup()
		{
			return group;
		}

		public AboutInfo setGroup(String group)
		{
			this.group = group;
			return this;
		}

		public String getUrl()
		{
			return url;
		}

		public AboutInfo setUrl(String url)
		{
			this.url = url;
			return this;
		}

		public String getImage()
		{
			return image;
		}

		public AboutInfo setImage(String image)
		{
			this.image = image;
			return this;
		}
	}
}
