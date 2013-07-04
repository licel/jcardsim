/*
 * Copyright © 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.licel.jcardsim.samples.RMIDemo;

import java.rmi.*;

import javacard.framework.APDU;
import javacard.framework.ISOException;
import javacard.framework.UserException;
import javacard.framework.Util;
import javacard.framework.service.*;

/**
 *
 * @author  vo113324
 */
public class PurseApplet extends javacard.framework.Applet {
    
    
    private Dispatcher disp;
    private RemoteService serv;
    private Remote purse;
    
    public PurseApplet() {
        purse = new PurseImpl();
        disp = new Dispatcher( (short) 1);
        serv = new RMIService(purse);
        disp.addService(serv, Dispatcher.PROCESS_COMMAND);
        register();
    }
    
    
    public static void install(byte[] aid, short s, byte b) {
        new PurseApplet();
    }
    
    public void process(APDU apdu) throws ISOException {
        
        disp.process(apdu);
        
    }
        
}




