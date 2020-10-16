/*
 * Copyright 2020 Licel LLC.
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

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import static com.licel.jcardsim.base.Simulator.ATR_SYSTEM_PROPERTY;
import static com.licel.jcardsim.base.Simulator.DEFAULT_ATR;
import com.licel.jcardsim.utils.AIDUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import javacard.framework.AID;
import javacard.framework.Applet;
import javacard.framework.JCSystem;
import javacard.framework.SystemException;
import org.objenesis.strategy.StdInstantiatorStrategy;

public class PersistentSimulatorRuntime extends SimulatorRuntime {
    private static final String PERSISTENT_BASE_DIR = "persistentSimulatorRuntime.dir";
    private final Kryo kryo;
    private String appletsDir;
        
    public PersistentSimulatorRuntime() {        
        kryo = new Kryo();
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        //kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
        
        String baseDir = System.getProperties().getProperty(PERSISTENT_BASE_DIR, null);
        if(baseDir != null) {
            File appletsDirFile = Paths.get(baseDir, System.getProperty(ATR_SYSTEM_PROPERTY, DEFAULT_ATR)).toFile();
            if(!appletsDirFile.exists()) {
                if(!appletsDirFile.mkdirs())
                    throw new RuntimeException("Fail to create a directory: " + appletsDirFile.getAbsolutePath());
            }
            appletsDir = appletsDirFile.getAbsolutePath();
        }
    }
    
    @Override
    public void loadApplet(AID aid, Class<? extends Applet> appletClass) {
        super.loadApplet(aid, appletClass);

        if(appletsDir != null) {
            File appletInstanceFile = new File(appletsDir, AIDUtil.toString(aid));
            if (appletInstanceFile.isFile()) {
                try {
                    try(Input input = new Input(new FileInputStream(appletInstanceFile))) {
                        Applet object = (Applet) kryo.readClassAndObject(input);
                        applets.put(aid, new ApplicationInstance(aid, object));
                    }
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
    
    @Override
    public void installApplet(AID loadFileAID, AID moduleAID, final AID appletAID,
                              byte[] bArray, short bOffset, byte bLength) {
        
        super.installApplet(loadFileAID, moduleAID, appletAID, bArray, bOffset, bLength);

        if(appletsDir != null) {
            try {
                AID varianceAid = appletAID;
                if(bLength > 0) {
                    varianceAid = new AID(bArray, bOffset, bLength);
                }
                File appletInstanceFile = new File(appletsDir, AIDUtil.toString(varianceAid));
                Applet applet = lookupApplet(varianceAid).getApplet();
                try(Output output = new Output(new FileOutputStream(appletInstanceFile))) {
                    kryo.writeClassAndObject(output, applet);
                }
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
        
    @Override
    public void reset() {
        super.reset();
        //Update files because transientMemory.clearOnReset() was called in super.reset();
        updateAppletFiles();
    }
        
    @Override
    public void resetRuntime() {
        activateSimulatorRuntimeInstance();
        transientMemory.clearOnReset();
        updateAppletFiles();
        
        Iterator<AID> aids = applets.keySet().iterator();
        ArrayList<AID> aidsToTrash = new ArrayList<AID>();
        while (aids.hasNext()) {
            AID aid = aids.next();
            aidsToTrash.add(aid);
        }
        for (AID anAidsToTrash : aidsToTrash) {
            clearApplet(anAidsToTrash);
        }

        loadFiles.clear();
        generatedLoadFileAIDs.clear();
        Arrays.fill(responseBuffer, (byte) 0);
        transactionDepth = 0;
        responseBufferSize = 0;
        currentAID = null;
        previousAID = null;
        
        transientMemory.forgetBuffers();
    }
    
    @Override
    public byte[] transmitCommand(byte[] command) throws SystemException {
        try {
            return super.transmitCommand(command);
        } finally {
            Applet applet = getApplet(getAID());
            if(appletsDir != null && applet != null) {
                updateAppletFile(getAID(), applet);
            }
        }
    }
    
    protected void clearApplet(AID aid) {
        activateSimulatorRuntimeInstance();
        ApplicationInstance applicationInstance = lookupApplet(aid);
        if (applicationInstance == null) {
            throw new SystemException(SystemException.ILLEGAL_AID);
        }

        applets.remove(aid);
        Applet applet = applicationInstance.getApplet();
        if (applet == null) {
            return;
        }

        if (getApplet(currentAID) == applet) {
            deselect(applicationInstance);
        }
    }
        
    @Override
    protected void deleteApplet(AID aid) {
        super.deleteApplet(aid);
        if(appletsDir != null) {
            File appletFile = new File(appletsDir, AIDUtil.toString(aid));
            appletFile.delete();
        }
    }
    
    @Override
    protected void deselect(ApplicationInstance applicationInstance) {
        try {
            super.deselect(applicationInstance);
        } finally {
            if((appletsDir != null) && (applicationInstance != null)) {
                Applet applet = applicationInstance.getApplet();
                AID aid = applicationInstance.getAID();
                updateAppletFile(aid, applet);
            }
        }
    }
            
    private void updateAppletFile(AID aid, Applet applet) {
        try {
            File appletInstanceFile = new File(appletsDir, AIDUtil.toString(aid));
            if(!appletInstanceFile.isFile())
                throw new RuntimeException("Path " + appletInstanceFile.getAbsolutePath() + " doesn't exist");
            try (Output output = new Output(new FileOutputStream(appletInstanceFile))) {
                kryo.writeClassAndObject(output, applet);
            }
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private void updateAppletFiles() {
        for(ApplicationInstance appInst : applets.values()) {
            Applet applet = appInst.getApplet();
            AID aid = appInst.getAID();
            updateAppletFile(aid, applet);
        }
    }
}