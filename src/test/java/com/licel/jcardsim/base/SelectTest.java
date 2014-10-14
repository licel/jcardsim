package com.licel.jcardsim.base;

import com.licel.jcardsim.samples.MultiInstanceApplet;
import javacard.framework.*;
import junit.framework.TestCase;
import org.bouncycastle.util.encoders.Hex;

import java.util.Arrays;

public class SelectTest extends TestCase {
    private static final byte CLA = (byte) 0x90;
    private static final byte INS_GET_FULL_AID = 0;

    private static boolean selectedCalled;

    private static class UnselectableApplet extends Applet {
        public static void install(byte[] bArray, short bOffset, byte bLength) {
            new UnselectableApplet().register();
        }

        @Override
        public boolean select() {
            selectedCalled = true;
            return false;
        }

        @Override
        public void process(APDU apdu) throws ISOException {
        }
    }

    public SelectTest(String name) {
        super(name);
    }

    private AID aid(String s) {
        byte[] ba = Hex.decode(s);
        return new AID(ba, (byte)0, (byte)ba.length);
    }

    public void testAidComperator() {
        AID[] input = new AID[] {
                aid("a000008812"),
                aid("ff00066767"),
                aid("d0000cafe001"),
                aid("d0000cafe000"),
                aid("d0000cafe00023"),
                aid("d0000cafe00001"),
                aid("0100cafe01"),
                aid("0200888888")
        };
        String expected = "[0100cafe01, 0200888888, a000008812, " +
                "d0000cafe000, d0000cafe00001, d0000cafe00023, d0000cafe001, ff00066767]";
        Arrays.sort(input, new SimulatorRuntime.AidComparator());
        assertEquals(expected, Arrays.toString(input));
    }

    private Simulator prepareSimulator() {
        AID aid0 = aid("010203040506070809");
        AID aid1 = aid("d0000cafe00001");
        AID aid2 = aid("d0000cafe00002");

        Simulator simulator = new Simulator();
        simulator.installApplet(aid0, MultiInstanceApplet.class);
        simulator.installApplet(aid2, MultiInstanceApplet.class);
        simulator.installApplet(aid1, MultiInstanceApplet.class);
        return simulator;
    }

    public void testPartialSelectWorks1() {
        Simulator simulator = prepareSimulator();

        // should select d0000cafe00001
        assertTrue(simulator.selectApplet(aid("d0000cafe0")));
        byte[] expected = Hex.decode("d0000cafe000019000");
        byte[] actual = simulator.transmitCommand(new byte[]{CLA,INS_GET_FULL_AID,0,0});
        assertEquals(Arrays.toString(expected), Arrays.toString(actual));
    }

    public void testPartialSelectWorks2() {
        Simulator simulator = prepareSimulator();

        // should select d0000cafe00001
        simulator.transmitCommand(new byte[]{0, ISO7816.INS_SELECT, 4, 0, 1, (byte) 0xD0});

        byte[] expected = Hex.decode("d0000cafe000019000");
        byte[] actual = simulator.transmitCommand(new byte[]{CLA,INS_GET_FULL_AID,0,0});
        assertEquals(Arrays.toString(expected), Arrays.toString(actual));
    }

    public void testEmptySelectWorks() {
        final byte[] expected = Hex.decode("0102030405060708099000");
        Simulator simulator = prepareSimulator();

        // should select 010203040506070809
        simulator.transmitCommand(new byte[]{0, ISO7816.INS_SELECT, 4, 0});
        byte[] actual = simulator.transmitCommand(new byte[]{CLA,INS_GET_FULL_AID,0,0});
        assertEquals(Arrays.toString(expected), Arrays.toString(actual));

        // should select 010203040506070809
        simulator.transmitCommand(new byte[]{0, ISO7816.INS_SELECT, 4, 0, 0});
        actual = simulator.transmitCommand(new byte[]{CLA,INS_GET_FULL_AID,0,0});
        assertEquals(Arrays.toString(expected), Arrays.toString(actual));
    }

    public void testCanNotSelectUnselectableApplet() {
        selectedCalled = false;

        AID aid = aid("010203040506070809");
        Simulator simulator = new Simulator();
        simulator.installApplet(aid, UnselectableApplet.class);

        byte[] result = simulator.selectAppletWithResult(aid);
        assertEquals(2, result.length);
        assertEquals(ISO7816.SW_APPLET_SELECT_FAILED, Util.getShort(result, (short)0));
        assertTrue(selectedCalled);
    }
}
