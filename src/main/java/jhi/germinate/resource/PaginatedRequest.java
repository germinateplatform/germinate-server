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
@ToString
public class PaginatedRequest
{
	private String   orderBy;
	private Integer  ascending;
	private int      limit     = Integer.MAX_VALUE;
	private int      page      = 0;
	private long     prevCount = -1;
	private boolean  minimal   = false;
	private Filter[] filter;
}
