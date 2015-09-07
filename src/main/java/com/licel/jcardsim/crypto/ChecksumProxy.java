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

import javacard.security.Checksum;
import javacard.security.CryptoException;

/**
 * ProxyClass for <code>Checksum</code>
 * @see Checksum
 */
public class ChecksumProxy {
    /**
     * Creates a <code>Checksum</code> object instance of the selected algorithm.
     * @param algorithm the desired checksum algorithm.
     * @param externalAccess <code>true</code> indicates that the instance will be shared among
     * multiple applet instances and that the <code>Checksum</code> instance will also be accessed (via a <code>Shareable</code>.
     * interface) when the owner of the <code>Checksum</code> instance is not the currently selected applet.
     * If <code>true</code> the implementation must not allocate CLEAR_ON_DESELECT transient space for internal data.
     * @return the <code>Checksum</code> object instance of the requested algorithm.
     * @throws CryptoException  with the following reason codes:
     * <ul>
     * <li><code>CryptoException.NO_SUCH_ALGORITHM</code> if the requested algorithm
     * or shared access mode is not supported.
     * </ul>
     */
    public static final Checksum getInstance(byte algorithm, boolean externalAccess)
            throws CryptoException {
        if (externalAccess) {
            CryptoException.throwIt(CryptoException.NO_SUCH_ALGORITHM);
        }
        Checksum instance = null;
        switch (algorithm) {
            case Checksum.ALG_ISO3309_CRC16:
                instance = new CRC16();
                break;

            case Checksum.ALG_ISO3309_CRC32:
                instance = new CRC32();
                break;

            default:
                CryptoException.throwIt(CryptoException.NO_SUCH_ALGORITHM);
                break;
        }
        return instance;
    }
    
}
