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
package com.licel.jcardsim.crypto;

import javacard.security.CryptoException;
import javacard.security.DSAKey;
import javacard.security.KeyBuilder;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.DSAKeyParameters;
import org.bouncycastle.crypto.params.DSAParameters;

/**
 * Base class for <code>DSAPublicKeyImpl/DSAPrivateKeyImpl</code>
 * on BouncyCastle CryptoAPI
 * @see DSAKey
 */
public class DSAKeyImpl extends KeyImpl implements DSAKey {

    protected ByteContainer p = new ByteContainer();
    protected ByteContainer q = new ByteContainer();
    protected ByteContainer g = new ByteContainer();
    protected boolean isPrivate;

    /**
     * Construct not-initialized dsa key
     * @param keyType - key type
     * @param keySize - key size in bits
     * @see KeyPair
     * @see KeyBuilder
     */
    public DSAKeyImpl(byte keyType, short size) {
        this.size = size;
        type = keyType;
    }

    /**
     * Construct and initialize dsa key with DSAKeyParameters.
     * Use in KeyPairImpl
     * @see KeyPair
     * @see DSAKeyParameters
     * @parameters params key params from BouncyCastle API
     */
    public DSAKeyImpl(DSAKeyParameters params) {
        this(params.isPrivate() ? KeyBuilder.TYPE_DSA_PRIVATE : KeyBuilder.TYPE_DSA_PUBLIC, (short) params.getParameters().getP().bitLength());
        p.setBigInteger(params.getParameters().getP());
        q.setBigInteger(params.getParameters().getQ());
        g.setBigInteger(params.getParameters().getG());
    }

    public void clearKey() {
        p.clear();
        q.clear();
        g.clear();
    }

    public boolean isInitialized() {
        return (p.isInitialized() && q.isInitialized() && g.isInitialized());
    }

    public void setP(byte[] buffer, short offset, short length) throws CryptoException {
        p.setBytes(buffer, offset, length);
    }

    public void setQ(byte[] buffer, short offset, short length) throws CryptoException {
        q.setBytes(buffer, offset, length);
    }

    public void setG(byte[] buffer, short offset, short length) throws CryptoException {
        g.setBytes(buffer, offset, length);
    }

    public short getP(byte[] buffer, short offset) {
        return p.getBytes(buffer, offset);
    }

    public short getQ(byte[] buffer, short offset) {
        return q.getBytes(buffer, offset);
    }

    public short getG(byte[] buffer, short offset) {
        return g.getBytes(buffer, offset);
    }

    /**
     * Get <code>DSAKeyParameters</code>
     * @return parameters for use with BouncyCastle API
     * @see DSAKeyParameters
     */
    public CipherParameters getParameters() {
        if (!isInitialized()) {
            CryptoException.throwIt(CryptoException.UNINITIALIZED_KEY);
        }
        return new DSAKeyParameters(isPrivate, new DSAParameters(p.getBigInteger(), q.getBigInteger(), g.getBigInteger()));
    }
}
