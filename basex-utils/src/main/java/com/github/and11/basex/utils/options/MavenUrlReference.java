package com.github.and11.basex.utils.options;

public interface MavenUrlReference extends UrlReference {
    MavenUrlReference groupId(String groupId);

    MavenUrlReference artifactId(String artifactId);

    MavenUrlReference type(String type);

    MavenUrlReference classifier(String classifier);

    MavenUrlReference version(String version);

    MavenUrlReference systemPath(String path);

    MavenUrlReference versionAsInProject();

    MavenUrlReference version(VersionResolver resolver);
    Boolean isSnapshot();

    public static interface VersionResolver {
        String getVersion(String groupId, String artifactId);
    }
}
