package com.licel.jcardsim.base;

import com.licel.jcardsim.samples.Sha1Applet;
import javacard.framework.AID;
import junit.framework.TestCase;
import org.bouncycastle.util.encoders.Hex;

import javax.smartcardio.ResponseAPDU;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class ExtendedLengthTest extends TestCase {
    private static final byte CLA = (byte) 0x80;
    private static final byte INS_DIGEST = 0;
    private static final byte INS_ECHO = 2;
    private static final byte INS_LEN = 4;
    private static final byte P1 = (byte) 0;
    private static final byte P2 = (byte) 0;
    private static final byte DUMMY = (byte) 0x41;

    private static final byte[] TEST_APPLET_AID_BYTES = Hex.decode("0102030405cafe01");

    public ExtendedLengthTest(String testName) {
        super(testName);
    }

    private Simulator prepareSimulator() {
        Simulator instance = new Simulator();
        AID aid = new AID(TEST_APPLET_AID_BYTES, (short) 0, (byte) TEST_APPLET_AID_BYTES.length);
        instance.installApplet(aid, Sha1Applet.class);
        instance.selectApplet(aid);
        return instance;
    }

    public void testRegularApduDigest() throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        byte[] expectedOutput = sha1.digest(new byte[]{DUMMY});

        Simulator instance = prepareSimulator();

        byte[] apdu = new byte[]{CLA, INS_DIGEST, P1, P2, 1, DUMMY};
        byte[] result = instance.transmitCommand(apdu);

        ResponseAPDU responseApdu = new ResponseAPDU(result);
        assertEquals(0x9000, responseApdu.getSW());
        assertEquals(Arrays.toString(expectedOutput), Arrays.toString(responseApdu.getData()));
    }

    public void testRegularApduLcLe() {
        byte lc = 1;
        byte le = (byte) 0xA0;
        byte[] expectedOutput = new byte[]{0, lc, 0, le};

        Simulator instance = prepareSimulator();

        byte[] apdu = new byte[]{CLA, INS_LEN, P1, P2, lc, 0, le};
        byte[] result = instance.transmitCommand(apdu);

        ResponseAPDU responseApdu = new ResponseAPDU(result);
        assertEquals(0x9000, responseApdu.getSW());
        assertEquals(Arrays.toString(expectedOutput), Arrays.toString(responseApdu.getData()));
    }

    public void testRegularApduCase2Le() {
        byte le = (byte) 0x4;
        byte[] expectedOutput = new byte[]{0, 0, 0, le};

        Simulator instance = prepareSimulator();

        byte[] apdu = new byte[]{CLA, INS_LEN, P1, P2, le};
        byte[] result = instance.transmitCommand(apdu);

        ResponseAPDU responseApdu = new ResponseAPDU(result);
        assertEquals(0x9000, responseApdu.getSW());
        assertEquals(Arrays.toString(expectedOutput), Arrays.toString(responseApdu.getData()));
    }

    public void testRegularApduEcho() throws NoSuchAlgorithmException {
        byte[] expectedOutput = new byte[]{DUMMY};

        Simulator instance = prepareSimulator();

        byte[] apdu = new byte[]{CLA, INS_ECHO, 0, 0, 1, DUMMY};
        byte[] result = instance.transmitCommand(apdu);

        ResponseAPDU responseApdu = new ResponseAPDU(result);
        assertEquals(0x9000, responseApdu.getSW());
        assertEquals(Arrays.toString(expectedOutput), Arrays.toString(responseApdu.getData()));
    }

    public void testExtendedApduDigestWith1Byte() throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        byte[] input = new byte[]{DUMMY};
        byte[] expectedOutput = sha1.digest(input);

        Simulator instance = prepareSimulator();

        byte[] apdu = new byte[]{CLA, INS_DIGEST, 0, 0, 0, 0, 1, DUMMY};
        byte[] result = instance.transmitCommand(apdu);

        ResponseAPDU responseApdu = new ResponseAPDU(result);
        assertEquals(0x9000, responseApdu.getSW());
        assertEquals(Arrays.toString(expectedOutput), Arrays.toString(responseApdu.getData()));
    }

    public void testExtendedApduCase2Le4() {
        byte le = (byte) 0x4;
        byte[] expectedOutput = new byte[]{0, 0, 0, le};

        Simulator instance = prepareSimulator();

        byte[] apdu = new byte[]{CLA, INS_LEN, P1, P2, 0, 0, le};
        byte[] result = instance.transmitCommand(apdu);

        ResponseAPDU responseApdu = new ResponseAPDU(result);
        assertEquals(0x9000, responseApdu.getSW());
        assertEquals(Arrays.toString(expectedOutput), Arrays.toString(responseApdu.getData()));
    }

    public void testExtendedApduCase2Le() {
        byte[] expectedOutput = new byte[]{0, 0, 1, 2};

        Simulator instance = prepareSimulator();

        byte[] apdu = new byte[]{CLA, INS_LEN, P1, P2, 0, 1, 2};
        byte[] result = instance.transmitCommand(apdu);

        ResponseAPDU responseApdu = new ResponseAPDU(result);
        assertEquals(0x9000, responseApdu.getSW());
        assertEquals(Arrays.toString(expectedOutput), Arrays.toString(responseApdu.getData()));
    }


    public void testExtendedApduLcLe() {
        byte[] expectedOutput = {0x0, 0x1, 0x1F, (byte) 0xCA};

        Simulator instance = prepareSimulator();

        byte[] apdu = new byte[]{CLA, INS_LEN, P1, P2, 0, 0, 1, DUMMY, 0x1F, (byte) 0xCA};
        byte[] result = instance.transmitCommand(apdu);

        ResponseAPDU responseApdu = new ResponseAPDU(result);
        assertEquals(0x9000, responseApdu.getSW());
        assertEquals(Arrays.toString(expectedOutput), Arrays.toString(responseApdu.getData()));
    }

    public void testExtendedApduEchoWith1Byte() throws NoSuchAlgorithmException {
        byte[] expectedOutput = {0x41};

        Simulator instance = prepareSimulator();
        byte[] apdu = new byte[]{CLA, INS_ECHO, 0, 0, 0, 0, 1, DUMMY};
        byte[] result = instance.transmitCommand(apdu);

        ResponseAPDU responseApdu = new ResponseAPDU(result);
        assertEquals(0x9000, responseApdu.getSW());
        assertEquals(Arrays.toString(expectedOutput), Arrays.toString(responseApdu.getData()));
    }

    public void testExtendedApduDigest() throws NoSuchAlgorithmException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        byte[] input = new byte[Short.MAX_VALUE];
        Arrays.fill(input, DUMMY);
        byte[] expectedOutput = sha1.digest(input);

        Simulator instance = prepareSimulator();

        ByteBuffer inputApdu = ByteBuffer.wrap(new byte[input.length + 7]);
        inputApdu.put(CLA);
        inputApdu.put(INS_DIGEST);
        inputApdu.put(P1);
        inputApdu.put(P2);
        inputApdu.put((byte) 0).putShort((short) input.length); // Lc
        inputApdu.put(input);

        byte[] result = instance.transmitCommand(inputApdu.array());
        ResponseAPDU responseApdu = new ResponseAPDU(result);
        assertEquals(0x9000, responseApdu.getSW());
        assertEquals(Arrays.toString(expectedOutput), Arrays.toString(responseApdu.getData()));
    }

    public void testExtendedApduEcho() {
        byte[] input = new byte[Short.MAX_VALUE - 2];
        Arrays.fill(input, (byte) 0x41);

        Simulator instance = prepareSimulator();

        ByteBuffer inputApdu = ByteBuffer.wrap(new byte[input.length + 7]);
        inputApdu.put(CLA);
        inputApdu.put(INS_ECHO);
        inputApdu.put(P1);
        inputApdu.put(P2);
        inputApdu.put((byte) 0).putShort((short) input.length); // Lc
        inputApdu.put(input);

        byte[] result = instance.transmitCommand(inputApdu.array());
        ResponseAPDU responseApdu = new ResponseAPDU(result);
        assertEquals(0x9000, responseApdu.getSW());
        assertEquals(Arrays.toString(input), Arrays.toString(responseApdu.getData()));
    }
}
