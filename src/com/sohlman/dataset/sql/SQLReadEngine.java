package com.sohlman.dataset.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import com.sohlman.dataset.DataSetException;
import com.sohlman.dataset.ReadEngine;
import com.sohlman.dataset.Row;
import com.sohlman.dataset.RowInfo;

/** Common retrieve engine for retrieving data from JDBC sources.
 *
 * <p>To create SQL statements see documentation of {@link com.sohlman.dataset.sql.SQLStatement SQLStatement} class.</p>
 * @author Sampsa Sohlman
 * 
 * @version 2002-10-10 Inteface has been changed
 */
public class SQLReadEngine implements ReadEngine
{

	/** Creates new SQLRetrieveEngine */
	private ConnectionContainer i_ConnectionContainer = null;
	private Connection i_Connection = null;
	private PreparedStatement i_PreparedStatement = null;
	private ResultSet i_ResultSet = null;
	//private PreparedStatement i_PreparedStatement = null;
	private String iS_ReadSQL = null;

	private SQLSelectFilter i_SQLSelectFilter = null;

	int ii_columnCount = 0;
	private int ii_rowCount = 0;

	public final static int DONE = -1;

	SQLStatement i_SQLStament;

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

	/* Removed
		/**
		 * Method setParameter.
		 * @param ai_index Select parameter index
		 * @param a_Object Select parameter
		 * @param ai_sqlType java.sql.Types type
	
			public void setParameter(int ai_index, Object a_Object, int ai_sqlType)
		{
			i_SQLStament.setParameter(ai_index, a_Object, a_Object, ai_sqlType);
		}
	*/

	/**
	 * Method setParameter.
	 * @param ai_index parameter index
	 * @param a_Object which is parameter
	 */
	public void setParameter(int ai_index, Object a_Object)
	{
		if (ai_index <= 0)
		{
			throw new ArrayIndexOutOfBoundsException("Select parameter index has to be greater than 0");
		}

		if (a_Object == null)
		{
			i_SQLStament.setParameter(ai_index, a_Object, a_Object, Types.NULL);
		}
		i_SQLStament.setParameter(ai_index, a_Object, a_Object);
	}
	/**
	 * Method setParameter.
	 * @param ai_index parameter to be set null
	 */
	public void setParameterNull(int ai_index)
	{
		setParameter(ai_index, null);
	}

	public Object getParameter(int ai_index)
	{
		if (i_SQLStament == null)
		{
			throw new IllegalStateException("Parameter no:" + ai_index + " not exist. You have to first set Select parameter");
		}

		return i_SQLStament.getParameterOrig(ai_index);
	}

	/** Set SQL statement for retrieve. Performs also PrepareStatement creation.
	 * @param aS_RetrieveSQL select or stored procedure statement, where ? marks places for parameters.
	 * @return true if success false if not.
	 */
	public void setSQL(String aS_ReadSQL) throws DataSetException
	{
		if (aS_ReadSQL != null)
		{
			iS_ReadSQL = aS_ReadSQL;
			i_SQLStament = new SQLStatement(aS_ReadSQL);
		}
		else
		{
			i_SQLStament = null;
		}

	}

	public String getSQL()
	{
		return iS_ReadSQL;
	}

	/** This is first method to call retrieve operation.
	 *
	 */
	public RowInfo readStart(RowInfo a_RowInfo) throws DataSetException
	{
		ii_rowCount = 0;
		try
		{
			i_Connection = i_ConnectionContainer.getConnection();

			if (i_Connection == null)
			{
				throw new DataSetException("Couldn't retrieve conneciton from ConnectionContainer");
			}

			i_PreparedStatement = i_SQLStament.getPreparedStatement(i_Connection);
			//				i_Connection.prepareStatement(iS_ReadSQL, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			//				for (int li_c = 0; li_c < ii_parameterOrder.length; li_c++)
			//				{
			//					l_PreparedStatement.setObject(li_c + 1, iO_Parameters[ii_parameterOrder[li_c] - 1]);
			//				}

			i_ResultSet = i_PreparedStatement.executeQuery();
			ResultSetMetaData l_ResultSetMetaData = i_ResultSet.getMetaData();

			int li_columnCount = l_ResultSetMetaData.getColumnCount();
			if (li_columnCount > 0)
			{
				int[] li_columnTypes = new int[li_columnCount];

				SQLColumnInfo[] l_SQLColumnInfos = new SQLColumnInfo[li_columnCount];
				String[] lS_ClassNames = new String[li_columnCount];
				String[] lS_ColumnNames = new String[li_columnCount];

				SQLRowInfo l_SQLRowInfo;
				if (i_SQLSelectFilter != null)
				{
					l_SQLRowInfo = i_SQLSelectFilter.getRowInfo(l_ResultSetMetaData);
				}
				else
				{

					for (int li_c = 1; li_c <= li_columnCount; li_c++)
					{
						l_SQLColumnInfos[li_c - 1] =
							new SQLColumnInfo(
								l_ResultSetMetaData.getColumnName(li_c),
								l_ResultSetMetaData.getColumnClassName(li_c),
								l_ResultSetMetaData.getColumnType(li_c));
					}
					l_SQLRowInfo = new SQLRowInfo(l_SQLColumnInfos);
				}
				if (a_RowInfo == null || (!l_SQLRowInfo.equals(a_RowInfo)))
				{
					return (RowInfo)l_SQLRowInfo;
				}
				else
				{
					// Check that columns info type is SQLRowInfo
					if (a_RowInfo instanceof SQLRowInfo)
					{
						// Check Column types and class names are same

						l_SQLRowInfo = (SQLRowInfo)a_RowInfo;
						for (int li_c = 1; li_c <= l_SQLRowInfo.getColumnCount(); li_c++)
						{
							if (l_SQLRowInfo.getColumnType(li_c) != li_columnTypes[li_c - 1])
							{
								throw new DataSetException("Database and predefined column types are different");
							}
							if (!l_SQLRowInfo.getColumnClassName(li_c).equals(lS_ClassNames[li_c - 1]))
							{
								throw new DataSetException("Database and predefined column classes are different");
							}
						}
						for (int li_c = 1; li_c <= l_SQLRowInfo.getColumnCount(); li_c++)
						{
							l_SQLRowInfo.setColumnName(li_c, lS_ColumnNames[li_c - 1]);
						}
						return (RowInfo)l_SQLRowInfo;
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
		catch (SQLException l_SQLException)
		{
			throw new DataSetException("readStart - SQL Error", l_SQLException);
		}
		catch (ClassNotFoundException l_ClassNotFoundException)
		{
			throw new DataSetException("ClassNotFound", l_ClassNotFoundException);
		}
	}

	/** Gets row from ResultSet to DataSet.
	 *
	 * <B>Only for dataset use.</B>
	 *
	 * @return Row object which contains retrieved data.
	 * null if no more data is found.
	 */
	public Row readRow(RowInfo a_RowInfo) throws DataSetException
	{
		if (i_ResultSet == null)
		{
			throw new DataSetException("readRow - ResultSet don't exist");
		}

		try
		{
			if (a_RowInfo != null)
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
						l_Objects = new Object[a_RowInfo.getColumnCount()];

						for (int li_c = 1; li_c <= l_Objects.length; li_c++)
						{
//							if(l_Objects[li_c - 1] instanceof String )
//							{
//								String l_String = i_ResultSet.getString(li_c);
//								l_String.trim();
//								l_Objects[li_c - 1] = l_String;
//							}
//							else
//							{
								l_Objects[li_c - 1] = i_ResultSet.getObject(li_c);
//							}
						}
					}

					ii_rowCount++;
					return new Row(l_Objects, a_RowInfo);
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
		if (i_PreparedStatement != null)
		{
			try
			{
				i_PreparedStatement.close();
			}
			catch (SQLException l_SQLException)
			{
				throw new DataSetException("Failed to close PreparedStatement message = " + l_SQLException.getMessage());
			}
			i_PreparedStatement = null;
		}

		if (i_ResultSet != null)
		{
			try
			{
				i_ResultSet.close();
				i_ResultSet = null;
			}
			catch (SQLException l_SQLException)
			{
				throw new DataSetException("Failed to close ResultSet message = " + l_SQLException.getMessage());
			}
		}
		if (i_Connection != null)
		{
			try
			{
				i_ConnectionContainer.releaseConnection();
				i_Connection = null;
			}
			catch (SQLException l_SQLException)
			{
				throw new DataSetException("Failed to close releaseConnection to connection container." + l_SQLException.getMessage());
			}
		}
		return ii_rowCount;
	}
}