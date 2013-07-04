/*
 * Copyright © 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// /*
// Workfile:@(#)CyclicFile.java	1.8
// Version:1.8
// Date:06/06/03
// 
// Archive:  /Products/Europa/samples/com/sun/javacard/samples/JavaPurse/CyclicFile.java 
// Modified:06/06/03 17:05:59
// Original author: Zhiqun Chen
// */

package com.licel.jcardsim.samples.JavaPurse;

import javacard.framework.*;

/**
 * Class <code>CyclicFile</code> implements ISO 7816 CyclicFile.
 * This class always allocates one more record than the maximum
 * number of allowed records in CyclicFile. Method getNewLogRecord
 * returns a new record without affecting contents of CyclicFile.
 * Method updateNewLogRecord updates the internal record pointers,
 * so the new record becomes the current record(record number one)
 * and the oldest record becomes the next new record.
 *
 * @author Zhiqun Chen (zhiqun.chen@sun.com)
 */

class CyclicFile
{

    // number of allocated records in CyclicFile. This
    // value is one plus the maximum number of records.
    private byte num_record;

    // pointer to the current record (record number).
    private byte current_record = -1;

    // pointer to next new record.
    private byte next_record;

    // records in CyclicFile
    private Record[] records;

    /**
     *  Constructor
     *  @param max_record maximum number of record in CyclicFile
     *  @param record_length record length
     */
    CyclicFile(byte max_record, byte record_length) {

      // allocate one more record than the maximum number
      // of allowed records in CyclicFile
      num_record = ++max_record;

      records = new Record[num_record];

      for ( byte i = 0; i < num_record; i++) {
        records[i] = new Record(record_length);
      }
    }

    /**
     *  Get a record with the specified record number
     *  @param recordNum record number
     *  @return the specified record
     */
    byte[] getRecord(byte recordNum){

      // check if the record number is in the range of 1..maximum record number
      if ( ( recordNum < 1 ) || ( recordNum > (byte)(num_record - 1 ) ) )
         ISOException.throwIt(ISO7816.SW_RECORD_NOT_FOUND);

      if ((recordNum = (byte) (current_record - recordNum + 1)) < 0 )
        recordNum += num_record;

      return records[recordNum].record;
    }

	/**
	* Find the record.
	* @param firstByte  if non-0, the record's first byte must match this value; if 0, any value of the record's first byte matches.
	* @return the record number, or 0 if the record is not found
	*/

	byte findRecord(byte firstByte) {

		byte eRecNum = num_record--; //max record
		byte currentRecNumber = 1;
		byte thisRec[];

		while (true) {

			thisRec = getRecord(currentRecNumber);

			if ( firstByte == 0) {
				return currentRecNumber;
			}

			if ( thisRec[0] == firstByte) {
				return currentRecNumber;
			}

			if ( currentRecNumber == eRecNum )
				return 0;
			currentRecNumber++;
		}
	}

	/**
	*  Get a new record
	*  @return the new record
	*/
	byte[] getNewLogRecord() {

		byte[] record = records[next_record].record;
		return record;

	}

    /**
     *  Update internal record pointers, so the new record
     *  becomes the current record and the oldest
     *  record in CyclicFile becomes the next new record
     *  @see getNewLogRecord
     */
    void updateNewLogRecord() {

      // update current record pointer and next record pointer
      if (++current_record == num_record)
        current_record = 0;

      if (++next_record == num_record)
        next_record = 0;
    }

}
