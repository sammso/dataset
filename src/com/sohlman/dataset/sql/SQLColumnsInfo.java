package com.sohlman.dataset.sql;

import com.sohlman.dataset.ColumnsInfo;


/**
 * SQLColumnInfo object is responsible of information for columns of database
 * 
 * @author Sampsa Sohlman
 * @version 2002-10-09 New object for different desing of column handling
 */
public class SQLColumnsInfo extends ColumnsInfo
{
	private int[] ii_columnTypes;
	
	
	/**
	 * Constructor
	 * @param aS_ColumnClassNames Column Class names
	 * @param aS_ColumnNames Column names from database
	 * @param ai_columnTypes Column types from database
	 * @throws IllegalArgumentException if argument counts differ or they are null
	 */
	public SQLColumnsInfo(String[] aS_ColumnClassNames,  String[] aS_ColumnNames, int[] ai_columnTypes)
	{
		super(aS_ColumnClassNames,aS_ColumnNames);
		
		if(ai_columnTypes==null)
		{
			throw new IllegalArgumentException("columnTypes definition is null");
		}
		if(ai_columnTypes.length!=aS_ColumnNames.length)
		{
			throw new IllegalArgumentException("Column type count has be equal as ColumnClassNames count and ColumnNames count");
		}
		ii_columnTypes = ai_columnTypes;
	}
	
	/**
	 * Method getColumnType for current index (index range is 1 - getColumnCount)
	 * 
	 * @param ai_index
	 * @return int
	 * @throws ArrayIndexOutOfBoundsException if index is out of range.
	 */
	public int getColumnType(int ai_index)
	{
		if(ai_index <= 0 || ai_index > getColumnCount())
		{
			throw new ArrayIndexOutOfBoundsException("getColumnType index out of range");
		}
				
		return ii_columnTypes[ai_index - 1];
	}
}
