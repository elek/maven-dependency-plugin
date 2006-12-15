package org.apache.maven.plugin.dependency.utils;

/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
/**
 * 
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

/**
 * @author brianf
 * 
 */
public class TestDependencyUtil
    extends TestCase
{
    List artifacts = new ArrayList();

    Log log = new SilentLog();

    File outputFolder;

    Artifact snap;

    Artifact release;

    protected void setUp()
        throws Exception
    {
        super.setUp();

        ArtifactHandler ah = new DefaultArtifactHandler();
        VersionRange vr = VersionRange.createFromVersion( "1.1" );
        release = new DefaultArtifact( "test", "one", vr, Artifact.SCOPE_COMPILE, "jar", null, ah, false );
        artifacts.add( release );

        vr = VersionRange.createFromVersion( "1.1-SNAPSHOT" );
        snap = new DefaultArtifact( "test", "two", vr, Artifact.SCOPE_PROVIDED, "war", "sources", ah, false );
        artifacts.add( snap );

        // pick random output location
        Random a = new Random();
        outputFolder = new File( "target/copy" + a.nextLong() + "/" );
        outputFolder.delete();
        assertFalse( outputFolder.exists() );
    }

    protected void tearDown()
    {

    }

    public void testDirectoryName()
        throws MojoExecutionException
    {
        File folder = new File( "target/a" );
        File name = DependencyUtil.getFormattedOutputDirectory( false, false, folder, (Artifact) artifacts.get( 0 ) );
        // object is the same.
        assertEquals( folder, name );

        name = DependencyUtil.getFormattedOutputDirectory( true, false, folder, (Artifact) artifacts.get( 0 ) );
        String expectedResult = folder.getAbsolutePath() + File.separatorChar + "jars";
        assertTrue( expectedResult.equalsIgnoreCase( name.getAbsolutePath() ) );

        name = DependencyUtil.getFormattedOutputDirectory( false, true, folder, (Artifact) artifacts.get( 0 ) );
        expectedResult = folder.getAbsolutePath() + File.separatorChar + "test-one-jar-1.1";
        assertEquals( expectedResult, name.getAbsolutePath() );

        name = DependencyUtil.getFormattedOutputDirectory( true, true, folder, (Artifact) artifacts.get( 0 ) );
        expectedResult = folder.getAbsolutePath() + File.separatorChar + "jars" + File.separatorChar
            + "test-one-jar-1.1";
        assertEquals( expectedResult, name.getAbsolutePath() );
    }

    public void testDirectoryName2()
        throws MojoExecutionException
    {
        File folder = new File( "target/a" );
        File name = DependencyUtil.getFormattedOutputDirectory( false, false, folder, (Artifact) artifacts.get( 1 ) );
        // object is the same.
        assertEquals( folder, name );

        name = DependencyUtil.getFormattedOutputDirectory( true, false, folder, (Artifact) artifacts.get( 1 ) );
        String expectedResult = folder.getAbsolutePath() + File.separatorChar + "wars";
        assertEquals( expectedResult, name.getAbsolutePath() );

        name = DependencyUtil.getFormattedOutputDirectory( false, true, folder, (Artifact) artifacts.get( 1 ) );
        expectedResult = folder.getAbsolutePath() + File.separatorChar + "test-two-war-sources-1.1-SNAPSHOT";
        assertEquals( expectedResult, name.getAbsolutePath() );

        name = DependencyUtil.getFormattedOutputDirectory( true, true, folder, (Artifact) artifacts.get( 1 ) );
        expectedResult = folder.getAbsolutePath() + File.separatorChar + "wars" + File.separatorChar
            + "test-two-war-sources-1.1-SNAPSHOT";
        assertEquals( expectedResult, name.getAbsolutePath() );
    }

    public void testFileName()
        throws MojoExecutionException
    {
        Artifact artifact = (Artifact) artifacts.get( 0 );

        String name = DependencyUtil.getFormattedFileName( artifact, false );
        String expectedResult = "one-1.1.jar";
        assertEquals( expectedResult, name );
        name = DependencyUtil.getFormattedFileName( artifact, true );
        expectedResult = "one.jar";
        assertEquals( expectedResult, name );
    }

    public void testFileNameClassifier()
        throws MojoExecutionException
    {
        ArtifactHandler ah = new DefaultArtifactHandler();
        VersionRange vr = VersionRange.createFromVersion( "1.1-SNAPSHOT" );
        Artifact artifact = new DefaultArtifact( "test", "two", vr, Artifact.SCOPE_PROVIDED, "war", "sources", ah,
                                                 false );

        String name = DependencyUtil.getFormattedFileName( artifact, false );
        String expectedResult = "two-1.1-SNAPSHOT-sources.war";
        assertEquals( expectedResult, name );

        name = DependencyUtil.getFormattedFileName( artifact, true );
        expectedResult = "two-sources.war";
        assertEquals( expectedResult, name );

        artifact = new DefaultArtifact( "test", "two", vr, Artifact.SCOPE_PROVIDED, "war", "", ah, false );
        name = DependencyUtil.getFormattedFileName( artifact, true );
        expectedResult = "two.war";
        assertEquals( expectedResult, name );

    }

    public void testFileNameClassifierWithFile()
        throws MojoExecutionException
    {
        //specifically testing the default operation that getFormattedFileName returns
        //the actual name of the file if available unless remove version is set.
        ArtifactHandler ah = new DefaultArtifactHandler();
        VersionRange vr = VersionRange.createFromVersion( "1.1-SNAPSHOT" );
        Artifact artifact = new DefaultArtifact( "test", "two", vr, Artifact.SCOPE_PROVIDED, "war", "sources", ah,
                                                 false );
        File file = new File("/target","test-file-name.jar");
        artifact.setFile(file);

        String name = DependencyUtil.getFormattedFileName( artifact, false );
        String expectedResult = "test-file-name.jar";
        assertEquals( expectedResult, name );

        name = DependencyUtil.getFormattedFileName( artifact, true );
        expectedResult = "two-sources.war";
        assertEquals( expectedResult, name );

        artifact = new DefaultArtifact( "test", "two", vr, Artifact.SCOPE_PROVIDED, "war", "", ah, false );
        name = DependencyUtil.getFormattedFileName( artifact, true );
        expectedResult = "two.war";
        assertEquals( expectedResult, name );

    }
}
