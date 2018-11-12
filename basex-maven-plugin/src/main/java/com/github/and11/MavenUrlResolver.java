package com.github.and11;

import com.github.and11.basex.utils.Option;
import com.github.and11.basex.utils.options.DocumentProvisionOption;
import com.github.and11.basex.utils.options.MavenUrlReference;
import com.github.and11.basex.utils.options.RepositoryProvisionOption;
import com.github.and11.basex.utils.options.UrlProvisionOption;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.github.and11.basex.utils.CoreOptions.document;
import static com.github.and11.basex.utils.CoreOptions.maven;
import static com.github.and11.basex.utils.CoreOptions.repository;

public class MavenUrlResolver {
    private final List<Dependency> dependencies;

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public MavenUrlResolver(List<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    public List<String> resolve(List<String> urls) {
        ArrayList<String> processed = new ArrayList<>();
        for (String url : urls) {
            if (url.startsWith("scan-deps:")) {
                scanDeps(url, processed);
            } else if (url.startsWith("scan-dir:")) {
                //scanDir(url, processed);
            }
            else {
                processed.add(url);
            }
        }
        return processed;
    }

    private void scanDeps(String pUrl, ArrayList<String> processed) {
        String mask = pUrl.replaceFirst("^scan-deps:", "");

        processed.addAll(asUrls(getDependencies(mask)));
    }

    private List<Dependency> getDependencies(String mask) {
        List<Dependency> deps = new ArrayList<>();
        for (Dependency dependency : getDependencies()) {
            String shortName = shortName(dependency);
            if (shortName.matches(mask)) {
                deps.add(dependency);
            }
        }
        return deps;
    }

    private Collection<String> asUrls(List<Dependency> dependencies) {
        ArrayList<String> options = new ArrayList<>();
        for (Dependency dependency : dependencies) {
            MavenUrlReference url = maven().groupId(dependency.getGroupId()).artifactId(dependency.getArtifactId())
                    .classifier(dependency.getClassifier()).type(dependency.getType())
                    .version(dependency.getVersion());

            if(Artifact.SCOPE_SYSTEM.equals(dependency.getScope())){
                url.systemPath(dependency.getSystemPath());
            }


            switch (dependency.getType()) {
                case "xml":
                    options.add(document(url).getURL());
                    break;
                case "xar":
                    options.add(repository(url).xar().getURL());
                    break;
                case "jar":
                    options.add(repository(url).jar().getURL());
                    break;
                case "xqm":
                    options.add(repository(url).xqm().getURL());
                    break;
                default:
                    options.add(document(url).getURL());
            }
        }

        return options;
    }

    private String shortName(Dependency dependency) {
        return new StringBuilder().append(dependency.getGroupId())
                .append("/")
                .append(dependency.getArtifactId())
                .append("/")
                .append(dependency.getVersion())
                .append("/")
                .append(dependency.getType()).toString();
    }

}
