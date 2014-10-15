package com.licel.jcardsim.utils;

import javacard.framework.AID;
import org.bouncycastle.util.encoders.Hex;

import java.util.Comparator;

public final class AIDUtil {
    /**
     * Create an AID from a byte array
     * @param aidBytes AID bytes
     * @return aid
     * @throws java.lang.NullPointerException if <code>aidBytes</code> is null
     * @throws java.lang.IllegalArgumentException if <code>aidBytes.length</code> is incorrect
     */
    public static AID create(byte[] aidBytes) {
        if (aidBytes == null) {
            throw new NullPointerException("aidString");
        }
        if (aidBytes.length < 5 || aidBytes.length > 16) {
            throw new IllegalArgumentException("AID size must be between 5 and 16 but was " +  aidBytes.length);
        }
        return new AID(aidBytes, (short) 0, (byte) aidBytes.length);
    }

    /**
     * Create an AID from a byte array
     * @param aidString AID bytes as hex string
     * @return aid
     * @throws java.lang.NullPointerException if <code>aidString</code> is null
     * @throws java.lang.IllegalArgumentException if length is incorrect
     */
    public static AID create(String aidString) {
        if (aidString == null) {
            throw new NullPointerException("aidString");
        }
        return create(Hex.decode(aidString));
    }

    /**
     * @return a Comparator for AIDs
     */
    public static Comparator<AID> comparator() {
        return new Comparator<AID>() {
            public int compare(AID aid, AID aid2) {
                return aid.toString().compareTo(aid2.toString());
            }
        };
    }

    private AIDUtil() {}
}
