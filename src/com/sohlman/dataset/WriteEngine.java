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
 * @version 2002-10-10
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

