package com.github.and11.mojos;

import com.github.and11.DependencySet;
import com.github.and11.FileSet;
import com.github.and11.ProvisionOptions;
import com.github.and11.basex.utils.BaseXContainer;
import com.github.and11.basex.utils.CoreOptions;
import com.github.and11.basex.utils.Option;
import com.github.and11.basex.utils.container.DefaultBaseXContainer;
import com.github.and11.basex.utils.container.DefaultBaseXContainersFactory;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.and11.basex.utils.CoreOptions.repository;
import static com.github.and11.basex.utils.CoreOptions.url;

@Mojo(name = "exec", requiresProject = true, threadSafe = true, defaultPhase = LifecyclePhase.PACKAGE)
public class ExecMojo extends AbstractProvisioningMojo {

    /*

    full provisioning



    <provision>
        <xar>
        <module>
        <document>
    </provision>


     */


    @Parameter
    private ProvisionOptions provision;

    @Parameter(readonly = true, required = false)
    List<DependencySet> dependencySets;

    @Parameter(readonly = true, required = false)
    List<FileSet> fileSets;


    @Component
    private MavenProjectHelper projectHelper;

    @Component
    private MavenProject mavenProject;



    private Option repositoryAsOption(final String pUrl){
        Pattern pattern = Pattern.compile("^([^+]+)\\+(.+)$");
        Matcher matcher = pattern.matcher(pUrl);

        if(matcher.matches()){
            String type = matcher.group(1);
            String url = matcher.group(2);
            return repository(url(url), type.toUpperCase());
        }
        else {
            return repository(url(pUrl));
        }
    }

    private Collection<? extends Option> getRepositories(){
        ArrayList<Option> options = new ArrayList<>();
        if(provision.getRepositories() != null){
            for (String repo : provision.getRepositories()) {
                options.add(repositoryAsOption(repo));
            }
        }
        return options;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        System.out.println("depsets: " + dependencySets);
        System.out.println("filesets: " + fileSets);

        System.out.println("provision: " + provision);


        ArrayList<Option> options = new ArrayList<>();
        options.addAll(getRepositories());
        options.addAll(getDocuments());

        try {
            BaseXContainer container = new DefaultBaseXContainersFactory(getClass().getClassLoader())
                    .createContainer(
                            options.toArray(new Option[options.size()]));
            System.out.println("AAAAAAAAAA " + container);
            //container.export();
        } catch (BaseXContainer.BaseXContainerException e) {
            throw new MojoExecutionException("", e);
        }

    }

    private Collection<? extends Option> getDocuments() {
        ArrayList<Option> options = new ArrayList<>();
        if(provision.getDocuments() != null){
            for (String document : provision.getDocuments()) {
                options.add(CoreOptions.document(url(document)));
            }
        }
        return options;
    }
}
