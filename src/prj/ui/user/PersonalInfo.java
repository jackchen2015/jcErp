/*
 * PersonalInfo.java
 *
 * Created on 2008年4月16日, 下午3:11
 */
package prj.ui.user;

import com.hongxin.saf.SingleFrameApplication;
import com.hongxin.util.GUIUtils;
import com.hongxin.util.service.ServiceUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

/**
 * 修改个人信息。
 * @author fanhuigang
 */
public class PersonalInfo extends JDialog
{
	/**
	 * 原始别名。
	 */
	private String originalAlias;

	private ResourceMap rm;

	public PersonalInfo(java.awt.Frame parent, boolean modal)
	{
		super(parent, modal);
		initComponents();
		initialize();
	}

	/**
	 * 初始化操作。
	 */
	private void initialize()
	{
		rm = Application.getInstance().getContext().getResourceMap(PersonalInfo.class);
		getRootPane().setDefaultButton(btnOk);
		tfAlias.setDocument(new ContentControlDocument());
		getCurUserInfo();
	}

	/**
	 * 获取当前用户信息。
	 */
	public void getCurUserInfo()
	{
		// 发送获取当前用户信息命令帧
		// 同步发送接收数据
//		ProcessData out = OmcProcessor.process(Command.user, Command.getUserInfo, null);
		if("" != null)
		{
			originalAlias = String.valueOf("");
			tfAlias.setText(originalAlias);
		}
	}

	/**
	 * 显示对话框。
	 * @return 用户选择结果
	 */
	public void showDialog()
	{
		GUIUtils.centerOnScreen(this);
		GUIUtils.addHideAction(this);
		getRootPane().setDefaultButton(btnOk);
		SingleFrameApplication.getInstance().show(this);
	}

	/**
	 * 取消
	 */
	@Action
	public void cancel()
	{
		//释放资源
		dispose();
	}

	/**
	 * 修改用户信息
	 */
	@Action
	public void modifyUserInfo()
	{
		//别名是否为空
		if(tfAlias.getText().isEmpty())
		{
			JOptionPane.showMessageDialog(this,
					rm.getString("msg.aliasname.mustNoNull"),
					rm.getString("msg.inputerror"),
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		//活的别名
		String alias = tfAlias.getText();
		boolean modifyAlias = !originalAlias.equals(alias);
		//获得密码
		String newPassword = new String(tfNewPassword.getPassword());
		String confirmPassword = new String(tfConfirmPassword.getPassword());
		String oldPassword = new String(tfOldPassword.getPassword());
		boolean modifyPassword;

		// 发送修改当前用户信息命令帧
//		if(modifyAlias)
//		{
//			UserInfo cui = new UserInfo();
//			cui.setName(alias);
//			//发送逻辑
//			ProcessData out =
//					OmcProcessor.process(Command.user, Command.modifyUserInfo, alias);
//			//成功
//			if(out.getState() == OmcConstants.coes_success)
//			{
//				originalAlias = tfAlias.getText();
//				JOptionPane.showMessageDialog(PersonalInfo.this,
//						rm.getString("msg.savealias.succeeded"),
//						rm.getString("this.title"),
//						JOptionPane.INFORMATION_MESSAGE);
//			}
//			//失败
//			else
//			{
//				tfAlias.setText(originalAlias);
//				JOptionPane.showMessageDialog(PersonalInfo.this,
//						rm.getString("msg.savealias.fail"),
//						rm.getString("this.title"),
//						JOptionPane.INFORMATION_MESSAGE);
//			}
//		}
		// 发送修改用户密码命令帧
//		if(modifyPassword)
//		{
//			//原始密码，新密码
//			String[] passwords =
//			{
//				oldPassword, newPassword
//			};
//			//发送
//			ProcessData out = OmcProcessor.process(Command.user, 
//					Command.modifyPassword, passwords);
//			// 如果最近5次密码重复
//			if(out.getState() == UserConstants.pass_repeat_too_much)
//			{	
//				JOptionPane.showMessageDialog(PersonalInfo.this,
//						rm.getString("msg.codeRepeatInFive"),
//						rm.getString("this.title"),
//						JOptionPane.ERROR_MESSAGE);
//				tfOldPassword.setText("");
//				tfNewPassword.setText("");
//				tfConfirmPassword.setText("");
//			}
//			//失败
//			else if(out.getState() == OmcConstants.coes_failure)
//			{
//				JOptionPane.showMessageDialog(PersonalInfo.this,
//						rm.getString("msg.savepassword.fail"),
//						rm.getString("this.title"),
//						JOptionPane.INFORMATION_MESSAGE);
//				tfOldPassword.setText("");
//				tfNewPassword.setText("");
//				tfConfirmPassword.setText("");
//			}
//			//成功
//			else if(out.getState() == OmcConstants.coes_success)
//			{
//				JOptionPane.showMessageDialog(PersonalInfo.this,
//						rm.getString("msg.savepassword.succeeded"),
//						rm.getString("this.title"),
//						JOptionPane.INFORMATION_MESSAGE);
//				// 通知密码修改
//				LoginService service =
//						(LoginService)ServiceUtils.getInstance().getService(ServiceConstants.SVC_LOGIN);
//				service.firePasswordChanged(tfNewPassword.getPassword());
//				tfOldPassword.setText("");
//				tfNewPassword.setText("");
//				tfConfirmPassword.setText("");
//			}
//		}
		//如果密码也不修改，提示什么都没修改

	}
	//控制输入长度不超过20字符的模型
	class ContentControlDocument extends DefaultStyledDocument
	{
		@Override
		public void insertString(int offs, String str, AttributeSet ax)
		{
			try
			{
				int oldLen = super.getLength();
				int strLen = str.length();
				//最大长度20
				int len = oldLen + strLen - 20;
				if(len > 0)
				{
					//提示超过
					JOptionPane.showMessageDialog(null,
							rm.getString("msg.lengthOverLimit"),
							rm.getString("msg.tip"),
							JOptionPane.INFORMATION_MESSAGE);
					//补足到最大长度20
					int newLen = strLen - len;
					if(newLen > 0)
					{
						str = str.substring(0, newLen);
						super.insertString(offs, str, ax);
					}
					return;
				}
				super.insertString(offs, str, ax);
			}
			catch(BadLocationException ex)
			{
				Logger.getLogger(PersonalInfo.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        mainPanel = new javax.swing.JPanel();
        lblAlias = new javax.swing.JLabel();
        tfAlias = new javax.swing.JTextField();
        lblOldPassword = new javax.swing.JLabel();
        tfOldPassword = new javax.swing.JPasswordField();
        lblNewPassword = new javax.swing.JLabel();
        tfNewPassword = new javax.swing.JPasswordField();
        lblConfirmPassword = new javax.swing.JLabel();
        tfConfirmPassword = new javax.swing.JPasswordField();
        btnOk = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setName("personalInfoForm"); // NOI18N
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
                formWindowClosing(evt);
            }
        });

        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setLayout(new java.awt.GridBagLayout());

        lblAlias.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblAlias.setName("lblAlias"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 3, 5);
        mainPanel.add(lblAlias, gridBagConstraints);

        tfAlias.setMinimumSize(new java.awt.Dimension(60, 21));
        tfAlias.setName("tfAlias"); // NOI18N
        tfAlias.setPreferredSize(new java.awt.Dimension(80, 21));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 80;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        mainPanel.add(tfAlias, gridBagConstraints);

        lblOldPassword.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblOldPassword.setName("lblOldPassword"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 3, 5);
        mainPanel.add(lblOldPassword, gridBagConstraints);

        tfOldPassword.setMinimumSize(new java.awt.Dimension(60, 21));
        tfOldPassword.setName("tfOldPassword"); // NOI18N
        tfOldPassword.setPreferredSize(new java.awt.Dimension(60, 21));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        mainPanel.add(tfOldPassword, gridBagConstraints);

        lblNewPassword.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblNewPassword.setName("lblNewPassword"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 3, 5);
        mainPanel.add(lblNewPassword, gridBagConstraints);

        tfNewPassword.setMinimumSize(new java.awt.Dimension(60, 21));
        tfNewPassword.setName("tfNewPassword"); // NOI18N
        tfNewPassword.setPreferredSize(new java.awt.Dimension(60, 21));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        mainPanel.add(tfNewPassword, gridBagConstraints);

        lblConfirmPassword.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblConfirmPassword.setName("lblConfirmPassword"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 3, 5);
        mainPanel.add(lblConfirmPassword, gridBagConstraints);

        tfConfirmPassword.setMinimumSize(new java.awt.Dimension(60, 21));
        tfConfirmPassword.setName("tfConfirmPassword"); // NOI18N
        tfConfirmPassword.setPreferredSize(new java.awt.Dimension(60, 21));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 80;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        mainPanel.add(tfConfirmPassword, gridBagConstraints);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(prj.PrjApp.class).getContext().getActionMap(PersonalInfo.class, this);
        btnOk.setAction(actionMap.get("modifyUserInfo")); // NOI18N
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(prj.PrjApp.class).getContext().getResourceMap(PersonalInfo.class);
        btnOk.setText(resourceMap.getString("btnOk.text")); // NOI18N
        btnOk.setName("btnOk"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 40, 0, 0);
        mainPanel.add(btnOk, gridBagConstraints);

        btnCancel.setAction(actionMap.get("cancel")); // NOI18N
        btnCancel.setText(resourceMap.getString("btnCancel.text")); // NOI18N
        btnCancel.setName("btnCancel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        mainPanel.add(btnCancel, gridBagConstraints);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void formWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosing
	{//GEN-HEADEREND:event_formWindowClosing
		cancel();
	}//GEN-LAST:event_formWindowClosing
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOk;
    private javax.swing.JLabel lblAlias;
    private javax.swing.JLabel lblConfirmPassword;
    private javax.swing.JLabel lblNewPassword;
    private javax.swing.JLabel lblOldPassword;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextField tfAlias;
    private javax.swing.JPasswordField tfConfirmPassword;
    private javax.swing.JPasswordField tfNewPassword;
    private javax.swing.JPasswordField tfOldPassword;
    // End of variables declaration//GEN-END:variables
}
