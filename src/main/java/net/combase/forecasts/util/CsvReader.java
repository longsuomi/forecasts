/**
 *
 */
package net.combase.forecasts.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;


public class CsvReader extends CsvBase
{
	private static int handlePlainField(final String line, final StringBuilder sb, final int pos,
		final char fieldSeperator)
	{
		final int sepPos = line.indexOf(fieldSeperator, pos);
		if (sepPos == -1)
		{
			sb.append(line.substring(pos));
			return line.length();
		}
		sb.append(line.substring(pos, sepPos));
		return sepPos;
	}


	private static int handleQuotedField(final String line, final StringBuilder sb, final int pos,
		final char fieldSeperator, final char quoteChr)
	{
		final int size = line.length();
		int j = pos;
		boolean quots = false;
		boolean stopAtSep = false;
		while (j < size)
		{
			final char c = line.charAt(j);
			if ((c == quoteChr) && (j + 1 < size))
			{
				stopAtSep = false;
				final char nc = line.charAt(j + 1);
				if (nc == quoteChr)
				{
					j++; // skip escape char
				}
				else if (nc == fieldSeperator)
				{
					quots = true;
					// next delimiter
					j++; // skip end quotes
					break;
				}
				else
					stopAtSep = true;
			}
			else if ((c == quoteChr) && (j + 1 == size))
			{
				// end quotes at end of line
				break;
			}
			else if ((c == fieldSeperator) && (stopAtSep))
			{
				quots = true;
				break;
			}
			sb.append(c);
			j++;
		}

		return (!quots) ? -1 : j;
	}


	/**
	 * The buffered reader linked to the CSV file to be read.
	 */
	private final BufferedReader m_in;


	/**
	 * CsvReader constructor just need the name of the existing CSV file that will be read.
	 * 
	 * @param in
	 *            The reader of the CSV file to be opened for reading
	 */
	public CsvReader(final Reader in)
	{
		super();
		m_in = new BufferedReader(in);
	}


	/**
	 * CsvReader constructor with a given field separator.
	 * 
	 * @param in
	 *            The reader of the CSV file to be opened for reading
	 * @param fieldSeparator
	 *            The field separator to be used; overwrites the default one
	 */
	public CsvReader(final Reader in, final char fieldSeparator)
	{
		super(fieldSeparator);
		m_in = new BufferedReader(in);
	}


	/**
	 * CsvReader constructor with given field separator and text quote.
	 * 
	 * @param in
	 *            The reader of the CSV file to be opened for reading
	 * @param fieldSeparator
	 *            The field separator to be used; overwrites the default one
	 * @param textQuote
	 *            The text quote to be used; overwrites the default one
	 */
	public CsvReader(final Reader in, final char fieldSeparator, final char textQuote)
	{
		super(fieldSeparator, textQuote);
		m_in = new BufferedReader(in);
	}


	/**
	 * Close the input CSV file.
	 * 
	 * @throws IOException
	 *             If an error occurs while closing the file
	 */
	@Override
	public void close() throws IOException
	{
		m_in.close();
	}


	/**
	 * Split the next line of the input CSV file into fields.
	 * <p>
	 * This is currently the most important function of the package.
	 * 
	 * @return Vector of strings containing each field from the next line of the file
	 * @throws IOException
	 *             If an error occurs while reading the new line from the file
	 */
	public List<String> readFields() throws IOException
	{
		boolean umbruch = false;
		boolean neuesFeld = true;
		List<String> fields = null;
		final StringBuilder sb = new StringBuilder();
		final char quoteChr = getTextQuote();
		final char fieldSeperator = getFieldSeparator();
		final String lineBreak = System.getProperty("line.separator");
		do
		{
			umbruch = false;
			final String line = m_in.readLine();

			if (line == null)
				return null;

			if (fields == null)
				fields = new ArrayList<String>();

			final int size = line.length();
			if (size == 0)
			{
				fields.add(line);
				return fields;
			}

			int pos = 0;
			do
			{
				if (neuesFeld)
					sb.setLength(0);
				else
					sb.append(lineBreak);

				if ((pos < size && line.charAt(pos) == quoteChr) || !neuesFeld)
				{
					if (neuesFeld)
						pos++;// skip quote
					pos = CsvReader.handleQuotedField(line, sb, pos, fieldSeperator, quoteChr);

					if (pos == -1)
					{
						umbruch = true;
						neuesFeld = false;
						break;
					}
				}
				else
				{
					pos = CsvReader.handlePlainField(line, sb, pos, fieldSeperator);
				}
				neuesFeld = true;

				fields.add(sb.toString());

				pos++;
			}
			while (pos < size);
		}
		while (umbruch);

		return fields;
	}
}
