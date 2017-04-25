package com.hongxin.omc.ui.user;

import com.hongxin.omc.param.protocol.MMLCommand;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.tree.TreePath;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;

/**
 * 用户权限treetablemodel实现
 * @author liming
 * Created on 2009-11-12, 17:24:30
 */
public class UserMMLTreeTableModel extends DefaultTreeTableModel
{
	/**
	 * 用户权限表列名
	 */
	private List<String> columns;
	/**
	 *
	 */
	private Map<DefaultMutableTreeTableNode, Boolean> dictMMLPurviewData;

	/**
	 * 初始化模型
	 * @param root 根节点
	 */
	public UserMMLTreeTableModel(DefaultMutableTreeTableNode root,
			List<String> columns, Map dictMMLPurviewData)
	{
		super(root);
		this.columns = columns;
		this.dictMMLPurviewData = dictMMLPurviewData;
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
		//只设置操作列
		DefaultMutableTreeTableNode itemNode =
				(DefaultMutableTreeTableNode)node;
		if(itemNode.getUserObject() instanceof MMLCommand && column == 1)
		{
			dictMMLPurviewData.put(itemNode, (Boolean)value);
			selectItemAll(itemNode, (Boolean)value);
		}
	}

	/**
	 * 全选
	 * @param node
	 * @param isSelected
	 */
	public void selectItemAll(final DefaultMutableTreeTableNode node,
			final boolean isSelected)
	{
		com.hongxin.util.HxSwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				Enumeration<? extends MutableTreeTableNode> children = node.
						children();
				while(children.hasMoreElements())
				{
					DefaultMutableTreeTableNode child =
							(DefaultMutableTreeTableNode)children.nextElement();
					// 设置子节点选择状态
					dictMMLPurviewData.put(child, (Boolean)isSelected);
					modelSupport.firePathChanged(new TreePath(getPathToRoot(child)));
					// 递归选择子节点
					selectItemAll(child, isSelected);
				}
			}
		});

	}

	/**
	 * 将权限记录显示在列表中
	 * @param node 节点
	 * @param column 列数
	 * @return 返回每条记录的值
	 */
	@Override
	public Object getValueAt(Object node,
			int column)
	{
		//获得节点
		DefaultMutableTreeTableNode itemNode =
				(DefaultMutableTreeTableNode)node;

		//判断对象是否为MMLCommand
		if(itemNode.getUserObject() instanceof MMLCommand && column == 0)
		{
			//赋值
			MMLCommand item = (MMLCommand)itemNode.getUserObject();
			return item.getNativeName();
		}
		//判断是否为MMl
		if(itemNode.getUserObject() instanceof MMLCommand && column == 1)
		{
			MMLCommand item = (MMLCommand)itemNode.getUserObject();
			return dictMMLPurviewData.get(itemNode);
		}

		return itemNode.getUserObject();
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
	 * 返回列类型
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
	 * 初始化树模型。由外部择时调用。
	 * @param listCommand MML命令列表
	 */
	public void initTreeModel(List<MMLCommand> listCommand)
	{
		createTreeModel(listCommand);
		com.hongxin.util.HxSwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				// 通知模型更改
				//nodeChanged((TreeNode)getRoot());
			}
		});
	}

	/**
	 * 获取树节点对应的节点信息对象。
	 * @param treeNode 树节点
	 * @return 节点信息对象
	 */
	public DefaultMutableTreeTableNode getTreeNodeInfo(Object treeNode)
	{
		DefaultMutableTreeTableNode node =
				(DefaultMutableTreeTableNode)treeNode;
		//节点不为空
		if(node != null)
		{
			return (DefaultMutableTreeTableNode)node.getUserObject();
		}
		return null;
	}

	/**
	 * 创建树模型。内部调用。
	 * @param listCommand MML命令列表
	 */
	private void createTreeModel(List<MMLCommand> listCommand)
	{
		// 分组节点缓存
		Map<String, DefaultMutableTreeTableNode> mapTreeNode =
				new HashMap<String, DefaultMutableTreeTableNode>();
		// 遍历MML命令集合
		for(MMLCommand command : listCommand)
		{
			// 先确保分组节点已经创建
			createGroupNode(command.getGroups(), mapTreeNode);
			// 在分组节点下创建MML命令节点
			if(command.getGroups().size() > 0 && mapTreeNode.containsKey(command.
					getGroups().get(command.getGroups().size() - 1)))
			{
				// 新建MML命令节点
				DefaultMutableTreeTableNode parent =
						mapTreeNode.get(command.getGroups().get(command.
						getGroups().size() - 1));
				parent.add(new DefaultMutableTreeTableNode(command));
			}
		}
		// 清理缓存
		mapTreeNode.clear();
	}

	/**
	 * 创建分组节点。
	 * @param listGroup 分组名称列表
	 * @param mapTreeNode 分组树节点缓存
	 */
	private void createGroupNode(List<String> listGroup,
			Map<String, DefaultMutableTreeTableNode> mapTreeNode)
	{
		for(int i = 0; i < listGroup.size(); i++)
		{
			String group = listGroup.get(i);
			// 当前分组节点是否已经存在
			if(mapTreeNode.containsKey(group))
			{
				continue;
			}
			// 获取父节点
			DefaultMutableTreeTableNode parent =
					(i == 0) ? (DefaultMutableTreeTableNode)getRoot() : mapTreeNode.
					get(listGroup.get(i - 1));
			if(parent == null)
			{
				break;
			}
			// 创建子节点
			DefaultMutableTreeTableNode child =
					new DefaultMutableTreeTableNode(new DefaultMutableTreeTableNode(group));
			parent.add(child);
			// 记录到缓存
			mapTreeNode.put(group, child);
		}
	}

	/**
	 * 选择所有的
	 * @param isSelected
	 */
	public void selectAll(boolean isSelected)
	{
		for(Map.Entry entry : dictMMLPurviewData.entrySet())
		{
			DefaultMutableTreeTableNode key =
					(DefaultMutableTreeTableNode)entry.getKey();
			// 设置选择
			dictMMLPurviewData.put(key, (Boolean)isSelected);
			modelSupport.firePathChanged(new TreePath(getPathToRoot(key)));
		}
	}
}
