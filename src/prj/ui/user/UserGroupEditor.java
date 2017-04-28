/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * UserGroupEditor.java
 *
 * Created on 2009-8-18, 13:52:29
 */
package prj.ui.user;

import com.hongxin.component.ComponentUtil;
import com.hongxin.component.export.AbstractRecordSet;
import com.hongxin.component.renderer.ConverterRenderer;
import com.hongxin.util.service.ServiceUtils;
import java.util.ArrayList;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import com.hongxin.util.GUIUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;
import org.jdesktop.application.Application;
import org.jdesktop.application.Task;
import org.jdesktop.beansbinding.Validator;
import prj.PrjApp;
import prj.ui.basic.DefaultDataExportTask;
import prj.user.po.Role;
import prj.user.po.UserGroup;
import util.SQLiteCRUD;

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
		userGroupInfo.getListRoleId().clear();
		userGroupInfo.getListGroupId().clear();
		this.listRole = listRole;
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
//		for(Integer groupType : UserGroupTypeConverter.instance.getListSValue())
//		{
//			// 根据当前操作用户的最高类型控制列表项
//			if(groupType <= topType)
//			{
//				cmbGroupType.addItem(groupType);
//			}
//		}
//		for(Integer groupLevel : UserGroupLevelConverter.instance.getListSValue())
//		{
//			// 根据当前操作用户的最高等级控制列表项
//			if(groupLevel <= topLevel)
//			{
//				cmbGroupLevel.addItem(groupLevel);
//			}
//		}
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
//			ProcessData out = OmcProcessor.process(Command.user,
//					Command.getUserGroupDetails, userGroupInfo.getId());
			SQLiteCRUD sqlOpt = PrjApp.getApplication().getSQLiteCRUD();
			List<String> grpresults = sqlOpt.selectByCondition("user_group", "roleid", "id", userGroupInfo.getId()+"");
			
			UserGroup userGroup = new UserGroup();
			if(grpresults.size()>0)
			{
				String first = grpresults.get(0);				
				userGroup.setListRoleId(Arrays.asList(Integer.parseInt(first)));
			}
			// 不能使用copy方法
			//userGroupInfo.copyFrom(userGroup);
			userGroupInfo.setListRoleId(userGroup.getListRoleId());
			userGroupInfo.setListGroupId(userGroup.getListGroupId());//设备列表
		}
		else
		{
			// 设置标题，新建用户组
			this.setTitle(rm.getString("new.title"));
		}
		// 初始化角色
		initSysRole();
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
//			ProcessData out = OmcProcessor.process(Command.user, 
//					Command.getSysRole,	listSysRoleId.get(0));
//			if(out.getData() != null)
//			{
//				sysRoleFuncTree.setSelectedFunction((List)out.getData());
//			}
			SQLiteCRUD sqlOpt = PrjApp.getApplication().getSQLiteCRUD();
			List<String> results = sqlOpt.selectByCondition("user_roleaction", "aid", "rid", listSysRoleId.get(0)+"");
			List<Integer> actions = new ArrayList<Integer>();
			for(String re:results)
			{
				actions.add(Integer.parseInt(re));
			}
			sysRoleFuncTree.setSelectedFunction(actions);
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
        jScrollPane1 = new javax.swing.JScrollPane();
        sysRoleFuncTree = new prj.ui.user.SysRoleFuncTree();
        btnSelectRole = new javax.swing.JButton();
        btnOk = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        lblUserCount = new javax.swing.JLabel();
        spnUserCount = new javax.swing.JSpinner();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(prj.PrjApp.class).getContext().getResourceMap(UserGroupEditor.class);
        cbSelectAllFunction.setText(resourceMap.getString("cbSelectAllFunction.text")); // NOI18N
        cbSelectAllFunction.setName("cbSelectAllFunction"); // NOI18N

        functionTableScrollPane.setName("functionTableScrollPane"); // NOI18N

        treeTableFunction.setName("treeTableFunction"); // NOI18N
        functionTableScrollPane.setViewportView(treeTableFunction);

        cbCheckAll.setText(resourceMap.getString("cbCheckAll.text")); // NOI18N
        cbCheckAll.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cbCheckAll.setName("cbCheckAll"); // NOI18N

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

        lblGroupLevel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblGroupLevel.setText(resourceMap.getString("lblGroupLevel.text")); // NOI18N
        lblGroupLevel.setName("lblGroupLevel"); // NOI18N

        cmbGroupLevel.setName("cmbGroupLevel"); // NOI18N

        mainTabbedPane.setName("mainTabbedPane"); // NOI18N

        sysRolePanel.setName("sysRolePanel"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        sysRoleFuncTree.setCheckBoxEnabled(false);
        sysRoleFuncTree.setName("sysRoleFuncTree"); // NOI18N
        sysRoleFuncTree.setRootVisible(false);
        jScrollPane1.setViewportView(sysRoleFuncTree);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(prj.PrjApp.class).getContext().getActionMap(UserGroupEditor.class, this);
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
            .addGroup(sysRolePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(sysRolePanelLayout.createSequentialGroup()
                    .addGap(14, 14, 14)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 497, Short.MAX_VALUE)
                    .addGap(14, 14, 14)))
        );
        sysRolePanelLayout.setVerticalGroup(
            sysRolePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sysRolePanelLayout.createSequentialGroup()
                .addContainerGap(368, Short.MAX_VALUE)
                .addComponent(btnSelectRole)
                .addGap(4, 4, 4))
            .addGroup(sysRolePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(sysRolePanelLayout.createSequentialGroup()
                    .addGap(3, 3, 3)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 362, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(30, Short.MAX_VALUE)))
        );

        mainTabbedPane.addTab(resourceMap.getString("sysRolePanel.TabConstraints.tabTitle"), sysRolePanel); // NOI18N

        btnOk.setAction(actionMap.get("saveUserGroup")); // NOI18N
        btnOk.setName("btnOk"); // NOI18N

        btnCancel.setAction(actionMap.get("exit")); // NOI18N
        btnCancel.setText(resourceMap.getString("btnCancel.text")); // NOI18N
        btnCancel.setName("btnCancel"); // NOI18N

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
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                    .addComponent(btnOk))
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

	/**
	 * 保存
	 */
	@Action
	public void saveUserGroup()
	{
		// 用户组名称
		String userGroupName = tfGroupName.getText().trim();
//		ElementNameValidator validator = new ElementNameValidator(false, 
//				rm.getInteger("field.name.len.short"));
//		Validator.Result result = validator.validate(userGroupName);
//		if(result != null)
//		{
//			JOptionPane.showMessageDialog(this,
//					rm.getString("msg.group.name.error", result.
//					getDescription()),
//					rm.getString("msg.error"),
//					JOptionPane.ERROR_MESSAGE);
//			return;
//		}
		final CyclicBarrier barrier = new CyclicBarrier(2);
		// 检查设备和根子网匹配状态
		final Set<Integer> rootDistrict = new HashSet();
		// 计算选择的设备组包含的根子网
//		List<Integer> listGroupId = ((DeviceGroupTableModel)tableDeviceGroup.getModel()).getSelectedGroup();
//		if(!listGroupId.isEmpty())
//		{
//			// 计算根子网
//			try
//			{
//				barrier.reset();
//				// 新增用户组操作
//				OmcProcessor.process(Command.user, Command.getDeviceGroupRootDistrict,
//						listGroupId, new ProcessCallback()
//				{
//					@Override
//					public void processCompleted(final ProcessData out)
//					{
//						if(out.getData() != null)
//						{
//							rootDistrict.addAll((List)out.getData());
//						}
//						try
//						{
//							barrier.await(OmcConstants.sync_timeout, TimeUnit.MILLISECONDS);
//						}
//						catch(Exception exp)
//						{}
//					}
//				});
//				barrier.await(OmcConstants.sync_timeout, TimeUnit.MILLISECONDS);
//			}
//			catch(Exception exp)
//			{}
//		}

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
		userGroupInfo.setListRoleId(getListSysRole());
		// 设备组id
//		userGroupInfo.setListGroupId(listGroupId);
		// 根子网

		// 用户总数
		userGroupInfo.setUserCount((Integer)spnUserCount.getValue());
		//新建用户组
		if(!editMode)
		{
//			try
//			{
//				barrier.reset();
//				// 新增用户组操作
//				OmcProcessor.process(Command.user, Command.addUserGroup,
//						userGroupInfo, new ProcessCallback()
//				{
//					@Override
//					public void processCompleted(final ProcessData out)
//					{
//						try
//						{
//							barrier.await();
//						}
//						catch(Exception exp)
//						{}
//						javax.swing.SwingUtilities.invokeLater(new Runnable()
//						{
//							@Override
//							public void run()
//							{
//								// 0,失败
//								// 1，用户组名重复
//								// 2，成功
//								switch(out.getState())
//								{
//									//失败
//									case 0:
//										JOptionPane.showMessageDialog(null,
//												rm.
//												getString("mb.addusergroup.fail"),
//												rm.getString("mb.tip"),
//												JOptionPane.INFORMATION_MESSAGE);
//										break;
//									//重复
//									case 2:
//										JOptionPane.showMessageDialog(null,
//												rm.
//												getString("mb.usergroupname.repeat"),
//												rm.getString("mb.tip"),
//												JOptionPane.INFORMATION_MESSAGE);
//										break;
//									//成功
//									case 1:
//										JOptionPane.showMessageDialog(null,
//												rm.
//												getString("mb.addusergroup.success"),
//												rm.getString("mb.tip"),
//												JOptionPane.INFORMATION_MESSAGE);
//										//刷新用户组列表
//										userGroupInfo.setId((Integer)out.
//												getData());
//										UserGroupEditor.this.dispose();
//										isok = true;
//										break;
//								}
//							}
//						});
//					}
//				});
//				barrier.await(OmcConstants.sync_timeout2, TimeUnit.MILLISECONDS);
//			}
//			catch(Exception exp)
//			{
//				JOptionPane.showMessageDialog(null,
//						rm.getString("msg.timeout"),
//						rm.getString("msg.prompt"),
//						JOptionPane.ERROR_MESSAGE);
//			}
		}
		else
		{
			// 编辑用户组 
//			try
//			{
//				barrier.reset();
//				OmcProcessor.process(Command.user, Command.modifyUserGroup,
//						userGroupInfo, new ProcessCallback()
//				{
//					@Override
//					public void processCompleted(final ProcessData out)
//					{
//						try
//						{
//							barrier.await();
//						}
//						catch(Exception exp)
//						{}
//						javax.swing.SwingUtilities.invokeLater(new Runnable()
//						{
//							@Override
//							public void run()
//							{
//								// 0 失败
//								// 1 成功
//								switch(out.getState())
//								{
//									//失败
//									case 0:
//										JOptionPane.showMessageDialog(null,
//												rm.
//												getString("mb.editusergroup.fail"),
//												rm.getString("mb.tip"),
//												JOptionPane.INFORMATION_MESSAGE);
//										break;
//									//重复
//									case 2:
//										JOptionPane.showMessageDialog(null,
//												rm.
//												getString("mb.usergroupname.repeat"),
//												rm.getString("mb.tip"),
//												JOptionPane.INFORMATION_MESSAGE);
//										break;
//									//成功
//									case 1:
//										JOptionPane.showMessageDialog(null,
//												rm.
//												getString("mb.editusergroup.success"),
//												rm.getString("mb.tip"),
//												JOptionPane.INFORMATION_MESSAGE);
//										//刷新用户组列表
//										isok = true;
//										UserGroupEditor.this.dispose();
//										break;
//								}
//							}
//						});
//					}
//				});
//				barrier.await(OmcConstants.sync_timeout2, TimeUnit.MILLISECONDS);
//			}
//			catch(Exception exp)
//			{
//				JOptionPane.showMessageDialog(null,
//						rm.getString("msg.timeout"),
//						rm.getString("msg.prompt"),
//						JOptionPane.ERROR_MESSAGE);
//			}
		}
	}

	/**
	 * 退出
	 */
	@Action
	public void exit()
	{
		if(listSysRoleId != null)
		{
			listSysRoleId.clear();
		}
		if(listRole != null)
		{
			listRole = null;
		}
		dispose();
		isok = false;
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOk;
    private javax.swing.JButton btnSelectRole;
    private javax.swing.JCheckBox cbCheckAll;
    private javax.swing.JCheckBox cbSelectAllFunction;
    private javax.swing.JComboBox cmbGroupLevel;
    private javax.swing.JComboBox cmbGroupType;
    private javax.swing.JScrollPane functionTableScrollPane;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblGroupLevel;
    private javax.swing.JLabel lblGroupName;
    private javax.swing.JLabel lblGroupType;
    private javax.swing.JLabel lblUserCount;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTabbedPane mainTabbedPane;
    private javax.swing.JSpinner spnUserCount;
    /*
    private com.hongxin.omc.ui.user.SysRoleFuncTree sysRoleFuncTree;
    */
    private prj.ui.user.SysRoleFuncTree sysRoleFuncTree;
    private javax.swing.JPanel sysRolePanel;
    private javax.swing.JTextField tfDescription;
    private javax.swing.JTextField tfGroupName;
    private org.jdesktop.swingx.JXTreeTable treeTableFunction;
    // End of variables declaration//GEN-END:variables
}
