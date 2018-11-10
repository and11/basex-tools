/*
 * Copyright 2009 Alin Dreghiciu.
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
package com.github.and11.basex.utils;

import com.github.and11.basex.utils.options.MavenArtifactUrlReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

public class MavenUtils
{

    private MavenUtils()
    {
        // utility class
    }
    public static String getArtifactVersion( final String groupId,
                                             final String artifactId )
    {
        final Properties dependencies = new Properties();
        try
        {
            dependencies.load(
                new FileInputStream( getFileFromClasspath( "META-INF/maven/dependencies.properties" ) )
            );
            final String version = dependencies.getProperty( groupId + "/" + artifactId + "/version" );
            if( version == null )
            {
                throw new RuntimeException(
                    "Could not resolve version. Do you have a dependency for " + groupId + "/" + artifactId
                    + " in your maven project?"
                );
            }
            return version;
        }
        catch( IOException e )
        {
            // TODO throw a better exception
            throw new RuntimeException(
                "Could not resolve version. Did you configured the plugin in your maven project?"
                + "Or maybe you did not run the maven build and you are using an IDE?"
            );
        }
    }

    public static MavenArtifactUrlReference.VersionResolver asInProject()
    {
        return new MavenArtifactUrlReference.VersionResolver()
        {
            public String getVersion( final String groupId,
                                      final String artifactId )
            {
                return getArtifactVersion( groupId, artifactId );
            }

        };
    }

    private static File getFileFromClasspath( final String filePath )
        throws FileNotFoundException
    {
        try
        {
            URL fileURL = MavenUtils.class.getClassLoader().getResource( filePath );
            if( fileURL == null )
            {
                throw new FileNotFoundException( "File [" + filePath + "] could not be found in classpath" );
            }
            return new File( fileURL.toURI() );
        }
        catch( URISyntaxException e )
        {
            throw new FileNotFoundException( "File [" + filePath + "] could not be found: " + e.getMessage() );
        }
    }

}