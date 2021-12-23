## Quick Start Guide: API

jCardSim provides an API for rapid prototyping of Java Card applications and unit testing. There are three different ways it can be used:

 - the `CardSimulator` class
 - the `CardTerminalSimulator` class (for applications that use `javax.smartcardio`)
 - the `JavaCardRemoteInterface` interface

### Using the `CardSimulator` class

~~~java
	// 1. Create simulator
	CardSimulator simulator = new CardSimulator();

	// 2. Install applet
	AID appletAID = AIDUtil.create("F000000001");
	simulator.installApplet(appletAID, HelloWorldApplet.class);

	// 3. Select applet
	simulator.selectApplet(appletAID);

	// 4. Send APDU
	CommandAPDU commandAPDU = new CommandAPDU(0x00, 0x01, 0x00, 0x00);
	ResponseAPDU response = simulator.transmitCommand(commandAPDU);

	// 5. Check response status word
	assertEquals(0x9000, response.getSW());
~~~

The example below uses [`HelloWorldApplet`](../src/main/java/com/licel/jcardsim/samples/HelloWorldApplet.java), which responds to command APDUs as follows:

 - `CLA=0x00 INS=0x02 P1=0x00 P2=0x00 LC=0x00`: do nothing
 - `CLA=0x00 INS=0x01 P1=0x00 P2=0x00 LC=0x00`: return the bytes in "Hello world !"
 - `CLA=0x00 INS=0x01 P1=0x01 P2=0x00 LC=<length> DATA=<data>`: return the command data (echo)

 ~~~java
	CardSimulator simulator = new CardSimulator();

	byte[] appletAIDBytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
	AID appletAID = new AID(appletAIDBytes, (short) 0, (byte) appletAIDBytes.length);

	simulator.installApplet(appletAID, HelloWorldApplet.class);
	simulator.selectApplet(appletAID);

	// test NOP
	ResponseAPDU response = simulator.transmitCommand(new CommandAPDU(0x00, 0x02, 0x00, 0x00));
	assertEquals(0x9000, response.getSW());

	// test hello world from card
	response = simulator.transmitCommand(new CommandAPDU(0x00, 0x01, 0x00, 0x00));
	assertEquals(0x9000, response.getSW());
	assertEquals("Hello world !", new String(response.getData()));

	// test echo
	CommandAPDU echo = new CommandAPDU(0x00, 0x01, 0x01, 0x00, ("Hello javacard world !").getBytes());
	response = simulator.transmitCommand(echo);
	assertEquals(0x9000, response.getSW());
	assertEquals("Hello javacard world !", new String(response.getData()));
~~~

It is also possible to work with plain `byte` arrays, for example:

~~~java
	// test NOP
	byte[] response = simulator.transmitCommand(new byte[]{0,2,0,0});
	ByteUtil.requireSW(response, 0x9000);
~~~

To simplify the handling of AIDs and byte arrays we provide `AIDUtil` and `ByteUtil`:

~~~java
	// AID from byte array
	AID applet1AID = AIDUtil.create(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9});

	// AID form String
	AID applet2AID = AIDUtil.create("010203040506070809");

	assertEquals(applet1AID, applet2AID);

	// String to byte array
	String hexString = ByteUtil.hexString(new byte[]{0,2,0,0});

	// byte array from String
	byte[] bytes = ByteUtil.byteArray("00 02 00 00");

	assertEquals("00020000", hexString);
	assertEquals("00020000", ByteUtil.hexString(bytes));
~~~

### Using the `CardTerminalSimulator` class

To simplify unit testing of applications that use `javax.smartcardio`, we provide a `CardTerminalSimulator` class that simulates `javax.smartcardio.TerminalFactory`.

~~~java
	// 1. Create simulator and install applet
	CardSimulator simulator = new CardSimulator();
	AID appletAID = AIDUtil.create("F000000001");
	simulator.installApplet(appletAID, HelloWorldApplet.class);

	// 2. Create Terminal
	CardTerminal terminal = CardTerminalSimulator.terminal(simulator);

	// 3. Connect to Card
	Card card = terminal.connect("T=1");
	CardChannel channel = card.getBasicChannel();

	// 4. Select applet
	CommandAPDU selectCommand = new CommandAPDU(AIDUtil.select(appletAID));
	channel.transmit(selectCommand);

	// 5. Send APDU
	CommandAPDU commandAPDU = new CommandAPDU(0x00, 0x01, 0x00, 0x00);
	ResponseAPDU response = simulator.transmitCommand(commandAPDU);

	// 6. Check response status word
	assertEquals(0x9000, response.getSW());
~~~

**Note:** Pre-installed applets can be configured using system properties: `System.setProperty(...)`.
Properties follow the same format used in the [CLI configuration file](../jcardsim.cfg).

It is also possible to simulate multiple terminals using `javax.smartcardio.CardTerminals`.
In this case each `javax.smartcardio.CardTerminal` starts in an empty state (`isCardPresent()` returns `false`).
Cards can be inserted via `CardSimulator#assignToTerminal(terminal)` and removed via
`CardSimulator#assignToTerminal(null)`.

~~~java
	// Obtain CardTerminal
	CardTerminals cardTerminals = CardTerminalSimulator.terminals("My terminal 1", "My terminal 2");
	CardTerminal terminal1 = cardTerminals.getTerminal("My terminal 1");
	CardTerminal terminal2 = cardTerminals.getTerminal("My terminal 2");

	assertEquals(false, terminal1.isCardPresent());
	assertEquals(false, terminal2.isCardPresent());

	// Create simulator and install applet
	CardSimulator simulator = new CardSimulator();
	AID appletAID = AIDUtil.create("F000000001");
	simulator.installApplet(appletAID, HelloWorldApplet.class);

	// Insert Card into "My terminal 1"
	simulator.assignToTerminal(terminal1);
	assertEquals(true,  terminal1.isCardPresent());
	assertEquals(false, terminal2.isCardPresent());
~~~

Creating a terminal via `javax.smartcardio.TerminalFactory`:

~~~java
	// Register security provider
	if (Security.getProvider("CardTerminalSimulator") == null) {
		Security.addProvider(new CardTerminalSimulator.SecurityProvider());
	}

	// Get TerminalFactory
	Object params = null;
	TerminalFactory factory = TerminalFactory.getInstance("CardTerminalSimulator", params);

	// Get CardTerminal
	CardTerminals cardTerminals = factory.terminals();
	CardTerminal terminal = cardTerminals.getTerminal("jCardSim.Terminal");
	assertNotNull(terminal);

	// Insert Card
	simulator.assignToTerminal(terminal);
~~~

Creating multiple terminals via `javax.smartcardio.TerminalFactory`:

~~~java
	String[] names = new String[] {"My terminal 1", "My terminal 2"};
	TerminalFactory factory = TerminalFactory.getInstance("CardTerminalSimulator", names);

	CardTerminals cardTerminals = factory.terminals();
	assertNotNull(cardTerminals.getTerminal("My terminal 1"));
	assertNotNull(cardTerminals.getTerminal("My terminal 2"));
~~~

#### Current version's limitations

- The `javax.smartcardio.Card#openLogicalChannel` method is not supported.
- The `javax.smartcardio.Card#transmitControlCommand` method is not supported.

#### Legacy TerminalFactory

Previous versions of jCardSim provided a limited `TerminalFactory` implementation (`JCSTerminal`). An example is provided in [JCardSimProviderTest.java](https://github.com/licel/jcardsim/blob/master/src/test/java/com/licel/jcardsim/smartcardio/JCardSimProviderTest.java).

### Using the `JavaCardRemoteInterface` interface

It is possible to interact with a remote instance of jCardSim. For example you may
run one or multiple instances of virtual Java Card and connect to it via TCP/IP.

An example is provided in [JavaCardRemoteServerTest.java](../src/test/java/com/licel/jcardsim/remote/JavaCardRemoteServerTest.java).
