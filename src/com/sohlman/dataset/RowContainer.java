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
 * @author  Sampsa Sohlman
 * @version 2002-09-10
 */
public class RowContainer
{
    Row i_Row_Orig = null;
    Row i_Row_Current = null;
    
    /** Creates new RowContainer */
    public RowContainer(Row a_Row_Orig, Row a_Row_Current)
    {
		i_Row_Orig = a_Row_Orig;	
		i_Row_Current = a_Row_Current;
    }
    
    public boolean equals(RowContainer a_RowContainer)
    {
		return i_Row_Current.equals(a_RowContainer.i_Row_Current);
    }
    
	/**
	 * returns original row after read in DataSet.
	 *
	 * @return Row Origigal row.
	 */
    public Row getOrigRow()
    {
    	return i_Row_Orig;
    } 
    
	/**
	 * Returns current row. 
	 * 
	 * @return Row Current row in DataSet
	 */
    public Row getRow()
    {
    	return i_Row_Current;
    }
	/**
	 * Returns current row. 
	 * (helps debugging)
	 * 
	 * @return Row Current row in DataSet
	 */    
    public String toString()
    {
    	return i_Row_Current.toString();
    }
}
