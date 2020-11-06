/*
 * Copyright 2020 Licel Corporation.
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

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class VSmartCardReloader {
    static public void main(String args[]) throws Exception {
        if (args.length !=3) {
            System.out.println("Usage: java com.licel.jcardsim.remote.VSmartCardReloader <host> <port> <jcardsim.cfg>");
            System.exit(-1);
        }
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        File cfg = new File(args[2]);
        if(!cfg.isFile()) {
            System.out.println("Input config: " + cfg.getAbsolutePath() + " is not a file");
            System.exit(-1);
        }
        try (Socket socket = new Socket(hostname, port)) {
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            writer.println(cfg.getAbsolutePath());
        }
        System.out.println("Ok!");
    }
}
