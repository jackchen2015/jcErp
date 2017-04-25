/*
 * Copyright 2013 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */

package prj.ui.user;

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
	public void showPersonalInfo()
	{
		PersonalInfo psnInfo = new PersonalInfo(SingleFrameApplication.getInstance().getMainFrame(), true);
		psnInfo.showDialog();
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
	public List<Integer> showUserSelectionDialog(List<Integer> listUserId)
	{
		UserSelectionPanel dialog = new UserSelectionPanel();
		return dialog.showDialog(listUserId);
	}

}
