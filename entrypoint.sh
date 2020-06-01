#!/bin/sh -l

FEATURE=$1

echo "Download JDK ${FEATURE}..."

wget https://github.com/sormuras/bach/raw/master/install-jdk.sh
JAVA_URL=$(./install-jdk.sh --verbose --feature ${FEATURE} --dry-run --emit-url | tail --lines 1)

JDK_FILE="/home/runner/work/_temp/_github_home/$(basename ${JAVA_URL})"
JDK_VERSION=${FEATURE}

wget --directory-prefix ${HOME} ${JAVA_URL}

echo ::set-env name=JDK_FILE::${JDK_FILE}
echo ::set-env name=JDK_VERSION::${JDK_VERSION}

echo ::set-output name=file::${JDK_FILE}
echo ::set-output name=version::${JDK_VERSION}
