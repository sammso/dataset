package com.sohlman.dataset;

import com.sohlman.dataset.Row;
import com.sohlman.dataset.DataSetException;
import com.sohlman.dataset.KeyAction;
import java.util.Vector;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.io.PrintStream;
import java.util.List;
/** 
 * <p>DataSet is common component to handle data in table from.
 * variable sources like SQL database, tabular file, XML file etc</p>
 * <p><b>Usage</b></p>
 * <ol>        
 * <li>Create Row model object and assign it to to dataset by using setRowModel() method.<b><i>(mandatory)</i></b> <i>ReadEngine also have possibility to create new row model.</i></i></li>
 * <li>Implement ReadEngine class <b><i>(optional)</i></b></li>
 * <ul>        
 * 	There is no need to create {@link ReadEngine ReadEngine} or some common solution is used like {@link com.sohlman.library.dataset.sql.SQLReadEngine SQLReadEngine}.
 * </ul>       
 * <li>Implement {@link WriteEngine WriteEngine} class <b><i>(optional)</i></b></li>
 * <ul>        
 * 	There is no need to create WriteEngine if datasource is readonly(*) or some common solution is used like {@link com.sohlman.library.dataset.sql.SQLWriteEngine SQLWriteEngine}.<br>
 * 	<i>(*)Data is still updateable through datamodification methods, but save() method returns -1.</i>
 * </ul>       
 * <li>Implement {@link KeyAction KeyAction} <b><i>(optional)</i></b></li>
 * <li>Instantiate DataSet</li>
 * <li>Assign {@link ReadEngine ReadEngine}, {@link WriteEngine WriteEngine}, {@link KeyAction KeyAction} to DataSet <b><i>(optional)</i></b></li>
 * <li>Read data, using read() method<b> <i>(optional)</i></b></li>
 * <li>Modify data using addRow, insertRow(), RemoveRow(), setItemAt() and setRowAt()  methods<b> <i>(optional)</i></b></li>
 * <li>Save changes, using save() <b> <i>(optional)</i></b>
 * </ol>               
 * @author Sampsa Sohlman
 * @version 0.9
 */

public class DataSet
{
	/** This buffer contains "visible" data.
	 */
	private DataSetVector iVe_Data;
	/** This buffer contains data which is deleted.
	 */
	private Vector iVe_Deleted;
	/** This buffer contains links to new inserted data.
	 */
	private Vector iVe_New;
	/** This buffer contains links to modified data
	 */
	private Vector iVe_Modified;
	/** Column names
	 */
	private String[] iS_ColumnNames;

	/** This example row object.
	 */
	private Row i_Row_ModelObject;

	// Write engines
	/** Write engine which will only write changes back to source.
	 */
	private WriteEngine i_WriteEngine = null;
	/** If row model is created or not. Some ReadEngines might create Row model automaticly, if it is not defined.
	 */
	private boolean ib_rowModelObjectExist = false;

	// Read engins
	/** Read engine, which reads data row by row.
	 */
	private ReadEngine i_ReadEngine = null;

	private DataSetComparator i_DataSetComparator = null;

	/** With key action is possible deside if we hare doing just modify or insert / delete
	 */
	private KeyAction i_KeyAction = null;
	// Array which describes column class names

	private String[] iS_ColumnClasses;

	private Vector iVe_Listeners;
	
	public final static int NO_MORE_ROWS = -1;

	/**
	 */
	private final static int READ = 1;
	private final static int SAVE = 2;
	private final static int INSERT = 3;
	private final static int REMOVE = 4;
	private final static int RESET = 5;
	private final static int SET = 6;
	private final static int SETROW = 7;
	/** Describes error situation.
	 */
	public final static int ERROR = 0;
	/** Row status new
	 */
	public final static int NEW = 1;
	/** Row status modified
	 */
	public final static int MODIFIED = 2;
	/** Row status not modified
	 */
	public final static int NOTMODIFIED = 4;

	/** Creates new DataSet
	 */

	public DataSet()
	{
		iVe_Data = new DataSetVector();
		iVe_Deleted = new Vector();
		iVe_Modified = new Vector();
		iVe_New = new Vector();
	}

	/** Set's Row object which is used as model when creating new rows.<br>
	 * It has to contain all fields without null values.
	 * @param a_Row Row object which is used as model.
	 */
	public void setModelRowObject(Row a_Row)
	{
		reset();
		if (a_Row != null)
		{
			ib_rowModelObjectExist = true;
			i_Row_ModelObject = a_Row;
		}
		else
		{
			ib_rowModelObjectExist = false;
			i_Row_ModelObject = null;
		}
	}
	
	/** Set's defintion of rows as types of objects. These objects must be cloneable.<br>
	 * It has to contain all fields without null values.
	 * @param aS_ClassNames array of class names that object contains
	 */	
	public void defineRow(String[] aS_ClassNames)
	{
		setModelRowObject(new BasicRow(aS_ClassNames));
	}
	

	/** Adds listener to DataSet
	 * @param a_DataSetListener Object implementing DataSetListener inteface
	 */
	public void addListener(DataSetListener a_DataSetListener)
	{
		if (iVe_Listeners == null)
		{
			iVe_Listeners = new Vector();
		}

		iVe_Listeners.add(a_DataSetListener);
	}

	/** Removes listener
	 * @param a_DataSetListener Listener what is wanted to remove.
	 */
	public void removeListener(DataSetListener a_DataSetListener)
	{
		if (iVe_Listeners != null)
		{
			iVe_Listeners.remove(a_DataSetListener);
			if (iVe_Listeners.isEmpty())
			{
				iVe_Listeners = null;
			}
		}
	}

	/** Set columnNames for DataSet.
	 * @param aS_ColumnNames Array of columnNames.
	 */
	public final void setColumnNames(String[] aS_ColumnNames)
	{
		iS_ColumnNames = aS_ColumnNames;
	}

	/** Creates new empty row, to wanted position.<br>
	 * <b>This method is thread safe</b>
	 * @return Position where row was created.<br>-1 if now row created.
	 * @param ai_index Number of row where new row is to be inserted. All the other rows are moved down.<br>
	 * If -1 then act as addRow()
	 */
	public final int insertRow(int ai_index)
	{
		int li_return = 0;

		li_return = doInsertRow(ai_index);

		if (li_return > 0 && iVe_Listeners != null)
		{
			Enumeration l_Enumeration;
			l_Enumeration = iVe_Listeners.elements();
			while (l_Enumeration.hasMoreElements())
			{
				((DataSetListener) l_Enumeration.nextElement()).rowInserted(li_return);
			}
		}
		return li_return;
	}

	/** Creates new empty row end of DataSet.<br>
	 * <b>This method is thread safe</b>
	 * @return Position where row was created.<br>-1 if now row created.
	 */
	public final int addRow()
	{
		int li_return = 0;
		li_return = insertRow(-1);

		if ( li_return > 0  && iVe_Listeners != null)
		{
			Enumeration l_Enumeration;
			l_Enumeration = iVe_Listeners.elements();
			while (l_Enumeration.hasMoreElements())
			{
				((DataSetListener) l_Enumeration.nextElement()).rowInserted(li_return);
			}
		}

		return li_return;
	}

	/** Removes row from DataSet.<br>
	 * <b>This method is thread safe</b>
	 * @return Old index of removed row<br>
	 * -1 if remove failed.
	 * @param ai_index Row index for row to be removed.
	 */
	public final int removeRow(int ai_index)
	{
		int li_return = 0;
		// doReset never throws exception so we don't need to catch it.

			li_return = doRemoveRow(ai_index);
		if (li_return > 0 && iVe_Listeners != null)
		{
			Enumeration l_Enumeration;
			l_Enumeration = iVe_Listeners.elements();
			while (l_Enumeration.hasMoreElements())
			{
				((DataSetListener) l_Enumeration.nextElement()).rowRemoved(ai_index);
			}
		}
		return li_return;
	}

	/** Resets all buffers.<br>
	 * <b>This method is thread safe</b>
	 */
	public final void reset()
	{
		if (iVe_Data != null)
		{
			iVe_Data.removeAllElements();
		}
		if (iVe_New != null)
		{
			iVe_New.removeAllElements();
		}
		if (iVe_Modified != null)
		{
			iVe_Modified.removeAllElements();
		}

		if (iVe_Deleted != null)
		{
			iVe_Deleted.removeAllElements();
		};
	}

	/** Read data with using ReadEngine
	 * @throws DataSetException Throws dataSet exception on error situation.
	 * @return Number of rows read.
	 */
	public int read() throws DataSetException
	{
		int li_return;

		if (iVe_Listeners != null)
		{
			Enumeration l_Enumeration;
			l_Enumeration = iVe_Listeners.elements();
			while (l_Enumeration.hasMoreElements())
			{
				((DataSetListener) l_Enumeration.nextElement()).readStart();
			}
		}

		li_return = doRead();

		if (iVe_Listeners != null)
		{
			Enumeration l_Enumeration;
			l_Enumeration = iVe_Listeners.elements();
			while (l_Enumeration.hasMoreElements())
			{
				((DataSetListener) l_Enumeration.nextElement()).readEnd(li_return);
			}
		}
		return li_return;
	}

	/** Writes data with using WriteEngine
	 * @throws DataSetException Throws dataSet exception on error situation.
	 * @return Count of rows that has been written.
	 */
	public int save() throws DataSetException
	{
		int li_return;

		if (iVe_Listeners != null)
		{
			Enumeration l_Enumeration;
			l_Enumeration = iVe_Listeners.elements();
			while (l_Enumeration.hasMoreElements())
			{
				((DataSetListener) l_Enumeration.nextElement()).writeStart();
			}
		}
		li_return = doSave();
		if (iVe_Listeners != null)
		{
			Enumeration l_Enumeration;
			l_Enumeration = iVe_Listeners.elements();
			while (l_Enumeration.hasMoreElements())
			{
				((DataSetListener) l_Enumeration.nextElement()).writeEnd(li_return);
			}
		}
		return li_return;
	}

	/** Modify whole row object.
	 * @param ai_row Row number to be modified.
	 * @param a_Row New row object that will replace current one.
	 * @return Number where row is inserted or -1 if error has occurred.
	 */
	private final int doModifyRow(int ai_index, Row a_Row)
	{
		if (ai_index > 0 && ai_index <= iVe_Data.size())
		{
			RowContainer l_RowContainer = (RowContainer) iVe_Data.get(ai_index - 1);
			Row l_Row = l_RowContainer.i_Row_Current;

			if (l_RowContainer != null)
			{
				if (i_KeyAction != null && l_RowContainer.i_Row_Orig != null && i_KeyAction.isKeyModified(l_Row, a_Row))
				{
					doRemoveRow(ai_index);
					doInsertRow(ai_index, new RowContainer(null, a_Row));
					return ai_index;
				}
				else
				{
					l_RowContainer.i_Row_Current = a_Row;
					if (iVe_New.indexOf(l_RowContainer) == -1)
					{
						if (iVe_Modified.indexOf(l_RowContainer) == -1)
						{
							iVe_Modified.add(l_RowContainer);
						}
					};
					return ai_index;
				}
			}
		}
		return -1;
	}

	/** Modify one cell in DataSet.
	 * @param ai_rowIndex Index of row
	 * @param ai_columnIndex Column index. 0 is first.
	 * @param a_Object Replacing object.<br>
	 * <i>Note replacing object has to be same datatype.</i>
	 * @return index of row which is replaced or<br>
	 * -1 if error
	 */
	public final boolean setValueAt(Object a_Object, int ai_rowIndex, int ai_columnIndex)
	{
		if (doSetValueAt(a_Object, ai_rowIndex, ai_columnIndex) > 0)
		{
			if (iVe_Listeners != null)
			{
				Enumeration l_Enumeration;
				l_Enumeration = iVe_Listeners.elements();
				while (l_Enumeration.hasMoreElements())
				{
					((DataSetListener) l_Enumeration.nextElement()).rowModified(ai_rowIndex, ai_columnIndex);
				}
			}
			return true;
		}
		else
		{
			return false;
		}
	}

	/** Get certain value from row or column.
	 * @param ai_rowIndex Row to read value
	 * @param ai_columnIndex Column to read value
	 * @return Column. This must be casted right object type.
	 */
	public final Object getValueAt(int ai_rowIndex, int ai_columnIndex)
	{
		Object l_Object = null;
		if (ai_rowIndex > 0 && ai_rowIndex <= iVe_Data.size())
		{
			// Because of multithreading it is not sure that we will get handle
			// for the row.
			try
			{
				RowContainer l_RowContainer;
				l_RowContainer = (RowContainer) iVe_Data.get(ai_rowIndex - 1);
				Row l_Row = l_RowContainer.i_Row_Current;
				l_Object = l_Row.getValueAt(ai_columnIndex);
			}
			catch (ArrayIndexOutOfBoundsException l_ArrayIndexOutOfBoundsException)
			{
				l_ArrayIndexOutOfBoundsException.printStackTrace();
			}
		}
		return l_Object;
	}

	/** Set set KeyAction object for DataSet<br>
	 * With key action is possible deside if we hare doing just 'modify' or 'insert / delete' when setting item.<br>
	 * If this method is not all the objects are always considered as modified.
	 * @param a_KeyAction Refrence to KeyAction Object
	 */
	public final void setKeyAction(KeyAction a_KeyAction)
	{
		i_KeyAction = a_KeyAction;
	}

	/** Set setReadEngine for DataSet
	 * @param a_ReadEngine Assigned read engine.
	 */
	public final void setReadEngine(ReadEngine a_ReadEngine)
	{
		// future when many types of
		// write engines exists. Do check and set all other nulls.

		i_ReadEngine = a_ReadEngine;
	}

	/** Set WriteEngine for DataSet
	 * @param a_WriteEngine Refrence to new WriteEngine.
	 */
	public final void setWriteEngine(WriteEngine a_WriteEngine)
	{
		// future when many types of
		// write engines exists. Do check and set all other nulls.

		i_WriteEngine = a_WriteEngine;
	}

	/** Removes ReadEngine from DataSet
	 */
	public final void removeReadEngine()
	{
		i_ReadEngine = null;
	}

	/** Removes KeyAction object from DataSet
	 */
	public final void removeKeyAction()
	{
		i_KeyAction = null;
	}

	/** Removes WriteEngine from DataSet
	 */
	public final void removeWriteEngine()
	{
		i_WriteEngine = null;
	}

	/** Return allowed column class.
	 * @param ai_columnIndex Index of column that you want to study
	 * @return String class name of object that is stored to column.
	 */
	public String getColumnClassName(int ai_columnIndex)
	{
		return i_Row_ModelObject.getClassName(ai_columnIndex);
	}

	/** Return column count for DataSet
	 * @return Number of columns
	 */
	public int getColumnCount()
	{
		// FUTURE
		if (i_Row_ModelObject != null)
		{
			return i_Row_ModelObject.getColumnCount();
		}
		else
		{
			return -1;
		}
	}

	/** Returns current column name for index.<br>
	 * Requires tha column names are set.
	 * @param ai_index Column index
	 * @return Column name
	 */
	public String getColumnName(int ai_index)
	{
		return iS_ColumnNames[ai_index];
	}

	/** Returns row count of DataSet
	 * @return Row count of DataSet
	 */
	public int getRowCount()
	{
		return iVe_Data.size();
	}

	/** <b>This is for Debugging.</b><br><br>
	 * Prints buffrers to PrintStream
	 * <b>Future will be removed</b>
	 * @param a_PrintStream PrintStream where to write debug information
	 */
	public void printBuffers(PrintStream a_PrintStream)
	{
		a_PrintStream.println("*******************************************************");
		a_PrintStream.println("Databuffer");
		a_PrintStream.println("-------------------------------------------------------");
		printBuffer(iVe_Data, a_PrintStream);
		a_PrintStream.println("NewBuffer");
		a_PrintStream.println("-------------------------------------------------------");
		printBuffer(iVe_New, a_PrintStream);
		a_PrintStream.println("ModifiedBuffer");
		a_PrintStream.println("-------------------------------------------------------");
		printBuffer(iVe_Modified, a_PrintStream);
		a_PrintStream.println("DeleteBuffer");
		a_PrintStream.println("-------------------------------------------------------");
		printBuffer(iVe_Deleted, a_PrintStream);
		a_PrintStream.println("*******************************************************");
	}

	/**
	 * @param aVe_Buffer
	 * @param a_PrintStream
	 */
	private void printBuffer(Vector aVe_Buffer, PrintStream a_PrintStream)
	{
		int li_countRows, li_countColumns;
		Row l_Row;

		li_countRows = aVe_Buffer.size();

		li_countColumns = i_Row_ModelObject.getColumnCount();

		for (int li_c1 = 0; li_c1 < li_countRows; li_c1++)
		{
			l_Row = ((RowContainer) aVe_Buffer.get(li_c1)).i_Row_Current;
			for (int li_c2 = 1; li_c2 <= li_countColumns; li_c2++)
			{
				a_PrintStream.print(l_Row.getValueAt(li_c2) + "\t");
			}
			a_PrintStream.print("\n");
		}
	}

	/** Returns handle for ReadEngine.
	 * @return Reference to current RetrieveEngine.<br>
	 * null if it is not defined.
	 */
	public final ReadEngine getReadEngine()
	{
		return i_ReadEngine;
	}

	/** Returns handle for current WriteEngine.
	 * @return Reference to current UpdateEngine.<br>
	 * null if it is not defined.
	 */
	public final WriteEngine getWriteEngine()
	{
		return i_WriteEngine;
	}

	private final int doSave() throws DataSetException
	{
		boolean lb_failed = false;
		if (i_WriteEngine != null)
		{
			int li_return = 0;
			try
			{
				i_WriteEngine.writeStart();
				/*

				*/
				i_WriteEngine.write(this);
				iVe_New.removeAllElements();
				iVe_Modified.removeAllElements();
				iVe_Deleted.removeAllElements();

			}
			finally
			{
				li_return = i_WriteEngine.writeEnd();
			}
			return li_return;
		}
		else
		{
			throw new DataSetException("save - no WriteEngine defined");
		}
	}

	private final int doRead() throws DataSetException
	{
		if (i_ReadEngine != null)
		{
			doReset();
			Row l_Row = null;
			int li_count = 0;
			int li_return = 0;
			try
			{
				if (i_Row_ModelObject!=null)
				{
					l_Row = (Row) i_Row_ModelObject.clone();
				}
				
				l_Row = i_ReadEngine.readStart(l_Row);
				i_Row_ModelObject = l_Row;
				
				while (i_ReadEngine.readRow(l_Row) != NO_MORE_ROWS)
				{
					iVe_Data.add(new RowContainer(l_Row, l_Row));
					li_count++;
					l_Row = (Row)i_Row_ModelObject.clone();
				}
			}
			finally
			{
				li_return = i_ReadEngine.readEnd();
			}
			return li_return;
		}
		else
		{
			throw new DataSetException("read - No ReadEngines defined");
		}
	}

	private final int doRemoveRow(int ai_index)
	{
		if (ai_index > 0 && ai_index <= iVe_Data.size())
		{
			RowContainer l_RowContainer = (RowContainer) iVe_Data.get(ai_index - 1);
			iVe_Modified.remove(l_RowContainer);
			int li_index = iVe_New.indexOf(l_RowContainer);
			if (li_index >= 0)
			{
				iVe_New.remove(li_index);
			}
			else
			{
				iVe_Deleted.add(l_RowContainer);
			}
			iVe_Data.remove(ai_index - 1);

			return ai_index;
		}
		else
		{
			return -1;
		}
	}

	private final int doInsertRow(int ai_index)
	{
		Row l_Row;

		l_Row = (Row) i_Row_ModelObject.clone();
		l_Row.setAllNulls();
		return doInsertRow(ai_index, new RowContainer(null, l_Row));
	}

	private final int doInsertRow(int ai_index, RowContainer a_RowContainer)
	{
		if (ai_index == -1)
		{
			iVe_Data.add((Object) a_RowContainer);
			iVe_New.add((Object) a_RowContainer);
			return iVe_Data.size();
		}

		if (ai_index > 0)
		{
			iVe_Data.add(ai_index - 1, (Object) a_RowContainer);
			iVe_New.add((Object) a_RowContainer);
			return ai_index;
		}
		else
		{
			return -2;
		}
	}

	private final void doReset()
	{

	}

	private final int doSetValueAt(Object a_Object, int ai_rowIndex, int ai_columnIndex)
	{
		if (ai_rowIndex > 0 && ai_rowIndex <= iVe_Data.size())
		{
			RowContainer l_RowContainer = ((RowContainer) iVe_Data.get(ai_rowIndex - 1));

			Row l_Row = (Row) l_RowContainer.i_Row_Current.clone();
			if (l_Row.setValueAt(ai_columnIndex, a_Object) > 0)
			{
				return doModifyRow(ai_rowIndex, l_Row);
			}
			else
			{
				return -2;
			}
		}
		else
		{
			return -1;
		}
	}

	/** Seach matching row from dataset.
	 * @param a_Row Row which contains data that wanted to be found.
	 * @return row number where row has been found 0 if not
	 */
	public final int search(Row a_Row)
	{
		for (int li_index = 1; li_index <= getRowCount(); li_index++)
		{
			if (a_Row.equals(getRow(li_index)))
			{
				return li_index;
			}
		}
		return 0;
	}

	/** Seach matching row from dataset.
	 * @param a_Row Row which contains data that wanted to be found.
	 * @param ai_columns array of column numbers that are used to find row.
	 * @return row number where row has been found 0 if not
	 */
	public final int search(Row a_Row, int[] ai_columns)
	{
		for (int li_index = 1; li_index <= getRowCount(); li_index++)
		{
			if (a_Row.equals(getRow(li_index)))
			{
				return li_index;
			}
		}
		return 0;
	}
	/** Seach matching row from dataset.
	 * @param a_Object Object which contains data that wanted to be found.
	 * @param ai_column column that are searched.
	 * @return row number where row has been found 0 if not
	 */
	public final int search(Object a_Object, int ai_column)
	{
		for (int li_index = 1; li_index <= getRowCount(); li_index++)
		{
			Object l_Object = getValueAt(li_index, ai_column);
			if (a_Object == null && l_Object == null)
			{
				return li_index;
			}
			else if (a_Object.equals(l_Object))
			{
				return li_index;
			}
		}
		return 0;
	}

	/** Returns Row from ai_index location. Don't return reference to row.
	 * @param ai_index Index of row
	 * @return Copy of the row object in DataSet
	 */
	public final Row getRow(int ai_index)
	{
		if (ai_index > 0 && ai_index <= getRowCount())
		{
			RowContainer l_RowContainer = (RowContainer) iVe_Data.get(ai_index - 1);
			return (Row) l_RowContainer.i_Row_Current.clone();
		}
		else
		{
			return null;
		}
	}

	/** Set Row object to DataSet.
	 * @param ai_index Row where to modify row.
	 * @param a_Row Row object which is modifying
	 * @return Row number where change has been done.
	 */
	public final int setRowAt(int ai_index, Row a_Row)
	{
		return doModifyRow(ai_index, a_Row);
	}
	/** Current status of row<br>
	 * With key action is possible deside if we hare doing just 'modify' or 'insert / delete' when setting item.<br>
	 * If this method is not all the objects are always considered as modified.
	 * @return current status ERROR, NEW, MODIFIED, NOTMODIFIED
	 * @param ai_index Index of row which status is returned.
	 */
	public int getRowStatus(int ai_index)
	{
		if (ai_index > 0 && iVe_Data.size() <= ai_index)
		{
			ai_index--;
			RowContainer l_RowContainer = (RowContainer) iVe_Data.get(ai_index);

			if (l_RowContainer.i_Row_Orig == null)
			{
				return NEW;
			}
			else if (l_RowContainer.i_Row_Orig != l_RowContainer.i_Row_Current)
			{
				return MODIFIED;
			}
			else
			{
				return NOTMODIFIED;
			}
		}
		return ERROR;
	}

	/**
	 * Sorts data using current comparator.
	 * 
	 */
	public void sort()
	{
		Arrays.sort(iVe_Data.getObjects(), 0, iVe_Data.size(), i_DataSetComparator);
	}

	/**
	 * Set comparator for sorting. 
	 * @param a_Comparator Comparator object
	 *
	 */
	public void setComparator(Comparator a_Comparator)
	{
		i_DataSetComparator = new DataSetComparator(a_Comparator);
	}

	public void setBuffer(int ai_rowIndex, int ai_buffer)
	{

	}

	/**
	 * This method is made for if example data need to be handled like new.
	 * <br>In case of MODIFIED new rows are not counted, because there are
	 * no original value for them.
	 *
	 * @param ai_targetBuffer NEW, MODIFIED, OR NOTMODIFIED
	 */
	public void setBufferAll(int ai_targetBuffer)
	{
		if (ai_targetBuffer == this.NEW)
		{
			iVe_Modified.removeAllElements();
			iVe_Deleted.removeAllElements();
			iVe_New.removeAllElements();
			for (int li_y = 0; li_y < iVe_Data.size(); li_y++)
			{
				RowContainer l_RowContainer = (RowContainer) iVe_Data.get(li_y);
				l_RowContainer.i_Row_Orig = null;
				iVe_New.add(l_RowContainer);
			}
		}
		if (ai_targetBuffer == this.MODIFIED)
		{
			iVe_Modified.removeAllElements();
			iVe_Deleted.removeAllElements();
			iVe_New.removeAllElements();
			for (int li_y = 0; li_y < iVe_Data.size(); li_y++)
			{
				RowContainer l_RowContainer = (RowContainer) iVe_Data.get(li_y);

				if (l_RowContainer.i_Row_Orig != null)
				{
					iVe_Modified.add(l_RowContainer);
				}

			}
		}
		if (ai_targetBuffer == this.NOTMODIFIED)
		{
			iVe_Modified.removeAllElements();
			iVe_Deleted.removeAllElements();
			iVe_New.removeAllElements();
			for (int li_y = 0; li_y < iVe_Data.size(); li_y++)
			{
				RowContainer l_RowContainer = (RowContainer) iVe_Data.get(li_y);
				l_RowContainer.i_Row_Orig = l_RowContainer.i_Row_Current;
			}
		}
	}
	
	public List getDeleted()
	{
		return iVe_Deleted;
	}
	
	public List getModified()
	{
		return iVe_Modified;
	}
	
	public List getInserted()
	{
		return iVe_New;
	}
	
	public List getAllRows()
	{
		return iVe_Data;
	}
			
}