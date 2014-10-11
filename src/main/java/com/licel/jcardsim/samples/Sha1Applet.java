package com.licel.jcardsim.samples;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISO7816;
import javacard.framework.ISOException;
import javacard.framework.JCSystem;
import javacard.framework.Util;
import javacard.security.MessageDigest;
import javacardx.apdu.ExtendedLength;


public class Sha1Applet extends BaseApplet implements ExtendedLength {
    public static final byte INS_DIGEST = 0;
    public static final byte INS_ECHO = 2;
    public static final byte INS_LEN = 4;
    public static final byte CLA = (byte) 0x90;
    private static final byte CLA_MASK = (byte) 0xF0;

    private MessageDigest digest;

    public static void install(byte[] bArray, short bOffset, byte bLength) {
        new Sha1Applet().register();
    }

    protected Sha1Applet() {
        digest = MessageDigest.getInstance(MessageDigest.ALG_SHA, false);
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
                short len = digest.doFinal(buffer, offsetCData, lc, buffer, (short)0);
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
            default:
                ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
        }
    }
}
