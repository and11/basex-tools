package com.github.and11.basex.utils.streamhandlers;

import com.github.and11.basex.utils.UrlStreamHandler;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class CompositeUrlStreamHandler implements UrlStreamHandler {
    private ArrayList<UrlStreamHandler> handlers = new ArrayList<>();

    public CompositeUrlStreamHandler(UrlStreamHandler... handlers) {
        this.handlers.addAll(Arrays.asList(handlers));
    }

    public CompositeUrlStreamHandler addHandler(UrlStreamHandler... handlers){
        this.handlers.addAll(Arrays.asList(handlers));
        return this;
    }

    public CompositeUrlStreamHandler removeHandler(UrlStreamHandler... handlers){
        this.handlers.removeAll(Arrays.asList(handlers));
        return this;
    }

    private Optional<UrlStreamHandler> findHandler(String url){
        return handlers.stream().filter( h -> h.canHandle(url)).findFirst();
    }

    @Override
    public boolean canHandle(String url) {
        return findHandler(url).isPresent();
    }

    @Override
    public InputStream openStream(String url) throws UnresolvableUrlException {
        return findHandler(url).orElseThrow(() -> new UnresolvableUrlException(url, "can't find any handler for url " + url)).openStream(url);
    }
}
