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
package com.licel.jcardsim.utils;

import com.licel.jcardsim.base.SimulatorSystem;
import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;

/**
 * Test class APDUScriptTool.
 * @author LICEL LLC
 */
public class APDUScriptToolTest extends TestCase {

    private static final String TEST_APPLET_AID = "010203040506070809";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        System.clearProperty("com.licel.jcardsim.smartcardio.applet.0.AID");
        System.clearProperty("com.licel.jcardsim.smartcardio.applet.0.Class");
    }

    /**
     * Test of executeCommands method, of class APDUScriptTool.
     */
    public void testExecuteCommands() throws Exception {
        System.out.println("executeCommands");
        Properties cfg = new Properties();
        cfg.setProperty("com.licel.jcardsim.smartcardio.applet.0.AID", TEST_APPLET_AID);
        cfg.setProperty("com.licel.jcardsim.smartcardio.applet.0.Class", "com.licel.jcardsim.samples.HelloWorldApplet");
        StringBuilder sb = new StringBuilder();
        sb.append("powerup;\n");
        sb.append("//CREATE APPLET CMD\n");
        sb.append("0x80 0xb8 0x00 0x00 0x10 0x9 0x01 0x02 0x03 0x04 0x05 0x06 0x07 0x8 0x09 0x05 0x00 0x00 0x02 0xF 0xF 0x7f;\n");
        sb.append("// SELECT APPLET CMD\n");
        sb.append("0x00 0xa4 0x04 0x00 0x09 0x01 0x02 0x03 0x04 0x05 0x06 0x07 0x8 0x09 0x7f;\n");
        sb.append("// TEST NOP\n");
        sb.append("0x00 0x02 0x00 0x00 0x00 0x2; \n");
        sb.append("// TEST SW_INS_NOT_SUPPORTED\n");
        sb.append("0x00 0x05 0x00 0x00 0x00 0x2 ;\n");
        sb.append("// test hello world from card\n");
        sb.append("0x00 0x01 0x00 0x00 0x00 0x0d;\n");
        sb.append("// test echo\n");
        sb.append("0x00 0x01 0x01 0x00 0x0d 0x48 0x65 0x6c 0x6c 0x6f 0x20 0x77 0x6f 0x72 0x6c 0x64 0x20 0x21 0x0d;\n");
        sb.append("// test echo2\n");
        sb.append("0x00 0x03 0x01 0x02 0x05 0x01 0x02 0x03 0x04 0x05 0x7F;");
        sb.append("powerdown;\n");
        InputStream commandsStream = new ByteArrayInputStream(sb.toString().replaceAll("\n", System.getProperty("line.separator")).getBytes());     
        boolean isException = true;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            APDUScriptTool.executeCommands(cfg, commandsStream, new PrintStream(baos));
            isException = false;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        finally {
            commandsStream.close();
        }
        assertEquals(isException, false);

        String expectedOutput =
        "CLA: 80, INS: b8, P1: 00, P2: 00, Lc: 10, 09, 01, 02, 03, 04, 05, 06, 07, 08, 09, 05, 00, 00, 02, 0f, 0f, Le: 09, 01, 02, 03, 04, 05, 06, 07, 08, 09, SW1: 90, SW2: 00\n" +
        "CLA: 00, INS: a4, P1: 04, P2: 00, Lc: 09, 01, 02, 03, 04, 05, 06, 07, 08, 09, Le: 00, SW1: 90, SW2: 00\n" +
        "CLA: 00, INS: 02, P1: 00, P2: 00, Lc: 00, Le: 00, SW1: 90, SW2: 00\n" +
        "CLA: 00, INS: 05, P1: 00, P2: 00, Lc: 00, Le: 00, SW1: 6d, SW2: 00\n" +
        "CLA: 00, INS: 01, P1: 00, P2: 00, Lc: 00, Le: 0d, 48, 65, 6c, 6c, 6f, 20, 77, 6f, 72, 6c, 64, 20, 21, SW1: 90, SW2: 00\n" +
        "CLA: 00, INS: 01, P1: 01, P2: 00, Lc: 0d, 48, 65, 6c, 6c, 6f, 20, 77, 6f, 72, 6c, 64, 20, 21, Le: 0d, 48, 65, 6c, 6c, 6f, 20, 77, 6f, 72, 6c, 64, 20, 21, SW1: 90, SW2: 00\n" +
        "CLA: 00, INS: 03, P1: 01, P2: 02, Lc: 05, 01, 02, 03, 04, 05, Le: 05, 01, 02, 03, 04, 05, SW1: 90, SW2: 00\n";

        String output = baos.toString("UTF-8");
        System.out.print(output);
        assertEquals(expectedOutput, output.replace("\r\n","\n"));
    }
}
