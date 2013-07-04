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

package javacard.framework.service;

import java.rmi.Remote;

import javacard.framework.APDU;
import javacard.framework.ISO7816;
import com.licel.jcardsim.base.SimulatorSystem;

public class RMIService extends BasicService implements RemoteService {

    public static final byte DEFAULT_RMI_INVOKE_INSTRUCTION = (byte) 0x38;

    private byte _invokeInstructionByte = DEFAULT_RMI_INVOKE_INSTRUCTION;

    private Remote _remoteObject;

    public RMIService(Remote initialObject) throws NullPointerException {
        if (initialObject == null)
            throw SimulatorSystem.nullPointerException;
        _remoteObject = initialObject;
    }

    public void setInvokeInstructionByte(byte ins) {
        _invokeInstructionByte = ins;
    }

    public boolean processCommand(APDU apdu) {
        byte ins = getINS(apdu);
        if (ins == _invokeInstructionByte || ins == ISO7816.INS_SELECT)
            return true;
        return false;
    }
}
