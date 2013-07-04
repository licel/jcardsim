/*
 * Copyright © 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)JavaLoyaltyInterface.java	1.8 03/06/06
 */

package com.licel.jcardsim.samples.SampleLibrary;

import javacard.framework.*;

/**
 * Shareable Loyalty Interface
 *
 * @author Vadim Temkin
 */
public interface JavaLoyaltyInterface extends Shareable  {
  /**
   * Used to ask JavaLoyalty Card applet to grant points. <p>
   * Only primitive values, global arrays and Shareable Interface Objects should
   * be passed as parameters and results across a context switch. The byte array
   * buffer is APDU buffer in classes implementing this interface.
   * See <em>Java Card Runtime Environment (JCRE) Specification</em> for details.
   * <p> The format of data in the buffer is subset of Transaction Log record format:
   * 2 bytes of 0, 1 byte of transaction type, 2 bytes amount of transaction,
   * 4 bytes of CAD ID, 3 bytes of date, and 2 bytes of time.
   * @param buffer Apdu buffer containing transaction data.
   */
  void grantPoints (byte[] buffer);
}

