package com.sap.sss.vendorfake.docusign;

public class DocusignDocument {
    private String documentId;
    private String name;
    private String documentBase64;

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDocumentId() {
        return this.documentId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setDocumentBase64(String documentBase64) {
        this.documentBase64 = documentBase64;
    }

    public String getDocumentBase64() {
        return this.documentBase64;
    }
}

