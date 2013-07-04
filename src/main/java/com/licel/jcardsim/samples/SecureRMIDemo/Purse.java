/*
 * Copyright © 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.licel.jcardsim.samples.SecureRMIDemo;

import java.rmi.*;
import javacard.framework.*;

public interface Purse extends Remote{

    public static final short UNDERFLOW = (short)0x6000;
    public static final short OVERFLOW  = (short)0x6001;

    public static final short BAD_ARGUMENT  = (short)0x6002;

    public static final short REQUEST_DENIED  = (short)0x6003;
    
    public static final short CORRUPTED_DATA  = (short)0x6004;
    
    public static final short MAX_AMOUNT = (short)400; // for whatever reason
    
    // note: every method may throw RemoteException(RMI requirement) 
    //  and UserException (if the request is denied)
    
    public short getBalance() throws RemoteException, UserException;
    public void debit(short m) throws RemoteException, UserException;
    public void credit(short m) throws RemoteException, UserException;

    public void setAccountNumber(byte[] number) throws RemoteException, UserException;
    public byte[] getAccountNumber() throws RemoteException, UserException;
}
