/*
 * Row.java
 *
 * Created on 2. heinäkuuta 2001, 13:25
 *
 */

package com.sohlman.dataset;

import com.sohlman.dataset.Row;

/** Row object represent row in {@link DataSet DataSet} object.<br>
 * Row object is immutable, except through the DataSet
 * possible to change values inside of it.<br>
 * <br>
 * <B>Example how to create dataobject.</B>
 * <br><PRE>
 * Object[] l_Objects = {new Integer(li_index),"My Column data","Other column data"};
 * Row l_Row = new Row(l_Objects);
 * </CODE><br>
 *
 * @author Sampsa Sohlman
 * @version 1.0
 */

public class BasicRow implements Row
{

	Object[] iO_Columns = null;
	int ii_columnCount = 0;

	/** Constructor
	 * @param aO_Columns List object columns.
	 * Only basic datacolums are allowed.
	 */
	public BasicRow(Object[] aO_Columns)
	{
		iO_Columns = aO_Columns;
		ii_columnCount = aO_Columns.length;
	}

	/** Returns class name or row.<br>
	 * <b>Example</b></br>
	 * java.lang.String
	 * @param ai_index Index of column
	 * @return Classname of column
	 */
	public final String getClassName(int ai_index)
	{
		if (ai_index < iO_Columns.length)
		{
			return null;
		}
		else
		{
			return iO_Columns[ai_index].getClass().getName();
		}
	}

	/** Set's value for column
	 * @param ai_index Column index
	 * @param a_Object Object for column
	 * @return index of column changed or -1 error
	 */
	public final int setValueAt(int ai_index, Object a_Object)
	{
		if (ai_index > 0 && ai_index <= iO_Columns.length)
		{
			iO_Columns[ai_index - 1] = a_Object;
			return ai_index;
		}
		else
		{
			return -1;
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
		return ii_columnCount;
	}

	/** Creates copy of this object.
	 * @return Row (BasicRow) from this object.
	 */
	public Object clone()
	{
		Object[] l_Objects = new Object[iO_Columns.length];

		System.arraycopy(iO_Columns, 0, l_Objects, 0, iO_Columns.length);

		return (Object) new BasicRow(l_Objects);
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

		for (int li_x = 0; li_x <= ai_columns.length; li_x++)
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