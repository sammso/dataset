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
