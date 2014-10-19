package com.licel.jcardsim.samples;

import javacard.framework.*;
import javacard.security.MessageDigest;
import javacardx.apdu.ExtendedLength;


/**
 * Applet for calculating SHA1 digests
 *
 * Supported APDUs:
 *
 * <ul>
 *     <code>CLA=0x80 INS=0</code> digest of <code>CData</code>
 *     <code>CLA=0x80 INS=2</code> echo input
 *     <code>CLA=0x80 INS=4</code> echo value of <code>Le</code>
 *     <code>CLA=0x80 INS=8</code> return last digest
 * </ul>
 */
public class Sha1Applet extends BaseApplet implements ExtendedLength {
    private static final byte CLA = (byte) 0x80;
    private static final byte INS_DIGEST = 0;
    private static final byte INS_ECHO = 2;
    private static final byte INS_LEN = 4;
    private static final byte INS_LAST_DIGEST = 6;

    private static final byte CLA_MASK = (byte) 0xF0;

    private MessageDigest digest;
    private byte[] lastDigest;

    public static void install(byte[] bArray, short bOffset, byte bLength) {
        new Sha1Applet().register();
    }

    protected Sha1Applet() {
        digest = MessageDigest.getInstance(MessageDigest.ALG_SHA, false);
        lastDigest = JCSystem.makeTransientByteArray(digest.getLength(), JCSystem.CLEAR_ON_DESELECT);
    }

    public void process(APDU apdu) {
        byte[] buffer = apdu.getBuffer();

        final short readCount = apdu.setIncomingAndReceive();
        final short lc = apdu.getIncomingLength();
        final short offsetCData = apdu.getOffsetCdata();
        short read = readCount;
        while(read < lc) {
            read += apdu.receiveBytes(read);
        }

        if (selectingApplet()) {
            return;
        }

        if ((buffer[ISO7816.OFFSET_CLA] & CLA_MASK) != CLA) {
            ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
        }

        switch (buffer[ISO7816.OFFSET_INS]) {
            case INS_DIGEST:
                short len = digest.doFinal(buffer, offsetCData, lc, lastDigest, (short)0);
                Util.arrayCopy(lastDigest, (short)0, buffer, (short) 0, len);
                apdu.setOutgoingAndSend((short)0, len);
                break;
            case INS_ECHO: {
                apdu.setOutgoingAndSend(offsetCData, lc);
                break;
            }
            case INS_LEN: {
                short le = apdu.setOutgoing();
                apdu.setOutgoingLength((short)4);

                Util.setShort(buffer, (short)0, lc);
                Util.setShort(buffer, (short)2, le);

                apdu.sendBytes((short)0, (short)4);
                break;
            }
            case INS_LAST_DIGEST: {
                Util.arrayCopy(lastDigest, (short)0, buffer, (short) 0, (short) lastDigest.length);
                apdu.setOutgoingAndSend((short)0, (short) lastDigest.length);
                break;
            }
            default:
                ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
        }
    }
}
