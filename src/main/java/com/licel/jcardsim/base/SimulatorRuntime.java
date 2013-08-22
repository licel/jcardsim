/*
 * Copyright 2011 Licel LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.licel.jcardsim.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javacard.framework.*;

/**
 * Base implementation of Java Card Runtime
 * @see JCSystem
 * @see Applet
 */
public class SimulatorRuntime {

    private static final byte[] DEFAULT_APPLET_AID = new byte[]{};
    // storage for registered applets
    private HashMap applets = new HashMap();
    // current selected applet
    private AID currentAID;
    // previous selected applet
    private AID previousAID;
    // applet in INSTALL phase
    private AID appletToInstallAID;
    // inbound command byte array buffer
    byte[] commandBuffer;
    // outbound response byte array buffer
    byte[] responseBuffer = JCSystem.makeTransientByteArray((short) 255, JCSystem.CLEAR_ON_RESET);
    // outbound response byte array buffer size
    short responseBufferSize = 0;
    // SW
    byte[] theSW = JCSystem.makeTransientByteArray((short) 2, JCSystem.CLEAR_ON_RESET);

    /**
     * Return current applet context AID or null
     */
    public AID getAID() {
        return currentAID;
    }

    /**
     * Lookup applet by aid contains in byte array
     */
    public AID lookupAID(byte buffer[], short offset, byte length) {
        // no construct new AID, iterate applets
        Iterator aids = applets.keySet().iterator();
        while (aids.hasNext()) {
            AID aid = (AID) aids.next();
            if (aid.equals(buffer, offset, length)) {
                return aid;
            }
        }
        return null;
    }

    /**
     * Lookup applet by aid
     */
    public AppletHolder lookupApplet(AID lookupAid) {
        Iterator aids = applets.keySet().iterator();
        while (aids.hasNext()) {
            AID aid = (AID) aids.next();
            if (aid.equals(lookupAid)) {
                return (AppletHolder) applets.get(aid);
            }
        }
        return null;
    }

    /**
     * Return previous selected applet context AID or null
     */
    public AID getPreviousContextAID() {
        return previousAID;
    }

    public void appletInstalling(AID aid) {
        appletToInstallAID = aid;
    }

    /**
     * Return <code>Applet</code> by it's AID or null
     * @param aid applet <code>AID</code>
     */
    protected Applet getApplet(AID aid) {
        if (aid == null) {
            return null;
        }
        AppletHolder a = lookupApplet(aid);
        if(a == null) return null;
        else return a.getApplet();
    }

     /**
     * Return <code>Applet class</code> by it's AID or null
     * @param aid applet <code>AID</code>
     */
    protected Class getAppletClass(AID aid) {
        if (aid == null) {
            return null;
        }
        return lookupApplet(aid).getAppletClass();
    }   
    /**
     * Load applet
     */
    protected void loadApplet(AID aid, Class appletClass) {
        // see specification
        if (lookupApplet(aid) != null) {
            SystemException.throwIt(SystemException.ILLEGAL_AID);
        }
        applets.put(aid, new AppletHolder(appletClass));
    }

    /**
     * Register applet
     */
    protected void registerApplet(AID aid, Applet applet) {
        AppletHolder ah = null;
        // if register(Applet applet);
        if (aid == null && appletToInstallAID != null) {
            ah = lookupApplet(appletToInstallAID);
        } else if (aid != null) {
            ah = lookupApplet(aid);
        }
        if (ah == null) {
            SystemException.throwIt(SystemException.ILLEGAL_AID);
        }
        ah.setApplet(applet);
        ah.register();
        ah.install();
        appletToInstallAID = null;
    }

    /**
     * Select applet for using
     * @param aid applet aid
     * @return true if select sucess
     */
    boolean selectApplet(AID aid) {
        Applet newApplet = getApplet(aid);
        // deselect previous selected applet
        if (currentAID != null) {
            try {
                Applet applet = getApplet(currentAID);
                applet.deselect();
            } catch (Exception e) {
                // ignore all
            } finally {
                if (SimulatorSystem.getTransactionDepth() != 0) {
                    SimulatorSystem.abortTransaction();
                }
            }
        }
        if (newApplet == null) {
            return false;
        }
        // select new applet
        try {
            newApplet.select();
            previousAID = currentAID;
            currentAID = aid;
            return true;
        } catch (Exception e) {
        } finally {
            if (SimulatorSystem.getTransactionDepth() != 0) {
                SimulatorSystem.abortTransaction();
            }
        }
        return false;
    }

    /**
     * Transmit APDU to previous selected applet
     * @param commandAPDU command apdu
     * @return response apdu
     * @throws SystemException.ILLEGAL_USE if appplet not selected before
     */
    byte[] transmitCommand(byte[] command) throws SystemException {
        Applet applet = getApplet(getAID());
        byte[] response = null;
        Util.arrayFillNonAtomic(theSW, (short) 0, (short) 2, (byte) 0);
        if (applet == null) {
            SystemException.throwIt(SystemException.ILLEGAL_USE);
        }
        try {
            // set apdu
            Util.arrayCopyNonAtomic(command, (short) 0, APDU.getCurrentAPDUBuffer(),
                    (short) 0, (short) command.length);
            applet.process(APDU.getCurrentAPDU());
            response = JCSystem.makeTransientByteArray((short) (responseBufferSize + 2), JCSystem.CLEAR_ON_RESET);
            Util.arrayCopyNonAtomic(responseBuffer, (short) 0, response, (short) 0, responseBufferSize);
            Util.setShort(response, responseBufferSize, ISO7816.SW_NO_ERROR);
        } catch (Throwable e) {
            Util.setShort(theSW, (short) 0, ISO7816.SW_UNKNOWN);
            if (e instanceof CardException) {
                Util.setShort(theSW, (short) 0, ((CardException) e).getReason());
            } else if (e instanceof CardRuntimeException) {
                Util.setShort(theSW, (short) 0, ((CardRuntimeException) e).getReason());
            }
            response = theSW;
        }
        APDU.getCurrentAPDU().reset();
        Util.arrayFillNonAtomic(responseBuffer, (short) 0, (short) 255, (byte) 0);
        responseBufferSize = 0;
        return response;
    }


    /**
     * Copy response bytes to internal buffer
     * @param buffer source byte array
     * @param bOff the starting offset in buffer
     * @param len the length in bytes of the response
     */
    void sendAPDU(byte[] buffer, short bOff, short len) {
        responseBufferSize = Util.arrayCopyNonAtomic(buffer, bOff, responseBuffer, responseBufferSize, len);
    }

    /**
     * powerdown/powerup
     */
    void reset() {
        Iterator aids = applets.keySet().iterator();
        ArrayList aidsToTrash = new ArrayList();
        while (aids.hasNext()) {
            AID aid = (AID) aids.next();
            AppletHolder ah = lookupApplet(aid);
            if (ah.getState() != AppletHolder.INSTALLED) {
                aidsToTrash.add(aid);
            }
        }
        for(int i=0;i<aidsToTrash.size();i++) {
            applets.remove(aidsToTrash.get(i));
        }

        Util.arrayFillNonAtomic(responseBuffer, (short) 0, (short) responseBuffer.length, (byte) 0);
        responseBufferSize = 0;
        currentAID = null;
        previousAID = null;
        appletToInstallAID = null;
    }

    void resetRuntime() {
        Iterator aids = applets.keySet().iterator();
        ArrayList aidsToTrash = new ArrayList();
        while (aids.hasNext()) {
            AID aid = (AID) aids.next();
            aidsToTrash.add(aid);
        }
        for(int i=0;i<aidsToTrash.size();i++) {
            applets.remove(aidsToTrash.get(i));
        }

        Util.arrayFillNonAtomic(responseBuffer, (short) 0, (short) responseBuffer.length, (byte) 0);
        responseBufferSize = 0;
        currentAID = null;
        previousAID = null;
        appletToInstallAID = null;
    }
    
    // internal class which is holds Applet instance and it's state
    class AppletHolder {
        final static byte DOWNLOADING = 0;
        final static byte LOADED = 1;
        final static byte INSTALLED = 2;
        final static byte REGISTERED = 3;
        private byte state;
        private Applet applet;
        private Class appletClass;
        
        AppletHolder(Applet applet, byte state){
            this.applet = applet;
            this.state = state;
        }
        
        AppletHolder(Class appletClass) {
            this.appletClass = appletClass;
            this.state = LOADED;
        }
        
        void install(){
            this.state = INSTALLED;
        }
        
        void register(){
            this.state = REGISTERED;
        }
        
        byte getState(){
            return state;
        }
        
        void setApplet(Applet applet){
            this.applet = applet;
        }
        
        Applet getApplet(){
            return applet;
        }
        
        Class getAppletClass(){
            return appletClass;
        }
    
    }
}
