package com.sohlman.dataset.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;

/**
 * With this interface is possible make special retrieve handling for dataset or other objects that are reading database. <br><br>
 *
 * Use <code>l_SQLReadEngine.setFilter(..)</code> in your code if you want some special behavior.<br><br>
 *
 * @author  Sampsa Sohlman
 * @version 2002-03-20
 */
public interface SQLSelectFilter
{
    /** 
     * Implement this method to create row you want.
     * @param a_ResultSet ResultSet object that contains row data
     * @param int resultset column count
     * @return Row object.
     */
    public Object[] getColumnObjects(ResultSet a_ResultSet) throws SQLException;
    
    public SQLRowInfo getRowInfo(ResultSetMetaData a_ResultSetMetaData) throws SQLException;
}

