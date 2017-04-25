/*
 * Copyright 2013 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */

package com.hongxin.omc.ui.user;

import com.hongxin.omc.operation.UserSession;
import com.hongxin.omc.ui.OmcPreferences;
import com.hongxin.omc.ui.services.LoginService;
import com.hongxin.omc.ui.services.ServiceConstants;
import com.hongxin.omc.ui.services.UserModule;
import com.hongxin.saf.SingleFrameApplication;
import com.hongxin.util.service.ServiceUtils;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JDialog;

/**
 * 用户模块接口实现。
 * @author fanhuigang
 * Created on 2014-3-5, 12:52:34
 */
public class UserModuleImpl implements UserModule
{
	@Override
	public void showCurrentUserPrivilege()
	{
		JDialog window = new CurrentUserRight(SingleFrameApplication.getInstance().
				getMainFrame(), true);
		SingleFrameApplication.getInstance().show(window);
	}

	@Override
	public void showDeviceGroupMgmt()
	{
		DeviceGroupPanel.showDialog();
	}

	@Override
	public void lockSession()
	{
		LockSession.getInstance().lock();
	}

	@Override
	public void showOnlineUser()
	{
		OnlineUserPanel.showDialog();
	}

	@Override
	public void showPersonalInfo()
	{
		PersonalInfo psnInfo = new PersonalInfo(SingleFrameApplication.getInstance().getMainFrame(), true);
		psnInfo.showDialog();
	}

	@Override
	public void showUserCryptogramPolicy()
	{
		JDialog window = UserCryptogramPolicy.getInstance(SingleFrameApplication.
				getInstance().getMainFrame(), true);
		SingleFrameApplication.getInstance().show(window);
	}

	@Override
	public void showUserGroupMgmt()
	{
		JDialog window = UserGroupManage.getInstance(SingleFrameApplication.
				getInstance().getMainFrame(), true);
		SingleFrameApplication.getInstance().show(window);
	}

	@Override
	public void showUserMgmt()
	{
		JDialog window = UserManage.getInstance(SingleFrameApplication.getInstance().
				getMainFrame(), true);
		SingleFrameApplication.getInstance().show(window);
	}
	
	@Override
	public void showRoleMgmt()
	{
		SysRoleMgmt dialog = new SysRoleMgmt();
		dialog.showDialog(false);
	}

	@Override
	public boolean showIdentityValidateDialog()
	{
		return IdentityValidate.showDialog();
	}
	
	@Override
	public void logout()
	{
		boolean canLogout = true;
		// 已登录并且需要进行身份验证时，显示身份认证界面
		if(UserSession.getInstance().getUserInfo().isEntered()
				&& OmcPreferences.getInstance().getBoolean("sys.logout.validate", Boolean.TRUE))
		{
			canLogout = showIdentityValidateDialog();
		}
		if(canLogout)
		{
			LoginService loginService =
					(LoginService)ServiceUtils.getInstance().getService(ServiceConstants.SVC_LOGIN);
			// 注销/主动/通知
			loginService.logout(false, true, true);
		}
	}
	
	@Override
	public List<Integer> showUserSelectionDialog(List<Integer> listUserId)
	{
		UserSelectionPanel dialog = new UserSelectionPanel();
		return dialog.showDialog(listUserId);
	}

	@Override
	public JComponent createLoginComponent()
	{
		return new LoginPanel();
	}
}
