
package com.sohlman.dataset;

/**
 *
 * @author  Sampsa Sohlman
 * @version 2002-04-07
 */
public abstract class DataSetAdapter implements DataSetListener
{
    /** Creates new DataSetAdapter */
    public DataSetAdapter()
    {
    }
    
    public void rowRemoved(int ai_rowIndex)
    {
    }
    
    public void readEnd(int ai_rowCount)
    {
    }
    
    public void readStart()
    {
    }
    
    public void rowInserted(int ai_rowIndex)
    {
    }
    
    public void writeEnd(int ai_rowCount)
    {
    }
    
    public void writeStart()
    {
    }
    
    public void rowModified(int ai_rowIndex, int a_columnIndex)
    {
    }
    
}
