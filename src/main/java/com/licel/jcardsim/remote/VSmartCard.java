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
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Enumeration;
import java.util.Properties;
import java.net.SocketException;
import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * VSmartCard Card Implementation.
 *
 * @author alex@cooperi.net
 */
public class VSmartCard {

    Simulator sim;

    public VSmartCard(String host, int port) throws IOException {
        VSmartCardTCPProtocol driverProtocol = new VSmartCardTCPProtocol();
        driverProtocol.connect(host, port);
        startThread(driverProtocol, host, port);
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

    private void startThread(VSmartCardTCPProtocol driverProtocol, String host, int port) throws IOException {
        sim = new Simulator();
        final IOThread ioThread = new IOThread(sim, driverProtocol, host, port);
        ShutDownHook hook = new ShutDownHook(ioThread);
        Runtime.getRuntime().addShutdownHook(hook);
        ioThread.start();
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

    static class IOThread extends Thread implements SignalHandler {
        VSmartCardTCPProtocol driverProtocol;
        Simulator sim;
        boolean isRunning;
        String host;
        int port;
        boolean inserted;

        public IOThread(Simulator sim, VSmartCardTCPProtocol driverProtocol, String host, int port) {
            this.sim = sim;
            this.driverProtocol = driverProtocol;
            isRunning = true;
            this.host = host;
            this.port = port;
            inserted = true;
            Signal.handle(new Signal("USR2"), this);
        }

        @Override
        public void run() {
            while (isRunning) {
                try {
                    if (!inserted) {
                        synchronized(this) {
                            wait();
                        }
                    }
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
                } catch (SocketException e) {
                    if (!inserted)
                        sim.reset();
                } catch (Exception e) {}
            }
        }

        public void handle(Signal sig) {
            if (inserted) {
                inserted = false;
                driverProtocol.disconnect();
                System.out.println("Smartcard removed");
            } else {
                try {
                    driverProtocol.connect(host, port);
                    inserted = true;
                    synchronized(this) {
                        notify();
                    }
                    System.out.println("Smartcard inserted");
                } catch (Exception e) {}
            }
        }
    }

}
