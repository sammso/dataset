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
package com.sohlman.dataset;

import java.util.EventObject;

/**
 * @author Sampsa Sohlman
 * 
 * @version 2003-03-25
 */
public class DataSetEvent extends EventObject
{
	/* Column explanations */
	
	public static final int NOTVALID = -1;
	public static final int ALL = -2;
	
	/* Event explanations */
	 
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
