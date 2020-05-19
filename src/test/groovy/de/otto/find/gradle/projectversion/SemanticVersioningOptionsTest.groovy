package de.otto.find.gradle.projectversion

import org.assertj.core.api.Assertions
import org.testng.annotations.Test

import static de.otto.find.gradle.projectversion.SemanticVersion.semantic
import static de.otto.find.gradle.projectversion.SemanticVersioningStrategy.semanticVersioningStrategy

class SemanticVersioningOptionsTest {

    @Test
    void testSemanticVersioningOptionsDefaults() {
        SemanticVersioningOptions options = new SemanticVersioningOptions()

        Assertions.assertThat(options)
                .extracting({ o -> o.defaultBranch })
                .isEqualTo('master')
        Assertions.assertThat(options)
                .extracting({ o -> o.squash })
                .isEqualTo(false)
        Assertions.assertThat(options)
                .extracting({ o -> o.majorVersion })
                .isEqualTo(0)
    }

}
