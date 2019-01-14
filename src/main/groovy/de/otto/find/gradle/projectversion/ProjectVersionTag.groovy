package de.otto.find.gradle.projectversion

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class ProjectVersionTag extends DefaultTask {

    @TaskAction
    void tagVersion() {
        GitCommit commit = GitCommit.gitCommit(project)
        logger.quiet commit.tag(project.version)
    }
}
