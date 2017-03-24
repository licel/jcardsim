/*
 * Copyright 2014 Licel LLC.
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Client protocol impl (BixVReader (IPC)).
 *
 * See <a href="http://www.codeproject.com/Articles/134010/An-UMDF-Driver-for-a-Virtual-Smart-Card-Reader"
 * >http://www.codeproject.com/Articles/134010/An-UMDF-Driver-for-a-Virtual-Smart-Card-Reader</a>
 *
 * @author LICEL LLC
 */
public class BixVReaderIPCProtocol implements BixVReaderProtocol {
    RandomAccessFile dataPipe = null;
    RandomAccessFile eventsPipe = null;

    public void connect(int readerIdx) throws FileNotFoundException {
        dataPipe = new RandomAccessFile("\\\\.\\pipe\\SCardSimulatorDriver" + readerIdx, "rws");
        eventsPipe = new RandomAccessFile("\\\\.\\pipe\\SCardSimulatorDriverEvents" + readerIdx, "rws");
    }

    public void disconnect() {
        closePipe(dataPipe);
        closePipe(eventsPipe);
    }

    public int readCommand() throws IOException {
        byte[] cmdBuf = new byte[4];
        dataPipe.readFully(cmdBuf);
        int cmd = cmdFromBytes(cmdBuf);
        return cmd;
    }

    public void writeDataCommand(int cmd) throws IOException {
        writeCommand(dataPipe, cmd);
    }

    public void writeEventCommand(int cmd) throws IOException {
        writeCommand(eventsPipe, cmd);
    }

    public byte[] readData() throws IOException {
        int dataLen = readCommand();
        byte[] dataBuf = new byte[dataLen];
        dataPipe.readFully(dataBuf);
        return dataBuf;
    }

    public void writeData(byte[] data) throws IOException {
        byte[] dataBuf = new byte[4 + data.length];
        byte[] dataLen = cmdToBytes(data.length);
        System.arraycopy(dataLen, 0, dataBuf, 0, 4);
        System.arraycopy(data, 0, dataBuf, 4, data.length);
        dataPipe.write(dataBuf);
    }

    private byte[] cmdToBytes(int cmd) {
        return new byte[]{
                    (byte) (cmd),
                    (byte) (cmd >>> 8),
                    (byte) (cmd >>> 16),
                    (byte) (cmd >>> 24)};
    }

    private int cmdFromBytes(byte[] cmd) {
        return cmd[0] & 0xFF
                | (cmd[1] & 0xFF) << 8
                | (cmd[2] & 0xFF) << 16
                | (cmd[3] & 0xFF) << 24;
    }

    private void writeCommand(RandomAccessFile pipe, int cmd) throws IOException {
        pipe.write(cmdToBytes(cmd));
    }

    private void closePipe(RandomAccessFile pipe) {
        try {
            pipe.close();
        } catch (IOException ignored) {
        }
    }
}
