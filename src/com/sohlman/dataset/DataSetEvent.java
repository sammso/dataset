package com.sohlman.dataset;

import java.util.EventObject;

/**
 * @author Sampsa Sohlman
 * 
 * @version 2002-10-21
 */
public class DataSetEvent extends EventObject
{
	public static final int NOTVALID = -1;
	public static final int ALL = -2;
	public static final int ROW_INSERTED = 1;
	public static final int ROW_REMOVED = 2;
	public static final int READ_START = 3;	
	public static final int READ_END = 4;		
	public static final int SAVE_START = 5;
	public static final int SAVE_END = 6;
	public static final int RESET = 7;
	public static final int COLUMN_CHANGED = 8;
	
	private int ii_action;
	private int ii_row;
	private int ii_column;
	private int ii_count;
	
	DataSetEvent(DataSet a_DataSet, int ai_action, int ai_row, int ai_column)
	{
		super(a_DataSet);
		ii_action = ai_action;
		ii_row = ai_row;
		ii_column = ai_column;			
		ii_count = NOTVALID;		
	}
	
	DataSetEvent(DataSet a_DataSet, int ai_action)
	{
		super(a_DataSet);
		ii_action = ai_action;
		ii_row = NOTVALID;
		ii_column = NOTVALID;
		ii_count = NOTVALID;
	}
	
	DataSetEvent(DataSet a_DataSet, int ai_action, int ai_count)
	{
		super(a_DataSet);
		ii_action = ai_action;
		ii_row = NOTVALID;
		ii_column = NOTVALID;
		ii_count = ai_count;
	}		
	
	public int getColumn()
	{
		return ii_column;
	}
	
	public int getRow()
	{
		return ii_row;
	}
	
	public int getAction()
	{
		return ii_action;
	}
	
	public DataSet getDataSet()
	{
		return (DataSet)getSource();
	}
}
