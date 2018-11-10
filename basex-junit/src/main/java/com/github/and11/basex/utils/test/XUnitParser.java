package com.github.and11.basex.utils.test;



import com.github.and11.basex.utils.test.xunit.Testsuites;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.file.Path;

public class XUnitParser {
    public Testsuites parse(String data){
        try {
            JAXBContext ctx = JAXBContext.newInstance(Testsuites.class);
            Unmarshaller unm = ctx.createUnmarshaller();

            return (Testsuites) unm.unmarshal(new StringReader(data));
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
    public Testsuites parse(Path path){
        try (InputStream inputStream = new FileInputStream(path.toFile());
             Reader reader = new InputStreamReader(inputStream, "UTF-8");
        ){
            JAXBContext ctx = JAXBContext.newInstance(Testsuites.class);

            Unmarshaller unm = ctx.createUnmarshaller();


            return (Testsuites) unm.unmarshal(reader);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
