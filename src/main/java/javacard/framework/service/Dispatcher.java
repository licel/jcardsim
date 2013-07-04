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

import javacard.framework.APDU;
import javacard.framework.ISOException;
import javacard.framework.JCSystem;
import javacard.framework.ISO7816;
import javacard.framework.Util;
import com.licel.jcardsim.base.SimulatorSystem;

public class Dispatcher {

    public static final byte PROCESS_COMMAND = (byte) 2;

    public static final byte PROCESS_INPUT_DATA = (byte) 1;

    public static final byte PROCESS_NONE = (byte) 0;

    public static final byte PROCESS_OUTPUT_DATA = (byte) 3;

    private short _maxServices;
    private Service[] _services;
    private byte[] _phases;

    public Dispatcher(short maxServices) throws ServiceException {
        _services = new Service[maxServices];
    SimulatorSystem.setJavaOwner(_services, this);
        _phases = new byte[maxServices];
    SimulatorSystem.setJavaOwner(_phases, this);
        _maxServices = maxServices;
    }

    public void addService(Service service, byte phase) throws ServiceException {

        if (phase <= PROCESS_NONE || phase > PROCESS_OUTPUT_DATA || service == null)
            ServiceException.throwIt(ServiceException.ILLEGAL_PARAM);

        short i = (short) 0;
        short index = _maxServices;

        while (i < _maxServices) {
            if (_services[i] == null && index == _maxServices)
                index = i;
            if (_services[i] == service && _phases[i] == phase) {
                return;
            }
            i++;
        }

        if (index == _maxServices)
            ServiceException.throwIt(ServiceException.DISPATCH_TABLE_FULL);

        final boolean doTrans = (JCSystem.getTransactionDepth() == (byte) 0);
        if (doTrans)
            JCSystem.beginTransaction();

        _services[index] = service;
        _phases[index] = phase;
        if (doTrans)
            JCSystem.commitTransaction();
    }

    public void removeService(Service service, byte phase)
            throws ServiceException {
        if (phase <= PROCESS_NONE || phase > PROCESS_OUTPUT_DATA
                || service == null)
            ServiceException.throwIt(ServiceException.ILLEGAL_PARAM);

        short i = (short) 0;
        while (i < _maxServices) {
            if (_services[i] == service && _phases[i] == phase)
                break;
            i++;
        }
        if (i != _maxServices) {
            final boolean doTrans = (JCSystem.getTransactionDepth() == (byte) 0);
            if (doTrans)
                JCSystem.beginTransaction();
            _services[i] = null;
            _phases[i] = (byte) 0;
            if (doTrans)
                JCSystem.commitTransaction();
        }
    }

    public Exception dispatch(APDU command, byte phase) throws ServiceException {
        if (phase <= PROCESS_NONE || phase > PROCESS_OUTPUT_DATA)
            ServiceException.throwIt(ServiceException.ILLEGAL_PARAM);
        Exception result = null;
        byte phases = phase;
        try {
            while (phases <= PROCESS_OUTPUT_DATA) {
                dispatchPhase(command, phases);
                phases++;
            }
        } catch (Exception e) {
            result = e;
        }
        return result;
    }

    private void dispatchPhase(APDU command, byte phase) {
        short service = (short) 0;
        while (service < _maxServices) {
            if (_services[service] == null || _phases[service] != phase)
                break;

            if (phase == PROCESS_INPUT_DATA) {
                if (_services[service].processDataIn(command))
                    return;
            } else {
                if (phase == PROCESS_COMMAND) {
                    if (_services[service].processCommand(command))
                        return;
                } else {
                    if (command.getCurrentState() == APDU.STATE_OUTGOING) {
                        if (_services[service].processDataOut(command)) {
                            short len = (short) (command.getBuffer()[ISO7816.OFFSET_LC] & 0xff);
                            command.setOutgoingLength(len);
                            command.sendBytes(ISO7816.OFFSET_CDATA, len);
                            ISOException.throwIt(Util.makeShort(command
                                    .getBuffer()[ISO7816.OFFSET_P1], command
                                    .getBuffer()[ISO7816.OFFSET_P2]));
                        }
                    } else {
                        return;
                    }

                }
            }
            service++;
        }
    }

    public void process(APDU command) throws ISOException {
        Exception e = dispatch(command, PROCESS_INPUT_DATA);
        if (e instanceof ISOException) {
            if (((ISOException) e).getReason() != ISO7816.SW_NO_ERROR)
                throw (ISOException) e;
        }
        if (e == null)
            ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
        ISOException.throwIt(ISO7816.SW_UNKNOWN);
    }
}
