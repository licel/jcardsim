/*
 * Copyright © 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)PhotoCardImpl.java	1.2 03/06/06
 */

package com.licel.jcardsim.samples.photocard;

import javacard.framework.*;
import javacard.framework.service.*;
import java.rmi.RemoteException;


public class PhotoCardImpl extends CardRemoteObject implements PhotoCard {

    // Internal fields    
    // Arrat containing photo objects
    private Object[] photos;
    
    private boolean[] photoReady;
    private byte[] buffer;
    
    PhotoCardImpl() {
        super();                     // export it
        // Initialize here
        photos = new Object[(short)MAX_PHOTO_COUNT];
        photoReady = new boolean[(short)MAX_PHOTO_COUNT];
        buffer = new byte[(short)MAX_BUFFER_BYTES];
        for (short i = (short)0; i < (short)MAX_PHOTO_COUNT; i++) {
            photoReady[i] = false;
        }        
    }

    // Implementation methods from Interface PhotoCard
    public short requestPhotoStorage(short size) 
        throws RemoteException, UserException {
        
        // 16-byte safety factor. For object header.
        if (JCSystem.getAvailableMemory(JCSystem.MEMORY_TYPE_PERSISTENT) < 
            (short)(size + (short)16) ) {
            UserException.throwIt(NO_SPACE_AVAILABLE);
        }
        
        for (short i = (short)0; i < (short)MAX_PHOTO_COUNT;i++) {
            byte[] thePhoto = (byte[])photos[i];
            
            if (photos[i] == null) {
                photos[i] = new byte[size];
                return (short)(i + 1);
            }
        }
        UserException.throwIt(NO_SPACE_AVAILABLE);
        // Compiler requires this unreachable statement
        return (short)(-1);
    }
        
    public void loadPhoto(short photoID, byte[] data, 
        short size, short offset, boolean more) 
        throws RemoteException, UserException {
        
        if (photoID > (short)MAX_PHOTO_COUNT) {
            UserException.throwIt(PhotoCard.INVALID_PHOTO_ID);
        }
        
        byte[] selPhoto = (byte[])photos[(short)(photoID - (short)1)];
        
        if (selPhoto == null) {
            UserException.throwIt(PhotoCard.NO_PHOTO_STORED);
        }
        
        if (((short)(offset + size)) > selPhoto.length) {
            UserException.throwIt(PhotoCard.INVALID_ARGUMENT);
        }
        
        Util.arrayCopy(data, (short)0, selPhoto, offset, size);
        if (more == false) {
            photoReady[(short)(photoID - (short)1)] = true;
        }
    }
    
    public void deletePhoto(short photoID) 
        throws RemoteException, UserException {
                
        if (photoID > (short)MAX_PHOTO_COUNT) {
            UserException.throwIt(PhotoCard.INVALID_PHOTO_ID);
        }

        byte[] thePhoto = (byte[])photos[(short)(photoID - (short)1)];
        if (thePhoto != null) {
            thePhoto = null;
            JCSystem.requestObjectDeletion();
            photoReady[(short)(photoID - (short)1)] = false;
        } else {
            UserException.throwIt(PhotoCard.NO_PHOTO_STORED);
        }
    }

    public short getPhotoSize(short photoID) 
        throws RemoteException, UserException {

        if (photoID > (short)MAX_PHOTO_COUNT) {
            UserException.throwIt(PhotoCard.INVALID_PHOTO_ID);
        }

        byte[] thePhoto = (byte[])photos[(short)(photoID - (short)1)];
        if (thePhoto != null) {
             return (short)(thePhoto.length);
        } else {
             return (short)0;
        }
    }
    
    public byte[] getPhoto(short photoID, short offset, short size) 
        throws RemoteException, UserException {

        if (photoID > (short)MAX_PHOTO_COUNT) {
            UserException.throwIt(PhotoCard.INVALID_PHOTO_ID);
        }

        byte[] selPhoto = (byte[])photos[(short)(photoID - (short)1)];
                
        if ((selPhoto == null) || (photoReady[(short)(photoID - (short)1)] == false)) {
            UserException.throwIt(PhotoCard.NO_PHOTO_STORED);
        }

        if (((short)(offset + size)) > selPhoto.length) {
            UserException.throwIt(PhotoCard.INVALID_ARGUMENT);
        }
        Util.arrayCopy(selPhoto, offset, buffer, (short)0, size);
        return buffer;
    }

}

