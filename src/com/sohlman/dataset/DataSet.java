package com.sohlman.dataset;

import com.sohlman.dataset.Row;
import com.sohlman.dataset.DataSetException;
import com.sohlman.dataset.KeyAction;
import com.sohlman.dataset.RowInfo;
import java.util.Vector;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.io.PrintStream;
import java.util.List;

/** 
* <p>DataSet is common component to handle data in table form.
* It is possible to use different sources like SQL database, tabular file, XML file etc.</p>
* <p><b>Usage</b></p>
* <ol>        
* <li>First create {@link RowInfo RowInfo} which defines ColumnStructure for DataSet</li>
* <li>Implement {@link ReadEngine ReadEngine} class <i>(optional)</i></li>
* <ul>        
* 	There is no need to create {@link ReadEngine ReadEngine} or some common solution is used like {@link com.sohlman.dataset.sql.SQLReadEngine SQLReadEngine}.
* </ul>       
* <li>Implement {@link WriteEngine WriteEngine} class <i>(optional)</i></li>
* <ul>        
* 	There is no need to create WriteEngine if datasource is readonly(*) or some common solution is used like {@link com.sohlman.dataset.sql.SQLWriteEngine SQLWriteEngine}.<br>
* 	<i>(*)Data is still updateable through datamodification methods, but save() method returns -1.</i>
* </ul>
* <li>Instantiate DataSet</li>
* <li>Assign {@link ReadEngine ReadEngine}, {@link WriteEngine WriteEngine} to DataSet <i>(optional)</i></li>
* <li>Read data, using {@link #read read()} method<i>(optional)</i></li>
* <li>Modify data using {@link #addRow addRow}, {@link #insertRow insertRow}, {@link #removeRow(int) removeRow}, {@link #setValueAt setValueAt} and {@link #setRowAt setRowAt} methods<i>(optional)</i></li>
* <li>Save changes, using {@link #save save} method <i>(optional)</i></li>
* </ol>              
 * @author Sampsa Sohlman
 * @version 2002-10-10
 */

public class DataSet
{
	public final static String EX_STRUCTURE_IS_NOT_MATCH="Column structure of source and destination DataSet are not match";	
	
	/** This buffer contains "visible" data.
	 */
	private DataSetVector iVe_Data;
	/** This buffer contains data which is deleted.
	 * (has to be check if more efficient solution is available)
	 */
	private Vector iVe_Deleted;
	/** This buffer contains links to new inserted data.
	 */
	private Vector iVe_New;
	/** This buffer contains links to modified data
	 */
	private Vector iVe_Modified;
	/** RowInfo Object containing ColumnInformation
	 */
	private RowInfo i_RowInfo;

	// Write engines
	/** Write engine which will only write changes back to source.
	 */
	private WriteEngine i_WriteEngine = null;

	// Read engins
	/** Read engine, which reads data row by row.
	 */
	private ReadEngine i_ReadEngine = null;

	private DataSetComparator i_DataSetComparator = null;

	/** With key action is possible deside if we are doing just modify or insert / delete
	 */
	private KeyAction i_KeyAction = null;

	/** Listeners
	 */
	private Vector iVe_Listeners;

	public final static int NO_MORE_ROWS = -1;

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

	/** Set's defintion of rows as types of objects. These objects must be cloneable.<br>
	 * It has to contain all fields without null values.
	 * @param a_ColumnsInfo array of class names that object contains
	 */
	public void setRowInfo(RowInfo a_ColumnsInfo)
	{
		i_RowInfo = a_ColumnsInfo;
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

	/** Creates new empty row, to wanted position.<br>
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
				((DataSetListener) l_Enumeration.nextElement()).dataSetChanged(
					new DataSetEvent(this, DataSetEvent.ROW_INSERTED, li_return, DataSetEvent.NOTVALID));
			}
		}
		return li_return;
	}

	/** Creates new empty row end of DataSet.
	 * @return Index where row was created.<br>-1 if now row created.
	 */
	public final int addRow()
	{
		int li_row = 0;
		li_row = insertRow(-1);

		if (li_row > 0 && iVe_Listeners != null)
		{
			Enumeration l_Enumeration;
			l_Enumeration = iVe_Listeners.elements();
			while (l_Enumeration.hasMoreElements())
			{
				((DataSetListener) l_Enumeration.nextElement()).dataSetChanged(
					new DataSetEvent(this, DataSetEvent.ROW_INSERTED, li_row, DataSetEvent.NOTVALID));
			}
		}

		return li_row;
	}

	/** Removes row from DataSet.
	 * @param ai_index Row index for row to be removed.
	 * @return Old index of removed row<br>
	 * -1 if remove failed.
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
				((DataSetListener) l_Enumeration.nextElement()).dataSetChanged(new DataSetEvent(this, DataSetEvent.ROW_REMOVED, ai_index));
			}
		}
		return li_return;
	}

	/** Resets all buffers.
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

		if (iVe_Listeners != null)
		{
			Enumeration l_Enumeration;
			l_Enumeration = iVe_Listeners.elements();
			while (l_Enumeration.hasMoreElements())
			{
				((DataSetListener) l_Enumeration.nextElement()).dataSetChanged(new DataSetEvent(this, DataSetEvent.RESET));
			}
		}

	}

	/** Read data with using ReadEngine
	 * @throws DataSetException Throws dataSet exception on error situation.
	 * @return Number of rows read.
	 */
	public int read() throws DataSetException
	{
		int li_count;

		if (iVe_Listeners != null)
		{
			Enumeration l_Enumeration;
			l_Enumeration = iVe_Listeners.elements();
			while (l_Enumeration.hasMoreElements())
			{
				((DataSetListener) l_Enumeration.nextElement()).dataSetChanged(new DataSetEvent(this, DataSetEvent.READ_START));
			}
		}

		li_count = doRead();

		if (iVe_Listeners != null)
		{
			Enumeration l_Enumeration;
			l_Enumeration = iVe_Listeners.elements();
			while (l_Enumeration.hasMoreElements())
			{
				((DataSetListener) l_Enumeration.nextElement()).dataSetChanged(new DataSetEvent(this, DataSetEvent.READ_END, li_count));
			}
		}
		return li_count;
	}

	/** Writes data with using WriteEngine
	 * @throws DataSetException Throws dataSet exception on error situation.
	 * @return Count of rows that has been written.
	 */
	public int save() throws DataSetException
	{
		int li_count;

		if (iVe_Listeners != null)
		{
			Enumeration l_Enumeration;
			l_Enumeration = iVe_Listeners.elements();
			while (l_Enumeration.hasMoreElements())
			{
				((DataSetListener) l_Enumeration.nextElement()).dataSetChanged(new DataSetEvent(this, DataSetEvent.SAVE_START));
			}
		}
		li_count = doSave();
		if (iVe_Listeners != null)
		{
			Enumeration l_Enumeration;
			l_Enumeration = iVe_Listeners.elements();
			while (l_Enumeration.hasMoreElements())
			{
				((DataSetListener) l_Enumeration.nextElement()).dataSetChanged(new DataSetEvent(this, DataSetEvent.READ_END, li_count));
			}
		}
		return li_count;
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
					((DataSetListener) l_Enumeration.nextElement()).dataSetChanged(
						new DataSetEvent(this, DataSetEvent.COLUMN_CHANGED, ai_rowIndex, ai_columnIndex));
				}
			}
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Tells if DataSet can save changes
	 * @return boolean is dataset can be saved
	 */
	public boolean canSave()
	{
		if (i_WriteEngine != null
			&& ((iVe_Deleted != null && iVe_Deleted.size() > 0) || (iVe_Modified != null && iVe_Modified.size() > 0) || (iVe_New != null && iVe_New.size() > 0)))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Do we have write engine
	 * @return boolean is dataset can be saved
	 */
	public final boolean hasWriteEngine()
	{
		if (i_WriteEngine != null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	/**
	 * Do we have read engine
	 * @return boolean is dataset can be saved
	 */
	public final boolean hasReadEngine()
	{
		if (i_ReadEngine != null)
		{
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
		{ // Because of multithreading it is not sure that we will get handle
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
	} /** Set set KeyAction object for DataSet<br>
						 * With key action is possible deside if we hare doing just 'modify' or 'insert / delete' when setting item.<br>
						 * If this method is not all the objects are always considered as modified.
						 * @param a_KeyAction Refrence to KeyAction Object
						 */
	public final void setKeyAction(KeyAction a_KeyAction)
	{
		i_KeyAction = a_KeyAction;
	} /** Set setReadEngine for DataSet
						 * @param a_ReadEngine Assigned read engine.
						 */
	public final void setReadEngine(ReadEngine a_ReadEngine)
	{
		// future when many types of
		// write engines exists. Do check and set all other nulls.
		i_ReadEngine = a_ReadEngine;
	} /** Set WriteEngine for DataSet
					 * @param a_WriteEngine Refrence to new WriteEngine.
					 */
	public final void setWriteEngine(WriteEngine a_WriteEngine)
	{
		i_WriteEngine = a_WriteEngine;
	} /** Removes ReadEngine from DataSet
						 */
	public final void removeReadEngine()
	{
		i_ReadEngine = null;
	} /** Removes KeyAction object from DataSet
						 */
	public final void removeKeyAction()
	{
		i_KeyAction = null;
	} /** Removes WriteEngine from DataSet
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
		return i_RowInfo.getColumnClassName(ai_columnIndex);
	}

	public RowInfo getRowInfo()
	{
		return i_RowInfo;
	} 
	/** Return column count for DataSet
	* @return Number of columns
	*/
	public int getColumnCount()
	{
		if (i_RowInfo == null)
		{
			return 0;
		}
		else
		{
			return i_RowInfo.getColumnCount();
		}
	} 
	/** Returns current column name for index.<br>
	* Requires tha column names are set.
	* @param ai_index Column index
	* @return Column name
	* @throws ArrayIndexOutOfBoundsException if index is out of range or no columns exists
	*/
	public String getColumnName(int ai_index)
	{
		if (i_RowInfo == null)
		{
			throw new ArrayIndexOutOfBoundsException("There is no column information");
		}
		return i_RowInfo.getColumnName(ai_index);
	} /** Returns row count of DataSet
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
		StringBuffer lSb_Sep = new StringBuffer(18 * getColumnCount());
		for (int li_x = 0; li_x < getColumnCount(); li_x++)
			lSb_Sep.append("******************");
		String lS_Sep1 = lSb_Sep.toString();
		lSb_Sep = new StringBuffer(18 * getColumnCount());
		for (int li_x = 0; li_x < getColumnCount(); li_x++)
			lSb_Sep.append("------------------");
		String lS_Sep2 = lSb_Sep.toString();
		a_PrintStream.println(lS_Sep1);
		a_PrintStream.println("Databuffer");
		printColumnNames(a_PrintStream);
		a_PrintStream.println(lS_Sep2);
		printBuffer(iVe_Data, a_PrintStream);
		a_PrintStream.println(lS_Sep1);
		if (iVe_New.size() > 0)
		{
			a_PrintStream.println("NewBuffer");
			printColumnNames(a_PrintStream);
			a_PrintStream.println(lS_Sep2);
			printBuffer(iVe_New, a_PrintStream);
		}
		else
		{
			a_PrintStream.println("NewBuffer (empty)");
		}
		a_PrintStream.println(lS_Sep1);
		if (iVe_Modified.size() > 0)
		{
			a_PrintStream.println("ModifiedBuffer");
			printColumnNames(a_PrintStream);
			a_PrintStream.println(lS_Sep2);
			printBuffer(iVe_Modified, a_PrintStream);
		}
		else
		{
			a_PrintStream.println("ModifiedBuffer (empty)");
		}
		a_PrintStream.println(lS_Sep1);
		if (iVe_Deleted.size() > 0)
		{
			a_PrintStream.println("DeleteeBuffer");
			printColumnNames(a_PrintStream);
			a_PrintStream.println(lS_Sep2);
			printBuffer(iVe_Deleted, a_PrintStream);
		}
		else
		{
			a_PrintStream.println("DeletedBuffer (empty)");
		}

		a_PrintStream.println(lS_Sep1);
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
		li_countColumns = getColumnCount();
		for (int li_c1 = 0; li_c1 < li_countRows; li_c1++)
		{
			StringBuffer l_StringBuffer = createSpaceFilledStringBuffer(li_countColumns * 18);
			l_Row = ((RowContainer) aVe_Buffer.get(li_c1)).i_Row_Current;
			for (int li_c2 = 0; li_c2 < li_countColumns; li_c2++)
			{
				setStringToStringBuffer(l_StringBuffer, " " + l_Row.getValueAt(li_c2 + 1) + " ", li_c2 * 18);
			}
			setStringToStringBuffer(l_StringBuffer, " ", li_countColumns * 18 - 1);
			a_PrintStream.print(l_StringBuffer.toString());
			a_PrintStream.println();
		}
	}
	/**
	* @param a_PrintStream
	*/
	private void printColumnNames(PrintStream a_PrintStream)
	{
		int li_countRows, li_countColumns;
		li_countColumns = getColumnCount();
		StringBuffer l_StringBuffer = createSpaceFilledStringBuffer(li_countColumns * 18);
		for (int li_c2 = 0; li_c2 < li_countColumns; li_c2++)
		{
			setStringToStringBuffer(l_StringBuffer, " " + i_RowInfo.getColumnName(li_c2 + 1) + " ", li_c2 * 18);
		}
		a_PrintStream.print(l_StringBuffer.toString());
		a_PrintStream.println();
	} 
	/**
	* Create String buffer which is filled with space " "
	* @param ai_size Size of new StringBuffer
	* @return StringBuffer Space filled Stringbuffer
	*/
	private StringBuffer createSpaceFilledStringBuffer(int ai_size)
	{
		StringBuffer l_StringBuffer = new StringBuffer(ai_size);
		for (int li_c = 0; li_c < ai_size; li_c++)
		{
			l_StringBuffer.append(" ");
		}
		return l_StringBuffer;
	} 
	/**
	* Puts String to StringBuffer to wanted position by replacing data that are there.
	* @param a_StringBuffer StringBuffer object to modified
	* @param a_String Modifiying String. 
	* @param ai_pos Position where to start modification. 
	* @return boolean false if position is larger that size of StringBuffer othervice true or String is null
	*/
	private boolean setStringToStringBuffer(StringBuffer a_StringBuffer, String a_String, int ai_pos)
	{
		int li_size = a_StringBuffer.length();
		if (a_String == null)
			return false;
		if (li_size <= ai_pos)
			return false;
		int li_end = ai_pos + a_String.length();
		if (li_end > li_size)
		{
			li_end = li_size;
		}

		for (int li_x = ai_pos; li_x < li_end; li_x++)
		{
			a_StringBuffer.setCharAt(li_x, a_String.charAt(li_x - ai_pos));
		}
		return true;
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
			reset();
			int li_count = 0;
			int li_return = 0;
			try
			{
				i_RowInfo = i_ReadEngine.readStart(i_RowInfo);
				Row l_Row;
				while ((l_Row = i_ReadEngine.readRow(i_RowInfo)) != Row.NO_MORE_ROWS)
				{
					iVe_Data.add(new RowContainer(l_Row, l_Row));
					li_count++;
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
		Object[] l_Objects = new Object[getColumnCount()];
		Row l_Row = new Row(l_Objects, i_RowInfo);
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

		int li_row = doModifyRow(ai_index, a_Row);
		if (iVe_Listeners != null)
		{
			Enumeration l_Enumeration;
			l_Enumeration = iVe_Listeners.elements();
			while (l_Enumeration.hasMoreElements())
			{
				((DataSetListener) l_Enumeration.nextElement()).dataSetChanged(new DataSetEvent(this, DataSetEvent.COLUMN_CHANGED, li_row, DataSetEvent.ALL));
			}
		}
		return li_row;
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
	public void setComparator(RowComparator a_RowComparator)
	{
		i_DataSetComparator = new DataSetComparator(a_RowComparator);
	} 
	/**
	* This with this method it is possible to change row status.
	* <i>Currently this is only skelenton and under design.</i>
	* @param ai_rowIndex
	* @param ai_buffer
	*/
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
		else if (ai_targetBuffer == this.MODIFIED)
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
		else if (ai_targetBuffer == this.NOTMODIFIED)
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
		else
		{
			throw new IllegalArgumentException("Argument has to be DataSet.NEW, DataSet.MODIFIED, DataSet.NOTMODIFIED ");
		}
	}

	private final static int DESTINATION_MISSING = 1;
	private final static int SOURCE_MISSING = 2;
	private final static int COPY = 3;
	
	
	/**
	 * Method syncronizeFrom. (under development)
	 * 
	 * 
	 * @param a_DataSet_Source
	 * @return int[] Index 0 addcount 1 modify 2 remove count
	 */
	public int[] synchronizeFrom(DataSet a_DataSet_Source, RowComparator a_RowComparator) throws DataSetException
	{	
		// At first ai_columns, ai_keys don't work
		
		if(!(getRowInfo().equals(a_DataSet_Source.getRowInfo())))
		{
			throw new DataSetException(EX_STRUCTURE_IS_NOT_MATCH);			
		}
			
		
		// DataSet's need to be correct order because of copying	
		
		a_DataSet_Source.setComparator(a_RowComparator);
		a_DataSet_Source.sort();
		
		setComparator(a_RowComparator);
		sort();

			
		int li_sourceCounter = 1, li_destinationCounter = 1;
		int li_sourceCount = a_DataSet_Source.getRowCount();
		int li_destinationCount = getRowCount();
		int li_addCount = 0;
		int li_modifyCount = 0;
		int li_removeCount = 0;

		boolean lb_loop = true;
		if (li_sourceCount > 0 || li_destinationCount > 0)
		{
			while (lb_loop)
			{
				lb_loop = false;
				int li_result = compareRows(a_DataSet_Source, this, a_RowComparator,li_sourceCounter, li_destinationCounter, li_sourceCount, li_destinationCount);
				if (li_result == COPY)
				{
					if (copyRow(a_DataSet_Source, this, li_sourceCounter, li_destinationCounter))
					{
						li_modifyCount++;
					}
					li_sourceCounter++;
					li_destinationCounter++;
//					if (li_sourceCounter < li_sourceCount)
//						li_sourceCounter++;
//					if (li_destinationCounter < li_destinationCount)
//						li_destinationCounter++;
					lb_loop = true;
				}
				else if (li_result == DESTINATION_MISSING)
				{
					if(addRow(a_DataSet_Source, this, li_sourceCounter))
					{
						li_addCount++;
					}
					li_sourceCounter++;
//					if (li_sourceCounter < li_sourceCount)
//						li_sourceCounter++;
					lb_loop = true;
				}
				else if (li_result == SOURCE_MISSING)
				{
					this.removeRow(li_destinationCounter);
					li_removeCount++;
					li_destinationCount--;
					lb_loop = true;
				}
				if (li_sourceCounter > li_sourceCount && li_destinationCounter > li_destinationCount)
				{
					lb_loop = false;
				}
			}
		}
		int[] li_returnValue = { li_addCount, li_modifyCount,li_removeCount};
		return li_returnValue;

	}

	private boolean copyRow(DataSet a_DataSet_Source, DataSet a_DataSet_Destination, int ai_sourceCounter, int ai_destinationCounter)
	{
		int li_copyCount = 0;
		for (int li_x = 1; li_x <= a_DataSet_Source.getColumnCount(); li_x++)
		{
			int li_copy = 0;
			Object lO_Source = a_DataSet_Source.getValueAt(ai_sourceCounter, li_x);
			Object lO_Destination = a_DataSet_Destination.getValueAt(ai_destinationCounter, li_x);
			// Source has to be null if it's string and empty with trim
			//			if (lO_Source != null && lO_Source instanceof String)
			//			{
			//				String l_String = (String) lO_Source;
			//				if (l_String.trim().equals(""))
			//				{
			//					lO_Source = null;
			//				}
			//			}
			if ((lO_Source != null && lO_Destination == null) || (lO_Source == null && lO_Destination != null))
			{
				a_DataSet_Destination.setValueAt(lO_Source, ai_destinationCounter, li_x);
				li_copy = 1;
			}
			else if (lO_Source == null && lO_Destination == null)
			{

			}
			else if (!lO_Source.equals(lO_Destination))
			{
				a_DataSet_Destination.setValueAt(lO_Source, ai_destinationCounter, li_x);
				li_copy = 1;
			}
			li_copyCount = li_copyCount + li_copy;
		}

		if (li_copyCount > 0)
		{
			return true;
		}
		else
		{
			return false;
		}

	}

	private boolean addRow(DataSet a_DataSet_Source, DataSet a_DataSet_Destination, int li_ys)
	{
		int li_row = a_DataSet_Destination.addRow();
		boolean lb_add = false;
		for (int li_x = 1; li_x <= a_DataSet_Source.getColumnCount(); li_x++)
		{
			Object aO_Source = a_DataSet_Source.getValueAt(li_ys, li_x);
			//System.out.print(aO_Source + "\t");
			a_DataSet_Destination.setValueAt(aO_Source, li_row, li_x);
			lb_add = true;
		} //       ii_addCount = ii_addCount + li_add;
		//System.out.println();
		return lb_add;
	}
	
	public RowComparator getRowComparator()
	{
		return i_DataSetComparator.getRowComparator();	
	}

	private int compareRows(
		DataSet a_DataSet_Source,
		DataSet a_DataSet_Destination,
		RowComparator a_RowComparator,
		int ai_sourceCounter,
		int ai_destinationCounter,
		int ai_sourceCount,
		int ai_destinationCount)
	{

		if (ai_sourceCount < ai_sourceCounter)
		{
			return SOURCE_MISSING;
		}
		if (ai_destinationCount < ai_destinationCounter)
		{
			return DESTINATION_MISSING;
		}

//		Comparable l_Comparable_Source = (Comparable) a_DataSet_Source.getValueAt(ai_sourceCounter, 1);
//		Comparable l_Comparable_Destination = (Comparable) a_DataSet_Destination.getValueAt(ai_destinationCounter, 1);

		int li_result = a_RowComparator.compare(a_DataSet_Source.getRow(ai_sourceCounter),a_DataSet_Destination.getRow(ai_destinationCounter)) ;
//		int li_result = l_Comparable_Source.compareTo(l_Comparable_Destination);
		if (ai_sourceCount == ai_destinationCounter && ai_destinationCount > ai_destinationCounter && li_result != 0)
		{
			return SOURCE_MISSING;
		}
		if (ai_destinationCount == ai_destinationCounter && ai_sourceCount > ai_destinationCounter && li_result != 0)
		{
			return DESTINATION_MISSING;
		}

		if (li_result > 0)
		{
			if (ai_sourceCounter < ai_destinationCounter)
			{
				return DESTINATION_MISSING;
			}
			else
			{
				return SOURCE_MISSING;
			}
		}
		else if (li_result < 0)
		{
			if (ai_sourceCounter >= ai_destinationCounter)
			{
				return DESTINATION_MISSING;
			}
			else
			{
				return SOURCE_MISSING;
			}
		}
		else
		{
			return COPY;
		}
	} 
	
	/**
	* Method getDeleted.
	* This point only for WriteEngine
	* @return List
	*/
	public List getDeleted()
	{
		return iVe_Deleted;
	} 
	/**
	* Method getModified.
	* This point only for WriteEngine
	* @return List
	*/
	public List getModified()
	{
		return iVe_Modified;
	} 
	/**
	* Method getInserted.
	* This point only for WriteEngine
	* @return List
	*/
	public List getInserted()
	{
		return iVe_New;
	} 
	/**
	* Method getAllRows.
	* This point only for WriteEngine
	* @return List
	*/
	public List getAllRows()
	{
		return iVe_Data;
	}

}