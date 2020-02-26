package com.sap.sss.vendorfake.routers;

import com.sap.sss.vendorfake.datastore.InMemoryDataStore;
import com.sap.sss.vendorfake.docusign.*;
import com.sap.sss.vendorfake.models.OAuthCallbackData;
import com.sap.sss.vendorfake.models.OAuthPendingRequestContext;
import com.sap.sss.vendorfake.models.SapConnectedUsers;
import com.sap.sss.vendorfake.models.SapSetupNewTenantPayload;
import com.sap.sss.vendorfake.utiilities.CommonUtility;
import org.jsoup.Connection;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class OAuthRouter {

    // should be created for each tenant, static for now
    // private static String docuSignIntegrationKey = "bc46e8ab-7ff9-4bf0-a95e-9d226f408349";

    // private static String docuSignSecretKey = "a3ef9480-c0e3-40c2-be9b-532bbb9ccf84";

    private static String docuSignEnvironmentUrlString = "account-d.docusign.com";
    private static String docuSignRequestedScope = "signature";

    // TODO: Save docusign user to sapUser

    @POST
    @Path("setupNewTenant")
    public Response setupNewTenant(@HeaderParam("X-SAP-AA-UserId") String sapUserId, @HeaderParam("X-SAP-AA-TenantId") String sapTenantId, @HeaderParam("X-SAP-AA-ShouldUseSharedAuth") boolean shouldUseSharedAuth, @HeaderParam("X-SAP-AA-ActOnBehalfOfUserId") String sapOnBehalfUserId, @HeaderParam("X-SAP-AA-Signature") String signature, SapSetupNewTenantPayload setupNewTenantPayload) {
        Response checkAuthenticationResponse = CommonUtility.checkAuthenticationSignature(sapUserId, sapTenantId, shouldUseSharedAuth, sapOnBehalfUserId, signature);
        if(checkAuthenticationResponse != null) {
            return checkAuthenticationResponse;
        }

        InMemoryDataStore.getInstance().saveTenant(setupNewTenantPayload);

        return Response.status(Response.Status.OK).entity("Tenant " + setupNewTenantPayload.getTenantId() + " successfully setup!").build();
    }

    @GET
    @Path("obtainConsent")
    public Response obtainConsent(@QueryParam("sapUserId") String sapUserId, @QueryParam("sapTenantId") String sapTenantId) {
        Response checkTenantResponse = CommonUtility.checkTenant(sapTenantId);
        if(checkTenantResponse != null) {
            return checkTenantResponse;
        }

        SapSetupNewTenantPayload sapTenantPayload = InMemoryDataStore.getInstance().getTenant(sapTenantId);
        String docusignIntegrationKey = sapTenantPayload.getDocusignIntegrationKey();


        String pendingRequestUUID = UUID.randomUUID().toString();
        OAuthPendingRequestContext oAuthPendingRequestContext = new OAuthPendingRequestContext(sapUserId, sapTenantId);

        InMemoryDataStore.getInstance().savePendingRequestContext(pendingRequestUUID, oAuthPendingRequestContext);

        String url = "https://account-d.docusign.com/oauth/auth?response_type=code&scope=signature%20impersonation&client_id=" + docusignIntegrationKey + "&state=" + pendingRequestUUID + "&redirect_uri=https://sssvendorfake-silly-buffalo.cfapps.sap.hana.ondemand.com/oauthcallback";
        URI uri = URI.create(url);

        return Response.seeOther(uri).build();
    }

    @GET
    @Path("jwttest")
    public Response doJwt(@HeaderParam("X-SAP-AA-UserId") String sapUserId, @HeaderParam("X-SAP-AA-TenantId") String sapTenantId, @HeaderParam("X-SAP-AA-ShouldUseSharedAuth") boolean shouldUseSharedAuth, @HeaderParam("X-SAP-AA-ActOnBehalfOfUserId") String sapOnBehalfUserId, @HeaderParam("X-SAP-AA-Signature") String signature) {

        Response checkAuthenticationResponse = CommonUtility.checkAuthenticationSignature(sapUserId, sapTenantId, shouldUseSharedAuth, sapOnBehalfUserId, signature);
        if(checkAuthenticationResponse != null) {
            return checkAuthenticationResponse;
        }

        Response checkTenantResponse = CommonUtility.performAndCheckUserAndTenant(sapUserId, sapTenantId);
        if(checkTenantResponse != null) {
            return checkTenantResponse;
        }

        SapSetupNewTenantPayload sapTenantPayload = InMemoryDataStore.getInstance().getTenant(sapTenantId);
        String docusignIntegrationKey = sapTenantPayload.getDocusignIntegrationKey();

        SapConnectedUsers sapConnectedUsers = InMemoryDataStore.getInstance().getConnectedUsers(sapUserId, sapTenantId);
        String docusignUserId = sapConnectedUsers.getDocusignUserId();

        Date now = Calendar.getInstance().getTime();
        long nowEpochTime = now.getTime();

        DocusignJwtHeader docusignJwtHeader = new DocusignJwtHeader();
        String jwtHeaderJson = CommonUtility.getConfiguredGsonInstance().toJson(docusignJwtHeader);
        String jwtBase64HeaderJson = Base64.getUrlEncoder().encodeToString(jwtHeaderJson.getBytes());


        DocusignJwtBody docusignJwtBody = new DocusignJwtBody(docusignIntegrationKey, docusignUserId, nowEpochTime, nowEpochTime + 60 * 60 * 1000, docuSignEnvironmentUrlString, docuSignRequestedScope);
        String jwtBodyJson = CommonUtility.getConfiguredGsonInstance().toJson(docusignJwtBody);
        String jwtBase64BodyJson = Base64.getUrlEncoder().encodeToString(jwtBodyJson.getBytes(StandardCharsets.UTF_8));

        String docuSignPrivateKey = "-----BEGIN RSA PRIVATE KEY-----\n" +
                "MIIEogIBAAKCAQEAm9/gDjc0jBIH2Xu5QyuisUPjft7WrzUB22KjqZEoSGhThqaR\n" +
                "nkZAnBcHNFEmhU1iglvD75dsSAP8u2Kw2aXTcJ9gxoQZ+GImEK11/SJyxemhlviC\n" +
                "GRe4CWUqI0tV2aIwCh4scJjhUFV6bFuJzIZcF6JjFRmKc0cBLNsEljgCcw3ZdAXI\n" +
                "48hsLfJ8aPJMnTO3ppV1DVOe/mL8NOdMm/2WAaM3QNVQsWSCFgrBiJDZm5GYjXjR\n" +
                "FIiCd1Tjhtdti4pbwTu8Uh3ONQYFdVfc33xmjUY9fglLT1gd5Cnct+tpq0b/lxgg\n" +
                "+i1DSxBLywYDuSXDqD+vOKtotISO5/VWnPnBXwIDAQABAoIBAEWZhkLytXzhxC2B\n" +
                "CC0M+90pEhihSbz77zNVbPyW+ySxqLIUCCti9RB9W9MpdQjKj/TjIq8FruzNEvlc\n" +
                "zNiyKaI9GAosDCiTMqn5uhhoXFUwx91QY07u/1b6nAEcb135BHhQFsnECb9EG6Ds\n" +
                "krAHQnGvKYzPVbm5d7XeqcKlibpzOxi3eTtMdw5atWm7FwbwxMBt0xqpDmWg3tDV\n" +
                "ipe019eOk/D74uH4L20oYVg65Y5ogMSxLieiN6VUhAZSua7bs2Ce6o/z0OHCwzFO\n" +
                "hIbTjO37JvrEG63BM5/JgEjqF1kuXIQHoNXXjl52k+7ODdLySown3oEnvZlAIflE\n" +
                "8LZNxJECgYEA995Zj0c6C6/w8Quj/ItDhQxiESs/c4bpobDw5R83S/rBNd2ptdNr\n" +
                "JxO8Xau3iYVDMWweP8SkKuKBMrgIL4o8hoYkieQwg2GAV/rpIY1gYpFnl7srJnUO\n" +
                "F+NDZtoL9HrjcWmtGbNomeCIPVOuUZ1wqhoiV/ossvI6saoZENyjLocCgYEAoPzw\n" +
                "2wKN/ShNLwySmo1pr/IN6rB7ytPaa5X9UgRcH9beZ9DSiA0FgxlnPU3HJQRPgyhw\n" +
                "uDMdn4onE2tliX0WoSHXMXWhfXt5YwM2xUgwMO+8NLwLGOmbvwvsA9Ilnc4EcnNn\n" +
                "Hn+5gYPkosEgGpar+tYMPa54bBKQl4O7w5519GkCgYAUR5roQBmdry11a1Blbzd9\n" +
                "AUBcyz8LwrQGyKVM+braeo+oSpSCDeQsdE7rEwuXMtIGjyQCb6JG5/VOIwR4b0T4\n" +
                "dK00ovjdJvMLP7onRpvmNKNXJLcpFFas2alAFwL3Y76MHutMuQML4/UBn4EZqFn5\n" +
                "cN3yeMODeJIYyyP13zdyrQKBgH1GuEtFvqaNERsWxpLcjqzrSOcjtQGOQL9N9dY4\n" +
                "LFReia3x85MJxwtQ3mT3PIxSwWlINAczR88Z2/Shs179Z3m2ctY7OpMCXeCt5JY6\n" +
                "6b17IVNMLbqSN6/AoEYM51bYtd82bL1wGTRvJaF9dfUa4PQOU3JAbddzzu8JBTlh\n" +
                "+1pZAoGAf9av9DdXuV2la0TaOkeFsgLgZI2YtinOH5+QptKDfDS13ZSUyQKPnr7U\n" +
                "nAJf7DIbC0VTWBo+yVzzMUYeajfXs1yBiGfz2pSTgCpLJhzKOvEFP7ZfpLa35QwL\n" +
                "BwgANQglvgHzTTzIbDgQQ+BXoEeQVwqj5hFTvXQzp/Ioqsc4dts=\n" +
                "-----END RSA PRIVATE KEY-----\n";

        String docuSignCombinedToken1 = jwtBase64HeaderJson + "." + jwtBase64BodyJson;

        String docusignSignature = null;
        try {
            docusignSignature = CommonUtility.signSHA256RSAfromPKCS1(docuSignCombinedToken1, docuSignPrivateKey);

            String docusignCombinedToken2 = docuSignCombinedToken1 + "." + docusignSignature;

            return this.obtainJwtAccessToken(docusignCombinedToken2);
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

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Token signing failed!").build();
    }

    private Response obtainJwtAccessToken(String signedDocusignToken) {
        try {
            retrofit2.Response<DocusignAccessTokenResponse> accessTokenResponse = DocusignService.getInstance().obtainJwtAccessToken(signedDocusignToken).execute();

            if(accessTokenResponse.isSuccessful()) {
                System.out.println(accessTokenResponse.body().getAccess_token());
            } else {
                 System.out.println("Not successful!");
            }

            return Response.status(Response.Status.OK).entity(signedDocusignToken).build();
        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Docusign rest call failed!").build();
        }
    }

    @GET
    @Path("startAuthCodeGrant")
    public Response startAuthCodeGrant(@QueryParam("sapUserId") String sapUserId, @QueryParam("sapTenantId") String sapTenantId) {
        Response checkTenantResponse = CommonUtility.checkTenant(sapTenantId);
        if(checkTenantResponse != null) {
            return checkTenantResponse;
        }

        SapSetupNewTenantPayload sapTenantPayload = InMemoryDataStore.getInstance().getTenant(sapTenantId);
        String docusignIntegrationKey = sapTenantPayload.getDocusignIntegrationKey();

        String pendingRequestUUID = UUID.randomUUID().toString();
        OAuthPendingRequestContext oAuthPendingRequestContext = new OAuthPendingRequestContext(sapUserId, sapTenantId);

        InMemoryDataStore.getInstance().savePendingRequestContext(pendingRequestUUID, oAuthPendingRequestContext);

        String url = "https://account-d.docusign.com/oauth/auth?response_type=code&scope=signature&client_id=" + docusignIntegrationKey + "&state=" + pendingRequestUUID + "&redirect_uri=https://sssvendorfake-silly-buffalo.cfapps.sap.hana.ondemand.com/oauthcallback";
        URI uri = URI.create(url);

        return Response.seeOther(uri).build();
    }

    private Response obtainIndividualAccessToken(String token, String sapUserId, String sapTenantId) {

        SapSetupNewTenantPayload sapTenantPayload = InMemoryDataStore.getInstance().getTenant(sapTenantId);
        String docusignIntegrationKey = sapTenantPayload.getDocusignIntegrationKey();
        String docusignSecretKey = sapTenantPayload.getDocusignSecretKey();

        SapConnectedUsers sapConnectedUsers = InMemoryDataStore.getInstance().getConnectedUsers(sapUserId, sapTenantId);
        if(sapConnectedUsers == null) {
            sapConnectedUsers = new SapConnectedUsers(sapUserId, sapTenantId, null, null, null, null);
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

            return Response.status(Response.Status.OK).entity(sapConnectedUsers).build();
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