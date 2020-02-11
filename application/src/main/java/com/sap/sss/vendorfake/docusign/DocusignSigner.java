package com.sap.sss.vendorfake.docusign;

public class DocusignSigner {
    private String email;
    private String name;
    private String recipientId;

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getRecipientId() {
        return this.recipientId;
    }
}

