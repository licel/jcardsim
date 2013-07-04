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
package javacard.security;

import javacard.framework.CardRuntimeException;

/**
 * <code>CryptoException</code> represents a cryptography-related exception.
 * <p>The API classes throw Java Card runtime environment-owned instances of <code>CryptoException</code>.
 * <p>Java Card runtime environment-owned instances of exception classes are temporary Java Card runtime environment Entry Point Objects
 * and can be accessed from any applet context. References to these temporary objects
 * cannot be stored in class variables or instance variables or array components.
 * @see MessageDigest
 * @see Signature
 * @see RandomData
 * @see Cipher
 */
public class CryptoException extends CardRuntimeException {

    /**
     *  This reason code is used to indicate that one or more input parameters is out of allowed bounds.
     */
    public static final short ILLEGAL_VALUE = 1;
    /**
     * This reason code is used to indicate that the key is uninitialized.
     */
    public static final short UNINITIALIZED_KEY = 2;
    /**
     * This reason code is used to indicate that the requested algorithm or key type is not supported.
     */
    public static final short NO_SUCH_ALGORITHM = 3;
    /**
     * This reason code is used to indicate that the signature or cipher object has not been correctly initialized for the requested operation.
     */
    public static final short INVALID_INIT = 4;
    /**
     * This reason code is used to indicate that the signature or cipher algorithm does not pad the incoming message and the input message is not block aligned.
     */
    public static final short ILLEGAL_USE = 5;

    /**
     * Constructs a <code>CryptoException</code> with the specified reason.
     * To conserve on resources use <code>throwIt()</code>
     * to use the Java Card runtime environment-owned instance of this class.
     * @param reason the reason for the exception
     */
    public CryptoException(short reason) {
        super(reason);
    }

    /**
     * Throws the Java Card runtime environment-owned instance of <code>CryptoException</code> with the specified reason.
     * <p>Java Card runtime environment-owned instances of exception classes are temporary Java Card runtime environment Entry Point Objects
     * and can be accessed from any applet context. References to these temporary objects
     * cannot be stored in class variables or instance variables or array components.
     * See <em>Runtime Environment Specification for the Java Card Platform</em>, section 6.2.1 for details.
     * @param reason the reason for the exception
     * @throws CryptoException always
     */
    public static void throwIt(short reason) {
        throw new CryptoException(reason);
    }
}