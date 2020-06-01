#!/bin/sh -l

FEATURE=$1
OS='linux-x64'

echo "Download JDK ${FEATURE}..."

JAVA_NET="https://jdk.java.net/${feature}"
DOWNLOAD='https://download.java.net/java'

CANDIDATES=$(wget --quiet --output-document - ${JAVA_NET} | grep -Eo 'href[[:space:]]*=[[:space:]]*"[^\"]+"' | grep -Eo '(http|https)://[^"]+')
JAVA_URL=$(echo "${CANDIDATES}" | grep -Eo "${DOWNLOAD}/.+/jdk${FEATURE}.*/.*GPL/.*jdk-${FEATURE}.+${OS}_bin(.tar.gz|.zip)$" || true)

JDK_FILE="/home/runner/work/_temp/_github_home/$(basename ${JAVA_URL})"
JDK_VERSION=${FEATURE}

wget --directory-prefix ${HOME} ${JAVA_URL}

echo ::set-env name=JDK_FILE::${JDK_FILE}
echo ::set-env name=JDK_VERSION::${JDK_VERSION}

echo ::set-output name=file::${JDK_FILE}
echo ::set-output name=version::${JDK_VERSION}
