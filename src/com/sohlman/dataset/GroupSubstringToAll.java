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
 * This only for Strings
 * 
 * @author Sampsa Sohlman
 * @version 2003-02-27
 */
public class GroupSubstringToAll extends GroupCalc
{
	private int ii_start = 0;
	private int ii_end = 0;
	/**
	 * Constructor for GroupSubstringToAll.
	 * @param ai_columnId
	 */
	public GroupSubstringToAll(int ai_columnId)
	{
		super(ai_columnId);
	}

	/**
	 * Constructor for GroupSubstringToAll.
	 * @param ai_columnId
	 */
	public GroupSubstringToAll(int ai_columnId, int ai_start, int ai_end)
	{
		super(ai_columnId);
		ii_start = ai_start;
		ii_end = ai_end;
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
		
		String l_String = (String)a_Objects[0];
		if(ii_end==0)
		{
			return l_String;
		}
		else
		{
			int li_end = ii_end;
			
			if(l_String.length() < li_end)
			{
				li_end = l_String.length();
			}
			
			return l_String.substring(ii_start,li_end);
		}
	}

}
