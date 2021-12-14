jCardSim (Official repo of the [jCardSim](http://jcardsim.org) project)
========

### Congratulations! jCardSim has won [Duke's Choice 2013 Award](https://www.java.net/dukeschoice/2013)!

![alt text](https://licelus.com/wp-content/uploads/DCA2013_Badge_Winner.jpg "jCardSim is a winner of Duke's Choice 2013")

This repo is including several [pull request from the original GitHub repository](https://github.com/licel/jcardsim/pulls) 
and an up-to-date release files with all changes.

The following pull request have been included:

* #176: Wrap 'nbsp' to CDATA in pom.xml
* #174: test: Do not reuse KeyPair context to generate second key pair
* #171: Bump bcprov-jdk14 from 1.46 to 1.67
* #158: Do not throw CryptoException if EC KeyBuilder.buildKey() does not match a known curve
* #157: Max case 4 command APDU size is 261
* #155: Add option to randomize RandomData
* #151: Building on Linux w/ vpcd support
* #141: Make SecureRandom securely random
* #138: Logical Channel Support / pom.xml fix
* #113 Added resize of ByteContainer for longer buffer

Not included are:

* #75: Fix AssymetricSignatureImpl behaviour wrt ISO9796_MR signatures: covered by more recent patches
* #62: Intercept Shareable creation with proxy: correctness of patch not verified
* #159: Simulate card removal and insertion by raising SIGUSR2 (when using vpcd): Not yet included

jCardSim is an open source simulator for Java Card, v3.0.5:

* `javacard.framework.*`
* `javacard.framework.security.*`
* `javacardx.crypto.*`

Key Features:

* Rapid application prototyping
* Simplifies unit testing (5 lines of code)

```java
// 1. create simulator
CardSimulator simulator = new CardSimulator();

// 2. install applet
AID appletAID = AIDUtil.create("F000000001");
simulator.installApplet(appletAID, HelloWorldApplet.class);

// 3. select applet
simulator.selectApplet(appletAID);

// 4. send APDU
CommandAPDU commandAPDU = new CommandAPDU(0x00, 0x01, 0x00, 0x00);
ResponseAPDU response = simulator.transmitCommand(commandAPDU);

// 5. check response
assertEquals(0x9000, response.getSW());
```

* Emulation of Java Card Terminal, ability to use `javax.smartcardio`
* APDU scripting (scripts are compatible with `apdutool` from Java Card Development Kit)
* Simplifies verification tests creation (Common Criteria)

*JavaDoc*: https://github.com/licel/jcardsim/tree/master/javadoc

  (Javadoc rendered: https://jcardsim.org/jcardsim/)

*Latest release 3.0.5*: https://github.com/licel/jcardsim/raw/master/jcardsim-3.0.5-SNAPSHOT.jar


### What is the difference from Oracle Java Card Development Kit simulator?

* **Implementation of javacard.security.***

  One of the main differences is the implementation of `javacard.security.*`: the current version is analogous to an NXP JCOP 31/36k card. For example, in jCardSim we have support for on-card `KeyPair.ALG_EC_F2M/ALG_RSA_CRT` key generation. Oracle's simulator only supports `KeyPair.ALG_RSA` and `KeyPair.ALG_EC_FP`, which are not supported by real cards.

* **Execution of Java Card applications without converting into CAP**

  jCardSim can work with class files without any conversions. This allows us to simplify and accelerate the development and writing of unit tests.

* **Simulator API**

  jCardSim has a simple and usable API, which also allows you to work with the simulator using `javax.smartcardio.*`.

* **Cross-platform**

  jCardSim is completely written in Java and can therefore be used on all platforms which support Java (Windows, Linux, MacOS, etc).

### How to help jCardSim?

* Join the team of jCardSim developers.
* Try out [DexProtector](https://licelus.com/products/dexprotector). The product is designed for strong and robust protection of Android applications against reverse engineering and modification.
* Licel has one more product you may be interested in - [Stringer Java Obfuscator](https://licelus.com/products/stringer). This tool provides all the features you need to comprehensively protect your Java applications.

**License**: [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)

**Third-party libraries**: [Legion of the Bouncy Castle Java](http://www.bouncycastle.org/java.html)

**Trademarks**: Oracle, Java and Java Card are trademarks of Oracle Corporation.
