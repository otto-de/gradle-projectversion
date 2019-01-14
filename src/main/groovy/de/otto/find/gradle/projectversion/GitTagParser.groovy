package de.otto.find.gradle.projectversion

interface GitTagParser<T extends ProjectVersion> {
    T parseAsVersion(String versionString)
}
