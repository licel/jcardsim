/*
 * Copyright 2017 Licel Corporation.
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



package com.licel.jcardsim.remote;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.Socket;

import java.util.Formatter;
import java.util.concurrent.TimeUnit;

public class VSmartCardTCPProtocol {
    private Socket           socket;
    private InputStream  dataInput;
    private OutputStream dataOutput;
    private int frameLen = -1;

    public static final int POWER_OFF = 0;
    public static final int POWER_ON = 1;
    public static final int RESET = 2;
    public static final int GET_ATR = 4;
    public static final int APDU = -1;

    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException ignore) {}

        dataInput   = socket.getInputStream();
        dataOutput  = socket.getOutputStream();
    }

    public void disconnect() {
        closeSocket(socket);
    }

    public int readCommand() throws IOException {
        final byte[] cmdBuf = new byte[3];
        read(cmdBuf, 0, 2, dataInput);
        final int len = ((cmdBuf[0] << 8) & 0xFF00) | (cmdBuf[1] & 0xFF);
        if (len == 1) {
            read(cmdBuf, 2, 1, dataInput);
            final int cmd = cmdBuf[2];
            return (cmd);
        }
        frameLen = len;
        return (APDU);
    }

    public byte[] readData() throws IOException {
        if (frameLen == -1) {
            throw new IOException("No APDU command waiting");
        }
        byte[] buf = new byte[frameLen];
        read(buf, dataInput);
        frameLen = -1;
        return buf;
    }

    public void writeData(byte[] data) throws IOException {
        byte[] buf = new byte[2 + data.length];
        buf[0] = (byte)(((data.length & 0xFF00) >> 8) & 0xFF);
        buf[1] = (byte)(data.length & 0xFF);
        System.arraycopy(data, 0, buf, 2, data.length);
        dataOutput.write(buf);
    }

    private void closeSocket(Socket sock) {
        try {
            sock.close();
        } catch (IOException ignored) {}
    }

    private void read(byte[] buf, InputStream stream) throws IOException {
        read(buf, 0, buf.length, stream);
    }

    private void read(byte[] buf, int offset, int len, InputStream stream) throws IOException {
        while (len > 0) {
            int retval = stream.read(buf, offset, len);

            if (retval < 0) {
                throw new IOException("Got negative number from socket");
            }

            len    -= retval;
            offset += retval;
        }
    }
}
