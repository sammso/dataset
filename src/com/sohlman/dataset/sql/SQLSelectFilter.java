/*
DataSet Library
---------------
Copyright (C) 2001-2004 - Sampsa Sohlman, Teemu Sohlman

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA 
*/
package com.sohlman.dataset.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

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

