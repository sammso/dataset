package com.sohlman.dataset;

/**
 * @author Sampsa Sohlman
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
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
