package com.licel.jcardsim.crypto;

import junit.framework.TestCase;

import java.math.BigInteger;

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
}
