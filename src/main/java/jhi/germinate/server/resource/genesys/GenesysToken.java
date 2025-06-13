package jhi.germinate.server.resource.genesys;

import com.google.gson.annotations.SerializedName;

public class GenesysToken
{
	@SerializedName("access_token")
	private String accessToken;
	@SerializedName("token_type")
	private String tokenType;
	@SerializedName("expires_in")
	private Integer expiresIn;

	public GenesysToken()
	{
	}

	public String getAccessToken()
	{
		return accessToken;
	}

	public GenesysToken setAccessToken(String accessToken)
	{
		this.accessToken = accessToken;
		return this;
	}

	public String getTokenType()
	{
		return tokenType;
	}

	public GenesysToken setTokenType(String tokenType)
	{
		this.tokenType = tokenType;
		return this;
	}

	public Integer getExpiresIn()
	{
		return expiresIn;
	}

	public GenesysToken setExpiresIn(Integer expiresIn)
	{
		this.expiresIn = expiresIn;
		return this;
	}

	@Override
	public String toString()
	{
		return "GenesysToken{" +
				"accessToken='" + accessToken + '\'' +
				", tokenType='" + tokenType + '\'' +
				", expiresIn=" + expiresIn +
				'}';
	}
}
