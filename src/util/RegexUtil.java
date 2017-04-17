/*
 * Copyright 2009 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */

package util;

import java.util.regex.Pattern;

/**
 * 正则表达式工具类。
 * @author fanhuigang
 * Created on 2010-2-3, 16:56:33
 */
public class RegexUtil
{
	/**
	 * IPv4正则表达式。
	 */
	private static final Pattern ipv4Pattern = Pattern.compile("\\b(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\."
		+ "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\."
		+ "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\."
		+ "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b");
	/**
	 * IPv6正则表达式。
	 */
	private static final Pattern ipv6Pattern = Pattern.compile("^\\s*((([0-9A-Fa-f]{1,4}:){7}(([0-9A-Fa-f]{1,4})|:))"
			+ "|(([0-9A-Fa-f]{1,4}:){6}(:|((25[0-5]|2[0-4]\\d|[01]?\\d{1,2})(\\.(25[0-5]|2[0-4]\\d|[01]?\\d{1,2})){3})|(:[0-9A-Fa-f]{1,4})))"
			+ "|(([0-9A-Fa-f]{1,4}:){5}((:((25[0-5]|2[0-4]\\d|[01]?\\d{1,2})(\\.(25[0-5]|2[0-4]\\d|[01]?\\d{1,2})){3})?)|((:[0-9A-Fa-f]{1,4}){1,2})))"
			+ "|(([0-9A-Fa-f]{1,4}:){4}(:[0-9A-Fa-f]{1,4}){0,1}((:((25[0-5]|2[0-4]\\d|[01]?\\d{1,2})(\\.(25[0-5]|2[0-4]\\d|[01]?\\d{1,2})){3})?)|((:[0-9A-Fa-f]{1,4}){1,2})))"
			+ "|(([0-9A-Fa-f]{1,4}:){3}(:[0-9A-Fa-f]{1,4}){0,2}((:((25[0-5]|2[0-4]\\d|[01]?\\d{1,2})(\\.(25[0-5]|2[0-4]\\d|[01]?\\d{1,2})){3})?)|((:[0-9A-Fa-f]{1,4}){1,2})))"
			+ "|(([0-9A-Fa-f]{1,4}:){2}(:[0-9A-Fa-f]{1,4}){0,3}((:((25[0-5]|2[0-4]\\d|[01]?\\d{1,2})(\\.(25[0-5]|2[0-4]\\d|[01]?\\d{1,2})){3})?)|((:[0-9A-Fa-f]{1,4}){1,2})))"
			+ "|(([0-9A-Fa-f]{1,4}:)(:[0-9A-Fa-f]{1,4}){0,4}((:((25[0-5]|2[0-4]\\d|[01]?\\d{1,2})(\\.(25[0-5]|2[0-4]\\d|[01]?\\d{1,2})){3})?)|((:[0-9A-Fa-f]{1,4}){1,2})))"
			+ "|(:(:[0-9A-Fa-f]{1,4}){0,5}((:((25[0-5]|2[0-4]\\d|[01]?\\d{1,2})(\\.(25[0-5]|2[0-4]\\d|[01]?\\d{1,2})){3})?)|((:[0-9A-Fa-f]{1,4}){1,2})))"
			+ "|(((25[0-5]|2[0-4]\\d|[01]?\\d{1,2})(\\.(25[0-5]|2[0-4]\\d|[01]?\\d{1,2})){3}))" 
			+ ")(%.+)?\\s*$");
	/**
	 * 简单IPv6正则表达式。
	 */
	private static final Pattern simpleIpv6Pattern = Pattern.compile("^\\s*((([0-9A-Fa-f]{1,4}:){7}(([0-9A-Fa-f]{1,4})|:))"
			+ "|(([0-9A-Fa-f]{1,4}:){6}(:|(:[0-9A-Fa-f]{1,4})))"
			+ "|(([0-9A-Fa-f]{1,4}:){5}(:|(:[0-9A-Fa-f]{1,4}){1,2}))"
			+ "|(([0-9A-Fa-f]{1,4}:){4}(:|(:[0-9A-Fa-f]{1,4}){1,3}))"
			+ "|(([0-9A-Fa-f]{1,4}:){3}(:|(:[0-9A-Fa-f]{1,4}){1,4}))"
			+ "|(([0-9A-Fa-f]{1,4}:){2}(:|(:[0-9A-Fa-f]{1,4}){1,5}))"
			+ "|(([0-9A-Fa-f]{1,4}:)(:|(:[0-9A-Fa-f]{1,4}){1,6}))"
			+ "|(:(:|(:[0-9A-Fa-f]{1,4}){1,6}))"
			+ ")\\s*$");
	/**
	 * 数字串正则表达式。
	 */
	private static final Pattern numberStringPattern =  Pattern.compile("^[0-9]+$");
	/**
	 * 16进制字符串正则表达式。
	 */
	private static final Pattern hexStringPattern =  Pattern.compile("^[a-fA-F0-9]+$");
	/**
	 * 元素名称字符串正则表达式。
	 */
	private static final Pattern elementNameStringPattern = Pattern.compile("[\\p{L}\\p{N}\\(][\\p{L}\\p{N}\\-_.+#()/]*");
	/**
	 * 标识符字符串正则表达式。
	 */
	private static final Pattern identifierStringPattern = Pattern.compile("\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*");

	/**
	 * 判断目标字符串是否有效IPv4地址。
	 * @param netAddress 网络地址字符串
	 * @return 验证结果
	 */
	public static boolean matchIPv4(String netAddress)
	{
		return ipv4Pattern.matcher(netAddress).matches();
	}
	
	/**
	 * 判断目标字符串是否有效IPv6地址。
	 * @param netAddress 网络地址字符串
	 * @return 验证结果
	 */
	public static boolean matchIPv6(String netAddress)
	{
		return ipv6Pattern.matcher(netAddress).matches();
	}
	
	/**
	 * 判断目标字符串是否有效简单IPv6地址。
	 * @param netAddress 网络地址字符串
	 * @return 验证结果
	 */
	public static boolean matchSimpleIPv6(String netAddress)
	{
		return simpleIpv6Pattern.matcher(netAddress).matches();
	}

	/**
	 * 判断目标字符串是否有效数字串。
	 * @param numberString 待验证字符串
	 * @return 验证结果
	 */
	public static boolean matchNumberString(String numberString)
	{
		return numberStringPattern.matcher(numberString).matches();
	}

	/**
	 * 判断目标字符串是否有效16进制字符串。
	 * @param hexString 待验证字符串
	 * @return 验证结果
	 */
	public static boolean matchHexString(String hexString)
	{
		return hexStringPattern.matcher(hexString).matches();
	}

	/**
	 * 判断目标字符串是否有效元素名称字符串。
	 * @param nameString 待验证字符串
	 * @return 验证结果
	 */
	public static boolean matchElementNameString(String nameString)
	{
		return elementNameStringPattern.matcher(nameString).matches();
	}

	/**
	 * 判断目标字符串是否有效标识符名称字符串。
	 * @param identifierString 待验证字符串
	 * @return 验证结果
	 */
	public static boolean matchIdentifierString(String identifierString)
	{
		return identifierStringPattern.matcher(identifierString).matches();
	}

	/**
	 * 判断目标字符串是否有效密码字符串。
	 * @param password 待验证密码字符串
	 * @return 验证结果
	 */
	public static boolean matchPasswordString(String password)
	{
		return password.length() >= 6
				&& password.length() <= 10
				&& Pattern.compile(".*\\p{Lower}+.*").matcher(password).matches()
				&& Pattern.compile(".*\\p{Upper}+.*").matcher(password).matches()
				&& Pattern.compile(".*\\p{Digit}+.*").matcher(password).matches()
				&& Pattern.compile(".*\\p{Punct}+.*").matcher(password).matches();
	}
}
