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

import com.licel.jcardsim.io.JavaCardInterface;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javacard.framework.AID;
import javacard.framework.SystemException;

/**
 * RMI client
 *
 * @author LICEL LLC
 */
public class JavaCardRemoteClient implements JavaCardInterface {

    JavaCardRemoteInterface remote;

    public JavaCardRemoteClient(String serverHost, int serverIp) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(serverHost, serverIp);
        remote = (JavaCardRemoteInterface) (registry.lookup(JavaCardRemoteInterface.RMI_SERVER_ID));
    }

    public AID loadApplet(AID aid, String appletClassName) throws SystemException {
        try {
            return remote.loadApplet(new SerializableAID(aid), appletClassName).getAID();
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
        return null;
    }

    public AID loadApplet(AID aid, String appletClassName, byte[] appletJarContents) throws SystemException {
        try {
            return remote.loadApplet(new SerializableAID(aid), appletClassName, appletJarContents).getAID();
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
        return null;
    }

    public AID createApplet(AID aid, byte[] bArray, short bOffset, byte bLength) throws SystemException {
        try {
            return remote.createApplet(new SerializableAID(aid), bArray, bOffset, bLength).getAID();
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
        return null;
    }

    public AID installApplet(AID aid, String appletClassName, byte[] bArray, short bOffset, byte bLength) throws SystemException {
        return createApplet(loadApplet(aid, appletClassName), bArray, bOffset, bLength);
    }

    public AID installApplet(AID aid, String appletClassName, byte[] appletJarContents, byte[] bArray, short bOffset, byte bLength) throws SystemException {
        return createApplet(loadApplet(aid, appletClassName), bArray, bOffset, bLength);
    }

    public boolean selectApplet(AID aid) {
        try {
            return remote.selectApplet(new SerializableAID(aid));
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
        return false;
    }

    public byte[] getATR() {
        try {
            return remote.getATR();
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
        return null;
    }

    public byte[] transmitCommand(byte[] data) {
        try {
            return remote.transmitCommand(data);
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
        return null;
    }

    public void reset() {
        try {
            remote.reset();
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
    }

    private void handleRemoteException(RemoteException e) {
        if (e.getCause() instanceof SystemException) {
            throw (SystemException) e.getCause();
        } else {
            SystemException.throwIt(SystemException.NO_RESOURCE);
        }
    }

    public byte[] selectAppletWithResult(AID aid) {
        try {
            return remote.selectAppletWithResult(new SerializableAID(aid));
        } catch (RemoteException e) {
            handleRemoteException(e);
        }
        return null;
    }
}
