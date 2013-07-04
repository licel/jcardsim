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
 * Test javacard sample (logical channels demo).
 */
public class channelDemoTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of javacard sample (logical channels demo).
     */
    public void testExecuteCommands() throws Exception {

        System.out.println("executeCommands");

        Properties cfg = new Properties();
        cfg.setProperty("com.licel.jcardsim.smartcardio.applet.0.AID", "a00000006203010c0b02");
        cfg.setProperty("com.licel.jcardsim.smartcardio.applet.0.Class", "com.licel.jcardsim.samples.ChannelsDemo.ConnectionManager");
        cfg.setProperty("com.licel.jcardsim.smartcardio.applet.1.AID", "a00000006203010c0b01");
        cfg.setProperty("com.licel.jcardsim.smartcardio.applet.1.Class", "com.licel.jcardsim.samples.ChannelsDemo.AccountAccessor");

        StringBuilder sb = new StringBuilder();
        sb.append("powerup;\n");
        sb.append("// create ConnectionManager\n");
        sb.append("0x80 0xB8 0x00 0x00 0x0c 0x0a 0xa0 0x00 0x00 0x00 0x62 0x03 0x01 0x0c 0x0b 0x02 0x00 0x7F;\n");
        sb.append("// create AccountAccessor\n");
        sb.append("0x80 0xB8 0x00 0x00 0x12 0x0a 0xa0 0x00 0x00 0x00 0x62 0x03 0x01 0x0c 0x0b 0x01 0x08 0x01 0x98 0x00 0x01 0x00 0x05 0x7F;\n");
        sb.append("// Select Account accessor\n");
        sb.append("0x00 0xa4 0x04 0x00 0x0a 0xa0 0x00 0x00 0x00 0x62 0x03 0x01 0x0c 0x0b 0x01 0x7F;\n");
        sb.append("// 90 00 = SW_NO_ERROR\n");
        sb.append("// Initialize Account accessor with 100 credits\n");
        sb.append("0x80 0x20 0x00 0x00 0x02 0x00 0x64 0x7F;\n");
        sb.append("// 90 00 = SW_NO_ERROR\n");
        sb.append("// Assume some time happens, and the user turns the device on to use it...\n");
        sb.append("// Terminal selects Connection Manager upon turing on the device\n");
        sb.append("0x00 0xa4 0x04 0x00 0x0a 0xa0 0x00 0x00 0x00 0x62 0x03 0x01 0x0c 0x0b 0x02 0x7F;\n");
        sb.append("// 90 00 = SW_NO_ERROR\n");
        sb.append("// Upon network carrier detect, terminal decodes area code (408 dec) \n");
        sb.append("// and sends a \"Time tick\" to the card\n");
        sb.append("0x80 0x10 0x00 0x00 0x02 0x01 0x98 0x7F;\n");
        sb.append("// 90 00 = SW_NO_ERROR\n");
        sb.append("// The user starts using the device.  The terminal quieries for available credits.\n");
        sb.append("// First, open a new logical channel, via MANAGE CHANNEL OPENcommand.\n");
        sb.append("0x00 0x70 0x00 0x00 0x00 0x01;\n");
        sb.append("// In order to do so without losing network information (in ConnectionManager \n");
        sb.append("// applet), Account Accessor applet is selected on a second logical channel.\n");
        sb.append("0x01 0xa4 0x04 0x00 0x0a 0xa0 0x00 0x00 0x00 0x62 0x03 0x01 0x0c 0x0b 0x01 0x7F;\n");
        sb.append("// 90 00 = SW_NO_ERROR\n");
        sb.append("// Terminal queries balance on user account.\n");
        sb.append("0x81 0x10 0x00 0x00 0x00 0x02;\n");
        sb.append("// 0x00 0x64 0x90 0x00 = 100 credits and SW_NO_ERROR\n");
        sb.append("// User starts a network service.  For this, sufficient credits shall be available\n");
        sb.append("// on the user account to pay for the first time unot of device use.\n");
        sb.append("0x80 0x20 0x00 0x00 0x00 0x7F;\n");
        sb.append("// 90 00 = SW_NO_ERROR\n");
        sb.append("// Service initiation has a charge of one time unit.  Verified by obtaining balance.\n");
        sb.append("0x81 0x10 0x00 0x00 0x00 0x02;\n");
        sb.append("// 0x00 0x63 0x90 0x00 = SW_NO_ERROR\n");
        sb.append("// Time tick for another time unit\n");
        sb.append("0x80 0x10 0x00 0x00 0x02 0x01 0x98 0x7F;\n");
        sb.append("// 90 00 = SW_NO_ERROR\n");
        sb.append("// Terminal quieries balance. \n");
        sb.append("0x81 0x10 0x00 0x00 0x00 0x02;\n");
        sb.append("// 0x00 0x62 0x90 0x00 = SW_NO_ERROR\n");
        sb.append("// Time tick.  The user has moved to a new area code (650).\n");
        sb.append("0x80 0x10 0x00 0x00 0x02 0x02 0x8A 0x7F;\n");
        sb.append("// 90 00 = SW_NO_ERROR\n");
        sb.append("// Terminal quieries balance. \n");
        sb.append("0x81 0x10 0x00 0x00 0x00 0x02;\n");
        sb.append("// 0x00 0x5D 0x90 0x00 = SW_NO_ERROR\n");
        sb.append("// Time tick.  Area code (650).\n");
        sb.append("0x80 0x10 0x00 0x00 0x02 0x02 0x8A 0x7F;\n");
        sb.append("// 90 00 = SW_NO_ERROR\n");
        sb.append("// Terminal quieries balance. \n");
        sb.append("0x81 0x10 0x00 0x00 0x00 0x02;\n");
        sb.append("// 0x00 0x58 0x90 0x00 = SW_NO_ERROR\n");
        sb.append("// User terminates network service. No charges applied.\n");
        sb.append("0x80 0x30 0x00 0x00 0x00 0x7F;\n");
        sb.append("// 90 00 = SW_NO_ERROR\n");
        sb.append("// Time tick.  Area code (650).\n");
        sb.append("0x80 0x10 0x00 0x00 0x02 0x02 0x8A 0x7F;\n");
        sb.append("// 90 00 = SW_NO_ERROR\n");
        sb.append("// Terminal quieries balance. No change, since device is not in use.\n");
        sb.append("0x81 0x10 0x00 0x00 0x00 0x02;\n");
        sb.append("// 0x00 0x58 0x90 0x00 = SW_NO_ERROR\n");
        sb.append("// The Account manager applet is deselected,\n");
        sb.append("// via a MANAGE CHANNEL CLOSE command.\n");
        sb.append("0x00 0x70 0x80 0x01 0x00 0x7F;\n");
        sb.append("// User powers down the device. \n");
        sb.append("powerdown;\n");
        sb.append("\n");

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
