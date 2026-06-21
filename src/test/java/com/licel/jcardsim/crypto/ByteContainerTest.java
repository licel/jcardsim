package com.licel.jcardsim.crypto;

import junit.framework.TestCase;

import java.math.BigInteger;
import javacard.framework.JCSystem;

public class ByteContainerTest extends TestCase {
    public ByteContainerTest(String name) {
        super(name);
    }

    public void testPositiveIntegerWithLeadingZero() {
        BigInteger expected = new BigInteger("4720643197658441292834747278018339");
        assertTrue(expected.toByteArray()[0] == 0);
        checkRoundTrip(expected);
    }

    public void testPositiveIntegerWithoutLeadingZero() {
        BigInteger expected = new BigInteger("5192296858534827689835882578830703");
        assertTrue(expected.toByteArray()[0] != 0);
        checkRoundTrip(expected);
    }

    public void testZero() {
        BigInteger expected = new BigInteger("0");
        assertTrue(expected.toByteArray()[0] == 0);
        checkRoundTrip(expected);
    }

    public void testNegativeNumber() {
        BigInteger expected = new BigInteger("-123");

        try {
            new ByteContainer().setBigInteger(expected);
            fail("No exception");
        }
        catch (IllegalArgumentException ignore) {
        }

        try {
            new ByteContainer(expected);
            fail("No exception");
        }
        catch (IllegalArgumentException ignore) {
        }
    }

    private void checkRoundTrip(BigInteger expected) {
        ByteContainer byteContainer = new ByteContainer();
        byteContainer.setBigInteger(expected);
        assertEquals(expected, byteContainer.getBigInteger());

        assertEquals(expected, new ByteContainer(expected).getBigInteger());
    }
    
    public void testSetBytes() {
        short TEST_DATA_LEN = (short) 32;
        byte[] expected = new byte[TEST_DATA_LEN];
        ByteContainer container = new ByteContainer(expected, (short) 0, (short) expected.length);
        byte[] dataResult = container.getBytes(JCSystem.MEMORY_TYPE_TRANSIENT_RESET);
        assertEquals(expected.length, dataResult.length);
        
        // Try to set with shorter and longer array - length should change accordingly
        byte[] shorter = new byte[(short) (TEST_DATA_LEN - 2)];
        container.setBytes(shorter);
        dataResult = container.getBytes(JCSystem.MEMORY_TYPE_TRANSIENT_RESET);
        assertEquals(shorter.length, dataResult.length);
        byte[] longer = new byte[(short) (TEST_DATA_LEN + 2)];
        container.setBytes(longer);
        dataResult = container.getBytes(JCSystem.MEMORY_TYPE_TRANSIENT_RESET);
        assertEquals(longer.length, dataResult.length);
    }
    
}
