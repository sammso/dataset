package com.sohlman.dataset;

/**
 * Calculates max for group
 * 
 * @author Sampsa Sohlman
 * @version 2003-02-27
 */
public class GroupMax extends GroupCalc
{

	/**
	 * Constructor for GroupMax.
	 */
	public GroupMax(int ai_columnId)
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
		
		Comparable l_Comparable_Max = (Comparable)a_Objects[0];
		
		for(int li_index = 1 ; li_index < a_Objects.length ; li_index++)
		{
			Comparable l_Comparable_Current = (Comparable)a_Objects[li_index];
			
			if(l_Comparable_Current.compareTo(l_Comparable_Max) > 0)
			{
				l_Comparable_Max = l_Comparable_Current;
			}
		}
		
		return l_Comparable_Max;
	}
}
