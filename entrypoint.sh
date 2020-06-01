#!/bin/sh -l

FEATURE=$1
OS='linux-x64'

echo "Download JDK ${FEATURE}..."

PROPERTIES='https://github.com/sormuras/bach/raw/master/install-jdk.properties'
JAVA_URL=$(wget --quiet --output-document - ${PROPERTIES} | grep -i "${FEATURE}-${OS}=" | awk -F "=" '{print $2}')

echo "JAVA_URL=${JAVA_URL}"

JDK_FILE="/home/runner/work/_temp/_github_home/$(basename ${JAVA_URL})"
JDK_VERSION=${FEATURE}

wget --directory-prefix ${HOME} ${JAVA_URL}

echo ::set-env name=JDK_FILE::${JDK_FILE}
echo ::set-env name=JDK_VERSION::${JDK_VERSION}
echo ::set-env name=JAVA_VERSION::${JDK_VERSION}

echo ::set-output name=file::${JDK_FILE}
echo ::set-output name=version::${JDK_VERSION}
