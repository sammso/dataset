package com.sohlman.sqltool;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.sohlman.dataset.DataSetException;
import com.sohlman.dataset.sql.SQLColumnInfo;
import com.sohlman.dataset.sql.SQLDataSet;
import com.sohlman.dataset.swing.DataSetTableModel;
import com.sohlman.easylayout.EasyLayout;
import com.sohlman.easylayout.Position;

/**
 * @author Sampsa Sohlman
 * 
 * @version Dec 31, 2003
 */
public class SQLResultJPanel extends JPanel implements ActionListener
{
	private SQLDataSet i_SQLDataSet;
	private JTable i_JTable;
	private JButton i_JButton_Save;
	private JButton i_JButton_Close;
	private JButton i_JButton_Refresh;
	private JButton i_JButton_Add;
	private JButton i_JButton_Insert;

	private JButton i_JButton_Remove;
	private JTabbedPane i_JTabbedPane;
	private JTextArea i_JTextArea_Result;

	private SQLResultJPanel()
	{
		createComponents();
		layoutComponents();
	}

	private void createComponents()
	{
		i_JTable = new JTable();
		i_JButton_Add = new JButton("Add");
		i_JButton_Add.addActionListener(this);

		i_JButton_Insert = new JButton("Insert");
		i_JButton_Insert.addActionListener(this);

		i_JButton_Remove = new JButton("Remove");
		i_JButton_Remove.addActionListener(this);

		i_JButton_Close = new JButton("Close Tab");
		i_JButton_Close.addActionListener(this);

		i_JButton_Refresh = new JButton("Refresh");
		i_JButton_Refresh.addActionListener(this);

		i_JButton_Save = new JButton("Save");
		i_JButton_Save.addActionListener(this);

	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent a_ActionEvent)
	{
		if (a_ActionEvent.getSource() == i_JButton_Save)
		{
			if (i_SQLDataSet.hasWriteEngine())
			{
				try
				{
					i_SQLDataSet.save();
				}
				catch (DataSetException l_DataSetException)
				{
					l_DataSetException.printStackTrace();
				}
			}

		}
		else if (a_ActionEvent.getSource() == i_JButton_Close)
		{
			i_JTabbedPane.remove(this);
		}
		else if (a_ActionEvent.getSource() == i_JButton_Insert)
		{
			int li_row = i_JTable.getSelectedRow();
			if (li_row >= 0 && li_row < i_SQLDataSet.getRowCount())
			{
				i_SQLDataSet.insertRow(li_row + 1);
			}
		}
		else if (a_ActionEvent.getSource() == i_JButton_Add)
		{
			i_SQLDataSet.addRow();
		}
		else if (a_ActionEvent.getSource() == i_JButton_Remove)
		{
			int li_row = i_JTable.getSelectedRow();
			if (li_row >= 0 && li_row < i_SQLDataSet.getRowCount())
			{
				i_SQLDataSet.removeRow(li_row + 1);
			}
		}
		else if (a_ActionEvent.getSource() == i_JButton_Refresh)
		{
			this.execute();
		}
	}

	private void layoutComponents()
	{
		EasyLayout l_EasyLayout = new EasyLayout(new int[] { 0, 0, 100, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0, 0, 100 }, 2, 2);

		setLayout(l_EasyLayout);

		i_JTable.setBorder(BorderFactory.createBevelBorder(1));
		i_JTable.setPreferredScrollableViewportSize(new Dimension(500, 70));

		JScrollPane l_JScrollPane_JTable = new JScrollPane(i_JTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(l_JScrollPane_JTable, new Position(0, 0, 4, 7));

		add(i_JButton_Refresh, new Position(0, 0));

		add(i_JButton_Close, new Position(5, 0));
		add(i_JButton_Refresh, new Position(5, 1));
		add(i_JButton_Save, new Position(5, 2));
		add(i_JButton_Add, new Position(5, 3));
		add(i_JButton_Insert, new Position(5, 4));
		add(i_JButton_Remove, new Position(5, 5));

	}

	public static SQLResultJPanel createInstance(String aS_SQL, Connection a_Connection, JTextArea a_JTextArea)
	{

		SQLDataSet l_SQLDataSet = new SQLDataSet();
		try
		{
			l_SQLDataSet.setConnection(a_Connection);
			l_SQLDataSet.setSQLSelect(aS_SQL);
			l_SQLDataSet.setAutoGenerateWriteSQL(true);

			int li_count = l_SQLDataSet.read();

			if (li_count > 0)
			{
				a_JTextArea.append(li_count + " rows read\n");
				SQLResultJPanel l_SQLResultJPanel = new SQLResultJPanel();

				l_SQLResultJPanel.i_SQLDataSet = l_SQLDataSet;
				l_SQLResultJPanel.i_JTextArea_Result = a_JTextArea;
				l_SQLResultJPanel.i_JTable.setModel(new DataSetTableModel(l_SQLDataSet));
				Enumeration enum = l_SQLResultJPanel.i_JTable.getColumnModel().getColumns();
				while (enum.hasMoreElements())
				{
					TableColumn l_TableColumn = (TableColumn) enum.nextElement();
					int li_column = l_TableColumn.getModelIndex();
					SQLColumnInfo l_SQLColumnInfo = (SQLColumnInfo) l_SQLDataSet.getRowInfo().getColumnInfo(li_column + 1);
					l_TableColumn.setCellEditor(getCellEditor(l_SQLColumnInfo));
					l_TableColumn.setCellRenderer(getCellRenderer(l_SQLColumnInfo));
				}

				return l_SQLResultJPanel;
			}
			else
			{
				return null;
			}
		}
		catch (DataSetException l_DataSetException)
		{
			a_JTextArea.append("ERROR:\n\t" + l_DataSetException.getMessage() + "\n");
			return null;
		}
	}

	public static TableCellEditor getCellEditor(SQLColumnInfo a_SQLColumnInfo)
	{
		return null;
	}

	public static TableCellRenderer getCellRenderer(SQLColumnInfo a_SQLColumnInfo)
	{
		// here should be put correct editor
		return null;
	}

	public void setJTabbedPane(JTabbedPane a_JTabbedPane)
	{
		i_JTabbedPane = a_JTabbedPane;
	}

	public int execute()
	{
		try
		{
			int li_count = i_SQLDataSet.read();

			i_JTextArea_Result.append(li_count + " rows read\n");
			return li_count;

		}
		catch (DataSetException l_DataSetException)
		{
			i_JTextArea_Result.append("ERROR:\n\t" + l_DataSetException.getMessage() + "\n");
			return -1;
		}
	}
}
