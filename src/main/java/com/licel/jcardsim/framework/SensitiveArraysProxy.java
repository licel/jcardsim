/*
 * Copyright 2026 Licel LLC.
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
package com.licel.jcardsim.framework;

import com.licel.jcardsim.base.SensitiveMemory;
import com.licel.jcardsim.base.SimulatorSystem;
import javacard.framework.JCSystem;
import javacard.framework.SystemException;
import javacard.framework.TransactionException;

public final class SensitiveArraysProxy {

    private SensitiveArraysProxy() {
    }

    private static SensitiveMemory sensitiveMemory() {
        return SimulatorSystem.instance().getSensitiveMemory();
    }

    public static boolean isIntegritySensitiveArraysSupported() {
        return true;
    }

    public static Object makeIntegritySensitiveArray( byte type, byte memoryType, short length) {
        if (length < 0) {
            throw new NegativeArraySizeException();
        }

        final Object array;

        switch (type) {

            case JCSystem.ARRAY_TYPE_BOOLEAN:
                array = createBooleanArray(memoryType, length);
                break;

            case JCSystem.ARRAY_TYPE_BYTE:
                array = createByteArray(memoryType, length);
                break;

            case JCSystem.ARRAY_TYPE_SHORT:
                array = createShortArray(memoryType, length);
                break;

            case JCSystem.ARRAY_TYPE_OBJECT:
                array = createObjectArray(memoryType, length);
                break;

            case JCSystem.ARRAY_TYPE_INT:
                array = createIntArray(memoryType, length);
                break;

            default:
                SystemException.throwIt(SystemException.ILLEGAL_VALUE);
                return null;
        }

        sensitiveMemory().registerArray(array, memoryType);

        return array;
    }

    public static boolean isIntegritySensitive(Object obj) {
        return sensitiveMemory().isSensitive(obj);
    }

    public static void assertIntegrity(Object obj) {
        sensitiveMemory().assertIntegrity(obj);
    }

    public static short clearArray(Object obj) throws TransactionException {
        return sensitiveMemory().clearArray(obj);
    }

    private static byte[] createByteArray(byte memoryType, short length) {
        switch (memoryType) {
            case JCSystem.MEMORY_TYPE_PERSISTENT:
                return new byte[length];

            case JCSystem.MEMORY_TYPE_TRANSIENT_RESET:
                return JCSystem.makeTransientByteArray(length, JCSystem.CLEAR_ON_RESET);

            case JCSystem.MEMORY_TYPE_TRANSIENT_DESELECT:
                return JCSystem.makeTransientByteArray(length, JCSystem.CLEAR_ON_DESELECT);

            default:
                SystemException.throwIt(SystemException.ILLEGAL_VALUE);
                return null;
        }
    }

    private static boolean[] createBooleanArray(byte memoryType, short length) {
        switch (memoryType) {
            case JCSystem.MEMORY_TYPE_PERSISTENT:
                return new boolean[length];

            case JCSystem.MEMORY_TYPE_TRANSIENT_RESET:
                return JCSystem.makeTransientBooleanArray(length, JCSystem.CLEAR_ON_RESET);

            case JCSystem.MEMORY_TYPE_TRANSIENT_DESELECT:
                return JCSystem.makeTransientBooleanArray(length, JCSystem.CLEAR_ON_DESELECT);

            default:
                SystemException.throwIt(SystemException.ILLEGAL_VALUE);
                return null;
        }
    }

    private static short[] createShortArray(byte memoryType, short length) {
        switch (memoryType) {
            case JCSystem.MEMORY_TYPE_PERSISTENT:
                return new short[length];

            case JCSystem.MEMORY_TYPE_TRANSIENT_RESET:
                return JCSystem.makeTransientShortArray(length, JCSystem.CLEAR_ON_RESET);

            case JCSystem.MEMORY_TYPE_TRANSIENT_DESELECT:
                return JCSystem.makeTransientShortArray(length, JCSystem.CLEAR_ON_DESELECT);

            default:
                SystemException.throwIt(SystemException.ILLEGAL_VALUE);
                return null;
        }
    }

    private static Object[] createObjectArray(byte memoryType, short length) {
        switch (memoryType) {

            case JCSystem.MEMORY_TYPE_PERSISTENT:
                return new Object[length];

            case JCSystem.MEMORY_TYPE_TRANSIENT_RESET:
                return JCSystem.makeTransientObjectArray(length, JCSystem.CLEAR_ON_RESET);

            case JCSystem.MEMORY_TYPE_TRANSIENT_DESELECT:
                return JCSystem.makeTransientObjectArray(length, JCSystem.CLEAR_ON_DESELECT);

            default:
                SystemException.throwIt(SystemException.ILLEGAL_VALUE);
                return null;
        }
    }

    private static int[] createIntArray(byte memoryType, short length) {
        if (memoryType != JCSystem.MEMORY_TYPE_PERSISTENT) {
            SystemException.throwIt(SystemException.ILLEGAL_VALUE);
        }

        return new int[length];
    }
}