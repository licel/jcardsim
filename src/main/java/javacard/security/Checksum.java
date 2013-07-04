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
package javacard.security;

import com.licel.jcardsim.crypto.CRC16;
import com.licel.jcardsim.crypto.CRC32;

/**
 * The <code>Checksum</code> class is the base class for CRC (cyclic redundancy check) checksum algorithms.
 * Implementations of Checksum algorithms must extend this class and implement all the abstract methods.
 * <p> A tear or card reset event resets a
 * <code>Checksum</code> object to the initial state (state upon construction).
 * <p> Even if a transaction is in progress, update of intermediate result state in the implementation
 * instance shall not participate in the transaction.
 */
public abstract class Checksum {

    /**
     * ISO/IEC 3309 compliant 16 bit CRC algorithm.
     */
    public static final byte ALG_ISO3309_CRC16 = 1;
    /**
     * ISO/IEC 3309 compliant 32 bit CRC algorithm.
     */
    public static final byte ALG_ISO3309_CRC32 = 2;

    /**
     * Creates a <code>Checksum</code> object instance of the selected algorithm.
     * @param algorithm the desired checksum algorithm.
     * @param externalAccess <code>true</code> indicates that the instance will be shared among
     * multiple applet instances and that the <code>Checksum</code> instance will also be accessed (via a <code>Shareable</code>.
     * interface) when the owner of the <code>Checksum</code> instance is not the currently selected applet.
     * If <code>true</code> the implementation must not allocate CLEAR_ON_DESELECT transient space for internal data.
     * @return the <code>Checksum</code> object instance of the requested algorithm.
     * @throws CryptoException  with the following reason codes:
     * <ul>
     * <li><code>CryptoException.NO_SUCH_ALGORITHM</code> if the requested algorithm
     * or shared access mode is not supported.
     * </ul>
     */
    public static final Checksum getInstance(byte algorithm, boolean externalAccess)
            throws CryptoException {
        if (externalAccess) {
            CryptoException.throwIt(CryptoException.NO_SUCH_ALGORITHM);
        }
        Checksum instance = null;
        switch (algorithm) {
            case ALG_ISO3309_CRC16:
                instance = new CRC16();
                break;

            case ALG_ISO3309_CRC32:
                instance = new CRC32();
                break;

            default:
                CryptoException.throwIt(CryptoException.NO_SUCH_ALGORITHM);
                break;
        }
        return instance;
    }

    /**
     * Protected Constructor
     */
    protected Checksum() {
    }

    /**
     * Gets the Checksum algorithm. Valid codes listed in ALG_ .. constants above, for example, {@link #ALG_ISO3309_CRC16}
     * @return the algorithm code defined above
     */
    public abstract byte getAlgorithm();

    /**
     * Resets and initializes the <code>Checksum</code> object with the algorithm specific
     * <p>Note:
     * <ul>
     *<li><em>The ALG_ISO3309_CRC16 algorithm expects 2 bytes of parameter information in
     *</em><code>bArray</code><em> representing the initial checksum value.</em>
     *<li><em>The ALG_ISO3309_CRC32 algorithm expects 4 bytes of parameter information in
     *</em><code>bArray</code><em> representing the initial checksum value.</em>
     *</ul>
     * @param bArray byte array containing algorithm specific initialization information
     * @param bOff offset within <code>bArray</code> where the algorithm specific data begins
     * @param bLen byte length of algorithm specific parameter data.
     * @throws CryptoException with the following reason codes:
     * <ul>
     * <li><code>CryptoException.ILLEGAL_VALUE</code> if a byte array parameter option is not supported by the algorithm or if
     * the <code>bLen</code> is an incorrect byte length for the algorithm specific data.
     * </ul>
     */
    public abstract void init(byte bArray[], short bOff, short bLen)
            throws CryptoException;

    /**
     * Generates a CRC checksum of all/last input data. The CRC engine processes input data
     * starting with the byte at offset <CODE>inOffset</CODE> and continuing on until the
     * byte at <CODE>(inOffset+inLength-1)</CODE> of the <CODE>inBuff</CODE> array. Within
     * each byte the processing proceeds from the least significant bit to the most.
     * <p>Completes and returns the checksum computation.
     * The <code>Checksum</code> object is reset to the initial state(state upon construction)
     * when this method completes.
     * <p>Note:
     * <ul>
     * <li><em>The ALG_ISO3309_CRC16 and ALG_ISO3309_CRC32 algorithms reset the initial checksum
     * value to 0. The initial checksum value can be re-initialized using the
     * </em>{@link #init(byte[], short, short) }<em> method.</em>
     * </ul>
     * <p>The input and output buffer data may overlap.
     * @param inBuff the input buffer of data to be checksummed
     * @param inOffset the offset into the input buffer at which to begin checksum generation
     * @param inLength the byte length to checksum
     * @param outBuff the output buffer, may be the same as the input buffer
     * @param outOffset the offset into the output buffer where the resulting checksum value begins
     * @return number of bytes of checksum output in <code>outBuff</code>
     */
    public abstract short doFinal(byte inBuff[], short inOffset, short inLength, byte outBuff[], short outOffset);

    /**
     * Accumulates a partial checksum of the input data. The CRC engine processes input data
     * starting with the byte at offset <CODE>inOffset</CODE> and continuing on until the
     * byte at <CODE>(inOffset+inLength-1)</CODE> of the <CODE>inBuff</CODE> array. Within
     * each byte the processing proceeds from the least significant bit to the most.
     * <p>This method requires temporary storage of intermediate results.
     * This may result in additional resource consumption and/or slow performance.
     * This method should only be used if all the input data required for the checksum
     * is not available in one byte array. The {@link  Checksum.html#doFinal(byte[], short, short, byte[], short)}
     * <CODE>doFinal(byte[], short, short, byte[], short)</CODE>  method is recommended whenever possible.
     * <p>Note:
     * <ul>
     * <li><em>If <code>inLength</code> is 0 this method does nothing.</em>
     * </ul>
     * @param inBuff the input buffer of data to be checksummed
     * @param inOffset the offset into the input buffer at which to begin checksum generation
     * @param inLength the byte length to checksum
     * @see #doFinal(byte[], short, short, byte[], short)
     */
    public abstract void update(byte inBuff[], short inOffset, short inLength);
}
