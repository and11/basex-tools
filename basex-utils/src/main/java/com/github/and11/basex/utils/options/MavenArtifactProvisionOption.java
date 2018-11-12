/*
 * Copyright 2008 Alin Dreghiciu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.and11.basex.utils.options;

public class MavenArtifactProvisionOption
    extends AbstractProvisionOption<MavenArtifactProvisionOption>
    implements MavenUrlReference
{

    /**
     * Maven artifact.
     */
    private final MavenUrlReference artifact;
    /**
     * True if the user used update method.
     */
    private boolean m_updateUsed;

    /**
     * Constructor.
     */
    public MavenArtifactProvisionOption()
    {
        artifact = new MavenArtifactUrlReference();
    }

    /**
     * Constructor based on a mevn artifact option.
     *
     * @param artifact maven artifact (cannot be null)
     */
    public MavenArtifactProvisionOption(final MavenUrlReference artifact )
    {

//        validateNotNull( artifact, "Maven artifact" );
        this.artifact = artifact;
    }

    /**
     * {@inheritDoc}
     */
    public MavenArtifactProvisionOption groupId( final String groupId )
    {
        artifact.groupId( groupId );
        m_updateUsed = false;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public MavenArtifactProvisionOption artifactId( final String artifactId )
    {
        artifact.artifactId( artifactId );
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public MavenArtifactProvisionOption type( final String type )
    {
        artifact.type( type );
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public MavenArtifactProvisionOption classifier( String classifier )
    {
        artifact.classifier( classifier );
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public MavenArtifactProvisionOption version( final String version )
    {
        artifact.version( version );
        return this;
    }

    @Override
    public MavenUrlReference systemPath(String path) {
        return artifact.systemPath(path);
    }

    /**
     * {@inheritDoc}
     */
    public MavenArtifactProvisionOption version( final MavenArtifactUrlReference.VersionResolver resolver )
    {
        artifact.version( resolver );
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public MavenArtifactProvisionOption versionAsInProject()
    {
        artifact.versionAsInProject();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public Boolean isSnapshot()
    {
        return artifact.isSnapshot();
    }

    /**
     * {@inheritDoc}
     */
    public String getURL()
    {
        return artifact.getURL();
    }

    /**
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return artifact.toString();
    }

}