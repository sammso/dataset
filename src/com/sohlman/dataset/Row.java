package com.sohlman.dataset;

/** <p>Row object represent row in {@link DataSet DataSet} object.</p>
 * <p>With row object is possible to use data to store different types of objects.</p>
 * @author Sampsa Sohlman
 * @version 1.0
 */
public interface Row
{    
    public String getClassName(int ai_index);
    
    public int setValueAt(int ai_index, Object a_Object);
    
    public Object getValueAt(int ai_index);
    
    public void copyFromOtherRow(Row a_Row);
  
    public int getColumnCount();
    
    public void setAllNulls();
    
    public Object clone();
   
    public boolean equals(Row a_Row);
    
    public boolean equals(Row a_Row, int[] ai_columns);
}