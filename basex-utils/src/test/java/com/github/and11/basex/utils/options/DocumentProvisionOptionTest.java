package com.github.and11.basex.utils.options;

import org.junit.Test;

import static org.junit.Assert.*;

public class DocumentProvisionOptionTest {
    @Test
    public void test(){
        DocumentProvisionOption option = new DocumentProvisionOption("http://some.url");
        assertEquals("doc:http://some.url", option.getURL());
    }

    @Test
    public void testCollection(){
        DocumentProvisionOption option = new DocumentProvisionOption("http://some.url").collection("some-collection");
        assertEquals("doc:http://some.url@some-collection", option.getURL());
    }
}