# Download "latest-and-greatest" JDK action

Download JDK from https://jdk.java.net, including early-access versions.

## Inputs

### `feature`

**Required** The feature number of the JDK to be downloaded.
Defaults to `15` for the time being -- will default to the highest GA feature number available, soon.

## Outputs

### `file`

The local path to the downloaded JDK archive.

You may also access the path via `${{ env.JDK_FILE }}`.

### `version`

The version of the downloaded JDK archive.

You may also access the version via `${{ env.JDK_VERSION }}`.

## Examples

### Example usage with passing Action outputs to `setup-java`

```yaml
- uses: sormuras/download-jdk@v1
  id: jdk
    with:
      feature: 15
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
