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
 * <code>TransactionException</code> represents an exception in the transaction subsystem.
 * The methods referred to in this class are in the <code>JCSystem</code> class.
 * <p>The <code>JCSystem</code> class and the transaction facility throw Java Card runtime environment-owned instances
 * of <code>TransactionException</code>.
 * <p>Java Card runtime environment-owned instances of exception classes are temporary Java Card runtime environment Entry Point Objects
 * and can be accessed from any applet context. References to these temporary objects
 * cannot be stored in class variables or instance variables or array components.
 * See <em>Runtime Environment Specification for the Java Card Platform</em>, section 6.2.1 for details.
 *
 */
public class TransactionException extends CardRuntimeException {

    /**
     * This reason code is used by the <code>beginTransaction</code> method to indicate
     * a transaction is already in progress.
     */
    public static final short IN_PROGRESS = 1;
    /**
     * This reason code is used by the <code>abortTransaction</code> and <code>commitTransaction</code> methods
     * when a transaction is not in progress.
     */
    public static final short NOT_IN_PROGRESS = 2;
    /**
     * This reason code is used during a transaction to indicate that the commit buffer is full.
     */
    public static final short BUFFER_FULL = 3;
    /**
     * This reason code is used during a transaction to indicate
     * an internal Java Card runtime environment problem (fatal error).
     */
    public static final short INTERNAL_FAILURE = 4;

    
    /**
     * Constructs a TransactionException with the specified reason.
     * To conserve on resources use <code>throwIt()</code>
     * to use the Java Card runtime environment-owned instance of this class.
     * @param reason the reason for the exception
     */
    public TransactionException(short reason) {
        super(reason);
    }

    /**
     * Throws the Java Card runtime environment-owned instance of <code>TransactionException</code> with the specified reason.
     * <p>Java Card runtime environment-owned instances of exception classes are temporary Java Card runtime environment Entry Point Objects
     * and can be accessed from any applet context. References to these temporary objects
     * cannot be stored in class variables or instance variables or array components.
     * See <em>Runtime Environment Specification for the Java Card Platform</em>, section 6.2.1 for details.
     * @param reason the reason for the exception
     * @throws TransactionException always
     */
    public static void throwIt(short reason) throws TransactionException {
        throw new TransactionException(reason);
    }
}
