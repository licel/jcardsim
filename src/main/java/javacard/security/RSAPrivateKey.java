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
 *
 * The <code>RSAPrivateKey</code> class is used to sign data using the RSA algorithm
 * in its modulus/exponent form. It may also be used by the <code>javacardx.crypto.Cipher</code> class
 * to encrypt/decrypt messages.
 * <p>When both the modulus and exponent of the key are set, the key is
 * initialized and ready for use.
 * @see RSAPublicKey
 * @see RSAPrivateCrtKey
 * @see KeyBuilder
 * @see Signature
 * @see Cipher
 * @see KeyEncryption
 */
public interface RSAPrivateKey
        extends PrivateKey {

    /**
     * Returns the private exponent value of the key in plain text.
     * The data format is big-endian and right-aligned (the least significant bit is the least significant
     * bit of last byte).
     * @param buffer the output buffer
     * @param offset the offset into the output buffer at which the modulus value starts
     * @return with the following reason code:<ul>
     * <li><code>CryptoException.UNINITIALIZED_KEY</code> if the private exponent value
     * of the key has not been
     * successfully initialized since the
     * time the initialized state of the key was set to false.
     * </ul>
     * @throws CryptoException with the following reason code:<ul>
     * <li><code>CryptoException.UNINITIALIZED_KEY</code> if the private exponent value
     * of the key has not been
     * successfully initialized since the
     * time the initialized state of the key was set to false.
     * </ul>
     */
    public abstract short getExponent(byte[] buffer, short offset)
            throws CryptoException;

    /**
     * Returns the modulus value of the key in plain text.
     * The data format is big-endian and right-aligned (the least significant bit is the least significant
     * bit of last byte).
     * @param buffer the output buffer
     * @param offset the offset into the output buffer at which the modulus value starts
     * @return the byte length of the modulus value returned
     * @throws CryptoException with the following reason code:<ul>
     * <li><code>CryptoException.UNINITIALIZED_KEY</code> if the modulus value
     * of the key has not been
     * successfully initialized since the
     * time the initialized state of the key was set to false.
     * </ul>
     */
    public abstract short getModulus(byte[] buffer, short offset)
            throws CryptoException;

    /**
     * Sets the private exponent value of the key.
     * The plain text data format is big-endian and right-aligned (the least significant bit is the least significant
     * bit of last byte). Input exponent data is copied into the internal representation.
     * <p>Note:<ul>
     * <li><em>If the key object implements the </em><code>javacardx.crypto.KeyEncryption</code><em>
     * interface and the </em><code>Cipher</code><em> object specified via </em><code>setKeyCipher()</code><em>
     * is not </em><code>null</code><em>, the exponent value is decrypted using the </em><code>Cipher</code><em> object.</em>
     * </ul>
     * @param buffer the input buffer
     * @param offset the offset into the input buffer at which the modulus value begins
     * @param length the length of the modulus
     * @throws CryptoException with the following reason code:<ul>
     * <li><code>CryptoException.ILLEGAL_VALUE</code> if the input exponent data length is inconsistent
     * with the implementation or if input data decryption is required and fails.
     * </ul>
     */
    public abstract void setExponent(byte[] buffer, short offset, short length)
            throws CryptoException;

    /**
     * Sets the modulus value of the key.
     * The plain text data format is big-endian and right-aligned (the least significant bit is the least significant
     * bit of last byte). Input modulus data is copied into the internal representation.
     * <p>Note:<ul>
     * <li><em>If the key object implements the </em><code>javacardx.crypto.KeyEncryption</code><em>
     * interface and the </em><code>Cipher</code><em> object specified via </em><code>setKeyCipher()</code><em>
     * is not </em><code>null</code><em>, the modulus value is decrypted using the </em><code>Cipher</code><em> object.</em>
     * </ul>
     * @param buffer the input buffer
     * @param offset the offset into the input buffer at which the modulus value begins
     * @param length the length of the modulus
     * @throws CryptoException with the following reason code:<ul>
     * <li><code>CryptoException.ILLEGAL_VALUE</code> if the input modulus data length is inconsistent
     * with the implementation or if input data decryption is required and fails.
     * </ul>
     */
    public abstract void setModulus(byte[] buffer, short offset, short length)
            throws CryptoException;
}
