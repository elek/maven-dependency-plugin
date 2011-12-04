package org.apache.maven.plugin.dependency.resolution;

import org.apache.maven.shared.dependency.tree.DependencyNode;

/**
 * Artifact resolution mode.
 * 
 * @author elek
 * 
 */
public interface DependencySource {

	DependencyNode getArtifactTree();

}
