package com.licel.jcardsim;

import com.licel.jcardsim.samples.HelloWorldApplet;
import com.licel.jcardsim.smartcardio.CardSimulator;
import com.licel.jcardsim.smartcardio.CardTerminalSimulator;
import com.licel.jcardsim.utils.AIDUtil;
import com.licel.jcardsim.utils.ByteUtil;
import javacard.framework.AID;
import junit.framework.TestCase;

import javax.smartcardio.*;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

/**
 * Contains all listing from the documentation
 */
public class DocumentationCodeSamplesTest extends TestCase {
    public void testCodeListingReadme() {
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
    }

    public void testCodeListing1() {
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
    }


    public void testCodeListing2() {
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
    }

    public void testCodeListing3() {
        CardSimulator simulator = new CardSimulator();

        byte[] appletAIDBytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9};
        AID appletAID = new AID(appletAIDBytes, (short) 0, (byte) appletAIDBytes.length);

        simulator.installApplet(appletAID, HelloWorldApplet.class);
        simulator.selectApplet(appletAID);

        // test NOP
        byte[] response = simulator.transmitCommand(new byte[]{0x00, 0x02, 0x00, 0x00});
        ByteUtil.requireSW(response, 0x9000);
    }

    public void testCodeListing4() {
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
    }

    public void testCodeListing5() throws CardException {
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
    }

    public void testCodeListing6() throws CardException {
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
        assertEquals(true, terminal1.isCardPresent());
        assertEquals(false, terminal2.isCardPresent());
    }

    public void testCodeListing7() throws CardException, NoSuchAlgorithmException {
        // Register provider
        if (Security.getProvider("CardTerminalSimulator") == null) {
            Security.addProvider(new CardTerminalSimulator.SecurityProvider());
        }

        // Get TerminalFactory
        TerminalFactory factory = TerminalFactory.getInstance("CardTerminalSimulator", null);
        CardTerminals cardTerminals = factory.terminals();

        // Get CardTerminal
        CardTerminal terminal = cardTerminals.getTerminal("jCardSim.Terminal");
        assertNotNull(terminal);
    }

    public void testCodeListing8() throws CardException, NoSuchAlgorithmException {
        // Register provider
        if (Security.getProvider("CardTerminalSimulator") == null) {
            Security.addProvider(new CardTerminalSimulator.SecurityProvider());
        }

        // Get TerminalFactory with custom names
        String[] names = new String[] {"My terminal 1", "My terminal 2"};
        TerminalFactory factory = TerminalFactory.getInstance("CardTerminalSimulator", names);
        CardTerminals cardTerminals = factory.terminals();
        assertNotNull(cardTerminals.getTerminal("My terminal 1"));
        assertNotNull(cardTerminals.getTerminal("My terminal 2"));
    }
}
