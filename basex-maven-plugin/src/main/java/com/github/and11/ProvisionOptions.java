package com.github.and11;

import com.github.and11.basex.utils.Option;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.component.annotations.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProvisionOptions {
    @Parameter
    private List<String> descriptors;

    @Parameter
    private String databaseName;

    @Parameter
    private String defaultCollectionName;

    public String getDefaultCollectionName() {
        return defaultCollectionName;
    }

    public ProvisionOptions setDefaultCollectionName(String defaultCollectionName) {
        this.defaultCollectionName = defaultCollectionName;
        return this;
    }

    @Parameter
    private File databaseDir;

    public List<String> getDescriptors() {
        return descriptors;
    }

    public ProvisionOptions setDescriptors(List<String> descriptors) {
        this.descriptors = descriptors;
        return this;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public ProvisionOptions setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
        return this;
    }

    public File getDatabaseDir() {
        return databaseDir;
    }

    public ProvisionOptions setDatabaseDir(File databaseDir) {
        this.databaseDir = databaseDir;
        return this;
    }

    @Override
    public String toString() {
        return "ProvisionOptions{" +
                "descriptors=" + descriptors +
                ", databaseName='" + databaseName + '\'' +
                ", databaseDir=" + databaseDir +
                '}';
    }
}
