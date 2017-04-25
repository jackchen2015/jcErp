/*
 * Copyright 2009 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */
package com.hongxin.omc.ui.user;

import com.hongxin.omc.protocol.SystemItem;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import java.util.Map;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.swing.tree.TreePath;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;

/**
 * 用户权限treetablemodel实现
 * @author liming
 * Created on 2009-11-12, 17:24:30
 */
public class UserPurviewTreeTableModel extends DefaultTreeTableModel
{
	/**
	 * 用户权限表列名
	 */
	private List<String> columns;
	/**
	 *
	 */
	private Map<DefaultMutableTreeTableNode, Boolean> dictSysPurviewData;

	/**
	 * 初始化模型
	 * @param root 根节点
	 */
	public UserPurviewTreeTableModel(DefaultMutableTreeTableNode root,
			List<String> columns,
			Map<DefaultMutableTreeTableNode, Boolean> dictSysPurviewData)
	{
		super(root);
		this.columns = columns;
		this.dictSysPurviewData = dictSysPurviewData;
	}

	/**
	 * 根据传入参数设置权限的选择状态
	 * @param value 节点值
	 * @param node 节点
	 * @param column 列数
	 */
	@Override
	public void setValueAt(Object value, Object node, int column)
	{
		//设置操作列
		DefaultMutableTreeTableNode itemNode =
				(DefaultMutableTreeTableNode)node;
		//用户对象是SystemITem && 列为1
		if(itemNode.getUserObject() instanceof SystemItem && column == 1)
		{
			//是否选择
			dictSysPurviewData.put(itemNode, (Boolean)value);
			selectItemAll(itemNode, (Boolean)value);
		}
		//是否为String对象
		else if(itemNode.getUserObject() instanceof String && column == 1)
		{
			dictSysPurviewData.put(itemNode, (Boolean)value);
			selectItemAll(itemNode, (Boolean)value);
		}
	}

	/**
	 * 将权限记录显示在列表中
	 * @param node 节点
	 * @param column 列数
	 * @return 返回每条记录的值
	 */
	@Override
	public Object getValueAt(Object node, int column)
	{
		//获得节点
		DefaultMutableTreeTableNode itemNode = (DefaultMutableTreeTableNode)node;
		//判断对象类型
		if(itemNode.getUserObject() instanceof SystemItem)
		{
			SystemItem item = (SystemItem)itemNode.getUserObject();
			switch(column)
			{
				case 0:
					//节点名称
					return item.getItemName();
				case 1:
					return dictSysPurviewData.get(itemNode);
			}
		}
		else
		{
			switch(column)
			{
				case 0:
					return itemNode.getUserObject();
				case 1:
					return dictSysPurviewData.get(itemNode);
			}
		}
		//}
		return null;
	}

	/**
	 * 设置列名
	 * @param column 列数
	 * @return 返回列名
	 */
	@Override
	public String getColumnName(int column)
	{
		return columns.get(column);
	}

	/**
	 * 设置每列的可编辑状态
	 * @param node 节点
	 * @param column 列数
	 * @return 返回列的可编辑状态
	 */
	@Override
	public boolean isCellEditable(java.lang.Object node, int column)
	{
		if(column == 1)
		{
			return true;
		}
		return false;
	}

	/**
	 * 获得列数
	 * @return 返回列数
	 */
	@Override
	public int getColumnCount()
	{
		return 2;
	}

	/**
	 * 返回列的类型
	 * @param column
	 * @return
	 */
	@Override
	public Class<?> getColumnClass(
			int column)
	{
		switch(column)
		{
			case 0:
				//字符
				return String.class;
			case 1:
				//Boolean
				return Boolean.class;
			default:
				return String.class;
		}
	}
	
	/**
	 * 设置全选状态。
	 * @param isSelected 是否选择
	 */
	public void selectAll(boolean isSelected)
	{
		for(Iterator iter = dictSysPurviewData.entrySet().iterator(); iter.hasNext();)
		{
			Map.Entry entry = (Map.Entry)iter.next();
			DefaultMutableTreeTableNode key =
					(DefaultMutableTreeTableNode)entry.getKey();
			dictSysPurviewData.put(key, (Boolean)isSelected);
			modelSupport.firePathChanged(new TreePath(getPathToRoot(key)));
		}
	}

	/**
	 * 选择所有的
	 * @param node
	 * @param isSelected
	 */
	public void selectItemAll(final DefaultMutableTreeTableNode node,
			final boolean isSelected)
	{
		Enumeration<? extends MutableTreeTableNode> children = node.children();
		while(children.hasMoreElements())
		{
			DefaultMutableTreeTableNode child = (DefaultMutableTreeTableNode)children.nextElement();
			// 设置子节点选择状态
			dictSysPurviewData.put(child, (Boolean)isSelected);
			modelSupport.firePathChanged(new TreePath(getPathToRoot(child)));
			// 递归选择子节点
			selectItemAll(child, isSelected);
		}
	}
}
