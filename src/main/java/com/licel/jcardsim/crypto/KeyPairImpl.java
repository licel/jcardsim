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

import java.math.BigInteger;
import java.security.SecureRandom;
import javacard.security.CryptoException;
import javacard.security.KeyBuilder;
import javacard.security.KeyPair;
import javacard.security.PrivateKey;
import javacard.security.PublicKey;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.generators.DSAKeyPairGenerator;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.generators.RSAKeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.DSAKeyParameters;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import org.bouncycastle.crypto.params.DSAValidationParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.RSAKeyGenerationParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;

/**
 * Implementation <code>KeyPair</code> based
 * on BouncyCastle CryptoAPI
 * @see KeyPair
 * @see RSAKeyPairGenerator
 * @see DSAKeyPairGenerator
 * @see ECKeyPairGenerator
 */
public final class KeyPairImpl {

    byte algorithm;
    AsymmetricCipherKeyPairGenerator engine;
    PrivateKey privateKey;
    PublicKey publicKey;
    SecureRandom rnd = new SecureRandom();
    KeyGenerationParameters keyGenerationParameters;

    /**
     * (Re)Initializes the key objects encapsulated in this <code>KeyPair</code> instance
     * with new key values. The initialized public and private key objects
     * encapsulated in this instance will then be suitable for use with the
     * <code>Signature</code>, <code>Cipher</code> and <code>KeyAgreement</code> objects.
     * An internal secure random number generator is used during new key pair generation.
     * <p>Notes:<ul>
     * <li><em>For the RSA algorithm, if the exponent value in the public key object is pre-initialized,
     * it will be retained. Otherwise, a default value of 65537 will be used.</em>
     * <li><em>For the DSA algorithm, if the p, q and g parameters of the public key object are pre-initialized,
     * they will be retained. Otherwise, default precomputed parameter sets will be used. The required
     * default precomputed values are listed in </em>Appendix B<em> of </em>Java Cryptography Architecture
     * API Specification & Reference<em> document.</em>
     * <li><em>For the EC case, if the Field, A, B, G and R parameters of the
     * key pair are pre-initialized, then they will be retained. Otherwise
     * default pre-specified values MAY be used (e.g. WAP predefined curves),
     * since computation of random generic EC keys is infeasible on the smart
     * card platform.</em>
     * <li><em>If the time taken to generate the key values is excessive, the implementation may automatically
     * request additional APDU processing time from the CAD.</em>
     * </ul>
     * @throws CryptoException with the following reason codes:<ul>
     * <li><code>CryptoException.ILLEGAL_VALUE</code> if the exponent
     * value parameter in RSA or the p, q, g parameter set in DSA or
     * the Field, A, B, G and R parameter set in EC is invalid.
     * </ul>
     * @see APDU
     * @see Signature
     * @see Cipher
     * @see RSAPublicKey
     * @see ECKey
     * @see DSAKey
     */
    public final void genKeyPair()
            throws CryptoException {
        AsymmetricCipherKeyPair kp = engine.generateKeyPair();
        // rsa
        if (kp.getPublic() instanceof RSAKeyParameters) {
            publicKey = new RSAKeyImpl((RSAKeyParameters) kp.getPublic());
            if (kp.getPrivate() instanceof RSAPrivateCrtKeyParameters) {
                privateKey = new RSAPrivateCrtKeyImpl((RSAPrivateCrtKeyParameters) kp.getPrivate());
            } else if (kp.getPrivate() instanceof RSAKeyParameters) {
                privateKey = new RSAKeyImpl((RSAKeyParameters) kp.getPrivate());
            }
        } else // dsa
        if (kp.getPublic() instanceof DSAPublicKeyParameters) {
            publicKey = new DSAPublicKeyImpl((DSAPublicKeyParameters) kp.getPublic());
            privateKey = new DSAPrivateKeyImpl((DSAPrivateKeyParameters) kp.getPrivate());
        } else // ecc
        if (kp.getPublic() instanceof ECPublicKeyParameters) {
            publicKey = new ECPublicKeyImpl((ECPublicKeyParameters) kp.getPublic());
            privateKey = new ECPrivateKeyImpl((ECPrivateKeyParameters) kp.getPrivate());
        } else {
            CryptoException.throwIt(CryptoException.ILLEGAL_USE);
        }
    }

    /**
     * Constructs a <code>KeyPair</code> instance for the specified algorithm and keylength;
     * the encapsulated keys are uninitialized.
     * To initialize the <code>KeyPair</code> instance use the <code>genKeyPair()</code> method.<p>
     * The encapsulated key objects are of the specified <code>keyLength</code> size and
     * implement the appropriate <code>Key</code> interface associated with the specified algorithm
     * (example - <code>RSAPublicKey</code> interface for the public key and <code>RSAPrivateKey</code>
     * interface for the private key within an <code>ALG_RSA</code> key pair).<p>
     * <p>Notes:<ul>
     * <li><em>The key objects encapsulated in the generated </em><code>KeyPair</code><em> object
     * need not support the </em><code>KeyEncryption</code><em> interface.</em>
     * </ul>
     * @param algorithm the type of algorithm whose key pair needs to be generated.
     * Valid codes listed in <code>ALG_..</code> constants above. {@link KeyPair}
     * @param keyLength  the key size in bits. The valid key bit lengths are key type dependent.
     * See the <code>KeyBuilder</code> class.
     * @see KeyBuilder
     * @throws CryptoException with the following reason codes:<ul>
     * <li><code>CryptoException.NO_SUCH_ALGORITHM</code> if the requested algorithm
     * associated with the specified type, size of key is not supported.</ul>
     * @see KeyBuilder
     * @see Signature
     * @see KeyEncryption
     * @see Cipher
     */
    public KeyPairImpl(byte algorithm, short keyLength)
            throws CryptoException {
        this.algorithm = algorithm;
        switch (algorithm) {
            case KeyPair.ALG_RSA:
            case KeyPair.ALG_RSA_CRT:
                if (keyGenerationParameters == null) {
                    initRSADefaultsParameters(keyLength);
                }
                engine = new RSAKeyPairGenerator();
                break;
            //
            case KeyPair.ALG_DSA:
                if (keyLength < 512 || keyLength > 1024 || keyLength % 64 != 0) {
                    CryptoException.throwIt(CryptoException.ILLEGAL_VALUE);
                }
                if (keyGenerationParameters == null) {
                    initDSADefaultsParameters(keyLength);
                }
                engine = new DSAKeyPairGenerator();
                break;

            // ecc
            case KeyPair.ALG_EC_F2M:
            case KeyPair.ALG_EC_FP:
                if (keyGenerationParameters == null) {
                    initECDefaultsParameters(keyLength);
                }
                engine = new ECKeyPairGenerator();
                break;

            default:
                CryptoException.throwIt(CryptoException.NO_SUCH_ALGORITHM);
                break;
        }
        engine.init(keyGenerationParameters);
    }

    /**
     * Constructs a new <code>KeyPair</code> object containing the specified
     * public key and private key.
     * <p>Note that this constructor only stores references to the public
     * and private key components in the generated <code>KeyPair</code> object. It
     * does not throw an exception if the key parameter objects are uninitialized.
     * @param publicKey the public key.
     * @param privateKey the private key.
     * @throws CryptoException with the following reason codes:<ul>
     * <li><code>CryptoException.ILLEGAL_VALUE</code> if the input parameter key
     * objects are inconsistent with each other - i.e mismatched algorithm, size etc.
     * <li><code>CryptoException.NO_SUCH_ALGORITHM</code> if the algorithm
     * associated with the specified type, size of key is not supported.
     * </ul>
     */
    public KeyPairImpl(PublicKey publicKey, PrivateKey privateKey)
            throws CryptoException {
        if (publicKey == null || privateKey == null) {
            CryptoException.throwIt(CryptoException.ILLEGAL_VALUE);
        }
        if ((publicKey != null) && !(publicKey instanceof KeyWithParameters)) {
            CryptoException.throwIt(CryptoException.ILLEGAL_VALUE);
        }
        this.publicKey = publicKey;
        if ((privateKey != null) && !(privateKey instanceof KeyWithParameters)) {
            CryptoException.throwIt(CryptoException.ILLEGAL_VALUE);
        }
        this.privateKey = privateKey;
        KeyWithParameters params = (this.privateKey == null ? (KeyWithParameters) this.publicKey : (KeyWithParameters) this.privateKey);
        setKeyGenerationParametersFromKey((AsymmetricKeyParameter) params.getParameters());
    }

    /**
     * Returns a reference to the public key component of this <code>KeyPair</code> object.
     * @return a reference to the public key.
     */
    public PublicKey getPublic() {
        return publicKey;
    }

    /**
     * Returns a reference to the private key component of this <code>KeyPair</code> object.
     * @return a reference to the private key.
     */
    public PrivateKey getPrivate() {
        return privateKey;
    }

    /**
     * Set KeyGenerations parameters from keypair
     * @param keyParameter
     */
    private void setKeyGenerationParametersFromKey(AsymmetricKeyParameter keyParameter) {
        if (keyParameter instanceof DSAKeyParameters) {
            keyGenerationParameters = new DSAKeyGenerationParameters(rnd, ((DSAKeyParameters) keyParameter).getParameters());
        } else if (keyParameter instanceof RSAKeyParameters) {
        } else if (keyParameter instanceof ECKeyParameters) {
            keyGenerationParameters = new ECKeyGenerationParameters(((ECKeyParameters) keyParameter).getParameters(), rnd);
        } else {
            CryptoException.throwIt(CryptoException.ILLEGAL_VALUE);
        }

    }

    /**
     * Init RSA KeyGeneration Defaults Parameters
     * {@link http://docs.oracle.com/javase/6/docs/technotes/guides/security/StandardNames.html#alg}
     * @param keyLength in bits
     */
    private void initRSADefaultsParameters(short keyLength) {
        keyGenerationParameters = new RSAKeyGenerationParameters(new BigInteger("10001", 16),
                rnd, keyLength, 80);
    }

    /**
     * Init DSA KeyGeneration Defaults Parameters
     * {@link http://docs.oracle.com/javase/6/docs/technotes/guides/security/StandardNames.html#alg}
     * @param keyLength in bits
     */
    private void initDSADefaultsParameters(short keyLength) {
        BigInteger p = null;
        BigInteger q = null;
        BigInteger g = null;
        BigInteger seed = null;
        short counter = 0;
        switch (keyLength) {
            case 512:
                counter = 123;
                p = new BigInteger("fca682ce8e12caba26efccf7110e526db078b05edecbcd1eb4a208f3"
                        + "ae1617ae01f35b91a47e6df63413c5e12ed0899bcd132acd50d99151"
                        + "bdc43ee737592e17", 16);
                q = new BigInteger("962eddcc369cba8ebb260ee6b6a126d9346e38c5", 16);
                g = new BigInteger("678471b27a9cf44ee91a49c5147db1a9aaf244f05a434d6486931d2d"
                        + "14271b9e35030b71fd73da179069b32e2935630e1c2062354d0da20a"
                        + "6c416e50be794ca4", 16);
                seed = new BigInteger("b869c82b35d70e1b1ff91b28e37a62ecdc34409b", 16);
                break;
            case 768:
                counter = 263;
                p = new BigInteger("e9e642599d355f37c97ffd3567120b8e25c9cd43e927b3a9670fbec5"
                        + "d890141922d2c3b3ad2480093799869d1e846aab49fab0ad26d2ce6a"
                        + "22219d470bce7d777d4a21fbe9c270b57f607002f3cef8393694cf45"
                        + "ee3688c11a8c56ab127a3daf", 16);
                q = new BigInteger("9cdbd84c9f1ac2f38d0f80f42ab952e7338bf511", 16);
                g = new BigInteger("30470ad5a005fb14ce2d9dcd87e38bc7d1b1c5facbaecbe95f190aa7"
                        + "a31d23c4dbbcbe06174544401a5b2c020965d8c2bd2171d366844577"
                        + "1f74ba084d2029d83c1c158547f3a9f1a2715be23d51ae4d3e5a1f6a"
                        + "7064f316933a346d3f529252", 16);
                seed = new BigInteger("77d0f8c4dad15eb8c4f2f8d6726cefd96d5bb399", 16);
                break;
            case 1024:
                counter = 92;
                p = new BigInteger("fd7f53811d75122952df4a9c2eece4e7f611b7523cef4400c31e3f80"
                        + "b6512669455d402251fb593d8d58fabfc5f5ba30f6cb9b556cd7813b"
                        + "801d346ff26660b76b9950a5a49f9fe8047b1022c24fbba9d7feb7c6"
                        + "1bf83b57e7c6a8a6150f04fb83f6d3c51ec3023554135a169132f675"
                        + "f3ae2b61d72aeff22203199dd14801c7", 16);
                q = new BigInteger("9760508f15230bccb292b982a2eb840bf0581cf5", 16);
                g = new BigInteger("f7e1a085d69b3ddecbbcab5c36b857b97994afbbfa3aea82f9574c0b"
                        + "3d0782675159578ebad4594fe67107108180b449167123e84c281613"
                        + "b7cf09328cc8a6e13c167a8b547c8d28e0a3ae1e2bb3a675916ea37f"
                        + "0bfa213562f1fb627a01243bcca4f1bea8519089a883dfe15ae59f06"
                        + "928b665e807b552564014c3bfecf492a", 16);
                seed = new BigInteger("8d5155894229d5e689ee01e6018a237e2cae64cd", 16);
                break;
            default:
                CryptoException.throwIt(CryptoException.ILLEGAL_VALUE);
                break;
        }
        keyGenerationParameters = new DSAKeyGenerationParameters(rnd,
                new DSAParameters(p, q, g, new DSAValidationParameters(seed.toByteArray(), counter)));

    }

    /**
     * Init EC KeyGeneration Defaults Parameters
     * {@link http://www.secg.org/collateral/sec2_final.pdf}
     * @param keyLength in bits
     */
    private void initECDefaultsParameters(short keyLength) {
        byte keyType = 0;
        switch (algorithm) {
            case KeyPair.ALG_EC_F2M:
                keyType = KeyBuilder.TYPE_EC_F2M_PUBLIC;
                break;
            case KeyPair.ALG_EC_FP:
                keyType = KeyBuilder.TYPE_EC_FP_PUBLIC;
                break;
            default:
                CryptoException.throwIt(CryptoException.ILLEGAL_VALUE);
                break;
        }
        keyGenerationParameters = new ECKeyGenerationParameters(ECKeyImpl.getDefaultsDomainParameters(keyType, keyLength), rnd);
    }
}