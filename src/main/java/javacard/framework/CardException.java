/*
 * Copyright 2011 Licel LLC.
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
package javacard.framework;

/**
 * The <code>CardException</code> class
 * defines a field <code>reason </code>and two accessor methods <code>
 * getReason()</code> and <code>setReason()</code>. The <code>reason</code>
 * field encapsulates an exception cause identifier in the Java Card platform.
 * All Java Card platform checked Exception classes should extend
 * <code>CardException</code>. This class also provides a resource-saving mechanism
 * (<code>throwIt()</code> method) for using a Java Card runtime environment-owned instance of this class.
 * <p> Even if a transaction is in progress, the update of the internal <code>reason</code>
 * field shall not participate in the transaction. The value of the internal <code>reason</code>
 * field of Java Card runtime environment-owned instance is reset to 0 on a tear or reset.
 *
 */
public class CardException extends Exception {

    private byte theSw[];

    /**
     * Construct a CardException instance with the specified reason.
     * To conserve on resources, use the <code>throwIt()</code> method
     * to use the Java Card runtime environment-owned instance of this class.
     * @param reason the reason for the exception
     */
    public CardException(short reason) {
        theSw = JCSystem.makeTransientByteArray((short) 2, JCSystem.CLEAR_ON_RESET);
        Util.setShort(theSw, (short) 0, reason);
    }

    /**
     * Get reason code
     * @return the reason for the exception
     */
    public short getReason() {
        return Util.getShort(theSw, (short) 0);
    }

    /**
     * Set reason code
     * @param reason the reason for the exception
     */
    public void setReason(short reason) {
        Util.arrayFillNonAtomic(theSw, (short) 0, (short) 1, (byte) (reason >>> 8));
        Util.arrayFillNonAtomic(theSw, (short) 1, (short) 1, (byte) reason);
    }

    /**
     * Throw the Java Card runtime environment-owned instance of <code>CardException</code> class with the
     * specified reason.
     * <p>Java Card runtime environment-owned instances of exception classes are temporary Java Card runtime environment Entry Point Objects
     * and can be accessed from any applet context. References to these temporary objects
     * cannot be stored in class variables or instance variables or array components.
     * See <em>Runtime Environment Specification for the Java Card Platform</em>, section 6.2.1 for details.
     * @param reason  the reason for the exception
     * @throws CardException always
     */
    public static void throwIt(short reason)
            throws CardException {
        throw new CardException(reason);
    }
}
