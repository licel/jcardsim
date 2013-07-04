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

import java.util.ArrayList;
import javacard.framework.JCSystem;
import javacard.framework.SystemException;

/**
 * Basic implementation of storage transient memory of JCRE
 */
public class TransientMemory {

    ArrayList clearOnDeselect = new ArrayList();
    ArrayList clearOnReset = new ArrayList();

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
    boolean[] makeBooleanArray(short length, byte event) {
        boolean[] array = new boolean[length];
        storeArray(array, event);
        return array;
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
    byte[] makeByteArray(short length, byte event) {
        byte[] array = new byte[length];
        storeArray(array, event);
        return array;
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
    short[] makeShortArray(short length, byte event) {
        short[] array = new short[length];
        storeArray(array, event);
        return array;
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
    Object[] makeObjectArray(short length, byte event) {
        Object[] array = new Object[length];
        storeArray(array, event);
        return array;
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
    byte isTransient(Object theObj) {
        if (clearOnDeselect.contains(theObj)) {
            return JCSystem.CLEAR_ON_DESELECT;
        } else if (clearOnReset.contains(theObj)) {
            return JCSystem.CLEAR_ON_RESET;
        }
        return JCSystem.NOT_A_TRANSIENT_OBJECT;
    }

    /**
     * Store <code>arrayRef</code> in memory depends by event type
     * @param arrayRef array reference
     * @param event event type
     */
    private void storeArray(Object arrayRef, byte event) {
        switch (event) {
            case JCSystem.CLEAR_ON_DESELECT:
                clearOnDeselect.add(arrayRef);
                break;
            case JCSystem.CLEAR_ON_RESET:
                clearOnReset.add(arrayRef);
                break;
            default:
                SystemException.throwIt(SystemException.ILLEGAL_VALUE);
        }
    }
}
