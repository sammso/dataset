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
 * @version
 */
class RowContainer
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
}
