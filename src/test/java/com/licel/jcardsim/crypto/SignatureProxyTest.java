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

import javacard.security.Signature;
import junit.framework.TestCase;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SignatureProxyTest extends TestCase {
    public SignatureProxyTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // The deprecated signature algorithm list is created because JavaCard 3.0.5 API uses only javadoc annotation @deprecated
    // And not use the Java annotation @Deprecated, which can be read by java.lang.reflect.Field
    // https://docs.oracle.com/javacard/3.0.5/api/javacard/security/Signature.html
    String[] SIGNATURE_DEPRECATED_ALG_JAVACARD_V3_0_5 = {
            "ALG_AES_MAC_192_NOPAD",
            "ALG_AES_MAC_256_NOPAD",
    };

    public void testSupportSignatureForJavaCardv3_0_5() throws ClassNotFoundException {

        ArrayList<Field> signature_alg_fields = new ArrayList<>();

        for(Field field : Class.forName("javacard.security.Signature").getDeclaredFields()){
            if( field.getName().startsWith("ALG_") ){
                List<String> deprecated_list = Arrays.asList(SIGNATURE_DEPRECATED_ALG_JAVACARD_V3_0_5);
                if( !deprecated_list.contains(field.getName()))
                    signature_alg_fields.add(field);
            }
        }

        for( Field alg_field : signature_alg_fields ) {
            try {
                Signature sig = Signature.getInstance(alg_field.getByte(null), false);
            }
            catch (Throwable ex){
                System.out.println("Signature algorithm " + alg_field.getName() + " has not been implemented yet!!!");
            }
        }

    }
}
