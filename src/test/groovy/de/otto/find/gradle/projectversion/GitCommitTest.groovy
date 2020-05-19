package de.otto.find.gradle.projectversion

import org.assertj.core.api.Assertions
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.testng.annotations.Test

import static de.otto.find.gradle.projectversion.SemanticVersion.semantic

class GitCommitTest {

    @Test
    void testUntaggedCommit() {
        Project project = ProjectBuilder.builder().withName("test").build();
        GitCommit gitCommit = GitCommit.gitCommit(project);

        gitCommit.description = ['0.8.0', '2', 'gf133199']

        Assertions.assertThat(gitCommit)
                .extracting({ g -> g.distance })
                .isEqualTo(2)
        Assertions.assertThat(gitCommit)
                .extracting({ g -> g.untaggedOrDirty })
                .isEqualTo(true)
        Assertions.assertThat(gitCommit)
                .extracting({ g -> g.dirty })
                .isEqualTo(false)
        Assertions.assertThat(gitCommit)
                .extracting({ g -> g.parseAsVersion({ s -> semantic(s) }) })
                .isEqualTo(semantic(0, 8, 0, true))
    }

    @Test
    void testTaggedCommit() {
        Project project = ProjectBuilder.builder().withName("test").build();
        GitCommit gitCommit = GitCommit.gitCommit(project);

        gitCommit.description = ['0.8.0']

        Assertions.assertThat(gitCommit)
                .extracting({ g -> g.distance })
                .isEqualTo(0)
        Assertions.assertThat(gitCommit)
                .extracting({ g -> g.untaggedOrDirty })
                .isEqualTo(false)
        Assertions.assertThat(gitCommit)
                .extracting({ g -> g.dirty })
                .isEqualTo(false)
        Assertions.assertThat(gitCommit)
                .extracting({ g -> g.parseAsVersion({ s -> semantic(s) }) })
                .isEqualTo(semantic(0, 8, 0, true))
    }

    @Test
    void testDirtyWorkspace() {
        Project project = ProjectBuilder.builder().withName("test").build();
        GitCommit gitCommit = GitCommit.gitCommit(project);

        gitCommit.description = ['0.8.0', '2', 'gf133199', 'dirty']

        Assertions.assertThat(gitCommit)
                .extracting({ g -> g.distance })
                .isEqualTo(3)
        Assertions.assertThat(gitCommit)
                .extracting({ g -> g.untaggedOrDirty })
                .isEqualTo(true)
        Assertions.assertThat(gitCommit)
                .extracting({ g -> g.dirty })
                .isEqualTo(true)
        Assertions.assertThat(gitCommit)
                .extracting({ g -> g.parseAsVersion({ s -> semantic(s) }) })
                .isEqualTo(semantic(0, 8, 0, true))
    }

}
