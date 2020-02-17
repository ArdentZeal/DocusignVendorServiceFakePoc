package com.sap.sss.vendorfake.routers;

import com.sap.sss.vendorfake.docusign.DocusignJwtAccessTokenResponse;
import com.sap.sss.vendorfake.docusign.DocusignJwtBody;
import com.sap.sss.vendorfake.docusign.DocusignJwtHeader;
import com.sap.sss.vendorfake.docusign.DocusignService;
import com.sap.sss.vendorfake.models.OAuthCallbackData;
import com.sap.sss.vendorfake.utiilities.CommonUtility;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class OAuthRouter {

    // should be created for each tenant, static for now
    private static String docuSignIntegrationKey = "bc46e8ab-7ff9-4bf0-a95e-9d226f408349";

    private static String docuSignEnvironmentUrlString = "account-d.docusign.com";
    private static String docuSignRequestedScope = "signature impersonation";

    // should be matched for each SAP User, static for now
    private static String docuSignUserId = "8ea115a3-963f-45e4-bd42-37331705769f";

    private static List<OAuthCallbackData> savedOAuthData = new ArrayList<>();

    @GET
    @Path("obtainConsent")
    public Response obtainConsent() {
        String url = "https://account-d.docusign.com/oauth/auth?response_type=code&scope=signature%20impersonation&client_id=" + docuSignIntegrationKey + "&redirect_uri=https://sssvendorfake-silly-buffalo.cfapps.sap.hana.ondemand.com/oauthcallback";
        URI uri = URI.create(url);

        return Response.seeOther(uri).build();
    }

    @GET
    @Path("jwttest")
    public Response doJwt() {

        Date now = Calendar.getInstance().getTime();
        long nowEpochTime = now.getTime();

        System.out.println(nowEpochTime);

        DocusignJwtHeader docusignJwtHeader = new DocusignJwtHeader();
        String jwtHeaderJson = CommonUtility.getConfiguredGsonInstance().toJson(docusignJwtHeader);
        System.out.println(jwtHeaderJson);
        String jwtBase64HeaderJson = Base64.getEncoder().encodeToString(jwtHeaderJson.getBytes());

        DocusignJwtBody docusignJwtBody = new DocusignJwtBody(docuSignIntegrationKey, docuSignUserId, nowEpochTime, nowEpochTime + 60 * 60 * 1000, docuSignEnvironmentUrlString, docuSignRequestedScope);
        String jwtBodyJson = CommonUtility.getConfiguredGsonInstance().toJson(docusignJwtBody);
        System.out.println(jwtBodyJson);
        String jwtBase64BodyJson = Base64.getEncoder().encodeToString(jwtBodyJson.getBytes());

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

        System.out.println(docuSignCombinedToken1);

        String signedDocusignToken = null;
        try {
            signedDocusignToken = CommonUtility.signSHA256RSAfromPKCS1(docuSignCombinedToken1, docuSignPrivateKey);

            String docusignCombinedToken2 = docuSignCombinedToken1 + "." + signedDocusignToken;

            return this.obtainAccessToken(docusignCombinedToken2);
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

    private Response obtainAccessToken(String signedDocusignToken) {
        try {
            retrofit2.Response<DocusignJwtAccessTokenResponse> accessTokenResponse = DocusignService.getInstance().obtainAccessToken(signedDocusignToken).execute();

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
    @Path("oauthcallback")
    public Response getOAuthCallback(@QueryParam("code") String code, @QueryParam("state") String state)
    {
        OAuthCallbackData oAuthData = new OAuthCallbackData(code, state);

        savedOAuthData.add(0, oAuthData);
        if(savedOAuthData.size() > 10) {
            savedOAuthData.remove(savedOAuthData.size() - 1);
        }

        return Response.status(Response.Status.OK).entity(oAuthData).build();
    }

    @GET
    @Path("oauthsaved")
    public Response getSavedOAuthCallback()
    {
        return Response.status(Response.Status.OK).entity(savedOAuthData).build();
    }
}