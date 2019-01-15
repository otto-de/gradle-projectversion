package de.otto.find.gradle.projectversion

import static de.otto.find.gradle.projectversion.SemanticVersioningStrategy.semanticVersioningStrategy
import static de.otto.find.gradle.projectversion.StaticVersionResolver.staticVersionResolver

class GitProjectVersionResolver<T extends ProjectVersion> implements ProjectVersionResolver {

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

    static <T extends ProjectVersion> GitProjectVersionResolver<T> gitProjectVersionResolver(
            GitCommit gitCommit,
            GitTagParser<T> gitTagParser,
            VersioningStrategy<T> strategy,
            VersioningStrategyOptions options) {

        // only increment version number if changes are present to last tagged commit
        gitCommit.isUntaggedOrDirty() ?
                new GitProjectVersionResolver<>(gitCommit,
                        Objects.requireNonNull(gitTagParser),
                        Objects.requireNonNull(strategy),
                        Objects.requireNonNull(options)) :
                staticVersionResolver(gitCommit.parseAsVersion(gitTagParser), Objects.requireNonNull(options))

        new GitProjectVersionResolver<>(gitCommit,
                Objects.requireNonNull(gitTagParser),
                Objects.requireNonNull(strategy),
                Objects.requireNonNull(options)
        )
    }

    static ProjectVersionResolver gitProjectVersionResolver(GitCommit gitCommit) {
        gitProjectVersionResolver(gitCommit, { String versionString ->
            SemanticVersion.semantic(versionString)
        }, semanticVersioningStrategy(gitCommit), new SemanticVersioningOptions())
    }

    @Override
    T currentVersion() {
        T currentVersion = gitCommit.parseAsVersion gitTagParser
        strategy.nextVersion(currentVersion, options)
    }

    @Override
    VersioningStrategyOptions getOptions() {
        options
    }
}
