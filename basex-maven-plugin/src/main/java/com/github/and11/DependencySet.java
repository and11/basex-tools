package com.github.and11;

import org.apache.maven.plugins.annotations.Parameter;

import java.util.Arrays;
import java.util.List;

public class DependencySet {
    @Parameter
    private String id;

    public String getId() {
        return id;
    }

    public DependencySet setId(String id) {
        this.id = id;
        return this;
    }

    public List<String> getIncludes() {
        return includes;
    }

    @Override
    public String toString() {
        return "DependencySet{" +
                "id='" + id + '\'' +
                ", includes=" + includes +
                ", excludes=" + excludes +
                '}';
    }

    public DependencySet setIncludes(List<String> includes) {
        this.includes = includes;
        return this;
    }

    public List<String> getExcludes() {
        return excludes;
    }

    public DependencySet setExcludes(List<String> excludes) {
        this.excludes = excludes;
        return this;
    }

    @Parameter
    private List<String> includes;
    @Parameter
    private List<String> excludes;
}
