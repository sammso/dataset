package com.sohlman.dataset.sql;

import com.sohlman.dataset.ColumnInfo;


/**
 * @author Sampsa Sohlman
 * @version 2003-12-11
 */
public class SQLColumnInfo extends ColumnInfo
{
	private int ii_type;
	private String iS_TypeName = "";
	private String iS_SchemaName = "";
	private String iS_TableName = "";
	private String iS_CatalogName = "";
	
	private boolean ib_isNullable = true;
	private boolean ib_isAutoIncrement = false;
	private boolean ib_isCurrency = false;
	private boolean ib_isReadOnly = false;
	private boolean ib_isSigned = false;
	
	
	private int ii_displaySize = 0;
	private int ii_scale = 0;
	private int ii_precision = 0;
	
	
	
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
	
	/**(Internal use) Set's type name like varchar
	 * @param aS_TypeName
	 */
	void setColumnTypeName(String aS_TypeName)
	{
		iS_TypeName = aS_TypeName;
	}
	
	/**
	 * Returns type name in database server
	 * 
	 * @return Type name of column in database
	 */
	public String getTypeName()
	{
		return iS_TypeName;
	}

	/**(Internal use) Set if it is possilbe hold null value
	 * @param ab_boolean
	 */
	void setIsNullable(boolean ab_boolean)
	{
		ib_isNullable = ab_boolean;
	}
	
	/**
	 * Tells if column is nullable in database
	 * 
	 * @param ab_boolean
	 * @return
	 */
	public boolean isNullable(boolean ab_boolean)
	{
		return ib_isNullable;
	}
	
	/** (Internal use) Sets autoIncrement
	 * @param ab_boolean
	 */
	void setIsAutoIncrement(boolean ab_boolean)
	{
		ib_isAutoIncrement = ab_boolean;
	}	
	
	/**
	 * Tells if column is autoincrement in database
	 * 
	 * @return if value is autoincrement in database
	 */
	public boolean isAutoIncrement()
	{
		return ib_isAutoIncrement;
	}
	
	/** (Internal use) Set's DisplaySize
	 * @param ai_size
	 */
	void setDisplaySize(int ai_size)
	{
		ii_displaySize = ai_size;
	}
	
	/**
	 * This is mostly ment give values of
	 * databases exaple size of char(20) or varchar(20)
	 * 
	 * @return usually max String size of column
	 */
	public boolean getCurrency()
	{
		return ib_isCurrency;
	}
	
	/** (Internal use) Set's DisplaySize
	 * @param ai_size
	 */
	void setCurrency(boolean ab_isCurrency)
	{
		ib_isCurrency = ab_isCurrency;
	}
	
	/**
	 * This is mostly ment give values of
	 * databases exaple size of char(20) or varchar(20)
	 * 
	 * @return usually max String size of column
	 */
	public int getDisplaySize()
	{
		return ii_displaySize;
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
