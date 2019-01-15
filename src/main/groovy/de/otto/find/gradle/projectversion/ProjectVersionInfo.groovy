package de.otto.find.gradle.projectversion

import org.gradle.api.DefaultTask
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

class ProjectVersionInfo extends DefaultTask {

    @Input
    final Property<GitCommit> gitCommit
    @Input
    final Property<ProjectVersion> version

    @Inject
    ProjectVersionInfo(ObjectFactory factory) {
        this.gitCommit = factory.property(GitCommit)
        this.version = factory.property(ProjectVersion)
    }

    @TaskAction
    void printInfo() {
        inputs.properties.each { k, v ->
            logger.quiet "${k} : ${v}"
        }
    }
}
