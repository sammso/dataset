package com.sohlman.dataset;

import java.io.Serializable;


/**
 * Column  info contains all needed data from column
 * 
 * 
 * @author Sampsa Sohlman
 */
public class ColumnInfo implements Serializable
{
	private String iS_Name;
	private Class i_Class;
	
	public ColumnInfo(String aS_Name, String aS_ClassName) throws ClassNotFoundException
	{
		setName(aS_Name);
		setClassName(aS_ClassName);
	}
	
	public ColumnInfo(String aS_Name, Class a_Class)
	{
		setName(aS_Name);
		setClass(a_Class);
	}		
	public String getName()
	{
		return iS_Name;
	}
	
	public String getClassName()
	{
		return i_Class.getName();
	}
	
	public Class getColumnClass()
	{
		return i_Class;
	}
	
	protected void setClass(Class a_Class)
	{
		if(a_Class==null)
		{
			throw new IllegalArgumentException("Class cannot be null");
		}
		i_Class = a_Class;
	}
	
	protected void setClassName(String aS_ClassName) throws ClassNotFoundException
	{
		setClass(Class.forName(aS_ClassName));
	}
	
	public void setName(String aS_Name)
	{
		if(aS_Name == null)
		{
			aS_Name = "";
		}
		iS_Name = aS_Name;
	}
	
	public boolean equals(ColumnInfo a_ColumnInfo)
	{
		return i_Class.getName().equals(a_ColumnInfo.getColumnClass().getName());
	}	
}
