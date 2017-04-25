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
 * 将数据导出为xml格式文件。
 * @author hanqiuzhi Created on 2013-5-14, 9:16:48
 */
class LogDataExportToXml extends LogDataExporter
{
	private StringBuilder xml;
	private BufferedWriter writer;

	public LogDataExportToXml(List<String> columns)
	{
		setColumns(columns);
	}

	@Override
	public void beforeExportRecordSet(File output)
	{
		xml = new StringBuilder();
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		xml.append("<root>\n");
		try
		{
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), "UTF-8"));
		}
		catch(Exception exp)
		{}
	}

	@Override
	public void afterExportRecordSet(File output)
	{
		xml.append("</root>");
		try
		{
			writer.write(xml.toString());
			writer.flush();
		}
		catch(IOException ex)
		{
			Logger.getLogger(LogDataExportToXml.class.getName()).
					log(Level.SEVERE, null, ex);
		}
		finally
		{
			try
			{
				writer.close();
			}
			catch(Exception exp)
			{}
			xml.delete(0, xml.length());
			xml = null;
		}
	}

	@Override
	public void exportRecordImpl(List listRowField)
	{
		xmlItemAppend(listRowField);
		if(xml.length() > 1024)
		{
			try
			{
				writer.write(xml.toString());
				xml.delete(0, xml.length());
			}
			catch(Exception exp)
			{}
		}
	}

	/**
	 * xml组合
	 *
	 * @param divideRow
	 */
	private void xmlItemAppend(List divideRow)
	{
		xml.append("<record>\n");
		//遍历数据集合
		for(int i = 0; i < divideRow.size(); i++)
		{
			if(!isColumnSelected(i))
			{
				continue;
			}
			String columnName = getColumns().get(i).replaceAll("/", "").
					replaceAll("\\(", "").replaceAll("\\)", "").
					replaceAll("<", "").replaceAll(">", "").replaceAll(" ", "");
			//填充每格数据
			String rowStr = toStringValue(i, divideRow.get(i));
			xml.append("<").append(columnName).append(">");
			xml.append("<![CDATA[");
			xml.append(rowStr);
			xml.append("]]>");
			xml.append("</").append(columnName).append(">");
		}
		xml.append("</record>\n");
	}
}
