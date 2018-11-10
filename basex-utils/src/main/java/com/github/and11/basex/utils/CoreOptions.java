package com.github.and11.basex.utils;

import com.github.and11.basex.utils.options.DefaultCompositeOption;
import com.github.and11.basex.utils.options.DefaultFunctionUrlReference;
import com.github.and11.basex.utils.options.DocumentProvisionOption;
import com.github.and11.basex.utils.options.FunctionUrlReference;
import com.github.and11.basex.utils.options.MavenArtifactProvisionOption;
import com.github.and11.basex.utils.options.MavenArtifactUrlReference;
import com.github.and11.basex.utils.options.ProvisionOption;
import com.github.and11.basex.utils.options.RawUrlReference;
import com.github.and11.basex.utils.options.RepositoryProvisionOption;
import com.github.and11.basex.utils.options.UrlProvisionOption;
import com.github.and11.basex.utils.options.UrlReference;
import com.github.and11.basex.utils.options.WorkingDirectoryOption;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.github.and11.basex.utils.options.OptionUtils.expand;

public class CoreOptions {

    public static MavenArtifactUrlReference maven() {
        return new MavenArtifactUrlReference();
    }

    public static RawUrlReference url(final String url) {
        return new RawUrlReference(url);
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

    public static RepositoryProvisionOption repository(UrlReference url, String type) {
        return repository(url, RepositoryProvisionOption.RepositoryType.valueOf(type));
    }

    public static DocumentProvisionOption document(UrlReference url) {
        return new DocumentProvisionOption(url);
    }

    public static FunctionUrlReference function(String namespace, String name, String... arguments){
        return new DefaultFunctionUrlReference().name(name).namespace(namespace).arguments(arguments);
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

    public static Option provision( final ProvisionOption... urls )
    {
        return composite( urls );
    }

    public static WorkingDirectoryOption workingDirectory(Path workingDirectory){
        return new WorkingDirectoryOption(workingDirectory);
    }

}
