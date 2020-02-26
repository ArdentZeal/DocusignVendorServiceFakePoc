package com.sap.sss.vendorfake.docusign;

import retrofit2.Call;
import retrofit2.http.*;

public interface DocusignServiceI {
    @POST("accounts/{accountId}/envelopes")
    Call<Void> createEnvelope(@Header(value = "Authorization") String authHeader, @Path("accountId") String accountId, @Body DocusignEnvelope envelope);
}