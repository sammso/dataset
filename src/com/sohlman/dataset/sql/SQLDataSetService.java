
package com.sohlman.dataset.sql;

import java.sql.Types;
import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Time;

import java.math.BigInteger;
import java.math.BigDecimal;

import com.sohlman.dataset.DataSetException;
import com.sohlman.dataset.Row;
import com.sohlman.dataset.Row;

/**
 *
 * @author  Sampsa Sohlman
 * @version 2002-10-10
 */
public class SQLDataSetService
{
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
