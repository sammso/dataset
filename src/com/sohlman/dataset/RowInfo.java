package com.sohlman.dataset;

import java.io.Serializable;

/**
 * @author Sampsa Sohlman
 *
 * @version 2002-10-09
 */
public class RowInfo implements Serializable
{	
	private ColumnInfo[] i_ColumnInfos;
	transient private ModifyAction i_ModifyAction;
	
	public RowInfo(ColumnInfo[] a_ColumnInfos)
	{
		i_ColumnInfos = a_ColumnInfos;			
	}
	
	public void setColumnName(int ai_index, String aS_Name)
	{
		getColumnInfo(ai_index).setName(aS_Name);
	}
	
	public int getIndexByColumnName(String aS_ColumnName) throws DataSetException
	{
		// Future implementation
		for(int li_index = 0 ; li_index < i_ColumnInfos.length ; li_index++)
		{
			if(i_ColumnInfos[li_index].getName().equals(aS_ColumnName))
			{
				return li_index + 1;
			}
		}
		throw new DataSetException("ColumnNameNotFound");
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
	public Class getColumnClass(int ai_index)
	{
		return getColumnInfo(ai_index).getColumnClass();
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
			if(!getColumnClass(li_x + 1).equals(a_RowInfo.getColumnClass(li_x)))
			{
				return false;	
			}
		}
		return true;
	}
	
	/**
	 * @return ModifyAction which 
	 */
	
	public ModifyAction getKeyAction()
	{
		return i_ModifyAction;	
	}
	
	public void setKeyAction(ModifyAction a_ModifyAction)
	{
		i_ModifyAction = a_ModifyAction;
	}	
}
