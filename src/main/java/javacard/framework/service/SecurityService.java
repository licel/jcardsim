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

public interface SecurityService extends Service {

    public static final short PRINCIPAL_APP_PROVIDER = (short) 3;

    public static final short PRINCIPAL_CARD_ISSUER = (short) 2;

    public static final short PRINCIPAL_CARDHOLDER = (short) 1;

    public static final byte PROPERTY_INPUT_CONFIDENTIALITY = (byte) 1;

    public static final byte PROPERTY_INPUT_INTEGRITY = (byte) 2;

    public static final byte PROPERTY_OUTPUT_CONFIDENTIALITY = (byte) 4;

    public static final byte PROPERTY_OUTPUT_INTEGRITY = (byte) 8;

    public boolean isAuthenticated(short principal) throws ServiceException;

    public boolean isChannelSecure(byte properties) throws ServiceException;

    public boolean isCommandSecure(byte properties) throws ServiceException;
}
