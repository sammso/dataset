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

