/*
 * Copyright 2011 Licel LLC.
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
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import junit.framework.TestCase;

/**
 * Test class Simulator.
 */
public class SimulatorTest extends TestCase {

    public SimulatorTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of installApplet method, of class Simulator.
     */
    public void testApplet() {
        Simulator simulator = new Simulator();
        simulator.resetRuntime();
        byte[] appletAIDBytes = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
        AID appletAID = new AID(appletAIDBytes, (short) 0, (byte) appletAIDBytes.length);
        simulator.installApplet(appletAID, HelloWorldApplet.class);
        simulator.selectApplet(appletAID);
        // test NOP
        ResponseAPDU response = simulator.transmitCommand(new CommandAPDU(0x01, 0x02, 0x00, 0x00));
        assertEquals(0x9000, response.getSW());
        // test SW_INS_NOT_SUPPORTED
        response = simulator.transmitCommand(new CommandAPDU(0x01, 0x05, 0x00, 0x00));
        assertEquals(ISO7816.SW_INS_NOT_SUPPORTED, response.getSW());
        // test hello world from card
        response = simulator.transmitCommand(new CommandAPDU(0x01, 0x01, 0x00, 0x00));
        assertEquals(0x9000, response.getSW());
        assertEquals("Hello world !", new String(response.getData()));
        // test echo
        response = simulator.transmitCommand(new CommandAPDU(0x01, 0x01, 0x01, 0x00, ("Hello javacard world !").getBytes()));
        assertEquals(0x9000, response.getSW());
        assertEquals("Hello javacard world !", new String(response.getData()));
        // test echo v2
        response = simulator.transmitCommand(new CommandAPDU(0x01, 0x03, 0x00, 0x00, ("Hello javacard world !").getBytes()));
        assertEquals(0x9000, response.getSW());
        assertEquals("Hello javacard world !", new String(response.getData()));
    }
}
