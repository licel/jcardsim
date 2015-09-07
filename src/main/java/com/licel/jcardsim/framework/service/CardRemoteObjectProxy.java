/*
 * Copyright 2015 Licel Corporation.
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
package com.licel.jcardsim.framework.service;

import java.rmi.Remote;

import com.licel.jcardsim.base.SimulatorRuntime;
import com.licel.jcardsim.base.SimulatorSystem;

/**
 * Proxy for <code>CardRemoteObjectProxy</code>
 * @see CardRemoteObjectProxy
 */
public class CardRemoteObjectProxy implements Remote {

    public CardRemoteObjectProxy() {
        export(this);
    }

    public static void export(Remote obj) throws SecurityException {
        SimulatorRuntime runtime = SimulatorSystem.instance();

        if (runtime.getJavaContext(runtime.getJavaOwner(obj)) !=
            runtime.getJavaContext(runtime.getJavaOwner(runtime.getPreviousActiveObject())))
            throw new SecurityException();
    }

    public static void unexport(Remote obj) throws SecurityException {
        SimulatorRuntime runtime = SimulatorSystem.instance();

        if (runtime.getJavaContext(runtime.getJavaOwner(obj)) !=
            runtime.getJavaContext(runtime.getJavaOwner(runtime.getPreviousActiveObject())))
            throw new SecurityException();
    }
}
