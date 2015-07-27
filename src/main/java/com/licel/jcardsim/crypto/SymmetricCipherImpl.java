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
import javacardx.crypto.Cipher;
import org.spongycastle.crypto.BufferedBlockCipher;
import org.spongycastle.crypto.modes.CBCBlockCipher;
import org.spongycastle.crypto.paddings.ISO7816d4Padding;
import org.spongycastle.crypto.paddings.PKCS7Padding;
import org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.spongycastle.crypto.paddings.ZeroBytePadding;
import org.spongycastle.crypto.params.ParametersWithIV;

/**
 * Implementation <code>Cipher</code> with symmetric keys based
 * on BouncyCastle CryptoAPI.
 * @see Cipher
 */
public class SymmetricCipherImpl extends Cipher {

    byte algorithm;
    BufferedBlockCipher engine;
    boolean isInitialized;

    public SymmetricCipherImpl(byte algorithm) {
        this.algorithm = algorithm;
    }

    public void init(Key theKey, byte theMode) throws CryptoException {
        selectCipherEngine(theKey);
        engine.init(theMode == MODE_ENCRYPT, ((SymmetricKeyImpl) theKey).getParameters());
        isInitialized = true;
    }

    public void init(Key theKey, byte theMode, byte[] bArray, short bOff, short bLen) throws CryptoException {
        switch (algorithm) {
            case ALG_DES_ECB_NOPAD:
            case ALG_DES_ECB_ISO9797_M1:
            case ALG_DES_ECB_ISO9797_M2:
            case ALG_DES_ECB_PKCS5:
                CryptoException.throwIt(CryptoException.ILLEGAL_VALUE);
                break;
            case ALG_DES_CBC_NOPAD:
            case ALG_DES_CBC_ISO9797_M1:
            case ALG_DES_CBC_ISO9797_M2:
            case ALG_DES_CBC_PKCS5:
                if (bLen != (short) 8) {
                    CryptoException.throwIt(CryptoException.ILLEGAL_VALUE);
                }
                break;
        }
        selectCipherEngine(theKey);
        byte[] iv = JCSystem.makeTransientByteArray(bLen, JCSystem.CLEAR_ON_RESET);
        Util.arrayCopyNonAtomic(bArray, bOff, iv, (short) 0, bLen);
        engine.init(theMode == MODE_ENCRYPT, new ParametersWithIV(((SymmetricKeyImpl) theKey).getParameters(), iv));
        isInitialized = true;
    }

    public byte getAlgorithm() {
        return algorithm;
    }

    public short doFinal(byte[] inBuff, short inOffset, short inLength, byte[] outBuff, short outOffset) throws CryptoException {
        if (!isInitialized) {
            CryptoException.throwIt(CryptoException.INVALID_INIT);
        }

        short processedBytes = (short) engine.processBytes(inBuff, inOffset, inLength, outBuff, outOffset);
        try {
            return (short) (engine.doFinal(outBuff, outOffset + processedBytes) + processedBytes);
        } catch (Exception ex) {
            CryptoException.throwIt(CryptoException.ILLEGAL_USE);
        }
        return -1;
    }

    public short update(byte[] inBuff, short inOffset, short inLength, byte[] outBuff, short outOffset) throws CryptoException {
        if (!isInitialized) {
            CryptoException.throwIt(CryptoException.INVALID_INIT);
        }
        return (short) engine.processBytes(inBuff, inOffset, inLength, outBuff, outOffset);
    }

    private void selectCipherEngine(Key theKey) {
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
        switch (algorithm) {
            case ALG_DES_CBC_NOPAD:
            case ALG_AES_BLOCK_128_CBC_NOPAD:
                engine = new BufferedBlockCipher(new CBCBlockCipher(key.getCipher()));
                break;
            case ALG_DES_CBC_ISO9797_M1:
                engine = new PaddedBufferedBlockCipher(new CBCBlockCipher(key.getCipher()), new ZeroBytePadding());
                break;
            case ALG_DES_CBC_ISO9797_M2:
                engine = new PaddedBufferedBlockCipher(new CBCBlockCipher(key.getCipher()), new ISO7816d4Padding());
                break;
            case ALG_DES_CBC_PKCS5:
                engine = new PaddedBufferedBlockCipher(new CBCBlockCipher(key.getCipher()), new PKCS7Padding());
                break;
            case ALG_DES_ECB_NOPAD:
            case ALG_AES_BLOCK_128_ECB_NOPAD:                
                engine = new BufferedBlockCipher(key.getCipher());
                break;
            case ALG_DES_ECB_ISO9797_M1:
                engine = new PaddedBufferedBlockCipher(key.getCipher(), new ZeroBytePadding());
                break;
            case ALG_DES_ECB_ISO9797_M2:
                engine = new PaddedBufferedBlockCipher(key.getCipher(), new ISO7816d4Padding());
                break;
            case ALG_DES_ECB_PKCS5:
                engine = new PaddedBufferedBlockCipher(key.getCipher(), new PKCS7Padding());
                break;
            default:
                CryptoException.throwIt(CryptoException.NO_SUCH_ALGORITHM);
                break;
        }
    }
}
