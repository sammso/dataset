package com.sohlman.dataset;

import java.util.Comparator;

/**
 *
 * @author  Sampsa Sohlman
 * @version 2002-03-03
 */
public final class DataSetComparator implements Comparator
{    
    private Comparator i_Comparator;
    /** Creates new DataSetComparator */
    public DataSetComparator(Comparator a_Comparator)
    {
	i_Comparator = a_Comparator;
    }
    
    public int compare(Object a_Object_1, Object a_Object_2)
    {
	RowContainer l_RowContainer_1 = (RowContainer)a_Object_1;
	RowContainer l_RowContainer_2 = (RowContainer)a_Object_2;
	return i_Comparator.compare(l_RowContainer_1.i_Row_Current, l_RowContainer_2.i_Row_Current);
    }
    
    public boolean equals(Object a_Object)
    {
	return i_Comparator.equals(a_Object);
    }
}
