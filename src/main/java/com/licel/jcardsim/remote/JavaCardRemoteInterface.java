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

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RMI Wrapper for the <code>JavaCardInterface</code>
 * 
 * @author LICEL LLC
 */
public interface JavaCardRemoteInterface extends Remote {
    
    public String RMI_SERVER_ID = "jCardSim.rmiServer";

    public SerializableAID loadApplet(SerializableAID aid, String appletClassName) throws RemoteException;

    public SerializableAID loadApplet(SerializableAID aid, String appletClassName, byte[] appletJarContents) throws RemoteException;

    public SerializableAID createApplet(SerializableAID aid, byte[] bArray, short bOffset, byte bLength) throws RemoteException;
    
    public byte[] transmitCommand(byte[] data) throws RemoteException;

    public boolean selectApplet(SerializableAID aid) throws RemoteException;

    public byte[] selectAppletWithResult(SerializableAID aid) throws RemoteException;
    
    public void reset() throws RemoteException;

    public byte[] getATR() throws RemoteException;
}
