package com.sohlman.dataset;

/** With KeyColumnsAction object is possible to
 * define which columns act like key columns.
 * If these columns are changed DataSet perform Delete &
 * Insert operation in it's buffers.<br>
 *
 * @author Sampsa Sohlman
 * @version 2001-09-17
 */
public class KeyColumnsAction implements com.sohlman.dataset.ModifyAction
{
    /** List of columns
     */    
	private int[] ii_columns;
	/** Constructor
	 * @param ai_columns Column numbers that are doing the updates.
	 * 0 is first column.
	 */
	public KeyColumnsAction(int[] ai_columns)
	{
		ii_columns = ai_columns;
	}
	
	/** This only DataSet use only 
	 * @param a_Row_Orig Object of Original row object
	 * @param a_Row_New New Row object
	 * @return True if they delete & insert operation is made othervice false
	 *
	 */	
	public boolean isKeyModified(Row a_Row_Orig, Row a_Row_New)
	{
		for(int li_a = 0; li_a < ii_columns.length ; li_a++)
		{
			if(!a_Row_Orig.getValueAt(ii_columns[li_a]).equals(a_Row_New.getValueAt(ii_columns[li_a])))
			{
				return true;
			}
		}
		return false;
	}
}
