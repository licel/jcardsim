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

import javacardx.crypto.Cipher;
import junit.framework.TestCase;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CipherProxyTest extends TestCase {
    public CipherProxyTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // The deprecated cipher algorithm list is created because JavaCard 3.0.5 API uses only javadoc annotation @deprecated
    // And not use the Java annotation @Deprecated, which can be read by java.lang.reflect.Field
    // https://docs.oracle.com/javacard/3.0.5/api/javacardx/crypto/Cipher.html
    String[] CIPHER_DEPRECATED_ALG_JAVACARD_V3_0_5 = {
        "ALG_AES_BLOCK_192_CBC_NOPAD",
        "ALG_AES_BLOCK_192_ECB_NOPAD",
        "ALG_AES_BLOCK_256_CBC_NOPAD",
        "ALG_AES_BLOCK_256_ECB_NOPAD",
        "ALG_RSA_ISO14888",
        "ALG_RSA_ISO9796",
    };

    public void testSupportCipherForJavaCardv3_0_5() throws ClassNotFoundException {
        ArrayList<Field> cipher_alg_fields = new ArrayList<Field>();

        for(Field field : Class.forName("javacardx.crypto.Cipher").getDeclaredFields()){
            if( field.getName().startsWith("ALG_") ){
                List<String> deprecated_list = Arrays.asList(CIPHER_DEPRECATED_ALG_JAVACARD_V3_0_5);
                if( !deprecated_list.contains(field.getName()))
                    cipher_alg_fields.add(field);
            }
        }

        for( Field alg_field : cipher_alg_fields ) {
            try {
                Cipher engine = Cipher.getInstance(alg_field.getByte(null), false);
            }
            catch (Throwable ex){
                System.out.println("Cipher algorithm " + alg_field.getName() + " has not been implemented yet!!!");
            }
        }

    }
}
