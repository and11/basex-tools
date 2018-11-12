package com.github.and11.basex.utils;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionUtils {
    static String writeFunctionArguments(String... args) {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream();
             OutputStreamWriter oWriter = new OutputStreamWriter(stream);
        ) {

            CSVWriter writer = new CSVWriter(oWriter, ';', '"', '"', "");
            writer.writeNext(args, false);
            writer.close();

            return stream.toString(StandardCharsets.UTF_8.name());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static String[] readFunctionArguments(String args) throws IOException {
        System.out.println("reading arguments: " + args);

        if(StringUtils.isEmpty(args)){
            return new String[]{""};
        }

        CSVParser parser = new CSVParserBuilder()
                .withSeparator(';')
                .build();

        try (ByteArrayInputStream is = new ByteArrayInputStream(args.getBytes(StandardCharsets.UTF_8))) {
            CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(is))
                    .withSkipLines(0)
                    .withCSVParser(parser)
                    .build();

            List<String[]> res = csvReader.readAll();

            return res.get(0);
        }
    }

    public static String composeUrl(String namespace, String name, String mode, String... arguments){
        StringBuilder sb = new StringBuilder();

        sb.append("xqf:")
                .append(mode)
                .append(":")
                .append(namespace)
                .append("#")
                .append(name);

        if (arguments != null && arguments.length > 0) {
            sb.append("@").append(writeFunctionArguments(arguments));
        }

        return sb.toString();
    }

    public static class Function {
        private String namespace;
        private String name;
        private String mode;
        private String[] arguments;

        public String getNamespace() {
            return namespace;
        }

        public String getName() {
            return name;
        }

        public String getMode() {
            return mode;
        }

        public String[] getArguments() {
            return arguments;
        }

        @Override
        public String toString() {
            return "Function{" +
                    "namespace='" + namespace + '\'' +
                    ", name='" + name + '\'' +
                    ", mode='" + mode + '\'' +
                    ", arguments=" + Arrays.toString(arguments) +
                    '}';
        }
    }

    public static Pattern urlPattern = Pattern.compile("^xqf:([^:]+):([^#]+)#([^@]+)(?:@(.*))?$");

    public static Function parseUrl(String url) throws UrlStreamHandler.UnresolvableUrlException, IOException {

        Function function = new Function();

        Matcher matcher = urlPattern.matcher(url);
        if(!matcher.matches()){
            throw new UrlStreamHandler.UnresolvableUrlException(url);
        }

        function.mode = matcher.group(1);
        function.namespace = matcher.group(2);
        function.name = matcher.group(3).replace("@$","");
        System.out.println("groupCount: " + matcher.groupCount());
        System.out.println("url: " + url);
        if(matcher.group(4) != null){
            function.arguments = readFunctionArguments(matcher.group(4));
        }

        return function;
    }


}
