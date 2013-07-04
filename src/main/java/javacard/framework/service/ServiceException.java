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

package javacard.framework.service;

public class ServiceException extends javacard.framework.CardRuntimeException {

    public static final short CANNOT_ACCESS_IN_COMMAND = (short) 4;

    public static final short CANNOT_ACCESS_OUT_COMMAND = (short) 5;

    public static final short COMMAND_DATA_TOO_LONG = (short) 3;

    public static final short COMMAND_IS_FINISHED = (short) 6;

    public static final short DISPATCH_TABLE_FULL = (short) 2;

    public static final short ILLEGAL_PARAM = (short) 1;

    public static final short REMOTE_OBJECT_NOT_EXPORTED = (short) 7;

    private static ServiceException _systemInstance;

    public ServiceException(short reason) {
        super(reason);
        if (_systemInstance == null)
            _systemInstance = this;
    }

    public static void throwIt(short reason) throws ServiceException {
        _systemInstance.setReason(reason);
        throw _systemInstance;
    }

}
