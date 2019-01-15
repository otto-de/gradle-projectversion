package de.otto.find.gradle.projectversion

interface VersioningStrategy<T extends ProjectVersion> {
    T nextVersion(T oldVersion, VersioningStrategyOptions options)
}
