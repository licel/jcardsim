/*
 * Copyright 2014 Licel LLC.
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

/**
 *
 * The
 * <code>InitializedMessageDigest</code> class is a subclass of the base
 * class
 * <code>MessageDigest</code>. This class is used to generate a hash representing
 * a specified message but with the additional
 * capability to initialize the starting hash value corresponding to a previously
 * hashed part of the message.
 * Implementations of
 * <code>InitializedMessageDigest</code>
 * algorithms must extend this class and implement all the abstract methods.
 * <p> A tear or card reset event resets a
 * <code>InitializedMessageDigest</code> object to the initial state (state upon construction).
 * <p> Even if a transaction is in progress, update of intermediate result state in the implementation
 * instance shall not participate in the transaction.
 *
 * @since 2.2.2
 */
public abstract class InitializedMessageDigest extends MessageDigest {

    /**
     * protected constructor
     */
    protected InitializedMessageDigest() {
    }

    /**
     * This method initializes the starting hash value in place of the default
     * value used by the
     * <code>MessageDigest</code> superclass. The starting
     * hash value represents the previously computed
     * hash (using the same algorithm) of the first part of the message. The remaining
     * bytes of the message must be presented to this
     * <code>InitializedMessageDigest</code>
     * object via the
     * <code>update</code> and
     * <code>doFinal</code> methods to
     * generate the final message digest.
     * <p>Note:<ul>
     * <li><em>The maximum allowed value of the byte length of the first part of the message is
     * algorithm specific</em>
     * </ul>
     * <P>
     *
     * @param initialDigestBuf input buffer containing the starting hash value representing the previously
     * computed hash (using the same algorithm) of first part of the message
     * @param initialDigestOffset offset into <code>initialDigestBuf</code> array where initial digest value data begins
     * @param initialDigestLength the length of data in <code>initialDigestBuf</code> array.
     * @param digestedMsgLenBuf the byte array containing the number of bytes in the first part of the message that
     * has previously been hashed to obtain the specified initial digest value value
     * @param digestedMsgLenOffset the offset within <code>digestedMsgLenBuf</code> where the digested length
     * begins(the bytes starting at this offset for <code>digestedMsgLenLength</code> bytes are concatenated to form
     * the actual digested message length value)
     * @param digestedMsgLenLength byte length of the digested length
     * @throws CryptoException with the following reason codes: <code>CryptoException.ILLEGAL_VALUE</code>
     * if the parameter <code>initialDigestLength</code> is not equal to the length of message digest of the algorithm
     * (see LENGTH_* constants {@link MessageDigest#LENGTH_SHA}) or if the number of bytes in the first part of the message that
     * has previously been hashed is 0 or not a multiple of the algorithm's block size or greater than the maximum length
     * supported by the algorithm (see ALG_* algorithm descriptions {@link MessageDigest#ALG_SHA}).
     */
    public abstract void setInitialDigest(byte[] initialDigestBuf, short initialDigestOffset,
            short initialDigestLength, byte[] digestedMsgLenBuf, short digestedMsgLenOffset,
            short digestedMsgLenLength) throws CryptoException;
}
