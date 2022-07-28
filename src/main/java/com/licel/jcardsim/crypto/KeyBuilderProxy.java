/*
 * Copyright 2015 Licel Corporation.
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
package com.licel.jcardsim.crypto;

import javacard.security.CryptoException;
import javacard.security.Key;
import javacard.security.KeyBuilder;
import javacard.security.KoreanSEEDKey;

/**
 * ProxyClass for <code>KeyBuilder</code>
 * @see KeyBuilder
 */
public class KeyBuilderProxy {
    /**
     * Creates uninitialized cryptographic keys for signature and cipher algorithms. Only instances created
     * by this method may be the key objects used to initialize instances of
     * <code>Signature</code>, <code>Cipher</code> and <code>KeyPair</code>.
     * Note that the object returned must be cast to their appropriate key type interface.
     * @param keyType the type of key to be generated. Valid codes listed in TYPE.. constants.
     * See {@link KeyBuilder#TYPE_DES_TRANSIENT_RESET}.
     * @param keyLength the key size in bits. The valid key bit lengths are key type dependent. Some common
     * key lengths are listed above above in the LENGTH_.. constants.
     * See {@link KeyBuilder#LENGTH_DES}.
     * @param keyEncryption if <code>true</code> this boolean requests a key implementation
     * which implements the <code>javacardx.crypto.KeyEncryption</code> interface.
     * The key implementation returned may implement the <code>javacardx.crypto.KeyEncryption</code>
     * interface even when this parameter is <code>false</code>.
     * @return the key object instance of the requested key type, length and encrypted access
     * @throws CryptoException with the following reason codes:<ul>
     * <li><code>CryptoException.NO_SUCH_ALGORITHM</code> if the requested algorithm
     * associated with the specified type, size of key and key encryption interface is not supported.</ul>
     */
    public static Key buildKey(byte keyType, short keyLength, boolean keyEncryption)
            throws CryptoException {
        Key key = null;
        switch (keyType) {
            // des
            case KeyBuilder.TYPE_DES_TRANSIENT_RESET:
            case KeyBuilder.TYPE_DES_TRANSIENT_DESELECT:
            case KeyBuilder.TYPE_DES:
                if (keyLength != 64 && keyLength != 128 && keyLength != 192) {
                    CryptoException.throwIt(CryptoException.ILLEGAL_VALUE);
                }
                key = new SymmetricKeyImpl(keyType, keyLength);
                break;

            // rsa
            case KeyBuilder.TYPE_RSA_PUBLIC:
                key = new RSAKeyImpl(false, keyLength);
                break;

            case KeyBuilder.TYPE_RSA_PRIVATE:
                key = new RSAKeyImpl(true, keyLength);
                break;

            case KeyBuilder.TYPE_RSA_CRT_PRIVATE:
                key = new RSAPrivateCrtKeyImpl(keyLength);
                break;

            // dsa
            case KeyBuilder.TYPE_DSA_PUBLIC:
                key = new DSAPublicKeyImpl(keyLength);
                break;

            case KeyBuilder.TYPE_DSA_PRIVATE:
                key = new DSAPrivateKeyImpl(keyLength);
                break;

            // ecc
            case KeyBuilder.TYPE_EC_F2M_PUBLIC:
                key = new ECPublicKeyImpl(keyType, keyLength);
                break;
            case KeyBuilder.TYPE_EC_F2M_PRIVATE:
                key = new ECPrivateKeyImpl(keyType, keyLength);
                break;

            case KeyBuilder.TYPE_EC_FP_PUBLIC:
                key = new ECPublicKeyImpl(keyType, keyLength);
                break;
            case KeyBuilder.TYPE_EC_FP_PRIVATE:
                key = new ECPrivateKeyImpl(keyType, keyLength);
                break;

            // aes
            case KeyBuilder.TYPE_AES_TRANSIENT_RESET:
            case KeyBuilder.TYPE_AES_TRANSIENT_DESELECT:
            case KeyBuilder.TYPE_AES:
                if (keyLength != 128 && keyLength != 192 && keyLength != 256) {
                    CryptoException.throwIt(CryptoException.ILLEGAL_VALUE);
                }
                key = new SymmetricKeyImpl(keyType, keyLength);
                break;

            // hmac
            case KeyBuilder.TYPE_HMAC_TRANSIENT_RESET:
            case KeyBuilder.TYPE_HMAC_TRANSIENT_DESELECT:
            case KeyBuilder.TYPE_HMAC:
                key = new SymmetricKeyImpl(keyType, keyLength);
                break;
                
            // dh
            case KeyBuilder.TYPE_DH_PUBLIC_TRANSIENT_RESET:
            case KeyBuilder.TYPE_DH_PUBLIC_TRANSIENT_DESELECT:
            case KeyBuilder.TYPE_DH_PUBLIC:
                key = new DHPublicKeyImpl(keyLength);
                break;
                
            case KeyBuilder.TYPE_DH_PRIVATE_TRANSIENT_RESET:
            case KeyBuilder.TYPE_DH_PRIVATE_TRANSIENT_DESELECT:
            case KeyBuilder.TYPE_DH_PRIVATE:
                key = new DHPrivateKeyImpl(keyLength);
                break;

            case KeyBuilder.TYPE_KOREAN_SEED_TRANSIENT_RESET:
            case KeyBuilder.TYPE_KOREAN_SEED_TRANSIENT_DESELECT:
            case KeyBuilder.TYPE_KOREAN_SEED:
                if (keyLength != KeyBuilder.LENGTH_KOREAN_SEED_128) {
                    CryptoException.throwIt(CryptoException.ILLEGAL_VALUE);
                }
                key = new SymmetricKeyImpl(keyType, keyLength);

                break;
            default:
                CryptoException.throwIt(CryptoException.NO_SUCH_ALGORITHM);
                break;
        }
        return key;
    }
    
}
