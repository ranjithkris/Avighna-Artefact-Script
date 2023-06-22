#!/bin/bash

root_dir=$(pwd)

brew update
brew install zip
brew install unzip
brew install maven
brew install graphviz

npm install -g node-gyp

curl -s "https://get.sdkman.io" | bash

bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && yes | sdk install java 11.0.17-librca"
bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && yes | sdk install java 8.0.352-librca"

chmod 755 avighna-agent-1.0.0.jar
chmod 755 avighna-cmd-interface-1.0.0.jar
mkdir Spring-Projects
mkdir Reflection-Projects
mkdir Guice-Projects

# Start with Spring projects
cd "$root_dir"/Spring-Projects || exit

# Install Fredbet project
git clone https://github.com/ranjithkris/fredbet.git
cd "$root_dir"/Spring-Projects/fredbet/ || exit
git checkout -b tags_release_2.0.0 tags/release_2.0.0
bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk use java 8.0.352-librca && mvn clean install -DskipTests"

# Install Spring Initializer project
cd "$root_dir"/Spring-Projects || exit
git clone https://github.com/spring-io/start.spring.io.git
cd "$root_dir"/Spring-Projects/start.spring.io/ || exit
git checkout 6c0944ead1bfef3444a9e1a422b3f134de2b7f48
bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk use java 11.0.17-librca && mvn clean install -DskipTests"

# Install Spring Zipkin project
cd "$root_dir"/Spring-Projects || exit
git clone https://github.com/ranjithkris/zipkin.git
cd "$root_dir"/Spring-Projects/zipkin/ || exit
git checkout 8a4f4b9c9a5a3204d9663ecef39d687785369c9a
bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk use java 8.0.352-librca && mvn clean install -DskipTests"
git clone https://github.com/ranjithkris/pyramid_zipkin-example.git
cd "$root_dir"/Spring-Projects/zipkin/pyramid_zipkin-example/ || exit
pip install pyramid_zipkin -U
python3 setup.py install

# Install CGBench project
cd "$root_dir"/Spring-Projects || exit
git clone https://github.com/ranjithkris/CGBench.git
cd "$root_dir"/Spring-Projects/CGBench/ || exit
git checkout origin/for-avighna
rm -r guice-credentials/
rm -r music-store/
rm -r onlinechat/
rm -r onlineshop/
rm -r teleforum/
rm -r webgoat/
bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk use java 8.0.352-librca && python3 buildAll.py"
mv "$root_dir"/Spring-Projects/CGBench/guice/ "$root_dir"/Guice-Projects/

# Install Spring Petclinic project
cd "$root_dir"/Spring-Projects || exit
git clone https://github.com/ranjithkris/spring-petclinic.git
cd  "$root_dir"/Spring-Projects/spring-petclinic/ || exit
git checkout 0c1fa8e8e2744125cc4bee725fe2de6dd76d3a4f
bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk use java 11.0.17-librca && mvn clean install -DskipTests"

# Install Streamflow project (Guice Framework)
cd "$root_dir"/Guice-Projects || exit
git clone https://github.com/ranjithkris/streamflow.git
cd "$root_dir"/Guice-Projects/streamflow/ || exit
git checkout -b tags_0.12.0 tags/0.12.0
bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk use java 8.0.352-librca && mvn clean install -DskipTests"

# Install Java Reflection Test cases project by Florian Kübler
cd "$root_dir"/Reflection-Projects || exit
git clone https://github.com/ranjithkris/JavaReflectionTestCases.git
cd "$root_dir"/Reflection-Projects/JavaReflectionTestCases/ || exit
bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk use java 8.0.352-librca && mvn clean install -DskipTests"

# Install Runner projects

# Install avighna which is used by helper runner
cd "$root_dir"/avighna || exit
bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk use java 8.0.352-librca && mvn clean install -DskipTests"

# Install CGBenchRunner
cd "$root_dir"/CGBenchRunner/ || exit
bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk use java 8.0.352-librca && mvn clean install -DskipTests"

# Install JavaReflectionTestCaseRunner
cd "$root_dir"/JavaReflectionTestRunner/ || exit
bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk use java 8.0.352-librca && mvn clean install -DskipTests"

bash -c "source $HOME/.sdkman/bin/sdkman-init.sh && sdk use java 8.0.352-librca"

cd "$root_dir"/ || exit