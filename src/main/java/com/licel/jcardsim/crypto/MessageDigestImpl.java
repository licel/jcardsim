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
import javacard.security.MessageDigest;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;

/**
 * Implementation <code>MessageDigest</code> based
 * on BouncyCastle CryptoAPI
 * @see MessageDigest
 * @see MD5Digest
 * @see RIPEMD160Digest
 * @see SHA1Digest
 */
public class MessageDigestImpl extends MessageDigest {

    private Digest engine;
    private byte algorithm;

    public MessageDigestImpl(byte algorithm) {
        this.algorithm = algorithm;
        switch (algorithm) {
            case ALG_SHA:
                engine = new SHA1Digest();
                break;
            case ALG_MD5:
                engine = new MD5Digest();
                break;
            case ALG_RIPEMD160:
                engine = new RIPEMD160Digest();
                break;
            default:
                CryptoException.throwIt(CryptoException.NO_SUCH_ALGORITHM);
                break;
        }
    }

    public byte getAlgorithm() {
        return algorithm;
    }

    public byte getLength() {
        return (byte) engine.getDigestSize();
    }

    public short doFinal(byte inBuff[], short inOffset, short inLength,
            byte outBuff[], short outOffset) {
        engine.update(inBuff, inOffset, inLength);
        return (short) engine.doFinal(outBuff, outOffset);
    }

    public void update(byte inBuff[], short inOffset, short inLength) {
        engine.update(inBuff, inOffset, inLength);
    }

    public void reset() {
        engine.reset();
    }
}
