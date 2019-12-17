# Download "latest-and-greatest" JDK action

Download JDK from https://jdk.java.net, including earyl-access versions.

## Inputs

### `feature`

**Required** The feature number of the JDK to be downloaded.
Defaults to `14` for the time being -- will default to the highest feature number available, soon.

## Outputs

### `file`

The local path to the downloaded JDK archive.
You may also access the path via `${{ env.JDK_VERSION }}`.

### `version`

The version of the downloaded JDK archive.
You may also access the version via `${{ env.JDK_FILE }}`.

## Examples

### Example usage with outputs

```yaml
- uses: sormuras/download-jdk@master
  id: jdk
    with:
      feature: ${{ matrix.feature }}
- uses: actions/setup-java@v1
    with:
      java-version: ${{ steps.jdk.outputs.version }}
      jdkFile: "${{ steps.jdk.outputs.file }}"
- run: java --version
```

### Example usage with environment variables

```yaml
- uses: sormuras/download-jdk@master
  with:
    feature: '14'
- uses: actions/setup-java@v1
  with:
    java-version: ${{ env.JDK_VERSION }}
    jdkFile: ${{ env.JDK_FILE }}
- run: java --version
```
