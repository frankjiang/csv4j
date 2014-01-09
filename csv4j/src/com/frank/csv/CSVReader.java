/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * CSVReader.java is built in 2013-4-15.
 */
package com.frank.csv;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * The reader for a CSV file.
 * 
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class CSVReader
{
	/**
	 * The buffer size for CSV data reading.
	 */
	protected int		bufferSize	= 10000;
	/**
	 * The delimiter for row data.
	 */
	protected String	delimiter	= "\r\n";
	/**
	 * The character set of the CSV file.
	 */
	protected Charset	charset;
	/**
	 * The flag for the data whether it is titled.
	 */
	protected boolean	isTitled;

	/**
	 * Construct an instance of untitled CSVReader with default character set.
	 */
	public CSVReader()
	{
		this(Charset.defaultCharset(), false);
	}

	/**
	 * Construct an instance of untitled CSVReader with specified character set.
	 * 
	 * @param charset
	 *            the character set of the CSV file
	 * @param isTitled
	 *            the flag for the data whether it is titled
	 */
	public CSVReader(Charset charset)
	{
		this(charset, false);
	}

	/**
	 * Construct an instance of CSVReader with specified title condition.
	 * 
	 * @param charset
	 *            the character set of the CSV file
	 * @param isTitled
	 *            the flag for the data whether it is titled
	 */
	public CSVReader(boolean isTitled)
	{
		this(Charset.defaultCharset(), isTitled);
	}

	/**
	 * Construct an instance of CSVReader with specified character set and title
	 * condition.
	 * 
	 * @param charset
	 *            the character set of the CSV file
	 * @param isTitled
	 *            the flag for the data whether it is titled
	 */
	public CSVReader(Charset charset, boolean isTitled)
	{
		this.charset = charset;
		this.isTitled = isTitled;
	}

	/**
	 * The basic function: read CSV data from specified input stream with
	 * specified character set and title condition.
	 * 
	 * @param in
	 *            the specified input stream
	 * @param charset
	 *            the specified character set for the input stream
	 * @param isTitled
	 *            the flag for the data whether it is titled
	 * @return CSV instance
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	protected CSV read(InputStream in, Charset charset, boolean isTitled)
			throws IOException
	{
		InputStreamReader reader = new InputStreamReader(in, charset);
		char[] cbuf = new char[bufferSize];
		int r = reader.read(cbuf);
		StringBuffer sb = new StringBuffer(bufferSize);
		while (r != -1)
		{
			sb.append(cbuf, 0, r);
			r = reader.read(cbuf);
		}
		reader.close();
		return parse(sb.toString(), isTitled);
	}

	/**
	 * Read CSV data from specified input stream.
	 * 
	 * @param in
	 *            the specified input stream
	 * @return CSV instance
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public CSV read(InputStream in) throws IOException
	{
		return read(in, charset, isTitled);
	}

	/**
	 * Read CSV data from specified file.
	 * 
	 * @param file
	 *            the CSV file
	 * @return CSV instance
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public CSV read(File file) throws IOException
	{
		return read(new FileInputStream(file));
	}

	/**
	 * Read CSV data from specified file.
	 * 
	 * @param file
	 *            the CSV file name
	 * @return CSV instance
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public CSV read(String filename) throws IOException
	{
		return read(new FileInputStream(new File(filename)));
	}

	/**
	 * Read CSV data from specified URL. The connection will be made according
	 * to system proxy.
	 * 
	 * @param uri
	 *            the specified URL
	 * @param timeout
	 *            an <tt>int</tt> that specifies the timeout value to be used in
	 *            milliseconds
	 * @return CSV instance
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public CSV read(URL url, int timeout) throws IOException
	{
		return read(url, null, timeout);
	}

	/**
	 * Read CSV data from specified URI. The connection will be made according
	 * to system proxy.
	 * 
	 * @param uri
	 *            the specified URI
	 * @param timeout
	 *            an <tt>int</tt> that specifies the timeout value to be used in
	 *            milliseconds
	 * @return CSV instance
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public CSV read(URI uri, int timeout) throws IOException
	{
		return read(uri.toURL(), timeout);
	}

	/**
	 * Read CSV data from specified URL. The connection is built through the
	 * specified proxy.
	 * 
	 * @param url
	 *            the specified URL
	 * @param proxy
	 *            the Proxy through which this connection will be made. If
	 *            direct connection is desired, Proxy.NO_PROXY should be
	 *            specified. A null value will be regarded as using system
	 *            proxy.
	 * @param timeout
	 *            an <tt>int</tt> that specifies the timeout value to be used in
	 *            milliseconds
	 * @return CSV instance
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public CSV read(URL url, Proxy proxy, int timeout) throws IOException
	{
		URLConnection urlc = proxy == null ? url.openConnection() : url
				.openConnection(proxy);
		if (!urlc.getContentType().equals("text/csv"))
			throw new IllegalArgumentException(
					String.format(
							"Illegal content type: The response content type is \"%s\", not \"text/csv\" as expected.",
							urlc.getContentType()));
		urlc.setReadTimeout(timeout);
		Charset charset = null;
		String contentEncoding = urlc.getContentEncoding();
		if (contentEncoding != null)
			try
			{
				charset = Charset.forName(contentEncoding);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		return read(urlc.getInputStream(), charset == null ? this.charset
				: charset, isTitled);
	}

	/**
	 * Read CSV data from specified URL. The connection is built through the
	 * specified proxy.
	 * 
	 * @param url
	 *            the specified URL
	 * @param host
	 *            the host address for the proxy, cannot be <tt>null</tt>
	 * @param port
	 *            the port for the proxy
	 * @param timeout
	 *            an <tt>int</tt> that specifies the timeout value to be used in
	 *            milliseconds
	 * @return CSV instance
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public CSV read(URL url, String host, int port, int timeout)
			throws IOException
	{
		return read(url, new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host,
				port)), timeout);
	}

	/**
	 * Read CSV data from specified URI. The connection is built through the
	 * specified proxy.
	 * 
	 * @param uri
	 *            the specified URI
	 * @param host
	 *            the host address for the proxy, cannot be <tt>null</tt>
	 * @param port
	 *            the port for the proxy
	 * @param timeout
	 *            an <tt>int</tt> that specifies the timeout value to be used in
	 *            milliseconds
	 * @return CSV instance
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public CSV read(URI uri, String host, int port, int timeout)
			throws IOException
	{
		return read(uri.toURL(), host, port, timeout);
	}

	/**
	 * Read CSV data from specified URI.
	 * 
	 * @param uri
	 *            the specified URI
	 * @param proxy
	 *            the Proxy through which this connection will be made. If
	 *            direct connection is desired, Proxy.NO_PROXY should be
	 *            specified. A null value will be regarded as Proxy.NO_PROXY.
	 * @param timeout
	 *            an <tt>int</tt> that specifies the timeout value to be used in
	 *            milliseconds
	 * @return CSV instance
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public CSV read(URI uri, Proxy proxy, int timeout) throws IOException
	{
		return read(uri.toURL(), proxy, timeout);
	}

	/**
	 * Parse the CSV data according to the content string.
	 * 
	 * @param s
	 *            the content string
	 * @param isTitled
	 *            the flag for the data whether it is titled
	 * @return CSV instance
	 */
	protected CSV parse(String s, boolean isTitled)
	{
		StringTokenizer st = new StringTokenizer(s, delimiter);
		int rows = st.countTokens();
		if (rows < 1)
			return null;
		else
		{
			String[] str = parseLine(st.nextToken());
			CSV csv = new CSV(str.length, isTitled ? rows - 1 : rows, isTitled);
			int row = 0;
			int column;
			if (isTitled)
				csv.setTitles(str);
			else
			{
				for (column = 0; column < str.length; column++)
					csv.setData(row, column, str[column]);
				row++;
			}
			while (st.hasMoreTokens())
			{
				str = parseLine(st.nextToken());
				for (column = 0; column < str.length; column++)
					csv.setData(row, column, str[column]);
				row++;
			}
			return csv;
		}
	}

	/**
	 * Parse one line data to the string array.
	 * 
	 * @param line
	 *            the line data
	 * @return the string array
	 */
	protected String[] parseLine(String line)
	{
		StringTokenizer st = new StringTokenizer(line, ",");
		Vector<String> v = new Vector<String>(st.countTokens());
		while (st.hasMoreTokens())
		{
			String s = st.nextToken();
			if (s.length() > 0 && s.charAt(0) == '"' && st.hasMoreTokens())
			{
				String t = st.nextToken();
				s = s.substring(1) + t.substring(0, t.length() - 1);
				s = s.replaceAll("\"\"", "\"");
			}
			v.add(s);
		}
		return v.toArray(new String[v.size()]);
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
	 * Get the buffer size for CSV data reading.
	 * 
	 * @return the buffer size
	 */
	public int getBufferSize()
	{
		return bufferSize;
	}

	/**
	 * Set the buffer size for CSV data reading.
	 * 
	 * @param bufferSize
	 *            the value of the buffer size
	 */
	public void setBufferSize(int bufferSize)
	{
		this.bufferSize = bufferSize;
	}

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
	 * Get the flag for the data whether it is titled.
	 * 
	 * @return the flag
	 */
	public boolean isTitled()
	{
		return isTitled;
	}

	/**
	 * Set the flag for the data whether it is titled.
	 * 
	 * @param isTitled
	 *            the value of the flag
	 */
	public void setTitled(boolean isTitled)
	{
		this.isTitled = isTitled;
	}
}
