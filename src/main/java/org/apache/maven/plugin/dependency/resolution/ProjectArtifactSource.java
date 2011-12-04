package org.apache.maven.plugin.dependency.resolution;

import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.tree.DependencyNode;

public class ProjectArtifactSource implements DependencySource {

	MavenProject project;

	public ProjectArtifactSource(MavenProject project) {
		super();
		this.project = project;
	}

	public DependencyNode getArtifactTree() {
		DependencyNode node = new DependencyNode(project.getArtifact());
		for (Artifact artifact : (Set<Artifact>) project.getArtifacts()) {
			node.addChild(new DependencyNode(artifact));
		}
		return node;
	}
}
