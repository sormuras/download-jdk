# Download "latest-and-greatest" JDK action

Download a JDK from https://jdk.java.net, including Early-Access builds.

Use the downloaded JDK file and its version as inputs for [actions/setup-java@v1](https://github.com/actions/setup-java).

## Inputs

### `feature`

**Required** The feature number or the project name of the JDK to be downloaded.
Defaults to `15` for the time being -- will default to the highest GA feature number available, soon.

Possible values include:
- `17`
- `16`
- **`15`**
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

## Examples

### Example usage with passing outputs to `actions/setup-java`

```yaml
- uses: sormuras/download-jdk@v1
  id: jdk
  with:
    feature: Loom
- uses: actions/setup-java@v1
  with:
    java-version: ${{ steps.jdk.outputs.version }}
    jdkFile: ${{ steps.jdk.outputs.file }}
- run: java --version
```

### Minimal example with default feature and using environment variables

```yaml
- uses: sormuras/download-jdk@v1
- uses: actions/setup-java@v1
  with:
    java-version: ${{ env.JDK_VERSION }}
    jdkFile: ${{ env.JDK_FILE }}
- run: java --version
```
