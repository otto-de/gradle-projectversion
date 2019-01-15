package de.otto.find.gradle.projectversion

class GitProjectVersionResolver <T extends ProjectVersion> implements ProjectVersionResolver {

    private final GitCommit gitCommit
    private final GitTagParser<T> gitTagParser
    private final VersioningStrategy<T> strategy
    private final VersioningStrategyOptions options

    private GitProjectVersionResolver(GitCommit gitCommit,
                                      GitTagParser<T> gitTagParser,
                                      VersioningStrategy<T> strategy,
                                      VersioningStrategyOptions options) {
        this.gitCommit = gitCommit
        this.gitTagParser = gitTagParser
        this.strategy = strategy
        this.options = options
    }

    static ProjectVersionResolver gitProjectVersionResolver(GitCommit gitCommit) {
        new GitProjectVersionResolver(gitCommit,
                { String versionString ->
                    SemanticVersion.semantic(versionString)
                },
                SemanticVersioningStrategy.semanticVersioningStrategy(gitCommit),
                new SemanticVersioningOptions())
    }

    static <T extends ProjectVersion> GitProjectVersionResolver<T> gitProjectVersionResolver(
            GitCommit gitCommit,
            GitTagParser<T> gitTagParser,
            VersioningStrategy<T> strategy,
            VersioningStrategyOptions options) {
        new GitProjectVersionResolver<>(gitCommit,
                Objects.requireNonNull(gitTagParser),
                Objects.requireNonNull(strategy),
                Objects.requireNonNull(options)
        )
    }

    @Override
    T currentVersion() {
        List<String> descriptionParts = gitCommit.describe()
        def currentVersion = gitTagParser.parseAsVersion(descriptionParts[0])

        // only increment version number if changes are present to last tagged commit
        GitCommit.isUntaggedOrDirty(descriptionParts) ?
                strategy.nextVersion(currentVersion, options) :
                currentVersion
    }

    @Override
    VersioningStrategyOptions getOptions() {
        options
    }
}
