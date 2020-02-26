package com.sap.sss.vendorfake.models;

public class SapConnectedUsers {

    private String sapUserId;
    private String sapTenantId;
    private String docusignUserId;
    private String docusignAccountId;
    private String docusignAccessToken;
    private String docusignRefreshToken;

    public SapConnectedUsers(String sapUserId, String sapTenantId, String docusignUserId, String docusignAccountId, String docusignAccessToken, String docusignRefreshToken) {
        this.sapUserId = sapUserId;
        this.sapTenantId = sapTenantId;
        this.docusignUserId = docusignUserId;
        this.docusignAccountId = docusignAccountId;
        this.docusignAccessToken = docusignAccessToken;
        this.docusignRefreshToken = docusignRefreshToken;
    }

    public String getSapUserId() {
        return sapUserId;
    }

    public void setSapUserId(String sapUserId) {
        this.sapUserId = sapUserId;
    }

    public String getSapTenantId() {
        return sapTenantId;
    }

    public void setSapTenantId(String sapTenantId) {
        this.sapTenantId = sapTenantId;
    }

    public String getDocusignUserId() {
        return docusignUserId;
    }

    public void setDocusignUserId(String docusignUserId) {
        this.docusignUserId = docusignUserId;
    }

    public String getDocusignAccountId() {
        return docusignAccountId;
    }

    public void setDocusignAccountId(String docusignAccountId) {
        this.docusignAccountId = docusignAccountId;
    }

    public String getDocusignAccessToken() {
        return docusignAccessToken;
    }

    public void setDocusignAccessToken(String docusignAccessToken) {
        this.docusignAccessToken = docusignAccessToken;
    }

    public String getDocusignRefreshToken() {
        return docusignRefreshToken;
    }

    public void setDocusignRefreshToken(String docusignRefreshToken) {
        this.docusignRefreshToken = docusignRefreshToken;
    }
}
