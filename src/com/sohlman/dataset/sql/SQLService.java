/*
 * SQLService.java
 */

package com.sohlman.dataset.sql;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.PreparedStatement;

/** This object is collection of JDBC based SQL methods
 *
 *
 * @author  Sampsa Sohlman
 * @version 2002-10-10
 */
public class SQLService
{
	/** Get's single row of data from database.
	 * @param a_Connection Connection object to be used
	 * @param aS_SQL String containing SQL statement
	 * @return Object[] Array of Objects which contain row objects. <br>
	 * null if now rows returned
	 * @throws SQLException if something goes wrong
	 */

	public static Object[] getSingleRow(Connection a_Connection, String aS_SQL) throws SQLException
	{
		Statement l_Statement = a_Connection.createStatement();
		ResultSet l_ResultSet = l_Statement.executeQuery(aS_SQL);
		ResultSetMetaData l_ResultSetMetaData = l_ResultSet.getMetaData();

		if (l_ResultSet.next())
		{
			int li_columnCount = l_ResultSetMetaData.getColumnCount();

			Object[] l_Objects = new Object[li_columnCount];
			for (int li_c = 0; li_c < li_columnCount; li_c++)
			{
				l_Objects[li_c] = l_ResultSet.getObject(li_c + 1);
			}
			l_ResultSet.close();
			l_Statement.close();

			return l_Objects;
		}
		else
		{
			return null;
		}
	}

	/** Get's single row of data from database.<br>
	 * If more rows are returned only first will be read.<br>
	 * No exceptions will be thrown
	 * @param a_Connection Connection object to be used
	 * @param aS_SQL String containing SQL statement
	 * @return Object[] Array of Objects which contain row objects<br>
	 * null if now rows returned
	 */
	public static Object[] getSingleRowNoException(Connection a_Connection, String aS_SQL)
	{
		try
		{
			return getSingleRow(a_Connection, aS_SQL);
		}
		catch (SQLException a_SQLException)
		{
			return null;
		}
	}
	/** Get's single row of data from database.<br>
	 * If more rows are returned only first will be read.
	 * @param a_Connection Connection object to be used
	 * @param aS_SQL String containing SQL statement
	 * @param aO_Parameters Object array containing parameters.<br>
	 * All parameters in array should be different than null.<br>
	 * This information is needed if one of the parameters is nulls.
	 * @return Object[] Array of Objects which contain row objects<br>
	 * null if now rows returned
	 * @throws SQLException if something goes wrong
	 */
	public static Object[] getSingleRow(Connection a_Connection, String aS_SQL, Object[] aO_Parameters) throws SQLException
	{
		return getSingleRow(a_Connection, aS_SQL, aO_Parameters, null);
	}

	/** Get's single row of data from database.<br>
	 * If more rows are returned only first will be read.
	 * @param a_Connection Connection object to be used
	 * @param aS_SQL String containing SQL statement
	 * @param aO_Parameters Object array containing parameters.
	 * @param ai_parameterTypes int array containing type information of parameters
	 * This information is needed if one of the parameters is nulls.
	 * @return Object[] Array of Objects which contain row objects<br>
	 * null if now rows returned
	 * @throws SQLException if something goes wrong
	 */

	public static Object[] getSingleRow(Connection a_Connection, String aS_SQL, Object[] aO_Parameters, int[] ai_parameterTypes) throws SQLException
	{

		int[] li_keys = getKeys(aS_SQL, false);
		String lS_SQL = createFinalSQL(aS_SQL, false);
		PreparedStatement l_PreparedStatement = a_Connection.prepareStatement(lS_SQL);

		for (int li_c = 0; li_c < aO_Parameters.length; li_c++)
		{
			Object l_Object = aO_Parameters[li_keys[li_c] - 1];
			if (ai_parameterTypes != null && l_Object == null)
			{
				l_PreparedStatement.setNull(li_c + 1, ai_parameterTypes[li_c]);
			}
			if (ai_parameterTypes == null && l_Object == null)
			{
				throw new SQLException("getSingleRow parameters types are not defined. Which is required if null there is null parameter");
			}
			else
			{
				l_PreparedStatement.setObject(li_c + 1, l_Object);
			}
		}

		ResultSet l_ResultSet = l_PreparedStatement.executeQuery();
		ResultSetMetaData l_ResultSetMetaData = l_ResultSet.getMetaData();

		if (l_ResultSet.next())
		{
			int li_columnCount = l_ResultSetMetaData.getColumnCount();

			Object[] l_Objects = new Object[li_columnCount];
			for (int li_c = 0; li_c < li_columnCount; li_c++)
			{
				l_Objects[li_c] = l_ResultSet.getObject(li_c + 1);
			}
			l_ResultSet.close();
			l_PreparedStatement.close();
			return l_Objects;
		}
		else
		{
			return null;
		}

	}

	private final static int BEGINS_WITH_ZERO = -2;
	private final static int NO_NUMBER = -1;

	/**
	 * This is mainly ment to be used with prepared statements<br> 
	 * SELECT * FROM table WHERE columnA = :2 and columnB = :1<br>
	 * returning<br>
	 * array position 0 = 1 and position 2 = 2<br>
	 * <br>
	 * Use this with createFinalSQL
	 * <br>
	 * @param a_String String containing SQL
	 * @param ab_useON if before numbers are used O or N charachters
	 * @return int[] array containing array of index positions
	 * @throws SQLException 
	 */
	public static int[] getKeys(String a_String, boolean ab_useON) throws SQLException
	{
		int li_index = 0;
		int[] li_keys = new int[100];
		int[] li_keysTmp;
		int li_key = 0;
		int li_keyIndex = 0;
		char l_char;
		while ((li_index = a_String.indexOf(":", li_index)) >= 0)
		{
			if (li_index < (a_String.length() - 1))
			{
				li_index++;
				l_char = a_String.charAt(li_index);
				if (l_char == 'O' || l_char == 'N' || l_char == 'o' || l_char == 'n' || ((!ab_useON) && l_char != ':'))
				{
					if (ab_useON)
					{
						li_index++;
					}
					li_key = getNumber(a_String.substring(li_index));

					if (li_key >= 0)
					{
						if ((l_char == 'O' || l_char == 'o') && ab_useON)
						{
							li_key = (-1 * li_key);
						}
						// Check if array is full the reserve new array and put
						// data there
						if (li_keyIndex >= li_keys.length)
						{
							li_keysTmp = new int[li_keyIndex + 100];
							for (int li_c = 0; li_c < li_keys.length; li_c++)
							{
								li_keysTmp[li_c] = li_keys[li_c];
							}
							li_keys = li_keysTmp;
						}
						li_keys[li_keyIndex] = li_key;
						li_keyIndex++;
					}
					else
					{
						if (li_key == BEGINS_WITH_ZERO)
						{
							throw new SQLException("SQL parameter number begins with zero");
						}
						if (li_key == NO_NUMBER)
						{
							throw new SQLException("SQL parameter is not number");
						}
					}

				}
				else if (l_char != ':')
				{
					throw new SQLException("SQL parameter needs new or old definition");
				}
			}
			else
			{
				li_index++;
			}
		}

		// normalize

		li_keysTmp = new int[li_keyIndex];
		for (int li_c = 0; li_c < li_keyIndex; li_c++)
		{
			li_keysTmp[li_c] = li_keys[li_c];
		}
		li_keys = li_keysTmp;
		return li_keys;

	}

	/**
	 * This is made for getKeys method
	 */

	private static int getNumber(String a_String)
	{
		StringBuffer lSb_Number = new StringBuffer();
		char lc_number, lc_newOrOld;
		boolean lb_firstZero = false;
		int li_error = 0;
		for (int li_c = 0; li_c < a_String.length(); li_c++)
		{
			// Parameter
			switch (lc_number = a_String.charAt(li_c))
			{
				case '0' :
					if (li_c == 0)
					{
						return BEGINS_WITH_ZERO;
					}
				case '1' :
				case '2' :
				case '3' :
				case '4' :
				case '5' :
				case '6' :
				case '7' :
				case '8' :
				case '9' :
					lSb_Number.append(lc_number);
					break;
				default :
					li_c = a_String.length();
			}
		}

		if (lSb_Number.length() == 0)
		{
			return NO_NUMBER;
		}
		else
		{
			return parseInt(lSb_Number.toString(), -1);
		}
	}

	/**
	 * This is mainly ment to be used with prepared statements<br> 
	 * SELECT * FROM table WHERE columnA = :2 and columnB = :1<br>
	 * returning<br>
	 * SELECT * FROM table WHERE columnA = :? and columnB = :?<br>
	 * <br>
	 * Use this with getKeys which gives the array of the positions
	 * <br>
	 * @param a_String String containing SQL
	 * @param ab_useON if before numbers are used O or N charachters
	 * @return String final SQL statements
	 */
	public static String createFinalSQL(String aS_SQL, boolean ab_useNO)
	{
		boolean lb_specialCharachter = false;
		boolean lb_numero = false;
		char lc_char;
		StringBuffer lSb_SQL = new StringBuffer(aS_SQL.length());

		for (int li_c = 0; li_c < aS_SQL.length(); li_c++)
		{
			lc_char = aS_SQL.charAt(li_c);
			if (ab_useNO)
			{
				switch (lc_char)
				{
					case 'o' :
					case 'O' :
					case 'n' :
					case 'N' :
						if (!lb_specialCharachter)
						{
							lSb_SQL.append(lc_char);
						}
						else
						{
							lSb_SQL.append('?');
						}
						break;
					case '0' :
					case '1' :
					case '2' :
					case '3' :
					case '4' :
					case '5' :
					case '6' :
					case '7' :
					case '8' :
					case '9' :
						if (!lb_specialCharachter)
						{
							lSb_SQL.append(lc_char);
						}
						break;
					case ':' :
						if (lb_specialCharachter)
						{
							lSb_SQL.append(lc_char);
						}
						else
						{
							lb_specialCharachter = true;
						}
						break;
					default :
						lb_specialCharachter = false;
						lSb_SQL.append(lc_char);
						break;
				}
			}
			else
			{
				switch (lc_char)
				{
					case '0' :
					case '1' :
					case '2' :
					case '3' :
					case '4' :
					case '5' :
					case '6' :
					case '7' :
					case '8' :
					case '9' :
						if (!lb_specialCharachter)
						{
							lSb_SQL.append(lc_char);
						}
						else
						{
							if (!lb_numero)
							{
								lSb_SQL.append('?');
							}
						}
						lb_numero = true;
						break;
					case ':' :
						if (lb_specialCharachter)
						{
							lSb_SQL.append(lc_char);
						}
						else
						{
							lb_specialCharachter = true;
						}
						lb_numero = false;
						break;
					default :
						lb_specialCharachter = false;
						lb_numero = false;
						lSb_SQL.append(lc_char);
						break;
				}
			}
		}

		return lSb_SQL.toString();
	}

	public static void main(String args[])
	{
		try
		{
			String lS_SQL = "UPDATE event_properties SET property_shortvalue = :n5, property_value = :n6 WHERE event_id = :o1 AND property_type_id = :o2";
			boolean lb_useNO = true;
			int[] li_keys = getKeys(lS_SQL, lb_useNO);

			System.out.println("Orig SQL : \n" + lS_SQL);
			lS_SQL = createFinalSQL(lS_SQL, lb_useNO);
			System.out.println("Final SQL : \n" + lS_SQL);

			for (int li_c = 0; li_c < li_keys.length; li_c++)
			{
				System.out.println(li_c + " : " + li_keys[li_c]);
			}
		}
		catch (Exception a_Exception)
		{
			a_Exception.printStackTrace();
		}
	}

	/**
	 *	Parses int from from String if string is not valid int then returns default value
	 *
	 *	Function simplifies int parsing from string no need to use exception handling,
	 *	because it is already taken care of.
	 *
	 *	@param aS_String string to be parsed int
	 *	@param ai_default string to parsed int
	 *
	 *	@return parsed int value or default if String doesn't contain int
	 *
	 */
	public static int parseInt(String aS_String, int ai_default)
	{
		if (aS_String == null)
			return ai_default;
		aS_String = aS_String.trim();
		try
		{
			return Integer.parseInt(aS_String);
		}
		catch (NumberFormatException a_NumberFormatException)
		{
			return ai_default;
		}
	}
}
