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
	 * @param a_ColumnsInfo which defines type of row. If row differs then 
	 * {@link DataSetException DataSetException} is thrown. If value is null then
	 * It will genereate new RowInfo.
	 * @return RowInfo which acts as row model object 
	 * @throws DataSetException on error situation
	 */	
	public RowInfo readStart(RowInfo a_RowInfo) throws DataSetException;    
	
	/** Gets row from ReadEngine
	 *
	 * <B>It is important that this is return null value some point, othervice there might be eternal loop in application.</B>
	 *
	 * @param a_ColumnsInfo ColumnInfo which are used to create the row 
	 * @return how many rows have been read. DataSet.NO_MORE_ROWS if no new row found.
	 */	
	public Row readRow(RowInfo a_RowInfo) throws DataSetException;
		
	/** Last action when all the rows are retrieved
	 * @return How may rows are retrieved
	 * @throws DataSetException on error situation
	 */	
	public int readEnd()  throws DataSetException;	
}

