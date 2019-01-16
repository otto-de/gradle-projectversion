package de.otto.find.gradle.projectversion

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.testng.annotations.Test

import static de.otto.find.gradle.projectversion.FixedVersion.defaultVersion
import static de.otto.find.gradle.projectversion.FixedVersion.fixed
import static de.otto.find.gradle.projectversion.SemanticVersion.semantic
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
                minimumMajorVersion = 3
            }
        }

        assertThat(project.version, equalTo(semantic(3, 0, 0, true)))
    }

    @Test
    void testReadConfiguringProperties() {
        Project project = ProjectBuilder.builder().withName("test").build()
        project.pluginManager.apply ProjectVersionPlugin

        project.projectVersion {
            useSemanticVersioning() {
                minimumMajorVersion = 3
            }
        }

        ProjectBuildInfo task = project.tasks.buildInfo as ProjectBuildInfo

        project.group = 'de.otto.find'

        task.displayBuildInfo()
//
//        assertThat(version.get(), equalTo(semantic(3, 0, 0, true)))
    }
}
