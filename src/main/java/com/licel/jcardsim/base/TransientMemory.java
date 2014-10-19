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

    ArrayList<Object> clearOnDeselect = new ArrayList<Object>();
    ArrayList<Object> clearOnReset = new ArrayList<Object>();

    /**
     * @see javacard.framework.JCSystem#makeTransientBooleanArray(short, byte)
     */
    boolean[] makeBooleanArray(short length, byte event) {
        boolean[] array = new boolean[length];
        storeArray(array, event);
        return array;
    }

    /**
     * @see javacard.framework.JCSystem#makeTransientByteArray(short, byte)
     */
    byte[] makeByteArray(int length, byte event) {
        byte[] array = new byte[length];
        storeArray(array, event);
        return array;
    }

    /**
     * @see javacard.framework.JCSystem#makeTransientShortArray(short, byte)
     */
    short[] makeShortArray(short length, byte event) {
        short[] array = new short[length];
        storeArray(array, event);
        return array;
    }

    /**
     * @see javacard.framework.JCSystem#makeTransientObjectArray(short, byte)
     */
    Object[] makeObjectArray(short length, byte event) {
        Object[] array = new Object[length];
        storeArray(array, event);
        return array;
    }

    /**
     * @see javacard.framework.JCSystem#isTransient(Object)
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
