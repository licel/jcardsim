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
package com.licel.jcardsim.smartcardio;

import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Arrays;
import java.util.List;
import javacard.framework.ISO7816;
import javax.smartcardio.*;
import junit.framework.TestCase;
import org.bouncycastle.util.encoders.Hex;

/**
 * Test Java Card Terminal emulation.
 *
 * @author LICEL LLC
 */
public class JCardSimProviderTest extends TestCase {

    private static final ATR ETALON_ATR = new ATR(Hex.decode(JCSCard.DEFAULT_ATR));
    private static final String TEST_APPLET_AID = "010203040506070809";

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testProvider() throws CardException, NoSuchAlgorithmException {
        System.setProperty("com.licel.jcardsim.smartcardio.applet.0.AID", TEST_APPLET_AID);
        System.setProperty("com.licel.jcardsim.smartcardio.applet.0.Class", "com.licel.jcardsim.samples.HelloWorldApplet");
        if (Security.getProvider("jCardSim") == null) {
            JCardSimProvider provider = new JCardSimProvider();
            Security.addProvider(provider);
        }
        TerminalFactory tf = TerminalFactory.getInstance("jCardSim", null);
        CardTerminals ct = tf.terminals();
        List<CardTerminal> list = ct.list();
        CardTerminal jcsTerminal = null;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName().equals("jCardSim.Terminal")) {
                jcsTerminal = list.get(i);
                break;
            }
        }
        // check terminal exists
        assertTrue(jcsTerminal != null);
        // check if card is present
        assertTrue(jcsTerminal.isCardPresent());
        // check card 
        Card jcsCard = jcsTerminal.connect("T=0");
        assertTrue(jcsCard != null);
        // check card ATR
        assertEquals(jcsCard.getATR(), ETALON_ATR);
        // check card protocol
        assertEquals(jcsCard.getProtocol(), "T=0");
        // get basic channel
        CardChannel jcsChannel = jcsCard.getBasicChannel();
        assertTrue(jcsChannel != null);
        // create applet data = aid len (byte), aid bytes, params lenth (byte), param 
        byte[] aidBytes = Hex.decode(TEST_APPLET_AID);
        byte[] createData = new byte[1+aidBytes.length+1+2+3];
        createData[0] = (byte) aidBytes.length;
        System.arraycopy(aidBytes, 0, createData, 1, aidBytes.length);
        createData[1+aidBytes.length] = (byte) 5;
        createData[2+aidBytes.length] = 0; // aid
        createData[3+aidBytes.length] = 0; // control
        createData[4+aidBytes.length] = 2; // params
        createData[5+aidBytes.length] = 0xF; // params
        createData[6+aidBytes.length] = 0xF; // params
        CommandAPDU createApplet = new CommandAPDU(0x80, 0xb8, 0, 0, createData);
        ResponseAPDU response = jcsChannel.transmit(createApplet);
        assertEquals(response.getSW(), 0x9000);
        assertEquals(true, Arrays.equals(response.getData(), aidBytes));
        // select applet
        CommandAPDU selectApplet = new CommandAPDU(ISO7816.CLA_ISO7816, ISO7816.INS_SELECT, 0, 0, Hex.decode(TEST_APPLET_AID));
        response = jcsChannel.transmit(selectApplet);
        assertEquals(response.getSW(), 0x9000);
        // test NOP
        response = jcsChannel.transmit(new CommandAPDU(0x01, 0x02, 0x00, 0x00));
        assertEquals(0x9000, response.getSW());
        // test SW_INS_NOT_SUPPORTED
        response = jcsChannel.transmit(new CommandAPDU(0x01, 0x05, 0x00, 0x00));
        assertEquals(ISO7816.SW_INS_NOT_SUPPORTED, response.getSW());
        // test hello world from card
        response = jcsChannel.transmit(new CommandAPDU(0x01, 0x01, 0x00, 0x00));
        assertEquals(0x9000, response.getSW());
        assertEquals("Hello world !", new String(response.getData()));
        // test echo
        response = jcsChannel.transmit(new CommandAPDU(0x01, 0x01, 0x01, 0x00, ("Hello javacard world !").getBytes()));
        assertEquals(0x9000, response.getSW());
        assertEquals("Hello javacard world !", new String(response.getData()));
        // test echo v2
        response = jcsChannel.transmit(new CommandAPDU(0x01, 0x03, 0x00, 0x00, ("Hello javacard world !").getBytes()));
        assertEquals(0x9000, response.getSW());
        assertEquals("Hello javacard world !", new String(response.getData()));
        // test echo intall params
        response = jcsChannel.transmit(new CommandAPDU(0x01, 0x04, 0x00, 0x00));
        assertEquals(0x9000, response.getSW());
        assertEquals(0xF, response.getData()[0]);
        assertEquals(0xF, response.getData()[1]);
    }
}
