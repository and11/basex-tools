package com.github.and11.basex.utils.options;

import com.github.and11.basex.utils.FunctionUtils;
import com.github.and11.basex.utils.UrlStreamHandler;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static com.github.and11.basex.utils.FunctionUtils.composeUrl;

public class DefaultFunctionUrlReference implements FunctionUrlReference {

    private String name;
    private String namespace;
    private String mode = "xml";
    private String[] arguments;

    public DefaultFunctionUrlReference() {
    }

    public DefaultFunctionUrlReference(UrlReference url) throws IOException, UrlStreamHandler.UnresolvableUrlException {
        FunctionUtils.Function descriptor = FunctionUtils.parseUrl(url.getURL());
        name = descriptor.getName();
        namespace = descriptor.getNamespace();
        mode = descriptor.getMode();
        arguments  =descriptor.getArguments();
    }

    @Override
    public FunctionUrlReference name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public FunctionUrlReference mode(String mode) {
        this.mode = mode;
        return this;
    }

    @Override
    public FunctionUrlReference namespace(String namespace) {
        this.namespace = namespace;
        return this;
    }

    @Override
    public FunctionUrlReference arguments(String... arguments) {
        this.arguments = arguments;
        return this;
    }


    @Override
    public String toString() {
        return "DefaultFunctionUrlReference{" +
                "name='" + name + '\'' +
                ", namespace='" + namespace + '\'' +
                ", arguments=" + Arrays.toString(arguments) +
                '}';
    }


    @Override
    public String getURL() {
        return composeUrl(namespace, name, mode, arguments);
    }
}
