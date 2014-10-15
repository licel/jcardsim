package com.licel.jcardsim.base;

import javacard.framework.ISO7816;
import javacard.framework.Util;

public enum ApduCase {
    Case1(false),
    Case2(false), Case2Extended(true),
    Case3(false), Case3Extended(true),
    Case4(false), Case4Extended(true);

    private final boolean extended;

    ApduCase(boolean extended) {
        this.extended = extended;
    }

    public boolean isExtended() {
        return extended;
    }

    public static ApduCase getCase(byte[] command) {
        if (command == null) {
            throw new NullPointerException("command");
        }
        if (command.length < 4) {
            throw new IllegalArgumentException("command: malformed APDU, length < 4");
        }
        if (command.length == 4) {
            return Case1; // case 1 (CLA, INS, P1, P2)
        }
        if (command.length == 5) {
            return Case2; // case 2 (CLA, INS, P1, P2, Le)
        }
        if (command.length == 7 && command[ISO7816.OFFSET_LC] == 0) {
            return Case2Extended; // case 2 (CLA, INS, P1, P2, Le)
        }
        if (command[ISO7816.OFFSET_LC] == 0) {
            int lc = Util.getShort(command, (short) (ISO7816.OFFSET_LC + 1));
            int offset = ISO7816.OFFSET_LC + 3;
            if (lc + offset == command.length) {
                return Case3Extended; // case 3 (CLA, INS, P1, P2, Lc, Data)
            } else if (lc + offset + 2 == command.length) {
                return Case4Extended; // case 4 (CLA, INS, P1, P2, Lc, Data, Le)
            } else {
                throw new IllegalArgumentException("Invalid extended C-APDU: Lc or Le is invalid");
            }
        } else {
            int lc = (command[ISO7816.OFFSET_LC] & 0xFF);
            int offset = ISO7816.OFFSET_LC + 1;
            if (lc + offset == command.length) {
                return Case3; // case 3 (CLA, INS, P1, P2, Lc, Data)
            } else if (lc + offset + 1 == command.length) {
                return Case4; // case 4 (CLA, INS, P1, P2, Lc, Data, Le)
            } else {
                throw new IllegalArgumentException("Invalid C-APDU: Lc or Le is invalid");
            }
        }
    }
}
