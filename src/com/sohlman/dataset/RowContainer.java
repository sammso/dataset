/*
 * RowContainer.java
 *
 * Created on 8. joulukuuta 2001, 16:03
 */

package com.sohlman.dataset;

import com.sohlman.dataset.*;

/**
 *
 * @author  Sampsa Sohlman
 * @version 2001-09-10
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
}
