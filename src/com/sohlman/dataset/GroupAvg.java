package com.sohlman.dataset;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author Sampsa Sohlman
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class GroupAvg extends GroupSum
{

	/**
	 * Constructor for GroupAvg.
	 * @param ai_columnId
	 */
	public GroupAvg(int ai_columnId)
	{
		super(ai_columnId);
	}

	/**
	 * @see com.sohlman.dataset.GroupCalc#calculateGroupBy(Object[])
	 */
	public Object calculateGroupBy(Object[] a_Objects)
	{
		if(a_Objects==null)
		{
			return null;	
		}
		if(a_Objects.length==1)
		{
			return a_Objects[0];
		}		
		return (Object)divide((Number)super.calculateGroupBy(a_Objects), convertToNumber(a_Objects[0].getClass(), a_Objects.length)) ;		
	}		
}
