package de.otto.find.gradle.projectversion

import static java.util.Objects.requireNonNull

class StaticVersionResolver<T extends ProjectVersion> implements ProjectVersionResolver {

    private final T version
    private final VersioningStrategyOptions options

    private StaticVersionResolver(T version, VersioningStrategyOptions options) {
        this.version = version
        this.options = options
    }

    static <T extends ProjectVersion> ProjectVersionResolver staticVersionResolver(T version,
                                                                                   VersioningStrategyOptions options) {
        new StaticVersionResolver<>(requireNonNull(version), requireNonNull(options))
    }

    @Override
    T currentVersion() {
        return version
    }

    @Override
    VersioningStrategyOptions getOptions() {
        return options
    }
}
