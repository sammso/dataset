package com.sohlman.dataset.swing;

import javax.swing.AbstractListModel;

import com.sohlman.dataset.DataSet;
import com.sohlman.dataset.DataSetEvent;
import com.sohlman.dataset.DataSetListener;

/**
 * @author Sampsa Sohlman
 * @version 25.4.2003
 */
public class DataSetListModel extends AbstractListModel
{
	protected int ii_visibleColumn;
	protected DataSet i_DataSet;
	private DataSetListModel i_DataSetListModel_This = this;
	
	private DataSetListener i_DataSetListener = new DataSetListener()
	{
		/**
		 * @see com.sohlman.dataset.DataSetListener#dataSetChanged(com.sohlman.dataset.DataSetEvent)
		 */
		public void dataSetChanged(DataSetEvent a_DataSetEvent)
		{
			if (a_DataSetEvent.getAction() == DataSetEvent.COLUMN_CHANGED)
			{
				if (ii_visibleColumn == a_DataSetEvent.getColumn())
				{
					fireContentsChanged(i_DataSetListModel_This, a_DataSetEvent.getRow() - 1, a_DataSetEvent.getRow() - 1);
				}
			}
			else if (a_DataSetEvent.getAction() == DataSetEvent.READ_END)
			{
				fireIntervalAdded(i_DataSetListModel_This, i_DataSet.getRowCount(), 0);
			}
			else if(a_DataSetEvent.getAction() == DataSetEvent.RESET)
			{
								
			}
			else if (a_DataSetEvent.getAction() == DataSetEvent.ROW_INSERTED)
			{
				fireContentsChanged(i_DataSetListModel_This, a_DataSetEvent.getRow() , a_DataSetEvent.getRow());
			}
			else if (a_DataSetEvent.getAction() == DataSetEvent.ROW_REMOVED)
			{
				fireContentsChanged(i_DataSetListModel_This, 0 , i_DataSet.getRowCount());
			}
		}
	};

	/**
	 * Wraps DataSet so it is possible to use inside JList
	 * 
	 * @param a_DataSet DataSet to wrap inside to JList
	 * @param ai_visibleColumnNumber  Which DataSet column want to be shown on JList
	 */
	public DataSetListModel(DataSet a_DataSet, int ai_visibleColumnNumber)
	{
		i_DataSet = a_DataSet;
		i_DataSet.addListener(i_DataSetListener);
		ii_visibleColumn = ai_visibleColumnNumber;
	}

	/**
	 * Set visible column from DataSet to ListModel
	 * 
	 * @param ai_visibleColumn
	 */
	public void setVisibleColumn(int ai_visibleColumn)
	{
		ii_visibleColumn = ai_visibleColumn;
	}

	/**
	 * Set DataSet
	 * 
	 * @param a_DataSet DataSet
	 */
	public void setDataSet(DataSet a_DataSet)
	{
		i_DataSet = a_DataSet;
	}

	/**
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	public Object getElementAt(int ai_index)
	{
		return i_DataSet.getValueAt(ai_index + 1, ii_visibleColumn);
	}

	/**
	 * @see javax.swing.ListModel#getSize()
	 */
	public int getSize()
	{
		return i_DataSet.getRowCount();
	}

}
