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
 * The Shareable interface serves to identify all shared objects.
 * Any object that needs to be shared through the applet firewall
 * must directly or indirectly implement this interface. Only those
 * methods specified in a shareable interface are available through
 * the firewall.
 *
 * Implementation classes can implement any number of shareable
 * interfaces and can extend other shareable implementation classes.
 *
 */
public class Shareable {

}
