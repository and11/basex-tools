package com.github.and11.mojos;

import com.github.and11.Function;
import com.github.and11.ProvisionOptions;
import com.github.and11.basex.utils.BaseXContainer;
import com.github.and11.basex.utils.CoreOptions;
import com.github.and11.basex.utils.UrlStreamHandler;
import com.github.and11.basex.utils.container.DefaultBaseXContainersFactory;
import com.github.and11.basex.utils.options.FunctionUrlReference;
import com.github.and11.basex.utils.options.UrlProvisionOption;
import com.github.and11.basex.utils.options.UrlReference;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.and11.basex.utils.CoreOptions.createDatabase;
import static com.github.and11.basex.utils.CoreOptions.document;
import static com.github.and11.basex.utils.CoreOptions.function;
import static com.github.and11.basex.utils.CoreOptions.openDatabase;
import static com.github.and11.basex.utils.CoreOptions.url;
import static com.github.and11.basex.utils.CoreOptions.workingDirectory;

@Mojo(name = "exec", requiresProject = true, threadSafe = true, defaultPhase = LifecyclePhase.PACKAGE)
public class ExecMojo extends AbstractProvisioningMojo {

    @Parameter
    private ProvisionOptions provision;

    @Parameter(readonly = true, required = true)
    List<Function> functions;

    @Parameter(defaultValue = "${project.build.directory}/${project.artifactId}-${project.version}.db", required = true)
    private File defaultDatabaseDir;

    @Parameter(defaultValue = "maindb", required = true)
    private String defaultDatabaseName;

    @Parameter(defaultValue = "collection", required = true)
    private String defaultCollectionName;

    @Component
    private MavenProjectHelper projectHelper;

    @Component
    private MavenProject mavenProject;


    private Path getDatabaseDir(ProvisionOptions opts) {
        return opts.getDatabaseDir() == null ? defaultDatabaseDir.toPath() : opts.getDatabaseDir().toPath();
    }

    private String getDatabaseName(ProvisionOptions opts) {
        return opts.getDatabaseName() == null ? defaultDatabaseName : opts.getDatabaseName();
    }

    private String getCollectionName(ProvisionOptions opts) {
        return opts.getDefaultCollectionName() == null ? defaultCollectionName : opts.getDefaultCollectionName();
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        ArrayList<UrlReference> opts = new ArrayList<>();

        MavenUrlResolver resolver = new MavenUrlResolver(mavenProject.getDependencies());


        opts.addAll(
            resolver.resolve(provision.getDescriptors()).stream().map(CoreOptions::url).collect(Collectors.toList())
        );

        try {

            BaseXContainer container = new DefaultBaseXContainersFactory(getClass().getClassLoader())
                    .createContainer(
                            workingDirectory(getDatabaseDir(provision)),
                            createDatabase(getDatabaseName(provision)),
                            openDatabase(provision.getDatabaseName()).collection(getCollectionName(provision))
                    );

            container.provision(opts.toArray(new UrlProvisionOption[]{}));

            for (Function function : functions) {

                FunctionUrlReference f = function(url(function.getDescriptor()));

                if (function.getArguments().isPresent()) {
                    f.arguments(function.getArguments().get().toArray(new String[]{}));
                }

                Path outputFile = function.getOutput().toPath();
                try (OutputStream output = Files.newOutputStream(outputFile)) {
                    container.export(f, output);

                    if (function.getRecipientCollectionName().isPresent()) {
                        container.provision(document(url(outputFile.toUri().toString())).collection(function.getRecipientCollectionName().get()));
                    }
                }
            }

        } catch (IOException e) {
            throw new MojoExecutionException("", e);
        } catch (BaseXContainer.BaseXContainerException e) {
            throw new MojoExecutionException("", e);

        } catch (UrlStreamHandler.UnresolvableUrlException e) {
            throw new MojoExecutionException("", e);
        }

    }

}
