/*
DataSet Library
---------------
Copyright (C) 2001-2004 - Sampsa Sohlman, Teemu Sohlman

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
package com.sohlman.dataset.sql;

import com.sohlman.dataset.RowInfo;


/**
 * SQLColumnInfo object is responsible of information for columns of database
 * 
 * @author Sampsa Sohlman
 * @version 2002-10-09 New object for different desing of column handling
 */
public class SQLRowInfo extends RowInfo
{
	public SQLRowInfo(SQLColumnInfo[] a_SQLColumnInfos)
	{
		super(a_SQLColumnInfos);
	}
	
	public int getColumnType(int ai_index)
	{
		return ((SQLColumnInfo)getColumnInfo(ai_index)).getType();
	}
}
