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
 * <code>SystemException</code> represents a <code>JCSystem</code> class related exception.
 * It is also thrown by the <code>javacard.framework.Applet.register()</code> methods and by
 * the <code>AID</code> class constructor.
 * <p>These API classes throw Java Card runtime environment-owned instances of <code>SystemException</code>.
 * <p>Java Card runtime environment-owned instances of exception classes are temporary Java Card runtime environment Entry Point Objects
 * and can be accessed from any applet context. References to these temporary objects
 * cannot be stored in class variables or instance variables or array components.
 * See <em>Runtime Environment Specification for the Java Card Platform</em>, section 6.2.1 for details.
 * @author dudarev
 */
public class SystemException extends CardRuntimeException {

    /**
     * This reason code is used to indicate that one or more input parameters
     * is out of allowed bounds.
     */
    public static final short ILLEGAL_VALUE = 1;
    /**
     * This reason code is used by the <code>makeTransient..()</code> methods
     * to indicate that no room is available in volatile memory for the requested object.
     */
    public static final short NO_TRANSIENT_SPACE = 2;
    /**
     * This reason code is used to indicate that the request to create
     * a transient object is not allowed in the current applet context.
     * See <em>Runtime Environment Specification for the Java Card Platform</em>, section 6.2.1 for details.
     */
    public static final short ILLEGAL_TRANSIENT = 3;
    /**
     * This reason code is used by the <code>javacard.framework.Applet.register()</code> method
     * to indicate that the input AID parameter is not a legal AID value.
     */
    public static final short ILLEGAL_AID = 4;
    /**
     * This reason code is used to indicate that there is insufficient resource
     * in the Card for the request.
     * <p>For example, the Java Card Virtual Machine may <code>throw</code>
     * this exception reason when there is insufficient heap space to create a new instance.
     */
    public static final short NO_RESOURCE = 5;
    /**
     * This reason code is used to indicate that the requested function is not
     * allowed. For example, <CODE>JCSystem.requestObjectDeletion()</CODE> method throws this exception if
     * the object deletion mechanism is not implemented.
     */
    public static final short ILLEGAL_USE = 6;

    /**
     * Constructs a SystemException.
     * To conserve on resources use <code>throwIt()</code>
     * to use the Java Card runtime environment-owned instance of this class.
     * @param reason the reason for the exception
     */
    public SystemException(short reason) {
        super(reason);
    }

    /**
     * Throws the Java Card runtime environment-owned instance of <code>SystemException</code> with the specified reason.
     * <p>Java Card runtime environment-owned instances of exception classes are temporary Java Card runtime environment Entry Point Objects
     * and can be accessed from any applet context. References to these temporary objects
     * cannot be stored in class variables or instance variables or array components.
     * See <em>Runtime Environment Specification for the Java Card Platform</em>, section 6.2.1 for details.
     * @param reason reason the reason for the exception
     * @throws SystemException always
     */
    public static void throwIt(short reason)
            throws SystemException {
        throw new SystemException(reason);
    }
}
