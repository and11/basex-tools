package com.github.and11.mojos;

import com.github.and11.MavenUrlResolver;
import com.github.and11.basex.utils.BaseXContainer;
import com.github.and11.basex.utils.CoreOptions;
import com.github.and11.basex.utils.container.DefaultBaseXContainersFactory;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.and11.basex.utils.CoreOptions.createDatabase;
import static com.github.and11.basex.utils.CoreOptions.openDatabase;
import static com.github.and11.basex.utils.CoreOptions.workingDirectory;

@Mojo(name = "create", requiresProject = true, threadSafe = true, defaultPhase = LifecyclePhase.PACKAGE)
public class CreateMojo extends AbstractBaseXMojo {

    @Component
    private MavenProjectHelper projectHelper;

    @Component
    private MavenProject mavenProject;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try (BaseXContainer container = new DefaultBaseXContainersFactory(getClass().getClassLoader())
                .createContainer(
                        workingDirectory(getDatabaseDir().toPath()),
                        createDatabase(getDatabaseName()))) {

        } catch (Exception e) {
            throw new MojoExecutionException("create failed", e);
        }
    }
}