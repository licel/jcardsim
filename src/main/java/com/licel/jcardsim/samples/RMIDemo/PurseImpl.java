/*
 * Copyright © 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.licel.jcardsim.samples.RMIDemo;

import javacard.framework.UserException;
import javacard.framework.Util;
import javacard.framework.service.CardRemoteObject;
import java.rmi.RemoteException;
/**
 *
 */
public class PurseImpl extends CardRemoteObject implements Purse {
    
    private short balance = 0;
    private byte[] number;
    
    
    public PurseImpl() {
        super();                     // export it
        number = new byte[5];
    }
    
    public void setAccountNumber(byte[] number) throws RemoteException, UserException {
        if(number.length != 5) UserException.throwIt(BAD_ARGUMENT);
        Util.arrayCopy(number, (short)0, this.number, (short)0, (short)5);
    }
    
    public void credit(short m) throws RemoteException, UserException {
        
        if(m<=0) UserException.throwIt(BAD_ARGUMENT);
        
        if((short)(balance+m) > MAX_AMOUNT) UserException.throwIt(OVERFLOW);
        
        balance +=m;

    }
    
    public void debit(short m) throws RemoteException, UserException {
        
        if(m<=0) UserException.throwIt(BAD_ARGUMENT);
        
        if((short)(balance-m) < 0) UserException.throwIt(UNDERFLOW);
        
        balance -=m;
    }
    
    public byte[] getAccountNumber() throws RemoteException {
        return number;
    }
    
    public short getBalance() throws RemoteException {
        return balance;
    }
    
}
