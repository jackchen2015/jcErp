/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prj.ui.basic;

import com.hongxin.omc.ui.util.FileDialogUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

/**
 * 将日志数据导出为xls格式
 * @author liming
 */
class LogDataExportToExcel extends LogDataExporter
{
	/**
	 * 导出文件
	 */
	private File output;
	/**
	 * 工作簿对象
	 */
	private HSSFWorkbook workBook;
	/**
	 * 工作表对象
	 */
	private HSSFSheet sheet;
	/**
	 * 文件个数
	 */
	private int fileCount;
	/**
	 * 单元格样式对象
	 */
	private HSSFCellStyle cellStyle;
	
	/**
	 * 构造Excel文件格式的导出类
	 * @param titleColunms 
	 */
	public LogDataExportToExcel(List<String> columns)
	{
		setColumns(columns);
		fileCount = 0 ;
	}
	
	/**
	 * 设置Excel工作簿颜色
	 * @param workBook Excel工作簿
	 */
    private void setCustomColor(HSSFWorkbook workBook)
    {
		//解析十六进制颜色,转换为RGB格式
        String cStr = "#B2A1C7";
        int[] color = new int[3];
        color[0] = Integer.parseInt(cStr.substring(1, 3), 16);
        color[1] = Integer.parseInt(cStr.substring(3, 5), 16);
        color[2] = Integer.parseInt(cStr.substring(5, 7), 16);
		//设置调色板对象
        HSSFPalette palette = workBook.getCustomPalette();
        palette.setColorAtIndex(HSSFColor.GREY_25_PERCENT.index, (byte)color[0], (byte)color[1], (byte)color[2]);
    }
	
	/**
	 * 创建Excel表工作簿
	 */
	private void createWorkBook()
	{
		// 准备数据,创建EXCEL工作簿
		workBook = new HSSFWorkbook();
		// 设置单元格样式
		HSSFCellStyle cellTitleStyle = workBook.createCellStyle();
		cellStyle = workBook.createCellStyle();
		// 设置字体大小
		HSSFFont font = workBook.createFont();
		font.setFontHeightInPoints((short)12);//
		// 设置标题加粗
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		cellTitleStyle.setFont(font);
		// 设置背景填充
		cellTitleStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		cellTitleStyle.setFillForegroundColor(HSSFColor.GOLD.index);
		cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        setCustomColor(workBook);
		cellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
		// 声明sheet工作表对象
		sheet = workBook.createSheet(getDefaultSheetName());
		// 创建表格的标题行
		HSSFRow titleRow = sheet.createRow(getStartRowIndex());
		HSSFRow descRow = null;
		HSSFCellStyle descStyle = null;
		if(getStartRowIndex() > 0 
				&& getBooleanOption(option_export_desc)
				&& getListColumnDesc() != null 
				&& !getListColumnDesc().isEmpty())
		{
			descRow = sheet.createRow(getStartRowIndex() - 1);
			descStyle = workBook.createCellStyle();
			HSSFFont descFont = workBook.createFont();
			// 加粗字体
			descFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			descStyle.setFont(descFont);
			descStyle.setWrapText(true);
		}
		sheet.createRow(getStartRowIndex());
		// 设置标题及列高
		int col = 0;
		for(int i = 0; i < getColumns().size(); i++)
		{
			if(!isColumnSelected(i))
			{
				continue;
			}
			HSSFCell titleCell = titleRow.createCell(col);
			titleCell.setCellValue(new HSSFRichTextString(getColumns().get(i)));
			titleCell.setCellStyle(cellTitleStyle);
			sheet.setColumnWidth(col, (short)4500);
			if(descRow != null)
			{
				HSSFCell descCell = descRow.createCell(col);
				descCell.setCellValue(new HSSFRichTextString(getListColumnDesc().get(i)));
				descCell.setCellStyle(descStyle);
			}
			col++;
		}
	}
	
	/**
	 * 导出Excel文件
	 * @param output 导出文件对象
	 */
	public void exportFile(File output)
	{
		//创建日志对象与文件输出流
		Logger logger = Logger.getLogger(LogDataExportToExcel.class.getName());
		FileOutputStream fileOut = null;
		try
		{
			//根据文件输出流,导出文件
			fileOut = new FileOutputStream(output);
			workBook.write(fileOut);
		}
		catch(Exception ex)
		{
			//发生异常时记录日志
			if(ex instanceof IOException)
			{
				logger.info("notFound filePath...");
			}
			logger.log(Level.SEVERE, null, ex);
		}
		finally
		{
			//关闭输出流
			try
			{
				fileOut.close();
			}
			catch(IOException ex)
			{
				logger.log(Level.SEVERE, null, ex);
			}
		}
	}
	
	/**
	 * 需要导出的数据
	 * @param listRowField 数据对象
	 */
	public void putDataSource(List listRowField)
	{
		int rowCount = sheet.getLastRowNum();
		HSSFRow row = sheet.createRow(++rowCount);
		exportRow(row, listRowField);
		// 如果单个工具表的数据行数达到59990行
		if(rowCount == 59990)
		{
			// 分文件导出
			createOtherFile();
		}
	}
	
	/**
	 * 导出拆分后的记录行。
	 * @param titleRow 行
	 * @param listRowField 拆分后的行字段集合
	 */
	private void exportRow(HSSFRow row, List listRowField)
	{
		createHSSFRow(row, listRowField);
		if(sheet.getLastRowNum() % 2 == 1)
		{
			// 奇数行渲染背景色
			for(int j = 0; j < row.getLastCellNum(); j++)
			{
				row.getCell(j).setCellStyle(cellStyle);
			}
		}
	}
	
	/**
	 * 获取缺省sheet名称。
	 * @return 缺省sheet名称
	 */
	private String getDefaultSheetName()
	{
		String file = output.getName();
		return file.substring(0, file.length() - FileDialogUtil.DOT_FILE_XLS.length());
	}
	
	/**
	 * 分文件导出
	 * @param buffList 还未导出完的数据
	 */
	private void createOtherFile()
	{
		// 导出一次文件
		exportFile(output);
		fileCount++;
		// 修改保存的文件名称
		String filePath = output.getPath();
		filePath = filePath.substring(0, filePath.length() - FileDialogUtil.DOT_FILE_XLS.length());
		filePath = filePath + "_"+ fileCount + FileDialogUtil.DOT_FILE_XLS;
		output = new File(filePath);
		// 创建新的工作簿
		createWorkBook();
	}
	
	/**
	 * 设置告警日志查询表数据
	 * @param titleRow excel表行对象
	 * @param oitem 行数据结构
	 */
	private void createHSSFRow(HSSFRow row, List rowData)
	{
		// 编辑数据结合,写入数据行的每个单元格记录
		int col = 0;
		for(int i = 0; i < rowData.size(); i++)
		{
			if(!isColumnSelected(i))
			{
				continue;
			}
			Object value = toObjectValue(i, rowData.get(i));
			HSSFCell cell;
			if(value instanceof Number)
			{
				cell = row.createCell(col, HSSFCell.CELL_TYPE_NUMERIC);
				cell.setCellValue(((Number)value).doubleValue());
			}
			else
			{
				cell = row.createCell(col, HSSFCell.CELL_TYPE_STRING);
				cell.setCellValue(new HSSFRichTextString(toStringValue(i, rowData.get(i))));
			}
			col++;
		}
	}
	
	/**
	 * 导出记录集前的预处理操作。
	 * @param output 输出文件
	 */
	@Override
	public void beforeExportRecordSet(File output)
	{
		//创建工作簿
		this.output = output;
		createWorkBook();
	}
	
	/**
	 * 导出记录集后的后续操作。
	 * @param output 输出文件
	 */
	@Override
	public void afterExportRecordSet(File output)
	{
		//导出文件
		exportFile(this.output);
	}
	
	/**
	 * 导出记录集的一条记录。
	 * @param listRowField 记录对象
	 */
	@Override
	public void exportRecordImpl(List listRowField)
	{
		putDataSource(listRowField);
	}
}
