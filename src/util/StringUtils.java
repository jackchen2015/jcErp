/*
 * Copyright 2010 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */
package util;

/**
 * 字符串工具类。
 * @author fanhuigang
 * Created on 2011-5-25, 14:19:18
 */
public class StringUtils
{
	/**
	 * 判断指定字符串是否为空。支持null参数。
	 * @param str 字符串对象
	 * @return 判断结果
	 */
	public static boolean hasLength(String str)
	{
		return (str != null && str.length() > 0);
	}

	/**
	 * 对目标字符串执行trim操作。支持null参数。
	 * @param str 字符串对象
	 * @return 执行结果
	 */
	public static String trim(String str)
	{
		if(!hasLength(str))
		{
			return str;
		}
		return str.trim();
	}
}
