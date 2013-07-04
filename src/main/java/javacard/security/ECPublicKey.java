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
 * The <CODE>ECPublicKey</CODE> interface is used to verify signatures on signed data
 * using the ECDSA algorithm and to generate shared secrets using the ECDH
 * algorithm. An implementation of <CODE>ECPublicKey</CODE> interface must also implement
 * the <CODE>ECKey</CODE> interface methods.
 * <p>When all components of the key (W, A, B, G, R, Field) are
 * set, the key is initialized and ready for use.
 * <p>The notation used to describe parameters specific to the EC algorithm is
 * based on the naming conventions established in [IEEE P1363].
 * @see ECPrivateKey
 * @see KeyBuilder
 * @see Signature
 * @see KeyEncryption
 * @see KeyAgreement
 */
public interface ECPublicKey
        extends PublicKey, ECKey {

    /**
     * Sets the point of the curve comprising the public key. The point
     * should be specified as an octet string as per ANSI X9.62. A specific implementation need
     * not support the compressed form, but must support the uncompressed form
     * of the point.
     * The plain text data format is big-endian and right-aligned
     * (the least significant bit is the least significant bit of last byte).
     * Input parameter data is copied into the internal representation.
     * <p>Note:
     * <ul>
     * <li><em>If the key object implements the </em><code>javacardx.crypto.KeyEncryption</code><em>
     * interface and the </em><code>Cipher</code><em> object specified via </em><code>setKeyCipher()</code><em>
     * is not </em><code>null</code><em>, the key value is decrypted using the </em><code>Cipher</code><em> object.</em>
     * </ul>
     * @param buffer the input buffer
     * @param offset the offset into the input buffer at which the
     * point specification begins
     * @param length the byte length of the point specification
     * @throws CryptoException with the following reason code:
     * <ul>
     * <li><code>CryptoException.ILLEGAL_VALUE</code> if the input parameter
     * data format is incorrect, or if the input parameter data is inconsistent with the key length,
     * or if input data decryption is required and fails.
     * </ul>
     */
    public abstract void setW(byte[] buffer, short offset, short length)
            throws CryptoException;

    /**
     * Returns the point of the curve comprising the public key in plain text form.
     * The point is represented as an octet string in compressed or
     * uncompressed forms as per ANSI X9.62.
     * The data format is big-endian and right-aligned
     * (the least significant bit is the least significant bit of last byte).
     * @param buffer the output buffer
     * @param offset the offset into the output buffer at which the
     * point specification data is to begin
     * @return the byte length of the point specificiation
     * @throws CryptoException with the following reason code:
     * <ul>
     * <li><code>CryptoException.UNINITIALIZED_KEY</code> if the point of the curve
     * comprising the public key has not been
     * successfully initialized since the
     * time the initialized state of the key was set to false.
     * </ul>
     * @see Key
     */
    public abstract short getW(byte[] buffer, short offset)
            throws CryptoException;
}