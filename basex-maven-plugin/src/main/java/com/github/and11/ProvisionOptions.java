package com.github.and11;

import com.github.and11.basex.utils.Option;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.ArrayList;
import java.util.List;

public class ProvisionOptions {
    @Parameter
    private List<String> repositories;

    @Parameter
    private List<String> documents;

    public List<String> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<String> repositories) {
        this.repositories = repositories;
    }

    public List<String> getDocuments() {
        return documents;
    }

    public void setDocuments(List<String> documents) {
        this.documents = documents;
    }

    @Override
    public String toString() {
        return "ProvisionOptions{" +
                "repositories=" + repositories +
                ", documents=" + documents +
                '}';
    }
}
