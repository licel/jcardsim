/*
 * Copyright © 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// /*
// Workfile:@(#)Record.java	1.7
// Version:1.7
// Date:06/06/03
// 
// Archive:  /Products/Europa/samples/com/sun/javacard/samples/JavaPurse/Record.java 
// Modified:06/06/03 17:05:59
// Original author: Zhiqun Chen
// */

package com.licel.jcardsim.samples.JavaPurse;

/**
 * A Record.
 * <p>The main reason for this class is that Java Card doesn't support multidimensional
 * arrays, but supports array of objects
 */

class Record
{

	byte[] record;

	Record(byte[] data) {
      this.record = data;
    }

    Record(short size) {
      record = new byte[size];
    }

}

