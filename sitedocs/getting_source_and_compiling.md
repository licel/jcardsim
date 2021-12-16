## Building & Development

### Building

1. Install the [Java Development Kit (JDK) 8](http://www.oracle.com/technetwork/java/javase/downloads/) from Oracle.

2. Install the [Java Card Classic Development Kit](http://www.oracle.com/technetwork/java/embedded/javacard/downloads/) from Oracle, which provides the Java Card API classes. Set the `JC_CLASSIC_HOME` environment variable system-wide.

3. Install [Apache Maven](https://maven.apache.org/download.html), which is used to build jCardSim. Follow the [installation tips](https://maven.apache.org/install.html) to set the `PATH` and `JAVA_HOME` environment variables.

4. From the directory containing the jCardSim source code, execute the following commands:

~~~
mvn clean install
~~~

### Creating Release

Open Maven's `settings.xml` and edit to fit your settings:

~~~xml
<profiles>
    <profile>
        <id>jcardsim</id>
        <properties>
            <!-- the group id to use for the artifact - each deployment must use a unique-->
            <group.id>your registered Sonatype coordinates</group.id>
            <!-- Run gpg --list-signatures --keyid-format 0xshort and select the key id -->
            <gpg.keyname>0x.....</gpg.keyname>
            <github.username>github username</github.username>
            <github.name>your name</github.name>
            <github.mail>email to use</github.mail>
            <!-- optional: in case you use several ssh keys and have a ssh host identifier set -->
            <github.dev.host>github.com</github.dev.host>
        </properties>
    </profile>
</profiles>
~~~

Release the jar on Sonatype:

~~~shell
mvn release:clean release:prepare -Pjcardsim
mvn release:perform -Pjcardsim
~~~

__NOTE:__ It might take a while (multiple hours) until the Sonatype Nexus server has successfully checked the 
upload in the staging environment and closed it. Because the Nexus Maven plugin times out after 300 seconds 
it might report that the connection was reset or the build failed because of a rule check. 
This might be not the case. Wait until the upload is marked as closed and release it manually in the  Nexus UI.

### Development
jCardSim is an open source project, and it would be a pleasure for us to see you as committers and contributors!

We are using the following principles in jCardSim's development process:

- [Test-Driven Development](http://en.wikipedia.org/wiki/Test-driven_development)
- [Code Conventions for the Java Programming Language](http://www.oracle.com/technetwork/java/codeconvtoc-136057.html)

