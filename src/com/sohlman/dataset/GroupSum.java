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


/**
 * @author Sampsa Sohlman
 * @version 2003-02-25
 */
public class GroupSum extends GroupCalc
{

	/**
	 * Constructor for GroupSum.
	 * @param ai_columnId
	 */
	public GroupSum(int ai_columnId)
	{
		super(ai_columnId);
	}	
	/**
	 * @see com.sohlman.dataset.GroupCalc#calculateGroupBy(Object[])
	 */
	public Object calculateGroupBy(Object[] a_Objects)
	{
		if(a_Objects==null)
		{
			return null;	
		}
		if(a_Objects.length==1)
		{
			return a_Objects[0];
		}
		Number l_Number = (Number)a_Objects[0];
		
		for(int li_index = 1 ;li_index < a_Objects.length ; li_index++)
		{
			l_Number = sum(l_Number,(Number)a_Objects[li_index]);
		}
		return (Object)l_Number;
	}
}
