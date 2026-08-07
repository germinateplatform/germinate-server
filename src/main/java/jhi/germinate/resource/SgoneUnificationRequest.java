package jhi.germinate.resource;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class SgoneUnificationRequest
{
	private List<SgoneUnification> unifications;

	@NoArgsConstructor
	@Getter
	@Setter
	@Accessors(chain = true)
	public static class SgoneUnification
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
