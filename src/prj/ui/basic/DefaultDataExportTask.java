/*
 * Copyright 2009 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */
package prj.ui.basic;

import com.hongxin.component.converter.FieldValueConverter;
import com.hongxin.component.export.DefaultLogExporter;
import com.hongxin.component.export.DefaultRecordSetExporter;
import com.hongxin.component.export.Exporter;
import com.hongxin.component.export.RecordSet;
import com.hongxin.component.export.RowFieldDivider;
import com.hongxin.saf.AsynBlockTask;
import com.hongxin.util.service.ServiceUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

/**
 * 通用数据导出任务。
 * @author liming Created on 2009-11-10, 11:15:22
 */
public class DefaultDataExportTask extends AsynBlockTask<Object, Void>
{
	/**
	 * 保存的文件。
	 */
	private File output;
	/**
	 * 是否选择保存 true:保存;false:不保存。
	 */
	private boolean choose;
	/**
	 * 导出状态 1:导出失败，文件被打开。
	 */
	private int exportState;
	/**
	 * 标题列集合。
	 */
	private List<String> listTitleColumn;
	/**
	 * 导出列选择状态。
	 */
	private List<Boolean> listColumnSelection;
	/**
	 * 导出选项配置。
	 */
	private Map mapExportOption;
	/**
	 * 记录集对象。
	 */
	private RecordSet recordset;
	/**
	 * 导出者对象。
	 */
	private Exporter exporter;
	/**
	 * 字段值转换器对象。
	 */
	private FieldValueConverter converter;
	/**
	 * 行字段分割器对象。
	 */
	private RowFieldDivider divider;
	/**
	 * 导出起始行索引。
	 */
	private int startRowIndex;
	/**
	 * 列描述信息。
	 */
	private List<String> listColumnDesc;

	/**
	 * 部分参数构造函数。
	 * @param columns 列名集合
	 * @param recordset 记录集对象
	 * @param divider 字段分割接口
	 */
	public DefaultDataExportTask(List<String> columns, RecordSet recordSet,
			RowFieldDivider divider)
	{
		this(columns, recordSet, divider, (String)null);
	}
	
	/**
	 * 部分参数构造函数。
	 * @param columns 列名集合
	 * @param recordSet 记录集对象
	 * @param divider 字段分割接口
	 * @param enableSelectExportColumn 是否允许选择导出列
	 */
	public DefaultDataExportTask(List<String> columns, RecordSet recordSet,
			RowFieldDivider divider, boolean enableSelectExportColumn)
	{
		this(columns, recordSet, divider, null, 0, null, (String)null, enableSelectExportColumn);
	}
	
	/**
	 * 部分参数构造函数。
	 * @param columns 列名集合
	 * @param recordset 记录集对象
	 * @param divider 字段分割接口
	 * @param taskTitle 任务标题
	 */
	public DefaultDataExportTask(List<String> columns, RecordSet recordSet,
			RowFieldDivider divider, String taskTitle)
	{
		this(columns, recordSet, divider, null, taskTitle);
	}

	/**
	 * 部分参数构造函数。
	 * @param columns 列名集合
	 * @param recordset 记录集对象
	 * @param converter 字段值转换器
	 */
	public DefaultDataExportTask(List<String> columns, RecordSet recordSet,
			RowFieldDivider divider,
			FieldValueConverter converter)
	{
		this(columns, recordSet, divider, converter, null);
	}
	
	/**
	 * 部分参数构造函数。
	 * @param columns 列名集合
	 * @param recordset 记录集对象
	 * @param converter 字段值转换器
	 * @param divider 行字段分割器
	 * @param taskTitle 任务标题
	 */
	public DefaultDataExportTask(List<String> columns, RecordSet recordSet,
			RowFieldDivider divider,
			FieldValueConverter converter,
			String taskTitle)
	{
		this(columns, recordSet, divider, converter, 0, null, taskTitle);
	}
	
	/**
	 * 部分参数构造函数。
	 * @param columns 列名集合
	 * @param recordset 记录集对象
	 * @param converter 字段值转换器
	 * @param divider 行字段分割器
	 * @param startRowIndex 导出起始行索引(仅对支持空行的文件格式有效)
	 */
	public DefaultDataExportTask(List<String> columns, RecordSet recordSet,
			RowFieldDivider divider,
			FieldValueConverter converter,
			int startRowIndex)
	{
		this(columns, recordSet, divider, converter, 
				startRowIndex, null);
	}
	
	/**
	 * 部分参数构造函数。
	 * @param columns 列名集合
	 * @param recordset 记录集对象
	 * @param converter 字段值转换器
	 * @param divider 行字段分割器
	 * @param startRowIndex 导出起始行索引(仅对支持空行的文件格式有效)
	 * @param listColumnDesc 列描述信息
	 */
	public DefaultDataExportTask(List<String> columns, RecordSet recordSet,
			RowFieldDivider divider,
			FieldValueConverter converter,
			int startRowIndex, List<String> listColumnDesc)
	{
		this(columns, recordSet, divider, converter, 
				startRowIndex, listColumnDesc, null);
	}
	
	/**
	 * 部分参数构造函数。
	 * @param columns 列名集合
	 * @param recordset 记录集对象
	 * @param converter 字段值转换器
	 * @param divider 行字段分割器
	 * @param startRowIndex 导出起始行索引(仅对支持空行的文件格式有效)
	 * @param listColumnDesc 列描述信息
	 * @param taskTitle 任务标题
	 */
	public DefaultDataExportTask(List<String> columns, RecordSet recordSet,
			RowFieldDivider divider,
			FieldValueConverter converter, int startRowIndex, 
			List<String> listColumnDesc, String taskTitle)
	{
		this(columns, recordSet, divider, converter, startRowIndex, 
				listColumnDesc, taskTitle, true);
	}
	
	/**
	 * 部分参数构造函数。
	 * @param columns 列名集合
	 * @param recordset 记录集对象
	 * @param converter 字段值转换器
	 * @param divider 行字段分割器
	 * @param startRowIndex 导出起始行索引(仅对支持空行的文件格式有效)
	 * @param listColumnDesc 列描述信息
	 * @param taskTitle 任务标题
	 * @param enableSelectExportColumn 是否允许选择导出列
	 */
	public DefaultDataExportTask(List<String> columns, RecordSet recordSet,
			RowFieldDivider divider,
			FieldValueConverter converter, int startRowIndex, 
			List<String> listColumnDesc, String taskTitle, 
			boolean enableSelectExportColumn)
	{
		this(columns, recordSet, divider, converter, startRowIndex, 
				listColumnDesc, taskTitle, enableSelectExportColumn, false);
	}
	
	/**
	 * 部分参数构造函数。
	 * @param columns 列名集合
	 * @param recordset 记录集对象
	 * @param converter 字段值转换器
	 * @param divider 行字段分割器
	 * @param startRowIndex 导出起始行索引(仅对支持空行的文件格式有效)
	 * @param listColumnDesc 列描述信息
	 * @param taskTitle 任务标题
	 * @param enableSelectExportColumn 是否允许选择导出列
	 * @param enableExportColumnDesc 是否允许导出列描述
	 */
	public DefaultDataExportTask(List<String> columns, RecordSet recordSet,
			RowFieldDivider divider,
			FieldValueConverter converter, int startRowIndex, 
			List<String> listColumnDesc, String taskTitle, 
			boolean enableSelectExportColumn,
			boolean enableExportColumnDesc)
	{
		this(columns, recordSet, divider, converter, startRowIndex, 
				listColumnDesc, taskTitle, enableSelectExportColumn, 
				enableExportColumnDesc ? Collections.singletonMap("export_column_desc", 
				Application.getInstance().getContext().getResourceMap(DefaultDataExportTask.class).getString("msg.export.title.desc")) : null, 
				null);
	}
	
	/**
	 * 全参数构造函数。
	 * @param columns 列名集合
	 * @param recordset 记录集对象
	 * @param converter 字段值转换器
	 * @param divider 行字段分割器
	 * @param startRowIndex 导出起始行索引(仅对支持空行的文件格式有效)
	 * @param listColumnDesc 列描述信息
	 * @param taskTitle 任务标题
	 * @param enableSelectExportColumn 是否允许选择导出列
	 * @param mapOptionDesc 导出选项描述集合
	 * @param mapOptionValue 导出选项缺省配置集合
	 */
	public DefaultDataExportTask(List<String> columns, RecordSet recordSet,
			RowFieldDivider divider,
			FieldValueConverter converter, int startRowIndex, 
			List<String> listColumnDesc, String taskTitle, 
			boolean enableSelectExportColumn, 
			Map<String, String> mapOptionDesc, 
			Map<String, Boolean> mapOptionValue)
	{
		super(Application.getInstance(), 40000);
		// 设置任务标题
		setTitle(taskTitle != null ? taskTitle : getResourceMap().getString("msg.title"));
		this.recordset = recordSet;
		this.divider = divider;
		this.converter = converter;
		this.startRowIndex = startRowIndex;
		this.listColumnDesc = listColumnDesc;
		// 导出列集合
		setTitleCloumns(columns);
		// 显示文件选择界面
		showFileDialog(columns, enableSelectExportColumn, 
				mapOptionDesc, mapOptionValue);
	}

	/**
	 * 显示文件选择界面。
	 * @param columns 导出列集合
	 * @param enableSelectExportColumn 是否允许选择导出列
	 * @param mapOptionDesc 导出选项描述集合
	 * @param mapOptionValue 导出选项缺省配置集合
	 */
	private void showFileDialog(List<String> columns, 
			boolean enableSelectExportColumn, 
			Map<String, String> mapOptionDesc, 
			Map<String, Boolean> mapOptionValue)
	{
		// 支持的文件类型
		String[] fileTypeArray = new String[]
		{
			FileDialogUtil.FILE_XLS, FileDialogUtil.FILE_TXT, 
			FileDialogUtil.FILE_PDF, FileDialogUtil.FILE_HTML, 
			FileDialogUtil.FILE_CSV, FileDialogUtil.FILE_XML
		};
		// 显示保存文件界面
		// 是否使用accessory组件
		ExportOptionPanel optionComponent = null;
		boolean enableAccessoryComponent = enableSelectExportColumn 
				|| (mapOptionDesc != null && !mapOptionDesc.isEmpty());
		if(enableAccessoryComponent)
		{
			optionComponent = new ExportOptionPanel();
			// 选择导出列支持
			optionComponent.setEnableSelectExportColumn(enableSelectExportColumn);
			optionComponent.setListExportColumn(columns);
			// 其他导出选项配置
			optionComponent.setAvailableBooleanOptoin(mapOptionDesc, mapOptionValue);
		}
		File file = FileDialogUtil.showSaveDialog(null, false, JFileChooser.FILES_ONLY, 
				fileTypeArray, null, null, optionComponent);
		if(file != null)
		{
			if(optionComponent != null)
			{
				listColumnSelection = enableSelectExportColumn ? optionComponent.getExportColumnSelection() : null;
				mapExportOption = optionComponent.getAvailableBooleanOption();
			}
			choose = true;
			output = file;
			exporter = getExporter(file.getPath());
		}
		else
		{
			choose = false;
		}
	}

	@Override
	protected Object doInBackground()
	{
		// 如果未选择保存文件,则后台操作结束
		if(!choose)
		{
			return null;
		}
		// 设置数据集
		exporter.setRecordSet(recordset);
		// 获取分页数据
		exporter.export(output);
		return null;
	}

	/**
	 * 根据文件类型获取对应exporter。
	 * @param file 导出文件
	 */
	private Exporter getExporter(String file)
	{
		Exporter targetExporter = null;
		// 根据导出的文件格式，创建文件导出类
		if(file.endsWith(FileDialogUtil.DOT_FILE_XLS))
		{
			// 导出Excel文件
			targetExporter = new LogDataExportToExcel(listTitleColumn);
		}
		else if(file.endsWith(FileDialogUtil.DOT_FILE_TXT))
		{
			// 导出TXT文件
			targetExporter = new LogDataExportToTxt(listTitleColumn);
		}
		else if(file.endsWith(FileDialogUtil.DOT_FILE_PDF))
		{
			// 导出PDF文件
			targetExporter = new LogDataExportToPdf(listTitleColumn);
		}
		else if(file.endsWith(FileDialogUtil.DOT_FILE_HTML))
		{
			// 导出HTML文件
			targetExporter = new LogDataExportToHtml(listTitleColumn);
		}
		else if(file.endsWith(FileDialogUtil.DOT_FILE_XML))
		{
			// 导出xmlL文件
			targetExporter = new LogDataExportToXml(listTitleColumn);
		}
		else if(file.endsWith(FileDialogUtil.DOT_FILE_CSV))
		{
			// 导出CSV文件
			targetExporter = new LogDataExportToCsv(listTitleColumn);
		}
		// 设置转换器
		if(targetExporter instanceof DefaultLogExporter)
		{
			((DefaultLogExporter)targetExporter).setConverter(converter);
		}
		// 设置分割器
		if(targetExporter instanceof DefaultRecordSetExporter)
		{
			((DefaultRecordSetExporter)targetExporter).setDivider(divider);
		}
		// 设置导出起始行索引
		if(targetExporter instanceof LogDataExporter)
		{
			((LogDataExporter)targetExporter).setEnableAutoIndex(true);
			((LogDataExporter)targetExporter).setStartRowIndex(startRowIndex);
			((LogDataExporter)targetExporter).setListColumnDesc(listColumnDesc);
			((LogDataExporter)targetExporter).setListColumnSelection(listColumnSelection);
			((LogDataExporter)targetExporter).setBooleanOption(mapExportOption);
		}
		return targetExporter;
	}
	
	@Override
	protected void failed(Throwable cause)
	{
		ResourceMap rm = getResourceMap();
		JOptionPane.showMessageDialog(null, 
				rm.getString("msg.export.error"), 
				rm.getString("msg.prompt"), 
				JOptionPane.ERROR_MESSAGE);
	}

	//后台操作执行完毕后,输出用户提示
	@Override
	protected void succeeded(Object result)
	{
		// 导出文件提示		
		ResourceMap rm = getResourceMap();
		if(choose && exportState == 0)
		{
			StringBuilder sbd = new StringBuilder();
			sbd.append(rm.getString("msg.export.succeed"));
			sbd.append("&nbsp;");
			sbd.append(output);			
		}
		else if(exportState == 1)
		{
			JOptionPane.showMessageDialog(null, 
					rm.getString("msg.export.occupied"), 
					rm.getString("msg.prompt"), 
					JOptionPane.WARNING_MESSAGE);
		}
		else if(exportState == 2)
		{
			JOptionPane.showMessageDialog(null, 
					rm.getString("msg.export.error"), 
					rm.getString("msg.prompt"), 
					JOptionPane.WARNING_MESSAGE);
		}
	}

	/**
	 * 创建表的标题列
	 *
	 * @param columns
	 */
	private void setTitleCloumns(List<String> columns)
	{
		if(columns != null)
		{
			listTitleColumn = columns;
		}
		else
		{
			listTitleColumn = new ArrayList<String>();
		}
	}
}
