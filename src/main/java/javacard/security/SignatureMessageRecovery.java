/*
 * Copyright 2014 Licel LLC.
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
package javacard.security;

/**
 * A subclass of the abstract
 * <code>Signature</code> class must implement this
 * <code>SignatureMessageRecovery</code> interface to provide message recovery functionality.
 * An instance implementing this interface is returned by the
 * {@link javacard.security.Signature#getInstance(byte, boolean)} method when algorithm type with suffix
 * *_MR is specified. e.g.{@link javacard.security.Signature#ALG_RSA_SHA_ISO9796_MR}.
 *
 * <p>This interface provides specialized versions of some of the methods defined in the
 * <code>Signature</code>
 * class to provide message recovery functions. An alternate version of the
 * <code>sign()</code> and
 * <code>verify()</code> methods is supported here along with a
 * new
 * <code>beginVerify</code> method to allow the message encoded in the signature to be recovered.
 *
 * <p>For signing a message with message recovery functionality, the user must cast the
 * <code>Signature</code> object to this interface, initialize the object for signing with a
 * private key using the
 * <code>init()</code> method, and issue 0 or more
 * <code>update()</code> method
 * calls and then finally call the
 * <code>sign()</code> method to obtain the signature.
 *
 * <p>For recovering the encoded message and verifying functionality, the user must
 * cast the
 * <code>Signature</code> object to this interface, initialize the object for
 * verifying with a public key using the
 * <code>init()</code> method, first recover the
 * message using the
 * <code>beginVerify()</code> method and then issue 0 or more
 * <code>update()</code>
 * method calls and then finally call the
 * <code>verify()</code> method to verify the
 * signature.
 *
 * <p>Note:<br/>
 * A
 * <code>Signature</code> object implementing this interface must throw
 * <code>CryptoException</code> with
 * <code>CryptoException.ILLEGAL_USE</code> reason code when one of the
 * following methods applicable only to a
 * <code>Signature</code> object which does not
 * include message recovery functionality, is called:
 * <ul>
 * <li><em>init(Key, byte, byte[] short, short)</em>
 * <li><em>sign(byte[], short, short, byte[], short)</em>
 * <li><em>verify(byte[], short, short, byte[], short, short)</em>
 * </ul>
 *
 * @since 2.2.2
 */
public interface SignatureMessageRecovery {

    /**
     * Initializes the
     * <code>Signature</code> object with the appropriate
     * <code>Key</code>.
     * This method should be used for algorithms which do not need initialization parameters
     * or use default parameter values.
     * <p><code>init()</code> must be used to update the
     * <code>Signature</code> object with a new key.
     * If the
     * <code>Key</code> object is modified after invoking the
     * <code>init()</code> method,
     * the behavior of the
     * <code>update()</code>,
     * <code>sign()</code>, and
     * <code>verify()</code>
     * methods is unspecified.
     * <P>
     *
     * @param theKey the key object to use for signing or verifying
     * @param theMode one of MODE_SIGN or MODE_VERIFY
     * @throws CryptoException with the following reason codes: CryptoException.ILLEGAL_VALUE if theMode option is an undefined value or if the Key is inconsistent with theMode or with the Signature implementation. CryptoException.UNINITIALIZED_KEY if theKey instance is uninitialized.
     */
    void init(Key theKey, byte theMode) throws CryptoException;

    /**
     * This method begins the verification sequence by recovering the message
     * encoded within the signature itself and initializing the internal hash
     * function. The recovered message data overwrites the signature data in the
     * <code>sigAndRecDataBuff</code> input byte array.
     * <p>Notes:<ul>
     * <li><em>This method must be called during the verification sequence
     * prior to either the </em><code>update()</code><em> or the </em><code>verify()</code><em> methods during verification.</em>
     * <li><em> The trailing (</em><code>sigLength</code><em> - recovered message length) bytes of
     * signature data in </em><code>sigAndRecDataBuff</code><em> may also be overwritten
     * by this method.</em>
     * </ul>
     * <P>
     *
     * @param sigAndRecDataBuff contains the signature data as input and also contains the recoverable part of the message as output.
     * @param buffOffset offset into the sigAndRecDataBuff array where data begins for signature and where this method will start writing recovered message data.
     * @param sigLength the length of signature data
     * @return byte length of recovered message data written to sigAndRecDataBuff
     * @throws CryptoException with the following reason codes: CryptoException.ILLEGAL_USE for the following conditions: if this object is initialized for signature sign mode if sigLength is inconsistent with this Signature algorithm if the decrypted message representative does not meet the algorithm specifications if the bit length of the decrypted message representative is not a multiple of 8. CryptoException.UNINITIALIZED_KEY if key not initialized. CryptoException.INVALID_INIT if this Signature object is not initialized.
     */
    short beginVerify(byte[] sigAndRecDataBuff, short buffOffset, short sigLength) throws CryptoException;

    /**
     * Generates the signature of all/last input data. In addition, this method
     * returns the number of bytes beginning with the first byte of the message
     * that was encoded into the signature itself. The encoded message is called
     * the recoverable message and its length is called
     * the recoverable message length. This recoverable message
     * need not be transmitted and can be recovered during verification.
     * <p>A call to this method also resets this
     * <code>Signature</code> object to the state it was in
     * when previously initialized via a call to
     * <code>init()</code>.
     * That is, the object is reset and available to sign another message.
     * <p>The input and output buffer data may overlap.
     * <P>
     *
     * @param inBuff the input buffer of data to be signed
     * @param inOffset the offset into the input buffer at which to begin signature generation
     * @param inLength the byte length to sign
     * @param sigBuff the output buffer to store signature data
     * @param sigOffset the offset into sigBuff at which to begin signature data
     * @param recMsgLen the output buffer containing the number of bytes of the recoverable message beginning with the first byte of the message that was encoded into the signature itself
     * @param recMsgLenOffset offset into the recMsgLen output buffer where the byte length of the recoverable message is stored. Note that a single short value is stored at recMsgLenOffset offset.
     * @return number of bytes of signature output in sigBuff
     * @throws CryptoException with the following reason codes: CryptoException.UNINITIALIZED_KEY if key not initialized. CryptoException.INVALID_INIT if this Signature object is not initialized or initialized for signature verify mode.
     */
    short sign(byte[] inBuff, short inOffset, short inLength, byte[] sigBuff, short sigOffset, short[] recMsgLen, short recMsgLenOffset) throws CryptoException;

    /**
     * Verifies the signature of all/last input data against the passed in signature.
     * <p>A call to this method also resets this
     * <code>Signature</code> object to the state it was in
     * when previously initialized via a call to
     * <code>init()</code>.
     * That is, the object is reset and available to verify another message.
     * <P>
     *
     * @param inBuff the input buffer of data to be verified
     * @param inOffset the offset into the input buffer at which to begin signature generation
     * @param inLength the byte length to sign
     * @return true if the signature verifies, false otherwise
     * @throws CryptoException with the following reason codes: CryptoException.UNINITIALIZED_KEY if key not initialized. CryptoException.INVALID_INIT if this Signature object is not initialized or initialized for signature sign mode. CryptoException.ILLEGAL_USE if one of the following conditions is met: if beginVerify method has not been called.
     */
    boolean verify(byte[] inBuff, short inOffset, short inLength) throws CryptoException;

    /**
     * Gets the Signature algorithm.
     * <P>
     *
     *
     * @return the algorithm code implemented by this Signature instance.
     */
    byte getAlgorithm();

    /**
     * Returns the byte length of the signature data.
     * <P>
     *
     *
     * @return the byte length of the signature data
     * @throws CryptoException with the following reason codes: CryptoException.INVALID_INIT if this Signature object is not initialized. CryptoException.UNINITIALIZED_KEY if key not initialized.
     */
    short getLength() throws CryptoException;

    /**
     * Accumulates a signature of the input data. This method requires temporary storage of
     * intermediate results. In addition, if the input data length is not block aligned
     * (multiple of block size)
     * then additional internal storage may be allocated at this time to store a partial
     * input data block.
     * This may result in additional resource consumption and/or slow performance.
     * This method should only be used if all the input data required for signing/verifying
     * is not available in one byte array. If all of the input data required for
     * signing/verifying is located in a single byte array, use of the
     * <code>sign()</code>
     * or
     * <code>beginVerify</code> method and
     * <code>verify()</code> method is recommended.
     * The
     * <code>sign()</code> or
     * <code>verify()</code>
     * method must be called to complete processing of input data accumulated by one or more
     * calls to the
     * <code>update()</code> method.
     * <p>Note:<ul>
     * <li><em>If
     * <code>inLength</code> is 0 this method does nothing.</em>
     * </ul>
     * <P>
     *
     * @param inBuff the input buffer of data to be signed/verified
     * @param inOffset the offset into the input buffer where input data begins
     * @param inLength the byte length to sign/verify
     * @throws CryptoException with the following reason codes: CryptoException.UNINITIALIZED_KEY if key not initialized. CryptoException.INVALID_INIT if this Signature object is not initialized. CryptoException.ILLEGAL_USE if the mode set in the init() method is MODE_VERIFY and the beginVerify() method is not yet called.
     * @see #sign(byte[], short, short, byte[], short, short[], short)
     * @see #verify(byte[], short, short)
     */
    void update(byte[] inBuff, short inOffset, short inLength) throws CryptoException;
}
