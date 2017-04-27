/*
 * Copyright 2009 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */

/*
 * UserGroupManage.java
 *
 * Created on 2009-8-20, 15:13:20
 */
package prj.ui.user;

import com.hongxin.component.ComponentUtil;
import com.hongxin.component.export.TableModelRecordSet;
import com.hongxin.component.privilege.PrivilegeController;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import com.hongxin.saf.AsynBlockTask;
import com.hongxin.saf.SingleFrameApplication;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.table.AbstractTableModel;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;
import prj.ui.basic.DefaultDataExportTask;
import prj.user.po.Role;
import prj.user.po.UserGroup;

/**
 * 用户组管理,实现新增/修改/删除/锁定/复制用户组等基本操作
 *
 * @author liming
 */
public class UserGroupManage extends javax.swing.JPanel
{
	/**
	 * 资源文件对象
	 */
	private ResourceMap rm;
	/**
	 * 父窗口对象
	 */
	private JDialog dialog;
	
	/**
	 * 初始化用户组管理界面的基本信息显示
	 *
	 * @param parent 输入项：父窗口对象
	 */
	public UserGroupManage()
	{
		rm = Application.getInstance().getContext().getResourceMap(UserGroupManage.class);
		initComponents();
		//设置用户组数据
		initialize();
	}

	/**
	 * 获得用户管理窗体对象
	 *
	 * @param parent 主框架
	 * @param modal 是否模式窗口，取值如下
	 * <ul>
	 * <li>true 模式窗口</li>
	 * <li>false 非模式窗口</li>
	 * </ul>
	 * @return 用户管理窗体对象
	 */
	public static JDialog getInstance(java.awt.Frame parent, boolean modal)
	{
		final UserGroupManage um = new UserGroupManage();
		um.dialog = new JDialog(parent, modal);
		um.dialog.
				setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		um.dialog.setName(um.getName());
		um.dialog.setTitle(um.rm.getString("form.title"));
		um.dialog.setLayout(new BorderLayout());
		um.dialog.add(um, BorderLayout.CENTER);
		um.dialog.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				um.exit();
			}
		});
		um.dialog.pack();
		return um.dialog;
	}

	/**
	 * 初始化用户数据
	 */
	private void initialize()
	{
//		PrivilegeController.checkPrivilege(this.getClass(), this, this);
		// 获取用户组信息
		getAllUserGroupMap();
		// 注册系统信息回调方法
	}


	/**
	 * 获得用户组数据
	 *
	 * @return 返回项：
	 * <ul>
	 * <li>true：获取用户组信息成功</li>
	 * <li>false：获取用户组信息失败</li>
	 * </ul>
	 */
	private void getAllUserGroupMap()
	{
		// 获取用户组信息
//		Task acquireTask = new AcquireUserDataTask(Application.getInstance());
//		Application.getInstance().getContext().getTaskService().
//				execute(acquireTask);
		List allGrps = new ArrayList();
		List allRoles = new ArrayList();
			createUserGroupTable(allGrps,
					allRoles);		
	}

	// <editor-fold defaultstate="collapsed" desc="查询用户组操作的处理器">
//	private class AcquireUserDataTask extends AsynBlockTask<Object, Void> 
//	implements ProcessCallback
//	{
//		/**
//		 * 操作粘合对象。
//		 */
//		private OmcOperationGlue glue;
//		
//		AcquireUserDataTask(org.jdesktop.application.Application app)
//		{
//			// Runs on the EDT.  Copy GUI state that
//			// doInBackground() depends on from parameters
//			// to CommitTask fields, here.
//			super(app, OmcPreferences.getInstance().
//					getInteger("sys.asyn.timeout", 60000));
//			// 设置任务标题
//			setTitle(SpeedUtil.
//					getCommandName(Command.user, Command.getAllUserGroups));
//		}
//
//		@Override
//		protected Object doInBackground()
//		{
//			// 异步执行命令
//			glue = new OmcOperationGlue(this);
//			glue.addOperation(Command.user, Command.getAllUserGroups, true, null);
//			glue.addOperation(Command.user, Command.listSysRole, true, null);
//			glue.start();
//			// 阻塞等待
//			waitForReady();
//			return null;
//		}
//
//		@Override
//		public void processCompleted(ProcessData out)
//		{
//			// 任务结束
//			setTaskFinished(true);
//		}
//		
//		@Override
//		protected void succeeded(Object o)
//		{
//			// 显示用户组列表
//			ProcessData result1 = (ProcessData)glue.getResult(Command.user, Command.getAllUserGroups);
//			ProcessData result2 = (ProcessData)glue.getResult(Command.user, Command.listSysRole);
//			createUserGroupTable((List)result1.getData(),
//					(List)result2.getData());
//		}
//	}
	// </editor-fold>

	// <editor-fold defaultstate="collapsed" desc="用户组表格模型">
	private class UserGroupTableModel extends AbstractTableModel
	{
		/**
		 * 列集合。
		 */
		protected List<String> columns;
		/**
		 * 用户组集合。
		 */
		private List<UserGroup> listUserGroup;
		/**
		 * 角色集合。
		 */
		private List<Role> listRole;

		public UserGroupTableModel()
		{
			columns = new ArrayList();
			columns.add(rm.getString("col.index"));
			columns.add(rm.getString("col.name"));
			columns.add(rm.getString("col.type"));
			columns.add(rm.getString("col.role"));
			columns.add(rm.getString("col.lock"));
			columns.add(rm.getString("col.desc"));
			listUserGroup = new ArrayList();
			listRole = new ArrayList();
		}

		@Override
		public boolean isCellEditable(int row, int column)
		{
			return false;
		}

		@Override
		public int getColumnCount()
		{
			return columns.size();
		}

		@Override
		public String getColumnName(int column)
		{
			return columns.get(column);
		}
		
		@Override
		public int getRowCount()
		{
			return listUserGroup.size();
		}

		@Override
		public Object getValueAt(int row, int col)
		{
			UserGroup group = listUserGroup.get(row);
			switch(col)
			{
				case 0:
					return row + 1;
				case 1:
					return group.getName();
				case 2:
					if(group.getGroupType() > UserGroup.TYPE_SUPER && group.getGroupType() != UserGroup.TYPE_PREP)
					{
//						return UserGroupTypeConverter.instance.convertForward(UserGroup.TYPE_SUPER);
						return "";
					}
					else
					{
//						return UserGroupTypeConverter.instance.convertForward(group.getGroupType());
						return "";
					}
				case 3:
					return getRoleText(group);
				case 4:
//					return UserGroupStatusConverter.instance.convertForward(group.getLockStatus());
					return "";
				case 5:
					return group.getDescription();
				default:
					return null;
			}
		}
		
		/**
		 * 获取目标用户组对应的角色文本。
		 * @param group 用户组对象
		 * @return 用户组对应角色文本
		 */
		private String getRoleText(UserGroup group)
		{
			List<Integer> listRoleId = group.getListRoleId();
			if(listRoleId != null && !listRoleId.isEmpty())
			{
				if(listRoleId.size() == 1)
				{
					for(Role role : listRole)
					{
						if(role.getId() == listRoleId.get(0))
						{
							return role.getName();
						}
					}
				}
				else
				{
					StringBuilder sbd = new StringBuilder();
					for(Role role : listRole)
					{
						if(sbd.length() > 0)
						{
							sbd.append(",");
						}
						if(listRoleId.indexOf(role.getId()) != -1)
						{
							sbd.append(role.getName());
						}
					}
					return sbd.toString();
				}
			}
			return null;
		}
		
		/**
		 * 初始化模型数据。
		 * @param listUserGroup 用户组列表
		 * @param listRole 角色列表
		 */
		public void initModel(List<UserGroup> listUserGroup, List<Role> listRole)
		{
			this.listUserGroup.clear();
			this.listUserGroup.addAll(listUserGroup);
			this.listRole.clear();
			this.listRole.addAll(listRole);
			fireTableDataChanged();
		}
		
		/**
		 * 清除数据。
		 */
		public void clear()
		{
			this.listUserGroup.clear();
			this.listRole.clear();
			fireTableDataChanged();
		}
		
		/**
		 * 获取角色列表。
		 * @return 角色列表
		 */
		public List<Role> getListRole()
		{
			return Collections.unmodifiableList(listRole);
		}
		
		/**
		 * 获取目标用户组集合对应的最高级别。
		 * @param listGroupId 用户组id集合
		 * @return 最高级别
		 */
		public int getTopGroupLevel(List<Integer> listGroupId)
		{
			// 计算用户组最高级别
			int topLevel = 0;
			for(Integer groupId : listGroupId)
			{
				for(UserGroup group : listUserGroup)
				{
					if(group.getId() == groupId)
					{
						topLevel = Math.max(topLevel, group.getGroupLevel());
						break;
					}
				}
			}
			return topLevel;
		}
		
		/**
		 * 获取指定索引位置的用户组。
		 * @param modelIndex 模型索引
		 * @return 用户组对象
		 */
		public UserGroup getUserGroupAt(int modelIndex)
		{
			return listUserGroup.get(modelIndex);
		}
		
		/**
		 * 添加用户组。
		 * @param group 用户组对象
		 */
		public void addUserGroup(UserGroup group)
		{
			if(listUserGroup.indexOf(group) == -1)
			{
				listUserGroup.add(group);
				fireTableRowsInserted(listUserGroup.size() - 1, 
						listUserGroup.size() - 1);
			}
		}
		
		/**
		 * 更新用户组。
		 * @param group 用户组对象
		 */
		public void updateUserGroup(UserGroup group)
		{
			int index = listUserGroup.indexOf(group);
			if(index != -1)
			{
				listUserGroup.get(index).copyFrom(group);
				fireTableRowsUpdated(index, index);
			}
		}
		
		/**
		 * 删除用户组。
		 * @param group 用户组对象
		 */
		public void removeUserGroup(UserGroup group)
		{
			int index = listUserGroup.indexOf(group);
			if(index != -1)
			{
				listUserGroup.remove(index);
				fireTableRowsDeleted(index, index);
			}
		}
	}
	// </editor-fold>

	/**
	 * 创建用户组列表。
	 * @param listUserGroup 用户组列表
	 */
	private void createUserGroupTable(List<UserGroup> listUserGroup, 
			List<Role> listRole)
	{
		userGroupModel.initModel(listUserGroup, listRole);
	}

	/**
	 * 获取当前用户所属用户组的最高等级。
	 *
	 * @return 最高等级
	 */
	private int getCurrentUserTopLevel()
	{
		List<Integer> allGids = new ArrayList<Integer>();
		//List<Integer> allGids = UserSession.getInstance().getUserInfo().getListGroupId()
		return userGroupModel.getTopGroupLevel(allGids);
	}

	/**
	 * 获取当前用户所属用户组的最高类型。
	 *
	 * @return 最高类型
	 */
	private int getCurrentUserTopType()
	{
//		return UserSession.getInstance().getUserInfo().getTopGroupType();
		return 1;
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

        tableScrollPane = new javax.swing.JScrollPane();
        userGroupTable = ComponentUtil.createTable(true, false);
        toolBar = new javax.swing.JToolBar();
        btnAdd = ComponentUtil.createToolBarButton();
        btnEdit = ComponentUtil.createToolBarButton();
        btnDelete = ComponentUtil.createToolBarButton();
        separator1 = new javax.swing.JToolBar.Separator();
        separator2 = new javax.swing.JToolBar.Separator();
        btnExport = ComponentUtil.createToolBarButton();
        btnQuit = new javax.swing.JButton();

        setName("Form_UserGroupManage_1"); // NOI18N

        tableScrollPane.setName("tableScrollPane"); // NOI18N

        userGroupModel = new UserGroupTableModel();
        userGroupTable.setAutoCreateRowSorter(true);
        userGroupTable.setModel(userGroupModel);
        userGroupTable.setName("userGroupTable"); // NOI18N
        tableScrollPane.setViewportView(userGroupTable);

        toolBar.setBorder(null);
        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        toolBar.setName("toolBar"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(prj.PrjApp.class).getContext().getActionMap(UserGroupManage.class, this);
        btnAdd.setAction(actionMap.get("addUserGroup")); // NOI18N
        btnAdd.setFocusable(false);
        btnAdd.setName("btnAdd"); // NOI18N
        toolBar.add(btnAdd);

        btnEdit.setAction(actionMap.get("editUserGroup")); // NOI18N
        btnEdit.setFocusable(false);
        btnEdit.setName("btnEdit"); // NOI18N
        toolBar.add(btnEdit);

        btnDelete.setAction(actionMap.get("delUserGroup")); // NOI18N
        btnDelete.setFocusable(false);
        btnDelete.setName("btnDelete"); // NOI18N
        toolBar.add(btnDelete);

        separator1.setName("separator1"); // NOI18N
        toolBar.add(separator1);

        separator2.setName("separator2"); // NOI18N
        toolBar.add(separator2);

        btnExport.setAction(actionMap.get("export")); // NOI18N
        btnExport.setFocusable(false);
        btnExport.setName("btnExport"); // NOI18N
        toolBar.add(btnExport);

        btnQuit.setAction(actionMap.get("exit")); // NOI18N
        btnQuit.setName("btnQuit"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 651, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(toolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnQuit, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnQuit)
                    .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

	/**
	 * 获得选中的用户组对象
	 *
	 * @return 用户组对象
	 */
	private UserGroup getSelectedUserGroup()
	{
		// 获得当前选中的用户组
		int index = userGroupTable.getSelectedRow();
		if(index == -1)
		{
			JOptionPane.showMessageDialog(this,
					rm.getString("mb.selectgroup.null"),
					rm.getString("mb.tip"),
					JOptionPane.INFORMATION_MESSAGE);
			return null;
		}
		return userGroupModel.getUserGroupAt(userGroupTable.convertRowIndexToModel(index));
	}

	/**
	 * 退出
	 */
	@Action
	public void exit()
	{
		if(dialog != null)
		{
			dialog.dispose();
		}
//		OmcProcessor.unregister(Command.user, Command.userGroupChange);
		userGroupModel.clear();
	}

	/**
	 * 新增用户组
	 */
	@Action
	public void addUserGroup()
	{
		//创建一个用户组对象
		UserGroup userGroup = new UserGroup();
		userGroup.setId(-1);
		UserGroupEditor window = new UserGroupEditor(SingleFrameApplication.
				getInstance().getMainFrame(), true, userGroupModel.getListRole(),
				userGroup, getCurrentUserTopType(), getCurrentUserTopLevel());
		//打开新增用户组界面
		SingleFrameApplication.getInstance().show(window);
		//判断是否处理成功
		if(window.isok)
		{
			userGroupModel.addUserGroup(userGroup);
		}
	}

	/**
	 * 编辑用户组
	 */
	@Action
	public void editUserGroup()
	{
		//获取选择的用户组对象
		UserGroup userGroup = getSelectedUserGroup();
		//用户组为Null退出
		if(userGroup == null)
		{
			return;
		}
		UserGroup userGroupNew = userGroup.clone();
		//打开编辑界面
		UserGroupEditor window = new UserGroupEditor(SingleFrameApplication.
				getInstance().getMainFrame(), true, userGroupModel.getListRole(), 
				userGroupNew, getCurrentUserTopType(), getCurrentUserTopLevel());
		SingleFrameApplication.getInstance().show(window);
		//判断是否处理成功
		if(window.isok)
		{
			// 更新用户组
			userGroupModel.updateUserGroup(userGroupNew);
		}
	}
	
	/**
	 * 删除用户组
	 */
	@Action
	public void delUserGroup()
	{
		//获取选择的用户组对象
		UserGroup userGroup = getSelectedUserGroup();
		if(userGroup == null)
		{
			return;
		}
		//超级管理员用户组不可删除
		if(userGroup.getId() == 0)
		{
			JOptionPane.showMessageDialog(this,
					rm.getString("mb.delusergroup.no"),
					rm.getString("mb.tip"),
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		//预置用户组不可删除
		if(userGroup.getGroupType() == UserGroup.TYPE_PREP)
		{
			JOptionPane.showMessageDialog(this,
					rm.getString("mb.delprepusergroup.no"),
					rm.getString("mb.tip"),
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		//管理员用户组不可删除
		if(userGroup.getGroupType() == UserGroup.TYPE_SUPER)
		{
			JOptionPane.showMessageDialog(this,
					rm.getString("mb.deladminusergroup.no"),
					rm.getString("mb.tip"),
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}		// 获取选中的用户组名
		String userGroupName = userGroup.getName();
		// 提示用户组确认删除,并获得用户操作0:确认删除1:取消删除
		int type = JOptionPane.showConfirmDialog(null,
				rm.getString("mb.delusergroup", userGroupName),
				rm.getString("mb.tip"),
				JOptionPane.YES_NO_OPTION);
		// 如果用户确认删除
		if(type == 0)
		{
			// 执行删除操作
//			ProcessData out = OmcProcessor.process(Command.user,
//					Command.deleteUserGroup, userGroup);
//			// 0 失败
//			// 1 成功
//			switch(out.getState())
//			{
//				case 0:
//					JOptionPane.showMessageDialog(this,
//							rm.getString("mb.delusergourp.fail"),
//							rm.getString("msg.error"),
//							JOptionPane.ERROR_MESSAGE);
//					break;
//				case 1:
//					// 删除用户组
//					userGroupModel.removeUserGroup(userGroup);
//					JOptionPane.showMessageDialog(this,
//							rm.getString("mb.delusergourp.success"),
//							rm.getString("mb.tip"),
//							JOptionPane.INFORMATION_MESSAGE);
//					break;
//				case 2:
//					JOptionPane.showMessageDialog(this,
//							rm.getString("mb.delusergourp.fail2"),
//							rm.getString("msg.error"),
//							JOptionPane.ERROR_MESSAGE);
//					break;
//				case UserConstants.remove_usergroup_moveuser:
//					JOptionPane.showMessageDialog(this,
//							rm.getString("msg.remove.usergroup.moveuser", 
//							StringUtils.join((List)out.getData(), ",")),
//							rm.getString("msg.error"),
//							JOptionPane.ERROR_MESSAGE);
//					break;
//				case UserConstants.remove_user_group_forbidden:
//					JOptionPane.showMessageDialog(this,
//							rm.getString("msg.del.super.forbid", userGroupName),
//							rm.getString("msg.error"),
//							JOptionPane.ERROR_MESSAGE);
//					break;
//			}
		}
	}

	@Action
	public Task export()
	{
		// 检查列表数据
		if(userGroupTable.getModel().getRowCount() == 0)
		{
			JOptionPane.showMessageDialog(null, 
					rm.getString("msg.export.empty"), 
					rm.getString("msg.prompt"), 
					JOptionPane.INFORMATION_MESSAGE);
			return null;
		}
		// 提取导出列
		List<String> listColumn = new ArrayList();
		for(int i = 0; i < userGroupTable.getModel().getColumnCount(); i++)
		{
			listColumn.add(userGroupTable.getModel().getColumnName(i));
		}
		TableModelRecordSet recordSet = new TableModelRecordSet(userGroupTable.getModel());
		return new DefaultDataExportTask(listColumn, recordSet, null);
	}


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnQuit;
    private javax.swing.JToolBar.Separator separator1;
    private javax.swing.JToolBar.Separator separator2;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JToolBar toolBar;
    private UserGroupTableModel userGroupModel;
    private javax.swing.JTable userGroupTable;
    // End of variables declaration//GEN-END:variables
}
