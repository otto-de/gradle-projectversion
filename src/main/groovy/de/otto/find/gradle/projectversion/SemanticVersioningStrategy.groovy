package de.otto.find.gradle.projectversion

import org.gradle.internal.Cast

class SemanticVersioningStrategy implements VersioningStrategy<SemanticVersion> {

    final String branch
    final int distance
    final boolean dirty

    SemanticVersioningStrategy(String branch, int distance, boolean dirty) {
        this.branch = branch
        this.distance = distance
        this.dirty = dirty
    }

    static VersioningStrategy<SemanticVersion> semanticVersioningStrategy(String branch, int distance, boolean dirty) {
        new SemanticVersioningStrategy(branch, distance, dirty)
    }

    static VersioningStrategy<SemanticVersion> semanticVersioningStrategy(GitCommit gitCommit) {
        semanticVersioningStrategy(
                gitCommit.branch,
                gitCommit.distance,
                gitCommit.dirty)
    }

    @Override
    SemanticVersion nextVersion(SemanticVersion oldVersion, VersioningStrategyOptions options) {
        def semanticVersioningOptions = Cast.<SemanticVersioningOptions> uncheckedCast(options)
        return branch == null || branch.isEmpty() || semanticVersioningOptions.defaultBranch == branch ?
                oldVersion
                        .nextMinor(semanticVersioningOptions.squash ? 1 : distance)
                        .withMinimumMajor(semanticVersioningOptions.majorVersion)
                        .withReleased(!dirty) :
                oldVersion
                        .nextPatch(semanticVersioningOptions.squash ? 1 : distance)
                        .withReleased(!dirty)
    }
}

