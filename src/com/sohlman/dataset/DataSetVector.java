/*
 * DataSetVector.java
 *
 * Created on 3. maaliskuuta 2002, 14:00
 */

package com.sohlman.dataset;

/**
 * This create do Vector is possible to sort
 * 
 * This object is only for internal use
 *
 * @author  Sampsa Sohlman
 * @version 2002-10-10
 */
final class DataSetVector extends java.util.Vector
{
    Object[] getObjects()
    {
	return elementData;
    }
}
