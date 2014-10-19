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

import java.lang.reflect.Constructor;

/**
 * Base implementation of <code>JCSystem</code>
 * @see JCSystem
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

    private static final APDU shortAPDU = createAPDU(false);
    private static final APDU extendedAPDU = createAPDU(true);
    private static boolean enableExtendApdu = false;

    private static APDU createAPDU(boolean extended) {
        try {
            Constructor<?> ctor = APDU.class.getDeclaredConstructors()[0];
            ctor.setAccessible(true);
            return (APDU) ctor.newInstance(extended);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private SimulatorSystem() {
        nullPointerException = new NullPointerException();
        securityException = new SecurityException();
    }

    /**
     * @see javacard.framework.JCSystem#isTransient(Object)
     */
    public static byte isTransient(Object theObj) {
        return transientMemory.isTransient(theObj);
    }

    /**
     * @see javacard.framework.JCSystem#makeTransientBooleanArray(short, byte)
     */
    public static boolean[] makeTransientBooleanArray(short length, byte event) {
        return transientMemory.makeBooleanArray(length, event);
    }

    /**
     * @see javacard.framework.JCSystem#makeTransientByteArray(short, byte)
     */
    public static byte[] makeTransientByteArray(short length, byte event) {
        return transientMemory.makeByteArray(length, event);
    }

    /**
     * Reserve <code>CLEAR_ON_RESET</code> memory for internal use
     * @see javacard.framework.JCSystem#makeTransientByteArray(short, byte)
     */
    public static byte[] makeInternalBuffer(int length) {
        return transientMemory.makeByteArray(length, JCSystem.CLEAR_ON_RESET);
    }

    /**
     * @see javacard.framework.JCSystem#makeTransientShortArray(short, byte)
     */
    public static short[] makeTransientShortArray(short length, byte event) {
        return transientMemory.makeShortArray(length, event);
    }

    /**
     * @see javacard.framework.JCSystem#makeTransientObjectArray(short, byte)
     */
    public static Object[] makeTransientObjectArray(short length, byte event) {
        return transientMemory.makeObjectArray(length, event);
    }

    /**
     * @see javacard.framework.JCSystem#getAID()
     */
    public static AID getAID() {
        return runtime.getAID();
    }

    /**
     * @see javacard.framework.JCSystem#lookupAID(byte[], short, byte)
     */
    public static AID lookupAID(byte buffer[], short offset, byte length) {
        return runtime.lookupAID(buffer, offset, length);
    }

    /**
     * @see javacard.framework.JCSystem#beginTransaction()
     */
    public static void beginTransaction() {
        if (transactionDepth != 0) {
            TransactionException.throwIt(TransactionException.IN_PROGRESS);
        }
        transactionDepth = 1;
    }

    /**
     * @see javacard.framework.JCSystem#abortTransaction()
     */
    public static void abortTransaction() {
        if (transactionDepth == 0) {
            TransactionException.throwIt(TransactionException.NOT_IN_PROGRESS);
        }
        transactionDepth = 0;
    }

    /**
     * @see javacard.framework.JCSystem#commitTransaction()
     */
    public static void commitTransaction() {
        if (transactionDepth == 0) {
            TransactionException.throwIt(TransactionException.NOT_IN_PROGRESS);
        }
        transactionDepth = 0;
    }

    /**
     * @see javacard.framework.JCSystem#getTransactionDepth()
     */
    public static byte getTransactionDepth() {
        return transactionDepth;
    }

    /**
     * @see javacard.framework.JCSystem#getUnusedCommitCapacity()
     * @return The current implementation always returns 32767
     */
    public static short getUnusedCommitCapacity() {
        return Short.MAX_VALUE;
    }

    /**
     * @see javacard.framework.JCSystem#getMaxCommitCapacity()
     * @return The current implementation always returns 32767
     */
    public static short getMaxCommitCapacity() {
        return Short.MAX_VALUE;
    }

    /**
     * @see javacard.framework.JCSystem#getPreviousContextAID()
     */
    public static AID getPreviousContextAID() {
        return runtime.getPreviousContextAID();
    }

    /**
     * @see javacard.framework.JCSystem#getAvailableMemory(byte)
     * @return The current implementation always returns 32767
     */
    public static short getAvailablePersistentMemory() {
        return Short.MAX_VALUE;
    }

    /**
     * @see javacard.framework.JCSystem#getAvailableMemory(byte)
     * @return The current implementation always returns 32767
     */
    public static short getAvailableTransientResetMemory() {
        return Short.MAX_VALUE;
    }

    /**
     * @see javacard.framework.JCSystem#getAvailableMemory(byte)
     * @return The current implementation always returns 32767
     */
    public static short getAvailableTransientDeselectMemory() {
        return Short.MAX_VALUE;
    }

    /**
     * @see javacard.framework.JCSystem#getAppletShareableInterfaceObject(javacard.framework.AID, byte)
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
     * @see javacard.framework.JCSystem#isObjectDeletionSupported()
     */
    public static boolean isObjectDeletionSupported() {
        return false;
    }

    /**
     * Always throw SystemException.ILLEGAL_USE
     */
    public static void requestObjectDeletion() {
        // do nothing
    }

    public static byte getCurrentlySelectedChannel() {
        return currentChannel;
    }

    /**
     * @see javacard.framework.JCSystem#isAppletActive(javacard.framework.AID)
     */
    public static boolean isAppletActive(AID theApplet) {
        return (theApplet == runtime.getAID());
    }

    public static void sendAPDU(byte[] buffer, short bOff, short len) {
        runtime.sendAPDU(buffer, bOff, len);
    }

    /**
     * @see javacard.framework.Applet#register()
     */
    public static void registerApplet(Applet applet) throws SystemException {
        runtime.registerApplet(null, applet);
    }

    /**
      * @see javacard.framework.Applet#register()(byte[], short, byte)
      */
    public static void registerApplet(Applet applet, byte[] bArray, short bOffset, byte bLength)
            throws SystemException {
        runtime.registerApplet(new AID(bArray, bOffset, bLength), applet);
    }
    
    /**
     * Select applet by It's AID
     * This method or selectApplet() must be called before start working with applet instance
     * @param aid appletId
     * @return data from select operation
     */
    static byte[] selectAppletWithResult(AID aid) {
    	return runtime.selectApplet(aid);
    }

    /**
     * Transmit <code>commandAPDU</code> to previous selected applet
     * @param commandAPDU commandAPDU
     * @return responseAPDU
     */
    static byte[] transmitCommand(byte[] commandAPDU) {
        return runtime.transmitCommand(commandAPDU);
    }

    /**
     * Return if the applet is currently being selected
     * @param aThis applet
     * @return true if applet is being selected
     */
    public static boolean isAppletSelecting(Applet aThis) {
    	return runtime.isAppletSelecting(aThis);
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

    protected static void setExtendedApduMode(boolean enabled) {
        enableExtendApdu = enabled;
    }

    public static APDU getCurrentAPDU() {
        return enableExtendApdu ? extendedAPDU : shortAPDU;
    }
}
