package com.sohlman.dataset;

import java.util.Comparator;

/**
 * @author Sampsa Sohlman
 * 
 * @version 2002-11-08
 */
public class RowComparator implements Comparator
{
	private int[] ii_sortOrder; 
	
	public RowComparator(int[] ai_sortOrder)
	{
		ii_sortOrder = ai_sortOrder;	
	}	
	
	public RowComparator()
	{
		ii_sortOrder=null;
	}
	
	/**
	 * @see java.util.Comparator#compare(Object, Object)
	 */
	public final int compare(Object aO_Row1, Object aO_Row2)
	{	
		return compare((Row)aO_Row1, (Row)aO_Row2);
	}	
	
	public int compare(Row a_Row_1, Row a_Row_2)
	{	
		if(ii_sortOrder==null)
		{
			int li_columnCount = a_Row_1.getColumnCount();
			
			for(int li_index = 1 ; li_index <= li_columnCount ; li_index ++)
			{
				int li_value = DataSetService.compareComparables((Comparable)a_Row_1.getValueAt(li_index), (Comparable)a_Row_2.getValueAt(li_index));
				if(li_value > 0)
				{
					return li_value;
				}
				else if(li_value < 0)
				{
					return li_value;
				}
			}
		}
		else
		{
			int li_columnCount = a_Row_1.getColumnCount();	
			for(int li_index = 0 ; li_index < ii_sortOrder.length ; li_index ++)
			{
				// Ignore wrong indexes
				if( ii_sortOrder[li_index] > 0 && ii_sortOrder[li_index] <= li_columnCount )
				{
					int li_value = DataSetService.compareComparables((Comparable)a_Row_1.getValueAt(ii_sortOrder[li_index]), (Comparable)a_Row_2.getValueAt(ii_sortOrder[li_index]));
					if(li_value > 0)
					{
						return li_value;
					}
					else if(li_value < 0)
					{
						return li_value;
					}
				}
			}			
		}
		return 0;	
	}
}
