package com.github.and11.basex.utils.resolvers;

import com.github.and11.basex.utils.BaseXContainer;
import com.github.and11.basex.utils.UrlStreamHandler;
import org.basex.query.QueryException;
import org.basex.query.QueryProcessor;
import org.basex.query.value.Value;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class XQFunctionUrlStreamHandler implements UrlStreamHandler {

    private final BaseXContainer.CloseableContext basexContext;

    public XQFunctionUrlStreamHandler(BaseXContainer.CloseableContext basexContext) {
        this.basexContext = basexContext;
    }

    @Override
    public boolean canHandle(String url) {
        return url != null && url.toLowerCase().startsWith("xqf:");
    }

    @Override
    public InputStream openStream(String url) throws UnresolvableUrlException {

        String[] parts = url.replaceFirst("xqf:", "").split("/");
        String namespace = parts[0];
        String name = parts[1];
        String outputMethod = "xml";

        String func = composeFunction(namespace, name);
        QueryProcessor qp = basexContext.buildQueryProcessor(func);

        try {
            Path temp = Files.createTempFile(UUID.randomUUID().toString(), ".tmp");
            qp.bind("targetFile", temp.toFile().getAbsolutePath());
            qp.bind("outputMethod", outputMethod);
            Value result = qp.value();
            return Files.newInputStream(temp, StandardOpenOption.DELETE_ON_CLOSE);
        } catch (IOException e) {
            throw new UnresolvableUrlException(url, e);
        } catch (QueryException e) {
            throw new UnresolvableUrlException(url, e);
        }
    }

    private String composeFunction(String namespace, String name, String... args) {
        StringBuilder sb = new StringBuilder();

        sb.append("import module namespace mns = '%s';\n")
                .append("declare namespace file = \"http://expath.org/ns/file\";\n")
                .append("declare variable $targetFile external;\n")
                .append("declare variable $outputMethod external;\n");


        List<String> arguments = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            sb.append("declare variable v").append(i).append(" external;\n");
            arguments.add("v" + i);
        }

        sb.append("file:write($targetFile, mns:%s(%s),\n")
                .append("<output:serialization-parameters>")
                .append("<output:method value=\"{$outputMethod}\"/>")
                .append("<output:omit-xml-declaration value=\"no\"/>")
                .append("<output:indents value=\"4\"/>")
                .append("<output:indent value=\"yes\"/>")
                .append("</output:serialization-parameters>")
                .append(")");

            return String.format(sb.toString(), namespace, name, String.join(",", arguments)

        );


    }
}
