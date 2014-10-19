package com.licel.jcardsim.base;

import com.licel.jcardsim.samples.Sha1Applet;
import com.licel.jcardsim.utils.AIDUtil;
import javacard.framework.AID;
import javacard.framework.JCSystem;
import javacard.framework.SystemException;
import junit.framework.TestCase;

import javax.smartcardio.ResponseAPDU;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class TransientMemoryTest extends TestCase {
    private static final byte CLA = (byte) 0x80;
    private static final byte INS_DIGEST = 0;
    private static final byte INS_LAST_DIGEST = 6;

    public TransientMemoryTest(String name) {
        super(name);
    }

    public void testMemoryManagementWorks() {
        final Object dummy1 = new Object();

        final short size = 1;
        TransientMemory transientMemory = new TransientMemory();

        byte[] corBytes = transientMemory.makeByteArray(size, JCSystem.CLEAR_ON_RESET);
        short[] corShorts = transientMemory.makeShortArray(size, JCSystem.CLEAR_ON_RESET);
        boolean[] corBooleans = transientMemory.makeBooleanArray(size, JCSystem.CLEAR_ON_RESET);

        Object[] corObjects = transientMemory.makeObjectArray(size, JCSystem.CLEAR_ON_RESET);
        corBytes[0] = 123;
        corShorts[0] = 123;
        corBooleans[0] = true;
        corObjects[0] = dummy1;

        byte[] codBytes = transientMemory.makeByteArray(size, JCSystem.CLEAR_ON_DESELECT);
        short[] codShorts = transientMemory.makeShortArray(size, JCSystem.CLEAR_ON_DESELECT);
        boolean[] codBooleans = transientMemory.makeBooleanArray(size, JCSystem.CLEAR_ON_DESELECT);

        Object[] codObjects = transientMemory.makeObjectArray(size, JCSystem.CLEAR_ON_DESELECT);
        codBytes[0] = 123;
        codShorts[0] = 123;
        codBooleans[0] = true;
        codObjects[0] = dummy1;

        transientMemory.clearOnDeselect();
        assertTrue(codBytes[0] == 0 && corBytes[0] != 0);
        assertTrue(codShorts[0] == 0 && corShorts[0] != 0);
        assertTrue(codObjects[0] == null && corObjects[0] == dummy1);
        assertTrue(!codBooleans[0] && corBooleans[0]);

        codBytes[0] = 123;
        codShorts[0] = 123;
        codBooleans[0] = true;
        codObjects[0] = dummy1;

        transientMemory.clearOnReset();
        assertTrue(codBytes[0] == 0 && corBytes[0] == 0);
        assertTrue(codShorts[0] == 0 && corShorts[0] == 0);
        assertTrue(codObjects[0] == null && corObjects[0] == null);
        assertTrue(!codBooleans[0] && !corBooleans[0]);

        assertEquals(JCSystem.CLEAR_ON_RESET, transientMemory.isTransient(corBytes));
        assertEquals(JCSystem.CLEAR_ON_RESET, transientMemory.isTransient(corShorts));
        assertEquals(JCSystem.CLEAR_ON_RESET, transientMemory.isTransient(corBooleans));
        assertEquals(JCSystem.CLEAR_ON_RESET, transientMemory.isTransient(corObjects));

        assertEquals(JCSystem.CLEAR_ON_DESELECT, transientMemory.isTransient(codBytes));
        assertEquals(JCSystem.CLEAR_ON_DESELECT, transientMemory.isTransient(codShorts));
        assertEquals(JCSystem.CLEAR_ON_DESELECT, transientMemory.isTransient(codBooleans));
        assertEquals(JCSystem.CLEAR_ON_DESELECT, transientMemory.isTransient(codObjects));

        transientMemory.forgetBuffers();

        assertEquals(JCSystem.NOT_A_TRANSIENT_OBJECT, transientMemory.isTransient(corBytes));
        assertEquals(JCSystem.NOT_A_TRANSIENT_OBJECT, transientMemory.isTransient(corShorts));
        assertEquals(JCSystem.NOT_A_TRANSIENT_OBJECT, transientMemory.isTransient(corBooleans));
        assertEquals(JCSystem.NOT_A_TRANSIENT_OBJECT, transientMemory.isTransient(corObjects));
        assertEquals(JCSystem.NOT_A_TRANSIENT_OBJECT, transientMemory.isTransient(codBytes));
        assertEquals(JCSystem.NOT_A_TRANSIENT_OBJECT, transientMemory.isTransient(codShorts));
        assertEquals(JCSystem.NOT_A_TRANSIENT_OBJECT, transientMemory.isTransient(codBooleans));
        assertEquals(JCSystem.NOT_A_TRANSIENT_OBJECT, transientMemory.isTransient(codObjects));
    }


    public void testInvalidEventThrows() {
        final byte invalid = JCSystem.CLEAR_ON_DESELECT + JCSystem.CLEAR_ON_RESET;
        TransientMemory transientMemory = new TransientMemory();

        try {
            transientMemory.makeByteArray(2, invalid);
            fail("No exception");
        }
        catch (SystemException e) {
            assertEquals(SystemException.ILLEGAL_VALUE, e.getReason());
        }

        try {
            transientMemory.makeBooleanArray((short) 1, invalid);
            fail("No exception");
        }
        catch (SystemException e) {
            assertEquals(SystemException.ILLEGAL_VALUE, e.getReason());
        }

        try {
            transientMemory.makeObjectArray((short) 1, invalid);
            fail("No exception");
        }
        catch (SystemException e) {
            assertEquals(SystemException.ILLEGAL_VALUE, e.getReason());
        }

        try {
            transientMemory.makeBooleanArray((short) 1, invalid);
            fail("No exception");
        }
        catch (SystemException e) {
            assertEquals(SystemException.ILLEGAL_VALUE, e.getReason());
        }
    }

    public void testCleanOnDeselectWorks() throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        byte[] expectedOutput = sha1.digest(new byte[]{'A'});
        AID aid = AIDUtil.create("0102030405");

        Simulator instance = new Simulator();
        instance.installApplet(aid, Sha1Applet.class);
        instance.selectApplet(aid);

        // calculate SHA1
        byte[] apdu = new byte[]{CLA, INS_DIGEST, 0, 0, 1, 'A'};
        byte[] result = instance.transmitCommand(apdu);

        ResponseAPDU responseApdu = new ResponseAPDU(result);
        assertEquals(0x9000, responseApdu.getSW());
        assertEquals(Arrays.toString(expectedOutput), Arrays.toString(responseApdu.getData()));

        // check last digest
        apdu = new byte[]{CLA, INS_LAST_DIGEST, 0, 0};
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(0x9000, responseApdu.getSW());
        assertEquals(Arrays.toString(expectedOutput), Arrays.toString(responseApdu.getData()));

        // trigger clean on deselect
        instance.selectApplet(aid);

        // check last digest is all zero
        apdu = new byte[]{CLA, INS_LAST_DIGEST, 0, 0};
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(0x9000, responseApdu.getSW());
        assertEquals(Arrays.toString(new byte[20]), Arrays.toString(responseApdu.getData()));
    }
}
