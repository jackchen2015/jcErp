/*
 * Copyright 2010 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */
package prj.ui.basic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 将日志文件导出Csv格式
 *
 * @author hanqiuzhi Created on 2013-4-19, 10:04:41
 */
class LogDataExportToCsv extends LogDataExporter
{
	/**
	 * 缓存输出对象
	 */
	private BufferedWriter bw;

	/**
	 * 构造Txt文件格式的导出类
	 *
	 * @param titleColunms
	 */
	public LogDataExportToCsv(List<String> columns)
	{
		setColumns(columns);
	}

	/**
	 * 导出记录集前的预处理操作。
	 *
	 * @param output 输出文件
	 */
	@Override
	public void beforeExportRecordSet(File output)
	{
		try
		{
			OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(output), "GB2312");
			bw = new BufferedWriter(fw);
			// 写标题
			writeTitle();
		}
		catch(IOException ex)
		{
			Logger.getLogger(LogDataExportToCsv.class.getName()).
					log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * 写标题
	 * @throws IOException 
	 */
	private void writeTitle() throws IOException
	{
		//写标题
		List<String> columns = this.getColumns();
		int col = 0;
		for(int i = 0; i < columns.size(); ++i)
		{
			if(!isColumnSelected(i))
			{
				continue;
			}
			// 字段值
			String rowStr = columns.get(i);
			if(rowStr == null)
			{
				rowStr = "";
			}
			// 包含','字符
			if(rowStr.contains(","))
			{
				rowStr = rowStr.replaceAll(",", "\",\"");
			}
			// 最后一个不需要加','号
			if(col > 0)
			{
				rowStr = "," + rowStr;
			}
			bw.write(rowStr);
			col++;
		}
		bw.newLine();
	}

	/**
	 * 导出记录集后的后续操作。
	 *
	 * @param output 输出文件
	 */
	@Override
	public void afterExportRecordSet(File output)
	{
		try
		{
			bw.close();
		}
		catch(IOException ex)
		{
			Logger.getLogger(LogDataExportToCsv.class.getName()).
					log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * 导出记录集的一条记录。
	 *
	 * @param record 记录对象
	 */
	@Override
	public void exportRecordImpl(List listRowField)
	{
		//写出告警日志记录,根据数据类型,创建文本字符串
		csvRowWriter(listRowField);
	}

	/**
	 * 将一行数据写入文本输出流中
	 *
	 * @param rowVector 一行记录
	 * @param bw 文本写入字符输出流
	 * @throws IOException IO异常
	 */
	private void csvRowWriter(List rowVector)
	{
		try
		{
			//遍历数据集合
			int col = 0;
			for(int i = 0; i < rowVector.size(); i++)
			{
				if(!isColumnSelected(i))
				{
					continue;
				}
				//填充每格数据
				String rowStr = toStringValue(i, rowVector.get(i));
				if(rowStr == null)
				{
					rowStr = "";
				}
				// 包含','字符
				if(rowStr.contains(","))
				{
					rowStr = rowStr.replaceAll(",", "\",\"");
				}
				// 最后一个不需要加','号
				if(col > 0)
				{
					rowStr = "," + rowStr;
				}
				bw.write(rowStr);
				col++;
			}
			bw.newLine();
		}
		catch(IOException ex)
		{
			Logger.getLogger(LogDataExportToCsv.class.getName()).
					log(Level.SEVERE, null, ex);
		}
	}
}
