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
package com.licel.jcardsim.samples;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;
import junit.framework.TestCase;
import com.licel.jcardsim.base.SimulatorSystem;
import com.licel.jcardsim.utils.APDUScriptTool;

/**
 * Test javacard sample (demo3).
 */
public class demo3Test extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of javacard sample (demo3).
     */
    public void testExecuteCommands() throws Exception {

        System.out.println("executeCommands");

        Properties cfg = new Properties();
        cfg.setProperty("com.licel.jcardsim.smartcardio.applet.0.AID", "a00000006203010c0201");
        cfg.setProperty("com.licel.jcardsim.smartcardio.applet.0.Class", "com.licel.jcardsim.samples.JavaPurse.JavaPurse");

        StringBuilder sb = new StringBuilder();
        sb.append("powerup;\n");
        sb.append("// create JavaPurse\n");
        sb.append("0x80 0xB8 0 0 0x0c 0x0a 0xa0 0 0 0 0x62 0x03 0x01 0x0c 2 0x01 0 0x7F;\n");
        sb.append("/////////////////////////////////////////////////////////////////////\n");
        sb.append("// Initialize JavaPurse\n");
        sb.append("/////////////////////////////////////////////////////////////////////\n");
        sb.append("// Select JavaPurse\n");
        sb.append("0x00 0xa4 0x04 0x00 10 0xa0 0 0 0 0x62 3 1 0xc 2 1 127;\n");
        sb.append("// 90 00 = SW_NO_ERROR\n");
        sb.append("// Initialize Parameter Update\n");
        sb.append("0x80 0x24 0x00 0x00 0x00 0x7F; \n");
        sb.append("//00 00 00 00 0c 1f 63 00 01 90 00 = Purse ID : 0x00000000; ExpDate 12/31/99; PUN 1\n");
        sb.append("//For the second and consecutive runs it can be 69 82\n");
        sb.append("// Complete Parameter Update: CAD ID 0x11223344; Set Master PIN 12345678\n");
        sb.append("0x80 0x26 0x00 0x00 0x1A 0x11 0x22 0x33 0x44 0x00 0x00 0x00 0x00 0xC1 0x08 0x01 0x02 0x03 0x04 0x05 0x06 0x07 0x08 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x7F;\n");
        sb.append("// 00 00 00 00 00 00 00 00 90 00 \n");
        sb.append("// For second and consecutive runs it can be 91 04\n");
        sb.append("// Verify PIN : Master PIN\n");
        sb.append("0x00 0x20 0x00 0x81 0x08 0x01 0x02 0x03 0x04 0x05 0x06 0x07 0x08 0x7F;\n");
        sb.append("// 90 00;\n");
        sb.append("// Initialize Parameter Update\n");
        sb.append("0x80 0x24 0x00 0x00 0x00 0x7F;\n");
        sb.append("// 00 00 00 00 0c 1f 63 00 02 90 00 = Purse ID : 0x00000000; ExpDate 12/31/99; PUN 2\n");
        sb.append("// Complete Parameter Update: CAD ID 0x11223344; Set User PIN 1234\n");
        sb.append("0x80 0x26 0x00 0x00 0x16 0x11 0x22 0x33 0x44 0x00 0x00 0x00 0x00 0xC2 0x04 0x01 0x02 0x03 0x04 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x7F;\n");
        sb.append("// 00 00 00 00 00 00 00 00 90 00;\n");
        sb.append("// Initialize Parameter Update\n");
        sb.append("0x80 0x24 0x00 0x00 0x00 0x7F;\n");
        sb.append("// 00 00 00 00 0c 1f 63 00 03 90 00 = Purse ID : 0x00000000; ExpDate 12/31/99; PUN 3\n");
        sb.append("// Complete Parameter Update: CAD ID 0x11223344; Set ExpDate 12/31/98\n");
        sb.append("0x80 0x26 0x00 0x00 0x15 0x11 0x22 0x33 0x44 0x00 0x00 0x00 0x00 0xC5 0x03 0x0c 0x1f 0x62 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x7F;\n");
        sb.append("// 00 00 00 00 00 00 00 00 90 00;\n");
        sb.append("// Initialize Parameter Update\n");
        sb.append("0x80 0x24 0x00 0x00 0x00 0x7F;\n");
        sb.append("// 00 00 00 00 0c 1f 62 00 04 90 00 = Purse ID : 0x00000000; ExpDate 12/31/98; PUN 4\n");
        sb.append("// Complete Parameter Update: CAD ID 0x11223344; Set Purse ID 0x05050505\n");
        sb.append("0x80 0x26 0x00 0x00 0x16 0x11 0x22 0x33 0x44 0x00 0x00 0x00 0x00 0xC6 0x04 0x05 0x05 0x05 0x05 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x7F;\n");
        sb.append("// 00 00 00 00 00 00 00 00 90 00;\n");
        sb.append("// Initialize Parameter Update\n");
        sb.append("0x80 0x24 0x00 0x00 0x00 0x7F;\n");
        sb.append("// 05 05 05 05 0c 1f 62 00 05 90 00 = Purse ID : 0x05050505; ExpDate 12/31/98; PUN 5\n");
        sb.append("// Complete Parameter Update: CAD ID 0x11223344; Set Max Balance $320.00;\n");
        sb.append("0x80 0x26 0x00 0x00 0x14 0x11 0x22 0x33 0x44 0x00 0x00 0x00 0x00 0xC7 0x02 0x7D 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x7F;\n");
        sb.append("// 00 00 00 00 00 00 00 00 90 00;\n");
        sb.append("// Initialize Parameter Update\n");
        sb.append("0x80 0x24 0x00 0x00 0x00 0x7F;\n");
        sb.append("// 05 05 05 05 0c 1f 62 00 06 90 00 = Purse ID : 0x05050505; ExpDate 12/31/98; PUN 6\n");
        sb.append("// Complete Parameter Update: CAD ID 0x11223344; Set Max Transaction $30.00;\n");
        sb.append("0x80 0x26 0x00 0x00 0x14 0x11 0x22 0x33 0x44 0x00 0x00 0x00 0x00 0xC8 0x02 0x0B 0xB8 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x7F;\n");
        sb.append("// 00 00 00 00 00 00 00 00 90 00;\n");
        sb.append("// Initialize Parameter Update\n");
        sb.append("0x80 0x24 0x00 0x00 0x00 0x7F;\n");
        sb.append("// 05 05 05 05 0c 1f 62 00 07 90 00 = Purse ID : 0x05050505; ExpDate 12/31/98; PUN 7\n");
        sb.append("// Complete Parameter Update: CAD ID 0x11223344; Set Java Purse Version 2.1.0.1\n");
        sb.append("0x80 0x26 0x00 0x00 0x16 0x11 0x22 0x33 0x44 0x00 0x00 0x00 0x00 0xC9 0x04 0x02 0x01 0x00 0x01 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x7F;\n");
        sb.append("// 00 00 00 00 00 00 00 00 90 00;\n");
        sb.append("// Initialize Parameter Update\n");
        sb.append("0x80 0x24 0x00 0x00 0x00 0x7F;\n");
        sb.append("// 05 05 05 05 0c 1f 62 00 08 90 00 = Purse ID : 0x05050505; ExpDate 12/31/98; PUN 8\n");
        sb.append("// Complete Parameter Update: CAD ID 0x11223344; Loyalty1 = \"0xa0,00,00,00,62,03,01,0c,05,01 \"\n");
        sb.append("0x80 0x26 0x00 0x00 0x1E 0x11 0x22 0x33 0x44 0x00 0x00 0x00 0x00 0xCA 0x0C 0x33 0x55 0xA0 0x00 0x00 0x00 0x62 0x03 0x01 0x0C 0x05 0x01 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x7F;\n");
        sb.append("// 00 00 00 00 00 00 00 00 90 00;\n");
        sb.append("//////////////////////////////////////////////////////////////////////\n");
        sb.append("// End of initialization session, all values are set up.\n");
        sb.append("//////////////////////////////////////////////////////////////////////\n");
        sb.append("//////////////////////////////////////////////////////////////////////\n");
        sb.append("// Regular  transaction session	 at CAD 22446688 in the Bank\n");
        sb.append("//////////////////////////////////////////////////////////////////////\n");
        sb.append("// Select JavaPurse\n");
        sb.append("0x00 0xa4 0x04 0x00 10 0xa0 0 0 0 0x62 3 1 0xc 2 1 127;\n");
        sb.append("// 90 00 = SW_NO_ERROR\n");
        sb.append("// Verify PIN (User PIN 01020304)\n");
        sb.append("0x00 0x20 0x00 0x82 0x04 0x01 0x02 0x03 0x04 0x00;\n");
        sb.append("// 90 00;\n");
        sb.append("// Initialize Transaction: Credit $250.00 \n");
        sb.append("0x80 0x20 0x01 0x00 0x0a 0x61 0xa8 0x22 0x44 0x66 0x88 0x00 0x00 0x00 0x00 0x7F;\n");
        sb.append("// 05 05 05 05 0c 1f 62 00 00 00 07 00 00 00 00 00 00 00 00 90 00 \n");
        sb.append("//= Purse ID : 0x05050505; ExpDate 12/31/98; TN=7\n");
        sb.append("// Complete Transaction: Date 10/27/97; Time 15:33\n");
        sb.append("0x80 0x22 0x00 0x00 0x0d 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x0a 0x1b 0x61 0x0f 0x21 0x7F;\n");
        sb.append("// 61 a8 00 00 00 00 00 00 00 00 90 00	= Purse Balance $250.00;\n");
        sb.append("// Initialize Transaction: Debit $25.00;\n");
        sb.append("0x80 0x20 0x02 0x00 0x0a 0x09 0xc4 0x22 0x44 0x66 0x88 0x00 0x00 0x00 0x00 0x7F;\n");
        sb.append("// 05 05 05 05 0c 1f 62 61 a8 00 08 00 00 00 00 00 00 00 00 90 00;\n");
        sb.append("//= Purse ID : 0x05050505; ExpDate 12/31/98; TN=8\n");
        sb.append("// Complete Transaction: Date 10/27/97; Time 15:35\n");
        sb.append("0x80 0x22 0x00 0x00 0x0d 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x0a 0x1b 0x61 0x0f 0x23 0x7F;\n");
        sb.append("// 57 e4 00 00 00 00 00 00 00 00 90 00	= Purse Balance $225.00;\n");
        sb.append("/////////////////////////////////////////////////////////////////////\n");
        sb.append("// Regular  transaction session	 at CAD 33557799 in a store\n");
        sb.append("/////////////////////////////////////////////////////////////////////\n");
        sb.append("// Select JavaPurse\n");
        sb.append("0x00 0xa4 0x04 0x00 10 0xa0 0 0 0 0x62 3 1 0xc 2 1 127;\n");
        sb.append("// 90 00 = SW_NO_ERROR\n");
        sb.append("// Verify PIN (User PIN 01020304)\n");
        sb.append("0x00 0x20 0x00 0x82 0x04 0x01 0x02 0x03 0x04 0x00;\n");
        sb.append("// 90 00;\n");
        sb.append("// Initialize Transaction: Debit $22.95\n");
        sb.append("0x80 0x20 0x02 0x00 0x0a 0x08 0xf7 0x33 0x55 0x77 0x99 0x00 0x00 0x00 0x00 0x7F;\n");
        sb.append("// 05 05 05 05 0c 1f 62 57 e4 00 09 00 00 00 00 00 00 00 00 90 00;\n");
        sb.append("//= Purse ID : 0x05050505; ExpDate 12/31/98; TN=9\n");
        sb.append("// Complete Transaction: Date 10/27/97; Time 17:45\n");
        sb.append("0x80 0x22 0x00 0x00 0x0d 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x0a 0x1b 0x61 0x11 0x2d 0x7F;\n");
        sb.append("// 4e ed 00 00 00 00 00 00 00 00 90 00	= Purse Balance $202.05\n");
        sb.append("/////////////////////////////////////////////////////////////////////\n");
        sb.append("// Session of reading balance and log at CAD 22446688 in the Bank\n");
        sb.append("/////////////////////////////////////////////////////////////////////\n");
        sb.append("// Select JavaPurse\n");
        sb.append("0x00 0xa4 0x04 0x00 10 0xa0 0 0 0 0x62 3 1 0xc 2 1 127;\n");
        sb.append("// 90 00 = SW_NO_ERROR\n");
        sb.append("// Verify PIN (User PIN 01020304)\n");
        sb.append("0x00 0x20 0x00 0x82 0x04 0x01 0x02 0x03 0x04 0x00;\n");
        sb.append("// 90 00;\n");
        sb.append("// Read the only record in Balances file : \n");
        sb.append("// SFI = 4 (00100), record N is specified in P1 => P2 = 00100100 = 0x24\n");
        sb.append("0x00 0xb2 0x01 0x24 0x00 0x7F;\n");
        sb.append("// 4e ed 7d 00 4e 20 90 00 = Balance = $202.05, Max Balance = $320.00, Max Transaction = $200;\n");
        sb.append("// Read the first record in log file\n");
        sb.append("// SFI = 3 (00011), record N is specified in P1 => P2 = 00011100 = 0x1c\n");
        sb.append("0x00 0xb2 0x01 0x1c 0x00 0x7F;\n");
        sb.append("// 00 09 02 08 f7 33 55 77 99 0a 1b 61 11 2d 4e ed 90 00 90 00 \n");
        sb.append("// TN = 9; Transaction Type = DEBIT(02); Amount = $22.95(08f7); CAD ID 33557799;\n");
        sb.append("// Date 10/27/97 (0a 1b 61); Time 17:45(11 2d); Balance $202.05 (4b 13), SW = NO_ERROR (9000)\n");
        sb.append("// Initialize transaction: Debit $9.86\n");
        sb.append("0x80 0x20 0x02 0x00 0x0a 0x03 0xda 0x33 0x44 0x55 0x66 0x00 0x00 0x00 0x00 0x7F;\n");
        sb.append("// 05 05 05 05 0c 1f 62 4e ed 00 0a 00 00 00 00 00 00 00 00 90 00;\n");
        sb.append("// Complete Transaction: Date 10/28/97; Time 18:53\n");
        sb.append("0x80 0x22 0x00 0x00 0x0d 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x0a 0x1c 0x61 0x12 0x35 0x7F;\n");
        sb.append("// 4b 13 00 00 00 00 00 00 00 00 90 00 = Balance = $192.19;\n");
        sb.append("// Initialize transaction: Debit $192.19\n");
        sb.append("0x80 0x20 0x02 0x00 0x0a 0x4b 0x13 0x33 0x44 0x55 0x66 0x00 0x00 0x00 0x00 0x7F;\n");
        sb.append("// 05 05 05 05 0c 1f 62 4b 13 00 0b 00 00 00 00 00 00 00 00 90 00;\n");
        sb.append("// Complete Transaction: Date 10/28/97; Time 18:53\n");
        sb.append("0x80 0x22 0x00 0x00 0x0d 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x0a 0x1c 0x61 0x12 0x35 0x7F;\n");
        sb.append("// 00 00 00 00 00 00 00 00 00 00 90 00 = Balance = $0;\n");
        sb.append("powerdown;\n");

        InputStream commandsStream = new ByteArrayInputStream(sb.toString().replaceAll("\n", System.getProperty("line.separator")).getBytes());     
        boolean isException = true;
        try {
            SimulatorSystem.resetRuntime();
            APDUScriptTool.executeCommands(cfg, commandsStream, null);
            isException = false;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        assertEquals(isException, false);
        commandsStream.close();
    }
}
