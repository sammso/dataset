package com.sohlman.datasetapps.sqltool;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.sohlman.dataset.*;
import com.sohlman.dataset.swing.*;

import java.sql.*;
import java.util.Enumeration;
import java.util.Properties;

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
				int[] li_rows = i_JTable.getSelectedRows();

				for (int li_y = li_rows.length - 1; li_y > 0; li_y--)
				{
					System.out.println(li_rows[li_y]);
					i_SQLDataSet.removeRow(li_rows[li_y] + 1);
				}
				setEnabledDisabled();
			}
			if (a_ActionEvent.getSource() == i_JButton_Print)
			{
				i_SQLDataSet.printBuffers(System.out);
			}
			if (a_ActionEvent.getSource() == i_JButton_Read)
			{
				try
				{
					i_SQLDataSet.reset();
					i_SQLDataSet.setSQLStatements(i_JTextArea_SQL.getText(), null, null, null);
					i_SQLDataSet.read();
					i_JTextArea_ResultText.append(i_SQLDataSet.getRowCount() + " rows read \r\n");
					i_JTabbedPane.setSelectedIndex(0);

				}
				catch (DataSetException l_DataSetException)
				{
					if (l_DataSetException.getSourceException() != null && l_DataSetException.getSourceException() instanceof SQLException)
					{
						SQLException l_SQLException = (SQLException) l_DataSetException.getSourceException();
						i_JTextArea_ResultText.append("Error\r\n" + l_SQLException.getMessage() + "\r\n");
						i_JTabbedPane.setSelectedIndex(1);
					}

				}
				setEnabledDisabled();
			}
			if (a_ActionEvent.getSource() == i_JButton_Save)
			{
				try
				{
					i_SQLDataSet.save();
					i_JTextArea_ResultText.append("Saved!\r\n");
					i_JTabbedPane.setSelectedIndex(1);

				}
				catch (DataSetException l_DataSetException)
				{
					if (l_DataSetException.getSourceException() != null && l_DataSetException.getSourceException() instanceof SQLException)
					{
						SQLException l_SQLException = (SQLException) l_DataSetException.getSourceException();
						i_JTextArea_ResultText.append("Error\r\n" + l_SQLException.getMessage() + "\r\n");
						i_JTabbedPane.setSelectedIndex(1);

					}
				}
				setEnabledDisabled();
			}

		}
	};


	/**
	 * Method setEnabledDisabled.
	 */
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

		if (i_SQLDataSet.getColumnCount() > 0)
		{
			i_JButton_Add.setEnabled(true);
		}
		else
		{
			i_JButton_Add.setEnabled(false);
		}

		i_JButton_Save.setEnabled(i_SQLDataSet.hasWriteEngine());

	}

	/**
	 * Method main.
	 * @param aS_Arguments
	 */
	public static void main(String[] aS_Arguments)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		}
		catch (Exception l_Exception)
		{
		}
		(new SQLTool()).execute(aS_Arguments);

	}

	/**
	 * Method printMessage.
	 */
	protected static void printMessage()
	{
		System.out.println();		
		System.out.println("Usage : ");
		System.out.println("java -jar SQLTool.jar <ini file (optional)>");
		System.out.println();
	}

	public void execute(String[] aS_Arguments)
	{
		String lS_FileName = null;

		if (aS_Arguments.length > 0)
		{
			lS_FileName = aS_Arguments[0];
		}

		Properties l_Properties = loadProperties(lS_FileName);

		if (l_Properties != null)
		{
			if (connectToDb(l_Properties))
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

//				l_JPanel_Buttons.add(i_JButton_Print);

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
				l_JPanel_Select.add(l_JPanel_SQL, BorderLayout.CENTER);
				l_JPanel_Select.add(l_JLabel, BorderLayout.NORTH);

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
		}
		else
		{
			System.out.println("Failed to read " + lS_FileName);
			printMessage();
		}
	}

	private SQLDataSet getSQLDataSet()
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

		l_SQLDataSet.setAutoGenerateWriteSQL(true);
		i_SQLDataSet = l_SQLDataSet;
		return i_SQLDataSet;
	}

	public boolean connectToDb(Properties a_Properties)
	{

		try
		{
			ClassLoader l_ClassLoader = loadDriver(a_Properties);

			if (l_ClassLoader == null)
			{
				System.out.println("Driver not is not set");
				printMessage();
				return true;
			}
			String lS_Class = a_Properties.getProperty("sql.class");
			String lS_Url = a_Properties.getProperty("sql.url");
			String lS_UserId = a_Properties.getProperty("sql.userid");
			String lS_Password = a_Properties.getProperty("sql.password");
			String lS_Catalog = a_Properties.getProperty("sql.catalog");

			Class l_Class = l_ClassLoader.loadClass(lS_Class);
			Driver l_Driver = (Driver) l_Class.newInstance();

			if (lS_UserId != null)
			{
				Properties l_Properties = new Properties();
				l_Properties.put("user", lS_UserId);
				l_Properties.put("password", lS_Password);
				i_Connection = l_Driver.connect(lS_Url, l_Properties);
			}
			else
			{
				Properties l_Properties = new Properties();
				i_Connection = l_Driver.connect(lS_Url, l_Properties);
			}

			if (lS_Catalog != null)
			{
				i_Connection.setCatalog(lS_Catalog);
			}

			i_Connection.setAutoCommit(true);
			return true;

		}
		catch (ClassNotFoundException l_ClassNotFoundException)
		{
			System.out.println("Driver not found");
			System.out.println("Message: \n" + l_ClassNotFoundException.getMessage());
			printMessage();
			return false;
		}

		catch (SQLException l_SQLException)
		{
			l_SQLException.printStackTrace();
			return false;
		}
		catch (MalformedURLException l_MalformedURLException)
		{
			l_MalformedURLException.printStackTrace();
			return false;
		}
		catch (InstantiationException l_InstantiationException)
		{
			l_InstantiationException.printStackTrace();		
			return false;
		}
		catch (IllegalAccessException l_IllegalAccessException)
		{
			return false;
		}
	}

	public Properties loadProperties(String aS_FileName)
	{
		Properties l_Properties = new Properties();
		try
		{
			if (aS_FileName == null)
			{
				aS_FileName = "SQLTool.ini";
			}
			FileInputStream l_FileInputStream = new FileInputStream(aS_FileName);

			l_Properties.load(l_FileInputStream);

			l_FileInputStream.close();
			return l_Properties;
		}
		catch (IOException l_IOException)
		{

			return null;
		}
	}

	public ClassLoader loadDriver(Properties a_Properties) throws MalformedURLException
	{
		Enumeration l_Enumeration = a_Properties.propertyNames();
		int li_jarCount = 0;
		while (l_Enumeration.hasMoreElements())
		{
			String lS_Name = (String) l_Enumeration.nextElement();

			if (lS_Name.startsWith("jar"))
			{
				li_jarCount++;
			}
		}

		if (li_jarCount == 0)
		{
			return null;
		}

		URL[] l_URL_List = new URL[li_jarCount];

		l_Enumeration = a_Properties.propertyNames();
		int li_counter = 0;
		while (l_Enumeration.hasMoreElements())
		{
			String lS_Name = (String) l_Enumeration.nextElement();

			if (lS_Name.startsWith("jar"))
			{
				String lS_Value = a_Properties.getProperty(lS_Name);
				File l_File = new File(lS_Value);
				if(!l_File.exists())
				{
					System.out.println("File \"" + l_File.getAbsolutePath() + "\" not exits");					
				}
				else
				{
					System.out.println(l_File.getName() + " : " + l_File.exists());
					l_URL_List[li_counter] = l_File.toURL();
					li_counter++;
				}
			}
		}

		return new URLClassLoader(l_URL_List);
	}
}
