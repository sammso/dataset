/*
DataSet Library
---------------
Copyright (C) 2001-2005 - Sampsa Sohlman, Teemu Sohlman

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA 
*/
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

