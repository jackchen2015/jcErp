/*
 * Copyright 2009 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */
package prj;

import com.hongxin.util.service.Service;
import java.awt.Component;
import javax.swing.JComponent;

/**
 * 玻璃窗格服务接口。
 * @author fanhuigang
 */
public interface GlassboxService extends Service
{
	/**
	 * 锁定优先级 - 高。
	 */
	public static final int LOCK_PRIOR_HIGH = 0;
	/**
	 * 锁定优先级 - 正常。
	 */
	public static final int LOCK_PRIOR_NORMAL = 1;

	/**
	 * 当前UI是否独占状态。独占状态包括阻塞或锁定。
	 * @return 是否独占状态
	 */
	public boolean isExclusive();

	/**
	 * 获取使当前UI处于独占状态的组件对象。
	 * @return 组件对象或null
	 */
	public Component getExclusiveComponent();
	
	/**
	 * 在顶层界面上显示指定消息。
	 * @param message 消息内容
	 * @deprecated 不再支持
	 */
	void topMessage(String message);

	/**
	 * 阻塞整个应用的输入。
	 */
	void blockInput();

	/**
	 * 解除输入阻塞。
	 */
	void unblockInput();

	/**
	 * 使用指定UI锁定应用程序。
	 * @param lockUI 锁定UI
	 */
	void lockApplication(JComponent lockUI);
	
	/**
	 * 使用指定UI锁定应用程序。
	 * @param lockUI 锁定UI
	 * @param exclusive 是否独占模式
	 */
	void lockApplication(JComponent lockUI, boolean exclusive);

	/**
	 * 解除应用程序的锁定。
	 * @param lockUI 锁定UI
	 */
	void unlockApplication(Component lockUI);
	
	/**
	 * 强制取消所有锁定。
	 */
	void dismiss();
}
