/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prj.ui.basic;

import prj.ui.basic.CnPdfCell;
import java.io.File;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Table;
import com.lowagie.text.html.HtmlWriter;
import com.lowagie.text.html.simpleparser.StyleSheet;
import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 将日志数据导出为html格式
 * @author liming
 */
class LogDataExportToHtml extends LogDataExporter
{
	/**
	 * 导出状态
	 */
	private int exportState;
	/**
	 * HTML文档结构对象
	 */
	private	Document doc;
	/**
	 * 文件输出对象
	 */
	private FileOutputStream fos;
	/**
	 * pdf表格对象
	 */
	private Table tab;
	
	/**
	 * 构造Txt文件格式的导出类
	 * @param titleColunms 
	 */
	public LogDataExportToHtml(List<String> columns)
	{
		setColumns(columns);
	}
	
	/**
	 * 创建PDF文件
	 */
	private void createPdfFile(File output)
	{
		//HTMLWorker
		try
		{
			//创建Document文档对象
			doc = new Document(PageSize.B0, 10, 10, 10, 10);
			//设置文档样式
			StyleSheet st = new StyleSheet();
			st.loadStyle("body", "leading", "16,0");
			fos = new FileOutputStream(output);
			HtmlWriter.getInstance(doc, fos);
			doc.open();
			//添加标题行
			tab = createPdfTableHeaderByLogData();
			tab.setAutoFillEmptyCells(true);
			tab.setBorderColor(Color.GRAY);
			tab.setPadding(5f);
		}
		catch(Exception ex)
		{
			if(ex instanceof FileNotFoundException)
			{
				//文件被打开
				exportState = 1;
			}
			else
			{
				//导出时出现异常
				exportState = 2;
			}
			Logger.getLogger(LogDataExportToHtml.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	/**
	 * 创建一个包含表格标题的HTML文件表格对象
	 * @return 表格对象
	 * @throws Exception 
	 */
	private Table createPdfTableHeaderByLogData() throws Exception
	{
		// 计算导出列
		int count = 0;
		for(int i = 0; i < getColumns().size(); i++)
		{
			if(isColumnSelected(i))
			{
				count++;
			}
		}
		Table headerTab = new Table(count);
		//创建标题行
		for(int i = 0; i < getColumns().size(); i++)
		{
			if(!isColumnSelected(i))
			{
				continue;
			}
			String columnName = getColumns().get(i);
			CnPdfCell headerCell = new CnPdfCell(columnName, true);
			headerCell.setHeader(true);
			headerTab.addCell(headerCell);
		}
		return headerTab;
	}
	
	/**
	 * 导出记录集前的预处理操作。
	 * @param output 输出文件
	 */
	@Override
	public void beforeExportRecordSet(File output)
	{
		createPdfFile(output);
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
			doc.add(tab);
			//关闭输出流
			if(doc.isOpen())
			{
				doc.close();
			}
			fos.close();
		}
		catch(Exception ex)
		{
			Logger.getLogger(LogDataExportToHtml.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	/**
	 * 导出记录集的一条记录。
	 * @param record 记录对象
	 */
	@Override
	public void exportRecordImpl(List listRowField)
	{
		try
		{
			// 创建数据行
			List<CnPdfCell> cellList = createPdfTableByLogData(listRowField);
			// 将数据行加入表格中
			for(int i = 0; i < cellList.size(); i++)
			{
				if(!isColumnSelected(i))
				{
					continue;
				}
				tab.addCell(cellList.get(i));
			}
		}
		catch(Exception ex)
		{
			Logger.getLogger(LogDataExportToHtml.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	/**
	 * 根据日志数据，创建一行HTML记录
	 * @param record 一条日志数据对象
	 * @return HTML单元格集合
	 * @throws Exception 
	 */
	private List<CnPdfCell> createPdfTableByLogData(List listRowField) throws Exception
	{
		//创建html单元格集合
		List<CnPdfCell> cellList = new ArrayList();
		//根据数据类型,创建pdf单元格集合
		cellList.addAll(createPdfCellByRowData(listRowField));
		
		return cellList;
	}
	
	/**
	 * 根据数据集合，创建HTML单元格对象集合
	 * @param rowVector 日志数据集合
	 * @return 创建HTML单元格对象集合
	 * @throws Exception 
	 */
	private List<CnPdfCell> createPdfCellByRowData(List rowVector) throws Exception
	{
		// 创建pdf单元格集合
		List<CnPdfCell> cellList = new ArrayList();
		// 创建excel表格行
		int fieldIndex = 0;
		for(Object itemValue : rowVector)
		{
			// 填充每格数据
			cellList.add(new CnPdfCell(toStringValue(fieldIndex++, itemValue)));
		}
		return cellList;
	}
}
