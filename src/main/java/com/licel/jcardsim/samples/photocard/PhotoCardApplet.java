/*
 * Copyright © 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)PhotoCardApplet.java	1.2 03/06/06
 */

package com.licel.jcardsim.samples.photocard;

import java.rmi.*;

import javacard.framework.APDU;
import javacard.framework.ISOException;
import javacard.framework.UserException;
import javacard.framework.Util;
import javacard.framework.service.*;

/**
 *
 * @author  oscarm
 */
public class PhotoCardApplet extends javacard.framework.Applet {
    
    
    private Dispatcher disp;
    private RemoteService serv;
    private Remote photoStorage;
    
    public PhotoCardApplet() {
        photoStorage = new PhotoCardImpl();
        
        disp = new Dispatcher( (short) 1);
        serv = new RMIService(photoStorage);
        disp.addService(serv, Dispatcher.PROCESS_COMMAND);
        
        register();
    }
    
    
    public static void install(byte[] aid, short s, byte b) {
        new PhotoCardApplet();
    }
    
    public void process(APDU apdu) throws ISOException {
        
        disp.process(apdu);
        
    }
        
}

