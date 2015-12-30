/*
 * Copyright 2015 Fidesmo AB.
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
package com.licel.jcardsim.base;

import javacard.framework.*;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;


class ShareableProxy implements InvocationHandler {

    SimulatorRuntime runtime;
    AID serverAID;
    Shareable shareable;


    protected ShareableProxy (SimulatorRuntime runtime, AID serverAID, Shareable shareable) {
        this.runtime = runtime;
        this.serverAID = serverAID;
        this.shareable = shareable;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        AID oldAID = runtime.previousAID;
        runtime.previousAID = runtime.currentAID;
        runtime.currentAID = serverAID;

        Object result = method.invoke(shareable, args);

        runtime.currentAID = runtime.previousAID;
        runtime.previousAID = oldAID;
        return result;
    }
}
