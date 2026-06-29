package com.licel.jcardsim.base;

import javacard.framework.JCSystem;
import javacard.framework.SensitiveArrays;
import javacard.framework.SystemException;
import junit.framework.TestCase;

import java.util.Arrays;


public class SensitiveArraysTest extends TestCase {
    public SensitiveArraysTest(String name) {
        super(name);
    }

    public void testCheckSensitiveArraysSupport() {
        assertTrue(SensitiveArrays.isIntegritySensitiveArraysSupported());
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

    public void testCreateNotSupportSensitiveIntArray() {
        try {
            Object obj = SensitiveArrays.makeIntegritySensitiveArray(JCSystem.ARRAY_TYPE_INT, JCSystem.MEMORY_TYPE_PERSISTENT, (short) 8);
            fail("Expected SystemException.ILLEGAL_VALUE");
        } catch (SystemException expected) {
            // success
            assertEquals(SystemException.ILLEGAL_VALUE, expected.getReason());

        }
    }

    public void testNotSensitivePersistentArray() {
        byte[] array = new byte[16];
        assertFalse(SensitiveArrays.isIntegritySensitive(array));
    }

    public void testNotSensitiveTransientArray() {
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
            fail("Expected SystemException.ILLEGAL_VALUE");
        } catch (SystemException expected) {
            // success
            assertEquals(SystemException.ILLEGAL_VALUE, expected.getReason());

        }
    }

    public void testClearSensitiveByteArray() {
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

    public void testClearSensitiveShortArray() {
        short[] array = (short[]) SensitiveArrays.makeIntegritySensitiveArray(JCSystem.ARRAY_TYPE_SHORT, JCSystem.MEMORY_TYPE_PERSISTENT, (short) 5);
        array[0] = 100;
        array[1] = 200;

        SensitiveArrays.clearArray(array);

        for (short value : array) {
            assertEquals(0, value);
        }
    }

    public void testClearSensitiveBooleanArray() {
        boolean[] array = (boolean[]) SensitiveArrays.makeIntegritySensitiveArray(JCSystem.ARRAY_TYPE_BOOLEAN, JCSystem.MEMORY_TYPE_PERSISTENT, (short) 5);
        array[0] = true;
        array[1] = true;

        SensitiveArrays.clearArray(array);

        for (boolean value : array) {
            assertFalse(value);
        }
    }

    public void testClearSensitiveObjectArray() {
        Object[] array = (Object[]) SensitiveArrays.makeIntegritySensitiveArray(JCSystem.ARRAY_TYPE_OBJECT, JCSystem.MEMORY_TYPE_PERSISTENT, (short) 5);
        array[0] = "ABC";

        SensitiveArrays.clearArray(array);

        for (Object value : array) {
            assertNull(value);
        }
    }

    public void testClearNotSensitivePersistentByteArrays() {
        byte[] array = new byte[16];
        for (short i = 0; i < array.length; i++) {
            array[i] = (byte) (i + 1);
        }

        try {
            short len = SensitiveArrays.clearArray(array);
            fail("Expected SystemException.ILLEGAL_VALUE");
        } catch (SystemException expected) {
            // success
            assertEquals(SystemException.ILLEGAL_VALUE, expected.getReason());
        }
    }

    public void testClearNotSensitiveTransientByteArrays() {
        byte[] array = JCSystem.makeTransientByteArray((short) 8, JCSystem.MEMORY_TYPE_TRANSIENT_DESELECT);
        for (short i = 0; i < array.length; i++) {
            array[i] = (byte) (i + 1);
        }

        try {
            short len = SensitiveArrays.clearArray(array);
            fail("Expected SystemException.ILLEGAL_VALUE");
        } catch (SystemException expected) {
            // success
            assertEquals(SystemException.ILLEGAL_VALUE, expected.getReason());
        }

    }

    public void testClearNotSensitivePersistentShortArrays() {
        short[] array = new short[16];
        for (short i = 0; i < array.length; i++) {
            array[i] = (short) (i + 1);
        }

        try {
            short len = SensitiveArrays.clearArray(array);
            fail("Expected SystemException.ILLEGAL_VALUE");
        } catch (SystemException expected) {
            // success
            assertEquals(SystemException.ILLEGAL_VALUE, expected.getReason());
        }
    }

    public void testClearNotSensitiveTransientShortArrays() {
        short[] array = JCSystem.makeTransientShortArray((short) 16, JCSystem.MEMORY_TYPE_TRANSIENT_DESELECT);
        for (short i = 0; i < array.length; i++) {
            array[i] = (short) (i + 1);
        }

        try {
            short len = SensitiveArrays.clearArray(array);
            fail("Expected SystemException.ILLEGAL_VALUE");
        } catch (SystemException expected) {
            // success
            assertEquals(SystemException.ILLEGAL_VALUE, expected.getReason());

        }
    }

    public void testClearNotSensitivePersistentBooleanArrays() {
        boolean[] array = new boolean[16];
        for (short i = 0; i < array.length; i++) {
            array[i] = true;
        }

        try {
            short len = SensitiveArrays.clearArray(array);
            fail("Expected SystemException.ILLEGAL_VALUE");
        } catch (SystemException expected) {
            // success
            assertEquals(SystemException.ILLEGAL_VALUE, expected.getReason());
        }
    }

    public void testClearNotSensitiveTransientBooleanArrays() {
        boolean[] array = JCSystem.makeTransientBooleanArray((short) 16, JCSystem.MEMORY_TYPE_TRANSIENT_DESELECT);
        for (short i = 0; i < array.length; i++) {
            array[i] = true;
        }

        try {
            short len = SensitiveArrays.clearArray(array);
            fail("Expected SystemException.ILLEGAL_VALUE");
        } catch (SystemException expected) {
            // success
            assertEquals(SystemException.ILLEGAL_VALUE, expected.getReason());
        }
    }

    public void testClearNotSensitivePersistentObjectArrays() {
        Object[] array = new Object[16];
        for (short i = 0; i < array.length; i++) {
            array[i] = "ABCD";
        }

        try {
            short len = SensitiveArrays.clearArray(array);
            fail("Expected SystemException.ILLEGAL_VALUE");
        } catch (SystemException expected) {
            // success
            assertEquals(SystemException.ILLEGAL_VALUE, expected.getReason());
        }
    }

    public void testClearNotSensitiveTransientObjectArrays() {
        Object[] array = JCSystem.makeTransientObjectArray((short) 16, JCSystem.MEMORY_TYPE_TRANSIENT_DESELECT);
        for (short i = 0; i < array.length; i++) {
            array[i] = "ABCD";
        }

        try {
            short len = SensitiveArrays.clearArray(array);
            fail("Expected SystemException.ILLEGAL_VALUE");
        } catch (SystemException expected) {
            // success
            assertEquals(SystemException.ILLEGAL_VALUE, expected.getReason());
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

    public void testClearAllSensitiveMemoryOnResetRuntime() {
        SimulatorRuntime runtime = new PersistentSimulatorRuntime();
        Simulator instance = new Simulator(runtime);

        byte[] persistentByteArray = (byte[]) SensitiveArrays.makeIntegritySensitiveArray(JCSystem.ARRAY_TYPE_BYTE, JCSystem.MEMORY_TYPE_PERSISTENT, (short) 10);
        byte[] transientByteArray = (byte[]) SensitiveArrays.makeIntegritySensitiveArray(JCSystem.ARRAY_TYPE_BYTE, JCSystem.MEMORY_TYPE_TRANSIENT_RESET, (short) 10);

        Arrays.fill(persistentByteArray, (byte) 0x55);
        Arrays.fill(transientByteArray, (byte) 0xAA);

        instance.reset();

        for (byte b : persistentByteArray) {
            assertEquals((byte) 0x55, b);
        }

        for (byte b : transientByteArray) {
            assertEquals((byte) 0, b);
        }

        assertTrue(SensitiveArrays.isIntegritySensitive(persistentByteArray));
        assertTrue(SensitiveArrays.isIntegritySensitive(transientByteArray));

        Arrays.fill(transientByteArray, (byte) 0xAA);

        instance.resetRuntime();

        assertFalse(SensitiveArrays.isIntegritySensitive(persistentByteArray));
        assertFalse(SensitiveArrays.isIntegritySensitive(transientByteArray));

        for (byte b : persistentByteArray) {
            assertEquals((byte) 0, b);
        }

        for (byte b : transientByteArray) {
            assertEquals((byte) 0, b);
        }
    }
}