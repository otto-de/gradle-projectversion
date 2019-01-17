package de.otto.find.gradle.projectversion

class SemanticVersion extends ProjectVersion {
    private final int major
    private final int minor
    private final int patch
    private final boolean released

    private SemanticVersion(int major, int minor, int patch, boolean released) {
        this.major = major
        this.minor = minor
        this.patch = patch
        this.released = released
    }

    static SemanticVersion semantic(int major, int minor, int patch, boolean released) {
        new SemanticVersion(major, minor, patch, released)
    }

    static SemanticVersion semantic(String version) {
        def versionParts = version.tokenize(".")
        new SemanticVersion(
                Integer.valueOf(versionParts[0]),
                Integer.valueOf(versionParts[1]),
                Integer.valueOf(versionParts[2]),
                true)
    }

    SemanticVersion withMinimumMajor(int minMajor) {
        minMajor > major ?
                new SemanticVersion(minMajor, 0, 0, false) :
                this
    }

    SemanticVersion nextMinor() {
        new SemanticVersion(major, minor + 1, 0, false)
    }

    SemanticVersion nextPatch() {
        new SemanticVersion(major, minor, patch + 1, false)
    }

    SemanticVersion withReleased(boolean status) {
        released == status ? this : new SemanticVersion(major, minor, patch, status)
    }

    @Override
    protected String getVersion() {
        released ? "$major.$minor.$patch" : "$major.$minor.$patch-SNAPSHOT"
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        SemanticVersion that = (SemanticVersion) o

        if (major != that.major) return false
        if (minor != that.minor) return false
        if (patch != that.patch) return false
        if (released != that.released) return false

        return true
    }

    int hashCode() {
        int result
        result = major
        result = 31 * result + minor
        result = 31 * result + patch
        result = 31 * result + (released ? 1 : 0)
        return result
    }
}
