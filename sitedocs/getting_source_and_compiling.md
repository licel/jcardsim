## Building & Development

### Source code
The official jCardSim source repository is located at [https://github.com/licel/jcardsim](https://github.com/licel/jcardsim).


### Building

1. Install the [Java Development Kit (JDK) 8](http://www.oracle.com/technetwork/java/javase/downloads/) from Oracle.

2. Install the [Java Card Classic Development Kit](http://www.oracle.com/technetwork/java/embedded/javacard/downloads/) from Oracle, which provides the Java Card API classes. Set the `JC_CLASSIC_HOME` environment variable system-wide.

3. Install [Apache Maven](https://maven.apache.org/download.html), which is used to build jCardSim. Follow the [installation tips](https://maven.apache.org/install.html) to set the `PATH` and `JAVA_HOME` environment variables.

4. From the directory containing the jCardSim source code, execute the following commands:
    ~~~
    mvn initialize
    mvn clean install
    ~~~


### Development
jCardSim is an open source project, and it would be a pleasure for us to see you as committers and contributors!

We are using the following principles in jCardSim's development process:

- [Test-Driven Development](http://en.wikipedia.org/wiki/Test-driven_development)
- [Code Conventions for the Java Programming Language](http://www.oracle.com/technetwork/java/codeconvtoc-136057.html)

