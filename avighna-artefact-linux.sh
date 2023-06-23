#!/bin/bash

export ROOT_DIR=$(pwd)

# Make the default shell as bash
echo "dash dash/sh boolean false" | debconf-set-selections
DEBIAN_FRONTEND=noninteractive dpkg-reconfigure dash
ENV ENV ~/.profile

# Install Python 3 and Pip 3 for Zipkin project
apt update
apt install software-properties-common
echo | add-apt-repository ppa:deadsnakes/ppa
apt update
echo 'y' | apt install python3
python3 --version
apt-get install python3-pip -y
pip3 --version
ipip3 install --upgrade pip
pip3 install setuptools

npm install -g node-gyp

# Install zip and unzip packages
apt-get update -y && apt-get install -y zip
apt-get update -y && apt-get install -y unzip

# Install sdkman and then install Java 11 and Java 8
curl -s "https://get.sdkman.io" | bash

bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && yes | sdk install java 11.0.17-librca"
bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && yes | sdk install java 8.0.352-librca"

#Install Maven and Graphviz
apt-get update -y && apt-get install -y maven
apt-get update -y && apt-get install -y graphviz

mkdir "$ROOT_DIR"/JarFiles/Reflection-Projects/

cd "$ROOT_DIR"/JarFiles/Spring-Projects/zipkin/ || exit
git clone https://github.com/ranjithkris/pyramid_zipkin-example.git
cd "$ROOT_DIR"/JarFiles/Spring-Projects/zipkin/pyramid_zipkin-example/ || exit
pip3 install pyramid_zipkin -U
python3 setup.py install

# Install CGBench project
cd "$ROOT_DIR"/JarFiles/Spring-Projects || exit
git clone https://github.com/ranjithkris/CGBench.git
cd "$ROOT_DIR"/JarFiles/Spring-Projects/CGBench/ || exit
git checkout -b for-avighna origin/for-avighna
# RUN git checkout 65820234b5b9a6f65bd69d78570a3caedee7f1a1
rm -r guice-credentials/
rm -r music-store/
rm -r onlinechat/
rm -r onlineshop/
rm -r teleforum/
rm -r webgoat/
rm -r guice/
bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk use java 8.0.352-librca && python3 buildAll.py"

# Install Java Reflection Test cases project by Florian Kübler
cd "$ROOT_DIR"/JarFiles/Reflection-Projects || exit
git clone https://github.com/ranjithkris/JavaReflectionTestCases.git
cd "$ROOT_DIR"/JarFiles/Reflection-Projects/JavaReflectionTestCases/ || exit
bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk use java 8.0.352-librca && mvn clean install -DskipTests"

# Install avighna which is used by helper runner
cd "$ROOT_DIR"/ || exit
mvn install:install-file \
  -Dfile="$ROOT_DIR"/JarFiles/avighna-merger-1.0-SNAPSHOT-jar-with-dependencies.jar \
  -DgroupId=de.fraunhofer.iem \
  -DartifactId=avighna-merger \
  -Dversion=1.0-SNAPSHOT \
  -Dpackaging=jar \
  -DgeneratePom=true

# Install CGBenchRunner and JavaReflectionTestCaseRunner
cd "$ROOT_DIR"/ || exit
bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk use java 8.0.352-librca && mvn clean install -DskipTests"

# Make Java 8 as default
bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk default java 8.0.352-librca"
bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk use java 8.0.352-librca"

cd "$ROOT_DIR"/ || exit
