package com.sohlman.dataset.sql;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.ListIterator;

import com.sohlman.dataset.DataSet;
import com.sohlman.dataset.Row;
import com.sohlman.dataset.RowContainer;
import com.sohlman.dataset.DataSetException;

/**
 * <ul>
 * 	<li>Set Connection object</li>
 * 	<li>Set SQL Statements (Connection object has be set before)</li>
 * 	<li>Set Set SQLColumnsInfo</li>
 *  <li>Set SQLRetrieveEngine related which is related to same DataSet</li>
 * </ul>
 * @author Sampsa Sohlman
 * @version 2002-10-10
 */
public class SQLWriteEngine implements com.sohlman.dataset.WriteEngine
{
	private PreparedStatement i_PreparedStatement_Insert = null;
	private PreparedStatement i_PreparedStatement_Update = null;
	private PreparedStatement i_PreparedStatement_Delete = null;
	private Connection i_Connection = null;
	private ConnectionContainer i_ConnectionContainer = null;
	private String iS_ErrorMsg = "";
	private int ii_updateCount = 0;
	private String iS_Insert = null;
	private String iS_Update = null;
	private String iS_Delete = null;
	private SQLWriteFilter i_SQLWriteFilter = null;

	private boolean ib_noRowsInsertedError = true;
	private boolean ib_noRowsUpdatedError = true;
	private boolean ib_noRowsDeletedError = true;

	private int[] ii_insertParameters;
	private int[] ii_updateParameters;
	private int[] ii_deleteParameters;
	private SQLColumnsInfo i_SQLColumnsInfo;

	/** Creates new SQLUpdateEngine */
	public SQLWriteEngine()
	{

	}

	/** Creates new SQLUpdateEngine with parameters
	 * @param a_SQLRetrieveEngine Related SQLRetrieve Engine
	 * @param ai_keys Array column numbers that are used for update and delete arguments in SQL statement.<br>
	 * @param a_Connection Connection object to database
	 * @param aS_Insert Insert SQL statement
	 * @param aS_Update Update SQL statement
	 * @param aS_Delete Delete SQL statement
	 */
	public SQLWriteEngine(ConnectionContainer a_ConnectionContainer, String aS_InsertSQL, String aS_UpdateSQL, String aS_DeleteSQL, SQLColumnsInfo a_SQLColumnsInfo)
		throws DataSetException
	{
		setConnection(a_ConnectionContainer);
		//setSQLRetrieveEngine(a_SQLReadEngine); // Important Retrieve engine has to be fore setSQLStatemen
		setSQLColumnsInfo(a_SQLColumnsInfo);
		setSQL(aS_InsertSQL, aS_UpdateSQL, aS_DeleteSQL);

	}

	/** Set Connection object to SQLUpdateEngine
	 * @param a_Connecion Current connection object
	 */
	public void setSQLColumnsInfo(SQLColumnsInfo a_SQLColumnsInfo)
	{		
		i_SQLColumnsInfo = a_SQLColumnsInfo;
	}

	/** Set Connection object to SQLUpdateEngine
	 * @param a_Connecion Current connection object
	 */
	public void setConnection(ConnectionContainer a_ConnectionContainer)
	{
		i_ConnectionContainer = a_ConnectionContainer;
	}

	public void setSQLWriteFilter(SQLWriteFilter a_SQLWriteFilter)
	{
		i_SQLWriteFilter = a_SQLWriteFilter;
	}

	/** Set SQL statements for Update operation. Performs also Prepared statement.
	 * @param aS_Insert Insert SQL statement. All the columns must be right order.
	 * @param aS_Update Update SQL statement.<br>
	 * Last parameters '?..' or ':5..' are for keys.<br>
	 * @param aS_Delete Delete SQL statement.<br>
	 * Last parameters '?..' or ':5..' are for keys.<br>
	 * @return true if success <br>
	 * false if error (See error code and text)
	 */
	public void setSQL(String aS_Insert, String aS_Update, String aS_Delete) throws DataSetException
	{
		String lS_CurrentSQL = null;
		try
		{
			if (aS_Insert != null)
			{
				lS_CurrentSQL = "INSERT";				
				ii_insertParameters = SQLService.getKeys(aS_Insert, false);
				iS_Insert = SQLService.createFinalSQL(aS_Insert, false);

			}
			else
			{
				ii_insertParameters = null;
				iS_Insert = null;
			}

			if (aS_Update != null)
			{
				lS_CurrentSQL = "UPDATE";				
				ii_updateParameters = SQLService.getKeys(aS_Update, true);
				iS_Update = SQLService.createFinalSQL(aS_Update, true);
			}
			else
			{
				ii_updateParameters = null;
				iS_Update = null;
			}

			if (aS_Delete != null)
			{
				lS_CurrentSQL = "DELETE";				
				ii_deleteParameters = SQLService.getKeys(aS_Delete, true);
				iS_Delete = SQLService.createFinalSQL(aS_Delete, true);
			}
			else
			{
				ii_deleteParameters = null;
				iS_Delete = null;
			}
		}
		catch (SQLException a_SQLException)
		{
			throw new DataSetException("Error while parsing " + lS_CurrentSQL + "  Statement", a_SQLException);
		}
	}

	/**
	 * Handles insert to source.
	 * @param a_Row Row which will be inserted to source.
	 * @return true if insert is succeeded false if not
	 */
	public void insertRow(Row a_Row) throws DataSetException
	{
		if (i_SQLWriteFilter != null)
		{
			switch (i_SQLWriteFilter.insert(a_Row))
			{
				case SQLWriteFilter.SKIP :
					return;
				case SQLWriteFilter.UPDATE :
					doUpdate(a_Row, a_Row);
					return;
				case SQLWriteFilter.DELETE :
					doDelete(a_Row, a_Row);
					return;
			}
		}
		doInsert(a_Row);
	}

	/**
	 * Handles modifying to source.
	 * @param a_Row Row which will be modifying from source.
	 * @return true if modify is succeeded false if not
	 */
	public void modifyRow(Row a_Row_Original, Row a_Row_Current) throws DataSetException
	{
		if (i_SQLWriteFilter != null)
		{
			switch (i_SQLWriteFilter.update(a_Row_Original, a_Row_Current))
			{
				case SQLWriteFilter.SKIP :
					return;
				case SQLWriteFilter.INSERT :
					doInsert(a_Row_Current);
					return;
				case SQLWriteFilter.DELETE :
					doDelete(a_Row_Original, a_Row_Current);
					return;
			}
		}

		doUpdate(a_Row_Original, a_Row_Current);
	}

	/**
	 * Handles delete from source.
	 * @param a_Row Row which will be deleted from source.
	 * @return true if delete is succeeded false if not
	 */
	public void deleteRow(Row a_Row_Original, Row a_Row_Current) throws DataSetException
	{
		if (i_SQLWriteFilter != null)
		{
			switch (i_SQLWriteFilter.delete(a_Row_Original, a_Row_Current))
			{
				case SQLWriteFilter.SKIP :
					return;
				case SQLWriteFilter.INSERT :
					doInsert(a_Row_Current);
					return;
				case SQLWriteFilter.UPDATE :
					doUpdate(a_Row_Original, a_Row_Current);
					return;
			}
		}
		doDelete(a_Row_Original, a_Row_Current);

	}

	/**
	 * Will be called when update is done
	 * @return count of rows that has been inserted+deleted+modified
	 */
	public int writeEnd() throws DataSetException
	{
		if (i_Connection != null)
		{
			try
			{
				i_ConnectionContainer.releaseConnection();
			}
			catch (SQLException a_SQLException)
			{
				throw new DataSetException("writeEnd - Unable to release connection.", a_SQLException);
			}
			i_Connection = null;
		}
		return ii_updateCount;
	}

	/**
	 * First method to be called.
	 * @return true if success<br>
	 * false if failure
	 */
	public void writeStart() throws DataSetException
	{
		ii_updateCount = 0;
		if (i_SQLColumnsInfo == null)
		{
			throw new DataSetException("writeStart - No SQLColumnInfo defined");
		}

		try
		{
			i_Connection = i_ConnectionContainer.getConnection();
			if (iS_Insert != null)
			{
				i_PreparedStatement_Insert = i_Connection.prepareStatement(iS_Insert);
			}
			else
			{
				i_PreparedStatement_Insert = null;
			}
			if (iS_Update != null)
			{
				i_PreparedStatement_Update = i_Connection.prepareStatement(iS_Update);
			}
			else
			{
				i_PreparedStatement_Update = null;
			}
			if (iS_Delete != null)
			{
				i_PreparedStatement_Delete = i_Connection.prepareStatement(iS_Delete);
			}
			else
			{
				i_PreparedStatement_Delete = null;
			}
		}
		catch (SQLException a_SQLException)
		{
			throw new DataSetException("writeStart - SQLException occurred.", a_SQLException);
		}

	}

	private void doUpdateAction(Row a_Row, PreparedStatement a_PreparedStatement, int[] ai_updateKeys) throws SQLException
	{
		int li_c;
		int li_count;

		li_count = a_Row.getColumnCount();

		for (li_c = 0; li_c < li_count; li_c++)
		{
			a_PreparedStatement.setObject(li_c + 1, a_Row.getValueAt(li_c));
		}

		if (ai_updateKeys != null)
		{
			for (li_c = 0; li_c < ai_updateKeys.length; li_c++)
			{
				a_PreparedStatement.setObject(li_c + li_count, a_Row.getValueAt(ai_updateKeys[li_c]));
			}
		}

		a_PreparedStatement.executeUpdate();
		ii_updateCount++;
	}

	private void doDelete(Row a_Row_Original, Row a_Row_Current) throws DataSetException
	{
		if (i_SQLColumnsInfo == null)
		{
			throw new DataSetException("delete - No SQLColumnInfo defined");
		}

		if (i_PreparedStatement_Delete != null && iS_Delete != null)
		{
			if (ii_deleteParameters.length > 0)
			{
				try
				{
					int li_c;
					int li_count;

					li_count = a_Row_Original.getColumnCount();

					for (li_c = 0; li_c < ii_deleteParameters.length; li_c++)
					{
						
						if (ii_deleteParameters[li_c] > 0)
						{
							if (a_Row_Original.getValueAt(ii_deleteParameters[li_c]) == null)
							{
								i_PreparedStatement_Delete.setNull(li_c + 1, i_SQLColumnsInfo.getColumnType(ii_deleteParameters[li_c]));
							}
							else
							{
								i_PreparedStatement_Delete.setObject(li_c + 1, a_Row_Original.getValueAt(ii_deleteParameters[li_c]));
							}
						}
						else
						{
							if (a_Row_Current.getValueAt(-1 * ii_deleteParameters[li_c]) == null)
							{
								i_PreparedStatement_Delete.setNull(li_c + 1, i_SQLColumnsInfo.getColumnType((-1 * ii_deleteParameters[li_c]) ));
							}
							else
							{
								i_PreparedStatement_Delete.setObject(li_c + 1, a_Row_Current.getValueAt(-1 * ii_deleteParameters[li_c]));
							}
						}
					}

					if (i_PreparedStatement_Delete.executeUpdate() == 0 && ib_noRowsDeletedError)
					{
						i_ConnectionContainer.setErrorFlag(true);
						throw new DataSetException("deleteRow - No rows deleted");
					}
					ii_updateCount++;
				}
				catch (SQLException a_SQLException)
				{
					i_ConnectionContainer.setErrorFlag(true);
					throw new DataSetException("deleteRow - SQL Error", a_SQLException);
				}
			}
			else
			{
				i_ConnectionContainer.setErrorFlag(true);
				throw new DataSetException("deleteRow - no delete parameters defined");
			}
		}

		// This is allowed
		/*        else
		        {
		            i_ConnectionContainer.setErrorFlag(true);
		            throw new DataSetException("deleteRow - No delete statement defined");
		        }*/
	}

	public void write(DataSet a_DataSet) throws DataSetException
	{
		RowContainer l_RowContainer;

		Iterator l_Iterator = a_DataSet.getDeleted().iterator();
		
		while (l_Iterator.hasNext())
		{
			l_RowContainer = (RowContainer) l_Iterator.next();
			deleteRow(l_RowContainer.getOrigRow(), l_RowContainer.getRow());
		}

		l_Iterator = a_DataSet.getModified().iterator();
		while (l_Iterator.hasNext())
		{
			l_RowContainer = (RowContainer) l_Iterator.next();
			modifyRow(l_RowContainer.getOrigRow(), l_RowContainer.getRow());
		}

		l_Iterator = a_DataSet.getInserted().iterator();
		while (l_Iterator.hasNext())
		{
			l_RowContainer = (RowContainer) l_Iterator.next();
			insertRow(l_RowContainer.getRow());
		}
	}

	private void doUpdate(Row a_Row_Original, Row a_Row_Current) throws DataSetException
	{
		if (i_SQLColumnsInfo == null)
		{
			throw new DataSetException("update - No SQLColumnInfo defined");
		}
		if (i_PreparedStatement_Update != null && iS_Update != null)
		{
			if (ii_updateParameters.length > 0)
			{
				try
				{
					int li_c;
					int li_count;

					li_count = a_Row_Original.getColumnCount();

					for (li_c = 0; li_c < ii_updateParameters.length; li_c++)
					{

						if (ii_updateParameters[li_c] > 0)
						{
							if (a_Row_Current.getValueAt(ii_updateParameters[li_c]) == null)
							{
								i_PreparedStatement_Update.setNull(li_c + 1, i_SQLColumnsInfo.getColumnType(ii_updateParameters[li_c] ));
							}
							else
							{
								i_PreparedStatement_Update.setObject(li_c + 1, a_Row_Current.getValueAt(ii_updateParameters[li_c]));
							}
						}
						else
						{
							if (a_Row_Original.getValueAt((-1) * ii_updateParameters[li_c]) == null)
							{
								i_PreparedStatement_Update.setNull(li_c + 1, i_SQLColumnsInfo.getColumnType(((-1) * ii_updateParameters[li_c])));
							}
							else
							{
								i_PreparedStatement_Update.setObject(li_c + 1, a_Row_Original.getValueAt((-1) * ii_updateParameters[li_c]));
							}
						}
					}
					int li_return = i_PreparedStatement_Update.executeUpdate();
					if (li_return == 0 && ib_noRowsUpdatedError)
					{
						i_ConnectionContainer.setErrorFlag(true);
						DataSetException l_DataSetException = new DataSetException("modifyRow - No rows updated");
						throw l_DataSetException;
					}
					ii_updateCount++;
				}
				catch (SQLException a_SQLException)
				{
					i_ConnectionContainer.setErrorFlag(true);
					throw new DataSetException("modifyRow - SQL error", a_SQLException);
				}
			}
			else
			{
				i_ConnectionContainer.setErrorFlag(true);
				throw new DataSetException("modifyRow - no update parameters defined");
			}
		}
		// Not needed
		/*        else
		        {
		            i_ConnectionContainer.setErrorFlag(true);
		            throw new DataSetException("modifyRow - No update statement defined");
		        }*/
	}

	private void doInsert(Row a_Row) throws DataSetException
	{
		if (i_SQLColumnsInfo == null)
		{
			throw new DataSetException("insert - No SQLColumnInfo defined");
		}

		if (i_PreparedStatement_Insert != null && iS_Insert != null)
		{
			if (ii_insertParameters.length > 0)
			{
				try
				{
					int li_c;
					int li_count;
					int li_index;
					li_count = a_Row.getColumnCount();

					for (li_c = 0; li_c < ii_insertParameters.length; li_c++)
					{
						li_index = ii_insertParameters[li_c];

						if (a_Row.getValueAt(li_index) == null)
						{
							i_PreparedStatement_Insert.setNull(li_c + 1, i_SQLColumnsInfo.getColumnType(li_index));
						}
						else
						{
							i_PreparedStatement_Insert.setObject(li_c + 1, a_Row.getValueAt(li_index));
						}
					}

					if (i_PreparedStatement_Insert.executeUpdate() == 0 && ib_noRowsInsertedError)
					{
						i_ConnectionContainer.setErrorFlag(true);
						throw new DataSetException("insertRow - No rows inserted");
					}
					ii_updateCount++;
				}
				catch (SQLException a_SQLException)
				{
					i_ConnectionContainer.setErrorFlag(true);
					throw new DataSetException("insertRow - SQL error", a_SQLException);
				}
			}
			else
			{
				i_ConnectionContainer.setErrorFlag(true);
				throw new DataSetException("insertRow - no insert parameters defined");
			}
		}
		/*        else
		        {
		            i_ConnectionContainer.setErrorFlag(true);
		            throw new DataSetException("insertRow - No insert SQL statement defined");
		        }
		 */
	}

	/**
	 * Method setErrorOnNoRowsInserted.
	 * No rows is not updated on database based on SQL statement, is error generated.
	 * 
	 * @param ab_noRowsInsertedError
	 * @param ab_noRowsUpdatedError
	 * @param ab_noRowsDeletedError
	 */
	public void setErrorOnNoRowsInserted(boolean ab_noRowsInsertedError, boolean ab_noRowsUpdatedError, boolean ab_noRowsDeletedError)
	{
		ib_noRowsInsertedError = ab_noRowsInsertedError;
		ib_noRowsUpdatedError = ab_noRowsUpdatedError;
		ib_noRowsDeletedError = ab_noRowsDeletedError;
	}
}
