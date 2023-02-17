package jhi.germinate.resource;

import java.util.*;

public class CarouselConfig extends HashMap<String, List<CarouselConfig.ImageConfig>>
{
	public static class ImageConfig {
		private String name;
		private String text;

		public String getName()
		{
			return name;
		}

		public ImageConfig setName(String name)
		{
			this.name = name;
			return this;
		}

		public String getText()
		{
			return text;
		}

		public ImageConfig setText(String text)
		{
			this.text = text;
			return this;
		}
	}
}
