/*
 * Copyright 2013 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */

package com.hongxin.omc.ui.user;

import com.hongxin.component.renderer.TransparentTreeCellRenderer;
import com.hongxin.omc.util.OmcDictionary;
import com.hongxin.util.GUIUtils;
import com.jidesoft.swing.CheckBoxTree;
import com.jidesoft.swing.TreeSearchable;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.jdesktop.application.Application;

/**
 * 角色权限tree组件。
 * @author fanhuigang
 * Created on 2015-2-11, 8:38:58
 */
public class SysRoleFuncTree extends CheckBoxTree
{
	public SysRoleFuncTree()
	{
		super(new SysRoleFuncTreeModel());
		setCellRenderer(new FunctionTreeCellRenderer());
		// 增加搜索支持
		new TreeSearchable(SysRoleFuncTree.this).toString();
	}
	
	/**
	 * 模型包装方法 - 初始化模型。
	 */
	public void initModel()
	{
		((SysRoleFuncTreeModel)getModel()).initModel();
		GUIUtils.expandTreeNode(this, (DefaultMutableTreeNode)getModel().getRoot());
	}
	
	/**
	 * 设置全选。
	 * @param selected 选择状态
	 */
	public void selectAll(boolean selected)
	{
		if(selected)
		{
			getCheckBoxTreeSelectionModel().addSelectionPath(new TreePath(((DefaultMutableTreeNode)getModel().getRoot()).getPath()));
		}
		else
		{
			getCheckBoxTreeSelectionModel().clearSelection();
		}
	}
	
	/**
	 * 获取选择的权限列表。
	 * @return 权限列表
	 */
	public List<Integer> getSelectedFunction()
	{
		List<Integer> listFunction = new ArrayList();
		TreePath[] paths = getCheckBoxTreeSelectionModel().getSelectionPaths();
		if(paths != null)
		{
			for(TreePath path : paths)
			{
				getCheckedFunction(path, listFunction);
			}
		}
		return listFunction;
	}
	
	/**
	 * 添加选择的权限。
	 * @param listFunction 权限id集合
	 */
	public void addSelectedFunction(List<Integer> listFunction)
	{
		// 追加设置
		if(listFunction != null && !listFunction.isEmpty())
		{
			for(Integer function : listFunction)
			{
				TreePath path = ((SysRoleFuncTreeModel)getModel()).getFunctionPath(function);
				if(path != null)
				{
					// 选中状态
					getCheckBoxTreeSelectionModel().addSelectionPath(path);
				}
			}
		}
	}
	
	/**
	 * 设置选择的权限。
	 * @param listFunction 权限id集合
	 */
	public void setSelectedFunction(List<Integer> listFunction)
	{
		if(listFunction == null || listFunction.isEmpty())
		{
			getCheckBoxTreeSelectionModel().clearSelection();
			return;
		}
		// 先清除后设置
		getCheckBoxTreeSelectionModel().clearSelection();
		for(Integer function : listFunction)
		{
			TreePath path = ((SysRoleFuncTreeModel)getModel()).getFunctionPath(function);
			if(path != null)
			{
				// 选中状态
				getCheckBoxTreeSelectionModel().addSelectionPath(path);
			}
		}
	}
	
	/**
	 * 递归获取选择的权限。
	 * @param path 目标path
	 * @param listFunc 选中的权限集合
	 */
	private void getCheckedFunction(TreePath path, List<Integer> listFunc)
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
		// 检查权限节点
		Integer function = ((SysRoleFuncTreeModel)getModel()).getFunction(node);
		if(function != null && listFunc.indexOf(function) == -1)
		{
			listFunc.add(function);
		}
		// 是否包含子节点
		if(node.getChildCount() > 0)
		{
			for(int i = 0; i < node.getChildCount(); i++)
			{
				TreePath childPath = path.pathByAddingChild(node.getChildAt(i));
				if(getCheckBoxTreeSelectionModel().isPathSelected(childPath, isDigIn()))
				{
					getCheckedFunction(childPath, listFunc);
				}
			}
		}
	}
	
	@Override
    public String convertValueToText(Object value, boolean selected,
                                     boolean expanded, boolean leaf, int row,
                                     boolean hasFocus)
	{
		if(value instanceof DefaultMutableTreeNode)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
			if(node.getUserObject() instanceof String)
			{
				return (String)node.getUserObject();
			}
			else if(node.getUserObject() instanceof Integer)
			{
				return OmcDictionary.getInstance().sys_getNameById((Integer)node.getUserObject());
			}
		}
		return super.convertValueToText(value, selected, expanded, leaf, row, hasFocus);
	}
	
	/**
	 * 权限tree渲染器。
	 */
	private class FunctionTreeCellRenderer extends TransparentTreeCellRenderer
	{
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus)
		{
			super.getTreeCellRendererComponent(tree, value, sel, expanded,
					leaf, row, hasFocus);
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
			if(node.getUserObject() instanceof Integer)
			{
				setIcon(Application.getInstance().getContext().getResourceMap().getIcon("image.gear"));
				setText(OmcDictionary.getInstance().sys_getNameById((Integer)node.getUserObject()));
			}
			else if(node.getUserObject() instanceof String)
			{
				setIcon(Application.getInstance().getContext().getResourceMap().getIcon("image.groupAll"));
				setText((String)node.getUserObject());
			}
			return this;
		}
	}
}
