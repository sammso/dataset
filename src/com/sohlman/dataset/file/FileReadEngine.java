/*
DataSet Library
---------------
Copyright (C) 2001-2005 - Sampsa Sohlman, Teemu Sohlman

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
package com.sohlman.dataset.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.sohlman.dataset.ColumnInfo;
import com.sohlman.dataset.DataSetException;
import com.sohlman.dataset.ReadEngine;
import com.sohlman.dataset.Row;
import com.sohlman.dataset.RowInfo;

/**
 * This class fills DataSet from fixed position text files.
 * <br> In future there should be also feature for comma separated reader. 
 * 
 * @author Sampsa Sohlman
 *
 * @version 2003-05-02
 */
public class FileReadEngine implements ReadEngine
{	
	private File i_File;
	private BufferedReader i_BufferedReader;
	private FileRowInfo i_FileRowInfo;
	private int ii_counter = 0;
	private String iS_LastReadLine = null;
	private Timestamp iTs_RangeStart;
	private Timestamp iTs_RangeEnd;
	

	public FileReadEngine(File a_File, FileRowInfo a_FileRowInfo)
	{
		i_File = a_File;
		i_FileRowInfo = a_FileRowInfo;
	}

	public FileReadEngine(File a_File)
	{
		i_File = a_File;
	}

	public String getLastReadLine()
	{
		return iS_LastReadLine;
	}

	public File getFile()
	{
		return i_File;
	}

	/** 
	 * @see com.sohlman.dataset.ReadEngine#readStart
	 */
	public RowInfo readStart(RowInfo a_RowInfo) throws DataSetException
	{
		if (a_RowInfo == null && i_FileRowInfo == null)
		{
			throw new DataSetException("FileRowInfo has to be defined");
		}

		if (i_FileRowInfo == null)
		{
			i_FileRowInfo = (FileRowInfo) a_RowInfo;
		}

		try
		{
			i_BufferedReader = new BufferedReader(new FileReader(i_File));
		}
		catch (FileNotFoundException l_FileNotFoundException)
		{
			throw new DataSetException("FileNotFound", l_FileNotFoundException);
		}
		ii_counter = 0;
		return i_FileRowInfo;
	}

	/** 
	 * @see com.sohlman.dataset.ReadEngine#readRow
	 */
	public Row readRow(RowInfo a_RowInfo) throws DataSetException
	{
		Object[] l_Objects = new Object[a_RowInfo.getColumnCount()];

		try
		{
			String lS_Line = null;
			boolean lb_ok = false;

			while ((!lb_ok) && ((lS_Line = i_BufferedReader.readLine()) != null))
			{
				iS_LastReadLine = lS_Line;
				lb_ok = validateLine(lS_Line);
			}
			if (lb_ok && lS_Line != null)
			{
				for (int li_index = 1; li_index <= i_FileRowInfo.getColumnCount(); li_index++)
				{
					int li_startPosition = i_FileRowInfo.getColumnStartPosition(li_index);
					int li_endPosition = i_FileRowInfo.getColumnEndPosition(li_index);

					if (li_startPosition < 0 || li_startPosition > lS_Line.length())
					{
						l_Objects[li_index - 1] = null;
					}
					else
					{
						if (lS_Line.length() < li_endPosition)
						{
							li_endPosition = lS_Line.length();
						}
						String lS_Data = lS_Line.substring(i_FileRowInfo.getColumnStartPosition(li_index), li_endPosition);
						l_Objects[li_index - 1] = getObject(lS_Data, i_FileRowInfo.getColumnInfo(li_index));
					}
				}
				ii_counter++;
				return new Row(l_Objects, i_FileRowInfo);
			}
			else
			{
				return null;
			}
		}
		catch (IOException l_IOException)
		{
			throw new DataSetException("IOException", l_IOException);
		}
	}

	protected boolean validateLine(String aS_Line)
	{
		return true;
	}

	private Object getObject(String aS_Data, ColumnInfo a_ColumnInfo) throws DataSetException
	{
		if (aS_Data == null)
		{
			return null;
		}

		aS_Data = aS_Data.trim();
		FileColumnInfo l_FileColumnInfo = (FileColumnInfo) a_ColumnInfo;
		String lS_ClassName = l_FileColumnInfo.getClassName();

		try
		{
			if (lS_ClassName.equals("java.lang.String"))
			{
				aS_Data = aS_Data.trim();
				if (aS_Data.equals("") && l_FileColumnInfo.emptyIsNull())
				{
					return null;
				}
				else
				{
					return aS_Data;
				}
			}
			if (lS_ClassName.equals("java.lang.Boolean"))
			{

				return new Boolean(aS_Data);
			}
			else if (lS_ClassName.equals("java.lang.Byte"))
			{
				if (l_FileColumnInfo.getFormat().equalsIgnoreCase("hex"))
				{
					return Byte.decode(aS_Data);
				}
				else
				{
					return new Byte(aS_Data);
				}
			}
			else if (lS_ClassName.equals("java.math.BigInteger"))
			{
				return new BigInteger(aS_Data);
			}
			else if (lS_ClassName.equals("java.math.BigDecimal"))
			{
				return new BigDecimal(aS_Data);
			}
			else if (lS_ClassName.equals("java.sql.Date") || lS_ClassName.equals("java.util.Date"))
			{
				if (l_FileColumnInfo.getFormat() == null)
				{

					return java.sql.Date.valueOf(aS_Data);

				}
				else
				{
					SimpleDateFormat l_SimpleDateFormat = new SimpleDateFormat(l_FileColumnInfo.getFormat());
					java.util.Date l_Date = l_SimpleDateFormat.parse(aS_Data);
					l_Date = new java.sql.Date(l_Date.getTime());
					
					if(l_Date==null)
					{
						return null;
					}
					else if(l_Date.before(iTs_RangeStart) || l_Date.after(iTs_RangeEnd))
					{
						return null;
					}
				}
			}
			else if (lS_ClassName.equals("java.sql.Time"))
			{
				if (l_FileColumnInfo.getFormat() == null)
				{

					return java.sql.Time.valueOf(aS_Data);

				}
				else
				{
					SimpleDateFormat l_SimpleDateFormat = new SimpleDateFormat(l_FileColumnInfo.getFormat());
					java.util.Date l_Date = l_SimpleDateFormat.parse(aS_Data);
					
					return new java.sql.Time(l_Date.getTime());
				}
			}
			else if (lS_ClassName.equals("java.sql.Timestamp"))
			{
				if (l_FileColumnInfo.getFormat() == null)
				{

					return java.sql.Timestamp.valueOf(aS_Data);

				}
				else
				{
					SimpleDateFormat l_SimpleDateFormat = new SimpleDateFormat(l_FileColumnInfo.getFormat());
					java.util.Date l_Date = l_SimpleDateFormat.parse(aS_Data);
					Timestamp l_Timestamp = new Timestamp(l_Date.getTime());
					
					if(l_Timestamp==null)
					{
						return null;
					}
					else if((iTs_RangeStart!=null && l_Timestamp.before(iTs_RangeStart)) || (iTs_RangeEnd!=null && l_Timestamp.after(iTs_RangeEnd)))
					{
						return null;
					}
					
					return l_Timestamp;
				}
			}
			else if (lS_ClassName.equals("java.lang.Double"))
			{
				return new Double(Double.parseDouble(aS_Data));
			}
			else if (lS_ClassName.equals("java.lang.Float"))
			{
				return new Float(Float.parseFloat(aS_Data));
			}
			else if (lS_ClassName.equals("java.lang.Integer"))
			{
				return new Integer(Integer.parseInt(aS_Data));
			}
			else
			{
				throw new DataSetException(l_FileColumnInfo.getClassName() + " is unsupported class");
			}
		}
		catch (Exception l_Exception)
		{
			if (!l_FileColumnInfo.emptyIsNull())
			{
				throw new DataSetException("Bad data format in column " + l_FileColumnInfo.getName() + " Format " + l_FileColumnInfo.getFormat(), l_Exception);	
			}
		}
		return null;
	}

	/** 
	 * @see com.sohlman.dataset.ReadEngine#readEnd
	 */
	public int readEnd() throws DataSetException
	{
		return ii_counter;
	}
	
	
	
	/**
	 * Set range for Timestamp handling. If parsed date or Timestamp is outside then
	 * it is handled as null
	 * 
	 * @param aTs_Start Start time for period
	 * @param aTs_End End time for period
	 */
	public void setTimestampRange(Timestamp aTs_Start, Timestamp aTs_End)
	{
		iTs_RangeStart = aTs_Start;
		iTs_RangeEnd = aTs_End;
	}	
	
	/**
	 * @see #setTimestampRange(Timestamp, Timestamp)
	 * 
	 * @return Timestamp StartRange
	 */
	public Timestamp getTimestampStartRange()
	{
		return iTs_RangeStart;
	}

	/**
	 * @see #setTimestampRange(Timestamp, Timestamp)
	 * 
	 * @return Timestamp StartRange
	 */	
	public Timestamp getTimestampEndRange()
	{
		return iTs_RangeEnd;
	}
}
