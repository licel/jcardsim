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
import javacard.security.KeyAgreement;
import javacard.security.PrivateKey;
import org.bouncycastle.crypto.BasicAgreement;
import org.bouncycastle.crypto.agreement.ECDHBasicAgreement;
import org.bouncycastle.crypto.agreement.ECDHCBasicAgreement;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;

/**
 * Implementation <code>KeyAgreement</code> based
 * on BouncyCastle CryptoAPI
 * @see KeyAgreement
 * @see ECDHBasicAgreement
 * @see ECDHCBasicAgreement
 */
public class KeyAgreementImpl extends KeyAgreement {

    BasicAgreement engine;
    byte algorithm;
    ECPrivateKeyImpl privateKey;

    public KeyAgreementImpl(byte algorithm) {
        this.algorithm = algorithm;
        switch (algorithm) {
            case ALG_EC_SVDP_DH:
                engine = new ECDHBasicAgreement();
                break;
            case ALG_EC_SVDP_DHC:
                engine = new ECDHCBasicAgreement();
                break;
            default:
                CryptoException.throwIt(CryptoException.NO_SUCH_ALGORITHM);
                break;
        }
    }

    public void init(PrivateKey privateKey) throws CryptoException {
        if (privateKey == null) {
            CryptoException.throwIt(CryptoException.UNINITIALIZED_KEY);
        }
        if (!(privateKey instanceof ECPrivateKeyImpl)) {
            CryptoException.throwIt(CryptoException.ILLEGAL_VALUE);
        }
        engine.init(((ECPrivateKeyImpl) privateKey).getParameters());
        this.privateKey = (ECPrivateKeyImpl) privateKey;
    }

    public byte getAlgorithm() {
        return algorithm;
    }

    public short generateSecret(byte[] publicData,
            short publicOffset,
            short publicLength,
            byte[] secret,
            short secretOffset) throws CryptoException {
        byte[] publicKey = JCSystem.makeTransientByteArray(publicLength, JCSystem.CLEAR_ON_RESET);
        Util.arrayCopyNonAtomic(publicData, publicOffset, publicKey, (short) 0, publicLength);
        ECPublicKeyParameters ecp = new ECPublicKeyParameters(
                ((ECPrivateKeyParameters) privateKey.getParameters()).getParameters().getCurve().decodePoint(publicKey), ((ECPrivateKeyParameters) privateKey.getParameters()).getParameters());
        byte[] result = engine.calculateAgreement(ecp).toByteArray();
        if (result.length > secret.length - secretOffset) {
            CryptoException.throwIt(CryptoException.ILLEGAL_VALUE);
        }
        Util.arrayCopyNonAtomic(result, (short) 0, secret, secretOffset, (short) result.length);
        return (short) result.length;
    }
}
