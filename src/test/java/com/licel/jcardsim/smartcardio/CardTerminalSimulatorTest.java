package com.licel.jcardsim.smartcardio;

import com.licel.jcardsim.utils.AutoResetEvent;
import javacard.framework.ISO7816;
import junit.framework.TestCase;
import org.spongycastle.util.encoders.Hex;

import javax.smartcardio.*;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class CardTerminalSimulatorTest extends TestCase {
    private static final ATR ETALON_ATR = new ATR(Hex.decode("3BFA1800008131FE454A434F5033315632333298"));
    private static final String TEST_APPLET_AID = "010203040506070809";

    public CardTerminalSimulatorTest(String name) {
        super(name);
    }

    @Override
    protected void setUp() throws Exception {
        System.setProperty("com.licel.jcardsim.card.applet.0.AID", TEST_APPLET_AID);
        System.setProperty("com.licel.jcardsim.card.applet.0.Class", "com.licel.jcardsim.samples.HelloWorldApplet");
    }

    @Override
    protected void tearDown() throws Exception {
        System.clearProperty("com.licel.jcardsim.card.applet.0.AID");
        System.clearProperty("com.licel.jcardsim.card.applet.0.Class");
    }

    public void testCreateSingleTerminal() throws CardException, InterruptedException {
        final AutoResetEvent autoResetEvent = new AutoResetEvent();

        // get instance
        final CardTerminals terminals = CardTerminalSimulator.terminals("my terminal");
        final CardTerminal terminal = terminals.getTerminal("my terminal");

        // create and insert card
        CardSimulator cardSimulator = new CardSimulator();
        cardSimulator.assignToTerminal(terminal);
        assertSame(terminal, cardSimulator.getAssignedCardTerminal());

        // connect to card
        Card card = terminal.connect("T=1");
        test(card);

        // assign same card
        new Thread() {
            @Override
            public void run() {
                try {
                    if (terminals.waitForChange(0)) {
                        autoResetEvent.signal();
                    }
                } catch (CardException e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();
        cardSimulator.assignToTerminal(terminal);
        assertTrue(autoResetEvent.await(1, TimeUnit.SECONDS));
        assertSame(terminal, cardSimulator.getAssignedCardTerminal());

        // assign different card
        CardSimulator cardSimulator2 = new CardSimulator();
        assertNull(cardSimulator2.getAssignedCardTerminal());
        cardSimulator2.assignToTerminal(terminal);

        assertSame(terminal, cardSimulator2.getAssignedCardTerminal());
        assertNull(cardSimulator.getAssignedCardTerminal());
    }

    public void testCreateTerminals() throws CardException {
        // get instance
        CardTerminals terminals = CardTerminalSimulator.terminals("terminal #1", "terminal #2");
        CardTerminal terminal = terminals.getTerminal("terminal #2");

        // create and insert card
        CardSimulator cardSimulator = new CardSimulator();
        cardSimulator.assignToTerminal(terminal);

        // connect to card
        Card card = terminal.connect("T=1");
        test(card);
    }

    public void testProvider() throws CardException, NoSuchAlgorithmException {
        // register security provider
        if (Security.getProvider("CardTerminalSimulator") == null) {
            Security.addProvider(new CardTerminalSimulator.SecurityProvider());
        }

        // get instance
        TerminalFactory tf = TerminalFactory.getInstance("CardTerminalSimulator", null);
        CardTerminals terminals = tf.terminals();
        CardTerminal terminal = terminals.getTerminal("jCardSim.Terminal");

        // create and insert card
        CardSimulator cardSimulator = new CardSimulator();
        cardSimulator.assignToTerminal(terminal);

        // connect to card
        Card card = terminal.connect("T=1");
        test(card);
    }

    public void testProviderCustomNames() throws CardException, NoSuchAlgorithmException {
        // register security provider
        if (Security.getProvider("CardTerminalSimulator") == null) {
            Security.addProvider(new CardTerminalSimulator.SecurityProvider());
        }

        // get instance
        TerminalFactory tf = TerminalFactory.getInstance("CardTerminalSimulator", new String[]{"x", "y"});
        CardTerminals terminals = tf.terminals();
        CardTerminal terminal = terminals.getTerminal("y");

        // create and insert card
        CardSimulator cardSimulator = new CardSimulator();
        cardSimulator.assignToTerminal(terminal);

        // connect to card
        Card card = terminal.connect("T=1");

        test(card);
        assertEquals(2, terminals.list().size());
        assertEquals("x", terminals.list().get(0).getName());
        assertEquals("y", terminals.list().get(1).getName());
    }

    public void testWaitForInsert() throws CardException, InterruptedException {
        final AutoResetEvent autoResetEvent = new AutoResetEvent();
        final CardTerminals terminals = CardTerminalSimulator.terminals("my terminal");
        final CardTerminal terminal = terminals.getTerminal("my terminal");
        assertEquals(true, terminal.waitForCardAbsent(1));
        assertEquals(false, terminal.waitForCardPresent(1));

        final CardSimulator cardSimulator = new CardSimulator();
        new Thread() {
            @Override
            public void run() {
                cardSimulator.assignToTerminal(terminal);
                autoResetEvent.signal();
            }
        }.start();

        autoResetEvent.await(1, TimeUnit.MINUTES);
        assertEquals(true, terminal.waitForCardPresent(1));

        // connect to card
        Card card = terminal.connect("T=1");
        test(card);
    }

    public void testWaitForCardAbsent() throws CardException, InterruptedException {
        final CardTerminals terminals = CardTerminalSimulator.terminals("my terminal");
        final CardTerminal terminal = terminals.getTerminal("my terminal");

        final CardSimulator cardSimulator = new CardSimulator();
        cardSimulator.assignToTerminal(terminal);

        assertEquals(true, terminal.waitForCardPresent(1));
        assertEquals(false, terminal.waitForCardAbsent(1));

        new Thread() {
            @Override
            public void run() {
                cardSimulator.assignToTerminal(null);
            }
        }.start();

        assertEquals(true, terminal.waitForCardAbsent(0));
    }

    public void testWaitForCardChange() throws CardException, InterruptedException {
        final CardTerminals terminals = CardTerminalSimulator.terminals("my terminal");

        final CardSimulator cardSimulator = new CardSimulator();
        assertEquals(false, terminals.waitForChange(1));

        CardTerminal terminal = terminals.getTerminal("my terminal");
        cardSimulator.assignToTerminal(terminal);

        assertEquals(true, terminals.waitForChange(1));
        assertEquals(false, terminals.waitForChange(1));
        cardSimulator.assignToTerminal(null);
        assertEquals(true, terminals.waitForChange(1));
    }

    public void testList() throws CardException, InterruptedException {
        final CardTerminals terminals = CardTerminalSimulator.terminals("1", "2", "3", "4");

        CardTerminal terminal1 = terminals.getTerminal("1");

        assertEquals(4, terminals.list().size());
        assertEquals(4, terminals.list(CardTerminals.State.ALL).size());

        CardSimulator cardSimulator = new CardSimulator();
        cardSimulator.assignToTerminal(terminal1);
        assertTrue(terminal1.isCardPresent());
        assertEquals(3, terminals.list(CardTerminals.State.CARD_ABSENT).size());
        assertEquals(3, terminals.list(CardTerminals.State.CARD_REMOVAL).size());
        assertEquals(0, terminals.list(CardTerminals.State.CARD_PRESENT).size());
        assertEquals(1, terminals.list(CardTerminals.State.CARD_INSERTION).size());
        assertEquals(0, terminals.list(CardTerminals.State.CARD_PRESENT).size());

        assertEquals(true, terminals.waitForChange(1));
        assertEquals(1, terminals.list(CardTerminals.State.CARD_INSERTION).size());
        assertEquals(0, terminals.list(CardTerminals.State.CARD_REMOVAL).size());

        cardSimulator.assignToTerminal(null);
        assertFalse(terminal1.isCardPresent());
        assertEquals(true, terminals.waitForChange(1));

        assertEquals(4, terminals.list(CardTerminals.State.ALL).size());
        assertEquals(3, terminals.list(CardTerminals.State.CARD_ABSENT).size());
        assertEquals(1, terminals.list(CardTerminals.State.CARD_REMOVAL).size());

        assertEquals(0, terminals.list(CardTerminals.State.CARD_INSERTION).size());
        assertEquals(0, terminals.list(CardTerminals.State.CARD_PRESENT).size());
        assertEquals(4, terminals.list(CardTerminals.State.CARD_ABSENT).size());
    }

    public void testExclusive() throws CardException, InterruptedException {
        final CardTerminal terminal = CardTerminalSimulator.terminal(new CardSimulator());
        final AutoResetEvent autoResetEvent = new AutoResetEvent();
        final AtomicBoolean gotException = new AtomicBoolean(false);

        final CardSimulator cardSimulator = new CardSimulator();
        cardSimulator.assignToTerminal(terminal);
        final Card card = terminal.connect("T=1");
        final CommandAPDU commandAPDU = new CommandAPDU(0, 0, 0, 0);

        card.beginExclusive();
        card.getBasicChannel().transmit(commandAPDU);

        new Thread() {
            @Override
            public void run() {
                try {
                    card.getBasicChannel().transmit(commandAPDU);
                } catch (CardException e) {
                    gotException.set(true);
                } finally {
                    autoResetEvent.signal();
                }
                autoResetEvent.signal();
            }
        }.start();

        autoResetEvent.await(1, TimeUnit.MINUTES);
        card.endExclusive();
    }

    private void test(Card jcsCard) throws CardException {
        assertTrue(jcsCard != null);
        // check card ATR
        assertEquals(jcsCard.getATR(), ETALON_ATR);
        // check card protocol
        assertEquals(jcsCard.getProtocol(), "T=1");
        // get basic channel
        CardChannel jcsChannel = jcsCard.getBasicChannel();
        assertTrue(jcsChannel != null);
        // create applet data = aid len (byte), aid bytes, params length (byte), param
        byte[] aidBytes = Hex.decode(TEST_APPLET_AID);
        byte[] createData = new byte[1 + aidBytes.length + 1 + 2 + 3];
        createData[0] = (byte) aidBytes.length;
        System.arraycopy(aidBytes, 0, createData, 1, aidBytes.length);
        createData[1 + aidBytes.length] = (byte) 5;
        createData[2 + aidBytes.length] = 0; // aid
        createData[3 + aidBytes.length] = 0; // control
        createData[4 + aidBytes.length] = 2; // params
        createData[5 + aidBytes.length] = 0xF; // params
        createData[6 + aidBytes.length] = 0xF; // params
        CommandAPDU createApplet = new CommandAPDU(0x80, 0xb8, 0, 0, createData);
        ResponseAPDU response = jcsChannel.transmit(createApplet);
        assertEquals(response.getSW(), 0x9000);
        assertEquals(true, Arrays.equals(response.getData(), aidBytes));
        // select applet
        CommandAPDU selectApplet = new CommandAPDU(ISO7816.CLA_ISO7816, ISO7816.INS_SELECT, 4, 0, Hex.decode(TEST_APPLET_AID));
        response = jcsChannel.transmit(selectApplet);
        assertEquals(response.getSW(), 0x9000);
        // test NOP
        response = jcsChannel.transmit(new CommandAPDU(0x01, 0x02, 0x00, 0x00));
        assertEquals(0x9000, response.getSW());
        // test SW_INS_NOT_SUPPORTED
        response = jcsChannel.transmit(new CommandAPDU(0x01, 0x05, 0x00, 0x00));
        assertEquals(ISO7816.SW_INS_NOT_SUPPORTED, response.getSW());
        // test hello world from card
        response = jcsChannel.transmit(new CommandAPDU(0x01, 0x01, 0x00, 0x00));
        assertEquals(0x9000, response.getSW());
        assertEquals("Hello world !", new String(response.getData()));
        // test echo
        response = jcsChannel.transmit(new CommandAPDU(0x01, 0x01, 0x01, 0x00, ("Hello javacard world !").getBytes()));
        assertEquals(0x9000, response.getSW());
        assertEquals("Hello javacard world !", new String(response.getData()));
        // test echo v2
        response = jcsChannel.transmit(new CommandAPDU(0x01, 0x03, 0x00, 0x00, ("Hello javacard world !").getBytes()));
        assertEquals(0x9000, response.getSW());
        assertEquals("Hello javacard world !", new String(response.getData()));
        // test echo install params
        response = jcsChannel.transmit(new CommandAPDU(0x01, 0x04, 0x00, 0x00));
        assertEquals(0x9000, response.getSW());
        assertEquals(0xF, response.getData()[0]);
        assertEquals(0xF, response.getData()[1]);
        // test continued data
        response = jcsChannel.transmit(new CommandAPDU(0x01, 0x06, 0x00, 0x00));
        assertEquals(0x6107, response.getSW());
        assertEquals("Hello ", new String(response.getData()));
        // test https://github.com/licel/jcardsim/issues/13
        byte[] listObjectsCmd = new byte[5];
        listObjectsCmd[0] = (byte) 0xb0;
        listObjectsCmd[1] = (byte) 0x58;
        listObjectsCmd[2] = (byte) 0x00;
        listObjectsCmd[3] = (byte) 0x00;
        listObjectsCmd[4] = (byte) 0x0E;
        response = jcsChannel.transmit(new CommandAPDU(listObjectsCmd));
        assertEquals(0x9C12, response.getSW());
        // application specific sw + data
        response = jcsChannel.transmit(new CommandAPDU(0x01, 0x07, 0x00, 0x00));
        assertEquals(0x9B00, response.getSW());
        assertEquals("Hello world !", new String(response.getData()));
        // sending maximum data
        response = jcsChannel.transmit(new CommandAPDU(0x01, 0x08, 0x00, 0x00));
        assertEquals(0x9000, response.getSW());
    }
}
