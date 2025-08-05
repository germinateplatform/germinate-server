package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class SgoneGermplasmUnificationRequest
{
	private List<SgoneGermplasmUnification> unifications;

	@NoArgsConstructor
	@Getter
	@Setter
	@Accessors(chain = true)
	public static class SgoneGermplasmUnification
	{
		private SgonePojo preferred;
		private List<SgonePojo> others;
	}

	@NoArgsConstructor
	@Getter
	@Setter
	@Accessors(chain = true)
	public static class SgonePojo
	{
		private String id;
		private String name;
	}
}
