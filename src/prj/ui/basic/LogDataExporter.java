/*
 * Copyright 2013 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */

package prj.ui.basic;

import com.hongxin.component.export.DefaultRecordSetExporter;
import com.hongxin.component.export.SimpleRowFieldDivider;
import com.hongxin.component.export.TableRecordSet;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jdesktop.application.Application;

/**
 * 通用日志数据导出实现。
 * @author fanhuigang
 * Created on 2014-4-3, 20:15:15
 */
public class LogDataExporter extends DefaultRecordSetExporter
{
	/**
	 * 导出选项 - 导出列描述。
	 */
	public static final String option_export_desc = "export_column_desc";
	/**
	 * 导出起始行索引(即标题行索引，对支持空行的文件格式有效)。
	 */
	private int startRowIndex;
	/**
	 * 使用自动序号。
	 */
	private boolean enableAutoIndex;
	/**
	 * 列描述集合(默认输出到标题行索引 - 1的位置)。
	 */
	private List<String> listColumnDesc;
	/**
	 * 导出列集合选择状态。
	 */
	private List<Boolean> listColumnSelection;
	/**
	 * boolean导出选项配置。
	 */
	private Map<String, Boolean> mapBooleanOption;
	/**
	 * 自动序号。
	 */
	private int autoIndex;
	
	@Override
	public void exportRecord(Object record)
	{
		// 区分记录集
		List listRowField;
		if(getRecordSet() instanceof TableRecordSet)
		{
			TableRecordSet table = (TableRecordSet)getRecordSet();
			listRowField = new ArrayList();
			// 根据导出列提取字段值
			for(String column : getColumns())
			{
				listRowField.add(table.getFieldValue(getColumns().indexOf(column)));
			}
		}
		else
		{
			// 分割记录行
			listRowField = getDivider().divideRow(record);
		}
		// 嵌套记录
		if(!listRowField.isEmpty() && listRowField.get(0) instanceof List)
		{
			for(Object field : listRowField)
			{
				exportRecordInternal((List)field);
			}
		}
		else
		{
			exportRecordInternal(listRowField);
		}
	}
	
	/**
	 * 导出行记录数据的内部实现。
	 * @param listRowField 行记录数据
	 */
	private void exportRecordInternal(List listRowField)
	{
		// 自动序号列的处理: 允许使用自动序号且字段值缺少序号
		if(enableAutoIndex && listRowField.size() == getColumns().size() - 1)
		{
			int index = getColumns().indexOf(Application.getInstance().getContext().getResourceMap().getString("global.field.index"));
			if(index != -1)
			{
				listRowField.add(index, autoIndex + 1);
			}
		}
		exportRecordImpl(listRowField);
		autoIndex++;
	}
	
	/**
	 * 导出记录扩展实现。
	 * @param listRowField 行字段集合
	 */
	public void exportRecordImpl(List listRowField)
	{}
	
	@Override
	public void afterExportRecordSet(File output)
	{
		super.afterExportRecordSet(output);
		autoIndex = 0;
	}

	/**
	 * 导出起始行索引(对支持空行的文件格式有效)。
	 * @return the startRowIndex
	 */
	public int getStartRowIndex()
	{
		return startRowIndex;
	}

	/**
	 * 导出起始行索引(对支持空行的文件格式有效)。
	 * @param startRowIndex the startRowIndex to set
	 */
	public void setStartRowIndex(int startRowIndex)
	{
		if(startRowIndex >= 0)
		{
			this.startRowIndex = startRowIndex;
		}
	}
	
	/**
	 * 使用自动序号。
	 * @return the enableAutoIndex
	 */
	public boolean isEnableAutoIndex()
	{
		return enableAutoIndex;
	}

	/**
	 * 使用自动序号。
	 * @param enableAutoIndex the enableAutoIndex to set
	 */
	public void setEnableAutoIndex(boolean enableAutoIndex)
	{
		this.enableAutoIndex = enableAutoIndex;
	}

	/**
	 * 列描述集合。
	 * @return the listColumnDesc
	 */
	public List<String> getListColumnDesc()
	{
		return listColumnDesc;
	}

	/**
	 * 列描述集合。
	 * @param listColumnDesc the listColumnDesc to set
	 */
	public void setListColumnDesc(List<String> listColumnDesc)
	{
		this.listColumnDesc = listColumnDesc;
	}

	/**
	 * 导出列集合选择状态。
	 * @param listColumnSelection the listColumnSelection to set
	 */
	public void setListColumnSelection(List<Boolean> listColumnSelection)
	{
		this.listColumnSelection = listColumnSelection;
	}
	
	/**
	 * 检查指定列是否选择导出。
	 * @param index 列索引
	 * @return 选择状态
	 */
	public boolean isColumnSelected(int index)
	{
		return listColumnSelection == null || listColumnSelection.get(index);
	}

	/**
	 * boolean导出选项配置。
	 * @return the mapBooleanOption
	 */
	public Map<String, Boolean> getBooleanOption()
	{
		return mapBooleanOption;
	}
	
	/**
	 * 获取指定boolean导出选项配置内容。
	 * @return 指定选项配置内容，不存在时返回false
	 */
	public boolean getBooleanOption(String option)
	{
		if(mapBooleanOption != null && mapBooleanOption.containsKey(option))
		{
			return mapBooleanOption.get(option);
		}
		return false;
	}

	/**
	 * boolean导出选项配置。
	 * @param mapBooleanOption the mapBooleanOption to set
	 */
	public void setBooleanOption(Map<String, Boolean> mapBooleanOption)
	{
		this.mapBooleanOption = mapBooleanOption;
		// 传递到字段分割接口
		if(mapBooleanOption != null
				&& !mapBooleanOption.isEmpty()
				&& getDivider() instanceof SimpleRowFieldDivider)
		{
			for(Map.Entry entry : mapBooleanOption.entrySet())
			{
				((SimpleRowFieldDivider)getDivider()).putProperty(entry.getKey(), entry.getValue());
			}
		}
	}
}
