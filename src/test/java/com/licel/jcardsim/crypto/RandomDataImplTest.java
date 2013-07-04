/*
 * Copyright 2011 Licel LLC.
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

import javacard.framework.JCSystem;
import junit.framework.TestCase;

/**
 * Test for <code>RandomDataImpl</code>
 */
public class RandomDataImplTest extends TestCase {

    public RandomDataImplTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of generateData method, of class RandomDataImpl.
     */
    public void testGenerateData() {
        System.out.println("generateData");
        byte[] buffer = JCSystem.makeTransientByteArray((short) 8, JCSystem.CLEAR_ON_RESET);
        RandomDataImpl instance = new RandomDataImpl();
        instance.generateData(buffer, (short) 0, (short) buffer.length);
    }

    /**
     * Test of setSeed method, of class RandomDataImpl.
     */
    public void testSetSeed() {
        System.out.println("setSeed");
        byte[] buffer = JCSystem.makeTransientByteArray((short) 8, JCSystem.CLEAR_ON_RESET);
        RandomDataImpl instance = new RandomDataImpl();
        instance.setSeed(buffer, (short) 0, (short) buffer.length);
    }
}
