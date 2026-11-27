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

package com.licel.jcardsim.base;

import javacard.framework.JCSystem;
import javacard.framework.SystemException;

import java.util.*;

public class SensitiveMemory {
    private final Set<Object> persistentArrays = new HashSet<>();
    private final Set<Object> transientArrays = new HashSet<>();

    public void registerArray(Object array, byte memoryType) {
        if (array == null) {
            throw new NullPointerException();
        }

        switch (memoryType) {
            case JCSystem.MEMORY_TYPE_PERSISTENT:
                persistentArrays.add(array);
                break;

            case JCSystem.MEMORY_TYPE_TRANSIENT_RESET:
            case JCSystem.MEMORY_TYPE_TRANSIENT_DESELECT:
                transientArrays.add(array);
                break;

            default:
                SystemException.throwIt(SystemException.ILLEGAL_VALUE);
        }
    }

    public boolean isSensitive(Object array) {
        if (array == null) {
            throw new NullPointerException();
        }

        return persistentArrays.contains(array) || transientArrays.contains(array);
    }

    public void assertIntegrity(Object array) {
        if (array == null) {
            throw new NullPointerException();
        }

        if (!isSensitive(array)) {
            SystemException.throwIt(SystemException.ILLEGAL_VALUE);
        }
    }

    public short clearArray(Object array) {
        if (array == null) {
            throw new NullPointerException();
        }

        if (!isSensitive(array)) {
            SystemException.throwIt(SystemException.ILLEGAL_VALUE);
        }

        return zero(array);
    }

    protected void zero(Set<Object> arrays) {
        for (Object obj : arrays) {
            zero(obj);
        }
    }

    private short zero(Object obj) {
        if( !(obj instanceof byte[]) && !(obj instanceof short[]) && !(obj instanceof boolean[]) && !(obj instanceof Object[]) ) {
            SystemException.throwIt(SystemException.ILLEGAL_VALUE);
        }

        if (obj instanceof byte[]) {
            byte[] array = (byte[]) obj;
            Arrays.fill(array, (byte) 0);
            return (short) array.length;
        }

        if (obj instanceof short[]) {
            short[] array = (short[]) obj;
            Arrays.fill(array, (short) 0);
            return (short) array.length;
        }

        if (obj instanceof int[]) {
            int[] array = (int[]) obj;
            Arrays.fill(array, 0);
            return (short) array.length;
        }

        if (obj instanceof Object[]) {
            Object[] array = (Object[]) obj;
            Arrays.fill(array, null);
            return (short) array.length;
        }

        if (obj instanceof boolean[]) {
            boolean[] array = (boolean[]) obj;
            Arrays.fill(array, false);
            return (short) array.length;
        }

        return 0;
    }

    public short size() {
        return (short) ( persistentArrays.size() + transientArrays.size() );
    }

    public void clearAll() {
        zero(persistentArrays);
        zero(transientArrays);

        persistentArrays.clear();
        transientArrays.clear();
    }
}
