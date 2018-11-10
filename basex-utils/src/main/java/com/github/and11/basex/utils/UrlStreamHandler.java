package com.github.and11.basex.utils;

import org.eclipse.aether.resolution.ArtifactResolutionException;

import java.io.IOException;
import java.io.InputStream;

public interface UrlStreamHandler {

    class UnresolvableUrlException extends Exception {
        private final String url;

        @Override
        public String toString() {
            return "UnresolvableUrlException{" +
                    "url='" + url + '\'' +
                    '}';
        }

        public UnresolvableUrlException(String url) {
            this.url = url;
        }

        public UnresolvableUrlException(String url, String message) {
            super(message);
            this.url = url;
        }

        public UnresolvableUrlException(String url, Exception e) {
            super(e);
            this.url = url;
        }
    }

    boolean canHandle(String url) ;
    InputStream openStream(String url) throws UnresolvableUrlException;
}
