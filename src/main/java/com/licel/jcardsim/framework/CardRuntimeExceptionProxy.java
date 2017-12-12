/*
 * Copyright 2015 Licel Corporation.
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
package com.licel.jcardsim.framework;

/**
 * ProxyClass for <code>CardRuntimeException</code>
 * @see javacard.framework.CardRuntimeException
 */
public class CardRuntimeExceptionProxy extends RuntimeException {

    private short reason;

    /**
     * Constructs a CardRuntimeException instance with the specified reason.
     * To conserve on resources, use the <code>throwIt()</code> method
     * to employ the Java Card runtime environment-owned instance of this class.
     * @param reason the reason for the exception
     */
    public CardRuntimeExceptionProxy(short reason) {
        this.reason = reason;
    }

    /**
     * Get reason code
     * @return the reason for the exception
     */
    public short getReason() {
        return reason;
    }

    /**
     * Set reason code
     * @param reason the reason for the exception
     */
    public void setReason(short reason) {
        this.reason = reason;
    }

    /**
     * Throws the Java Card runtime environment-owned instance of the <code>CardRuntimeException</code> class with the
     * specified reason.
     * <p>Java Card runtime environment-owned instances of exception classes are temporary Java Card runtime environment Entry Point Objects
     * and can be accessed from any applet context. References to these temporary objects
     * cannot be stored in class variables or instance variables or array components.
     * See <em>Runtime Environment Specification for the Java Card Platform</em>, section 6.2.1 for details.
     * @param reason the reason for the exception
     * @throws CardRuntimeExceptionProxy always
     */
    public static void throwIt(short reason)
            throws CardRuntimeExceptionProxy {
        throw new CardRuntimeExceptionProxy(reason);
    }
}
