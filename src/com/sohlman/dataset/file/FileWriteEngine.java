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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import com.sohlman.dataset.DataSet;
import com.sohlman.dataset.DataSetException;
import com.sohlman.dataset.DataSetService;
import com.sohlman.dataset.Row;
import com.sohlman.dataset.RowContainer;
import com.sohlman.dataset.WriteEngine;

/**
 * 
 * FileWriteEngine for saving results to text. Current version supports only fixed length files
 * but future version will support also comma separated files.
 * 
 * 
 * @author Sampsa Sohlman
 * @version 2003-01-26
 */
public class FileWriteEngine implements WriteEngine
{
	private File i_File;
	private int ii_counter;
	private int ii_rowSize;	
	/**
	 * Constructor for FileWriteEngine.
	 */
	public FileWriteEngine(File a_File)
	{
		setFile(a_File);
	}
	
	public FileWriteEngine()
	{
	}
	
	public void setFile(File a_File)
	{
		i_File = a_File;	
	}
	

	/**
	 * @see com.sohlman.dataset.WriteEngine#writeStart()
	 */
	public void writeStart() throws DataSetException
	{
		ii_counter=0;
	}

	/**
	 * @see com.sohlman.dataset.WriteEngine#write(DataSet)
	 */
	public void write(DataSet a_DataSet) throws DataSetException
	{
		FileRowInfo l_FileRowInfo = (FileRowInfo)a_DataSet.getRowInfo();
		ii_rowSize = 0;		
		for(int li_index = 1 ; li_index <= l_FileRowInfo.getColumnCount() ; li_index++)
		{
			int li_endPosition = l_FileRowInfo.getColumnEndPosition(li_index);
			if( li_endPosition > ii_rowSize)
			{
				ii_rowSize = li_endPosition;
			}
		}

		FileWriter l_FileWriter = null;		

		try
		{
			l_FileWriter = new FileWriter(i_File);			
			PrintWriter l_PrintWriter = new PrintWriter(l_FileWriter);
			RowContainer l_RowContainer;
	
			Iterator l_Iterator = a_DataSet.getAllRows().iterator();
	
			while (l_Iterator.hasNext())
			{
				l_RowContainer = (RowContainer) l_Iterator.next();
				writeRow(l_PrintWriter,l_RowContainer.getRow(), l_FileRowInfo);
			}			
		}
		catch(IOException l_IOException)
		{
			throw new DataSetException(l_IOException);
		}
		finally
		{
			try
			{
				if(l_FileWriter!=null)
				{
					l_FileWriter.close();
				}
			}
			catch(Exception l_Exception)
			{
			}
		}
		
	}
	
	private void writeRow(PrintWriter a_PrintWriter, Row a_Row, FileRowInfo a_FileRowInfo) throws DataSetException
	{
		StringBuffer l_StringBuffer = DataSetService.createSpaceFilledStringBuffer(ii_rowSize);
				
		for(int li_index = 1 ; li_index <= a_FileRowInfo.getColumnCount() ; li_index++)
		{
			FileColumnInfo l_FileColumnInfo = (FileColumnInfo)a_FileRowInfo.getColumnInfo(li_index);

			String l_String = getFormattedString(l_FileColumnInfo, a_Row.getValueAt(li_index));

			DataSetService.setStringToStringBuffer(l_StringBuffer,l_String,l_FileColumnInfo.getStartPosition());			
		}
		
		a_PrintWriter.println(l_StringBuffer.toString());
	}



	/**
	 * @see com.sohlman.dataset.WriteEngine#writeEnd()
	 */
	public int writeEnd() throws DataSetException
	{
		return 0;
	}
	
	public String getFormattedString(FileColumnInfo a_FileColumnInfo, Object a_Object) throws DataSetException
	{
		if (a_Object == null)
		{
			return "";
		}
		
		String lS_ClassName = a_FileColumnInfo.getClassName();

		try
		{
			if (lS_ClassName.equals("java.lang.String"))
			{
				String l_String = (String)a_Object;
				
				l_String.trim();
				
				return l_String;
			}
			if (lS_ClassName.equals("java.lang.Boolean"))
			{
				Boolean l_Boolean = (Boolean)a_Object;
				return l_Boolean.toString();
			}
			else if (lS_ClassName.equals("java.lang.Byte"))
			{
				Byte l_Byte = (Byte) a_Object;
				
				// Not sure if it is working.
				
				return l_Byte.toString();
			}
			else if (lS_ClassName.equals("java.math.BigInteger"))
			{
				BigInteger l_BigInteger = (BigInteger)a_Object;
				
				return l_BigInteger.toString();
			}
			else if (lS_ClassName.equals("java.math.BigDecimal"))
			{
				BigDecimal l_BigDecimal = (BigDecimal)a_Object;
				return l_BigDecimal.toString();
			}
			else if (lS_ClassName.equals("java.sql.Date"))
			{
				java.sql.Date l_Date = (java.sql.Date)a_Object;
				
				if (a_FileColumnInfo.getFormat() == null)
				{
					return l_Date.toString();
				}
				else
				{
					SimpleDateFormat l_SimpleDateFormat = new SimpleDateFormat(a_FileColumnInfo.getFormat());
        
        			return l_SimpleDateFormat.format(l_Date);
				}
			}
			else if (lS_ClassName.equals("java.util.Date"))
			{
				java.util.Date l_Date = (java.util.Date)a_Object;
				
				if (a_FileColumnInfo.getFormat() == null)
				{
					return l_Date.toString();
				}
				else
				{
					SimpleDateFormat l_SimpleDateFormat = new SimpleDateFormat(a_FileColumnInfo.getFormat());
        
        			return l_SimpleDateFormat.format(l_Date);
				}
			}
			
			else if (lS_ClassName.equals("java.sql.Time"))
			{
				java.sql.Time l_Time = (java.sql.Time)a_Object;
				
				if (a_FileColumnInfo.getFormat() == null)
				{
					return l_Time.toString();
				}
				else
				{
					SimpleDateFormat l_SimpleDateFormat = new SimpleDateFormat(a_FileColumnInfo.getFormat());
        
        			return l_SimpleDateFormat.format(l_Time);
				}
			}			
			else if (lS_ClassName.equals("java.sql.Timestamp"))
			{
				java.sql.Timestamp l_Timestamp = (java.sql.Timestamp)a_Object;
				
				if (a_FileColumnInfo.getFormat() == null)
				{
					return l_Timestamp.toString();
				}
				else
				{
					SimpleDateFormat l_SimpleDateFormat = new SimpleDateFormat(a_FileColumnInfo.getFormat());
        
        			return l_SimpleDateFormat.format(l_Timestamp);
				}
			}
			else if (lS_ClassName.equals("java.lang.Double"))
			{
				Double l_Double = (Double)a_Object;
				return l_Double.toString();
			}
			else if (lS_ClassName.equals("java.lang.Float"))
			{
				Float l_Float = (Float)a_Object;
				return l_Float.toString();
			}
			else if (lS_ClassName.equals("java.lang.Integer"))
			{
				Integer l_Integer = (Integer)a_Object;
				return l_Integer.toString();
			}
			else
			{
				throw new DataSetException(a_FileColumnInfo.getClassName() + " is unsupported class");
			}
		}
		catch (Exception l_Exception)
		{
			if (!a_FileColumnInfo.emptyIsNull())
			{
				throw new DataSetException("Bad data format", l_Exception);
			}
		}
		return "";
	}
}
