# Download JDK action

Download JDK from https://jdk.java.net

## Inputs

### `feature`

**Required** The feature number of the JDK to be downloaded.
Defaults to `"14"` for the time being -- will default to the highest feature number available.

## Outputs

### `jdk-file`

The downloaded JDK archive.

## Example usage

```yaml
- uses: sormuras/download-jdk@master
  with:
    feature: '14'
- uses: actions/setup-java@v1
  with:
    java-version: "${{ env.JAVA_VERSION }}"
    jdkFile: "${{ env.JDK_FILE }}"
- run: java --version
```
