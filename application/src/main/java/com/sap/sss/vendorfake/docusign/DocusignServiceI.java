package com.sap.sss.vendorfake.docusign;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface DocusignServiceI {
    @Headers({
            "X-DocuSign-Authentication: {\"Username\":\"matthias.weidemann@sap.com\",\"Password\":\"MH0hIxIZiEeNMr5/qYgCmytxptk=\",\"IntegratorKey\": \"bc46e8ab-7ff9-4bf0-a95e-9d226f408349\"}"
    })
    @POST("envelopes")
    Call<Void> createEnvelope(@Body DocusignEnvelope envelope);

    @POST("oauth/token")
    Call<DocusignJwtAccessTokenResponse> obtainAccessToken(@Body DocusignJwtAccessTokenPayload accessTokenPayload);
}