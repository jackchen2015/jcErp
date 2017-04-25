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
import com.hongxin.saf.AsynBlockTask;
import com.hongxin.saf.SingleFrameApplication;

import com.hongxin.util.GUIUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
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
import prj.user.po.Role;
import prj.user.po.User;
import prj.user.po.UserGroup;

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
		PrivilegeController.checkPrivilege(this.getClass(), this, this);
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
			List userGrpList = new ArrayList();
			// 用户组
			if(userGrpList != null)
			{
				listUserGroup.clear();
				listUserGroup.addAll(userGrpList);
			}
			List userList = new ArrayList();
			// 用户
			if(userList != null)
			{
				listUser.clear();
				listUser.addAll(userList);
			}
			List roleList = new ArrayList();
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
		OmcProcessor.process(Command.user, Command.modifyPassword, passwords);
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
				true, userInfo.getStatus() != OmcConstants.us_online && userInfo.getId() != 0, 
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
		OmcProcessor.process(Command.user, Command.modifyUserInfo, name);
	}

	/**
	 * 锁定用户组
	 */
	@Action
	public void lockUser()
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
		for(User user : listSelectedUser)
		{
			// 不能锁定自己
			if(UserSession.getInstance().getUserInfo().getName().equalsIgnoreCase(user.getName()))
			{
				JOptionPane.showMessageDialog(this, 
						rm.getString("mb.cannotLockUnlock"), 
						rm.getString("msg.error"), 
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			// 检查状态，在线用户不允许锁定
			if(user.getStatus() == OmcConstants.us_online)
			{
				JOptionPane.showMessageDialog(this, 
						rm.getString("mb.lock.online.disable"), 
						rm.getString("msg.error"), 
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		// 确认操作
		int type = JOptionPane.showConfirmDialog(null, 
				rm.getString("msg.lock.unlock.confirm"), 
				rm.getString("msg.confirm"), 
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
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
		// 进行解锁/锁定操作
		ProcessData out = OmcProcessor.process(Command.user, Command.lockUser, 
				input);
		// 根据操作结果输出用户提示
		if(out.getData() != null)
		{
			int[] stateArray = (int[])out.getData();
			StringBuilder sbd = new StringBuilder();
			sbd.append("<html>");
			for(int i = stateArray.length - 1; i >= 0; i--)
			{
				switch(stateArray[i])
				{
					case 0:
						sbd.append(rm.getString("mb.user.unlock", listSelectedUser.get(i).getName()));
						listSelectedUser.get(i).setState(0);
						break;
					default:
						sbd.append(rm.getString("mb.user.lock", listSelectedUser.get(i).getName()));
						listSelectedUser.get(i).setState(1);
						break;
				}
				sbd.append("<br>");
			}
			sbd.append("</html>");
			JOptionPane.showMessageDialog(null, 
					sbd.toString(), 
					rm.getString("msg.prompt"), 
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		JOptionPane.showMessageDialog(null, 
				rm.getString("msg.lock.unlock.error"), 
				rm.getString("msg.error"), 
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * 复制用户
	 */
	@Action
	public void copyUser()
	{
		// 获得选中的用户节点对象
		User userInfo = getSelectUserNode();
		if(userInfo == null)
		{
			return;
		}
		// 创建拷贝用户对象
		CopyUser copyUser = new CopyUser();
		// 被拷贝的用户名
		copyUser.setCopyUserName(userInfo.getName());
		CopyUserDialog window =
				new CopyUserDialog(SingleFrameApplication.getInstance().getMainFrame(), true, copyUser);
		SingleFrameApplication.getInstance().show(window);
		// 判断是否处理成功
		if(window.isok)
		{
			// 复制用户操作
			ProcessData out = OmcProcessor.process(Command.user, 
					Command.copyUser, copyUser);
			// 根据状态，提示信息
			switch(out.getState())
			{
				case 0:
					JOptionPane.showMessageDialog(this, 
							rm.getString("mb.copyuser.error"), 
							rm.getString("mb.tip"), 
							JOptionPane.INFORMATION_MESSAGE);
					break;
				case 1:
					JOptionPane.showMessageDialog(this, 
							rm.getString("mb.copyuser.success"), 
							rm.getString("mb.tip"), 
							JOptionPane.INFORMATION_MESSAGE);
					// 设置新增用户ID
					User newUser = userInfo.clone();
					newUser.setName(copyUser.getName());
					newUser.setId(Integer.parseInt(out.getData().toString()));
					// 清除登录时间和别名
					newUser.setLastLoginTime(null);
					newUser.setAliasName(null);
					newUser.setStatus(OmcConstants.us_offline);
					// 刷新树
					listUser.add(newUser);
					createUserTable();
					break;
				case 2:
					JOptionPane.showMessageDialog(this, 
							rm.getString("mb.username.repeat"), 
							rm.getString("mb.tip"), 
							JOptionPane.INFORMATION_MESSAGE);
					break;
				case 4:
					// 用户名与用户别名重复
					JOptionPane.showMessageDialog(this, 
							rm.getString("mb.username.alias.repeat"), 
							rm.getString("mb.tip"), 
							JOptionPane.INFORMATION_MESSAGE);
					break;
				case 7:
					// 未找到被拷贝用户
					JOptionPane.showMessageDialog(this, 
							rm.getString("mb.userNotExist"), 
							rm.getString("mb.tip"), 
							JOptionPane.INFORMATION_MESSAGE);
					break;
				case 12:
					// 超过用户总数限制
					JOptionPane.showMessageDialog(this, 
							rm.getString("mb.usercount.overflow"), 
							rm.getString("mb.tip"), 
							JOptionPane.INFORMATION_MESSAGE);
					break;
			}
		}
	}

	/**
	 * 强制用户下线
	 */
	@Action
	public void forceUserOffLine()
	{
		// 获得选中的用户节点对象
		User userInfo = getSelectUserNode();
		if(userInfo == null)
		{
			return;
		}
		// 如果选择的用户不在线，则不允许
		if(userInfo.getStatus() != OmcConstants.us_online)
		{
			JOptionPane.showMessageDialog(this, 
					rm.getString("mb.notOnLine"), 
					rm.getString("mb.tip"), 
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		// 如果选择的用户是自己
		if(userInfo.getName().equals(UserSession.getInstance().getUserInfo().getName()))
		{
			JOptionPane.showMessageDialog(this, 
					rm.getString("mb.currentUserCanNotForceOffLine"), 
					rm.getString("mb.tip"), 
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		// 获取选中的用户名
		String userName = userInfo.getName();
		// 提示用户确认,并获得用户操作0:确认1:取消
		int type = JOptionPane.showConfirmDialog(this, 
				rm.getString("mb.text.forceOffLine",userName) , 
				rm.getString("mb.tip"), 
				JOptionPane.YES_NO_OPTION);
		if(type == 0)
		{
			// 执行操作
			ProcessData out = OmcProcessor.process(Command.user, 
					Command.forceOffline, userName);
			// 根据操作结果输出用户提示
			switch(out.getState())
			{
				// 错误
				case 0:
					JOptionPane.showMessageDialog(this, 
							rm.getString("mb.text.forceOffLine.error",userName), 
							rm.getString("mb.tip"), 
							JOptionPane.INFORMATION_MESSAGE);
					break;
				case 1:
				// 成功
					JOptionPane.showMessageDialog(this, 
							rm.getString("mb.text.forceOffLine.success",userName), 
							rm.getString("mb.tip"), 
							JOptionPane.INFORMATION_MESSAGE);
					//刷新树
//					listSelectedUser.remove(userInfo);//强制下线，并不代表要从树中删除用户。
					createUserTable();
					break;
			}
		}
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
		ProcessData out = OmcProcessor.process(Command.user, 
				Command.deleteUser, input);
		// 0/失败或用户在线
		// 1/成功
		// 根据操作结果输出用户提示
		if(out.getData() != null)
		{
			int[] stateArray = (int[])out.getData();
			StringBuilder sbd = new StringBuilder();
			boolean refresh = false;
			sbd.append("<html>");
			for(int i = stateArray.length - 1; i >= 0; i--)
			{
				switch(stateArray[i])
				{
					case 0:
						sbd.append(rm.getString("mb.text.deluserinfo.error", listSelectedUser.get(i).getName()));
						break;
					case 1:
						sbd.append(rm.getString("mb.text.deluserinfo.success", listSelectedUser.get(i).getName()));
						// 从缓存删除
						refresh = true;
						this.listUser.remove(listSelectedUser.get(i));
						break;
					case 6:
						sbd.append(rm.getString("mb.useronline", listSelectedUser.get(i).getName()));
						break;
					case UserConstants.remove_user_group_forbidden:
						sbd.append(rm.getString("msg.del.super.forbid", listSelectedUser.get(i).getName()));
						break;
				}
				sbd.append("<br>");
			}
			sbd.append("</html>");
			if(refresh)
			{
				createUserTable();
			}
			JOptionPane.showMessageDialog(null, 
					sbd.toString(), 
					rm.getString("msg.prompt"), 
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		JOptionPane.showMessageDialog(null, 
				rm.getString("msg.delete.error"), 
				rm.getString("msg.error"), 
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * 设备用户
	 */
	@Action(block = Task.BlockingScope.ACTION)
	public Task deviceUser()
	{
		// 获得选中的用户节点对象
		User userInfo = getSelectUserNode();
		if(userInfo == null)
		{
			return null;
		}
		// 显示设备用户配置界面
		DeviceUserPanel dup = new DeviceUserPanel(userInfo);
		if(!dup.showDialog(true))
		{
			// 取消配置
			return null;
		}
		// 保存配置
		return new SubmitDeviceUserTask(dup.getDeviceAndUserList());
	}

	/**
	 * 保存设备用户配置任务。
	 */
    private class SubmitDeviceUserTask extends AsynBlockTask<Object, Void> 
	implements ProcessCallback
	{
		/**
		 * 设备用户配置。
		 */
		private List<DeviceAndUser> listDeviceUser;
		/**
		 * 操作结果。
		 */
		private ProcessData output;
		
        SubmitDeviceUserTask(List<DeviceAndUser> listDeviceUser)
		{
            super(Application.getInstance(), OmcConstants.log_timeout);
			setTitle(SpeedUtil.getCommandName(Command.user, Command.setDeviceUser));
            this.listDeviceUser = listDeviceUser;
        }
		
        @Override
		protected Object doInBackground()
		{
			// 执行保存操作
			OmcProcessor.process(Command.user, Command.setDeviceUser, listDeviceUser, this);
			waitForReady();
			return null;
        }
		
		@Override
		public void processCompleted(ProcessData out)
		{
			this.output = out;
			setTaskFinished(true);
		}
		
        @Override
		protected void succeeded(Object result)
		{
            // 0，失败
            // 1，重名
            // 2，成功
            switch(output.getState())
            {
				//失败
				case 0:
					JOptionPane.showMessageDialog(null, 
					rm.getString("mb.save.error"), 
					rm.getString("mb.tip"), 
					JOptionPane.INFORMATION_MESSAGE);
				break;
				//重复
				case 2:
					JOptionPane.showMessageDialog(null, 
					rm.getString("mb.deviceUser.repeat"), 
					rm.getString("mb.tip"), 
					JOptionPane.INFORMATION_MESSAGE);
				break;
				//成功
				case 1:
					JOptionPane.showMessageDialog(null, 
					rm.getString("mb.save.success"), 
					rm.getString("mb.tip"), 
					JOptionPane.INFORMATION_MESSAGE);
				break;
            }
        }
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

    private class ExportTask extends org.jdesktop.application.Task<Object, Void> {
        ExportTask(org.jdesktop.application.Application app) {
            // Runs on the EDT.  Copy GUI state that
            // doInBackground() depends on from parameters
            // to ExportTask fields, here.
            super(app);
        }
        @Override protected Object doInBackground() {
            // Your Task's code here.  This method runs
            // on a background thread, so don't reference
            // the Swing GUI from here.
            return null;  // return your result
        }
        @Override protected void succeeded(Object result) {
            // Runs on the EDT.  Update the GUI based on
            // the result computed by doInBackground().
        }
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
