# Avighna Artefact

## Initial setup

- Clone the avighna artefact git repository

```shell
git clone https://github.com/ranjithkris/Avighna-Artefact-Script.git
```

- Go to the cloned repository

````shell
cd Avighna-Artefact-Script
````

- Run the script to setup to projects
  - For Mac os
    ````shell
    ./avighna-artefact-macos.sh
    ````
  - For Linux
    ````shell
    ./avighna-artefact-linux.sh
    ````
  - For Windows
    ````shell
    # Use WSL Ubuntu and run the below script
    ./avighna-artefact-linux.sh
    ````
    **Note**: To download the files from WSL to window host machine open file explorer and open \\wsl$. Use the below link to know how to do it https://www.howtogeek.com/426749/how-to-access-your-linux-wsl-files-in-windows-10/
    
**Note**: 
- If the script fails to install some package, then install the below packages manually
  - Java 8
  - Python 3
  - Pip 3
  - Node
  - Npm
  - Yarn 
- Sometimes I faced the issue of permission denied. In that case, make sure to be logged in as root

# Run Avighna with the evaluation projects
**Note:**
- To know the options available for Avighna use the below command.
- Always run the project from the root git repository directory, i.e, Avighna-Artefact-Script

```shell script
java -jar JarFiles/avighna-cmd-interface-1.0.0.jar

#Below is the output of the above command

    ___        _       __
   /   |_   __(_)___ _/ /_  ____  ____ _
  / /| | | / / / __ `/ __ \/ __ \/ __ `/
 / ___ | |/ / / /_/ / / / / / / / /_/ /
/_/  |_|___/_/\__, /_/ /_/_/ /_/\__,_/
             /____/     Cmd-Interface



usage: avighna-cmd-interface
 -aa,--app-arg <arg>              Program arguments for the given
                                  application. If multiple arguments then
                                  separate it by :
 -aaj,--avighna-agent-jar <arg>   Path of the avighna agent jar file
 -aj,--app-jar <arg>              Path of the application jar file to
                                  generate dynamic information
 -dar,--dacapo-app-arg <arg>      Arguments to the DACAPO application. The
                                  value format is <dacapo
                                  application>:<data size>. These provided
                                  arguments will be appended after the
                                  given application jar file to the final
                                  command
 -dtf,--dont-track-fake-edge      Dont track fake edges while generating
                                  dynamic traces
 -lrf,--list-request-file <arg>   Path of the YAML file that contains the
                                  list of requests. Request must be full
                                  curl command
 -od,--out-root-dir <arg>         Root directory to store the output
 -rap,--root-app-packages <arg>   Root application package(s) for
                                  generating the dynamic cg. Multiple
                                  packages are seperated by :
 -sdf,--save-dot-files            Save the generated dynamic trace as dot
                                  files
 -sif,--save-img-files            Save the generated dynamic trace as
                                  image files
 -sra,--single-run-app            This argument indicates that the given
                                  app is a single run and not the web
                                  application which runs infinitely until
                                  explicitly terminated.
```

- For the below commands to run Avighna on different evaluation projects, we have used the options -sif to save the DOT graph as image file to visualize. This option takes more time depending on the size of the DOT graph. If you do not wish to visualize the DOT graph and want to run the command quickly, then remove -sif option.

# Running Avighna on Spring applications:
## fredbet application
- **Link:** https://github.com/fred4jupiter/fredbet
- **Tag:** tag/release_2.0.0
- **Root Application Package:** de.fred4jupiter.fredbet
- **Present Working Directory:** Avighna-Artefact-Script (The below commands uses relative path, make sure you are in the correct present working directory)
- Run Avighna on fredbet.

```shell script
java -jar JarFiles/avighna-cmd-interface-1.0.0.jar -aaj JarFiles/avighna-agent-1.0.0.jar -aj JarFiles/Spring-Projects/fredbet/target/fredbet.jar -od avighna-output/Spring-Projects/fredbet/avighna-agent-output/ -rap de.fred4jupiter.fredbet -sdf -sif
```

- Once the application is up and running, Open the browser in the host machine and open the link http://localhost:8080/
- Once you are done with using the fredbet application, go to terminal and press 'y' and enter. Avighna will create the DST file. DST file contains dynamic traces, which will be used to merge with the static callgraph.

## start.spring.io application
- **Link:** https://github.com/spring-io/start.spring.io
- **Branch:** main
- **Commit Hash:** 6c0944ead1bfef3444a9e1a422b3f134de2b7f48
- **Module:** start-site. There are many modules in start.spring.io project. For this evaluation, we only run on start-site module.
- **Root Application Package:** io.spring.start.site
- **Present Working Directory:** Avighna-Artefact-Script (The below commands uses relative path, make sure you are in the correct present working directory)
- Run Avighna on start.spring.io (start-site).

```shell script
java -jar JarFiles/avighna-cmd-interface-1.0.0.jar -aaj JarFiles/avighna-agent-1.0.0.jar -aj JarFiles/Spring-Projects/start.spring.io/start-site/target/start-site-exec.jar -od avighna-output/Spring-Projects/start.spring.io/start-site/avighna-agent-output/ -rap io.spring.start.site -sdf -sif
```

- Once the application is up and running, Open the browser in the host machine and open the link http://localhost:8080/
- Once you are done with using the start.spring.io application, go to terminal and press 'y' and enter. Avighna will create the DST file. DST file contains dynamic traces, which will be used to merge with the static callgraph.

## Zipkin application
- **Link:** https://github.com/openzipkin/zipkin
- **Branch:** master
- **Commit Hash:** 8a4f4b9c9a5a3204d9663ecef39d687785369c9a
- **Module:** zipkin-server. There are many modules in zipkin project. For this evaluation, we only run on zipkin-server module.
- **Root Application Package:** zipkin.server and zipkin2.server
- **Present Working Directory:** Avighna-Artefact-Script (The below commands uses relative path, make sure you are in the correct present working directory)
- In order to test zipkin, we need a server and for that we used example project from zipkin---https://github.com/openzipkin/pyramid_zipkin-example.
  First, we need to run both the frontend and backend server from this project.
- The below command combines three commands. First to run the backend server, second to run the frontend server and third to run the Zipkin with Avighna agent.

**Note**: When you run the below command, it displays the pid of the first two server. Keep note of these two
pid's. Once you finish running Zipkin, please make sure to kill those two process since those process 
uses the port 8081 and 9000.

```shell script
python3 JarFiles/Spring-Projects/zipkin/pyramid_zipkin-example/frontend.py & python3 JarFiles/Spring-Projects/zipkin/pyramid_zipkin-example/backend.py & java -jar JarFiles/avighna-cmd-interface-1.0.0.jar -aaj JarFiles/avighna-agent-1.0.0.jar -aj JarFiles/Spring-Projects/zipkin/zipkin-server/target/zipkin-server-2.23.17-SNAPSHOT-exec.jar -od avighna-output/Spring-Projects/zipkin/zipkin-server/avighna-agent-output/ -rap zipkin.server:zipkin2.server -sdf -sif

# The above command runs in background. When you run this command, you see something like below 
# [1] 7527
# [2] 7528
# Once you complete kill those two process, then run the below command
```

- Once the application is up and running, Open the browser in the host machine and open the below links.
- For frontend: http://localhost:8081/
- For backend: http://localhost:9000/api
- For Zipkin main application: http://localhost:9411/
- Once you are done with using the zipkin application, go to terminal and press 'y' and enter. Avighna will create the DST file. DST file contains dynamic traces, which will be used to merge with the static callgraph.

## spring-petclinic application
- **Link:** https://github.com/spring-projects/spring-petclinic
- **Branch:** main
- **Commit Hash:** 0c1fa8e8e2744125cc4bee725fe2de6dd76d3a4f
- **Root Application Package:** org.springframework.samples.petclinic
- **Present Working Directory:** /root/avighna/ (The below commands uses relative path, make sure you are in the correct present working directory)
- Run Avighna on spring-petclinic.

```shell script
java -jar JarFiles/avighna-cmd-interface-1.0.0.jar -aaj JarFiles/avighna-agent-1.0.0.jar -aj JarFiles/Spring-Projects/spring-petclinic/target/spring-petclinic-2.7.0-SNAPSHOT.jar -od avighna-output/Spring-Projects/spring-petclinic/avighna-agent-output/ -rap org.springframework.samples.petclinic -sdf -sif
```

- Once the application is up and running, Open the browser in the host machine and open the link http://localhost:9001/
- Once you are done with using the spring-petclinic application, go to terminal and press 'y' and enter. Avighna will create the DST file. DST file contains dynamic traces, which will be used to merge with the static callgraph.

# Running Avighna on Guice applications:
##  guice application from CGBench
- **Link:** https://github.com/linghuiluo/CGBench/tree/master/guice
- **Branch:** master
- **Commit Hash:** d3e2587c3a8a2d5c5895851c93eb6699c8b052bc
- **Root Application Package:** com.baeldung.examples
- **Present Working Directory:** Avighna-Artefact-Script (The below commands uses relative path, make sure you are in the correct present working directory)
- Run Avighna on guice project.

- First run the application using avighna-ccmd-interface to generate the avighna-agent configuration file

````shell
java -jar JarFiles/avighna-cmd-interface-1.0.0.jar -aaj JarFiles/avighna-agent-1.0.0.jar -aj JarFiles/Guice-Projects/guice/target/guice-1.0-SNAPSHOT-jar-with-dependencies.jar -od avighna-output/Guice-Projects/guice/avighna-agent-output/ -rap java.com.baeldung.examples -sdf -sif
````

- Then, run the application directly using the avighna-agent
  **Note:** The above command does not use avighna-cmd-interface, bacause this project needs interactive terminal (We will add this in avighna-cmd-interface in future). Therefore, we run avighna directly on this project with the help of avighna-agent.

```shell script
java -Xbootclasspath/p:JarFiles/avighna-agent-1.0.0.jar -javaagent:JarFiles/avighna-agent-1.0.0.jar=avighna-output/Guice-Projects/guice/avighna-agent-output/dynamic_agent.yml -noverify -jar JarFiles/Guice-Projects/guice/target/guice-1.0-SNAPSHOT-jar-with-dependencies.jar
```

- Once the application is up and running, in the terminal start typing some message and click enter. You can send any number of messages.
- Once you are done using the guice application, type 'q' to exit.

## CGBench and JavaTestReflection

Just run the below two jar file

````shell
java -jar CGBenchRunner/target/CGBenchRunner-1.0-SNAPSHOT-jar-with-dependencies.jar
java -jar JavaReflectionTestRunner/target/JavaReflectionTestRunner-1.0-SNAPSHOT-jar-with-dependencies.jar
````

**Note:**
- CGBenchRunner takes too long to complete
- Currently, for JavaReflectionTestRunner misses three or four modules from JavaReflectionTestCases. I will complete that in future