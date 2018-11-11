package com.github.and11.basex.utils.streamhandlers;

import com.github.and11.basex.utils.UrlStreamHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class DefaultUrlStreamHandler implements UrlStreamHandler {



    @Override
    public boolean canHandle(String url) {
        System.out.println("canhandle: " + url);
        try {
            URL u = new URL(url);

            return true;
        } catch (MalformedURLException e) {
            System.out.println("WONT HANDLE");
            return false;
        }
    }

    @Override
    public InputStream openStream(String pUrl) throws UnresolvableUrlException {

        try {
            URL url = new URL(pUrl);
            return url.openStream();
        } catch (MalformedURLException e) {
            throw new UnresolvableUrlException(pUrl, e);
        } catch (IOException e) {
            throw new UnresolvableUrlException(pUrl, e);
        }

    }
}
