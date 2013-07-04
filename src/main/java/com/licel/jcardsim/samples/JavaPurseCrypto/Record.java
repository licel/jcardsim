/*
 * Copyright © 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// /*
// Workfile:@(#)Record.java	1.5
// Version:1.5
// Date:03/26/01
// 
// Archive:  /Products/Europa/samples/com/sun/javacard/samples/JavaPurse/Record.java 
// Modified:03/26/01 17:08:44
// Original author: Zhiqun Chen
// */

package com.licel.jcardsim.samples.JavaPurseCrypto;

/**
 * A Record.
 * <p>The main reason for this class is that Java Card doesn't support multidimensional
 * arrays, but supports array of objects
 */

public class Record
{

    byte[] record;

    public Record(byte[] data) {
      this.record = data;
    }

    public Record(short size) {
      record = new byte[size];
    }

}

