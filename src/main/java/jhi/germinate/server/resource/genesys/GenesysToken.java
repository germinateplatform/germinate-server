package jhi.germinate.server.resource.genesys;

import com.google.gson.annotations.SerializedName;
import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@ToString
public class GenesysToken
{
	@SerializedName("access_token")
	private String  accessToken;
	@SerializedName("token_type")
	private String  tokenType;
	@SerializedName("expires_in")
	private Integer expiresIn;
}
