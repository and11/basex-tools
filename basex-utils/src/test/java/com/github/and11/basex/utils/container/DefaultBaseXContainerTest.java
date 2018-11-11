package com.github.and11.basex.utils.container;

import com.github.and11.basex.utils.BaseXContainer;
import com.github.and11.basex.utils.UrlStreamHandler;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static com.github.and11.basex.utils.CoreOptions.createDatabase;

import static com.github.and11.basex.utils.CoreOptions.function;
import static com.github.and11.basex.utils.CoreOptions.mavenBundle;
import static com.github.and11.basex.utils.CoreOptions.document;
import static com.github.and11.basex.utils.CoreOptions.openDatabase;
import static com.github.and11.basex.utils.CoreOptions.repository;
import static com.github.and11.basex.utils.CoreOptions.url;
import static com.github.and11.basex.utils.CoreOptions.workingDirectory;


public class DefaultBaseXContainerTest {

    //    @Test
    public void test() throws IOException, BaseXContainer.BaseXContainerException {
//        DefaultBaseXContainer c = new DefaultBaseXContainer(DefaultBaseXContainersFactory.getStandardResolvers(getClass().getClassLoader()),
////                repository(mavenBundle().groupId("com.nexign.oapi.spec.tests").artifactId("spec-miner-xqf").version("1.2.0").type("xar")).xar(),
////                repository(url("file://var/tesb_delete_call.txt")),
//                document(url("classpath:test.xml")),
//                document(function("test", "func"))
////                document(url("http://yandex.ru")).collection("ddd"),
////                document(url("file://var/tesb_delete_call.txt"))
//        );

        //c.setClassLoader(getClass().getClassLoader());
//        c.start();
        //c.provision(repository(mavenBundle()), document(url("file:///var/tesb_delete_call.txt")));

    }

    //    @Test
//    public void test2() throws Exception {
//
//        DefaultBaseXContainer c = new DefaultBaseXContainer(DefaultBaseXContainersFactory.getStandardResolvers(getClass().getClassLoader()),
//                workingDirectory(Paths.get("target/testdb")),
//                database("test1"),
//                repository(url("classpath:test.xqm")).xqm(),
//                document(url("classpath:test.xml")),
//                document(function("test", "create"))
//        );
//
//        c.start();
//        c.export(function("test","export").arguments("test1"), System.out);
//        c.close();
//
//        DefaultBaseXContainer c2 = new DefaultBaseXContainer(DefaultBaseXContainersFactory.getStandardResolvers(getClass().getClassLoader()),
//                workingDirectory(Paths.get("target/testdb")),
//                database("test2"),
//                repository(url("classpath:test.xqm")).xqm(),
//                document(url("classpath:test2.xml")),
//                document(function("test", "create"))
//        );
//
//        c2.start();
//        c2.export(function("test","export").arguments("test1"), System.out);
//        c2.export(function("test","export").arguments("test2"), System.out);
//
//    }
//
    @Test
    public void test3() throws Exception {

        DefaultBaseXContainer c = new DefaultBaseXContainer(DefaultBaseXContainersFactory.getStandardResolvers(getClass().getClassLoader()),
                workingDirectory(Paths.get("target/testdb")),
                createDatabase("test1"),
//                createDatabase("test2"),
//                createDatabase("test3"),
                openDatabase("test1").collection("samples"));

        c.start();
        c.provision(
                url("repo:classpath:test.xqm@xqm"),
                url("doc:classpath:test.xml@samples2"));

        c.export(function("test", "export").arguments("test1/samples2"), System.out);
        //c.export(function("test", "export").arguments("test1/samples"), System.out);
        c.close();

    }
}