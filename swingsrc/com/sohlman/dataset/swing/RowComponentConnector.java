package com.sohlman.dataset.swing;

import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.sohlman.dataset.DataSet;
import com.sohlman.dataset.DataSetListener;

/**
 * @author Sampsa Sohlman
 * @version 24.4.2003
 */
public class RowComponentConnector
{	
	private FocusListener i_FocusListener = new FocusAdapter()
	{
		/**
		 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
		 */
		public void focusLost(FocusEvent arg0)
		{
			
		}
	};
	private ActionListener i_ActionListner;
	private DataSetListener i_DataSetListener;
	private DataSet i_DataSet;
	private int ii_row;
	
	public void setDataSet(DataSet a_DataSet)
	{
		i_DataSet = a_DataSet;
	}
	
	public void setCurrentRow(int ai_index)
	{
		if(ai_index < 0 || ai_index > i_DataSet.getRowCount())
		{
			throw new ArrayIndexOutOfBoundsException("index out of range.");
		}
		ii_row = ai_index;
	}
	
	public void setJComponent(JTextField a_JTextField, int ai_columnIndex)
	{
		
	}
	
	public void setJComponent(JTextArea a_JTextArea, int ai_columnIndex)
	{
		
	}
	
	public void setJComponent(JCheckBox a_JCheckBox, int ai_columnIndex, Object aO_True, Object aO_False)
	{
		
	}
	
	public void setJComponent(JComboBox a_JComboBox, int ai_columnIndex)
	{
		
	}
	
	public void setJComponent(JList a_JList, int ai_columnIndex)
	{
	}
	
	public void setJComponent(JLabel a_JLabel, int ai_columnIndex)
	{
		
	}
}