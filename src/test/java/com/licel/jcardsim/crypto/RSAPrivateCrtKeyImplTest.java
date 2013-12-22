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
package com.licel.jcardsim.crypto;

import junit.framework.TestCase;
import org.bouncycastle.util.encoders.Hex;

/**
 * Test for <code>RSAPrivateCrtKeyImpl</code>
 * Test data from NXP JCOP31-36 JavaCard*
 */
public class RSAPrivateCrtKeyImplTest extends TestCase {
    // RSA CRT Private Key Components (2048-bit)

    final static String P = "DA2CBBE7572AE9441538EFE23943A6C72B564482E482D50954769920AD4A45179D2842327BF8271C592FA9D6DCCAE737B45BCA4A3B026CF5C32E6008284D8ABA3A1B249EF900CCF40746C599D9217EC3D1179DC35976AE05A50CF6F8AE23E15B8E0D4481D1D6035826AB7F43CECEB511699814BF2EFCF0EA37E230C4F586E6C7";
    final static String Q = "A5CDB97E613BDE20D7938C102DBB1D4F3B7BE88A1E7919329F7207F044B2A1560FB370C742A7E15C5BBF77AC2463AB9C00B7FF2B765E1EECF4ED5F2F5DDA8476BCF6F154286154C4CC9C51FDEBEAFE8630D299A03AE306B7179D83328A1D18301E2C66AA94A802EB4FF34291C43E2BA4A3642965B5322DBBDCF5D016718070D7";
    final static String DP = "AA732286557A87ED91EAFEB6ADE865A6DAFBB5E0D12849C9D53C26DBF9A6B99DFE41129EA06DBA1892B1032E8326DE478DD7DC8DEBD6344C3925C50EFA75C23945E628D12E566AC907DA49CCAD6F56BB236320F51AB2F6B8203FA5BF99451CEE4B67A02B1045F42CED6134B7441E08264769F2637F114C22A0704D8BC30A96C7";
    final static String DQ = "94B23E9437F1B011B6246E2DA845B25077A1757841420BB619C6F395A896A7187EB648037C2501788A13D9E8B92DE61EDF8A243F3F45C3E870F45D3426B0ADB1AB60B027F09AF1A2197A6C1214BD488CE2464D5EA4D12C199205423F561C27B027AF5C57C940307606DABC2DEC58715D92E76634FFDEC3A342D3F54DDB76FC3B";
    final static String PQ = "92CC66692E8D14EB93D0705EFA58276CD7A1A8B82C17544E8D181C3F987ED62E83355844D2E89380A17169614D8367709CB62A3DF9339BD537078AD1629D1B87DCA66E31809ADB78746633C28A6EA064B34EF750C27E32800ECE3C5F17524D35EE3CEAE8989745EAECD411D519210D4FCD699F4218A22249F76BE75A2F8160A0";

    public RSAPrivateCrtKeyImplTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of isInitialized method, of class RSAPrivateCrtKeyImpl.
     */
    public void testIsInitialized() {
        System.out.println("isInitialized");
        RSAPrivateCrtKeyImpl key = new RSAPrivateCrtKeyImpl((short)2048);
        short compLen = (short) Hex.decode(P).length;
        key.setP(Hex.decode(P), (short)0, compLen);
        key.setQ(Hex.decode(Q), (short)0, compLen);
        key.setDP1(Hex.decode(DP), (short)0, compLen);
        key.setDQ1(Hex.decode(DQ), (short)0, compLen);
        key.setPQ(Hex.decode(PQ), (short)0, compLen);
        assertEquals(true, key.isInitialized());
    }
}
