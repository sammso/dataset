package com.sohlman.dataset.sql.swingdemo;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import java.awt.*;
import java.awt.event.*;
import com.sohlman.dataset.*;
import com.sohlman.dataset.swing.*;

import java.sql.*;

import com.sohlman.dataset.sql.*;
/**
 * @author Sampsa Sohlman
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SwingDemo
{
	private JButton i_JButton_Add;
	private JButton i_JButton_Delete;
	private JButton i_JButton_Print;
	private JTextArea i_JTextArea_SQL;
	private JFrame i_JFrame;
	private JTable i_JTable;
	private DataSet i_DataSet = null;
	private Connection i_Connection = null;

	private ActionListener i_ActionListener = new ActionListener()
	{
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
		 */
		public void actionPerformed(ActionEvent a_ActionEvent)
		{
			if (a_ActionEvent.getSource() == i_JButton_Add)
			{
				i_DataSet.addRow();
				i_JTable.updateUI();
			}
			if (a_ActionEvent.getSource() == i_JButton_Delete)
			{
				int li_row = i_JTable.getSelectedRow();
				i_DataSet.removeRow(li_row + 1);
				i_JTable.updateUI();
			}
			if (a_ActionEvent.getSource() == i_JButton_Print)
			{
				i_DataSet.printBuffers(System.out);
				
				
				
				
				
				//System.out.println("Table name is \"" + getTableNameFromSelectSQL(i_JTextArea_SQL.getText()) + "\"");
			}
		}
	};

	private int ii_number = 0;

	public static void main(String[] aS_Args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		}
		catch (Exception l_Exception)
		{
		}

		(new SwingDemo()).execute(aS_Args);
	}

	public void execute(String[] aS_Args)
	{
		i_JFrame = new JFrame("SQL DataSet demo");
		Container l_Container = i_JFrame.getContentPane();
		JPanel l_JPanel_Buttons = new JPanel();
		l_JPanel_Buttons.setLayout(new BoxLayout(l_JPanel_Buttons, BoxLayout.Y_AXIS));
		l_JPanel_Buttons.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

		i_JButton_Add = new JButton("Add");
		i_JButton_Add.addActionListener(i_ActionListener);
		l_JPanel_Buttons.add(i_JButton_Add);

		i_JButton_Delete = new JButton("Delete");
		i_JButton_Delete.addActionListener(i_ActionListener);
		l_JPanel_Buttons.add(i_JButton_Delete);

		i_JButton_Print = new JButton("Print");
		i_JButton_Print.addActionListener(i_ActionListener);
		l_JPanel_Buttons.add(i_JButton_Print);

		JPanel l_JPanel_List = new JPanel();
		l_JPanel_List.setLayout(new BorderLayout());

		i_JTextArea_SQL = new JTextArea();
		i_JTextArea_SQL.setRows(10);
		i_JTextArea_SQL.setAutoscrolls(true);
		i_JTextArea_SQL.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

		l_JPanel_List.add(i_JTextArea_SQL, BorderLayout.NORTH);

		connectToDb();
		i_JTable = new JTable(new JDataSet(getSQLDataSet()));
		i_JTable.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		l_JPanel_List.add(i_JTable);

		JPanel l_JPanel = new JPanel();
		l_JPanel.setLayout(new BorderLayout());
		l_JPanel.add(l_JPanel_List, BorderLayout.CENTER);
		l_JPanel.add(l_JPanel_Buttons, BorderLayout.EAST);

		l_Container.add(l_JPanel, BorderLayout.CENTER);

		i_JFrame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});

		i_JFrame.pack();
		i_JFrame.setVisible(true);
	}

	private DataSet getSQLDataSet()
	{
		SQLDataSet l_SQLDataSet = new SQLDataSet();

		ConnectionContainer l_ConnectionContainer = new ConnectionContainer()
		{
			/**
			 * @see com.sohlman.dataset.sql.ConnectionContainer#beginTransaction()
			 */
			public void beginTransaction() throws SQLException
			{
				setConnection(i_Connection);
			}

			/**
			 * @see com.sohlman.dataset.sql.ConnectionContainer#endTransaction()
			 */
			public void endTransaction() throws SQLException
			{
				setConnection(null);
			}
		};

		try
		{
			l_SQLDataSet.setConnectionContainer(l_ConnectionContainer);
			l_SQLDataSet.setSQLStatements("select * from test", null, null, null);
			l_SQLDataSet.read();
		}
		catch (DataSetException l_DataSetException)
		{
			System.out.println(l_DataSetException.getMessage());
		}

		i_DataSet = (DataSet) l_SQLDataSet;
		return i_DataSet;
	}

	public void connectToDb()
	{
		try
		{
			Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver");
			i_Connection = DriverManager.getConnection("jdbc:microsoft:sqlserver://localhost;databasename=testdb", "dbotestdb", "test");
			i_Connection.setAutoCommit(true);
		}
		catch (Exception l_Exception)
		{

		}

	}

	private DataSet getDataSet()
	{
		DataSet l_DataSet = new DataSet();

		String[] lS_Columns = { "java.lang.Integer", "java.lang.String", "java.sql.Timestamp" };

		//l_DataSet.setModelRowObject(new BasicRow(l_Objects));
		// OR
		l_DataSet.defineRow(lS_Columns);

		int li_row;

		l_DataSet.setReadEngine(new ReadEngine()
		{
			private int ii_rowId = 0;
			public Row readStart(Row a_Row_Model) throws DataSetException
			{
				return a_Row_Model;
			}

			public int readRow(Row a_Row) throws DataSetException
			{
				if (ii_rowId >= 10)
				{
					return DataSet.NO_MORE_ROWS;
				}
				ii_rowId++;
				a_Row.setValueAt(1, new Integer(ii_rowId));
				a_Row.setValueAt(2, "Hello world '" + ii_rowId + "'");
				a_Row.setValueAt(3, null);

				return ii_rowId;
			}

			/** Last action when all the rows are retrieved
			 * @return How may rows are retrieved
			 */
			public int readEnd() throws DataSetException
			{
				return ii_rowId - 1;
			}

		});

		try
		{
			l_DataSet.read();
		}
		catch (DataSetException l_DataSetException)
		{
		}
		i_DataSet = l_DataSet;
		return l_DataSet;

	}

	public String getTableNameFromSelectSQL(String aS_Sql)
	{
		String lS_SQL = aS_Sql.toUpperCase();

		int li_tableNameStart = lS_SQL.indexOf("FROM") + 4;
		int li_tableNameEnd = lS_SQL.indexOf("WHERE", li_tableNameStart);
		if (li_tableNameEnd == -1)
		{
			li_tableNameEnd = lS_SQL.indexOf("ORDER", li_tableNameStart);
			if (li_tableNameEnd == -1)
			{
				li_tableNameEnd = lS_SQL.indexOf("GROUP", li_tableNameStart);
			}
		}
	
		if(li_tableNameEnd==-1)
		{
			li_tableNameEnd = lS_SQL.length();	
		}

		String lS_TableName = aS_Sql.substring(li_tableNameStart, li_tableNameEnd).trim();
		

		return lS_TableName;
	}

}
