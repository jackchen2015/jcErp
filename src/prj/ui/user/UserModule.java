/*
 * Copyright 2013 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */

package prj.ui.user;

import java.util.List;
import javax.swing.JComponent;

/**
 * 用户管理模块接口。
 * @author fanhuigang
 * Created on 2014-3-5, 12:46:33
 */
public interface UserModule
{
	/**
	 * 显示当前用户权限配置界面。
	 */
	void showCurrentUserPrivilege();
	
	/**
	 * 显示当前用户信息界面。
	 */
	void showPersonalInfo();
	
	/**
	 * 显示用户组管理界面。
	 */
	void showUserGroupMgmt();
	
	/**
	 * 显示用户管理界面。
	 */
	void showUserMgmt();
	
	/**
	 * 显示角色管理界面。
	 */
	void showRoleMgmt();
	
	/**
	 * 显示身份认证界面。
	 * @return 操作状态
	 */
	boolean showIdentityValidateDialog();

	
	/**
	 * 显示用户选择界面。
	 * @param listUserId 选择用户id列表
	 * @return 选择用户id列表或null
	 */
	List<Integer> showUserSelectionDialog(List<Integer> listUserId);
	
}
