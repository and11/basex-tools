package com.github.and11.basex.utils.options;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

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

    @Override
    public String getURL() {
        String url = "doc:" + super.getURL();
        if(collection != null){
            url = url + "@" + collection;
        }
        return url;
    }
}
