package com.licel.jcardsim.remote;

import com.licel.jcardsim.utils.AIDUtil;
import javacard.framework.AID;
import junit.framework.TestCase;
import org.bouncycastle.util.Arrays;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class JavaCardRemoteServerTest extends TestCase {
    private static final String TEST_APPLET_AID = "010203040506070809";
    private static final String TEST_APPLET_CLASS = "com.licel.jcardsim.samples.HelloWorldApplet";

    public JavaCardRemoteServerTest(String name) {
        super(name);
    }

    public void testServer() throws RemoteException, NotBoundException, InterruptedException {
        System.out.println("testServer ...");
        System.setProperty("com.licel.jcardsim.card.applet.0.AID", TEST_APPLET_AID);
        System.setProperty("com.licel.jcardsim.card.applet.0.Class", TEST_APPLET_CLASS);

        String host = "127.0.0.1";
        int port = 7777;
        new JavaCardRemoteServer(host, port);
        JavaCardRemoteClient client = new JavaCardRemoteClient(host, port);

        final AID aid = AIDUtil.create(TEST_APPLET_AID);
        client.createApplet(aid, new byte[0], (short) 0, (byte) 0);

        assertEquals(true, client.selectApplet(aid));
        // test NOP
        byte[] response = client.transmitCommand(new byte[]{0x01, 0x02, 0x00, 0x00});
        assertEquals(Arrays.areEqual(new byte[]{(byte) 0x90, 0x00}, response), true);
        System.out.println("testServer ... done");
    }

    @Override
    protected void tearDown() throws Exception {
        System.clearProperty("com.licel.jcardsim.card.applet.0.AID");
        System.clearProperty("com.licel.jcardsim.card.applet.0.Class");
    }
}
