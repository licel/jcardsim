/*
 * Copyright © 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.licel.jcardsim.samples.ChannelsDemo;

import javacard.framework.*;

/**
 * This applet keeps track of the network connection for a
 * fictional wireless device.  Every time unit the device is
 * being actively used on the network will result in decreasing
 * credits available to the user.  If the user changes areas,
 * different charge rates applies.  If the user runs out of
 * credits, then the connection is terminated.
 */

public class ConnectionManager extends Applet 
    implements MultiSelectable {

    // code of CLA byte in the command APDU header
    final static byte CM_CLA            = (byte)0x80;

    // codes of INS byte in the command APDU header
    final static byte TIMETICK          = (byte) 0x10;
    final static byte SETCONNECTION     = (byte) 0x20;
    final static byte RESETCONNECTION   = (byte) 0x30;

    // Connection status constants
    final static short INACTIVE_AREA    = (short) 0xFFFF;
    final static byte  CONNECTION_INUSE = (byte)  0x01;
    final static byte  CONNECTION_FREE  = (byte)  0x00;

    final static short SW_CONNECTION_BUSY  = (short)0x6A50;
    final static short SW_NEGATIVE_BALANCE = (short)0x6A51;
    final static short SW_NO_NETWORK       = (short)0x6A52;
    final static short SW_NO_ACCOUNT       = (short)0x6A53;

    // Connection data
    private short[]   activeAreaCode;
    private byte[]    connectionStatus;

    private ConnectionManager (byte[] bArray, short bOffset, byte bLength) {
        // The connection manager keeps track of the area where the 
        // device is operating.  Since the area changes as the user moves,
        // we keep track of the area by setting a variable in Transient
        // Clear-On-Deselect memory.
        activeAreaCode = 
            JCSystem.makeTransientShortArray((short)1, 
            JCSystem.CLEAR_ON_DESELECT);
        activeAreaCode[0] = INACTIVE_AREA;
        connectionStatus = 
            JCSystem.makeTransientByteArray((short)1, 
            JCSystem.CLEAR_ON_DESELECT);
        register();
    }

    public static void install(byte[] bArray, short bOffset, byte bLength){
        new ConnectionManager(bArray, bOffset, bLength);
    }

    public boolean select() {
        initState();
        return true;
    }

    public void deselect() {
        clearState();
    }

    public boolean select(boolean appInstAlreadySelected) {
        // The connection manager can only be selected on one
        // logical channel. Reject selection if terminal tries
        // to select connection in more than one channel.
        if (appInstAlreadySelected) {
            // No more than 1 channel selection allowed
            return false;
        } else {
            initState();
            return true;
        }
    }

    public void deselect(boolean appInstStillSelected) {
        clearState();
    }

    public void process(APDU apdu) {
    
        byte[] buffer = apdu.getBuffer();
        buffer[ISO7816.OFFSET_CLA] = (byte)(buffer[ISO7816.OFFSET_CLA] 
            & (byte)0xFC);

        if ((buffer[ISO7816.OFFSET_CLA] == 0) &&
            (buffer[ISO7816.OFFSET_INS] == (byte)(0xA4)) )
            return;
            
        if (buffer[ISO7816.OFFSET_CLA] != CM_CLA)
            ISOException.throwIt (ISO7816.SW_CLA_NOT_SUPPORTED);
            
        switch (buffer[ISO7816.OFFSET_INS]) {
        case TIMETICK:      
            timeTick(apdu);
            return;
        case SETCONNECTION:
            setConnection();
            return;
        case RESETCONNECTION: 
            resetConnection();
            return;
        default:       
            ISOException.throwIt (ISO7816.SW_INS_NOT_SUPPORTED);
        }

    }
    
    // Connection management methods
    private void timeTick(APDU apdu) {
        // Updates the area code according to the passed parameter.
        byte[] buffer = apdu.getBuffer();
        byte numBytes = (byte)(buffer[ISO7816.OFFSET_LC]);
        byte byteRead = (byte)(apdu.setIncomingAndReceive());

        if ( ( numBytes != 2 ) || (byteRead != 2) )
        ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);

        // get area code
        short newAreaCode = (short)
            ((short)(buffer[ISO7816.OFFSET_CDATA] << (short)8) |
            (short)(buffer[ISO7816.OFFSET_CDATA + 1] & 0x00FF));
        
        if (newAreaCode != INACTIVE_AREA) {
            activeAreaCode[0] = newAreaCode;
        } else {
            resetConnection();
            ISOException.throwIt(SW_NO_NETWORK);
        }
        
        // If a connection is active, the user account is debited.
        // If user runs out of credits, the connection is terminated.        
        if (connectionStatus[0] == CONNECTION_INUSE) {
            
            if (AccountAccessor.getAccount() == null) {
                ISOException.throwIt(SW_NO_ACCOUNT);
            }
            
            if (AccountAccessor.getAccount().debit(activeAreaCode[0]) == false) {
                resetConnection();
                ISOException.throwIt(SW_NEGATIVE_BALANCE);
            }
        }
    }
    
    private void setConnection() {

        if (AccountAccessor.getAccount() == null) {
            ISOException.throwIt(SW_NO_ACCOUNT);
        }
        
        if (connectionStatus[0] == CONNECTION_INUSE) {
            ISOException.throwIt(SW_CONNECTION_BUSY);
        }
    
        if (activeAreaCode[0] == INACTIVE_AREA) {
            ISOException.throwIt(SW_NO_NETWORK);
        }
        
        // The first time unit is charged at connection setup
        if (AccountAccessor.getAccount().debit(activeAreaCode[0])) {
            connectionStatus[0] = CONNECTION_INUSE;
        } else {
           ISOException.throwIt(SW_NEGATIVE_BALANCE); 
        }        
    }
    
    private void resetConnection() {
        // Terminate the connection
        connectionStatus[0] = CONNECTION_FREE;
    }
    
    private void initState() {
        connectionStatus[0] = CONNECTION_FREE;
        activeAreaCode[0] = INACTIVE_AREA;
    }
    
    private void clearState() {
        activeAreaCode[0] = INACTIVE_AREA;
        connectionStatus[0] = CONNECTION_FREE;
    }    
}
