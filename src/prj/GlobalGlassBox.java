/*
 * Copyright 2009 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */
package prj;

import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import com.hongxin.component.PainterUtil;
import com.hongxin.saf.SingleFrameApplication;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.AlphaPainter;
import util.GUIUtil;

/**
 * 透明窗格面板。
 * @author fanhuigang
 */
public class GlobalGlassBox extends JXPanel implements GlassboxService
{
	/**
	 * 鼠标事件监听器。
	 */
	private MouseInputListener blockMouseEvents;
	/**
	 * 键盘事件监听器。
	 */
	private KeyListener blockKeyEvents;
	/**
	 * 输入验证器。
	 */
	private InputVerifier blockInputVerifier;
	/**
	 * block背景painter。
	 */
	private AlphaPainter blockPainter;
	/**
	 * 阻塞状态。
	 */
	private AtomicBoolean blocked = new AtomicBoolean(false);
	/**
	 * 锁定状态。
	 */
	private AtomicBoolean locked = new AtomicBoolean(false);
	/**
	 * 锁定状态时现场窗口列表。
	 */
	private List<Window> lockedWindow;
	/**
	 * 同步锁
	 */
	private final Object synLock = new Object();

	public GlobalGlassBox()
	{
		blockPainter = new AlphaPainter();
		blockPainter.setCacheable(true);
		blockPainter.setAlpha(0.75f);
		blockPainter.setVisible(false);
		setBackgroundPainter(blockPainter);
		setLayout(new java.awt.BorderLayout());
		setOpaque(false);
	}
	
	@Override
	public void updateUI()
	{
		if(blockPainter != null)
		{
			blockPainter.setPainters(PainterUtil.defaultPinstripePainter());
		}
		super.updateUI();
	}

	@Override
	public void topMessage(final String message)
	{
		// deprecated
	}

	/**
	 * 当前UI是否独占状态。独占状态包括阻塞或锁定。
	 * @return 是否独占状态
	 */
	@Override
	public boolean isExclusive()
	{
		synchronized(synLock)
		{
			return blocked.get() || locked.get();
		}
	}

	@Override
	public Component getExclusiveComponent()
	{
		if(isExclusive() && getComponentCount() > 0)
		{
			return getComponent(0);
		}
		return null;
	}

	/**
	 * 设置显示消息。
	 * @param message 待显示的消息文本
	 */
	public void setMessage(String message)
	{
		// 非阻塞和锁定状态
		if(!isExclusive())
		{
			// deprecated
		}
	}

	@Override
	public void blockInput()
	{
		synchronized(synLock)
		{
			if(!blocked.compareAndSet(false, true))
			{
				// 已处于阻塞状态
				return;
			}
			// 保持焦点
			if(blockInputVerifier == null)
			{
				blockInputVerifier = new InputVerifier()
				{
					@Override
					public boolean verify(JComponent c)
					{
						return !c.isVisible();
					}
				};
			}
			if(blockMouseEvents == null)
			{
				blockMouseEvents = new MouseInputAdapter()
				{};
			}
			com.hongxin.util.HxSwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					// 添加listener
					addMouseMotionListener(blockMouseEvents);
					addMouseListener(blockMouseEvents);
					// 设置inputVerifier
					setInputVerifier(blockInputVerifier);
					// 设置block painter
					blockPainter.setVisible(true);
					// 显示UI
					setVisible(true);
					// 获取焦点
					requestFocusInWindow();
				}
			});
		}

	}

	@Override
	public void unblockInput()
	{
		synchronized(synLock)
		{
			if(!blocked.get())
			{
				// 非阻塞状态
				return;
			}
			com.hongxin.util.HxSwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					// 隐藏UI
					setVisible(false);
					// 清除inputVerifier
					setInputVerifier(null);
					// 清除block painter
					blockPainter.setVisible(false);
					// 清除listener
					if(blockMouseEvents != null)
					{
						removeMouseListener(blockMouseEvents);
						removeMouseMotionListener(blockMouseEvents);
					}
				}
			});
			// 更新阻塞状态
			blocked.compareAndSet(true, false);
		}
	}

	/**
	 * 锁定应用前预处理事件。
	 * @param disposeDialog 是否强制销毁所有显示的对话框界面
	 */
	private void beforeLockApplication(boolean disposeDialog)
	{
		// 关闭顶层窗口
		List<Window> listWindow = SingleFrameApplication.getInstance().
				getVisibleSecondaryWindows();
		for(Window wnd : listWindow)
		{
			if(wnd instanceof JDialog)
			{
				if(disposeDialog)
				{
					wnd.dispose();
				}
			}
		}
		lockedWindow = null;
		// 最小化heavyweight组件视图
		List<net.infonode.docking.View> listView =
				GUIUtil.getView(JWebBrowser.class);
		for(net.infonode.docking.View view : listView)
		{
			// 注意：最小化前必须调用restore
			view.restore();
			// 最小化视图
			view.minimize();
		}
	}

	/**
	 * 解除应用锁定后处理事件。
	 */
	private void afterUnlockApplication()
	{
		// 还原顶层窗口
		if(lockedWindow != null && !lockedWindow.isEmpty())
		{
			for(Window wnd : lockedWindow)
			{
				if(wnd instanceof JDialog)
				{
					wnd.setVisible(true);
				}
			}
		}
	}

	@Override
	public void lockApplication(final JComponent lockUI)
	{
		lockApplication(lockUI, false);
	}

	@Override
	public void lockApplication(final JComponent lockUI, final boolean exclusive)
	{
		synchronized(synLock)
		{
			if(!locked.compareAndSet(false, true))
			{
				// 已处于锁定状态
				return;
			}
			// 保持焦点
			final InputVerifier retainFocusVerifier = new InputVerifier()
			{
				@Override
				public boolean verify(JComponent c)
				{
					return true;
				}
			};
			if(blockMouseEvents == null)
			{
				blockMouseEvents = new MouseInputAdapter()
				{};
			}
			if(blockKeyEvents == null)
			{
				blockKeyEvents = new KeyAdapter()
				{};
			}
			com.hongxin.util.HxSwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						// 清理现场
						beforeLockApplication(exclusive);
						lockUI.addMouseMotionListener(blockMouseEvents);
						lockUI.addMouseListener(blockMouseEvents);
						lockUI.addKeyListener(blockKeyEvents);
						if(lockUI instanceof JComponent)
						{
							((JComponent)lockUI).setInputVerifier(retainFocusVerifier);
						}
						// 删除所有子组件
						removeAll();
						// 设置block painter
						//setBackgroundPainter(blockPainter);
						// 添加组件
						//lockUI.setOpaque(false);
						add(lockUI, BorderLayout.CENTER);
						validate();
						// 显示lockUI
						setVisible(true);
						// 获取焦点
						lockUI.requestFocusInWindow();
						// disable
						SingleFrameApplication.getInstance().getRootPane().getJMenuBar().
								setEnabled(false);
						SingleFrameApplication.getInstance().getRootPane().getContentPane().
								setEnabled(false);
					}
					catch(Exception exp)
					{}
				}
			});
		}

	}

	@Override
	public void unlockApplication(final Component lockUI)
	{
		synchronized(synLock)
		{
			if(!locked.get())
			{
				// 非锁定状态
				return;
			}
			com.hongxin.util.HxSwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						if(getComponentCount() == 0)
						{
							// 无锁定组件
							return;
						}
						// 清除inputVerifier
						Component currentLock = getComponent(0);
						if(currentLock != lockUI)
						{
							// 解除锁定者必须为锁定者
							return;
						}
						if(currentLock instanceof JComponent)
						{
							((JComponent)currentLock).setInputVerifier(null);
						}
						// 隐藏UI
						setVisible(false);
						// 清除listener
						if(blockMouseEvents != null)
						{
							currentLock.removeMouseListener(blockMouseEvents);
							currentLock.removeMouseMotionListener(blockMouseEvents);
						}
						if(blockKeyEvents != null)
						{
							currentLock.removeKeyListener(blockKeyEvents);
						}
						// 删除子组件
						removeAll();
						validate();
						// enable
						SingleFrameApplication.getInstance().getRootPane().getJMenuBar().
								setEnabled(true);
						SingleFrameApplication.getInstance().getRootPane().getContentPane().
								setEnabled(true);
						// 还原现场
						afterUnlockApplication();
					}
					catch(Exception exp)
					{}
				}
			});
			// 更新锁定状态
			locked.compareAndSet(true, false);
		}
	}
	
	@Override
	public void dismiss()
	{
		if(isExclusive())
		{
			synchronized(synLock)
			{
				if(blocked.get())
				{
					unblockInput();
				}
				else if(locked.get())
				{
					unlockApplication(getComponent(0));
				}
			}
		}
	}
}
