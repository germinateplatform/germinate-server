package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author Sebastian Raubach
 */
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class EntityTypeStats
{
	private Integer entityTypeId;
	private String  entityTypeName;
	private Integer count;
}
