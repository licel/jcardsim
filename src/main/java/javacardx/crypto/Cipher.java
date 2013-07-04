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
package javacardx.crypto;

import com.licel.jcardsim.crypto.AssymetricCipherImpl;
import com.licel.jcardsim.crypto.SymmetricCipherImpl;
import javacard.security.CryptoException;
import javacard.security.Key;

/**
 * The <code>Cipher</code> class is the abstract base class for Cipher algorithms. Implementations of Cipher
 * algorithms must extend this class and implement all the abstract methods.
 * <p>The term "pad" is used in the public key cipher algorithms below to refer to all the
 * operations specified in the referenced scheme to transform the message block into
 * the cipher block size.
 * <p> The asymmetric key algorithms encrypt using either a public key (to cipher) or a private key (to sign).
 * In addition they decrypt using the either a private key (to decipher) or a public key (to verify).
 * <p> A tear or card reset event resets an initialized
 * <code>Cipher</code> object to the state it was in when previously initialized
 * via a call to <code>init()</code>. For algorithms which support keys with transient
 * key data sets, such as DES, triple DES and AES,
 * the <code>Cipher</code> object key becomes
 * uninitialized on clear events associated with the <code>Key</code>
 * object used to initialize the <code>Cipher</code> object.
 * <p> Even if a transaction is in progress, update of intermediate result state in the implementation
 * instance shall not participate in the transaction.<br>
 * <p>Note:
 * <ul>
 * <li><em>On a tear or card reset event, the AES, DES, and triple DES algorithms in CBC mode
 * reset the initial vector(IV) to 0. The initial vector(IV) can be re-initialized using the
 * </em><code>init(Key, byte, byte[], short, short)</code><em> method.</em>
 * </ul>
 */
public abstract class Cipher {

    /**
     * Cipher algorithm <code>ALG_DES_CBC_NOPAD</code> provides a cipher using DES in CBC mode
     * or triple DES in outer CBC mode, and
     * does not pad input data. If the input data is not (8-byte) block
     * aligned it throws <code>CryptoException</code> with the reason code <code>ILLEGAL_USE</code>.
     */
    public static final byte ALG_DES_CBC_NOPAD = 1;
    /**
     * Cipher algorithm <code>ALG_DES_CBC_ISO9797_M1</code> provides a cipher using DES
     * in CBC mode or triple DES in outer CBC mode, and pads
     * input data according to the ISO 9797 method 1 scheme.
     */
    public static final byte ALG_DES_CBC_ISO9797_M1 = 2;
    /**
     * Cipher algorithm <code>ALG_DES_CBC_ISO9797_M2</code> provides a cipher using DES
     * in CBC mode or triple DES in outer CBC mode, and pads
     * input data according to the ISO 9797 method 2 (ISO 7816-4, EMV'96) scheme.
     */
    public static final byte ALG_DES_CBC_ISO9797_M2 = 3;
    /**
     * Cipher algorithm <code>ALG_DES_CBC_PKCS5</code> provides a cipher using DES
     * in CBC mode or triple DES in outer CBC mode, and pads
     * input data according to the PKCS#5 scheme.
     */
    public static final byte ALG_DES_CBC_PKCS5 = 4;
    /**
     * Cipher algorithm <code>ALG_DES_ECB_NOPAD</code> provides a cipher using DES in ECB mode,
     * and does not pad input data. If the input data is not (8-byte) block
     * aligned it throws <code>CryptoException</code> with the reason code <code>ILLEGAL_USE</code>.
     */
    public static final byte ALG_DES_ECB_NOPAD = 5;
    /**
     * Cipher algorithm <code>ALG_DES_ECB_ISO9797_M1</code> provides a cipher using DES
     * in ECB mode, and pads
     * input data according to the ISO 9797 method 1 scheme.
     */
    public static final byte ALG_DES_ECB_ISO9797_M1 = 6;
    /**
     * Cipher algorithm <code>ALG_DES_ECB_ISO9797_M2</code> provides a cipher using DES
     * in ECB mode, and pads
     * input data according to the ISO 9797 method 2 (ISO 7816-4, EMV'96) scheme.
     */
    public static final byte ALG_DES_ECB_ISO9797_M2 = 7;
    /**
     * Cipher algorithm <code>ALG_DES_ECB_PKCS5</code> provides a cipher using DES
     * in ECB mode, and pads
     * input data according to the PKCS#5 scheme.
     */
    public static final byte ALG_DES_ECB_PKCS5 = 8;
    /**
     * Cipher algorithm <code>ALG_RSA_ISO14888</code> provides a cipher using RSA, and pads
     * input data according to the ISO 14888 scheme.
     */
    public static final byte ALG_RSA_ISO14888 = 9;
    /**
     * Cipher algorithm <code>ALG_RSA_PKCS1</code> provides a cipher using RSA, and pads
     * input data according to the PKCS#1 (v1.5) scheme.
     * <p>Note:
     * <ul>
     * <li><em>This algorithm is only suitable for messages of limited length.
     * The total number of input bytes processed may not be more than k-11,
     * where k is the RSA key's modulus size in bytes.</em>
     * <li><em> The encryption block(EB) during encryption with a Public key
     * is built as follows:<br>
     * &nbsp; EB = 00 || 02 || PS || 00 || M<br>
     * &nbsp; &nbsp; &nbsp; :: M (input bytes) is the plaintext message<br>
     * &nbsp; &nbsp; &nbsp; :: PS is an octet string of length k-3-||M|| of pseudo
     * random nonzero octets. The length of PS must be at least 8 octets.<br>
     * &nbsp; &nbsp; &nbsp; :: k is the RSA modulus size.</em><br>
     * <li><em> The encryption block(EB) during encryption with a Private key
     * (used to compute signatures when the message digest is computed off-card)
     * is built as follows:<br>
     * &nbsp; EB = 00 || 01 || PS || 00 || D<br>
     * &nbsp; &nbsp; &nbsp; :: D (input bytes) is the DER encoding of the
     * hash computed elsewhere with an algorithm ID
     * prepended if appropriate<br>
     * &nbsp; &nbsp; &nbsp; :: PS is an octet string of length k-3-||D|| with value
     * FF. The length of PS must be at least 8 octets.<br>
     * &nbsp; &nbsp; &nbsp; :: k is the RSA modulus size.</em><br>
     * </ul>
     */
    public static final byte ALG_RSA_PKCS1 = 10;
    /**
     * This Cipher algorithm <code>ALG_RSA_ISO9796</code> should not be used. The
     * ISO 9796-1 algorithm was withdrawn by ISO in July 2000.
     */
    public static final byte ALG_RSA_ISO9796 = 11;
    /**
     * Cipher algorithm <code>ALG_RSA_NOPAD</code> provides a cipher using RSA and
     * does not pad input data. If the input data is bounded by incorrect
     * padding bytes while using RSAPrivateCrtKey, incorrect output may result.
     * If the input data is not block aligned it throws <code>CryptoException</code> with
     * the reason code <code>ILLEGAL_USE</code>.
     */
    public static final byte ALG_RSA_NOPAD = 12;
    /**
     * Cipher algorithm <code>ALG_AES_BLOCK_128_CBC_NOPAD</code> provides a cipher using AES with
     * block size 128 in CBC mode and
     * does not pad input data. If the input data is not block
     * aligned it throws <code>CryptoException</code> with the reason code <code>ILLEGAL_USE</code>.
     */
    public static final byte ALG_AES_BLOCK_128_CBC_NOPAD = 13;
    /**
     * Cipher algorithm <code>ALG_AES_BLOCK_128_ECB_NOPAD</code> provides a cipher using AES with
     * block size 128 in ECB mode and
     * does not pad input data. If the input data is not block
     * aligned it throws <code>CryptoException</code> with the reason code <code>ILLEGAL_USE</code>.
     */
    public static final byte ALG_AES_BLOCK_128_ECB_NOPAD = 14;
    /**
     * Cipher algorithm <code>ALG_RSA_PKCS1_OAEP</code> provides a cipher using RSA, and
     * pads input data according to the PKCS#1-OAEP scheme (IEEE 1363-2000).
     */
    public static final byte ALG_RSA_PKCS1_OAEP = 15;
    /**
     * Used in <code>init()</code> methods to indicate decryption mode.
     */
    public static final byte MODE_DECRYPT = 1;
    /**
     * Used in <code>init()</code> methods to indicate encryption mode.
     */
    public static final byte MODE_ENCRYPT = 2;

    /**
     * Protected constructor.
     */
    protected Cipher() {
    }

    /**
     * Creates a <code>Cipher</code> object instance of the selected algorithm.
     * @param algorithm the desired Cipher algorithm. Valid codes listed in
     * ALG_ .. constants above, for example, {@link #ALG_DES_CBC_NOPAD}
     * @param externalAccess indicates that the instance will be shared among
     * multiple applet instances and that the <code>Cipher</code> instance will also be accessed (via a <code>Shareable</code>
     * interface) when the owner of the <code>Cipher</code> instance is not the currently selected applet.
     * If <code>true</code> the implementation must not allocate CLEAR_ON_DESELECT transient space for internal data.
     * @return the <code>Cipher</code> object instance of the requested algorithm
     * @throws CryptoException with the following reason codes:
     * <ul>
     *  <li><code>CryptoException.NO_SUCH_ALGORITHM</code> if the requested algorithm is not supported
     *  or shared access mode is not supported.
     * </ul>
     */
    public static final Cipher getInstance(byte algorithm, boolean externalAccess)
            throws CryptoException {
        Cipher instance = null;
        if (externalAccess) {
            CryptoException.throwIt((short) 3);
        }
        switch (algorithm) {
            case ALG_DES_CBC_NOPAD:
            case ALG_DES_CBC_ISO9797_M1:
            case ALG_DES_CBC_ISO9797_M2:
            case ALG_DES_CBC_PKCS5:
            case ALG_DES_ECB_NOPAD:
            case ALG_DES_ECB_ISO9797_M1:
            case ALG_DES_ECB_ISO9797_M2:
            case ALG_DES_ECB_PKCS5:
            case ALG_AES_BLOCK_128_CBC_NOPAD:
            case ALG_AES_BLOCK_128_ECB_NOPAD:
                instance = new SymmetricCipherImpl(algorithm);
                break;
            case ALG_RSA_PKCS1:
            case ALG_RSA_NOPAD:
            case ALG_RSA_ISO14888:
            case ALG_RSA_ISO9796:
            case ALG_RSA_PKCS1_OAEP:
                instance = new AssymetricCipherImpl(algorithm);
                break;
            default:
                CryptoException.throwIt(CryptoException.NO_SUCH_ALGORITHM);
                break;
        }
        return instance;
    }

    /**
     * Initializes the <code>Cipher</code> object with the appropriate <code>Key</code>.
     * This method should be used
     * for algorithms which do not need initialization parameters or use default parameter
     * values.
     * <p><code>init()</code> must be used to update the <code>Cipher</code> object with a new key.
     * If the <code>Key</code> object is modified after invoking the <code>init()</code> method,
     * the behavior of the <code>update()</code> and <code>doFinal()</code>
     * methods is unspecified.
     * <p>Note:
     * <ul>
     * <li><em>AES, DES, and triple DES algorithms in CBC mode will use 0 for initial vector(IV) if this
     * method is used.</em>
     * </ul>
     * @param theKey the key object to use for encrypting or decrypting
     * @param theMode one of <code>MODE_DECRYPT</code> or <code>MODE_ENCRYPT</code>
     * @throws CryptoException with the following reason codes:
     * <ul>
     * <li><code>CryptoException.ILLEGAL_VALUE</code> if <code>theMode</code> option is an undefined value or
     * if the <code>Key</code> is inconsistent with the <code>Cipher</code> implementation.
     * <li><code>CryptoException.UNINITIALIZED_KEY</code> if <code>theKey</code> instance is uninitialized.
     * </ul>
     */
    public abstract void init(Key theKey, byte theMode)
            throws CryptoException;

    /**
     * Initializes the <code>Cipher</code> object with the appropriate Key and algorithm specific
     * parameters.
     * <p><code>init()</code> must be used to update the <code>Cipher</code> object with a new key.
     * If the <code>Key</code> object is modified after invoking the <code>init()</code> method,
     * the behavior of the <code>update()</code> and <code>doFinal()</code>
     * methods is unspecified.
     * <p>Note:
     * <ul>
     * <li><em>DES and triple DES algorithms in CBC mode expect an 8-byte parameter value for
     * the initial vector(IV) in </em><code>bArray</code><em>.</em>
     * <li><em>AES algorithms in CBC mode expect a 16-byte parameter value for
     * the initial vector(IV) in </em><code>bArray</code><em>.</em>
     * <li><em>AES algorithms in ECB mode, DES algorithms in ECB mode,
     * RSA and DSA algorithms throw </em><code>CryptoException.ILLEGAL_VALUE</code><em>.</em>
     * </ul>
     * @param theKey the key object to use for encrypting or decrypting.
     * @param theMode one of <code>MODE_DECRYPT</code> or <code>MODE_ENCRYPT</code>
     * @param bArray byte array containing algorithm specific initialization info
     * @param bOff offset within bArray where the algorithm specific data begins
     * @param bLen byte length of algorithm specific parameter data
     * @throws CryptoException with the following reason codes:
     * <ul>
     * <li><code>CryptoException.ILLEGAL_VALUE</code> if <code>theMode</code> option is an undefined value
     * or if a byte array parameter option is not supported by the algorithm or if
     * the <code>bLen</code> is an incorrect byte length for the algorithm specific data or
     * if the <code>Key</code> is inconsistent with the <code>Cipher</code> implementation.
     * <li><code>CryptoException.UNINITIALIZED_KEY</code> if <code>theKey</code> instance is uninitialized.
     * </ul>
     */
    public abstract void init(Key theKey, byte theMode, byte[] bArray, short bOff, short bLen)
            throws CryptoException;

    /**
     * Gets the Cipher algorithm.
     * @return the algorithm code defined above
     */
    public abstract byte getAlgorithm();

    /**
     * Generates encrypted/decrypted output from all/last input data.  This method must be invoked
     * to complete a cipher operation.  This method processes any remaining input data buffered by
     * one or more calls to the <code>update()</code> method as well as input data supplied in the
     * <code>inBuff</code> parameter.
     * <p>A call to this method also resets this <code>Cipher</code> object to the state it was in
     *  when previously initialized via a call to <code>init()</code>.
     * That is, the object is reset and available to encrypt or decrypt
     * (depending on the operation mode that was specified in the call to <code>init()</code>) more data.
     * In addition, note that the initial vector(IV) used in AES and DES algorithms will be reset to 0.
     * <p>Notes:
     * <ul>
     * <li><em>When using block-aligned data (multiple of block size),
     * if the input buffer, </em><code>inBuff</code><em> and the output buffer,
     * </em><code>outBuff</code><em>
     * are the same array, then the output data area must not partially overlap the input data area such that
     * the input data is modified before it is used;
     * if </em><code>inBuff==outBuff</code><em> and<br> </em><code>inOffset < outOffset < inOffset+inLength</code><em>,
     * incorrect output may result.</em>
     * <li><em>When non-block aligned data is presented as input data, no amount of input
     * and output buffer data overlap is allowed;
     * if </em><code>inBuff==outBuff</code><em> and<br> </em><code>outOffset < inOffset+inLength</code><em>,
     * incorrect output may result.</em>
     * <li><em>AES, DES, and triple DES algorithms in CBC mode reset the initial vector(IV)
     * to 0. The initial vector(IV) can be re-initialized using the
     * </em><code>init(Key, byte, byte[], short, short)</code><em> method.</em>
     * <li><em>On decryption operations (except when ISO 9797 method 1 padding is used),
     * the padding bytes are not written to </em><code>outBuff</code><em>.</em>
     * <li><em>On encryption and decryption operations, the number of bytes output into </em><code>outBuff</code><em>
     * may be larger or smaller than </em><code>inLength</code><em> or even 0.</em>
     * <li><em>On decryption operations resulting in an </em><code>ArrayIndexOutOfBoundException</code><em>,
     * </em><code>outBuff</code><em> may be partially modified.</em>
     * </ul>
     * @param inBuff the input buffer of data to be encrypted/decrypted
     * @param inOffset the offset into the input buffer at which to begin encryption/decryption
     * @param inLength the byte length to be encrypted/decrypted
     * @param outBuff the output buffer, may be the same as the input buffer
     * @param outOffset the offset into the output buffer where the resulting output data begins
     * @return number of bytes output in <code>outBuff</code>
     * @throws CryptoException with the following reason codes:
     * <ul>
     * <li><code>CryptoException.UNINITIALIZED_KEY</code> if key not initialized.
     * <li><code>CryptoException.INVALID_INIT</code> if this <code>Cipher</code> object is
     * not initialized.
     * <li><code>CryptoException.ILLEGAL_USE</code> if one of the following conditions is met:
     * <ul>
     * <li>This <code>Cipher</code> algorithm
     * does not pad the message and the message is not block aligned.
     * <li>This <code>Cipher</code> algorithm
     * does not pad the message and no input
     * data has been provided in <code>inBuff</code> or via the <code>update()</code> method.
     * <li>The input message length is not supported.
     * <li>The decrypted data is not bounded by appropriate padding bytes.
     * </ul>
     * </ul>
     */
    public abstract short doFinal(byte[] inBuff, short inOffset, short inLength, byte[] outBuff, short outOffset)
            throws CryptoException;

    /**
     * Generates encrypted/decrypted output from input data. This method is intended for multiple-part
     * encryption/decryption operations.
     * <p>This method requires temporary storage of
     * intermediate results. In addition, if the input data length is not block aligned
     * (multiple of block size)
     * then additional internal storage may be allocated at this time to store a partial
     * input data block.
     * This may result in additional resource consumption and/or slow performance.
     * <p>This method should only be used if all the input data required for the cipher
     * is not available in one byte array.  If all the input data required for the cipher
     * is located in a single byte array, use of the <code>doFinal()</code> method to
     * process all of the input data is recommended.  The <code>doFinal()</code> method
     * must be invoked to complete processing of any remaining input data buffered by one or more calls
     * to the <code>update()</code> method.
     * <p>Notes:<ul>
     * <li><em>When using block-aligned data (multiple of block size),
     * if the input buffer, </em><code>inBuff</code><em> and the output buffer,
     * </em><code>outBuff</code><em>
     * are the same array, then the output data area must not partially overlap the input data area such that
     * the input data is modified before it is used;
     * if </em><code>inBuff==outBuff</code><em> and<br> </em><code>inOffset < outOffset < inOffset+inLength</code><em>,
     * incorrect output may result.</em>
     * <li><em>When non-block aligned data is presented as input data, no amount of input
     * and output buffer data overlap is allowed;
     * if </em><code>inBuff==outBuff</code><em> and<br> </em><code>outOffset < inOffset+inLength</code><em>,
     * incorrect output may result.</em>
     * <li><em>On decryption operations(except when ISO 9797 method 1 padding is used),
     * the padding bytes are not written to </em><code>outBuff</code><em>.</em>
     * <li><em>On encryption and decryption operations,
     * block alignment considerations may require that
     * the number of bytes output into </em><code>outBuff</code><em> be larger or smaller than
     * </em><code>inLength</code><em> or even 0.</em>
     * <li><em>If </em><code>inLength</code><em> is 0 this method does nothing.</em>
     * </ul>
     * @param inBuff the input buffer of data to be encrypted/decrypted
     * @param inOffset the offset into the input buffer at which to begin encryption/decryption
     * @param inLength the byte length to be encrypted/decryptedv
     * @param outBuff the output buffer, may be the same as the input buffer
     * @param outOffset the offset into the output buffer where the resulting ciphertext/plaintext begins
     * @return number of bytes output in <code>outBuff</code>
     * @throws CryptoException with the following reason codes:
     * <ul>
     * <li><code>CryptoException.UNINITIALIZED_KEY</code> if key not initialized.
     * <li><code>CryptoException.INVALID_INIT</code> if this <code>Cipher</code> object is
     * not initialized.
     * <li><code>CryptoException.ILLEGAL_USE</code> if the input message length is not supported.
     * </ul>
     */
    public abstract short update(byte[] inBuff, short inOffset, short inLength, byte[] outBuff, short outOffset)
            throws CryptoException;


}
