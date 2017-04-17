/*
 * Copyright 2009 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */

package util;

/**
 * HTML工具类。
 * @author fanhuigang
 * Created on 2010-6-24, 14:19:13
 */
public class HtmlUtil
{
	/**
	 * 获取指定字符串的utf-8编码字节内容。
	 * @param value 字符串
	 * @return utf-8编码的字节内容
	 */
	public static byte[] getUTFBytes(String value)
	{
		try
		{
			return value.getBytes("utf-8");
		}
		catch(Exception exp)
		{
			return new byte[0];
		}
	}

	/**
	 * 获取设置charset属性的meta标签。
	 * @return 标签内容
	 */
	public static String getCharsetMetaString(String charset)
	{
		return String.format("<meta http-equiv=&quot;Content-Type&quot; content=&quot;text/html; charset=%s&quot;/&gt;", charset);
	}

	/**
	 * 获取html头标签字符串。
	 * @return 标签字符串
	 */
	public static String getHtmlHeader()
	{
		StringBuilder sbd = new StringBuilder();

		sbd.append("<html><head>");
		sbd.append(getCharsetMetaString("utf-8"));
		sbd.append("</head><body>");

		return sbd.toString();
	}

	/**
	 * 获取html尾标签字符串。
	 * @return 标签字符串
	 */
	public static String getHtmlFooter()
	{
		return "</body></html>";
	}

	/**
	 * 格式化plain文本。将普通文本中的特殊字符，例如回车换行符等转换为对应tag。
	 * @param plainText plain文本内容
	 * @return 格式化后的文本内容
	 */
	public static String getFormattedPlainText(String plainText)
	{
		// 替换\r\n符号为<br>，替换\n符号为<br>
		return plainText.replace("\r\n", "<br>").replace("\n", "<br>").replace(" ", "&nbsp;");
	}
}
