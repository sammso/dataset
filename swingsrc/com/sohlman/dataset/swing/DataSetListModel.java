package com.sohlman.dataset.swing;

import javax.swing.AbstractListModel;
import javax.swing.event.ListDataListener;

import com.sohlman.dataset.DataSet;
import com.sohlman.dataset.DataSetEvent;
import com.sohlman.dataset.DataSetListener;

/**
 * @author Sampsa Sohlman
 * @version 25.4.2003
 */
public class DataSetListModel extends AbstractListModel 
{
	private int ii_maxListSize = 100;
	private int ii_visibleColumn;
	private DataSet i_DataSet;
	private DataSetListModel i_DataSetListModel_This = this;
	private DataSetListener i_DataSetListener = new DataSetListener()
	{
		/**
		 * @see com.sohlman.dataset.DataSetListener#dataSetChanged(com.sohlman.dataset.DataSetEvent)
		 */
		public void dataSetChanged(DataSetEvent a_DataSetEvent)
		{
			if(a_DataSetEvent.getAction()==DataSetEvent.READ_END)
			{
				fireIntervalAdded(i_DataSetListModel_This, i_DataSet.getRowCount(), 0);
			}
			if(a_DataSetEvent.getAction()==DataSetEvent.COLUMN_CHANGED)
			{
				if(ii_visibleColumn == a_DataSetEvent.getColumn())
				{
					fireContentsChanged(i_DataSetListModel_This, a_DataSetEvent.getRow() - 1, a_DataSetEvent.getRow() - 1);
				}
			}
		}
	};

	public void setVisibleColumn(int ai_visibleColumn)
	{
		ii_visibleColumn = ai_visibleColumn;
	}

	public void setDataSet(DataSet a_DataSet)
	{
		i_DataSet = a_DataSet;
	}
	
	
	/**
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	public Object getElementAt(int ai_index)
	{
		return i_DataSet.getValueAt(ai_index + 1 ,ii_visibleColumn);
	}

	/**
	 * @see javax.swing.ListModel#getSize()
	 */
	public int getSize()
	{
		return i_DataSet.getRowCount();
	}

}
