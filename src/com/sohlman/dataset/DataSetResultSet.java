package com.sohlman.dataset;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import org.apache.xalan.lib.sql.SQLErrorDocument;

import com.sohlman.dataset.sql.SQLDataSet;

/**
 * ResultSet wrapper for DataSet
 * 
 * <b>UNDER IMPLEMENTATION</b>
 * 
 * @author Sampsa Sohlman
 * @version 2003-02-03
 */
class DataSetResultSet implements ResultSet
{
	private int ii_cursor = 0;
	private DataSet i_DataSet;
	
	/**
	 * Constructor for DataSetResultSet.
	 */
	public DataSetResultSet(DataSet a_DataSet)
	{
		i_DataSet = a_DataSet;			
	}

	/**
	 * @see java.sql.ResultSet#next()
	 */
	public boolean next() throws SQLException
	{
		if(i_DataSet==null)
		{
			throw new SQLException("ResultSet has been closed");
		}

		if(i_DataSet.getRowCount() <= ii_cursor)
		{
			return false;	
		}
		
		ii_cursor++;
				
		return true;
	}

	/**
	 * @see java.sql.ResultSet#close()
	 */
	public void close() throws SQLException
	{
		i_DataSet = null;
	}

	/**
	 * @see java.sql.ResultSet#wasNull()
	 */
	public boolean wasNull() throws SQLException
	{
		return false;
	}

	/**
	 * @see java.sql.ResultSet#getString(int)
	 */
	public String getString(int ai_columnIndex) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getBoolean(int)
	 */
	public boolean getBoolean(int ai_columnIndex) throws SQLException
	{
		return false;
	}

	/**
	 * @see java.sql.ResultSet#getByte(int)
	 */
	public byte getByte(int ai_columnIndex) throws SQLException
	{
		return 0;
	}

	/**
	 * @see java.sql.ResultSet#getShort(int)
	 */
	public short getShort(int ai_columnIndex) throws SQLException
	{
		return 0;
	}

	/**
	 * @see java.sql.ResultSet#getInt(int)
	 */
	public int getInt(int ai_columnIndex) throws SQLException
	{
		return 0;
	}

	/**
	 * @see java.sql.ResultSet#getLong(int)
	 */
	public long getLong(int ai_columnIndex) throws SQLException
	{
		return 0;
	}

	/**
	 * @see java.sql.ResultSet#getFloat(int)
	 */
	public float getFloat(int ai_columnIndex) throws SQLException
	{
		return 0;
	}

	/**
	 * @see java.sql.ResultSet#getDouble(int)
	 */
	public double getDouble(int ai_columnIndex) throws SQLException
	{
		return 0;
	}

	/**
	 * @see java.sql.ResultSet#getBigDecimal(int, int)
	 * Not implemented
	 * @deprecated
	 */
	public BigDecimal getBigDecimal(int arg0, int arg1) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getBytes(int)
	 */
	public byte[] getBytes(int ai_columnIndex) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getDate(int)
	 */
	public Date getDate(int ai_columnIndex) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getTime(int)
	 */
	public Time getTime(int ai_columnIndex) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getTimestamp(int)
	 */
	public Timestamp getTimestamp(int ai_columnIndex) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getAsciiStream(int)
	 * (Not implemented)
	 */
	public InputStream getAsciiStream(int ai_columnIndex) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getUnicodeStream(int)
	 * @deprecated
	 */
	public InputStream getUnicodeStream(int ai_columnIndex) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getBinaryStream(int)
	 */
	public InputStream getBinaryStream(int ai_columnIndex) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getString(String)
	 */
	public String getString(String aS_ColumnName) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getBoolean(String)
	 */
	public boolean getBoolean(String aS_ColumnName) throws SQLException
	{
		return false;
	}

	/**
	 * @see java.sql.ResultSet#getByte(String)
	 */
	public byte getByte(String aS_ColumnName) throws SQLException
	{
		return 0;
	}

	/**
	 * @see java.sql.ResultSet#getShort(String)
	 */
	public short getShort(String aS_ColumnName) throws SQLException
	{
		return 0;
	}

	/**
	 * @see java.sql.ResultSet#getInt(String)
	 */
	public int getInt(String aS_ColumnName) throws SQLException
	{
		return 0;
	}

	/**
	 * @see java.sql.ResultSet#getLong(String)
	 */
	public long getLong(String aS_ColumnName) throws SQLException
	{
		return 0;
	}

	/**
	 * @see java.sql.ResultSet#getFloat(String)
	 */
	public float getFloat(String aS_ColumnName) throws SQLException
	{
		return 0;
	}

	/**
	 * @see java.sql.ResultSet#getDouble(String)
	 */
	public double getDouble(String aS_ColumnName) throws SQLException
	{
		return 0;
	}

	/**
	 * @see java.sql.ResultSet#getBigDecimal(String, int)
	 * @deprecated
	 */
	public BigDecimal getBigDecimal(String arg0, int arg1) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getBytes(String)
	 */
	public byte[] getBytes(String aS_ColumnName) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getDate(String)
	 */
	public Date getDate(String aS_ColumnName) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getTime(String)
	 */
	public Time getTime(String aS_ColumnName) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getTimestamp(String)
	 */
	public Timestamp getTimestamp(String aS_ColumnName) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getAsciiStream(String)
	 */
	public InputStream getAsciiStream(String aS_ColumnName) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getUnicodeStream(String)
	 * @deprecated
	 */
	public InputStream getUnicodeStream(String aS_ColumnName) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getBinaryStream(String)
	 */
	public InputStream getBinaryStream(String aS_ColumnName) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getWarnings()
	 */
	public SQLWarning getWarnings() throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#clearWarnings()
	 */
	public void clearWarnings() throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#getCursorName()
	 */
	public String getCursorName() throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getMetaData()
	 */
	public ResultSetMetaData getMetaData() throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getObject(int)
	 */
	public Object getObject(int ai_) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getObject(String)
	 */
	public Object getObject(String arg0) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#findColumn(String)
	 */
	public int findColumn(String arg0) throws SQLException
	{
		return 0;
	}

	/**
	 * @see java.sql.ResultSet#getCharacterStream(int)
	 */
	public Reader getCharacterStream(int arg0) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getCharacterStream(String)
	 */
	public Reader getCharacterStream(String arg0) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getBigDecimal(int)
	 */
	public BigDecimal getBigDecimal(int arg0) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getBigDecimal(String)
	 */
	public BigDecimal getBigDecimal(String arg0) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#isBeforeFirst()
	 */
	public boolean isBeforeFirst() throws SQLException
	{
		return false;
	}

	/**
	 * @see java.sql.ResultSet#isAfterLast()
	 */
	public boolean isAfterLast() throws SQLException
	{
		return false;
	}

	/**
	 * @see java.sql.ResultSet#isFirst()
	 */
	public boolean isFirst() throws SQLException
	{
		return false;
	}

	/**
	 * @see java.sql.ResultSet#isLast()
	 */
	public boolean isLast() throws SQLException
	{
		return false;
	}

	/**
	 * @see java.sql.ResultSet#beforeFirst()
	 */
	public void beforeFirst() throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#afterLast()
	 */
	public void afterLast() throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#first()
	 */
	public boolean first() throws SQLException
	{
		return false;
	}

	/**
	 * @see java.sql.ResultSet#last()
	 */
	public boolean last() throws SQLException
	{
		return false;
	}

	/**
	 * @see java.sql.ResultSet#getRow()
	 */
	public int getRow() throws SQLException
	{
		return 0;
	}

	/**
	 * @see java.sql.ResultSet#absolute(int)
	 */
	public boolean absolute(int arg0) throws SQLException
	{
		return false;
	}

	/**
	 * @see java.sql.ResultSet#relative(int)
	 */
	public boolean relative(int arg0) throws SQLException
	{
		return false;
	}

	/**
	 * @see java.sql.ResultSet#previous()
	 */
	public boolean previous() throws SQLException
	{
		if(i_DataSet==null)
		{
			throw new SQLException("ResultSet has been closed");
		}

		if( ii_cursor <= 0)
		{
			return false;	
		}
		
		ii_cursor--;
				
		return true;
	}

	/**
	 * @see java.sql.ResultSet#setFetchDirection(int)
	 */
	public void setFetchDirection(int arg0) throws SQLException
	{

	}

	/**
	 * @see java.sql.ResultSet#getFetchDirection()
	 */
	public int getFetchDirection() throws SQLException
	{
		return 0;
	}

	/**
	 * @see java.sql.ResultSet#setFetchSize(int)
	 */
	public void setFetchSize(int arg0) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#getFetchSize()
	 */
	public int getFetchSize() throws SQLException
	{
		return 0;
	}

	/**
	 * @see java.sql.ResultSet#getType()
	 */
	public int getType() throws SQLException
	{
		return TYPE_SCROLL_INSENSITIVE;
	}

	/**
	 * @see java.sql.ResultSet#getConcurrency()
	 */
	public int getConcurrency() throws SQLException
	{
		return ResultSet.CONCUR_UPDATABLE;
	}

	/**
	 * @see java.sql.ResultSet#rowUpdated()
	 */
	public boolean rowUpdated() throws SQLException
	{
		return false;
	}

	/**
	 * @see java.sql.ResultSet#rowInserted()
	 */
	public boolean rowInserted() throws SQLException
	{
		return false;
	}

	/**
	 * @see java.sql.ResultSet#rowDeleted()
	 */
	public boolean rowDeleted() throws SQLException
	{
		return false;
	}

	/**
	 * @see java.sql.ResultSet#updateNull(int)
	 */
	public void updateNull(int arg0) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateBoolean(int, boolean)
	 */
	public void updateBoolean(int arg0, boolean arg1) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateByte(int, byte)
	 */
	public void updateByte(int arg0, byte arg1) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateShort(int, short)
	 */
	public void updateShort(int arg0, short arg1) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateInt(int, int)
	 */
	public void updateInt(int arg0, int arg1) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateLong(int, long)
	 */
	public void updateLong(int arg0, long arg1) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateFloat(int, float)
	 */
	public void updateFloat(int arg0, float arg1) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateDouble(int, double)
	 */
	public void updateDouble(int arg0, double arg1) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateBigDecimal(int, BigDecimal)
	 */
	public void updateBigDecimal(int arg0, BigDecimal arg1) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateString(int, String)
	 */
	public void updateString(int arg0, String arg1) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateBytes(int, byte[])
	 */
	public void updateBytes(int arg0, byte[] arg1) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateDate(int, Date)
	 */
	public void updateDate(int arg0, Date arg1) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateTime(int, Time)
	 */
	public void updateTime(int arg0, Time arg1) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateTimestamp(int, Timestamp)
	 */
	public void updateTimestamp(int arg0, Timestamp arg1) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateAsciiStream(int, InputStream, int)
	 */
	public void updateAsciiStream(int arg0, InputStream arg1, int arg2) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateBinaryStream(int, InputStream, int)
	 */
	public void updateBinaryStream(int arg0, InputStream arg1, int arg2) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateCharacterStream(int, Reader, int)
	 */
	public void updateCharacterStream(int arg0, Reader arg1, int arg2) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateObject(int, Object, int)
	 */
	public void updateObject(int arg0, Object arg1, int arg2) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateObject(int, Object)
	 */
	public void updateObject(int arg0, Object arg1) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateNull(String)
	 */
	public void updateNull(String arg0) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateBoolean(String, boolean)
	 */
	public void updateBoolean(String arg0, boolean arg1) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateByte(String, byte)
	 */
	public void updateByte(String arg0, byte arg1) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateShort(String, short)
	 */
	public void updateShort(String arg0, short arg1) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateInt(String, int)
	 */
	public void updateInt(String arg0, int arg1) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateLong(String, long)
	 */
	public void updateLong(String arg0, long arg1) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateFloat(String, float)
	 */
	public void updateFloat(String arg0, float arg1) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateDouble(String, double)
	 */
	public void updateDouble(String arg0, double arg1) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateBigDecimal(String, BigDecimal)
	 */
	public void updateBigDecimal(String arg0, BigDecimal arg1) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateString(String, String)
	 */
	public void updateString(String arg0, String arg1) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateBytes(String, byte[])
	 */
	public void updateBytes(String arg0, byte[] arg1) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateDate(String, Date)
	 */
	public void updateDate(String arg0, Date arg1) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateTime(String, Time)
	 */
	public void updateTime(String arg0, Time arg1) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateTimestamp(String, Timestamp)
	 */
	public void updateTimestamp(String arg0, Timestamp arg1) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateAsciiStream(String, InputStream, int)
	 */
	public void updateAsciiStream(String arg0, InputStream arg1, int arg2) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateBinaryStream(String, InputStream, int)
	 */
	public void updateBinaryStream(String arg0, InputStream arg1, int arg2) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateCharacterStream(String, Reader, int)
	 */
	public void updateCharacterStream(String arg0, Reader arg1, int arg2) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateObject(String, Object, int)
	 */
	public void updateObject(String arg0, Object arg1, int arg2) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateObject(String, Object)
	 */
	public void updateObject(String arg0, Object arg1) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#insertRow()
	 */
	public void insertRow() throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateRow()
	 */
	public void updateRow() throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#deleteRow()
	 */
	public void deleteRow() throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#refreshRow()
	 */
	public void refreshRow() throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#cancelRowUpdates()
	 */
	public void cancelRowUpdates() throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#moveToInsertRow()
	 */
	public void moveToInsertRow() throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#moveToCurrentRow()
	 */
	public void moveToCurrentRow() throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#getStatement()
	 */
	public Statement getStatement() throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getObject(int, Map)
	 */
	public Object getObject(int arg0, Map arg1) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getRef(int)
	 */
	public Ref getRef(int arg0) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getBlob(int)
	 */
	public Blob getBlob(int arg0) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getClob(int)
	 */
	public Clob getClob(int arg0) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getArray(int)
	 */
	public Array getArray(int arg0) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getObject(String, Map)
	 */
	public Object getObject(String arg0, Map arg1) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getRef(String)
	 */
	public Ref getRef(String arg0) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getBlob(String)
	 */
	public Blob getBlob(String arg0) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getClob(String)
	 */
	public Clob getClob(String arg0) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getArray(String)
	 */
	public Array getArray(String arg0) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getDate(int, Calendar)
	 */
	public Date getDate(int arg0, Calendar arg1) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getDate(String, Calendar)
	 */
	public Date getDate(String arg0, Calendar arg1) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getTime(int, Calendar)
	 */
	public Time getTime(int arg0, Calendar arg1) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getTime(String, Calendar)
	 */
	public Time getTime(String arg0, Calendar arg1) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getTimestamp(int, Calendar)
	 */
	public Timestamp getTimestamp(int arg0, Calendar arg1) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getTimestamp(String, Calendar)
	 */
	public Timestamp getTimestamp(String arg0, Calendar arg1) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getURL(int)
	 */
	public URL getURL(int arg0) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#getURL(String)
	 */
	public URL getURL(String arg0) throws SQLException
	{
		return null;
	}

	/**
	 * @see java.sql.ResultSet#updateRef(int, Ref)
	 */
	public void updateRef(int arg0, Ref arg1) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateRef(String, Ref)
	 */
	public void updateRef(String arg0, Ref arg1) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateBlob(int, Blob)
	 */
	public void updateBlob(int arg0, Blob arg1) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateBlob(String, Blob)
	 */
	public void updateBlob(String aS_ColumnName, Blob a_Blob) throws SQLException
	{
	}

	/**
	 * @see java.sql.ResultSet#updateClob(int, Clob)
	 */
	public void updateClob(int arg0, Clob a_Clob) throws SQLException
	{
		throw new NoSuchMethodError("DataSetResultSet does not support : updateClob(int , Clob ");				
	}

	/**
	 * @see java.sql.ResultSet#updateClob(String, Clob)
	 */
	public void updateClob(String aS_ColumnName, Clob a_Clob) throws SQLException
	{
		throw new NoSuchMethodError("DataSetResultSet does not support : updateClob(String , Clob ");		
	}

	/**
	 * @see java.sql.ResultSet#updateArray(int, Array)
	 */
	public void updateArray(int arg0, Array arg1) throws SQLException
	{
		throw new NoSuchMethodError("DataSetResultSet does not support : updateArray(int , Array ");
	}

	/**
	 * @see java.sql.ResultSet#updateArray(String, Array)
	 */
	public void updateArray(String aS_ColumnName, Array arg1) throws SQLException
	{
		throw new NoSuchMethodError("DataSetResultSet does not support : updateArray(String , Array ");
	}

}
