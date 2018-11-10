package com.github.and11.basex.utils.options;

public class DocumentProvisionOption
        extends AbstractUrlProvisionOption<DocumentProvisionOption>{

    public String getCollection() {
        return collection;
    }

    private String collection;

    public DocumentProvisionOption collection(String collection) {
        this.collection = collection;
        return this;
    }

    public DocumentProvisionOption(String url) {
        super(url);
    }

    public DocumentProvisionOption(UrlReference url) {
        super(url);
    }
}
