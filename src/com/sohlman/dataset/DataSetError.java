/*
 * DataSetError.java
 *
 * Created on 29. lokakuuta 2001, 12:45
 */

package com.sohlman.dataset;

/** DataSetError sister of DataSetException.<br>
 * DataSetError is to be used only when programmer
 * definitative programming error.<br>
 * This error is not ment to be cach on run time.<br>
 * <b>Example:</b><br>
 * <ul>
 * <li>To use object. Not all variables are not defined.</li>
 * </ul>
 * @author Sampsa Sohlman
 * @version 2001-29-10
 */
public class DataSetError extends java.lang.Error
{
    /** Source exception. If other exception is cause for throwing this it is stored here.
     */    
    private Exception i_Exception_Source;
    /** Possible source object.
     */    
    private Object i_Object_Source;
    
    
    /** Creates new <code>DataSetError</code> without detail message.
     */
    public DataSetError()
    {
    }
    
    
    /** Constructs an <code>DataSetError</code> with the specified detail message.
     * @param aS_Msg Message for user.
     */
    public DataSetError(String aS_Msg)
    {
	super(aS_Msg);
    }
    
    /** Constructs an <code>DataSetError</code>
     * @param a_Exception Source exception
     */    
    public DataSetError(Exception a_Exception)
    {
	i_Exception_Source = a_Exception;
    }
    
    /** Constructs an <code>DataSetError</code>
     * @param a_Object Source object
     */    
    public DataSetError(Object a_Object)
    {
	i_Object_Source = a_Object;
    }
    
    /** Constructs an <code>DataSetError</code>
     * @param a_Exception Source exception
     * @param a_Object Source object
     */    
    public DataSetError(Exception a_Exception, Object a_Object)
    {
	i_Exception_Source = a_Exception;
	i_Object_Source = a_Object;
    }

    /** Constructs an <code>DataSetError</code>
     * @param aS_Msg Message for user.
     * @param a_Exception Source exception
     */    
    public DataSetError(String aS_Msg, Exception a_Exception)
    {
	super(aS_Msg);
	i_Exception_Source = a_Exception;
    }
    
    /** Constructs an <code>DataSetError</code>
     * @param aS_Msg Message for user.
     * @param a_Object Source object
     */    
    public DataSetError(String aS_Msg, Object a_Object)
    {
	super(aS_Msg);
	i_Object_Source = a_Object;
    }    

    /** Constructs an <code>DataSetError</code>
     * @param aS_Msg Message for user.
     * @param a_Exception Source Exception
     * @param a_Object Source object
     */    
    public DataSetError(String aS_Msg, Exception a_Exception, Object a_Object)
    {
	super(aS_Msg);
	i_Exception_Source = a_Exception;
	i_Object_Source = a_Object;
    }
    
    /** Get's handle for SourceException
     * @return Source Exception
     */    
    public Exception getSourceException()
    {
	return i_Exception_Source;
    }
    
    /** Get's handle for SourceObject
     * @return SourceObject
     */    
    public Object getSourceObject()
    {
	return i_Object_Source;
    }
    
    public String getMessage()
    {
	String lS_Message;
	
	super.getMessage();
	
	if(i_Exception_Source!=null)
	{
	    return super.getMessage() + "\n Source Exception Message : " + i_Exception_Source.getMessage();
	}
	else
	{
	    return super.getMessage();
	}
    }
}
