/*
 * SQLWriteFilter.java
 *
 * Created on 19. maaliskuuta 2002, 10:09
 */

package com.sohlman.dataset.sql;

import com.sohlman.dataset.Row;

/**
 * This interface gives opportunity to do something when DataSet writes 
 * Data to database
 * DOCUMENT THIS LATER SEE EVENT CALEDAR SOLUTION
 *
 * @author  Sampsa Sohlman
 * @version
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

