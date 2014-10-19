package com.licel.jcardsim.base;

import com.licel.jcardsim.samples.MultiInstanceApplet;
import com.licel.jcardsim.utils.AIDUtil;
import com.licel.jcardsim.utils.ByteUtil;
import javacard.framework.*;
import junit.framework.TestCase;
import org.bouncycastle.util.encoders.Hex;

import java.util.Arrays;

public class SelectTest extends TestCase {
    private static final byte CLA = (byte) 0x80;
    private static final byte INS_GET_FULL_AID = 0;

    private static boolean selectedCalled;

    private static class UnselectableApplet extends Applet {
        @SuppressWarnings("unused")
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

    public void testAidComparator() {
        AID[] input = new AID[] {
                aid("A000008812"),
                aid("FF00066767"),
                aid("D0000CAFE001"),
                aid("D0000CAFE000"),
                aid("D0000CAFE00023"),
                aid("D0000CAFE00001"),
                aid("0100CAFE01"),
                aid("0200888888")
        };
        Arrays.sort(input, AIDUtil.comparator());

        String[] tmp = new String[input.length];
        for (int i = 0; i < input.length; i++) {
            tmp[i] = AIDUtil.toString(input[i]);
        }
        String expected = "[0100CAFE01, 0200888888, A000008812, " +
                "D0000CAFE000, D0000CAFE00001, D0000CAFE00023, D0000CAFE001, FF00066767]";

        assertEquals(expected, Arrays.toString(tmp));
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
        byte[] actual = simulator.transmitCommand(new byte[]{CLA,INS_GET_FULL_AID, 0, 0});
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

    public void testApduWithoutSelectedAppletFails() {
        Simulator simulator = new Simulator();
        byte[] cmd = new byte[]{CLA, INS_GET_FULL_AID, 0, 0};
        byte[] result;

        result = simulator.transmitCommand(cmd);
        assertEquals(ISO7816.SW_COMMAND_NOT_ALLOWED, ByteUtil.getSW(result));

        result = CardManager.dispatchApdu(simulator, new byte[]{CLA,INS_GET_FULL_AID, 0, 0});
        assertEquals(ISO7816.SW_COMMAND_NOT_ALLOWED, ByteUtil.getSW(result));
    }
}
