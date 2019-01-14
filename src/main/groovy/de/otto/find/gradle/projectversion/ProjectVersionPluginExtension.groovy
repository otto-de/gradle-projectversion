package de.otto.find.gradle.projectversion

import org.gradle.api.Project
import org.gradle.api.provider.Property

class ProjectVersionPluginExtension {
    private final Project project
    final Property<ProjectVersion> version

    ProjectVersionPluginExtension(Project project) {
        this.project = project
        version = project.objects.property(ProjectVersion)
    }

    ProjectVersion getVersion() {
        version.getOrElse(FixedVersion.defaultVersion())
    }

    void setVersion(String version) {
        this.version.set(FixedVersion.fixed(version))
        apply()
    }

    void setVersion(ProjectVersionResolver resolver) {
        this.version.set(resolver.currentVersion())
        apply()
    }

    void deriveFromGitTag() {
        setVersion(GitProjectVersionResolver.gitProjectVersionResolver(GitCommit.gitCommit(project)))
    }

    private void apply() {
        project.version = getVersion()
    }
}
