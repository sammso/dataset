
package com.sohlman.dataset.sql;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

import com.sohlman.dataset.DataSetException;

/**
 *
 * @author  Sampsa Sohlman
 * @version 2002-10-10
 */
public class SQLDataSetService
{
	public final static String EX_MORE_THAN_ONE_TABLE_DEF = "More than one table definitions exists";
	
	public static Object createObjectFromSQLTypeNoException(int ai_SQLType)
	{
		try
		{
			return createObjectFromSQLType(ai_SQLType);
		}
		catch (DataSetException a_DataSetException)
		{
		}
		return null;
	}

	public static Object createObjectFromSQLType(int ai_SQLType) throws DataSetException
	{
		switch (ai_SQLType)
		{
			case Types.BIT :
				return new Boolean(false);
			case Types.BIGINT :
				return new BigInteger("0");
			case Types.DECIMAL :
				return new BigDecimal("0");
			case Types.CHAR :
			case Types.VARCHAR :
			case Types.LONGVARCHAR :
				return new String("");
			case Types.INTEGER :
				return new Integer(0);
			case Types.SMALLINT :
				return new Short((short) 0);
			case Types.TINYINT :
				return new Byte((byte) 0);
			case Types.DOUBLE :
				return new Double(0);
			case Types.FLOAT :
			case Types.REAL :
				return new Float(0);
			case Types.TIME :
				return new Time(0);
			case Types.TIMESTAMP :
				return new Timestamp(0);
			case Types.DATE :
				return new java.sql.Date(0);
			default :
				throw new DataSetException("java.sql.Types Type: " + ai_SQLType + " is not supported");
		}
	}

	public static boolean isValidSQLType(int ai_SQLType)
	{
		switch (ai_SQLType)
		{
			case Types.BIT :
			case Types.BIGINT :
			case Types.DECIMAL :
			case Types.CHAR :
			case Types.VARCHAR :
			case Types.LONGVARCHAR :
			case Types.INTEGER :
			case Types.SMALLINT :
			case Types.TINYINT :
			case Types.DOUBLE :
			case Types.FLOAT :
			case Types.REAL :
			case Types.TIME :
			case Types.TIMESTAMP :
			case Types.DATE :
				return true;
			default :
				return false;
		}
	}
	
	
	/**
	 * Get's table name from SELECT string. If there is more than one table then
	 * DataSetException is thrown
	 * 
	 * @param aS_Sql
	 * @return String containing table name. If tablename is inside "" then "" are 
	 * also returned
	 * @throws DataSetException On error this exception is thrown
	 */
	public static String getTableNameFromSelectSQL(String aS_Sql) throws DataSetException
	{
		String lS_SQL = aS_Sql.toUpperCase().trim();

		//int li_tableNameStart = lS_SQL.indexOf("FROM");
		int li_tableNameStart = keyWordSearchIndexOf(lS_SQL, "FROM", 0);

		// Check if 

		if (!(isSpaceTabReturnNothing(lS_SQL, li_tableNameStart - 1)&&isSpaceTabReturnNothing(lS_SQL, li_tableNameStart + 4)))
		{
			li_tableNameStart = -1;
		}
		if (li_tableNameStart == -1)
			throw new DataSetException("FROM keyword not found");

		li_tableNameStart += 4;

		int li_tableNameEnd = keyWordSearchIndexOf(lS_SQL, "WHERE", li_tableNameStart);
		if (!(isSpaceTabReturnNothing(lS_SQL, li_tableNameEnd - 1)&&isSpaceTabReturnNothing(lS_SQL, li_tableNameEnd + 5)))
		{
			li_tableNameEnd = -1;
		}		
		if (li_tableNameEnd == -1)
		{
			li_tableNameEnd = keyWordSearchIndexOf(lS_SQL, "ORDER", li_tableNameStart);
			if (!(isSpaceTabReturnNothing(lS_SQL, li_tableNameEnd - 1)&&isSpaceTabReturnNothing(lS_SQL, li_tableNameEnd + 5)))
			{
				li_tableNameEnd = -1;
			}		
			if (li_tableNameEnd == -1)
			{
				li_tableNameEnd = keyWordSearchIndexOf(lS_SQL, "GROUP", li_tableNameStart);
				if (!(isSpaceTabReturnNothing(lS_SQL, li_tableNameEnd - 1)&&isSpaceTabReturnNothing(lS_SQL, li_tableNameEnd + 5)))
				{
					li_tableNameEnd = -1;
				}		
				
			}
		}

		if (li_tableNameEnd == -1)
		{
			li_tableNameEnd = lS_SQL.length();
		}

		String lS_TableName = aS_Sql.substring(li_tableNameStart, li_tableNameEnd).trim();

		// If space found more than one table defintion found 
		// this don't support alias tables with as or "" named tables		
		if (keyWordSearchIndexOf(lS_TableName, ",", 0) > 0)
		{
			throw new DataSetException(EX_MORE_THAN_ONE_TABLE_DEF);
		}
		if (keyWordSearchIndexOf(lS_TableName, " ", 0) > 0)
		{
			throw new DataSetException("JOIN or AS keyword found from 'FROM' statement");
		}

		return lS_TableName;
	}


	public static boolean isSpaceTabReturnNothing(String a_String, int ai_index)
	{
		if (ai_index < 0)
			return true;
		if (ai_index >= a_String.length())
			return true;

		char l_char = a_String.charAt(ai_index);
		switch (l_char)
		{
			case ' ' :
			case '\t' :
			case '\n' :
				return true;
		}
		return false;
	}	

	public static int keyWordSearchIndexOf(String aS_From, String aS_What, int ai_start)
	{
		int li_length = aS_From.length() - aS_What.length();
		char[] lc_from = aS_From.toCharArray();
		char[] lc_what = aS_What.toCharArray();

		char lc_lastChar = 'S';
		boolean lb_doubleQuote = false;
		boolean lb_singleQuote = false;

		for (int li_index = ai_start; li_index < li_length; li_index++)
		{
			char lc_char = lc_from[li_index];

			if (lc_char == '"') // We are now in column name or 
			{
				if (lb_doubleQuote)
				{
					if (li_index == ai_start || lc_lastChar != '"')
					{
						lb_doubleQuote = false;
					}
				}
				else
				{
					lb_doubleQuote = true;
				}
			}
			else if (lc_char == '\'')
			{
				if (lb_singleQuote)
				{
					if (li_index == ai_start || lc_lastChar != '\'')
					{
						lb_singleQuote = false;
					}
				}
				else
				{
					lb_singleQuote = true;
				}
			}
			else
			{
				if ((!lb_doubleQuote) && (!lb_singleQuote) && lc_what[0] == lc_char)
				{
					int li_i = lc_what.length - 1;
					for (; li_i > 0; li_i--)
					{
						char lc_tmpWhat = lc_what[li_i];
						char lc_tmpFrom = lc_from[li_i + li_index];
						
						
						if (lc_what[li_i] != lc_from[li_i + li_index])
						{
							break;
						}
					}

					if (li_i == 0)
					{
						return li_index;
					}
				}
			}
			lc_lastChar = lc_char;
		}
		return -1;
	}
	
	/*
		public static Row createRowModelObject(int[] ai_SQLTypes) throws DataSetException
		{
			Object[] l_Objects = new Object[ai_SQLTypes.length];

			for (int li_c = 0; li_c < ai_SQLTypes.length; li_c++)
			{
				l_Objects[li_c] = createObjectFromSQLType(ai_SQLTypes[li_c]);
			}

			return (Row) new BasicRow(l_Objects);
		}*/	
}
