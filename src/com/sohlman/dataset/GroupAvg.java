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


/**
 * @author Sampsa Sohlman
 * @version 2003-02-26
 */
public class GroupAvg extends GroupSum
{

	/**
	 * Constructor for GroupAvg.
	 * @param ai_columnId
	 */
	public GroupAvg(int ai_columnId)
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
		return (Object)divide((Number)super.calculateGroupBy(a_Objects), convertToNumber(a_Objects[0].getClass(), a_Objects.length)) ;		
	}		
}
