package com.sohlman.dataset.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.text.JTextComponent;

import com.sohlman.dataset.DataSet;
import com.sohlman.dataset.DataSetListener;

/**
 * @author Sampsa Sohlman
 * @version 24.4.2003
 */
public class RowToComponentConnector
{
	Hashtable iHt_Components;

	private FocusListener i_FocusListener = new FocusAdapter()
	{
		/**
		 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
		 */
		public void focusLost(FocusEvent a_FocusEvent)
		{
			RowComponentContainer l_RowComponentContainer = (RowComponentContainer)iHt_Components.get(a_FocusEvent.getComponent());
			i_DataSet.setValueAt(l_RowComponentContainer.getValue(), ii_row,l_RowComponentContainer.getColumnIndex()); 
		}
	};
	private ActionListener i_ActionListner = new ActionListener()
	{
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent a_ActionEvent)
		{
			RowComponentContainer l_RowComponentContainer = (RowComponentContainer)iHt_Components.get(a_ActionEvent.getSource());
			i_DataSet.setValueAt(l_RowComponentContainer.getValue(), ii_row,l_RowComponentContainer.getColumnIndex()); 			
		}
	};
	private DataSetListener i_DataSetListener;
	private DataSet i_DataSet;
	private int ii_row;

	public void setDataSet(DataSet a_DataSet)
	{
		i_DataSet = a_DataSet;
	}

	public void setCurrentRow(int ai_index)
	{
		if (ai_index < 0 || ai_index > i_DataSet.getRowCount())
		{
			throw new ArrayIndexOutOfBoundsException("index out of range.");
		}
		ii_row = ai_index;
		
		Enumeration l_Enumeration = iHt_Components.elements();
		while(l_Enumeration.hasMoreElements())
		{
			Object l_Object = l_Enumeration.nextElement();
			RowComponentContainer l_RowComponentContainer = (RowComponentContainer)l_Object;
			l_RowComponentContainer.setValue(i_DataSet.getValueAt(ii_row,l_RowComponentContainer.getColumnIndex()));
		}
		
	}

	public void setJComponent(JTextComponent a_JTextComponent, int ai_columnIndex)
	{
		a_JTextComponent.addFocusListener(i_FocusListener);
		setRowComponentContainer(new RowComponentContainer(a_JTextComponent, ai_columnIndex, null));
	}

	public void setJComponent(JCheckBox a_JCheckBox, int ai_columnIndex, Object aO_True, Object aO_False)
	{
		Object[] lO_Values = { aO_True, aO_False };		
		a_JCheckBox.addActionListener(i_ActionListner);
		setRowComponentContainer(new RowComponentContainer(a_JCheckBox, ai_columnIndex, lO_Values));
	}

	private void setRowComponentContainer(RowComponentContainer a_RowComponentContainer)
	{
		if (iHt_Components == null)
		{
			iHt_Components = new Hashtable();
		}
		iHt_Components.put(a_RowComponentContainer.getComponent(), a_RowComponentContainer);
	}

	public void setJComponent(JComboBox a_JComboBox, int ai_columnIndex)
	{
		a_JComboBox.addActionListener(i_ActionListner);
		setRowComponentContainer(new RowComponentContainer(a_JComboBox, ai_columnIndex, null));
	}

	public void setJComponent(JList a_JList, int ai_columnIndex)
	{
	
	}

	public void setJComponent(JLabel a_JLabel, int ai_columnIndex)
	{
		//setRowComponentContainer(new RowComponentContainer(a_JLabel, ai_columnIndex, null));
	}

	public void enable(boolean ab_value)
	{
		
	}
}