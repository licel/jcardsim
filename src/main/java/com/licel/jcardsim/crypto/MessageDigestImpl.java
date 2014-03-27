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

import java.lang.reflect.Field;
import javacard.security.CryptoException;
import javacard.security.InitializedMessageDigest;
import javacard.security.MessageDigest;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.util.Pack;

/**
 * Implementation
 * <code>MessageDigest</code> based
 * on BouncyCastle CryptoAPI
 * @see MessageDigest
 * @see MD5Digest
 * @see RIPEMD160Digest
 * @see SHA1Digest
 */
public class MessageDigestImpl extends InitializedMessageDigest {

    private Digest engine;
    private byte algorithm;
    private short blockSize;
    // internals BouncyCastle
    private String byteCountFieldName = "byteCount";
    private Class digestClass;
    private byte componentSize;
    private byte componentCount;
    private byte componentStartIdx;

    public MessageDigestImpl(byte algorithm) {
        this.algorithm = algorithm;
        blockSize = 64;       
        componentStartIdx = 1;
        switch (algorithm) {
            case ALG_SHA:
                engine = new SHA1Digest();
                digestClass = engine.getClass();
                break;
            case ALG_MD5:
                engine = new MD5Digest();
                digestClass = engine.getClass();
                break;
            case ALG_RIPEMD160:
                engine = new RIPEMD160Digest();
                digestClass = engine.getClass();
                componentStartIdx = 0;
                break;
            case ALG_SHA_256:
                engine = new SHA256Digest();
                digestClass = engine.getClass();
                break;
            case ALG_SHA_384:
                engine = new SHA384Digest();
                blockSize = 128;                
                byteCountFieldName = "byteCount1";
                digestClass = engine.getClass().getSuperclass();
                break;
            case ALG_SHA_512:
                engine = new SHA512Digest();
                blockSize = 128;                
                byteCountFieldName = "byteCount1";
                digestClass = engine.getClass().getSuperclass();
                break;
            default:
                CryptoException.throwIt(CryptoException.NO_SUCH_ALGORITHM);
                break;
        }
        componentSize = (byte)(blockSize == 64 ? 4 : 8);
        componentCount = (byte) (engine.getDigestSize() / componentSize);
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

    public void setInitialDigest(byte[] initialDigestBuf, short initialDigestOffset,
            short initialDigestLength, byte[] digestedMsgLenBuf, short digestedMsgLenOffset,
            short digestedMsgLenLength) throws CryptoException {
        // initialDigestLength must be == DIGEST_SIZE
        if (engine.getDigestSize() != initialDigestLength) {
            CryptoException.throwIt(CryptoException.ILLEGAL_VALUE);
        }
        // digestedMsgLenLength must be > 0 and < long value, more formal 2^64-1 bits
        // TODO support length more 2^128-1 bits (SHA-384, SHA-512) 
        if (digestedMsgLenLength == 0 || digestedMsgLenLength > 8) {
            CryptoException.throwIt(CryptoException.ILLEGAL_VALUE);
        }
        long byteCount = 0;
        for (short i = 0; i < digestedMsgLenLength; i++) {
            byteCount = (byteCount << 8) + (digestedMsgLenBuf[digestedMsgLenOffset + i] & 0xff);
        }
        // byte count % block size must be == 0
        if (byteCount % blockSize != 0) {
            CryptoException.throwIt(CryptoException.ILLEGAL_VALUE);
        }
        // set hash state - BouncyCastle specific
        try {
            for (byte i = 0; i < componentCount; i++) {
                // some reflection work
                Field h = digestClass.getDeclaredField("H" + (i + componentStartIdx));
                h.setAccessible(true);
                if (componentSize == 4) {
                    h.setInt(engine, Pack.bigEndianToInt(initialDigestBuf, initialDigestOffset + i * componentSize));
                } else {
                    h.setLong(engine, Pack.bigEndianToLong(initialDigestBuf, initialDigestOffset + i * componentSize));
                }
            }
            // set byteCount
            Field h = digestClass.getSuperclass().getDeclaredField(byteCountFieldName);
            h.setAccessible(true);
            h.setLong(engine, byteCount);
        } catch (Exception e) {
            CryptoException.throwIt(CryptoException.ILLEGAL_USE);
        }
    }

    void getIntermediateDigest(byte[] intermediateDigest, int off) {
        // get hash state - BouncyCastle specific
        try {
            for (byte i = 0; i < componentCount; i++) {
                // some reflection work
                Field h = digestClass.getDeclaredField("H" + (i + componentStartIdx));
                h.setAccessible(true);
                if (componentSize == 4) {
                    Pack.intToBigEndian(h.getInt(engine), intermediateDigest, off + i * componentSize);
                } else {
                    Pack.longToBigEndian(h.getLong(engine), intermediateDigest, off + i * componentSize);
                }
            }
        } catch (Exception e) {
            CryptoException.throwIt(CryptoException.ILLEGAL_USE);
        }
    }

    short getBlockSize(){
        return blockSize;
    }
}
