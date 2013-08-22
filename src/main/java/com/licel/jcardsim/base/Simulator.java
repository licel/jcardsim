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

import java.lang.reflect.Method;
import javacard.framework.AID;
import javacard.framework.Applet;
import javacard.framework.JCSystem;
import javacard.framework.SystemException;

/**
 * Main class for deal with Applets
 */
public class Simulator {
            
    /**
     * Construct <code>Simulator</code> object and init base systems
     */
    public Simulator() {
        JCSystem.getVersion();
    }

    /**
     * Load <code>Applet</code> into Simulator
     * @param aid applet aid
     * @param appletClass applet class
     * @return applet <code>AID</code>
     * @throws SystemException if <code>appletClass</code> not instanceof 
     * <code>javacard.framework.Applet</code>
     */
    public AID loadApplet(AID aid, Class appletClass) throws SystemException {
          if (!(appletClass.getSuperclass() == Applet.class)) {
            SystemException.throwIt(SystemException.ILLEGAL_VALUE);
        }
        SimulatorSystem.getRuntime().loadApplet(aid, appletClass);
        return aid;  
    }

    /**
     * Create <code>Applet</code> instance in Simulator
     * @param aid applet aid
     * @param bArray the array containing installation parameters
     * @param bOffset the starting offset in bArray
     * @param bLength the length in bytes of the parameter data in bArray
     * @return applet <code>AID</code>
     * @throws SystemException if exception in <code>Applet.install(..)</code>
     * method occurs.
     */
    public AID createApplet(AID aid, byte bArray[], short bOffset,
            byte bLength) throws SystemException {
        try {
            Class appletClass = SimulatorSystem.getRuntime().getAppletClass(aid);
            if(appletClass == null) SystemException.throwIt(SystemException.ILLEGAL_AID);
            SimulatorSystem.getRuntime().appletInstalling(aid);
            Method initMethod = appletClass.getMethod("install",
                    new Class[]{byte[].class, short.class, byte.class});
            initMethod.invoke(null, new Object[]{bArray, new Short(bOffset), new Byte(bLength)});
        } catch (Exception ex) {
            SystemException.throwIt(SimulatorSystem.SW_APPLET_CRATION_FAILED);
        }
        return aid;
    }
    
    /**
     * Install <code>Applet</code> into Simulator without installing data
     * @param aid applet aid or null
     * @param appletClass applet class
     * @return applet <code>AID</code>
     * @throws SystemException if <code>appletClass</code> not instanceof 
     * <code>javacard.framework.Applet</code>
     */
    public AID installApplet(AID aid, Class appletClass) throws SystemException {
        return installApplet(aid, appletClass, new byte[]{}, (short) 0, (byte) 0);
    }

    /**
     * Install <code>Applet</code> into Simulator.
     * This method is equal to:
     * <code>
     * loadApplet(...);
     * createApplet(...);
     * </code>
     * @param aid applet aid or null
     * @param appletClass applet class
     * @param bArray the array containing installation parameters
     * @param bOffset the starting offset in bArray
     * @param bLength the length in bytes of the parameter data in bArray
     * @return applet <code>AID</code>
     * @throws SystemException if <code>appletClass</code> not instanceof 
     * <code>javacard.framework.Applet</code>
     */
    public AID installApplet(AID aid, Class appletClass, byte bArray[], short bOffset,
            byte bLength) throws SystemException {
        loadApplet(aid, appletClass);
        return createApplet(aid, bArray, bOffset, bLength);
    }

    /**
     * Select applet by it's AID 
     * It's method must be called before start working with applet instance
     * @param aid appletId
     * @return true if applet selection success
     */
    public boolean selectApplet(AID aid) throws SystemException {
        return SimulatorSystem.selectApplet(aid);
    }

    /**
     * Transmit APDU to previous selected applet
     * @param commandAPDU command apdu
     * @return response apdu
     * @see CommandAPDU
     * @see ResponseAPDU
     * @throws SystemException.ILLEGAL_USE if appplet not selected before
     */
    public byte[] transmitCommand(byte[] command)
            throws SystemException {
        return SimulatorSystem.transmitCommand(command);
    }
    
    /**
     * powerdown/powerup
     */
    public void reset() {
        SimulatorSystem.getRuntime().reset();
    }

    public void resetRuntime() {
        SimulatorSystem.getRuntime().resetRuntime();
    }
}
