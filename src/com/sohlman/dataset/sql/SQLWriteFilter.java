/*
DataSet Library
---------------
Copyright (C) 2001-2005 - Sampsa Sohlman, Teemu Sohlman

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

import com.sohlman.dataset.Row;

/**
 * This interface gives opportunity to do something just before when DataSet writes 
 * Data to database
 *
 * @author  Sampsa Sohlman
 * @version 2002-03-19
 */
public interface SQLWriteFilter
{
    public final static int NORMAL = 1 ;
    public final static int SKIP   = 2 ;
    public final static int INSERT = 3 ;
    public final static int UPDATE = 4 ;
    public final static int DELETE = 5 ;
    
    public int insert(Row a_Row);
    public int update(Row a_Orig_Row, Row a_New_Row);
    public int delete(Row a_Orig_Row, Row a_New_Row);
}

