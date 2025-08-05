package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.*;

public class AboutConfig extends ArrayList<AboutConfig.AboutInfo>
{
	@NoArgsConstructor
	@Getter
	@Setter
	@Accessors(chain = true)
	public static class AboutInfo {
		private String name;
		private String description;
		private String group;
		private String url;
		private String image;
	}
}
