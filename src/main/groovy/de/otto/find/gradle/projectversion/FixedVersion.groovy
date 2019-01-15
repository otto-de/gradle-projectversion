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

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        FixedVersion that = (FixedVersion) o

        if (version != that.version) return false

        return true
    }

    int hashCode() {
        return (version != null ? version.hashCode() : 0)
    }
}
