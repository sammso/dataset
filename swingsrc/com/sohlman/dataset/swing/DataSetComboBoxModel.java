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
package com.sohlman.dataset.swing;

import javax.swing.ComboBoxModel;

import com.sohlman.dataset.DataSet;

/**
 * DataSetComboBox is wrapper to put ComboBox to use DataSet data 
 * 
 * @author Sampsa Sohlman
 * @version 25.4.2003
 */
public class DataSetComboBoxModel extends DataSetListModel implements ComboBoxModel
{
	private int ii_selectedItem = 0;	
	
	/**
	 * 
	 * @param a_DataSet DataSet to wrap
	 * @param ai_visibleColumnNumber Which DataSet column want to be shown on ComboBox
	 */
	public DataSetComboBoxModel(DataSet a_DataSet, int ai_visibleColumnNumber)
	{
		super(a_DataSet, ai_visibleColumnNumber);
	}
	
	/**
	 * @see javax.swing.ComboBoxModel#setSelectedItem(java.lang.Object)
	 */
	public void setSelectedItem(Object a_Object)
	{
		ii_selectedItem = i_DataSet.search(a_Object,ii_visibleColumn);
	}
	
	/**
	 * Set selected item by selecting from DataSet index.
	 * 
	 * @param ai_index
	 */
	public void setSelectedItem(int ai_index)
	{
		ii_selectedItem = ai_index;
	}
	
	/**
	 * @return selected item index (dataset row index)
	 */
	public int getSelectedItemIndex()
	{
		return ii_selectedItem;
	}

	/**
	 * @see javax.swing.ComboBoxModel#getSelectedItem()
	 */
	public Object getSelectedItem()
	{
		if(ii_selectedItem >= 1 && ii_selectedItem <= i_DataSet.getRowCount())
		{
			return i_DataSet.getValueAt(ii_selectedItem, ii_visibleColumn);
		}
		else
		{
			return null;
		}
	}
}
