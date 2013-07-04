/*
 * Copyright © 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.licel.jcardsim.samples.ChannelsDemo;

import javacard.framework.*;

/**
 * This applet keeps track of the account information for a
 * fictional wireless device connecting to a network service.  
 * The device has a home area, but it is also capable of operating
 * in remote areas at a higher rate.  Using the device debuts the
 * amount of available credits. The terminal can also add credits
 * to the account via a specific command.
 */

public class AccountAccessor extends Applet 
    implements MultiSelectable {

    // code of CLA byte in the command APDU header
    final static byte AA_CLA            = (byte)0x80;

    // codes of INS byte in the command APDU header
    final static byte GET_BALANCE       = (byte) 0x10;
    final static byte CREDIT            = (byte) 0x20;

    final static short MAX_BALANCE      = (short) 0x7FFF;
    final static short SW_MAX_BALANCE_EXCEEDED  = (short) 0x6A54;
    final static short SW_INVALID_TRANSACTION_AMOUNT  = (short) 0x6A55;
    
    // Account status constants
    final static byte AREA_HOME         = (byte) 0;
    final static byte AREA_REMOTE       = (byte) 1;
    
    //ConnectionManager AID
    final static byte[] CONNECTION_MGR_AID_BYTES =
              { (byte)0xA0, 0, 0, 0, (byte)0x62, 0x03, 0x01, 0x0C, 0x0B, 0x2 };
              
    //error return status
    final static short SW_NO_CONNECTION = (short)0x6905;

    // Connection data
    private short[]   chargeRate;
    private short     balance;
    private short     homeArea;

    static AccountAccessor theAccount = null;

    private AccountAccessor (byte[] bArray, short bOffset, byte bLength) {
        chargeRate = new short[2];
        theAccount = this;
        
        // Set up initial account information
        balance = 0;
        homeArea = (short)((short)(bArray[bOffset++] << (short)8) | 
            (short)(bArray[bOffset++] & 0x00FF));
            
        chargeRate[AREA_HOME] = (short)((short)(bArray[bOffset++] << 
            (short)8) | (short)(bArray[bOffset++] & (short)0x00FF));

        chargeRate[AREA_REMOTE] = (short)((short)(bArray[bOffset++] << 
            (short)8) | (short)(bArray[bOffset++] & (short)0x00FF));
        
        register();
    }

    public static void install(byte[] bArray, short bOffset, byte bLength){
        new AccountAccessor(bArray, bOffset, bLength);
    }

    public boolean select() {
        return true;
    }

    public void deselect() {
        // Nothing to do
    }

    public boolean select(boolean appInstAlreadySelected) {
        return true;
    }

    public void deselect(boolean appInstStillSelected) {
        // Nothing to do
    }


    public void process(APDU apdu) {
    
        byte[] buffer = apdu.getBuffer();
        buffer[ISO7816.OFFSET_CLA] = (byte)(buffer[ISO7816.OFFSET_CLA] 
            & (byte)0xFC);

        if ((buffer[ISO7816.OFFSET_CLA] == 0) &&
            (buffer[ISO7816.OFFSET_INS] == (byte)(0xA4)) )
            return;
            
        if (buffer[ISO7816.OFFSET_CLA] != AA_CLA)
            ISOException.throwIt (ISO7816.SW_CLA_NOT_SUPPORTED);
            
        switch (buffer[ISO7816.OFFSET_INS]) {
         case GET_BALANCE:   
            getBalance(apdu);
            return;
         case CREDIT:        
            credit(apdu);
            return;
         default:       
            ISOException.throwIt (ISO7816.SW_INS_NOT_SUPPORTED);
        }
    }
    
    boolean debit(short areaCode) {
        short amtToDebit;
        if (areaCode == homeArea) {
            amtToDebit = chargeRate[AREA_HOME];
        } else {
            amtToDebit = chargeRate[AREA_REMOTE];
        }
        
        if (balance >= amtToDebit) {
            JCSystem.beginTransaction();
            balance = (short)(balance - amtToDebit);
            JCSystem.commitTransaction();
            return true;
        } else {
            return false;
        }
    }
    
    public static AccountAccessor getAccount() {
        return theAccount;
    }
        
    private void credit(APDU apdu) {

        byte[] buffer = apdu.getBuffer();
        byte numBytes = buffer[ISO7816.OFFSET_LC];
        byte byteRead = (byte)(apdu.setIncomingAndReceive());

        if ( ( numBytes != 2 ) || (byteRead != 2) )
        ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);

        short creditAmount = (short)
            ((short)(buffer[ISO7816.OFFSET_CDATA] << (short)8) |
            (short)(buffer[ISO7816.OFFSET_CDATA + 1]));

        if ( ( creditAmount > MAX_BALANCE)
            || ( creditAmount < (short)0 ) ) {
            ISOException.throwIt(SW_INVALID_TRANSACTION_AMOUNT);
        }
        
        if ( (short)( balance + creditAmount)  > MAX_BALANCE ) {
            ISOException.throwIt(SW_MAX_BALANCE_EXCEEDED);
        }

        JCSystem.beginTransaction();
        balance = (short)(balance + creditAmount);
        JCSystem.commitTransaction();
    
    }
    
    private void getBalance(APDU apdu) {
        
        AID connection_aid = JCSystem.lookupAID( CONNECTION_MGR_AID_BYTES,
                             (short)0, (byte)CONNECTION_MGR_AID_BYTES.length);
        if (!(JCSystem.isAppletActive(connection_aid)))
         ISOException.throwIt(SW_NO_CONNECTION);
        
        byte[] buffer = apdu.getBuffer();
        short le = apdu.setOutgoing();

        if ( le < 2 )
            ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);

        apdu.setOutgoingLength((byte)2);

        buffer[0] = (byte)(balance >> (short)8);
        buffer[1] = (byte)(balance & (short)0x00FF);

        apdu.sendBytes((short)0, (short)2);
    }
    
}
