package com.sohlman.dataset.file;

import com.sohlman.dataset.RowInfo;

/**
 * @author Sampsa Sohlman
 */
public class FileRowInfo extends RowInfo
{	
	private String[] iS_FormatStrings;
	private int[] ii_startPositions;
	private int[] ii_endPositions;	
	
	/**
	 * Constructor for FileColumnsInfo.
	 * @param aS_ColumnClassNames
	 * @param aS_ColumnNames
	 */
	public FileRowInfo(FileColumnInfo[] a_FileColumnInfo)
	{
		super(a_FileColumnInfo);
	}
	
	public int getColumnStartPosition(int ai_index)
	{
		return ((FileColumnInfo)getColumnInfo(ai_index)).getStartPosition();
	}
	
	public int getColumnEndPosition(int ai_index)
	{
		return ((FileColumnInfo)getColumnInfo(ai_index)).getEndPosition();
	}
	
	public String getColumnFormatString(int ai_index)
	{
		return ((FileColumnInfo)getColumnInfo(ai_index)).getFormat();
	}
	
	public boolean emptyColumnIsNull(int ai_index)
	{
		return ((FileColumnInfo)getColumnInfo(ai_index)).emptyIsNull();
	}
}

