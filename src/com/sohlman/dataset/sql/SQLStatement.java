/*
DataSet Library
---------------
Copyright (C) 2001-2004 - Sampsa Sohlman, Teemu Sohlman

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA 
*/
package com.sohlman.dataset.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;

/**
 * <p>SQLReadEngine and SQLWriteEngine compatible SQL statement class</p>
 * <p><u><b>SQL statement format</b></u></p>
 * <p>SQLStatement is using little more complex SQL statement format than JDBC prepared statement. This is because the goal
 * was to make possible to separe SQL from code. Also when doing write statements, SQL syntax is different when example null values has been used example WHERE statements.</p>
 * <p>Instead of java.sql.PreparedStatement's "?" parameter substitutes SQLStatement class is using :1, :o1 and :n1 as substitutes and one function :isnull(). :1 substitute is telling which parameter number or column is used. Case of UPDATE it is important to tell if you are using modified or not modified value. With function :isnull( ; ; ) you can change SQL stamement in case that parameter is null, see following examples.</p>
 * <p><u>Example statements:</7u></p>
 * <p>SELECT column1 FROM mytable WHERE column1 > :1</p>
 * <ul>
 * 	<p>Select column1 from mytable so that value of column1 is larger than first parameter. Set parameter using {@link SQLReadEngine#setParameter SQLReadEngine(int, Object ,int)} or {@link SQLDataSet#setParameter SQLReadEngine(int, Object ,int)}</p>
 * </ul>
 * <p>INSERT INTO mytable ( column1  ) VALUES ( :1 )</p>
 * <ul>
 * 	<p>Insert new value from DataSet to mytable's column1. :1 is substituting of DataSet column number 1</p>
 * </ul>
 * <p>UPDATE mytable SET column1 := :n1 WHERE column1 :isnull( :o1 ; is null ; = :o1)</p>
 * <ul>
 * 	<p>Update new value from DataSet to mytable's column1 where using DataSet orignal column value. If original columnValue is null then then compare it with "is null" statement.</p>
 * </ul>
 * <p>DELETE FROM mytable WHERE column1 :isnull( :1 ; is null ; = :1)</p>
 * 
 * @author Sampsa Sohlman
 * @version 2003-02-03
 */
class SQLStatement
{
	protected String iS_SQLStatement;
	protected String iS_FinalSQL_Last;
	protected Vector iVe_ParamOrig;
	protected Vector iVe_DataTypes;
	protected Vector iVe_ParamNew;
	protected PreparedStatement i_PreparedStatement_Current;
	protected PreparedStatement i_PreparedStatement_Last;
	protected final static int BEGINS_WITH_ZERO = -2;
	protected final static int NO_NUMBER = -1;
	protected boolean ib_useOldNew = false;
	protected boolean ib_containsCommands = false;

	public SQLStatement(String aS_SQL)
	{
		iS_SQLStatement = aS_SQL;
	}

	/**
	 * Resets parameters and set's count
	 * 
	 * @param ai_count
	 */
	public void setParameterCount(int ai_count)
	{
		iVe_ParamOrig = new Vector();
		iVe_ParamNew = new Vector();
		iVe_DataTypes = new Vector();

		iVe_ParamOrig.setSize(ai_count);
		iVe_ParamNew.setSize(ai_count);
		iVe_DataTypes.setSize(ai_count);
	}

	/**
	 * Set parameter
	 * @param ai_index parameter index 1 - max
	 * @param a_Object_Orig Original value of object
	 * @param a_Object_New Changed value of object
	 * @param ai_dataType java.sql.Types describing datatype
	 */
	public void setParameter(int ai_index, Object a_Object_Orig, Object a_Object_New, int ai_dataType)
	{
		if (iVe_ParamOrig == null)
		{
			iVe_ParamOrig = new Vector();
			iVe_ParamNew = new Vector();
			iVe_DataTypes = new Vector();
		}

		if (iVe_ParamOrig.size() <= ai_index)
		{
			iVe_ParamOrig.setSize(ai_index);
			iVe_ParamNew.setSize(ai_index);
			iVe_DataTypes.setSize(ai_index);
		}

		iVe_ParamOrig.set(ai_index - 1, a_Object_Orig);
		iVe_ParamNew.set(ai_index - 1, a_Object_New);
		iVe_DataTypes.set(ai_index - 1, new Integer(ai_dataType));
	}

	

	/**
	 * Set parameter without SQL type
	 * @param ai_index
	 * @param a_Object_Orig
	 * @param a_Object_New
	 */
	public void setParameter(int ai_index, Object a_Object_Orig, Object a_Object_New)
	{
		if (iVe_ParamOrig == null)
		{
			iVe_ParamOrig = new Vector();
			iVe_ParamNew = new Vector();
			iVe_DataTypes = new Vector();
		}

		if (iVe_ParamOrig.size() <= ai_index)
		{
			iVe_ParamOrig.setSize(ai_index);
			iVe_ParamNew.setSize(ai_index);
			iVe_DataTypes.setSize(ai_index);
		}

		iVe_ParamOrig.set(ai_index - 1, a_Object_Orig);
		iVe_ParamNew.set(ai_index - 1, a_Object_New);
		iVe_DataTypes.set(ai_index - 1, new Integer(Types.NULL));
	}

	/**
	 * set SQL types for SQLstamenent
	 * @param a_SQLColumnsInfo SQLRowInfo where types are read.
	 */
	public void setSQLTypes(SQLRowInfo a_SQLColumnsInfo)
	{
		int li_count = a_SQLColumnsInfo.getColumnCount();
		if (iVe_DataTypes == null)
		{
			iVe_DataTypes = new Vector(li_count);
			iVe_ParamOrig = new Vector(li_count);
			iVe_ParamNew = new Vector(li_count);
		}
		if (iVe_DataTypes.size() <= li_count)
		{
			iVe_DataTypes.setSize(li_count);
			iVe_ParamOrig.setSize(li_count);
			iVe_ParamNew.setSize(li_count);
		}

		for (int li_index = 1; li_index <= li_count; li_index++)
		{
			iVe_DataTypes.set(li_index - 1, new Integer(a_SQLColumnsInfo.getColumnType(li_index)));
		}
	}

	/**
	 * Clears parameters, but not SQLType of parameters
	 * 
	 */
	public void clearParameters()
	{
		if (iVe_ParamNew != null)
		{
			int li_count = iVe_ParamNew.size();
			for (int li_index = 1; li_index <= li_count; li_index++)
			{
				iVe_ParamNew.set(li_index, null);
				iVe_ParamOrig.set(li_index, null);
			}
		}
	}

	/**
	 * Method getParameterOrig.
	 * @param ai_index
	 * @return Object
	 */
	public Object getParameterOrig(int ai_index)
	{
		if (ai_index > getParameterCount())
		{
			throw new ArrayIndexOutOfBoundsException("Parameter index out of range");
		}
		return iVe_ParamOrig.get(ai_index - 1);
	}

	/**
	 * Method getParameterNew.
	 * @param ai_index
	 * @return Object
	 */
	public Object getParameterNew(int ai_index)
	{
		if (ai_index > getParameterCount())
		{
			throw new ArrayIndexOutOfBoundsException("Parameter index out of range");
		}
		return iVe_ParamNew.get(ai_index - 1);
	}
	
	/**
	 * Method getParameterSQLType.
	 * @param ai_index 
	 * @return int
	 */
	public int getParameterSQLType(int ai_index)
	{
		if (ai_index > getParameterCount())
		{
			throw new ArrayIndexOutOfBoundsException("Parameter index out of range");
		}
		return ((Integer) iVe_DataTypes.get(ai_index - 1)).intValue();
	}

	/**
	 * Return count of parameters
	 * @return int
	 */
	public int getParameterCount()
	{
		if (iVe_ParamOrig == null)
		{
			return 0;
		}
		return iVe_ParamOrig.size();
	}

	/**
	 * Creates prepa
	 * @param a_Connection
	 * @return PreparedStatement
	 */
	public PreparedStatement getPreparedStatement(Connection a_Connection) throws SQLException
	{
		// Parse commands and create SQL

		String lS_SQL = parseCommands(iS_SQLStatement);

		if (iS_FinalSQL_Last == null)
		{
			iS_FinalSQL_Last = "";
		}

		// It is different generate keys 
		int[] li_keys = getKeys(lS_SQL);

		lS_SQL = createFinalSQL(lS_SQL);
		// set parameters.
		PreparedStatement l_PreparedStatement;
//		if (!iS_FinalSQL_Last.equals(lS_SQL))
//		{
			// Prepared statement has to be closed
			// i_PreparedStatement_Current.close();
			l_PreparedStatement = a_Connection.prepareStatement(lS_SQL);
//			iS_FinalSQL_Last = lS_SQL;
//		}
//		else
//		{
//			l_PreparedStatement = i_PreparedStatement_Current;
//		}

		for (int li_c = 0; li_c < li_keys.length; li_c++)
		{

			if (li_keys[li_c] > 0)
			{
				Object lO_newParamenter = getParameterNew(li_keys[li_c]);
				int li_parameterType = getParameterSQLType(li_keys[li_c]);
				if( lO_newParamenter == null && li_parameterType==Types.NULL)
				{
					//
					// Not defined parameter type
					//
					throw new IllegalArgumentException("Parameter value is null and it is not allowed");
				}
				else if (lO_newParamenter == null)
				{
					l_PreparedStatement.setNull(li_c + 1, li_parameterType);
				}
				else
				{
					l_PreparedStatement.setObject(li_c + 1, lO_newParamenter);
				}	
			}
			else
			{
				if (getParameterOrig(-1 * li_keys[li_c]) == null)
				{
					l_PreparedStatement.setNull(li_c + 1, getParameterSQLType((-1 * li_keys[li_c])));
				}
				else
				{
					l_PreparedStatement.setObject(li_c + 1, getParameterOrig(-1 * li_keys[li_c]));
				}
			}

		}
		i_PreparedStatement_Current = l_PreparedStatement;
		return i_PreparedStatement_Current;
	}

	protected String parseCommands(String a_String)
	{
		a_String = parseAllIsNullCommands(a_String);

		return a_String;
	}

	public void setMode(boolean ab_isNew)
	{
		ib_useOldNew = ab_isNew;
	}

	/**
	 * Parses :isNull(;;) formats command for SQL string.
	 * 
	 * :isNull(:o1; 
	 * 
	 * 
	 * @param a_String SQL Sentence in string
	 * @return String SQL Sentence without parseIsNull commands
	 */
	protected String parseAllIsNullCommands(String a_String)
	{
		String lS_String = a_String.toUpperCase();
		StringBuffer lSb_FinalSQL = new StringBuffer();
		int li_start = 0;
		int li_end = 0;
		do // Parse by statement
		{
			li_start = lS_String.indexOf(":ISNULL(", li_end);

			// Start to create final SQL
			if (li_start > 0)
			{
				lSb_FinalSQL.append(a_String.substring(li_end, li_start));
				li_end = li_start;
				do
				{
					li_end = lS_String.indexOf(")", li_end);
				}
				while (li_end > 0 && lS_String.charAt(li_end - 1) == '\\');

				if (li_end > 0)
				{
					li_end++;
					lSb_FinalSQL.append(parseIsNullCommand(a_String.substring(li_start, li_end)));
				}

			}
			else
			{
				lSb_FinalSQL.append(a_String.substring(li_end));
				li_end = -1; // End the loop ":ISNULL(" not found
			}
			// Find ")" but if it is marked as "/)" ingore	
		}
		while (li_end > 0);

		return lSb_FinalSQL.toString();
	}

	protected String parseIsNullCommand(String aS_IsNullCommand)
	{
		int li_itIsNullIndex = aS_IsNullCommand.indexOf(";", 8);
		int li_ItIsNotNullIndex = li_itIsNullIndex;

		do
		{
			li_ItIsNotNullIndex = aS_IsNullCommand.indexOf(";", li_ItIsNotNullIndex + 1);

		}
		while (li_ItIsNotNullIndex > 0 && aS_IsNullCommand.charAt(li_ItIsNotNullIndex - 1) == '\\');

		String lS_Column = aS_IsNullCommand.substring(8, li_itIsNullIndex).trim();
		String lS_IsNull = aS_IsNullCommand.substring(li_itIsNullIndex + 1, li_ItIsNotNullIndex);
		String lS_IsNotNull = aS_IsNullCommand.substring(li_ItIsNotNullIndex + 1, aS_IsNullCommand.length() - 1);

		if (lS_Column.charAt(0) == ':')
		{
			int li_index = 1;
			boolean lb_readOrig = true;
			if (lS_Column.charAt(li_index) == 'o' || lS_Column.charAt(li_index) == 'O')
			{
				// old value
				li_index++;
			}
			else if (lS_Column.charAt(li_index) == 'n' || lS_Column.charAt(li_index) == 'N')
			{
				// new value	
				lb_readOrig = false;
				li_index++;
			}
			li_index = parseInt(lS_Column.substring(li_index), -1);
			if (li_index != -1)
			{
				if (lb_readOrig)
				{
					if (getParameterOrig(li_index) == null)
					{
						return lS_IsNull;
					}
					else
					{
						return lS_IsNotNull;
					}
				}
				else
				{
					if (getParameterNew(li_index) == null)
					{
						return lS_IsNull;
					}
					else
					{
						return lS_IsNotNull;
					}
				}
			}

		}
		return "";
	}

	protected int[] getKeys(String a_String) throws SQLException
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
				//				if (l_char == 'O' || l_char == 'N' || l_char == 'o' || l_char == 'n' || ((!ab_useON) && l_char != ':'))
				if (l_char == 'O' || l_char == 'N' || l_char == 'o' || l_char == 'n' || (l_char >= '1' && l_char <= '9'))
				{
					if (l_char == 'O' || l_char == 'N' || l_char == 'o' || l_char == 'n')
					{
						li_index++;
					}
					li_key = getNumber(a_String.substring(li_index));

					if (li_key >= 0)
					{
						if ((l_char == 'O' || l_char == 'o'))
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
	 * This is mainly ment to be used with prepared statements<br> 
	 * SELECT * FROM table WHERE columnA = :2 and columnB = :1<br>
	 * returning<br>
	 * SELECT * FROM table WHERE columnA = :? and columnB = :?<br>
	 * <br>
	 * Use this with getKeys which gives the array of the positions
	 * <br>
	 * @param a_String String containing SQL
	 * @return String final SQL statements
	 */
	public String createFinalSQL(String aS_SQL)
	{
		boolean lb_specialCharachter = false;
		boolean lb_numero = false;
		char lc_char;
		StringBuffer lSb_SQL = new StringBuffer(aS_SQL.length());

		for (int li_c = 0; li_c < aS_SQL.length(); li_c++)
		{
			lc_char = aS_SQL.charAt(li_c);
			switch (lc_char)
			{
				case 'o' :
				case 'O' :
				case 'n' :
				case 'N' :
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

		return lSb_SQL.toString();
	}

	/**
	 * This is made for getKeys method
	 */

	private int getNumber(String a_String)
	{
		StringBuffer lSb_Number = new StringBuffer();
		char lc_number;

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
	protected int parseInt(String aS_String, int ai_default)
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
