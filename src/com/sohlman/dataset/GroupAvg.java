package com.sohlman.dataset;


/**
 * @author Sampsa Sohlman
 * @version 2003-02-26
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
