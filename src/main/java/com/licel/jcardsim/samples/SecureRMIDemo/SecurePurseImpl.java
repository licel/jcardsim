/*
 * Copyright © 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.licel.jcardsim.samples.SecureRMIDemo;

import javacard.framework.UserException;
import javacard.framework.Util;
import javacard.framework.service.CardRemoteObject;
import javacard.framework.service.SecurityService;
import java.rmi.RemoteException;
/**
 *
 */
public class SecurePurseImpl extends CardRemoteObject implements Purse {
    
    private short balance = 0;
    private byte[] number;
    
    private SecurityService security;
    
    
    public SecurePurseImpl(SecurityService security) {
        super();                     // export it
        this.security = security;
        number = new byte[5];
    }
    
    public void debit(short m) throws RemoteException, UserException {
        
        if( !security.isCommandSecure(SecurityService.PROPERTY_INPUT_INTEGRITY)){
            UserException.throwIt(CORRUPTED_DATA);
        }
            
        if(!security.isAuthenticated(SecurityService.PRINCIPAL_CARDHOLDER)) {
            UserException.throwIt(REQUEST_DENIED);
        }
        
        
        if(m<=0) UserException.throwIt(BAD_ARGUMENT);
        
        if((short)(balance-m) < 0) UserException.throwIt(UNDERFLOW);
        
        balance -=m;
    }
    
    
    public void credit(short m) throws RemoteException, UserException {
        
        if( !security.isCommandSecure(SecurityService.PROPERTY_INPUT_INTEGRITY)){
            UserException.throwIt(CORRUPTED_DATA);
        }
            
        if(!security.isAuthenticated(SecurityService.PRINCIPAL_APP_PROVIDER)) {
            UserException.throwIt(REQUEST_DENIED);
        }
        
        
        
        if(m<=0) UserException.throwIt(BAD_ARGUMENT);
        
        if((short)(balance+m) > MAX_AMOUNT) UserException.throwIt(OVERFLOW);
        
        balance +=m;
    }
    
    
    
    public short getBalance() throws RemoteException, UserException {
        
        if( !security.isCommandSecure(SecurityService.PROPERTY_INPUT_INTEGRITY)){
            UserException.throwIt(CORRUPTED_DATA);
        }
            
        if(!security.isAuthenticated(SecurityService.PRINCIPAL_APP_PROVIDER)) {
            UserException.throwIt(REQUEST_DENIED);
        }
        
        
        return balance;
    }
    
    public void setAccountNumber(byte[] number) throws RemoteException, UserException {
        
        if( !security.isCommandSecure(SecurityService.PROPERTY_INPUT_INTEGRITY)){
            UserException.throwIt(CORRUPTED_DATA);
        }
            
        if(!security.isAuthenticated(SecurityService.PRINCIPAL_APP_PROVIDER)) {
            UserException.throwIt(REQUEST_DENIED);
        }
        
        
        
        
        if(number.length != 5) UserException.throwIt(BAD_ARGUMENT);
        Util.arrayCopy(number, (short)0, this.number, (short)0, (short)5);
    }
    
    
    
    public byte[] getAccountNumber() throws RemoteException, UserException {
        
        if( !security.isCommandSecure(SecurityService.PROPERTY_INPUT_INTEGRITY)){
            UserException.throwIt(CORRUPTED_DATA);
        }
            
        if(!security.isAuthenticated(SecurityService.PRINCIPAL_CARDHOLDER)) {
            UserException.throwIt(REQUEST_DENIED);
        }
        
        
        return number;
    }
    
    
    
}
