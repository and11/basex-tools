package com.github.and11.basex.utils.streamhandlers;

import com.github.and11.basex.utils.UrlStreamHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ClasspathUrlStreamHandler implements UrlStreamHandler {

    private final ClassLoader classLoader;

    public ClasspathUrlStreamHandler(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public InputStream openStream(String url) throws UnresolvableUrlException {
        String path = url.replaceFirst("classpath:", "");
        URL resource = classLoader.getResource(path);
        if(resource == null){
            throw new UnresolvableUrlException(url, "can't find resource by path: " + path);
        }
        try {
            System.out.println("found resource: " + resource);
            return resource.openStream();
        } catch (IOException e) {
            throw new UnresolvableUrlException(url, "can't read resource by path: " + path);
        }
    }

    @Override
    public boolean canHandle(String url) {
        return url != null && url.toLowerCase().startsWith("classpath:");
    }
}
