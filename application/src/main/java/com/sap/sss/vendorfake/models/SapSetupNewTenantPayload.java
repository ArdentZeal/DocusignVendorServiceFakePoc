package com.sap.sss.vendorfake.models;

public class SapSetupNewTenantPayload {
    private String tenantId;
    private String docusignIntegrationKey;
    private String docusignSecretKey;

    public SapSetupNewTenantPayload(String tenantId, String docusignIntegrationKey, String docusignSecretKey) {
        this.tenantId = tenantId;
        this.docusignIntegrationKey = docusignIntegrationKey;
        this.docusignSecretKey = docusignSecretKey;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getDocusignIntegrationKey() {
        return docusignIntegrationKey;
    }

    public void setDocusignIntegrationKey(String docusignIntegrationKey) {
        this.docusignIntegrationKey = docusignIntegrationKey;
    }

    public String getDocusignSecretKey() {
        return docusignSecretKey;
    }

    public void setDocusignSecretKey(String docusignSecretKey) {
        this.docusignSecretKey = docusignSecretKey;
    }
}
