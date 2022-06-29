/*
 * Copyright 2022 Karsten Ohme.
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
import javacard.security.KeyBuilder;
import javacardx.crypto.AEADCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.modes.CCMBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.jcajce.spec.AEADParameterSpec;

import java.security.spec.AlgorithmParameterSpec;

/**
 * Implementation <code>AEADCipher</code> with symmetric keys based
 * on BouncyCastle CryptoAPI.
 *
 * @see AEADCipher
 */
public class AEADCipherImpl extends AEADCipher {

    byte algorithm;
    AEADBlockCipher engine;
    boolean isInitialized;

    public AEADCipherImpl(byte algorithm) {
        this.algorithm = algorithm;
    }

    public void init(Key theKey, byte theMode) throws CryptoException {
        selectCipherEngine(theKey);
        engine.init(theMode == MODE_ENCRYPT, ((SymmetricKeyImpl) theKey).getParameters());
        isInitialized = true;
    }

    public void init(Key theKey, byte theMode, byte[] bArray, short bOff, short bLen) throws CryptoException {
        selectCipherEngine(theKey);
        byte[] iv = JCSystem.makeTransientByteArray(bLen, JCSystem.CLEAR_ON_RESET);
        Util.arrayCopyNonAtomic(bArray, bOff, iv, (short) 0, bLen);
        engine.init(theMode == MODE_ENCRYPT, new ParametersWithIV(((SymmetricKeyImpl) theKey).getParameters(), iv));
        isInitialized = true;
    }

    @Override
    public void init(Key theKey, byte theMode, byte[] nonceBuf, short nonceOff, short nonceLen, short adataLen, short messageLen, short tagSize) throws CryptoException {
        selectCipherEngine(theKey);
        CipherParameters cipherParameters = null;
        switch (algorithm) {
            case ALG_AES_CCM:
                byte[] nonce = JCSystem.makeTransientByteArray(nonceLen, JCSystem.CLEAR_ON_DESELECT);
                Util.arrayCopyNonAtomic(nonceBuf, nonceOff, nonce, (short) 0, nonceLen);
                cipherParameters = new AEADParameters((KeyParameter) ((SymmetricKeyImpl) theKey).getParameters(),
                        tagSize * 8, nonce);
                break;
            default:
                CryptoException.throwIt(CryptoException.NO_SUCH_ALGORITHM);
                break;
        }
        engine.init(theMode == MODE_ENCRYPT, cipherParameters);
        isInitialized = true;
    }

    @Override
    public void updateAAD(byte[] aadBuf, short aadOff, short aadLen) throws CryptoException {
        engine.processAADBytes(aadBuf, aadOff, aadLen);
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

    @Override
    public short retrieveTag(byte[] tagBuf, short tagOff, short tagLen) throws CryptoException {
        return Util.arrayCopyNonAtomic(engine.getMac(), (short) 0, tagBuf, tagOff, tagLen);
    }

    @Override
    public boolean verifyTag(byte[] receivedTagBuf, short receivedTagOff, short receivedTagLen, short requiredTagLen) throws CryptoException {
        if (requiredTagLen != receivedTagLen) {
            return false;
        }
        return Util.arrayCompare(engine.getMac(), (short)0, receivedTagBuf, receivedTagOff, receivedTagLen) == 0;
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
        if (!checkKeyCompatibility(theKey)) {
            CryptoException.throwIt(CryptoException.ILLEGAL_VALUE);
        }

        SymmetricKeyImpl key = (SymmetricKeyImpl) theKey;
        switch (algorithm) {
            case ALG_AES_CCM:
                engine = new CCMBlockCipher(key.getCipher());
                break;
            default:
                CryptoException.throwIt(CryptoException.NO_SUCH_ALGORITHM);
                break;
        }
    }

    private boolean checkKeyCompatibility(Key theKey) {
        switch (theKey.getType()) {
            case KeyBuilder.TYPE_AES:
            case KeyBuilder.TYPE_AES_TRANSIENT_RESET:
            case KeyBuilder.TYPE_AES_TRANSIENT_DESELECT:
                if (algorithm == AEADCipher.ALG_AES_CCM) {
                    return true;
                }
            break;
        }

        return false;

    }

    public byte getPaddingAlgorithm() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public byte getCipherAlgorithm() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
