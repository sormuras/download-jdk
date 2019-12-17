#!/bin/sh -l

FEATURE=$1

echo "Download JDK ${FEATURE}..."

DOCKERFILE=${HOME}/jdk-$1.dockerfile
wget https://github.com/docker-library/openjdk/raw/master/${FEATURE}/jdk/Dockerfile --output-document ${DOCKERFILE}

JAVA_URL=$(cat ${DOCKERFILE} | sed -n 's/ENV JAVA_URL //p')
JAVA_VERSION=$(cat ${DOCKERFILE} | sed -n 's/ENV JAVA_VERSION //p')

wget --directory-prefix ${HOME} ${JAVA_URL}

echo ::set-env name=JAVA_VERSION::${JAVA_VERSION}
echo ::set-env name=JDK_FILE::"/home/runner/work/_temp/_github_home/$(basename ${JAVA_URL})"
        
echo ::set-output name=jdk-file::"/home/runner/work/_temp/_github_home/$(basename ${JAVA_URL})"
