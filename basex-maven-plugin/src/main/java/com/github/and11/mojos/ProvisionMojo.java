package com.github.and11.mojos;

import com.github.and11.MavenUrlResolver;
import com.github.and11.basex.utils.BaseXContainer;
import com.github.and11.basex.utils.CoreOptions;
import com.github.and11.basex.utils.container.DefaultBaseXContainersFactory;
import com.github.and11.basex.utils.options.InitializationOption;
import com.github.and11.basex.utils.options.ProvisionOption;
import com.github.and11.basex.utils.options.UrlProvisionOption;
import com.github.and11.basex.utils.options.UrlReference;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.github.and11.basex.utils.CoreOptions.createDatabase;
import static com.github.and11.basex.utils.CoreOptions.database;
import static com.github.and11.basex.utils.CoreOptions.openDatabase;
import static com.github.and11.basex.utils.CoreOptions.url;
import static com.github.and11.basex.utils.CoreOptions.workingDirectory;

@Mojo(name = "provision", requiresProject = true, threadSafe = true, defaultPhase = LifecyclePhase.PACKAGE)
public class ProvisionMojo extends AbstractBaseXMojo {

    @Parameter(required = true, readonly = true)
    private List<String> descriptors;

    @Component
    private MavenProjectHelper projectHelper;

    @Component
    private MavenProject mavenProject;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        ArrayList<ProvisionOption> opts = new ArrayList<>();

        MavenUrlResolver resolver = new MavenUrlResolver(mavenProject.getDependencies());

        opts.addAll(
                resolver.resolve(descriptors).stream().map(CoreOptions::url).collect(Collectors.toList())
        );

        try (BaseXContainer container = new DefaultBaseXContainersFactory(getClass().getClassLoader())
                .createContainer(
                        workingDirectory(getDatabaseDir().toPath()),
                        openDatabase(getDatabaseName()).collection(getDefaultCollection()))) {

            container.provision(opts.toArray(new ProvisionOption[]{}));

        } catch (Exception e) {
            throw new MojoExecutionException("provisioning failed", e);
        }
    }
}