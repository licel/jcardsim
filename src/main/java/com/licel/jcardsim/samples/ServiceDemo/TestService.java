/*
 * Copyright © 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * TestService.java
 *
 * Created on September 19, 2001, 4:28 PM
 */

package com.licel.jcardsim.samples.ServiceDemo;

import javacard.framework.*;
import javacard.framework.service.*;

/**
 *
 */
public class TestService extends BasicService {
    
    public boolean processCommand(APDU command) {
        
        
        
        //        receiveInData(command);
        
        if(getINS(command) == (byte)1) {
            setOutputLength(command, (short)1);
            command.getBuffer()[5] = (byte) 0xAB;
            succeedWithStatusWord(command, (short) 0x6617 );
            
            return true;
        }
        
        if(getINS(command) == (byte)2) {
//            receiveInData(command);
//            succeed(command);

            setOutputLength(command, (short)0);

            succeedWithStatusWord(command, (short) 0x6618 );

            return true;
        }

        if(getINS(command) == (byte)3) {
//            receiveInData(command);

            setOutputLength(command, (short)0);
            succeed(command);
            return true;
        }
        
        if(getINS(command) == (byte)3) {
            receiveInData(command);

            setOutputLength(command, (short)0);
            succeed(command);
            return true;
        }
        
        return false;
    }
    
}
