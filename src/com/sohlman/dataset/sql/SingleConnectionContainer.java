package com.sohlman.dataset.sql;

import java.sql.SQLException;
import java.sql.Connection;

/**
 * Wraps Connection container for single connection application.
 * 
 * @author Sampsa Sohlman
 * @version 2002-11-06
 */
public class SingleConnectionContainer extends ConnectionContainer
{

	public	SingleConnectionContainer(Connection a_Connection)
	{
		setConnection(a_Connection);
	}

	/**
	 * @see com.sohlman.dataset.sql.ConnectionContainer#beginTransaction()
	 */
	public void beginTransaction() throws SQLException
	{
		// Override
	}

	/**
	 * @see com.sohlman.dataset.sql.ConnectionContainer#endTransaction()
	 */
	public void endTransaction() throws SQLException
	{
		// Override
	}
}
