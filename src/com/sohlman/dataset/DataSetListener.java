package com.sohlman.dataset;

/**
 *
 * @author  Sampsa Sohlman
 * @version 2001-10-22
 */
public interface DataSetListener
{
    public void rowInserted(int ai_rowIndex);
    public void rowModified(int ai_rowIndex, int a_columnIndex);
    public void rowRemoved(int ai_rowIndex);
    public void readStart();
    public void readEnd(int ai_rowCount);
    public void writeStart();
    public void writeEnd(int ai_rowCount);
}
