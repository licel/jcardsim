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

import javacard.framework.JCSystem;
import javacard.framework.Util;
import javacard.security.CryptoException;
import javacard.security.Key;
import javacard.security.Signature;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.signers.DSADigestSigner;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.ISO9796d2Signer;
import org.bouncycastle.crypto.signers.RSADigestSigner;

/*
 * Implementation <code>Signature</code> with asymmetric keys based
 * on BouncyCastle CryptoAPI
 * @see Signature
 */
public class AsymmetricSignatureImpl extends Signature {

    Signer engine;
    Key key;
    byte algorithm;
    boolean isInitialized;

    public AsymmetricSignatureImpl(byte algorithm) {
        this.algorithm = algorithm;
        switch (algorithm) {
            case ALG_RSA_SHA_ISO9796:
                engine = new ISO9796d2Signer(new RSAEngine(), new SHA1Digest());
                break;
            case ALG_RSA_SHA_PKCS1:
                engine = new RSADigestSigner(new SHA1Digest());
                break;
            case ALG_RSA_MD5_PKCS1:
                engine = new RSADigestSigner(new MD5Digest());
                break;
            case ALG_RSA_RIPEMD160_ISO9796:
                engine = new ISO9796d2Signer(new RSAEngine(), new RIPEMD160Digest());
                break;
            case ALG_RSA_RIPEMD160_PKCS1:
                engine = new RSADigestSigner(new RIPEMD160Digest());
                break;
            case ALG_ECDSA_SHA:
                engine = new DSADigestSigner(new ECDSASigner(), new SHA1Digest());
                break;
        }
    }

    public void init(Key theKey, byte theMode) throws CryptoException {
        if (theKey == null) {
            CryptoException.throwIt(CryptoException.UNINITIALIZED_KEY);
        }
        if (!theKey.isInitialized()) {
            CryptoException.throwIt(CryptoException.UNINITIALIZED_KEY);
        }
        if (!(theKey instanceof KeyWithParameters)) {
            CryptoException.throwIt(CryptoException.ILLEGAL_VALUE);
        }
        engine.init(theMode == MODE_SIGN, ((KeyWithParameters) theKey).getParameters());
        this.key = theKey;
        isInitialized = true;
    }

    public void init(Key theKey, byte theMode, byte[] bArray, short bOff, short bLen) throws CryptoException {
        CryptoException.throwIt(CryptoException.ILLEGAL_VALUE);
    }

    public short getLength() throws CryptoException {
        if (!isInitialized) {
            CryptoException.throwIt(CryptoException.INVALID_INIT);
        }
        if (!key.isInitialized()) {
            CryptoException.throwIt(CryptoException.UNINITIALIZED_KEY);
        }
        switch (algorithm) {
            case ALG_RSA_SHA_ISO9796:
            case ALG_RSA_SHA_PKCS1:
            case ALG_RSA_MD5_PKCS1:
            case ALG_RSA_RIPEMD160_ISO9796:
            case ALG_RSA_RIPEMD160_PKCS1:
                return (short)(key.getSize()>>3);
            case ALG_ECDSA_SHA:
                // x,y + der payload
                return (short)(((key.getSize()*2)>>3) + 8);
        }
        return 0;
    }

    public byte getAlgorithm() {
        return algorithm;
    }

    public void update(byte[] inBuff, short inOffset, short inLength) throws CryptoException {
        if (!isInitialized) {
            CryptoException.throwIt(CryptoException.INVALID_INIT);
        }
        engine.update(inBuff, inOffset, inLength);
    }

    public short sign(byte[] inBuff, short inOffset, short inLength, byte[] sigBuff, short sigOffset) throws CryptoException {
        if (!isInitialized) {
            CryptoException.throwIt(CryptoException.INVALID_INIT);
        }
        engine.update(inBuff, inOffset, inLength);
        byte[] sig;
        try {
            sig = engine.generateSignature();
            Util.arrayCopyNonAtomic(sig, (short) 0, sigBuff, sigOffset, (short) sig.length);
            return (short) sig.length;
        } catch (org.bouncycastle.crypto.CryptoException ex) {
            CryptoException.throwIt(CryptoException.ILLEGAL_USE);
        } catch (DataLengthException ex) {
            CryptoException.throwIt(CryptoException.ILLEGAL_VALUE);
        } finally {
            engine.reset();
        }
        return -1;
    }

    public boolean verify(byte[] inBuff, short inOffset, short inLength, byte[] sigBuff, short sigOffset, short sigLength) throws CryptoException {
        if (!isInitialized) {
            CryptoException.throwIt(CryptoException.INVALID_INIT);
        }
        engine.update(inBuff, inOffset, inLength);
        byte[] sig = JCSystem.makeTransientByteArray(sigLength, JCSystem.CLEAR_ON_RESET);
        Util.arrayCopyNonAtomic(sigBuff, sigOffset, sig, (short) 0, sigLength);
        boolean b = engine.verifySignature(sig);
        engine.reset();
        return b;
    }
}
