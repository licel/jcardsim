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

package javacard.framework;

/**
 * This interface represents a PIN. An implementation must maintain these internal values:
 * <ul>
 * <li>PIN value.
 * <li>Try limit - the maximum number of times an incorrect PIN can be
 *  presented before the PIN is blocked. When the PIN is blocked, it
 *  cannot be validated even on valid PIN presentation.
 * <li>Max PIN size - the maximum length of PIN allowed.
 * <li>Try counter - the remaining number of times an incorrect PIN
 *  presentation is permitted before the <code>PIN</code> becomes blocked.
 * <li>Validated flag - true if a valid PIN has been presented. This flag is
 *   reset on every card reset.
 * </ul>
 * This interface does not make any assumptions about where the data
 * for the PIN value comparison is stored.<p>
 *
 * An owner implementation of this interface must provide a way to initialize/update
 * the PIN value. The owner implementation of the interface must protect against attacks based
 * on program flow prediction. In addition, even if a transaction is in progress, update of
 * internal state such as the try counter, the validated flag, and the blocking state,
 * shall not participate in the transaction during PIN presentation.<p>
 *
 * A typical card global PIN usage will combine an instance of <code>OwnerPIN</code> class and a
 * a Proxy PIN interface which extends both the <code>PIN</code> and the <code>Shareable</code> interfaces
 * and re-declares the methods of the PIN interface.
 * The <code>OwnerPIN</code> instance would be manipulated only by the owner who has update privilege.
 * All others would access the global PIN functionality via the proxy PIN interface.
 *
 */
public interface PIN {

    /**
     * Returns the number of times remaining that an incorrect PIN can
     * be presented before the <code>PIN</code> is blocked.
     * @return the number of times remaining
     */
    public abstract byte getTriesRemaining();

    /**
     * Compares <code>pin</code> against the PIN value. If they match and the
     * <code>PIN</code> is not blocked, it sets the validated flag
     * and resets the try counter to its maximum. If it does not match,
     * it decrements the try counter and, if the counter has reached
     * zero, blocks the <code>PIN</code>. Even if a transaction is in progress, update of
     * internal state - the try counter, the validated flag, and the blocking state,
     * shall not participate in the transaction.
     * <p>
     * Note:<ul>
     * <li><em>If </em><code>NullPointerException</code><em> or </em><code>ArrayIndexOutOfBoundsException</code><em> is
     * thrown, the validated flag must be set to false, the try counter must be decremented
     * and, the <code>PIN</code> blocked if the counter reaches zero.</em>
     * <li><em>If </em><code>offset</code><em> or </em><code>length</code><em> parameter
     * is negative an </em><code>ArrayIndexOutOfBoundsException</code><em> exception is thrown.</em>
     * <li><em>If </em><code>offset+length</code><em> is greater than </em><code>pin.length</code><em>, the length
     * of the </em><code>pin</code><em> array, an </em><code>ArrayIndexOutOfBoundsException</code><em> exception is thrown.</em>
     * <li><em>If </em><code>pin</code><em> parameter is </em><code>null</code><em>
     * a </em><code>NullPointerException</code><em> exception is thrown.</em></ul>
     * @param pin the byte array containing the PIN value being checked
     * @param offset the starting offset in the <code>pin</code> array
     * @param length the length of <code>pin</code>
     * @return <code>true</code> if the PIN value matches; <code>false</code> otherwise
     * @throws ArrayIndexOutOfBoundsException if the check operation would cause access of data outside array bounds.
     * @throws NullPointerException if <code>pin</code> is <code>null</code>
     */
    public abstract boolean check(byte pin[], short offset, byte length)
            throws ArrayIndexOutOfBoundsException, NullPointerException;

    /**
     * Returns <code>true</code> if a valid PIN value has been presented since the last
     * card reset or last call to <code>reset()</code>.
     * @return <code>true</code> if validated; <code>false</code> otherwise
     */
    public abstract boolean isValidated();

    /**
     * If the validated flag is set, this method resets the validated flag and
     * resets the <code>PIN</code> try counter to the value of the <code>PIN</code> try limit.
     * If the validated flag is not set, this method does nothing.
     */
    public abstract void reset();
}
