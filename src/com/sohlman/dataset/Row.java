/*
 * Row.java
 *
 * Created on 2. heinäkuuta 2001, 13:25
 *
 */

package com.sohlman.dataset;


/** Row object represent row in {@link DataSet DataSet} object.<br>
 *
 * @author Sampsa Sohlman
 * @version 2002-10-9
 */

public class Row
{
	public final static Row NO_MORE_ROWS = null;

	Object[] iO_Columns;
	RowInfo i_RowInfo;

	/** Constructor
	 * @param aO_Columns List object columns.
	 * @param aS_ColumnClassNames Class names like "java.lang.String"
	 * Only basic datacolums are allowed.
	 */
	public Row(Object[] aO_Columns, RowInfo a_RowInfo)
	{
		iO_Columns = aO_Columns;
		i_RowInfo = a_RowInfo;
	}
	

	/** Set's value for column
	 * @param ai_index Column index
	 * @param a_Object Object for column
	 * @return index of column changed or -1 error
	 */
	public final int setValueAt(int ai_index, Object a_Object)
	{
		
		//if(a_Object!=null) System.out.println(ai_index + " : " + a_Object.getClass().getName() + " :  " + a_Object.toString());
		if (ai_index > 0 && ai_index <= iO_Columns.length)
		{
			if(a_Object==null)
			{
				iO_Columns[ai_index - 1] = null;	
			}
			else if(a_Object.getClass().getName().equals(i_RowInfo.getColumnClassName(ai_index)))
			{
				iO_Columns[ai_index - 1] = a_Object;
			}
			else
			{
				ai_index = -1;
			}
			return ai_index;
		}
		else
		{
			throw new ArrayIndexOutOfBoundsException("Column index " + ai_index + " is out of Row range");
		}
	}

	/** Returns value of selected column
	 * @param ai_index Index of column
	 * @return Object that column is containing
	 */
	public final Object getValueAt(int ai_index)
	{
		if (ai_index > 0 && ai_index <= iO_Columns.length)
		{
			return iO_Columns[ai_index - 1];
		}
		else
		{
			return null;
		}
	}

	/** Copy data from other row object
	 * @param a_Row Row object
	 */
	public final void copyFromOtherRow(Row a_Row)
	{
		int li_count = 0;
		li_count = a_Row.getColumnCount();
		for (int li_c = 1; li_c <= li_count; li_c++)
		{
			setValueAt(li_c, a_Row.getValueAt(li_c));
		}
	}
	
	public RowInfo getRowInfo()
	{
		return	i_RowInfo;
	}

	/** Returns object array from columns.
	 * @return Object array
	 */
	final public Object[] getColumns()
	{
		int[] li_columns = new int[iO_Columns.length];
		int li_counter;
		for (li_counter = 0; li_counter < li_columns.length; li_counter++)
		{
			li_columns[li_counter] = li_counter;
		}
		return getColumns(li_columns);
	}

	/** Returns object array from columns.
	 * @param ai_columns Column numbers that wanted to be returned
	 * @return Object array
	 */
	final public Object[] getColumns(int[] ai_columns)
	{
		if (ai_columns.length > iO_Columns.length)
		{
			return null;
		}

		Object[] l_Object = new Object[ai_columns.length];

		for (int li_c = 0; li_c < ai_columns.length; li_c++)
		{
			if (ai_columns[li_c] < iO_Columns.length)
			{
				l_Object[li_c] = iO_Columns[ai_columns[li_c]];
			}
			else
			{
				return null;
			}
		}

		return l_Object;
	}

	/** Column count of row
	 * @return column count
	 */
	public int getColumnCount()
	{
		return i_RowInfo.getColumnCount();
	}

	/** Creates copy of this object.
	 * @return Row (BasicRow) from this object.
	 */
	public Object clone()
	{
		Object[] l_Objects = new Object[iO_Columns.length];

		System.arraycopy(iO_Columns, 0, l_Objects, 0, iO_Columns.length);

		return (Object) new Row(l_Objects, i_RowInfo);
	}

	/** Set all column Values to null.
	 */
	public void setAllNulls()
	{
		if (iO_Columns != null)
		{
			for (int li_c = 0; li_c < iO_Columns.length; li_c++)
			{
				iO_Columns[li_c] = null;
			}
		}
	}

	/** Check if it is equal to another Row object.
	 * @param a_Row Row object to be compared
	 * @return true if equal else false
	 */
	public boolean equals(Row a_Row)
	{
		int li_count = 0;

		for (int li_x = 1; li_x <= getColumnCount(); li_x++)
		{
			if (getValueAt(li_x) != null && a_Row.getValueAt(li_x) != null)
			{
				if (getValueAt(li_x).equals(a_Row.getValueAt(li_x)))
				{
					li_count++;
				}
			}
			else if (getValueAt(li_x) == null && a_Row.getValueAt(li_x) == null)
			{
				li_count++;
			}
		}

		return li_count == getColumnCount();
	}

	public boolean equals(Row a_Row, int[] ai_columns)
	{
		int li_count = 0;

		for (int li_x = 0; li_x < ai_columns.length; li_x++)
		{
			if (getValueAt(ai_columns[li_x]) != null && a_Row.getValueAt(ai_columns[li_x]) != null)
			{
				if (getValueAt(ai_columns[li_x]).equals(a_Row.getValueAt(ai_columns[li_x])))
				{
					li_count++;
				}
			}
			else if (getValueAt(ai_columns[li_x]) == null && a_Row.getValueAt(ai_columns[li_x]) == null)
			{
				li_count++;
			}
		}
		
		return li_count == ai_columns.length;
	}
	


}