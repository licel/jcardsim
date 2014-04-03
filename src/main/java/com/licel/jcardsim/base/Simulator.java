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

import com.licel.jcardsim.io.JavaCardInterface;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import javacard.framework.AID;
import javacard.framework.Applet;
import javacard.framework.JCSystem;
import javacard.framework.SystemException;
import org.bouncycastle.util.encoders.Hex;

/**
 * Main class for deal with Applets
 */
public class Simulator implements JavaCardInterface {

    // default ATR - NXP JCOP 31/36K
    static final String DEFAULT_ATR = "3BFA1800008131FE454A434F5033315632333298";
    // ATR system property name
    static final String ATR_SYSTEM_PROPERTY = "com.licel.jcardsim.card.ATR";
    // card ATR 
    byte[] atr = null;
    static final String PROPERTY_PREFIX = "com.licel.jcardsim.card.applet.";
    static final String OLD_PROPERTY_PREFIX = "com.licel.jcardsim.smartcardio.applet.";
    // Applet AID system property template
    static final MessageFormat AID_SP_TEMPLATE = new MessageFormat("{0}.AID");
    // Applet ClassName system property template
    static final MessageFormat APPLET_CLASS_SP_TEMPLATE = new MessageFormat("{0}.Class");
    // Applet Class Loader
    AppletClassLoader cl = new AppletClassLoader(new URL[]{});

    /**
     * Construct Simulator object and init base systems
     */
    public Simulator() {
        resetRuntime();
        JCSystem.getVersion();
        atr = Hex.decode(System.getProperty(ATR_SYSTEM_PROPERTY, DEFAULT_ATR));
        // init preinstalled applets
        for (int i = 0; i < 10; i++) {
            String selectedPrefix = PROPERTY_PREFIX;
            String aidPropertyName = PROPERTY_PREFIX + AID_SP_TEMPLATE.format(new Object[]{new Integer(i)});
            String aidPropertyOldName = OLD_PROPERTY_PREFIX + AID_SP_TEMPLATE.format(new Object[]{new Integer(i)});
            String appletAID = System.getProperty(aidPropertyName);
            if (appletAID == null) {
                appletAID = System.getProperty(aidPropertyOldName);
                if (appletAID != null) {
                    selectedPrefix = OLD_PROPERTY_PREFIX;
                }
            }
            if (appletAID != null) {
                String appletClassName = System.getProperty(selectedPrefix + APPLET_CLASS_SP_TEMPLATE.format(new Object[]{new Integer(i)}));
                if (appletClassName != null) {
                    byte[] aidBytes = Hex.decode(appletAID);
                    if (aidBytes == null || aidBytes.length < 5 || aidBytes.length > 16) {
                        // skip incorrect applet
                        continue;
                    }
                    loadApplet(new AID(aidBytes, (short) 0, (byte) aidBytes.length), appletClassName);
                }
            }
        }

    }

    public AID loadApplet(AID aid, String appletClassName, byte[] appletJarContents) throws SystemException {
        // simple method, but emulate real card login
        // download data
        byte[] aidData = new byte[16];
        aid.getBytes(aidData, (short) 0);
        Class appletClass = null;
        try {
            cl.addAppletContents(appletJarContents);
            appletClass = cl.loadClass(appletClassName);
        } catch (Exception e) {
            SystemException.throwIt(SystemException.ILLEGAL_VALUE);

        }
        if (appletClass != null) {
            return loadApplet(aid, appletClass);
        } else {
            SystemException.throwIt(SystemException.ILLEGAL_VALUE);
            return null;
        }
    }

    public AID loadApplet(AID aid, String appletClassName) throws SystemException {
        Class appletClass = null;
        try {
            appletClass = cl.loadClass(appletClassName);
        } catch (ClassNotFoundException ex) {
            SystemException.throwIt(SystemException.ILLEGAL_VALUE);
        }
        if (!(appletClass.getSuperclass() == Applet.class)) {
            SystemException.throwIt(SystemException.ILLEGAL_VALUE);
        }
        return loadApplet(aid, appletClass);
    }

    /**
     * Load
     * <code>Applet</code> into Simulator
     *
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

    public AID createApplet(AID aid, byte bArray[], short bOffset,
            byte bLength) throws SystemException {
        try {
            Class appletClass = SimulatorSystem.getRuntime().getAppletClass(aid);
            if (appletClass == null) {
                SystemException.throwIt(SystemException.ILLEGAL_AID);
            }
            SimulatorSystem.getRuntime().appletInstalling(aid);
            Method initMethod = appletClass.getMethod("install",
                    new Class[]{byte[].class, short.class, byte.class});
            initMethod.invoke(null, new Object[]{bArray, new Short(bOffset), new Byte(bLength)});
        } catch (Exception ex) {
            ex.printStackTrace();
            SystemException.throwIt(SimulatorSystem.SW_APPLET_CRATION_FAILED);
        }
        return aid;
    }

    /**
     * Install
     * <code>Applet</code> into Simulator without installing data
     *
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
     * Install
     * <code>Applet</code> into Simulator. This method is equal to:
     * <code>
     * loadApplet(...);
     * createApplet(...);
     * </code>
     *
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

    public AID installApplet(AID aid, String appletClassName, byte bArray[], short bOffset,
            byte bLength) throws SystemException {
        loadApplet(aid, appletClassName);
        return createApplet(aid, bArray, bOffset, bLength);
    }

    public AID installApplet(AID aid, String appletClassName, byte[] appletContents, byte bArray[], short bOffset,
            byte bLength) throws SystemException {
        loadApplet(aid, appletClassName, appletContents);
        return createApplet(aid, bArray, bOffset, bLength);
    }

    public boolean selectApplet(AID aid) throws SystemException {
    	byte[] resp = SimulatorSystem.selectAppletWithResult(aid);
    	if(resp != null && resp.length > 1) {
        	int len = resp.length;
        	if(resp[len - 2] == (byte)0x90 && resp[len - 1] == 0) {
        		return true;
        	}
        }
        return false;
    }
    
    public byte[] selectAppletWithResult(AID aid) throws SystemException {
    	return SimulatorSystem.selectAppletWithResult(aid);
    }

    public byte[] transmitCommand(byte[] command)
            throws SystemException {
        return SimulatorSystem.transmitCommand(command);
    }

    public void reset() {
        SimulatorSystem.getRuntime().reset();
    }

    public final void resetRuntime() {
        SimulatorSystem.getRuntime().resetRuntime();
    }

    public byte[] getATR() {
        return atr;
    }

    class AppletClassLoader extends URLClassLoader {

        AppletClassLoader(URL[] urls) {
            super(urls, Simulator.class.getClassLoader());
        }

        void addAppletContents(byte[] appletJarContents) throws IOException {
            File downloadedAppletJar = File.createTempFile("applet", "contents");
            downloadedAppletJar.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(downloadedAppletJar);
            fos.write(appletJarContents);
            fos.close();
            addURL(downloadedAppletJar.toURI().toURL());

        }
    }
}
