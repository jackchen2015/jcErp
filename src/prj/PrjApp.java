/*
 * PrjApp.java
 */

package prj;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import prj.user.LoginAdapter;
import prj.user.LoginEvent;
import prj.user.UserLogin;

/**
 * The main class of the application.
 */
public class PrjApp extends SingleFrameApplication {

	private static final Logger logger = Logger.getLogger(PrjApp.class.getName());
	private JFrame frameLogin;
	private String logonUser;

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
//        show(new PrjView(this));
		// 注册shutdown hook
		Runtime.getRuntime().addShutdownHook(new ShutdownHook());
		frameLogin = new JFrame(getContext().getResourceMap().getString("login.title"));
		frameLogin.setIconImage(getContext().getResourceMap().getImageIcon("login.icon").getImage());
		frameLogin.setResizable(false);
		frameLogin.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frameLogin.addWindowListener(new LoginFrameListener());
		UserLogin loginPanel = new UserLogin();
		loginPanel.addLoginListener(new LoginListenerImpl());
		frameLogin.getContentPane().add(loginPanel);
		loginPanel.setDefaultButton(frameLogin.getRootPane());
		frameLogin.pack();
		// 居中显示
		frameLogin.setLocationRelativeTo(null);
		frameLogin.setVisible(true);

	}
	class LoginFrameListener extends WindowAdapter
	{
		@Override
		public void windowClosing(WindowEvent e)
		{
			exit(e);
		}
	}

	private class ShutdownHook extends Thread
	{
		@Override
		public void run()
		{
			try
			{
//				UserDatabaseFactory.getInstance().close();
			}
			catch(Exception exp)
			{
			}
		}
	}

	private class LoginListenerImpl extends LoginAdapter
	{
		@Override
		public void loginSucceeded(LoginEvent source)
		{
			// 加载配置
//			B2bUtils.loadOptions();
			// 记录登录用户
			setLogonUser(source.getUser());
			logger.info("user " + logonUser + " login");
			// SAF会将首次显示的视图作为主视图
			show(new PrjView(PrjApp.this	));
			// 隐藏登录视图
			frameLogin.setVisible(false);
		}

		@Override
		public void loginCanceled(LoginEvent source)
		{
			System.exit(0);
		}
	}
    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }
	/**
	 * @return the logonUser
	 */
	public String getLogonUser()
	{
		return logonUser;
	}

	/**
	 * @param logonUser the logonUser to set
	 */
	public void setLogonUser(String logonUser)
	{
		this.logonUser = logonUser;
	}

    /**
     * A convenient static getter for the application instance.
     * @return the instance of PrjApp
     */
    public static PrjApp getApplication() {
        return Application.getInstance(PrjApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(PrjApp.class, args);
    }
}
