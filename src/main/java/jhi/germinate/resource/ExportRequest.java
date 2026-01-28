package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.*;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class ExportRequest
{
	private FilterGroup[]       filters;
	private Map<String, String> columnNameMapping = new HashMap<>();
	private String              forcedFileExtension;
}
