package de.otto.find.gradle.projectversion

import org.gradle.api.DefaultTask
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

class ProjectVersionTag extends DefaultTask {

    @Input
    final Property<GitCommit> gitCommit

    @Inject
    ProjectVersionTag(ObjectFactory objects) {
        this.gitCommit = objects.property(GitCommit)
    }

    @TaskAction
    void tagVersion() {
        logger.quiet gitCommit.get().tag(project.version)
    }
}
