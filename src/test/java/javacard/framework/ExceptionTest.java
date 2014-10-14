package javacard.framework;

import junit.framework.TestCase;

public class ExceptionTest extends TestCase {
    public ExceptionTest(String name) {
        super(name);
    }

    public void testSystemExceptionMessageIllegalAID() {
        try {
            throw new SystemException(SystemException.ILLEGAL_AID);
        }
        catch (SystemException e) {
            assertEquals("ILLEGAL_AID", e.getMessage());
        }
    }

    public void testSystemExceptionMessage() {
        try {
            throw new SystemException((short) 123);
        }
        catch (SystemException e) {
            assertEquals("Unknown reason (123)", e.getMessage());
        }
    }
}
