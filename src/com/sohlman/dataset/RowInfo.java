package com.sohlman.dataset;

/**
 * @author Sampsa Sohlman
 *
 * @version 2002-10-09
 */
public class RowInfo
{	
	private ColumnInfo[] i_ColumnInfos;
	
	/*
	public RowInfo(String[] aS_ColumnClassNames,  String[] aS_ColumnNames)
	{
		if(aS_ColumnClassNames==null || aS_ColumnNames==null)
		{
			throw new IllegalArgumentException("Parameters cannot be null");
		}
		
		if(aS_ColumnClassNames.length != aS_ColumnNames.length)
		{
			throw new IllegalArgumentException("ColumnClassNames count has to be equal to ColumnNames count");
		}
						
		iS_ColumnNames = aS_ColumnNames;
		iS_ColumnClassNames = aS_ColumnClassNames;
		ii_columnCount = iS_ColumnNames.length;
	}
	
	public RowInfo(String[] aS_ColumnClassNames)
	{
		if(aS_ColumnClassNames==null)
		{
			throw new IllegalArgumentException("Parameters cannot be null");
		}
		
		iS_ColumnNames = new String[aS_ColumnClassNames.length];
		String lS_Empty = "";
		
		for(int li_x = 0 ; li_x < iS_ColumnNames.length ; li_x++)
		{
			iS_ColumnNames[li_x] = lS_Empty;
		}
		iS_ColumnClassNames = aS_ColumnClassNames;		
		
	}
	*/
	public RowInfo(ColumnInfo[] a_ColumnInfos)
	{
		i_ColumnInfos = a_ColumnInfos;
	}
	
	public void setColumnName(int ai_index, String aS_Name)
	{
		getColumnInfo(ai_index).setName(aS_Name);
	}
	
	public int getIndexByColumnName(String aS_Index)
	{
		// Future implementation
		
		return 0;
	}
	
	/**
	 * Get column name for specified index
	 * @param ai_index
	 * @return String which contains current column name
	 * @throws ArrayIndexOutOfBoundsException if index is out of range
	 */
	public String getColumnName(int ai_index)
	{
		return getColumnInfo(ai_index).getName();
	}
	
	public ColumnInfo getColumnInfo(int ai_index)
	{
		int li_count = i_ColumnInfos.length;
		if(ai_index <= 0 || ai_index > li_count)
		{
			throw new ArrayIndexOutOfBoundsException("Column index has to be range 1 - " + li_count);
		}
		return i_ColumnInfos[ai_index - 1];
	}
	
	/**
	 * Get column class name for specified index
	 * @param ai_index
	 * @return String which contains class name for column
	 * @throws ArrayIndexOutOfBoundsException if index is out of range
	 */
	public String getColumnClassName(int ai_index)
	{	
		return getColumnInfo(ai_index).getClassName();	
	}	
	
	
	/**
	 * Method getColumnClass.
	 * 
	 * @param ai_index Requested column index
	 * @return Class for that column
	 * @throws ClassNotFoundException if class is not in classpath
	 * @throws ArrayIndexOutOfBoundsException if index is out of range
	 */
	public Class getColumnClass(int ai_index) throws ClassNotFoundException
	{
		return i_ColumnInfos[ai_index].getColumnClass();
	}
	
	
	/**
	 * Tells columnCount for DataSet and this object
	 * @return int column count
	 */
	public int getColumnCount()
	{
		return i_ColumnInfos.length;
	}
	
	/**
	 * Checks if this eguals with another RowInfo object.<p>
	 * It will check that ClassNames and column count are match
	 * 
	 * @param a_ColumnsInfo
	 * @return boolean
	 */
	public boolean equals(RowInfo a_RowInfo)
	{
		int li_count = i_ColumnInfos.length;		
		if(li_count!=a_RowInfo.getColumnCount())
		{
			return false;
		}
		
		for(int li_x = 0 ; li_x < li_count ; li_x ++)
		{
			if(!getColumnClassName(li_x + 1).equals(a_RowInfo.getColumnClassName(li_x + 1)))
			{
				return false;	
			}
		}
		return true;
	}
}
