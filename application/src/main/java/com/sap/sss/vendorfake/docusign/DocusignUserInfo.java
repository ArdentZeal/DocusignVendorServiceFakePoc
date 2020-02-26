package com.sap.sss.vendorfake.docusign;

import java.util.List;

public class DocusignUserInfo {

    private String sub;
    private String name;
    private String given_name;
    private String family_name;
    private String email;
    private List<DocusignAccount> accounts;

    public DocusignUserInfo(String sub, String name, String given_name, String family_name, String email, List<DocusignAccount> accounts) {
        this.sub = sub;
        this.name = name;
        this.given_name = given_name;
        this.family_name = family_name;
        this.email = email;
        this.accounts = accounts;
    }


    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGiven_name() {
        return given_name;
    }

    public void setGiven_name(String given_name) {
        this.given_name = given_name;
    }

    public String getFamily_name() {
        return family_name;
    }

    public void setFamily_name(String family_name) {
        this.family_name = family_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<DocusignAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<DocusignAccount> accounts) {
        this.accounts = accounts;
    }
}
