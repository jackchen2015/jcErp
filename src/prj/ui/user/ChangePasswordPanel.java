/*
 * Copyright 2010 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */

/*
 * ChangePasswordPanel.java
 *
 * Created on 2011-1-28, 10:06:35
 */
package prj.ui.user;

import com.hongxin.component.ComponentUtil;
import com.hongxin.util.service.ServiceUtils;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

/**
 * 修改密码界面。
 * @author fanhuigang
 */
public class ChangePasswordPanel extends org.jdesktop.swingx.JXPanel
{
	/**
	 * 密码策略。
	 */
	private CryptoGramPolicyInfo policy;
	
	/** Creates new form ChangePasswordPanel */
	public ChangePasswordPanel()
	{
		initComponents();
	}

	/**
	 * 显示用户名。
	 */
	protected void setUserName(String userName)
	{
		tfUser.setText(userName);
	}

	/**
	 * 获取缺省按钮组件。
	 * @return 缺省按钮组件
	 */
	protected JButton getDefaultButton()
	{
		return btnCommit;
	}
	
	/**
	 * 密码策略。
	 * @param policy the policy to set
	 */
	public void setPolicy(CryptoGramPolicyInfo policy)
	{
		this.policy = policy;
	}

	/**
	 * 提交修改的密码
	 */
	@Action
	public void commit()
	{
		char[] newPasswordChars = tfNewPass.getPassword();
		String newPassword = new String(newPasswordChars);
		String confirmPassword = new String(tfConfirmPass.getPassword());
		String oldPassword = new String(tfOldPass.getPassword());
		boolean modifyPassword;
		if(null == policy || policy.isEmpty())
		{
			policy = new CryptoGramPolicyInfo();
		}
		ResourceMap rm = Application.getInstance().getContext().getResourceMap(ChangePasswordPanel.class);
		//效验密码策略
		int checkResult = policy.modifyPasswordPolicy(oldPassword, newPassword, 
				confirmPassword, tfUser.getText());
		if(checkResult == CryptoGramPolicyInfo.normalPolicy)
		{
			modifyPassword = true;
		}
		else
		{
			// 使用安全策略类中的密码校验方法
			UserPasswordPolicyConverter uppc =
					new UserPasswordPolicyConverter(policy.getMinLength(), policy.getMaxLength());
			JOptionPane.showMessageDialog(this,
					uppc.convertForward(checkResult),
					rm.getString("msg.inputerror"),
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(!modifyPassword)
		{
			return;
		}
		// 发送修改用户密码命令帧
		String[] passwords =
		{
			oldPassword, newPassword
		};
		ProcessData out = OmcProcessor.process(Command.user, 
				Command.modifyPassword, passwords);
		if(out.getState() == UserConstants.pass_repeat_too_much)
		{
			JOptionPane.showMessageDialog(null,
					rm.getString("msg.repeat.too.much"),
					rm.getString("this.title"),
					JOptionPane.ERROR_MESSAGE);
		}
		else if(out.getState() == OmcConstants.coes_failure)
		{
			JOptionPane.showMessageDialog(null,
					rm.getString("msg.orgPassError"),
					rm.getString("this.title"),
					JOptionPane.INFORMATION_MESSAGE);
		}
		else if(out.getState() == OmcConstants.coes_success)
		{
			JOptionPane.showMessageDialog(null,
					rm.getString("msg.succeeded"),
					rm.getString("this.title"),
					JOptionPane.INFORMATION_MESSAGE);
			// 通知密码修改
			LoginService service =
					(LoginService)ServiceUtils.getInstance().getService(ServiceConstants.SVC_LOGIN);
			service.firePasswordChanged(newPasswordChars);
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
        java.awt.GridBagConstraints gridBagConstraints;

        lblUser = new javax.swing.JLabel();
        tfUser = new javax.swing.JTextField();
        lblOldPass = new javax.swing.JLabel();
        tfOldPass = new javax.swing.JPasswordField();
        lblNewPass = new javax.swing.JLabel();
        tfNewPass = new javax.swing.JPasswordField();
        lblConfirmPass = new javax.swing.JLabel();
        tfConfirmPass = new javax.swing.JPasswordField();
        btnCommit = ComponentUtil.createPaintableButton();

        setName("Form_AlarmAffirmAndClear"); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(ChangePasswordPanel.class);
        lblUser.setText(resourceMap.getString("lblUser.text")); // NOI18N
        lblUser.setName("lblUser"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        add(lblUser, gridBagConstraints);

        tfUser.setEditable(false);
        tfUser.setText(resourceMap.getString("tfUser.text")); // NOI18N
        tfUser.setName("tfUser"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 180;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        add(tfUser, gridBagConstraints);

        lblOldPass.setText(resourceMap.getString("lblOldPass.text")); // NOI18N
        lblOldPass.setName("lblOldPass"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        add(lblOldPass, gridBagConstraints);

        tfOldPass.setText(resourceMap.getString("tfOldPass.text")); // NOI18N
        tfOldPass.setName("tfOldPass"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 180;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        add(tfOldPass, gridBagConstraints);

        lblNewPass.setText(resourceMap.getString("lblNewPass.text")); // NOI18N
        lblNewPass.setName("lblNewPass"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        add(lblNewPass, gridBagConstraints);

        tfNewPass.setText(resourceMap.getString("tfNewPass.text")); // NOI18N
        tfNewPass.setName("tfNewPass"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 180;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        add(tfNewPass, gridBagConstraints);

        lblConfirmPass.setText(resourceMap.getString("lblConfirmPass.text")); // NOI18N
        lblConfirmPass.setName("lblConfirmPass"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        add(lblConfirmPass, gridBagConstraints);

        tfConfirmPass.setText(resourceMap.getString("tfConfirmPass.text")); // NOI18N
        tfConfirmPass.setName("tfConfirmPass"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 180;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        add(tfConfirmPass, gridBagConstraints);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(ChangePasswordPanel.class, this);
        btnCommit.setAction(actionMap.get("commit")); // NOI18N
        btnCommit.setText(resourceMap.getString("btnCommit.text")); // NOI18N
        btnCommit.setName("btnCommit"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 133, 5, 10);
        add(btnCommit, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCommit;
    private javax.swing.JLabel lblConfirmPass;
    private javax.swing.JLabel lblNewPass;
    private javax.swing.JLabel lblOldPass;
    private javax.swing.JLabel lblUser;
    private javax.swing.JPasswordField tfConfirmPass;
    private javax.swing.JPasswordField tfNewPass;
    private javax.swing.JPasswordField tfOldPass;
    private javax.swing.JTextField tfUser;
    // End of variables declaration//GEN-END:variables
}
