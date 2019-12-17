#!/bin/sh -l

echo "Hello $1"

DOCKERFILE=${HOME}/jdk-14.dockerfile
wget https://github.com/docker-library/openjdk/raw/master/14/jdk/Dockerfile --output-document ${DOCKERFILE}

JAVA_URL=$(cat ${DOCKERFILE} | sed -n 's/ENV JAVA_URL //p')
JAVA_VERSION=$(cat ${DOCKERFILE} | sed -n 's/ENV JAVA_VERSION //p')

wget --directory-prefix ${HOME} ${JAVA_URL}

echo ::set-env name=JAVA_VERSION::${JAVA_VERSION}
echo ::set-env name=JDK_FILE::"${HOME}/$(basename ${JAVA_URL})"
        
echo ::set-output name=java_version::${JAVA_VERSION}
