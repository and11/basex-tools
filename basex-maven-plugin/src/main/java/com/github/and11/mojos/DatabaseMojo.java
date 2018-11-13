package com.github.and11.mojos;

import com.github.and11.basex.utils.BaseXContainer;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.zip.ZipArchiver;

import java.io.File;
import java.util.List;

@Mojo(name = "database", requiresProject = true, threadSafe = true, defaultPhase = LifecyclePhase.PACKAGE)
public class DatabaseMojo extends AbstractBaseXMojo {

    @Parameter(required = false, defaultValue = "${project.build.directory}/${project.artifactId}-${project.version}.basex")
    private File artifactFile;

    @Component
    private ArchiverManager archiverManager;

    @Component
    private MavenProject mavenProject;

    @Component
    private MavenProjectHelper projectHelper;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            ZipArchiver archiver = (ZipArchiver) archiverManager.getArchiver("zip");
            archiver.addDirectory(getDatabaseDir());
            archiver.setDestFile(artifactFile);
            archiver.setIncludeEmptyDirs(true);
            archiver.createArchive();

            if("basex".equals(mavenProject.getPackaging())){
                mavenProject.setFile(artifactFile);
                //projectHelper.attachArtifact(mavenProject, artifactFile, null);
            }

        } catch (final Exception e) {
            throw new MojoExecutionException("can't create zip archive " + artifactFile, e);
        }
    }
}
