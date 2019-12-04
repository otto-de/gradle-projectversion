# gradle-projectversion

Automatic versioning of your gradle-built artifacts.

## Usage
_The minimum required Gradle version is `5.1` as the plugin uses lazy properties._

The plugin must be activated with the following steps:
1. Ensure that the _current_ version `x.y.z` of your project is reflected as a _tag_ with the pattern `vx.y.z` in your
   Git repository.  
2. Remove any preconfigured `project.version` assignment in your `build.gradle`.
3. Apply the plugin with your method of choice.
4. Configure the versioning strategy employed by the plugin.  

The second step is necessary for the plugin to do anything useful. Refer to the next section for example 
configurations.

During execution, the _version_ property is automatically derived from the latest Git tag using the configured
strategy. This is done _on first access_ of the property. Therefore, the configuration should be near the top of your
`build.gradle`. The plugin provides the `buildInfo` task which may be used to print the computed version.

Depending on the state of your Git repository, the following will happen:
* Current commit is tagged and workspace is clean → Current Version from Git tag is used.
* Current commit is not tagged and workspace is clean → Next version based on latest tagged commit is used.
* Workspace is dirty → Next version suffixed with `-SNAPSHOT` is used.
  
The plugin provides the `tagVersion` task to store the computed version as Git tag.

## Examples
### Print build info
```groovy
plugins {
    id 'de.otto.find.project-version' version '0.8.0'
}
```

```shell script
$ ./gradlew buildInfo

> Task :buildInfo
gradleVersion=5.6.2
name=gradle-projectversion
group=de.otto.find
version=0.8.0
commit=aaccf75161bf4dc1cc5337622cba24e43525d9ac
```

### Auto-versioning in Git repositories
```groovy
plugins {
    id 'de.otto.find.project-version' version '0.8.0'
}

projectVersion {
    useSemanticVersioning() {
        majorVersion = 1
    }
}
```

### Sprint-based major versions
```groovy
plugins {
    id 'de.otto.find.project-version' version '0.8.0'
}

projectVersion {
    useSemanticVersioning() {
        useSprintNumber() {
            sprintStart = Instant.parse("2011-09-28T23:00:00Z")
            sprintLength = Duration.ofDays(14)
            sprintStartNumber = 1
        }
    }
}
```

### Extending build info
```groovy
plugins {
    id 'de.otto.find.project-version' version '0.8.0'
}

projectVersion {
    buildInfo([
            team    : 'teamname',
            vertical: 'projectvertical'
    ])
}
```

### Using build info to filter resources
```groovy
import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    // Apply the java plugin to add support for Java
    id 'java'

    // Auto-versioning
    id 'de.otto.find.project-version' version '0.8.0'
}

tasks.withType(ProcessResources) {
    doFirst {
        logger.quiet "Build Info: [$name]"
        logger.quiet "--------------------------------------------------------------"
        inputs.properties.buildInfo.each { k, v -> logger.quiet "${k} : ${v}" }
        logger.quiet "--------------------------------------------------------------"
    }

    inputs.property 'buildInfo', projectVersion.buildInfo

    filter ReplaceTokens, tokens: inputs.properties.buildInfo
}
```

### Setting a fixed version
```groovy
plugins {
    id 'de.otto.find.project-version' version '0.8.0'
}

projectVersion {
    version = '1.2.0'
}
```
