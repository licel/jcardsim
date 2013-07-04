/*
 * Copyright 2012 Licel LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.licel.jcardsim.samples;

import javacard.framework.*;

/**
 * Basic HelloWorld JavaCard Applet
 * @author LICEL LLC
 */
public class HelloWorldApplet extends Applet {

    /**
     * Instruction: say hello
     */
    private final static byte SAY_HELLO_INS = (byte) 0x01;
    /**
     * Instruction: say echo v2
     */
    private final static byte SAY_ECHO2_INS = (byte) 0x03;
    /**
     * Instruction: get install params
     */
    private final static byte SAY_IPARAMS_INS = (byte) 0x04;    
    /**
     * Instruction: NOP
     */
    private final static byte NOP_INS = (byte) 0x02;
    /**
     * Byte array representing "Hello Java Card world!" string.
     */
    private static byte[] helloMessage = new byte[]{
        0x48, 0x65, 0x6C, 0x6C, 0x6F, 0x20, // "Hello "
        0x77, 0x6F, 0x72, 0x6C, 0x64, 0x20, 0x21 // "world !"
    };

    private byte[] echoBytes;
    private byte[] initParamsBytes;
    private static final short LENGTH_ECHO_BYTES = 256;

    /**
     * Only this class's install method should create the applet object.
     */
    protected HelloWorldApplet(byte[] bArray, short bOffset, byte bLength) {
        echoBytes = new byte[LENGTH_ECHO_BYTES];
        if (bLength > 0) {
            byte iLen = bArray[bOffset]; // aid length
            bOffset = (short) (bOffset + iLen + 1);
            byte cLen = bArray[bOffset]; // info length
            bOffset = (short) (bOffset + cLen + 1);
            byte aLen = bArray[bOffset]; // applet data length
            initParamsBytes = new byte[aLen];
            Util.arrayCopyNonAtomic(bArray, (short) (bOffset + 1), initParamsBytes, (short) 0, aLen);
        }
        register();
    }    

    /**
     * This method is called once during applet instantiation process.
     * @param bArray
     * @param bOffset
     * @param bLength
     * @throws ISOException
     */
    public static void install(byte[] bArray, short bOffset, byte bLength)
            throws ISOException {
        new HelloWorldApplet(bArray, bOffset,bLength);
    }

    /**
     * This method is called each time the applet receives APDU.
     */
    public void process(APDU apdu) {
        // good practice
        if(selectingApplet()) return;
        byte[] buffer = apdu.getBuffer();
        // Now determine the requested instruction:
        switch (buffer[ISO7816.OFFSET_INS]) {
            case SAY_HELLO_INS:
                sayHello(apdu);
                return;
            case SAY_ECHO2_INS:
                sayEcho2(apdu);
                return;
            case SAY_IPARAMS_INS:
                sayIParams(apdu);
                return;
            case NOP_INS:
                return;
            default:
                // We do not support any other INS values
                ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
        }
        return;
    }

    /**
     * Sends hello message to host using given APDU.
     *
     * @param apdu APDU that requested hello message
     */
    private void sayHello(APDU apdu) {
        // Here all bytes of the APDU are stored
        byte[] buffer = apdu.getBuffer();
        // receive all bytes
        // if P1 = 0x01 (echo)
        short incomeBytes = apdu.setIncomingAndReceive();
        byte[] echo;
        if (buffer[ISO7816.OFFSET_P1] == 0x01) {
            echo = JCSystem.makeTransientByteArray(incomeBytes, JCSystem.CLEAR_ON_RESET);
            Util.arrayCopyNonAtomic(buffer, ISO7816.OFFSET_CDATA, echo, (short) 0, incomeBytes);
        } else {
            echo = JCSystem.makeTransientByteArray((short) helloMessage.length, JCSystem.CLEAR_ON_RESET);
            Util.arrayCopyNonAtomic(helloMessage, (short) 0, echo, (short) 0, (short) helloMessage.length);
        }
        // Tell JVM that we will send data
        apdu.setOutgoing();
        // Set the length of data to send
        apdu.setOutgoingLength((short) echo.length);
        // Send our message starting at 0 position
        apdu.sendBytesLong(echo, (short) 0, (short) echo.length);
    }


    /**
     * echo v2
     */
    private void sayEcho2(APDU apdu) {
        byte buffer[] = apdu.getBuffer();

        short bytesRead = apdu.setIncomingAndReceive();
        short echoOffset = (short) 0;

        while (bytesRead > 0) {
            Util.arrayCopyNonAtomic(buffer, ISO7816.OFFSET_CDATA, echoBytes, echoOffset, bytesRead);
            echoOffset += bytesRead;
            bytesRead = apdu.receiveBytes(ISO7816.OFFSET_CDATA);
        }

        apdu.setOutgoing();
        apdu.setOutgoingLength((short) echoOffset);
        // echo data
        apdu.sendBytesLong(echoBytes, (short) 0, echoOffset);

    }

    /**
     * echo install params
     */
    private void sayIParams(APDU apdu) {
        apdu.setOutgoing();
        apdu.setOutgoingLength((short)initParamsBytes.length);
        // echo install parmas
        apdu.sendBytesLong(initParamsBytes, (short) 0, (short)initParamsBytes.length);
    }   

}
