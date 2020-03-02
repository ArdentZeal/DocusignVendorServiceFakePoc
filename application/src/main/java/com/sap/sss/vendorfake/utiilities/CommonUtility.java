package com.sap.sss.vendorfake.utiilities;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sap.sss.vendorfake.datastore.InMemoryDataStore;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.util.Base64;

public class CommonUtility {
    public static Gson getConfiguredGsonInstance() {
        GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls();

        return Converters.registerInstant(gsonBuilder).create();
    }

    // Create base64 encoded signature using SHA256/RSA.
    public static String signSHA256RSAfromPKCS8(String input, String strPk) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        // Remove markers and new line characters in private key
        String realPK = strPk.replaceAll("-----END PRIVATE KEY-----", "")
                .replaceAll("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("\n", "");

        byte[] b1 = Base64.getDecoder().decode(realPK);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(b1);
        KeyFactory kf = KeyFactory.getInstance("RSA");

        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(kf.generatePrivate(spec));
        privateSignature.update(input.getBytes(StandardCharsets.UTF_8));
        byte[] s = privateSignature.sign();
        return Base64.getEncoder().encodeToString(s);
    }

    public static PrivateKey stringToPrivateKey(String strPk) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        String realPK = strPk.replaceAll("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("-----BEGIN RSA PRIVATE KEY-----", "")
                .replaceAll("\n", "");

        byte[] bytes = Base64.getDecoder().decode(realPK);

        DerInputStream derReader = new DerInputStream(bytes);
        DerValue[] seq = derReader.getSequence(0);
        // skip version seq[0];
        BigInteger modulus = seq[1].getBigInteger();
        BigInteger publicExp = seq[2].getBigInteger();
        BigInteger privateExp = seq[3].getBigInteger();
        BigInteger prime1 = seq[4].getBigInteger();
        BigInteger prime2 = seq[5].getBigInteger();
        BigInteger exp1 = seq[6].getBigInteger();
        BigInteger exp2 = seq[7].getBigInteger();
        BigInteger crtCoef = seq[8].getBigInteger();

        RSAPrivateCrtKeySpec keySpec = new RSAPrivateCrtKeySpec(modulus, publicExp, privateExp, prime1, prime2, exp1, exp2, crtCoef);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    public static String signSHA256RSAfromPKCS1(String input, String strPk) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException, IOException {
        String realPK = strPk.replaceAll("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("-----BEGIN RSA PRIVATE KEY-----", "")
                .replaceAll("\n", "");

        byte[] bytes = Base64.getDecoder().decode(realPK);

        DerInputStream derReader = new DerInputStream(bytes);
        DerValue[] seq = derReader.getSequence(0);
        // skip version seq[0];
        BigInteger modulus = seq[1].getBigInteger();
        BigInteger publicExp = seq[2].getBigInteger();
        BigInteger privateExp = seq[3].getBigInteger();
        BigInteger prime1 = seq[4].getBigInteger();
        BigInteger prime2 = seq[5].getBigInteger();
        BigInteger exp1 = seq[6].getBigInteger();
        BigInteger exp2 = seq[7].getBigInteger();
        BigInteger crtCoef = seq[8].getBigInteger();

        RSAPrivateCrtKeySpec keySpec = new RSAPrivateCrtKeySpec(modulus, publicExp, privateExp, prime1, prime2, exp1, exp2, crtCoef);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(privateKey);
        privateSignature.update(input.getBytes(StandardCharsets.UTF_8));
        byte[] s = privateSignature.sign();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(s);
    }

    public static Response peformAndCheckAllAuthentication(String sapUserId, String sapTenantId, String signature) {
        Response checkAuthenticationSignatureResponse = CommonUtility.checkAuthenticationSignature(sapUserId, sapTenantId, signature);
        if(checkAuthenticationSignatureResponse != null) {
            return checkAuthenticationSignatureResponse;
        }

        Response performAndCheckUserAndTenantResponse = CommonUtility.performAndCheckUserAndTenant(sapUserId, sapTenantId);
        if(performAndCheckUserAndTenantResponse != null) {
            return performAndCheckUserAndTenantResponse;
        }

        return null;
    }

    public static Response checkAuthenticationSignature(String sapUserId, String sapTenantId, String signature) {
        // we do not really check signature for now, implement shared secret or private key later in real app

        if(false) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Signature could not be trusted!").build();
        }

        return null;
    }

    public static Response performAndCheckUserAndTenant(String sapUserId, String sapTenantId) {

        Response checkTenantResponse = CommonUtility.checkTenant(sapTenantId);
        if(checkTenantResponse != null) {
            return checkTenantResponse;
        }

        Response checkConnectedUsersResponse = CommonUtility.checkConnectedUsers(sapUserId, sapTenantId);
        if(checkConnectedUsersResponse != null) {
            return checkConnectedUsersResponse;
        }


        return null;
    }

    public static Response checkTenant(String sapTenantId) {
        if(InMemoryDataStore.getInstance().getTenant(sapTenantId) == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("We do not know tenantId " + sapTenantId + " yet, please call /setupNewTenant first!").build();
        }

        return null;
    }

    public static Response checkConnectedUsers(String sapUserId, String sapTenantId) {
        if(InMemoryDataStore.getInstance().getConnectedUsers(sapUserId, sapTenantId) == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("We do not know tenantId " + sapTenantId + " yet, please call /setupNewTenant first!").build();
        }

        return null;
    }
}