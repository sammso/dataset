package com.sohlman.dataset;

/**
 * <p>Master inteface for all WriteEngine interfaces. </p>
 * <p>This is designed for mainly for future use</p>
 *
 * @author  Sampsa Sohlman
 * @version 
 */
public interface WriteEngine
{
	/** DataSet calls this method before update starts.
	 * @return Count of how many rows are updated<br>
	 * < 0 if error situation.
	 */	
	public void writeStart() throws DataSetException;

	/**
	 * Writes changes to datasource
	 * @param a_DataSet DataSet to be written
	 */
	public void write(DataSet a_DataSet) throws DataSetException;

	/** Will be called when update is done. Also when error has happened.
	 * @return Count of rows that has been inserted + modified + deleted<br>
	 * @throws DataSetException On case of error
	 */		
	public int writeEnd() throws DataSetException;
}

