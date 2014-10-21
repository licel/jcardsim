package com.licel.jcardsim.base;

import com.licel.jcardsim.samples.HelloWorldApplet;
import com.licel.jcardsim.utils.AIDUtil;
import com.licel.jcardsim.utils.ByteUtil;
import javacard.framework.*;
import junit.framework.TestCase;

public class ApduContextTest extends TestCase {
    private static class DummyApplet extends Applet implements AppletEvent {
        public static boolean exceptionInSelect = false;
        public static boolean exceptionInInstall = false;
        public static boolean exceptionInDeselect = false;
        public static boolean exceptionInUninstall = false;
        public static boolean exceptionIllegalUse1 = false;
        public static boolean exceptionIllegalUse2 = false;

        @SuppressWarnings("unused")
        public static void install(byte[] bArray, short bOffset, byte bLength) {
            exceptionInSelect = false;
            exceptionInInstall = false;
            exceptionInDeselect = false;
            exceptionInUninstall = false;


            try {
                APDU.getCurrentAPDU();
            }
            catch (SecurityException se) {
                exceptionInInstall = true;
            }

            new DummyApplet().register();
        }

        @Override
        public boolean select() {
            try {
                APDU.getCurrentAPDU();
            }
            catch (SecurityException se) {
                exceptionInSelect = true;
            }
            return true;
        }

        @Override
        public void process(APDU a) throws ISOException {
            APDU apdu = APDU.getCurrentAPDU();
            try {
                apdu.getIncomingLength();
                exceptionIllegalUse1 = false;
            }
            catch (APDUException e) {
                exceptionIllegalUse1 = e.getReason() == APDUException.ILLEGAL_USE;
            }
            try {
                apdu.getOffsetCdata();
                exceptionIllegalUse2 = false;
            }
            catch (APDUException e) {
                exceptionIllegalUse2 = e.getReason() == APDUException.ILLEGAL_USE;
            }
            apdu.setIncomingAndReceive();
            apdu.getIncomingLength();
            apdu.getOffsetCdata();
        }

        @Override
        public void deselect() {
            try {
                APDU.getCurrentAPDU();
            }
            catch (SecurityException se) {
                exceptionInDeselect = true;
            }
        }

        public void uninstall() {
            try {
                APDU.getCurrentAPDU();
            }
            catch (SecurityException se) {
                exceptionInUninstall = true;
            }
        }
    }

    public ApduContextTest(String name) {
        super(name);
    }

    public void testCallingGetCurrentAPDUinWrongContextThrows() {
        Simulator simulator = new Simulator();
        AID otherAppletAID = AIDUtil.create("d0000cafe00001");
        AID dummyAppletAID = AIDUtil.create("d0000cafe00002");

        simulator.installApplet(otherAppletAID, HelloWorldApplet.class);
        simulator.installApplet(dummyAppletAID, DummyApplet.class);
        assertTrue(DummyApplet.exceptionInInstall);

        simulator.selectApplet(dummyAppletAID);
        assertTrue(DummyApplet.exceptionInSelect);

        byte[] response = simulator.transmitCommand(new byte[]{(byte) 0x80,0,0,0});
        assertEquals(ISO7816.SW_NO_ERROR, ByteUtil.getSW(response));
        assertTrue(DummyApplet.exceptionIllegalUse1);
        assertTrue(DummyApplet.exceptionIllegalUse2);

        simulator.selectApplet(otherAppletAID);
        assertTrue(DummyApplet.exceptionInDeselect);

        simulator.deleteApplet(dummyAppletAID);
        assertTrue(DummyApplet.exceptionInUninstall);
    }
}
