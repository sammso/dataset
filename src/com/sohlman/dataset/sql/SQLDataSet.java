package com.sohlman.dataset.sql;

import java.sql.Connection;
import java.sql.DriverManager;

import com.sohlman.dataset.DataSet;
import com.sohlman.dataset.DataSetException;

/**
 * <p>SQLDataSet class give high level layer for creating SQL database related 
 * applicaitions with small amount of code. It already includes 
 *  functionality of {@link SQLReadEngine SQLReadEngine} and {@link SQLWriteEngine SQLWriteEngine}</p>
 * <p><b>Usage</b></p>
 * <ol>        
 * <li>First create {@link SQLRowInfo SQLRowInfo} which defines ColumnStructure for SQLDataSet. <i>(optional)</i><br> 
 * If you always read data from database before writing there is not need to define {@link SQLRowInfo SQLRowInfo}
 *  object.
 * </li> 
 * <li>Set SQLStamements using {@link #setSQLStatements setSQLStatements} 
 *     or {@link #setSQLSelect setSQLSelect} 
 *     or {@link #setWriteSQLStametents setWriteSQLStametents }
 *     depending of need of use of SQLDataSet.
 * </li>
 * <li>If you have read engine and you data from one table with SELECT statment, ask SQLDataSet to generate automatically write statements by setting {@link #setAutoGenerateWriteSQL setAutoGenerateWriteSQL} true.</li>      
 * <li>Read data, using {@link com.sohlman.dataset.DataSet#read() read} method <i>(optional)</i></li>
 * <li>Modify data using {@link com.sohlman.dataset.DataSet#addRow addRow}, {@link com.sohlman.dataset.DataSet#insertRow insertRow}, 
 * {@link com.sohlman.dataset.DataSet#removeRow removeRow}, {@link com.sohlman.dataset.DataSet#setValueAt setValueAt} and 
 * {@link com.sohlman.dataset.DataSet#setRowAt setRowAt} methods <i>(optional)</i>
 * </li> 
 * <li>Save changes, using {@link com.sohlman.dataset.DataSet#save() save()} <i>(optional)</i><li>
 * <li>You can ask Databata base information from {@link com.sohlman.dataset.sql.SQLRowInfo SQLRowInfo} object, which you can
 * object, which you can get with {@link com.sohlman.dataset.DataSet#getRowInfo() getRowInfo} object.
 * </li>
 * </ol>
 * <p>To create SQL statements see documentation of {@link com.sohlman.dataset.sql.SQLStatement SQLStatement} class.</p>
 * 
 * @author  Sampsa Sohlman
 * @version 2002-10-31
 */
public class SQLDataSet extends DataSet
{
	private ConnectionContainer i_ConnectionContainer;
	private String iS_SelectSQL;
	private String iS_InsertSQL;
	private String iS_UpdateSQL;
	private String iS_DeleteSQL;
	private boolean ib_noRowsInsertedError = false;
	private boolean ib_noRowsUpdatedError = false; 
	private boolean ib_noRowsDeletedError = false;
	
	private boolean ib_autoGenerateWriteSQL = false;

	private SQLSelectFilter i_SQLSelectFilter = null;
	private SQLWriteFilter i_SQLWriteFilter = null;

	/** Creates new SQLDataSet */
	public SQLDataSet()
	{

	}


	/**
	 * Set javax.sql.Connection to SQLDataSet
	 * <br>
	 * It is same as {@link #setConnectionContainer setConnectionContainer}(new {@link  com.sohlman.dataset.sql.SingleConnectionContainer SingleConnectionContainer}(..));
	 * 
	 * @param a_Connection
	 * @throws DataSetException
	 */
	public void setConnection(Connection a_Connection) throws DataSetException
	{
		setConnectionContainer(new SingleConnectionContainer(a_Connection));
	}

	/**
	 * Set {@link com.sohlman.dataset.sql.ConnectionContainer ConnectionContainer} object for SQLDataSet.
	 * It is required for connection to database.
	 * @param a_ConnectionContainer
	 * @throws DataSetException
	 */
	public void setConnectionContainer(ConnectionContainer a_ConnectionContainer) throws DataSetException
	{
		i_ConnectionContainer = a_ConnectionContainer;
		setUpIfPossible();
	}

	/**
	 * Return current {@link ConnectionContainer ConnectionContainer} object
	 * @return ConnectionContainer
	 */
	public ConnectionContainer getConnectionContainer()
	{
		return i_ConnectionContainer;
	}

	/**
	 * If it is possible then SQLDataSet will automaticly create
	 *
	 * @param ab_value
	 */
	public void setAutoGenerateWriteSQL(boolean ab_value)
	{
		ib_autoGenerateWriteSQL = ab_value;
	}

	/**
	 * <p>Sets SQL Statements for reading and writing.<p>
	 * <p>Stamenets can took parameters and these parameters start with semi colon ":"</p>
	 * <p>With update and delete staments also has to tell if the value is new (changed) or old (not changed).
	 *  New value has prefix ":n" and old value has value ":o". Select and insert prefix is only ":"</p>
	 * 
	 * 
	 * @param aS_SelectSQL Select statement example SELECT column1, column2 FROM mytable WHERE column1 = :1
	 * @param aS_InsertSQL Insert statement example INSERT INTO mytable ( column1, column2 ) VALUES ( :1, :2 )
	 * @param aS_UpdateSQL Update statement example UPDATE mytable set column1 = :n1, column2 = :n2 WHERE column1 = :o1
	 * @param aS_DeleteSQL Update statement example DELETE FROM mytable WHERE column1 = :o1
	 * @throws DataSetException
	 */
	public void setSQLStatements(String aS_SelectSQL, String aS_InsertSQL, String aS_UpdateSQL, String aS_DeleteSQL) throws DataSetException
	{
		iS_SelectSQL = aS_SelectSQL;
		iS_InsertSQL = aS_InsertSQL;
		iS_UpdateSQL = aS_UpdateSQL;
		iS_DeleteSQL = aS_DeleteSQL;
		setUpIfPossible();
	}
	
	/**
	 * <p>Set select statement. <p>
	 * <p>@see #setSQLStatements constructing SQL</p>
	 * @param aS_SelectSQL
	 * @throws DataSetException
	 */
	public void setSQLSelect(String aS_SelectSQL) throws DataSetException
	{
		iS_SelectSQL = aS_SelectSQL;
		setUpIfPossible();
	}	

	/**
	 * <p>Set write SQL statements</p>
	 * <p>@see #setSQLStatements constructing SQL</p>
	 * @param aS_InsertSQL
	 * @param aS_UpdateSQL
	 * @param aS_DeleteSQL
	 * @throws DataSetException
	 */
	public void setWriteSQLStametents(String aS_InsertSQL, String aS_UpdateSQL, String aS_DeleteSQL) throws DataSetException
	{
		iS_InsertSQL = aS_InsertSQL;
		iS_UpdateSQL = aS_UpdateSQL;
		iS_DeleteSQL = aS_DeleteSQL;
		setUpIfPossible();
	}

	/**
	 * Set's parameter for SQL select statement.<br>
	 * Select statement has to be set before to this work.
	 * 
	 * @see com.sohlman.dataset.sql.SQLReadEngine#setParameter
	 */

	public void setParameter(int ai_index, Object a_Object)
	{
		SQLReadEngine l_SQLReadEngine = (SQLReadEngine) getReadEngine();
		if (l_SQLReadEngine != null)
		{
			l_SQLReadEngine.setParameter(ai_index, a_Object);
		}
	}
	
	public void setParameterNull(int ai_index)
	{
		SQLReadEngine l_SQLReadEngine = (SQLReadEngine) getReadEngine();
		if (l_SQLReadEngine != null)
		{
			l_SQLReadEngine.setParameterNull(ai_index);
		}		
	}
	

	/**
	 * Sets {@link SQLWriteFilter SQLWriteFilter} object
	 * @param a_SQLWriteFilter
	 * @throws DataSetException
	 */
	public void setSQLWriteFilter(SQLWriteFilter a_SQLWriteFilter) throws DataSetException
	{
		i_SQLWriteFilter = a_SQLWriteFilter;
		setUpIfPossible();
	}

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

			if (getRowInfo() != null && (iS_InsertSQL != null || iS_DeleteSQL != null || iS_UpdateSQL != null))
			{
				SQLWriteEngine l_SQLWriteEngine = (SQLWriteEngine) getWriteEngine();
				if (l_SQLWriteEngine == null)
				{
					if (getRowInfo() != null)
					{
						l_SQLWriteEngine =
							new SQLWriteEngine(i_ConnectionContainer, iS_InsertSQL, iS_UpdateSQL, iS_DeleteSQL, (SQLRowInfo) getRowInfo());
						setWriteEngine(l_SQLWriteEngine);
					}
				}
				else
				{
					l_SQLWriteEngine.setSQLRowInfo((SQLRowInfo) getRowInfo());
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
	/**
	 * Method getSelectSQL.
	 * @return String which contains current Select statement
	 */
	public String getSelectSQL()
	{
		return iS_SelectSQL;
	}
	/**
	 * Method getInsertSQL.
	 * @return String which contains current Insert statement
	 */
	public String getInsertSQL()
	{
		return iS_InsertSQL;
	}
	/**
	 * Method getUpdateSQL.
	 * @return String which contains current Update statement
	 */
	public String getUpdateSQL()
	{
		return iS_UpdateSQL;
	}

	/**
	 * Method getDeleteSQL.
	 * @return String which contains current Delete statement
	 */
	public String getDeleteSQL()
	{
		return iS_DeleteSQL;
	}

	/**
	 * @see com.sohlman.dataset.DataSet#save
	 */
	public int save() throws DataSetException
	{
		setUpIfPossible();
		SQLWriteEngine l_SQLWriteEngine = (SQLWriteEngine) getWriteEngine();
		if(l_SQLWriteEngine!=null)
		{
			l_SQLWriteEngine.geneteErrorOnNoRowsAction(ib_noRowsInsertedError, ib_noRowsUpdatedError, ib_noRowsDeletedError);
		}
		return super.save();
	}
	/**
	 * @see com.sohlman.dataset.DataSet#read
	 */	
	public int read() throws DataSetException
	{
		int li_count = super.read();
		if(ib_autoGenerateWriteSQL)
		{
			try
			{
				SQLWriteEngine l_SQLWriteEngine = new SQLWriteEngine(i_ConnectionContainer,getSelectSQL(),(SQLRowInfo)getRowInfo());
				setWriteEngine(l_SQLWriteEngine);
			}
			catch(Exception l_Exception)
			{
				// Ignore all exceptions 
				setWriteEngine(null);
			}
			
		}
		return li_count;
	}

	/**
	 * No rows is not updated on database based on SQL statement, is error generated.
	 * 
	 * @param ab_noRowsInsertedError
	 * @param ab_noRowsUpdatedError
	 * @param ab_noRowsDeletedError
	 */
	public void generateErrorOnNoRowsAction(boolean ab_noRowsInsertedError, boolean ab_noRowsUpdatedError, boolean ab_noRowsDeletedError)
	{
		SQLWriteEngine l_SQLWriteEngine = (SQLWriteEngine) getWriteEngine();
		if(l_SQLWriteEngine==null)
		{
			ib_noRowsDeletedError = ab_noRowsDeletedError;
			ib_noRowsInsertedError = ab_noRowsInsertedError;
			ib_noRowsUpdatedError = ab_noRowsUpdatedError;
		}
		
	}
	
	public static void main(String[] aS_Args)
	{
		try
		{
			//Class.forName ("com.sap.dbtech.jdbc.DriverSapDB");
			Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver"); 
			//Connection l_Connection = DriverManager.getConnection("jdbc:sapdb://localhost/test", "dba", "dba");
			Connection l_Connection = DriverManager.getConnection("jdbc:microsoft:sqlserver://localhost:1433", "dbotestdb", "dbotestdb");

			SQLDataSet l_SQLDataSet = new SQLDataSet();
			l_SQLDataSet.setConnection(l_Connection);
			//l_SQLDataSet.setSQLSelect("select * from CASES");
			l_SQLDataSet.setSQLSelect("select * from weburl");
			l_SQLDataSet.read();
			l_SQLDataSet.printBuffers(System.out);
			
		}
		catch(Exception l_Exception)
		{
			l_Exception.printStackTrace();
		}		
	}
}
