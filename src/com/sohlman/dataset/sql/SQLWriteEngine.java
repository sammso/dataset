package com.sohlman.dataset.sql;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.ListIterator;

import com.sohlman.dataset.RowInfo;
import com.sohlman.dataset.DataSet;
import com.sohlman.dataset.Row;
import com.sohlman.dataset.RowContainer;
import com.sohlman.dataset.DataSetException;

/**
 * <ul>
 * 	<li>Set Connection object</li>
 * 	<li>Set SQL Statements (Connection object has be set before)</li>
 * 	<li>Set Set SQLRowInfo</li>
 *  <li>Set SQLRetrieveEngine related which is related to same DataSet</li>
 * </ul>
* <p>To create SQL statements see documentation of {@link SQLStatement SQLStatement} class.</p>
 * @author Sampsa Sohlman
 * @version 2002-10-10
 */
public class SQLWriteEngine implements com.sohlman.dataset.WriteEngine
{
	public final static String EX_UPDATE_NO_ROWS_UPDATED = "No rows updated to database";
	public final static String EX_INSERT_NO_ROWS_INSERTED = "No rows inserted to database";
	public final static String EX_DELETE_NO_ROWS_DELETED = "No rows deleted to database";
	public final static String EX_NO_COLUMNINFO_DEFINED = "No SQLColumnInfo defined";
	public final static String EX_SQLEXCEPTION = "SQLException";
	public final static String EX_UPDATE_SQL_MISSING = "SQL update statements missing";
	public final static String EX_GETCONNECTION_FAILED = "Get connection failed";
	public final static String EX_RELEASECONNECTION_FAILED = "Release connection failed";
	public final static String EX_MORE_THAN_ONE_TABLE_DEF = "More than one table definitions exists";

	private SQLStatement i_SQLStatement_Insert;
	private SQLStatement i_SQLStatement_Update;
	private SQLStatement i_SQLStatement_Delete;

	private Connection i_Connection;
	private ConnectionContainer i_ConnectionContainer;
	private int ii_updateCount = 0;
	;
	private SQLWriteFilter i_SQLWriteFilter;

	private boolean ib_noRowsInsertedError = true;
	private boolean ib_noRowsUpdatedError = true;
	private boolean ib_noRowsDeletedError = true;

	private SQLRowInfo i_SQLColumnsInfo;

	/** Creates new SQLUpdateEngine */
	public SQLWriteEngine()
	{

	}

	/** Creates new SQLUpdateEngine with parameters
	 * @param a_SQLRetrieveEngine Related SQLRetrieve Engine
	 * @param a_Connection Connection object to database
	 * @param aS_Insert Insert SQL statement
	 * @param aS_Update Update SQL statement
	 * @param aS_Delete Delete SQL statement
	 * @param a_SQLColumnsInfo
	 */
	public SQLWriteEngine(
		ConnectionContainer a_ConnectionContainer,
		String aS_InsertSQL,
		String aS_UpdateSQL,
		String aS_DeleteSQL,
		SQLRowInfo a_SQLColumnsInfo)
		throws DataSetException
	{
		setConnection(a_ConnectionContainer);
		setSQLColumnsInfo(a_SQLColumnsInfo);
		setSQL(aS_InsertSQL, aS_UpdateSQL, aS_DeleteSQL);

	}

	/** Creates new SQLUpdateEngine with parameters
	 * @param a_SQLRetrieveEngine Related SQLRetrieve Engine
	 * @param a_Connection Connection object to database
	 * @param aS_Select Select SQL statement
	 * @param a_SQLColumnsInfo
	 */
	public SQLWriteEngine(ConnectionContainer a_ConnectionContainer, String aS_SelectSQL, SQLRowInfo a_SQLColumnsInfo) throws DataSetException
	{
		setConnection(a_ConnectionContainer);
		setSQLColumnsInfo(a_SQLColumnsInfo);
		createWriteStatementsFromSelect(aS_SelectSQL);
	}

	/** Set Connection object to SQLUpdateEngine
	 * @param a_Connecion Current connection object
	 */
	public void setSQLColumnsInfo(SQLRowInfo a_SQLColumnsInfo)
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

	/**
	 * Creates write SQL statements automaticly from select statements.
	 * <br><b>This is experimental</b>
	 * @param aS_Select Select statement
	 * @throws DataSetException If there are more tables than one in statement
	 * @throws IllegalStateException If SQLRowInfo is not set (programming fault)
	 */
	public void createWriteStatementsFromSelect(String aS_Select) throws DataSetException
	{
		String lS_TableName = getTableNameFromSelectSQL(aS_Select);
		String lS_Insert = createInsertSQL(lS_TableName);
		String lS_Update = createUpdateSQL(lS_TableName);
		String lS_Delete = createDeleteSQL(lS_TableName);
		setSQL(lS_Insert, lS_Update, lS_Delete);
	}

	private String getTableNameFromSelectSQL(String aS_Sql) throws DataSetException
	{
		String lS_SQL = aS_Sql.toUpperCase();

		int li_tableNameStart = lS_SQL.indexOf("FROM");

		if (li_tableNameStart == -1)
			throw new DataSetException("FROM clause not found");

		li_tableNameStart += 4;

		int li_tableNameEnd = lS_SQL.indexOf("WHERE", li_tableNameStart);
		if (li_tableNameEnd == -1)
		{
			li_tableNameEnd = lS_SQL.indexOf("ORDER", li_tableNameStart);
			if (li_tableNameEnd == -1)
			{
				li_tableNameEnd = lS_SQL.indexOf("GROUP", li_tableNameStart);
			}
		}

		if (li_tableNameEnd == -1)
		{
			li_tableNameEnd = lS_SQL.length();
		}

		String lS_TableName = aS_Sql.substring(li_tableNameStart, li_tableNameEnd).trim();

		// If space found more than one table defintion found 
		// this don't support alias tables with as or "" named tables		
		if (lS_TableName.indexOf(" ") > 0)
		{
			throw new DataSetException(EX_MORE_THAN_ONE_TABLE_DEF);
		}
		return lS_TableName;
	}

	private String createInsertSQL(String aS_Table)
	{
		if (i_SQLColumnsInfo == null)
		{
			throw new IllegalStateException("SQLRowInfo is not set");
		}
		StringBuffer lSb_InsertSQL = new StringBuffer();
		lSb_InsertSQL.append("INSERT INTO ").append(aS_Table).append(" ( ");

		for (int li_x = 1; li_x <= i_SQLColumnsInfo.getColumnCount(); li_x++)
		{
			if (li_x > 1)
			{
				lSb_InsertSQL.append(", ");
			}
			lSb_InsertSQL.append(i_SQLColumnsInfo.getColumnName(li_x));
		}

		lSb_InsertSQL.append(" ) VALUES ( ");

		for (int li_x = 1; li_x <= i_SQLColumnsInfo.getColumnCount(); li_x++)
		{
			if (li_x > 1)
			{
				lSb_InsertSQL.append(", ");
			}
			lSb_InsertSQL.append(":");
			lSb_InsertSQL.append(li_x);
		}

		lSb_InsertSQL.append(" )");
		return lSb_InsertSQL.toString();
	}

	private String createUpdateSQL(String aS_Table)
	{
		if (i_SQLColumnsInfo == null)
		{
			throw new IllegalStateException("SQLRowInfo is not set");
		}		
		StringBuffer lSb_UpdateSQL = new StringBuffer();
		lSb_UpdateSQL.append("UPDATE ").append(aS_Table).append(" SET ");


		for (int li_x = 1; li_x <= i_SQLColumnsInfo.getColumnCount(); li_x++)
		{
			if (li_x > 1)
			{
				lSb_UpdateSQL.append(", ");
			}
			lSb_UpdateSQL.append(i_SQLColumnsInfo.getColumnName(li_x));
			lSb_UpdateSQL.append(" = :n");
			lSb_UpdateSQL.append(li_x);
		}

		lSb_UpdateSQL.append(" WHERE ");
		for (int li_x = 1; li_x <= i_SQLColumnsInfo.getColumnCount(); li_x++)
		{
			if (li_x > 1)
			{
				lSb_UpdateSQL.append(" AND ");
			}
			lSb_UpdateSQL.append(i_SQLColumnsInfo.getColumnName(li_x));
			lSb_UpdateSQL.append(":isnull(:o");
			lSb_UpdateSQL.append(li_x);
			lSb_UpdateSQL.append(" ; IS NULL  ; = :o");
			lSb_UpdateSQL.append(li_x);
			lSb_UpdateSQL.append(")");
		}
		return lSb_UpdateSQL.toString();
	}

	private String createDeleteSQL(String aS_Table)
	{
		if (i_SQLColumnsInfo == null)
		{
			throw new IllegalStateException("SQLRowInfo is not set");
		}		
		StringBuffer lSb_DeleteSQL = new StringBuffer();
		lSb_DeleteSQL.append("DELETE FROM ").append(aS_Table);

		lSb_DeleteSQL.append(" WHERE ");
		for (int li_x = 1; li_x <= i_SQLColumnsInfo.getColumnCount(); li_x++)
		{
			if (li_x > 1)
			{
				lSb_DeleteSQL.append(" AND ");
			}
			lSb_DeleteSQL.append(i_SQLColumnsInfo.getColumnName(li_x));
			lSb_DeleteSQL.append(":isnull(:o");
			lSb_DeleteSQL.append(li_x);
			lSb_DeleteSQL.append(" ; IS NULL  ; = :o");
			lSb_DeleteSQL.append(li_x);
			lSb_DeleteSQL.append(")");
		}
		return lSb_DeleteSQL.toString();
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
		if (aS_Insert != null)
		{
			i_SQLStatement_Insert = new SQLStatement(aS_Insert);
		}
		else
		{
			i_SQLStatement_Insert = null;
		}
		if (aS_Update != null)
		{
			i_SQLStatement_Update = new SQLStatement(aS_Update);
		}
		else
		{
			i_SQLStatement_Update = null;
		}
		if (aS_Delete != null)
		{
			i_SQLStatement_Delete = new SQLStatement(aS_Delete);
		}
		else
		{
			i_SQLStatement_Delete = null;
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
					execSQLStatement(i_SQLStatement_Update, a_Row, a_Row);
					//					doUpdate(a_Row, a_Row);
					return;
				case SQLWriteFilter.DELETE :
					//					doDelete(a_Row, a_Row);
					execSQLStatement(i_SQLStatement_Delete, a_Row, a_Row);
					return;
			}
		}
		execSQLStatement(i_SQLStatement_Insert, a_Row, a_Row);
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
					//					doInsert(a_Row_Current);
					execSQLStatement(i_SQLStatement_Insert, a_Row_Current, a_Row_Current);
					return;
				case SQLWriteFilter.DELETE :
					//					doDelete(a_Row_Original, a_Row_Current);
					execSQLStatement(i_SQLStatement_Delete, a_Row_Original, a_Row_Current);
					return;
			}
		}
		execSQLStatement(i_SQLStatement_Update, a_Row_Original, a_Row_Current);
		//		doUpdate(a_Row_Original, a_Row_Current);
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
					//					doInsert(a_Row_Current);
					execSQLStatement(i_SQLStatement_Insert, a_Row_Current, a_Row_Current);
					return;
				case SQLWriteFilter.UPDATE :
					//					doUpdate(a_Row_Original, a_Row_Current);
					execSQLStatement(i_SQLStatement_Update, a_Row_Original, a_Row_Current);
					return;
			}
		}
		execSQLStatement(i_SQLStatement_Delete, a_Row_Original, a_Row_Current);
		//		doDelete(a_Row_Original, a_Row_Current);

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
			catch (SQLException l_SQLException)
			{
				throw new DataSetException(EX_RELEASECONNECTION_FAILED, l_SQLException);
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
			throw new DataSetException(EX_NO_COLUMNINFO_DEFINED);
		}

		if (i_SQLStatement_Insert != null)
		{
			i_SQLStatement_Delete.setSQLTypes(i_SQLColumnsInfo);
		}
		if (i_SQLStatement_Update != null)
		{
			i_SQLStatement_Update.setSQLTypes(i_SQLColumnsInfo);
		}
		if (i_SQLStatement_Insert != null)
		{
			i_SQLStatement_Insert.setSQLTypes(i_SQLColumnsInfo);
		}
		try
		{
			i_Connection = i_ConnectionContainer.getConnection();
		}
		catch (SQLException l_SQLException)
		{
			throw new DataSetException(EX_GETCONNECTION_FAILED, l_SQLException);
		}
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

	private void execSQLStatement(SQLStatement a_SQLStatement, Row a_Row_Original, Row a_Row_Current) throws DataSetException
	{
		if (i_SQLColumnsInfo == null)
		{
			throw new DataSetException(EX_NO_COLUMNINFO_DEFINED);
		}

		if (a_SQLStatement != null)
		{
			try
			{

				// SetParameters
				for (int li_c = 1; li_c <= i_SQLColumnsInfo.getColumnCount(); li_c++)
				{
					a_SQLStatement.setParameter(li_c, a_Row_Original.getValueAt(li_c), a_Row_Current.getValueAt(li_c));
				}
				PreparedStatement l_PreparedStatement = a_SQLStatement.getPreparedStatement(i_Connection);

				int li_return = l_PreparedStatement.executeUpdate();

				if (li_return == 0 && ib_noRowsUpdatedError && a_SQLStatement == i_SQLStatement_Update)
				{
					i_ConnectionContainer.setErrorFlag(true);
					DataSetException l_DataSetException = new DataSetException(EX_UPDATE_NO_ROWS_UPDATED);
					throw l_DataSetException;
				}
				if (li_return == 0 && ib_noRowsInsertedError && a_SQLStatement == i_SQLStatement_Insert)
				{
					i_ConnectionContainer.setErrorFlag(true);
					DataSetException l_DataSetException = new DataSetException(EX_INSERT_NO_ROWS_INSERTED);
					throw l_DataSetException;
				}
				if (li_return == 0 && ib_noRowsDeletedError && a_SQLStatement == i_SQLStatement_Delete)
				{
					i_ConnectionContainer.setErrorFlag(true);
					DataSetException l_DataSetException = new DataSetException(EX_DELETE_NO_ROWS_DELETED);
					throw l_DataSetException;
				}

				ii_updateCount++;
			}
			catch (SQLException a_SQLException)
			{
				i_ConnectionContainer.setErrorFlag(true);
				throw new DataSetException(EX_SQLEXCEPTION, a_SQLException);
			}
		}
	}

	/**
	 * Method setErrorOnNoRowsAction.
	 * No rows is not updated on database based on SQL statement, is error generated.
	 * 
	 * @param ab_noRowsInsertedError
	 * @param ab_noRowsUpdatedError
	 * @param ab_noRowsDeletedError
	 */
	public void geneteErrorOnNoRowsAction(boolean ab_noRowsInsertedError, boolean ab_noRowsUpdatedError, boolean ab_noRowsDeletedError)
	{
		ib_noRowsInsertedError = ab_noRowsInsertedError;
		ib_noRowsUpdatedError = ab_noRowsUpdatedError;
		ib_noRowsDeletedError = ab_noRowsDeletedError;
	}
}
