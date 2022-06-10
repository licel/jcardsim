/*
 * Copyright 2022 Licel Corporation.
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
package com.licel.jcardsim.base;

import com.licel.jcardsim.samples.RuntimeExceptionApplet;
import com.licel.jcardsim.utils.AIDUtil;
import javacard.framework.*;
import javacard.framework.service.ServiceException;
import javacard.security.CryptoException;
import javacardx.biometry.BioException;
import javacardx.biometry1toN.Bio1toNException;
import javacardx.external.ExternalException;
import javacardx.framework.string.StringException;
import javacardx.framework.tlv.TLVException;
import javacardx.framework.util.UtilException;
import junit.framework.TestCase;

import javax.smartcardio.ResponseAPDU;

public class AppletProcessExceptionTest extends TestCase {
    private static final byte CLA_CRYPTO_EXCEPTION = 1;
    private final static byte CLA_APDU_EXCEPTION = 2;
    private final static byte CLA_SYSTEM_EXCEPTION = 3;
    private final static byte CLA_SERVICE_EXCEPTION = 4;
    private final static byte CLA_BIO_EXCEPTION = 5;
    private final static byte CLA_BIO_1_TO_N_EXCEPTION = 6;
    private final static byte CLA_EXTERNAL_EXCEPTION = 7;
    private final static byte CLA_PIN_EXCEPTION = 8;
    private final static byte CLA_STRING_EXCEPTION = 9;
    private final static byte CLA_TLV_EXCEPTION = 10;
    private final static byte CLA_TRANSACTION_EXCEPTION = 11;
    private final static byte CLA_UTIL_EXCEPTION = 12;
    private static final byte INS_JUST_THROW = 0;
    private static final byte INS_HAS_CATCH_EXCEPTION = 1;

    private final static String appletAIDStr = "010203040506070809";

    public AppletProcessExceptionTest(String name) {super(name);}

    public void testCryptoException(){
        Simulator instance = getReadySimulator();

        // Test CryptoException.ILLEGAL_VALUE without try catch
        byte[] apdu = new byte[]{CLA_CRYPTO_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, CryptoException.ILLEGAL_VALUE);
        byte[] result = instance.transmitCommand(apdu);
        ResponseAPDU responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test CryptoException.ILLEGAL_VALUE with try catch
        apdu = new byte[]{CLA_CRYPTO_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, CryptoException.ILLEGAL_VALUE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test CryptoException.UNINITIALIZED_KEY without try catch
        apdu = new byte[]{CLA_CRYPTO_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, CryptoException.UNINITIALIZED_KEY);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test CryptoException.UNINITIALIZED_KEY with try catch
        apdu = new byte[]{CLA_CRYPTO_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, CryptoException.UNINITIALIZED_KEY);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test CryptoException.NO_SUCH_ALGORITHM without try catch
        apdu = new byte[]{CLA_CRYPTO_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, CryptoException.NO_SUCH_ALGORITHM);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test CryptoException.NO_SUCH_ALGORITHM with try catch
        apdu = new byte[]{CLA_CRYPTO_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, CryptoException.NO_SUCH_ALGORITHM);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test CryptoException.INVALID_INIT without try catch
        apdu = new byte[]{CLA_CRYPTO_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, CryptoException.INVALID_INIT);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test CryptoException.INVALID_INIT with try catch
        apdu = new byte[]{CLA_CRYPTO_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, CryptoException.INVALID_INIT);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test CryptoException.ILLEGAL_USE without try catch
        apdu = new byte[]{CLA_CRYPTO_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, CryptoException.ILLEGAL_USE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test CryptoException.ILLEGAL_USE with try catch
        apdu = new byte[]{CLA_CRYPTO_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, CryptoException.ILLEGAL_USE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());
    }

    public void testAPDUException(){
        Simulator instance = getReadySimulator();

        // Test APDUException.ILLEGAL_USE without try catch
        byte[] apdu = new byte[]{CLA_APDU_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, APDUException.ILLEGAL_USE);
        byte[] result = instance.transmitCommand(apdu);
        ResponseAPDU responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test APDUException.ILLEGAL_USE with try catch
        apdu = new byte[]{CLA_APDU_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, APDUException.ILLEGAL_USE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test APDUException.BUFFER_BOUNDS without try catch
        apdu = new byte[]{CLA_APDU_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, APDUException.BUFFER_BOUNDS);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test APDUException.BUFFER_BOUNDS with try catch
        apdu = new byte[]{CLA_APDU_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, APDUException.BUFFER_BOUNDS);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test APDUException.BAD_LENGTH without try catch
        apdu = new byte[]{CLA_APDU_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, APDUException.BAD_LENGTH);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test APDUException.BAD_LENGTH with try catch
        apdu = new byte[]{CLA_APDU_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, APDUException.BAD_LENGTH);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test APDUException.IO_ERROR without try catch
        apdu = new byte[]{CLA_APDU_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, APDUException.IO_ERROR);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test APDUException.IO_ERROR with try catch
        apdu = new byte[]{CLA_APDU_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, APDUException.IO_ERROR);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test APDUException.NO_T0_GETRESPONSE without try catch
        apdu = new byte[]{CLA_APDU_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, APDUException.NO_T0_GETRESPONSE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test APDUException.NO_T0_GETRESPONSE with try catch
        apdu = new byte[]{CLA_APDU_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, APDUException.NO_T0_GETRESPONSE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test APDUException.T1_IFD_ABORT without try catch
        apdu = new byte[]{CLA_APDU_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, APDUException.T1_IFD_ABORT);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test APDUException.T1_IFD_ABORT with try catch
        apdu = new byte[]{CLA_APDU_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, APDUException.T1_IFD_ABORT);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test APDUException.NO_T0_REISSUE without try catch
        apdu = new byte[]{CLA_APDU_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, APDUException.NO_T0_REISSUE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test APDUException.NO_T0_REISSUE with try catch
        apdu = new byte[]{CLA_APDU_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, APDUException.NO_T0_REISSUE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());
    }

    public void testSystemException(){
        Simulator instance = getReadySimulator();

        // Test SystemException.ILLEGAL_VALUE without try catch
        byte[] apdu = new byte[]{CLA_SYSTEM_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, SystemException.ILLEGAL_VALUE);
        byte[] result = instance.transmitCommand(apdu);
        ResponseAPDU responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test SystemException.ILLEGAL_VALUE with try catch
        apdu = new byte[]{CLA_SYSTEM_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, SystemException.ILLEGAL_VALUE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test SystemException.NO_TRANSIENT_SPACE without try catch
        apdu = new byte[]{CLA_SYSTEM_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, SystemException.NO_TRANSIENT_SPACE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test SystemException.NO_TRANSIENT_SPACE with try catch
        apdu = new byte[]{CLA_SYSTEM_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, SystemException.NO_TRANSIENT_SPACE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test SystemException.ILLEGAL_TRANSIENT without try catch
        apdu = new byte[]{CLA_SYSTEM_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, SystemException.ILLEGAL_TRANSIENT);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test SystemException.ILLEGAL_TRANSIENT with try catch
        apdu = new byte[]{CLA_SYSTEM_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, SystemException.ILLEGAL_TRANSIENT);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test SystemException.ILLEGAL_AID without try catch
        apdu = new byte[]{CLA_SYSTEM_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, SystemException.ILLEGAL_AID);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test SystemException.ILLEGAL_AID with try catch
        apdu = new byte[]{CLA_SYSTEM_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, SystemException.ILLEGAL_AID);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test SystemException.NO_RESOURCE without try catch
        apdu = new byte[]{CLA_SYSTEM_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, SystemException.NO_RESOURCE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test SystemException.NO_RESOURCE with try catch
        apdu = new byte[]{CLA_SYSTEM_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, SystemException.NO_RESOURCE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test SystemException.ILLEGAL_USE without try catch
        apdu = new byte[]{CLA_SYSTEM_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, SystemException.ILLEGAL_USE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test SystemException.ILLEGAL_USE with try catch
        apdu = new byte[]{CLA_SYSTEM_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, SystemException.ILLEGAL_USE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());
    }

    public void testServiceException(){
        Simulator instance = getReadySimulator();

        // Test ServiceException.ILLEGAL_PARAM without try catch
        byte[] apdu = new byte[]{CLA_SERVICE_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, ServiceException.ILLEGAL_PARAM);
        byte[] result = instance.transmitCommand(apdu);
        ResponseAPDU responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test ServiceException.ILLEGAL_PARAM without try catch
        apdu = new byte[]{CLA_SERVICE_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, ServiceException.ILLEGAL_PARAM);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test ServiceException.DISPATCH_TABLE_FULL without try catch
        apdu = new byte[]{CLA_SERVICE_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, ServiceException.DISPATCH_TABLE_FULL);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test ServiceException.DISPATCH_TABLE_FULL with try catch
        apdu = new byte[]{CLA_SERVICE_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, ServiceException.DISPATCH_TABLE_FULL);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test ServiceException.COMMAND_DATA_TOO_LONG without try catch
        apdu = new byte[]{CLA_SERVICE_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, ServiceException.COMMAND_DATA_TOO_LONG);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test ServiceException.COMMAND_DATA_TOO_LONG with try catch
        apdu = new byte[]{CLA_SERVICE_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, ServiceException.COMMAND_DATA_TOO_LONG);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test ServiceException.CANNOT_ACCESS_IN_COMMAND without try catch
        apdu = new byte[]{CLA_SERVICE_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, ServiceException.CANNOT_ACCESS_IN_COMMAND);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test ServiceException.CANNOT_ACCESS_IN_COMMAND with try catch
        apdu = new byte[]{CLA_SERVICE_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, ServiceException.CANNOT_ACCESS_IN_COMMAND);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test ServiceException.CANNOT_ACCESS_OUT_COMMAND without try catch
        apdu = new byte[]{CLA_SERVICE_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, ServiceException.CANNOT_ACCESS_OUT_COMMAND);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test ServiceException.CANNOT_ACCESS_OUT_COMMAND with try catch
        apdu = new byte[]{CLA_SERVICE_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, ServiceException.CANNOT_ACCESS_OUT_COMMAND);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test ServiceException.COMMAND_IS_FINISHED without try catch
        apdu = new byte[]{CLA_SERVICE_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, ServiceException.COMMAND_IS_FINISHED);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test ServiceException.COMMAND_IS_FINISHED with try catch
        apdu = new byte[]{CLA_SERVICE_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, ServiceException.COMMAND_IS_FINISHED);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test ServiceException.REMOTE_OBJECT_NOT_EXPORTED without try catch
        apdu = new byte[]{CLA_SERVICE_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, ServiceException.REMOTE_OBJECT_NOT_EXPORTED);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test ServiceException.REMOTE_OBJECT_NOT_EXPORTED with try catch
        apdu = new byte[]{CLA_SERVICE_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu,ISO7816.OFFSET_P1, ServiceException.REMOTE_OBJECT_NOT_EXPORTED);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());
    }

    public void testBioException() {
        Simulator instance = getReadySimulator();

        // Test BioException.ILLEGAL_VALUE without try catch
        byte[] apdu = new byte[]{CLA_BIO_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, BioException.ILLEGAL_VALUE);
        byte[] result = instance.transmitCommand(apdu);
        ResponseAPDU responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test BioException.ILLEGAL_VALUE without try catch
        apdu = new byte[]{CLA_BIO_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, BioException.ILLEGAL_VALUE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test BioException.INVALID_DATA without try catch
        apdu = new byte[]{CLA_BIO_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, BioException.INVALID_DATA);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test BioException.INVALID_DATA with try catch
        apdu = new byte[]{CLA_BIO_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, BioException.INVALID_DATA);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test BioException.NO_SUCH_BIO_TEMPLATE without try catch
        apdu = new byte[]{CLA_BIO_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, BioException.NO_SUCH_BIO_TEMPLATE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test BioException.NO_SUCH_BIO_TEMPLATE with try catch
        apdu = new byte[]{CLA_BIO_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, BioException.NO_SUCH_BIO_TEMPLATE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test BioException.NO_TEMPLATES_ENROLLED without try catch
        apdu = new byte[]{CLA_BIO_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, BioException.NO_TEMPLATES_ENROLLED);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test BioException.NO_TEMPLATES_ENROLLED with try catch
        apdu = new byte[]{CLA_BIO_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, BioException.NO_TEMPLATES_ENROLLED);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test BioException.ILLEGAL_USE without try catch
        apdu = new byte[]{CLA_BIO_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, BioException.ILLEGAL_USE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test BioException.ILLEGAL_USE with try catch
        apdu = new byte[]{CLA_BIO_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, BioException.ILLEGAL_USE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());
    }

    public void testBio1toNException() {
        Simulator instance = getReadySimulator();

        // Test Bio1toNException.ILLEGAL_VALUE without try catch
        byte[] apdu = new byte[]{CLA_BIO_1_TO_N_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, Bio1toNException.ILLEGAL_VALUE);
        byte[] result = instance.transmitCommand(apdu);
        ResponseAPDU responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test Bio1toNException.ILLEGAL_VALUE with try catch
        apdu = new byte[]{CLA_BIO_1_TO_N_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, Bio1toNException.ILLEGAL_VALUE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test Bio1toNException.INVALID_DATA without try catch
        apdu = new byte[]{CLA_BIO_1_TO_N_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, Bio1toNException.INVALID_DATA);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test Bio1toNException.INVALID_DATA with try catch
        apdu = new byte[]{CLA_BIO_1_TO_N_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, Bio1toNException.INVALID_DATA);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test Bio1toNException.UNSUPPORTED_BIO_TYPE without try catch
        apdu = new byte[]{CLA_BIO_1_TO_N_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, Bio1toNException.UNSUPPORTED_BIO_TYPE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test Bio1toNException.UNSUPPORTED_BIO_TYPE with try catch
        apdu = new byte[]{CLA_BIO_1_TO_N_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, Bio1toNException.UNSUPPORTED_BIO_TYPE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test Bio1toNException.NO_BIO_TEMPLATE_ENROLLED without try catch
        apdu = new byte[]{CLA_BIO_1_TO_N_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, Bio1toNException.NO_BIO_TEMPLATE_ENROLLED);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test Bio1toNException.NO_BIO_TEMPLATE_ENROLLED with try catch
        apdu = new byte[]{CLA_BIO_1_TO_N_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, Bio1toNException.NO_BIO_TEMPLATE_ENROLLED);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test Bio1toNException.ILLEGAL_USE without try catch
        apdu = new byte[]{CLA_BIO_1_TO_N_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, Bio1toNException.ILLEGAL_USE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test Bio1toNException.ILLEGAL_USE with try catch
        apdu = new byte[]{CLA_BIO_1_TO_N_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, Bio1toNException.ILLEGAL_USE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test Bio1toNException.BIO_TEMPLATE_DATA_CAPACITY_EXCEEDED without try catch
        apdu = new byte[]{CLA_BIO_1_TO_N_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, Bio1toNException.BIO_TEMPLATE_DATA_CAPACITY_EXCEEDED);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test Bio1toNException.BIO_TEMPLATE_DATA_CAPACITY_EXCEEDED with try catch
        apdu = new byte[]{CLA_BIO_1_TO_N_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, Bio1toNException.BIO_TEMPLATE_DATA_CAPACITY_EXCEEDED);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test Bio1toNException.MISMATCHED_BIO_TYPE without try catch
        apdu = new byte[]{CLA_BIO_1_TO_N_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, Bio1toNException.MISMATCHED_BIO_TYPE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test Bio1toNException.MISMATCHED_BIO_TYPE with try catch
        apdu = new byte[]{CLA_BIO_1_TO_N_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, Bio1toNException.MISMATCHED_BIO_TYPE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());
    }

    public void testExternalException() {
        Simulator instance = getReadySimulator();

        // Test ExternalException.NO_SUCH_SUBSYSTEM without try catch
        byte[] apdu = new byte[]{CLA_EXTERNAL_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, ExternalException.NO_SUCH_SUBSYSTEM);
        byte[] result = instance.transmitCommand(apdu);
        ResponseAPDU responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test ExternalException.NO_SUCH_SUBSYSTEM with try catch
        apdu = new byte[]{CLA_EXTERNAL_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, ExternalException.NO_SUCH_SUBSYSTEM);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test ExternalException.INVALID_PARAM without try catch
        apdu = new byte[]{CLA_EXTERNAL_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, ExternalException.INVALID_PARAM);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test ExternalException.INVALID_PARAM with try catch
        apdu = new byte[]{CLA_EXTERNAL_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, ExternalException.INVALID_PARAM);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test ExternalException.INTERNAL_ERROR without try catch
        apdu = new byte[]{CLA_EXTERNAL_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, ExternalException.INTERNAL_ERROR);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test ExternalException.INTERNAL_ERROR with try catch
        apdu = new byte[]{CLA_EXTERNAL_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, ExternalException.INTERNAL_ERROR);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());
    }

    public void testPINException() {
        Simulator instance = getReadySimulator();

        // Test PINException.ILLEGAL_VALUE without try catch
        byte[] apdu = new byte[]{CLA_PIN_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, PINException.ILLEGAL_VALUE);
        byte[] result = instance.transmitCommand(apdu);
        ResponseAPDU responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test PINException.ILLEGAL_VALUE with try catch
        apdu = new byte[]{CLA_PIN_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, PINException.ILLEGAL_VALUE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test PINException.ILLEGAL_STATE without try catch
        apdu = new byte[]{CLA_PIN_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, PINException.ILLEGAL_STATE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test PINException.ILLEGAL_STATE with try catch
        apdu = new byte[]{CLA_PIN_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, PINException.ILLEGAL_STATE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());
    }

    public void testStringException() {
        Simulator instance = getReadySimulator();

        // Test StringException.UNSUPPORTED_ENCODING without try catch
        byte[] apdu = new byte[]{CLA_STRING_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, StringException.UNSUPPORTED_ENCODING);
        byte[] result = instance.transmitCommand(apdu);
        ResponseAPDU responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test StringException.UNSUPPORTED_ENCODING with try catch
        apdu = new byte[]{CLA_STRING_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, StringException.UNSUPPORTED_ENCODING);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test StringException.ILLEGAL_NUMBER_FORMAT without try catch
        apdu = new byte[]{CLA_STRING_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, StringException.ILLEGAL_NUMBER_FORMAT);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test StringException.ILLEGAL_NUMBER_FORMAT with try catch
        apdu = new byte[]{CLA_STRING_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, StringException.ILLEGAL_NUMBER_FORMAT);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test StringException.INVALID_BYTE_SEQUENCE without try catch
        apdu = new byte[]{CLA_STRING_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, StringException.INVALID_BYTE_SEQUENCE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test StringException.INVALID_BYTE_SEQUENCE with try catch
        apdu = new byte[]{CLA_STRING_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, StringException.INVALID_BYTE_SEQUENCE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());
    }

    public void testTLVException() {
        Simulator instance = getReadySimulator();

        // Test TLVException.INVALID_PARAM without try catch
        byte[] apdu = new byte[]{CLA_TLV_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, TLVException.INVALID_PARAM);
        byte[] result = instance.transmitCommand(apdu);
        ResponseAPDU responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test TLVException.INVALID_PARAM with try catch
        apdu = new byte[]{CLA_TLV_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, TLVException.INVALID_PARAM);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test TLVException.ILLEGAL_SIZE without try catch
        apdu = new byte[]{CLA_TLV_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, TLVException.ILLEGAL_SIZE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test TLVException.ILLEGAL_SIZE with try catch
        apdu = new byte[]{CLA_TLV_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, TLVException.ILLEGAL_SIZE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test TLVException.EMPTY_TAG without try catch
        apdu = new byte[]{CLA_TLV_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, TLVException.EMPTY_TAG);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test TLVException.EMPTY_TAG with try catch
        apdu = new byte[]{CLA_TLV_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, TLVException.EMPTY_TAG);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test TLVException.EMPTY_TLV without try catch
        apdu = new byte[]{CLA_TLV_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, TLVException.EMPTY_TLV);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test TLVException.EMPTY_TLV with try catch
        apdu = new byte[]{CLA_TLV_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, TLVException.EMPTY_TLV);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test TLVException.MALFORMED_TAG without try catch
        apdu = new byte[]{CLA_TLV_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, TLVException.MALFORMED_TAG);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test TLVException.MALFORMED_TAG with try catch
        apdu = new byte[]{CLA_TLV_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, TLVException.MALFORMED_TAG);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test TLVException.MALFORMED_TLV without try catch
        apdu = new byte[]{CLA_TLV_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, TLVException.MALFORMED_TLV);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test TLVException.INSUFFICIENT_STORAGE with try catch
        apdu = new byte[]{CLA_TLV_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, TLVException.INSUFFICIENT_STORAGE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test TLVException.INSUFFICIENT_STORAGE with try catch
        apdu = new byte[]{CLA_TLV_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, TLVException.INSUFFICIENT_STORAGE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test TLVException.TAG_SIZE_GREATER_THAN_127 with try catch
        apdu = new byte[]{CLA_TLV_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, TLVException.TAG_SIZE_GREATER_THAN_127);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test TLVException.TAG_SIZE_GREATER_THAN_127 with try catch
        apdu = new byte[]{CLA_TLV_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, TLVException.TAG_SIZE_GREATER_THAN_127);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test TLVException.TAG_NUMBER_GREATER_THAN_32767 with try catch
        apdu = new byte[]{CLA_TLV_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, TLVException.TAG_NUMBER_GREATER_THAN_32767);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test TLVException.TAG_NUMBER_GREATER_THAN_32767 with try catch
        apdu = new byte[]{CLA_TLV_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, TLVException.TAG_NUMBER_GREATER_THAN_32767);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test TLVException.TLV_SIZE_GREATER_THAN_32767 with try catch
        apdu = new byte[]{CLA_TLV_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, TLVException.TLV_SIZE_GREATER_THAN_32767);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test TLVException.TLV_SIZE_GREATER_THAN_32767 with try catch
        apdu = new byte[]{CLA_TLV_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, TLVException.TLV_SIZE_GREATER_THAN_32767);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test TLVException.TLV_LENGTH_GREATER_THAN_32767 with try catch
        apdu = new byte[]{CLA_TLV_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, TLVException.TLV_LENGTH_GREATER_THAN_32767);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test TLVException.TLV_LENGTH_GREATER_THAN_32767 with try catch
        apdu = new byte[]{CLA_TLV_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, TLVException.TLV_LENGTH_GREATER_THAN_32767);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());
    }

    public void testTransactionException() {
        Simulator instance = getReadySimulator();

        // Test TransactionException.IN_PROGRESS without try catch
        byte[] apdu = new byte[]{CLA_TRANSACTION_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, TransactionException.IN_PROGRESS);
        byte[] result = instance.transmitCommand(apdu);
        ResponseAPDU responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test TransactionException.IN_PROGRESS with try catch
        apdu = new byte[]{CLA_TRANSACTION_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, TransactionException.IN_PROGRESS);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test TransactionException.NOT_IN_PROGRESS without try catch
        apdu = new byte[]{CLA_TRANSACTION_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, TransactionException.NOT_IN_PROGRESS);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test TransactionException.NOT_IN_PROGRESS with try catch
        apdu = new byte[]{CLA_TRANSACTION_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, TransactionException.NOT_IN_PROGRESS);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test TransactionException.BUFFER_FULL without try catch
        apdu = new byte[]{CLA_TRANSACTION_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, TransactionException.BUFFER_FULL);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test TransactionException.BUFFER_FULL with try catch
        apdu = new byte[]{CLA_TRANSACTION_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, TransactionException.BUFFER_FULL);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test TransactionException.INTERNAL_FAILURE without try catch
        apdu = new byte[]{CLA_TRANSACTION_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, TransactionException.INTERNAL_FAILURE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test TransactionException.INTERNAL_FAILURE with try catch
        apdu = new byte[]{CLA_TRANSACTION_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, TransactionException.INTERNAL_FAILURE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test TransactionException.ILLEGAL_USE without try catch
        apdu = new byte[]{CLA_TRANSACTION_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, TransactionException.ILLEGAL_USE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test TransactionException.ILLEGAL_USE with try catch
        apdu = new byte[]{CLA_TRANSACTION_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, TransactionException.ILLEGAL_USE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());
    }

    public void testUtilException() {
        Simulator instance = getReadySimulator();

        // Test UtilException.ILLEGAL_VALUE without try catch
        byte[] apdu = new byte[]{CLA_UTIL_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, UtilException.ILLEGAL_VALUE);
        byte[] result = instance.transmitCommand(apdu);
        ResponseAPDU responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test UtilException.ILLEGAL_VALUE with try catch
        apdu = new byte[]{CLA_UTIL_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, UtilException.ILLEGAL_VALUE);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());

        // Test UtilException.TYPE_MISMATCHED without try catch
        apdu = new byte[]{CLA_UTIL_EXCEPTION, INS_JUST_THROW, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, UtilException.TYPE_MISMATCHED);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_UNKNOWN, responseApdu.getSW());

        // Test UtilException.TYPE_MISMATCHED with try catch
        apdu = new byte[]{CLA_UTIL_EXCEPTION, INS_HAS_CATCH_EXCEPTION, 0, 0};
        Util.setShort(apdu, ISO7816.OFFSET_P1, UtilException.TYPE_MISMATCHED);
        result = instance.transmitCommand(apdu);
        responseApdu = new ResponseAPDU(result);
        assertEquals(ISO7816.SW_FUNC_NOT_SUPPORTED, responseApdu.getSW());
    }
    private Simulator getReadySimulator() {
        Simulator instance = new Simulator();
        AID appletAID = AIDUtil.create(appletAIDStr);

        instance.installApplet(appletAID, RuntimeExceptionApplet.class);
        instance.selectApplet(appletAID);
        return instance;
    }
}
