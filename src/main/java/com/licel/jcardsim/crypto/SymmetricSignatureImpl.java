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
import javax.xml.bind.DatatypeConverter;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.macs.CBCBlockCipherMac;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.ISO7816d4Padding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.ZeroBytePadding;
import org.bouncycastle.crypto.params.ParametersWithIV;

/**
 * Implementation <code>Signature</code> with symmetric keys based
 * on BouncyCastle CryptoAPI
 * @see Signature
 */
public class SymmetricSignatureImpl extends Signature {

    Mac engine;
    byte algorithm;
    boolean isInitialized;
    BlockCipherPadding paddingEngine;
    Digest digestEngine;
    short macSize;

    public SymmetricSignatureImpl(byte algorithm) {
        this.algorithm = algorithm;
        switch (algorithm) {
            case ALG_DES_MAC4_NOPAD:
                macSize = 32;
                paddingEngine = null;
                break;
            case ALG_DES_MAC8_NOPAD:
                macSize = 64;
                paddingEngine = null;
                break;
            case ALG_DES_MAC4_ISO9797_M1:
                macSize = 32;
                paddingEngine = new ZeroBytePadding();
                break;
            case ALG_DES_MAC8_ISO9797_M1:
                macSize = 64;
                paddingEngine = new ZeroBytePadding();
                break;
            case ALG_DES_MAC4_ISO9797_M2:
                macSize = 32;
                paddingEngine = new ISO7816d4Padding();
                break;
            case ALG_DES_MAC8_ISO9797_M2:
                macSize = 64;
                paddingEngine = new ISO7816d4Padding();
                break;
            case ALG_DES_MAC4_PKCS5:
                macSize = 32;
                paddingEngine = new PKCS7Padding();
                break;
            case ALG_DES_MAC8_PKCS5:
                macSize = 64;
                paddingEngine = new PKCS7Padding();
                break;
            case ALG_AES_MAC_128_NOPAD:
                macSize = 128;
                paddingEngine = null;
                break;
            case ALG_HMAC_SHA1:                
                digestEngine = new SHA1Digest();
                break;
            case ALG_HMAC_SHA_256:                
                digestEngine = new SHA256Digest();
                break;
            case ALG_HMAC_SHA_384:                
                digestEngine = new SHA384Digest();
                break;
            case ALG_HMAC_SHA_512:                
                digestEngine = new SHA512Digest();
                break;
            case ALG_HMAC_MD5:                
                digestEngine = new MD5Digest();
                break;
            case ALG_HMAC_RIPEMD160:                
                digestEngine = new RIPEMD160Digest();
                break;
                
            default:
                CryptoException.throwIt(CryptoException.NO_SUCH_ALGORITHM);
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
        if (!(theKey instanceof SymmetricKeyImpl)) {
            CryptoException.throwIt(CryptoException.ILLEGAL_VALUE);
        }
        SymmetricKeyImpl key = (SymmetricKeyImpl) theKey;
        if(digestEngine == null) {
            engine = new CBCBlockCipherMac(key.getCipher(), macSize, paddingEngine);
        } else {
            engine = new HMac(digestEngine);
        }
        engine.init(key.getParameters());
        isInitialized = true;
    }

    public void init(Key theKey, byte theMode, byte[] bArray, short bOff, short bLen) throws CryptoException {
        if (theKey == null) {
            CryptoException.throwIt(CryptoException.UNINITIALIZED_KEY);
        }
        if (!theKey.isInitialized()) {
            CryptoException.throwIt(CryptoException.UNINITIALIZED_KEY);
        }
        if (!(theKey instanceof SymmetricKeyImpl)) {
            CryptoException.throwIt(CryptoException.ILLEGAL_VALUE);
        }
        SymmetricKeyImpl key = (SymmetricKeyImpl) theKey;
        if (bLen != key.getCipher().getBlockSize()) {
            CryptoException.throwIt(CryptoException.ILLEGAL_VALUE);
        }
        if(digestEngine == null) {
            engine = new CBCBlockCipherMac(key.getCipher(), macSize, paddingEngine);
        } else {
            engine = new HMac(digestEngine);
        }
        // CBC-MAC iv == 0
        if (algorithm != ALG_AES_MAC_128_NOPAD) {
            engine.init(new ParametersWithIV(key.getParameters(), bArray, bOff, bLen));
        }
        isInitialized = true;
    }

    public short getLength() throws CryptoException {
        if (!isInitialized) {
            CryptoException.throwIt(CryptoException.INVALID_INIT);
        }
        return (short) engine.getMacSize();
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
        if ((algorithm == ALG_DES_MAC8_NOPAD || algorithm == ALG_DES_MAC4_NOPAD)
                && ((inLength % 8) != 0)) {
            CryptoException.throwIt(CryptoException.ILLEGAL_USE);
        }
        engine.update(inBuff, inOffset, inLength);
        short processedBytes = (short) engine.doFinal(sigBuff, sigOffset);
        engine.reset();
        return processedBytes;
    }

    public boolean verify(byte[] inBuff, short inOffset, short inLength, byte[] sigBuff, short sigOffset, short sigLength) throws CryptoException {
        if (!isInitialized) {
            CryptoException.throwIt(CryptoException.INVALID_INIT);
        }
        if ((algorithm == ALG_DES_MAC8_NOPAD || algorithm == ALG_DES_MAC4_NOPAD)
                && ((inLength % 8) != 0)) {
            CryptoException.throwIt(CryptoException.ILLEGAL_USE);
        }
        engine.update(inBuff, inOffset, inLength);
        byte[] sig = JCSystem.makeTransientByteArray((short) (engine.getMacSize()), JCSystem.CLEAR_ON_RESET);
        engine.doFinal(sig, (short) 0);
        engine.reset();
        return Util.arrayCompare(sig, (short) 0, sigBuff, sigOffset, (short) sig.length) == 0;
    }
}
