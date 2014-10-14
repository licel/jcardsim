package com.licel.jcardsim.base;

import com.licel.jcardsim.samples.MultiInstanceApplet;
import javacard.framework.AID;
import javacard.framework.Util;
import junit.framework.TestCase;
import org.bouncycastle.util.encoders.Hex;

public class DeleteTest extends TestCase {
    private static final byte CLA = (byte) 0x90;
    private static final byte INS_GET_COUNT = 2;

    public DeleteTest(String name) {
        super(name);
    }

    private AID aid(String s) {
        byte[] ba = Hex.decode(s);
        return new AID(ba, (byte)0, (byte)ba.length);
    }

    public void testDeleteWorks() {
        byte[] result;
        AID aid1 = aid("d0000cafe00001");
        AID aid2 = aid("d0000cafe00002");

        Simulator simulator = new Simulator();

        // install first instance
        simulator.installApplet(aid1, MultiInstanceApplet.class);
        simulator.selectApplet(aid1);

        // check instance counter == 1
        result = simulator.transmitCommand(new byte[]{CLA, INS_GET_COUNT, 0, 0});
        assertEquals(1, Util.getShort(result, (short)0));

        // install second instance
        simulator.installApplet(aid2, MultiInstanceApplet.class);

        // check instance counter == 2
        result = simulator.transmitCommand(new byte[]{CLA, INS_GET_COUNT, 0, 0});
        assertEquals(2, Util.getShort(result, (short)0));

        // delete instance 1
        simulator.deleteApplet(aid1);

        // check instance counter == 1
        simulator.selectApplet(aid2);
        result = simulator.transmitCommand(new byte[]{CLA, INS_GET_COUNT, 0, 0});
        assertEquals(1, Util.getShort(result, (short)0));
    }
}
