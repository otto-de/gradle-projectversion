# gradle-projectversion

Automatic versioning of your gradle-built artifacts.

## Usage

### Setting a fixed version
```groovy
apply plugin: ProjectVersionPlugin

projectVersion {
    version = '1.2.0'
}
```

```bash
$ ./gradlew :properties -q 2>&1 | grep 'version:'
version: 1.2.0
```
