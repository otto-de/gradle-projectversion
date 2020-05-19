package de.otto.find.gradle.projectversion

import org.gradle.api.Action
import org.gradle.internal.Actions
import org.gradle.internal.Cast
import org.gradle.util.ConfigureUtil

import javax.annotation.Nullable

import static java.util.Objects.requireNonNull

class SemanticVersioningOptions extends VersioningStrategyOptions {

    int majorVersion = 0
    boolean squash = false
    String defaultBranch = 'master'

    private static <T extends SprintNumber> int resolveSprintNumber(
            T sprintNumber,
            @Nullable Action<? super T> resolverConfigure) {
        requireNonNull(sprintNumber)
        if (resolverConfigure != null) {
            resolverConfigure.execute(Cast.<T> uncheckedCast(sprintNumber))
        }
        sprintNumber.sprintNumber()
    }

    void useSprintNumber() {
        useSprintNumber(Actions.<SprintNumber> doNothing())
    }

    void useSprintNumber(@Nullable Closure sprintNumberConfigure) {
        useSprintNumber(ConfigureUtil.<SprintNumber> configureUsing(sprintNumberConfigure))
    }

    void useSprintNumber(Action<? super SprintNumber> sprintNumberConfigure) {
        majorVersion = resolveSprintNumber(new SprintNumber(), sprintNumberConfigure)
    }
}

