package org.apache.maven.plugin.dependency.fromConfiguration;

import java.io.File;

import org.apache.maven.artifact.Artifact;
import org.codehaus.plexus.util.StringUtils;

/**
 * Format of the output files.
 * 
 * @author elek
 * 
 */
public class Format {

	private boolean useBaseVersion = true;

	private boolean prependGroupId;

	private boolean removeVersion;

	private boolean removeClassifier;

	private boolean repositoryFormat;

	/**
	 * Strip artifact version during copy (only works if prefix is set)
	 * 
	 * @parameter expression="${mdep.stripVersion}" default-value="false"
	 * @parameter
	 */
	private boolean stripVersion = false;

	/**
	 * The prefix to prepend on each dependent artifact. If undefined, the paths
	 * refer to the actual files store in the local repository (the stipVersion
	 * parameter does nothing then).
	 * 
	 * @parameter expression="${mdep.prefix}"
	 */
	private String prefix;

	/**
	 * Override the char used between the paths. This field is initialized to
	 * contain the first character of the value of the system property
	 * file.separator. On UNIX systems the value of this field is '/'; on
	 * Microsoft Windows systems it is '\'. The default is File.separator
	 * 
	 * @since 2.0
	 * @parameter default-value="" expression="${mdep.fileSeparator}"
	 */
	private String fileSeparator;

	/**
	 * Replace the absolute path to the local repo with this property. This
	 * field is ignored it prefix is declared. The value will be forced to
	 * "${M2_REPO}" if no value is provided AND the attach flag is true.
	 * 
	 * @since 2.0
	 * @parameter default-value="" expression="${mdep.localRepoProperty}"
	 */
	private String localRepoProperty;

	public String getFormattedFileName(Artifact artifact) {

		StringBuilder destFileName = new StringBuilder();

		if (repositoryFormat) {
			//
			destFileName.append(
					artifact.getGroupId().replace('.', File.separatorChar))
					.append(File.separatorChar);
			// artifact id
			destFileName.append(artifact.getArtifactId()).append(
					File.separatorChar);
			// version
			destFileName.append(artifact.getBaseVersion()).append(
					File.separatorChar);

		}
		if (prependGroupId) {
			destFileName.append(artifact.getGroupId()).append(".");
		}

		destFileName.append(artifact.getArtifactId());
		if (!removeVersion) {
			destFileName.append("-");
			if (useBaseVersion) {
				destFileName.append(artifact.getBaseVersion());
			} else {
				destFileName.append(artifact.getVersion());
			}
		}
		if (!removeClassifier
				&& StringUtils.isNotEmpty(artifact.getClassifier())) {
			destFileName.append("-");
			destFileName.append(artifact.getClassifier());
		}
		destFileName.append(".");
		destFileName.append(artifact.getArtifactHandler().getExtension());

		return destFileName.toString();
	}
}
