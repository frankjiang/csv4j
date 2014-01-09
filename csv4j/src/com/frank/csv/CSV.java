/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * CSV.java is built in 2013-4-14.
 */
package com.frank.csv;

import java.io.IOException;
import java.util.HashMap;
import java.util.NoSuchElementException;

/**
 * The CSV data set class, which contains the CSV data in strings.
 * 
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class CSV
{
	/**
	 * The delimiter for row data.
	 */
	protected String					delimiter	= "\r\n";
	/**
	 * The amount of rows.
	 */
	protected int						rows;
	/**
	 * The amount of columns.
	 */
	protected int						columns;
	/**
	 * The titles for each columns.
	 */
	protected String[]					titles;
	/**
	 * The map which bound the title string with its index in the
	 * {@link #titles}.
	 */
	protected HashMap<String, Integer>	map;
	/**
	 * The CSV data.
	 */
	protected String[][]				data;

	/**
	 * Construct an instance of CSV data.
	 * 
	 * @param columns
	 *            the amount of columns
	 * @param rows
	 *            the amount of rows
	 * @param isTitled
	 *            the flag for the data whether it is titled
	 */
	public CSV(int columns, int rows, boolean isTitled)
	{
		this.columns = columns;
		this.rows = rows;
		if (isTitled)
		{
			titles = new String[columns];
			map = new HashMap<String, Integer>(columns);
		}
		else
			titles = null;
		data = new String[rows][columns];
	}

	/**
	 * Construct an instance of titled CSV data.
	 * 
	 * @param columns
	 *            the amount of columns
	 * @param rows
	 *            the amount of rows
	 */
	public CSV(int columns, int rows)
	{
		this(columns, rows, false);
	}

	/**
	 * Returns the amount of rows.
	 * 
	 * @return the rows
	 */
	public int rows()
	{
		return rows;
	}

	/**
	 * Returns the amount of columns.
	 * 
	 * @return the columns
	 */
	public int columns()
	{
		return columns;
	}
	
	/**
	 * Returns the state that whether the 
	 * @return
	 */
	public boolean isTitled()
	{
		return titles!=null;
	}

	/**
	 * Returns the titles for this CSV data.
	 * 
	 * @return the titles
	 */
	public String[] getTitles()
	{
		if (titles == null)
			throw new IllegalStateException(
					"The current CSV data contains no titles.");
		else
			return titles;
	}

	/**
	 * Returns the index of the title in the current titles.
	 * 
	 * @param title
	 *            the specified title
	 * @return the index of the title, or -1 if not found
	 */
	protected int index(String title)
	{
		if (titles == null)
			throw new IllegalStateException(
					"The current CSV data contains no titles.");
		Integer index = map.get(title);
		return index == null ? -1 : index;
	}

	/**
	 * Set the titles for this CSV data.
	 * 
	 * @param titles
	 *            the value of titles
	 */
	public void setTitles(String[] titles)
	{
		if (titles == null)
			throw new IllegalStateException(
					"The current CSV data contains no titles.");
		if (titles.length != columns)
			throw new IllegalArgumentException(
					String.format(
							"The length of input title array(%d) is not compatible with the columns size(%d).",
							titles.length, columns));
		this.titles = titles;
		map.clear();
		for (int i = 0; i < titles.length; i++)
			map.put(titles[i], i);
	}

	/**
	 * Get the specified field in CSV data according to the row index and
	 * the column index.
	 * 
	 * @param row
	 *            the row index of the field
	 * @param column
	 *            the column index of the field
	 * @return the specified field in CSV data
	 */
	public String getData(int row, int column)
	{
		if (row < 0 || row >= rows || column < 0 || column >= columns)
			throw new ArrayIndexOutOfBoundsException(
					String.format(
							"The request field(row, column) = (%d, %d) is out of the bounds of (%d, %d)",
							row, column, rows, columns));
		else
			return data[row][column];
	}

	/**
	 * Get the specified field in CSV data according to the row index and
	 * the column title.
	 * 
	 * @param row
	 *            the row index of the field
	 * @param title
	 *            the column title of the field
	 * @return the specified field in CSV data
	 */
	public String getData(int row, String title)
	{
		int column = index(title);
		if (column == -1)
			throw new NoSuchElementException(
					"There is no such title \"%s\" in current CSV data.");
		else
			return getData(row, column);
	}

	/**
	 * Set the specified field in CSV data according to the row index and
	 * the column index.
	 * 
	 * @param row
	 *            the row index of the field
	 * @param column
	 *            the column index of the field
	 * @param s
	 *            the field value
	 */
	public void setData(int row, int column, String s)
	{
		if (row < 0 || row >= rows || column < 0 || column >= columns)
			throw new ArrayIndexOutOfBoundsException(
					String.format(
							"The request field(row, column) = (%d, %d) is out of the bounds of (%d, %d)",
							row, column, rows, columns));
		else
			data[row][column] = s;
	}

	/**
	 * Set the specified field in CSV data according to the row index and
	 * the column index.
	 * 
	 * @param row
	 *            the row index of the field
	 * @param title
	 *            the column title of the field
	 * @param s
	 *            the field value
	 */
	public void setData(int row, String title, String s)
	{
		int column = index(title);
		if (column == -1)
			throw new NoSuchElementException(
					"There is no such title \"%s\" in current CSV data.");
		else
			setData(row, column, s);
	}

	/**
	 * Parse the content of data to the CSV format.
	 * 
	 * @param s
	 *            the content string
	 * @return the content string in CSV format
	 */
	protected String parse(String s)
	{
		if (s == null)
			return "";
		else
		{
			boolean hasComma = s.indexOf(',') > -1;
			boolean hasQuotation = s.indexOf('"') > -1;
			if (hasQuotation)
				s = s.replaceAll("\"", "\"\"");
			if (hasComma)
				s = String.format("\"%s\"", s);
			return s;
		}
	}

	/**
	 * Write a line to an appendable composite.
	 * 
	 * @param a
	 *            the appendable composite
	 * @param line
	 *            the specified line
	 * @throws IOException
	 *             - If an I/O error occurs
	 */
	protected void writeLine(Appendable a, String[] line) throws IOException
	{
		if (line.length > 0)
		{
			for (int i = 0; i < line.length - 1; i++)
			{
				a.append(parse(line[i]));
				a.append(',');
			}
			a.append(parse(line[line.length - 1]));
		}
		a.append(delimiter);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		try
		{
			if (titles != null)
				writeLine(sb, titles);
			for (String[] line : data)
				writeLine(sb, line);
			sb.delete(sb.length() - delimiter.length(), sb.length());
		}
		catch (IOException e)
		{
		}
		return sb.toString();
	}

	/**
	 * Get the current delimiter.
	 * 
	 * @return the delimiter
	 */
	public String getDelimiter()
	{
		return delimiter;
	}

	/**
	 * Set the current delimiter.
	 * 
	 * @param delimiter
	 *            the value of delimiter
	 */
	public void setDelimiter(String delimiter)
	{
		this.delimiter = delimiter;
	}
	
	/**
	 * Set the current delimiter.
	 * 
	 * @param delimiter
	 *            the value of delimiter
	 */
	public void setDelimiter(char delimiter)
	{
		this.delimiter = Character.toString(delimiter);
	}

	/**
	 * Getter for rows.
	 * @return the rows
	 */
	public int getRows()
	{
		return rows;
	}

	/**
	 * Getter for columns.
	 * @return the columns
	 */
	public int getColumns()
	{
		return columns;
	}

	/**
	 * Getter for data.
	 * @return the data
	 */
	public String[][] getData()
	{
		return data;
	}
}
