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

package javacardx.crypto;

/**
 *<code>KeyEncryption</code> interface defines the methods used to enable encrypted
 * key data access to a key implementation.
 *<p>
 */
public interface KeyEncryption {

    /**
     * Sets the <code>Cipher</code> object to be used to decrypt the input key data
     * and key parameters in the set methods.<p>
     * Default <code>Cipher</code> object is <code>null</code> - no decryption performed.
     * @param keyCipher the decryption <code>Cipher</code> object to decrypt the input key data.
     * The <code>null</code> parameter indicates that no decryption is required.
     */
    public void setKeyCipher(Cipher keyCipher);

    /**
     * Returns the <code>Cipher</code> object to be used to decrypt the input key data
     * and key parameters in the set methods.<p>
     * Default is <code>null</code> - no decryption performed.
     * @return <code>keyCipher</code>, the decryption <code>Cipher</code> object to decrypt the input key data.
     * The <code>null</code> return indicates that no decryption is performed.
     */
    public Cipher getKeyCipher();
}
