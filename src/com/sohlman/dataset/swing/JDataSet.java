package com.sohlman.dataset.swing;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.JTable;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;

import com.sohlman.dataset.DataSet;
import com.sohlman.dataset.DataSetListener;

/**
 * TableModel wrapper for DataSet
 * 
 * 
 * @author Sampsa Sohlman
 * @version 2002-09-27 First version
 * 
 */
public class JDataSet extends AbstractTableModel implements DataSetListener
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

	public JDataSet(DataSet a_DataSet)
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
		if (ai_columnIndex <= 0)
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
				a_Object = StringToSpecifiedType(a_Object, lS_ColumnClassName);
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

	public Object StringToSpecifiedType(Object a_Object, String aS_ClassName)
	{
		if (a_Object == null)
		{
			return null;
		}
		if (aS_ClassName.equals("java.lang.Boolean"))
		{
			return new Boolean(Boolean.getBoolean((String) a_Object));
		}
		if (aS_ClassName.equals("java.lang.Byte"))
		{
			return new Byte(Byte.parseByte((String) a_Object));
		}
		if (aS_ClassName.equals("java.lang.BigInteger"))
		{
			return new BigInteger((String) a_Object);
		}
		if (aS_ClassName.equals("java.lang.BigDecimal"))
		{
			return new BigDecimal((String) a_Object);
		}
		if (aS_ClassName.equals("java.sql.Date") || aS_ClassName.equals("java.util.Date"))
		{
			return Date.valueOf((String) a_Object);
		}

		if (aS_ClassName.equals("java.sql.Time") || aS_ClassName.equals("java.util.Time"))
		{
			return Time.valueOf((String) a_Object);
		}

		if (aS_ClassName.equals("java.sql.Timestamp"))
		{
			return Timestamp.valueOf((String) a_Object);
		}

		if (aS_ClassName.equals("java.lang.Double"))
		{
			return new Double(Double.parseDouble((String) a_Object));
		}
		if (aS_ClassName.equals("java.lang.Float"))
		{
			return new Float(Float.parseFloat((String) a_Object));
		}

		if (aS_ClassName.equals("java.lang.Integer"))
		{
			return new Integer(Integer.parseInt((String) a_Object));
		}

		if (aS_ClassName.equals("java.lang.String"))
		{
			return a_Object.toString();
		}

		throw new IllegalArgumentException(a_Object.getClass().getName() + " is not supported class type");
	}

	/**
	 * @see com.sohlman.dataset.DataSet#getValueAt()
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

	/**
	 * @see com.sohlman.dataset.DataSet#getRowCount()
	 */
	public int getRowCount()
	{
		return i_DataSet.getRowCount();
	}

	public boolean isCellEditable(int ai_rowIndex, int ai_columnIndex)
	{
		if (lb_isNotEditableCells == null)
		{
			return true;
		}

		return lb_isNotEditableCells[ai_columnIndex - 1];
	}

	public void rowInserted(int ai_rowIndex)
	{
		fireTableRowsInserted(ai_rowIndex - 1, ai_rowIndex - 1);		
	}

	public void rowModified(int ai_rowIndex, int a_columnIndex)
	{
		fireTableCellUpdated(ai_rowIndex - 1, a_columnIndex - 1);		
	}

	public void rowRemoved(int ai_rowIndex)
	{
		fireTableRowsDeleted(ai_rowIndex - 1, ai_rowIndex - 1);
	}

	public void readStart()
	{
	}

	public void readEnd(int ai_rowCount)
	{
		fireTableStructureChanged();
	}

	public void writeStart()
	{
	}

	public void writeEnd(int ai_rowCount)
	{
	}
}
