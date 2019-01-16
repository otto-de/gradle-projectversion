package de.otto.find.gradle.projectversion

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

class ProjectBuildInfo extends DefaultTask {

    @Input
    final Property<GitCommit> gitCommit

    @Input
    final Property<ProjectVersion> version

    @Input
    final MapProperty<String, String> buildInfo

    @Inject
    ProjectBuildInfo(ObjectFactory factory, ProviderFactory providerFactory) {
        gitCommit = factory.property(GitCommit)
        version = factory.property(ProjectVersion)
        Provider<Project> base = providerFactory.provider({ project })

        MapProperty<String, String> property = factory.mapProperty(String, String)
        property.put('build.gradleVersion', base.map({ p -> p.gradle.gradleVersion }))
        property.put('build.name', base.map({ p -> p.name }))
        property.put('build.group', base.map({ p -> p.group.toString() }))
        property.put('build.version', version.map({ v -> v.toString() }))
        property.put('build.commit', gitCommit.map({ gC -> gC.id }))
        property.put('build.branch', gitCommit.map({ gC -> gC.branch }))
        this.buildInfo = property
    }

    ProjectBuildInfo addGradleVersion(String gradleVersion) {
        addBuildInfo('build.gradleVersion', gradleVersion)
    }

    ProjectBuildInfo addProjectName(String projectName) {
        addBuildInfo('build.name', projectName)
    }

    ProjectBuildInfo addProjectGroup(String projectGroup) {
        addBuildInfo('build.group', projectGroup)
    }


    ProjectBuildInfo addBuildInfo(String key, String value) {
        buildInfo.put key, value
        this
    }

    ProjectBuildInfo addBuildInfo(String key, Provider<String> value) {
        buildInfo.put key, value
        this
    }

    @TaskAction
    void displayBuildInfo() {
        buildInfo.get().each { k, v -> logger.quiet "${k} : ${v}" }
    }
}
