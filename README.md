jCardSim (Official repo of the [jCardSim](http://jcardsim.org) project)
========

### Congratulations! jCardSim has won [Duke's Choice 2013 Award](https://www.java.net//dukeschoice)!

![alt text](https://licel.ru/wp-content/uploads/DCA2013_Badge_Winner.jpg "jCardSim is a winner of Duke's Choice 2013")

**Please note** that we moved our code repository here from Google Code. 

jCardSim is an open source simulator implements Java Card, v.2.2.1:

* `javacard.framework.*`
* `javacard.framework.security.*`
* `javacardx.crypto.*`

Key Features:

* Rapid application prototyping
* Ease of writing Unit-tests (5 lines of code)

```java
//1. create simulator
JavaxSmartCardInterface simulator = new JavaxSmartCardInterface();
//2. install applet
simulator.installApplet(appletAID, HelloWorldApplet.class);
//3. select applet
simulator.selectApplet(appletAID);
//4. send apdu
ResponseAPDU response = simulator.transmitCommand(new CommandAPDU(0x01, 0x01, 0x00, 0x00));
//5. check response
assertEquals(0x9000, response.getSW());
```

* Emulation of Java Card Terminal, ability to use `javax.smartcardio`
* APDU scripting (scripts are compatible with apdutool from Java Card Development Kit)
* Ease of verification tests creation (Common Criteria)

*JavaDoc*: https://jcardsim.googlecode.com/svn/trunk/javadoc/index.html

*Latest stable release*: https://github.com/licel/jcardsim/raw/master/jcardsim-2.2.1-all.jar

*Snapshot Maven Repository*: https://oss.sonatype.org

```xml
<dependency>
  <groupId>com.licel</groupId>
  <artifactId>jcardsim</artifactId>
  <version>2.2.1-SNAPSHOT</version>
</dependency>
```

### What is the difference from Oracle Java Card Development Kit simulator?

* **Implementation of javacard.security.***

One of the main differences is the implementation of `javacard.security.*`: current version is analogous with NXP JCOP 31/36k card. For example, in jCardSim we have support an on-card `KeyPair.ALG_EC_F2M/ALG_RSA_CRT` key generation. Simulator from Oracle has only `KeyPair.ALG_RSA` and `KeyPair.ALG_EC_FP` support, which not supported on real cards.

* **Execution of Java Card applications without converting into CAP**

jCardSim can work with class files, without any conversions. This allows us to simplify and accelerate the development and writing of unit-tests.

* **Simulator API**

jCardSim has simple and usable API, which allows you to work with simulator like with real Java Card using `javax.smartcardio.*`.

* **Cross-platform**

jCardSim completely written in Java and can therefore be used at all platforms which supports Java (Windows, Linux, MacOS, etc).

### How to help jCardSim?

* Join team of jCardSim developers.
* Try out [DexProtector](http://dexprotector.com). The product is designed for strong and robust protection of Android applications against reverse engineering and modification.
* Licel has one more product you may be interested in - [Stringer Java Obfuscator](https://jfxstore.com/stringer). This tool provides all the features you need to comprehensively protect your Java applications.

**License**: [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)

**Third-party libraries**: [Legion of the Bouncy Castle Java](http://www.bouncycastle.org/java.html)

**Trademarks**: Oracle, Java and Java Card are trademarks of Oracle Corporation.
