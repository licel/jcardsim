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
import com.licel.jcardsim.base.SimulatorSystem;

public class CardRemoteObject implements Remote {

    public CardRemoteObject() {
        export(this);
    }

    public static void export(Remote obj) throws SecurityException {
        if (SimulatorSystem.getJavaContext(SimulatorSystem.getJavaOwner(obj)) != SimulatorSystem
                .getJavaContext(SimulatorSystem
                        .getJavaOwner(SimulatorSystem.previousActiveObject)))
            throw SimulatorSystem.securityException;
    }

    public static void unexport(Remote obj) throws SecurityException {
        if (SimulatorSystem.getJavaContext(SimulatorSystem.getJavaOwner(obj)) != SimulatorSystem
                .getJavaContext(SimulatorSystem
                        .getJavaOwner(SimulatorSystem.previousActiveObject)))
            throw SimulatorSystem.securityException;
    }
}
