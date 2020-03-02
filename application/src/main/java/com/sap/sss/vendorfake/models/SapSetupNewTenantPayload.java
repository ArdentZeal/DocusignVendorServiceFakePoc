package com.sap.sss.vendorfake.models;

public class SapSetupNewTenantPayload {
    private String tenantId;
    private boolean isSharedAuth;
    private String vendorAppIdentifier;
    private String vendorAppSecretKey;

    public SapSetupNewTenantPayload(String tenantId, boolean isSharedAuth, String vendorAppIdentifier, String vendorAppSecretKey) {
        this.tenantId = tenantId;
        this.isSharedAuth = isSharedAuth;
        this.vendorAppIdentifier = vendorAppIdentifier;
        this.vendorAppSecretKey = vendorAppSecretKey;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public boolean isSharedAuth() {
        return isSharedAuth;
    }

    public void setSharedAuth(boolean sharedAuth) {
        isSharedAuth = sharedAuth;
    }

    public String getVendorAppIdentifier() {
        return vendorAppIdentifier;
    }

    public void setVendorAppIdentifier(String vendorAppIdentifier) {
        this.vendorAppIdentifier = vendorAppIdentifier;
    }

    public String getVendorAppSecretKey() {
        return vendorAppSecretKey;
    }

    public void setVendorAppSecretKey(String vendorAppSecretKey) {
        this.vendorAppSecretKey = vendorAppSecretKey;
    }
}
