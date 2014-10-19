/*
 * Copyright 2013 Licel LLC.
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
package com.licel.jcardsim.remote;

import com.licel.jcardsim.base.Simulator;
import java.io.FileInputStream;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Enumeration;
import java.util.Properties;

/**
 * RMI Server
 * @author LICEL LLC
 */
public class JavaCardRemoteServer extends java.rmi.server.UnicastRemoteObject
        implements JavaCardRemoteInterface {
    
    Simulator sim;

    public JavaCardRemoteServer(String host, int port) throws RemoteException {
        System.setProperty("java.rmi.server.hostname", host);
        Registry registry = LocateRegistry.createRegistry(port);
        registry.rebind(RMI_SERVER_ID, this);
        sim = new Simulator();
    }

    static public void main(String args[]) throws Exception {
        if (args.length !=1) {
            System.out.println("Usage: java com.licel.jcardsim.remote.JavaCardRemoteServer <jcardsim.cfg>");
            System.exit(-1);
        }
        Properties cfg = new Properties();
        // init Simulator
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(args[0]);
            cfg.load(fis);
        } catch (Throwable t) {
            System.err.println("Unable to load configuration " + args[0] + " due to: " + t.getMessage());
            System.exit(-1);
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
        
        Enumeration keys = cfg.propertyNames();
        while(keys.hasMoreElements()) {
            String propertyName = (String) keys.nextElement();
            System.setProperty(propertyName, cfg.getProperty(propertyName));
        }
        
        String serverHost = System.getProperty("com.licel.jcardsim.terminal.host");
        if(serverHost == null) {
            System.err.println("Invalid configuration: missing 'com.licel.jcardsim.terminal.host' property");
            System.exit(-1);
        }
        String serverPort = System.getProperty("com.licel.jcardsim.terminal.port");
        if(serverPort == null) {
            System.err.println("Invalid configuration: missing 'com.licel.jcardsim.terminal.port' property");
            System.exit(-1);
        }
        JavaCardRemoteServer server = new JavaCardRemoteServer(serverHost, Integer.parseInt(serverPort));
    }

    /**
     * Implementation
     */
    public SerializableAID loadApplet(SerializableAID aid, String appletClassName) throws RemoteException {
        return new SerializableAID(sim.loadApplet(aid.getAID(), appletClassName));
    }

    /**
     * Implementation
     */
    public SerializableAID loadApplet(SerializableAID aid, String appletClassName, byte[] appletJarContents) throws RemoteException {
        return new SerializableAID(sim.loadApplet(aid.getAID(), appletClassName, appletJarContents));
    }
    
    /**
     * Implementation
     */
    public SerializableAID createApplet(SerializableAID aid, byte[] bArray, short bOffset, byte bLength) throws RemoteException {
        return new SerializableAID(sim.createApplet(aid.getAID(), bArray, bOffset, bLength));
    }

    /**
     * Implementation
     */
    public byte[] transmitCommand(byte[] data) throws RemoteException {
        return sim.transmitCommand(data);
    }

    /**
     * Implementation
     */
    public boolean selectApplet(SerializableAID aid) throws RemoteException {
        return sim.selectApplet(aid.getAID());
    }


    /**
     * Implementation
     */
    public void reset() throws RemoteException {
        sim.reset();
    }

    /**
     * Implementation
     */
    public byte[] getATR() throws RemoteException {
        return sim.getATR();
    }

    public byte[] selectAppletWithResult(SerializableAID aid) throws RemoteException {
        return sim.selectAppletWithResult(aid.getAID());
    }

}
