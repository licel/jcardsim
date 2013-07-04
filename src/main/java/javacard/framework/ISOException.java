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
 * <code>ISOException</code> class encapsulates an ISO 7816-4 response status word as
 * its <code>reason</code> code.
 * <p>The <code>APDU</code> class throws Java Card runtime environment-owned instances of <code>ISOException</code>.
 * <p>Java Card runtime environment-owned instances of exception classes are temporary Java Card runtime environment Entry Point Objects
 * and can be accessed from any applet context. References to these temporary objects
 * cannot be stored in class variables or instance variables or array components.
 * See <em>Runtime Environment Specification for the Java Card Platform</em>, section 6.2.1 for details.
 */
public class ISOException extends CardRuntimeException {

    /**
     * Constructs an ISOException instance with the specified status word.
     * To conserve on resources use <code>throwIt()</code>
     * to employ the Java Card runtime environment-owned instance of this class.
     * @param sw the ISO 7816-4 defined status word
     */
    public ISOException(short sw) {
        super(sw);
    }

    /**
     * Throws the Java Card runtime environment-owned instance of the ISOException class with the specified status word.
     * <p>Java Card runtime environment-owned instances of exception classes are temporary Java Card runtime environment Entry Point Objects
     * and can be accessed from any applet context. References to these temporary objects
     * cannot be stored in class variables or instance variables or array components.
     * See <em>Runtime Environment Specification for the Java Card Platform</em>, section 6.2.1 for details.
     * @param sw the ISO 7816-4 defined status word
     * @throws ISOException always
     */
    public static void throwIt(short sw) throws ISOException {
        throw new ISOException(sw);
    }
}
