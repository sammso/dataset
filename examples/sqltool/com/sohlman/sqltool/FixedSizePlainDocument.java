package com.sohlman.sqltool;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * @author Sampsa Sohlman
 * 
 * @version 2004-02-07
 */
public class FixedSizePlainDocument extends PlainDocument
{
	private int ii_size;

	public FixedSizePlainDocument(int ai_size)
	{
		ii_size = ai_size;
	}

	public void insertString(int ai_offset, String a_String, AttributeSet a_AttributeSet) throws BadLocationException
	{
		if ((getLength() + a_String.length()) <= ii_size)
		{
			super.insertString(ai_offset, a_String, a_AttributeSet);
		}
/*		else
		{
			throw new BadLocationException("Insertion exceeds max size of document", ai_offset);
		}*/
	}
}
