package com.sohlman.dataset;

/**
 * @author Sampsa Sohlman
 * @version 2003-02-26
 */
public class GroupCount extends GroupCalc
{

	/**
	 * Constructor for GroupCount.
	 * @param ai_columnId
	 */
	public GroupCount(int ai_columnId)
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
		return (Number) convertToNumber(a_Objects[0].getClass(),a_Objects.length);
	}

}
