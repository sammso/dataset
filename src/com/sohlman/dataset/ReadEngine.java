package com.sohlman.dataset;

/**
 * <p>Master inteface for all WriteEngine interfaces. </p>
 * <p>This is designed for mainly for future use</p>
 *
 * @author  Sampsa Sohlman
 * @version 1.0
 */
public interface ReadEngine
{
	/** This is first method to call retrieve operation.
	 *
	 * @return true if contniue to retrieve operation false if cancel
	 *
	 */	
	public void readStart(Row a_Row_Model) throws DataSetException;    
	/** Last action when all the rows are retrieved
	 * @return How may rows are retrieved
	 */	
	public int readEnd()  throws DataSetException;	
}

