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

import com.licel.jcardsim.base.CardManager;
import com.licel.jcardsim.base.Simulator;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * BixVReader Card Implementation.
 * 
 * @author LICEL LLC
 */
public class BixVReaderCard {
    
    Simulator sim;
        
    public BixVReaderCard(int idx) throws IOException {
        BixVReaderIPCProtocol driverProtocol = new BixVReaderIPCProtocol();
        driverProtocol.connect(idx);
        startThread(driverProtocol);
    }
    
    public BixVReaderCard(String host, int port, int event_port) throws IOException {
        BixVReaderTCPProtocol driverProtocol = new BixVReaderTCPProtocol();
        driverProtocol.connect(host, port, event_port);
        startThread(driverProtocol);
    }
    
    static public void main(String args[]) throws Exception {
        if (args.length !=1) {
            System.out.println("Usage: java com.licel.jcardsim.remote.BixVReaderCard <jcardsim.cfg>");
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
        
        String host = System.getProperty("com.licel.jcardsim.bixvreader.host");
        
        if (host != null) {
            String propKey = "com.licel.jcardsim.bixvreader.port";
            String port = System.getProperty(propKey);
            
            if(port == null) {
                throw new InvalidParameterException("Missing value for property: " + propKey);
            }
            
            propKey = "com.licel.jcardsim.bixvreader.eport";
            String eventPort = System.getProperty(propKey);
            
            if(eventPort == null) {
                throw new InvalidParameterException("Missing value for property: " + propKey);
            }

            BixVReaderCard server = new BixVReaderCard(host, Integer.parseInt(port), Integer.parseInt(eventPort));
        } else {
            int readerIdx = Integer.parseInt(System.getProperty("com.licel.jcardsim.bixvreader.idx", "0"));
            BixVReaderCard server = new BixVReaderCard(readerIdx);
        }
    }

    private void startThread(BixVReaderProtocol driverProtocol) throws IOException {
        sim = new Simulator();
        final IOThread ioThread = new IOThread(sim, driverProtocol);
        ShutDownHook hook = new ShutDownHook(ioThread);
        Runtime.getRuntime().addShutdownHook(hook);
        ioThread.start();
        driverProtocol.writeEventCommand(BixVReaderProtocol.CARD_INSERTED);
    }
    
     static class ShutDownHook extends Thread {

        IOThread ioThread;

        public ShutDownHook(IOThread ioThread) {
            this.ioThread = ioThread;
        }

         public void run() {
             ioThread.isRunning = false;
             System.out.println("Shutdown connections");
             try {
                 ioThread.driverProtocol.writeEventCommand(BixVReaderIPCProtocol.CARD_REMOVED);
             } catch (Exception ignored) {
             }
             ioThread.driverProtocol.disconnect();
         }
    }
    
    static class IOThread extends Thread {

        BixVReaderProtocol driverProtocol;
        Simulator sim;
        boolean isRunning;

        public IOThread(Simulator sim, BixVReaderProtocol driverProtocol) {
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
                        case 0:
                        case 1:
                            sim.reset();
                            driverProtocol.writeData(sim.getATR());
                            break;
                        case 2:
                            byte[] apdu = driverProtocol.readData();
                            driverProtocol.writeData(CardManager.dispatchApdu(sim, apdu));
                            break;
                    }
                } catch (Exception e) {}
            }
        }
    }

}
