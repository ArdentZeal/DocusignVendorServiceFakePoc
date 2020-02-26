package com.sap.sss.vendorfake.docusign;

public class DocusignAccount {
    private String account_id;
    private boolean is_default;
    private String account_name;
    private String base_uri;

    public DocusignAccount(String account_id, boolean is_default, String account_name, String base_uri) {
        this.account_id = account_id;
        this.is_default = is_default;
        this.account_name = account_name;
        this.base_uri = base_uri;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public boolean isIs_default() {
        return is_default;
    }

    public void setIs_default(boolean is_default) {
        this.is_default = is_default;
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }

    public String getBase_uri() {
        return base_uri;
    }

    public void setBase_uri(String base_uri) {
        this.base_uri = base_uri;
    }
}
