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

/**
 * <code>AESKey</code> contains a 16/24/32 byte key for AES computations based
 * on the Rijndael algorithm.
 * <p>When the key data is set, the key is initialized and ready for use.
 * <p>
 * @see KeyBuilder
 * @see Signature
 * @see Cipher
 * @see KeyEncryption
 */
public interface AESKey
        extends SecretKey {

    /**
     * Sets the <code>Key</code> data. The plaintext length of input key data is 16/24/32 bytes.
     * The data format is big-endian and right-aligned (the least significant bit is the least significant
     * bit of last byte). Input key data is copied into the internal representation.
     * <p>Note:
     * <ul>
     * <li><em>If the key object implements the </em><code>javacardx.crypto.KeyEncryption</code><em>
     * interface and the </em><code>Cipher</code><em> object specified via </em><code>setKeyCipher()</code><em>
     * is not </em><code>null</code><em>, </em><code>keyData</code><em> is decrypted using the </em><code>Cipher</code><em> object.</em>
     * </ul>
     * @param keyData byte array containing key initialization data
     * @param kOff offset within keyData to start
     * @throws CryptoException with the following reason code:
     * <ul>
     * <li><code>CryptoException.ILLEGAL_VALUE</code> if input data decryption is required and fails.
     * </ul>
     * @throws NullPointerException if the <CODE>keyData</CODE> parameter is
     * <CODE>null</CODE>.
     * @throws ArrayIndexOutOfBoundsException if <CODE>kOff</CODE> is negative
     * or the <CODE>keyData</CODE> array is too short.
     */
    public abstract void setKey(byte[] keyData, short kOff)
            throws CryptoException, NullPointerException, ArrayIndexOutOfBoundsException;

    /**
     * Returns the <code>Key</code> data in plain text. The length of output key data is 16/24/32 bytes.
     * The data format is big-endian and right-aligned (the least significant bit is the least significant
     * bit of last byte).
     *
     * @param keyData byte array to return key data
     * @param kOff offset within <code>keyData</code> to start
     * @return the byte length of the key data returned
     * @throws CryptoException with the following reason code:
     * <ul>
     * <li><code>CryptoException.UNINITIALIZED_KEY</code> if the key data has not been
     * successfully initialized since the
     * time the initialized state of the key was set to false.
     * </ul>
     */
    public abstract byte getKey(byte[] keyData, short kOff)
            throws CryptoException;
}