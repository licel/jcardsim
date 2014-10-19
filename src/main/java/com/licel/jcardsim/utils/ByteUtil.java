package com.licel.jcardsim.utils;

import org.bouncycastle.util.encoders.Hex;

/**
 * Utility methods for dealing with byte arrays
 */
public final class ByteUtil {
    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    /**
     * Create byte array from hex string
     * @param hexString hex string
     * @return new byte array
     * @throws java.lang.NullPointerException if <code>hexString</code> is null
     */
    public static byte[] byteArray(String hexString) {
        if (hexString == null) {
            throw new NullPointerException("hexArray");
        }
        return Hex.decode(hexString);
    }

    /**
     * Convert byte array into hex string
     * @param bytes hex string
     * @return hexString
     * @throws java.lang.NullPointerException if <code>bytes</code> is null
     */
    public static String hexString(byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException("bytes");
        }
        return hexString(bytes, 0, bytes.length);
    }

    /**
     * Convert byte array into hex string
     * @param bytes hex string
     * @param offset offset
     * @param length length
     * @return hexString
     * @throws java.lang.NullPointerException if <code>bytes</code> is null
     */
    public static String hexString(byte[] bytes, int offset, int length) {
        // http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java
        if (bytes == null) {
            throw new NullPointerException("bytes");
        }
        char[] hexChars = new char[length * 2];
        for ( int j = offset; j < (offset + length); j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Extract status word from APDU
     * @param apduBuffer apdu bytes
     * @return status word
     * @throws java.lang.NullPointerException if <code>apduBuffer</code> is null
     * @throws java.lang.IllegalArgumentException if <code>apduBuffer.length</code>  is &lt; 2
     */
    public static short getSW(byte[] apduBuffer) {
        if (apduBuffer == null) {
            throw new NullPointerException("bytes");
        }
        if (apduBuffer.length < 2) {
            throw new IllegalArgumentException("bytes.length must be at least 2");
        }
        return getShort(apduBuffer, apduBuffer.length - 2);
    }

    /**
     * Read short from array
     * @see javacard.framework.Util#getShort(byte[], short)
     * @param bArray byte array
     * @param offset offset
     * @return short value
     */
    public static short getShort(byte[] bArray, int offset) {
        return (short) (((short) bArray[offset] << 8) + ((short) bArray[offset + 1] & 0xff));
    }

    private ByteUtil() {}
}
