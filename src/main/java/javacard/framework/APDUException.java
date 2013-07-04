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
 * <code>APDUException</code> represents an <code>APDU</code> related exception.
 * <p>The <code>APDU</code> class throws Java Card runtime environment-owned instances of <code>APDUException</code>.
 * <p>Java Card runtime environment-owned instances of exception classes are temporary Java Card runtime environment Entry Point Objects
 * and can be accessed from any applet context. References to these temporary objects
 * cannot be stored in class variables or instance variables or array components.
 * See <em>Runtime Environment Specification for the Java Card Platform</em>, section 6.2.1 for details.
 */
public class APDUException extends CardRuntimeException {

    /**
     * This APDUException reason code indicates that the method should not be invoked
     * based on the current state of the APDU.
     */
    public static final short ILLEGAL_USE = 1;
    /**
     * This reason code is used by the <code>APDU.sendBytes()</code> method to indicate
     * that the sum of buffer offset parameter and the byte length parameter exceeds the APDU
     * buffer size.
     */
    public static final short BUFFER_BOUNDS = 2;
    /**
     * This reason code is used by the <code>APDU.setOutgoingLength()</code> method to indicate
     * that the length parameter is greater that 256 or
     * if non BLOCK CHAINED data transfer is requested and <code>len</code> is greater than
     * (IFSD-2), where IFSD is the Outgoing Block Size.
     */
    public static final short BAD_LENGTH = 3;
    /**
     * This reason code indicates that an unrecoverable error occurred in the
     * I/O transmission layer.
     */
    public static final short IO_ERROR = 4;
    /**
     * This reason code indicates that during T=0 protocol, the CAD did not return a GET RESPONSE
     * command in response to a <61xx> response status to send additional data. The outgoing
     * transfer has been aborted. No more data or status can be sent to the CAD
     * in this <code>Applet.process()</code> method.
     */
    public static final short NO_T0_GETRESPONSE = 170;
    /**
     * This reason code indicates that during T=1 protocol, the CAD returned an ABORT S-Block
     * command and aborted the data transfer. The incoming or outgoing
     * transfer has been aborted. No more data can be received from the CAD.
     * No more data or status can be sent to the CAD
     * in this <code>Applet.process()</code> method.
     */
    public static final short T1_IFD_ABORT = 171;
    /**
     * This reason code indicates that during T=0 protocol, the CAD did not reissue the
     * same APDU command with the corrected length in response to a <6Cxx> response status
     * to request command reissue with the specified length. The outgoing
     * transfer has been aborted. No more data or status can be sent to the CAD
     * in this <code>Applet.process()</code> method
     */
    public static final short NO_T0_REISSUE = 172;

    /**
     * Constructs an APDUException.
     * To conserve on resources use <code>throwIt()</code>
     * to use the Java Card runtime environment-owned instance of this class.
     * @param reason the reason for the exception.
     */
    public APDUException(short reason) {
        super(reason);
    }

    /**
     * Throws the Java Card runtime environment-owned instance of <code>APDUException</code> with the specified reason.
     * <p>Java Card runtime environment-owned instances of exception classes are temporary Java Card runtime environment Entry Point Objects
     * and can be accessed from any applet context. References to these temporary objects
     * cannot be stored in class variables or instance variables or array components.
     * See <em>Runtime Environment Specification for the Java Card Platform</em>, section 6.2.1 for details.
     * @param reason the reason for the exception.
     * @throws APDUException always
     */
    public static void throwIt(short reason) throws APDUException {
        throw new APDUException(reason);
    }
}
