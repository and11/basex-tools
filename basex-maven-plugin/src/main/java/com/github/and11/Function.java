package com.github.and11;

import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class Function {

    @Parameter(required = true,readonly = true)
    private String descriptor;

    @Parameter(required = false,readonly = true)
    private String recipientCollectionName;

    public String getDescriptor() {
        return descriptor;
    }

    public Function setDescriptor(String descriptor) {
        this.descriptor = descriptor;
        return this;
    }

    public Function setRecipientCollectionName(String recipientCollectionName) {
        this.recipientCollectionName = recipientCollectionName;
        return this;
    }

    @Parameter(required = true)
    private File output;

    public Optional<String> getRecipientCollectionName() {
        return Optional.ofNullable(recipientCollectionName);
    }

    @Parameter
    private List<String> arguments;

    public Optional<List<String>> getArguments() {
        return Optional.ofNullable(arguments);
    }

    public Function setArguments(List<String> arguments) {
        this.arguments = arguments;
        return this;
    }

    public File getOutput() {
        return output;
    }

    public Function setOutput(File output) {
        this.output = output;
        return this;
    }

}
