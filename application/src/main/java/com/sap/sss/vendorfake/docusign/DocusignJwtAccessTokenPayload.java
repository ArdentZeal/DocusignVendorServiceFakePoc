package com.sap.sss.vendorfake.docusign;

public class DocusignJwtAccessTokenPayload {
    private String grant_type = "urn:ietf:params:oauth:grant-type:jwt-bearer";
    private String assertion;

    public DocusignJwtAccessTokenPayload(String assertion) {
        this.assertion = assertion;
    }

    public String getAssertion() {
        return assertion;
    }

    public void setAssertion(String assertion) {
        this.assertion = assertion;
    }
}
