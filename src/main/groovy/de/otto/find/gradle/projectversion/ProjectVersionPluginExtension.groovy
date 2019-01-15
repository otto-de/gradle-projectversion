package de.otto.find.gradle.projectversion

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.internal.Actions
import org.gradle.internal.Cast
import org.gradle.util.ConfigureUtil

import javax.annotation.Nullable

import static de.otto.find.gradle.projectversion.GitCommit.gitCommit
import static de.otto.find.gradle.projectversion.GitProjectVersionResolver.gitProjectVersionResolver

class ProjectVersionPluginExtension {
    private final Project project

    final Property<GitCommit> gitCommit
    final Property<ProjectVersion> version

    private ProjectVersionResolver projectVersionResolver;

    ProjectVersionPluginExtension(Project project) {
        this.project = project
        ObjectFactory factory = project.objects
        version = factory
                .property(ProjectVersion)
                .convention(FixedVersion.defaultVersion())
        gitCommit = factory
                .property(GitCommit)
                .convention(gitCommit(project))
    }

    void setVersion(String version) {
        this.version.set(FixedVersion.fixed(version))
        apply()
    }

    void setVersion(ProjectVersionResolver resolver) {
        useProjectVersionResolver(resolver)
        applyResolver()
    }

    private void applyResolver() {
        this.version.set(projectVersionResolver.currentVersion())
        apply()
    }

    private void apply() {
        project.version = version.get()
    }

    private ProjectVersionResolver useProjectVersionResolver(ProjectVersionResolver resolver) {
        useProjectVersionResolver(resolver, null);
    }


    private <T extends VersioningStrategyOptions> ProjectVersionResolver useProjectVersionResolver(
            ProjectVersionResolver projectVersionResolver,
            @Nullable Action<? super T> resolverConfigure) {
        if (projectVersionResolver == null) {
            throw new IllegalArgumentException("projectVersionResolver is null!");
        }

        this.projectVersionResolver = projectVersionResolver;

        if (resolverConfigure != null) {
            resolverConfigure.execute(Cast.<T>uncheckedCast(this.projectVersionResolver.getOptions()));
        }

        this.projectVersionResolver
    }

    void useSemanticVersioning() {
        useSemanticVersioning(Actions.<SemanticVersioningOptions>doNothing());
    }

    void useSemanticVersioning(@Nullable Closure semanticVersioningConfigure) {
        useSemanticVersioning(ConfigureUtil.<SemanticVersioningOptions>configureUsing(semanticVersioningConfigure));
    }

    void useSemanticVersioning(Action<? super SemanticVersioningOptions> semanticVersioningConfigure) {
        this.<SemanticVersioningOptions>useProjectVersionResolver(getDefaultProjectVersionResolver(), semanticVersioningConfigure)

        // TODO: defer versioning to "END" of "projectVersion" closure
        applyResolver()
    }


    private ProjectVersionResolver getDefaultProjectVersionResolver() {
        gitProjectVersionResolver(gitCommit.get())
    }

    @Override
    String toString() {
        "ProjectVersionPluginExtension{project=$project, gitCommit=$gitCommit, version=$version, projectVersionResolver=$projectVersionResolver}";
    }
}
