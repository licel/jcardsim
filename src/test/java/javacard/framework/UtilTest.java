package javacard.framework;

import junit.framework.TestCase;

public class UtilTest extends TestCase {
	/**
	 * 
	 */
	public void testArrayCompare1() {
		byte[] src = new byte[] {0x01};
		byte[] dest = new byte[] {0x02};
		byte res = Util.arrayCompare(src, (short)0, dest, (short)0, (short)1);
		assertEquals(-1, res);
	}
	/**
	 * 
	 */
	public void testArrayCompare2() {
		byte[] src = new byte[] {(byte)0xff};
		byte[] dest = new byte[] {0x01};
		byte res = Util.arrayCompare(src, (short)0, dest, (short)0, (short)1);
		assertEquals(1, res);
	}
}
