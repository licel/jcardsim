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
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import javacard.framework.AID;
import junit.framework.TestCase;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

/**
 *
 */
public class SimulatorTest extends TestCase {
    
    private static final byte[] ETALON_ATR = Hex.decode("3BFA1800008131FE454A434F5033315632333298");
    private static final byte[] TEST_APPLET_AID_BYTES = Hex.decode("010203040506070809");
    private static final Class TEST_APPLET_CLASS = HelloWorldApplet.class;
    private static final String TEST_APPLET_CLASSNAME = "com.licel.jcardsim.samples.HelloWorldApplet";
    private static final AID TEST_APPLET_AID = new AID(TEST_APPLET_AID_BYTES, (short)0, (byte) TEST_APPLET_AID_BYTES.length);
    private static final byte[] TEST_APPLET1_AID_BYTES = Hex.decode("01020304050607080A");
    private static final String TEST_APPLET1_CLASSNAME = "com.licel.jcardsim.samples.HelloWorldApplet1";
    private static final AID TEST_APPLET1_AID = new AID(TEST_APPLET1_AID_BYTES, (short)0, (byte) TEST_APPLET1_AID_BYTES.length);
    
    byte[] createData = null;
    byte[] appletJarContents = null;
    
    public SimulatorTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createData = new byte[1+TEST_APPLET_AID_BYTES.length+1+2+3];
        createData[0] = (byte) TEST_APPLET_AID_BYTES.length;
        System.arraycopy(TEST_APPLET_AID_BYTES, 0, createData, 1, TEST_APPLET_AID_BYTES.length);
        createData[1+TEST_APPLET_AID_BYTES.length] = (byte) 5;
        createData[2+TEST_APPLET_AID_BYTES.length] = 0; // aid
        createData[3+TEST_APPLET_AID_BYTES.length] = 0; // control
        createData[4+TEST_APPLET_AID_BYTES.length] = 2; // params
        createData[5+TEST_APPLET_AID_BYTES.length] = 0xF; // params
        createData[6+TEST_APPLET_AID_BYTES.length] = 0xF; // params
        InputStream is = SimulatorTest.class.getResourceAsStream("helloworld.jar");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] chunk = new byte[1024];
        int readed = is.read(chunk);
        while (readed > 0) {
                bos.write(chunk, 0, readed);
                readed = is.read(chunk);
        }
        appletJarContents = bos.toByteArray();
        bos.close();
        is.close();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of loadApplet method, of class Simulator.
     */
    public void testLoadApplet_3args() {
        System.out.println("loadApplet");
        Simulator instance = new Simulator();
        assertEquals(instance.loadApplet(TEST_APPLET1_AID, TEST_APPLET1_CLASSNAME, appletJarContents).equals(TEST_APPLET1_AID),true);
    }

    /**
     * Test of loadApplet method, of class Simulator.
     */
    public void testLoadApplet_AID_String() {
        System.out.println("loadApplet");
        Simulator instance = new Simulator();
        assertEquals(instance.loadApplet(TEST_APPLET_AID, TEST_APPLET_CLASSNAME).equals(TEST_APPLET_AID),true);
    }

    /**
     * Test of loadApplet method, of class Simulator.
     */
    public void testLoadApplet_AID_Class() {
        System.out.println("loadApplet");
        Simulator instance = new Simulator();
        assertEquals(instance.loadApplet(TEST_APPLET_AID, TEST_APPLET_CLASS).equals(TEST_APPLET_AID),true);
    }

    /**
     * Test of createApplet method, of class Simulator.
     */
    public void testCreateApplet() {
        System.out.println("createApplet");
        Simulator instance = new Simulator();
        assertEquals(instance.loadApplet(TEST_APPLET_AID, TEST_APPLET_CLASS).equals(TEST_APPLET_AID),true);
        assertEquals(instance.createApplet(TEST_APPLET_AID, createData, (short)0, (byte) createData.length).equals(TEST_APPLET_AID),true);
    }

    /**
     * Test of installApplet method, of class Simulator.
     */
    public void testInstallApplet_AID_Class() {
        System.out.println("installApplet");
        Simulator instance = new Simulator();
        instance.installApplet(TEST_APPLET_AID, TEST_APPLET_CLASS);
        assertEquals(instance.selectApplet(TEST_APPLET_AID), true);
    }

    /**
     * Test of installApplet method, of class Simulator.
     */
    public void testInstallApplet_5args_1() {
        System.out.println("installApplet");
        Simulator instance = new Simulator();
        assertEquals(instance.installApplet(TEST_APPLET_AID, TEST_APPLET_CLASS, createData, (short)0, (byte) createData.length).equals(TEST_APPLET_AID),true);
    }

    /**
     * Test of installApplet method, of class Simulator.
     */
    public void testInstallApplet_5args_2() {
        System.out.println("installApplet");
        Simulator instance = new Simulator();
        assertEquals(instance.installApplet(TEST_APPLET_AID, TEST_APPLET_CLASSNAME, createData, (short)0, (byte) createData.length).equals(TEST_APPLET_AID),true);
    }

    /**
     * Test of installApplet method, of class Simulator.
     */
    public void testInstallApplet_6args() {
        System.out.println("installApplet");
        Simulator instance = new Simulator();
        assertEquals(instance.installApplet(TEST_APPLET1_AID, TEST_APPLET1_CLASSNAME, appletJarContents, createData, (short)0, (byte) createData.length).equals(TEST_APPLET1_AID),true);
        assertEquals(instance.selectApplet(TEST_APPLET1_AID), true);
        // test NOP
        byte[] response = instance.transmitCommand(new byte[]{0x01, 0x02, 0x00, 0x00});
        assertEquals(Arrays.areEqual(new byte[]{(byte)0x90, 0x00}, response), true);
    }

    /**
     * Test of selectApplet method, of class Simulator.
     */
    public void testSelectApplet() {
        System.out.println("selectApplet");
        Simulator instance = new Simulator();
        instance.installApplet(TEST_APPLET_AID, TEST_APPLET_CLASS);
        assertEquals(instance.selectApplet(TEST_APPLET_AID), true);
    }
    
    /**
     * Test of selectAppletWithResult method, of class Simulator.
     */
    public void testSelectAppletWithResult() {
        System.out.println("selectApplet");
        Simulator instance = new Simulator();
        instance.installApplet(TEST_APPLET_AID, TEST_APPLET_CLASS);
        byte[] result = instance.selectAppletWithResult(TEST_APPLET_AID);
        assertEquals(result[0], (byte)0x90);
        assertEquals(result[1], 0x00);
    }

    /**
     * Test of transmitCommand method, of class Simulator.
     */
    public void testTransmitCommand() {
        System.out.println("transmitCommand");
        Simulator instance = new Simulator();
        instance.installApplet(TEST_APPLET_AID, TEST_APPLET_CLASS);
        assertEquals(instance.selectApplet(TEST_APPLET_AID), true);
        // test NOP
        byte[] response = instance.transmitCommand(new byte[]{0x01, 0x02, 0x00, 0x00});
        assertEquals(Arrays.areEqual(new byte[]{(byte)0x90, 0x00}, response), true);
    }

    /**
     * Test of reset method, of class Simulator.
     */
    public void testReset() {
        System.out.println("reset");
        Simulator instance = new Simulator();
        instance.installApplet(TEST_APPLET_AID, TEST_APPLET_CLASS);
        instance.reset();
        // after reset installed applets not deleted
        assertEquals(instance.selectApplet(TEST_APPLET_AID), true);
    }

    /**
     * Test of resetRuntime method, of class Simulator.
     */
    public void testResetRuntime() {
        System.out.println("resetRuntime");
        Simulator instance = new Simulator();
        instance.installApplet(TEST_APPLET_AID, TEST_APPLET_CLASS);
        instance.resetRuntime();
        // after reset runtime all applets removed
        assertEquals(instance.selectApplet(TEST_APPLET_AID), false);
    }

    /**
     * Test of getATR method, of class Simulator.
     */
    public void testGetATR() {
        System.out.println("getATR");
        Simulator instance = new Simulator();
        byte[] result = instance.getATR();
        assertEquals(Arrays.areEqual(ETALON_ATR, result), true);
    }
}
