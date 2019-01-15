package de.otto.find.gradle.projectversion

import org.gradle.api.Project

class GitCommit {
    private static final VERSION_PREFIX = "v"

    private final File vcsRoot

    List<String> description

    private GitCommit(File vcsRoot) {
        this.vcsRoot = vcsRoot
    }

    static GitCommit gitCommit(File vcsRoot) {
        new GitCommit(Objects.requireNonNull(vcsRoot))
    }

    static GitCommit gitCommit(Project project) {
        gitCommit(project.projectDir)
    }

    String tag(Object version) {
        if (isDirty()) {
            throw new IllegalStateException('Workspace is not clean. Please commit your changes first.')
        }
        isUntaggedOrDirty() ?
                "git tag $VERSION_PREFIX$version".execute([], vcsRoot).text.trim() :
                "tag $VERSION_PREFIX$version already exists"
    }

    String getId() {
        "git rev-parse HEAD".execute([], vcsRoot).text.trim()
    }

    String getBranch() {
        "git rev-parse --abbrev-ref HEAD".execute([], vcsRoot).text.trim()
    }

    List<String> getDescription() {
        description == null ?
                this.description = describe() :
                description
    }

    def <T extends ProjectVersion> T parseAsVersion(GitTagParser<T> gitTagParser) {
        gitTagParser.parseAsVersion getDescription()[0]
    }

    boolean isDirty() {
        List<String> descriptionParts = getDescription()
        return descriptionParts[descriptionParts.size() - 1] == 'dirty'
    }

    boolean isUntaggedOrDirty() {
        return getDescription().size() > 1
    }

    private List<String> describe() {
        // git describe yields something like v0.1.0-1-g768be9d
        def description = "git describe --tags --match ${VERSION_PREFIX}* --dirty".execute([], vcsRoot).text.trim()
        // initialize to v0.0.0-whoKnows if no tag yet
        return description.length() == 0 ?
                ["0.0.0", "notset"] :
                trimPrefix(description, VERSION_PREFIX).tokenize("-")
    }

    private static String trimPrefix(String str, String prefix) {
        str.substring(prefix.length())
    }

}
