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
package com.licel.jcardsim.framework;

import javacard.framework.CardException;


/**
 * ProxyClass for <code>*Exception.throwIt(..)</code>
 */
public class JavaCardExceptionProxy extends CardException {

    public JavaCardExceptionProxy(short s) {
        super(s);
    }
    
    public static void throwIt(short reason)
            throws JavaCardExceptionProxy {
        throw new JavaCardExceptionProxy(reason);
    }
    
}
