package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.*;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class TraitStats
{
	private Integer            traitId;
	private String             traitName;
	private String             traitNameShort;
	private Double             min;
	private Double             avg;
	private Double             max;
	private Integer            count;
	private List<List<String>> categories;
	private String dataType;

	public TraitStats setCategories(String[][] categories)
	{
		List<List<String>> result = new ArrayList<>();

		for (String[] cat : categories)
			result.add(new ArrayList<>(Arrays.asList(cat)));

		this.categories = result;
		return this;
	}
}
