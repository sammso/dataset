/*
 * SQLRetrieveEngine.java
 *
 * Created on 17. elokuuta 2001, 12:04
 */

package com.sohlman.dataset.sql;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import com.sohlman.dataset.RowReadEngine;
import com.sohlman.dataset.Row;
import com.sohlman.dataset.BasicRow;
import com.sohlman.dataset.DataSetException;
import com.sohlman.dataset.DataSetError;

/** Common retrieve engine for retrieving data from JDBC sources.
 *
 * @author Sampsa Sohlman
 * @version 2001-08-20
 * @see Update object
 */
public class SQLReadEngine implements RowReadEngine
{
    
    /** Creates new SQLRetrieveEngine */
    private ConnectionContainer i_ConnectionContainer = null;
    private Connection i_Connection = null;
    private ResultSet i_ResultSet = null;
    //private PreparedStatement i_PreparedStatement = null;
    private String iS_ReadSQL = null;
    
    private SQLSelectFilter i_SQLSelectFilter = null;
    
    int ii_columnCount = 0;
    private int ii_rowCount = 0;
    private String[] iS_ColumnNames = null;
    private Object[] iO_Parameters;
    private int[] ii_parameterOrder;
    
    public final static int DONE = -1;
    
    private int[] ii_columnTypes = null;
    
    /** Constructor
     */
    public SQLReadEngine()
    {
    }
    
    /** Constructor with parameters.<br>
     * With this constructor is possible to
     * set Connection, and SQL String
     * @param a_Connection Connection object
     * @param aS_RetrieveSQL select or stored procedure statement, where ? marks places for parameters.
     */
    public SQLReadEngine(ConnectionContainer a_ConnectionContainer, String aS_RetrieveSQL) throws DataSetException
    {
	setConnection(a_ConnectionContainer);
	setSQL(aS_RetrieveSQL);
    }
    
    public SQLReadEngine(ConnectionContainer a_ConnectionContainer, String aS_RetrieveSQL, Object[] aO_Parameters) throws DataSetException, SQLException
    {
	setConnection(a_ConnectionContainer);
	setSQL(aS_RetrieveSQL);
	iO_Parameters = aO_Parameters;
    }
    
    public void setSQLSelectFilter(SQLSelectFilter a_SQLSelectFilter)
    {
	i_SQLSelectFilter = a_SQLSelectFilter;
    }
    
    /** Connects to Connection object to this object.
     * @param a_Connecion Connection object
     */
    public void setConnection(ConnectionContainer a_ConnectionContainer)
    {
	i_ConnectionContainer = a_ConnectionContainer;
    }
    
    
    public void setParameter(int ai_index, Object a_Object)
    {
	if(iO_Parameters!=null && iO_Parameters.length >= ai_index)
	{
	    iO_Parameters[ai_index - 1] = a_Object;
	}
    }
    
    public Object getParameter(int ai_index)
    {
	if(iO_Parameters!=null && iO_Parameters.length > ai_index)
	{
	    return iO_Parameters[ai_index];
	}
	else
	{
	    return null;
	}
    }
    
    public int[] getColumnTypes()
    {
	return ii_columnTypes;
    }
    
    /** Set SQL statement for retrieve. Performs also PrepareStatement creation.
     * @param aS_RetrieveSQL select or stored procedure statement, where ? marks places for parameters.
     * @return true if success false if not.
     */
    public void setSQL(String aS_ReadSQL) throws DataSetException
    {
	try
	{
	    if(aS_ReadSQL != null)
	    {
		ii_parameterOrder = SQLService.getKeys(aS_ReadSQL,false);
		iO_Parameters = new Object[ii_parameterOrder.length];
		iS_ReadSQL = SQLService.createFinalSQL(aS_ReadSQL,false);
	    }
	    else
	    {
		ii_parameterOrder = null;
		iO_Parameters = null;
		iS_ReadSQL = null;
	    }
	}
	catch(SQLException a_SQLException)
	{
	    throw new DataSetException("Error while parsing Select statment", a_SQLException);
	}
    }
    
    /** This is first method to call retrieve operation.
     *
     */
    public void readStart(Row a_Row_Model) throws DataSetException
    {
	ii_rowCount = 0;
	try
	{
	    if(iS_ReadSQL != null)
	    {
		i_Connection = i_ConnectionContainer.getConnection();
		
		PreparedStatement l_PreparedStatement = i_Connection.prepareStatement(iS_ReadSQL, ResultSet.TYPE_FORWARD_ONLY ,ResultSet.CONCUR_READ_ONLY);
		for(int li_c = 0 ; li_c < ii_parameterOrder.length ; li_c++)
		{
		    l_PreparedStatement.setObject(li_c + 1, iO_Parameters[ii_parameterOrder[li_c] - 1]);
		}
		
		i_ResultSet = l_PreparedStatement.executeQuery();
		ResultSetMetaData l_ResultSetMetaData = i_ResultSet.getMetaData();
		
		ii_columnCount = l_ResultSetMetaData.getColumnCount();
		if(ii_columnCount > 0)
		{
		    iS_ColumnNames = new String[ii_columnCount];
		    ii_columnTypes = new int[ii_columnCount];
		    
		    Object[] l_Objects = new Object[ii_columnCount];
		    
		    for(int li_c = 1 ; li_c <= ii_columnCount ; li_c++)
		    {
			if(i_SQLSelectFilter!=null)
			{
			    ii_columnTypes = i_SQLSelectFilter.getColumnTypes(l_ResultSetMetaData);
			}
			else
			{
			    ii_columnTypes[li_c - 1] = l_ResultSetMetaData.getColumnType(li_c);
			    iS_ColumnNames[li_c - 1] = l_ResultSetMetaData.getColumnName(li_c);
			    if(a_Row_Model == null)
			    {
				l_Objects[li_c - 1] = Class.forName(l_ResultSetMetaData.getColumnClassName(li_c)).newInstance();
			    }
			}
		    }
		    
		    if(a_Row_Model == null)
		    {
			a_Row_Model = (Row)new BasicRow(l_Objects);
		    }
		}
		else
		{
		    throw new DataSetException("readStart - ResultSet column count is 0");
		}
	    }
	    else
	    {
		throw new DataSetException("readStart - No SQL statement defined.");
	    }
	    
	}
	catch(SQLException a_SQLException)
	{
	    throw new DataSetException("readStart - SQL Error", a_SQLException);
	}
	catch(ClassNotFoundException a_ClassNotFoundException)
	{
	    throw new DataSetException("readStart - ClassNotFoundException", a_ClassNotFoundException);
	}
	catch(InstantiationException a_InstantiationException)
	{
	    throw new DataSetException("readStart - InstantiationException", a_InstantiationException);
	}
	catch(IllegalAccessException a_IllegalAccessException)
	{
	    throw new DataSetException("readStart - IllegalAccessException", a_IllegalAccessException);
	}
    }
    
    /** Gets row from ResultSet to DataSet.
     *
     * <B>Only for dataset use.</B>
     *
     * @return Row object which contains retrieved data.
     * null if no more data is found.
     */
    public int readRow(Row a_Row) throws DataSetException
    {
	if(i_ResultSet == null)
	{
	    throw new DataSetError("retrieveRow - ResultSet don't exist");
	}
	try
	{
	    if(a_Row != null)
	    {
		if(i_ResultSet.next())
		{
		    if(i_SQLSelectFilter!=null)
		    {
			Object[] l_Objects = i_SQLSelectFilter.getColumnObjects(i_ResultSet);
			for(int li_c = 1; li_c <= i_SQLSelectFilter.getColumnCount() ; li_c++ )
			{
			    a_Row.setValueAt(li_c, l_Objects[li_c - 1]);
			}			
		    }
		    else
		    {
			for(int li_c = 1; li_c <= ii_columnCount ; li_c++)
			{
			    a_Row.setValueAt(li_c, i_ResultSet.getObject(li_c));
			}
		    }
		    ii_rowCount++;
		    return ii_rowCount;
		}
		else
		{
		    return DONE;
		}
	    }
	    else
	    {
		throw new DataSetException("retrieveRow - Parameter row object is null");
	    }
	}
	catch(SQLException a_SQLException)
	{
	    throw new DataSetException("retrieveRow - SQL Error", a_SQLException);
	}
    }
    /** Last action when all the rows are retrieved
     * @return How may rows are retrieved
     */
    public int readEnd() throws DataSetException
    {
	try
	{
	    if(i_ResultSet!=null)
	    {
		i_ResultSet.close();
	    }
	    i_ResultSet = null;
	}
	catch(SQLException a_SQLException)
	{
	    throw new DataSetException("retrieveEnd - SQL Error", a_SQLException);
	}
	finally
	{
	    if(i_Connection!=null)
	    {
		try
		{
		    i_ConnectionContainer.releaseConnection();
		    i_Connection = null;
		}
		catch(SQLException a_SQLException_2)
		{
		    throw new DataSetException("retrieveEnd - Unable, to release connection.", a_SQLException_2);
		}
	    }
	}
	return ii_rowCount;
    }
}