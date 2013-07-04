/*
 * Copyright © 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)MySecurityService.java	1.7 03/06/06
 */

package com.licel.jcardsim.samples.SecureRMIDemo;

import javacard.framework.*;
import javacard.framework.service.*;


public class MySecurityService extends BasicService implements SecurityService {
    
    private static final byte[] PRINCIPAL_APP_PROVIDER_ID = {0x12, 0x34};
    private static final byte[] PRINCIPAL_CARDHOLDER_ID = {0x43, 0x21};
    
    private OwnerPIN provider_pin, cardholder_pin = null;
    
    public MySecurityService() {
        provider_pin = new OwnerPIN((byte)2,(byte)2);
        cardholder_pin = new OwnerPIN((byte)2,(byte)2);
        provider_pin.update(PRINCIPAL_APP_PROVIDER_ID, (short)0, (byte)2);
        cardholder_pin.update(PRINCIPAL_CARDHOLDER_ID, (short)0, (byte)2);
    }
    
    /** Pre-processes the input data for the command in the <CODE>APDU</CODE> object.
     * When invoked, the APDU object
     * should either be in <CODE>STATE_INITIAL</CODE> with the APDU buffer in the Init format
     * or in <CODE>STATE_FULL_INCOMING</CODE> with the APDU buffer in the Input Ready format
     * defined in <CODE>BasicService</CODE>.
     * <p>The method must return <CODE>true</CODE> if no more
     * pre-processing should be performed, and <CODE>false</CODE> otherwise.
     * In particular, it must return <CODE>false</CODE> if it has not performed any
     * processing on the command.
     * <P>
     * After normal completion, the <CODE>APDU</CODE> object is usually in <CODE>STATE_FULL_INCOMING</CODE>
     * with the APDU buffer in the Input Ready format defined in <CODE>BasicService</CODE>.
     * However, in some cases if the Service processes the command entirely,
     * the <CODE>APDU</CODE> object may be in <CODE>STATE_OUTGOING</CODE>
     * with the APDU buffer in the Output Ready format defined in <CODE>BasicService</CODE>.
     * @param apdu the <CODE>APDU</CODE> object containing the command being processed.
     * @return <code>true</code> if input processing is finished, <CODE>false</CODE> otherwise.
     */
    public boolean processDataIn(APDU apdu) {
        
        
        if(selectingApplet()) {
            commandProperties = 0;
            authenticated = 0;
            return false;   // in case some other service is interested
        }
        else {
            return preprocessCommandAPDU(apdu);
        }
    }
    
    /**
     * Checks whether a secure channel is in use between the card and the host for
     * the ongoing command that guarantees the indicated properties. The result is only
     * correct after pre-processing the command (for instance during the processing of
     * the command). For properties on incoming data, the result is guaranteed to be
     * correct; for outgoing data, the result reflects the expectations of the client
     * software, with no other guarantee.
     * @param properties the required properties.
     * @return true if the required properties are <CODE>true</CODE>, <CODE>false</CODE> othewise
     * @throws ServiceException with the following reason code:<ul>
     * <li><code>ServiceException.ILLEGAL_PARAM</code> if the specified
     * property is unknown.
     * </ul>
     */
    public boolean isCommandSecure(byte properties) throws ServiceException {
        return (commandProperties & properties) != 0;
    }
    
    private byte commandProperties;
    
    
    /**
     * Checks whether or not the specified principal is currently authenticated.
     * The validity timeframe(selection or reset) and authentication method as well
     * as the exact interpretation of the specified principal parameter needs to be
     * detailed by the implementation class.
     * The only generic guarantee is that the authentication has been performed in the
     * current card session.
     * @param principal an identifier of the principal that needs to be authenticated
     * @return true if the expected principal is authenticated
     * @throws ServiceException with the following reason code:<ul>
     * <li><code>ServiceException.ILLEGAL_PARAM</code> if the specified
     * principal is unknown.
     * </ul>
     */
    public boolean isAuthenticated(short principal) throws ServiceException {
        
        return (authenticated == principal);
    }
    
    private byte authenticated;
    
    private boolean preprocessCommandAPDU(APDU apdu) {
        
        receiveInData(apdu);
        
        if(checkAndRemoveChecksum(apdu)) {
            commandProperties |= SecurityService.PROPERTY_INPUT_INTEGRITY;
        }
        else {
            commandProperties &= ~SecurityService.PROPERTY_INPUT_INTEGRITY;
        }
        
        return false;
    }
    
    private boolean checkAndRemoveChecksum(APDU apdu) {
        byte[] buffer = apdu.getBuffer();
        short Lc = buffer[4];
        
        if(Lc<2) return false;  // must have at least the checksum
        
        short csum1 = 0;
        buffer[4] -= 2;       // decrease Lc
        Lc = buffer[4];
        
        for(short n = 5; n<(short)(Lc+5); ++n) {
            csum1 += buffer[n];
        }
        
        final short csum2 = Util.getShort(buffer, (short)(Lc+5));
        
        
        return (csum1 == csum2);
    }
    
    
    public boolean processCommand(APDU apdu) {
        if(isAuthenticate(apdu)) {
            receiveInData(apdu);
            if(apdu.getBuffer()[4] == 2) {
                authenticated = 0;
                
                //                short id = Util.getShort(apdu.getBuffer(), (short)5);
                
                if(provider_pin.check(apdu.getBuffer(), (short)5, (byte)2)) {
                    authenticated = PRINCIPAL_APP_PROVIDER;
                    setOutputLength(apdu,(short)0);
                    succeed(apdu);
                }
                else if(cardholder_pin.check(apdu.getBuffer(), (short)5, (byte)2)) {
                    authenticated = PRINCIPAL_CARDHOLDER;
                    setOutputLength(apdu,(short)0);
                    succeed(apdu);
                }
                else {
                    fail(apdu, (short)0x6666);
                }
            }
            else {
                fail(apdu, (short) 0x6565);
            }
            return true;
        }
        else {
            return false;
        }
    }
    
    public boolean processDataOut(APDU apdu) {
        
        if(selectingApplet()) return false;
        
        // if not select...
        // compute and append checksum
        byte[] buffer = apdu.getBuffer();
        short Le = (short)(buffer[4] & 0x00FF);
        
        short csum = 0;
        
        for(short n = 5; n<(short)(Le+5); ++n) {
            csum += buffer[n];
        }
        
        javacard.framework.Util.setShort(buffer, (short)(Le+5), csum);
        buffer[4] += 2;
        return false;
        
        
    }
    
    public boolean isChannelSecure(byte prop) {
        return false;
    }
    
    private static final byte INS_SELECT   = (byte)0xA4;
    private static final byte APDU_CMD_MASK = (byte)0xFC;
    
    private static final byte CLA_AUTH = (byte)0x80;
    private static final byte INS_AUTH = (byte)0x39;
    
    private boolean isAuthenticate(APDU command) {
        return
        (getCLA(command) == CLA_AUTH
        &&
        getINS(command) == INS_AUTH);
        
    }
    
    
    
}
