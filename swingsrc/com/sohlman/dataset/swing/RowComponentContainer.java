package com.sohlman.dataset.swing;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.text.JTextComponent;

/**
 * @author Sampsa Sohlman
 * @version 25.4.2003
 */
class RowComponentContainer
{
	private int ii_columnIndex;
	private JComponent i_JComponent;
	private JTextComponent i_JTextComponent;
	private JCheckBox i_JCheckBox;
	private JComboBox i_JComboBox;

	private Object[] iO_Params;

	RowComponentContainer(JTextComponent a_JTextComponent, int ai_columnIndex, Object[] aO_Params)
	{
		ii_columnIndex = ai_columnIndex;
		i_JComponent = a_JTextComponent;
		iO_Params = aO_Params;
		i_JTextComponent = a_JTextComponent;
	}

	RowComponentContainer(JCheckBox a_JCheckBox, int ai_columnIndex, Object[] aO_Params)
	{
		ii_columnIndex = ai_columnIndex;
		i_JComponent = a_JCheckBox;
		iO_Params = aO_Params;
		i_JCheckBox = a_JCheckBox;
	}

	RowComponentContainer(JComboBox a_JComboBox, int ai_columnIndex, Object[] aO_Params)
	{
		ii_columnIndex = ai_columnIndex;
		i_JComponent = a_JComboBox;
		iO_Params = aO_Params;
		i_JComboBox = a_JComboBox;
	}

	JComponent getComponent()
	{
		return i_JComponent;
	}

	int getColumnIndex()
	{
		return ii_columnIndex;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object a_Object)
	{
		return i_JComponent == ((RowComponentContainer)a_Object).getComponent();
	}
	/**
	 * Returns has code of component
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return i_JComponent.hashCode();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return i_JComponent.toString();
	}

	public Object getValue()
	{
		if (i_JCheckBox != null)
		{
			if (i_JCheckBox.isSelected())
			{
				return iO_Params[0];
			}
			else
			{
				return iO_Params[1];
			}

		}
		else if (i_JTextComponent != null)
		{
			return i_JTextComponent.getText();
		}
		else if (i_JComboBox != null)
		{
			return i_JComboBox.getSelectedItem();
		}		

		return null;
	}
	
	public void setValue(Object a_Object)
	{
		System.out.println("XX");		
		if (i_JCheckBox != null)
		{

			if(a_Object.equals(iO_Params[0]))
			{
				i_JCheckBox.setSelected(true);
			}
			else
			{
				i_JCheckBox.setSelected(false);
			}

		}
		else if (i_JTextComponent != null)
		{
			i_JTextComponent.setText(a_Object.toString());
		}
		else if (i_JComboBox != null)
		{
			i_JComboBox.setSelectedItem(a_Object);
		}			
	}
}