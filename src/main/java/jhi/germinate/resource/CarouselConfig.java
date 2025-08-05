package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.*;

public class CarouselConfig extends HashMap<String, List<CarouselConfig.ImageConfig>>
{
	@NoArgsConstructor
	@Getter
	@Setter
	@Accessors(chain = true)
	public static class ImageConfig {
		private String name;
		private String text;
	}
}
