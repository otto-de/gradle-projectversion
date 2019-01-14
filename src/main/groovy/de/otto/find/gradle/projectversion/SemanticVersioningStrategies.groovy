package de.otto.find.gradle.projectversion

class SemanticVersioningStrategies {

    static VersioningStrategy<SemanticVersion> defaultStrategy(GitCommit gitCommit,
                                                               int major,
                                                               String defaultBranch) {
        def description = gitCommit.describe()
        def dirty = gitCommit.isDirty(description)
        gitCommit.isBranch(defaultBranch) ?
                bumpRegularVersion(major, !dirty) :
                bumpPatchVersion(!dirty)
    }

    private static VersioningStrategy<SemanticVersion> bumpRegularVersion(int major, boolean released) {
        new VersioningStrategy<SemanticVersion>() {
            @Override
            SemanticVersion nextVersion(SemanticVersion oldVersion) {
                oldVersion.nextMinor().withMinimumMajor(major).withReleased(released)
            }

        }
    }

    private static VersioningStrategy<SemanticVersion> bumpPatchVersion(boolean released) {
        new VersioningStrategy<SemanticVersion>() {
            @Override
            SemanticVersion nextVersion(SemanticVersion oldVersion) {
                oldVersion.nextPatch().withReleased(released)
            }
        }
    }
}

