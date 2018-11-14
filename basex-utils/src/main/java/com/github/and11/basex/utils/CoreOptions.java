package com.github.and11.basex.utils;

import com.github.and11.basex.utils.options.CreateDatabaseOption;
import com.github.and11.basex.utils.options.DatabaseOption;
import com.github.and11.basex.utils.options.DefaultCompositeOption;
import com.github.and11.basex.utils.options.DefaultFunctionUrlReference;
import com.github.and11.basex.utils.options.DocumentProvisionOption;
import com.github.and11.basex.utils.options.FunctionUrlReference;
import com.github.and11.basex.utils.options.MavenArtifactProvisionOption;
import com.github.and11.basex.utils.options.MavenArtifactUrlReference;
import com.github.and11.basex.utils.options.OpenDatabaseOption;
import com.github.and11.basex.utils.options.ProvisionOption;
import com.github.and11.basex.utils.options.RawUrlReference;
import com.github.and11.basex.utils.options.RepositoryProvisionOption;
import com.github.and11.basex.utils.options.UrlProvisionOption;
import com.github.and11.basex.utils.options.UrlReference;
import com.github.and11.basex.utils.options.WorkingDirectoryOption;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.github.and11.basex.utils.OptionUtils.expand;

public class CoreOptions {

    public static MavenArtifactUrlReference maven() {
        return new MavenArtifactUrlReference();
    }

    public static RawUrlReference urlref(final String url) {
        return new RawUrlReference(url);
    }
    public static UrlProvisionOption url(final String url) {
        return new UrlProvisionOption(url);
    }

    public static Option composite(final Option... options) {
        return new DefaultCompositeOption(options);
    }

    public static MavenArtifactProvisionOption mavenBundle()
    {
        return new MavenArtifactProvisionOption();
    }

    public static RepositoryProvisionOption repository(UrlReference url) {
        return new RepositoryProvisionOption(url);
    }
    public static RepositoryProvisionOption repository(UrlReference url, RepositoryProvisionOption.RepositoryType type) {
        return new RepositoryProvisionOption(url).type(type);
    }

    public static DatabaseOption database(UrlReference url){
        return new DatabaseOption(url);
    }
    public static RepositoryProvisionOption repository(UrlReference url, String type) {
        return repository(url, RepositoryProvisionOption.RepositoryType.valueOf(type));
    }

    public static DocumentProvisionOption document(UrlReference url) {
        return new DocumentProvisionOption(url);
    }

    public static FunctionUrlReference function(String namespace, String name, String... arguments){
        return new DefaultFunctionUrlReference().name(name).namespace(namespace).arguments(arguments);
    }
    public static FunctionUrlReference function(UrlReference url) throws IOException, UrlStreamHandler.UnresolvableUrlException {
        return new DefaultFunctionUrlReference(url);
    }

    public static Option[] options( final Option... options )
    {
        return expand( options );
    }

    public static Option provision( final String... urls )
    {
        final List<ProvisionOption> options = new ArrayList<ProvisionOption>();
        for( String url : urls )
        {
            options.add( new UrlProvisionOption( url ) );
        }
        return provision( options.toArray( new ProvisionOption[options.size()] ) );
    }

    public static Option provision(final ProvisionOption... urls )     {
        return composite( urls );
    }

    public static CreateDatabaseOption createDatabase(String name){
        return new CreateDatabaseOption(name);
    }

    public static CreateDatabaseOption createDatabase(){
        return new CreateDatabaseOption(UUID.randomUUID().toString());
    }
    public static OpenDatabaseOption openDatabase(String name){
        return new OpenDatabaseOption(name);
    }

    public static WorkingDirectoryOption workingDirectory(Path workingDirectory){
        return new WorkingDirectoryOption(workingDirectory);
    }

}
