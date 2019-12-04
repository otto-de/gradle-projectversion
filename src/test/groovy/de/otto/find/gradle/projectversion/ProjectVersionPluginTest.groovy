package de.otto.find.gradle.projectversion

import org.gradle.api.Project
import org.gradle.api.provider.MapProperty
import org.gradle.testfixtures.ProjectBuilder
import org.testng.annotations.Test

import java.time.Duration

import static de.otto.find.gradle.projectversion.FixedVersion.defaultVersion
import static de.otto.find.gradle.projectversion.FixedVersion.fixed
import static de.otto.find.gradle.projectversion.SemanticVersion.semantic
import static java.time.Instant.now
import static java.time.temporal.ChronoUnit.DAYS
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.instanceOf

class ProjectVersionPluginTest {

    @Test
    void testPluginDefaultsWhenApplied() {
        Project project = ProjectBuilder.builder().withName("test").build()

        project.pluginManager.apply ProjectVersionPlugin

        def pluginExtension = project.extensions.findByType(ProjectVersionPluginExtension)
        assertThat(project.version, equalTo('unspecified'))
        assertThat(pluginExtension.version.get(), equalTo(defaultVersion()))
    }

    @Test
    void testRegisterTasksWhenApplied() {
        Project project = ProjectBuilder.builder().withName("test").build()
        project.pluginManager.apply ProjectVersionPlugin

        def versionInfo = project.tasks.buildInfo
        def tagVersion = project.tasks.tagVersion

        assertThat(versionInfo, instanceOf(ProjectBuildInfo))
        assertThat(tagVersion, instanceOf(ProjectVersionTag))
    }

    @Test
    void testSettingFixedVersion() {
        Project project = ProjectBuilder.builder().withName("test").build()
        project.pluginManager.apply ProjectVersionPlugin

        project.extensions.projectVersion.version '0.3.0'

        assertThat(project.version, equalTo(fixed('0.3.0')))
    }

    @Test
    void testSettingGitDerivedVersion() {
        Project project = ProjectBuilder.builder().withName("test").build()
        project.pluginManager.apply ProjectVersionPlugin

        project.projectVersion {
            useSemanticVersioning()
        }

        assertThat(project.version, equalTo(semantic(0, 1, 0, true)))
    }

    @Test
    void testConfiguringGitDerivedVersion() {
        Project project = ProjectBuilder.builder().withName("test").build()
        project.pluginManager.apply ProjectVersionPlugin

        project.projectVersion {
            useSemanticVersioning() {
                majorVersion = 3
            }
        }

        assertThat(project.version, equalTo(semantic(3, 0, 0, true)))
    }

    @Test
    void testConfiguringSprintNumberBasedGitDerivedVersion() {
        Project project = ProjectBuilder.builder().withName("test").build()
        project.pluginManager.apply ProjectVersionPlugin

        project.projectVersion {
            useSemanticVersioning() {
                useSprintNumber() {
                    sprintStart = now().minus(50, DAYS)
                    sprintLength = Duration.ofDays(20)
                    sprintStartNumber = 10
                }
            }
        }

        assertThat(project.version, equalTo(semantic(12, 0, 0, true)))
    }

    @Test
    void testConfiguringBuildInfo() {
        Project project = ProjectBuilder.builder().withName("test").build()
        project.pluginManager.apply ProjectVersionPlugin

        project.projectVersion {
            useSemanticVersioning() {
                majorVersion = 3
            }

            buildInfo 'custom', 'myValue'
            buildInfo 'another', 'value'
        }
        project.group = 'de.otto.find'

        MapProperty<String, String> info = project.projectVersion.buildInfo as MapProperty<String, String>

        assertThat(info.get(), equalTo([
                gradleVersion : '5.6.2',
                name : 'test',
                group : 'de.otto.find',
                version : '3.0.0',
                commit : '',
                branch : '',
                custom : 'myValue',
                another : 'value'
        ]))
    }
}
