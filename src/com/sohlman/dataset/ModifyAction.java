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

/** UpdateAction tells the DataSet what kind of action it should take. Modify or Delete + Insert
 *
 * @author Sampsa Sohlman
 * @version 2001-08-15
 */
public interface ModifyAction
{
	/** This method tells what kind of action should be taken, when DataSet row is modified.<br>
	 * Just normal modify<br>
	 * <b>OR</b>
	 * Delete + Insert<b>
	 * @param a_Row_Orig This parameter contains original row.
	 * @param a_Row_New This parameter contains new Row object
	 * @return <b>true</b> if key is modified<br>
	 * <b>false</b> if key is not modified<br>
	 */	
	public boolean isKeyModified(Row a_Row_Orig, Row a_Row_New);
}

