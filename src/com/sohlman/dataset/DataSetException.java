package com.sohlman.dataset;

/** Exception object for DataSet error handling
 * @author Sampsa Sohlman
 * @version 2001-10-18
 */
public class DataSetException extends java.lang.Exception
{
	/** Source exception. If other exception is cause for throwing this it is stored here.
	 */
	private Exception i_Exception_Source;
	/** Possible source object.
	 */
	private Object i_Object_Source;

	/** Creates new <code>DataSetException</code> without detail message.
	 */
	public DataSetException()
	{
	}

	/** Constructs an <code>DataSetException</code> with the specified detail message.
	 * @param aS_Msg Message for user.
	 */
	public DataSetException(String aS_Msg)
	{
		super(aS_Msg);
	}

	/** Constructs an <code>DataSetException</code>
	 * @param a_Exception Source exception
	 */
	public DataSetException(Exception a_Exception)
	{
		i_Exception_Source = a_Exception;
	}

	/** Constructs an <code>DataSetException</code>
	 * @param a_Object Source object
	 */
	public DataSetException(Object a_Object)
	{
		i_Object_Source = a_Object;
	}

	/** Constructs an <code>DataSetException</code>
	 * @param a_Exception Source exception
	 * @param a_Object Source object
	 */
	public DataSetException(Exception a_Exception, Object a_Object)
	{
		i_Exception_Source = a_Exception;
		i_Object_Source = a_Object;
	}

	/** Constructs an <code>DataSetException</code>
	 * @param aS_Msg Message for user.
	 * @param a_Exception Source exception
	 */
	public DataSetException(String aS_Msg, Exception a_Exception)
	{
		super(aS_Msg);
		i_Exception_Source = a_Exception;
	}

	/** Constructs an <code>DataSetException</code>
	 * @param aS_Msg Message for user.
	 * @param a_Object Source object
	 */
	public DataSetException(String aS_Msg, Object a_Object)
	{
		super(aS_Msg);
		i_Object_Source = a_Object;
	}

	/** Constructs an <code>DataSetException</code>
	 * @param aS_Msg Message for user.
	 * @param a_Exception Source Exception
	 * @param a_Object Source object
	 */
	public DataSetException(String aS_Msg, Exception a_Exception, Object a_Object)
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
		super.getMessage();

		if (i_Exception_Source != null)
		{
			return super.getMessage() + "\n Source Exception Message : " + i_Exception_Source.getMessage();
		}
		else
		{
			return super.getMessage();
		}
	}
}
