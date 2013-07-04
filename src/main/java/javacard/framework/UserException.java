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
 * <code>UserException</code> represents a User exception.
 * This class also provides a resource-saving mechanism (the <code>throwIt()</code> method) for user
 * exceptions by using a Java Card runtime environment-owned instance.
 * <p>Java Card runtime environment-owned instances of exception classes are temporary Java Card runtime environment Entry Point Objects
 * and can be accessed from any applet context. References to these temporary objects
 * cannot be stored in class variables or instance variables or array components.
 * See <em>Runtime Environment Specification for the Java Card Platform</em>, section 6.2.1 for details.
 */
public class UserException extends CardException {

    /**
     * Constructs a <code>UserException</code> with reason = 0.
     * To conserve on resources use <code>throwIt()</code>
     * to use the Java Card runtime environment-owned instance of this class.
     */
    public UserException() {
        this((short) 0);
    }

    /**
     * Constructs a <code>UserException</code> with the specified reason.
     * To conserve on resources use <code>throwIt()</code>
     * to use the Java Card runtime environment-owned instance of this class.
     * @param reason the reason for the exception
     */
    public UserException(short reason) {
        super(reason);
    }

    /**
     * Throws the Java Card runtime environment-owned instance of <code>UserException</code> with the specified reason.
     * <p>Java Card runtime environment-owned instances of exception classes are temporary Java Card runtime environment Entry Point Objects
     * and can be accessed from any applet context. References to these temporary objects
     * cannot be stored in class variables or instance variables or array components.
     * See <em>Runtime Environment Specification for the Java Card Platform</em>, section 6.2.1 for details.
     * @param reason the reason for the exception
     * @throws UserException always
     */
    public static void throwIt(short reason)
            throws UserException {
        throw new UserException(reason);
    }
}
