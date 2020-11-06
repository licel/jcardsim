/*
 * Copyright 2018 Joyent, Inc
 * Copyright 2020 The University of Queensland
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

import com.licel.jcardsim.base.CardManager;
import com.licel.jcardsim.base.Simulator;
import static com.licel.jcardsim.base.Simulator.ATR_SYSTEM_PROPERTY;
import static com.licel.jcardsim.base.Simulator.DEFAULT_ATR;
import com.licel.jcardsim.base.SimulatorRuntime;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidParameterException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * VSmartCard Card Implementation.
 *
 * @author alex@cooperi.net
 */
public class VSmartCard {
    static final String RELOADER_PORT_PROPERTY = "com.licel.jcardsim.vsmartcard.reloader.port";
    static final String RELOADER_PORT_DEFAULT = "8099";
    static final String RELOADER_DELAY_PROPERTY = "com.licel.jcardsim.vsmartcard.reloader.delay";
    static final String RELOADER_DELAY_DEFAULT = "1000"; //milisec
    
    Simulator sim;
    ReloadThread reloader;
    
    public VSmartCard(String host, int port) throws IOException {
        VSmartCardTCPProtocol driverProtocol = new VSmartCardTCPProtocol();
        driverProtocol.connect(host, port);
        startThread(driverProtocol);
    }

    static public void main(String args[]) throws Exception {
        if (args.length !=1) {
            System.out.println("Usage: java com.licel.jcardsim.remote.VSmartCard <jcardsim.cfg>");
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
        
        System.setProperty(ATR_SYSTEM_PROPERTY, cfg.getProperty(ATR_SYSTEM_PROPERTY, DEFAULT_ATR));
        System.setProperty(RELOADER_PORT_PROPERTY, cfg.getProperty(RELOADER_PORT_PROPERTY, RELOADER_PORT_DEFAULT));
        System.setProperty(RELOADER_DELAY_PROPERTY, cfg.getProperty(RELOADER_DELAY_PROPERTY, RELOADER_DELAY_DEFAULT));
        
        final Enumeration<?> keys = cfg.propertyNames();
        while (keys.hasMoreElements()) {
            String propertyName = (String) keys.nextElement();
            System.setProperty(propertyName, cfg.getProperty(propertyName));
        }
        
        String propKey = "com.licel.jcardsim.vsmartcard.host";
        String host = System.getProperty(propKey);
        if (host == null) {
            throw new InvalidParameterException("Missing value for property: " + propKey);
        }

        propKey = "com.licel.jcardsim.vsmartcard.port";
        String port = System.getProperty(propKey);
        if (port == null) {
            throw new InvalidParameterException("Missing value for property: " + propKey);
        }

        new VSmartCard(host, Integer.parseInt(port));
    }

    private void startThread(VSmartCardTCPProtocol driverProtocol) throws IOException {
        System.out.println("Trying to load an instance of com.licel.globalplatform.GpSimulatorRuntime");
        SimulatorRuntime simRuntime;
        try {
            simRuntime = (SimulatorRuntime)Class.forName("com.licel.globalplatform.GpSimulatorRuntime").newInstance();
            System.out.println("Succesfully loaded the instance!");
        } catch (Throwable ex) {
            ex.printStackTrace(System.err);
            System.out.println("Failed to load the instance! Will use the default SimulatorRuntime");
            simRuntime = new SimulatorRuntime();
        }
        sim = new Simulator(simRuntime);
        
        final IOThread ioThread = new IOThread(sim, driverProtocol);
        ShutDownHook hook = new ShutDownHook(ioThread);
        Runtime.getRuntime().addShutdownHook(hook);
        ioThread.start();
        reloader = new ReloadThread(hook);
        reloader.start();
    }

    static class ShutDownHook extends Thread {
        IOThread ioThread;

        public ShutDownHook(IOThread ioThread) {
            this.ioThread = ioThread;
        }

        public void run() {
            ioThread.isRunning = false;
            System.out.println("Shutdown connections");
            ioThread.driverProtocol.disconnect();
        }
    }

    static class ReloadThread extends Thread {
        ShutDownHook hook;
        
        public ReloadThread(ShutDownHook hook) {
            this.hook = hook;
        }
        
        @Override
        public void run() {
            String port = System.getProperty(RELOADER_PORT_PROPERTY);
            String delay = System.getProperty(RELOADER_DELAY_PROPERTY);
            try {
                String newConfig;
                try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(port))) {
                    System.out.println("Start reloader server on port " + port);
                    try(Socket socket = serverSocket.accept()) {
                        InputStream input = socket.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                        newConfig = reader.readLine();
                        System.out.println("Got a new config: " + newConfig);
                    }                        
                }
                Runtime.getRuntime().removeShutdownHook(hook);
                hook.start();
                while(!hook.ioThread.driverProtocol.isClosed()) {
                    Thread.sleep(100);
                }
                System.out.println("Card remove delay: " + delay + "...");
                Thread.sleep(Integer.parseInt(delay));
                VSmartCard.main(new String[]{ newConfig });
            } catch(InterruptedException ignore) {
            } catch(Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    
    static class IOThread extends Thread {
        VSmartCardTCPProtocol driverProtocol;
        Simulator sim;
        boolean isRunning;

        public IOThread(Simulator sim, VSmartCardTCPProtocol driverProtocol) {
            this.sim = sim;
            this.driverProtocol = driverProtocol;
            isRunning = true;
        }

        @Override
        public void run() {
            while (isRunning) {
                try {
                    int cmd = driverProtocol.readCommand();
                    switch (cmd) {
                        case VSmartCardTCPProtocol.POWER_ON:
                        case VSmartCardTCPProtocol.RESET:
                            sim.reset();
                            break;
                        case VSmartCardTCPProtocol.GET_ATR:
                            driverProtocol.writeData(sim.getATR());
                            break;
                        case VSmartCardTCPProtocol.APDU:
                            final byte[] apdu = driverProtocol.readData();
                            final byte[] reply = CardManager.dispatchApdu(sim, apdu);
                            driverProtocol.writeData(reply);
                            break;
                    }
                } catch (Exception e) {}
            }
        }
    }

}
