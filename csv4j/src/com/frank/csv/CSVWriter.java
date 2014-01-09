/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * CSVWriter.java is built in 2013-4-17.
 */
package com.frank.csv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

/**
 * The writer for a CSV file.
 * 
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class CSVWriter
{
	/**
	 * The delimiter for row data.
	 */
	protected String	delimiter	= "\r\n";
	/**
	 * The character set of the CSV file.
	 */
	protected Charset	charset;

	/**
	 * Get the character set of the CSV file.
	 * 
	 * @return the character set
	 */
	public Charset getCharset()
	{
		return charset;
	}

	/**
	 * Set the character set of the CSV file.
	 * 
	 * @param charset
	 *            the value of character set
	 */
	public void setCharset(Charset charset)
	{
		this.charset = charset;
	}

	/**
	 * Construct an instance of CSV writer with specified character set.
	 * 
	 * @param charset
	 */
	public CSVWriter(Charset charset)
	{
		this.charset = charset;
	}

	/**
	 * Construct an instance of CSV writer with default character set.
	 */
	public CSVWriter()
	{
		charset = Charset.defaultCharset();
	}

	/**
	 * Write the specified CSV data to the specified output stream.
	 * 
	 * @param csv
	 *            the specified CSV data
	 * @param out
	 *            the specified output stream
	 * @throws IOException
	 *             if I/O error occurs
	 */
	public void write(CSV csv, OutputStream out) throws IOException
	{
		OutputStreamWriter writer = new OutputStreamWriter(out, charset);
		if (delimiter.equals(csv.delimiter))
			writer.append(csv.toString());
		else
		{
			String temp = csv.delimiter;
			csv.setDelimiter(delimiter);
			writer.append(csv.toString());
			csv.setDelimiter(temp);
		}
		writer.close();
	}

	/**
	 * Write the specified CSV data to the specified file.
	 * 
	 * @param csv
	 *            the specified CSV data
	 * @param file
	 *            the specified file
	 * @throws IOException
	 *             if I/O error occurs
	 */
	public void write(CSV csv, File file) throws IOException
	{
		write(csv, new FileOutputStream(file));
	}

	/**
	 * Write the specified CSV data to the specified file.
	 * 
	 * @param csv
	 *            the specified CSV data
	 * @param filename
	 *            name of the specified file
	 * @throws IOException
	 *             if I/O error occurs
	 */
	public void write(CSV csv, String filename) throws IOException
	{
		write(csv, new FileOutputStream(new File(filename)));
	}
}
