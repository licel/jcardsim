/*
 * Copyright © 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * @(#)JavaPurse.java	1.14 03/06/06
 */

package com.licel.jcardsim.samples.JavaPurse;

import javacard.framework.*;
import com.licel.jcardsim.samples.SampleLibrary.JavaLoyaltyInterface;

/**
 * This class is intended to demonstrate how an electronic cash application
 * might be developed using Java Card
 * <p>See <em>Java Card(TM) Development Kit User's Guide</em> 
 * for details.
 *
 * @author Vadim Temkin
 */
public class JavaPurse extends Applet  {
  /*
   *
   * Constants
   *
   */
  final static byte  PURSE_CLA	= (byte)0x80;	//CLA value for Java Purse
  final static byte  VERIFY = (byte)0x20; //INS value for ISO 7816-4 VERIFY command
  final static byte  READ = (byte)0xB2;	//INS value for ISO 7816-4 READ RECORD command
  final static byte  INITIALIZE_TRANSACTION = (byte)0x20; //INS byte for Initialize Transaction
  final static byte  COMPLETE_TRANSACTION = (byte)0x22; //INS byte for Complete Transaction
  final static byte  INITIALIZE_UPDATE = (byte)0x24; //INS byte for Initialize Parameter Update
  final static byte  COMPLETE_UPDATE = (byte)0x26; //INS byte for Complete Parameter Update
  final static byte  CREDIT = (byte)0x01;	//P1 byte for Credit in Initialize Transaction
  final static byte  DEBIT = (byte)0x02;	//P1 byte for Debit in Initialize Transaction
  final static byte  MASTER_PIN = (byte)0x81; //P2 byte for Master PIN in Verify
  final static byte  USER_PIN = (byte)0x82; //P2 byte for User PIN in Verify
  
  final static short	SW_CREDIT_TOO_HIGH = (short)0x9101;//SW bytes for Credit Too High condition
  final static short	SW_NOT_ENOUGH_FUNDS = (short)0x9102;//SW bytes for Not Enough Funds	condition
  final static short	SW_AMOUNT_TOO_HIGH = (short)0x9103;//SW bytes for Amount Too High condition
  final static short	SW_COMMAND_OUT_OF_SEQUENCE = (short)0x9104;//SW bytes for Command out of Sequence
  final static short	SW_WRONG_SIGNATURE = (short)0x9105;//SW bytes for Wrong Signature condition
  final static short 	SW_PIN_FAILED = (short)0x69C0;//SW bytes for PIN Failed condition
  //The last nibble is replaced with the
  //number of remaining tries
  
  final static byte  	LC_IT	 	= 10;	//Lc byte for Initialize Transaction
  final static byte  	LC_CT	 	= 13;	//Lc byte for Complete Transaction
  final static byte  	LC_CU_MIN 	= 18;	//Lc byte for Complete Update
  final static byte 	CAD_ID_OFFSET   = 7;	//Offset for CAD ID in Process Initialize Transaction
  final static short	DATE_LENGTH = 3;	//Length of ExpDate array
  final static short	DATETIME_LENGTH = 5;//Length of DateTime array
  final static short	ID_LENGTH	= 4;	//Length for CAD ID and Purse ID arrays
  final static short	SHORT_LENGTH= 2;	//Length of a short value for offset computations
  final static short	START		= 0;	//For offset computations
  final static short	SIGNATURE_LENGTH
  = 8;	//Length of signatures
  final static short	MAX_LOYALTY = 4;	//Max number of loyalty applets
  
  // transientShorts array indices
  final static byte  	TN_IX = 0;
  final static byte  	NEW_BALANCE_IX=(byte)TN_IX+1;
  final static byte 	CURRENT_BALANCE_IX=(byte)NEW_BALANCE_IX+1;
  final static byte 	AMOUNT_IX=(byte)CURRENT_BALANCE_IX+1;
  final static byte   TRANSACTION_TYPE_IX=(byte)AMOUNT_IX+1;
  final static byte	SELECTED_FILE_IX=(byte)TRANSACTION_TYPE_IX+1;
  final static byte   NUM_TRANSIENT_SHORTS=(byte)SELECTED_FILE_IX+1;
  
  // transientBools array indices
  final static byte  	TRANSACTION_INITIALIZED=0;
  final static byte  	UPDATE_INITIALIZED=(byte)TRANSACTION_INITIALIZED+1;
  final static byte   NUM_TRANSIENT_BOOLS=(byte)UPDATE_INITIALIZED+1;
  
  // constants for response to applet SELECT command
  private final static byte FCI_TEMPLATE_TAG = (byte)0x6F;
  private final static byte FCI_AID_TAG = (byte)0x84;
  private static byte[] FCI_PROPERIETARY = 
  {(byte)0xA5,   // tag
   (byte)0x01,   // length
   (byte)0x42 }; // value 
  
  private ParametersFile 		parametersFile;
  private CyclicFile 			transactionLogFile;
  private short 	TN;
  private short 	PUN;
  private boolean isPersonalized; //set to true when Master PIN is updated first time
  /*
   * File System emulation constants
   */
  final static short PARAMETERS_FID =	(short) 0x9102;
  final static short TRANSACTION_LOG_FID = (short) 0x9103;
  final static short BALANCES_FID	= (short) 0x9104;
  final static byte  FID_BYTE = (byte) 0x91;
  final static byte  TRANSACTION_RECORD_LENGTH = 18;
  final static byte  TRANSACTION_RECORD_NUMBER = 10;
  final static byte  BALANCES_RECORD_LENGTH = 6;
  final static byte  BALANCES_RECORD_NUMBER = 1;
  final static byte  PARAMETERS_RECORD_NUMBER = 11;
  final static byte  OFFSET_BAL_CURRENT =	0;
  final static byte  OFFSET_BAL_MAX =	2;
	final static byte  OFFSET_AMOUNT_MAX = 4;
  final static byte  NUMBER_OF_FILES = 3;
  
  
  private OwnerPIN masterPIN;
  private OwnerPIN userPIN;
  
  /*
   * Tags for TLV records in Complete Parameter Update C-APDU.
   */
  final static byte MASTER_PIN_UPDATE 	= (byte)0xC1;
  final static byte USER_PIN_UPDATE 		= (byte)0xC2;
  final static byte EXP_DATE_UPDATE 		= (byte)0xC5;
  final static byte PURSE_ID_UPDATE 		= (byte)0xC6;
  final static byte MAX_BAL_UPDATE 		= (byte)0xC7;
  final static byte MAX_M_UPDATE 			= (byte)0xC8;
  final static byte VERSION_UPDATE 		= (byte)0xC9;
  final static byte LOYALTY1_UPDATE 		= (byte)0xCA;
  final static byte LOYALTY2_UPDATE 		= (byte)0xCB;
  final static byte LOYALTY3_UPDATE 		= (byte)0xCC;
  final static byte LOYALTY4_UPDATE 		= (byte)0xCD;
  
  final static short TLV_OFFSET = 13; //Offset of TLV in Complete Parameter Update
  
  
  /*
   *
   * Various and sundry byte arrays
   *
   */
  
  private byte[] CAD_ID_array;
  private byte[] byteArray8;
  private short[] transientShorts;
  private boolean[] transientBools;
  private byte[] ID_Purse;
  private byte[] ExpDate;
  private byte[] balancesRecord;
  
  /*
   * The two associated arrays to represent Loyalty Applets.
   * If value of loyaltyCAD matches first 2 bytes of CAD ID for transaction
   * the grantPoints method of corresponding loyalty Shareable Interface Object
   * is called. These arrays are populated by Parameter Update APDU commands.
   */
  private short[] loyaltyCAD;
  private JavaLoyaltyInterface[] loyaltySIO;
  
  /**
   * Installs Java Purse applet.
   * @param bArray install parameter array.
   * @param bOffset where install data begins.
   * @param bLength install parameter data length.
   */
  public static void install( byte[] bArray, short bOffset, byte bLength )
  {
    new JavaPurse(bArray, bOffset, bLength);
  }
  
  /**
   * Performs memory allocations, initialization, and applet registration.
   *
   * @param bArray received by install.
   * @param bOffset received by install.
   * @param bLength received by install.
   */
  protected JavaPurse(byte[] bArray, short bOffset, byte bLength)
  {
    ID_Purse = new byte[ID_LENGTH];
    ExpDate = new byte[DATE_LENGTH];
    ExpDate[0] = (byte)12; ExpDate[1] = (byte)31; ExpDate[2] = (byte)99;
    balancesRecord = new byte[BALANCES_RECORD_LENGTH];
    loyaltyCAD = new short[MAX_LOYALTY];
    loyaltySIO = new JavaLoyaltyInterface[MAX_LOYALTY];
    
    TN = 0;
    PUN = 0;
    isPersonalized = false;
    
    //Create transient objects.
    transientShorts = JCSystem.makeTransientShortArray( NUM_TRANSIENT_SHORTS,
		                                        JCSystem.CLEAR_ON_DESELECT);
    transientBools = JCSystem.makeTransientBooleanArray( NUM_TRANSIENT_BOOLS,
							 JCSystem.CLEAR_ON_DESELECT);
    
    CAD_ID_array = JCSystem.makeTransientByteArray( (short)4,
						    JCSystem.CLEAR_ON_DESELECT);
    byteArray8 = JCSystem.makeTransientByteArray( (short)8,
						  JCSystem.CLEAR_ON_DESELECT);
    
    masterPIN = new OwnerPIN ((byte)1, (byte)8);
    //There is only one try - it's not supposed to be done by human operator
    userPIN = new OwnerPIN ((byte)5, (byte)8);

    parametersFile = new ParametersFile(PARAMETERS_RECORD_NUMBER);
    
    transactionLogFile = new CyclicFile(TRANSACTION_RECORD_NUMBER,
					TRANSACTION_RECORD_LENGTH);
    Util.setShort(balancesRecord, OFFSET_BAL_CURRENT, (short)0);
    
    /*
     * if AID length is not zero register Java Loyalty
     * applet with specified AID
     *
     * NOTE: all the memory allocations should be performed before register()
     */
    
    byte aidLen = bArray[bOffset];
    if (aidLen== (byte)0){
      register();
    } else {
      register(bArray, (short)(bOffset+1), aidLen);
    }
    
  }
  
  
  /**
   * Performs the session finalization.
   */
  public void deselect()
  {
    userPIN.reset();
    masterPIN.reset();
  }
  
  
  /**
   * Dispatches APDU commands.
   * @param apdu APDU object
   */
  public void process(APDU apdu)
  {
    byte[] buffer = apdu.getBuffer();
    
    // Mask channel info out
    buffer[ISO7816.OFFSET_CLA] = (byte)(buffer[ISO7816.OFFSET_CLA] & (byte)0xFC);
    
    if (buffer[ISO7816.OFFSET_CLA] == PURSE_CLA) {
      switch (buffer[ISO7816.OFFSET_INS])	{
      case INITIALIZE_TRANSACTION: processInitializeTransaction(apdu); break;
      case COMPLETE_TRANSACTION:	 processCompleteTransaction(apdu); break;
      case INITIALIZE_UPDATE:		 processInitializeUpdate(apdu);	break;
      case COMPLETE_UPDATE:		 processCompleteUpdate(apdu); break;
      default:
	ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
      }
    } else
      if (buffer[ISO7816.OFFSET_CLA] == ISO7816.CLA_ISO7816) {
	if (buffer[ISO7816.OFFSET_INS] == VERIFY){
	  processVerifyPIN(apdu);
	} else
	  if (buffer[ISO7816.OFFSET_INS] == ISO7816.INS_SELECT){
	    if (selectingApplet())
	      processSelectPurse(apdu);
	    else processSelectFile(apdu);
	  } else
	    if (buffer[ISO7816.OFFSET_INS] == READ) {
	      processReadRecord(apdu);
	    } else ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
      } else ISOException.throwIt(ISO7816.SW_CLA_NOT_SUPPORTED);
  }
  /**
   * Handles Select Purse APDU.
   * @param apdu APDU object
   */
  private void processSelectPurse(APDU apdu)
  {
    //
    // There might be multiple instances of Java Purse on the card with
    // different AID values. This method returns the FCI here identifying its 
    // particular instance.
    //
    
    // Format of response data
    //  FCI tag, FCI length,
    //     AID tag, length, value,
    //     PROPRIETARY tag, value.
    //
    
    // TEMPLATE
    byte[] buffer = apdu.getBuffer();
    buffer[0] = FCI_TEMPLATE_TAG;
    // buffer[1] set later
    
    // AID
    buffer[2] = FCI_AID_TAG;
    buffer[3] = JCSystem.getAID().getBytes(buffer, (short)4);
    short offset=(short)(3+buffer[3]);
    
    // PROPRIETARY DATA 
    buffer[offset++] = (byte)FCI_PROPERIETARY.length; 
    offset = Util.arrayCopyNonAtomic(FCI_PROPERIETARY, (short)0,
				     buffer, offset,
				     (short)FCI_PROPERIETARY.length);
    
    // FCI template length
    buffer[1] = (byte)(offset-(short)2);
    
    apdu.setOutgoingAndSend((short)0, offset);
  }
  
  /**
   * Handles Initialize Transaction APDU.
   * <p>See <em>Java Card(TM) Development Kit User's Guide</em> for details.
   *
   * @param apdu APDU object
   */
  private void processInitializeTransaction(APDU apdu)
  {
    if (transientBools[TRANSACTION_INITIALIZED])
      ISOException.throwIt(SW_COMMAND_OUT_OF_SEQUENCE);
    if (!userPIN.isValidated())
      ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);
    byte[] buffer = apdu.getBuffer();
    if (buffer[ISO7816.OFFSET_LC] != LC_IT)
      ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
    if (buffer[ISO7816.OFFSET_P2] != 0)
      ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2);
    apdu.setIncomingAndReceive();    // get expected data
    byte transactionType = buffer[ISO7816.OFFSET_P1];
    transientShorts[TRANSACTION_TYPE_IX] = transactionType;
    short amount = Util.getShort(buffer, ISO7816.OFFSET_CDATA);
    transientShorts[AMOUNT_IX] = amount;
    
    short balance = checkTransactionValues(transactionType, amount);
    
    // Increment TN in Transient Memory & compute signature
    short newTN = (short)(TN + 1);
    transientShorts[TN_IX] = newTN;
    Util.arrayCopyNonAtomic(buffer, CAD_ID_OFFSET, CAD_ID_array, START, ID_LENGTH);
    
    // The crypto processing could be done here
    Util.arrayFillNonAtomic(byteArray8, (short)0, (short) byteArray8.length, (byte)0);
    
    // Send	R-APDU
    short offset = Util.arrayCopyNonAtomic(ID_Purse, START, buffer, START, ID_LENGTH);
    offset = Util.arrayCopyNonAtomic(ExpDate, START, buffer, offset, DATE_LENGTH);
    offset = Util.setShort(buffer, offset, balance);
    offset = Util.setShort(buffer, offset, newTN);
    offset = Util.arrayCopyNonAtomic(byteArray8, START, buffer, offset, SIGNATURE_LENGTH);
    
    apdu.setOutgoingAndSend(START, (short)(offset - START));
    transientBools[TRANSACTION_INITIALIZED] = true;
  }
  
  /**
   * Handles Complete Transaction APDU.
   * @param apdu APDU object
   */
  private void processCompleteTransaction(APDU apdu)
  {
    if (!transientBools[TRANSACTION_INITIALIZED])
      ISOException.throwIt(SW_COMMAND_OUT_OF_SEQUENCE);
    byte[] buffer = apdu.getBuffer();
    if (buffer[ISO7816.OFFSET_LC] != LC_CT)
      ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
    if ((buffer[ISO7816.OFFSET_P1] != 0) || (buffer[ISO7816.OFFSET_P2] != 0))
      ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2);
    
    apdu.setIncomingAndReceive();    // get expected data
    
    //restore transaction data from transient
    short newTN = transientShorts[TN_IX];
    short amount = transientShorts[AMOUNT_IX];
    short newBalance = transientShorts[NEW_BALANCE_IX];
    //The signature verification could be here
    Util.arrayFillNonAtomic(byteArray8, (short)0, (short) byteArray8.length,(byte)0);
    boolean signatureOK = (0 == Util.arrayCompare(buffer,
						  (short)ISO7816.OFFSET_CDATA,
						  byteArray8,
						  START,
						  SIGNATURE_LENGTH));
    
    //prepare transaction record in APDU buffer
    short offset = Util.setShort(buffer, START, newTN);
    buffer[offset] = (byte)transientShorts[TRANSACTION_TYPE_IX];
    offset++;
    offset = Util.setShort(buffer, offset, amount);
    //CAD ID was left in this array from Initialize Transaction
    offset = Util.arrayCopyNonAtomic(CAD_ID_array, START, buffer, offset, ID_LENGTH);
    
    //Date and time are copied in APDU buffer to where they should go
    //in the transaction record.
    short balanceOffset = offset =
      Util.arrayCopyNonAtomic(buffer, (short)(ISO7816.OFFSET_CDATA + 8), buffer,
			      offset, DATETIME_LENGTH);
    //Balance and SW will be added to transactionRecord	later
    if (!signatureOK){
      //Branch for unsuccessful transaction. Balance is not updated,
      //otherwise transactionLog is recorded the same way as in successful transaction
      offset = Util.setShort(buffer, offset, transientShorts[CURRENT_BALANCE_IX]); // old balance
      Util.setShort(buffer, offset, SW_WRONG_SIGNATURE);
      //done with preparing transaction record
      
      byte[] theRecord = transactionLogFile.getNewLogRecord();
      //The following	few	steps have to be performed atomically!
      JCSystem.beginTransaction();
      TN = newTN;
      Util.arrayCopy(buffer, START,
		     theRecord, START, TRANSACTION_RECORD_LENGTH);
      transactionLogFile.updateNewLogRecord();
      JCSystem.commitTransaction();
      
      //Now we can throw exception
      transientBools[TRANSACTION_INITIALIZED] = false;
      ISOException.throwIt(SW_WRONG_SIGNATURE);
    } else {
      //Branch for successful transaction.
      offset = Util.setShort(buffer, offset, transientShorts[NEW_BALANCE_IX]);
      Util.setShort(buffer, offset, ISO7816.SW_NO_ERROR);
      // done with preparing transaction record
      
      byte[] theRecord = transactionLogFile.getNewLogRecord();
      //The following few steps have to be performed atomically!
      JCSystem.beginTransaction();
      TN = transientShorts[TN_IX];
      //Update balance
      Util.setShort(balancesRecord, START, newBalance);
      Util.arrayCopy(buffer, START,
		     theRecord, START, TRANSACTION_RECORD_LENGTH);
      transactionLogFile.updateNewLogRecord();
      JCSystem.commitTransaction();
    }
    
    // Do loyalty work
    // We have all the information in the buffer, the loyalty applets shouldn't
    // know transaction number and the balance of purse - so we zero out these
    // fields first
    Util.setShort(buffer, START, (short)0);
    Util.setShort(buffer, balanceOffset, (short)0);
    short loyaltyCADValue = Util.getShort(CAD_ID_array, START);
    for (byte loyaltyIndex = 0; loyaltyIndex < MAX_LOYALTY; loyaltyIndex++)
      if (loyaltyCAD[loyaltyIndex] == loyaltyCADValue)
	{ loyaltySIO[loyaltyIndex].grantPoints (buffer);break;}
    
    //Put all '0's in signature3 (there could be crypto calculations here)
    Util.arrayFillNonAtomic(byteArray8, (short)0, (short) byteArray8.length, (byte)0);
    
    //send R-APDU
    offset = Util.setShort(buffer, START, newBalance);
    offset = Util.arrayCopyNonAtomic(byteArray8, START, buffer, offset, SIGNATURE_LENGTH);
    apdu.setOutgoingAndSend(START, (short)(offset - START));
    
    transientBools[TRANSACTION_INITIALIZED] = false;
  }
  
  
  /**
   * Handles Initialize Parameter Update APDU.
   *
   * <p><em>NOTE:</em> In this sample implementation we assume that all the
   * Parameter Updates are performed in somewhat secured facility and therefor
   * the tearing of the card is not an issue. That's why we don't do any
   * transactional protection while processing Initialize and Complete
   * Parameter Update APDU commands.
   *
   * @param apdu APDU object
   */
  private void processInitializeUpdate(APDU apdu)
  {
    if (transientBools[UPDATE_INITIALIZED])
      ISOException.throwIt(SW_COMMAND_OUT_OF_SEQUENCE);
    if (!masterPIN.isValidated() && isPersonalized)
      ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);
    
    byte[] buffer = apdu.getBuffer();
    if ((buffer[ISO7816.OFFSET_P1] != 0) || (buffer[ISO7816.OFFSET_P2] != 0))
      ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2);
    
    // Because this is a case 2 command (outgoing data only), the contents of P3
    //  are undefined. In T=0, P3 is Le. In T=1, P3 is Lc. Therefore, we don't
    //  bother to test the contents of buffer[ISO7816.OFFSET_LC].
    
    
    PUN++; //Increment parameter Update Number
    
    // Send R-APDU
    short offset = Util.arrayCopyNonAtomic(ID_Purse, START, buffer, START, ID_LENGTH);
    offset = Util.arrayCopyNonAtomic(ExpDate, START, buffer, offset, DATE_LENGTH);
    offset = Util.setShort(buffer, offset, PUN);
    apdu.setOutgoingAndSend(START, (short)(offset - START));
    
    transientBools[UPDATE_INITIALIZED] = true;
  }
  
  /**
   * Handles Complete Parameter Update APDU.
   * @param apdu APDU object
   */
  private void processCompleteUpdate(APDU apdu)
  {
    if (!transientBools[UPDATE_INITIALIZED])
      ISOException.throwIt(SW_COMMAND_OUT_OF_SEQUENCE);
    byte[] buffer = apdu.getBuffer();
    if ((buffer[ISO7816.OFFSET_P1] != 0) || (buffer[ISO7816.OFFSET_P2] != 0))
      ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2);
    short count = apdu.setIncomingAndReceive();    // get expected data
    byte lc = buffer[ISO7816.OFFSET_LC];
    //Put all '0's in byteArray8 and compare with buffer
    //(there could be crypto calculations here)
    Util.arrayFillNonAtomic(byteArray8, (short)0, (short) byteArray8.length, (byte)0);
    if (0 != Util.arrayCompare(byteArray8,
			       START,
			       buffer,
			       (short)(ISO7816.OFFSET_CDATA + lc - SIGNATURE_LENGTH),
			       SIGNATURE_LENGTH))
      ISOException.throwIt(SW_WRONG_SIGNATURE);
    switch (buffer[TLV_OFFSET]) {
    case MASTER_PIN_UPDATE:	updatePIN(apdu, masterPIN); setIsPersonalized(); break;
    case USER_PIN_UPDATE: 	updatePIN(apdu, userPIN); break;
    case EXP_DATE_UPDATE: 	updateParameterValue(apdu, ExpDate); break;
    case PURSE_ID_UPDATE: 	updateParameterValue(apdu, ID_Purse); break;
    case MAX_BAL_UPDATE: 	updateBalanceValue(apdu, OFFSET_BAL_MAX); break;
    case MAX_M_UPDATE: 		updateBalanceValue(apdu, OFFSET_AMOUNT_MAX); break;
    case VERSION_UPDATE: 	updateParametersFile(apdu); break;
    case LOYALTY1_UPDATE:	updateLoyaltyProgram(apdu, (byte)0); break;
    case LOYALTY2_UPDATE:	updateLoyaltyProgram(apdu, (byte)1); break;
    case LOYALTY3_UPDATE:	updateLoyaltyProgram(apdu, (byte)2); break;
    case LOYALTY4_UPDATE:	updateLoyaltyProgram(apdu, (byte)3); break;
    default: ISOException.throwIt(ISO7816.SW_FUNC_NOT_SUPPORTED);
    }
    
    // The crypto processing could be done here
    Util.arrayFillNonAtomic(byteArray8, (short)0, (short) byteArray8.length, (byte)0);
    Util.arrayCopyNonAtomic(byteArray8, START, buffer, START, SIGNATURE_LENGTH);
    apdu.setOutgoingAndSend(START, SIGNATURE_LENGTH);
    
    transientBools[UPDATE_INITIALIZED] = false;
    
  }
  
  /**
   * Handles Verify Pin APDU.
   * @param apdu APDU object
   */
  private void processVerifyPIN(APDU apdu)
  {
    byte[] buffer = apdu.getBuffer();
    byte pinLength = buffer[ISO7816.OFFSET_LC];
    byte triesRemaining	= (byte)0;
    short count = apdu.setIncomingAndReceive();    // get expected data
    if (count < pinLength) ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
    byte pinType = buffer[ISO7816.OFFSET_P2];
    switch (pinType) {
    case MASTER_PIN:
      if (!masterPIN.check(buffer, ISO7816.OFFSET_CDATA, pinLength)){
	triesRemaining = masterPIN.getTriesRemaining();
	//The last nibble of return	code is	number of remaining	tries
	ISOException.throwIt((short)(SW_PIN_FAILED + triesRemaining));
      }
      break;
    case USER_PIN:
      if (!userPIN.check(buffer, ISO7816.OFFSET_CDATA, pinLength)){
	triesRemaining = userPIN.getTriesRemaining();
	//The last nibble of return	code is	number of remaining	tries
	ISOException.throwIt((short)(SW_PIN_FAILED + triesRemaining));
      }
      break;
    default: ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2);
    }
  }
  /**
   * Verifies numerical limitations on Transaction Amount.
   *
   * <p><em>NOTE:</em> With some values of maxBalance and maxAmount the logic
   * in this method might become somewhat unrealistic. It's the result of using
   * short arithmetic on values which might be too big to fit in short variables.
   *
   * @param transactionType type of transaction.
   * @param amount transaction amount.
   * @return new balance
   */
  private short checkTransactionValues(byte transactionType, short amount)
  {
    short newBalance;
    short currentBalance = Util.getShort(balancesRecord, OFFSET_BAL_CURRENT);
    short maxBalance = Util.getShort(balancesRecord, OFFSET_BAL_MAX);
    short maxAmount = Util.getShort(balancesRecord, OFFSET_AMOUNT_MAX);
    switch (transactionType) {
    case CREDIT : {
      newBalance = (short)(currentBalance + amount);
      transientShorts[NEW_BALANCE_IX] = newBalance;
      if (newBalance > maxBalance || newBalance < 0) 	//to prevent rollover
	ISOException.throwIt(SW_CREDIT_TOO_HIGH);
      break;
    }
    case DEBIT : {
      if (amount > maxAmount) ISOException.throwIt(SW_AMOUNT_TOO_HIGH);
      newBalance = (short)(currentBalance - amount);
      transientShorts[NEW_BALANCE_IX] = newBalance;
      if (newBalance < 0)ISOException.throwIt(SW_NOT_ENOUGH_FUNDS);
      break;
    }
    default	: ISOException.throwIt(ISO7816.SW_INCORRECT_P1P2);
    }
    transientShorts[CURRENT_BALANCE_IX] = currentBalance;
    return currentBalance;
  }
  
  /**
   * Updates PIN.
   * @param apdu APDU object
   * @param PIN OwnerPIN object (masterPIN or userPIN)
   */
  
  private void updatePIN(APDU apdu, OwnerPIN PIN){
    byte[] buffer = apdu.getBuffer();
    PIN.update(buffer, (short)(TLV_OFFSET + 2),	buffer[TLV_OFFSET + 1]);
  }
  
  /**
   * Set JavaPurse in personalized state.
   * It happens only once - after the first update of masterPIN
   */
  
  private void setIsPersonalized() {
    if (!isPersonalized) isPersonalized = true;//happens only once
  }
  
  /**
   * Update value of a Expiration Date or ID_Purse. Also updates corresponding
   * records in Parameters File
   * @param apdu APDU object
   * @param value the byte array to be updated
   */
  
  private void updateParameterValue(APDU apdu, byte[] value){
    byte[] buffer = apdu.getBuffer();
    Util.arrayCopyNonAtomic(buffer, (short)(TLV_OFFSET + 2), value, 
			    START, buffer[TLV_OFFSET + 1]);
    updateParametersFile(apdu);
  }
  
  /**
   * Updates values of maximum balance or maximum amount for transaction in the
   * balancesRecord.
   * @param apdu APDU object
   * @param offset the offset in balancesRecord to be updated
   */
  
  private void updateBalanceValue(APDU apdu, short offset){
    byte[] buffer = apdu.getBuffer();
    Util.arrayCopyNonAtomic(buffer, (short)(TLV_OFFSET + 2), 
			    balancesRecord, offset, SHORT_LENGTH);
  }
  
  /**
   * Updates record in Parameters File.
   * @param apdu APDU object
   */
  
  private void updateParametersFile(APDU apdu){
    byte[] buffer = apdu.getBuffer();
    byte recordNumber = parametersFile.findRecord(buffer[TLV_OFFSET]);//match tag
    
    if (recordNumber == (byte)0) {
      /*
       * The record is not found. We have to create a new record.
       * NOTE: This is an example that a requirement to perform all memory
       * allocations (all "new") in class constructor is not an absolute one.
       */
      byte[] newRecord = new byte[buffer[TLV_OFFSET + 1] + 2];
      Util.arrayCopyNonAtomic(buffer,
			      TLV_OFFSET,
			      newRecord,
			      START,
			      (short)(buffer[TLV_OFFSET + 1] + 2));
      parametersFile.addRecord(newRecord);
    } else {
      byte[] theRecord = parametersFile.getRecord(recordNumber);
      Util.arrayCopyNonAtomic(buffer,
			      TLV_OFFSET,
			      theRecord,
			      START,
			      (short)(buffer[TLV_OFFSET + 1] + 2));
    }
  }
  
  /**
   * Selects file by FID according to ISO7816-4.
   * This implementation doesn't support all variations of SELECT command
   * in the standard, but provides reasonable subset for selection by FID
   * (P1==2).
   * @param apdu APDU object
   */
  
  private void processSelectFile(APDU apdu){
    
    byte[] buffer = apdu.getBuffer();
    
    // get the apdu data
    apdu.setIncomingAndReceive();
    
    if (buffer[ISO7816.OFFSET_P1] == (byte)2) {
      
      //  select file by FID
      if ( buffer[ISO7816.OFFSET_LC] != (byte)2)
	ISOException.throwIt(ISO7816.SW_WRONG_LENGTH);
      short fid = Util.getShort(buffer, ISO7816.OFFSET_CDATA);
      switch (fid) {
      case PARAMETERS_FID:
      case TRANSACTION_LOG_FID:
      case BALANCES_FID:
	transientShorts[SELECTED_FILE_IX] = fid;
	break;
      default:  ISOException.throwIt(ISO7816.SW_FILE_NOT_FOUND);
      }
    }	else ISOException.throwIt(ISO7816.SW_FUNC_NOT_SUPPORTED);
  }
  
  /**
   * Reads a record by the record number or reads the first occurrence
   * of the record by the record identifier.
   * This implementation doesn't support all variations of READ RECORD command
   * in the ISO 7816-4 standard, but provides reasonable subset of it.	It is
   * here to demonstrate that even without "built-in" support for ISO 7816
   * File System an Applet can have the behavior prescribed by the standard.
   *
   * @param apdu APDU object
   */
  
  private void processReadRecord(APDU apdu){
    
    // used to hold the record read
    byte record[] = null;
    short fid = 0;
    
    // get the APDU buffer and fields in the APDU header
    byte buffer[] = apdu.getBuffer();
    byte P1 = buffer[ISO7816.OFFSET_P1];
    byte P2 = buffer[ISO7816.OFFSET_P2];
    
    // process file selection here  according to ISO 7816-4, 6.5.3
    // P2 = xxxxxyyy
    // if xxxxx = 0, use the current selected file
    //    xxxxx(not all equal) = other value, select EF by SFI as
    //         specified in xxxxx
    
    if ( (P2 >> 3) == 0 ) {
      if (transientShorts[SELECTED_FILE_IX] == (short)0)
	ISOException.throwIt(ISO7816.SW_COMMAND_NOT_ALLOWED);
      else fid = transientShorts[SELECTED_FILE_IX];
    } else {
      // Short file identifier
      byte sfi = (byte) ( ( P2 >> 3 ) & 0x1F );
      fid = Util.makeShort(FID_BYTE, sfi);
      switch (fid) {
      case PARAMETERS_FID:
      case TRANSACTION_LOG_FID:
      case BALANCES_FID:
	transientShorts[SELECTED_FILE_IX] = fid;
	break;
      default:  ISOException.throwIt(ISO7816.SW_FILE_NOT_FOUND);
      }
    }
    
    // check for security status (validated PIN)
    switch (fid) {
    case TRANSACTION_LOG_FID:
    case BALANCES_FID:
      if (!userPIN.isValidated())
	ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);
      break;
    case PARAMETERS_FID:
      if (!masterPIN.isValidated())
	ISOException.throwIt(ISO7816.SW_SECURITY_STATUS_NOT_SATISFIED);
    }
    
    // get the last three bits of P2
    P2 = (byte) (P2 & 0x07);
    
    // select the record by record number
    if ( ( P2 & 0x04 ) != 0 ){
      
      if ( P2 == 0x04 ) {
	// P1 = 00 indicates in ISO 7816-4 the current record: we don't
	// support it in this implementation
	if (P1 == 0)
	  ISOException.throwIt(ISO7816.SW_RECORD_NOT_FOUND);
	switch (fid) {
	case BALANCES_FID:
	  // There is only one record in balancesRecord
	  if (P1 == 1)
	    record = balancesRecord;
	  else ISOException.throwIt(ISO7816.SW_RECORD_NOT_FOUND);
	  break;
	case TRANSACTION_LOG_FID:
	  record = transactionLogFile.getRecord(P1);
	  break;
	case PARAMETERS_FID:
	  record = parametersFile.getRecord(P1);
	}
	if (record == null)
	  ISOException.throwIt(ISO7816.SW_FILE_NOT_FOUND);
      }else
	ISOException.throwIt(ISO7816.SW_FUNC_NOT_SUPPORTED);
    } else {
      // select record by record identifier (first byte of the record)
      
      // read the first occurrence
      if ( P2 == 0) {
	switch (fid) {
	case BALANCES_FID:
	  // There is only one record in balancesRecords
	  if (balancesRecord[0] == P1)
	    record = balancesRecord;
	  else ISOException.throwIt(ISO7816.SW_RECORD_NOT_FOUND);
	  break;
	case TRANSACTION_LOG_FID:
	  P1 = transactionLogFile.findRecord(P1);
	  if  (P1 == 0 )
	    ISOException.throwIt(ISO7816.SW_RECORD_NOT_FOUND);
	  else
	    record = transactionLogFile.getRecord(P1);
	  break;
	case PARAMETERS_FID:
	  P1 = parametersFile.findRecord(P1);
	  if  (P1 == 0 )
	    ISOException.throwIt(ISO7816.SW_RECORD_NOT_FOUND);
	  else
	    record = parametersFile.getRecord(P1);
	}
      } else {
	// function not supported, when P2 = 1, 2, 3
	ISOException.throwIt(ISO7816.SW_FUNC_NOT_SUPPORTED);
      }
    }
    
    // set the data transfer direction to outbound
    short Le = apdu.setOutgoing();
    
    // if Lr < Le, set Le == Lr
    // otherwise send exactly Le bytes back
    if (record.length < Le) {
      Le = (short)record.length;
    }
    
    apdu.setOutgoingLength(Le);
    apdu.sendBytesLong(record, (short)0, Le);
    
  }
  
  /**
   * Updates loyalty program.
   * It takes standard TLV record for Parameter Update, interprets first
   * two bytes as loyaltyCAD (to be compared with first two bytes of CAD ID
   * in a transaction), the rest of record as AID for loyalty applet.
   * In case of successful retrieval of Shareable Interface Object with this AID
   * it is stored as an element of array, and method grantPoints of this loyalty
   * applet will be called in transaction to grant loyalty points. If SIO is not
   * returned, or loyaltyCAD in parameter is 0 the corresponding elements in
   * loyaltyCAD  and loyaltySIO arrays are cleared. The Parameters File is updated.
   *
   * @param apdu APDU object
   * @param loyaltyIndex index to loyaltyCAD and loyaltySIO arrays
   * @see com.sun.javacard.JavaLoyalty.JavaLoyalty
   */
  
  private void updateLoyaltyProgram(APDU apdu, byte loyaltyIndex){
    byte[] buffer = apdu.getBuffer();
    loyaltyCAD[loyaltyIndex] = Util.getShort(buffer,(short) (TLV_OFFSET+2));
    if (loyaltyCAD[loyaltyIndex] != (short)0) {
      
      AID loyaltyAID =  JCSystem.lookupAID(buffer, (short) (TLV_OFFSET+4),
					   (byte)(buffer[TLV_OFFSET+1]-2));
      if (loyaltyAID != null) {
	loyaltySIO[loyaltyIndex] = (JavaLoyaltyInterface)
	  JCSystem.getAppletShareableInterfaceObject(loyaltyAID, (byte)0);
	if (loyaltySIO[loyaltyIndex] == null)
	  loyaltyCAD[loyaltyIndex] = (short)0;
      }
      else
	loyaltyCAD[loyaltyIndex] = (short)0;
    }
    if (loyaltyCAD[loyaltyIndex] == (short)0) {
      // clean-up
      buffer[TLV_OFFSET+1] = (byte)2;
      Util.arrayFillNonAtomic (buffer,(short) (TLV_OFFSET+2),
			       (short) (buffer.length - TLV_OFFSET - 2), (byte)0);
    }
    updateParametersFile(apdu);
  }
  
}

