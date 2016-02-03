/*
 * PrjView.java
 */

package prj;

import com.hongxin.saf.SingleFrameApplication;
import com.hongxin.saf.SingleFrameView;
import com.hongxin.component.PainterUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import javax.swing.BorderFactory;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow;
import net.infonode.docking.util.ViewMap;
import net.infonode.util.Direction;
import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXPanel;
import prj.ui.DeviceTypePanel;
import prj.ui.DptPanel;
import prj.ui.HourWorkTypePanel;
import prj.ui.ManageTreePanel;
import prj.ui.ObjectDetailsPanel;
import prj.ui.RtSceneSTPanel;
import prj.ui.StylePartPanel;
import prj.ui.WorkLevelPanel;
import prj.ui.user.UserManage;
import prj.ui.user.UserModule;
import prj.user.UserPurview;
import util.Constants;
import util.GUIUtil;

/**
 * The application's main frame.
 */
public class PrjView extends SingleFrameView {
	/**
	 * Alloy主题缓存。
	 */
	private Map<String, Object> mapAlloyTheme;

    public PrjView(SingleFrameApplication app) {
        super(app);
		mapAlloyTheme = new HashMap<String, Object>();
		System.getProperties().setProperty("system.session.autolocking", "false");
		System.getProperties().setProperty("sys.laf.theme", "false");
		System.getProperties().setProperty("sys.laf.theme", "false");

//        initComponents();
		initialize();
        // status bar initialization - message timeout, idle icon and busy animation, etc
//        ResourceMap resourceMap = getResourceMap();
//        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");

    }

	
	/**
	 * 初始化视图。   
	 */
	public void initialize()
	{
		// 预初始化组件事件
		beforeInitComponents();
		// 初始化组件
		initComponentsImpl();
		// 自定义初始化操作
		final GlobalGlassBox glassService = new GlobalGlassBox();
		glassService.blockInput();
		javax.swing.SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				// after初始化组件
				afterInitComponents();
				glassService.unblockInput();
			}
		});
	}
	
	/**
	 * 初始化组件。
	 */
	protected void initComponentsImpl()
	{
		initComponents();
	}

	@Override
	public void showComponents()
	{
		// 根据权限定制界面
//		PrivilegeController.checkPrivilege(JOmcUIView.class, this, menuBar, baseToolBar);
		// 设置各个组件
		setComponent(mainPanel);
//		setMenuBar(menuBar);
//		setStatusBar(statusBar);
		setToolBar(jToolBar1);
	}
	
	// <editor-fold defaultstate="collapsed" desc="PCL">
	/**
	 * 系统属性PCL.
	 */
	private class SystemPropertyPCL implements PropertyChangeListener
	{
		@Override
		public void propertyChange(java.beans.PropertyChangeEvent evt)
		{}
	}
	
	/**
	 * 系统lafPCL.
	 */
	private class LafPropertyPCL implements PropertyChangeListener
	{
		@Override
		public void propertyChange(java.beans.PropertyChangeEvent evt)
		{
			if(evt.getPropertyName().equalsIgnoreCase("lookAndFeel"))
			{
				((JXPanel)getRootPane().getContentPane()).setBackgroundPainter(PainterUtil.defaultBackgroundPainter());
			}
		}
	}
	
	/**
	 * 系统idle属性PCL.
	 */
	private class IdlePropertyPCL implements PropertyChangeListener
	{
		@Override
		public void propertyChange(java.beans.PropertyChangeEvent evt)
		{
			// 进入idle状态时自动锁定
			if(((JXFrame)getFrame()).isIdle())
			{
				if(System.getProperty("system.session.autolocking").equals("false")
						//&& UserSession.getInstance().getUserInfo().isEntered()
						)
				{
					lockSession();
				}
			}
		}
	}
	// </editor-fold>

	/**
	 * 组件初始化前事件。
	 */
	protected void beforeInitComponents()
	{
		// global background
		((JXPanel)getRootPane().getContentPane()).setBackgroundPainter(PainterUtil.defaultBackgroundPainter());
		// 初始化服务
		registerService();
	}

	/**
	 * 组件初始化后事件。
	 */
	protected void afterInitComponents()
	{
		// 初始化视图
		initViews();
		// 初始化菜单
		initMenuBar();
		// 初始化工具栏
		initToolBar();
		// 初始化状态栏
		initStatusBar();
		// 初始化服务
		initService();
		// 系统属性PCL
		getApplication().addPropertyChangeListener(new SystemPropertyPCL());
		// 系统idle事件
		getFrame().addPropertyChangeListener("idle", new IdlePropertyPCL());
		// 系统laf事件
		UIManager.addPropertyChangeListener(new LafPropertyPCL());
	}

	/**
	 * 初始化菜单栏。
	 */
	protected void initMenuBar()
	{
		// 设置主题菜单状态
		Component[] themeMenus = jMenu8.getMenuComponents();
		if(!GUIUtil.isThemeSupported())
		{
			// laf不支持主题，禁用菜单
			for(Component themeMenu : themeMenus)
			{
				themeMenu.setEnabled(false);
			}
		}
		else
		{
			// laf支持主题，检查当前主题
			for(Component themeMenu : themeMenus)
			{
				if(themeMenu instanceof JMenuItem)
				{
					String command =
							((JMenuItem)themeMenu).getActionCommand();
					if(GUIUtil.isCurrentTheme(command))
					{
						((JMenuItem)themeMenu).setSelected(true);
						break;
					}
				}
			}
		}
	}

	/**
	 * 初始化工具栏。
	 */
	protected void initToolBar()
	{
		// place holder
	}
	
	/**
	 * 初始化状态栏。
	 */
	protected void initStatusBar()
	{
		statusBar.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED, 
		Color.WHITE, statusBar.getBackground(), statusBar.getBackground(), Color.GRAY));
	}

	/**
	 * 注册服务。
	 */
	protected void registerService()
	{
		// 注册服务
		// 地图服务
//		ServiceUtils.getInstance().register(ServiceConstants.SVC_MAP, ServiceLoader.
//				load(MapService.class).iterator().next());
//		// 告警服务
//		ServiceUtils.getInstance().register(ServiceConstants.SVC_ALARM, ServiceLoader.
//				load(AlarmService.class).iterator().next());
//		// 配置服务
//		ServiceUtils.getInstance().register(ServiceConstants.SVC_CONFIG, ServiceLoader.
//				load(ConfigService.class).iterator().next());
	}

	/**
	 * 初始化服务。
	 */
	protected void initService()
	{
		// 初始化登录服务
//		((LoginService)ServiceUtils.getInstance().getService(ServiceConstants.SVC_LOGIN)).
//				initialize();
//		// 初始化框架服务
//		((FrameService)ServiceUtils.getInstance().getService(ServiceConstants.SVC_FRAME)).
//				initialize();
//		// 初始化地图服务
//		((MapService)ServiceUtils.getInstance().getService(ServiceConstants.SVC_MAP)).
//				initialize();
//		// 初始化配置服务
//		((ConfigService)ServiceUtils.getInstance().getService(ServiceConstants.SVC_CONFIG)).
//				initialize();
	}
	
	/**
	 * 判断当前用户是否具有指定权限。
	 * @param id 权限ID
	 * @return 判断结果
	 */
	private boolean hasPurview(int id)
	{
		return UserPurview.getInstance().hasPurview(id);
	}

	/**
	 * 初始化静态视图。
	 */
	protected void initViews()
	{
		//初始化

		ResourceMap rm = getResourceMap();
		// 初始化dockingPanel
		SingleFrameApplication application = SingleFrameApplication.getInstance();
		// 初始化各个view
//		ConfigModule cm = ServiceLoader.load(ConfigModule.class).iterator().next();
//		TopoModule tm = ServiceLoader.load(TopoModule.class).iterator().next();
//		SystemModule sm = ServiceLoader.load(SystemModule.class).iterator().next();
//		OperaModule mm = ServiceLoader.load(OperaModule.class).iterator().next();
//		AlarmModule am = ServiceLoader.load(AlarmModule.class).iterator().next();
		// 对象树
//		application.addStaticView(OmcPerspectives.VIEW_DEVICETREE,
//				rm.getString("staticview.device"), rm.getIcon("image.treeView"),
//				cm.createObjectTreeComponent(), Direction.LEFT);
		application.addStaticView(Constants.MANAGETREE, "管理树", rm.getIcon("image.treeView"), 
				new ManageTreePanel(), Direction.LEFT);
		// 对象属性
		application.addStaticView(Constants.VIEW_PROPERTY,
				"详细信息", rm.getIcon("image.details"),
				new ObjectDetailsPanel(), Direction.LEFT);
		//管理树
//		application.addStaticView(OmcPerspectives.VIEW_MANAGETREE,
//				rm.getString("staticview.managetree"), rm.getIcon("image.treeView"),
//				cm.createManageTreeComponent(), Direction.LEFT);
		// 输出窗口
//		application.addStaticView(OmcPerspectives.VIEW_OUTPUT,
//				rm.getString("staticview.output"), rm.getIcon("image.eventlog"),
//				new OutputView(), Direction.DOWN);
		
//		if(hasPurview(UserPurview.View_Cluster))
//		{
//			// 群组视图addDynamicView
//			application.addStaticView(Constants.VIEW_DASHBOARD,
//					rm.getString("staticview.cluster"), rm.getIcon("image.treeView"),
//					cm.createClusterTreeComponent(), Direction.LEFT);
//		}
//		if(hasPurview(UserPurview.View_Manage))
//		{
//			application.addStaticView(Constants.MANAGETREE, 
//				rm.getString("staticview.managetree"), rm.getIcon("image.treeView"),
//				cm.createManageTreeComponent(), Direction.LEFT);
//		}
//		if(hasPurview(UserPurview.View_Alarm))
//		{
//			// 告警视图
//			application.addStaticView(OmcPerspectives.VIEW_ALARM,
//					rm.getString("staticview.alarm"), rm.getIcon("image.alert"),
//					am.createAlarmViewComponent(), Direction.DOWN);
//		}
//		if(hasPurview(UserPurview.View_Alarm))
//		{
//			// 告警统计视图
////			application.addStaticView(OmcPerspectives.VIEW_ALARMSTATIC,
////					rm.getString("staticview.alarmstatic"), rm.getIcon("image.perf"),
////					cm.createAlarmStaticViewComponent(), Direction.DOWN);
//		}
//		if(hasPurview(UserPurview.View_Config))
//		{
//			// 配置视图
//			application.addStaticView(OmcPerspectives.VIEW_CONFIGTREE,
//					rm.getString("staticview.config"), rm.getIcon("image.treeView"),
//					cm.createConfigTreeComponent(), Direction.LEFT);
//		}
//		if(hasPurview(UserPurview.View_File))
//		{
//			application.addStaticView(OmcPerspectives.VIEW_FILETREE,
//					rm.getString("staticview.file"), rm.getIcon("image.treeView"),
//					cm.createFileTreeComponent(), Direction.LEFT);
//		}
//		if(hasPurview(UserPurview.View_Topo))
//		{
//			application.addStaticView(OmcPerspectives.VIEW_SKETCH,
//					rm.getString("staticview.sketch"), rm.getIcon("image.hardware"),
//					tm.getFrameSketchComponent(), Direction.DOWN);
//		}
//		if(hasPurview(UserPurview.View_Map))
//		{
//			application.addStaticView(OmcPerspectives.VIEW_MAP,
//					rm.getString("staticview.map"), rm.getIcon("image.globe"),
//					tm.getMapComponent(), Direction.DOWN);
//		}
//		if(hasPurview(UserPurview.View_System))
//		{
//			application.addStaticView(OmcPerspectives.VIEW_SYSTEMMONITOR,
//					rm.getString("staticview.system"), rm.getIcon("image.monitor"),
//					sm.createSystemMonitorComponent(), Direction.DOWN);
//		}
		if(!hasPurview(UserPurview.View_BoardState))
		{
			application.addStaticView(Constants.VIEW_RTSCENEST,
					"实时信息", rm.getIcon("image.boardState"),
					new RtSceneSTPanel(), Direction.DOWN);
			application.addStaticView(6,
					"实时信息1", rm.getIcon("image.boardState"),
					new RtSceneSTPanel(), Direction.DOWN);
		}
//		if(hasPurview(UserPurview.View_OpenScene))
//		{
//			application.addStaticView(OmcPerspectives.VIEW_OPENSCENE,
//					rm.getString("staticview.openScene"), rm.getIcon("image.scene"),
//					mm.createOpenSceneViewComponent(), Direction.DOWN);
//		}
//		if(hasPurview(UserPurview.View_Dashboard))
//		{
//			application.addStaticView(Constants.VIEW_DASHBOARD,
//					rm.getString("staticview.dashboard"), rm.getIcon("image.star"),
//					new MultDashboardPanel(), Direction.DOWN);
//		}
		// 初始化RootWindow
		mainPanel.add(setDefaultLayout(), java.awt.BorderLayout.CENTER);
		// TODO 注册视图模式
	}

	/**
	 * 检查并获取Tab视图。
	 * @param tabWindow 中心视图
	 * @return 创建的或已存在的Tab视图
	 */
	private TabWindow getTabWindow(TabWindow tabWindow)
	{
		if(tabWindow == null)
		{
			tabWindow = new TabWindow();
		}
		return tabWindow;
	}
	
	/**
	 * 设置docking view的初始layout
	 * @return 唯一RootWindow对象
	 */
	protected RootWindow setDefaultLayout()
	{
		SingleFrameApplication application = SingleFrameApplication.getInstance();
		// 设置view的初始layout
		RootWindow rw = application.getDockingRootWindow();
		// 是否允许自定义布局
		Boolean enableLayout =
				Boolean.getBoolean(System.getProperty("system.layout.enable"));
		// 允许自定义布局并且布局版本没有过期时使用自定义布局
		//if(enableLayout && !OmcPreferences.getInstance().isLayoutExpired())
		if(enableLayout)
		{
//			try
//			{
				// 加载自定义布局
//				String layout = OmcPreferences.getInstance().getString("system.layout");
//				if(layout != null && !layout.isEmpty())
//				{
//					SafUtil.readLayout(rw, layout);
//					return rw;
//				}
//			}
//			catch(IOException | DataFormatException | NullPointerException exp)
//			{}
//			finally
//			{
//				application.afterRestoreLayout();
//			}
		}
		// 获取静态视图
		ViewMap vm = application.getStaticViews();
		// 缺省布局下
		// 设备树、设备属性垂直分割
		// 中心视图为多Tab页面
		TabWindow centerWindow = null;
		if(!hasPurview(UserPurview.View_Dashboard))
		{
			// dashboard视图
			centerWindow = getTabWindow(centerWindow);
//			centerWindow.addTab(vm.getView(Constants.VIEW_DASHBOARD));
			centerWindow.addTab(vm.getView(Constants.VIEW_RTSCENEST));
			centerWindow.addTab(vm.getView(6));
		}
		
		
		// 告警
//		if(hasPurview(UserPurview.View_Alarm))
//		{
//			centerWindow = getTabWindow(centerWindow);
//			centerWindow.addTab(vm.getView(OmcPerspectives.VIEW_ALARM));
//		}
//		// 拓扑
//		if(hasPurview(UserPurview.View_Topo))
//		{
//			centerWindow = getTabWindow(centerWindow);
//			centerWindow.addTab(vm.getView(OmcPerspectives.VIEW_SKETCH));
//		}
//		// 地图
//		if(hasPurview(UserPurview.View_Map))
//		{
//			centerWindow = getTabWindow(centerWindow);
//			centerWindow.addTab(vm.getView(OmcPerspectives.VIEW_MAP));
//		}
//		// 系统状态
//		if(hasPurview(UserPurview.View_System))
//		{
//			centerWindow = getTabWindow(centerWindow);
//			centerWindow.addTab(vm.getView(OmcPerspectives.VIEW_SYSTEMMONITOR));
//		}
//		// 单板状态
//		if(hasPurview(UserPurview.View_BoardState))
//		{
//			centerWindow = getTabWindow(centerWindow);
//			centerWindow.addTab(vm.getView(OmcPerspectives.VIEW_BOARDSTATE));
//		}
//		// 开站场景
//		if(hasPurview(UserPurview.View_OpenScene))
//		{
//			centerWindow = getTabWindow(centerWindow);
//			centerWindow.addTab(vm.getView(OmcPerspectives.VIEW_OPENSCENE));
//		}
		// 左侧视图
		TabWindow leftWindow = null;
		leftWindow = getTabWindow(leftWindow);
		/* 设备树 + 设备信息 */
		leftWindow.addTab(
				new SplitWindow(false,
				0.7f,
				/*NORTH*/
				vm.getView(Constants.MANAGETREE),
				/*SOUTH*/
				vm.getView(Constants.VIEW_PROPERTY)));
		// 群组树
		if(hasPurview(UserPurview.View_Cluster))
		{
		//	leftWindow = getTabWindow(leftWindow);
		//	leftWindow.addTab(vm.getView(OmcPerspectives.VIEW_CLUSTERTREE));
		}
		// 配置树
//		if(hasPurview(UserPurview.View_Config))
//		{
//			leftWindow = getTabWindow(leftWindow);
//			leftWindow.addTab(vm.getView(OmcPerspectives.VIEW_CONFIGTREE));
//		}
		// 文件树
		if(hasPurview(UserPurview.View_File))
		{
		//	leftWindow = getTabWindow(leftWindow);
		//	leftWindow.addTab(vm.getView(OmcPerspectives.VIEW_FILETREE));
		}
		//管理树
		if(!hasPurview(UserPurview.View_Manage))
		{
			leftWindow = getTabWindow(leftWindow);
			leftWindow.addTab(vm.getView(Constants.MANAGETREE));
		}
		// 下方视图
		TabWindow bottomWindow = null;
		bottomWindow = getTabWindow(bottomWindow);
//		bottomWindow.addTab(vm.getView(OmcPerspectives.VIEW_OUTPUT));
		// 设置布局
		if(centerWindow != null)
		{
			rw.setWindow(new SplitWindow(false,
					0.75f,
					/*TOP*/
					new SplitWindow(true, 0.25f, leftWindow, centerWindow),
					/*BOTTOM*/
					bottomWindow));
		}
		else
		{
			rw.setWindow(new SplitWindow(false,
					0.75f,
					/*TOP*/
					leftWindow,
					/*BOTTOM*/
					bottomWindow));
		}
		// 缺省最小化输出视图
//		if(vm.getView(OmcPerspectives.VIEW_OUTPUT) != null)
//		{
//			vm.getView(OmcPerspectives.VIEW_OUTPUT).minimize();
//		}
//		// 缺省显示页面(对象树和dashboard)
//		if(vm.getView(OmcPerspectives.VIEW_DEVICETREE) != null)
//		{
//			vm.getView(OmcPerspectives.VIEW_DEVICETREE).restoreFocus();
//		}
//		if(vm.getView(OmcPerspectives.VIEW_DASHBOARD) != null)
//		{
//			vm.getView(OmcPerspectives.VIEW_DASHBOARD).restoreFocus();
//		}
		return rw;
	}
	
	/**
	 * 将当前布局保存存为缺省布局
	 */
	public void saveAsDefaultLayout()
	{
		SingleFrameApplication application = SingleFrameApplication.getInstance();
		RootWindow rw = application.getDockingRootWindow();
		// 是否允许自定义布局
		Boolean enableLayout = Boolean.getBoolean(System.getProperty("system.layout.enable"));
		if(enableLayout)
		{
			// 保存自定义布局
//			OmcPreferences.getInstance().setProperty("system.layout", 
//					SafUtil.writeLayout(rw));
		}
	}
	
	@Action
	public void lockSession()
	{
//		try
//		{
//			UserModule um = ServiceLoader.load(UserModule.class).iterator().next();
//			um.lockSession();
//		}
//		catch(Exception exp)
//		{}
	}
	
	
	
    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = PrjApp.getApplication().getMainJFrame();
            aboutBox = new PrjAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        PrjApp.getApplication().show(aboutBox);
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

        mainPanel = new javax.swing.JPanel();
        menuBar = new javax.swing.JMenuBar();
        sysMenu = new javax.swing.JMenu();
        dptItem = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem21 = new javax.swing.JMenuItem();
        jMenuItem48 = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        jMenuItem47 = new javax.swing.JMenuItem();
        jMenu7 = new javax.swing.JMenu();
        jMenuItem50 = new javax.swing.JMenuItem();
        jMenuItem49 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem18 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem13 = new javax.swing.JMenuItem();
        jMenuItem14 = new javax.swing.JMenuItem();
        jMenuItem15 = new javax.swing.JMenuItem();
        jMenuItem16 = new javax.swing.JMenuItem();
        jMenuItem17 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenuItem26 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem19 = new javax.swing.JMenuItem();
        jMenuItem20 = new javax.swing.JMenuItem();
        jMenuItem22 = new javax.swing.JMenuItem();
        jMenuItem23 = new javax.swing.JMenuItem();
        jMenuItem24 = new javax.swing.JMenuItem();
        jMenu5 = new javax.swing.JMenu();
        jMenuItem27 = new javax.swing.JMenuItem();
        jMenuItem28 = new javax.swing.JMenuItem();
        jMenuItem29 = new javax.swing.JMenuItem();
        jMenuItem30 = new javax.swing.JMenuItem();
        jMenuItem31 = new javax.swing.JMenuItem();
        jMenu6 = new javax.swing.JMenu();
        jMenuItem32 = new javax.swing.JMenuItem();
        jMenuItem33 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem34 = new javax.swing.JMenuItem();
        jMenuItem35 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem36 = new javax.swing.JMenuItem();
        jMenuItem37 = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItem38 = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuItem39 = new javax.swing.JMenuItem();
        jMenuItem40 = new javax.swing.JMenuItem();
        jMenuItem41 = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItem42 = new javax.swing.JMenuItem();
        jMenuItem43 = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        jMenuItem44 = new javax.swing.JMenuItem();
        jMenuItem45 = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        jMenuItem46 = new javax.swing.JMenuItem();
        jMenu8 = new javax.swing.JMenu();
        jMenuItem51 = new javax.swing.JMenuItem();
        jMenuItem52 = new javax.swing.JMenuItem();
        jMenuItem53 = new javax.swing.JMenuItem();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem25 = new javax.swing.JMenuItem();
        statusBar = new org.jdesktop.swingx.JXStatusBar();
        jToolBar1 = new javax.swing.JToolBar();
        jButton1 = new javax.swing.JButton();
        jSeparator10 = new javax.swing.JToolBar.Separator();
        jButton2 = new javax.swing.JButton();
        jSeparator11 = new javax.swing.JToolBar.Separator();
        jButton3 = new javax.swing.JButton();
        jSeparator12 = new javax.swing.JToolBar.Separator();
        jButton4 = new javax.swing.JButton();

        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setLayout(new java.awt.BorderLayout());

        menuBar.setName("menuBar"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(prj.PrjApp.class).getContext().getResourceMap(PrjView.class);
        sysMenu.setText(resourceMap.getString("sysMenu.text")); // NOI18N
        sysMenu.setName("sysMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(prj.PrjApp.class).getContext().getActionMap(PrjView.class, this);
        dptItem.setAction(actionMap.get("showDptInfo")); // NOI18N
        dptItem.setText(resourceMap.getString("dptItem.text")); // NOI18N
        dptItem.setName("dptItem"); // NOI18N
        sysMenu.add(dptItem);

        jSeparator8.setName("jSeparator8"); // NOI18N
        sysMenu.add(jSeparator8);

        jMenuItem3.setAction(actionMap.get("workLevelAction")); // NOI18N
        jMenuItem3.setText(resourceMap.getString("jMenuItem3.text")); // NOI18N
        jMenuItem3.setName("jMenuItem3"); // NOI18N
        sysMenu.add(jMenuItem3);

        jMenuItem4.setAction(actionMap.get("hourWorkTypeAction")); // NOI18N
        jMenuItem4.setText(resourceMap.getString("jMenuItem4.text")); // NOI18N
        jMenuItem4.setName("jMenuItem4"); // NOI18N
        sysMenu.add(jMenuItem4);

        jMenuItem1.setText(resourceMap.getString("jMenuItem1.text")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        jMenuItem1.setVisible(false);
        sysMenu.add(jMenuItem1);

        jMenuItem21.setAction(actionMap.get("DeviceTypeAction")); // NOI18N
        jMenuItem21.setText(resourceMap.getString("jMenuItem21.text")); // NOI18N
        jMenuItem21.setName("jMenuItem21"); // NOI18N
        sysMenu.add(jMenuItem21);

        jMenuItem48.setAction(actionMap.get("StylePartAction")); // NOI18N
        jMenuItem48.setText(resourceMap.getString("jMenuItem48.text")); // NOI18N
        jMenuItem48.setName("jMenuItem48"); // NOI18N
        sysMenu.add(jMenuItem48);

        jSeparator9.setName("jSeparator9"); // NOI18N
        sysMenu.add(jSeparator9);

        jMenuItem47.setAction(actionMap.get("exit")); // NOI18N
        jMenuItem47.setText(resourceMap.getString("jMenuItem47.text")); // NOI18N
        jMenuItem47.setName("jMenuItem47"); // NOI18N
        sysMenu.add(jMenuItem47);

        menuBar.add(sysMenu);

        jMenu7.setText(resourceMap.getString("jMenu7.text")); // NOI18N
        jMenu7.setName("jMenu7"); // NOI18N

        jMenuItem50.setText(resourceMap.getString("jMenuItem50.text")); // NOI18N
        jMenuItem50.setName("jMenuItem50"); // NOI18N
        jMenu7.add(jMenuItem50);

        jMenuItem49.setText(resourceMap.getString("jMenuItem49.text")); // NOI18N
        jMenuItem49.setName("jMenuItem49"); // NOI18N
        jMenu7.add(jMenuItem49);

        jMenuItem2.setAction(actionMap.get("userManage")); // NOI18N
        jMenuItem2.setText(resourceMap.getString("jMenuItem2.text")); // NOI18N
        jMenuItem2.setName("jMenuItem2"); // NOI18N
        jMenu7.add(jMenuItem2);

        menuBar.add(jMenu7);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setText(resourceMap.getString("aboutMenuItem.text")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        jMenuItem5.setText(resourceMap.getString("jMenuItem5.text")); // NOI18N
        jMenuItem5.setName("jMenuItem5"); // NOI18N
        helpMenu.add(jMenuItem5);

        jMenuItem6.setText(resourceMap.getString("jMenuItem6.text")); // NOI18N
        jMenuItem6.setName("jMenuItem6"); // NOI18N
        helpMenu.add(jMenuItem6);

        jMenuItem7.setText(resourceMap.getString("jMenuItem7.text")); // NOI18N
        jMenuItem7.setName("jMenuItem7"); // NOI18N
        helpMenu.add(jMenuItem7);

        menuBar.add(helpMenu);

        jMenu1.setText(resourceMap.getString("jMenu1.text")); // NOI18N
        jMenu1.setName("jMenu1"); // NOI18N

        jMenuItem8.setText(resourceMap.getString("jMenuItem8.text")); // NOI18N
        jMenuItem8.setName("jMenuItem8"); // NOI18N
        jMenu1.add(jMenuItem8);

        jMenuItem9.setText(resourceMap.getString("jMenuItem9.text")); // NOI18N
        jMenuItem9.setName("jMenuItem9"); // NOI18N
        jMenu1.add(jMenuItem9);

        jMenuItem18.setText(resourceMap.getString("jMenuItem18.text")); // NOI18N
        jMenuItem18.setName("jMenuItem18"); // NOI18N
        jMenu1.add(jMenuItem18);

        jMenuItem10.setText(resourceMap.getString("jMenuItem10.text")); // NOI18N
        jMenuItem10.setName("jMenuItem10"); // NOI18N
        jMenu1.add(jMenuItem10);

        jMenuItem11.setText(resourceMap.getString("jMenuItem11.text")); // NOI18N
        jMenuItem11.setName("jMenuItem11"); // NOI18N
        jMenu1.add(jMenuItem11);

        menuBar.add(jMenu1);

        jMenu2.setText(resourceMap.getString("jMenu2.text")); // NOI18N
        jMenu2.setName("jMenu2"); // NOI18N

        jMenuItem13.setText(resourceMap.getString("jMenuItem13.text")); // NOI18N
        jMenuItem13.setName("jMenuItem13"); // NOI18N
        jMenu2.add(jMenuItem13);

        jMenuItem14.setText(resourceMap.getString("jMenuItem14.text")); // NOI18N
        jMenuItem14.setName("jMenuItem14"); // NOI18N
        jMenu2.add(jMenuItem14);

        jMenuItem15.setText(resourceMap.getString("jMenuItem15.text")); // NOI18N
        jMenuItem15.setName("jMenuItem15"); // NOI18N
        jMenu2.add(jMenuItem15);

        jMenuItem16.setText(resourceMap.getString("jMenuItem16.text")); // NOI18N
        jMenuItem16.setName("jMenuItem16"); // NOI18N
        jMenu2.add(jMenuItem16);

        jMenuItem17.setText(resourceMap.getString("jMenuItem17.text")); // NOI18N
        jMenuItem17.setName("jMenuItem17"); // NOI18N
        jMenu2.add(jMenuItem17);

        jMenuItem12.setText(resourceMap.getString("jMenuItem12.text")); // NOI18N
        jMenuItem12.setName("jMenuItem12"); // NOI18N
        jMenu2.add(jMenuItem12);

        jMenuItem26.setText(resourceMap.getString("jMenuItem26.text")); // NOI18N
        jMenuItem26.setName("jMenuItem26"); // NOI18N
        jMenu2.add(jMenuItem26);

        menuBar.add(jMenu2);

        jMenu3.setText(resourceMap.getString("jMenu3.text")); // NOI18N
        jMenu3.setName("jMenu3"); // NOI18N

        jMenuItem19.setText(resourceMap.getString("jMenuItem19.text")); // NOI18N
        jMenuItem19.setName("jMenuItem19"); // NOI18N
        jMenu3.add(jMenuItem19);

        jMenuItem20.setText(resourceMap.getString("jMenuItem20.text")); // NOI18N
        jMenuItem20.setName("jMenuItem20"); // NOI18N
        jMenu3.add(jMenuItem20);

        jMenuItem22.setText(resourceMap.getString("jMenuItem22.text")); // NOI18N
        jMenuItem22.setName("jMenuItem22"); // NOI18N
        jMenu3.add(jMenuItem22);

        jMenuItem23.setText(resourceMap.getString("jMenuItem23.text")); // NOI18N
        jMenuItem23.setName("jMenuItem23"); // NOI18N
        jMenu3.add(jMenuItem23);

        jMenuItem24.setText(resourceMap.getString("jMenuItem24.text")); // NOI18N
        jMenuItem24.setName("jMenuItem24"); // NOI18N
        jMenu3.add(jMenuItem24);

        menuBar.add(jMenu3);

        jMenu5.setText(resourceMap.getString("jMenu5.text")); // NOI18N
        jMenu5.setName("jMenu5"); // NOI18N

        jMenuItem27.setText(resourceMap.getString("jMenuItem27.text")); // NOI18N
        jMenuItem27.setName("jMenuItem27"); // NOI18N
        jMenu5.add(jMenuItem27);

        jMenuItem28.setText(resourceMap.getString("jMenuItem28.text")); // NOI18N
        jMenuItem28.setName("jMenuItem28"); // NOI18N
        jMenu5.add(jMenuItem28);

        jMenuItem29.setText(resourceMap.getString("jMenuItem29.text")); // NOI18N
        jMenuItem29.setName("jMenuItem29"); // NOI18N
        jMenu5.add(jMenuItem29);

        jMenuItem30.setText(resourceMap.getString("jMenuItem30.text")); // NOI18N
        jMenuItem30.setName("jMenuItem30"); // NOI18N
        jMenu5.add(jMenuItem30);

        jMenuItem31.setText(resourceMap.getString("jMenuItem31.text")); // NOI18N
        jMenuItem31.setName("jMenuItem31"); // NOI18N
        jMenu5.add(jMenuItem31);

        menuBar.add(jMenu5);

        jMenu6.setText(resourceMap.getString("jMenu6.text")); // NOI18N
        jMenu6.setName("jMenu6"); // NOI18N

        jMenuItem32.setText(resourceMap.getString("jMenuItem32.text")); // NOI18N
        jMenuItem32.setName("jMenuItem32"); // NOI18N
        jMenu6.add(jMenuItem32);

        jMenuItem33.setText(resourceMap.getString("jMenuItem33.text")); // NOI18N
        jMenuItem33.setName("jMenuItem33"); // NOI18N
        jMenu6.add(jMenuItem33);

        jSeparator1.setName("jSeparator1"); // NOI18N
        jMenu6.add(jSeparator1);

        jMenuItem34.setText(resourceMap.getString("jMenuItem34.text")); // NOI18N
        jMenuItem34.setName("jMenuItem34"); // NOI18N
        jMenu6.add(jMenuItem34);

        jMenuItem35.setText(resourceMap.getString("jMenuItem35.text")); // NOI18N
        jMenuItem35.setName("jMenuItem35"); // NOI18N
        jMenu6.add(jMenuItem35);

        jSeparator2.setName("jSeparator2"); // NOI18N
        jMenu6.add(jSeparator2);

        jMenuItem36.setText(resourceMap.getString("jMenuItem36.text")); // NOI18N
        jMenuItem36.setName("jMenuItem36"); // NOI18N
        jMenu6.add(jMenuItem36);

        jMenuItem37.setText(resourceMap.getString("jMenuItem37.text")); // NOI18N
        jMenuItem37.setName("jMenuItem37"); // NOI18N
        jMenu6.add(jMenuItem37);

        jSeparator3.setName("jSeparator3"); // NOI18N
        jMenu6.add(jSeparator3);

        jMenuItem38.setText(resourceMap.getString("jMenuItem38.text")); // NOI18N
        jMenuItem38.setName("jMenuItem38"); // NOI18N
        jMenu6.add(jMenuItem38);

        jSeparator4.setName("jSeparator4"); // NOI18N
        jMenu6.add(jSeparator4);

        jMenuItem39.setText(resourceMap.getString("jMenuItem39.text")); // NOI18N
        jMenuItem39.setName("jMenuItem39"); // NOI18N
        jMenu6.add(jMenuItem39);

        jMenuItem40.setText(resourceMap.getString("jMenuItem40.text")); // NOI18N
        jMenuItem40.setName("jMenuItem40"); // NOI18N
        jMenu6.add(jMenuItem40);

        jMenuItem41.setText(resourceMap.getString("jMenuItem41.text")); // NOI18N
        jMenuItem41.setName("jMenuItem41"); // NOI18N
        jMenu6.add(jMenuItem41);

        jSeparator5.setName("jSeparator5"); // NOI18N
        jMenu6.add(jSeparator5);

        jMenuItem42.setText(resourceMap.getString("jMenuItem42.text")); // NOI18N
        jMenuItem42.setName("jMenuItem42"); // NOI18N
        jMenu6.add(jMenuItem42);

        jMenuItem43.setText(resourceMap.getString("jMenuItem43.text")); // NOI18N
        jMenuItem43.setName("jMenuItem43"); // NOI18N
        jMenu6.add(jMenuItem43);

        jSeparator6.setName("jSeparator6"); // NOI18N
        jMenu6.add(jSeparator6);

        jMenuItem44.setText(resourceMap.getString("jMenuItem44.text")); // NOI18N
        jMenuItem44.setName("jMenuItem44"); // NOI18N
        jMenu6.add(jMenuItem44);

        jMenuItem45.setText(resourceMap.getString("jMenuItem45.text")); // NOI18N
        jMenuItem45.setName("jMenuItem45"); // NOI18N
        jMenu6.add(jMenuItem45);

        jSeparator7.setName("jSeparator7"); // NOI18N
        jMenu6.add(jSeparator7);

        jMenuItem46.setText(resourceMap.getString("jMenuItem46.text")); // NOI18N
        jMenuItem46.setName("jMenuItem46"); // NOI18N
        jMenu6.add(jMenuItem46);

        menuBar.add(jMenu6);

        jMenu8.setText(resourceMap.getString("jMenu8.text")); // NOI18N
        jMenu8.setName("jMenu8"); // NOI18N

        jMenuItem51.setAction(actionMap.get("setLafTheme")); // NOI18N
        jMenuItem51.setText(resourceMap.getString("jMenuItem51.text")); // NOI18N
        jMenuItem51.setActionCommand(resourceMap.getString("jMenuItem51.actionCommand")); // NOI18N
        jMenuItem51.setName("jMenuItem51"); // NOI18N
        jMenu8.add(jMenuItem51);

        jMenuItem52.setAction(actionMap.get("setLafTheme")); // NOI18N
        jMenuItem52.setText(resourceMap.getString("jMenuItem52.text")); // NOI18N
        jMenuItem52.setActionCommand(resourceMap.getString("jMenuItem52.actionCommand")); // NOI18N
        jMenuItem52.setName("jMenuItem52"); // NOI18N
        jMenu8.add(jMenuItem52);

        jMenuItem53.setAction(actionMap.get("setLafTheme")); // NOI18N
        jMenuItem53.setText(resourceMap.getString("jMenuItem53.text")); // NOI18N
        jMenuItem53.setActionCommand(resourceMap.getString("jMenuItem53.actionCommand")); // NOI18N
        jMenuItem53.setName("jMenuItem53"); // NOI18N
        jMenu8.add(jMenuItem53);

        menuBar.add(jMenu8);

        jMenu4.setText(resourceMap.getString("jMenu4.text")); // NOI18N
        jMenu4.setName("jMenu4"); // NOI18N

        jMenuItem25.setAction(actionMap.get("showAboutBox")); // NOI18N
        jMenuItem25.setIcon(resourceMap.getIcon("jMenuItem25.icon")); // NOI18N
        jMenuItem25.setText(resourceMap.getString("jMenuItem25.text")); // NOI18N
        jMenuItem25.setName("jMenuItem25"); // NOI18N
        jMenu4.add(jMenuItem25);

        menuBar.add(jMenu4);

        statusBar.setBorder(javax.swing.BorderFactory.createLineBorder(resourceMap.getColor("statusBar.border.lineColor"))); // NOI18N
        statusBar.setName("statusBar"); // NOI18N
        statusBar.setOpaque(false);

        statusBar.add(new JLabel("当前版本V1.0.0"));
        statusBar.add(new JLabel("最后更新时间2017.03.22"), org.jdesktop.swingx.JXStatusBar.Constraint.ResizeBehavior.FILL);
        statusBar.add(new UserStatusPane());

        jToolBar1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jToolBar1.setRollover(true);
        jToolBar1.setName("jToolBar1"); // NOI18N

        jButton1.setIcon(resourceMap.getIcon("jButton1.icon")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setName("jButton1"); // NOI18N
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton1);

        jSeparator10.setName("jSeparator10"); // NOI18N
        jToolBar1.add(jSeparator10);

        jButton2.setIcon(resourceMap.getIcon("jButton2.icon")); // NOI18N
        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setName("jButton2"); // NOI18N
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton2);

        jSeparator11.setName("jSeparator11"); // NOI18N
        jToolBar1.add(jSeparator11);

        jButton3.setIcon(resourceMap.getIcon("jButton3.icon")); // NOI18N
        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setName("jButton3"); // NOI18N
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton3);

        jSeparator12.setName("jSeparator12"); // NOI18N
        jToolBar1.add(jSeparator12);

        jButton4.setIcon(resourceMap.getIcon("jButton4.icon")); // NOI18N
        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setName("jButton4"); // NOI18N
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton4);

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusBar);
        setToolBar(jToolBar1);
    }// </editor-fold>//GEN-END:initComponents

	@Action
	public void showDptInfo()
	{
		DptPanel panel = new DptPanel();
		panel.showDialog();
	}

	@Action
	public void workLevelAction()
	{
		WorkLevelPanel wlp = new WorkLevelPanel();
		wlp.showDialog();
	}

	@Action
	public void hourWorkTypeAction()
	{
		HourWorkTypePanel hwtp = new HourWorkTypePanel();
		hwtp.showDialog();
	}

	@Action
	public void DeviceTypeAction()
	{
		DeviceTypePanel dtp = new DeviceTypePanel();
		dtp.showDialog();
	}

	@Action
	public void StylePartAction()
	{
		StylePartPanel spp = new StylePartPanel();
		spp.showDialog();
	}

	@Action
	public void exit()
	{
		System.exit(0);
	}

	@Action
	public void setLafTheme(ActionEvent event)
	{
		String themeName = event.getActionCommand();
		try
		{
			javax.swing.LookAndFeel laf = UIManager.getLookAndFeel();
			if(themeName.contains("alloy"))
			{
				com.incors.plaf.alloy.AlloyTheme theme = (com.incors.plaf.alloy.AlloyTheme)mapAlloyTheme.get(themeName);
				if(theme == null)
				{
					if(themeName.contains("glass"))
					{
						theme = new com.incors.plaf.alloy.themes.glass.GlassTheme();
						mapAlloyTheme.put(themeName, theme);
					}
					else if(themeName.contains("acid"))
					{
						theme = new com.incors.plaf.alloy.themes.acid.AcidTheme();
						mapAlloyTheme.put(themeName, theme);
					}
					else if(themeName.contains("bedouin"))
					{
						theme = new com.incors.plaf.alloy.themes.bedouin.BedouinTheme();
						mapAlloyTheme.put(themeName, theme);
					}
				}
				if(laf instanceof com.incors.plaf.alloy.AlloyLookAndFeel)
				{
					com.incors.plaf.alloy.AlloyLookAndFeel aLaf = (com.incors.plaf.alloy.AlloyLookAndFeel)laf;
					if(theme != null)
					{
						// 设置主题
						aLaf.setTheme(theme, true);
					}
				}
				else
				{
					laf = new com.incors.plaf.alloy.AlloyLookAndFeel(theme);
					UIManager.setLookAndFeel(laf);
				}
				// 保存设置
				System.setProperty("sys.laf.theme", themeName);
			}
			// 手动触发laf变更事件
			// 部分UI组件库，如InfoNode、SwingX，依赖lookAndFeel属性变更事件实现更新显示
			// 因此，即使laf没有发生实际变更，也需要手动触发一次事件
			PropertyChangeListener[] listeners = UIManager.getPropertyChangeListeners();
			for(PropertyChangeListener l : listeners)
			{
				l.propertyChange(new PropertyChangeEvent(UIManager.class, 
						"lookAndFeel",  "", UIManager.getLookAndFeel().getName()));
			}
			SingleFrameApplication.getInstance().updateLookAndFeel();
			// 同步事件
//			lookAndFeelChanged();
		}
		catch(Exception exp)
		{}

		javax.swing.JMenuItem menu =
				(javax.swing.JMenuItem)event.getSource();
		menu.setSelected(true);
	}

	@Action
	public void userManage()
	{
		try
		{
			UserModule um = ServiceLoader.load(UserModule.class).iterator().next();
			um.showUserMgmt();
		}
		catch(Exception exp)
		{
			exp.printStackTrace();
		}
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem dptItem;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenu jMenu7;
    private javax.swing.JMenu jMenu8;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem15;
    private javax.swing.JMenuItem jMenuItem16;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem18;
    private javax.swing.JMenuItem jMenuItem19;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem20;
    private javax.swing.JMenuItem jMenuItem21;
    private javax.swing.JMenuItem jMenuItem22;
    private javax.swing.JMenuItem jMenuItem23;
    private javax.swing.JMenuItem jMenuItem24;
    private javax.swing.JMenuItem jMenuItem25;
    private javax.swing.JMenuItem jMenuItem26;
    private javax.swing.JMenuItem jMenuItem27;
    private javax.swing.JMenuItem jMenuItem28;
    private javax.swing.JMenuItem jMenuItem29;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem30;
    private javax.swing.JMenuItem jMenuItem31;
    private javax.swing.JMenuItem jMenuItem32;
    private javax.swing.JMenuItem jMenuItem33;
    private javax.swing.JMenuItem jMenuItem34;
    private javax.swing.JMenuItem jMenuItem35;
    private javax.swing.JMenuItem jMenuItem36;
    private javax.swing.JMenuItem jMenuItem37;
    private javax.swing.JMenuItem jMenuItem38;
    private javax.swing.JMenuItem jMenuItem39;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem40;
    private javax.swing.JMenuItem jMenuItem41;
    private javax.swing.JMenuItem jMenuItem42;
    private javax.swing.JMenuItem jMenuItem43;
    private javax.swing.JMenuItem jMenuItem44;
    private javax.swing.JMenuItem jMenuItem45;
    private javax.swing.JMenuItem jMenuItem46;
    private javax.swing.JMenuItem jMenuItem47;
    private javax.swing.JMenuItem jMenuItem48;
    private javax.swing.JMenuItem jMenuItem49;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem50;
    private javax.swing.JMenuItem jMenuItem51;
    private javax.swing.JMenuItem jMenuItem52;
    private javax.swing.JMenuItem jMenuItem53;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator10;
    private javax.swing.JToolBar.Separator jSeparator11;
    private javax.swing.JToolBar.Separator jSeparator12;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private org.jdesktop.swingx.JXStatusBar statusBar;
    private javax.swing.JMenu sysMenu;
    // End of variables declaration//GEN-END:variables

    private JDialog aboutBox;
}
