### Downloading jCardSim
Follow the link to download jCardSim jar archive:  
[https://github.com/licel/jcardsim/raw/master/jcardsim-2.2.1-all.jar](https://github.com/licel/jcardsim/raw/master/jcardsim-2.2.1-all.jar)

### Starting jCardSim in a CLI mode
To simplify the development and debugging processes, jCardSim is working with a files of classes directly. You haven't to convert your application to CAP for an execution in simulator.

Simulator has CLI and API, so you can choose what you want to use. See more about using API and Unit Test writing in the second part of 
[Quick Start Guide](http://jcardsim.org/docs/quick-start-guide-simulator-api).  
For an application execution and interaction with through APDU scripts you should use `com.licel.jcardsim.utils.APDUScriptTool` class.

**Start parameters:**

	java -cp jcardsim-2.2.1-all.jar com.licel.jcardsim.utils.APDUScriptTool <jcardsim.cfg> <apdu script> [out file]


<code>jcardsim.cfg</code> — file with settings of simulator. Here you can set information about your applets. It has the following format:

	com.licel.jcardsim.card.applet.{index}.AID=<Applet AID>
	com.licel.jcardsim.card.applet.{index}.Class=<Applet ClassName>

where `{index}` is the number from 0 to 10.  
>**NOTE:** Applet classes and it's dependencies must be in class path when simulator starts.

In an example case settings file will look like:

	com.licel.jcardsim.card.applet.0.AID=010203040506070809
	com.licel.jcardsim.card.applet.0.Class=com.licel.jcardsim.samples.HelloWorldApplet

`<apdu script>` - file with APDU commands in C-APDU format, this file compatible with script format of apdutool from Java Card Development Kit.

C-APDUs ends with `(;)` and for the comments you should use `//`

APDU commands could be represented by DEC or HEX characters. HEX characters should start from `0x`.
One C-APDU command can span multiple lines.

C-APDU command has the following format:

	<CLA> <INS> <P1> <P2> <LC> [<byte 0> <byte 1> ... <byte LC-1>] <LE> ;
where:  
`<CLA>` :: ISO 7816-4 class byte. 
`<INS>` :: ISO 7816-4 instruction byte. 
`<P1>`  :: ISO 7816-4 P1 parameter byte. 
`<P2>`  :: ISO 7816-4 P2 parameter byte. 
`<LC>`  :: ISO 7816-4 input byte count. 1 byte 
`<byte 0> ... <byte LC-1>` :: input data bytes. 
`<LE>`  :: ISO 7816- 4 expected output length. 1 byte

As an example of Java Card Applet we will use `com.licel.jcardsim.samples.HelloWorld`. It is a simple applet, it can process following APDU commands:

- Do nothing `CLA=0x01 INS=0x02 P1=0x00 P2=0x00 LC=0x00`
- Return bytes of "Hello world!" `CLA=0x01 INS=0x01 P1=0x00 P2=0x00 LC=0x00`
- Return sended data (echo) `CLA=0x01 INS=0x01 P1=0x01 P2=0x00 LC=<length> DATA=<data>`

Let's write C-APDU script for our HelloWorld applet:

	// CREATE APPLET CMD
	0x80 0xb8 0x00 0x00 0x10 0x9 0x01 0x02 0x03 0x04 0x05 0x06 0x07 0x8 0x09 0x05 0x00 0x00 0x02 0xF 0xF 0x7f;
	// SELECT APPLET CMD
	0x00 0xa4 0x00 0x00 0x09 0x01 0x02 0x03 0x04 0x05 0x06 0x07 0x8 0x09 0x2;
	// TEST NOP
	0x01 0x02 0x00 0x00 0x00 0x2;
	// test hello world from card
	0x01 0x01 0x00 0x00 0x00 0x0d;
	// test echo
	0x01 0x01 0x01 0x00 0x0d 0x48 0x65 0x6c 0x6c 0x6f 0x20 0x77 0x6f 0x72 0x6c 0x64 0x20 0x21 0x0d;


Now we are saving C-APDU script into helloworld.apdu file and starting the simulator. If we don't point out third parameter the result prints to the console.

	java -cp jcardsim-2.2.1-all.jar com.licel.jcardsim.utils.APDUScriptTool jcardsim.cfg helloworld.apdu

**Result:**

	CLA: 80, INS: b8, P1: 00, P2: 00, Lc: 10, 09, 01, 02, 03, 04, 05, 06, 07, 08, 09, 05, 00, 00, 02, 0f, 0f, Le: 09, 01, 02, 03, 04, 05, 06, 07, 08, 09, SW1: 90, SW2: 00
	CLA: 00, INS: a4, P1: 00, P2: 00, Lc: 09, 01, 02, 03, 04, 05, 06, 07, 08, 09, Le: 00, SW1: 90, SW2: 00
	CLA: 01, INS: 02, P1: 00, P2: 00, Lc: 00, Le: 00, SW1: 90, SW2: 00
	CLA: 01, INS: 01, P1: 00, P2: 00, Lc: 00, Le: 0d, 48, 65, 6c, 6c, 6f, 20, 77, 6f, 72, 6c, 64, 20, 21, SW1: 90, SW2: 00
	CLA: 01, INS: 01, P1: 01, P2: 00, Lc: 0d, 48, 65, 6c, 6c, 6f, 20, 77, 6f, 72, 6c, 64, 20, 21, Le: 0d, 48, 65, 6c, 6c, 6f, 20, 77, 6f, 72, 6c, 64, 20, 21, SW1: 90, SW2: 00

The output is also comply with apdutool from the Java Card Development Kit.

> The second part: [Quick Start Guide: Simulator API](http://jcardsim.org/docs/quick-start-guide-simulator-api).
