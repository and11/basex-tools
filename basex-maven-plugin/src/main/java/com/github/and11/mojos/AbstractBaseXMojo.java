package com.github.and11.mojos;

import com.github.and11.basex.utils.BaseXContainersFactory;
import com.github.and11.basex.utils.container.DefaultBaseXContainersFactory;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

public abstract class AbstractBaseXMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.build.directory}/${project.artifactId}-${project.version}.db", required = true)
    private File databaseDir;

    @Parameter(defaultValue = "main", required = true)
    private String databaseName;

    protected File getDatabaseDir() {
        return databaseDir;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    @Parameter(defaultValue = "collection", required = true)
    private String defaultCollection;

    @Parameter
    private File mavenSettings;

    protected String getDefaultCollection() {
        return defaultCollection;
    }

    protected BaseXContainersFactory getBaseXContainersFactory(){
        return new DefaultBaseXContainersFactory(getClass().getClassLoader()).setMavenSettings(mavenSettings);
    }
}
