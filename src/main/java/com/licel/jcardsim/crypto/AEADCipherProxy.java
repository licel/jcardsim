/*
 * Copyright 2022 Karsten Ohme
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
import javacardx.crypto.AEADCipher;
import javacardx.crypto.Cipher;

/**
 * ProxyClass for <code>AEADCipher</code>
 *
 * @see javacardx.crypto.AEADCipher
 */
public class AEADCipherProxy {

    /**
     * Creates a <code>Cipher</code> object instance of the selected algorithm.
     *
     * @param algorithm      the desired Cipher algorithm. Valid codes listed in
     *                       ALG_ .. constants above, for example, {@link Cipher#ALG_DES_CBC_NOPAD}
     * @param externalAccess indicates that the instance will be shared among
     *                       multiple applet instances and that the <code>Cipher</code> instance will also be accessed (via a <code>Shareable</code>
     *                       interface) when the owner of the <code>Cipher</code> instance is not the currently selected applet.
     *                       If <code>true</code> the implementation must not allocate CLEAR_ON_DESELECT transient space for internal data.
     * @return the <code>Cipher</code> object instance of the requested algorithm
     * @throws CryptoException with the following reason codes:
     *                         <ul>
     *                          <li><code>CryptoException.NO_SUCH_ALGORITHM</code> if the requested algorithm is not supported
     *                          or shared access mode is not supported.
     *                         </ul>
     */
    public static final Cipher getInstance(byte algorithm, boolean externalAccess)
            throws CryptoException {
        Cipher instance = null;
        if (externalAccess) {
            CryptoException.throwIt((short) 3);
        }
        switch (algorithm) {
            case AEADCipher.ALG_AES_CCM:
                instance = new AEADCipherImpl(algorithm);
                break;
            default:
                CryptoException.throwIt(CryptoException.NO_SUCH_ALGORITHM);
                break;
        }
        return instance;
    }

}
