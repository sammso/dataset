/*
DataSet Library
---------------
Copyright (C) 2001-2004 - Sampsa Sohlman, Teemu Sohlman

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
package com.sohlman.dataset.swing;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.PlainDocument;

import com.sohlman.dataset.ColumnInfo;
import com.sohlman.dataset.DataSet;

/**
 * 
 * @author Sampsa Sohlman
 * 
 * @version 2004-02-07
 */
public class DataSetDocument extends PlainDocument
{
	private int ii_row;
	private int ii_column;
	private int ii_maxTextSize;
	private boolean ib_emptyIsNull = false;
	private DataSet i_DataSet;
	private String aS_Format;
	
	private DocumentListener i_DocumentListener = new DocumentListener()
	{
		/**
		 * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
		 */
		public void changedUpdate(DocumentEvent a_DocumentEvent)
		{
			// TODO Auto-generated method stub
			System.out.println("changedUpdate");
			System.out.println(a_DocumentEvent.getDocument());
		}

		/**
		 * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
		 */
		public void insertUpdate(DocumentEvent a_DocumentEvent)
		{
			// TODO Auto-generated method stub

		}

		 /**
		 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
		 */
		public void removeUpdate(DocumentEvent a_DocumentEvent)
		{
			// TODO Auto-generated method stub

		}

	};
	
	public DataSetDocument(DataSet a_DataSet, int ai_row, int ai_column, String aS_Format)
	{
		i_DataSet = a_DataSet;
		ii_row = ai_row;
		ii_column = ai_column;
	}
	
	
	/**
	 * @see javax.swing.text.Document#getLength()
	 */
	public int getLength()
	{
		Object l_Object = i_DataSet.getValueAt(ii_row, ii_column);
		
		if(l_Object==null)
		{
			return 0;
		}
		else
		{
			return String.valueOf(l_Object).length();	
		}
	}
	
	
	
	private String getText()
	{	
		ColumnInfo l_ColumnInfo = i_DataSet.getRowInfo().getColumnInfo(ii_column);
		
		if(l_ColumnInfo.getColumnClass().isAssignableFrom(String.class))
		{
			return (String)i_DataSet.getValueAt(ii_row, ii_column);
		}
		else if(l_ColumnInfo.getColumnClass().isAssignableFrom(Number.class))
		{
			StringBuffer l_StringBuffer = new StringBuffer();
			
			Object l_Object = i_DataSet.getValueAt(ii_row, ii_column);
			return String.valueOf(l_Object);
		}
		else if(l_ColumnInfo.getColumnClass().isAssignableFrom(java.util.Date.class))
		{
			
			Object l_Object = i_DataSet.getValueAt(ii_row, ii_column);
			return String.valueOf(l_Object);
		}
		else
		{
			Object l_Object = i_DataSet.getValueAt(ii_row, ii_column);
			return String.valueOf(l_Object);
		}
	}
	
	
}
