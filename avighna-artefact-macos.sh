#!/bin/bash

export ROOT_DIR=$(pwd)

# Install the required packages
brew update
brew install python@3
python3 --version
pip3 --version
pip3 install --upgrade pip
pip3 install setuptools
brew install maven
brew install graphviz
brew install node
node --version
npm --version
npm install -g yarn
yarn --version
npm install -g node-gyp

# Install zip and unzip packages
brew install zip
brew install unzip

# Install sdkman and then install Java 11 and Java 8
curl -s "https://get.sdkman.io" | bash

bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && yes | sdk install java 11.0.17-librca"
bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && yes | sdk install java 8.0.352-librca"

mkdir "$ROOT_DIR"/JarFiles/Reflection-Projects/

cd "$ROOT_DIR"/JarFiles/Spring-Projects/zipkin/ || exit
git clone https://github.com/ranjithkris/pyramid_zipkin-example.git
cd "$ROOT_DIR"/JarFiles/Spring-Projects/zipkin/pyramid_zipkin-example/ || exit
pip install pyramid_zipkin -U
python3 setup.py install

# Install CGBench project
cd "$ROOT_DIR"/JarFiles/Spring-Projects || exit
git clone https://github.com/ranjithkris/CGBench.git
cd "$ROOT_DIR"/JarFiles/Spring-Projects/CGBench/ || exit
git checkout -b for-avighna origin/for-avighna
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

# Install CGBenchRunner
cd "$ROOT_DIR"/CGBenchRunner/ || exit
bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk use java 8.0.352-librca && mvn clean install -DskipTests"

# Install JavaReflectionTestCaseRunner
cd "$ROOT_DIR"/JavaReflectionTestRunner/ || exit
bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk use java 8.0.352-librca && mvn clean install -DskipTests"

bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk default java 8.0.352-librca"
bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk use java 8.0.352-librca"

cd "$ROOT_DIR"/ || exit