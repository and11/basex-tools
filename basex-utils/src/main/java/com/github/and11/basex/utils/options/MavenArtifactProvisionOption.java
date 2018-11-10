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
    private final MavenUrlReference m_artifact;
    /**
     * True if the user used update method.
     */
    private boolean m_updateUsed;

    /**
     * Constructor.
     */
    public MavenArtifactProvisionOption()
    {
        m_artifact = new MavenArtifactUrlReference();
    }

    /**
     * Constructor based on a mevn artifact option.
     *
     * @param artifact maven artifact (cannot be null)
     */
    public MavenArtifactProvisionOption(final MavenUrlReference artifact )
    {

//        validateNotNull( artifact, "Maven artifact" );
        m_artifact = artifact;
    }

    /**
     * {@inheritDoc}
     */
    public MavenArtifactProvisionOption groupId( final String groupId )
    {
        m_artifact.groupId( groupId );
        m_updateUsed = false;
        return itself();
    }

    /**
     * {@inheritDoc}
     */
    public MavenArtifactProvisionOption artifactId( final String artifactId )
    {
        m_artifact.artifactId( artifactId );
        return itself();
    }

    /**
     * {@inheritDoc}
     */
    public MavenArtifactProvisionOption type( final String type )
    {
        m_artifact.type( type );
        return itself();
    }

    /**
     * {@inheritDoc}
     */
    public MavenArtifactProvisionOption classifier( String classifier )
    {
        m_artifact.classifier( classifier );
        return itself();
    }

    /**
     * {@inheritDoc}
     */
    public MavenArtifactProvisionOption version( final String version )
    {
        m_artifact.version( version );
        return itself();
    }

    /**
     * {@inheritDoc}
     */
    public MavenArtifactProvisionOption version( final MavenArtifactUrlReference.VersionResolver resolver )
    {
        m_artifact.version( resolver );
        return itself();
    }

    /**
     * {@inheritDoc}
     */
    public MavenArtifactProvisionOption versionAsInProject()
    {
        m_artifact.versionAsInProject();
        return itself();
    }

    /**
     * {@inheritDoc}
     */
    public Boolean isSnapshot()
    {
        return m_artifact.isSnapshot();
    }

    /**
     * {@inheritDoc}
     */
    public String getURL()
    {
        return m_artifact.getURL();
    }

    /**
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return m_artifact.toString();
    }

    /**
     * {@inheritDoc}
     */
    protected MavenArtifactProvisionOption itself()
    {
        return this;
    }

}