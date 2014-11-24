jCardSim was originally developed for a fast prototyping of the Java Card applications, and writing unit-tests.

There are two ways to use the simulator:

 - Using of Simulator API
 - Using the `javax.smartcardio` for an interaction with JavaCard

In both cases it is possible to interact with a remote instance of jCardSim. For example you may run one or multiple instances of virtual Java Card and connect to it via TCP/IP.  

### Using Simulator API's methods

The main interface for working with simulator is `com.licel.jcardsim.io.JavaCardInterface`, its specification available [here](http://jcardsim.org/jcardsim/com/licel/jcardsim/io/JavaCardInterface.html). In order to get its implementation use `com.licel.jcardsim.io.CAD`.

At first it is necessary to set connection parameters:

	// 0 - Local Mode
	// 1 - Remote Mode
	// 2 - Local Mode with ResponseAPDU transmitCommand(CommandAPDU) method
	System.setProperty("com.licel.jcardsim.terminal.type", "2");
	CAD cad = new CAD(System.getProperties());

Create connection:

	JavaxSmartCardInterface simulator = (JavaxSmartCardInterface) cad.getCardInterface();

Next, installing an applet:

	simulator.installApplet(appletAID, HelloWorldApplet.class);

Selecting:

	simulator.selectApplet(appletAID);

Sending an APDU command:

	ResponseAPDU response = simulator.transmitCommand(new CommandAPDU(0x01, 0x01, 0x00, 0x00));

And check a result of the execution:

	assertEquals(0x9000, response.getSW());

The example of how to work with HelloWorldApplet (from first part of [Quick Start Guide: Using in CLI mode](http://jcardsim.org/docs/quick-start-guide-using-in-cli-mode)):

	System.setProperty("com.licel.jcardsim.terminal.type", "2");
	CAD cad = new CAD(System.getProperties());
	JavaxSmartCardInterface simulator = (JavaxSmartCardInterface) cad.getCardInterface();
	byte[] appletAIDBytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
	AID appletAID = new AID(appletAIDBytes, (short) 0, (byte) appletAIDBytes.length);
	simulator.installApplet(appletAID, HelloWorldApplet.class);
	simulator.selectApplet(appletAID);
	// test NOP
	ResponseAPDU response = simulator.transmitCommand(new CommandAPDU(0x01, 0x02, 0x00, 0x00));
	assertEquals(0x9000, response.getSW());
	// test hello world from card
	response = simulator.transmitCommand(new CommandAPDU(0x01, 0x01, 0x00, 0x00));
	assertEquals(0x9000, response.getSW());
	assertEquals("Hello world !", new String(response.getData()));
	// test echo
	response = simulator.transmitCommand(new CommandAPDU(0x01, 0x01, 0x01, 0x00, ("Hello javacard world !").getBytes()));
	assertEquals(0x9000, response.getSW());
	assertEquals("Hello javacard world !", new String(response.getData()));

### Using  `javax.smartcardio` for an interaction with JavaCard
For ease of writing Unit tests for an applications which use `javax.smartcardio`, we have the provider for Java Card Terminal's emulation in jCardSim.

The complete example can be found at [JCardSimProviderTest.java](https://github.com/licel/jcardsim/blob/master/src/test/java/com/licel/jcardsim/smartcardio/JCardSimProviderTest.java).

To use it you have to register jCardSim TerminalFactory Provider:

	if (Security.getProvider("jCardSim") == null) {
	       JCardSimProvider provider = new JCardSimProvider();
	       Security.addProvider(provider);
	}

Choose terminal:

	TerminalFactory tf = TerminalFactory.getInstance("jCardSim", null);
	CardTerminals ct = tf.terminals();
	List<CardTerminal> list = ct.list();
	CardTerminal jcsTerminal = null;
	for (int i = 0; i < list.size(); i++) {
	     if (list.get(i).getName().equals("jCardSim.Terminal")) {
	         jcsTerminal = list.get(i);
	         break;
	      }
	}

Then, you can use `javax.smartcardio` API.
> **Note:** Pre-installed applets can be configured using system properties: `System.setProperty(...)`, the format is equal with configuration file of the CLI mode of jCardSim.

Example of how to work with HelloWorldApplet:  
	
	String TEST_APPLET_AID = "010203040506070809";
	System.setProperty("com.licel.jcardsim.applet.0.AID", TEST_APPLET_AID);
	System.setProperty("com.licel.jcardsim.applet.0.Class", "com.licel.jcardsim.samples.HelloWorldApplet");
	if (Security.getProvider("jCardSim") == null) {
	      JCardSimProvider provider = new JCardSimProvider();
	       Security.addProvider(provider);
	}
	TerminalFactory tf = TerminalFactory.getInstance("jCardSim", null);
	CardTerminals ct = tf.terminals();
	List<CardTerminal> list = ct.list();
	CardTerminal jcsTerminal = null;
	for (int i = 0; i < list.size(); i++) {
	     if (list.get(i).getName().equals("jCardSim.Terminal")) {
	           jcsTerminal = list.get(i);
	          break;
	      }
	 }
	 Card jcsCard = jcsTerminal.connect("T=0");
	 CardChannel jcsChannel = jcsCard.getBasicChannel();
	 // create applet data = aid len (byte), aid bytes, params lenth (byte), param 
	 byte[] aidBytes = Hex.decode(TEST_APPLET_AID);
	 byte[] createData = new byte[1+aidBytes.length+1];
	 createData[0] = (byte) aidBytes.length;
	 System.arraycopy(aidBytes, 0, createData, 1, aidBytes.length);
	 CommandAPDU createApplet = new CommandAPDU(0x80, 0xb8, 0, 0, createData);
	 ResponseAPDU response = jcsChannel.transmit(createApplet);
	 assertEquals(response.getSW(), 0x9000);
	 assertEquals(true, Arrays.equals(response.getData(), aidBytes));
	 // select applet
	 CommandAPDU selectApplet = new CommandAPDU(ISO7816.CLA_ISO7816, ISO7816.INS_SELECT, 0, 0, Hex.decode(TEST_APPLET_AID));
	 response = jcsChannel.transmit(selectApplet);
	 assertEquals(response.getSW(), 0x9000);
	 // test NOP
	 response = jcsChannel.transmit(new CommandAPDU(0x01, 0x02, 0x00, 0x00));
	 assertEquals(0x9000, response.getSW());
	 // test hello world from card
	 response = jcsChannel.transmit(new CommandAPDU(0x01, 0x01, 0x00, 0x00));
	 assertEquals(0x9000, response.getSW());
	 assertEquals("Hello world !", new String(response.getData()));
	 // test echo
	 response = jcsChannel.transmit(new CommandAPDU(0x01, 0x01, 0x01, 0x00, ("Hello javacard world !").getBytes()));
	 assertEquals(0x9000, response.getSW());
	 assertEquals("Hello javacard world !", new String(response.getData()));

**Current version's limitations:**  

The `openLogicalChannel()` method always returns the `basicChannel`.
