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
package com.sohlman.dataset;


/** Row object represent row in {@link DataSet DataSet} object.<br>
 *
 * @author Sampsa Sohlman
 * @version 2002-10-09
 */

public class Row
{
	public final static Row NO_MORE_ROWS = null;
	public final static int ERROR = -1;

	Object[] iO_Columns;
	Object[] iO_Columns_Current;
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
	 * @throws ClassCastException if column class is different than defined column class
	 * ArrayIndexOutOfBoundsException if array is out of range
	 */
	public final int setValueAt(int ai_index, Object a_Object)
	{
		if (ai_index > 0 && ai_index <= iO_Columns.length)
		{
			if(a_Object==null)
			{
				iO_Columns[ai_index - 1] = null;	
			}
			else if(i_RowInfo.getColumnClass(ai_index).isInstance(a_Object))
			{
				iO_Columns[ai_index - 1] = a_Object;
			}
			else
			{
				throw new ClassCastException("DataSet column class definition for column '" + ai_index + "' different than what is tried to set.\r\nColumn class definition : " + i_RowInfo.getColumnClass(ai_index).getName() + "\r\nAnd tried to set : " + a_Object.getClass().getName());
				//ai_index = -1;
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
	 * @throws ArrayIndexOutOfBoundsException if row or column is out of range
	 */
	public final Object getValueAt(int ai_index)
	{
		if (ai_index > 0 && ai_index <= iO_Columns.length)
		{
			return iO_Columns[ai_index - 1];
		}
		else
		{
			throw new ArrayIndexOutOfBoundsException("Column index " + ai_index + " is out of range");
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
	
	public String toString()
	{
		StringBuffer l_StringBuffer = new StringBuffer();
		l_StringBuffer.append(" : ");		
		for (int li_x = 0; li_x <iO_Columns.length; li_x++)
		{
			l_StringBuffer.append(iO_Columns[li_x]);
			l_StringBuffer.append(" : ");			
		}		
		return l_StringBuffer.toString();
	}
}