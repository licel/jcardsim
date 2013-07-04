/*
 * Copyright © 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)PhotoCard.java	1.2 03/06/06
 */

package com.licel.jcardsim.samples.photocard;

import java.rmi.*;
import javacard.framework.*;

/** 
  * PhotoCard interface
  * Defines methods to be used as interface between photo client
  * and storage smart card
  */
public interface PhotoCard extends Remote {

    // User exception error codes
    
    /**
      * No space available for photo storage
      */
    public static final short NO_SPACE_AVAILABLE = (short)0x6000;

    /**
      * No photo stored in selected location
      */
    public static final short NO_PHOTO_STORED  = (short)0x6001;

    /**
      * Invalid photo ID
      */
    public static final short INVALID_PHOTO_ID = (short)0x6002;
    
    /**
      * Invalid argument value
      */
    public static final short INVALID_ARGUMENT   = (short)0x6003;
    
    /**
      * Maximum photo size
      */
    public static final short MAX_SIZE           = (short)0x7FFF;

    /**
      * Maximum on-card photos
      */
    public static final short MAX_PHOTO_COUNT    = (short)4;

    /**
      * Maximum bytes for transfer
      */
    public static final short MAX_BUFFER_BYTES   = (short)96;
    
    
    /** 
      * This method requests the smart card to allocate space to store
      * a photo image of the specified size.
      * @param size - Image size to store in the smart card
      * @return photoID - ID slot in card where photo will be stored
      * @exception UserException - thrown if error condition occurs, or
      *  invalid parameters passed.
      */
    public short requestPhotoStorage(short size) 
        throws RemoteException, UserException;

    /** 
      * This method loads a series of bytes belonging to the photo
      * into the smart card at the position specified.
      * @param photoID - photo slot where to store data
      * @param data - byte array contaiing binary photo information
      * @param size - number of bytes being passed into the smart card
      * @param offset - position inside photo buffer where to store data.
      * @boolean more - <b>true</b> indicates more data coming; <b>false</b>
      *  inidicates this is the last data chunk.
      * @exception UserException - thrown if error condition occurs, or
      *  invalid parameters passed.
      */
    public void loadPhoto(short photoID, byte[] data, 
        short size, short offset, boolean more) 
        throws RemoteException, UserException;

    /** 
      * This method deletes the photo whose ID is specified in the card.
      * @param photoID - ID slot of photo to delete
      * @exception UserException - thrown if error condition occurs, or
      *  invalid parameters passed.
      */
    public void deletePhoto(short photoID) 
        throws RemoteException, UserException;

    /** 
      * This method retrieves the photo size whose ID is specified.
      * @param photoID - ID slot of photo to access
      * @exception UserException - thrown if error condition occurs, or
      *  invalid parameters passed.
      */
    public short getPhotoSize(short photoID) 
        throws RemoteException, UserException;
        
    /** 
      * This method retrueves a series of bytes belonging to the photo
      * from the smart card at the position specified.
      * @param photoID - photo slot where to store data
      * @param size - number of bytes expected from the smart card
      * @param offset - position inside photo buffer where to access data.
      * @return byte array with binary data from photo stored inside the
      *  smart card
      * @exception UserException - thrown if error condition occurs, or
      *  invalid parameters passed.
      */        
    public byte[] getPhoto(short photoID, short offset, short size) 
        throws RemoteException, UserException;
}
