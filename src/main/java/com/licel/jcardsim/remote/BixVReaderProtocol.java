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

import java.io.IOException;

public interface BixVReaderProtocol {
    public final static int CARD_INSERTED = 1;
    public final static int ATR_REQUEST   = 1;
    public final static int TRANSMIT_DATA = 2;
    public final static int RESET         = 0;
    public final static int CARD_REMOVED  = 0;

    public void disconnect();

    public int readCommand() throws IOException;

    public byte[] readData() throws IOException;

    public void writeData(byte[] data) throws IOException;

    public void writeDataCommand(int cmd) throws IOException;

    public void writeEventCommand(int cmd) throws IOException;
}
