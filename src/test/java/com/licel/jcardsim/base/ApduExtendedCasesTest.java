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

import com.licel.jcardsim.samples.ApduExtendedCasesApplet;
import com.licel.jcardsim.utils.AIDUtil;
import javacard.framework.AID;
import javacard.framework.ISO7816;
import junit.framework.TestCase;
import org.bouncycastle.util.Arrays;
import javax.smartcardio.ResponseAPDU;


public class ApduExtendedCasesTest extends TestCase {

    private static final byte CLA = (byte) 0x80;
    private static final byte INS = (byte)0xb4;
    private static final byte P1 = (byte) 0;
    private static final byte P2 = (byte) 0;
    byte[] appletAIDBytes = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09 };
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testApduCase2_Request256BytesWithLeZeroValue(){
        Simulator instance = getReadySimulator();

        byte Le = 0;
        byte[] apdu = new byte[]{CLA, INS, P1, P2, Le};
        byte[] response = instance.transmitCommand(apdu);
        ResponseAPDU responseApdu = new ResponseAPDU(response);
        assertEquals(ISO7816.SW_NO_ERROR,(short) responseApdu.getSW() );

        // Check content
        for( short i = 0 ; i < 256; i++){
            assertEquals((byte)0x5a,response[i]);
        }

    }

    public void testApduCase2E_Request256BytesWith3ByteLe(){
        Simulator instance = getReadySimulator();

        //  Le = 0x00, 0x01,0x00 -> 256
        byte[] apdu = new byte[]{CLA, INS, P1, P2, 0x00, 0x01,0x00};
        byte[] response = instance.transmitCommand(apdu);
        ResponseAPDU responseApdu = new ResponseAPDU(response);
        assertEquals(ISO7816.SW_NO_ERROR,(short) responseApdu.getSW() );

        // Check content
        for( short i = 0 ; i < 256; i++){
            assertEquals((byte)0x5a,response[i]);
        }
    }

    public void testApduCase3E_Send256Bytes(){
        Simulator instance = getReadySimulator();

        //  Lc = 0x00, 0x01,0x00 -> 256
        byte[] apduHeader_Case3E =new byte[]{CLA, INS, P1, P2, 0x00, 0x01, 0x00};
        byte[] data = new byte[256];
        Arrays.fill(data, (byte) 0x5a);

        byte[] apdu = new byte[apduHeader_Case3E.length + data.length];

        System.arraycopy(apduHeader_Case3E, 0, apdu, 0, apduHeader_Case3E.length);
        System.arraycopy(data, 0, apdu, apduHeader_Case3E.length, data.length);

        byte[] response = instance.transmitCommand(apdu);
        ResponseAPDU responseApdu = new ResponseAPDU(response);
        assertEquals(ISO7816.SW_NO_ERROR,(short) responseApdu.getSW() );
    }


    public void testApduCase4_Request256BytesWithLeZeroValue(){
        Simulator instance = getReadySimulator();

        byte Lc = 0x01;
        byte CData = 0;
        byte Le = 0;
        byte[] apdu = new byte[]{CLA, INS, P1, P2, Lc, CData, Le};
        byte[] response = instance.transmitCommand(apdu);
        ResponseAPDU responseApdu = new ResponseAPDU(response);
        assertEquals(ISO7816.SW_NO_ERROR,(short) responseApdu.getSW() );

        // Check content
        for( short i = 0 ; i < 256; i++){
            assertEquals((byte)0x5a,response[i]);
        }

    }

    public void testApduCase4E_Send256BytesAndRequest256Bytes() {
        Simulator instance = getReadySimulator();
        // Lc = 0x00, 0x01, 0x00 -> 256
        byte[] Lc = new byte[]{0x00, 0x01, 0x00};
        byte[] data = new byte[256];
        Arrays.fill(data, (byte) 0x5a);

        // Le = 0x01, 0x00 -> Le contains only 2 bytes with the valid data, without the first zero byte.
        byte[] Le = new byte[]{0x01, 0x00};

        byte[] apduHeader =new byte[]{CLA, INS, P1, P2};

        byte[] apduCase4E = new byte[apduHeader.length + Lc.length + data.length + Le.length];

        System.arraycopy(apduHeader,0, apduCase4E, 0, apduHeader.length);
        System.arraycopy(Lc,0,apduCase4E,apduHeader.length,Lc.length);
        System.arraycopy(data,0,apduCase4E, apduHeader.length + Lc.length, data.length);
        System.arraycopy(Le, 0, apduCase4E, apduHeader.length + Lc.length + data.length, Le.length);

        byte[] response = instance.transmitCommand(apduCase4E);

        ResponseAPDU responseApdu = new ResponseAPDU(response);
        assertEquals(ISO7816.SW_NO_ERROR, (short)responseApdu.getSW());

        // Check content
        for( short i = 0 ; i < 256; i++){
            assertEquals((byte)0x5a,response[i]);
        }

    }
        private Simulator getReadySimulator() {
        Simulator instance = new Simulator();
        AID appletAID = AIDUtil.create(appletAIDBytes);

        instance.installApplet(appletAID, ApduExtendedCasesApplet.class);
        instance.selectApplet(appletAID);
        return instance;
    }
}
