package com.sohlman.dataset.sql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Purpose of this abstract class is to provide for handling database connection interface to {@link SQLReadEngine SQLReadEngine} and {@link SQLWriteEngine SQLWriteEngine}.<br>
 * <br>
 * This class should en capsulate example different types of connection pools. 
 * <br>
 * Inherit Class and implement begin {@link #beginTransaction beginTransaction()} and {@link #endTransaction endTransAction()} methods.
 *
 * @author Sampsa Sohlman
 * @version 1.0
 */
public abstract class ConnectionContainer
{
	private Connection i_Connection;
	private boolean ib_multiTransaction = false;
	private boolean ib_errorFlag = false;

	/** 
	 * Use this method inside {@link #endTransaction() endTransaction} method to check if error has occurred or not.
	 *
	 * @return true if error has been occurred false if not
	 */
	protected final boolean hasErrorFlag()
	{
		return ib_errorFlag;
	}
	/** 
	 * This is mainly internal use but it is allowed also extrenal.
	 * @param ab_flag Current error status. true if error situation false if not.
	 */
	public final void setErrorFlag(boolean ab_flag)
	{
		ib_errorFlag = ab_flag;
	}
	
	/** Use this to tell what is your connection object. 
	 * <br>Call this method in {@link #beginTransaction beginTransaction} method which you have implemented.
	 * @param a_Connection your current connection object.
	 */
	protected final void setConnection(Connection a_Connection)
	{
		i_Connection = a_Connection;
	}

	/**
	 * <b>For internal use only.</b><br>
	 * {@link SQLReadEngine SQLReadEngine} and {@link SQLWriteEngine SQLWriteEngine} are using this object for getting connection.
	 * If transaction is not set begin transaction is called.
	 * @return Connection object
	 */

	public final Connection getConnection() throws SQLException
	{
		if (i_Connection == null)
		{
			ib_multiTransaction = false;
			beginTransaction();
		}
		else
		{
			ib_multiTransaction = true;
		}
		return i_Connection;
	}
	/**
	 * <b>For internal use only.</b><br>
	 * {@link SQLReadEngine SQLReadEngine} and {@link SQLWriteEngine SQLWriteEngine} are using this object for releasing the connection.
	 *  If {@link #beginTransaction beginTransaction} method has used, it calls also {@link #endTransaction endTransaction} othervice programmer has to use 
	 *  {@link #endTransaction endTransaction} method.
	 */
	public final void releaseConnection() throws SQLException
	{
		if (!ib_multiTransaction)
		{
			endTransaction();
		}
	}
	/** <b><u>Implement this method</u></b><br>
	 * 
	 * <ul>
	 * <li>Create connection or get handle to connection from connection pool</li>
	 * <li>Start transaction <i>Optional</i></li>
	 * <li>Clear or error flag {@link #setErrorFlag(boolean) setErrorFlag(false)}</li>
	 * </ul>
	 */
	public abstract void beginTransaction() throws SQLException;

	/** <b><u>Implement this method</u></b><br>
	 * 
	 * <ul>
	 * <li>Do commit or rollback depending of status {@link #hasErrorFlag() hasErrorFlag()}</li>
	 * <li>End connection <i>Optional</i></li>
	 * <li>Release connection to connection pool or disconnect</li>
	 * <li>Set Connection to null with {@link #setConnection(Connection) setConnection} method.</li>
	 * </ul>
	 */

	public abstract void endTransaction() throws SQLException;
}
