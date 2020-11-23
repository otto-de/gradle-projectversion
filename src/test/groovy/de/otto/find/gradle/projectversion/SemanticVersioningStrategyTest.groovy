package de.otto.find.gradle.projectversion

import org.assertj.core.api.Assertions
import org.testng.annotations.Test

import static de.otto.find.gradle.projectversion.SemanticVersion.semantic

class SemanticVersioningStrategyTest {

    @Test
    void testUntaggedCommitSquash() {
        SemanticVersioningOptions options = new SemanticVersioningOptions()
        options.squash = true

        def actual = SemanticVersioningStrategy.semanticVersioningStrategy('main', 2, false)
                .nextVersion(semantic(0, 8, 0, true), options)

        Assertions.assertThat(actual)
                .isEqualTo(semantic(0, 9, 0, true))
    }

    @Test
    void testUntaggedCommit() {
        SemanticVersioningOptions options = new SemanticVersioningOptions()

        def actual = SemanticVersioningStrategy.semanticVersioningStrategy('main', 2, false)
                .nextVersion(semantic(0, 8, 0, true), options)

        Assertions.assertThat(actual)
                .isEqualTo(semantic(0, 10, 0, true))
    }

    @Test
    void testHigherMajor() {
        SemanticVersioningOptions options = new SemanticVersioningOptions()
        options.majorVersion = 1

        def actual = SemanticVersioningStrategy.semanticVersioningStrategy('main', 2, false)
                .nextVersion(semantic(0, 8, 0, true), options)

        Assertions.assertThat(actual)
                .isEqualTo(semantic(1, 0, 0, true))
    }

    @Test
    void testIntegrateToReleaseSquash() {
        SemanticVersioningOptions options = new SemanticVersioningOptions()
        options.squash = true
        options.defaultBranch = 'release'

        def actual = SemanticVersioningStrategy.semanticVersioningStrategy('main', 2, false)
                .nextVersion(semantic(0, 8, 0, true), options)

        Assertions.assertThat(actual)
                .isEqualTo(semantic(0, 8, 1, true))
    }

    @Test
    void testIntegrateToRelease() {
        SemanticVersioningOptions options = new SemanticVersioningOptions()
        options.defaultBranch = 'release'

        def actual = SemanticVersioningStrategy.semanticVersioningStrategy('main', 2, false)
                .nextVersion(semantic(0, 8, 0, true), options)

        Assertions.assertThat(actual)
                .isEqualTo(semantic(0, 8, 2, true))
    }

    @Test
    void testDirtyWorkspaceSquash() {
        SemanticVersioningOptions options = new SemanticVersioningOptions()
        options.squash = true

        def actual = SemanticVersioningStrategy.semanticVersioningStrategy('main', 2, true)
                .nextVersion(semantic(0, 8, 0, true), options)

        Assertions.assertThat(actual)
                .isEqualTo(semantic(0, 9, 0, false))
    }

    @Test
    void testDirtyWorkspace() {
        SemanticVersioningOptions options = new SemanticVersioningOptions()

        def actual = SemanticVersioningStrategy.semanticVersioningStrategy('main', 3, true)
                .nextVersion(semantic(0, 8, 0, true), options)

        Assertions.assertThat(actual)
                .isEqualTo(semantic(0, 11, 0, false))
    }


}
