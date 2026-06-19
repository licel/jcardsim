package com.licel.jcardsim.base;

import javacard.framework.JCSystem;
import javacard.framework.SensitiveArrays;
import junit.framework.TestCase;

import java.util.Arrays;


public class SensitiveArraysTest extends TestCase {
    public SensitiveArraysTest(String name) {
        super(name);
    }

    public void testCreatePersistentByteArray() {

        Object obj = SensitiveArrays.makeIntegritySensitiveArray(JCSystem.ARRAY_TYPE_BYTE, JCSystem.MEMORY_TYPE_PERSISTENT, (short) 16);

        assertNotNull(obj);
        assertTrue(obj instanceof byte[]);
        assertEquals(16, ((byte[]) obj).length);
        assertTrue(SensitiveArrays.isIntegritySensitive(obj));
    }

    public void testCreatePersistentBooleanArray() {
        Object obj = SensitiveArrays.makeIntegritySensitiveArray( JCSystem.ARRAY_TYPE_BOOLEAN, JCSystem.MEMORY_TYPE_PERSISTENT, (short) 8);

        assertNotNull(obj);
        assertTrue(obj instanceof boolean[]);
        assertEquals(8, ((boolean[]) obj).length);
        assertTrue(SensitiveArrays.isIntegritySensitive(obj));

    }

    public void testCreatePersistentShortArray() {
        Object obj = SensitiveArrays.makeIntegritySensitiveArray(JCSystem.ARRAY_TYPE_SHORT, JCSystem.MEMORY_TYPE_PERSISTENT, (short) 8);

        assertNotNull(obj);
        assertTrue(obj instanceof short[]);
        assertEquals(8, ((short[]) obj).length);
        assertTrue(SensitiveArrays.isIntegritySensitive(obj));
    }

    public void testCreatePersistentObjectArray() {
        Object obj = SensitiveArrays.makeIntegritySensitiveArray(JCSystem.ARRAY_TYPE_OBJECT, JCSystem.MEMORY_TYPE_PERSISTENT, (short) 8);

        assertNotNull(obj);
        assertTrue(obj instanceof Object[]);
        assertEquals(8, ((Object[]) obj).length);
        assertTrue(SensitiveArrays.isIntegritySensitive(obj));
    }

    public void testNormalArrayNotSensitive() {
        byte[] array = new byte[16];
        assertFalse(SensitiveArrays.isIntegritySensitive(array));
    }

    public void testNormalTransientArrayNotSensitive() {
        byte[] array = JCSystem.makeTransientByteArray((short) 16, JCSystem.MEMORY_TYPE_TRANSIENT_DESELECT);
        assertFalse(SensitiveArrays.isIntegritySensitive(array));
    }

    public void testAssertIntegritySuccess() {
        Object obj = SensitiveArrays.makeIntegritySensitiveArray(JCSystem.ARRAY_TYPE_BYTE, JCSystem.MEMORY_TYPE_PERSISTENT, (short) 16);
        try {
            SensitiveArrays.assertIntegrity(obj);
        } catch (Exception e) {
            fail("Unexpected exception: " + e);
        }
    }

    public void testAssertIntegrityFails() {
        byte[] array = new byte[16];
        try {
            SensitiveArrays.assertIntegrity(array);
            fail("Expected SecurityException");
        } catch (SecurityException expected) {
            // success
        }
    }

    public void testClearByteArray() {
        byte[] array = (byte[]) SensitiveArrays.makeIntegritySensitiveArray(JCSystem.ARRAY_TYPE_BYTE, JCSystem.MEMORY_TYPE_PERSISTENT, (short) 10);

        for (short i = 0; i < array.length; i++) {
            array[i] = (byte) (i + 1);
        }

        short len = SensitiveArrays.clearArray(array);

        assertEquals(10, len);

        for (byte b : array) {
            assertEquals(0, b);
        }
    }

    public void testClearShortArray() {
        short[] array = (short[]) SensitiveArrays.makeIntegritySensitiveArray(JCSystem.ARRAY_TYPE_SHORT, JCSystem.MEMORY_TYPE_PERSISTENT, (short) 5);

        array[0] = 100;
        array[1] = 200;

        SensitiveArrays.clearArray(array);

        for (short value : array) {
            assertEquals(0, value);
        }
    }

    public void testClearBooleanArray() {
        boolean[] array = (boolean[]) SensitiveArrays.makeIntegritySensitiveArray(JCSystem.ARRAY_TYPE_BOOLEAN, JCSystem.MEMORY_TYPE_PERSISTENT, (short) 5);

        array[0] = true;
        array[1] = true;

        SensitiveArrays.clearArray(array);

        for (boolean value : array) {
            assertFalse(value);
        }
    }

    public void testClearObjectArray() {
        Object[] array = (Object[]) SensitiveArrays.makeIntegritySensitiveArray(JCSystem.ARRAY_TYPE_OBJECT, JCSystem.MEMORY_TYPE_PERSISTENT, (short) 5);

        array[0] = "ABC";

        SensitiveArrays.clearArray(array);

        for (Object value : array) {
            assertNull(value);
        }
    }

    public void testTransientResetArrayClearedOnReset() {
        SimulatorRuntime runtime = new PersistentSimulatorRuntime();
        Simulator instance = new Simulator(runtime);

        byte[] array = (byte[]) SensitiveArrays.makeIntegritySensitiveArray(JCSystem.ARRAY_TYPE_BYTE, JCSystem.MEMORY_TYPE_TRANSIENT_RESET, (short) 10);

        Arrays.fill(array, (byte) 0x55);

        instance.reset();

        for (byte b : array) {
            assertEquals(0, b);
        }
    }

    public void testTransientDeselectArrayClearedOnReset() {
        SimulatorRuntime runtime = new PersistentSimulatorRuntime();
        Simulator instance = new Simulator(runtime);

        byte[] array = (byte[]) SensitiveArrays.makeIntegritySensitiveArray(JCSystem.ARRAY_TYPE_BYTE, JCSystem.MEMORY_TYPE_TRANSIENT_DESELECT, (short) 10);

        Arrays.fill(array, (byte) 0x55);

        instance.reset();

        for (byte b : array) {
            assertEquals(0, b);
        }
    }


    public void testPersistentArraySurvivesReset() {
        SimulatorRuntime runtime = new PersistentSimulatorRuntime();
        Simulator instance = new Simulator(runtime);

        byte[] array = (byte[]) SensitiveArrays.makeIntegritySensitiveArray(JCSystem.ARRAY_TYPE_BYTE, JCSystem.MEMORY_TYPE_PERSISTENT, (short) 10);

        Arrays.fill(array, (byte) 0x55);

        instance.reset();

        for (byte b : array) {
            assertEquals((byte) 0x55, b);
        }
    }
}