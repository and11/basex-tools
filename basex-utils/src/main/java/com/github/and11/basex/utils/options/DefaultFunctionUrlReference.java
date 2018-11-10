package com.github.and11.basex.utils.options;

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

public class DefaultFunctionUrlReference implements FunctionUrlReference {

    private String name;
    private String namespace;
    private String[] arguments;

    @Override
    public FunctionUrlReference name(String name) {
        this.name = name;
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

    static String writeFunctionArguments(String... args) {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream();
             OutputStreamWriter oWriter = new OutputStreamWriter(stream);
             ) {

            CSVWriter writer = new CSVWriter(oWriter, ';', '"');
            writer.writeNext(args,false);
            writer.close();

            return stream.toString(StandardCharsets.UTF_8.name());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static List<String> readFunctionArguments(String args) throws IOException {
        CSVParser parser = new CSVParserBuilder()
                .withSeparator(';')
                .build();

        try (ByteArrayInputStream is = new ByteArrayInputStream(args.getBytes(StandardCharsets.UTF_8))) {
            CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(is))
                    .withSkipLines(0)
                    .withCSVParser(parser)
                    .build();

            List<String[]> res = csvReader.readAll();

            return Arrays.asList(res.get(0));
        }


    }

    public static void main(String[] args) throws IOException {

        List<String> fa = readFunctionArguments("a;b;c");
        System.out.println("args: " + fa);

        System.out.println("res: " +writeFunctionArguments("a","b" ,"e;d"));
    }


    @Override
    public String getURL() {
        StringBuilder sb = new StringBuilder();

        sb.append("xqf:")
                .append(namespace)
                .append("/")
                .append(name);

        return sb.toString();
    }

}
