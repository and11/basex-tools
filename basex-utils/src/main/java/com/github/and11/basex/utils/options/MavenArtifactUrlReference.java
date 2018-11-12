package com.github.and11.basex.utils.options;

import com.github.and11.basex.utils.MavenUtils;

public class MavenArtifactUrlReference implements MavenUrlReference {

    private String groupId;
    private String artifactId;
    private String type;
    private String classifier;
    private String version;
    private String systemPath;

    @Override
    public MavenUrlReference groupId(String groupId) {

        this.groupId = groupId;
        return this;
    }

    @Override
    public MavenUrlReference artifactId(String artifactId) {

        this.artifactId = artifactId;
        return this;
    }

    @Override
    public MavenUrlReference type(String type) {

        this.type = type;
        return this;
    }

    @Override
    public MavenUrlReference classifier(String classifier) {

        this.classifier = classifier;
        return this;
    }

    @Override
    public MavenUrlReference version(String version) {

        this.version = version;
        return this;
    }

    @Override
    public MavenUrlReference systemPath(String path) {
        this.systemPath = path;
        return this;
    }

    @Override
    public MavenUrlReference versionAsInProject() {
        return version( MavenUtils.asInProject() );

    }

    @Override
    public MavenUrlReference version(VersionResolver resolver) {
        return version(resolver.getVersion(groupId,artifactId));
    }

    public Boolean isSnapshot()
    {
        return version == null ? null : version.endsWith( "SNAPSHOT" );
    }

    @Override
    public String getURL() {

        final StringBuilder url = new StringBuilder();
        url.append( "mvn:" ).append( groupId ).append( "/" ).append( artifactId );
        if( version != null || type != null || classifier != null )
        {
            url.append( "/" );
        }
        if( version != null )
        {
            url.append( version );
        }
        if( type != null || classifier != null )
        {
            url.append( "/" );
        }
        if( type != null )
        {
            url.append( type );
        }
        if( classifier != null )
        {
            url.append( "/" ).append( classifier );
        }

        if(systemPath != null){
            url.append("@").append(systemPath);
        }
        return url.toString();
        
    }

    @Override
    public String toString() {
        return "MavenArtifactUrlReference{" +
                "groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", type='" + type + '\'' +
                ", classifier='" + classifier + '\'' +
                ", version='" + version + '\'' +
                ", systemPath='" + systemPath + '\'' +
                '}';
    }
}
