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
 * Demo application which demonstrate database connection from Swing
 * 
 * @author Sampsa Sohlman
 */
public class SwingDemo
{
	private JButton i_JButton_Add;
	private JButton i_JButton_Delete;
	private JButton i_JButton_Print;
	
	private JButton i_JButton_Read;
	private JButton i_JButton_Save;
	
	private String iS_TableName;
	private String iS_LastReadSQL = "";

	
	private JTextArea i_JTextArea_SQL;
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
			}
			if (a_ActionEvent.getSource() == i_JButton_Delete)
			{
				int li_row = i_JTable.getSelectedRow();
				i_SQLDataSet.removeRow(li_row + 1);
			}
			if (a_ActionEvent.getSource() == i_JButton_Print)
			{
				i_SQLDataSet.printBuffers(System.out);
				try
				{
					System.out.println("Table name");
					String lS_Table = getTableNameFromSelectSQL(iS_LastReadSQL);
					System.out.println(lS_Table);
					System.out.println("Write SQL statements");
					System.out.println(createInsertSQL(lS_Table));
					System.out.println(createUpdateSQL(lS_Table));
					System.out.println (createDeleteSQL(lS_Table));										
					
				}
				catch(Exception l_Exception)
				{
					
				}
				
			}
			if (a_ActionEvent.getSource() == i_JButton_Read)
			{
				System.out.println("Read");
				try
				{
					i_SQLDataSet.setSQLStatements(i_JTextArea_SQL.getText(),null,null,null);
					i_SQLDataSet.read();
					iS_LastReadSQL = i_SQLDataSet.getSelectSQL();
					System.out.println(i_SQLDataSet.getRowCount());
				}
				catch(DataSetException l_DataSetException)
				{
					l_DataSetException.printStackTrace();
				}
			}
			if (a_ActionEvent.getSource() == i_JButton_Save)
			{
				System.out.println("Save");
				
				try
				{
					String lS_Table = getTableNameFromSelectSQL(i_JTextArea_SQL.getText());
					i_SQLDataSet.setWriteSQLStametents(createInsertSQL(lS_Table), createUpdateSQL(lS_Table),createDeleteSQL(lS_Table));
					i_SQLDataSet.save();
				}
				catch(DataSetException l_DataSetException)
				{
					l_DataSetException.printStackTrace();
				}
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

		i_JButton_Read = new JButton("Read");
		i_JButton_Read.addActionListener(i_ActionListener);
		l_JPanel_Buttons.add(i_JButton_Read);
		
		i_JButton_Save = new JButton("Save");
		i_JButton_Save.addActionListener(i_ActionListener);
		l_JPanel_Buttons.add(i_JButton_Save);

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
	
		JPanel l_JPanel_SQL = new JPanel();
		l_JPanel_SQL.setLayout(new BorderLayout());	
		l_JPanel_SQL.setBorder(BorderFactory.createBevelBorder(1));
		
		i_JTextArea_SQL = new JTextArea();
		i_JTextArea_SQL.setRows(10);
		i_JTextArea_SQL.setAutoscrolls(true);
		i_JTextArea_SQL.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		l_JPanel_SQL.add(i_JTextArea_SQL);
		
		l_JPanel_List.add(l_JPanel_SQL, BorderLayout.NORTH);

		connectToDb();
		
		JPanel l_JPanel_JTable = new JPanel();

		l_JPanel_JTable.setLayout(new BorderLayout());	
		i_JTable = new JTable(new DataSetTableModel(getSQLDataSet()));
		i_JTable.setBorder(BorderFactory.createBevelBorder(1));
		i_JTable.setPreferredScrollableViewportSize(new Dimension(500, 70));
			
		JScrollPane l_JScrollPane_JTable = new JScrollPane(i_JTable);
		l_JPanel_JTable.add(l_JScrollPane_JTable, BorderLayout.CENTER);
		l_JPanel_List.add(l_JPanel_JTable, BorderLayout.CENTER);

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
		}
		catch (DataSetException l_DataSetException)
		{
			System.out.println(l_DataSetException.getMessage());
		}

		i_SQLDataSet = l_SQLDataSet;
		return i_SQLDataSet;
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

	public String getTableNameFromSelectSQL(String aS_Sql) throws DataSetException
	{
		String lS_SQL = aS_Sql.toUpperCase();

		int li_tableNameStart = lS_SQL.indexOf("FROM");
		
		if(li_tableNameStart==-1) throw new DataSetException("FROM clause not found");
		
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
	
		if(li_tableNameEnd==-1)
		{
			li_tableNameEnd = lS_SQL.length();	
		}

		String lS_TableName = aS_Sql.substring(li_tableNameStart, li_tableNameEnd).trim();

		// If space found more than one table defintion found 
		// this don't support alias tables with as or "" named tables		
		if(lS_TableName.indexOf(" ") > 0)
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

		for(int li_x = 1 ; li_x <= l_ColumnsInfo.getColumnCount() ; li_x++)
		{
			if(li_x>1)
			{
				lSb_InsertSQL.append(", ");
			}	
			lSb_InsertSQL.append(l_ColumnsInfo.getColumnName(li_x));		
		}
		
		
		lSb_InsertSQL.append(" ) VALUES ( ");
		
		
		
		for(int li_x = 1 ; li_x <= l_ColumnsInfo.getColumnCount() ; li_x++)
		{
			if(li_x>1)
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
		
		for(int li_x = 1 ; li_x <= l_ColumnsInfo.getColumnCount() ; li_x++)
		{
			if(li_x>1)
			{
				lSb_UpdateSQL.append(", ");
			}	
			lSb_UpdateSQL.append(l_ColumnsInfo.getColumnName(li_x));
			lSb_UpdateSQL.append(" = :n");
			lSb_UpdateSQL.append(li_x);			
		}
		
		lSb_UpdateSQL.append(" WHERE ");
		for(int li_x = 1 ; li_x <= l_ColumnsInfo.getColumnCount() ; li_x++)
		{
			if(li_x>1)
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
		for(int li_x = 1 ; li_x <= l_ColumnsInfo.getColumnCount() ; li_x++)
		{
			if(li_x>1)
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
