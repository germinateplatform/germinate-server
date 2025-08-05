package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class HiddenColumns
{
	private List<String> germplasm;
	private List<String> germplasmAttributes;
	private List<String> images;
	private List<String> climates;
	private List<String> climateData;
	private List<String> comments;
	private List<String> fileresources;
	private List<String> maps;
	private List<String> markers;
	private List<String> mapDefinitions;
	private List<String> datasets;
	private List<String> datasetAttributes;
	private List<String> experiments;
	private List<String> entities;
	private List<String> groups;
	private List<String> institutions;
	private List<String> locations;
	private List<String> pedigrees;
	private List<String> pedigreedefinitions;
	private List<String> traits;
	private List<String> trialsData;
	private List<String> collaborators;
	private List<String> publications;
}
