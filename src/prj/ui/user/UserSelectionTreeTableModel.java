/*
 * Copyright 2013 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */

package prj.ui.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.tree.TreePath;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import prj.user.po.User;
import prj.user.po.UserGroup;

/**
 * 用户选择TreeTable模型。
 * @author fanhuigang
 * Created on 2014-5-27, 18:06:47
 */
public class UserSelectionTreeTableModel extends DefaultTreeTableModel
{
	/**
	 * 用户组信息。
	 */
	private Map<Integer, String> mapGroup;
	/**
	 * 用户信息。
	 */
	private Map<Integer, String> mapUser;
	/**
	 * 节点选择状态。
	 */
	private Map<NodeIndex, Boolean> mapSelection;
	private List<String> columns;
	
	public UserSelectionTreeTableModel()
	{
		super(new DefaultMutableTreeTableNode(new NodeIndex(0, UserUtil.NODE_ROOT)));
		ResourceMap rm = Application.getInstance().getContext().getResourceMap(UserSelectionTreeTableModel.class);
		mapSelection = new HashMap();
		columns = new ArrayList<String>();
		columns.add(rm.getString("cols.name"));
		columns.add(rm.getString("cols.selection"));
	}
	
	/**
	 * 初始化模型数据。
	 * @param listGroup 用户组列表
	 * @param listUser 用户列表
	 */
	public void initModel(List<UserGroup> listGroup, List<User> listUser)
	{
		clear();
		// 建立树模型
		mapGroup = new HashMap();
		mapUser = new HashMap();
		Map<NodeIndex, DefaultMutableTreeTableNode> mapNode = new HashMap();
		// 用户组节点
		DefaultMutableTreeTableNode parentNode = (DefaultMutableTreeTableNode)getRoot();
		for(UserGroup group : listGroup)
		{
			mapGroup.put(group.getId(), group.getName());
			NodeIndex index = new NodeIndex(group.getId(), UserUtil.NODE_GROUP);
			DefaultMutableTreeTableNode node = new DefaultMutableTreeTableNode(index);
			parentNode.add(node);
			mapNode.put(index, node);
		}
		// 用户节点
		for(User user : listUser)
		{
			mapUser.put(user.getId(), user.getName());
			NodeIndex userIndex = new NodeIndex(user.getId(), UserUtil.NODE_USER);
			for(Integer group : user.getGroups())
			{
				NodeIndex groupIndex = new NodeIndex(group, UserUtil.NODE_GROUP);
				parentNode = mapNode.get(groupIndex);
				if(parentNode != null)
				{
					DefaultMutableTreeTableNode node = new DefaultMutableTreeTableNode(userIndex);
					parentNode.add(node);
				}
			}
		}
		mapNode.clear();
		// 触发事件
		modelSupport.fireNewRoot();
	}
	
	/**
	 * 设置用户选择状态。
	 * @param listUserId 待选择用户id列表 
	 */
	public void setSelectedUser(List<Integer> listUserId)
	{
		if(listUserId != null && !listUserId.isEmpty())
		{
			for(Integer userId : listUserId)
			{
				if(mapUser.containsKey(userId))
				{
					mapSelection.put(new NodeIndex(userId, UserUtil.NODE_USER), Boolean.TRUE);
				}
			}
			// 触发事件
			modelSupport.fireNewRoot();
		}
	}
	
	/**
	 * 检查选择是否为空。
	 * @return 检查结果
	 */
	public boolean isSelectionEmpty()
	{
		for(NodeIndex index : mapSelection.keySet())
		{
			if(index.getType() == UserUtil.NODE_USER)
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 获取选择的用户id列表。
	 * @return 选择的用户id列表
	 */
	public List<Integer> getSelectedUserId()
	{
		List<Integer> listUserId = null;
		for(NodeIndex index : mapSelection.keySet())
		{
			if(index.getType() == UserUtil.NODE_USER)
			{
				if(listUserId == null)
				{
					listUserId = new ArrayList();
				}
				listUserId.add(index.getId());
			}
		}
		return listUserId != null ? listUserId : Collections.EMPTY_LIST;
	}
	
	/**
	 * 删除所有子节点。
	 */
	public void clear()
	{
		for(int i = getRoot().getChildCount() - 1; i >= 0; i--)
		{
			((DefaultMutableTreeTableNode)getRoot()).remove(i);
		}
		// 触发事件
		modelSupport.fireNewRoot();
	}
	
	@Override
	public int getColumnCount()
	{
		return columns.size();
	}

	@Override
	public Class<?> getColumnClass(int column)
	{
		switch(column)
		{
			case 1:
				return Boolean.class;
			default:
				return String.class;
		}
	}

	@Override
	public boolean isCellEditable(Object node, int column)
	{
		return column == 1;
	}

	@Override
	public String getColumnName(int column)
	{
		return columns.get(column);
	}
	
	@Override
	public Object getValueAt(Object node, int column)
	{
		DefaultMutableTreeTableNode treeNode = (DefaultMutableTreeTableNode)node;
		NodeIndex index = (NodeIndex)treeNode.getUserObject();
		switch(column)
		{
			case 0:
				if(index.getType() == UserUtil.NODE_GROUP)
				{
					return mapGroup.get(index.getId());
				}
				else if(index.getType() == UserUtil.NODE_USER)
				{
					// 用户信息
					return mapUser.get(index.getId());
				}
			case 1:
				return mapSelection.containsKey(index);
		}
		return null;
	}
	
	@Override
	public void setValueAt(Object value, Object node, int column)
	{
		DefaultMutableTreeTableNode treeNode = (DefaultMutableTreeTableNode)node;
		NodeIndex index = (NodeIndex)treeNode.getUserObject();
		Boolean selection = (Boolean)value;
		if(selection.booleanValue())
		{
			// 选择
			mapSelection.put(index, selection);
			// 组选择递归
			if(index.getType() == UserUtil.NODE_GROUP)
			{
				// 选择用户组
				Enumeration<? extends MutableTreeTableNode> children = treeNode.children();
				while(children.hasMoreElements())
				{
					DefaultMutableTreeTableNode child = (DefaultMutableTreeTableNode)children.nextElement();
					NodeIndex childIndex = (NodeIndex)child.getUserObject();
					mapSelection.put(childIndex, selection);
					modelSupport.firePathChanged(new TreePath(getPathToRoot(child)));
				}
			}
			else if(index.getType() == UserUtil.NODE_USER)
			{
				// 选择用户
				DefaultMutableTreeTableNode parentNode = (DefaultMutableTreeTableNode)treeNode.getParent();
				NodeIndex parentIndex = (NodeIndex)parentNode.getUserObject();
				if(!mapSelection.containsKey(parentIndex))
				{
					// 所有子节点都选择时自动选择父节点
					boolean selected = true;
					Enumeration<? extends MutableTreeTableNode> children = parentNode.children();
					while(children.hasMoreElements())
					{
						DefaultMutableTreeTableNode child = (DefaultMutableTreeTableNode)children.nextElement();
						NodeIndex childIndex = (NodeIndex)child.getUserObject();
						selected &= mapSelection.containsKey(childIndex);
						if(!selected)
						{
							break;
						}
					}
					if(selected)
					{
						mapSelection.put(parentIndex, selected);
						modelSupport.firePathChanged(new TreePath(getPathToRoot(parentNode)));
					}
				}
			}
		}
		else
		{
			// 取消选择
			mapSelection.remove(index);
			// 组选择递归
			if(index.getType() == UserUtil.NODE_GROUP)
			{
				Enumeration<? extends MutableTreeTableNode> children = treeNode.children();
				while(children.hasMoreElements())
				{
					DefaultMutableTreeTableNode child = (DefaultMutableTreeTableNode)children.nextElement();
					NodeIndex childIndex = (NodeIndex)child.getUserObject();
					mapSelection.remove(childIndex);
					modelSupport.firePathChanged(new TreePath(getPathToRoot(child)));
				}
			}
			else if(index.getType() == UserUtil.NODE_USER)
			{
				// 取消选择用户
				DefaultMutableTreeTableNode parentNode = (DefaultMutableTreeTableNode)treeNode.getParent();
				NodeIndex parentIndex = (NodeIndex)parentNode.getUserObject();
				if(mapSelection.containsKey(parentIndex))
				{
					// 所有子节点都取消选择时自动取消父节点的选择
					boolean selected = false;
					Enumeration<? extends MutableTreeTableNode> children = parentNode.children();
					while(children.hasMoreElements())
					{
						DefaultMutableTreeTableNode child = (DefaultMutableTreeTableNode)children.nextElement();
						NodeIndex childIndex = (NodeIndex)child.getUserObject();
						selected |= mapSelection.containsKey(childIndex);
						if(selected)
						{
							break;
						}
					}
					if(!selected)
					{
						mapSelection.remove(parentIndex);
						modelSupport.firePathChanged(new TreePath(getPathToRoot(parentNode)));
					}
				}
			}
		}
		modelSupport.firePathChanged(new TreePath(getPathToRoot(treeNode)));
	}
}
