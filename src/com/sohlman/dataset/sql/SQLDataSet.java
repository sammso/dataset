package com.sohlman.dataset.sql;

import com.sohlman.dataset.DataSet;
import com.sohlman.dataset.BasicRow;
import com.sohlman.dataset.DataSetException;

/**
 * SQLDataSet Object give high level layer for creating SQL database related applicaitions with small amount of code.<br><br>
 * <b>Example 1</b><br>
 * <pre>
 * SQLDataSet l_SQLDataSet = new SQLDataSet();
 * l_SQLDataSet.setConnectionContainer(l_ConnectionContainer);
 * </pre>
 *
 * @author  Sampsa Sohlman
 * @version
 */
public class SQLDataSet extends DataSet
{
	private int[] ii_columnTypes = null;
	private ConnectionContainer i_ConnectionContainer;
	private String iS_SelectSQL;
	private String iS_InsertSQL;
	private String iS_UpdateSQL;
	private String iS_DeleteSQL;

	private SQLSelectFilter i_SQLSelectFilter = null;
	private SQLWriteFilter i_SQLWriteFilter = null;

	/** Creates new SQLDataSet */
	public SQLDataSet()
	{

	}
/*
	public SQLDataSet(int[] ai_columnTypes) throws DataSetException
	{
		setColumnTypes(ai_columnTypes);
	}*/
	/**
	 * Set's column types for DataSet<br>
	 * @see
	 */
/*	public void setColumnTypes(int[] ai_columnTypes) throws DataSetException
	{
		ii_columnTypes = ai_columnTypes;
		setModelRowObject(SQLDataSetService.createRowModelObject(ai_columnTypes));
	}
*/

	/**
	 * 
	 * @see SQLReadEngine#getColumnName
	 * 
	 */

	public String getColumnName(int ai_index)
	{
		SQLReadEngine l_SQLReadEngine = (SQLReadEngine) getReadEngine();
		if(l_SQLReadEngine!=null)
		{
			return l_SQLReadEngine.getColumnName(ai_index);
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

	public void setConnectionContainer(ConnectionContainer a_ConnectionContainer) throws DataSetException
	{
		i_ConnectionContainer = a_ConnectionContainer;
		setUpIfPossible();
	}

	public ConnectionContainer getConnectionContainer()
	{
		return i_ConnectionContainer;
	}

	public void setSQLStatements(String aS_SelectSQL, String aS_InsertSQL, String aS_UpdateSQL, String aS_DeleteSQL) throws DataSetException
	{
		iS_SelectSQL = aS_SelectSQL;
		iS_InsertSQL = aS_InsertSQL;
		iS_UpdateSQL = aS_UpdateSQL;
		iS_DeleteSQL = aS_DeleteSQL;
		setUpIfPossible();
	}

	/**
	 * Set's parameter for SQL select statement.
	 *
	 *
	 */

	public void setParameter(int ai_index, Object a_Object)
	{
		SQLReadEngine l_SQLReadEngine = (SQLReadEngine) getReadEngine();
		if (l_SQLReadEngine != null)
		{
			l_SQLReadEngine.setParameter(ai_index, a_Object);
		}
	}

	public void setSQLWriteFilter(SQLWriteFilter a_SQLWriteFilter) throws DataSetException
	{
		i_SQLWriteFilter = a_SQLWriteFilter;
		setUpIfPossible();
	}

	/**
	 *
	 *
	 */
	private void setUpIfPossible() throws DataSetException
	{
		// Connection container is important

		if (i_ConnectionContainer != null)
		{
			// Select statement
			if (iS_SelectSQL != null)
			{
				SQLReadEngine l_SQLReadEngine = (SQLReadEngine) getReadEngine();
				if (l_SQLReadEngine == null)
				{
					l_SQLReadEngine = new SQLReadEngine(i_ConnectionContainer, iS_SelectSQL);
					setReadEngine(l_SQLReadEngine);
				}
				else
				{
					l_SQLReadEngine.setSQL(iS_SelectSQL);
				}
				if (i_SQLSelectFilter != null)
				{
					l_SQLReadEngine.setSQLSelectFilter(i_SQLSelectFilter);
				}
			}

			if (ii_columnTypes == null)
			{
				if (getReadEngine() != null)
				{
					ii_columnTypes = ((SQLReadEngine) getReadEngine()).getColumnTypes();
				}
			}

			if (ii_columnTypes != null && (iS_InsertSQL != null || iS_DeleteSQL != null || iS_UpdateSQL != null))
			{
				SQLWriteEngine l_SQLWriteEngine = (SQLWriteEngine) getWriteEngine();
				if (l_SQLWriteEngine == null)
				{
					if (ii_columnTypes != null)
					{
						l_SQLWriteEngine = new SQLWriteEngine(i_ConnectionContainer, iS_InsertSQL, iS_UpdateSQL, iS_DeleteSQL, ii_columnTypes);
						setWriteEngine(l_SQLWriteEngine);
					}
				}
				else
				{
					l_SQLWriteEngine.setColumnTypes(ii_columnTypes);
					l_SQLWriteEngine.setSQL(iS_InsertSQL, iS_UpdateSQL, iS_DeleteSQL);
				}
			}

			if (i_SQLWriteFilter != null && getWriteEngine() != null)
			{
				SQLWriteEngine l_SQLWriteEngine = (SQLWriteEngine) getWriteEngine();
				l_SQLWriteEngine.setSQLWriteFilter(i_SQLWriteFilter);
			}
		}
	}

	public String getSelectSQL()
	{
		return iS_SelectSQL;
	}

	public String getInsertSQL()
	{
		return iS_InsertSQL;
	}

	public String getUpdateSQL()
	{
		return iS_UpdateSQL;
	}

	public String getDeleteSQL()
	{
		return iS_DeleteSQL;
	}

	public int save() throws DataSetException
	{
		setUpIfPossible();
		return super.save();
	}

	public int read() throws DataSetException
	{
		int li_rowCount = super.read();
		if (li_rowCount >= 0 && ii_columnTypes == null)
		{
		}
		return 0;
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
    	SQLWriteEngine l_SQLWriteEngine = (SQLWriteEngine) getWriteEngine();
    	l_SQLWriteEngine.setErrorOnNoRowsInserted(ab_noRowsInsertedError, ab_noRowsUpdatedError, ab_noRowsDeletedError);
    }
}
