package com.sap.sss.vendorfake.routers;

import com.sap.sss.vendorfake.datastore.InMemoryDataStore;
import com.sap.sss.vendorfake.docusign.*;
import com.sap.sss.vendorfake.models.OAuthPendingRequestContext;
import com.sap.sss.vendorfake.models.SapConnectedUsers;
import com.sap.sss.vendorfake.models.SapSetupNewTenantPayload;
import com.sap.sss.vendorfake.utiilities.CommonUtility;
import io.jsonwebtoken.Jwts;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class OAuthRouter {
    private static String docuSignEnvironmentUrlString = "account-d.docusign.com";
    private static String docuSignRequestedScope = "signature impersonation";

    @POST
    @Path("setupNewTenant")
    public Response setupNewTenant(@HeaderParam("X-SAP-AA-UserId") String sapUserId, @HeaderParam("X-SAP-AA-TenantId") String sapTenantId, @HeaderParam("X-SAP-AA-Signature") String signature, SapSetupNewTenantPayload setupNewTenantPayload) {
        Response checkAuthenticationResponse = CommonUtility.checkAuthenticationSignature(sapUserId, sapTenantId, signature);
        if(checkAuthenticationResponse != null) {
            return checkAuthenticationResponse;
        }

        InMemoryDataStore.getInstance().saveTenant(setupNewTenantPayload);

        return Response.status(Response.Status.OK).entity("Tenant " + setupNewTenantPayload.getTenantId() + " successfully setup!").build();
    }

    @GET
    @Path("askForUserPermissionAndConnectUsers")
    public Response askForUserPermissionAndConnectUsers(@QueryParam("sapUserId") String sapUserId, @QueryParam("sapTenantId") String sapTenantId, @QueryParam("sapSignature") String sapSignature) {

        Response checkTenantResponse = CommonUtility.checkTenant(sapTenantId);
        if(checkTenantResponse != null) {
            return checkTenantResponse;
        }

        SapSetupNewTenantPayload sapTenantPayload = InMemoryDataStore.getInstance().getTenant(sapTenantId);

        if(sapTenantPayload.isSharedAuth()) {
            return this.obtainJwtConsent(sapUserId, sapTenantId);
        } else {
            return this.getAuthCodeGrantPermission(sapUserId, sapTenantId);
        }
    }

    private Response obtainJwtConsent(String sapUserId, String sapTenantId) {
        Response checkTenantResponse = CommonUtility.checkTenant(sapTenantId);
        if(checkTenantResponse != null) {
            return checkTenantResponse;
        }

        SapSetupNewTenantPayload sapTenantPayload = InMemoryDataStore.getInstance().getTenant(sapTenantId);
        String docusignIntegrationKey = sapTenantPayload.getVendorAppIdentifier();

        String pendingRequestUUID = UUID.randomUUID().toString();
        OAuthPendingRequestContext oAuthPendingRequestContext = new OAuthPendingRequestContext(sapUserId, sapTenantId);

        InMemoryDataStore.getInstance().savePendingRequestContext(pendingRequestUUID, oAuthPendingRequestContext);

        String url = "https://account-d.docusign.com/oauth/auth?response_type=code&scope=signature%20impersonation&client_id=" + docusignIntegrationKey + "&state=" + pendingRequestUUID + "&redirect_uri=https://sssvendorfake-silly-buffalo.cfapps.sap.hana.ondemand.com/oauthcallback";
        URI uri = URI.create(url);

        return Response.seeOther(uri).build();
    }

    private Response getAuthCodeGrantPermission(String sapUserId, String sapTenantId) {
        Response checkTenantResponse = CommonUtility.checkTenant(sapTenantId);
        if(checkTenantResponse != null) {
            return checkTenantResponse;
        }

        SapSetupNewTenantPayload sapTenantPayload = InMemoryDataStore.getInstance().getTenant(sapTenantId);
        String docusignIntegrationKey = sapTenantPayload.getVendorAppIdentifier();

        String pendingRequestUUID = UUID.randomUUID().toString();
        OAuthPendingRequestContext oAuthPendingRequestContext = new OAuthPendingRequestContext(sapUserId, sapTenantId);

        InMemoryDataStore.getInstance().savePendingRequestContext(pendingRequestUUID, oAuthPendingRequestContext);

        String url = "https://account-d.docusign.com/oauth/auth?response_type=code&scope=signature&client_id=" + docusignIntegrationKey + "&state=" + pendingRequestUUID + "&redirect_uri=https://sssvendorfake-silly-buffalo.cfapps.sap.hana.ondemand.com/oauthcallback";
        URI uri = URI.create(url);

        return Response.seeOther(uri).build();
    }

    @GET
    @Path("getJwtToken")
    public Response getJwtToken(@QueryParam("docusignIntegrationKey") String docusignIntegrationKey, @QueryParam("docusignUserId") String docusignUserId) {
        return Response.status(Response.Status.OK).entity(this.createJwtTokenWithLibrary(docusignIntegrationKey, docusignUserId)).build();
    }

    private String createJwtTokenWithLibrary(String docusignIntegrationKey, String docusignUserId) {
        Date now = Calendar.getInstance().getTime();

        Map<String, Object> additionalClaims = new HashMap<>();
        additionalClaims.put("scope", docuSignRequestedScope);

        String docuSignPrivateKey = "-----BEGIN RSA PRIVATE KEY-----\n" +
                "MIIEpAIBAAKCAQEAoyo/pPuXTi6miNWUh2MifOeo2N11nM+fpOU01zB1oDTMFoUe\n" +
                "LOmfVo2AOe0jpEjmQ4+8FaCUmlW53KxX/deIbhSnbNXpFKT22xom5KvmQT6xxfhm\n" +
                "5Jwum4FOyspJsIE6PDCtNHAUMncSxtC9DfIHj2OI2z8RVTWLCiBPeBtilIQR95Ik\n" +
                "aXKzXck8VjrXIf+eqc/TXeNTKMz9RhqVFdZnIPenTPOZ8cYrXWhvSKIPF1fse1GR\n" +
                "Op2nI0oqqq3vzrPV2c9g6dryQW8tZX/tZts73uWFGkeUPkKKLrZPeIXceeQ6GwiS\n" +
                "uND+QFkDcR+73fowFRJgKNBDfoobrbw5ii2gKwIDAQABAoIBACZUYPIdZjxoChgf\n" +
                "8E3jtHDaLNiRIbVuMscWTxT0HdW+QWlS6TVMxnEbOZGiCxrnQyyA4gLEn9QnqktU\n" +
                "MzF/Bd4yPOh80c3XOORcnuFeHm+aTkG+6lDu/aXrOPq7jZcIrIkCOFYWtC/st/z7\n" +
                "gUX64trE25wBk01MkDaXQ4PoDv7rK1UJaPd7Vqwnr3zzsgVLhHVlF9J7KnVXWKFD\n" +
                "KplOfU4v4o5kPN0mNcqHcZ6jGg8HAwOGS8vAbW5YpWLCeG4PxLAqD5LSsovyAsAP\n" +
                "Mf39QuL/jAuEXpjnIDKfAZ2IZhgPcg4NIXnVv9skLkiKtD0V9OIjMsJAEHkSrZcm\n" +
                "qOor+fkCgYEA/a7a+2q7ywNxXNa7gkHJAC0npEjnZY/WqMaKp5pLe+6k1G0T/gXs\n" +
                "lkT21qND6j+VcdlPSGru7wXhwZ7aSBgKlDU7vovVv6bF2wL4e8mSY/XwybQeOYcC\n" +
                "SN9O9l04p8Sl7GQmn1b6hCXCvr5bBCUxfm+6uBpi5SozD52kTJHa/5MCgYEApKfA\n" +
                "B/9/zrIVodA1MexmtkiNMVNXFcmvqzq3ZafBcvz12el2jDFfEQnS2BKx2OIEpGCf\n" +
                "Hdn2ndzW0WX0Rs26wwUduC95LolCToBC4n36SQ1wfsm/3OkOooGkfHUm6tIzZt96\n" +
                "NOok3hueCPUqSV0b9/kyfnyTWhqFaYhz6XbZTAkCgYEArBrqudNJoIuvZxrPj6lt\n" +
                "4k7ALDbBtieFrG82Nkr5lxTqgquV+qquPayAAlI1i0Cj9N9HaIwTmdnVtXQ+Btc4\n" +
                "piAPblCULTfJ17IGPoUcafC68TzfnIu5wxKtEXthKoDBSMURZtytjOXJX3rpaMCK\n" +
                "+Yp3lNth6LNefOOoScJSXz0CgYEAmoccv+TXy+JyTtSat+nHU5evevVeK4KHLUoD\n" +
                "yJGyCfrBuOtUaKoFMHZpvIN/Ca7E8IgFjPx8aRdTPF5U7QYzGsf4Zl2Xe0cyRX42\n" +
                "R143wMuuIi+xst++7mCBQJSqG4N+3jMp+/Mq+pAstvdv4j5R+12SOAcuO0fcoXiA\n" +
                "YEE8GhECgYBLoa249qXJljBSyORWNourisoN4nnGOt+wqrMm/DxstrhirDdXBhCa\n" +
                "kd+RYMo9cWOIrLWBTtQEDdvHwhpZE8oAI5Hnr6Xk16E3xb9BDGD2F4TtXbQBXozm\n" +
                "Fr+OGZgVs38Eb8e/ftmWm4Kv9MvSCXN6WUm64UN9H8tMjMvlCL4CpQ==\n" +
                "-----END RSA PRIVATE KEY-----\n";


        try {
            Key privateKey = CommonUtility.stringToPrivateKey(docuSignPrivateKey);

            String jws = Jwts.builder()
                    .setHeaderParam("typ", "JWT")
                    .setHeaderParam("alg", "RS256")
                    .setSubject(docusignUserId)
                    .setIssuer(docusignIntegrationKey)
                    .setAudience(docuSignEnvironmentUrlString)
                    .setIssuedAt(now)
                    .addClaims(additionalClaims)
                    .signWith(privateKey)
                    .compact();

            return jws;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String createJwtToken(String docusignIntegrationKey, String docusignUserId) {
        Date now = Calendar.getInstance().getTime();
        long nowEpochTime = now.getTime();

        DocusignJwtHeader docusignJwtHeader = new DocusignJwtHeader();
        String jwtHeaderJson = CommonUtility.getConfiguredGsonInstance().toJson(docusignJwtHeader);
        String jwtBase64HeaderJson = Base64.getUrlEncoder().withoutPadding().encodeToString(jwtHeaderJson.getBytes());


        DocusignJwtBody docusignJwtBody = new DocusignJwtBody(docusignIntegrationKey, docusignUserId, nowEpochTime, nowEpochTime + 60 * 60 * 1000, docuSignEnvironmentUrlString, docuSignRequestedScope);
        String jwtBodyJson = CommonUtility.getConfiguredGsonInstance().toJson(docusignJwtBody);
        String jwtBase64BodyJson = Base64.getUrlEncoder().withoutPadding().encodeToString(jwtBodyJson.getBytes(StandardCharsets.UTF_8));

        String docuSignPrivateKey = "-----BEGIN RSA PRIVATE KEY-----\n" +
                "MIIEpAIBAAKCAQEAoyo/pPuXTi6miNWUh2MifOeo2N11nM+fpOU01zB1oDTMFoUe\n" +
                "LOmfVo2AOe0jpEjmQ4+8FaCUmlW53KxX/deIbhSnbNXpFKT22xom5KvmQT6xxfhm\n" +
                "5Jwum4FOyspJsIE6PDCtNHAUMncSxtC9DfIHj2OI2z8RVTWLCiBPeBtilIQR95Ik\n" +
                "aXKzXck8VjrXIf+eqc/TXeNTKMz9RhqVFdZnIPenTPOZ8cYrXWhvSKIPF1fse1GR\n" +
                "Op2nI0oqqq3vzrPV2c9g6dryQW8tZX/tZts73uWFGkeUPkKKLrZPeIXceeQ6GwiS\n" +
                "uND+QFkDcR+73fowFRJgKNBDfoobrbw5ii2gKwIDAQABAoIBACZUYPIdZjxoChgf\n" +
                "8E3jtHDaLNiRIbVuMscWTxT0HdW+QWlS6TVMxnEbOZGiCxrnQyyA4gLEn9QnqktU\n" +
                "MzF/Bd4yPOh80c3XOORcnuFeHm+aTkG+6lDu/aXrOPq7jZcIrIkCOFYWtC/st/z7\n" +
                "gUX64trE25wBk01MkDaXQ4PoDv7rK1UJaPd7Vqwnr3zzsgVLhHVlF9J7KnVXWKFD\n" +
                "KplOfU4v4o5kPN0mNcqHcZ6jGg8HAwOGS8vAbW5YpWLCeG4PxLAqD5LSsovyAsAP\n" +
                "Mf39QuL/jAuEXpjnIDKfAZ2IZhgPcg4NIXnVv9skLkiKtD0V9OIjMsJAEHkSrZcm\n" +
                "qOor+fkCgYEA/a7a+2q7ywNxXNa7gkHJAC0npEjnZY/WqMaKp5pLe+6k1G0T/gXs\n" +
                "lkT21qND6j+VcdlPSGru7wXhwZ7aSBgKlDU7vovVv6bF2wL4e8mSY/XwybQeOYcC\n" +
                "SN9O9l04p8Sl7GQmn1b6hCXCvr5bBCUxfm+6uBpi5SozD52kTJHa/5MCgYEApKfA\n" +
                "B/9/zrIVodA1MexmtkiNMVNXFcmvqzq3ZafBcvz12el2jDFfEQnS2BKx2OIEpGCf\n" +
                "Hdn2ndzW0WX0Rs26wwUduC95LolCToBC4n36SQ1wfsm/3OkOooGkfHUm6tIzZt96\n" +
                "NOok3hueCPUqSV0b9/kyfnyTWhqFaYhz6XbZTAkCgYEArBrqudNJoIuvZxrPj6lt\n" +
                "4k7ALDbBtieFrG82Nkr5lxTqgquV+qquPayAAlI1i0Cj9N9HaIwTmdnVtXQ+Btc4\n" +
                "piAPblCULTfJ17IGPoUcafC68TzfnIu5wxKtEXthKoDBSMURZtytjOXJX3rpaMCK\n" +
                "+Yp3lNth6LNefOOoScJSXz0CgYEAmoccv+TXy+JyTtSat+nHU5evevVeK4KHLUoD\n" +
                "yJGyCfrBuOtUaKoFMHZpvIN/Ca7E8IgFjPx8aRdTPF5U7QYzGsf4Zl2Xe0cyRX42\n" +
                "R143wMuuIi+xst++7mCBQJSqG4N+3jMp+/Mq+pAstvdv4j5R+12SOAcuO0fcoXiA\n" +
                "YEE8GhECgYBLoa249qXJljBSyORWNourisoN4nnGOt+wqrMm/DxstrhirDdXBhCa\n" +
                "kd+RYMo9cWOIrLWBTtQEDdvHwhpZE8oAI5Hnr6Xk16E3xb9BDGD2F4TtXbQBXozm\n" +
                "Fr+OGZgVs38Eb8e/ftmWm4Kv9MvSCXN6WUm64UN9H8tMjMvlCL4CpQ==\n" +
                "-----END RSA PRIVATE KEY-----\n";

        String docuSignCombinedToken1 = jwtBase64HeaderJson + "." + jwtBase64BodyJson;

        String docusignSignature = null;
        try {
            docusignSignature = CommonUtility.signSHA256RSAfromPKCS1(docuSignCombinedToken1, docuSignPrivateKey);

            String docusignCombinedToken2 = docuSignCombinedToken1 + "." + docusignSignature;

            return docusignCombinedToken2;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Response obtainJwtAccessToken(String sapUserId, String sapTenantId) {

        SapSetupNewTenantPayload sapTenantPayload = InMemoryDataStore.getInstance().getTenant(sapTenantId);
        String docusignIntegrationKey = sapTenantPayload.getVendorAppIdentifier();

        SapConnectedUsers sapConnectedUsers = InMemoryDataStore.getInstance().getConnectedUsers(sapUserId, sapTenantId);
        String docusignUserId = sapConnectedUsers.getDocusignUserId();

        String signedJwtToken = this.createJwtToken(docusignIntegrationKey, docusignUserId);

        try {
            retrofit2.Response<DocusignAccessTokenResponse> accessTokenResponse = DocusignService.getInstance().obtainJwtAccessToken(signedJwtToken).execute();

            if(accessTokenResponse.isSuccessful()) {
                sapConnectedUsers.setDocusignJwtAccessToken(accessTokenResponse.body().getAccess_token());
                InMemoryDataStore.getInstance().saveConnectedUsers(sapConnectedUsers);
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity(accessTokenResponse.message()).build();
            }

            return Response.status(Response.Status.OK).entity(sapConnectedUsers).build();
        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Docusign rest call failed!").build();
        }
    }

    private Response obtainIndividualAccessToken(String token, String sapUserId, String sapTenantId) {

        SapSetupNewTenantPayload sapTenantPayload = InMemoryDataStore.getInstance().getTenant(sapTenantId);
        String docusignIntegrationKey = sapTenantPayload.getVendorAppIdentifier();
        String docusignSecretKey = sapTenantPayload.getVendorAppSecretKey();

        SapConnectedUsers sapConnectedUsers = InMemoryDataStore.getInstance().getConnectedUsers(sapUserId, sapTenantId);
        if(sapConnectedUsers == null) {
            sapConnectedUsers = new SapConnectedUsers(sapUserId, sapTenantId, null, null, null, null, null);
        }

        try {
            retrofit2.Response<DocusignAccessTokenResponse> accessTokenResponse = DocusignService.getInstance().obtainAuthCodeGrantAccessToken(this.buildBase64AuthKey(docusignIntegrationKey, docusignSecretKey), token).execute();

            if(accessTokenResponse.isSuccessful()) {
                String accessToken = accessTokenResponse.body().getAccess_token();
                String refreshToken = accessTokenResponse.body().getRefresh_token();

                sapConnectedUsers.setDocusignAccessToken(accessToken);
                sapConnectedUsers.setDocusignRefreshToken(refreshToken);
                InMemoryDataStore.getInstance().saveConnectedUsers(sapConnectedUsers);

                return this.selectDefaultAccount(sapUserId, sapTenantId, accessToken);
                // return Response.status(Response.Status.OK).entity(sapConnectedUsers).build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST).entity("Docusign rest call not successful: " + accessTokenResponse.message()).build();
            }


        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Docusign rest call failed!").build();
        }
    }

    private Response selectDefaultAccount(String sapUserId, String sapTenantId, String accessToken) {

        SapSetupNewTenantPayload setupNewTenantPayload = InMemoryDataStore.getInstance().getTenant(sapTenantId);
        SapConnectedUsers sapConnectedUsers = InMemoryDataStore.getInstance().getConnectedUsers(sapUserId, sapTenantId);
        // check for null and throw error in prod

        String authHeader = "Bearer " + accessToken;
        try {
            retrofit2.Response<DocusignUserInfo> userInfoResponse = DocusignService.getInstance().getUserInfo(authHeader).execute();

            DocusignUserInfo docusignUserInfo = userInfoResponse.body();
            sapConnectedUsers.setDocusignUserId(docusignUserInfo.getSub());

             for(DocusignAccount account : docusignUserInfo.getAccounts()) {
                 if(account.isIs_default()) {
                     sapConnectedUsers.setDocusignAccountId(account.getAccount_id());
                 }
             }

             if(setupNewTenantPayload.isSharedAuth()) {
                 return this.obtainJwtAccessToken(sapUserId, sapTenantId);
             } else {
                 return Response.status(Response.Status.OK).entity(sapConnectedUsers).build();
             }
        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Docusign rest call failed!").build();
        }
    }

    @GET
    @Path("oauthcallback")
    public Response getOAuthCallback(@QueryParam("code") String code, @QueryParam("state") String state)
    {
        OAuthPendingRequestContext oAuthPendingRequestContext = InMemoryDataStore.getInstance().getPendingRequestContext(state);

        if(oAuthPendingRequestContext == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("We could not find the pending Request Context for " + state).build();
        }

        return this.obtainIndividualAccessToken(code, oAuthPendingRequestContext.getSapUserId(), oAuthPendingRequestContext.getSapTenantId());
    }

    private String buildBase64AuthKey(String docuSignIntegrationKey, String docuSignSecretKey) {
        String intAndSecretKeys = docuSignIntegrationKey + ":" + docuSignSecretKey;
        return "Basic " + Base64.getUrlEncoder().encodeToString(intAndSecretKeys.getBytes(StandardCharsets.UTF_8));
    }
}