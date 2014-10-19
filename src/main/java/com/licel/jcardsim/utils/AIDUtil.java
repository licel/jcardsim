package com.licel.jcardsim.utils;

import javacard.framework.AID;
import org.bouncycastle.util.encoders.Hex;

import java.util.Comparator;

/**
 * Utility methods for dealing AIDs
 */
public final class AIDUtil {
    private static final Comparator<AID> aidComparator = new Comparator<AID>() {
        public int compare(AID aid1, AID aid2) {
            String s1 = (aid1 != null) ? AIDUtil.toString(aid1) : "";
            String s2 = (aid1 != null) ? AIDUtil.toString(aid2) : "";
            return s1.compareTo(s2);
        }
    };

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
     * Convert AID to hex-string
     * @param aid AID to convert
     * @return hex string
     * @throws java.lang.NullPointerException if <code>aid</code> is null
     */
    public static String toString(AID aid) {
        if (aid == null) {
            throw new NullPointerException("aid");
        }
        byte[] buffer = new byte[16];
        short len = aid.getBytes(buffer, (short) 0);
        return ByteUtil.hexString(buffer, 0, len);
    }

    /**
     * @return a Comparator for AIDs
     */
    public static Comparator<AID> comparator() {
        return aidComparator;
    }

    private AIDUtil() {}
}
