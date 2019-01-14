package de.otto.find.gradle.projectversion

import org.gradle.api.Project

class GitCommit {
    private static final VERSION_PREFIX = "v"

    private final File vcsRoot

    private GitCommit(File vcsRoot) {
        this.vcsRoot = vcsRoot
    }

    static GitCommit gitCommit(File vcsRoot) {
        new GitCommit(Objects.requireNonNull(vcsRoot))
    }

    static GitCommit gitCommit(Project project) {
        gitCommit(project.projectDir)
    }

    List<String> describe() {
        // git describe yields something like v0.1.0-1-g768be9d
        def description = "git describe --tags --match ${VERSION_PREFIX}* --dirty".execute([], vcsRoot).text.trim()
        // initialize to v0.0.0-whoKnows if no tag yet
        return description.length() == 0 ?
                ["0.0.0", "notset"] :
                trimPrefix(description, VERSION_PREFIX).tokenize("-")
    }

    String tag(Object version) {
        def descriptionParts = describe()
        if (isUntaggedOrDirty(descriptionParts)) {
            if (isDirty(descriptionParts)) {
                throw new IllegalStateException('Workspace is not clean. Please commit your changes first.')
            }
            return "git tag ${VERSION_PREFIX}${version}".execute([], vcsRoot).text.trim()
        }
        return "tag already exists"

    }

    String getId() {
        return "git rev-parse HEAD".execute([], vcsRoot).text.trim()
    }

    String getBranch() {
        return "git rev-parse --abbrev-ref HEAD".execute([], vcsRoot).text.trim()
    }

    boolean isBranch(String branch) {
        this.branch == branch
    }

    private static String trimPrefix(String str, String prefix) {
        str.substring(prefix.length())
    }

    static boolean isUntaggedOrDirty(List<String> descriptionParts) {
        return descriptionParts.size() > 1
    }

    static boolean isDirty(List<String> descriptionParts) {
        return descriptionParts[descriptionParts.size() - 1] == 'dirty'
    }
}
