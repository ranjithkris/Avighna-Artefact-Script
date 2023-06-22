#!/bin/bash

export ROOT_DIR=$(pwd)

npm install -g node-gyp

# Install zip and unzip packages
brew install zip
brew install unzip

# Install sdkman and then install Java 11 and Java 8
curl -s "https://get.sdkman.io" | bash

bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && yes | sdk install java 11.0.17-librca"
bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && yes | sdk install java 8.0.352-librca"

#Install Maven and Graphviz
brew install maven
brew install graphviz

# give permissions to jar files and create necessary directories
cd "$ROOT_DIR"/ || exit
chmod 755 "$ROOT_DIR"/avighna-agent-1.0.0.jar
chmod 755 "$ROOT_DIR"/avighna-cmd-interface-1.0.0.jar
chmod 755 "$ROOT_DIR"/avighna-merger-1.0-SNAPSHOT-jar-with-dependencies.jar
mkdir Spring-Projects
mkdir Reflection-Projects
mkdir Guice-Projects

# Start with Spring projects
cd "$ROOT_DIR"/Spring-Projects || exit

# Install Fredbet project
git clone https://github.com/ranjithkris/fredbet.git
cd "$ROOT_DIR"/Spring-Projects/fredbet/ || exit
git checkout -b tags_release_2.0.0 tags/release_2.0.0
bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk use java 8.0.352-librca && mvn clean install -DskipTests"

# Install Spring Initializer project
cd "$ROOT_DIR"/Spring-Projects || exit
git clone https://github.com/ranjithkris/start.spring.io.git
cd "$ROOT_DIR"/Spring-Projects/start.spring.io/ || exit
git checkout 6c0944ead1bfef3444a9e1a422b3f134de2b7f48
bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk use java 11.0.17-librca && mvn clean install -DskipTests"

# Install Spring Zipkin project
cd "$ROOT_DIR"/Spring-Projects || exit
git clone https://github.com/ranjithkris/zipkin.git
cd "$ROOT_DIR"/Spring-Projects/zipkin/ || exit
git checkout 8a4f4b9c9a5a3204d9663ecef39d687785369c9a
bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk use java 8.0.352-librca && mvn clean install -DskipTests"
git clone https://github.com/ranjithkris/pyramid_zipkin-example.git
cd "$ROOT_DIR"/Spring-Projects/zipkin/pyramid_zipkin-example/ || exit
pip install pyramid_zipkin -U
python3 setup.py install

# Install CGBench project
cd "$ROOT_DIR"/Spring-Projects || exit
git clone https://github.com/ranjithkris/CGBench.git
cd "$ROOT_DIR"/Spring-Projects/CGBench/ || exit
git checkout -b for-avighna origin/for-avighna
# RUN git checkout 65820234b5b9a6f65bd69d78570a3caedee7f1a1
rm -r guice-credentials/
rm -r music-store/
rm -r onlinechat/
rm -r onlineshop/
rm -r teleforum/
rm -r webgoat/
bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk use java 8.0.352-librca && python3 buildAll.py"
mv guice/ "$ROOT_DIR"/Guice-Projects/

# Install Spring Petclinic project
cd "$ROOT_DIR"/Spring-Projects || exit
git clone https://github.com/ranjithkris/spring-petclinic.git
cd "$ROOT_DIR"/Spring-Projects/spring-petclinic/ || exit
git checkout 0c1fa8e8e2744125cc4bee725fe2de6dd76d3a4f
bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk use java 11.0.17-librca && mvn clean install -DskipTests"

# Install Streamflow project (Guice Framework)
cd "$ROOT_DIR"/Guice-Projects || exit
git clone https://github.com/ranjithkris/streamflow.git
cd "$ROOT_DIR"/Guice-Projects/streamflow/ || exit
git checkout -b tags_0.12.0 tags/0.12.0
bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk use java 8.0.352-librca && mvn clean install -DskipTests"

# Install Java Reflection Test cases project by Florian Kübler
cd "$ROOT_DIR"/Reflection-Projects || exit
git clone https://github.com/ranjithkris/JavaReflectionTestCases.git
cd "$ROOT_DIR"/Reflection-Projects/JavaReflectionTestCases/ || exit
bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk use java 8.0.352-librca && mvn clean install -DskipTests"

# Install Runner projects

# Install avighna which is used by helper runner
cd "$ROOT_DIR"/ || exit
mvn install:install-file \
  -Dfile="$ROOT_DIR"/avighna-merger-1.0-SNAPSHOT-jar-with-dependencies.jar \
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

bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk use java 8.0.352-librca"

cd "$ROOT_DIR"/ || exit