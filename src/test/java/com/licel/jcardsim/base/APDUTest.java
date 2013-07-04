/*
 * Copyright 2013 Licel LLC.
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
package com.licel.jcardsim.base;

import com.licel.jcardsim.samples.HelloWorldApplet;
import java.util.Arrays;
import javacard.framework.AID;
import javacard.framework.ISO7816;
import javacard.framework.JCSystem;
import javacard.security.RandomData;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import junit.framework.TestCase;

/**
 * Test APDU engine for long data transmission.
 */
public class APDUTest extends TestCase {

    public APDUTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of applet which receives/returns more than 128 bytes of data.
     */
    public void testAPDUApplet() {
        Simulator simulator = new Simulator();
        simulator.resetRuntime();
        byte[] appletAIDBytes = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        AID appletAID = new AID(appletAIDBytes, (short) 0, (byte) appletAIDBytes.length);
        simulator.installApplet(appletAID, HelloWorldApplet.class);
        simulator.selectApplet(appletAID);

        byte[] apduData = JCSystem.makeTransientByteArray((short) 255, JCSystem.CLEAR_ON_RESET);
        RandomData rnd = RandomData.getInstance(RandomData.ALG_PSEUDO_RANDOM);
        rnd.generateData(apduData, (short) 0, (short) apduData.length);

        CommandAPDU echoCmd = new CommandAPDU(0x01, 0x03, 0x00, 0x00, apduData);
        ResponseAPDU response = simulator.transmitCommand(echoCmd);
        assertEquals(0x9000, response.getSW());
        assertEquals(true, Arrays.equals(apduData, response.getData()));
    }
}
