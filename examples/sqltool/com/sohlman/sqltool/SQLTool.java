package com.sohlman.sqltool;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

/**
 * Demo application which demonstrate database connection from Swing
 * 
 * @author Sampsa Sohlman
 */
public class SQLTool
{
	private JButton i_JButton_Reset;
	private JButton i_JButton_Print;

	private JButton i_JButton_Execute;

	private JTextArea i_JTextArea_SQL;
	private JTextArea i_JTextArea_ResultText;
	private JTabbedPane i_JTabbedPane;

	private JFrame i_JFrame;
	private Connection i_Connection = null;
	private int ii_index = 1;

	private ActionListener i_ActionListener = new ActionListener()
	{
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
		 */
		public void actionPerformed(ActionEvent a_ActionEvent)
		{
			if (a_ActionEvent.getSource() == i_JButton_Print)
			{
				//i_SQLDataSet.printBuffers(System.out);
			}
			if (a_ActionEvent.getSource() == i_JButton_Execute)
			{
				String lS_SQL;

				lS_SQL = i_JTextArea_SQL.getSelectedText();
				
				if(lS_SQL==null || lS_SQL.trim().equals(""))
				{
					i_JTextArea_ResultText.append("\n (" + new Timestamp(System.currentTimeMillis()) + ") - Executing selected statements : \n");
					lS_SQL = i_JTextArea_SQL.getText().trim();
				}
				else
				{
					i_JTextArea_ResultText.append("\n (" + new Timestamp(System.currentTimeMillis()) + ") - Executing statements : \n");
				}
				
				
				SQLClauseTokenizer l_SQLClauseTokenizer = new SQLClauseTokenizer(lS_SQL);

				if (l_SQLClauseTokenizer.hasMoreTokens())
				{
					while (l_SQLClauseTokenizer.hasMoreTokens())
					{
						lS_SQL = l_SQLClauseTokenizer.nextToken();
						i_JTextArea_ResultText.append("\n " + ii_index + " - (" + new Timestamp(System.currentTimeMillis()) +") : \n");
						SQLResultJPanel l_SQLResultJPanel = SQLResultJPanel.createInstance(lS_SQL, i_Connection, i_JTextArea_ResultText);
						if(l_SQLResultJPanel!=null)
						{
							i_JTabbedPane.add(l_SQLResultJPanel, String.valueOf(ii_index));
							l_SQLResultJPanel.setJTabbedPane(i_JTabbedPane);
						}
						ii_index++;
					}
				}
				else
				{
					i_JTextArea_ResultText.append("\n " + ii_index + " - (" + new Timestamp(System.currentTimeMillis()) + ") : \n");
					SQLResultJPanel l_SQLResultJPanel = SQLResultJPanel.createInstance(lS_SQL, i_Connection, i_JTextArea_ResultText);
					if(l_SQLResultJPanel!=null)
					{
						i_JTabbedPane.add(l_SQLResultJPanel, String.valueOf(ii_index));
						l_SQLResultJPanel.setJTabbedPane(i_JTabbedPane);
					}
					ii_index++;
				}

				i_JTabbedPane.setSelectedIndex(0);

				setEnabledDisabled();
			}

		}
	};

	/**
	 * Method setEnabledDisabled.
	 */
	private void setEnabledDisabled()
	{
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
				createComponents();
				initializeComponents();
				layoutComponents();
			}
		}
		else
		{
			System.out.println("Failed to read " + lS_FileName);
			printMessage();
		}
	}

	private void layoutComponents()
	{
		Container l_Container = i_JFrame.getContentPane();
		JPanel l_JPanel_Buttons = new JPanel();
		l_JPanel_Buttons.setLayout(new BoxLayout(l_JPanel_Buttons, BoxLayout.Y_AXIS));
		l_JPanel_Buttons.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

		i_JButton_Execute.setText("Execute");
		l_JPanel_Buttons.add(i_JButton_Execute);

		i_JButton_Print.setText("Print");
		l_JPanel_Buttons.add(i_JButton_Print);

		JPanel l_JPanel_List = new JPanel();
		l_JPanel_List.setLayout(new BorderLayout());

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

		l_JPanel_List.add(i_JTabbedPane, BorderLayout.CENTER);

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

		i_JFrame.pack();
		i_JFrame.setVisible(true);
	}

	private void initializeComponents()
	{
		i_JButton_Reset.addActionListener(i_ActionListener);

		i_JButton_Execute.addActionListener(i_ActionListener);

		i_JButton_Print.addActionListener(i_ActionListener);

		i_JFrame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
	}

	private void createComponents()
	{
		i_JFrame = new JFrame("SQL Tool v 0.1");
		i_JTabbedPane = new JTabbedPane();
		i_JTextArea_ResultText = new JTextArea();
		i_JTextArea_SQL = new JTextArea();
		i_JButton_Reset = new JButton();
		i_JButton_Execute = new JButton();
		i_JButton_Print = new JButton();
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
				if (!l_File.exists())
				{
					//System.out.println("File \"" + l_File.getAbsolutePath() + "\" not exits");					
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
