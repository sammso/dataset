/*
 * UpdateEngine.java
 *
 * Created on 3. heinäkuuta 2001, 18:18
 */

package com.sohlman.dataset;

/** This interface provides methods for DataSet to write changes back to datasource.
 * @author Sampsa Sohlman
 * @version 2001-07-03
 */
public interface ModifiedWriteEngine extends WriteEngine
{
	/** DataSet calls this method before update starts.
	 * @return Count of how many rows are updated<br>
	 * < 0 if error situation.
	 */	
	public void writeStart() throws DataSetException;
	/** Handles delete from source.
	 * @param a_Row Row which will be deleted from source.
	 * @return true if delete is succeeded<br>
	 * false if not
	 */	
	public void deleteRow(Row a_Row_Original, Row a_Row_Current) throws DataSetException;
	/** Handles insert to source.
	 * @param a_Row Row which will be inserted to source.
	 * @return true if insert is succeeded<br>
	 * false if not
	 */		
	public void insertRow(Row a_Row) throws DataSetException;
	/** Handles modifying to source.
	 * @param a_Row Row which will be modifying from source.
	 * @return true if modify is succeeded<br>
	 * false if not
	 */		
	public void modifyRow(Row a_Row_Original, Row a_Row_Current) throws DataSetException;
	/** Will be called when update is done
	 * @return Count of rows that has been inserted + modified + deleted<br>
	 * < 0 if error.
	 */		
	public int writeEnd() throws DataSetException;
}

