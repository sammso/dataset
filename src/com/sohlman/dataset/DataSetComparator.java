package com.sohlman.dataset;

import java.util.Comparator;

/**
 * Internal use
 * 
 * @author  Sampsa Sohlman
 * @version 2002-11-08
 */
final class DataSetComparator implements Comparator
{
	private RowComparator i_RowComparator;
	/** Creates new DataSetComparator */
	public DataSetComparator(RowComparator a_RowComparator)
	{
		if(a_RowComparator==null)
		{
			throw new NullPointerException("Null value not accepted");
		}
		i_RowComparator = a_RowComparator;
		System.out.println(a_RowComparator);
	}

	public int compare(Object a_Object_1, Object a_Object_2)
	{
		RowContainer l_RowContainer_1 = (RowContainer) a_Object_1;
		RowContainer l_RowContainer_2 = (RowContainer) a_Object_2;	
			
		return i_RowComparator.compare(l_RowContainer_1.i_Row_Current, l_RowContainer_2.i_Row_Current);
	}

	public boolean equals(Object a_Object)
	{
		return i_RowComparator.equals(a_Object);
	}
	
	public RowComparator getRowComparator()
	{
		return i_RowComparator;
	}
}
