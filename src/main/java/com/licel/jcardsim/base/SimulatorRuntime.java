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

import com.licel.jcardsim.utils.AIDUtil;
import javacard.framework.*;
import javacardx.apdu.ExtendedLength;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Base implementation of Java Card Runtime
 * @see JCSystem
 * @see Applet
 */
public class SimulatorRuntime {

    // storage for registered applets
    private final SortedMap<AID, AppletHolder> applets = new TreeMap<AID, AppletHolder>(AIDUtil.comparator());
    // method for resetting APDUs
    private final Method apduPrivateResetMethod;
    // outbound response byte array buffer
    private final byte[] responseBuffer = SimulatorSystem.makeInternalBuffer(Short.MAX_VALUE + 2);

    // current selected applet
    private AID currentAID;
    // previous selected applet
    private AID previousAID;
    // applet in INSTALL phase
    private AID appletToInstallAID;
    // outbound response byte array buffer size
    private short responseBufferSize = 0;
    // if the applet is currently being selected
    private boolean selecting = false;

    public SimulatorRuntime() {
        try {
            apduPrivateResetMethod = APDU.class.getDeclaredMethod("internalReset", byte[].class);
            apduPrivateResetMethod.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

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
        for (AID aid : applets.keySet()) {
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
        for (AID aid : applets.keySet()) {
            if (aid.equals(lookupAid)) {
                return applets.get(aid);
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
        AppletHolder a = lookupApplet(aid);
        if (a == null) {
            return null;
        }
        return a.getAppletClass();
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
     * Delete applet
     */
    protected void deleteApplet(AID aid) {
        AppletHolder appletHolder = lookupApplet(aid);
        if (appletHolder == null) {
            throw new SystemException(SystemException.ILLEGAL_AID);
        }

        applets.remove(aid);
        Applet applet = appletHolder.getApplet();
        if (applet == null) {
            return;
        }

        if (getApplet(currentAID) == applet) {
            deselect(appletHolder);
        }

        if (applet instanceof AppletEvent) {
            try {
                ((AppletEvent) applet).uninstall();
            }
            catch (Exception e) {
                // ignore all
            }
        }
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
            throw new SystemException(SystemException.ILLEGAL_AID);
        }
        ah.setApplet(applet);
        ah.register();
        ah.install();
        appletToInstallAID = null;
    }

    /**
     * Select applet
     * @param aid Applet AID
     * @return data from select command
     */
    byte[] selectApplet(AID aid) {
        if (aid == null) {
            throw new NullPointerException("aid");
        }

        byte[] aidBuffer = new byte[16];
        byte length = aid.getBytes(aidBuffer, (short) 0);

        byte[] selectCmd = new byte[length + ISO7816.OFFSET_CDATA];
        selectCmd[ISO7816.OFFSET_CLA] = ISO7816.CLA_ISO7816;
        selectCmd[ISO7816.OFFSET_INS] = ISO7816.INS_SELECT;
        selectCmd[ISO7816.OFFSET_P1] = 0x04;
        selectCmd[ISO7816.OFFSET_P2] = 0x00;
        selectCmd[ISO7816.OFFSET_LC] = length;
        System.arraycopy(aidBuffer, 0, selectCmd, ISO7816.OFFSET_CDATA, length);

        return transmitCommand(selectCmd);
    }

    /**
     * Check if applet is currently being selected
     * @param aThis applet
     * @return true if applet is being selected
     */
    public boolean isAppletSelecting(Applet aThis) {
        return aThis == getApplet(getAID()) && selecting;
    }

    /**
     * Transmit APDU to previous selected applet
     * @param command command apdu
     * @return response apdu
     * @throws SystemException <code>SystemException.ILLEGAL_USE</code> if appplet not selected before
     */
    byte[] transmitCommand(byte[] command) throws SystemException {
        final ApduCase apduCase = ApduCase.getCase(command);
        final byte[] theSW = new byte[2];
        byte[] response;

        Applet applet = getApplet(getAID());

        selecting = false;
        // check if there is an applet to be selected
        if (!apduCase.isExtended() && isAppletSelectionApdu(command)) {
            AID newAid = findAppletForSelectApdu(command, apduCase);
            if (newAid != null) {
                deselect(lookupApplet(getAID()));
                currentAID = newAid;
                applet = getApplet(getAID());
                selecting = true;
            }
            else if (applet == null) {
                Util.setShort(theSW, (short) 0, ISO7816.SW_APPLET_SELECT_FAILED);
                return theSW;
            }
        }

        if (applet == null) {
            throw new SystemException(SystemException.ILLEGAL_USE);
        }

        if (apduCase.isExtended()) {
            if (applet instanceof ExtendedLength) {
                SimulatorSystem.setExtendedApduMode(true);
            }
            else {
                Util.setShort(theSW, (short)0, ISO7816.SW_WRONG_LENGTH);
                return theSW;
            }
        }
        else {
            SimulatorSystem.setExtendedApduMode(false);
        }

        try {
            if (selecting) {
                boolean success;
                try {
                    success = applet.select();
                }
                catch (Exception e) {
                    success = false;
                }
                if (!success) {
                    throw new ISOException(ISO7816.SW_APPLET_SELECT_FAILED);
                }
            }

            // set apdu
            APDU apdu = SimulatorSystem.getCurrentAPDU();
            apduPrivateResetMethod.invoke(apdu, command);
            applet.process(apdu);
            Util.setShort(theSW, (short) 0, (short) 0x9000);
        } catch (Throwable e) {
            Util.setShort(theSW, (short) 0, ISO7816.SW_UNKNOWN);
            if (e instanceof CardException) {
                Util.setShort(theSW, (short) 0, ((CardException) e).getReason());
            } else if (e instanceof CardRuntimeException) {
                Util.setShort(theSW, (short) 0, ((CardRuntimeException) e).getReason());
            }
        }
        finally {
            selecting = false;
        }

        // if theSW = 0x61XX or 0x9XYZ than return data (ISO7816-3)
        if(theSW[0] == 0x61 || (theSW[0] >= (byte)0x90 && theSW[0]<=0x9F)) {
            response = new byte[responseBufferSize + 2];
            Util.arrayCopyNonAtomic(responseBuffer, (short) 0, response, (short) 0, responseBufferSize);
            Util.arrayCopyNonAtomic(theSW, (short) 0, response, responseBufferSize, (short) 2);
        }
        else {
            response = theSW;
        }

        Util.arrayFillNonAtomic(responseBuffer, (short) 0, (short) 255, (byte) 0);
        responseBufferSize = 0;
        return response;
    }

    protected AID findAppletForSelectApdu(byte[] selectApdu, ApduCase apduCase) {
        if (apduCase == ApduCase.Case1 || apduCase == ApduCase.Case2) {
            // on a regular Smartcard we would select the CardManager applet
            // in this case we just select the first applet
            return applets.isEmpty() ? null : applets.firstKey();
        }

        for (AID aid : applets.keySet()) {
            if (aid.equals(selectApdu, ISO7816.OFFSET_CDATA, selectApdu[ISO7816.OFFSET_LC])) {
                return aid;
            }
        }

        for (AID aid : applets.keySet()) {
            if (aid.partialEquals(selectApdu, ISO7816.OFFSET_CDATA, selectApdu[ISO7816.OFFSET_LC])) {
                return aid;
            }
        }

        return null;
    }

    protected void deselect(AppletHolder appletHolder) {
        if (appletHolder != null) {
            try {
                Applet applet = appletHolder.getApplet();
                applet.deselect();
            } catch (Exception e) {
                // ignore all
            }
        }
        if (SimulatorSystem.getTransactionDepth() != 0) {
            SimulatorSystem.abortTransaction();
        }
        // TODO perform CLEAR_ON_DESELECT
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
        Iterator<AID> aids = applets.keySet().iterator();
        ArrayList<AID> aidsToTrash = new ArrayList<AID>();
        while (aids.hasNext()) {
            AID aid = aids.next();
            AppletHolder ah = lookupApplet(aid);
            if (ah.getState() != AppletHolder.INSTALLED) {
                aidsToTrash.add(aid);
            }
        }
        for (AID anAidsToTrash : aidsToTrash) {
            deleteApplet(anAidsToTrash);
        }

        Arrays.fill(responseBuffer, (byte) 0);
        responseBufferSize = 0;
        currentAID = null;
        previousAID = null;
        appletToInstallAID = null;
    }

    void resetRuntime() {
        Iterator<AID> aids = applets.keySet().iterator();
        ArrayList<AID> aidsToTrash = new ArrayList<AID>();
        while (aids.hasNext()) {
            AID aid = aids.next();
            aidsToTrash.add(aid);
        }
        for (AID anAidsToTrash : aidsToTrash) {
            deleteApplet(anAidsToTrash);
        }

        Arrays.fill(responseBuffer, (byte) 0);
        responseBufferSize = 0;
        currentAID = null;
        previousAID = null;
        appletToInstallAID = null;
    }

    static boolean isAppletSelectionApdu(byte[] apdu) {
        final byte channelMask = (byte) 0xFC; // mask out %b000000xx
        final byte p2Mask = (byte) 0xE3; // mask out %b000xxx00

        final byte cla = (byte) (apdu[ISO7816.OFFSET_CLA] & channelMask);
        final byte ins = apdu[ISO7816.OFFSET_INS];
        final byte p1 = apdu[ISO7816.OFFSET_P1];
        final byte p2 = (byte) (apdu[ISO7816.OFFSET_P2] & p2Mask);

        return cla == ISO7816.CLA_ISO7816 && ins == ISO7816.INS_SELECT &&
                p1 == 4 && p2 == 0;
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
