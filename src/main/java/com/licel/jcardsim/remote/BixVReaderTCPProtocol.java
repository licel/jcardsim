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

public class BixVReaderTCPProtocol implements BixVReaderProtocol {
    private Socket           socket;
    private Socket           eventSocket;
    private DataInputStream  dataInput;
    private DataOutputStream dataOutput;
    private DataOutputStream eventOutput;

    public void connect(String host, int port, int event_port) throws IOException {
        socket = new Socket(host, port);

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException ignore) {}

        eventSocket = new Socket(host, event_port);
        dataInput   = new DataInputStream(socket.getInputStream());
        dataOutput  = new DataOutputStream(socket.getOutputStream());
        eventOutput = new DataOutputStream(eventSocket.getOutputStream());
    }

    public void disconnect() {
        closeSocket(socket);
        closeSocket(eventSocket);
    }

    public int readCommand() throws IOException {
        byte[] cmdBuf = new byte[4];

        readFully(cmdBuf, dataInput);

        int cmd = cmdFromBytes(cmdBuf);

        return cmd;
    }

    public byte[] readData() throws IOException {
        int    dataLen = readCommand();
        byte[] dataBuf = new byte[dataLen];

        readFully(dataBuf, dataInput);

        return dataBuf;
    }
    
    public void writeData(byte[] data) throws IOException {
        byte[] dataBuf = new byte[4 + data.length];
        byte[] dataLen = cmdToBytes(data.length);

        System.arraycopy(dataLen, 0, dataBuf, 0, 4);
        System.arraycopy(data, 0, dataBuf, 4, data.length);
        dataOutput.write(dataBuf);
    }

    public void writeDataCommand(int cmd) throws IOException {
        writeCommand(dataOutput, cmd);
    }

    public void writeEventCommand(int cmd) throws IOException {
        writeCommand(eventOutput, cmd);
    }
    
    private static String bytesToHex(byte[] bytes) {
        Formatter formatter = new Formatter();

        for (byte b : bytes) {
            formatter.format("%02X ", b);
        }

        return formatter.toString();
    }

    private void closeSocket(Socket sock) {
        try {
            sock.close();
        } catch (IOException ignored) {}
    }

    private int cmdFromBytes(byte[] cmd) {
        return cmd[0] & 0xFF | (cmd[1] & 0xFF) << 8 | (cmd[2] & 0xFF) << 16 | (cmd[3] & 0xFF) << 24;
    }

    private byte[] cmdToBytes(int cmd) {
        return new byte[] { (byte) (cmd), (byte) (cmd >>> 8), (byte) (cmd >>> 16), (byte) (cmd >>> 24) };
    }
    
    private void readFully(byte[] buf, InputStream stream) throws IOException {
        int len    = buf.length;
        int offset = 0;

        while (len > 0) {
            int retval = stream.read(buf, offset, len);

            if (retval < 0) {
                throw new IOException("Got negative number from socket");
            }

            len    -= retval;
            offset += retval;
        }
    }

    private void writeCommand(OutputStream stream, int cmd) throws IOException {
        byte[] buf = cmdToBytes(cmd);

        stream.write(buf, 0, buf.length);
    }
}
