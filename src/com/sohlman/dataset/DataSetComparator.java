/*
DataSet Library
---------------
Copyright (C) 2001-2005 - Sampsa Sohlman, Teemu Sohlman

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

import java.util.Comparator;

/**
 * Internal use
 * 
 * @author  Sampsa Sohlman
 * @version 2003-03-21
 */
final class DataSetComparator implements Comparator
{
	private Comparator i_Comparator;
	/** Creates new DataSetComparator */
	public DataSetComparator(Comparator a_Comparator)
	{
		if(a_Comparator==null)
		{
			throw new NullPointerException("Null value not accepted");
		}
		i_Comparator = a_Comparator;
	}

	public int compare(Object a_Object_1, Object a_Object_2)
	{
		RowContainer l_RowContainer_1 = (RowContainer) a_Object_1;
		RowContainer l_RowContainer_2 = (RowContainer) a_Object_2;	
			
		return i_Comparator.compare((Object)l_RowContainer_1.i_Row_Current, (Object)l_RowContainer_2.i_Row_Current);
	}

	public boolean equals(Object a_Object)
	{
		return i_Comparator.equals(a_Object);
	}
	
	public Comparator getComparator()
	{
		return i_Comparator;
	}
}
