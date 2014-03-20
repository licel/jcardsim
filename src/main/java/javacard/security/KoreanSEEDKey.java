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
 * <code>KoreanSEEDKey</code> contains an 16-byte key for Korean Seed Algorithm
 * operations.
 * <p>When the key data is set, the key is initialized and ready for use.
 * <p>
 * 
 * @since 2.2.2
 * @see KeyBuilder
 * @see Signature
 * @see javacardx.crypto.Cipher
 * @see javacardx.crypto.KeyEncryption
 * 
 */
public interface KoreanSEEDKey extends SecretKey
{
	/**
	 * Sets the <code>Key</code> data. The plain text length of input key data is
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
	 * @throws CryptoException with the following reason code:<ul> 
         * <li><code>CryptoException.ILLEGAL_VALUE</code> if input data decryption is required and fails.
         * </ul>
	 * @throws ArrayIndexOutOfBoundsException if kOff is negative or the keyData array is too short
	 * @throws NullPointerException if the keyData parameter is null
	 */
	void setKey(byte[] keyData, short kOff) throws CryptoException, NullPointerException, ArrayIndexOutOfBoundsException;

	/**
	 * Returns the <code>Key</code> data in plain text. The length of output key data
         * is 16 bytes for Korean Seed Algorithm.
	 * The data format is big-endian and right-aligned 
         * (the least significant bit is the least significant
	 * bit of last byte).
	 * <P>
	 * 
	 * @param keyData byte array to return key data
	 * @param kOff offset within keyData to start
	 * @return the byte length of the key data returned
	 * @throws CryptoException  with the following reason code:<ul>
         * <li><code>CryptoException.UNINITIALIZED_KEY</code> if the key data has not been successfully initialized since the time the initialized state of the key was set to false.
         * </ul>
	 * @see Key
	 */
	byte getKey(byte[] keyData, short kOff);

}
