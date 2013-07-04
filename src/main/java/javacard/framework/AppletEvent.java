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
 * The <code>AppletEvent</code> interface provides a callback interface for
 * the Java Card runtime environment to inform the applet about life cycle events.
 * An applet instance - subclass of <code>Applet</code> - should implement
 * this interface if it needs to be informed about supported life cycle events.
 *
 * <p>
 * See <em>Runtime Environment Specification for the Java Card Platform</em> for details.
 */
public interface AppletEvent
{
    /**
     * Called by the Java Card runtime environment to inform this applet instance that the Applet Deletion
     * Manager has been requested to delete it.
     */
    public abstract void uninstall();
}
