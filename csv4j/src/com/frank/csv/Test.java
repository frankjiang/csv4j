/*
 * Copyright (c) 2011, 2020, Frank Jiang and/or its affiliates. All rights
 * reserved.
 * Test.java is built in 2013-4-15.
 */
package com.frank.csv;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Main.
 * 
 * @author <a href="mailto:jiangfan0576@gmail.com">Frank Jiang</a>
 * @version 1.0.0
 */
public class Test
{
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		test();
	}

	public static void test() throws Exception
	{
		// setProxy("10.3.135.203", 808);
		URL url = new URL(
				"http://table.finance.yahoo.com/table.csv?s=000001.ss");
		int timeout = 10000;
		InetSocketAddress sa = new InetSocketAddress("10.3.135.203", 808);
		Proxy proxy = new Proxy(Proxy.Type.HTTP, sa);
		URLConnection urlc = url.openConnection(proxy);
		// URLConnection urlc = url.openConnection();
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
		if (charset == null)
			charset = Charset.defaultCharset();
		int BUFF = 10000;
		InputStreamReader isr = new InputStreamReader(urlc.getInputStream());
		char[] cbuf = new char[BUFF];
		int r = isr.read(cbuf);
		StringBuffer sb = new StringBuffer(BUFF);
		while (r != -1)
		{
			sb.append(cbuf, 0, r);
			r = isr.read(cbuf);
		}
		isr.close();
		File f = new File("f.txt");
		if (!f.exists())
			f.createNewFile();
		PrintStream ps = new PrintStream(f);
		ps.print(sb.toString());
		ps.close();
		System.out.println(sb.toString());
	}

	/**
	 * 读取CSV文件
	 * 
	 * @throws Exception
	 * @throws UnknownHostException
	 */
	public static void read() throws UnknownHostException, Exception
	{
		int timeout = 1000;
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
				"10.3.135.203", 808));
		CSVReader csvr = new CSVReader(Charset.forName("gb2312"), true);
		CSV csv = csvr.read(new URL(
				"http://table.finance.yahoo.com/table.csv?s=601006.ss"), proxy,
				timeout);
		String key = "Date";
		String value = "2013-04-15";
		String target = "Close";
		System.out.println(Arrays.toString(csv.getTitles()));
		for (int row = 0; row < csv.rows; row++)
			if (csv.getData(row, key).equals(value))
				System.out.printf("%s in %s : %s\r\n", target, value,
						csv.getData(row, target));
	}

	public static void setProxy(String host, int port)
			throws UnknownHostException, IOException
	{
		System.getProperties().put("proxySet", "true");
		System.getProperties().put("proxyHost", host);
		System.getProperties().put("proxyPort", Integer.toString(port));
	}
}
