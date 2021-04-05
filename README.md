# Download "latest-and-greatest" JDK action

Download a JDK from https://jdk.java.net, including Early-Access builds.

Use the downloaded JDK file and its version as inputs for [actions/setup-java@v2](https://github.com/actions/setup-java).
`setup-java@v1` is supported, too.

## Inputs

### `feature`

**Required** The feature number or the project name of the JDK to be downloaded.
Defaults to `16` for the time being -- will default to the highest GA feature number available, soon.

Possible values include:
- `17`
- **`16`**
- `15`
- `Loom`
- `Panama`
- `Valhalla`

[Archived versions](https://jdk.java.net/archive) are supported, too, in order to help developers debug issues in older systems.
They are not updated with the latest security patches and are not recommended for use in production.

## Outputs

### `file`

The local path to the downloaded JDK archive.

You may also access the path via `${{ env.JDK_FILE }}`.

### `version`

The version of the downloaded JDK archive.

You may also access the version via `${{ env.JDK_VERSION }}`.

## Examples for `actions/setup-java@v2`

Most examples below make use of environment variables to share file locations and JDK version information.
If you prefer using explicit identifiers, replace them with the correct `id` tokens.
The first example shows both ways.

### JDK "latest-and-greatest" GA

No `with:`-argument is passed to `sormuras/download-jdk`.
The "latest-and-greatest" GA version of the JDK is selected by default.
This example use of environment variables to share file locations and JDK version information.

```yaml
- uses: sormuras/download-jdk@v1
- uses: actions/setup-java@v2
  with:
    java-version: ${{ env.JDK_VERSION }}
    distribution: jdkfile
    jdkFile: ${{ env.JDK_FILE }}
- run: java -version
```

The same can be achieved using explicit identifiers and output names.

```yaml
- uses: sormuras/download-jdk@v1
  id: download-jdk
- uses: actions/setup-java@v2
  with:
    java-version: ${{ steps.download-jdk.outputs.version }}
    distribution: jdkfile
    jdkFile: ${{ steps.download-jdk.outputs.file }}
- run: java -version
```

### JDK with explicit feature release number

Pass in a valid release number from 17 (or the current feature release number) down to 9 to `feature: RELEASE`.

```yaml
- uses: sormuras/download-jdk@v1
  with:
    feature: 17
- uses: actions/setup-java@v2
  with:
    java-version: ${{ env.JDK_VERSION }}
    distribution: jdkfile
    jdkFile: ${{ env.JDK_FILE }}
- run: java -version
```

Most of the releases, those that are older than 6 months, are [archived](https://jdk.java.net/archive).

> **WARNING** These older versions of the JDK are provided to help developers debug issues in older systems.
> They are not updated with the latest security patches and are not recommended for use in production.

### JDK with Project Loom

Pass in a valid release number from 17 (or the current feature release number) down to 9 to `feature: RELEASE`.


```yaml
- uses: sormuras/download-jdk@v1
  with:
    feature: Loom
- uses: actions/setup-java@v2
  with:
    java-version: ${{ env.JDK_VERSION }}
    distribution: jdkfile
    jdkFile: ${{ env.JDK_FILE }}
- run: java -version
```

## Support for `actions/setup-java@v1`

Most examples shown above also work with `v1` of `actions/setup-java`:
omit the `distribution: jdkfile` line.
