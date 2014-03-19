/*
 * Copyright 2013 Licel LLC.
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
 * <code>HMACKey</code> contains a key for HMAC operations. This key can be of
 * any length, but it is strongly recommended that the key is not shorter than the
 * byte length of the hash output used in the HMAC implementation.
 * Keys with length greater than the hash block length are first hashed with the
 * hash algorithm used for the HMAC implementation. <p>
 * <p>Implementations must support an HMAC key length equal to the length of
 * the supported hash algorithm block size (e.g 64 bits for SHA-1)
 * <p>When the key data is set, the key is initialized and ready for use.
 * <p>
 * 
 * @see KeyBuilder
 * @see Signature 
 * @see javacardx.crypto.Cipher
 * @see javacardx.crypto.KeyEncryption
 * @since 2.2.2
 */

public interface HMACKey extends SecretKey {

    /**
     * Sets the
     * <code>Key</code> data.
     * The data format is big-endian and right-aligned (the least significant bit is the least significant
     * bit of last byte). Input key data is copied into the internal representation.
     * <p>Note:<ul>
     * <li><em>If the key object implements the </em><code>javacardx.crypto.KeyEncryption</code><em>
     * interface and the </em><code>Cipher</code><em> object specified via </em><code>setKeyCipher()</code><em>
     * is not </em><code>null</code><em>, </em><code>keyData</code><em> is decrypted using the </em><code>Cipher</code><em> object.</em>
     * </ul>
     * <P>
     *
     * @param keyData byte array containing key initialization data
     * @param kOff offset within keyData to start
     * @param kLen the byte length of the key initialization data
     * @throws CryptoException - with the following reason code: CryptoException.ILLEGAL_VALUE if input data decryption is required and fails.
     * @throws ArrayIndexOutOfBoundsException - if kOff is negative or the keyData array is too short
     * @throws NullPointerException - if the keyData parameter is null
     */
    void setKey(byte[] keyData, short kOff, short kLen) throws CryptoException, NullPointerException, ArrayIndexOutOfBoundsException;

    /**
     * Returns the
     * <code>Key</code> data in plain text. The key can be any length, but
     * should be longer than the byte length of the hash algorithm output used. The data
     * format is big-endian and right-aligned (the least significant bit is the least
     * significant bit of last byte).
     * <P>
     *
     * @param keyData byte array to return key data
     * @param kOff offset within keyData to start
     * @return the byte length of the key data returned
     * @throws CryptoException - with the following reason code: CryptoException.UNINITIALIZED_KEY if the key data has not been successfully initialized since the time the initialized state of the key was set to false.
     * @see Key
     */
    byte getKey(byte[] keyData, short kOff);
}
