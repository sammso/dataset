package com.sohlman.dataset;

/**
 * Calucates min for Group
 * 
 * @author Sampsa Sohlman
 * @version 2003-02-27
 */
public class GroupMin extends GroupCalc
{

	/**
	 * Constructor for GroupMin.
	 * @param ai_columnId
	 */
	public GroupMin(int ai_columnId)
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
		
		Comparable l_Comparable_Min = (Comparable)a_Objects[0];
		
		for(int li_index = 1 ; li_index < a_Objects.length ; li_index++)
		{
			Comparable l_Comparable_Current = (Comparable)a_Objects[li_index];
			
			if(l_Comparable_Current.compareTo(l_Comparable_Min) < 0)
			{
				l_Comparable_Min = l_Comparable_Current;
			}
		}
		
		return l_Comparable_Min;
	}

}
