package com.sohlman.dataset;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * GroupCalc object is working with {@link com.sohlman.dataset.DataSet#groupByRows DataSet.groupByRows}<br>
 * 
 * (Note this is still experimental)
 * 
 * @author Sampsa Sohlman
 */
public abstract class GroupCalc
{
	private int ii_columnId;	
	public GroupCalc(int ai_columnId)
	{
		if(ai_columnId<1)
		{
			throw new IllegalArgumentException("Column id has be larger than 0");
		}
		
		ii_columnId = ai_columnId;
	}
	
	public int getColumnIndex()
	{
		return ii_columnId;
	}
	
	public abstract Object calculateGroupBy(Object[] a_Objects);
	
	/**
	 * Method This is part of functionality
	 * @param a_Number_1
	 * @param a_Number_2
	 * @return Number
	 */
	public static Number divide(Number a_Number_1, Number a_Number_2)
	{
		if(a_Number_1==null && a_Number_2 != null)
		{
			return a_Number_2;	
		}
		if(a_Number_2==null && a_Number_1 != null)
		{
			return a_Number_1;	
		}		
		
		if (a_Number_1 instanceof Integer )
		{
			return new Integer(((Integer)a_Number_1).intValue() / ((Integer)a_Number_2).intValue());
		}
		else if (a_Number_1 instanceof Long )
		{
			return new Long(((Long)a_Number_1).longValue() / ((Long)a_Number_2).longValue());
		}
		else if (a_Number_1 instanceof Double )
		{
			return new Double(((Double)a_Number_1).doubleValue() / ((Double)a_Number_2).doubleValue());
		}
		else if (a_Number_1 instanceof Float )
		{
			return new Float(((Float)a_Number_1).floatValue() / ((Float)a_Number_2).floatValue());
		}		
		else if (a_Number_1 instanceof Short )
		{
			short ls_value1 = ((Short)a_Number_1).shortValue();
			short ls_value2 = ((Short)a_Number_1).shortValue();			
			short ls_value = (short)(ls_value1 + ls_value2);
			return new Short( ls_value );
		}
		else if (a_Number_1 instanceof Byte )
		{
			byte lby_value1 = ((Byte)a_Number_1).byteValue();
			byte lby_value2 = ((Byte)a_Number_1).byteValue();			
			byte lby_value = (byte)(lby_value1 + lby_value2);
			return new Byte( lby_value );
		}		
		else if (a_Number_1 instanceof BigInteger )
		{
			BigInteger l_BigInteger_1 = (BigInteger) a_Number_1;
			BigInteger l_BigInteger_2 = (BigInteger) a_Number_2;	
		
			return l_BigInteger_1.divide(l_BigInteger_2);
		}
		else if (a_Number_1 instanceof BigDecimal )
		{
			BigDecimal l_BigDecimal_1 = (BigDecimal) a_Number_1;
			BigDecimal l_BigDecimal_2 = (BigDecimal) a_Number_2;	
		
			return l_BigDecimal_1.divide(l_BigDecimal_2, BigDecimal.ROUND_CEILING);
		}		
		return null;				
	}
	
	/**
	 * Method convertToNumber converts in value to Correct Number defined by class
	 * @param a_Class
	 * @param ai_value
	 * @return Number
	 */
	public static Number convertToNumber(Class a_Class, int ai_value)
	{	
		if (a_Class.equals(Integer.class) )
		{
			return new Integer(ai_value);
		}
		else if (a_Class.equals(Long.class) )
		{
			return new Long(ai_value);
		}
		else if (a_Class.equals(Double.class))
		{
			return new Double(ai_value);
		}
		else if (a_Class.equals(Float.class) )
		{
			return new Float(ai_value);
		}		
		else if (a_Class.equals(Short.class) )
		{
			short ls_value = (new Integer(ai_value)).shortValue();
			return new Short( ls_value );
		}
		else if (a_Class.equals(Byte.class) )
		{		
			byte lby_value = (new Integer(ai_value)).byteValue();
			return new Byte( lby_value );
		}		
		else if (a_Class.equals(BigInteger.class) )
		{		
			return new BigInteger(String.valueOf(ai_value));
		}
		else if (a_Class.equals(BigDecimal.class) )
		{		
			return new BigDecimal(ai_value);
		}		
		return null;				
	}
	
	/**
	 * Method sum.
	 * @param a_Number_1
	 * @param a_Number_2
	 * @return Number
	 */
	public static Number sum(Number a_Number_1, Number a_Number_2)
	{
		if(a_Number_1==null && a_Number_2 != null)
		{
			return a_Number_2;	
		}
		if(a_Number_2==null && a_Number_1 != null)
		{
			return a_Number_1;	
		}		
		
		if (a_Number_1 instanceof Integer )
		{
			return new Integer(((Integer)a_Number_1).intValue() + ((Integer)a_Number_2).intValue());
		}
		else if (a_Number_1 instanceof Long )
		{
			return new Long(((Long)a_Number_1).longValue() + ((Long)a_Number_2).longValue());
		}
		else if (a_Number_1 instanceof Double )
		{
			return new Double(((Double)a_Number_1).doubleValue() + ((Double)a_Number_2).doubleValue());
		}
		else if (a_Number_1 instanceof Float )
		{
			return new Float(((Float)a_Number_1).floatValue() + ((Float)a_Number_2).floatValue());
		}		
		else if (a_Number_1 instanceof Short )
		{
			short ls_value1 = ((Short)a_Number_1).shortValue();
			short ls_value2 = ((Short)a_Number_1).shortValue();			
			short ls_value = (short)(ls_value1 + ls_value2);
			return new Short( ls_value );
		}
		else if (a_Number_1 instanceof Short )
		{
			byte ls_value1 = ((Byte)a_Number_1).byteValue();
			byte ls_value2 = ((Byte)a_Number_1).byteValue();			
			byte ls_value = (byte)(ls_value1 + ls_value2);
			return new Byte( ls_value );
		}		
		else if (a_Number_1 instanceof BigInteger )
		{
			BigInteger l_BigInteger_1 = (BigInteger) a_Number_1;
			BigInteger l_BigInteger_2 = (BigInteger) a_Number_2;	
		
			return l_BigInteger_1.add(l_BigInteger_2);
		}
		else if (a_Number_1 instanceof BigDecimal )
		{
			BigDecimal l_BigDecimal_1 = (BigDecimal) a_Number_1;
			BigDecimal l_BigDecimal_2 = (BigDecimal) a_Number_2;	
		
			return l_BigDecimal_1.add(l_BigDecimal_2);
		}		
		return null;				
	}	
}
