package com.sohlman.dataset.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.sohlman.dataset.ColumnInfo;
import com.sohlman.dataset.RowInfo;
import com.sohlman.dataset.DataSetException;
import com.sohlman.dataset.ReadEngine;
import com.sohlman.dataset.Row;

/**
 * @author Sampsa Sohlman
 *
 * @version 2002-10-22
 */
public class FileReadEngine implements ReadEngine
{
	private File i_File;
	private BufferedReader i_BufferedReader;
	private FileRowInfo i_FileRowInfo;
	private int ii_counter;

	public FileReadEngine(File a_File, FileRowInfo a_FileColumnsInfo)
	{
		i_File = a_File;
		i_FileRowInfo = a_FileColumnsInfo;
	}

	/** 
	 * @see com.sohlman.dataset.ReadEngine#readStart
	 */
	public RowInfo readStart(RowInfo a_RowInfo) throws DataSetException
	{
		if (a_RowInfo == null)
		{
			throw new IllegalArgumentException("FileRowInfo has to be defined");
		}

		i_FileRowInfo = (FileRowInfo) a_RowInfo;

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

		Row l_Row = new Row(l_Objects, a_RowInfo);

		try
		{
			String lS_Line = null;
			boolean lb_ok = false;
			//        while((!lb_ok) && ((lS_Line = i_RandomAccessFile.readLine())!=null))
			while ((!lb_ok) && ((lS_Line = i_BufferedReader.readLine()) != null))
			{
				lb_ok = validateLine(lS_Line);
			}
			if (lb_ok && lS_Line != null)
			{
				for (int li_index = 1; li_index <= i_FileRowInfo.getColumnCount(); li_index++)
				{
					String lS_Data = lS_Line.substring(i_FileRowInfo.getColumnStartPosition(li_index), i_FileRowInfo.getColumnEndPosition(li_index));

					//String lS_Data = lS_Line.substring( i_ColumnInfos[li_x].ii_startIndex, i_ColumnInfos[li_x].ii_endIndex);
					l_Objects[li_index - 1] = getObject(lS_Data, i_FileRowInfo.getColumnInfo(li_index));
				}
				ii_counter++;
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
		return null;
	}
	
	private boolean validateLine(String aS_Line)
	{
		return true;
	}

	private Object getObject(String aS_Data, ColumnInfo a_ColumnInfo)
	{
		if (aS_Data == null)
		{
			return null;
		}
		
		
/*		
		switch ()
		{
			case Types.INTEGER :
				return StringService.parseInteger(aS_Data);
			case Types.TIMESTAMP :
				// Fix this
				return StringService.parseTimestamp(aS_Data, aS_Format, iTs_ValidStart, iTs_ValidEnd);
			case Types.VARCHAR :
			case Types.CHAR :
				return aS_Data.trim();
		}
*/		return null;
	}

	/** 
	 * @see com.sohlman.dataset.ReadEngine#readEnd
	 */
	public int readEnd() throws DataSetException
	{
		return 0;
	}
}
