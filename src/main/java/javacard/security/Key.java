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

package javacard.security;

/**
 * The <code>Key</code> interface is the base interface for all keys.
 * <p>
 * <p>A <code>Key</code> object sets its initialized state to true only when all the associated
 * <code>Key</code> object parameters have been set at least once since the time the initialized state was set to false.
 * <p>A newly created <code>Key</code> object sets its initialized state to false. Invocation of the
 * <code>clearKey()</code> method sets the initialized state to false. A key with transient key data
 * sets its initialized state to false on the associated clear events.
 *
 */
public interface Key {

    /**
     * Clears the key and sets its initialized state to false.
     */
    public abstract void clearKey();

    /**
     * Returns the key size in number of bits.
     * @return the key size in number of bits
     */
    public abstract short getSize();

    /**
     * Returns the key interface type.
     * @return the key interface type. Valid codes listed in TYPE.. constants
     * See <CODE>KeyBuilder.TYPE_DES_TRANSIENT_RESET</CODE>
     * <p>
     * @see KeyBuilder
     */
    public abstract byte getType();

    /**
     * Reports the initialized state of the key. Keys must be initialized before
     * being used.
     * <p>A <code>Key</code> object sets its initialized state to true only when all the associated
     * <code>Key</code> object parameters have been set at least once since the time the initialized state was set to false.
     * <p>A newly created <code>Key</code> object sets its initialized state to false. Invocation of the
     * <code>clearKey()</code> method sets the initialized state to false. A key with transient key data
     * sets its initialized state to false on the associated clear events.
     * @return <code>true</code> if the key has been initialized
     */
    public abstract boolean isInitialized();
}
