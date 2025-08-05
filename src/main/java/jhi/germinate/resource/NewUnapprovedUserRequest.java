package jhi.germinate.resource;

import jhi.gatekeeper.resource.NewUnapprovedUser;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author Sebastian Raubach
 */
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class NewUnapprovedUserRequest
{
	private NewUnapprovedUser user;
	private String            locale;
}
