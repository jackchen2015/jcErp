/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * UserEditor.java
 *
 * Created on 2009-8-18, 13:52:53
 */
package prj.ui.user;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import com.hongxin.util.GUIUtils;
import java.awt.Toolkit;
import java.util.Collections;
import java.util.Date;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import org.jdesktop.application.Application;
import prj.PrjApp;
import prj.user.po.Role;
import prj.user.po.User;
import prj.user.po.UserGroup;
import util.SQLiteCRUD;

/**
 * 用户编辑界面，完成新增/修改用户信息的基本操作
 * @author liming
 */
public class UserEditor extends javax.swing.JDialog
{
	/**
	 * 资源文件对象
	 */
	private ResourceMap rm;
	/**
	 * 用户对象。
	 */
	private User userInfo;
	/**
	 * 用户信息处理状态。
	 */
	public boolean isok = false;
	/**
	 * 用户组ID列表。
	 */
	private List<Integer> groupIdList;

	/**
	 * 用户组列表。
	 */
	private List<UserGroup> listUserGroup;
	/**
	 * 角色列表。
	 */
	private List<Role> listRole;
	/**
	 * 是否编辑模式。
	 */
	private boolean editMode;
	
	/**
	 * 初始化用户编辑界面。
	 * @param parent 顶层窗口对象
	 * @param modal 窗口模式
	 * @param editable 是否允许编辑
	 * @param user 用户对象
	 * @param listUserGroup 用户组列表
	 * @param listRole 角色列表
	 */
	public UserEditor(java.awt.Frame parent, boolean modal, boolean editable, 
			User user, List<UserGroup> listUserGroup, List<Role> listRole)
	{
		super(parent, modal);
		rm = Application.getInstance().getContext().getResourceMap(UserEditor.class);
		this.userInfo = user;
		this.listUserGroup = listUserGroup;
		this.listRole = listRole;
		this.isok = false;
		initComponents();
		// 缺省动作
		GUIUtils.addHideAction(UserEditor.this);
		getRootPane().setDefaultButton(btnOk);
		// 界面初始化
		btnOk.setEnabled(editable);
		initialize();
	}

	/**
	 * 初始化。
	 */
	private void initialize()
	{
		// 输入格式
		tfPassValidTime.setDocument(new DecimalOnlyDocument());

		// 设置窗体标题
		if(userInfo.getId() == -1)
		{
			editMode = false;
			// 新建
			this.setTitle(rm.getString("new.title"));
		}
		else
		{
			editMode = true;
			//编辑
			this.setTitle(rm.getString("edit.title"));
			setDefaultUserInfo();
		}
	}

	/**
	 * 编辑用户，则设置用户属性
	 */
	private void setDefaultUserInfo()
	{
		//设置用户基本属性
		tfUserName.setEditable(false);
		//用户名
		tfUserName.setText(userInfo.getName());
		//实名
		tfRealName.setText(userInfo.getRealName());
		//别名
		tfAliasName.setText(userInfo.getAliasName());
		//密码有效
		tfPassValidTime.setText(String.valueOf(userInfo.getPwdValid()));
		//密码有效期

		//工作单位
		tfWorkplace.setText(userInfo.getWorkplace());

		//用户组
		tfGroup.setText(getUserGroupIdString(userInfo.getGroups()));
	}

	/**
	 * 将GroupId列表转换成字符串形式
	 * @param groupIdList 用户组ID列表
	 * @return 用户组ID列表的字符串形式，每个ID使用","隔开
	 */
	private String getUserGroupIdString(List<Integer> groupIdList)
	{
		StringBuilder groupIds = new StringBuilder();
		//遍历用户组
		for(Integer id : groupIdList)
		{
			if(groupIds.length() > 0)
			{
				groupIds.append(",");
			}
			//获得用户组名
			String name = findUserGroupName(id);
			//如果不为Null，追加字符串
			if(!name.isEmpty())
			{
				groupIds.append(name);
			}
		}
		return groupIds.toString();
	}

	/**
	 * 查找用户组名
	 * @param groupId 组id
	 * @return 组名
	 */
	private String findUserGroupName(int groupId)
	{
		String groupName = "";
		//遍历用户组列表
		for(UserGroup userGroup : listUserGroup)
		{
			//如果组id 相同
			if(userGroup.getId() == groupId)
			{
				groupName = userGroup.getName();
				return groupName;
			}
		}
		return groupName;
	}

	/**
	 * 查找用户组Id
	 * @param groupName 用户组名
	 * @return 组id
	 */
	private int findUserGroupId(String groupName)
	{
		int groupId = -1;
		//遍历用户组列表
		for(UserGroup userGroup : listUserGroup)
		{
			//如果用户组名一致
			if(userGroup.getName().equals(groupName))
			{
				groupId = userGroup.getId();
				return groupId;
			}
		}
		return groupId;
	}

	/**
	 *  获得用户组的Id列表
	 * @param groupNames
	 * @return
	 */
	private List<Integer> getUserGroupIdList(String groupNames)
	{
		List<Integer> localGroupIdList = new ArrayList();
		String[] gourps = groupNames.split(",");
		//遍历用户组
		for(String gourpName : gourps)
		{
			//用户组名不为""
			if(!gourpName.equals(""))
			{
				//如果组Id找到
				if(findUserGroupId(gourpName) != -1)
				{
					//组id列表
					localGroupIdList.add(findUserGroupId(gourpName));
				}
			}
		}
		return localGroupIdList;
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

        lblUserName = new javax.swing.JLabel();
        tfUserName = new javax.swing.JTextField();
        lblRealName = new javax.swing.JLabel();
        tfRealName = new javax.swing.JTextField();
        lblAliasName = new javax.swing.JLabel();
        tfAliasName = new javax.swing.JTextField();
        lblValidTime = new javax.swing.JLabel();
        dateValidTime = new org.jdesktop.swingx.JXDatePicker();
        lblPassValidTime = new javax.swing.JLabel();
        tfPassValidTime = new javax.swing.JTextField();
        lblPass = new javax.swing.JLabel();
        tfPass = new javax.swing.JPasswordField();
        lblConfirmPass = new javax.swing.JLabel();
        tfConfirmPass = new javax.swing.JPasswordField();
        lblUserGroup = new javax.swing.JLabel();
        tfGroup = new javax.swing.JTextField();
        btnSelectGroup = new javax.swing.JButton();
        btnOk = new javax.swing.JButton();
        btnQuit = new javax.swing.JButton();
        lblWorkplace = new javax.swing.JLabel();
        tfWorkplace = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("Dialog_UserEditor_6"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosed(java.awt.event.WindowEvent evt)
            {
                formWindowClosed(evt);
            }
        });

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(prj.PrjApp.class).getContext().getResourceMap(UserEditor.class);
        lblUserName.setText(resourceMap.getString("lblUserName.text")); // NOI18N
        lblUserName.setName("lblUserName"); // NOI18N

        tfUserName.setName("tfUserName"); // NOI18N

        lblRealName.setText(resourceMap.getString("lblRealName.text")); // NOI18N
        lblRealName.setName("lblRealName"); // NOI18N

        tfRealName.setName("tfRealName"); // NOI18N

        lblAliasName.setText(resourceMap.getString("lblAliasName.text")); // NOI18N
        lblAliasName.setName("lblAliasName"); // NOI18N

        tfAliasName.setName("tfAliasName"); // NOI18N

        lblValidTime.setText(resourceMap.getString("lblValidTime.text")); // NOI18N
        lblValidTime.setName("lblValidTime"); // NOI18N

        dateValidTime.setFormats("yyyy-MM-dd");
        dateValidTime.setName("dateValidTime"); // NOI18N

        lblPassValidTime.setText(resourceMap.getString("lblPassValidTime.text")); // NOI18N
        lblPassValidTime.setName("lblPassValidTime"); // NOI18N

        tfPassValidTime.setText(resourceMap.getString("tfPassValidTime.text")); // NOI18N
        tfPassValidTime.setName("tfPassValidTime"); // NOI18N

        lblPass.setText(resourceMap.getString("lblPass.text")); // NOI18N
        lblPass.setName("lblPass"); // NOI18N

        tfPass.setText(resourceMap.getString("tfPass.text")); // NOI18N
        tfPass.setName("tfPass"); // NOI18N

        lblConfirmPass.setText(resourceMap.getString("lblConfirmPass.text")); // NOI18N
        lblConfirmPass.setName("lblConfirmPass"); // NOI18N

        tfConfirmPass.setText(resourceMap.getString("tfConfirmPass.text")); // NOI18N
        tfConfirmPass.setName("tfConfirmPass"); // NOI18N

        lblUserGroup.setText(resourceMap.getString("lblUserGroup.text")); // NOI18N
        lblUserGroup.setName("lblUserGroup"); // NOI18N

        tfGroup.setEditable(false);
        tfGroup.setName("tfGroup"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(prj.PrjApp.class).getContext().getActionMap(UserEditor.class, this);
        btnSelectGroup.setAction(actionMap.get("selectUserGroup")); // NOI18N
        btnSelectGroup.setText(resourceMap.getString("btnSelectGroup.text")); // NOI18N
        btnSelectGroup.setName("btnSelectGroup"); // NOI18N

        btnOk.setAction(actionMap.get("saveUser")); // NOI18N
        btnOk.setText(resourceMap.getString("btnOk.text")); // NOI18N
        btnOk.setName("btnOk"); // NOI18N

        btnQuit.setAction(actionMap.get("exit")); // NOI18N
        btnQuit.setName("btnQuit"); // NOI18N

        lblWorkplace.setText(resourceMap.getString("lblWorkplace.text")); // NOI18N
        lblWorkplace.setName("lblWorkplace"); // NOI18N

        tfWorkplace.setName("tfWorkplace"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblUserName)
                            .addComponent(lblAliasName)
                            .addComponent(lblRealName)
                            .addComponent(lblValidTime)
                            .addComponent(lblUserGroup)
                            .addComponent(lblConfirmPass)
                            .addComponent(lblPass)
                            .addComponent(lblPassValidTime)
                            .addComponent(lblWorkplace))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(tfWorkplace)
                                .addGap(1, 1, 1))
                            .addComponent(tfUserName)
                            .addComponent(tfRealName)
                            .addComponent(tfAliasName)
                            .addComponent(dateValidTime, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                            .addComponent(tfPassValidTime)
                            .addComponent(tfPass)
                            .addComponent(tfConfirmPass)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(tfGroup)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSelectGroup, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnOk)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnQuit, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(11, 11, 11))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUserName)
                    .addComponent(tfUserName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblRealName)
                    .addComponent(tfRealName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAliasName)
                    .addComponent(tfAliasName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblValidTime)
                    .addComponent(dateValidTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPassValidTime)
                    .addComponent(tfPassValidTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPass)
                    .addComponent(tfPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblConfirmPass)
                    .addComponent(tfConfirmPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUserGroup)
                    .addComponent(tfGroup, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSelectGroup))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblWorkplace)
                    .addComponent(tfWorkplace, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOk)
                    .addComponent(btnQuit))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosed(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosed
    {//GEN-HEADEREND:event_formWindowClosed
		isok = false;
    }//GEN-LAST:event_formWindowClosed

	/**
	 * 保存用户
	 */
	@Action
	public void saveUser()
	{
		// 用户名称
		String userName = tfUserName.getText().trim();
		// 用户名不为空，不包含\,?
		if(userName.contains("'") || userName.contains("\"") || userName.contains("?"))
		{
			JOptionPane.showMessageDialog(this, 
					rm.getString("mb.username.includeErrChar"), 
					rm.getString("mb.title.tip"), 
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
	    // 用户名不为“”
		if(userName.equals(""))
		{
			JOptionPane.showMessageDialog(this, 
					rm.getString("mb.username.null"), 
					rm.getString("mb.title.tip"), 
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

//		// 编辑用户的重名判断
//		if(userInfo.getId() != -1)
//		{
//			//用户名相同，重复
//			if(!userInfo.getName().equals(userName))
//			{
//				//用户名相同
//				if(UserManage.findUserNmae(userName))
//				{
//					JOptionPane.showMessageDialog(this, 
//							rm.getString("mb.username.repeat"), 
//							rm.getString("mb.title.tip"), 
//							JOptionPane.INFORMATION_MESSAGE);
//					return;
//				}
//			}
//		}
//		else
//		{
//			//用户组名相同，重复
//			if(UserManage.findUserNmae(userName))
//			{
//				JOptionPane.showMessageDialog(this, 
//						rm.getString("mb.username.repeat"), 
//						rm.getString("mb.title.tip"), 
//						JOptionPane.INFORMATION_MESSAGE);
//				return;
//			}
//		}
		// 用户实名
		String userRealName = tfRealName.getText().trim();
		// 用户实名长度
		if(userRealName.getBytes().length > rm.getInteger("field.name.len.short"))
		{
			JOptionPane.showMessageDialog(this, 
					rm.getString("mb.userrealname.length", rm.getInteger("field.name.len.short")), 
					rm.getString("mb.title.tip"), 
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		// 用户别名
		String userAliasName = tfAliasName.getText().trim();
		// 用户别名长度
		if(userAliasName.getBytes().length > rm.getInteger("field.name.len.short"))
		{
			JOptionPane.showMessageDialog(this, 
					rm.getString("mb.userAliasName.length", rm.getInteger("field.name.len.short")), 
					rm.getString("mb.title.tip"), 
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		// 状态

		// 新建用户
		if(!editMode)
		{
			// 获得密码
			String password1 = new String(tfPass.getPassword());
			String password2 = new String(tfConfirmPass.getPassword());
			// 验证是否一致
			if(!password1.equals(password2))
			{
				JOptionPane.showMessageDialog(this, 
						rm.getString("mb.password.conflict"), 
						rm.getString("mb.title.tip"), 
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			userInfo.setPassword(password1);
		}
		else
		{
			// 密码
			String password1 = new String(tfPass.getPassword());
			// 密码长度为0
			if(password1.length() == 0)
			{
				userInfo.setPassword(null);
			}
			else
			{
				// 密码
				String password2 = new String(tfConfirmPass.getPassword());
				if(!password1.equals(password2))
				{
					JOptionPane.showMessageDialog(this, 
							rm.getString("mb.password.conflict"), 
							rm.getString("mb.title.tip"), 
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}

				userInfo.setPassword(password1);
			}
		}

		if(tfPassValidTime.getText().length() == 0)
		{
			JOptionPane.showMessageDialog(this,
					rm.getString("msg.invalidPwdValid"),
					rm.getString("msg.inputerror"),
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		try
		{
			Integer.valueOf(tfPassValidTime.getText());
		}
		catch(NumberFormatException e)
		{
			JOptionPane.showMessageDialog(this,
					rm.getString("msg.numberFormatException"),
					rm.getString("msg.inputerror"),
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		userInfo.setPwdValid(Integer.valueOf(tfPassValidTime.getText()));
		// 所属用户组
		String userGroups = tfGroup.getText().trim();
		// 用户组不能为空
		if(userGroups.equals(""))
		{
			// 用户组必填
			JOptionPane.showMessageDialog(this, 
					rm.getString("mb.usergroup.must"), 
					rm.getString("mb.title.tip"), 
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		// 工作单位
		String workplace = tfWorkplace.getText().trim();
		// 编辑用户
		userInfo.setName(userName);
		userInfo.setRealName(userRealName);
		userInfo.setAliasName(userAliasName);

		userInfo.setGroups(getUserGroupIdList(userGroups));

		userInfo.setWorkplace(workplace);
		//isok = true;
		SQLiteCRUD sqlOpt = PrjApp.getApplication().getSQLiteCRUD();
		if(!editMode)
		{
			// 新增用户操作
			boolean result1 = sqlOpt.insert("user_user", new String[]{"number","username","password"}, new String[]{"233",userInfo.getName(), userInfo.getPassword()});
			List<Integer> ugs = userInfo.getGroups();
			boolean result2 = true;
			for(Integer ug:ugs)
			{
				result2 = result2 && sqlOpt.insert("user_groupuser", new String[]{"groupid","userid"}, new String[]{ug+"",userInfo.getId()+""});
			}
			if(result1&&result2)
			{
					JOptionPane.showMessageDialog(this, 
							rm.getString("mb.adduser.success"), 
							rm.getString("mb.tip"), 
							JOptionPane.INFORMATION_MESSAGE);
					isok = true;
					UserEditor.this.dispose();				
			}
//			ProcessData out = OmcProcessor.process(Command.user, 
//					Command.addUser, userInfo);
//			switch(out.getState())
//			{
//				case 0:
//					JOptionPane.showMessageDialog(this, 
//							rm.getString("mb.adduser.error"), 
//							rm.getString("mb.tip"), 
//							JOptionPane.INFORMATION_MESSAGE);
//					break;
//				case 1:
//					JOptionPane.showMessageDialog(this, 
//							rm.getString("mb.adduser.success"), 
//							rm.getString("mb.tip"), 
//							JOptionPane.INFORMATION_MESSAGE);
//					// 设置新增用户ID
//					userInfo.setId(Integer.parseInt(out.getData().toString()));
//					isok = true;
//					UserEditor.this.dispose();
//					break;
//				case 2:
//					// 用户名重复
//					JOptionPane.showMessageDialog(this, 
//							rm.getString("mb.username.repeat"), 
//							rm.getString("mb.tip"), 
//							JOptionPane.INFORMATION_MESSAGE);
//					break;
//				case 3:
//					// 用户别名重复
//					JOptionPane.showMessageDialog(this, 
//							rm.getString("mb.alais.repeat"), 
//							rm.getString("mb.tip"), 
//							JOptionPane.INFORMATION_MESSAGE);
//					break;
//				case 4:
//					// 用户名与用户别名重复
//					JOptionPane.showMessageDialog(this, 
//							rm.getString("mb.username.alias.repeat"), 
//							rm.getString("mb.tip"), 
//							JOptionPane.INFORMATION_MESSAGE);
//					break;
//				case 5:
//					// 用户别名与用户名重复
//					JOptionPane.showMessageDialog(this, 
//							rm.getString("mb.alias.username.repeat"), 
//							rm.getString("mb.tip"), 
//							JOptionPane.INFORMATION_MESSAGE);
//					break;
//				case 12:
//					// 超过用户总数限制
//					JOptionPane.showMessageDialog(this, 
//							rm.getString("mb.usercount.overflow"), 
//							rm.getString("mb.tip"), 
//							JOptionPane.INFORMATION_MESSAGE);
//					break;
//			}
		}
		else
		{
			// 编辑用户操作
			boolean result1 = sqlOpt.update("user_user", userInfo.getId()+"", "id", new String[]{"number","username","password"}, new String[]{"233",userInfo.getName(), userInfo.getPassword()});
			List<Integer> ugs = userInfo.getGroups();
			boolean result2 = true;
			for(Integer ug:ugs)
			{
				result2 = result2 && sqlOpt.update("user_groupuser", userInfo.getId()+"", "id", new String[]{"groupid","userid"}, new String[]{ug+"",userInfo.getId()+""});
			}
			if(result1&&result2)
			{
					JOptionPane.showMessageDialog(this, 
							rm.getString("mb.edituser.success"), 
							rm.getString("mb.tip"), 
							JOptionPane.INFORMATION_MESSAGE);
					isok = true;
					UserEditor.this.dispose();				
			}


//			ProcessData out =
//					OmcProcessor.process(Command.user, Command.modifyUser, userInfo);
////                0，	失败
////                1，	成功
//			switch(out.getState())
//			{
//				case 0:
//					JOptionPane.showMessageDialog(null, 
//							rm.getString("mb.edituser.error"), 
//							rm.getString("mb.tip"), 
//							JOptionPane.INFORMATION_MESSAGE);
//					break;
//				case 1:
//					JOptionPane.showMessageDialog(null, 
//							rm.getString("mb.edituser.success"), 
//							rm.getString("mb.tip"), 
//							JOptionPane.INFORMATION_MESSAGE);
//					isok = true;
//					UserEditor.this.dispose();
//					break;
//				case 2:
//					// 用户名重复
//					JOptionPane.showMessageDialog(null, 
//							rm.getString("mb.username.repeat"), 
//							rm.getString("mb.tip"), 
//							JOptionPane.INFORMATION_MESSAGE);
//					break;
//				case 3:
//					// 用户别名重复
//					JOptionPane.showMessageDialog(null, 
//							rm.getString("mb.alais.repeat"), 
//							rm.getString("mb.tip"), 
//							JOptionPane.INFORMATION_MESSAGE);
//					break;
//				case 4:
//					// 用户名与用户别名重复
//					JOptionPane.showMessageDialog(null, 
//							rm.getString("mb.username.alias.repeat"), 
//							rm.getString("mb.tip"), 
//							JOptionPane.INFORMATION_MESSAGE);
//					break;
//				case 5:
//					// 用户别名与用户名重复
//					JOptionPane.showMessageDialog(null, 
//							rm.getString("mb.alias.username.repeat"), 
//							rm.getString("mb.tip"), 
//							JOptionPane.INFORMATION_MESSAGE);
//					break;
//				case 6:
//					// 用户在线
//					JOptionPane.showMessageDialog(null, 
//							rm.getString("mb.useronline"), 
//							rm.getString("mb.tip"), 
//							JOptionPane.INFORMATION_MESSAGE);
//					break;
//				case 8:
//					// 最近5次密码重复
//					JOptionPane.showMessageDialog(null,
//							rm.getString("msg.codeRepeatInFive"),
//							rm.getString("this.title"),
//							JOptionPane.ERROR_MESSAGE);
//					break;
//				case 12:
//					// 超过用户总数限制
//					JOptionPane.showMessageDialog(this, 
//							rm.getString("mb.usercount.overflow"), 
//							rm.getString("mb.tip"), 
//							JOptionPane.INFORMATION_MESSAGE);
//					break;
//			}
		}
	}

	/**
	 * 退出，释放资源
	 */
	@Action
	public void exit()
	{
		isok = false;
		UserEditor.this.dispose();
	}

	/**
	 * 选择用户组
	 */
	@Action
	public void selectUserGroup()
	{
		List<UserGroup> listGroup = new ArrayList();
		// 遍历用户组
		for(UserGroup srcGroup : listUserGroup)
		{
			// 顶级用户组不在候选列表
			if(srcGroup.getGroupType() == UserGroup.TYPE_TOP)
			{
				continue;
			}
			listGroup.add(srcGroup);
		}
		// 默认选择用户组
		List<Integer> listSelectedGroup = editMode ? userInfo.getGroups() : Collections.EMPTY_LIST;
		UserGroupSelect dialog = new UserGroupSelect(listGroup, listSelectedGroup, listRole);
		if(dialog.showDialog())
		{
			// 获得新的用户组
			setDevList(dialog.getSelectGroup());
		}
	}

	/**
	 * 设置用户组列表
	 * @param listGroup
	 */
	public void setDevList(List<UserGroup> userGroupList)
	{
		//String oldValue = this.jUserGroup.getText();
		//如果为Null,创建
		if(groupIdList == null)
		{
			groupIdList = new ArrayList();
		}
		String groupId = "";
		//遍历用户组
		for(UserGroup item : userGroupList)
		{
			//用户组id字符串
			groupId = groupId + item.getName() + ",";
			this.groupIdList.add(item.getId());
		}
		this.tfGroup.setText(groupId);
	}

	/**
	 * 只能输入十进制数的文档模型
	 */
	class DecimalOnlyDocument extends PlainDocument
	{
		private boolean dot = false;//true:已经有小数点  0:还没有小数点

		@Override
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException
		{
			//数字字符串
			final String digitChars = "1234567890";
			StringBuilder tmp =
					new StringBuilder(super.getText(0, super.getLength()));
			tmp.insert(offs, str);
			if(offs == 0)
			{
				if(tmp.length() == 1)
				{
					//如果是数字，改变文档内容
					if(!(digitChars.indexOf(str) == -1))
					{
						super.insertString(offs, str, a);
					}
					else
					{
						//提示
						Toolkit.getDefaultToolkit().beep();//蜂鸣器响一声
					}
					return;
				}
				else
				{
					//如果是多字符
					super.insertString(offs, str, a);
					return;
				}
			}
			else
			{
				//获得内容
				String string = super.getText(0, super.getLength());
				//判断开始字符是0
				if(string.startsWith("0"))
				{
					Toolkit.getDefaultToolkit().beep();//蜂鸣器响一声
					return;
				}
			}
			//是数字，追加
			if(!(digitChars.indexOf(str) == -1))
			{
				super.insertString(offs, str, a);
			}
			else
			{
				Toolkit.getDefaultToolkit().beep();//蜂鸣器响一声
			}
		}
	}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnOk;
    private javax.swing.JButton btnQuit;
    private javax.swing.JButton btnSelectGroup;
    private org.jdesktop.swingx.JXDatePicker dateValidTime;
    private javax.swing.JLabel lblAliasName;
    private javax.swing.JLabel lblConfirmPass;
    private javax.swing.JLabel lblPass;
    private javax.swing.JLabel lblPassValidTime;
    private javax.swing.JLabel lblRealName;
    private javax.swing.JLabel lblUserGroup;
    private javax.swing.JLabel lblUserName;
    private javax.swing.JLabel lblValidTime;
    private javax.swing.JLabel lblWorkplace;
    private javax.swing.JTextField tfAliasName;
    private javax.swing.JPasswordField tfConfirmPass;
    private javax.swing.JTextField tfGroup;
    private javax.swing.JPasswordField tfPass;
    private javax.swing.JTextField tfPassValidTime;
    private javax.swing.JTextField tfRealName;
    private javax.swing.JTextField tfUserName;
    private javax.swing.JTextField tfWorkplace;
    // End of variables declaration//GEN-END:variables
}
