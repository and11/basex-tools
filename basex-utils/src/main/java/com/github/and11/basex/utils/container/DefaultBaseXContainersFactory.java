package com.github.and11.basex.utils.container;

import com.github.and11.basex.utils.BaseXContainer;
import com.github.and11.basex.utils.BaseXContainersFactory;
import com.github.and11.basex.utils.UrlStreamHandler;
import com.github.and11.basex.utils.options.InitializationOption;
import com.github.and11.basex.utils.streamhandlers.ClasspathUrlStreamHandler;
import com.github.and11.basex.utils.streamhandlers.CompositeUrlStreamHandler;
import com.github.and11.basex.utils.streamhandlers.MavenUrlStreamHandler;

import java.io.File;

public class DefaultBaseXContainersFactory implements BaseXContainersFactory {

    private ClassLoader classLoader;
    private File mavenSettings;

    public DefaultBaseXContainersFactory setMavenSettings(File mavenSettings) {
        this.mavenSettings = mavenSettings;
        return this;
    }

    public DefaultBaseXContainersFactory(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public static UrlStreamHandler getStandardResolvers(ClassLoader classLoader) {
        return new CompositeUrlStreamHandler(new MavenUrlStreamHandler(),
               new ClasspathUrlStreamHandler(classLoader));
    }

    public static UrlStreamHandler getStandardResolvers(ClassLoader classLoader, File mavenSettings) {
        return new CompositeUrlStreamHandler(new MavenUrlStreamHandler(mavenSettings),
                new ClasspathUrlStreamHandler(classLoader));
    }

    private ClassLoader getClassLoader() {
        return classLoader == null ? ClassLoader.getSystemClassLoader() : classLoader;
    }


    @Override
    public BaseXContainer createContainer(InitializationOption... options) throws BaseXContainer.BaseXContainerException {
        DefaultBaseXContainer container = new DefaultBaseXContainer(getStandardResolvers(getClassLoader(), mavenSettings), options);
        container.start();
        return container;
    }
}
