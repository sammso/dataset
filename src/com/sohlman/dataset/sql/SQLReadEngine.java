/*
 * SQLRetrieveEngine.java
 *
 * Created on 17. elokuuta 2001, 12:04
 */

package com.sohlman.dataset.sql;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import com.sohlman.dataset.ColumnsInfo;
import com.sohlman.dataset.DataSet;
import com.sohlman.dataset.ReadEngine;
import com.sohlman.dataset.Row;
import com.sohlman.dataset.Row;
import com.sohlman.dataset.DataSetException;

/** Common retrieve engine for retrieving data from JDBC sources.
 *
 * @author Sampsa Sohlman
 * 
 * @version 2002-10-10 Inteface has been changed
 * @see Update object
 */
public class SQLReadEngine implements ReadEngine
{

	/** Creates new SQLRetrieveEngine */
	private ConnectionContainer i_ConnectionContainer = null;
	private Connection i_Connection = null;
	private ResultSet i_ResultSet = null;
	//private PreparedStatement i_PreparedStatement = null;
	private String iS_ReadSQL = null;

	private SQLSelectFilter i_SQLSelectFilter = null;

	int ii_columnCount = 0;
	private int ii_rowCount = 0;

	private String[] iS_ColumnNames = null;
	private String[] iS_ColumnTableNames = null;

	private Object[] iO_Parameters;
	private int[] ii_parameterOrder;

	public final static int DONE = -1;

	private int[] ii_columnTypes = null;

	/** Constructor
	 */
	public SQLReadEngine()
	{
	}

	/** Constructor with parameters.<br>
	 * With this constructor is possible to
	 * set Connection, and SQL String
	 * @param a_Connection Connection object
	 * @param aS_RetrieveSQL select or stored procedure statement, where ? marks places for parameters.
	 */
	public SQLReadEngine(ConnectionContainer a_ConnectionContainer, String aS_RetrieveSQL) throws DataSetException
	{
		setConnection(a_ConnectionContainer);
		setSQL(aS_RetrieveSQL);
	}

	public SQLReadEngine(ConnectionContainer a_ConnectionContainer, String aS_RetrieveSQL, Object[] aO_Parameters) throws DataSetException, SQLException
	{
		setConnection(a_ConnectionContainer);
		setSQL(aS_RetrieveSQL);
		iO_Parameters = aO_Parameters;
	}

	public void setSQLSelectFilter(SQLSelectFilter a_SQLSelectFilter)
	{
		i_SQLSelectFilter = a_SQLSelectFilter;
	}

	/** Connects to Connection object to this object.
	 * @param a_Connecion Connection object
	 */
	public void setConnection(ConnectionContainer a_ConnectionContainer)
	{
		i_ConnectionContainer = a_ConnectionContainer;
	}

	public void setParameter(int ai_index, Object a_Object)
	{
		if (iO_Parameters != null && iO_Parameters.length >= ai_index)
		{
			iO_Parameters[ai_index - 1] = a_Object;
		}
	}

	public Object getParameter(int ai_index)
	{
		if (iO_Parameters != null && iO_Parameters.length > ai_index)
		{
			return iO_Parameters[ai_index];
		}
		else
		{
			return null;
		}
	}

	public int[] getColumnTypes()
	{
		return ii_columnTypes;
	}

	/** Set SQL statement for retrieve. Performs also PrepareStatement creation.
	 * @param aS_RetrieveSQL select or stored procedure statement, where ? marks places for parameters.
	 * @return true if success false if not.
	 */
	public void setSQL(String aS_ReadSQL) throws DataSetException
	{
		try
		{
			if (aS_ReadSQL != null)
			{
				ii_parameterOrder = SQLService.getKeys(aS_ReadSQL, false);
				iO_Parameters = new Object[ii_parameterOrder.length];
				iS_ReadSQL = SQLService.createFinalSQL(aS_ReadSQL, false);
			}
			else
			{
				ii_parameterOrder = null;
				iO_Parameters = null;
				iS_ReadSQL = null;
			}
		}
		catch (SQLException a_SQLException)
		{
			throw new DataSetException("Error while parsing Select statment", a_SQLException);
		}
	}

	public String getColumnTableName(int ai_index)
	{
		if (iS_ColumnTableNames == null)
		{
			return null;
		}
		return iS_ColumnTableNames[ai_index - 1];
	}

	/** This is first method to call retrieve operation.
	 *
	 */
	public ColumnsInfo readStart(ColumnsInfo a_ColumnsInfo) throws DataSetException
	{
		ii_rowCount = 0;
		try
		{
			if (iS_ReadSQL != null)
			{
				i_Connection = i_ConnectionContainer.getConnection();

				if (i_Connection == null)
				{
					throw new DataSetException("Couldn't retrieve conneciton from ConnectionContainer");
				}

				PreparedStatement l_PreparedStatement = i_Connection.prepareStatement(iS_ReadSQL, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				for (int li_c = 0; li_c < ii_parameterOrder.length; li_c++)
				{
					l_PreparedStatement.setObject(li_c + 1, iO_Parameters[ii_parameterOrder[li_c] - 1]);
				}

				i_ResultSet = l_PreparedStatement.executeQuery();
				ResultSetMetaData l_ResultSetMetaData = i_ResultSet.getMetaData();

				int li_columnCount = l_ResultSetMetaData.getColumnCount();
				if (li_columnCount > 0)
				{
					int[] li_columnTypes = new int[li_columnCount];

					String[] lS_ColumnTableNames = new String[li_columnCount];
					String[] lS_ClassNames = new String[li_columnCount];
					String[] lS_ColumnNames = new String[li_columnCount];

					SQLColumnsInfo l_SQLColumnsInfo;
					if (i_SQLSelectFilter != null)
					{
						l_SQLColumnsInfo = i_SQLSelectFilter.getColumnsInfo(l_ResultSetMetaData);
					}
					else
					{
						for (int li_c = 1; li_c <= li_columnCount; li_c++)
						{
							li_columnTypes[li_c - 1] = l_ResultSetMetaData.getColumnType(li_c);
							lS_ColumnNames[li_c - 1] = l_ResultSetMetaData.getColumnName(li_c);
							lS_ColumnTableNames[li_c - 1] = l_ResultSetMetaData.getTableName(li_c);
							lS_ClassNames[li_c - 1] = l_ResultSetMetaData.getColumnClassName(li_c);
						}
						l_SQLColumnsInfo = new SQLColumnsInfo(lS_ClassNames, lS_ColumnNames, li_columnTypes);
					}
					if (a_ColumnsInfo == null)
					{
						return (ColumnsInfo) l_SQLColumnsInfo;
					}
					else
					{
						// Check that columns info type is SQLColumnsInfo
						if (a_ColumnsInfo instanceof SQLColumnsInfo)
						{
							// Check Column types and class names are same
							l_SQLColumnsInfo = (SQLColumnsInfo) a_ColumnsInfo;
							for (int li_c = 1; li_c <= l_SQLColumnsInfo.getColumnCount(); li_c++)
							{
								if (l_SQLColumnsInfo.getColumnType(li_c) != li_columnTypes[li_c - 1])
								{
									throw new DataSetException("Database and predefined column types are different");
								}
								if (!l_SQLColumnsInfo.getColumnClassName(li_c).equals(lS_ClassNames[li_c - 1]))
								{
									throw new DataSetException("Database and predefined column classes are different");
								}
							}
							for (int li_c = 1; li_c <= l_SQLColumnsInfo.getColumnCount(); li_c++)
							{
								l_SQLColumnsInfo.setColumnName(li_c, lS_ColumnNames[li_c - 1]);
							}
							return (ColumnsInfo) l_SQLColumnsInfo;
						}
						else
						{
							// Throw Illegal argumentException because we don't want that it will be cached by DataSetException
							// catchers
							throw new IllegalArgumentException("DataSet ColumnInfo object is not SQLColumnInfo or it's child class");
						}
					}
				}
				else
				{
					throw new DataSetException("readStart - ResultSet column count is 0");
				}
			}
			else
			{
				throw new DataSetException("readStart - No SQL statement defined.");
			}

		}
		catch (SQLException a_SQLException)
		{
			throw new DataSetException("readStart - SQL Error", a_SQLException);
		} /*
				catch (ClassNotFoundException a_ClassNotFoundException)
				{
					throw new DataSetException("readStart - ClassNotFoundException", a_ClassNotFoundException);
				}
				catch (InstantiationException a_InstantiationException)
				{
					throw new DataSetException("readStart - InstantiationException\n" + a_InstantiationException.getMessage(), a_InstantiationException);
				}
				catch (IllegalAccessException a_IllegalAccessException)
				{
					throw new DataSetException("readStart - IllegalAccessException", a_IllegalAccessException);
				}*/
	}

	/** Gets row from ResultSet to DataSet.
	 *
	 * <B>Only for dataset use.</B>
	 *
	 * @return Row object which contains retrieved data.
	 * null if no more data is found.
	 */
	public Row readRow(ColumnsInfo a_ColumnsInfo) throws DataSetException
	{
		if (i_ResultSet == null)
		{
			throw new DataSetException("readRow - ResultSet don't exist");
		}

		try
		{
			if (a_ColumnsInfo != null)
			{
				if (i_ResultSet.next())
				{
					Object[] l_Objects;
					if (i_SQLSelectFilter != null)
					{
						l_Objects = i_SQLSelectFilter.getColumnObjects(i_ResultSet);

					}
					else
					{
						l_Objects = new Object[a_ColumnsInfo.getColumnCount()];

						for (int li_c = 1; li_c <= l_Objects.length; li_c++)
						{
							l_Objects[li_c - 1] = i_ResultSet.getObject(li_c);
						}
					}

					ii_rowCount++;
					return new Row(l_Objects, a_ColumnsInfo);
				}
				else
				{
					return Row.NO_MORE_ROWS;
				}
			}
			else
			{
				throw new DataSetException("readRow - Parameter row object is null");
			}
		}
		catch (SQLException a_SQLException)
		{
			throw new DataSetException("readRow - SQLException", a_SQLException);
		}
	}
	/** Last action when all the rows are retrieved
	 * @return How may rows are retrieved
	 */
	public int readEnd() throws DataSetException
	{
		try
		{
			if (i_ResultSet != null)
			{
				i_ResultSet.close();
			}
			i_ResultSet = null;
		}
		catch (SQLException a_SQLException)
		{
			throw new DataSetException("readEnd - SQL Error", a_SQLException);
		}
		finally
		{
			if (i_Connection != null)
			{
				try
				{
					i_ConnectionContainer.releaseConnection();
					i_Connection = null;
				}
				catch (SQLException a_SQLException_2)
				{
					throw new DataSetException("readEnd - Unable, to release connection.", a_SQLException_2);
				}
			}
		}
		return ii_rowCount;
	}

	/**
	 * Database stored column name
	 * @param ai_index of column name 1 - columncount
	 * @return String null if index out of boundary othervice name.
	 */
	public String getColumnName(int ai_index)
	{
		if (ai_index <= 0 && ai_index > iS_ColumnNames.length)
		{
			return iS_ColumnNames[ai_index - 1];
		}
		else
		{
			return null;
		}
	}

	/**
	 * Returns java.sql.Types column type
	 * @param ai_index 
	 * @return int Column type if out of index then 0
	 */
	public int getColumnType(int ai_index)
	{
		if (ai_index <= 0 && ai_index > iS_ColumnNames.length)
		{
			return ii_columnTypes[ai_index - 1];
		}
		else
		{
			return 0; // 0 is not defined in JavaSQL types
		}
	}
}