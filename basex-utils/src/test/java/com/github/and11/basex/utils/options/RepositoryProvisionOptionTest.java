package com.github.and11.basex.utils.options;

import org.junit.Test;

import static com.github.and11.basex.utils.CoreOptions.repository;
import static com.github.and11.basex.utils.CoreOptions.url;
import static org.junit.Assert.*;

public class RepositoryProvisionOptionTest {

    @Test
    public void testXARgetUrl(){
        RepositoryProvisionOption option = new RepositoryProvisionOption("http://somewhere").type(RepositoryProvisionOption.RepositoryType.XAR);
        assertEquals("repo:http://somewhere@xar", option.getURL());
    }

    @Test
    public void testJARgetUrl(){
        RepositoryProvisionOption option = new RepositoryProvisionOption("http://somewhere").type(RepositoryProvisionOption.RepositoryType.JAR);
        assertEquals("repo:http://somewhere@jar", option.getURL());
    }

    @Test
    public void testXQMgetUrl(){
        RepositoryProvisionOption option = new RepositoryProvisionOption("http://somewhere").type(RepositoryProvisionOption.RepositoryType.XQM);
        assertEquals("repo:http://somewhere@xqm", option.getURL());
    }
}