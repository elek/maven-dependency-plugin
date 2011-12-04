package org.apache.maven.plugin.dependency;

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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Comparator;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.dependency.fromConfiguration.Filter;
import org.apache.maven.plugin.dependency.fromConfiguration.Format;
import org.apache.maven.plugin.dependency.resolution.DependencySource;
import org.apache.maven.plugin.dependency.resolution.ProjectArtifactSource;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.maven.shared.artifact.filter.collection.ArtifactFilterException;
import org.apache.maven.shared.artifact.filter.collection.ArtifactsFilter;
import org.codehaus.plexus.util.IOUtil;

/**
 * This goal will output a classpath string of dependencies from the local
 * repository to a file or log.
 * 
 * @goal build-classpath
 * @requiresDependencyResolution test
 * @phase generate-sources
 * @author ankostis
 * @version $Id$
 * @since 2.0-alpha-2
 */
public class BuildClasspathMojo extends AbstractDependencyMojo implements
		Comparator<Artifact> {

	/**
	 * Filter definition.
	 * 
	 * @parameter
	 */
	private Filter filter = new Filter();
	/**
	 * The format of the output file name/
	 * 
	 * @parameter
	 */
	private Format format = new Format();

	/**
	 * The file to write the classpath string. If undefined, it just prints the
	 * classpath as [INFO].
	 * 
	 * @parameter expression="${mdep.outputFile}"
	 */
	private File outputFile;

	/**
	 * If 'true', it skips the up-to-date-check, and always regenerates the
	 * classpath file.
	 * 
	 * @parameter default-value="false" expression="${mdep.regenerateFile}"
	 */
	private boolean regenerateFile;

	/**
	 * Attach the classpath file to the main artifact so it can be installed and
	 * deployed.
	 * 
	 * @since 2.0
	 * @parameter default-value=false
	 */
	boolean attach;

	/**
	 * Write out the classpath in a format compatible with filtering
	 * (classpath=xxxxx)
	 * 
	 * @since 2.0
	 * @parameter default-value=false expression="${mdep.outputFilterFile}"
	 */
	boolean outputFilterFile;

	/**
	 * Override the char used between path folders. The system-dependent
	 * path-separator character. This field is initialized to contain the first
	 * character of the value of the system property path.separator. This
	 * character is used to separate filenames in a sequence of files given as a
	 * path list. On UNIX systems, this character is ':'; on Microsoft Windows
	 * systems it is ';'.
	 * 
	 * @since 2.0
	 * @parameter default-value="" expression="${mdep.pathSeparator}"
	 */
	private String pathSeparator;

	/**
	 * Maven ProjectHelper
	 * 
	 * @component
	 * @readonly
	 */
	private MavenProjectHelper projectHelper;

	/**
	 * Source of the dependencies;
	 */
	DependencySource dependencies;

	/**
	 * Main entry into mojo. Gets the list of dependencies and iterates through
	 * calling copyArtifact.
	 * 
	 * @throws MojoExecutionException
	 *             with a message if an error occurs.
	 * @see #getDependencies
	 * @see #copyArtifact(Artifact, boolean)
	 */
	public void execute() throws MojoExecutionException {
		dependencies = new ProjectArtifactSource(project);

		StringBuilder b = new StringBuilder();

		Set<Artifact> artifacts = (Set<Artifact>) project.getArtifacts();

		try {
			artifacts = filter.createArtifactFilter().filter(artifacts);
		} catch (ArtifactFilterException e) {
			throw new MojoExecutionException("Error on filterint artifacts", e);
		}

		for (Artifact a : artifacts) {
			appendArtifactPath(a, b);
		    b.append(File.pathSeparator);
		}

		String cpString = b.toString();

		if (outputFile == null) {
			getLog().info("Dependencies classpath:\n" + cpString);
		} else {
			if (regenerateFile || !isUpdToDate(cpString)) {
				storeClasspathFile(cpString, outputFile);
			} else {
				this.getLog().info(
						"Skipped writing classpath file '" + outputFile
								+ "'.  No changes found.");
			}
		}
		if (attach) {
			attachFile(cpString);
		}
	}

	protected void attachFile(String cpString) throws MojoExecutionException {
		File attachedFile = new File(project.getBuild().getDirectory(),
				"classpath");
		storeClasspathFile(cpString, attachedFile);

		projectHelper.attachArtifact(project, attachedFile, "classpath");
	}

	/**
	 * Appends the artifact path into the specified stringBuffer.
	 * 
	 * @param art
	 * @param sb
	 */
	protected void appendArtifactPath(Artifact art, StringBuilder sb) {
		sb.append(format.getFormattedFileName(art));
	}

	/**
	 * Checks that new classpath differs from that found inside the old
	 * classpathFile.
	 * 
	 * @param cpString
	 * @return true if the specified classpath equals to that found inside the
	 *         file, false otherwise (including when file does not exists but
	 *         new classpath does).
	 */
	private boolean isUpdToDate(String cpString) {
		try {
			String oldCp = readClasspathFile();
			return (cpString == oldCp || (cpString != null && cpString
					.equals(oldCp)));
		} catch (Exception ex) {
			this.getLog().warn(
					"Error while reading old classpath file '" + outputFile
							+ "' for up-to-date check: " + ex);

			return false;
		}
	}

	/**
	 * It stores the specified string into that file.
	 * 
	 * @param cpString
	 *            the string to be written into the file.
	 * @throws MojoExecutionException
	 */
	private void storeClasspathFile(String cpString, File out)
			throws MojoExecutionException {
		// make sure the parent path exists.
		out.getParentFile().mkdirs();

		Writer w = null;
		try {
			w = new BufferedWriter(new FileWriter(out));
			w.write(cpString);
			getLog().info("Wrote classpath file '" + out + "'.");
		} catch (IOException ex) {
			throw new MojoExecutionException(
					"Error while writting to classpath file '" + out + "': "
							+ ex.toString(), ex);
		} finally {
			IOUtil.close(w);
		}
	}

	/**
	 * Reads into a string the file specified by the mojo param 'outputFile'.
	 * Assumes, the instance variable 'outputFile' is not null.
	 * 
	 * @return the string contained in the classpathFile, if exists, or null
	 *         otherwise.
	 * @throws MojoExecutionException
	 */
	protected String readClasspathFile() throws IOException {
		if (outputFile == null) {
			throw new IllegalArgumentException(
					"The outputFile parameter cannot be null if the file is intended to be read.");
		}

		if (!outputFile.isFile()) {
			return null;
		}
		StringBuffer sb = new StringBuffer();
		BufferedReader r = null;

		try {
			r = new BufferedReader(new FileReader(outputFile));
			String l;
			while ((l = r.readLine()) != null) {
				sb.append(l);
			}

			return sb.toString();
		} finally {
			IOUtil.close(r);
		}
	}

	/**
	 * Compares artifacts lexicographically, using pattern
	 * [group_id][artifact_id][version].
	 * 
	 * @param art1
	 *            first object
	 * @param art2
	 *            second object
	 * @return the value <code>0</code> if the argument string is equal to this
	 *         string; a value less than <code>0</code> if this string is
	 *         lexicographically less than the string argument; and a value
	 *         greater than <code>0</code> if this string is lexicographically
	 *         greater than the string argument.
	 */
	public int compare(Artifact art1, Artifact art2) {
		if (art1 == art2) {
			return 0;
		} else if (art1 == null) {
			return -1;
		} else if (art2 == null) {
			return +1;
		}

		String s1 = art1.getGroupId() + art1.getArtifactId()
				+ art1.getVersion();
		String s2 = art2.getGroupId() + art2.getArtifactId()
				+ art2.getVersion();

		return s1.compareTo(s2);
	}

	protected ArtifactsFilter getMarkedArtifactFilter() {
		return null;
	}

	/**
	 * @return the outputFile
	 */
	public File getCpFile() {
		return this.outputFile;
	}

	/**
	 * @param theCpFile
	 *            the outputFile to set
	 */
	public void setCpFile(File theCpFile) {
		this.outputFile = theCpFile;
	}

	/**
	 * @return the regenerateFile
	 */
	public boolean isRegenerateFile() {
		return this.regenerateFile;
	}

	/**
	 * @param theRegenerateFile
	 *            the regenerateFile to set
	 */
	public void setRegenerateFile(boolean theRegenerateFile) {
		this.regenerateFile = theRegenerateFile;
	}

}
