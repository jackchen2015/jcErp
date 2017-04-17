/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * UserLogin.java
 *
 * Created on 2009-6-25, 11:06:12
 */
package prj.user;

import java.io.File;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.event.EventListenerList;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import prj.PrjApp;

/**
 *
 * @author fanhuigang
 */
public class UserLogin extends javax.swing.JPanel
{
	private EventListenerList listListeners = new EventListenerList();

	private ResourceMap resourceMap;
	
	/** Creates new form UserLogin */
	public UserLogin()
	{
		resourceMap = PrjApp.getApplication().getContext().getResourceMap(UserLogin.class);
		initComponents();
		addLoginListener(new LoginListenerImpl());
	}

	public void setDefaultButton(JRootPane rootPane)
	{
		rootPane.setDefaultButton(btnLogin);
	}

	public void addLoginListener(LoginListener listener)
	{
		listListeners.add(LoginListener.class, listener);
	}

	void fireLoginStarted(final LoginEvent source)
	{
		// Guaranteed to return a non-null array
		Object[] listeners = listListeners.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for(int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if(listeners[i] == LoginListener.class)
			{
				((LoginListener)listeners[i + 1]).loginStarted(source);
			}
		}
	}

	void fireLoginSucceeded(final LoginEvent source)
	{
		// Guaranteed to return a non-null array
		Object[] listeners = listListeners.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for(int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if(listeners[i] == LoginListener.class)
			{
				((LoginListener)listeners[i + 1]).loginSucceeded(source);
			}
		}
	}

	void fireLoginFailed(final LoginEvent source)
	{
		// Guaranteed to return a non-null array
		Object[] listeners = listListeners.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for(int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if(listeners[i] == LoginListener.class)
			{
				((LoginListener)listeners[i + 1]).loginFailed(source);
			}
		}
	}

	void fireLoginCanceled(final LoginEvent source)
	{
		// Guaranteed to return a non-null array
		Object[] listeners = listListeners.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for(int i = listeners.length - 2; i >= 0; i -= 2)
		{
			if(listeners[i] == LoginListener.class)
			{
				((LoginListener)listeners[i + 1]).loginCanceled(source);
			}
		}
	}

	private class LoginListenerImpl extends LoginAdapter
	{
		@Override
		public void loginFailed(LoginEvent source)
		{
			JOptionPane.showMessageDialog(UserLogin.this, resourceMap.getString("msg.login.failed"),
				resourceMap.getString("msg.caption.failed"), JOptionPane.ERROR_MESSAGE);
		}
	}

	@Action
	public void actionLogin()
	{
		String userName = tfUserName.getText();
		String password = new String(tfPassword.getPassword());
		if(userName == null || userName.trim().length() == 0
				|| password == null || password.trim().length() == 0)
		{
			JOptionPane.showMessageDialog(UserLogin.this, resourceMap.getString("msg.login.input"),
				resourceMap.getString("msg.caption.tip"), JOptionPane.ERROR_MESSAGE);
			return;
		}
		// 查询用户在线状态
//		if(B2bUtils.isUserOnline(userName))
//		{
//			JOptionPane.showMessageDialog(UserLogin.this, resourceMap.getString("msg.login.online"),
//				resourceMap.getString("msg.caption.tip"), JOptionPane.ERROR_MESSAGE);
//			return;
//		}
		fireLoginStarted(new LoginEvent(this).setUser(userName));
//		if(UserDatabaseFactory.getInstance().authenticate(userName, password))
		if(true)
		{
			fireLoginSucceeded(new LoginEvent(this).setUser(userName));
		}
		else
		{
			fireLoginFailed(new LoginEvent(this).setUser(userName));
		}
	}

	@Action
	public void actionCancel()
	{
		fireLoginCanceled(new LoginEvent(this));
	}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        tfUserName = new javax.swing.JTextField();
        tfPassword = new javax.swing.JPasswordField();
        btnLogin = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setName("Form_Login"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(prj.PrjApp.class).getContext().getResourceMap(UserLogin.class);
        jLabel1.setIcon(resourceMap.getIcon("jLabel1.icon")); // NOI18N
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        tfUserName.setText(resourceMap.getString("tfUserName.text")); // NOI18N
        tfUserName.setName("tfUserName"); // NOI18N

        tfPassword.setText(resourceMap.getString("tfPassword.text")); // NOI18N
        tfPassword.setName("tfPassword"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(prj.PrjApp.class).getContext().getActionMap(UserLogin.class, this);
        btnLogin.setAction(actionMap.get("actionLogin")); // NOI18N
        btnLogin.setText(resourceMap.getString("btnLogin.text")); // NOI18N
        btnLogin.setName("btnLogin"); // NOI18N

        btnCancel.setAction(actionMap.get("actionCancel")); // NOI18N
        btnCancel.setText(resourceMap.getString("btnCancel.text")); // NOI18N
        btnCancel.setName("btnCancel"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1)
            .addGroup(layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(btnLogin)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancel))
                    .addComponent(tfUserName, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                    .addComponent(tfPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE))
                .addGap(87, 87, 87))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(tfUserName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel)
                    .addComponent(btnLogin))
                .addContainerGap(21, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnLogin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPasswordField tfPassword;
    private javax.swing.JTextField tfUserName;
    // End of variables declaration//GEN-END:variables
}
