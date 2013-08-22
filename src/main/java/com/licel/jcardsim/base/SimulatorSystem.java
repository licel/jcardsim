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
package com.licel.jcardsim.base;

import javacard.framework.*;

/**
 * Base implementation of <code>JCSystem</code>
 * @see JCsystem
 */
public class SimulatorSystem {
    
    /**
     * Response status : Applet creation failed = 0x6444
     */
    public static final short SW_APPLET_CRATION_FAILED = 0x6444;
    /**
     * Response status : Exception occured = 0x6424
     */
    public static final short SW_EXCEPTION_OCCURED = 0x6424;
    
    // current depth of transaction
    private static byte transactionDepth = 0;
    // implementaion api version
    private static final short API_VERSION = 0x202;
    // transient memory storage
    private static TransientMemory transientMemory = new TransientMemory();

    private static SimulatorRuntime runtime = new SimulatorRuntime();

    public static byte currentChannel = 0;
    public static Object previousActiveObject;
    
    public static NullPointerException nullPointerException;
    public static SecurityException securityException;

    private SimulatorSystem() {
        nullPointerException = new NullPointerException();
        securityException = new SecurityException();
    }

    /**
     * Checks if the specified object is transient.
     * <p>Note:
     * <ul>
     * <em>This method returns </em><code>NOT_A_TRANSIENT_OBJECT</code><em> if the specified object is
     * <code>null</code> or is not an array type.</em>
     * </ul>
     * @param theObj the object being queried
     * @return <code>NOT_A_TRANSIENT_OBJECT</code>, <code>CLEAR_ON_RESET</code>, or <code>CLEAR_ON_DESELECT</code>
     * @see #makeTransientBooleanArray(short, byte)
     * @see #makeByteArray(short, byte)
     * @see #makeObjectArray(short, byte)
     * @see #makeShortArray(short, byte)
     */
    public static byte isTransient(Object theObj) {
        return transientMemory.isTransient(theObj);
    }

    /**
     * Creates a transient boolean array with the specified array length.
     * @param length the length of the boolean array
     * @param event the <code>CLEAR_ON...</code> event which causes the array elements to be cleared
     * @return the new transient boolean array
     * @throws NegativeArraySizeException if the <CODE>length</CODE> parameter is negative
     * @throws SystemException with the following reason codes:
     * <ul>
     * <li><code>SystemException.ILLEGAL_VALUE</code> if event is not a valid event code.
     * <li><code>SystemException.NO_TRANSIENT_SPACE</code> if sufficient transient space is not available.
     * <li><code>SystemException.ILLEGAL_TRANSIENT</code> if the current applet context
     * is not the currently selected applet context and <code>CLEAR_ON_DESELECT</code> is specified.
     * </ul>
     */
    public static boolean[] makeTransientBooleanArray(short length, byte event) {
        return transientMemory.makeBooleanArray(length, event);
    }

    /**
     * Creates a transient byte array with the specified array length.
     * @param length the length of the byte array
     * @param event the <code>CLEAR_ON...</code> event which causes the array elements to be cleared
     * @return the new transient byte array
     * @throws NegativeArraySizeException if the <CODE>length</CODE> parameter is negative
     * @throws SystemException with the following reason codes:
     * <ul>
     * <li><code>SystemException.ILLEGAL_VALUE</code> if event is not a valid event code.
     * <li><code>SystemException.NO_TRANSIENT_SPACE</code> if sufficient transient space is not available.
     * <li><code>SystemException.ILLEGAL_TRANSIENT</code> if the current applet context
     * is not the currently selected applet context and <code>CLEAR_ON_DESELECT</code> is specified.
     * </ul>
     */
    public static byte[] makeTransientByteArray(short length, byte event) {
        return transientMemory.makeByteArray(length, event);
    }

    /**
     * Creates a transient short array with the specified array length.
     * @param length the length of the short array
     * @param event the <code>CLEAR_ON...</code> event which causes the array elements to be cleared
     * @return the new transient short array
     * @throws NegativeArraySizeException if the <CODE>length</CODE> parameter is negative
     * @throws SystemException with the following reason codes:
     * <ul>
     * <li><code>SystemException.ILLEGAL_VALUE</code> if event is not a valid event code.
     * <li><code>SystemException.NO_TRANSIENT_SPACE</code> if sufficient transient space is not available.
     * <li><code>SystemException.ILLEGAL_TRANSIENT</code> if the current applet context
     * is not the currently selected applet context and <code>CLEAR_ON_DESELECT</code> is specified.
     * </ul>
     */
    public static short[] makeTransientShortArray(short length, byte event) {
        return transientMemory.makeShortArray(length, event);
    }

    /**
     * Creates a transient array of <code>Object</code> with the specified array length.
     * @param length the length of the Object array
     * @param event the <code>CLEAR_ON...</code> event which causes the array elements to be cleared
     * @return the new transient Object array
     * @throws NegativeArraySizeException if the <CODE>length</CODE> parameter is negative
     * @throws SystemException with the following reason codes:
     * <ul>
     * <li><code>SystemException.ILLEGAL_VALUE</code> if event is not a valid event code.
     * <li><code>SystemException.NO_TRANSIENT_SPACE</code> if sufficient transient space is not available.
     * <li><code>SystemException.ILLEGAL_TRANSIENT</code> if the current applet context
     * is not the currently selected applet context and <code>CLEAR_ON_DESELECT</code> is specified.
     * </ul>
     */
    public static Object[] makeTransientObjectArray(short length, byte event) {
        return transientMemory.makeObjectArray(length, event);
    }

    /**
     * Returns the Java Card runtime environment-owned instance of the <code>AID</code> object associated with
     * the current applet context, or
     * <code>null</code> if the <code>Applet.register()</code> method
     * has not yet been invoked.
     * <p>Java Card runtime environment-owned instances of <code>AID</code> are permanent Java Card runtime environment
     * Entry Point Objects and can be accessed from any applet context.
     * References to these permanent objects can be stored and re-used.
     * <p>See <em>Runtime Environment Specification for the Java Card Platform</em>, section 6.2.1 for details.
     * @return the <code>AID</code> object
     */
    public static AID getAID() {
        return runtime.getAID();
    }

    /**
     * Returns the Java Card runtime environment-owned instance of the <code>AID</code> object, if any,
     * encapsulating the specified AID bytes in the <code>buffer</code> parameter
     * if there exists a successfully installed applet on the card whose instance AID
     * exactly matches that of the specified AID bytes.
     * <p>Java Card runtime environment-owned instances of <code>AID</code> are permanent Java Card runtime environment
     * Entry Point Objects and can be accessed from any applet context.
     * References to these permanent objects can be stored and re-used.
     * <p>See <em>Runtime Environment Specification for the Java Card Platform</em>, section 6.2.1 for details.
     * @param buffer byte array containing the AID bytes
     * @param offset offset within buffer where AID bytes begin
     * @param length length of AID bytes in buffer
     * @return the <code>AID</code> object, if any; <code>null</code> otherwise. A VM exception
     * is thrown if <code>buffer</code> is <code>null</code>,
     * or if <code>offset</code> or <code>length</code> are out of range.
     */
    public static AID lookupAID(byte buffer[], short offset, byte length) {
        return runtime.lookupAID(buffer, offset, length);
    }

    /**
     * Begins an atomic transaction. If a transaction is already in
     * progress (transaction nesting depth level != 0), a TransactionException is
     * thrown.
     * <p>Note:
     * <ul>
     * <li><em>This method may do nothing if the <code>Applet.register()</code>
     * method has not yet been invoked. In case of tear or failure prior to successful
     * registration, the Java Card runtime environment will roll back all atomically updated persistent state.</em>
     * </ul>
     * @throws TransactionException  with the following reason codes:
     * <ul>
     * <li><code>TransactionException.IN_PROGRESS</code> if a transaction is already in progress.
     * </ul>
     * @see #abortTransaction()
     * @see #commitTransaction()
     */
    public static void beginTransaction() {
        if (transactionDepth != 0) {
            TransactionException.throwIt(TransactionException.IN_PROGRESS);
        }
        transactionDepth = 1;
    }

    /**
     * Aborts the atomic transaction. The contents of the commit
     * buffer is discarded.
     * <p>Note:
     * <ul>
     * <li><em>This method may do nothing if the <code>Applet.register()</code>
     * method has not yet been invoked. In case of tear or failure prior to successful
     * registration, the Java Card runtime environment will roll back all atomically updated persistent state.</em>
     * <li><em>Do not call this method from within a transaction which creates new objects because
     * the Java Card runtime environment may not recover the heap space used by the new object instances.</em>
     * <li><em>Do not call this method from within a transaction which creates new objects because
     * the Java Card runtime environment may, to ensure the security of the card and to avoid heap space loss,
     * lock up the card session to force tear/reset processing.</em>
     * <li><em>The Java Card runtime environment ensures that any variable of reference type which references an object
     * instantiated from within this aborted transaction is equivalent to
     * a </em><code>null</code><em> reference.</em>
     * </ul>
     * @throws TransactionException - with the following reason codes:
     * <ul>
     * <li><code>TransactionException.NOT_IN_PROGRESS</code> if a transaction is not in progress.
     * </ul>
     * @see #beginTransaction()
     * @see #commitTransaction()
     */
    public static void abortTransaction() {
        if (transactionDepth == 0) {
            TransactionException.throwIt(TransactionException.NOT_IN_PROGRESS);
        }
        transactionDepth = 0;
    }

    /**
     * Commits an atomic transaction. The contents of commit
     * buffer is atomically committed. If a transaction is not in
     * progress (transaction nesting depth level == 0) then a TransactionException is
     * thrown.
     * <p>Note:
     * <ul>
     * <li><em>This method may do nothing if the <code>Applet.register()</code>
     * method has not yet been invoked. In case of tear or failure prior to successful
     * registration, the Java Card runtime environment will roll back all atomically updated persistent state.</em>
     * </ul>
     * @throws TransactionException ith the following reason codes:
     * <ul>
     * <li><code>TransactionException.NOT_IN_PROGRESS</code> if a transaction is not in progress.
     * </ul>
     * @see #beginTransaction()
     * @see #abortTransaction()
     */
    public static void commitTransaction() {
        if (transactionDepth == 0) {
            TransactionException.throwIt(TransactionException.NOT_IN_PROGRESS);
        }
        transactionDepth = 0;
    }

    /**
     * Returns the current transaction nesting depth level. At present,
     * only 1 transaction can be in progress at a time.
     * @return 1 if transaction in progress, 0 if not
     */
    public static byte getTransactionDepth() {
        return transactionDepth;
    }

    /**
     * Returns the number of bytes left in the commit buffer.
     * <p> Note:
     * <ul>
     * <li><em>Current method implementation returns 32767.</em>
     * </ul>
     * @return the number of bytes left in the commit buffer
     * @see #getMaxCommitCapacity()
     */
    public static short getUnusedCommitCapacity() {
        return Short.MAX_VALUE;
    }

    /**
     * Returns the total number of bytes in the commit buffer.
     * This is approximately the maximum number of bytes of
     * persistent data which can be modified during a transaction.
     * However, the transaction subsystem requires additional bytes
     * of overhead data to be included in the commit buffer, and this
     * depends on the number of fields modified and the implementation
     * of the transaction subsystem. The application cannot determine
     * the actual maximum amount of data which can be modified during
     * a transaction without taking these overhead bytes into consideration.
     * <p> Note:
     * <ul>
     * <li><em>Current method implementation returns 32767.</em>
     * </ul>
     * @return the total number of bytes in the commit buffer
     * @see #getUnusedCommitCapacity()
     */
    public static short getMaxCommitCapacity() {
        return Short.MAX_VALUE;
    }

    /**
     * Obtains the Java Card runtime environment-owned instance of the <code>AID</code> object associated
     * with the previously active applet context. This method is typically used by a server applet,
     * while executing a shareable interface method to determine the identity of its client and
     * thereby control access privileges.
     * <p>Java Card runtime environment-owned instances of <code>AID</code> are permanent Java Card runtime environment
     * Entry Point Objects and can be accessed from any applet context.
     *  References to these permanent objects can be stored and re-used.
     * <p>See <em>Runtime Environment Specification for the Java Card Platform</em>, section 6.2.1 for details.
     * @return the <code>AID</code> object of the previous context, or <code>null</code> if Java Card runtime environment
     */
    public static AID getPreviousContextAID() {
        return runtime.getPreviousContextAID();
    }

    /**
     * Current method implementation returns 32767.
     * @return 32767
     */
    public static short getAvailablePersistentMemory() {
        return Short.MAX_VALUE;
    }

    /**
     * Current method implementation returns 32767.
     * @return 32767
     */
    public static short getAvailableTransientResetMemory() {
        return Short.MAX_VALUE;
    }

    /**
     * Current method implementation returns 32767.
     * @return 32767
     */
    public static short getAvailableTransientDeselectMemory() {
        return Short.MAX_VALUE;
    }

    /**
     * Called by a client applet to get a server applet's
     * shareable interface object. <p>This method returns <code>null</code>
     * if:
     * <ul>
     *  <li>the <code>Applet.register()</code> has not yet been invoked</li>
     *  <li>the server does not exist</li>
     *  <li>the server returns <code>null</code></li>
     * </ul>
     * @param serverAID the AID of the server applet
     * @param parameter optional parameter data
     * @return the shareable interface object or <code>null</code>
     * @see Applet.getShareableInterfaceObject(AID, byte)
     */
    public static Shareable getSharedObject(AID serverAID, byte parameter) {
        Applet serverApplet = runtime.getApplet(serverAID);
        if (serverApplet != null) {
            return serverApplet.getShareableInterfaceObject(runtime.getAID(),
                    parameter);
        }
        return null;
    }

    /**
     * Alway return false
     * @return false value
     */
    public static boolean isObjectDeletionSupported() {
        return false;
    }

    /**
     * Always throw SystemException.ILLEGAL_USE
     */
    public static void requestObjectDeletion() {
        SystemException.throwIt(SystemException.ILLEGAL_USE);
    }

    public static byte getCurrentlySelectedChannel() {
        return currentChannel;
    }

    /**
     * This method is used to determine if the specified applet is
     * active on the card.
     * <p>Note:
     * <ul>
     * <li><em>This method returns <code>false</code> if the specified applet is
     * not active, even if its context is active.</em>
     * </ul>
     * @param theApplet the AID of the applet object being queried
     * @return <code>true</code> if and only if the applet specified by the
     * AID parameter is currently active on this or another logical channel
     */
    public static boolean isAppletActive(AID theApplet) {
        return (theApplet == runtime.getAID());
    }

    public static void sendAPDU(byte[] buffer, short bOff, short len) {
        runtime.sendAPDU(buffer, bOff, len);
    }

    /**
     * This method is used by the applet to register <code>this</code> applet instance with
     * the Java Card runtime environment and to
     * assign the Java Card platform name of the applet as its instance AID bytes.
     * One of the <code>register()</code> methods must be called from within <code>install()</code>
     * to be registered with the Java Card runtime environment.
     * See <em>Runtime Environment Specification for the Java Card Platform</em>, section 3.1 for details.
     * <p>Note:<ul>
     * <li><em>The phrase "Java Card platform name of the applet" is a reference to the </em><code>AID[AID_length]</code><em>
     * item in the </em><code>applets[]</code><em> item of the </em><code>applet_component</code><em>, as documented in Section 6.5
     * Applet Component in the Virtual Machine Specification for the Java Card Platform.</em>
     * </ul>
     * @throws SystemException with the following reason codes:<ul>
     * <li><code>SystemException.ILLEGAL_AID</code> if the <code>Applet</code> subclass AID bytes are in use or
     * if the applet instance has previously successfully registered with the Java Card runtime environment via one of the
     * <code>register()</code> methods or if a Java Card runtime environment initiated <code>install()</code> method execution is not in progress.
     * </ul>
     */
    public static void registerApplet(Applet applet) throws SystemException {
        runtime.registerApplet(null, applet);
    }

    /**
     * This method is used by the applet to register <code>this</code> applet instance with the Java Card runtime environment and
     * assign the specified AID bytes as its instance AID bytes.
     * One of the <code>register()</code> methods must be called from within <code>install()</code>
     * to be registered with the Java Card runtime environment.
     * See <em>Runtime Environment Specification for the Java Card Platform</em>, section 3.1 for details.
     * <p>Note:<ul>
     * <li><em>The implementation may require that the instance AID bytes specified are the same as that
     * supplied in the install parameter data. An ILLEGAL_AID exception may be thrown otherwise.</em>
     * </ul>
     */
    public static void registerApplet(Applet applet, byte[] bArray, short bOffset, byte bLength)
            throws SystemException {
        runtime.registerApplet(new AID(bArray, bOffset, bLength), applet);
    }

    /**
     * Select applet by it's AID
     * This method must be called before start working with applet instance
     * @param aid appletId
     * @return true if applet selection success
     * before
     */
    static boolean selectApplet(AID aid) {
        return runtime.selectApplet(aid);
    }

    /**
     * Transmit <code>commandAPDU</code> to previous selected applet
     * @param commandAPDU
     * @return responseAPDU
     */
    static byte[] transmitCommand(byte[] command) {
        return runtime.transmitCommand(command);
    }

    public static boolean isAppletSelecting(Applet aThis) {
        // TODO !!! rewrite
        return false;
    }
    
    /**
     * Return <code>SimulatorRuntime</code>
     * @return instance of the SimulatorRuntime
     */
    static SimulatorRuntime getRuntime() {
        return runtime;
    }
    
    public static void resetRuntime() {
        runtime.resetRuntime();
    }
    
    public static void setJavaOwner(Object obj, Object owner) {
    }
    
    public static Object getJavaOwner(Object obj) {
        return obj;
    }
    
    public static short getJavaContext(Object obj) {
        return 0;
    }
}
