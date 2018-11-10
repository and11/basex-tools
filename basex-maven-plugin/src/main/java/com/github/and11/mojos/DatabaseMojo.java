package com.github.and11.mojos;

import com.github.and11.DependencySet;
import com.github.and11.FileSet;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

@Mojo(name = "database", requiresProject = true, threadSafe = true, defaultPhase = LifecyclePhase.PACKAGE)
public class DatabaseMojo extends AbstractProvisioningMojo{
    /*

    documents provisioning

     */

    @Parameter(readonly = true, required = false)
    DependencySet[] dependencySet;

    @Parameter(readonly = true, required = false)
    List<FileSet> fileSet;


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

    }
}
