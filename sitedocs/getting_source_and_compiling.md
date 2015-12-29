jCardSim is an open source project and it would be pleasure for us to see you as commiters and contributors!

### Source code
The official jCardSim source repository is located at [https://github.com/licel/jcardsim](https://github.com/licel/jcardsim).


### Building
1. jCardSim does not contain any Oracle's Java Card API source code, because of that it is needed to download JCDK from Oracle’s site and unpack it.
2. Set `jcdkLocation` property in `setting.xml` (profiles - profile), for example:

~~~
    <jcdkLocation>/Users/developer/jcdk304</jcdkLocation>
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

