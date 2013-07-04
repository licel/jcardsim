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
 * The </code>DSAKey</code> interface is the base interface for the DSA algorithm's private and
 * public key implementations. A DSA private key implementation must also implement
 * the <code>DSAPrivateKey</code> interface methods. A DSA public key implementation must also implement
 * the <code>DSAPublicKey</code> interface methods.
 * <p>When all four components of the key (X or Y,P,Q,G) are set, the key is
 * initialized and ready for use.
 * <p>
 * @see DSAPublicKey
 * @see DSAPrivateKey
 * @see KeyBuilder
 * @see Signature
 * @see KeyEncryption
 */
public interface DSAKey {

    /**
     * Sets the prime parameter value of the key.
     * The plain text data format is big-endian and right-aligned (the least significant bit is the least significant
     * bit of last byte). Input prime parameter data is copied into the internal representation.
     * <p>Note:
     * <ul>
     * <li><em>If the key object implements the </em><code>javacardx.crypto.KeyEncryption</code><em>
     * interface and the </em><code>Cipher</code><em> object specified via </em><code>setKeyCipher()</code><em>
     * is not </em><code>null</code><em>, the prime parameter value is decrypted using the </em><code>Cipher</code><em> object.</em>
     * </ul>
     * @param buffer the input buffer
     * @param offset the offset into the input buffer at which the prime parameter value begins
     * @param length the length of the prime parameter value
     * @throws CryptoException with the following reason code:
     * <ul>
     * <li><code>CryptoException.ILLEGAL_VALUE</code> if the input parameter data length is inconsistent
     * with the implementation or if input data decryption is required and fails.
     * </ul>
     */
    public abstract void setP(byte[] buffer, short offset, short length)
            throws CryptoException;

    /**
     * Sets the subprime parameter value of the key.
     * The plain text data format is big-endian and right-aligned (the least significant bit is the least significant
     * bit of last byte). Input subprime parameter data is copied into the internal representation.
     * <p>Note:
     * <ul>
     * <li><em>If the key object implements the </em><code>javacardx.crypto.KeyEncryption</code><em>
     * interface and the </em><code>Cipher</code><em> object specified via </em><code>setKeyCipher()</code><em>
     * is not </em><code>null</code><em>, the subprime parameter value is decrypted using the </em><code>Cipher</code><em> object.</em>
     * </ul>
     * @param buffer the input buffer
     * @param offset the offset into the input buffer at which the subprime parameter value begins
     * @param length the length of the subprime parameter value
     * @throws CryptoException with the following reason code:
     * <ul>
     * <li><code>CryptoException.ILLEGAL_VALUE</code> if the input parameter data length is inconsistent
     * with the implementation or if input data decryption is required and fails.
     * </ul>
     */
    public abstract void setQ(byte[] buffer, short offset, short length)
            throws CryptoException;

    /**
     * Sets the base parameter value of the key.
     * The plain text data format is big-endian and right-aligned (the least significant bit is the least significant
     * bit of last byte). Input base parameter data is copied into the internal representation.
     * <p>Note:
     * <ul>
     * <li><em>If the key object implements the </em><code>javacardx.crypto.KeyEncryption</code><em>
     * interface and the </em><code>Cipher</code><em> object specified via </em><code>setKeyCipher()</code><em>
     * is not </em><code>null</code><em>, the base parameter value is decrypted using the </em><code>Cipher</code><em> object.</em>
     * </ul>
     * @param buffer the input buffer
     * @param offset the offset into the input buffer at which the base parameter value begins
     * @param length the length of the base parameter value
     * @throws CryptoException with the following reason code:
     * <ul>
     * <li><code>CryptoException.ILLEGAL_VALUE</code> if the input parameter data length is inconsistent
     * with the implementation or if input data decryption is required and fails.
     * </ul>
     */
    public abstract void setG(byte[] buffer, short offset, short length)
            throws CryptoException;

    /**
     * Returns the prime parameter value of the key in plain text.
     * The data format is big-endian and right-aligned (the least significant bit is the least significant
     * bit of last byte).
     * @param buffer the output buffer
     * @param offset the offset into the output buffer at which the prime parameter value starts
     * @return the byte length of the prime parameter value returned
     * @throws CryptoException with the following reason code:
     * <ul>
     * <li><code>CryptoException.UNINITIALIZED_KEY</code> if the prime parameter has not been
     * successfully initialized since the
     * time the initialized state of the key was set to false.
     * </ul>
     * @see Key
     */
    public abstract short getP(byte[] buffer, short offset)
            throws CryptoException;

    /**
     * Returns the subprime parameter value of the key in plain text.
     * The data format is big-endian and right-aligned (the least significant bit is the least significant
     * bit of last byte).
     * @param buffer the output buffer
     * @param offset the offset into the output buffer at which the subprime parameter value begins
     * @return the byte length of the subprime parameter value returned
     * @throws CryptoException with the following reason code:
     * <ul>
     * <li><code>CryptoException.UNINITIALIZED_KEY</code> if the subprime parameter has not been
     * successfully initialized since the
     * time the initialized state of the key was set to false.
     * </ul>
     * @see Key
     */
    public abstract short getQ(byte[] buffer, short offset)
            throws CryptoException;

    /**
     * Returns the base parameter value of the key in plain text.
     * The data format is big-endian and right-aligned (the least significant bit is the least significant
     * bit of last byte).
     * @param buffer the output buffer
     * @param offset the offset into the output buffer at which the base parameter value begins
     * @return the byte length of the base parameter value returned
     * @throws CryptoException with the following reason code:
     * <ul>
     * <li><code>CryptoException.UNINITIALIZED_KEY</code> if the base parameter has not been
     * successfully initialized since the
     * time the initialized state of the key was set to false.
     * </ul>
     * @see Key
     */
    public abstract short getG(byte[] buffer, short offset)
            throws CryptoException;
}