package com.sohlman.dataset.swing;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

import com.sohlman.dataset.DataSet;

/**
 * @author Sampsa Sohlman
 * @version 25.4.2003
 */
public class DataSetListModel implements ListModel
{
	private DataSet i_DataSet;

	public void setDataSet(DataSet a_DataSet)
	{
		i_DataSet = a_DataSet;
	}

	/**
	 * @see javax.swing.ListModel#getSize()
	 */
	public int getSize()
	{
		return i_DataSet.getColumnCount();
	}

	/**
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	public Object getElementAt(int arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener)
	 */
	public void addListDataListener(ListDataListener arg0)
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.ListDataListener)
	 */
	public void removeListDataListener(ListDataListener arg0)
	{
		// TODO Auto-generated method stub

	}

}
