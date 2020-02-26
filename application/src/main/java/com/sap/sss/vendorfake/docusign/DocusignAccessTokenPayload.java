package com.sap.sss.vendorfake.docusign;

public class DocusignAccessTokenPayload {
    private String grant_type;
    private String assertion;
    private String redirect_uri;

    public DocusignAccessTokenPayload(String grant_type, String assertion, String redirect_uri) {
        this.grant_type = grant_type;
        this.assertion = assertion;
        this.redirect_uri = redirect_uri;
    }

    public String getGrant_type() {
        return grant_type;
    }

    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

    public String getAssertion() {
        return assertion;
    }

    public void setAssertion(String assertion) {
        this.assertion = assertion;
    }

    public String getRedirect_uri() {
        return redirect_uri;
    }

    public void setRedirect_uri(String redirect_uri) {
        this.redirect_uri = redirect_uri;
    }
}
