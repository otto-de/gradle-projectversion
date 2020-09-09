package de.otto.find.gradle.projectversion

import org.gradle.api.Project

class GitCommit {
    private static final VERSION_PREFIX = "v"

    private final File vcsRoot

    String branch
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
        branch == null ?
                this.branch = revParseBranch() :
                branch
    }

    List<String> getDescription() {
        description == null ?
                this.description = describe(VERSION_PREFIX) :
                description
    }

    def <T extends ProjectVersion> T parseAsVersion(GitTagParser<T> gitTagParser) {
        gitTagParser.parseAsVersion getDescription()[0]
    }

    int getDistance() {
        getDescription().size() > 2
                ? Integer.valueOf(getDescription()[1]) + (isDirty() ? 1 : 0)
                : (isDirty() ? 1 : 0)
    }

    boolean isDirty() {
        List<String> descriptionParts = getDescription()
        return descriptionParts[descriptionParts.size() - 1] == 'dirty'
    }

    boolean isUntaggedOrDirty() {
        return getDescription().size() > 1
    }

    private String revParseBranch() {
        "git rev-parse --abbrev-ref HEAD".execute([], vcsRoot).text.trim()
    }

    private List<String> describe(String versionPrefix, boolean firstParent = false) {
        // git describe yields something like v0.1.0-1-g768be9d
        def firstParentFlag = firstParent ?
                " --first-parent" :
                ""
        def description = "git describe --tags$firstParentFlag --match $versionPrefix* --dirty".execute([], vcsRoot).text.trim()
        // initialize to v0.0.0-1 if no tag yet
        return description.length() == 0 ?
                ["0.0.0", "1", "unknown"] :
                trimPrefix(description, versionPrefix).tokenize("-")
    }

    private static String trimPrefix(String str, String prefix) {
        str.substring(prefix.length())
    }

    @Override
    String toString() {
        return "GitCommit{vcsRoot=$vcsRoot}";
    }
}
