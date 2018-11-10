package com.github.and11.basex.utils.options;

public class RawUrlReference implements UrlReference {
    private final String url;

    public RawUrlReference(String url) {
        this.url = url;
    }

    @Override
    public String getURL() {
        return url;
    }

    @Override
    public String toString() {
        return "RawUrlReference{" +
                "url='" + url + '\'' +
                '}';
    }
}
