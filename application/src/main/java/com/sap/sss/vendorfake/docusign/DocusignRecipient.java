package com.sap.sss.vendorfake.docusign;

import java.util.ArrayList;

public class DocusignRecipient {
    private ArrayList<DocusignSigner> signers;

    public void setSigners(ArrayList<DocusignSigner> signers) {
        this.signers = signers;
    }

    public ArrayList<DocusignSigner> getSigners() {
        return this.signers;
    }
}

