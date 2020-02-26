package com.sap.sss.vendorfake.docusign;

import retrofit2.Call;
import retrofit2.http.*;

public interface DocusignOAuthServiceI {
    @FormUrlEncoded
    @POST("token")
    // @Field(value = "redirect_uri", encoded = false) String redirectUri
    Call<DocusignAccessTokenResponse> obtainAccessToken(@Header(value = "Authorization") String authHeader, @Field(value = "grant_type", encoded = false) String grantType, @Field(value = "code", encoded = true) String code);

    @GET("userinfo")
    Call<DocusignUserInfo> getUserInfo(@Header(value = "Authorization") String authHeader);
}