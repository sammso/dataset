package com.sohlman.dataset;

/**
 * @author Sampsa Sohlman
 *
 * @version 2002-10-09
 */
public class ColumnsInfo
{
	private String[] iS_ColumnClassNames = null;;
	private String[] iS_ColumnNames = null;
	private int ii_columnCount = 0;
	
	public ColumnsInfo(String[] aS_ColumnClassNames,  String[] aS_ColumnNames)
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
	
	public ColumnsInfo(String[] aS_ColumnClassNames)
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
	
	public void setColumnName(int ai_index, String aS_Name)
	{
		if(ai_index <= 0 || ai_index > ii_columnCount)
		{
			throw new ArrayIndexOutOfBoundsException("SetColumnName index out of range");
		}
		
		iS_ColumnNames[ai_index - 1] = aS_Name;
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
		if(ai_index <= 0 || ai_index > ii_columnCount)
		{
			throw new ArrayIndexOutOfBoundsException("getColumnName index out of range");
		}
				
		return iS_ColumnNames[ai_index - 1];
	}
	
	/**
	 * Get column class name for specified index
	 * @param ai_index
	 * @return String which contains class name for column
	 * @throws ArrayIndexOutOfBoundsException if index is out of range
	 */
	public String getColumnClassName(int ai_index)
	{
		if(ai_index <= 0 || ai_index > ii_columnCount)
		{
			throw new ArrayIndexOutOfBoundsException("SetColumnName index out of range");
		}
				
		return iS_ColumnClassNames[ai_index - 1];		
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
		if(ai_index <= 0 || ai_index > ii_columnCount)
		{
			throw new ArrayIndexOutOfBoundsException("getColumnClass index out of range");
		}		
		return Class.forName(getColumnClassName(ai_index));		
	}
	
	
	/**
	 * Tells columnCount for DataSet and this object
	 * @return int column count
	 */
	public int getColumnCount()
	{
		return ii_columnCount;
	}
	
	/**
	 * Checks if this eguals with another ColumnsInfo object.<p>
	 * It will check that ClassNames and column count are match
	 * 
	 * @param a_ColumnsInfo
	 * @return boolean
	 */
	public boolean equals(ColumnsInfo a_ColumnsInfo)
	{
		if(getColumnCount()!=a_ColumnsInfo.getColumnCount())
		{
			return false;
		}
		
		for(int li_x = 0 ; li_x < ii_columnCount ; li_x ++)
		{
			if(!getColumnClassName(li_x).equals(a_ColumnsInfo.getColumnClassName(li_x)))
			{
				return false;	
			}
		}
		return true;
	}	
}
