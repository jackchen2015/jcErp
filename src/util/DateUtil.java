/*
 * Copyright 2009 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */
package util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * 日期工具类。
 * @author fanhuigang
 */
public class DateUtil
{
	public static final Logger logger = Logger.getLogger(DateUtil.class.getName());
	/**
	 * 资源bundle。
	 */
	private static final ResourceBundle bundle = ResourceBundle.getBundle("util/messages");
	/**
	 * 时间常量定义 - 1秒。
	 */
	public static final long ONE_SECOND = 1000L;
	/**
	 * 时间常量定义 - 1分。
	 */	
	public static final long ONE_MINUTE = 60000L;
	/**
	 * 时间常量定义 - 1小时。
	 */	
	public static final long ONE_HOUR = 3600000L;
	/**
	 * 时间常量定义 - 1天。
	 */
	public static final long ONE_DAY = 86400000L;
	/**
	 * 时间常量定义 - 1周。
	 */	
	public static final long ONE_WEEK = 604800000L;
	/**
	 * 时间常量定义 - 1年。
	 */
	public static final long ONE_YEAR = 31556952000L;
	/**
	 * 时间常量定义 - 1世纪。
	 */	
	public static final long ONE_CENTURY = 3155695200000L;
	/**
	 * 日期时间格式key。
	 */
	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	public static String FK_DATETIME = "yyyy-MM-dd HH:mm:ss";
	/**
	 * yyyy-MM-dd HH:mm
	 */
	public static String FK_YMD_HM = "yyyy-MM-dd HH:mm";
	/**
	 * yyyy-MM-dd HH
	 */
	public static String FK_YMD_H = "yyyy-MM-dd HH";
	/**
	 * yyyyMMddHHmmss
	 */
	public static String FK_DATETIME_PACK = "yyyyMMddHHmmss";
	/**
	 * yyyy&MM&dd HH&mm&ss
	 */
	public static String FK_DATETIME_PARAM = "yyyy&MM&dd HH&mm&ss";
	/**
	 * 日期格式key。
	 */
	/**
	 * yyyy-MM-dd
	 */
	public static String FK_DATE = "yyyy-MM-dd";
	/**
	 * yyyyMMdd
	 */
	public static String FK_DATE_PACK = "yyyyMMdd";
	/**
	 * yyyy&MM&dd
	 */
	public static String FK_DATE_PARAM = "yyyy&MM&dd";
	/**
	 * 时间格式key。
	 */
	/**
	 * HH:mm
	 */
	public static String FK_TIME_MINUTE = "HH:mm";
	/**
	 * HH:mm:ss
	 */
	public static String FK_TIME = "HH:mm:ss";
	/**
	 * HHmmss
	 */
	public static String FK_TIME_PACK = "HHmmss";
	/**
	 * HH&mm&ss
	 */
	public static String FK_TIME_PARAM = "HH&mm&ss";
	/**
	 * 毫秒级时间格式key。
	 */
	/**
	 * HH:mm:ss:SS
	 */
	public static String FK_MILLITIME = "HH:mm:ss:SS";
	/**
	 * HHmmssSS
	 */
	public static String FK_MILLITIME_PACK = "HHmmssSS";
	/**
	 * HH&mm&ss&SS
	 */
	public static String FK_MILLITIME_PARAM = "HH&mm&ss&SS";
	/**
	 * 毫秒级日期时间格式key。
	 */
	/**
	 * yyyy-MM-dd HH:mm:ss:SSS
	 */
	public static String FK_MILLITDATETIME = "yyyy-MM-dd HH:mm:ss:SSS";
	/**
	 * yyyyMMddHHmmssSS
	 */
	public static String FK_MILLITDATETIME_PACK = "yyyyMMddHHmmssSSS";
	/**
	 * yyyy&MM&dd HH&mm&ss&SS
	 */
	public static String FK_MILLITDATETIME_PARAM = "yyyy&MM&dd HH&mm&ss&SSS";
	/**
	 * 时间格式map。
	 */
	private static Map<String, DateFormat> mapDateFormat;

	private synchronized static void initDateFormat()
	{
		if(mapDateFormat == null)
		{
			mapDateFormat = new ConcurrentHashMap<String, DateFormat>();
		}
	}

	/**
	 * 获取指定的时间格式化器。
	 * @param formatKey 时间格式key
	 * @return 格式化器
	 */
    public static DateFormat getDateFormat(String formatKey)
    {
		initDateFormat();
		DateFormat format = mapDateFormat.get(formatKey);
		if(format == null)
		{
			format = new SimpleDateFormat(formatKey);
			mapDateFormat.put(formatKey, format);
		}
		return format;
    }

    /**
     * 获取格式化的当前日期时间。
     * @return 格式化后的当前日期时间，日期时间格式为：YYYY-MM-DD HH:MM:SS
     */
    public static String getCurrentDatetime()
    {
        return formatDateTime(java.util.Calendar.getInstance().getTime());
    }

    /**
     * 获取格式化的当前日期。
     * @return 格式化后的当前日期，日期格式为：YYYY-MM-DD
     */    
    public static String getCurrentDate()
    {
        return formatDate(java.util.Calendar.getInstance().getTime());
    }

    /**
     * 获取格式化的当前日期。
	 * @param formatKey 格式化key
     * @return 格式化后的当前日期
     */
    public static String getCurrentDate(String formatKey)
    {
        return formatDate(formatKey, java.util.Calendar.getInstance().getTime());
    }

    /**
     * 获取格式化的当前时间。
     * @return 格式化后的当前时间，时间格式为：HH:MM:SS
     */
    public static String getCurrentTime()
    {
        return formatTime(java.util.Calendar.getInstance().getTime());
    }

    /**
     * 获取格式化的当前日期时间。
     * @return 格式化后的当前日期时间，日期时间格式为：YYYY-MM-DD HH:MM:SS:SS
     */
	public static String getCurrentMilliDateTime()
	{
		return formatMilliDateTime(java.util.Calendar.getInstance().getTime());
	}

	/**
	 * 获取下一个时间。
	 * @param date 原始时间对象
	 * @param field 增量字段
	 * @param amount 增量
	 * @return 应用增量后的时间
	 */
	public static Date getNextDate(Date date, int field, int amount)
	{
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(field, amount);

		return calendar.getTime();
	}

	/**
	 * 获取以当前时间为基准的下一个时间。
	 * @param field 增量字段
	 * @param amount 增量
	 * @return 应用增量后的时间
	 */
	public static Date getNextDate(int field, int amount)
	{
		return getNextDate(new Date(), field, amount);
	}

    /**
     * 格式化指定的日期时间。
     * @param date 待格式化的日期时间
     * @return 格式化后的日期时间，日期时间格式为：YYYY-MM-DD HH:MM:SS
     */
    public static String formatDateTime(Date date)
    {
		if(date != null)
		{
			return getDateFormat(FK_DATETIME).format(date);
		}
		return "";
    }

    /**
     * 格式化指定的日期时间。
     * @param date 待格式化的日期时间
     * @return 格式化后的日期时间，日期时间格式为：YYYY-MM-DD HH:MM:SS:SS
     */
    public static String formatMilliDateTime(Date date)
    {
		if(date != null)
		{
			return getDateFormat(FK_MILLITDATETIME).format(date);
		}
		return "";
    }

    /**
     * 格式化指定的日期。
     * @param date 待格式化的日期
     * @return 格式化后的日期，日期格式为：YYYY-MM-DD
     */
    public static String formatDate(Date date)
    {
		if(date != null)
		{
			return getDateFormat(FK_DATE).format(date);
		}
		return "";
    }

    /**
     * 格式化指定的时间。
     * @param date 待格式化的时间
     * @return 格式化后的时间，时间格式为：HH:MM:SS
     */
    public static String formatTime(Date date)
    {
		if(date != null)
		{
			return getDateFormat(FK_TIME).format(date);
		}
		return "";
    }

	/**
	 * 格式化指定的时间。
	 * @param formatKey 时间格式key
	 * @param date 待格式化的时间
	 * @return 格式化后的时间
	 */
	public static String formatDate(String formatKey, Date date)
	{
		if(date != null)
		{
			return getDateFormat(formatKey).format(date);
		}
		return "";
	}

	/**
	 * 获取时间的hour字段。
	 * @param date 时间对象
	 * @return hour字段，24小时模式
	 */
	public static int getHourOfDate(Date date)
	{
		if(date != null)
		{
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(date);
			return calendar.get(GregorianCalendar.HOUR_OF_DAY);
		}
		return 0;
	}

	/**
	 * 获取时间的minute字段。
	 * @param date 时间对象
	 * @return minute字段
	 */	
	public static int getMinuteOfDate(Date date)
	{
		if(date != null)
		{
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(date);
			return calendar.get(GregorianCalendar.MINUTE);
		}
		return 0;
	}

	/**
	 * 解析指定的文本为日期对象。解析异常则使用当前日期。
	 * @param formatKey 时间格式key
	 * @param date 文本内容
	 * @return 日期对象
	 */
	public static Date parseDate(String formatKey, String date)
	{
		try
		{
			return getDateFormat(formatKey).parse(date);
		}
		catch(Exception exp)
		{
			return new Date();
		}
	}

	/**
	 * 解析指定的文本为日期对象。解析异常返回null。
	 * @param formatKey 时间格式key
	 * @param date 文本内容
	 * @return 日期对象
	 */
	public static Date parseDateNoDefault(String formatKey, String date)
	{
		try
		{
			return getDateFormat(formatKey).parse(date);
		}
		catch(Exception exp)
		{
			return null;
		}
	}

	/**
	 * 解析指定的文本为日期对象。调用者处理异常
	 * @param formatKey 时间格式key
	 * @param date 文本内容
	 * @return 日期对象
	 */
	public static Date parseDateWithException(String formatKey, String date) throws ParseException
	{
		return getDateFormat(formatKey).parse(date);
	}

	/**
	 * 解析指定的文本为日期对象。
	 * @param date 文本内容
	 * @return 日期对象
	 */
	public static Date parseDate(String date)
	{
		try
		{
			return getDateFormat(FK_DATE).parse(date);
		}
		catch(ParseException exp)
		{
			return new Date();
		}
	}

	/**
	 * 解析指定的文本为日期对象。调用者处理异常。
	 * @param date 文本内容
	 * @return 日期对象
	 */
	public static Date parseDateWithException(String date) throws ParseException
	{
		return getDateFormat(FK_DATE).parse(date);
	}

	/**
	 * 解析指定的文本为日期时间。
	 * @param date 文本内容
	 * @return 日期时间对象
	 */
	public static Date parseDateTime(String date)
	{
		try
		{
			return getDateFormat(FK_DATETIME).parse(date);
		}
		catch(ParseException exp)
		{
			return new Date();
		}
	}

	/**
	 * 解析指定的文本为日期时间。调用者处理异常。
	 * @param date 文本内容
	 * @return 日期时间对象
	 */
	public static Date parseDateTimeWithException(String date) throws ParseException
	{
		return getDateFormat(FK_DATETIME).parse(date);
	}

	/**
	 * 解析指定的文本为时间。
	 * @param time 文本内容
	 * @return 时间对象
	 */
	public static Date parseTime(String time)
	{
		try
		{
			return getDateFormat(FK_TIME).parse(time);
		}
		catch(ParseException exp)
		{
			return new Date();
		}
	}

	/**
	 * 解析指定的文本为时间。调用者捕获异常。
	 * @param time 文本内容
	 * @return 时间对象
	 */
	public static Date parseTimeWithException(String time)
			throws ParseException
	{
		return getDateFormat(FK_TIME).parse(time);
	}

    /**
     * 获得预定义开始时间。
     * @return 开始时间，默认使用格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getBeginTime()
    {
		return getBeginTime(null);
    }

    /**
     * 获得预定义结束时间。
     * @return 结束时间，默认使用格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getEndTime()
    {
		return getEndTime(null);
    }

    /**
     * 获得预定义开始时间。
     * @return 开始时间，默认使用格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getBeginTime(Date date)
    {
		return String.format("%s 00:00:00", formatDate(date != null ? date : new Date()));
    }

    /**
     * 获得预定义结束时间。
     * @return 结束时间，默认使用格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getEndTime(Date date)
    {
		return String.format("%s 23:59:59", formatDate(date != null ? date : new Date()));
    }

	/**
	 * 判断date日期是否在beginDate日期与endDate日期之间
	 * @param date 日期
	 * @param beginDate 开始日期
	 * @param endDate 结束日期
	 * @return 判断结果
	 * false ：不在日期范围内
	 * true  ：在日期范围内
	 */
	public static boolean checkBeginAndEndTime(String date, Date beginDate, Date endDate)
	{
		long time = parseDateTime(date).getTime();
		long beginTime = beginDate.getTime();
		long endTime = endDate.getTime();
		if(time >= beginTime && time <=  endTime)
		{
			return true;
		}
		return false;
	}

	/**
	 * 按照YMDHMS比较指定的两个时间。
	 * @param time1 时间1
	 * @param time2 时间2
	 * @return 比较结果
	 */
	public static int compareYmdHms(Date time1, Date time2)
	{
		// 有效性检查
		if(time1 == null || time2 == null)
		{
			if(time1 == null && time2 != null)
			{
				return -1;
			}
			else if(time1 != null && time2 == null)
			{
				return 1;
			}
			else
			{
				return 0;
			}
		}
		int y1, mo1, d1, h1, m1, s1, y2, mo2, d2, h2, m2, s2;
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(time1);
		y1 = calendar.get(Calendar.YEAR);
		mo1 = calendar.get(Calendar.MONTH);
		d1 = calendar.get(Calendar.DAY_OF_MONTH);
		h1 = calendar.get(Calendar.HOUR_OF_DAY);
		m1 = calendar.get(Calendar.MINUTE);
		s1 = calendar.get(Calendar.SECOND);
		calendar.setTime(time2);
		y2 = calendar.get(Calendar.YEAR);
		mo2 = calendar.get(Calendar.MONTH);
		d2 = calendar.get(Calendar.DAY_OF_MONTH);
		h2 = calendar.get(Calendar.HOUR_OF_DAY);
		m2 = calendar.get(Calendar.MINUTE);
		s2 = calendar.get(Calendar.SECOND);

		if(y1 > y2)
		{
			return 1;
		}
		else if(y1 < y2)
		{
			return -1;
		}
		else
		{
			if(mo1 > mo2)
			{
				return 1;
			}
			else if(mo1 < mo2)
			{
				return -1;
			}
			else
			{
				if(d1 > d2)
				{
					return 1;
				}
				else if(d1 < d2)
				{
					return -1;
				}
				else
				{
					if(h1 > h2)
					{
						return 1;
					}
					else if(h1 < h2)
					{
						return -1;
					}
					else
					{
						if(m1 > m2)
						{
							return 1;
						}
						else if(m1 < m2)
						{
							return -1;
						}
						else
						{
							if(s1 > s2)
							{
								return 1;
							}
							else if(s1 < s2)
							{
								return -1;
							}
							else
							{
								return 0;
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 按照YMD比较指定的两个时间。
	 * @param time1 时间1
	 * @param time2 时间2
	 * @return 比较结果
	 */
	public static int compareYmd(Date time1, Date time2)
	{
		// 有效性检查
		if(time1 == null || time2 == null)
		{
			if(time1 == null && time2 != null)
			{
				return -1;
			}
			else if(time1 != null && time2 == null)
			{
				return 1;
			}
			else
			{
				return 0;
			}
		}
		int y1, mo1, d1, y2, mo2, d2;
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(time1);
		y1 = calendar.get(Calendar.YEAR);
		mo1 = calendar.get(Calendar.MONTH);
		d1 = calendar.get(Calendar.DAY_OF_MONTH);
		calendar.setTime(time2);
		y2 = calendar.get(Calendar.YEAR);
		mo2 = calendar.get(Calendar.MONTH);
		d2 = calendar.get(Calendar.DAY_OF_MONTH);

		if(y1 > y2)
		{
			return 1;
		}
		else if(y1 < y2)
		{
			return -1;
		}
		else
		{
			if(mo1 > mo2)
			{
				return 1;
			}
			else if(mo1 < mo2)
			{
				return -1;
			}
			else
			{
				if(d1 > d2)
				{
					return 1;
				}
				else if(d1 < d2)
				{
					return -1;
				}
				else
				{
					return 0;
				}
			}
		}
	}
	
	/**
	 * 计算时长。
	 * @param seconds 以毫秒为单位的时长
	 * @return 计算后的格式字符串
	 */
	public static String seconds2time(long seconds)
	{
		seconds /= 1000;
		long minutes = seconds / 60;
		seconds = seconds - minutes * 60;
		long hours = minutes / 60;
		minutes = minutes - hours * 60;
		long days = hours / 24;
		hours = hours - days * 24;
		// 格式化字符串
		StringBuilder time = new StringBuilder();
		if(days != 0)
		{
			time.append(Long.toString(days));
			time.append(bundle.getString("msg.day"));
			if(hours < 10 && hours > 0)
			{
				time.append("0");
			}
		}
		if(hours != 0)
		{
			time.append(Long.toString(hours));
			time.append(bundle.getString("msg.hour"));
			if(minutes < 10 && minutes > 0)
			{
				time.append("0");
			}
		}
		if(minutes != 0)
		{
			time.append(Long.toString(minutes));
			time.append(bundle.getString("msg.minute"));
			if(seconds < 10 && seconds > 0)
			{
				time.append("0");
			}
		}
		if(seconds != 0)
		{
			time.append(Long.toString(seconds));
			time.append(bundle.getString("msg.second"));
		}
		
		return time.toString();
	}
	
	public static void main(String[] args)
	{
		System.out.println(DateUtil.parseTime("13:10:30"));
		System.out.println(DateUtil.parseTime("12:10:30"));
		System.out.println(DateUtil.formatDate(DateUtil.parseTime("12:10:30")));
		System.out.println(DateUtil.parseDate("2012-04-05 12:00::00"));
	}
}
