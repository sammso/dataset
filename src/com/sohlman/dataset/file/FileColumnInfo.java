package com.sohlman.dataset.file;

import java.sql.Timestamp;

import com.sohlman.dataset.ColumnInfo;

/**
 * @author Sampsa Sohlman
 */
public class FileColumnInfo extends ColumnInfo
{
	public static final int NO_POSITION = -1;
	private boolean ib_emptyIsNull;
	private String iS_Format;
	private int ii_startPos;
	private int ii_endPos;

	/**
	 * Constructor FileColumnInfo.
	 * @param aS_Name
	 * @param aS_ClassName
	 * @param aS_Format
	 * @param ab_emptyIsNull
	 * @param ai_startPos
	 * @param ai_endPos
	 * @throws ClassNotFoundException
	 */
	public FileColumnInfo(String aS_Name, String aS_ClassName, String aS_Format, boolean ab_emptyIsNull, int ai_startPos, int ai_endPos) throws ClassNotFoundException
	{
		super(aS_Name, aS_ClassName);
		ib_emptyIsNull = ab_emptyIsNull;
		iS_Format = aS_Format; 
		ii_startPos = ai_startPos;
		ii_endPos = ai_endPos;
	}
	
	/**
	 * Constructor FileColumnInfo<br>
	 * Defines empty column that is not read from file and default value read value is null
	 * 
	 * @param aS_Name
	 * @param aS_ClassName
	 * @param aS_Format
	 * @throws ClassNotFoundException
	 */
	public FileColumnInfo(String aS_Name, String aS_ClassName, String aS_Format) throws ClassNotFoundException
	{
		super(aS_Name, aS_ClassName);
		ib_emptyIsNull = true;
		iS_Format = aS_Format;
		ii_startPos = NO_POSITION;
		ii_endPos = NO_POSITION;
	}	
	
	/**
	 * Method FileColumnInfo.
	 * @param aS_Name
	 * @param aS_ClassName
	 * @param aS_Format
	 * @param ab_emptyIsNull
	 * @throws ClassNotFoundException
	 */
	public FileColumnInfo(String aS_Name, String aS_ClassName, String aS_Format, boolean ab_emptyIsNull) throws ClassNotFoundException
	{
		super(aS_Name, aS_ClassName);
		ib_emptyIsNull = ab_emptyIsNull;
		iS_Format = aS_Format;
		ii_startPos = NO_POSITION;
		ii_endPos = NO_POSITION;		
	}
	
	/**
	 * Constructor FileColumnInfo.
	 * @param aS_Name
	 * @param a_Class
	 * @param aS_Format
	 * @param ab_emptyIsNull
	 * @param ai_startPos
	 * @param ai_endPos
	 */
	public FileColumnInfo(String aS_Name, Class a_Class, String aS_Format, boolean ab_emptyIsNull, int ai_startPos, int ai_endPos)
	{
		super(aS_Name, a_Class);
		ib_emptyIsNull = ab_emptyIsNull;
		iS_Format = aS_Format; 
		ii_startPos = ai_startPos;
		ii_endPos = ai_endPos;
	}
	
	/**
	 * Constructor FileColumnInfo<br>
	 * Defines empty column that is not read from file and default value read value is null
	 * 
	 * @param aS_Name
	 * @param a_Class
	 * @param aS_Format
	 */
	public FileColumnInfo(String aS_Name, Class a_Class, String aS_Format)
	{
		super(aS_Name, a_Class);
		ib_emptyIsNull = true;
		iS_Format = aS_Format;
		ii_startPos = NO_POSITION;
		ii_endPos = NO_POSITION;
	}	
	
	/**
	 * Method FileColumnInfo.
	 * @param aS_Name
	 * @param a_Class
	 * @param aS_Format
	 * @param ab_emptyIsNull
	 */
	public FileColumnInfo(String aS_Name, Class a_Class, String aS_Format, boolean ab_emptyIsNull)
	{
		super(aS_Name, a_Class);
		ib_emptyIsNull = ab_emptyIsNull;
		iS_Format = aS_Format;
		ii_startPos = NO_POSITION;
		ii_endPos = NO_POSITION;		
	}	
	
	/**
	 * Method getFormat.
	 * @return String
	 */
	public String getFormat()
	{
		return iS_Format;
	}
	
	/**
	 * Method getStartPosition.
	 * @return int
	 */
	public int getStartPosition()
	{
		return ii_startPos;
	}

	/**
	 * @return int
	 */
	public int getEndPosition()
	{
		return ii_endPos;
	}	
	
	public boolean emptyIsNull()
	{
		return ib_emptyIsNull;			
	}
}
