#!/bin/sh -l

FEATURE=$1

echo "Download JDK ${FEATURE}..."

DOCKERFILE=${HOME}/jdk-$1.dockerfile
wget https://github.com/docker-library/openjdk/raw/master/${FEATURE}/jdk/Dockerfile --output-document ${DOCKERFILE}

JAVA_URL=$(cat ${DOCKERFILE} | sed -n 's/ENV JAVA_URL //p')
JDK_FILE="/home/runner/work/_temp/_github_home/$(basename ${JAVA_URL})"
JDK_VERSION=$(cat ${DOCKERFILE} | sed -n 's/ENV JAVA_VERSION //p')

wget --directory-prefix ${HOME} ${JAVA_URL}

echo ::set-env name=JDK_FILE::${JDK_FILE}
echo ::set-env name=JDK_VERSION::${JDK_VERSION}
        
echo ::set-output name=file::${JDK_FILE}
echo ::set-output name=version::${JDK_VERSION}
