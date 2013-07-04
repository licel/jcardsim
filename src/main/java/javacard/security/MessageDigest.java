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

import com.licel.jcardsim.crypto.MessageDigestImpl;
/**
 *
 * The <code>MessageDigest</code> class is the base class for hashing algorithms. Implementations of <code>MessageDigest</code>
 * algorithms must extend this class and implement all the abstract methods.
 * <p> A tear or card reset event resets a
 * <code>MessageDigest</code> object to the initial state (state upon construction).
 * <p> Even if a transaction is in progress, update of intermediate result state in the implementation
 * instance shall not participate in the transaction.
 */
public abstract class MessageDigest {

    /**
     * Message Digest algorithm SHA.
     */
    public static final byte ALG_SHA = 1;
    /**
     * Message Digest algorithm MD5.
     */
    public static final byte ALG_MD5 = 2;
    /**
     * Message Digest algorithm RIPE MD-160.
     */
    public static final byte ALG_RIPEMD160 = 3;

    /**
     * Protected Constructor
     */
    protected MessageDigest() {
    }

    /**
     * Creates a <code>MessageDigest</code> object instance of the selected algorithm.
     * @param algorithm the desired message digest algorithm.
     * Valid codes listed in ALG_ .. constants above, for example, <A HREF="../../javacard/security/MessageDigest.html#ALG_SHA"><CODE>ALG_SHA</CODE></A>.
     * @param externalAccess <code>true</code> indicates that the instance will be shared among
     * multiple applet instances and that the <code>MessageDigest</code> instance will also be accessed (via a <code>Shareable</code>.
     * interface) when the owner of the <code>MessageDigest</code> instance is not the currently selected applet.
     * If <code>true</code> the implementation must not allocate CLEAR_ON_DESELECT transient space for internal data.
     * @return the <code>MessageDigest</code> object instance of the requested algorithm
     * @throws CryptoException with the following reason codes:<ul>
     * <li><code>CryptoException.NO_SUCH_ALGORITHM</code> if the requested algorithm
     * or shared access mode is not supported.</ul>
     */
    public static final MessageDigest getInstance(byte algorithm, boolean externalAccess)
            throws CryptoException {
        if (externalAccess) {
            CryptoException.throwIt(CryptoException.NO_SUCH_ALGORITHM);
        }
        MessageDigest instance = new MessageDigestImpl(algorithm);
        return instance;
    }

    /**
     * Gets the Message digest algorithm.
     * @return the algorithm code defined above
     */
    public abstract byte getAlgorithm();

    /**
     * Returns the byte length of the hash.
     * @return hash length
     */
    public abstract byte getLength();

    /**
     * Generates a hash of all/last input data.
     * Completes and returns the hash computation after performing final operations such as padding.
     * The <code>MessageDigest</code> object is reset to the initial state after this call is made.
     * <p>The input and output buffer data may overlap.
     * @param inBuff the input buffer of data to be hashed
     * @param inOffset the offset into the input buffer at which to begin hash generation
     * @param inLength  the byte length to hash
     * @param outBuff the output buffer, may be the same as the input buffer
     * @param outOffset the offset into the output buffer where the resulting hash value begins
     * @return number of bytes of hash output in <code>outBuff</code>
     */
    public abstract short doFinal(byte[] inBuff, short inOffset, short inLength, byte[] outBuff, short outOffset);

    /**
     * Accumulates a hash of the input data. This method requires temporary storage of
     * intermediate results. In addition, if the input data length is not block aligned
     * (multiple of block size)
     * then additional internal storage may be allocated at this time to store a partial
     * input data block.
     * This may result in additional resource consumption and/or slow performance.
     * This method should only be used if all the input data required for the hash
     * is not available in one byte array.  If all of the input data required for
     * the hash is located in a single byte array, use of the <code>doFinal()</code>
     * method is recommended.  The <code>doFinal()</code>
     * method must be called to complete processing of input data accumulated by one or more
     * calls to the <code>update()</code> method.
     * <p>Note:<ul>
     * <li><em>If </em><code>inLength</code><em> is 0 this method does nothing.</em>
     * </ul>
     * @param inBuff the input buffer of data to be hashed
     * @param inOffset the offset into the input buffer at which to begin hash generation
     * @param inLength the byte length to hash
     * See also: {@link #doFinal(byte[], short, short, byte[], short)}
     */
    public abstract void update(byte[] inBuff, short inOffset, short inLength);

    /**
     * Resets the <code>MessageDigest</code> object to the initial state for further use.
     */
    public abstract void reset();

}