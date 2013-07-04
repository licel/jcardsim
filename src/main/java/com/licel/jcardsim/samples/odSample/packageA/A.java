/*
 * Copyright © 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.licel.jcardsim.samples.odSample.packageA;

import javacard.framework.*;

/**
 * package AID 0xA0 0x00 0x00 0x00 0x62 0x03 0x01 0x0C 0x07 0x01
 * applet AID - 0xA0 0x00 0x00 0x00 0x62 0x03 0x01 0x0C 0x07 0x01 0x01
 *
 * This applet used to demonstrate object deletion mechanism and also to 
 * monitor memory usage as other packages/applets get added/deleted
 **/

public class A extends Applet {

  //APDU specific data
  public final static byte A_CLA=(byte)0xC0;
  public static short ST_ERROR_BAD_TRANSIENT_MEM_DESELECT = (short)0x9101;
  public static short ST_ERROR_BAD_TRANSIENT_MEM_DESELECT_ELEMENT=(short)0x9102;
  public static short ST_ERROR_BAD_TRANSIENT_MEM_RESET = (short)0x9103;
  public static short ST_ERROR_BAD_TRANSIENT_MEM_RESET_ELEMENT=(short)0x9104;
  public static short ST_ERROR_MEM_MATCH_OBJECTS=(short)0x9105;
  public static short ST_ERROR_MEM_MATCH_TRANSIENT_DESELECT_OBJECTS=(short)0x9106;
  public static short ST_ERROR_TRANSIENT_DESELECT_OBJECTS_EXIST=(short)0x9107;
  public static short ST_ERROR_TRANSIENT_RESET_OBJECTS_EXIST=(short)0x9108;
  public static short ST_ERROR_MEM_MATCH_TRANSIENT_RESET_OBJECTS=(short)0x9109;
  public static short ST_ERROR_MEM_MATCH_ALL_ATTRIBUTES=(short)0x910A;
  public static short ST_ERROR_MEM_MATCH_INITIAL=(short)0x910B;
  public static short ST_ERROR_MEM_MATCH_BEFORE_PACK=(short)0x910C;
  public static short ST_ERROR_APPLET_AID_NOT_FOUND=(short)0x910D;
  public static short ST_ERROR_SHAREABLE_NOT_FOUND=(short)0x910E;
  public static short ST_ERROR_TRANSIENT_DESELECT_MEM_NOT_RETURNED=(short)0x910F;
  public static short ST_ERROR_TRANSIENT_RESET_MEM_NOT_RETURNED=(short)0x9110;
  public static short SUCCESS=(short)0x9000;

	//BApp1AID is the Applet AID for applet BApp1 in packageB
	public static byte[] BApp1AID={(byte)0xA0, (byte)0x00,
								   (byte)0x00, (byte)0x00,
								   (byte)0x62, (byte)0x03,
								   (byte)0x01, (byte)0x0C,
								   (byte)0x07, (byte)0x02,
								   (byte)0x01};

	short initialMemUsage; //the initial available persistent memory snapshot
	short initialTransientDeselectMem; //initial available transient memory of type MEMORY_TYPE_TRANSIENT_DESELECT
	short initialTransientResetMem; //initial available transient memory of type MEMORY_TYPE_TRANSIENT_RESET
	short objectRefMemUsage; //memory snapshot after making children tree
	short transientDeselectUninitializedMemUsage;//memory after making array but not initializing
	short transientDeselectMemUsage;//memory snapshort after making transient usign deselect and initializing
	short transientResetUninitializedMemUsage;//memafter making array but not initializing it
	short transientResetMemUsage; //memory snapshot after making transient using reset

	//reference attributes
	ATreeNode left;
	ATreeNode right;
	static ATreeNode sleft;
	static ATreeNode sright;
	Object[] transientDeselectMem; //transient
	Object[] transientResetMem;//transient
	Shareable BApp1Ref; //ref to BApp1

	//Constants
	private static final short NUM_OBJECTS=2;
	private static final short MAX_TREE_DEPTH=2;

  //constants, INS codes and variables for these tests
  final static byte SETUP_PERSISTENT = (byte)0x19;
  final static byte SETUP_TRANSIENT = (byte)0x20;
  final static byte REQUEST_OD = (byte)0x10;
  final static byte REMOVE_TREES = (byte)0x11;
  final static byte ANALYZE_REMOVE_TREES = (byte)0x12;
  final static byte ANALYZE_TRANSIENT_DESELECT_MEM=(byte)0x13;
  final static byte ANALYZE_TRANSIENT_RESET_MEM=(byte)0x14;
  final static byte REMOVE_ALL_ATTRIBUTES=(byte)0x15;
  final static byte ANALYZE_REMOVE_ALL_ATTRIBUTES=(byte)0x16;
  final static byte CAPTURE_INITIAL_MEM=(byte)0x17;
  final static byte COMPARE_INITIAL_MEM=(byte)0x18;
  final static byte GET_SHAREABLE_REF=(byte)0x21;
	final static byte LOSE_REF_BAPP1=(byte)0x22;

	/**
	 * method instantiates aninstance of A
	 **/
	public static void install(byte []bArr,short bOffset,byte bLength){
		new A();
	}

  /**
   * method sets up tree of objects. Two are statically referenced.
   **/
  private void setupTrees(){
    left=new ATreeNode((short)0,MAX_TREE_DEPTH);
    right=new ATreeNode((short)0,MAX_TREE_DEPTH);
    sleft=new ATreeNode((short)0,MAX_TREE_DEPTH);
    sright=new ATreeNode((short)0,MAX_TREE_DEPTH);
  }

  /**
   * Constructor. Calls register and captures the initial memory usage
   **/
  private A(){
    //capture initial persistant memory available
    register();
    initialMemUsage=JCSystem.getAvailableMemory(JCSystem.MEMORY_TYPE_PERSISTENT);
  }

	/**
	 * method sets up the persistent objects by calling setupTrees()
	 * and captures the memory available
	 **/
  private void setupPersistent(){
    //setup object references
    setupTrees();
    objectRefMemUsage=JCSystem.getAvailableMemory(JCSystem.MEMORY_TYPE_PERSISTENT);
  }

	/**
	 * method sets up the transient objects and captures available memory
	 * of different kinds at various stages.
	 **/
  private void setupTransient(){
    //get initial memory available for persistent and transient memory
    initialMemUsage=JCSystem.getAvailableMemory(JCSystem.MEMORY_TYPE_PERSISTENT);
	initialTransientDeselectMem=JCSystem.getAvailableMemory(JCSystem.MEMORY_TYPE_TRANSIENT_DESELECT);
	initialTransientResetMem=JCSystem.getAvailableMemory(JCSystem.MEMORY_TYPE_TRANSIENT_RESET);	

    //setup CLEAR_ON_DESELECT memory
    transientDeselectMem=JCSystem.makeTransientObjectArray(NUM_OBJECTS,JCSystem.CLEAR_ON_DESELECT);
    transientDeselectUninitializedMemUsage=JCSystem.getAvailableMemory(JCSystem.MEMORY_TYPE_PERSISTENT);
    for(short i=0;i<NUM_OBJECTS;i++)
      transientDeselectMem[i]=new TransientArrayElement(i);
    transientDeselectMemUsage=JCSystem.getAvailableMemory(JCSystem.MEMORY_TYPE_PERSISTENT);
    
    //setup CL_ON_RESET memory
	transientResetMem=JCSystem.makeTransientObjectArray(NUM_OBJECTS,JCSystem.CLEAR_ON_RESET);
    transientResetUninitializedMemUsage=JCSystem.getAvailableMemory(JCSystem.MEMORY_TYPE_PERSISTENT);
    for(short i=0;i<NUM_OBJECTS;i++)
      transientResetMem[i]=new TransientArrayElement(i);
    transientResetMemUsage=JCSystem.getAvailableMemory(JCSystem.MEMORY_TYPE_PERSISTENT);
  }
  
  /**
   * verify the transient structures of type CLEAR_ON_DESELECT are intact
   **/
  private short verifyTransientDeselect(){
    if(transientDeselectMem==null)
      return ST_ERROR_BAD_TRANSIENT_MEM_DESELECT;
    for(short i=0;i<transientDeselectMem.length;i++){
      if(transientDeselectMem[i]==null ||
	 ((TransientArrayElement)transientDeselectMem[i]).data!=i)
	return ST_ERROR_BAD_TRANSIENT_MEM_DESELECT_ELEMENT;
    }
    return SUCCESS;
  }

  /**
   * verify the transient structures of type CLEAR_ON_RESET are intact
   **/
  private short verifyTransientReset(){
    if(transientResetMem==null)
      return ST_ERROR_BAD_TRANSIENT_MEM_RESET;
    for(short i=0;i<transientResetMem.length;i++){
      if(transientResetMem[i]==null ||
	 ((TransientArrayElement)transientResetMem[i]).data!=i)
	return ST_ERROR_BAD_TRANSIENT_MEM_RESET_ELEMENT;
    }
    return SUCCESS;
  }
 
  /**
   * request object deletion facility to delete objects that are not reachable
   **/
  private void requestOD(){
    JCSystem.requestObjectDeletion();
  }

  /**
   * method sets the persistent object references to null
   **/
  private void removeTrees(){
    //remove the trees from both static and non-static trees
    left=null;
	right=null;
	sleft=null;
	sright=null;
  }

  /**
   * method verifies if all memory pointed to the persistent objects
   * was collected
   **/
  private void analyzeRemoveTrees(){
	  short tempMem=JCSystem.getAvailableMemory(JCSystem.MEMORY_TYPE_PERSISTENT);
	  if(tempMem!=transientResetMemUsage){
		  //error. not all memory collected
		  ISOException.throwIt(transientResetMemUsage);		  
		  //ISOException.throwIt(ST_ERROR_MEM_MATCH_OBJECTS);		  
	  }
	  //verify all other objs pointed to by the DESELECT array are intact
	  short ret=verifyTransientDeselect();
	  if(ret!=SUCCESS)
		  ISOException.throwIt(ret);

	  //verify all objects pointed to by the RESET array are intact
	  ret=verifyTransientReset();
	  if(ret!=SUCCESS)
		  ISOException.throwIt(ret);
  }

	/**
	 * this method should be called after deselecting and selecting
	 **/
  private void analyzeTransientDeselectMem(){
    //by this point the arrays must have had there values nulled and references
    //to all transientarrayelements lost. Object deletion mechanism here would have deleted
    //all transientarrayelement objects pointed to by deselect array

    //verify objects pointed to by DESELECT array are gone
	  short ret=verifyTransientDeselect();
	  if(ret!=ST_ERROR_BAD_TRANSIENT_MEM_DESELECT_ELEMENT)
		  ISOException.throwIt(ST_ERROR_TRANSIENT_DESELECT_OBJECTS_EXIST);

    //verify persistent memory collected for all objects
    short tempMem=JCSystem.getAvailableMemory(JCSystem.MEMORY_TYPE_PERSISTENT);    
    if(tempMem!=(short)(transientResetMemUsage+
						(transientDeselectUninitializedMemUsage-transientDeselectMemUsage))){
      //error. not all memory collected
		ISOException.throwIt( ST_ERROR_MEM_MATCH_TRANSIENT_DESELECT_OBJECTS);
    }

    //verify all objects pointed to by the RESET array are intact
    ret=verifyTransientReset();
	if(ret!=SUCCESS)
		ISOException.throwIt(ret);
  }

  /**
   * this method should be called after card was reset. It analyzes
   * if all persistent memory has been returned including the memory
   * used by objects in the transient reset array.
   **/
  private void analyzeTransientResetMem(){
    //by now card should have been reset

    //verify objects pointed to by RESET array are gone
    short ret=verifyTransientReset();
    if(ret!=ST_ERROR_BAD_TRANSIENT_MEM_RESET_ELEMENT)
		ISOException.throwIt(ST_ERROR_TRANSIENT_RESET_OBJECTS_EXIST);

    //verify memory is collected for all objects
    short tempMem=JCSystem.getAvailableMemory(JCSystem.MEMORY_TYPE_PERSISTENT);    
    if(tempMem!=(short)((transientDeselectUninitializedMemUsage-transientDeselectMemUsage)+
						(transientResetUninitializedMemUsage))){
		//not all memory returned
		ISOException.throwIt( ST_ERROR_MEM_MATCH_TRANSIENT_RESET_OBJECTS);
    }
  }

	/**
	 * method sets attributes pointing to the two types of transient
	 * arrays to null. This creates garbage objects to be deleted. Method also
	 * requests for object deletion
	 **/
	private void removeAllAttributes(){
		//set all attributes to null
		transientDeselectMem=null;
		transientResetMem=null;
		JCSystem.requestObjectDeletion();
	}

  /**
   * method analyzes if all transient and persistent memory have been
   * returned to the memory manager. It is called after the card has
   * been reset.
   **/
  private void analyzeRemoveAllAttributes(){
    //now the memory should have gone back to initial mem
    short tempMem=JCSystem.getAvailableMemory(JCSystem.MEMORY_TYPE_PERSISTENT);    
    if(tempMem!=initialMemUsage){
      ISOException.throwIt(ST_ERROR_MEM_MATCH_ALL_ATTRIBUTES);
    }

	//test if all transient reset and deselect memory is returned
	if(initialTransientResetMem!=
	   JCSystem.getAvailableMemory(JCSystem.MEMORY_TYPE_TRANSIENT_RESET))		
		ISOException.throwIt(ST_ERROR_TRANSIENT_RESET_MEM_NOT_RETURNED);
	
	
	if(initialTransientDeselectMem!=
	   JCSystem.getAvailableMemory(JCSystem.MEMORY_TYPE_TRANSIENT_DESELECT))
		ISOException.throwIt(ST_ERROR_TRANSIENT_DESELECT_MEM_NOT_RETURNED);
  }

  /**
   * method captures the current memory used by compareWithInitial()
   * to check if memis same.
   **/
  private void captureInitialMem(){
    initialMemUsage=JCSystem.getAvailableMemory(JCSystem.MEMORY_TYPE_PERSISTENT);
  }

  /**
   *method compares the current memory with initialMemUsage and throws exception
   * if not the same
   **/
  private void compareWithInitial(){
    short curr=JCSystem.getAvailableMemory(JCSystem.MEMORY_TYPE_PERSISTENT);
    if(curr!=initialMemUsage)
      ISOException.throwIt( ST_ERROR_MEM_MATCH_INITIAL);
  }

	/**
	 * method gets a shareable reference to BApp1 (Applet B instance 1)
	 * to verify that we cannot delete it
	 **/
	private void getShareableRef(){
		AID bApp1AID=JCSystem.lookupAID(BApp1AID,(short)0,(byte)BApp1AID.length);
		if(bApp1AID==null)
			ISOException.throwIt(ST_ERROR_APPLET_AID_NOT_FOUND);
		//get the ref
		BApp1Ref=(Shareable)(JCSystem.getAppletShareableInterfaceObject(bApp1AID,(byte)0));
		if(BApp1Ref==null)
			ISOException.throwIt(ST_ERROR_SHAREABLE_NOT_FOUND);
	}
		
	/**
	 * method sets the BApp1 referenc to null
	 **/
	private void loseBAppRef(){
		BApp1Ref=null;
		JCSystem.requestObjectDeletion();
	}

	/**
	 * method processes the APDU commands passed to this applet instance.
	 * It dispatches the request by calling the appropriate method and
	 * returning appropriate result
	 **/
  public void process(APDU apdu) throws ISOException{
    byte buffer[]=apdu.getBuffer();
    // check SELECT APDU command
    if ((buffer[ISO7816.OFFSET_CLA] == 0) &&
		(buffer[ISO7816.OFFSET_INS] == (byte)(0xA4))){
		return;
    }
    short ret=0;
    short tempMem=0;
    switch (buffer[ISO7816.OFFSET_INS]){
    case SETUP_TRANSIENT:
      setupTransient();
	  break;
    case SETUP_PERSISTENT:
      setupPersistent();
	  break;
    case REQUEST_OD:
      requestOD();
	  break;
    case REMOVE_TREES:
      removeTrees();
      break;
    case ANALYZE_REMOVE_TREES:
		analyzeRemoveTrees();
		break;
    case ANALYZE_TRANSIENT_DESELECT_MEM:
		//this must be called after deselecting and selecting
		analyzeTransientDeselectMem();	
		break;
    case ANALYZE_TRANSIENT_RESET_MEM:
      //called after applet reset
      analyzeTransientResetMem();
	  break;
    case REMOVE_ALL_ATTRIBUTES:
      removeAllAttributes();
      break;
    case ANALYZE_REMOVE_ALL_ATTRIBUTES:
      analyzeRemoveAllAttributes();
	  break;
    case CAPTURE_INITIAL_MEM:
      captureInitialMem();
      break;
    case COMPARE_INITIAL_MEM:
		//Assumes captureInitialMem has been called
		compareWithInitial();
		break;
	case GET_SHAREABLE_REF:
		getShareableRef();
		break;
	case LOSE_REF_BAPP1:
		loseBAppRef();
		break;
    default:
      ISOException.throwIt(ISO7816.SW_INS_NOT_SUPPORTED);
    }
  }
}
      
