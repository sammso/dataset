package com.sohlman.dataset;

/** UpdateAction tells the DataSet what kind of action it should take. Modify or Delete + Insert
 *
 * @author Sampsa Sohlman
 * @version 2001-08-15
 */
public interface KeyAction
{
	/** This method tells what kind of action should be taken, when DataSet row is modified.<br>
	 * Just normal modify<br>
	 * <b>OR</b>
	 * Delete + Insert<b>
	 * @param a_Row_Orig This parameter contains original row.
	 * @param a_Row_New This parameter contains new Row object
	 * @return <b>true</b> if key is modified<br>
	 * <b>false</b> if key is not modified<br>
	 */	
	public boolean isKeyModified(Row a_Row_Orig, Row a_Row_New);
}

