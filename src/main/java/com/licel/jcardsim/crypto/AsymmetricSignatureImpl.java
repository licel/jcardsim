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
import javacard.framework.JCSystem;
import javacard.framework.Util;
import javacard.security.CryptoException;
import javacard.security.Key;
import javacard.security.Signature;
import javacard.security.SignatureMessageRecovery;
import javacard.security.MessageDigest;
import javacardx.crypto.Cipher;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.SignerWithRecovery;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.NullDigest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA224Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.signers.DSADigestSigner;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.crypto.signers.ISO9796d2Signer;
import org.bouncycastle.crypto.signers.PSSSigner;
import org.bouncycastle.crypto.signers.RSADigestSigner;
import org.bouncycastle.crypto.DSA;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERInteger;
import org.bouncycastle.asn1.DERSequence;

import java.io.IOException;
import java.math.BigInteger;

/*
 * Implementation <code>Signature</code> with asymmetric keys based
 * on BouncyCastle CryptoAPI.
 * @see Signature
 */
public class AsymmetricSignatureImpl extends Signature implements SignatureMessageRecovery{

    Signer engine;
    Key key;
    byte algorithm;
    byte cipherAlgorithm;
    byte paddingAlgorithm;
    boolean isInitialized;
    boolean isRecovery;
    byte[] preSig;
    DSA dsaImpl;

    public AsymmetricSignatureImpl(byte algorithm) {
        this(algorithm, (byte) 0, (byte) 0);
    }

    public AsymmetricSignatureImpl(byte algorithm, byte cipherAlgorithm, byte paddingAlgorithm) {
        this.algorithm = algorithm;
        this.cipherAlgorithm = cipherAlgorithm;
        this.paddingAlgorithm = paddingAlgorithm;
        isRecovery = false;
        if (isRawECDSAWithoutHash()) {
            engine = new DSADigestSigner(new ECDSASigner(), new BouncyCastlePrecomputedOrDigestProxy(new NullDigest()));
            return;
        }
        switch (algorithm) {
            case ALG_RSA_SHA_ISO9796:
                engine = new ISO9796d2Signer(new RSAEngine(), new SHA1Digest());
                break;
            case ALG_RSA_SHA_ISO9796_MR:
                engine = new ISO9796d2Signer(new RSAEngine(), new SHA1Digest());
                isRecovery = true;
                break;
            case ALG_RSA_SHA_PKCS1:
                engine = new RSADigestSigner(new BouncyCastlePrecomputedOrDigestProxy(new SHA1Digest()));
                break;
            case ALG_RSA_SHA_224_PKCS1:
                engine = new RSADigestSigner(new BouncyCastlePrecomputedOrDigestProxy(new SHA224Digest()));
                break;
            case ALG_RSA_SHA_256_PKCS1:
                engine = new RSADigestSigner(new BouncyCastlePrecomputedOrDigestProxy(new SHA256Digest()));
                break;
            case ALG_RSA_SHA_384_PKCS1:
                engine = new RSADigestSigner(new BouncyCastlePrecomputedOrDigestProxy(new SHA384Digest()));
                break;
            case ALG_RSA_SHA_512_PKCS1:
                engine = new RSADigestSigner(new BouncyCastlePrecomputedOrDigestProxy(new SHA512Digest()));
                break;
            case ALG_RSA_SHA_PKCS1_PSS:
                engine = new PSSSigner(new RSAEngine(), new BouncyCastlePrecomputedOrDigestProxy(new SHA1Digest()), 16);
                break;
            case ALG_RSA_SHA_224_PKCS1_PSS:
                engine = new PSSSigner(new RSAEngine(), new BouncyCastlePrecomputedOrDigestProxy(new SHA224Digest()), 28);
                break;
            case ALG_RSA_SHA_256_PKCS1_PSS:
                engine = new PSSSigner(new RSAEngine(), new BouncyCastlePrecomputedOrDigestProxy(new SHA256Digest()), 32);
                break;
            case ALG_RSA_SHA_384_PKCS1_PSS:
                engine = new PSSSigner(new RSAEngine(), new BouncyCastlePrecomputedOrDigestProxy(new SHA384Digest()), 48);
                break;
            case ALG_RSA_SHA_512_PKCS1_PSS:
                engine = new PSSSigner(new RSAEngine(), new BouncyCastlePrecomputedOrDigestProxy(new SHA512Digest()), 64);
                break;
            case ALG_RSA_MD5_PKCS1:
                engine = new RSADigestSigner(new BouncyCastlePrecomputedOrDigestProxy(new MD5Digest()));
                break;
            case ALG_RSA_RIPEMD160_ISO9796:
                engine = new ISO9796d2Signer(new RSAEngine(), new RIPEMD160Digest());
                break;
            case ALG_RSA_RIPEMD160_PKCS1:
                engine = new RSADigestSigner(new BouncyCastlePrecomputedOrDigestProxy(new RIPEMD160Digest()));
                break;
            case ALG_ECDSA_SHA:
                engine = new DSADigestSigner(new ECDSASigner(), new BouncyCastlePrecomputedOrDigestProxy(new SHA1Digest()));
                break;
            case ALG_ECDSA_SHA_224:
                engine = new DSADigestSigner(new ECDSASigner(), new BouncyCastlePrecomputedOrDigestProxy(new SHA224Digest()));
                break;
            case ALG_ECDSA_SHA_256:
                engine = new DSADigestSigner(new ECDSASigner(), new BouncyCastlePrecomputedOrDigestProxy(new SHA256Digest()));
                break;
            case ALG_ECDSA_SHA_384:
                engine = new DSADigestSigner(new ECDSASigner(), new BouncyCastlePrecomputedOrDigestProxy(new SHA384Digest()));
                break;
            case ALG_ECDSA_SHA_512:
                engine = new DSADigestSigner(new ECDSASigner(), new BouncyCastlePrecomputedOrDigestProxy(new SHA512Digest()));
                break;
            default:
                CryptoException.throwIt(CryptoException.NO_SUCH_ALGORITHM);
        }
    }

    private boolean isRawECDSAWithoutHash() {
        return algorithm == MessageDigest.ALG_NULL && cipherAlgorithm == Signature.SIG_CIPHER_ECDSA && paddingAlgorithm == Cipher.PAD_NULL;
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
        if (isRawECDSAWithoutHash()) {
            return getECDSASignatureLength();
        }
        switch (algorithm) {
            case ALG_RSA_SHA_ISO9796:
            case ALG_RSA_SHA_PKCS1:
            case ALG_RSA_SHA_224_PKCS1:
            case ALG_RSA_SHA_256_PKCS1:
            case ALG_RSA_SHA_384_PKCS1:
            case ALG_RSA_SHA_512_PKCS1:
            case ALG_RSA_SHA_PKCS1_PSS:
            case ALG_RSA_SHA_224_PKCS1_PSS:
            case ALG_RSA_SHA_256_PKCS1_PSS:
            case ALG_RSA_SHA_384_PKCS1_PSS:
            case ALG_RSA_SHA_512_PKCS1_PSS:
            case ALG_RSA_MD5_PKCS1:
            case ALG_RSA_RIPEMD160_ISO9796:
            case ALG_RSA_RIPEMD160_PKCS1:
                return (short)(key.getSize()>>3);
            case ALG_ECDSA_SHA:
            case ALG_ECDSA_SHA_256:
            case ALG_ECDSA_SHA_224:
            case ALG_ECDSA_SHA_384:
            case ALG_ECDSA_SHA_512:
                // x,y + der payload
                return getECDSASignatureLength();
        }
        return 0;
    }
    
    private short getECDSASignatureLength() {
        int keySizeInByte =key.getSize() / 8;
        int signatureSize = keySizeInByte * 2; // r, s
        return (short) (signatureSize + 8); // with payload
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
        if (isRecovery) {
            CryptoException.throwIt(CryptoException.ILLEGAL_USE);
        }
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
        if (isRecovery) {
            CryptoException.throwIt(CryptoException.ILLEGAL_USE);
        }
        if (!isInitialized) {
            CryptoException.throwIt(CryptoException.INVALID_INIT);
        }
        engine.update(inBuff, inOffset, inLength);
        byte[] sig = new byte[sigLength];
        Util.arrayCopyNonAtomic(sigBuff, sigOffset, sig, (short) 0, sigLength);
        boolean b = engine.verifySignature(sig);
        engine.reset();
        return b;
    }

    public short beginVerify(byte[] sigAndRecDataBuff, short buffOffset, short sigLength) throws CryptoException {
        if (!isRecovery) {
            CryptoException.throwIt(CryptoException.ILLEGAL_USE);
        }
        if (!isInitialized) {
            CryptoException.throwIt(CryptoException.INVALID_INIT);
        }
        preSig = JCSystem.makeTransientByteArray(sigLength, JCSystem.CLEAR_ON_RESET);
        Util.arrayCopyNonAtomic(sigAndRecDataBuff, buffOffset, preSig, (short) 0, sigLength);
        try {
            ((SignerWithRecovery) engine).updateWithRecoveredMessage(preSig);
            return (short) ((SignerWithRecovery) engine).getRecoveredMessage().length;
        } catch (Exception ex) {
            CryptoException.throwIt(CryptoException.ILLEGAL_VALUE);
        }
        return -1;
    }

    public short sign(byte[] inBuff, short inOffset, short inLength, byte[] sigBuff, short sigOffset, short[] recMsgLen,
            short recMsgLenOffset) throws CryptoException {
        if (!isRecovery) {
            CryptoException.throwIt(CryptoException.ILLEGAL_USE);
        }
        if (!isInitialized) {
            CryptoException.throwIt(CryptoException.INVALID_INIT);
        }
        engine.update(inBuff, inOffset, inLength);
        byte[] sig;
        try {
            sig = engine.generateSignature();
            Util.arrayCopyNonAtomic(sig, (short) 0, sigBuff, sigOffset, (short) sig.length);
            // there is no direct way to obtain encoded message length
            int keyBits = key.getSize();
            Field messageLengthField = engine.getClass().getDeclaredField("messageLength");
            messageLengthField.setAccessible(true);
            int messageLength = messageLengthField.getInt(engine);
            int digSize = 20;
            int x = (digSize + messageLength) * 8 + 16 + 4 - keyBits;
            int mR = messageLength;
            if (x > 0) {
                mR = messageLength - ((x + 7) / 8);
            }
            recMsgLen[recMsgLenOffset] = (short) mR;
            return (short) sig.length;
        } catch (org.bouncycastle.crypto.CryptoException ex) {
            CryptoException.throwIt(CryptoException.ILLEGAL_USE);
        } catch (DataLengthException ex) {
            CryptoException.throwIt(CryptoException.ILLEGAL_VALUE);
        } catch (Exception ex) {
            CryptoException.throwIt(CryptoException.ILLEGAL_USE);
        } finally {
            engine.reset();
        }
        return -1;
    }

    public boolean verify(byte[] inBuff, short inOffset, short inLength) throws CryptoException {
        if(!isRecovery){
            CryptoException.throwIt(CryptoException.ILLEGAL_USE);
        }
        if(preSig == null){
            CryptoException.throwIt(CryptoException.ILLEGAL_USE);
        }
        if (!isInitialized) {
            CryptoException.throwIt(CryptoException.INVALID_INIT);
        }
        engine.update(inBuff, inOffset, inLength);
        boolean b = engine.verifySignature(preSig);
        engine.reset();
        return b;
    }

    public void setInitialDigest(byte[] bytes, short s, short s1, byte[] bytes1, short s2, short s3) throws CryptoException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private byte[] ecSigDerEncode(BigInteger r, BigInteger s) throws IOException
    {
        ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new DERInteger(r));
        v.add(new DERInteger(s));
        return (new DERSequence(v).getDEREncoded());
    }

    public short signPreComputedHash(byte[] hashBuff,
                            short hashOffset,
                            short hashLength,
                            byte[] sigBuff,
                            short sigOffset) throws CryptoException {
        try {
            if((engine instanceof RSADigestSigner) || (engine instanceof DSADigestSigner) || (engine instanceof PSSSigner)) {
                // set precomputed hava value - BouncyCastle specific
                 Field h = engine.getClass().getDeclaredField(engine instanceof PSSSigner ? "contentDigest" : "digest");
                 h.setAccessible(true);
                 Object digestObject = h.get(engine);
                 digestObject.getClass().getMethod("setPrecomputedValue", new Class[]{byte[].class, int.class, int.class})
                         .invoke(digestObject, new Object[]{hashBuff,hashOffset,hashLength});
                 return sign(null, (short) 0, (short) 0, sigBuff, sigOffset);
            }
        } catch(ReflectiveOperationException e) {}
        try {
            if((engine instanceof DSADigestSigner) && dsaImpl != null) {
                final byte[] hash = new byte[hashLength];
                Util.arrayCopyNonAtomic(hashBuff, hashOffset, hash, (short)0, hashLength);
                final BigInteger[] sigBi = dsaImpl.generateSignature(hash);
                final byte[] sig = ecSigDerEncode(sigBi[0], sigBi[1]);
                Util.arrayCopyNonAtomic(sig, (short) 0, sigBuff, sigOffset, (short) sig.length);
                return (short) sig.length;
            }
        } catch(IOException e) {}

        CryptoException.throwIt(CryptoException.ILLEGAL_USE);
        return 0;
    }

    public boolean verifyPreComputedHash(byte[] hashBuff, short hashOffset, short hashLength, byte[] sigBuff, short sigOffset, short sigLength) throws CryptoException {
        try {
            if((engine instanceof RSADigestSigner) || (engine instanceof DSADigestSigner) || (engine instanceof PSSSigner)) {
                // set precomputed hava value - BouncyCastle specific
                 Field h = engine.getClass().getDeclaredField(engine instanceof PSSSigner ? "contentDigest" : "digest");
                 h.setAccessible(true);
                 Object digestObject = h.get(engine);
                 digestObject.getClass().getMethod("setPrecomputedValue", new Class[]{byte[].class, int.class, int.class})
                         .invoke(digestObject, new Object[]{hashBuff,hashOffset,hashLength});
                 return verify(null, (short) 0, (short) 0, sigBuff, sigOffset, sigLength);
            }
        } catch(ReflectiveOperationException e) {
        }

        CryptoException.throwIt(CryptoException.ILLEGAL_USE);
        return false;
    }

    public byte getPaddingAlgorithm() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public byte getCipherAlgorithm() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public byte getMessageDigestAlgorithm() {
       throw new UnsupportedOperationException("Not supported yet.");
    }
}
