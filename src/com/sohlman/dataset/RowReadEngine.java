/*
 * DataSetRetrieve.java
 *
 * Created on 3. heinäkuuta 2001, 18:08
 */

package com.sohlman.dataset;

/** Interface for DataSet to retrieve rows.
 * Inherit this interface to create datasource for dataset.
 *
 * @author Sampsa Sohlman
 * @version 2001-07-03
 */
public interface RowReadEngine extends ReadEngine
{
	/** This is first method to call retrieve operation.
	 *
	 * @return true if contniue to retrieve operation false if cancel
	 *
	 */	
	public void readStart(Row a_Row_Model) throws DataSetException;
	/** Gets row from RetrieveEngine.
	 *
	 * <B>It is important that this is return null value some point, othervice there might be eternal loop in application.</B>
	 *
	 * @return Row object which contains retrieved data.
	 * null if no more data is found.
	 */	
	public int readRow(Row a_Row) throws DataSetException;
	
	/** Last action when all the rows are retrieved
	 * @return How may rows are retrieved
	 */
	public int readEnd() throws DataSetException;
	
}

