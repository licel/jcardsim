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
import javacard.security.InitializedMessageDigest;
import javacard.security.MessageDigest;

/**
 * ProxyClass for <code>MessageDigest</code>
 * @see MessageDigest
 */
public class MessageDigestProxy {
    /**
     * Creates a <code>MessageDigest</code> object instance of the selected algorithm.
     * @param algorithm the desired message digest algorithm.
     * Valid codes listed in ALG_ .. constants above, for example, <A HREF="../../javacard/security/MessageDigest.html#ALG_SHA"><CODE>ALG_SHA</CODE></A>.
     * @param externalAccess <code>true</code> indicates that the instance will be shared among
     * multiple applet instances and that the <code>MessageDigest</code> instance will also be accessed (via a <code>Shareable</code>.
     * interface) when the owner of the <code>MessageDigest</code> instance is not the currently selected applet.
     * If <code>true</code> the implementation must not allocate CLEAR_ON_DESELECT transient space for internal data.
     * @return the <code>MessageDigest</code> object instance of the requested algorithm
     * @throws CryptoException with the following reason codes:<ul>
     * <li><code>CryptoException.NO_SUCH_ALGORITHM</code> if the requested algorithm
     * or shared access mode is not supported.</ul>
     */
    public static final MessageDigest getInstance(byte algorithm, boolean externalAccess)
    throws CryptoException {
        if (externalAccess) {
            CryptoException.throwIt(CryptoException.NO_SUCH_ALGORITHM);
        }
        MessageDigest instance = new MessageDigestImpl(algorithm);
        return instance;
    }
    
    /**
     * Creates a
     * <code>InitializedMessageDigest</code> object instance of the selected algorithm.
     * <p>
     *
     * @param algorithm the desired message digest algorithm. Valid codes listed in ALG_* constants above,
     * for example, {@link MessageDigest#ALG_SHA}.
     * @param externalAccess true indicates that the instance will be shared among multiple applet 
     * instances and that the <code>InitializedMessageDigest</code> instance will also be accessed (via a <code>Shareable</code>. interface) 
     * when the owner of the <code>InitializedMessageDigest</code> instance is not the currently selected applet. 
     * If true the implementation must not allocate CLEAR_ON_DESELECT transient space for internal data.
     * @return the <code>InitializedMessageDigest</code> object instance of the requested algorithm
     * @throws CryptoException with the following reason codes: <code>CryptoException.NO_SUCH_ALGORITHM</code>
     * if the requested algorithm or shared access mode is not supported.
     * @since 2.2.2
     */
    public static final InitializedMessageDigest getInitializedMessageDigestInstance(byte algorithm,
            boolean externalAccess) throws CryptoException {
        if (externalAccess) {
            CryptoException.throwIt(CryptoException.NO_SUCH_ALGORITHM);
        }
        InitializedMessageDigest instance = new MessageDigestImpl(algorithm);
        return instance;
    }
}
