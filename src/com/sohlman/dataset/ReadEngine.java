package com.sohlman.dataset;

/**
 * <p>Master inteface for all WriteEngine interfaces. </p>
 * <p>This is designed for mainly for future use</p>
 *
 * @author  Sampsa Sohlman
 * @version 2002-09-26 Inteface has been changed
 * @version 2001-07-02
 */
public interface ReadEngine
{
	/** This is first method to call retrieve operation.
	 *
	 * @param a_Row_Model which is type row is.
	 * @return Row which acts as row model object 
	 * 
	 */	
	public Row readStart(Row a_Row_Model) throws DataSetException;    
	
	/** Gets row from ReadEngine
	 *
	 * <B>It is important that this is return null value some point, othervice there might be eternal loop in application.</B>
	 *
	 * @return how many rows have been read. DataSet.NO_MORE_ROWS if no new row found.
	 */	
	public int readRow(Row a_Row) throws DataSetException;
		
	/** Last action when all the rows are retrieved
	 * @return How may rows are retrieved
	 */	
	public int readEnd()  throws DataSetException;	
}

