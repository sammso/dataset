package com.sohlman.datasetapps.sqltool;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import java.awt.*;
import java.awt.event.*;
import com.sohlman.dataset.*;
import com.sohlman.dataset.swing.*;

import java.sql.*;

import com.sohlman.dataset.sql.*;
/**
 * Demo application which demonstrate database connection from Swing
 * 
 * @author Sampsa Sohlman
 */
public class SQLTool
{
	private JButton i_JButton_Add;
	private JButton i_JButton_Delete;
	private JButton i_JButton_Print;	

	private JButton i_JButton_Read;
	private JButton i_JButton_Save;

	private String iS_TableName;
	private String iS_LastReadSQL = "";

	private JTextArea i_JTextArea_SQL;
	private JTextArea i_JTextArea_ResultText;
	private JTabbedPane i_JTabbedPane;

	private JFrame i_JFrame;
	private JTable i_JTable;
	private SQLDataSet i_SQLDataSet = null;
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
				i_SQLDataSet.addRow();
				setEnabledDisabled();
			}
			if (a_ActionEvent.getSource() == i_JButton_Delete)
			{
//				int li_row = i_JTable.getSelectedRow();
				int[] li_rows = i_JTable.getSelectedRows();
				for(int li_y = li_rows.length - 1 ; li_y > 0 ; li_y--)
				{	
					System.out.println(li_y);
					i_SQLDataSet.removeRow(li_rows[li_y] + 1);
				}
				setEnabledDisabled();

			}
			if (a_ActionEvent.getSource() == i_JButton_Print)
			{
				i_SQLDataSet.printBuffers(System.out);
				try
				{
					String lS_Table = getTableNameFromSelectSQL(iS_LastReadSQL);
					i_JTextArea_ResultText.append("\r\nGenerated Write SQL statements:\r\n");
					i_JTextArea_ResultText.append("Insert:\r\n");
					i_JTextArea_ResultText.append(createInsertSQL(lS_Table) + "\r\n");
					i_JTextArea_ResultText.append("Update:\r\n");
					i_JTextArea_ResultText.append(createUpdateSQL(lS_Table) + "\r\n");									
					i_JTextArea_ResultText.append("Update:\r\n");
					i_JTextArea_ResultText.append(createDeleteSQL(lS_Table) + "\r\n");

				}
				catch (Exception l_Exception)
				{

				}

			}
			if (a_ActionEvent.getSource() == i_JButton_Read)
			{
				//System.out.println("Read");
				try
				{
					i_SQLDataSet.reset();
					i_SQLDataSet.setSQLStatements(i_JTextArea_SQL.getText(), null, null, null);
					i_SQLDataSet.read();
					i_JTextArea_ResultText.append(i_SQLDataSet.getRowCount() + " rows read \r\n");
					iS_LastReadSQL = i_SQLDataSet.getSelectSQL();
					i_JTabbedPane.setSelectedIndex(0);

				}
				catch (DataSetException l_DataSetException)
				{
					if (l_DataSetException.getSourceException() != null && l_DataSetException.getSourceException() instanceof SQLException)
					{
						SQLException l_SQLException = (SQLException)l_DataSetException.getSourceException();
						i_JTextArea_ResultText.append("Error\r\n" + l_SQLException.getMessage() + "\r\n");
						i_JTabbedPane.setSelectedIndex(1);
					}

				}
				setEnabledDisabled();
			}
			if (a_ActionEvent.getSource() == i_JButton_Save)
			{
				//System.out.println("Save");

				try
				{
					String lS_Table = getTableNameFromSelectSQL(i_JTextArea_SQL.getText());
					i_SQLDataSet.setWriteSQLStametents(createInsertSQL(lS_Table), createUpdateSQL(lS_Table), createDeleteSQL(lS_Table));
					i_SQLDataSet.save();
					i_JTextArea_ResultText.append("Saved!\r\n");
					i_JTabbedPane.setSelectedIndex(1);

				}
				catch (DataSetException l_DataSetException)
				{
					if (l_DataSetException.getSourceException() != null && l_DataSetException.getSourceException() instanceof SQLException)
					{
						SQLException l_SQLException = (SQLException)l_DataSetException.getSourceException();
						i_JTextArea_ResultText.append("Error\r\n" + l_SQLException.getMessage() + "\r\n");
						i_JTabbedPane.setSelectedIndex(1);

					}
				}
				setEnabledDisabled();
			}

		}
	};

	private int ii_number = 0;

	private void setEnabledDisabled()
	{
		if (i_SQLDataSet.getRowCount() > 0)
		{
			i_JButton_Delete.setEnabled(true);
		}
		else
		{
			i_JButton_Delete.setEnabled(false);
		}
		try
		{
			String lS_Table = getTableNameFromSelectSQL(iS_LastReadSQL);
			if (lS_Table.equals("") || i_SQLDataSet.getRowCount() <= 0)
			{
				i_JButton_Save.setEnabled(false);
				i_JButton_Add.setEnabled(false);
			}
			else
			{
				i_JButton_Save.setEnabled(true);
				i_JButton_Add.setEnabled(true);
			}

		}
		catch (DataSetException l_DataSetException)
		{
			i_JButton_Save.setEnabled(false);
			i_JButton_Delete.setEnabled(false);
		}
	}

	public static void main(String[] aS_Args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		}
		catch (Exception l_Exception)
		{
		}

		(new SQLTool()).execute(aS_Args);
	}
	
	protected void printMessage()
	{
		System.out.println("");		
		System.out.println("Parameters:");
		System.out.println(" -D<jdbc driver> (required)");
		System.out.println(" -C<connection url> (required)");
		System.out.println(" -U<user id> (required)");		
		System.out.println(" -P<password> (optional empty is used)");
		System.out.println(" -c<catalog> (optional)");		
	}

	public void execute(String[] aS_Arguments)
	{	
		String lS_Class = null;
		String lS_Url = null ;
		String lS_UserId = null;
		String lS_Password = "";
		String lS_Catalog = null;
		
		if(aS_Arguments == null || aS_Arguments.length == 0)
		{
				System.out.println("No parameters");
				printMessage();			
				return;
		}
		
		for(int li_y = 0 ; li_y < aS_Arguments.length; li_y++)
		{
			if(aS_Arguments[li_y].startsWith("-D"))
			{
				lS_Class = aS_Arguments[li_y].substring(2);
			}
			else if(aS_Arguments[li_y].startsWith("-C"))
			{
				lS_Url = aS_Arguments[li_y].substring(2);
			}
			else if(aS_Arguments[li_y].startsWith("-U"))
			{
				lS_UserId = aS_Arguments[li_y].substring(2);
			}	
			else if(aS_Arguments[li_y].startsWith("-P"))
			{
				lS_Password = aS_Arguments[li_y].substring(2);
			}
			else if(aS_Arguments[li_y].startsWith("-c"))
			{
				lS_Catalog = aS_Arguments[li_y].substring(2);
			}
			else
			{
				System.out.println("Unknown parameter" + aS_Arguments[li_y]);
				printMessage();
				return;
			}
		}
		
		if(lS_Class==null || lS_Url==null || lS_UserId == null )
		{
				System.out.println("Required parameter is missing.");
				printMessage();
		}
		try
		{
		connectToDb(lS_Class, lS_Url, lS_UserId, lS_Password, lS_Catalog);
		
		i_JFrame = new JFrame("SQL DataSet demo");
		Container l_Container = i_JFrame.getContentPane();
		JPanel l_JPanel_Buttons = new JPanel();
		l_JPanel_Buttons.setLayout(new BoxLayout(l_JPanel_Buttons, BoxLayout.Y_AXIS));
		l_JPanel_Buttons.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

		i_JButton_Read = new JButton("Read");
		i_JButton_Read.addActionListener(i_ActionListener);
		l_JPanel_Buttons.add(i_JButton_Read);

		i_JButton_Save = new JButton("Save");
		i_JButton_Save.setEnabled(false);
		i_JButton_Save.addActionListener(i_ActionListener);
		l_JPanel_Buttons.add(i_JButton_Save);

		i_JButton_Add = new JButton("Add");
		i_JButton_Add.setEnabled(false);
		i_JButton_Add.addActionListener(i_ActionListener);
		l_JPanel_Buttons.add(i_JButton_Add);

		i_JButton_Delete = new JButton("Delete");
		i_JButton_Delete.setEnabled(false);
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

		JScrollPane l_JScrollPane_SQL = new JScrollPane(i_JTextArea_SQL);

		

		JPanel l_JPanel_SQL = new JPanel();
		l_JPanel_SQL.setLayout(new BorderLayout());
		l_JPanel_SQL.setBorder(BorderFactory.createBevelBorder(1));
		l_JPanel_SQL.add(l_JScrollPane_SQL);
		
		JLabel l_JLabel = new JLabel();
		l_JLabel.setText("Select statement");
		JPanel l_JPanel_Select = new JPanel();
		l_JPanel_Select.setLayout(new BorderLayout());
		l_JPanel_Select.add(l_JPanel_SQL,BorderLayout.CENTER);
		l_JPanel_Select.add(l_JLabel,BorderLayout.NORTH);

		
		l_JPanel_List.add(l_JPanel_Select, BorderLayout.NORTH);

		JPanel l_JPanel_JTable = new JPanel();

		l_JPanel_JTable.setLayout(new BorderLayout());
		i_JTable = new JTable(new DataSetTableModel(getSQLDataSet()));
		i_JTable.setBorder(BorderFactory.createBevelBorder(1));
		i_JTable.setPreferredScrollableViewportSize(new Dimension(500, 70));

		JScrollPane l_JScrollPane_JTable = new JScrollPane(i_JTable);
		l_JPanel_JTable.add(l_JScrollPane_JTable, BorderLayout.CENTER);

		i_JTabbedPane = new JTabbedPane();
		i_JTabbedPane.add("Result data", l_JPanel_JTable);

		l_JPanel_List.add(i_JTabbedPane, BorderLayout.CENTER);

		i_JTextArea_ResultText = new JTextArea();
		JScrollPane l_JScrollPane_ResultText = new JScrollPane(i_JTextArea_ResultText);
		JPanel l_JPanel_ResultText = new JPanel();

		l_JPanel_ResultText.setLayout(new BorderLayout());
		l_JPanel_ResultText.setBorder(BorderFactory.createBevelBorder(1));
		l_JPanel_ResultText.add(l_JScrollPane_ResultText);


		i_JTabbedPane.add("Result text", l_JPanel_ResultText);
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
		catch(Exception l_Exception)
		{
			System.out.println("Error: " + l_Exception.getMessage());
			printMessage();
		}
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
		}
		catch (DataSetException l_DataSetException)
		{
			System.out.println(l_DataSetException.getMessage());
		}

		i_SQLDataSet = l_SQLDataSet;
		return i_SQLDataSet;
	}

	public void connectToDb(String aS_Class, String aS_Url, String aS_UserId, String aS_Password, String aS_Catalog) throws Exception
	{
			Class.forName(aS_Class);
			i_Connection = DriverManager.getConnection(aS_Url, aS_UserId, aS_Password);

			if(aS_Catalog!=null)
			{
				i_Connection.setCatalog(aS_Catalog);
			}

			i_Connection.setAutoCommit(true);

	}

	public String getTableNameFromSelectSQL(String aS_Sql) throws DataSetException
	{
		String lS_SQL = aS_Sql.toUpperCase();

		int li_tableNameStart = lS_SQL.indexOf("FROM");

		if (li_tableNameStart == -1)
			throw new DataSetException("FROM clause not found");

		li_tableNameStart += 4;

		int li_tableNameEnd = lS_SQL.indexOf("WHERE", li_tableNameStart);
		if (li_tableNameEnd == -1)
		{
			li_tableNameEnd = lS_SQL.indexOf("ORDER", li_tableNameStart);
			if (li_tableNameEnd == -1)
			{
				li_tableNameEnd = lS_SQL.indexOf("GROUP", li_tableNameStart);
			}
		}

		if (li_tableNameEnd == -1)
		{
			li_tableNameEnd = lS_SQL.length();
		}

		String lS_TableName = aS_Sql.substring(li_tableNameStart, li_tableNameEnd).trim();

		// If space found more than one table defintion found 
		// this don't support alias tables with as or "" named tables		
		if (lS_TableName.indexOf(" ") > 0)
		{
			throw new DataSetException("More than one table definitions exists");
		}
		return lS_TableName;
	}

	public String createInsertSQL(String aS_Table)
	{
		StringBuffer lSb_InsertSQL = new StringBuffer();
		ColumnsInfo l_ColumnsInfo = i_SQLDataSet.getColumnsInfo();

		lSb_InsertSQL.append("INSERT INTO ").append(aS_Table).append(" ( ");

		for (int li_x = 1; li_x <= l_ColumnsInfo.getColumnCount(); li_x++)
		{
			if (li_x > 1)
			{
				lSb_InsertSQL.append(", ");
			}
			lSb_InsertSQL.append(l_ColumnsInfo.getColumnName(li_x));
		}

		lSb_InsertSQL.append(" ) VALUES ( ");

		for (int li_x = 1; li_x <= l_ColumnsInfo.getColumnCount(); li_x++)
		{
			if (li_x > 1)
			{
				lSb_InsertSQL.append(", ");
			}
			lSb_InsertSQL.append(":");
			lSb_InsertSQL.append(li_x);
		}

		lSb_InsertSQL.append(" )");
		return lSb_InsertSQL.toString();
	}

	public String createUpdateSQL(String aS_Table)
	{
		StringBuffer lSb_UpdateSQL = new StringBuffer();
		lSb_UpdateSQL.append("UPDATE ").append(aS_Table).append(" SET ");

		ColumnsInfo l_ColumnsInfo = i_SQLDataSet.getColumnsInfo();

		for (int li_x = 1; li_x <= l_ColumnsInfo.getColumnCount(); li_x++)
		{
			if (li_x > 1)
			{
				lSb_UpdateSQL.append(", ");
			}
			lSb_UpdateSQL.append(l_ColumnsInfo.getColumnName(li_x));
			lSb_UpdateSQL.append(" = :n");
			lSb_UpdateSQL.append(li_x);
		}

		lSb_UpdateSQL.append(" WHERE ");
		for (int li_x = 1; li_x <= l_ColumnsInfo.getColumnCount(); li_x++)
		{
			if (li_x > 1)
			{
				lSb_UpdateSQL.append(" AND ");
			}
			lSb_UpdateSQL.append(l_ColumnsInfo.getColumnName(li_x));
			lSb_UpdateSQL.append(":isnull(:o");
			lSb_UpdateSQL.append(li_x);
			lSb_UpdateSQL.append(" ; IS NULL  ; = :o");
			lSb_UpdateSQL.append(li_x);
			lSb_UpdateSQL.append(")");
		}
		return lSb_UpdateSQL.toString();
	}

	public String createDeleteSQL(String aS_Table)
	{
		StringBuffer lSb_DeleteSQL = new StringBuffer();
		lSb_DeleteSQL.append("DELETE FROM ").append(aS_Table);

		ColumnsInfo l_ColumnsInfo = i_SQLDataSet.getColumnsInfo();

		lSb_DeleteSQL.append(" WHERE ");
		for (int li_x = 1; li_x <= l_ColumnsInfo.getColumnCount(); li_x++)
		{
			if (li_x > 1)
			{
				lSb_DeleteSQL.append(" AND ");
			}
			lSb_DeleteSQL.append(l_ColumnsInfo.getColumnName(li_x));
			lSb_DeleteSQL.append(":isnull(:o");
			lSb_DeleteSQL.append(li_x);
			lSb_DeleteSQL.append(" ; IS NULL  ; = :o");
			lSb_DeleteSQL.append(li_x);
			lSb_DeleteSQL.append(")");
		}
		return lSb_DeleteSQL.toString();
	}
}
