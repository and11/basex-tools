package com.github.and11.basex.utils.container;

import com.github.and11.basex.utils.BaseXContainer;
import com.github.and11.basex.utils.UrlStreamHandler;
import org.junit.Test;

import java.io.IOException;

import static com.github.and11.basex.utils.CoreOptions.function;
import static com.github.and11.basex.utils.CoreOptions.mavenBundle;
import static com.github.and11.basex.utils.CoreOptions.document;
import static com.github.and11.basex.utils.CoreOptions.repository;
import static com.github.and11.basex.utils.CoreOptions.url;


public class DefaultBaseXContainerTest {

//    @Test
    public void test() throws IOException, BaseXContainer.BaseXContainerException {

        System.out.println("RC: " + getClass().getClassLoader().getResource("test.xml"));
        DefaultBaseXContainer c = new DefaultBaseXContainer(DefaultBaseXContainersFactory.getStandardResolvers(getClass().getClassLoader()),
                repository(mavenBundle().groupId("com.nexign.oapi.spec.tests").artifactId("spec-miner-xqf").version("1.2.0").type("xar")).xar(),
//                repository(url("file://var/tesb_delete_call.txt")),
                document(url("classpath:test.xml")),
                document(function("test", "func"))
//                document(url("http://yandex.ru")).collection("ddd"),
//                document(url("file://var/tesb_delete_call.txt"))
        );

        //c.setClassLoader(getClass().getClassLoader());
        c.start();
        //c.provision(repository(mavenBundle()), document(url("file:///var/tesb_delete_call.txt")));

    }

    @Test
    public void test2() throws IOException, BaseXContainer.BaseXContainerException, UrlStreamHandler.UnresolvableUrlException {

        DefaultBaseXContainer c = new DefaultBaseXContainer(DefaultBaseXContainersFactory.getStandardResolvers(getClass().getClassLoader()),
                repository(mavenBundle().groupId("com.nexign.oapi.spec.tests").artifactId("spec-miner-xqf").version("1.2.0").type("xar")).xar(),
                repository(url("classpath:test.xqm")).xqm(),
                //document(url("classpath:test.xml"))
                document(function("test", "create"))
//                document(function("test", "func"))
//                document(url("http://yandex.ru")).collection("ddd"),
//                document(url("file://var/tesb_delete_call.txt"))
        );

        //c.setClassLoader(getClass().getClassLoader());
        c.start();
        c.export(function("test","export"), System.out);
        //c.provision(repository(mavenBundle()), document(url("file:///var/tesb_delete_call.txt")));

    }

}