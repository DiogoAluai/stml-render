# How to run test
```bash
mvnexec:java -Dfps=10 -Dexec.args="example.xml"
```

# Remote Debugging
Use the following command, and then use something like IDEA JVM remote debug on the same port.
```bash
MAVEN_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005" \
 mvn exec:java -Dfps=10 -Dexec.args="example.xml"
```