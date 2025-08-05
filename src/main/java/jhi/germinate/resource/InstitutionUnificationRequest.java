package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class InstitutionUnificationRequest
{
	private Integer      preferredInstitutionId;
	private Integer[]    institutionIds;
}
