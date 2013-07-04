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
 * The <code>DSAPublicKey</code> interface is used to verify signatures
 * on signed data using the DSA algorithm.
 * An implementation of <code>DSAPublicKey</code> interface must also implement
 * the <code>DSAKey</code> interface methods.
 * <p>When all four components of the key (Y,P,Q,G) are set, the key is
 * initialized and ready for use.
 * @see DSAPrivateKey
 * @see KeyBuilder
 * @see Signature
 * @see KeyEncryption
 */
public interface DSAPublicKey
        extends PublicKey, DSAKey {

    /**
     * Sets the value of the key. When the base, prime and subprime parameters are initialized
     * and the key value is set, the key is ready for use.
     * The plain text data format is big-endian and right-aligned (the least significant bit is the least significant
     * bit of last byte). Input key data is copied into the internal representation.
     * <p>Note:
     * <ul>
     * <li><em>If the key object implements the </em><code>javacardx.crypto.KeyEncryption</code><em>
     * interface and the </em><code>Cipher</code><em> object specified via </em><code>setKeyCipher()</code><em>
     * is not </em><code>null</code><em>, the key value is decrypted using the </em><code>Cipher</code><em> object.</em>
     * </ul>
     * @param buffer the input buffer
     * @param offset the offset into the input buffer at which the key value begins
     * @param length the length of the key value
     * @throws CryptoException with the following reason code:
     * <ul>
     * <li><code>CryptoException.ILLEGAL_VALUE</code> if the input key data length is inconsistent
     * with the implementation or if input data decryption is required and fails.
     * </ul>
     */
    public abstract void setY(byte[] buffer, short offset, short length)
            throws CryptoException;

    /**
     * Returns the value of the key in plain text.
     * The data format is big-endian and right-aligned (the least significant bit is the least significant
     * bit of last byte).
     * @param buffer the output buffer
     * @param offset the offset into the input buffer at which the key value starts
     * @return the byte length of the key value returned
     * @throws CryptoException with the following reason code:
     * <ul>
     * <li><code>CryptoException.UNINITIALIZED_KEY</code> if the value of the key has not been
     * successfully initialized since the
     * time the initialized state of the key was set to false.
     * </ul>
     * @see Key
     */
    public abstract short getY(byte[] buffer, short offset)
            throws CryptoException;
}