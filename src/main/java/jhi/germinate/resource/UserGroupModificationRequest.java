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
public class UserGroupModificationRequest
{
	private Integer   userGroupId;
	private Integer[] userIds;
	private Boolean   addOperation;
}
