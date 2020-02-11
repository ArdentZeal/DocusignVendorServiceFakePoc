package com.sap.sss.vendorfake.docusign;

import java.util.ArrayList;

public class DocusignEnvelope {
    private DocusignRecipient recipients;
    private ArrayList<DocusignDocument> documents;
    private String emailSubject;
    private String status;

    public void setDocuments(ArrayList<DocusignDocument> documents) {
        this.documents = documents;
    }

    public ArrayList<DocusignDocument> getDocuments() {
        return this.documents;
    }

    public void setRecipient(DocusignRecipient recipients) {
        this.recipients = recipients;
    }

    public DocusignRecipient getRecipient() {
        return this.recipients;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public String getEmailSubject() {
        return this.emailSubject;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }
}

