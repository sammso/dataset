package com.sohlman.dataset;

/**
 * Column  info contains all needed data from column
 * 
 * 
 * @author Sampsa Sohlman
 */
public class ColumnInfo
{
	private String iS_Name;
	private String iS_ClassName;
	
	public ColumnInfo(String aS_Name, String aS_ClassName)
	{
		setName(aS_Name);
		setClassName(aS_ClassName);
	}
	
	public String getName()
	{
		return iS_Name;
	}
	
	public String getClassName()
	{
		return iS_ClassName;
	}
	
	public Class getColumnClass() throws ClassNotFoundException
	{
		return Class.forName(iS_ClassName);
	}
	
	public void setClassName(String aS_ClassName)
	{
		if(aS_ClassName==null)
		{
			throw new IllegalArgumentException("Class name cannot be null");
		}
		iS_ClassName = aS_ClassName;
	}
	
	public void setName(String aS_Name)
	{
		if(aS_Name == null)
		{
			aS_Name = "";
		}
		iS_Name = aS_Name;
	}
}
