/*
 * Copyright © 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.licel.jcardsim.samples.odSample.packageB;

import javacard.framework.*;
import com.licel.jcardsim.samples.odSample.libPackageC.*;

/**
 * package AID - 0xA0 0x00 0x00 0x00 0x62 0x03 0x01 0x0C 0x07 0x02
 * applet AID - 0xA0 0x00 0x00 0x00 0x62 0x03 0x01 0x0C 0x07 0x02 0x01
 *
 * Applet used to demonstrate applet deletion and package deletion. It
 * also demonstrates dependencies by sharing references to objects and
 * shearable references across packages
 **/
public class B extends Applet implements Shareable, AppletEvent{

    static BTreeNode sObj=null;
        static B sbFirst = null;
    short data;
    BTreeNode obj=null;
        B bFirst = null;
        B bRef = null;
    
    /**
     * method instantiates aninstance of B passing the arguments
     **/
    public static void install(byte []bArr,short bOffset,byte bLength){
        new B(bArr,bOffset,bLength);
    }
  
    /**
     * method returns pointer to this instance, ignores the param
     **/
    public Shareable getShareableInterfaceObject(AID client_aid,
                                                 byte param){
        return this;
    }

  /**
   * Constructor. Makes 2nd instance have a reference to the 1st instance.
   * The 2nd instance also has a reference to the BTreeNode object owned by the
   * 1st instance. Also registers with eigher the default AID or
   * the one provided in parameters
   **/
  private B(byte[] bArray, short offset, byte length){
    data=C.DATA;
    if(sObj==null){
      obj=new BTreeNode();
      sObj=obj;
      sbFirst = this;
      bFirst = this;
    }else{      
      // move static reference to BTreeNode object into instance field
      obj=sObj;
      sObj=null;    
      // move static reference to B's first instance into instance field
      bFirst = sbFirst;
      sbFirst = null;
      if(JCSystem.isObjectDeletionSupported()) {
          JCSystem.requestObjectDeletion();
      }
    }
    //register
    if(bArray[offset]==(short)0){
        this.register();
    }else{
        this.register(bArray,(short)(offset+1),bArray[offset]);
    }
  } 

    /**
     * method processes the APDU commands passed to this applet instance.
     * It only accepts the SELECT and SETUP dependency(0x12) commands.
     **/
    public void process(APDU apdu) throws ISOException{
            byte[] buffer = apdu.getBuffer();
            if (selectingApplet()) return;
            else if (buffer[ISO7816.OFFSET_CLA]==(byte)0x80){         
                 switch (buffer[ISO7816.OFFSET_INS]) {
                     case 0x12:
                         // setup reference from B's first instance to this
                         bFirst.setReference(this);
                         break;
                     default:
                       ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
                 }
            }
            else ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);               
        }
       
        /**
     * method sets reference in bRef field to input parameter B object. This
         * is used for applet dependency and uninstall demonstration
     **/
    public void setReference(B b){
            bRef = b;
        }
        
        /**
     * method resets reference in bRef field to null. This
         * is called to remove applet dependency
     **/
    public void resetReference(){
            bRef = null;
        }
        
        /**
         * uninstall method called before applet deletion
         */
        public void uninstall(){
            bFirst.resetReference();
        }
}
  
