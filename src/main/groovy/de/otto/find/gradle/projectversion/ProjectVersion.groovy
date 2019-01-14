package de.otto.find.gradle.projectversion

abstract class ProjectVersion {
    protected abstract String getVersion()

    @Override
    String toString() {
        getVersion()
    }
}
