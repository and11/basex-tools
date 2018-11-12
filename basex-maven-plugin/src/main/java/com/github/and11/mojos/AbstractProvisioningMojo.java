package com.github.and11.mojos;

import com.github.and11.MavenUrlResolver;
import com.github.and11.ProvisionOptions;
import com.github.and11.basex.utils.BaseXContainer;
import com.github.and11.basex.utils.CoreOptions;
import com.github.and11.basex.utils.container.DefaultBaseXContainersFactory;
import com.github.and11.basex.utils.options.InitializationOption;
import com.github.and11.basex.utils.options.UrlProvisionOption;
import com.github.and11.basex.utils.options.UrlReference;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.github.and11.basex.utils.CoreOptions.createDatabase;
import static com.github.and11.basex.utils.CoreOptions.database;
import static com.github.and11.basex.utils.CoreOptions.openDatabase;
import static com.github.and11.basex.utils.CoreOptions.options;
import static com.github.and11.basex.utils.CoreOptions.url;
import static com.github.and11.basex.utils.CoreOptions.workingDirectory;

public abstract class AbstractProvisioningMojo extends AbstractMojo {

    @Parameter
    private ProvisionOptions provision;

    @Parameter(defaultValue = "${project.build.directory}/${project.artifactId}-${project.version}.db", required = true)
    private File databaseDir;

    @Parameter
    private String openDatabase;

    @Parameter
    private String createDatabase;

    @Parameter(defaultValue = "collection", required = true)
    private String defaultCollection;

    @Component
    private MavenProjectHelper projectHelper;

    @Component
    private MavenProject mavenProject;

    @Parameter
    private String database;

    public MavenProjectHelper getProjectHelper() {
        return projectHelper;
    }

    public MavenProject getMavenProject() {
        return mavenProject;
    }

    protected Path getDatabaseDir(ProvisionOptions opts) {
        return databaseDir.toPath();
    }

    protected String getOpenDatabaseName(){
        return openDatabase;
    }

    protected String getCreateDatabaseName(){
        return createDatabase;
    }

    protected  String getDefaultCollection(){
        return defaultCollection;
    }

    private InitializationOption[] initializationOptions(){
        ArrayList<InitializationOption> options = new ArrayList<>();

        String oOpenDb = openDatabase;
        String oCreateDb = createDatabase;

        if(database != null){
            options.add(database(url(database)));
        }
        else {
            if(oCreateDb == null){
                oCreateDb = UUID.randomUUID().toString();
            }
            if(openDatabase == null){
                oOpenDb = oCreateDb;
            }

        }

        if(oOpenDb != null){
            options.add(openDatabase(openDatabase).collection(defaultCollection));
        }

        if(oCreateDb != null){
            options.add(createDatabase(createDatabase));
        }


        options.add(workingDirectory(databaseDir.toPath()));

        return options.toArray(new InitializationOption[]{});
    }

    protected BaseXContainer provision() throws MojoExecutionException {
        ArrayList<UrlReference> opts = new ArrayList<>();

        MavenUrlResolver resolver = new MavenUrlResolver(mavenProject.getDependencies());

        opts.addAll(
                resolver.resolve(provision.getDescriptors()).stream().map(CoreOptions::url).collect(Collectors.toList())
        );

        try {

            BaseXContainer container = new DefaultBaseXContainersFactory(getClass().getClassLoader())
                    .createContainer(initializationOptions());

            container.provision(opts.toArray(new UrlProvisionOption[]{}));

            return container;
        } catch (BaseXContainer.BaseXContainerException e) {
            throw new MojoExecutionException("provisioning failed", e);
        }
    }
}