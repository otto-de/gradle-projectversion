package de.otto.find.gradle.projectversion

class GitProjectVersionResolver <T extends ProjectVersion> implements ProjectVersionResolver {

    private final GitCommit gitCommit
    private final GitTagParser<T> gitTagParser
    private final VersioningStrategy<T> strategy

    private GitProjectVersionResolver(GitCommit gitCommit,
                                      GitTagParser<T> gitTagParser,
                                      VersioningStrategy<T> strategy) {
        this.gitCommit = gitCommit
        this.gitTagParser = gitTagParser
        this.strategy = strategy
    }

    static GitProjectVersionResolver gitProjectVersionResolver(GitCommit gitCommit,
                                                               int majorVersion = 0,
                                                               String defaultBranch = 'master') {
        new GitProjectVersionResolver(gitCommit,
                { String versionString ->
                    SemanticVersion.semantic(versionString)
                },
                SemanticVersioningStrategies.defaultStrategy(gitCommit, majorVersion, defaultBranch))
    }

    static <T extends ProjectVersion> GitProjectVersionResolver<T> gitProjectVersionResolver(
            GitCommit gitCommit,
            GitTagParser<T> gitTagParser,
            VersioningStrategy<T> strategy) {
        new GitProjectVersionResolver<>(gitCommit,
                Objects.requireNonNull(gitTagParser),
                Objects.requireNonNull(strategy))
    }

    @Override
    T currentVersion() {
        List<String> descriptionParts = gitCommit.describe()
        def currentVersion = gitTagParser.parseAsVersion(descriptionParts[0])

        // only increment version number if changes are present to last tagged commit
        GitCommit.isUntaggedOrDirty(descriptionParts) ?
                strategy.nextVersion(currentVersion) :
                currentVersion
    }
}
