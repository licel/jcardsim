/*
 * Copyright 2012 Licel LLC.
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
package com.licel.jcardsim.smartcardio;

import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;

/**
 * CardTerminal implementation class.
 * @author LICEL LLC
 */
public class JCSTerminal extends CardTerminal {

    final static String NAME="jCardSim.Terminal";
    static JCSCard card = null;
            
    public String getName() {
        return NAME;
    }

    public Card connect(String string) throws CardException {
        if (card == null) {
            card = new JCSCard();
        }
        card.reset();
        return card;
    }

    /**
     * Always returns true
     */
    public boolean isCardPresent() throws CardException {
        return true;
    }

    /**
     * Immediately returns true
     */
    public boolean waitForCardPresent(long l) throws CardException {
        return true;
    }

    /**
     * Immediately returns true
     */
    public boolean waitForCardAbsent(long l) throws CardException {
        return false;
    }
}
