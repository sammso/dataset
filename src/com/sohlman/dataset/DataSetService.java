package com.sohlman.dataset;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * DataSetService contains commonly used methods for DataSet library.
 * All methods are static
 * 
 * @author Sampsa Sohlman
 */
public class DataSetService
{
	/**
	 * Create String buffer which is filled with space " "
	 * @param ai_size Size of new StringBuffer
	 * @return StringBuffer Space filled Stringbuffer
	 */
	public static StringBuffer createSpaceFilledStringBuffer(int ai_size)
	{
		StringBuffer l_StringBuffer = new StringBuffer(ai_size);

		for (int li_c = 0; li_c < ai_size; li_c++)
		{
			l_StringBuffer.append(" ");
		}
		return l_StringBuffer;
	}
		
	/**
	 * Puts String to StringBuffer to wanted position by replacing data that are there.
	 * @param a_StringBuffer StringBuffer object to modified
	 * @param a_String Modifiying String. 
	 * @param ai_pos Position where to start modification. 
	 * @return boolean false if position is larger that size of StringBuffer othervice true or String is null
	 */
	public static boolean setStringToStringBuffer(StringBuffer a_StringBuffer, String a_String, int ai_pos)
	{
		int li_size = a_StringBuffer.length();
		if (a_String == null)
			return false;
		if (li_size <= ai_pos)
			return false;
		int li_end = ai_pos + a_String.length();
		if (li_end > li_size)
		{
			li_end = li_size;
		}

		for (int li_x = ai_pos; li_x < li_end; li_x++)
		{
			a_StringBuffer.setCharAt(li_x, a_String.charAt(li_x - ai_pos));
		}
		return true;
	}	
	/**
	* Puts String to StringBuffer to wanted position by replacing data that are there.
	* @param a_StringBuffer StringBuffer object to modified
	* @param a_String Modifiying String. 
	* @param ai_pos Position where to start modification. 
	* @param ai_end Position where to end modification
	* @return boolean false if position is larger that size of StringBuffer othervice true or String is null
	*/
	private boolean setStringToStringBuffer(StringBuffer a_StringBuffer, String a_String, int ai_pos, int ai_end)
	{
		int li_size = a_StringBuffer.length();
		if (a_String == null)
			return false;
		if (li_size <= ai_pos)
			return false;

		if (ai_end > li_size)
		{
			ai_end = li_size;
		}

		for (int li_x = ai_pos; li_x < ai_end; li_x++)
		{
			a_StringBuffer.setCharAt(li_x, a_String.charAt(li_x - ai_pos));
		}
		return true;
	}	
	
	/**
	 * Method StringToSpecifiedObject.
	 * 
	 * Converts String to object to specifiend class, by using default 
	 * 
	 * @param aS_Object
	 * @param aS_ObjectClassName
	 * @return Object
	 */
	public static Object StringToSpecifiedObject(String aS_Object, String aS_ObjectClassName)
	{
		if (aS_Object == null)
		{
			return null;
		}
		if (aS_ObjectClassName.equals("java.lang.Boolean"))
		{
			return new Boolean(Boolean.getBoolean((String) aS_Object));
		}
		if (aS_ObjectClassName.equals("java.lang.Byte"))
		{
			return new Byte(Byte.parseByte((String) aS_Object));
		}
		if (aS_ObjectClassName.equals("java.math.BigInteger"))
		{
			return new BigInteger((String) aS_Object);
		}
		if (aS_ObjectClassName.equals("java.math.BigDecimal"))
		{
			return new BigDecimal((String) aS_Object);
		}
		
		if( aS_ObjectClassName.equals("java.sql.Date") || aS_ObjectClassName.equals("java.util.Date"))
		{
			return java.sql.Date.valueOf((String) aS_Object);
		}

		if (aS_ObjectClassName.equals("java.sql.Time") )
		{
			return Time.valueOf((String) aS_Object);
		}

		if (aS_ObjectClassName.equals("java.sql.Timestamp"))
		{
			return Timestamp.valueOf((String) aS_Object);
		}

		if (aS_ObjectClassName.equals("java.lang.Double"))
		{
			return new Double(Double.parseDouble((String) aS_Object));
		}
		if (aS_ObjectClassName.equals("java.lang.Float"))
		{
			return new Float(Float.parseFloat((String) aS_Object));
		}

		if (aS_ObjectClassName.equals("java.lang.Integer"))
		{
			return new Integer(Integer.parseInt((String) aS_Object));
		}

		if (aS_ObjectClassName.equals("java.lang.String"))
		{
			return aS_Object.toString();
		}

		throw new IllegalArgumentException(aS_Object.getClass().getName() + " is not supported class type");
	}
}
