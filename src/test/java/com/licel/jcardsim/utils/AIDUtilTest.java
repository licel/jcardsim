package com.licel.jcardsim.utils;

import javacard.framework.AID;
import junit.framework.TestCase;

public class AIDUtilTest extends TestCase {
    public AIDUtilTest(String name) {
        super(name);
    }

    public void testSelectString() {
        assertEquals("00A4040005CAFECAFE0100" ,ByteUtil.hexString(AIDUtil.select("cafecafe01")));
        assertEquals("00A4040001CA00" ,ByteUtil.hexString(AIDUtil.select("ca")));
        assertEquals("00A404000000", ByteUtil.hexString(AIDUtil.select("")));
    }

    public void testSelectAID() {
        AID aid = AIDUtil.create("cafecafe01");
        assertEquals("00A4040005CAFECAFE0100" ,ByteUtil.hexString(AIDUtil.select(aid)));
    }
}
