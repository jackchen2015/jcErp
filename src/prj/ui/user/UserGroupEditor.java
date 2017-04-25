/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * UserGroupEditor.java
 *
 * Created on 2009-8-18, 13:52:29
 */
package com.hongxin.omc.ui.user;

import com.hongxin.component.ComponentUtil;
import com.hongxin.component.export.AbstractRecordSet;
import com.hongxin.component.renderer.ConverterRenderer;
import com.hongxin.omc.ui.renderer.TreeCheckNode;
import com.hongxin.omc.protocol.District;
import com.hongxin.omc.ui.services.ConfigService;
import com.hongxin.omc.ui.services.ServiceConstants;
import com.hongxin.omc.ui.renderer.TreeCheckRenderer;
import com.hongxin.util.service.ServiceUtils;
import java.awt.Component;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import com.hongxin.omc.operation.Command;
import com.hongxin.omc.operation.OmcProcessor;
import com.hongxin.omc.param.ParamDictionary;
import com.hongxin.omc.param.protocol.MMLCommand;
import com.hongxin.omc.param.protocol.Tr069Command;
import com.hongxin.speed.core.ProcessData;
import javax.swing.ListSelectionModel;
import java.util.HashMap;
import java.util.Map;
import com.hongxin.omc.util.OmcConstants;
import javax.swing.table.AbstractTableModel;
import com.hongxin.omc.protocol.Device;
import com.hongxin.omc.protocol.DeviceId;
import com.hongxin.omc.ui.common.DeviceQueryPanel;
import com.hongxin.omc.ui.converter.DistrictIdConverter;
import com.hongxin.omc.ui.export.DefaultDataExportTask;
import com.hongxin.omc.ui.renderer.DeviceFilterRenderer;
import com.hongxin.omc.ui.renderer.DistrictCellRenderer;
import com.hongxin.omc.ui.renderer.HierarchicalTreeCellRenderer;
import com.hongxin.omc.ui.user.converter.DeviceGroupConverter;
import com.hongxin.omc.ui.user.converter.UserGroupLevelConverter;
import com.hongxin.omc.ui.user.converter.UserGroupTypeConverter;
import com.hongxin.omc.ui.util.GUIUtil;
import com.hongxin.omc.ui.validator.ElementNameValidator;
import com.hongxin.omc.user.protocol.DeviceGroup;
import com.hongxin.omc.user.protocol.Role;
import com.hongxin.omc.user.protocol.UserGroup;
import com.hongxin.omc.util.OmcDictionary;
import com.hongxin.speed.core.ProcessCallback;
import com.hongxin.util.GUIUtils;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;
import org.jdesktop.beansbinding.Validator;
import org.jdesktop.swingx.JXTreeTable;

/**
 * 用户组编辑界面，完成新增/修改用户组权限等信息的基本操作
 *
 * @author liming
 */
public class UserGroupEditor extends javax.swing.JDialog
{
	/**
	 * 资源文件对象
	 */
	private ResourceMap rm;
	/**
	 * 用户组对象
	 */
	private UserGroup userGroupInfo;
	/**
	 * 用户信息处理状态
	 */
	public boolean isok = false;
	/**
	 * MML命令节点选择状态map。
	 */
	private Map<DefaultMutableTreeTableNode, Boolean> mapMmlSelection;
	/**
	 * Tr069命令节点选择状态map。
	 */
	private Map<DefaultMutableTreeTableNode, Boolean> mapTr069Selection;
	/**
	 * MML命令集合。
	 */
	private List<MMLCommand> listMmlCmd;
	/**
	 * Tr069命令集合。
	 */
	private List<Tr069Command> listTr069Cmd;
	/**
	 * 设备列表。
	 */
	private List<Integer> listDeviceId;
	/**
	 * 角色列表。
	 */
	private List<Integer> listSysRoleId;
	/**
	 * 角色信息列表。
	 */
	private List<Role> listRole;
	/**
	 * 编辑模式。
	 */
	private boolean editMode;
	/**
	 * 当前用户的最高类型。
	 */
	private int topType;
	/**
	 * 当前用户的最高等级。
	 */
	private int topLevel;

	/**
	 * 初始化用户组编辑界面
	 *
	 * @param parentNode 顶层窗口对象
	 * @param modal 窗口模式 true:模式窗口；false:非模式窗口
	 * @param userGroup 用户组对象
	 * @param topType 当前操作用户的最高类型
	 * @param topLevel 当前操作用户的最高等级
	 */
	public UserGroupEditor(java.awt.Frame parent, boolean modal,
			List<Role> listRole, UserGroup userGroup, int topType, int topLevel)
	{
		super(parent, modal);
		rm = Application.getInstance().getContext().getResourceMap(UserGroupEditor.class);
		userGroupInfo = userGroup;
		// 清空数据
		userGroupInfo.getDevices().clear();
		userGroupInfo.getListRoleId().clear();
		userGroupInfo.getListGroupId().clear();
		userGroupInfo.getMMLCommands().clear();
		this.listRole = listRole;
		listDeviceId = new ArrayList();
		listSysRoleId = new ArrayList();
		isok = false;
		editMode = userGroupInfo.getId() > -1;
		this.topLevel = topLevel;
		this.topType = topType;
		initComponents();
		// 初始化操作
		initialize();
	}

	/**
	 * 初始化用户信息
	 */
	private void initialize()
	{
		GUIUtils.addHideAction(UserGroupEditor.this);
		getRootPane().setDefaultButton(btnOk);
		// 初始化下拉列表
		for(Integer groupType : UserGroupTypeConverter.instance.getListSValue())
		{
			// 根据当前操作用户的最高类型控制列表项
			if(groupType <= topType)
			{
				cmbGroupType.addItem(groupType);
			}
		}
		for(Integer groupLevel : UserGroupLevelConverter.instance.getListSValue())
		{
			// 根据当前操作用户的最高等级控制列表项
			if(groupLevel <= topLevel)
			{
				cmbGroupLevel.addItem(groupLevel);
			}
		}
		// 根据操作类型，设置窗体标题
		if(userGroupInfo.getId() > -1)
		{
			this.setTitle(rm.getString("edit.title"));
			// 顶级用户组和预置用户组不允许编辑
			btnOk.setEnabled(userGroupInfo.getGroupType() != UserGroup.TYPE_TOP
								&& userGroupInfo.getGroupType() != UserGroup.TYPE_PREP);
			// 用户组名
			tfGroupName.setText(userGroupInfo.getName());
			// 用户组描述
			tfDescription.setText(userGroupInfo.getDescription());
			// 用户组类型
			if(userGroupInfo.getGroupType() == UserGroup.TYPE_PREP)
			{
				// 预置用户组
				cmbGroupType.addItem(UserGroup.TYPE_PREP);
				cmbGroupType.setSelectedItem(UserGroup.TYPE_PREP);
			}
			else if(userGroupInfo.getGroupType() > UserGroup.TYPE_SUPER)
			{
				// 顶级用户组显示为超级用户组
				cmbGroupType.setSelectedItem(UserGroup.TYPE_SUPER);
			}
			else
			{
				cmbGroupType.setSelectedItem(userGroupInfo.getGroupType());
			}
			// 用户组等级
			cmbGroupLevel.setSelectedItem(userGroupInfo.getGroupLevel());
			// 用户总数
			spnUserCount.setValue(userGroupInfo.getUserCount());
			// 获取用户组详细信息
			ProcessData out = OmcProcessor.process(Command.user,
					Command.getUserGroupDetails, userGroupInfo.getId());
			UserGroup userGroup = (UserGroup)out.getData();
			// 不能使用copy方法
			//userGroupInfo.copyFrom(userGroup);
			userGroupInfo.setDevices(userGroup.getDevices());
			userGroupInfo.setListRoleId(userGroup.getListRoleId());
			userGroupInfo.setMMLCommands(userGroup.getMMLCommands());
			userGroupInfo.setListGroupId(userGroup.getListGroupId());
		}
		else
		{
			// 设置标题，新建用户组
			this.setTitle(rm.getString("new.title"));
		}
		// 初始化所有设备组
		initDeviceGroup();
		// 初始化根子网
		initRootDistrict();
		// 初始化角色
		initSysRole();
		// 获取MML命令权限列表
		initMmlPurviewData();
		// 获取TR069命令权限列表
		initTr069PurviewData();
		// 获取设备权限列表
		initDevicePurview();
	}

	/**
	 * 初始化设备组。
	 */
	private void initDeviceGroup()
	{
		// 获取设备组集合
		ProcessData out = OmcProcessor.process(Command.user, 
				Command.getAllDeviceGroup, null);
		if(out.getData() != null)
		{
			((DeviceGroupTableModel)tableDeviceGroup.getModel()).
					initModel((List)out.getData());
		}
		// 设备组
		List<Integer> listGroupId = userGroupInfo.getListGroupId();
		if(listGroupId != null && !listGroupId.isEmpty())
		{
			// 设置设备组选中状态
			((DeviceGroupTableModel)tableDeviceGroup.getModel()).
					setSelection(listGroupId);
		}
	}
	
	/**
	 * 初始化根子网,选择根子网。
	 */
	private void initRootDistrict()
	{
		// 查找根子网
		Set<Integer> listRoot = new HashSet();
		ConfigService service =	(ConfigService)ServiceUtils.getInstance().getService(ServiceConstants.SVC_CONFIG);
		for(Integer districtId : service.getDistrictCacheKeys())
		{
			District district = service.findDistrict(districtId);
			if(district != null)
			{
				if(service.findDistrict(district.getParentId()) == null)
				{
					listRoot.add(district.getId());
				}
			}
		}
		((DefaultListModel)listRootDistrict.getModel()).removeAllElements();
		for(Integer rootId : listRoot)
		{
			((DefaultListModel)listRootDistrict.getModel()).addElement(rootId);
		}
		// 设置选择状态
		for(Integer rootId : userGroupInfo.getDistricts())
		{
			if(!listRoot.contains(rootId))
			{
				((DefaultListModel)listRootDistrict.getModel()).addElement(rootId);
			}
			listRootDistrict.addCheckBoxListSelectedValue(rootId, false);
		}
		listRootDistrict.setCellRenderer(new DistrictCellRenderer());
	}

	/**
	 * 获得MML命令的权限列表
	 *
	 * @return mmlCmdsList MML命令的权限列表
	 */
	private List<Integer> getMmlPurview()
	{
		List<Integer> mmlCmdsList = new ArrayList();
		for(Map.Entry entry : mapMmlSelection.entrySet())
		{
			if((Boolean)entry.getValue() == true)
			{
				DefaultMutableTreeTableNode item =
						(DefaultMutableTreeTableNode)entry.getKey();
				MMLCommand cmd = (MMLCommand)item.getUserObject();
				//添加到列表中
				mmlCmdsList.add(cmd.getId());
			}
		}
		if(mapTr069Selection != null && !mapTr069Selection.isEmpty())
		{
			for(Map.Entry entry : mapTr069Selection.entrySet())
			{
				if((Boolean)entry.getValue() == true)
				{
					DefaultMutableTreeTableNode item =
							(DefaultMutableTreeTableNode)entry.getKey();
					Tr069Command cmd = (Tr069Command)item.getUserObject();
					//添加到列表中
					mmlCmdsList.add(cmd.getCommandId());
				}
			}
		}
		return mmlCmdsList;
	}

	/**
	 * 获得设备权限列表
	 *
	 * @return devList 设备权限列表
	 */
	private List<Integer> getDevicePurview()
	{
		return ((DevModel)tableDevice.getModel()).getSelectedDevice();
	}

	/**
	 * 获得角色列表。
	 * @return 角色列表
	 */
	private List<Integer> getListSysRole()
	{
		return listSysRoleId;
	}

	/**
	 * 初始化角色。
	 */
	private boolean initSysRole()
	{
		// 基础权限列表
		sysRoleFuncTree.initModel();
		// 用户组所属角色
		listSysRoleId.addAll(userGroupInfo.getListRoleId());
		// 显示角色权限
		initSysRoleFunc();
		return true;
	}
	
	/**
	 * 初始化角色权限。
	 */
	private void initSysRoleFunc()
	{
		// 显示角色名称和明细
		if(listRole != null && !listRole.isEmpty() && !listSysRoleId.isEmpty())
		{
			// 显示角色名称
			for(Role role : listRole)
			{
				if(role.getId() == listSysRoleId.get(0))
				{
					mainTabbedPane.setTitleAt(mainTabbedPane.indexOfComponent(sysRolePanel), 
							rm.getString("msg.role.title", role.getName()));
					break;
				}
			}
			// 获取角色权限
			ProcessData out = OmcProcessor.process(Command.user, 
					Command.getSysRole,	listSysRoleId.get(0));
			if(out.getData() != null)
			{
				sysRoleFuncTree.setSelectedFunction((List)out.getData());
			}
		}
	}

	/**
	 * 获取MML命令权限项
	 */
	private boolean initMmlPurviewData()
	{
		// 从字典表对象中获取用户权限
		Set<Integer> modelIdSet = ParamDictionary.getInstance().
				cmd_getSupportedModelId();
		listMmlCmd = new ArrayList<MMLCommand>();
		// 遍历用户组权限的ID
		for(Integer modelId : modelIdSet)
		{
			// 获取MML命令
			List<MMLCommand> listModelCommand = ParamDictionary.getInstance().
					cmd_getAllMMLCommand(modelId);
			for(MMLCommand mml : listModelCommand)
			{
				// 是否已经存在
				if(!listMmlCmd.contains(mml))
				{
					listMmlCmd.add(mml);
				}
			}
			listModelCommand.clear();
		}
		// 获得用户权限分组
		List<List<String>> purviewGroup = getMMLGroup(listMmlCmd);
		// 获得权限表列名
		List<String> columns = getMmlTreeTableColumns();
		// 创建根节点
		DefaultMutableTreeTableNode root =
				new DefaultMutableTreeTableNode(rm.
				getString("tree.rootnode.mmlpurview"));
		// 创建节点数据对象
		mapMmlSelection = new HashMap();
		DefaultTreeTableModel purTableModel =
				new UserMMLTreeTableModel(root, columns, mapMmlSelection);
		// 根据权限分组，创建所有分组节点
		Map<String, DefaultMutableTreeTableNode> nodes =
				new HashMap<String, DefaultMutableTreeTableNode>();
		// 遍历所有分组
		StringBuilder sbd = new StringBuilder();
		for(int i = 0; i < purviewGroup.size(); i++)
		{
			sbd.delete(0, sbd.length());
			List<String> listGroupName = purviewGroup.get(i);
			for(int g = 0; g < listGroupName.size(); g++)
			{
				DefaultMutableTreeTableNode parentNode;
				if(g > 0)
				{
					parentNode = nodes.get(sbd.toString());
					sbd.append(",");
				}
				else
				{
					parentNode = root;
				}
				sbd.append(listGroupName.get(g));
				DefaultMutableTreeTableNode groupNode = nodes.get(sbd.toString());
				if(groupNode == null)
				{
					// 创建分组节点
					MMLCommand groupObject = new MMLCommand();
					groupObject.setNativeName(listGroupName.get(g));
					groupNode = new DefaultMutableTreeTableNode(groupObject);
					purTableModel.insertNodeInto(groupNode, parentNode, parentNode.getChildCount());
					nodes.put(sbd.toString(), groupNode);
				}
			}
		}
		// 遍历mml命令
		for(MMLCommand cmd : listMmlCmd)
		{
			sbd.delete(0, sbd.length());
			for(int i = 0; i < cmd.getGroups().size(); i++)
			{
				if(sbd.length() > 0)
				{
					sbd.append(",");
				}
				sbd.append(cmd.getGroups().get(i));
			}
			DefaultMutableTreeTableNode parentNode = nodes.get(sbd.toString());
			DefaultMutableTreeTableNode cmdNode = new DefaultMutableTreeTableNode(cmd);
			// 如果MMl被选中，则插入节点到缓存
			if(isMMLSelected(cmd))
			{
				mapMmlSelection.put(cmdNode, true);
			}
			else
			{
				mapMmlSelection.put(cmdNode, false);
			}
			purTableModel.insertNodeInto(cmdNode, parentNode, parentNode.getChildCount());
		}
		treeTableCommand.setTreeTableModel(purTableModel);
		// 设置用户表的各项属性
		// 不可编辑
		treeTableCommand.setEditable(true);
		// 展开所有节点
		treeTableCommand.expandAll();
		// 设置选择模式为单一选择
		treeTableCommand.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// 创建表格渲染器
		treeTableCommand.setTreeCellRenderer(new MMLTreeCellRenderer(treeTableCommand));
		return true;
	}
	
	/**
	 * 获取Tr069命令权限项
	 */
	private boolean initTr069PurviewData()
	{
		listTr069Cmd = ParamDictionary.getInstance().cmd_getAllTr069Command();
		if(listTr069Cmd == null || listTr069Cmd.isEmpty())
		{
			mainTabbedPane.remove(tr069PurviewPanel);
			return true;
		}
		// 获得命令表列名
		List<String> columns = getMmlTreeTableColumns();
		// 创建根节点
		DefaultMutableTreeTableNode root =
				new DefaultMutableTreeTableNode(rm.
				getString("tree.rootnode.mmlpurview"));
		// 创建节点数据对象
		mapTr069Selection = new HashMap();
		DefaultTreeTableModel purTableModel
				= new UserTr069TreeTableModel(root, columns, mapTr069Selection);
		treeTableTr069Command.setTreeTableModel(purTableModel);
//		((UserTr069TreeTableModel)purTableModel).initTreeModel(listTr069Cmd);
		for(Tr069Command command : listTr069Cmd)
		{
			DefaultMutableTreeTableNode parentNode = (DefaultMutableTreeTableNode)((DefaultTreeTableModel)purTableModel).getRoot();
			DefaultMutableTreeTableNode cmdNode = new DefaultMutableTreeTableNode(command);
			if(isTr069Selected(command))
			{
				mapTr069Selection.put(cmdNode, true);
				purTableModel.insertNodeInto(cmdNode, parentNode, parentNode.getChildCount());
			}
			else
			{
				mapTr069Selection.put(cmdNode, false);
				purTableModel.insertNodeInto(cmdNode, parentNode, parentNode.getChildCount());
			}
		}		
		// 设置用户表的各项属性
		// 不可编辑
		treeTableTr069Command.setEditable(true);
		// 展开所有节点
		treeTableTr069Command.expandAll();
		// 设置选择模式为单一选择
		treeTableTr069Command.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// 创建表格渲染器
		treeTableTr069Command.setTreeCellRenderer(new Tr069TreeCellRenderer(treeTableTr069Command));
		return true;
	}

	/**
	 * 获取设备权限。
	 */
	private void initDevicePurview()
	{
		listDeviceId.addAll(userGroupInfo.getDevices());
		((DevModel)tableDevice.getModel()).initModel(userGroupInfo.getDevices());
		((DevModel)tableDevice.getModel()).selectAll(true, true);
		// 设置设备总数
		lblCountValue.setText(String.valueOf(userGroupInfo.getDevices().size()));
	}
	
	// <editor-fold defaultstate="collapsed" desc="渲染器">
	/**
	 * MML命令树节点渲染器。
	 */
	private class MMLTreeCellRenderer extends HierarchicalTreeCellRenderer
	{
		public MMLTreeCellRenderer(JXTreeTable treeTable)
		{
			super(treeTable);
		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus)
		{
			Component comp = super.
					getTreeCellRendererComponent(tree, value, sel,
					expanded, leaf, row, hasFocus);
			DefaultMutableTreeTableNode ttn = (DefaultMutableTreeTableNode)value;
			if(ttn.isLeaf())
			{
				setIcon(Application.getInstance().getContext().getResourceMap().
						getIcon("image.command"));
			}
			else
			{
				setIcon(Application.getInstance().getContext().getResourceMap().
						getIcon("image.groupAll"));
			}
			return comp;
		}
	}
	
	/**
	 * Tr069命令树节点渲染器。
	 */
	private class Tr069TreeCellRenderer extends HierarchicalTreeCellRenderer
	{
		public Tr069TreeCellRenderer(JXTreeTable treeTable)
		{
			super(treeTable);
		}

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus)
		{
			Component comp = super.
					getTreeCellRendererComponent(tree, value, sel,
					expanded, leaf, row, hasFocus);
			DefaultMutableTreeTableNode ttn = (DefaultMutableTreeTableNode)value;
			if(ttn.isLeaf())
			{
				setIcon(Application.getInstance().getContext().getResourceMap().
						getIcon("image.command"));
			}
			else
			{
				setIcon(Application.getInstance().getContext().getResourceMap().
						getIcon("image.groupAll"));
			}
			return comp;
		}
	}	
	// </editor-fold>

	/**
	 * 检查mml命令是否选中。
	 * @param item mml命令对象
	 * @return 检查结果
	 */
	private boolean isMMLSelected(MMLCommand item)
	{
		return userGroupInfo.getMMLCommands().indexOf(item.getId()) != -1;
	}
	
	/**
	 * 检查Tr069命令是否选中。
	 * @param item mml命令对象
	 * @return 检查结果
	 */
	private boolean isTr069Selected(Tr069Command item)
	{
		return userGroupInfo.getMMLCommands().indexOf(item.getCommandId()) != -1;
	}	

	/**
	 * 获得MML权限分组
	 *
	 * @param userPurviewItems 用户权限列表
	 * @return 用户权限组
	 */
	private List<List<String>> getMMLGroup(List<MMLCommand> userMMLCommands)
	{
		List<List<String>> purviewGroup = new ArrayList<List<String>>();
		HashMap groupMap = new HashMap();
		//遍历MML命令，如果找不到对应的节点，则追加新的
		for(MMLCommand dictSysItem : userMMLCommands)
		{
			if(groupMap.get(dictSysItem.getGroups()) == null)
			{
				purviewGroup.add(dictSysItem.getGroups());
				groupMap.put(dictSysItem.getGroups(), dictSysItem.getId());
			}
		}
		groupMap.clear();
		return purviewGroup;
	}

	/**
	 * 获得mml权限表列名。
	 * @return 权限表列名
	 */
	private List<String> getMmlTreeTableColumns()
	{
		List<String> columns = new ArrayList(2);
		columns.add(rm.getString("tree.mml.cols.name"));
		columns.add(rm.getString("tree.mml.cols.selection"));
		return columns;
	}
	
	/**
	 * 用户组记录集访问接口。
	 */
	private class UserGroupRecordSet extends AbstractRecordSet
	{
		/**
		 * 用户组数据源。
		 */
		private UserGroup source;
		/**
		 * 角色权限数据源。
		 */
		private List<Integer> roleSource;
		/**
		 * 设备组数据源。
		 */
		private List<DeviceGroup> deviceGroupSource;
		/**
		 * 命令数据源。
		 */
		private List<MMLCommand> cmdSource;
		/**
		 * 记录索引指示器。
		 */
		private int indicator = -1;
		
		UserGroupRecordSet(UserGroup source, List<Integer> roleSource, 
				List<DeviceGroup> deviceGroupSource, List<MMLCommand> cmdSource)
		{
			this.source = source;
			this.roleSource = roleSource;
			this.deviceGroupSource = deviceGroupSource;
			this.cmdSource = cmdSource;
		}
		
		@Override
		public boolean next()
		{
			// 计算各种权限记录行数
			int count = roleSource.size();
			count += source.getMMLCommands().size();
			count += source.getListGroupId().size();
			count += source.getDevices().size();
			count += source.getDistricts().size();
			if(indicator == -1)
			{
				indicator += 1;
				return indicator < count + 4;
			}
			else
			{
				if(indicator < count + 4)
				{
					if(indicator == count + 3)
					{
						return false;
					}
					else
					{
						indicator += 1;
						return true;
					}
				}
			}
			return false;
		}
		
		/**
		 * 获取指定设备组。
		 * @param groupId 设备组id
		 * @return 设备组对象
		 */
		public DeviceGroup getDeviceGroup(int groupId)
		{
			if(deviceGroupSource != null && !deviceGroupSource.isEmpty())
			{
				for(DeviceGroup group : deviceGroupSource)
				{
					if(group.getId() == groupId)
					{
						return group;
					}
				}
			}
			return null;
		}
		
		/**
		 * 获取指定命令对象。
		 * @param cmdId 命令id
		 * @return 命令对象
		 */
		public MMLCommand getMmlCommand(int cmdId)
		{
			if(cmdSource != null && !cmdSource.isEmpty())
			{
				for(MMLCommand cmd : cmdSource)
				{
					if(cmd.getId() == cmdId)
					{
						return cmd;
					}
				}
			}
			return null;
		}

		@Override
		public Object getRecord()
		{
			if(indicator == 0)
			{
				// 名称
				return new String[] { rm.getString("msg.group.name"), source.getName() };
			}
			else if(indicator == 1)
			{
				// 描述
				return new String[] { rm.getString("msg.group.desc"), source.getDescription() };
			}
			else if(indicator == 2)
			{
				// 类型
				return new String[] { rm.getString("msg.group.type"), 
					UserGroupTypeConverter.instance.convertForward(source.getGroupType()) };
			}
			else if(indicator == 3)
			{
				// 等级
				return new String[] { rm.getString("msg.group.level"), 
					UserGroupLevelConverter.instance.convertForward(source.getGroupLevel()) };
			}
			else
			{
				// 各种权限列表
				// 区分权限类别
				int index = indicator - 4;
				if(index >= 0 && index < roleSource.size())
				{
					// 角色权限
					int func = roleSource.get(index);
					String funcName = String.format("%s/%s", 
							OmcDictionary.getInstance().sys_getItemAlias(func),
							OmcDictionary.getInstance().sys_getNameById(func));
					return new String[] { rm.getString("msg.func.role"), funcName };
				}
				index -= roleSource.size();
				if(index >= 0 && index < source.getMMLCommands().size())
				{
					// 命令权限
					MMLCommand cmd = getMmlCommand(source.getMMLCommands().get(index));
					return new String[] { rm.getString("msg.func.cmd"), 
					cmd != null ? cmd.getNativeName() : "----" };
				}
				index -= source.getMMLCommands().size();
				if(index >= 0 && index < source.getListGroupId().size() + source.getDevices().size())
				{
					// 设备权限，区分设备和设备组
					if(index >= 0 && index < source.getListGroupId().size())
					{
						// 设备组
						DeviceGroup group = getDeviceGroup(source.getListGroupId().get(index));
						return new String[] { rm.getString("msg.func.group"), 
						group != null ? DeviceGroupConverter.instance.convertForward(group) : "----" };
					}
					index -= source.getListGroupId().size();
					if(index >= 0 && index < source.getDevices().size())
					{
						// 设备
						ConfigService service =
								(ConfigService)ServiceUtils.getInstance().getService(ServiceConstants.SVC_CONFIG);
						Device device = service.findBBU(source.getDevices().get(index));
						return new String[] { rm.getString("msg.func.device"), 
						device != null ? device.getNumber() : "----" };
					}
					index -= source.getDevices().size();
				}
				else
				{
					index -= source.getListGroupId().size();
					index -= source.getDevices().size();
				}
				if(index >= 0 && index < source.getDistricts().size())
				{
					// 根子网权限
					String districtName = DistrictIdConverter.instance.convertForward(source.getDistricts().get(index));
					return new String[] { rm.getString("msg.func.district"), 
					districtName != null ? districtName : "----" };
				}
				return null;
			}
		}

		@Override
		public void moveFirst()
		{
			// 重置记录索引指示器
			indicator = -1;
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        cbSelectAllFunction = new javax.swing.JCheckBox();
        functionTableScrollPane = new javax.swing.JScrollPane();
        treeTableFunction = ComponentUtil.createTreeTable(true, false);
        cbCheckAll = new javax.swing.JCheckBox();
        mainPanel = new javax.swing.JPanel();
        lblGroupName = new javax.swing.JLabel();
        tfGroupName = new javax.swing.JTextField();
        lblDescription = new javax.swing.JLabel();
        tfDescription = new javax.swing.JTextField();
        lblGroupType = new javax.swing.JLabel();
        cmbGroupType = new javax.swing.JComboBox();
        lblGroupLevel = new javax.swing.JLabel();
        cmbGroupLevel = new javax.swing.JComboBox();
        mainTabbedPane = new javax.swing.JTabbedPane();
        sysRolePanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        sysRoleFuncTree = new com.hongxin.omc.ui.user.SysRoleFuncTree();
        btnSelectRole = new javax.swing.JButton();
        commandPurviewPanel = new javax.swing.JPanel();
        commandTableScrollPane = new javax.swing.JScrollPane();
        treeTableCommand = ComponentUtil.createTreeTable(true, false);
        cbSelectAllCommand = new javax.swing.JCheckBox();
        tr069PurviewPanel = new javax.swing.JPanel();
        commandTableScrollPane1 = new javax.swing.JScrollPane();
        treeTableTr069Command = ComponentUtil.createTreeTable(true, false);
        cbSelectAllTr069Command = new javax.swing.JCheckBox();
        devicePurviewPanel = new javax.swing.JPanel();
        tableGroupScrollPane = new javax.swing.JScrollPane();
        tableDeviceGroup = GUIUtil.createTable();
        lblCount = new javax.swing.JLabel();
        lblCountValue = new javax.swing.JLabel();
        btnSelectDevice = new javax.swing.JButton();
        tableDeviceScrollPane = new javax.swing.JScrollPane();
        tableDevice = ComponentUtil.createTable(true, false);
        cbSelectAllDevice = new javax.swing.JCheckBox();
        rootDistrictPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        listRootDistrict = new com.jidesoft.swing.CheckBoxList();
        btnOk = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        btnExport = new javax.swing.JButton();
        lblUserCount = new javax.swing.JLabel();
        spnUserCount = new javax.swing.JSpinner();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(UserGroupEditor.class);
        cbSelectAllFunction.setText(resourceMap.getString("cbSelectAllFunction.text")); // NOI18N
        cbSelectAllFunction.setName("cbSelectAllFunction"); // NOI18N
        cbSelectAllFunction.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cbSelectAllFunctionActionPerformed(evt);
            }
        });

        functionTableScrollPane.setName("functionTableScrollPane"); // NOI18N

        treeTableFunction.setName("treeTableFunction"); // NOI18N
        functionTableScrollPane.setViewportView(treeTableFunction);

        cbCheckAll.setText(resourceMap.getString("cbCheckAll.text")); // NOI18N
        cbCheckAll.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbCheckAll.setName("cbCheckAll"); // NOI18N
        cbCheckAll.addItemListener(new java.awt.event.ItemListener()
        {
            public void itemStateChanged(java.awt.event.ItemEvent evt)
            {
                cbCheckAllItemStateChanged(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Dialog_UserGroupEditor_6"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosed(java.awt.event.WindowEvent evt)
            {
                formWindowClosed(evt);
            }
        });

        mainPanel.setName("mainPanel"); // NOI18N

        lblGroupName.setText(resourceMap.getString("lblGroupName.text")); // NOI18N
        lblGroupName.setName("lblGroupName"); // NOI18N

        tfGroupName.setName("tfGroupName"); // NOI18N
        tfGroupName.setPreferredSize(new java.awt.Dimension(250, 21));

        lblDescription.setText(resourceMap.getString("lblDescription.text")); // NOI18N
        lblDescription.setName("lblDescription"); // NOI18N

        tfDescription.setName("tfDescription"); // NOI18N
        tfDescription.setPreferredSize(new java.awt.Dimension(250, 21));

        lblGroupType.setText(resourceMap.getString("lblGroupType.text")); // NOI18N
        lblGroupType.setName("lblGroupType"); // NOI18N

        cmbGroupType.setName("cmbGroupType"); // NOI18N
        cmbGroupType.setRenderer(new ConverterRenderer(UserGroupTypeConverter.instance));

        lblGroupLevel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblGroupLevel.setText(resourceMap.getString("lblGroupLevel.text")); // NOI18N
        lblGroupLevel.setName("lblGroupLevel"); // NOI18N

        cmbGroupLevel.setName("cmbGroupLevel"); // NOI18N
        cmbGroupLevel.setRenderer(new ConverterRenderer(UserGroupLevelConverter.instance));

        mainTabbedPane.setName("mainTabbedPane"); // NOI18N

        sysRolePanel.setName("sysRolePanel"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        sysRoleFuncTree.setCheckBoxEnabled(false);
        sysRoleFuncTree.setName("sysRoleFuncTree"); // NOI18N
        sysRoleFuncTree.setRootVisible(false);
        jScrollPane2.setViewportView(sysRoleFuncTree);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(UserGroupEditor.class, this);
        btnSelectRole.setAction(actionMap.get("selectSysRole")); // NOI18N
        btnSelectRole.setMargin(new java.awt.Insets(2, 4, 2, 4));
        btnSelectRole.setName("btnSelectRole"); // NOI18N

        javax.swing.GroupLayout sysRolePanelLayout = new javax.swing.GroupLayout(sysRolePanel);
        sysRolePanel.setLayout(sysRolePanelLayout);
        sysRolePanelLayout.setHorizontalGroup(
            sysRolePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sysRolePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnSelectRole)
                .addContainerGap(454, Short.MAX_VALUE))
            .addGroup(sysRolePanelLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        sysRolePanelLayout.setVerticalGroup(
            sysRolePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sysRolePanelLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
                .addGap(16, 16, 16)
                .addComponent(btnSelectRole)
                .addGap(4, 4, 4))
        );

        mainTabbedPane.addTab(resourceMap.getString("sysRolePanel.TabConstraints.tabTitle"), sysRolePanel); // NOI18N

        commandPurviewPanel.setName("commandPurviewPanel"); // NOI18N

        commandTableScrollPane.setName("commandTableScrollPane"); // NOI18N

        treeTableCommand.setName("treeTableCommand"); // NOI18N
        commandTableScrollPane.setViewportView(treeTableCommand);

        cbSelectAllCommand.setText(resourceMap.getString("cbSelectAllCommand.text")); // NOI18N
        cbSelectAllCommand.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbSelectAllCommand.setName("cbSelectAllCommand"); // NOI18N
        cbSelectAllCommand.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cbSelectAllCommandActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout commandPurviewPanelLayout = new javax.swing.GroupLayout(commandPurviewPanel);
        commandPurviewPanel.setLayout(commandPurviewPanelLayout);
        commandPurviewPanelLayout.setHorizontalGroup(
            commandPurviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(commandPurviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(commandPurviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbSelectAllCommand)
                    .addComponent(commandTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 505, Short.MAX_VALUE))
                .addContainerGap())
        );
        commandPurviewPanelLayout.setVerticalGroup(
            commandPurviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(commandPurviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(commandTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbSelectAllCommand)
                .addGap(4, 4, 4))
        );

        mainTabbedPane.addTab(resourceMap.getString("commandPurviewPanel.TabConstraints.tabTitle"), commandPurviewPanel); // NOI18N

        tr069PurviewPanel.setName("tr069PurviewPanel"); // NOI18N

        commandTableScrollPane1.setName("commandTableScrollPane1"); // NOI18N

        treeTableTr069Command.setName("treeTableTr069Command"); // NOI18N
        commandTableScrollPane1.setViewportView(treeTableTr069Command);

        cbSelectAllTr069Command.setText(resourceMap.getString("cbSelectAllTr069Command.text")); // NOI18N
        cbSelectAllTr069Command.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbSelectAllTr069Command.setName("cbSelectAllTr069Command"); // NOI18N
        cbSelectAllTr069Command.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cbSelectAllTr069CommandActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout tr069PurviewPanelLayout = new javax.swing.GroupLayout(tr069PurviewPanel);
        tr069PurviewPanel.setLayout(tr069PurviewPanelLayout);
        tr069PurviewPanelLayout.setHorizontalGroup(
            tr069PurviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tr069PurviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tr069PurviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbSelectAllTr069Command)
                    .addComponent(commandTableScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 505, Short.MAX_VALUE))
                .addContainerGap())
        );
        tr069PurviewPanelLayout.setVerticalGroup(
            tr069PurviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tr069PurviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(commandTableScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbSelectAllTr069Command)
                .addGap(4, 4, 4))
        );

        mainTabbedPane.addTab(resourceMap.getString("tr069PurviewPanel.TabConstraints.tabTitle"), tr069PurviewPanel); // NOI18N

        devicePurviewPanel.setName("devicePurviewPanel"); // NOI18N

        tableGroupScrollPane.setName("tableGroupScrollPane"); // NOI18N

        tableDeviceGroup.setModel(new DeviceGroupTableModel());
        tableDeviceGroup.setName("tableDeviceGroup"); // NOI18N
        tableGroupScrollPane.setViewportView(tableDeviceGroup);
        // 渲染器
        tableDeviceGroup.getColumnModel().getColumn(2).setCellRenderer(new DeviceFilterRenderer());

        lblCount.setText(resourceMap.getString("lblCount.text")); // NOI18N
        lblCount.setMaximumSize(new java.awt.Dimension(42, 15));
        lblCount.setName("lblCount"); // NOI18N

        lblCountValue.setText(resourceMap.getString("lblCountValue.text")); // NOI18N
        lblCountValue.setMaximumSize(new java.awt.Dimension(42, 15));
        lblCountValue.setName("lblCountValue"); // NOI18N
        lblCountValue.setPreferredSize(new java.awt.Dimension(60, 15));

        btnSelectDevice.setAction(actionMap.get("queryDevice")); // NOI18N
        btnSelectDevice.setText(resourceMap.getString("btnSelectDevice.text")); // NOI18N
        btnSelectDevice.setName("btnSelectDevice"); // NOI18N

        tableDeviceScrollPane.setName("tableDeviceScrollPane"); // NOI18N

        tableDevice.setAutoCreateRowSorter(true);
        tableDevice.setModel(new DevModel());
        tableDevice.setName("tableDevice"); // NOI18N
        tableDeviceScrollPane.setViewportView(tableDevice);

        cbSelectAllDevice.setText(resourceMap.getString("cbSelectAllDevice.text")); // NOI18N
        cbSelectAllDevice.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbSelectAllDevice.setName("cbSelectAllDevice"); // NOI18N
        cbSelectAllDevice.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                cbSelectAllDeviceActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout devicePurviewPanelLayout = new javax.swing.GroupLayout(devicePurviewPanel);
        devicePurviewPanel.setLayout(devicePurviewPanelLayout);
        devicePurviewPanelLayout.setHorizontalGroup(
            devicePurviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(devicePurviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(devicePurviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbSelectAllDevice)
                    .addComponent(tableDeviceScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 505, Short.MAX_VALUE)
                    .addComponent(tableGroupScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(devicePurviewPanelLayout.createSequentialGroup()
                        .addComponent(lblCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblCountValue, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSelectDevice)))
                .addContainerGap())
        );
        devicePurviewPanelLayout.setVerticalGroup(
            devicePurviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(devicePurviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tableGroupScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(devicePurviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(btnSelectDevice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblCount, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblCountValue, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tableDeviceScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbSelectAllDevice)
                .addGap(4, 4, 4))
        );

        mainTabbedPane.addTab(resourceMap.getString("devicePurviewPanel.TabConstraints.tabTitle"), devicePurviewPanel); // NOI18N

        rootDistrictPanel.setName("rootDistrictPanel"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        listRootDistrict.setModel(new DefaultListModel());
        listRootDistrict.setName("listRootDistrict"); // NOI18N
        jScrollPane1.setViewportView(listRootDistrict);

        javax.swing.GroupLayout rootDistrictPanelLayout = new javax.swing.GroupLayout(rootDistrictPanel);
        rootDistrictPanel.setLayout(rootDistrictPanelLayout);
        rootDistrictPanelLayout.setHorizontalGroup(
            rootDistrictPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rootDistrictPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 505, Short.MAX_VALUE)
                .addContainerGap())
        );
        rootDistrictPanelLayout.setVerticalGroup(
            rootDistrictPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rootDistrictPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                .addContainerGap())
        );

        mainTabbedPane.addTab(resourceMap.getString("rootDistrictPanel.TabConstraints.tabTitle"), rootDistrictPanel); // NOI18N

        btnOk.setAction(actionMap.get("saveUserGroup")); // NOI18N
        btnOk.setName("btnOk"); // NOI18N

        btnCancel.setAction(actionMap.get("exit")); // NOI18N
        btnCancel.setText(resourceMap.getString("btnCancel.text")); // NOI18N
        btnCancel.setName("btnCancel"); // NOI18N

        btnExport.setAction(actionMap.get("export")); // NOI18N
        btnExport.setName("btnExport"); // NOI18N

        lblUserCount.setText(resourceMap.getString("lblUserCount.text")); // NOI18N
        lblUserCount.setName("lblUserCount"); // NOI18N

        spnUserCount.setModel(new javax.swing.SpinnerNumberModel(0, 0, 100, 1));
        spnUserCount.setName("spnUserCount"); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(lblGroupName, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
                        .addComponent(lblGroupType, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(lblUserCount))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spnUserCount, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbGroupType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tfGroupName, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDescription)
                    .addComponent(lblGroupLevel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tfDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                    .addComponent(cmbGroupLevel, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(mainTabbedPane))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnExport)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnOk)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancel)))
                .addGap(10, 10, 10))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblGroupName)
                    .addComponent(tfGroupName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDescription)
                    .addComponent(tfDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblGroupType)
                    .addComponent(cmbGroupType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblGroupLevel)
                    .addComponent(cmbGroupLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUserCount)
                    .addComponent(spnUserCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainTabbedPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel)
                    .addComponent(btnOk)
                    .addComponent(btnExport))
                .addContainerGap())
        );

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

	//窗口关闭时间
    private void formWindowClosed(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosed
    {//GEN-HEADEREND:event_formWindowClosed
		exit();
    }//GEN-LAST:event_formWindowClosed

	//全选获取取消所有权限
	private void cbSelectAllFunctionActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cbSelectAllFunctionActionPerformed
	{//GEN-HEADEREND:event_cbSelectAllFunctionActionPerformed
		// 全选
		((UserPurviewTreeTableModel)treeTableFunction.getTreeTableModel()).
				selectAll(cbSelectAllFunction.isSelected());
	}//GEN-LAST:event_cbSelectAllFunctionActionPerformed

	//全选获取取消所有命令
	private void cbSelectAllCommandActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cbSelectAllCommandActionPerformed
	{//GEN-HEADEREND:event_cbSelectAllCommandActionPerformed
		// 通知更新
		((UserMMLTreeTableModel)treeTableCommand.getTreeTableModel()).
						selectAll(cbSelectAllCommand.isSelected());
	}//GEN-LAST:event_cbSelectAllCommandActionPerformed

	//权限获取取消所有设备
	private void cbSelectAllDeviceActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cbSelectAllDeviceActionPerformed
	{//GEN-HEADEREND:event_cbSelectAllDeviceActionPerformed
		// 通知更新
		((DevModel)tableDevice.getModel()).selectAll(cbSelectAllDevice.isSelected(), true);
	}//GEN-LAST:event_cbSelectAllDeviceActionPerformed

    private void cbCheckAllItemStateChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_cbCheckAllItemStateChanged
    {//GEN-HEADEREND:event_cbCheckAllItemStateChanged
		if(cbCheckAll.isSelected())
		{
			listRootDistrict.selectAll();
		}
		else
		{
			listRootDistrict.selectNone();
		}
    }//GEN-LAST:event_cbCheckAllItemStateChanged

    private void cbSelectAllTr069CommandActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cbSelectAllTr069CommandActionPerformed
    {//GEN-HEADEREND:event_cbSelectAllTr069CommandActionPerformed
        ((UserTr069TreeTableModel)treeTableTr069Command.getTreeTableModel()).
						selectAll(cbSelectAllTr069Command.isSelected());
    }//GEN-LAST:event_cbSelectAllTr069CommandActionPerformed

	/**
	 * 保存
	 */
	@Action
	public void saveUserGroup()
	{
		// 用户组名称
		String userGroupName = tfGroupName.getText().trim();
		ElementNameValidator validator = new ElementNameValidator(false, 
				rm.getInteger("field.name.len.short"));
		Validator.Result result = validator.validate(userGroupName);
		if(result != null)
		{
			JOptionPane.showMessageDialog(this,
					rm.getString("msg.group.name.error", result.
					getDescription()),
					rm.getString("msg.error"),
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		final CyclicBarrier barrier = new CyclicBarrier(2);
		// 检查设备和根子网匹配状态
		final Set<Integer> rootDistrict = new HashSet();
		// 计算选择的设备组包含的根子网
		List<Integer> listGroupId = ((DeviceGroupTableModel)tableDeviceGroup.getModel()).getSelectedGroup();
		if(!listGroupId.isEmpty())
		{
			// 计算根子网
			try
			{
				barrier.reset();
				// 新增用户组操作
				OmcProcessor.process(Command.user, Command.getDeviceGroupRootDistrict,
						listGroupId, new ProcessCallback()
				{
					@Override
					public void processCompleted(final ProcessData out)
					{
						if(out.getData() != null)
						{
							rootDistrict.addAll((List)out.getData());
						}
						try
						{
							barrier.await(OmcConstants.sync_timeout, TimeUnit.MILLISECONDS);
						}
						catch(Exception exp)
						{}
					}
				});
				barrier.await(OmcConstants.sync_timeout, TimeUnit.MILLISECONDS);
			}
			catch(Exception exp)
			{}
		}
		// 计算设备列表包含的根子网
		List<Integer> listDevicePurview = getDevicePurview();
		if(!listDevicePurview.isEmpty())
		{
			// 计算根子网
			try
			{
				barrier.reset();
				// 新增用户组操作
				OmcProcessor.process(Command.commonSyn, Command.queryRootDistrict,
						listDevicePurview, new ProcessCallback()
				{
					@Override
					public void processCompleted(final ProcessData out)
					{
						if(out.getData() != null)
						{
							rootDistrict.addAll((List)out.getData());
						}
						try
						{
							barrier.await(OmcConstants.sync_timeout, TimeUnit.MILLISECONDS);
						}
						catch(Exception exp)
						{}
					}
				});
				barrier.await(OmcConstants.sync_timeout, TimeUnit.MILLISECONDS);
			}
			catch(Exception exp)
			{}
		}
		// 合并设备权限包含的根子网和根子网权限配置的根子网
		for(Integer rootId : rootDistrict)
		{
			if(!((DefaultListModel)listRootDistrict.getModel()).contains(rootId))
			{
				((DefaultListModel)listRootDistrict.getModel()).addElement(rootId);
			}
			listRootDistrict.addCheckBoxListSelectedValue(rootId, false);
		}
		// 检查根子网权限，至少选择一个
		int[] rows = listRootDistrict.getCheckBoxListSelectedIndices();
		if(rows.length == 0)
		{
			JOptionPane.showMessageDialog(this,
					rm.getString("msg.root.district.empty"),
					rm.getString("msg.error"),
					JOptionPane.ERROR_MESSAGE);
			mainTabbedPane.setSelectedComponent(rootDistrictPanel);
			return;
		}
		// 检查角色配置
		if(listSysRoleId.isEmpty())
		{
			JOptionPane.showMessageDialog(this,
					rm.getString("msg.role.empty"),
					rm.getString("msg.error"),
					JOptionPane.ERROR_MESSAGE);
			mainTabbedPane.setSelectedComponent(sysRolePanel);
			return;
		}
		// 获得用户组基本信息
		userGroupInfo.setName(tfGroupName.getText());
		userGroupInfo.setDescription(tfDescription.getText());
		userGroupInfo.setGroupType((Integer)cmbGroupType.getSelectedItem());
		userGroupInfo.setGroupLevel((Integer)cmbGroupLevel.getSelectedItem());
		userGroupInfo.setMMLCommands(getMmlPurview());
		userGroupInfo.setListRoleId(getListSysRole());
		userGroupInfo.setDevices(listDevicePurview);
		// 设备组id
		userGroupInfo.setListGroupId(listGroupId);
		// 根子网
		List<Integer> listSelectedRootDistrict = new ArrayList();
		for(Object rootId : listRootDistrict.getCheckBoxListSelectedValues())
		{
			listSelectedRootDistrict.add((Integer)rootId);
		}
		userGroupInfo.setDistricts(listSelectedRootDistrict);
		// 用户总数
		userGroupInfo.setUserCount((Integer)spnUserCount.getValue());
		//新建用户组
		if(!editMode)
		{
			try
			{
				barrier.reset();
				// 新增用户组操作
				OmcProcessor.process(Command.user, Command.addUserGroup,
						userGroupInfo, new ProcessCallback()
				{
					@Override
					public void processCompleted(final ProcessData out)
					{
						try
						{
							barrier.await();
						}
						catch(Exception exp)
						{}
						javax.swing.SwingUtilities.invokeLater(new Runnable()
						{
							@Override
							public void run()
							{
								// 0,失败
								// 1，用户组名重复
								// 2，成功
								switch(out.getState())
								{
									//失败
									case 0:
										JOptionPane.showMessageDialog(null,
												rm.
												getString("mb.addusergroup.fail"),
												rm.getString("mb.tip"),
												JOptionPane.INFORMATION_MESSAGE);
										break;
									//重复
									case 2:
										JOptionPane.showMessageDialog(null,
												rm.
												getString("mb.usergroupname.repeat"),
												rm.getString("mb.tip"),
												JOptionPane.INFORMATION_MESSAGE);
										break;
									//成功
									case 1:
										JOptionPane.showMessageDialog(null,
												rm.
												getString("mb.addusergroup.success"),
												rm.getString("mb.tip"),
												JOptionPane.INFORMATION_MESSAGE);
										//刷新用户组列表
										userGroupInfo.setId((Integer)out.
												getData());
										UserGroupEditor.this.dispose();
										isok = true;
										break;
								}
							}
						});
					}
				});
				barrier.await(OmcConstants.sync_timeout2, TimeUnit.MILLISECONDS);
			}
			catch(Exception exp)
			{
				JOptionPane.showMessageDialog(null,
						rm.getString("msg.timeout"),
						rm.getString("msg.prompt"),
						JOptionPane.ERROR_MESSAGE);
			}
		}
		else
		{
			// 编辑用户组 
			try
			{
				barrier.reset();
				OmcProcessor.process(Command.user, Command.modifyUserGroup,
						userGroupInfo, new ProcessCallback()
				{
					@Override
					public void processCompleted(final ProcessData out)
					{
						try
						{
							barrier.await();
						}
						catch(Exception exp)
						{}
						javax.swing.SwingUtilities.invokeLater(new Runnable()
						{
							@Override
							public void run()
							{
								// 0 失败
								// 1 成功
								switch(out.getState())
								{
									//失败
									case 0:
										JOptionPane.showMessageDialog(null,
												rm.
												getString("mb.editusergroup.fail"),
												rm.getString("mb.tip"),
												JOptionPane.INFORMATION_MESSAGE);
										break;
									//重复
									case 2:
										JOptionPane.showMessageDialog(null,
												rm.
												getString("mb.usergroupname.repeat"),
												rm.getString("mb.tip"),
												JOptionPane.INFORMATION_MESSAGE);
										break;
									//成功
									case 1:
										JOptionPane.showMessageDialog(null,
												rm.
												getString("mb.editusergroup.success"),
												rm.getString("mb.tip"),
												JOptionPane.INFORMATION_MESSAGE);
										//刷新用户组列表
										isok = true;
										UserGroupEditor.this.dispose();
										break;
								}
							}
						});
					}
				});
				barrier.await(OmcConstants.sync_timeout2, TimeUnit.MILLISECONDS);
			}
			catch(Exception exp)
			{
				JOptionPane.showMessageDialog(null,
						rm.getString("msg.timeout"),
						rm.getString("msg.prompt"),
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * 退出
	 */
	@Action
	public void exit()
	{
		if(mapMmlSelection != null)
		{
			mapMmlSelection.clear();
		}
		if(listDeviceId != null)
		{
			listDeviceId.clear();
		}
		if(listSysRoleId != null)
		{
			listSysRoleId.clear();
		}
		if(listRole != null)
		{
			listRole = null;
		}
		if(listMmlCmd != null)
		{
			listMmlCmd.clear();
		}
		dispose();
		isok = false;
	}
	
	/**
	 * 设备表格模型。
	 */
	private class DevModel extends AbstractTableModel
	{
		private List<Integer> source;
		private List<String> deviceColumns;
		private Map<Integer, Boolean> mapSelection;

		public DevModel()
		{
			source = new ArrayList();
			deviceColumns = new ArrayList();
			mapSelection = new HashMap();
			deviceColumns.add(rm.getString("table.device.cols.index"));
			deviceColumns.add(rm.getString("table.device.cols.selection"));
			deviceColumns.add(rm.getString("table.device.cols.name"));
			deviceColumns.add(rm.getString("table.device.cols.number"));
		}
		
		/**
		 * 初始化模型数据。
		 * @param source 模型数据
		 */
		public void initModel(List<Integer> source)
		{
			this.source.clear();
			this.source.addAll(source);
			this.fireTableDataChanged();
		}

		@Override
		public int getRowCount()
		{
			return source == null ? 0 : source.size();
		}

		@Override
		public int getColumnCount()
		{
			return deviceColumns.size();
		}

		@Override
		public String getColumnName(int column)
		{
			return deviceColumns.get(column);
		}

		@Override
		public void setValueAt(Object value, int row, int column)
		{
			Integer item = source.get(row);
			if(column == 1)
			{
				Boolean selected = (Boolean)value;
				if(selected)
				{
					mapSelection.put(item, selected);
				}
				else
				{
					mapSelection.remove(item);
				}
				fireTableCellUpdated(row, column);
			}
		}

		@Override
		public Object getValueAt(int row, int column)
		{
			Integer item = source.get(row);
			// 查找设备对象
			ConfigService service =
					(ConfigService)ServiceUtils.getInstance().getService(ServiceConstants.SVC_CONFIG);
			Device device = service.findBBU(item);
			switch(column)
			{
				case 0:
					return row + 1;
				case 1:
					return mapSelection.containsKey(item);
				case 2:
					return device != null ? device.getName() : "----";
				case 3:
					return device != null ? device.getNumber() : "----";
			}
			return null;
		}

		@Override
		public Class getColumnClass(int column)
		{
			switch(column)
			{
				case 0:
					return Integer.class;
				case 1:
					return Boolean.class;
				default:
					return String.class;
			}
		}

		@Override
		public boolean isCellEditable(int row, int column)
		{
			// 如果是第1列则该单元格可编辑
			if(column == 1)
			{
				return true;
			}
			return false;
		}
		
		/**
		 * 获取选中的设备id列表。
		 * @return 选中的设备id列表
		 */
		public List<Integer> getSelectedDevice()
		{
			return new ArrayList(mapSelection.keySet());
		}

		/**
		 * 添加设备记录。
		 * @param deviceId 设备id
		 * @param fireEvent 是否触发事件
		 */
		public void append(Integer deviceId, boolean fireEvent)
		{
			if(source.indexOf(deviceId) == -1)
			{
				// 添加到模型数据中
				source.add(deviceId);
				if(fireEvent)
				{
					// 通知模型数据改变
					int index = getRowCount() - 1;
					fireTableRowsInserted(index, index);
				}
			}
		}

		//移除所有记录
		public void removeAll()
		{
			if(source.isEmpty())
			{
				return;
			}
			int rowNum = getRowCount();
			// 清除模型数据
			source.clear();
			mapSelection.clear();
			// 通知模型数据改变
			fireTableRowsDeleted(0, rowNum - 1);
		}
		
		/**
		 * 设置选择状态。
		 * @param deviceId 设备id
		 * @param selected 选择状态
		 * @param fireEvent 是否触发事件
		 */
		public void select(int deviceId, boolean selected, boolean fireEvent)
		{
			if(selected)
			{
				mapSelection.put(deviceId, true);
			}
			else
			{
				mapSelection.remove(deviceId);
			}
			if(fireEvent)
			{
				int index = source.indexOf(deviceId);
				if(index != -1)
				{
					fireTableCellUpdated(index, 1);
				}
			}
		}

		/**
		 * 设置全选状态。
		 * @param selected 选中状态
		 * @param fireEvent 是否触发事件
		 */
		public void selectAll(boolean selected, boolean fireEvent)
		{
			if(selected)
			{
				for(Integer item : source)
				{
					mapSelection.put(item, true);
				}
			}
			else
			{
				mapSelection.clear();
			}
			if(fireEvent)
			{
				fireTableDataChanged();
			}
		}
	}

	/**
	 * CheckBox的tree节点渲染
	 */
	class CheckRenderer extends TreeCheckRenderer
	{
		/**
		 * 将树的的节点渲染成JCheckBox，除Root节点以外
		 */
		@Override
		public Component getTreeCellRendererComponent(JTree tree,
				Object value,
				boolean isSelected, boolean expanded,
				boolean leaf, int row, boolean hasFocus)
		{
			//获得节点名称
			Object districtInfo = ((TreeCheckNode)value).getUserObject();
			//districtInfo = ((DistrictInfo)districtInfo).getName();
			districtInfo = ((District)districtInfo).getName();
			//通过渲染器调用以将指定值转换为文本
			String stringValue =
					tree.convertValueToText(districtInfo, isSelected,
					expanded, leaf, row, hasFocus);
			check.setSelected(((TreeCheckNode)value).isSelected());
			check.setText(stringValue);
			label.setFont(tree.getFont());
			label.setText(stringValue);
			label.setSelected(isSelected);
			label.setFocus(hasFocus);
			//为父节点以及子节点设置不同的图标
			if(leaf)
			{
				label.setIcon(UIManager.getIcon("Tree.leafIcon"));//设置父节点图标
			}
			else if(expanded)
			{
				label.setIcon(UIManager.getIcon("Tree.openIcon"));//设置父节点展开时的图标
			}
			else
			{
				label.setIcon(UIManager.getIcon("Tree.closedIcon"));//设置父节点关闭时的图标
			}
			//判断是否root节点，如果是Root节点则不渲染成JCheckbox
			Object rootInfo =
					((TreeCheckNode)tree.getModel().getRoot()).getUserObject();
			//rootInfo = ((DistrictInfo)rootInfo).getName();
			rootInfo = ((District)rootInfo).getName();
			if(stringValue.equals(rootInfo.toString()))
			{
				return label;
			}
			return check;
		}
	}

	/**
	 * 选择设备
	 *
	 * @return
	 */
	@Action
	public void queryDevice()
	{
		// 创建查询条件窗口
		DeviceQueryPanel deviceDialog = DeviceQueryPanel.getInstance(OmcConstants.dvt_bbu, false);
		// 显示查询条件窗口
		// 如果用户选择查询操作
		if(deviceDialog.showDialog())
		{
			List<DeviceId> listSelectedDevice = new ArrayList();
			listSelectedDevice.addAll(deviceDialog.getSelectedDeviceId());
			// 获得设备添加方式;0:追加;1:删除原有设备后添加
			int type = deviceDialog.getAppendType();
			// 根据设备添加方式,将设备信息添加到设备表中显示
			if(type == DeviceQueryPanel.AddDev_SUPERADD)
			{
				// 追加方式
				// 遍历原有的设备集合，追加新增的设备
				for(int i = listSelectedDevice.size() - 1; i >= 0; i--)
				{
					// 如果用户选择的设备与原有设备重复
					for(Integer item : listDeviceId)
					{
						// 删除重复设备
						if(item.intValue() == listSelectedDevice.get(i).getId())
						{
							listSelectedDevice.remove(i);
							break;
						}
					}
				}
				// 追加不重复的设备
				for(int i = 0; i < listSelectedDevice.size(); i++)
				{
					listDeviceId.add(listSelectedDevice.get(i).getId());
				}
				// 显示设备列表
				((DevModel)tableDevice.getModel()).initModel(listDeviceId);
				for(int i = 0; i < listSelectedDevice.size(); i++)
				{
					((DevModel)tableDevice.getModel()).select(listSelectedDevice.get(i).getId(), true, true);
				}
			}
			else if(type == DeviceQueryPanel.AddDev_DELADD)
			{
				// 删除原有设备后添加
				((DevModel)tableDevice.getModel()).removeAll();
				listDeviceId.clear();
				for(int i = 0; i < listSelectedDevice.size(); i++)
				{
					listDeviceId.add(listSelectedDevice.get(i).getId());
				}
				// 显示设备列表
				((DevModel)tableDevice.getModel()).initModel(listDeviceId);
				((DevModel)tableDevice.getModel()).selectAll(true, true);
			}
			// 更新设备数量
			lblCountValue.setText(String.valueOf(listDeviceId.size()));
		}
	}

	@Action
	public void selectSysRole()
	{
		SysRoleMgmt dialog = new SysRoleMgmt();
		if(dialog.showDialog(true))
		{
			// 获取选择的角色
			Role role = dialog.getSelectedRole();
			// 保存选择的角色并显示角色对应权限
			listSysRoleId.clear();
			listSysRoleId.add(role.getId());
			initSysRoleFunc();
		}
	}

	@Action
	public Task export()
	{
		// 区分编辑和新建
		// 编辑时导出原始配置信息，新建时导出当前配置信息
		UserGroup exportUserGroup;
		if(userGroupInfo != null)
		{
			exportUserGroup = userGroupInfo;
		}
		else
		{
			exportUserGroup = new UserGroup();
			// 获得用户组基本信息
			exportUserGroup.setName(tfGroupName.getText());
			exportUserGroup.setDescription(tfDescription.getText());
			exportUserGroup.setGroupType((Integer)cmbGroupType.getSelectedItem());
			exportUserGroup.setGroupLevel((Integer)cmbGroupLevel.getSelectedItem());
			exportUserGroup.setMMLCommands(getMmlPurview());
			exportUserGroup.setListRoleId(getListSysRole());
			exportUserGroup.setDevices(getDevicePurview());
			exportUserGroup.setListGroupId(((DeviceGroupTableModel)tableDeviceGroup.getModel()).getSelectedGroup());
		}
		// 角色权限
		List<Integer> listRoleFunc = sysRoleFuncTree.getSelectedFunction();
		// 角色权限排序
		Collections.sort(listRoleFunc, new Comparator()
		{
			@Override
			public int compare(Object o1, Object o2)
			{
				String func1 = String.format("%s/%s", 
						OmcDictionary.getInstance().sys_getItemAlias((Integer)o1),
						OmcDictionary.getInstance().sys_getNameById((Integer)o1));
				String func2 = String.format("%s/%s", 
						OmcDictionary.getInstance().sys_getItemAlias((Integer)o2),
						OmcDictionary.getInstance().sys_getNameById((Integer)o2));
				return func1.compareTo(func2);
			}
		});
		// 导出数据
		UserGroupRecordSet recordSet = new UserGroupRecordSet(exportUserGroup, 
				listRoleFunc,
				((DeviceGroupTableModel)tableDeviceGroup.getModel()).getSource(), 
				listMmlCmd);
		// 确定导出字段集合
		List<String> listField = new ArrayList();
		listField.add(rm.getString("msg.col.name"));
		listField.add(rm.getString("msg.col.value"));
		return new DefaultDataExportTask(listField, recordSet, null, null, null);
	}
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnOk;
    private javax.swing.JButton btnSelectDevice;
    private javax.swing.JButton btnSelectRole;
    private javax.swing.JCheckBox cbCheckAll;
    private javax.swing.JCheckBox cbSelectAllCommand;
    private javax.swing.JCheckBox cbSelectAllDevice;
    private javax.swing.JCheckBox cbSelectAllFunction;
    private javax.swing.JCheckBox cbSelectAllTr069Command;
    private javax.swing.JComboBox cmbGroupLevel;
    private javax.swing.JComboBox cmbGroupType;
    private javax.swing.JPanel commandPurviewPanel;
    private javax.swing.JScrollPane commandTableScrollPane;
    private javax.swing.JScrollPane commandTableScrollPane1;
    private javax.swing.JPanel devicePurviewPanel;
    private javax.swing.JScrollPane functionTableScrollPane;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblCount;
    private javax.swing.JLabel lblCountValue;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblGroupLevel;
    private javax.swing.JLabel lblGroupName;
    private javax.swing.JLabel lblGroupType;
    private javax.swing.JLabel lblUserCount;
    private com.jidesoft.swing.CheckBoxList listRootDistrict;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTabbedPane mainTabbedPane;
    private javax.swing.JPanel rootDistrictPanel;
    private javax.swing.JSpinner spnUserCount;
    private com.hongxin.omc.ui.user.SysRoleFuncTree sysRoleFuncTree;
    private javax.swing.JPanel sysRolePanel;
    private javax.swing.JTable tableDevice;
    private DefaultTableModel devModel;
    private javax.swing.JTable tableDeviceGroup;
    private javax.swing.JScrollPane tableDeviceScrollPane;
    private javax.swing.JScrollPane tableGroupScrollPane;
    private javax.swing.JTextField tfDescription;
    private javax.swing.JTextField tfGroupName;
    private javax.swing.JPanel tr069PurviewPanel;
    private org.jdesktop.swingx.JXTreeTable treeTableCommand;
    private org.jdesktop.swingx.JXTreeTable treeTableFunction;
    private org.jdesktop.swingx.JXTreeTable treeTableTr069Command;
    // End of variables declaration//GEN-END:variables
}
