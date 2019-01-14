package de.otto.find.gradle.projectversion

final class FixedVersion extends ProjectVersion {
    private static final ProjectVersion DEFAULT_VERSION = fixed('0.1.0')

    final String version

    private FixedVersion(String version) {
        this.version = version
    }

    static ProjectVersion fixed(String version) {
        new FixedVersion(Objects.requireNonNull(version))
    }

    static ProjectVersion defaultVersion() {
        DEFAULT_VERSION
    }
}
