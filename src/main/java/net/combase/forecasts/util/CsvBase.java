/**
 *
 */
package net.combase.forecasts.util;

import java.io.Closeable;


public abstract class CsvBase implements Closeable
{
	/**
	 * The default char used as field separator.
	 */
	public static final char DEFAULT_FIELD_SEPARATOR = ';';

	/**
	 * The default char used as text quote.
	 */
	public static final char DEFAULT_TEXT_QUOTE = '"';

	/**
	 * The current char used as field separator.
	 */
	private char m_fieldSeparator;

	/**
	 * The current char used as text quote.
	 */
	private char m_textQuote;


	/**
	 * CsvBase constructor with the default field separator and text quote.
	 */
	public CsvBase()
	{
		this(CsvBase.DEFAULT_FIELD_SEPARATOR, CsvBase.DEFAULT_TEXT_QUOTE);
	}


	/**
	 * CsvBase constructor with a given field separator and the default text quote.
	 * 
	 * @param fieldSeparator
	 *            The field separator to be used; overwrites the default one
	 */
	public CsvBase(final char fieldSeparator)
	{
		this(fieldSeparator, CsvBase.DEFAULT_TEXT_QUOTE);
	}


	/**
	 * CsvBase constructor with given field separator and text quote.
	 * 
	 * @param fieldSeparator
	 *            The field separator to be used; overwrites the default one
	 * @param textQuote
	 *            The text quote to be used; overwrites the default one
	 */
	public CsvBase(final char fieldSeparator, final char textQuote)
	{
		setFieldSeparator(fieldSeparator);
		setTextQuote(textQuote);
	}


	/**
	 * Get the current field separator.
	 * 
	 * @return The char containing the current field separator
	 */
	public final char getFieldSeparator()
	{
		return m_fieldSeparator;
	}


	/**
	 * Get the current text quote.
	 * 
	 * @return The char containing the current text quote
	 */
	public final char getTextQuote()
	{
		return m_textQuote;
	}


	/**
	 * Set the current field separator.
	 * 
	 * @param value
	 *            The new field separator to be used; overwrites the old one
	 */
	public final void setFieldSeparator(final char value)
	{
		m_fieldSeparator = value;
	}


	/**
	 * Set the current text quote.
	 * 
	 * @param value
	 *            The new text quote to be used; overwrites the old one
	 */
	public final void setTextQuote(final char value)
	{
		m_textQuote = value;
	}
}
