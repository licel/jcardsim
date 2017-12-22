jCardSim is an open source project and it would be pleasure for us to see you as commiters and contributors!

### Source code
The official jCardSim source repository is located at [https://github.com/licel/jcardsim](https://github.com/licel/jcardsim).


### Building
1. jCardSim does not contain any Oracle's Java Card API source code, because of that it is needed to download JCDK from Oracle’s site and unpack it.
2. Set `jcdkLocation` property in `~/.m2/settings.xml` (profiles - profile). The settings.xml file is located in ${user.home}/.m2 (https://maven.apache.org/settings.html) and contains all the local Maven settings. For example:

~~~xml
<?xml version="1.0" encoding="UTF-8"?>
<settings>
  <profiles>
    <profile>
        <id>jc304</id>
        <activation>
          <activeByDefault>true</activeByDefault>
        </activation>
        <properties>
          <jcdkLocation>/Users/developer/jcdk304</jcdkLocation>
        </properties>
    </profile>
  </profiles>
</settings>
~~~

3. We use [Maven](http://http://maven.apache.org/) for building. After downloading the source code, you have to enter the directory with jCardSim and execute the following commands:

~~~
    mvn initialize
    mvn clean install
~~~

### Development standards
We are using the following principles in jCardSim's development process:

- [Test-Driven Development](http://en.wikipedia.org/wiki/Test-driven_development)
- [Code Conventions for the Java Programming Language](http://www.oracle.com/technetwork/java/codeconvtoc-136057.html)

