/*
 * Copyright 2013 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */

package com.hongxin.omc.ui.user;

import com.hongxin.omc.operation.Purview;
import com.hongxin.omc.operation.UserPurview;
import java.util.HashMap;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * 角色权限tree模型。
 * @author fanhuigang
 * Created on 2015-2-11, 8:52:24
 */
public class SysRoleFuncTreeModel extends DefaultTreeModel
{
	/**
	 * 树节点缓存。
	 */
	private Map<Integer, DefaultMutableTreeNode> mapTreeNode = new HashMap();
	
	public SysRoleFuncTreeModel()
	{
		super(new DefaultMutableTreeNode("root"));
	}
	
	public SysRoleFuncTreeModel(TreeNode root)
	{
		super(root);
	}
	
	/**
	 * 初始化模型。
	 */
	public void initModel()
	{
		// 获取系统权限集合
		Map<Integer, Purview> mapFunc = UserPurview.getInstance().getAllPurviews();
		// 按照权限分组创建模型
		Map<String, DefaultMutableTreeNode> mapPath = new HashMap();
		for(Map.Entry<Integer, Purview> entry : mapFunc.entrySet())
		{
			Purview func = entry.getValue();
			// 查找path节点
			DefaultMutableTreeNode parentNode = createFuncPathNode(func.getGroup(), 
					(DefaultMutableTreeNode)getRoot(), mapPath);
			if(parentNode == null)
			{
				parentNode = (DefaultMutableTreeNode)getRoot();
			}
			// 创建权限节点
			DefaultMutableTreeNode funcNode = new DefaultMutableTreeNode(entry.getKey());
			parentNode.add(funcNode);
			mapTreeNode.put(entry.getKey(), funcNode);
		}
		mapPath.clear();
	}
	
	/**
	 * 获取目标节点对应的权限。
	 * @param node 节点对象
	 * @return 节点对应权限或null
	 */
	public Integer getFunction(TreeNode node)
	{
		DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)node;
		if(treeNode.getUserObject() instanceof Integer)
		{
			return (Integer)treeNode.getUserObject();
		}
		return null;
	}
	
	/**
	 * 获取目标权限对应的path。
	 * @param function 权限id
	 * @return 权限对应path或null
	 */
	public TreePath getFunctionPath(int function)
	{
		DefaultMutableTreeNode node = mapTreeNode.get(function);
		if(node != null)
		{
			return new TreePath(node.getPath());
		}
		return null;
	}
	
	/**
	 * 创建权限path节点。
	 * @param path 权限path
	 * @param parentNode 父节点
	 * @param cache 权限path节点缓存
	 * @return 权限path对应节点
	 */
	private DefaultMutableTreeNode createFuncPathNode(String path, 
			DefaultMutableTreeNode parentNode,
			Map<String, DefaultMutableTreeNode> cache)
	{
		if(path == null || path.trim().isEmpty())
		{
			return null;
		}
		// 检查path是否已经存在
		DefaultMutableTreeNode pathNode = cache.get(path);
		if(pathNode != null)
		{
			return pathNode;
		}
		// 按级拆分创建path节点
		DefaultMutableTreeNode subPathNode;
		StringBuilder pathBuilder = new StringBuilder();
		String[] pathArray = path.split("/");
		for(int i = 0; i < pathArray.length; i++)
		{
			if(i > 0)
			{
				pathBuilder.append("/");
			}
			pathBuilder.append(pathArray[i]);
			// 检查节点是否存在
			subPathNode = cache.get(pathBuilder.toString());
			// 创建path节点
			if(subPathNode == null)
			{
				subPathNode = new DefaultMutableTreeNode(pathArray[i]);
				cache.put(pathBuilder.toString(), subPathNode);
				parentNode.add(subPathNode);
			}
			parentNode = subPathNode;
		}
		return cache.get(path);
	}
}
