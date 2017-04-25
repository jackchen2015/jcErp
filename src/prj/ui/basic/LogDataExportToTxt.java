/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prj.ui.basic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 将日志数据导出为txt格式
 * @author liming
 */
class LogDataExportToTxt extends LogDataExporter
{
	/**
	 * 缓存输出对象
	 */
	private BufferedWriter bw;

	/**
	 * 构造Txt文件格式的导出类
	 * @param titleColunms 
	 */
	public LogDataExportToTxt(List<String> columns)
	{
		setColumns(columns);
	}

	/**
	 * 导出记录集前的预处理操作。
	 * @param output 输出文件
	 */
	@Override
	public void beforeExportRecordSet(File output)
	{
		FileWriter fw = null;
		try
		{
			fw = new FileWriter(output);
			bw = new BufferedWriter(fw);
		}
		catch(IOException ex)
		{
			Logger.getLogger(LogDataExportToTxt.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * 导出记录集后的后续操作。
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
			Logger.getLogger(LogDataExportToTxt.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * 导出记录集的一条记录。
	 * @param record 记录对象
	 */
	@Override
	public void exportRecordImpl(List listRowField)
	{
		//写出告警日志记录,根据数据类型,创建文本字符串
		txtRowWriter(listRowField);
	}

	/**
	 * 将一行数据写入文本输出流中
	 * @param rowVector 一行记录
	 * @param bw 文本写入字符输出流
	 * @throws IOException IO异常
	 */
	private void txtRowWriter(List rowVector)
	{
		try
		{
			//遍历数据集合
			for(int i = 0; i < rowVector.size(); i++)
			{
				if(!isColumnSelected(i))
				{
					continue;
				}
				String columnName = getColumns().get(i);
				//填充每格数据
				String rowStr = toStringValue(i, rowVector.get(i));
				bw.write(columnName + " = " + rowStr + "   ");
			}
			bw.newLine();
		}
		catch(IOException ex)
		{
			Logger.getLogger(LogDataExportToTxt.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
