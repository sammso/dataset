package com.sohlman.sqltool;

import com.sohlman.dataset.sql.SQLDataSetService;

/**
 * @author Sampsa Sohlman
 * 
 * @version Jan 8, 2004
 */
public class SQLClauseTokenizer
{
	private int ii_index = 0;
	private String iS_SQLUpperCase;
	private String iS_SQL;
	private String iS_Token = null;
	private boolean ib_getNextToken = true;
	private boolean ib_lastHasMoreToken = false;

	public SQLClauseTokenizer(String aS_SQL)
	{
		iS_SQL = aS_SQL;
		iS_SQLUpperCase = aS_SQL.toUpperCase();
	}

	public String nextToken()
	{
		ib_getNextToken = true;
		return iS_Token;
	}

	public boolean hasMoreTokens()
	{
		if (ib_getNextToken) // check if next token has to be searched
		{
			ib_getNextToken = false;
			if (ii_index >= iS_SQL.length())
			{
				ib_lastHasMoreToken = false;
				return ib_lastHasMoreToken;
			}
			else
			{

				int li_index = keyWordSearchIndexOf(iS_SQLUpperCase, "GO", ii_index);

				if (li_index == -1)
				{
					ib_lastHasMoreToken = false;
					return ib_lastHasMoreToken;
					//li_index = iS_SQL.length();
					//iS_Token = iS_SQL.substring(ii_index, li_index).trim();
				}
				else
				{
					iS_Token = iS_SQL.substring(ii_index, li_index).trim();
					li_index += 2;
				}

				ii_index = li_index;

				ib_lastHasMoreToken = true;
				return ib_lastHasMoreToken;
			}
		}
		else
		{
			return ib_lastHasMoreToken;
		}
	}

	public static void main(String[] aS_Arguments)
	{
		String lS_SQL = "SELECT alsdjflasdöjf öa GO alsjdfölaskdjf GOgo GO \" GO \" alsdkjf GO hello world";

		SQLClauseTokenizer l_SQLClauseTokenizer = new SQLClauseTokenizer(lS_SQL);

		while (l_SQLClauseTokenizer.hasMoreTokens())
		{
			System.out.println(l_SQLClauseTokenizer.nextToken());
		}
	}

	public int keyWordSearchIndexOf(String aS_From, String aS_What, int ai_start)
	{
		int li_length = aS_From.length() - aS_What.length();
		char[] lc_from = aS_From.toCharArray();
		char[] lc_what = aS_What.toCharArray();

		char lc_lastChar = 'S';
		boolean lb_doubleQuote = false;
		boolean lb_singleQuote = false;

		for (int li_index = ai_start; li_index < li_length; li_index++)
		{
			char lc_char = lc_from[li_index];

			if (lc_char == '"') // We are now in column name or 
			{
				if (lb_doubleQuote)
				{
					if (li_index == ai_start || lc_lastChar != '"')
					{
						lb_doubleQuote = false;
					}
				}
				else
				{
					lb_doubleQuote = true;
				}
			}
			else if (lc_char == '\'')
			{
				if (lb_singleQuote)
				{
					if (li_index == ai_start || lc_lastChar != '\'')
					{
						lb_singleQuote = false;
					}
				}
				else
				{
					lb_singleQuote = true;
				}
			}
			else
			{
				if ((!lb_doubleQuote) && (!lb_singleQuote) && lc_what[0] == lc_char)
				{
					int li_i = lc_what.length - 1;
					for (; li_i > 0; li_i--)
					{
						char lc_tmpWhat = lc_what[li_i];
						char lc_tmpFrom = lc_from[li_i + li_index];

						if (lc_what[li_i] != lc_from[li_i + li_index])
						{
							break;
						}
					}

					if (li_i == 0)
					{
						if (SQLDataSetService.isSpaceTabReturnNothing(iS_SQLUpperCase, li_index - 1)
							&& SQLDataSetService.isSpaceTabReturnNothing(iS_SQLUpperCase, li_index + aS_What.length()))
						{
							return li_index;
						}

					}
				}
			}
			lc_lastChar = lc_char;
		}
		return -1;
	}

	public boolean isSpaceTabReturnNothing(String a_String, int ai_index)
	{
		if (ai_index < 0)
			return true;
		if (ai_index >= a_String.length())
			return true;

		char l_char = a_String.charAt(ai_index);
		switch (l_char)
		{
			case ' ' :
			case '\t' :
			case '\n' :
				return true;
		}
		return false;
	}
}
