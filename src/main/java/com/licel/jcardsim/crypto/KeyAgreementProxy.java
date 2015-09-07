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
import javacard.security.KeyAgreement;

/**
 * ProxyClass for <code>KeyAgreement</code>
 * @see KeyAgreement
 */
public class KeyAgreementProxy {
    /**
     * Creates a <CODE>KeyAgreement</CODE> object instance of the selected algorithm.
     * @param algorithm the desired key agreement algorithm
     * Valid codes listed in ALG_ .. constants above, for example, <CODE>ALG_EC_SVDP_DH</CODE>
     * @param externalAccess  if <code>true</code> indicates that the instance will be shared among
     * multiple applet instances and that the <code>KeyAgreement</code> instance will also be accessed (via a <code>Shareable</code>
     * interface) when the owner of the <code>KeyAgreement</code> instance is not the currently selected applet.
     * If <code>true</code> the implementation must not
     * allocate <code>CLEAR_ON_DESELECT</code> transient space for internal data.
     * @return the KeyAgreement object instance of the requested algorithm
     * @throws CryptoException with the following reason codes:
     * <ul>
     * <li><code>CryptoException.NO_SUCH_ALGORITHM</code> if the requested
     * algorithm or shared access mode is not supported.
     * </ul>
     */
    public static final KeyAgreement getInstance(byte algorithm, boolean externalAccess)
            throws CryptoException {
        return new KeyAgreementImpl(algorithm);
    }
}
