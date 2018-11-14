package com.github.and11.mojos;

import com.github.and11.basex.utils.BaseXContainer;
import com.github.and11.basex.utils.container.DefaultBaseXContainersFactory;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import static com.github.and11.basex.utils.CoreOptions.createDatabase;
import static com.github.and11.basex.utils.CoreOptions.database;
import static com.github.and11.basex.utils.CoreOptions.url;
import static com.github.and11.basex.utils.CoreOptions.workingDirectory;

@Mojo(name = "open", requiresProject = true, threadSafe = true, defaultPhase = LifecyclePhase.PACKAGE)
public class OpenMojo extends AbstractBaseXMojo {

    @Component
    private MavenProjectHelper projectHelper;

    @Component
    private MavenProject mavenProject;

    @Parameter(required = true, readonly = true)
    private String database;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        try (BaseXContainer container = getBaseXContainersFactory()
                .createContainer(
                        workingDirectory(getDatabaseDir().toPath()),
                        database(url(database)))) {

        } catch (Exception e) {
            throw new MojoExecutionException("create failed", e);
        }
    }
}