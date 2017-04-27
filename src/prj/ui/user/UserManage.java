/*
 * Copyright 2009 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */

/*
 * UserManageP.java
 *
 * Created on 2009-8-20, 14:56:06
 */
package prj.ui.user;

import com.hongxin.component.ComponentUtil;
import com.hongxin.component.export.TableModelRecordSet;
import com.hongxin.component.privilege.PrivilegeController;
import com.hongxin.saf.SingleFrameApplication;

import com.hongxin.util.GUIUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.SearchPredicate;
import org.jdesktop.swingx.search.PatternModel;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import prj.PrjApp;
import prj.ui.basic.DefaultDataExportTask;
import prj.user.po.Role;
import prj.user.po.User;
import prj.user.po.UserGroup;
import util.SQLiteCRUD;

/**
 * 用户管理界面，实现新增/删除/编辑/锁定/设备用户/强制下线用户的基本操作
 * @author liming
 */
public class UserManage extends javax.swing.JPanel
{
	/**
	 * 在线用户图标
	 */
	private Icon onUserIcon;
	/**
	 * 离线用户图标
	 */
	private Icon offUserIcon;
	/**
	 * 用户组图标
	 */
	private Icon userGroupIcon;
	/**
	 * 用户组集合。
	 */
	private List<UserGroup> listUserGroup = new ArrayList();
	/**
	 * 用户集合。
	 */
	private List<User> listUser = new ArrayList();
	/**
	 * 角色集合。
	 */
	private List<Role> listRole = new ArrayList();
	/**
	 * 资源文件对象
	 */
	private ResourceMap rm;
	private JDialog dialog;
	private ColorHighlighter matchHighlighter;

	/**
	 * 初始化用户管理界面的基本信息显示
	 * @param parent 输入项：父窗口对象
	 */
	public UserManage()
	{
		// 获得资源文件对象
		rm = Application.getInstance().getContext().getResourceMap(UserManage.class);
		initComponents();
		// 初始化
		initialize();
	}

	/**
	 * 创建唯一实例
	 * @param parent
	 * @param modal
	 * @return
	 */
	public static JDialog getInstance(java.awt.Frame parent, boolean modal)
	{
		final UserManage um = new UserManage();
		um.dialog = new JDialog(parent, modal);
		um.dialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		um.dialog.setName(um.getName());
		um.dialog.setTitle(um.rm.getString("form.title"));
		um.dialog.setLayout(new BorderLayout());
		um.dialog.add(um, BorderLayout.CENTER);
		GUIUtils.addHideAction(um.dialog);
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
		// 权限控制
//		PrivilegeController.checkPrivilege(this.getClass(), this, this);
		onUserIcon = rm.getIcon("user.online.icon");
		offUserIcon = rm.getIcon("user.offline.icon");
		userGroupIcon = rm.getIcon("userGroup.icon");
		// 获得用户组、用户信息
		getAllUserInfo();
		// 文档事件
		matchHighlighter = new ColorHighlighter(HighlightPredicate.NEVER, null, Color.MAGENTA);
		userTable.addHighlighter(matchHighlighter);
		userSearch.getDocument().addDocumentListener(new UserManage.SearchDocumentListener());
	}

	private void getAllUserInfo()
	{
		SQLiteCRUD sqlOpt = PrjApp.getApplication().getSQLiteCRUD();
		List<List> grpresults = sqlOpt.select("user_group", new String[]{"id","groupname","groupdesc","grouptype","roleid"});
		List<List> userresults = sqlOpt.select("user_user", new String[]{"id","number","username","password","enusername","nameusebefore","sex","idnumber"});
		List<List> roleresults = sqlOpt.select("user_role", new String[]{"id","rolename","roledesc"});
		
		List<UserGroup> userGrpList = new ArrayList();
		List<User> userList = new ArrayList();
		List<Role> roleList = new ArrayList();
		for(List<String> grp:grpresults)
		{
			UserGroup ug = new UserGroup();
			ug.setId(Integer.parseInt(grp.get(0)));
			ug.setName(grp.get(1));
			ug.setDescription(grp.get(2));
			List<Integer> roleid = new ArrayList<Integer>();
			roleid.add(Integer.parseInt(grp.get(3)));
			ug.setListRoleId(roleid);
			userGrpList.add(ug);
		}
		
		for(List<String> usr:userresults)
		{
			User u = new User();
			u.setId(Integer.parseInt(usr.get(0)));
			u.setAliasName(usr.get(1));
			u.setName(usr.get(2));
			u.setPassword(usr.get(3));
			u.setRealName(usr.get(4));
			List<String> results = sqlOpt.selectByCondition("user_groupuser", "groupid", "userid", usr.get(0));
			List<Integer> gid = new ArrayList<Integer>();
			for(String r:results)
			{
				gid.add(Integer.parseInt(r));
			}
			u.setGroups(gid);
			userList.add(u);
		}
		
		for(List<String> role:roleresults)
		{
			Role r = new Role();
			r.setId(Integer.parseInt(role.get(0)));
			r.setName(role.get(1));
			r.setDescription(role.get(2));
			roleList.add(r);
		}

		// 用户组
		if(userGrpList != null)
		{
			listUserGroup.clear();
			listUserGroup.addAll(userGrpList);
		}
		// 用户
		if(userList != null)
		{
			listUser.clear();
			listUser.addAll(userList);
		}
		// 角色
		if(roleList != null)
		{
			listRole.clear();
			listRole.addAll(roleList);
		}
		javax.swing.SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				// 显示用户组列表
				createUserTable();
			}
		});

	
	}

	/**
	 * 创建用户列表
	 */
	private void createUserTable()
	{
		//创建根节点
		DefaultMutableTreeTableNode root =
				(DefaultMutableTreeTableNode)userTable.getTreeTableModel().getRoot();
		//创建用户组节点
		DefaultMutableTreeTableNode[] node =
				new DefaultMutableTreeTableNode[listUserGroup.size()];
		DefaultTreeTableModel model =
				(DefaultTreeTableModel)userTable.getTreeTableModel();
		//清除所有列
		int cnt = model.getChildCount(root);
		//遍历所有的子节点
		for(int i = cnt - 1; i >= 0; i--)
		{
			DefaultMutableTreeTableNode child =
					(DefaultMutableTreeTableNode)model.getChild(root, i);
			model.removeNodeFromParent(child);
		}
		//遍历所有用户组列表
		for(int i = 0; i < listUserGroup.size(); i++)
		{
			node[i] = new DefaultMutableTreeTableNode(listUserGroup.get(i));
			model.insertNodeInto(node[i], root, root.getChildCount());
			//遍历所有用户列表
			for(int j = 0; j < listUser.size(); j++)
			{
				DefaultMutableTreeTableNode leaafnode =
						new DefaultMutableTreeTableNode(listUser.get(j));
				//判断此用户组是否在当前用户用户组列表中
				if(findUserGroupId(listUserGroup.get(i).getId(), listUser.get(j).getGroups()))
				{
					model.insertNodeInto(leaafnode, node[i], node[i].getChildCount());
				}
			}
		}
		// 展开所有节点
		userTable.expandAll();
		userTable.setHorizontalScrollEnabled(true);
		userTable.packTable(-1);
	}

	/**
	 * 查询输入的用户组是否存在于用户组列表中
	 * @param groupId 输入项：用户组ID
	 * @param groupIdList 输入项：用户组ID集合
	 * @return 返回项：
	 * <ul>
	 * <li>true：输入用户组ID存在于用户组ID列表中</li>
	 * <li>false：输入用户组ID不存在于用户组ID列表中</li>
	 * </ul>
	 */
	private boolean findUserGroupId(int groupId, List<Integer> groupIdList)
	{
		for(Integer id : groupIdList)
		{
			if(groupId == id)
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 搜索文档事件监听器。
	 */
	private class SearchDocumentListener implements DocumentListener
	{
		private PatternModel patternModel;
		
		SearchDocumentListener()
		{
			patternModel = new PatternModel();
		}
		
		/**
		 * 搜索实现。
		 */
		private void search()
		{
			patternModel.setRawText(userSearch.getText());
			HighlightPredicate predicate = new SearchPredicate(patternModel.getPattern(), -1, -1);
			matchHighlighter.setHighlightPredicate(predicate);
		}
	

		@Override
		public void insertUpdate(javax.swing.event.DocumentEvent e)
		{
			search();
		}

		@Override
		public void removeUpdate(javax.swing.event.DocumentEvent e)
		{
			search();
		}

		@Override
		public void changedUpdate(javax.swing.event.DocumentEvent e)
		{
			search();
		}
	}

	// <editor-fold defaultstate="collapsed" desc="设置用户表显示模型">
	private class UserTableModel extends DefaultTreeTableModel
	{
		/**
		 * 保存用户表的列名
		 */
		private List<String> usercolumns;

		/**
		 * 初始化TreeTable
		 * @param root 根节点
		 */
		private UserTableModel(DefaultMutableTreeTableNode root)
		{
			super(root);
			usercolumns = new ArrayList();
			usercolumns.add(rm.getString("table.user.name"));
			usercolumns.add(rm.getString("table.user.alias"));
			usercolumns.add(rm.getString("table.user.workplace"));
			usercolumns.add(rm.getString("table.user.state"));
			usercolumns.add(rm.getString("table.user.status"));
			usercolumns.add(rm.getString("table.user.oneoffPass"));
			usercolumns.add(rm.getString("table.user.passValid"));
			usercolumns.add(rm.getString("table.user.userValid"));
			usercolumns.add(rm.getString("table.user.lastLoginTime"));
		}

		/**
		 * 根据当前节点,列数设置每单元格节点
		 * @param node 当前节点
		 * @param column 当前列
		 * @return 返回每单元格内容
		 */
		@Override
		public Object getValueAt(Object node, int column)
		{
			//获得node节点的UserObject对象
			Object nodeObject =
					((DefaultMutableTreeTableNode)node).getUserObject();
			if(nodeObject instanceof UserGroup)
			{
				UserGroup userGroup = (UserGroup)nodeObject;
				switch(column)
				{
					case 0:
						//显示用户组名称
						return userGroup.getName();
					case 1:
						return "";
				}
			}
			//用户对象
			if(nodeObject instanceof User)
			{
				User user = (User)nodeObject;
				switch(column)
				{
					case 0:
						//显示用户名
						return user.getName();
					case 1:
						//显示用户名
						return user.getAliasName();
					case 2:
						//显示工作单位
						return user.getWorkplace();
					case 3:
						//显示用户状态
						return "";
					case 4:
						//在线状态
						return "";
					case 5:
						//一次性密码
						switch(user.getOneoffPassword())
						{
							case 0:
								return rm.getString("no_oneOfPwd");
							case 1:
								return rm.getString("oneOfPwd");
						}
					case 6:
						//密码过期
						if(user.getPwdValid() == 0)
						{
							return rm.getString("pwd_valid_forever");
						}
						else
						{
							return user.getPwdValid() + rm.getString("mb.day");
						}
					case 7:
						//密码有效期
						return user.getValidTime();
					case 8:
						return user.getLastLoginTime();
				}
			}
			return null;
		}

		@Override
		public void setValueAt(Object value, Object node, int column)
		{

			//获得node节点的UserObject对象
			Object nodeObject =
					((DefaultMutableTreeTableNode)node).getUserObject();
			//用户对象
			if(nodeObject instanceof User)
			{
				User user = (User)nodeObject;
				switch(column)
				{
					case 0:
						break;
					case 1:
						user.setState((Integer)value);
						break;
				}
			}
		}

		/**
		 * 设置列名
		 * @param column 当前列数
		 * @return 返回当前列,列名
		 */
		@Override
		public String getColumnName(int column)
		{
			return usercolumns.get(column);
		}

		/**
		 * 获得列数
		 * @return 返回表列数
		 */
		@Override
		public int getColumnCount()
		{
			return usercolumns.size();
		}
	}
    // </editor-fold>

	/**
	 * 获得当前选择的用户节点对象
	 * @return 返回项：选择的User对象
	 */
	private User getSelectUserNode()
	{
		//获得当前选择的行数
		int indexRow = userTable.getSelectedRow();
		if(indexRow == -1)
		{
			JOptionPane.showMessageDialog(this, 
					rm.getString("mb.text.selectuser"), 
					rm.getString("mb.tip"), 
					JOptionPane.INFORMATION_MESSAGE);
			return null;
		}
		//当前选择的节点路径
		TreePath nodePath =
				userTable.getTreeSelectionModel().getSelectionPath();
		//获得当前选择的节点
		DefaultMutableTreeTableNode selectedNode =
				(DefaultMutableTreeTableNode)nodePath.getLastPathComponent();
		//判断当前选择的是用户还是用户组
		if(selectedNode.getUserObject() instanceof UserGroup)
		{
			JOptionPane.showMessageDialog(this, 
					rm.getString("mb.text.selectuser"), 
					rm.getString("mb.tip"), 
					JOptionPane.INFORMATION_MESSAGE);
		}
		else if(selectedNode.getUserObject() instanceof User)
		{//如果选择的是用户,则将该用户的信息保存到pram中
			User userInfo = (User)selectedNode.getUserObject();
			return userInfo;
		}
		return null;
	}
	
	/**
	 * 获取当前选择的用户列表。
	 * @return 选择的用户列表
	 */
	private List<User> getSelectedUser()
	{
		List<User> listSelectedUser = null;
		int[] rows = userTable.getSelectedRows();
		for(int row : rows)
		{
			TreePath path = userTable.getPathForRow(row);
			DefaultMutableTreeTableNode node =
					(DefaultMutableTreeTableNode)path.getLastPathComponent();
			if(node.getUserObject() instanceof User)
			{
				if(listSelectedUser == null)
				{
					listSelectedUser = new ArrayList();
				}
				listSelectedUser.add((User)node.getUserObject());
			}
		}
		return listSelectedUser != null ? listSelectedUser : Collections.EMPTY_LIST;
	}

	/**
	 * 退出
	 */
	@Action
	public void exit()
	{
		dialog.dispose();
		dialog = null;
		listUserGroup.clear();
		listUser.clear();
		listRole.clear();
	}

	/**
	 * 重置密码
	 */
	@Action
	public void resetPassword()
	{
		//获得选中的用户节点对象
		User userInfo = getSelectUserNode();
		if(userInfo == null)
		{
			return;
		}

		//获取选中的用户名
		String userName = userInfo.getName();
		//提示用户确认重置,并获得用户操作0:确认重置1:取消重置
		int type = JOptionPane.showConfirmDialog(this, 
				rm.getString("mb.text.resetpassword.select1", userName), 
				rm.getString("mb.tip"), 
				JOptionPane.YES_NO_OPTION);
		//如果用户确认重置
		if(type == 0)
		{
			//执行重置操作
//			ProcessData out = OmcProcessor.process(Command.user, Command.resetPassword, userName);
//			//根据操作返回结果,输出用户提示
//			if(out.getState() == 0)
//			{
//				JOptionPane.showMessageDialog(this, 
//						rm.getString("mb.text.resetpassword.error"), 
//						rm.getString("mb.tip"), 
//						JOptionPane.WARNING_MESSAGE);
//			}
//			//成功
//			else if(out.getState() == 1)
//			{
//				JOptionPane.showMessageDialog(this, 
//						rm.getString("mb.text.resetpassword.success"), 
//						rm.getString("mb.tip"), 
//						JOptionPane.INFORMATION_MESSAGE);
//			}
		}
	}

	/**
	 * 新增用户
	 */
	@Action//(block = Task.BlockingScope.ACTION)
	public void addUser()
	{
		// 创建新增用户对象
		User userInfo = new User();
		userInfo.setId(-1);
		// 用户编辑窗体
		UserEditor window = new UserEditor(SingleFrameApplication.getInstance().getMainFrame(), 
				true, true, userInfo, listUserGroup, listRole);
		SingleFrameApplication.getInstance().show(window);
		//判断是否处理成功
		if(window.isok)
		{
			listUser.add(userInfo);
			createUserTable();
		}
	}

	/**
	 * 重置密码
	 */
	@Action
	public void modifyPassword()
	{
		// 默认密码
		String[] passwords =
		{
			"Jk123!", "Jk123!"
		};
		// 编辑用户操作
//		OmcProcessor.process(Command.user, Command.modifyPassword, passwords);
	}

	/**
	 * 编辑用户
	 */
	@Action
	public void editUser()
	{
		//获得选中的用户节点对象
		User userInfo = getSelectUserNode();
		if(userInfo == null)
		{
			return;
		}
		// copy用户对象
		User userCopy = userInfo.clone();
		// 在线用户和缺省管理用户不可编辑
		UserEditor window =	new UserEditor(SingleFrameApplication.getInstance().getMainFrame(), 
				true, true, 
				userCopy, listUserGroup, listRole);
		SingleFrameApplication.getInstance().show(window);
		if(window.isok)
		{
			// 更新数据
			userInfo.copyFrom(userCopy);
			// 刷新显示
			createUserTable();
		}
	}

	/**
	 * 编辑个人信息
	 */
	@Action
	public void modifyUserInfo()
	{
		//获得选择的用户
		User userInfo = getSelectUserNode();
		//不为Null
		if(userInfo == null)
		{
			return;
		}
		//用户实名
		String name = userInfo.getRealName();
		//编辑用户操作
//		OmcProcessor.process(Command.user, Command.modifyUserInfo, name);
	}

	/**
	 * 删除用户
	 */
	@Action
	public void delUser()
	{
		// 获得选中的用户
		List<User> listSelectedUser = getSelectedUser();
		if(listSelectedUser.isEmpty())
		{
			JOptionPane.showMessageDialog(null, 
					rm.getString("mb.text.selectuser"), 
					rm.getString("msg.prompt"), 
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		// 确认删除
		int type = JOptionPane.showConfirmDialog(null, 
				rm.getString("mb.text.deluserinfo.select"), 
				rm.getString("msg.confirm"), 
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		// 如果用户确认删除
		if(type != JOptionPane.YES_OPTION)
		{
			return;
		}
		// 输入参数
		List<String> input = new ArrayList();
		for(User user : listSelectedUser)
		{
			input.add(user.getName());
		}
		// 执行删除操作
//		ProcessData out = OmcProcessor.process(Command.user, 
//				Command.deleteUser, input);
		// 0/失败或用户在线
		// 1/成功
		// 根据操作结果输出用户提示
//		if(out.getData() != null)
//		{
//			int[] stateArray = (int[])out.getData();
//			StringBuilder sbd = new StringBuilder();
//			boolean refresh = false;
//			sbd.append("<html>");
//			for(int i = stateArray.length - 1; i >= 0; i--)
//			{
//				switch(stateArray[i])
//				{
//					case 0:
//						sbd.append(rm.getString("mb.text.deluserinfo.error", listSelectedUser.get(i).getName()));
//						break;
//					case 1:
//						sbd.append(rm.getString("mb.text.deluserinfo.success", listSelectedUser.get(i).getName()));
//						// 从缓存删除
//						refresh = true;
//						this.listUser.remove(listSelectedUser.get(i));
//						break;
//					case 6:
//						sbd.append(rm.getString("mb.useronline", listSelectedUser.get(i).getName()));
//						break;
//					case UserConstants.remove_user_group_forbidden:
//						sbd.append(rm.getString("msg.del.super.forbid", listSelectedUser.get(i).getName()));
//						break;
//				}
//				sbd.append("<br>");
//			}
//			sbd.append("</html>");
//			if(refresh)
//			{
//				createUserTable();
//			}
//			JOptionPane.showMessageDialog(null, 
//					sbd.toString(), 
//					rm.getString("msg.prompt"), 
//					JOptionPane.INFORMATION_MESSAGE);
//			return;
//		}
		JOptionPane.showMessageDialog(null, 
				rm.getString("msg.delete.error"), 
				rm.getString("msg.error"), 
				JOptionPane.ERROR_MESSAGE);
	}

	@Action
	public Task export()
	{
		// 检查列表数据
		if(userTable.getModel().getRowCount() == 0)
		{
			JOptionPane.showMessageDialog(null, 
					rm.getString("msg.export.empty"), 
					rm.getString("msg.prompt"), 
					JOptionPane.INFORMATION_MESSAGE);
			return null;
		}
		// 提取导出列
		List<String> listColumn = new ArrayList();
		for(int i = 0; i < userTable.getModel().getColumnCount(); i++)
		{
			listColumn.add(userTable.getModel().getColumnName(i));
		}
		TableModelRecordSet recordSet = new TableModelRecordSet(userTable.getModel());
		return new DefaultDataExportTask(listColumn, recordSet, null);
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        tableScrollPane = new javax.swing.JScrollPane();
        userTable = ComponentUtil.createTreeTable(true, false);
        toolBar = new javax.swing.JToolBar();
        btnAddUser = ComponentUtil.createToolBarButton();
        btnEditUser = ComponentUtil.createToolBarButton();
        btnDeleteUser = ComponentUtil.createToolBarButton();
        separator1 = new javax.swing.JToolBar.Separator();
        btnResetPassword = ComponentUtil.createToolBarButton();
        separator3 = new javax.swing.JToolBar.Separator();
        btnExport = ComponentUtil.createToolBarButton();
        btnQuit = new javax.swing.JButton();
        userSearch = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        setName("Form_UserManage_6"); // NOI18N

        tableScrollPane.setName("tableScrollPane"); // NOI18N

        userTable.setEditable(false);
        userTable.setName("userTable"); // NOI18N
        userTable.setTreeCellRenderer(new UserTreeCellRenderer());
        userTable.setTreeTableModel(new UserTableModel(new DefaultMutableTreeTableNode("root")));
        tableScrollPane.setViewportView(userTable);

        toolBar.setBorder(null);
        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        toolBar.setName("toolBar"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(prj.PrjApp.class).getContext().getActionMap(UserManage.class, this);
        btnAddUser.setAction(actionMap.get("addUser")); // NOI18N
        btnAddUser.setFocusable(false);
        btnAddUser.setName("btnAddUser"); // NOI18N
        toolBar.add(btnAddUser);

        btnEditUser.setAction(actionMap.get("editUser")); // NOI18N
        btnEditUser.setFocusable(false);
        btnEditUser.setName("btnEditUser"); // NOI18N
        toolBar.add(btnEditUser);

        btnDeleteUser.setAction(actionMap.get("delUser")); // NOI18N
        btnDeleteUser.setFocusable(false);
        btnDeleteUser.setName("btnDeleteUser"); // NOI18N
        toolBar.add(btnDeleteUser);

        separator1.setName("separator1"); // NOI18N
        toolBar.add(separator1);

        btnResetPassword.setAction(actionMap.get("resetPassword")); // NOI18N
        btnResetPassword.setFocusable(false);
        btnResetPassword.setName("btnResetPassword"); // NOI18N
        toolBar.add(btnResetPassword);

        separator3.setName("separator3"); // NOI18N
        toolBar.add(separator3);

        btnExport.setAction(actionMap.get("export")); // NOI18N
        btnExport.setFocusable(false);
        btnExport.setName("btnExport"); // NOI18N
        toolBar.add(btnExport);

        btnQuit.setAction(actionMap.get("exit")); // NOI18N
        btnQuit.setName("btnQuit"); // NOI18N

        userSearch.setName("userSearch"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(prj.PrjApp.class).getContext().getResourceMap(UserManage.class);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tableScrollPane)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(userSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 380, Short.MAX_VALUE)
                        .addComponent(btnQuit, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnQuit)
                    .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(userSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents
	/**
	 * 树节点的渲染器
	 * 将用户状态小图标加入到用户名前，使用不同的图标表示用户的在线状态
	 */
	private class UserTreeCellRenderer extends DefaultTreeCellRenderer
	{
		@Override
		public Component getTreeCellRendererComponent(javax.swing.JTree tree,
				Object value, boolean sel,
				boolean expanded, boolean leaf, int row,
				boolean hasFocus)
		{
			super.getTreeCellRendererComponent(tree, value, sel,
					expanded, leaf, row, hasFocus);
			//叶子节点
			if(leaf)
			{
				//获得当前节点
				DefaultMutableTreeTableNode node =
						(DefaultMutableTreeTableNode)value;
				//获得节点属性对象
				if(node.getUserObject() instanceof User)
				{
					User user = (User)node.getUserObject();
					if(user != null)
					{
//						switch(user.getStatus())
//						{
//							//在线
//							case OmcConstants.us_online:
//								setText(user.getName());
//								setIcon(onUserIcon);
//								break;
//							//不在线
//							case OmcConstants.us_offline:
//								setText(user.getName());
//								setIcon(offUserIcon);
//								break;
//						}
						setText(user.getName());
						setIcon(onUserIcon);
					}
					
				}
			}
			//获得当前节点
			DefaultMutableTreeTableNode node =
					(DefaultMutableTreeTableNode)value;
			//用户组对象
			if(node.getUserObject() instanceof UserGroup)
			{
				//获得用户对象
				UserGroup user = (UserGroup)node.getUserObject();
				//判断是否不为Null
				if(user != null)
				{
					setText(user.getName());
					setIcon(userGroupIcon);
				}
			}
			return this;
		}
	}
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddUser;
    private javax.swing.JButton btnDeleteUser;
    private javax.swing.JButton btnEditUser;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnQuit;
    private javax.swing.JButton btnResetPassword;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JToolBar.Separator separator1;
    private javax.swing.JToolBar.Separator separator3;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JTextField userSearch;
    private org.jdesktop.swingx.JXTreeTable userTable;
    // End of variables declaration//GEN-END:variables
}
