package com.sohlman.datasetswing;
import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.sohlman.dataset.ColumnInfo;
import com.sohlman.dataset.DataSet;
import com.sohlman.dataset.RowInfo;
import com.sohlman.dataset.swing.DataSetComboBoxModel;
import com.sohlman.dataset.swing.DataSetListModel;
import com.sohlman.dataset.swing.RowToComponentConnector;
import com.sohlman.easylayout.EasyLayout;
import com.sohlman.easylayout.Position;

/**
 * @author Sampsa Sohlman
 * @version 13.5.2003
 */
public class Main extends JFrame
{
	JButton i_JButton_Add;
	JButton i_JButton_Remove;
	JTextField i_JTextField_Name;
	JList i_JList;
	
	RowToComponentConnector i_RowToComponentConnector;
	
	ActionListener i_ActionListener = new ActionListener()
	{
		public void actionPerformed(ActionEvent a_ActionEvent)
		{
			if(a_ActionEvent.getSource() == i_JButton_Add)
			{
				if(i_DataSet_List.getRowCount() == 0)
				{
					i_RowToComponentConnector.setVisible(true);
				}
				
				int li_row = i_DataSet_List.addRow();
				i_JList.setSelectedIndex(li_row - 1);
				i_RowToComponentConnector.setCurrentRow(li_row);
				i_JTextField_Name.grabFocus();
								
			}
			else if(a_ActionEvent.getSource() == i_JButton_Remove)
			{
				int li_index = i_JList.getSelectedIndex();
				
				if(li_index != -1)
				{
					if(i_DataSet_List.getRowCount() > 0)
					{							
						i_DataSet_List.removeRow(li_index + 1);
						i_JList.setSelectedIndex(li_index);
						if(li_index < i_DataSet_List.getRowCount())
						{
							i_RowToComponentConnector.setCurrentRow(li_index + 1);
						}
						else
						{
							i_RowToComponentConnector.setCurrentRow(li_index );
						}
					}
					else
					{
						i_RowToComponentConnector.setVisible(false);
					}									
				}				
			} 
		}
	};
	
	ListSelectionListener i_ListSelectionListener = new ListSelectionListener()
	{
		public void valueChanged(ListSelectionEvent a_ListSelectionEvent)
		{
			if(a_ListSelectionEvent.getSource()==i_JList)
			{
				int li_index = i_JList.getSelectedIndex();
				if(li_index >= 0)
				{
					int li_row = li_index + 1;
					if(i_DataSet_List.getRowCount() > 0)
					{
						i_RowToComponentConnector.setCurrentRow(li_row);
					}
					else
					{
						i_RowToComponentConnector.setVisible(false);
					}
				}
			}
		}
	};
	
	/**
	 * 
	 */
	public Main()
	{
		super();

		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try
		{	createDataSets();
			createControls();
		}
		catch (Exception l_Exception)
		{
			l_Exception.printStackTrace();
		}
	}

	DataSet i_DataSet_ComboBox, i_DataSet_List;

	protected void createDataSets()
	{
		i_DataSet_List = new DataSet();
		i_DataSet_List.setRowInfo(
			new RowInfo(
				new ColumnInfo[] {
					new ColumnInfo("Name", String.class),
					new ColumnInfo("Value", String.class),
					new ColumnInfo("Story", String.class)}));
		
		int li_row;

		
		for(int li_index = 0 ; li_index < 5 ; li_index ++)
		{
			li_row = i_DataSet_List.addRow();
			if(li_index % 2 == 0)
			{
				i_DataSet_List.setValue(new Object[]{ "Name is " + li_index, "Eka", "My story no " + li_index + " Eka"}, li_row);
			}
			else
			{
				i_DataSet_List.setValue(new Object[]{ "Name is " + li_index, "Toka", "My story no " + li_index  + " Toka"}, li_row);
			}
		}		
				
		i_DataSet_ComboBox = new DataSet();
		i_DataSet_ComboBox.setRowInfo(
			new RowInfo(
				new ColumnInfo[] {
					new ColumnInfo("Value", String.class)}));
		li_row = i_DataSet_ComboBox.addRow();
		i_DataSet_ComboBox.setValue(new Object[]{ "Eka" }, li_row); 
		li_row = i_DataSet_ComboBox.addRow();
		i_DataSet_ComboBox.setValue(new Object[]{ "Toka" }, li_row);		
		li_row = i_DataSet_ComboBox.addRow();
		i_DataSet_ComboBox.setValue(new Object[]{ "Kolmas" }, li_row);	

	}

	protected void createControls()
	{
		setTitle("DataSetSwing TestApplication");
		JPanel l_JPanel_ContentPane = (JPanel) this.getContentPane();
		int[] li_columnsPercentages = { 0, 0, 0, 100 };
		int[] li_rowPercentages = { 0, 0, 0, 100 };

		EasyLayout l_EasyLayout = new EasyLayout(null, null, li_columnsPercentages, li_rowPercentages, 3, 3);
		l_JPanel_ContentPane.setLayout(l_EasyLayout);

		JLabel l_JLabel_Name = new JLabel("Name");
		JLabel l_JLabel_List = new JLabel("List");		
		JLabel l_JLabel_Value = new JLabel("Value");
		JLabel l_JLabel_Story = new JLabel("Story");
		
		i_JButton_Add = new JButton("Add");
		i_JButton_Add.addActionListener(i_ActionListener);
		
		i_JButton_Remove = new JButton("Remove");
		i_JButton_Remove.addActionListener(i_ActionListener);
		
		i_JTextField_Name = new JTextField("");
		//JTextField l_JTextField_Story = new JTextField("");
		JTextArea l_JTextArea_Story = new JTextArea("");
		l_JTextArea_Story.setLineWrap(true);
		JScrollPane l_JScrollPane_JTextArea_Story = new JScrollPane(l_JTextArea_Story);//, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
		
		i_JList = new JList(new DataSetListModel(i_DataSet_List, 1));
		i_JList.addListSelectionListener(i_ListSelectionListener);
		JScrollPane l_JScrollPane_JList = new JScrollPane(i_JList);//, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED

		JComboBox l_JComboBox = new JComboBox(new DataSetComboBoxModel(i_DataSet_ComboBox, 1));

		i_RowToComponentConnector = new RowToComponentConnector();
		i_RowToComponentConnector.setDataSet(i_DataSet_List);
		i_RowToComponentConnector.setJComponent(i_JTextField_Name,1);
		i_RowToComponentConnector.setJComponent(l_JComboBox,2);
		i_RowToComponentConnector.setJComponent(l_JTextArea_Story,3);


		l_JPanel_ContentPane.add(l_JLabel_List, new Position(0,0));
		l_JPanel_ContentPane.add(l_JScrollPane_JList, new Position(0,1,1,3));
		l_JPanel_ContentPane.add(i_JButton_Add, new Position(1,0));
		l_JPanel_ContentPane.add(i_JButton_Remove, new Position(1,1));
		
		l_JPanel_ContentPane.add(l_JLabel_Name, new Position(2,0));
		l_JPanel_ContentPane.add(l_JLabel_Value, new Position(2,1));
		l_JPanel_ContentPane.add(l_JLabel_Story, new Position(2,2));
		
		l_JPanel_ContentPane.add(i_JTextField_Name, new Position(3,0));
		l_JPanel_ContentPane.add(l_JComboBox, new Position(3, 1));
		l_JPanel_ContentPane.add(l_JScrollPane_JTextArea_Story, new Position(2,3,2,1));
		//i_RowToComponentConnector.setCurrentRow(1);
		
		if(i_DataSet_List.getRowCount()>0)
		{
			i_JList.setSelectedIndex(0);
		}
		pack();
	}

	//Overridden so we can exit when window is closed
	protected void processWindowEvent(WindowEvent a_WindowEvent)
	{
		super.processWindowEvent(a_WindowEvent);
		if (a_WindowEvent.getID() == WindowEvent.WINDOW_CLOSING)
		{
			System.exit(0);
		}
	}

	public static void main(String[] aS_Arguments)
	{
		Main l_LayoutFrame = new Main();
		//Validate frames that have preset sizes
		//Pack frames that have useful preferred size info, e.g. from their layout

		l_LayoutFrame.validate();

		//Center the window
		Dimension l_Dimension_Size = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension l_Dimension_Frame = l_LayoutFrame.getSize();
		if (l_Dimension_Frame.height > l_Dimension_Size.height)
		{
			l_Dimension_Frame.height = l_Dimension_Size.height;
		}
		if (l_Dimension_Frame.width > l_Dimension_Size.width)
		{
			l_Dimension_Frame.width = l_Dimension_Size.width;
		}
		l_LayoutFrame.setLocation((l_Dimension_Size.width - l_Dimension_Frame.width) / 2, (l_Dimension_Size.height - l_Dimension_Frame.height) / 2);
		l_LayoutFrame.setVisible(true);
	}
}
