package de.otto.find.gradle.projectversion

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.testng.annotations.Test

import static de.otto.find.gradle.projectversion.FixedVersion.defaultVersion
import static de.otto.find.gradle.projectversion.FixedVersion.fixed
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
        assertThat(pluginExtension.version, equalTo(defaultVersion()))
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
        ProjectVersionPluginExtension versionPluginExtension = project.extensions.projectVersion

        versionPluginExtension.deriveFromGitTag()

        assertThat(project.version, instanceOf(SemanticVersion))
    }
}
