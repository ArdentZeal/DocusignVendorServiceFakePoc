package com.sap.sss.vendorfake.models;

public class OAuthPendingRequestContext {
    private String sapUserId;
    private String sapTenantId;

    public OAuthPendingRequestContext(String sapUserId, String sapTenantId) {
        this.sapUserId = sapUserId;
        this.sapTenantId = sapTenantId;
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
}
