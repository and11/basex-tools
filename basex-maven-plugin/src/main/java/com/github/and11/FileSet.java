package com.github.and11;

import org.apache.maven.plugins.annotations.Parameter;

public class FileSet {
    @Parameter
    private String id;

    @Parameter
    private String[] include;
    @Parameter
    private String[] exclude;
}
