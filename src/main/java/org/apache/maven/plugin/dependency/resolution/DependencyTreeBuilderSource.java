package org.apache.maven.plugin.dependency.resolution;

import java.io.StringWriter;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;
import org.apache.maven.shared.dependency.tree.filter.AncestorOrSelfDependencyNodeFilter;
import org.apache.maven.shared.dependency.tree.filter.DependencyNodeFilter;
import org.apache.maven.shared.dependency.tree.traversal.BuildingDependencyNodeVisitor;
import org.apache.maven.shared.dependency.tree.traversal.CollectingDependencyNodeVisitor;
import org.apache.maven.shared.dependency.tree.traversal.DependencyNodeVisitor;
import org.apache.maven.shared.dependency.tree.traversal.FilteringDependencyNodeVisitor;

public class DependencyTreeBuilderSource implements DependencySource {

	public DependencyNode getArtifactTree() {
		return null;
	}

//	/**
//	 * The dependency tree builder to use.
//	 * 
//	 * @component
//	 * @required
//	 * @readonly
//	 */
//	private DependencyTreeBuilder dependencyTreeBuilder;
//
//    /**
//     * @component role="org.apache.maven.artifact.metadata.ArtifactMetadataSource"
//     *            hint="maven"
//     * @required
//     * @readonly
//     */
//    protected ArtifactMetadataSource artifactMetadataSource;
//
//	private DependencyNode rootNode;
//
//	public Set<Artifact> getArtifactSet() {
//		rootNode = dependencyTreeBuilder.buildDependencyTree(project,
//				localRepository, artifactFactory, artifactMetadataSource,
//				artifactFilter, artifactCollector);
//
//		StringWriter writer = new StringWriter();
//
//		DependencyNodeVisitor visitor = getSerializingDependencyNodeVisitor(writer);
//
//		// TODO: remove the need for this when the serializer can calculate last
//		// nodes from visitor calls only
//		visitor = new BuildingDependencyNodeVisitor(visitor);
//
//		DependencyNodeFilter filter = createDependencyNodeFilter();
//
//		if (filter != null) {
//			CollectingDependencyNodeVisitor collectingVisitor = new CollectingDependencyNodeVisitor();
//			DependencyNodeVisitor firstPassVisitor = new FilteringDependencyNodeVisitor(
//					collectingVisitor, filter);
//			rootNode.accept(firstPassVisitor);
//
//			DependencyNodeFilter secondPassFilter = new AncestorOrSelfDependencyNodeFilter(
//					collectingVisitor.getNodes());
//			visitor = new FilteringDependencyNodeVisitor(visitor,
//					secondPassFilter);
//		}
//
//		rootNode.accept(visitor);
//
//		return writer.toString();
//	}

}
