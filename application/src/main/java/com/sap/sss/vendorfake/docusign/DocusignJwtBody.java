package com.sap.sss.vendorfake.docusign;

public class DocusignJwtBody {

    private String iss;
    private String sub;
    private long iat;
    private long exp;
    private String aud;
    private String scope;

    public DocusignJwtBody(String iss, String sub, long iat, long exp, String aud, String scope) {
        this.iss = iss;
        this.sub = sub;
        this.iat = iat;
        this.exp = exp;
        this.aud = aud;
        this.scope = scope;
    }

    public String getIss() {
        return iss;
    }

    public void setIss(String iss) {
        this.iss = iss;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public long getIat() {
        return iat;
    }

    public void setIat(long iat) {
        this.iat = iat;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public String getAud() {
        return aud;
    }

    public void setAud(String aud) {
        this.aud = aud;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }



}
