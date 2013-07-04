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
 * The <code>MultiSelectable</code> interface identifies the implementing
 * Applet subclass as being capable of concurrent selections.
 * A multiselectable applet is a subclass of <code>javacard.framework.Applet</code>
 * which directly or indirectly implements this interface. All of the applets within an
 * applet package must be multiselectable. If they are not, then none of the applets
 * can be multiselectable.
 *
 * <p>
 * An instance of a multiselectable applet can be selected on one logical channel
 * while the same applet instance or another applet instance from within the same package
 * is active on another logical channel.</p>
 *
 * <p>The methods of this interface are invoked by the Java Card runtime environment only when:</p>
 *  <ul>
 *    <li> the same applet instance is still active on another logical channel, or </li>
 *    <li> another applet instance from the same package is still active on another logical
 *     channel.</li>
 *  </ul>
 * <p> See <em>Runtime Environment Specification for the Java Card Platform</em> for details.</p>
 */
public interface MultiSelectable
{
    /**
     * Called by the Java Card runtime environment to inform that this applet instance has been selected while
     * the same applet instance or another applet instance from the same package is
     * active on another logical channel.
     *
     * <p>
     * It is called either when the MANAGE CHANNEL APDU (open) command or
     * the SELECT APDU command is received and before the applet instance is selected.
     * SELECT APDU commands use instance AID bytes for applet
     * selection.
     * See <em>Runtime Environment Specification for the Java Card Platform</em>, section
     * 4.5 for details.<p>
     * A subclass of <code>Applet</code> should, within this method,
     * perform any initialization that may be required to
     * process APDU commands that may follow.
     * This method returns a boolean to indicate that it is ready to accept
     * incoming APDU commands via its <code>process()</code> method. If this method returns
     * false, it indicates to the Java Card runtime environment that this applet instance declines to be selected.
     * <p>Note:<ul>
     *   <li><em>The <code>javacard.framework.Applet.select(</code>) method is not
     *   called if this method is invoked.</em></li>
     *  </ul>
     * @param appInstStillActive boolean flag is <code>true</code> when the same applet
     * instance is already active on another logical channel and <code>false</code> otherwise
     */
    public abstract boolean select(boolean appInstAlreadyActive);

    /**
     * Called by the Java Card runtime environment to inform that this applet instance has been selected while
     * the same applet instance or another applet instance from the same package is
     * active on another logical channel.
     *
     * <p>It is called either when the MANAGE CHANNEL APDU (open) command or
     * the SELECT APDU command is received and before the applet instance is selected.
     * SELECT APDU commands use instance AID bytes for applet
     * selection.
     * See <em>Runtime Environment Specification for the Java Card Platform</em>, section
     * 4.5 for details.<p>
     * A subclass of <code>Applet</code> should, within this method,
     * perform any initialization that may be required to
     * process APDU commands that may follow.
     * This method returns a boolean to indicate that it is ready to accept
     * incoming APDU
     * commands via its <code>process()</code> method. If this method returns
     * false, it indicates to the Java Card runtime environment that this applet instance declines to be selected.
     * <p>Note:<ul>
     * <li><em>The <CODE>javacard.framework.Applet.select(</CODE>) method is not
     * called if this method is invoked.</em></li>
     * </ul>
     * @param appInstStillActive boolean flag is <code>true</code> when the same applet instance
     * is still active on another logical channel and <CODE>false</CODE> otherwise
     */
    public abstract void deselect(boolean appInstStillActive);
}
