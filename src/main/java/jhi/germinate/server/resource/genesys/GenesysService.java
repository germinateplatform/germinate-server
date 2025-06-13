package jhi.germinate.server.resource.genesys;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface GenesysService
{
    @POST("/api/v2/request/external")
    Call<String> postGermplasm(@Body GenesysGermplasmResource.GenesysRequest request);

    @POST("/oauth/token")
    Call<GenesysToken> postToken(@Body RequestBody request);
}