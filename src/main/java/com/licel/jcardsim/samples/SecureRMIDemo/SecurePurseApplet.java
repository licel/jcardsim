/*
 * Copyright © 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.licel.jcardsim.samples.SecureRMIDemo;

import java.rmi.RemoteException;

import javacard.framework.APDU;
import javacard.framework.ISOException;
import javacard.framework.UserException;
import javacard.framework.Util;
import javacard.framework.service.*;

public class SecurePurseApplet extends javacard.framework.Applet {
    
    
    private Dispatcher disp;
    
    public SecurePurseApplet() {


        SecurityService sec = new MySecurityService();

        Purse purse = new SecurePurseImpl(sec);
        
        RemoteService rmi = new RMIService(purse);
        
        disp = new Dispatcher( (short) 4);
        disp.addService(sec, Dispatcher.PROCESS_INPUT_DATA);
        disp.addService(sec, Dispatcher.PROCESS_COMMAND);
        disp.addService(rmi, Dispatcher.PROCESS_COMMAND);
        disp.addService(sec, Dispatcher.PROCESS_OUTPUT_DATA);
        
        register();
    }
    
    
    public static void install(byte[] aid, short s, byte b) {
        new SecurePurseApplet();
    }
    
    public void process(APDU apdu) throws ISOException {
        
        disp.process(apdu);
        
    }
        
}




