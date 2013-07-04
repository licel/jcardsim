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
 * The <CODE>ECPrivateKey</CODE> interface is used to generate signatures on data using
 * the ECDSA (Elliptic Curve Digital Signature Algorithm) and to generate shared secrets
 * using the ECDH (Elliptic Curve Diffie-Hellman) algorithm. An implementation of
 * <CODE>ECPrivateKey</CODE> interface must also implement
 * the <CODE>ECKey</CODE> interface methods.
 * <p>When all components of the key (S, A, B, G, R, Field) are
 * set, the key is initialized and ready for use.
 * In addition, the <code>KeyAgreement</code> algorithm type
 * <CODE>ALG_EC_SVDP_DHC</CODE> requires that the cofactor, K, be initialized.
 * <p>The notation used to describe parameters specific to the EC algorithm is
 * based on the naming conventions established in [IEEE P1363].
 * @see ECPublicKey
 * @see KeyBuilder
 * @see Signature
 * @see KeyEncryption
 * @see KeyAgreement
 */
public interface ECPrivateKey
        extends PrivateKey, ECKey {

    /**
     * Sets the value of the secret key.
     * The plain text data format is big-endian and right-aligned
     * (the least significant bit is the least significant bit of last byte).
     * Input parameter data is copied into the internal representation.
     * <p>Note:<ul>
     * <li><em>If the key object implements the </em><code>javacardx.crypto.KeyEncryption</code><em>
     * interface and the </em><code>Cipher</code><em> object specified via </em><code>setKeyCipher()</code><em>
     * is not </em><code>null</code><em>, the key value is decrypted using the </em><code>Cipher</code><em> object.</em>
     * </ul>
     * @param buffer the input buffer
     * @param offset the offset into the input buffer at which the
     * secret value is to begin
     * @param length the byte length of the secret value
     * @throws CryptoException with the following reason code:
     * <ul>
     * <li><code>CryptoException.ILLEGAL_VALUE</code> if the input key data is inconsistent
     * with the key length or if input data decryption is required and fails.
     * </ul>
     */
    public abstract void setS(byte[] buffer, short offset, short length)
            throws CryptoException;

    /**
     * Returns the value of the secret key in plaintext form.
     * The data format is big-endian and right-aligned
     * (the least significant bit is the least significant bit of last byte).
     * @param buffer the output buffer
     * @param offset the offset into the input buffer at which the
     * secret value is to begin
     * @return the byte length of the secret value
     * @throws CryptoException with the following reason code:
     * <ul>
     * <li><code>CryptoException.UNINITIALIZED_KEY</code> if the value of the secret key
     * has not been
     * successfully initialized since the
     * time the initialized state of the key was set to false.
     * </ul>
     * @see Key
     */
    public abstract short getS(byte[] buffer, short offset)
            throws CryptoException;
}

