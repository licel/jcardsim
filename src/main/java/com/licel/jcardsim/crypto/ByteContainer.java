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
package com.licel.jcardsim.crypto;

import java.math.BigInteger;
import javacard.framework.JCSystem;
import javacard.framework.Util;
import javacard.security.CryptoException;

/**
 * This class contains byte array, initialization flag of this
 * array and memory type
 */
public final class ByteContainer {

    private byte[] data;
    private boolean isInitialized;
    private byte memoryType;

    /**
     * Construct <code>ByteContainer</code>
     * with memory type <code>JCSystem.MEMORY_TYPE_PERSISTENT</code>
     */
    public ByteContainer() {
        this(JCSystem.MEMORY_TYPE_PERSISTENT);
    }

    /**
     * Construct <code>ByteContainer</code>
     * with defined memory type
     * @param memoryType  memoryType from JCSystem.MEMORY_..
     */
    public ByteContainer(byte memoryType) {
        isInitialized = false;
        this.memoryType = memoryType;
    }

    /**
     * Construct <code>ByteContainer</code>
     * with memory type <code>JCSystem.MEMORY_TYPE_PERSISTENT</code>
     * and fills it by byte representation of <code>BigInteger</code>
     * @param bInteger <code>BigInteger</code> object
     */
    public ByteContainer(BigInteger bInteger) {
        this(bInteger.toByteArray(), (short) 0, (short) bInteger.toByteArray().length);
    }

    /**
     * Construct <code>ByteContainer</code>
     * with memory type <code>JCSystem.MEMORY_TYPE_PERSISTENT</code>
     * and fills it by defined byte array
     * @param buff byte array
     * @param offset
     * @param length
     */
    public ByteContainer(byte[] buff, short offset, short length) {
        setBytes(buff, offset, length);
    }

    /**
     * Fills <code>ByteContainer</code>by byte representation of <code>BigInteger</code>
     * @param bInteger
     */
    public void setBigInteger(BigInteger bInteger) {
        setBytes(bInteger.toByteArray());
    }

    /**
     * Fills <code>ByteContainer</code>by defined byte array
     * @param buff
     */
    public void setBytes(byte[] buff) {
        setBytes(buff, (short) 0, (short) buff.length);
    }

    /**
     * Fills <code>ByteContainer</code>by defined byte array
     * @param buff
     * @param offset
     * @param length
     */
    public void setBytes(byte[] buff, short offset, short length) {
        if (data == null) {
            switch (memoryType) {
                case JCSystem.MEMORY_TYPE_TRANSIENT_DESELECT:
                    data = JCSystem.makeTransientByteArray(length, JCSystem.CLEAR_ON_DESELECT);
                    break;
                case JCSystem.MEMORY_TYPE_TRANSIENT_RESET:
                    data = JCSystem.makeTransientByteArray(length, JCSystem.CLEAR_ON_DESELECT);
                    break;
                default:
                    data = new byte[length];
                    break;
            }
        }
        Util.arrayCopy(buff, offset, data, (short) 0, length);
        isInitialized = true;
    }

    /**
     * Return <code>BigInteger</code> representation of the <code>ByteContainer</code>
     * @return BigInteger
     */
    public BigInteger getBigInteger() {
        if (!isInitialized) {
            CryptoException.throwIt(CryptoException.UNINITIALIZED_KEY);
        }
        return new BigInteger(1, data);
    }

    /**
     * Return transient plain byte array representation of the <code>ByteContainer</code>
     * @param event type of transient byte array
     * @return plain byte array
     */
    public byte[] getBytes(byte event) {
        if (!isInitialized) {
            CryptoException.throwIt(CryptoException.UNINITIALIZED_KEY);
        }
        byte[] result = JCSystem.makeTransientByteArray((short) data.length, event);
        getBytes(result, (short) 0);
        return result;
    }

    /**
     * Copy byte array representation of the <code>ByteContainer</code>
     * @param dest destination byte array
     * @param offset
     * @return bytes copies
     */
    public short getBytes(byte[] dest, short offset) {
        if (!isInitialized) {
            CryptoException.throwIt(CryptoException.UNINITIALIZED_KEY);
        }
        if (dest.length - offset < data.length) {
            CryptoException.throwIt(CryptoException.ILLEGAL_VALUE);
        }
        Util.arrayCopy(data, (short) 0, dest, offset, (short) data.length);
        // https://code.google.com/p/jcardsim/issues/detail?id=14
        return (short)data.length;
    }

    /**
     * Clear internal structure of the <code>ByteContainer</code>
     */
    public void clear() {
        if (data != null) {
            Util.arrayFillNonAtomic(data, (short) 0, (short) data.length, (byte) 0);
        }
        isInitialized = false;
    }

    public boolean isInitialized() {
        return isInitialized;
    }
}
