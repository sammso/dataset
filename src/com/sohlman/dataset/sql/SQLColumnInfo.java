package com.sohlman.dataset.sql;

import com.sohlman.dataset.ColumnInfo;


/**
 * @author Sampsa Sohlman
 */
public class SQLColumnInfo extends ColumnInfo
{
	private int ii_type;
	/**
	 * Constructor for SQLColumnInfo.
	 * @param aS_Name
	 * @param aS_ClassName
	 * @param ai_type java.sql.Types defined datatype
	 * @throws ClassNotFoundException
	 */
	public SQLColumnInfo(String aS_Name, String aS_ClassName, int ai_type) throws ClassNotFoundException
	{
		super(aS_Name, aS_ClassName);
		ii_type = ai_type;
	}
	
	/**
	 * Constructor for SQLColumnInfo.
	 * @param aS_Name
	 * @param a_Class
	 * @param ai_type java.sql.Types defined datatype
	 */
	public SQLColumnInfo(String aS_Name, Class a_Class, int ai_type)
	{
		super(aS_Name, a_Class);
		ii_type = ai_type;
	}	

	

	/**
	 * Returns java.sql.Types type
	 * @return int
	 */
	public int getType()
	{
		return ii_type;			
	}
}
