package com.sohlman.dataset;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author Sampsa Sohlman
 * @version 2003-02-25
 */
public class GroupSum extends GroupCalc
{

	/**
	 * Constructor for GroupSum.
	 * @param ai_columnId
	 */
	public GroupSum(int ai_columnId)
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
		Number l_Number = (Number)a_Objects[0];
		
		for(int li_index = 1 ;li_index < a_Objects.length ; li_index++)
		{
			l_Number = sum(l_Number,(Number)a_Objects[li_index]);
		}
		return (Object)l_Number;
	}
}
