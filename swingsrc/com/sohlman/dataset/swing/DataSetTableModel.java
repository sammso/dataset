package com.sohlman.dataset.swing;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.sohlman.dataset.DataSet;
import com.sohlman.dataset.DataSetEvent;
import com.sohlman.dataset.DataSetListener;
import com.sohlman.dataset.DataSetService;

/**
 * TableModel wrapper for DataSet
 * 
 * 
 * @author Sampsa Sohlman
 * @version 2002-09-27 First version
 * 
 */
public class DataSetTableModel extends AbstractTableModel implements DataSetListener
{
	private DataSet i_DataSet;
	private JTable i_JTable;
	private boolean[] lb_isNotEditableCells = null;
	private boolean ib_autoConversion = true;

	/**
	 *  Conscructs from DataSet
	 * 
	 *  @param a_DataSet DataSet to be used as tablemodel
	 * 
	 */

	public DataSetTableModel(DataSet a_DataSet)
	{
		i_DataSet = a_DataSet;
		i_DataSet.addListener(this);
	}

	/**
	 * Method setAutoConversion
	 * 
	 * If DataSet tries to convert String to convert dataset
	 * 
	 * @param ab_choise
	 */
	public void setAutoConversion(boolean ab_choise)
	{
		ib_autoConversion = true;
	}

	public void setCellEditable(int ai_columnIndex, boolean ab_choise)
	{
		if (ai_columnIndex <= 0 || ai_columnIndex > i_DataSet.getColumnCount())
		{
			throw new ArrayIndexOutOfBoundsException("DataSet index starts from 1");
		}
		if (lb_isNotEditableCells == null)
		{
			int li_columnCount = i_DataSet.getColumnCount();
			if (li_columnCount <= 0)
			{
				throw new ArrayIndexOutOfBoundsException("No columns in DataSet");
			}

			if (li_columnCount > 0)
			{
				lb_isNotEditableCells = new boolean[li_columnCount];
				for (int li_x = 0; li_x < li_columnCount; li_x++)
				{
					lb_isNotEditableCells[li_x] = true;
				}
			}
		}

		if (ai_columnIndex > lb_isNotEditableCells.length)
		{
			throw new ArrayIndexOutOfBoundsException("DataSet maximum column index " + lb_isNotEditableCells.length);
		}

		lb_isNotEditableCells[ai_columnIndex - 1] = ab_choise;
	}

	/**
	 * @see com.sohlman.dataset.DataSet#setValueAt(Object, int, int)
	 */
	public void setValueAt(Object a_Object, int ai_rowIndex, int ai_columnIndex)
	{
		ai_rowIndex++;
		ai_columnIndex++;

		String lS_ColumnClassName = i_DataSet.getColumnClassName(ai_columnIndex);

		boolean lb_error = false;

		if (ib_autoConversion)
		{
			try
			{
				a_Object = DataSetService.StringToSpecifiedObject((String)a_Object, lS_ColumnClassName);
			}
			catch (Exception a_Exception)
			{
				lb_error = true;
			}

		}

		if (!lb_error)
		{
			i_DataSet.setValueAt(a_Object, ai_rowIndex, ai_columnIndex);
		}
	}



	/**
	 * @see com.sohlman.dataset.DataSet#getValueAt(int,int)
	 */
	public Object getValueAt(int ai_rowIndex, int ai_columnIndex)
	{
		ai_rowIndex++;
		ai_columnIndex++;
		return i_DataSet.getValueAt(ai_rowIndex, ai_columnIndex);
	}

	/**
	 * @see com.sohlman.dataset.DataSet#getColumnCount()
	 */
	public int getColumnCount()
	{
		return i_DataSet.getColumnCount();
	}

	public String getColumnName(int ai_index)
	{
		return i_DataSet.getColumnName(ai_index  + 1);
	}

	/**
	 * @see com.sohlman.dataset.DataSet#getRowCount()
	 */
	public int getRowCount()
	{
		return i_DataSet.getRowCount();
	}
	/**
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int,int)
	 */
	public boolean isCellEditable(int ai_rowIndex, int ai_columnIndex)
	{
		if (lb_isNotEditableCells == null)
		{
			return true;
		}

		return lb_isNotEditableCells[ai_columnIndex - 1];
	}

	
	public void dataSetChanged(DataSetEvent a_DataSetEvent)
	{
		if(a_DataSetEvent.getAction() == DataSetEvent.COLUMN_CHANGED)
		{
			if(a_DataSetEvent.getColumn()==DataSetEvent.ALL)
			{
//				fireTableCellUpdated(new TableModelEvent((TableModel)this, a_DataSetEvent.getRow() - 1, a_DataSetEvent.getRow() - 1, TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
			}
			else 
			{
				fireTableCellUpdated(a_DataSetEvent.getRow() - 1, a_DataSetEvent.getColumn() - 1);	
			}
		}
		if(a_DataSetEvent.getAction()==DataSetEvent.READ_END)
		{
			fireTableStructureChanged();
		}
		if(a_DataSetEvent.getAction() == DataSetEvent.ROW_REMOVED)
		{
			fireTableRowsDeleted(a_DataSetEvent.getRow() - 1, a_DataSetEvent.getRow() - 1);
		}
		if(a_DataSetEvent.getAction() == DataSetEvent.ROW_INSERTED)
		{
			fireTableRowsInserted(a_DataSetEvent.getRow() - 1, a_DataSetEvent.getRow() - 1);
		}	
	}
}