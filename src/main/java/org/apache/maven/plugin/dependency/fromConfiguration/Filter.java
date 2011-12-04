package org.apache.maven.plugin.dependency.fromConfiguration;

import org.apache.maven.plugin.dependency.utils.DependencyUtil;
import org.apache.maven.shared.artifact.filter.collection.ArtifactIdFilter;
import org.apache.maven.shared.artifact.filter.collection.ClassifierFilter;
import org.apache.maven.shared.artifact.filter.collection.FilterArtifacts;
import org.apache.maven.shared.artifact.filter.collection.GroupIdFilter;
import org.apache.maven.shared.artifact.filter.collection.ProjectTransitivityFilter;
import org.apache.maven.shared.artifact.filter.collection.ScopeFilter;
import org.apache.maven.shared.artifact.filter.collection.TypeFilter;

/**
 * Filter to define include and exclude pattern for the dependency list.
 * 
 * @author elek
 * 
 */
public class Filter {
	/**
	 * If we should exclude transitive dependencies
	 * 
	 * @since 2.0
	 * @optional
	 * @parameter expression="${excludeTransitive}" default-value="false"
	 */
	protected boolean excludeTransitive;

	/**
	 * Comma Separated list of Types to include. Empty String indicates include
	 * everything (default).
	 * 
	 * @since 2.0
	 * @parameter expression="${includeTypes}" default-value=""
	 * @optional
	 */
	protected String includeTypes;

	/**
	 * Comma Separated list of Types to exclude. Empty String indicates don't
	 * exclude anything (default).
	 * 
	 * @since 2.0
	 * @parameter expression="${excludeTypes}" default-value=""
	 * @optional
	 */
	protected String excludeTypes;

	/**
	 * Scope to include. An Empty string indicates all scopes (default).
	 * 
	 * @since 2.0
	 * @parameter expression="${includeScope}" default-value=""
	 * @optional
	 */
	protected String includeScope;

	/**
	 * Scope to exclude. An Empty string indicates no scopes (default).
	 * 
	 * @since 2.0
	 * @parameter expression="${excludeScope}" default-value=""
	 * @optional
	 */
	protected String excludeScope;

	/**
	 * Comma Separated list of Classifiers to include. Empty String indicates
	 * include everything (default).
	 * 
	 * @since 2.0
	 * @parameter expression="${includeClassifiers}" default-value=""
	 * @optional
	 */
	protected String includeClassifiers;

	/**
	 * Comma Separated list of Classifiers to exclude. Empty String indicates
	 * don't exclude anything (default).
	 * 
	 * @since 2.0
	 * @parameter expression="${excludeClassifiers}" default-value=""
	 * @optional
	 */
	protected String excludeClassifiers;

	/**
	 * Specify classifier to look for. Example: sources
	 * 
	 * @optional
	 * @since 2.0
	 * @parameter expression="${classifier}" default-value=""
	 */
	protected String classifier;

	/**
	 * Specify type to look for when constructing artifact based on classifier.
	 * Example: java-source,jar,war
	 * 
	 * @optional
	 * @since 2.0
	 * @parameter expression="${type}" default-value="java-source"
	 */
	protected String type;

	/**
	 * Comma separated list of Artifact names too exclude.
	 * 
	 * @since 2.0
	 * @optional
	 * @parameter expression="${excludeArtifactIds}" default-value=""
	 */
	protected String excludeArtifactIds;

	/**
	 * Comma separated list of Artifact names to include.
	 * 
	 * @since 2.0
	 * @optional
	 * @parameter expression="${includeArtifactIds}" default-value=""
	 */
	protected String includeArtifactIds;

	/**
	 * Comma separated list of GroupId Names to exclude.
	 * 
	 * @since 2.0
	 * @optional
	 * @parameter expression="${excludeGroupIds}" default-value=""
	 */
	protected String excludeGroupIds;
	/**
	 * Comma separated list of GroupIds to include.
	 * 
	 * @since 2.0
	 * @optional
	 * @parameter expression="${includeGroupIds}" default-value=""
	 */
	protected String includeGroupIds;

	public boolean isExcludeTransitive() {
		return excludeTransitive;
	}

	public String getIncludeTypes() {
		return includeTypes;
	}

	public String getExcludeTypes() {
		return excludeTypes;
	}

	public String getIncludeScope() {
		return includeScope;
	}

	public String getExcludeScope() {
		return excludeScope;
	}

	public String getIncludeClassifiers() {
		return includeClassifiers;
	}

	public String getExcludeClassifiers() {
		return excludeClassifiers;
	}

	public String getClassifier() {
		return classifier;
	}

	public String getType() {
		return type;
	}

	public String getExcludeArtifactIds() {
		return excludeArtifactIds;
	}

	public String getIncludeArtifactIds() {
		return includeArtifactIds;
	}

	public String getExcludeGroupIds() {
		return excludeGroupIds;
	}

	public void setExcludeTransitive(boolean excludeTransitive) {
		this.excludeTransitive = excludeTransitive;
	}

	public void setIncludeTypes(String includeTypes) {
		this.includeTypes = includeTypes;
	}

	public void setExcludeTypes(String excludeTypes) {
		this.excludeTypes = excludeTypes;
	}

	public void setIncludeScope(String includeScope) {
		this.includeScope = includeScope;
	}

	public void setExcludeScope(String excludeScope) {
		this.excludeScope = excludeScope;
	}

	public void setIncludeClassifiers(String includeClassifiers) {
		this.includeClassifiers = includeClassifiers;
	}

	public void setExcludeClassifiers(String excludeClassifiers) {
		this.excludeClassifiers = excludeClassifiers;
	}

	public void setClassifier(String classifier) {
		this.classifier = classifier;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setExcludeArtifactIds(String excludeArtifactIds) {
		this.excludeArtifactIds = excludeArtifactIds;
	}

	public void setIncludeArtifactIds(String includeArtifactIds) {
		this.includeArtifactIds = includeArtifactIds;
	}

	public void setExcludeGroupIds(String excludeGroupIds) {
		this.excludeGroupIds = excludeGroupIds;
	}

	public void setIncludeGroupIds(String includeGroupIds) {
		this.includeGroupIds = includeGroupIds;
	}

	public FilterArtifacts createArtifactFilter() {
		FilterArtifacts filter = new FilterArtifacts();

		filter.addFilter(new ScopeFilter(DependencyUtil
				.cleanToBeTokenizedString(this.includeScope), DependencyUtil
				.cleanToBeTokenizedString(this.excludeScope)));

		filter.addFilter(new TypeFilter(DependencyUtil
				.cleanToBeTokenizedString(this.includeTypes), DependencyUtil
				.cleanToBeTokenizedString(this.excludeTypes)));

		filter.addFilter(new ClassifierFilter(DependencyUtil
				.cleanToBeTokenizedString(this.includeClassifiers),
				DependencyUtil
						.cleanToBeTokenizedString(this.excludeClassifiers)));

		filter.addFilter(new GroupIdFilter(DependencyUtil
				.cleanToBeTokenizedString(this.includeGroupIds), DependencyUtil
				.cleanToBeTokenizedString(this.excludeGroupIds)));

		filter.addFilter(new ArtifactIdFilter(DependencyUtil
				.cleanToBeTokenizedString(this.includeArtifactIds),
				DependencyUtil
						.cleanToBeTokenizedString(this.excludeArtifactIds)));

		return filter;
	}

}
