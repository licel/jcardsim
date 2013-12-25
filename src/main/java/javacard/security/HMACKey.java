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
 * HMACKey contains a key for HMAC operations. This key can be of any length,
 * but it is strongly recommended that the key is not shorter than the byte
 * length of the hash output used in the HMAC implementation. Keys with length
 * greater than the hash block length are first hashed with the hash algorithm
 * used for the HMAC implementation. Implementations must support an HMAC key
 * length equal to the length of the supported hash algorithm block size (e.g 64
 * bits for SHA-1) When the key data is set, the key is initialized and ready
 * for use.
 *
 * @see: KeyBuilder, Signature, javacardx.crypto.Cipher,
 * javacardx.crypto.KeyEncryption
 */
public interface HMACKey extends SecretKey {
   /**
     * Returns the Key data in plain text. The key can be any length, but should
     * be longer than the byte length of the hash algorithm output used. The
     * data format is big-endian and right-aligned (the least significant bit is
     * the least significant bit of last byte).
     */
    public abstract byte getKey(byte[] keyData, short kOff);
    /**
     * Sets the Key data. The data format is big-endian and right-aligned (the
     * least significant bit is the least significant bit of last byte). Input
     * key data is copied into the internal representation. Note: If the key
     * object implements the javacardx.crypto.KeyEncryption interface and the
     * Cipher object specified via setKeyCipher() is not null, keyData is
     * decrypted using the Cipher object.
     */
    public abstract void setKey(byte[] keyData, short kOff, short kLen) throws CryptoException, NullPointerException, ArrayIndexOutOfBoundsException;
}
