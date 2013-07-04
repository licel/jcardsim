/*
 * Copyright © 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * Main.java
 *
 * Created on September 19, 2001, 3:24 PM
 */

package com.licel.jcardsim.samples.ServiceDemo;

import javacard.framework.APDU;
import javacard.framework.ISOException;
import javacard.framework.service.*;

/**
 *
 * @author  vo113324
 */
public class Main extends javacard.framework.Applet {

    
    private Dispatcher disp;
    private Service serv;
    
    public Main()
    {
        disp = new Dispatcher( (short) 1);
        serv = new TestService();
        disp.addService(serv, Dispatcher.PROCESS_COMMAND);
        
        register();
    }
    
    
    public static void install(byte[] aid, short s, byte b)
    {
        new Main();
    }
        
    public void process(APDU apdu) throws ISOException {
        
        disp.process(apdu);
        
    }
    
}
