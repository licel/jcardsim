/*
 * Copyright 2022 Licel Corporation.
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

import com.licel.jcardsim.samples.GlobalArrayClientApplet;
import com.licel.jcardsim.samples.GlobalArrayServerApplet;
import com.licel.jcardsim.utils.AIDUtil;

import org.bouncycastle.util.Arrays;

import javacard.framework.AID;
import javacard.framework.ISO7816;
import javacard.framework.JCSystem;
import javacard.framework.Util;
import junit.framework.TestCase;

public class GlobalArrayTest extends TestCase {
    byte[] serverAppletAIDBytes = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09 };
    byte[] wrongServerAppletAIDBytes = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x00 };
    AID serverAppletAID;

    String clientAppletAIDStr;
    AID clientAppletAID;
    byte[] clientAppletPar = null;

    byte[] bytesForTest = null;
    boolean[] booleansForTest = null;
    short[] shortsForTest = null;

    public GlobalArrayTest(String testName){
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        serverAppletAID = AIDUtil.create(serverAppletAIDBytes);
        clientAppletAIDStr = "090807060504030201";
        clientAppletAID = AIDUtil.create(clientAppletAIDStr);
        clientAppletPar = new byte[1+serverAppletAIDBytes.length];
        clientAppletPar[0] = (byte)serverAppletAIDBytes.length;
        System.arraycopy(serverAppletAIDBytes, 0, clientAppletPar, 1, serverAppletAIDBytes.length);

        bytesForTest = new byte[32];
        for(byte i = 0; i<32 ; i++){
            bytesForTest[i] = i;
        }

        booleansForTest = new boolean[32];
        for(byte i = 0; i<32 ; i++){
            if((i%2) != 0)
                booleansForTest[i] = true;
            else
                booleansForTest[i] = true;
        }

        shortsForTest = new short[32];
        for(byte i = 0; i<32 ; i++){
            shortsForTest[i] = (short)i;
        }


    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        
    }

    /**
     * Test access global byte array by server applet
    * */
    public void testAccessGlobalArrayByteByServerApplet(){
        Simulator instance = new Simulator();

        assertEquals(instance.installApplet(serverAppletAID,GlobalArrayServerApplet.class).equals(serverAppletAID),true);
        assertEquals(instance.selectApplet(serverAppletAID), true);

        // Get global array reference from server applet
        GlobalArrayServerApplet serverApplet = (GlobalArrayServerApplet)JCSystem.getAppletShareableInterfaceObject(serverAppletAID,(byte)0);
        Object globalArray = serverApplet.getGlobalArrayRef();

        // Check global array must be null, because it has not been created yet
        assertNull(globalArray);

        // Send C-APDU to create the byte global array for 32-byte size and filled with 0xCC
        byte[] response1 = instance.transmitCommand(new byte[]{0x10, 0x01, 32, (byte)0xCC});

        // Check command succeeded
        assertEquals(ISO7816.SW_NO_ERROR, Util.getShort(response1, (short) 0));

        // Get global array reference and check not null
        globalArray = serverApplet.getGlobalArrayRef();
        assertNotNull(globalArray);

        // Check global array content
        for( byte i = 0 ; i < 32; i++){
            assertEquals((byte)0xCC,((byte[]) globalArray)[i]);
        }

        // Create C-APDU to write 32 test bytes to global array
        byte[] commandAPDUHeaderWithLc = new byte[]{0x10, 0x02, 0, 0, 32};
        byte[] sendAPDU = new byte[5+32];
        System.arraycopy(commandAPDUHeaderWithLc, 0, sendAPDU, 0, 5);
        System.arraycopy(bytesForTest, 0, sendAPDU, 5, 32);

        // Send C-APDU
        byte[] response2 = instance.transmitCommand(sendAPDU);
        // Check command succeeded
        assertEquals(ISO7816.SW_NO_ERROR, Util.getShort(response2, (short) 0));
        // Check the global array with the writen data
        assertEquals(Arrays.areEqual(bytesForTest, (byte[]) globalArray), true);

    }

    /**
     * Test access the global byte array with the client applet
    * */
    public void testAccessGlobalArrayByteByClientApplet(){
        Simulator instance = new Simulator();

        // Install server and client applet
        assertEquals(instance.installApplet(serverAppletAID,GlobalArrayServerApplet.class).equals(serverAppletAID),true);
        assertEquals(instance.installApplet(clientAppletAID,GlobalArrayClientApplet.class,clientAppletPar,(short)0,(byte)clientAppletPar.length).equals(clientAppletAID),true);

        // Select server applet
        assertEquals(instance.selectApplet(serverAppletAID),true);

        // Send C-APDU to create the byte global array for 32-byte size and filled with 0x5A
        byte[] response1 = instance.transmitCommand(new byte[]{0x10, 0x01, 32, (byte) 0x5A});

        // Check command succeeded
        assertEquals(ISO7816.SW_NO_ERROR, Util.getShort(response1, (short) 0));

        // Select client applet
        assertEquals(instance.selectApplet(clientAppletAID),true);
        // Send C-APDU to read the global byte array for 32 bytes
        byte[] response2 = instance.transmitCommand(new byte[]{0x10, 0x01, 0x00, 0x00, 32});
        // Check command succeeded
        assertEquals(ISO7816.SW_NO_ERROR, Util.getShort(response2, (short) 32));

        // Check global array content
        for( byte i = 0 ; i < 32; i++){
            assertEquals((byte)0x5A,((byte[]) response2)[i]);
        }

         // Create C-APDU to write 32 test bytes to global array
         byte[] commandAPDUHeaderWithLc = new byte[]{0x10, 0x02, 0, 0, 32};
         byte[] sendAPDU = new byte[5+32];
         System.arraycopy(commandAPDUHeaderWithLc, 0, sendAPDU, 0, 5);
         System.arraycopy(bytesForTest, 0, sendAPDU, 5, 32);
 
         // Send C-APDU
         byte[] response3 = instance.transmitCommand(sendAPDU);
         // Check command succeeded
         assertEquals(ISO7816.SW_NO_ERROR, Util.getShort(response3, (short) 0));

        // Send C-APDU to read the global byte array for 32 bytes
        byte[] response4 = instance.transmitCommand(new byte[]{0x10, 0x01, 0x00, 0x00, 32});
        // Check command succeeded
        assertEquals(ISO7816.SW_NO_ERROR, Util.getShort(response4, (short) 32));

         // Check the global array with the writen data
        byte[] globalArrayBytes = new byte[32];
        System.arraycopy(response4, 0, globalArrayBytes, 0, globalArrayBytes.length);
        assertEquals(Arrays.areEqual(bytesForTest, (byte[]) globalArrayBytes), true);
    }
    
}
