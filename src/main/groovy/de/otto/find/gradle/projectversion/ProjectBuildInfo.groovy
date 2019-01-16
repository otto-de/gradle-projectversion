package de.otto.find.gradle.projectversion

import org.gradle.api.DefaultTask
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

class ProjectBuildInfo extends DefaultTask {

    @Input
    final MapProperty<String, String> buildInfo

    @Inject
    ProjectBuildInfo(ObjectFactory factory) {
        this.buildInfo = factory.mapProperty(String, String)
    }

    @TaskAction
    void displayBuildInfo() {
        buildInfo.get().each { k, v -> logger.quiet "$k=$v" }
    }
}
