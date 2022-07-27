/*
 * Copyright 2022 Licel Corporation.
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
package com.licel.jcardsim.crypto;

import javacard.security.MessageDigest;
import javacard.security.Signature;
import junit.framework.TestCase;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageDigestProxyTest extends TestCase {
    public MessageDigestProxyTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }


    public void testSupportMessageDigestForJavaCardv3_0_5() throws ClassNotFoundException {

        ArrayList<Field> md_alg_fields = new ArrayList<>();

        for(Field field : Class.forName("javacard.security.MessageDigest").getDeclaredFields()){
            if( field.getName().startsWith("ALG_") ){
                md_alg_fields.add(field);
            }
        }

        for( Field alg_field : md_alg_fields ) {
            try {
                MessageDigest md = MessageDigest.getInstance(alg_field.getByte(null), false);
            }
            catch (Throwable ex){
                System.out.println("Message Digest algorithm " + alg_field.getName() + " has not been implemented yet!!!");
            }
        }

    }
}
