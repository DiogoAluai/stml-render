## Build
STML Renderer depends on [daluai-jcurses](https://github.com/DiogoAluai/daluai-jcurses). 
It requires access to those libraries (both Java and C), which can be achieved by following `daluai-jcurses` installation instructions.
After the installation, build should succeed:
```bash
mvn package
```

## Run (after build)
Simple example:
```bash
mvn exec:java -Dfps=10 -Dexec.args="example/simple/index.stml"
```
And an example with data loading from a script:
```bash
mvn exec:java -Dfps=10 -Dexec.args="example/data/index.stml"
```
<div align="center">
 <img width="470" height="478" alt="image" src="https://github.com/user-attachments/assets/b74a59df-72b4-49c2-a908-02bb9f530f5a" />
</div>


## Remote Debugging (after build)
Use the following command, and then use something like IDEA JVM remote debug on the same port.
```bash
MAVEN_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005" \
 mvn exec:java -Dfps=10 -Dexec.args="example/index.stml"
```
