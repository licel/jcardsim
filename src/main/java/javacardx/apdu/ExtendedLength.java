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
package javacardx.apdu;

/**
 * The ExtendedLength interface serves as a tagging interface to indicate that
 * the applet supports extended length APDU. If this interface is implemented by
 * the applet instance, the applet may receive and send up to 32767 bytes of
 * APDU data.
 * 
 * <p>The APDU command header in the APDU buffer will use the variable
 * length header defined in ISO7816-4 with a 3 byte Lc value when the Lc field
 * in the incoming APDU header is 3 bytes long. The incoming data in that case
 * will begin at APDU buffer offset 7. 
 * 
 * <p>See <i>Runtime Environment Specification for the Java Card Platform</i> for details.
 *
 * @since 2.2.2
 */
public interface ExtendedLength {
}