/*
 * Copyright 2013 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */

/*
 * UserSelectionPanel.java
 *
 * Created on 2014-5-27, 18:04:19
 */

package com.hongxin.omc.ui.user;

import com.hongxin.component.ComponentUtil;
import com.hongxin.omc.operation.Command;
import com.hongxin.omc.operation.OmcOperationGlue;
import com.hongxin.omc.ui.OmcPreferences;
import com.hongxin.omc.ui.common.NodeIndex;
import com.hongxin.saf.AsynBlockTask;
import com.hongxin.saf.SingleFrameApplication;
import com.hongxin.speed.core.ProcessCallback;
import com.hongxin.speed.core.ProcessData;
import com.hongxin.speed.core.SpeedUtil;
import com.hongxin.util.GUIUtils;
import java.awt.Component;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;

/**
 * 用户选择界面。
 * @author fanhuigang
 */
public class UserSelectionPanel extends javax.swing.JPanel
{
	/**
	 * 待选择用户id列表。
	 */
	private List<Integer> listSelectedUser;
	/**
	 * 是否取消。
	 */
	private boolean cancelClicked = true;
	private JDialog dialog;
	private ResourceMap rm;
	
    /** Creates new form UserSelectionPanel */
    public UserSelectionPanel()
	{
        initComponents();
		initialize();
    }
	
	/**
	 * 初始化。
	 */
	private void initialize()
	{
		rm = Application.getInstance().getContext().getResourceMap(UserSelectionPanel.class);
		// 节点渲染器
		userTable.setTreeCellRenderer(new UserTreeNodeCellRenderer());
	}

	/**
	 * 获取用户数据任务实现。
	 */
	private class AcquireUserDataTask extends AsynBlockTask<Object, Void> implements ProcessCallback
	{
		/**
		 * 操作结果。
		 */
		OmcOperationGlue result;
		
		AcquireUserDataTask()
		{
			// Runs on the EDT.  Copy GUI state that
			// doInBackground() depends on from parameters
			// to CommitTask fields, here.
			super(Application.getInstance(), OmcPreferences.getInstance().getInteger("sys.asyn.timeout", 30000));
			// 设置任务标题
			setTitle(SpeedUtil.getCommandName(Command.user, Command.getAllUsers));
		}

		@Override
		protected Object doInBackground()
		{
			// 异步执行命令序列
			OmcOperationGlue glue = new OmcOperationGlue(this);
			glue.addOperation(Command.user, Command.getAllUserGroups, true, null);
			glue.addOperation(Command.user, Command.getAllUsers, true, null);
			// 开始执行
			glue.start();
			// 阻塞等待
			waitForReady();
			return null;  // return your result
		}

		@Override
		public void processCompleted(ProcessData out)
		{
			result = (OmcOperationGlue)out.getData();
			// 任务结束
			setTaskFinished(true);
		}
		
		@Override
		protected void succeeded(Object value)
		{
			// 获取命令结果
			final ProcessData result1 =
					result.getResult(Command.user, Command.getAllUserGroups);
			final ProcessData result2 =
					result.getResult(Command.user, Command.getAllUsers);
			// 显示用户列表
			((UserSelectionTreeTableModel)userTable.getTreeTableModel()).initModel((List)result1.getData(), (List)result2.getData());
			((UserSelectionTreeTableModel)userTable.getTreeTableModel()).setSelectedUser(listSelectedUser);
			userTable.expandAll();
		}
	}
	
	/**
	 * 显示界面。
	 * @param listUserId 选择用户id列表
	 * @return 选择用户id列表
	 */
	public List<Integer> showDialog(List<Integer> listUserId)
	{
		this.listSelectedUser = listUserId;
		// 查询用户信息
		AcquireUserDataTask task = new AcquireUserDataTask();
		Application.getInstance().getContext().getTaskService().execute(task);
		dialog = new JDialog(SingleFrameApplication.getInstance().getMainFrame(), true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setName(getName());
		dialog.add(this);
		dialog.setTitle(rm.getString("form.title"));
		GUIUtils.addHideAction(dialog);
		dialog.getRootPane().setDefaultButton(btnCancel);
		dialog.addWindowListener(new java.awt.event.WindowAdapter()
		{
			@Override
			public void windowClosing(java.awt.event.WindowEvent evt)
			{
				cancel();
			}
		});
		dialog.pack();
		SingleFrameApplication.getInstance().show(dialog);
		if(!cancelClicked)
		{
			return ((UserSelectionTreeTableModel)userTable.getTreeTableModel()).getSelectedUserId();
		}
		return null;
	}
	
	/**
	 * 用户tree节点渲染器实现。
	 */
	private class UserTreeNodeCellRenderer extends DefaultTreeCellRenderer
	{
		@Override
		public Component getTreeCellRendererComponent(javax.swing.JTree tree,
				Object value, boolean sel,
				boolean expanded, boolean leaf, int row,
				boolean hasFocus)
		{
			super.getTreeCellRendererComponent(tree, value, sel,
					expanded, leaf, row, hasFocus);
			DefaultMutableTreeTableNode node =
					(DefaultMutableTreeTableNode)value;
			if(node.getUserObject() instanceof NodeIndex)
			{
				NodeIndex index = (NodeIndex)node.getUserObject();
				if(index.getType() == UserUtil.NODE_GROUP)
				{
					setIcon(rm.getIcon("msg.group.icon"));
				}
				else if(index.getType() == UserUtil.NODE_USER)
				{
					setIcon(rm.getIcon("msg.user.icon"));
				}
				setText(userTable.getTreeTableModel().getValueAt(value, 0).toString());
			}
			return this;
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

        tableScrollPane = new javax.swing.JScrollPane();
        userTable = ComponentUtil.createTreeTable();
        btnCancel = new javax.swing.JButton();
        btnCommit = new javax.swing.JButton();

        setName("Form_UserSelectionPanel"); // NOI18N

        tableScrollPane.setName("tableScrollPane"); // NOI18N

        userTable.setName("userTable"); // NOI18N
        tableScrollPane.setViewportView(userTable);
        // 设置模型
        userTable.setTreeTableModel(new UserSelectionTreeTableModel());

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(UserSelectionPanel.class, this);
        btnCancel.setAction(actionMap.get("cancel")); // NOI18N
        btnCancel.setName("btnCancel"); // NOI18N

        btnCommit.setAction(actionMap.get("commit")); // NOI18N
        btnCommit.setName("btnCommit"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tableScrollPane)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 326, Short.MAX_VALUE)
                        .addComponent(btnCommit)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancel)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel)
                    .addComponent(btnCommit))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

	@Action
	public void commit()
	{
		// 选择用户
		if(((UserSelectionTreeTableModel)userTable.getTreeTableModel()).isSelectionEmpty())
		{
			JOptionPane.showMessageDialog(null, 
					rm.getString("msg.selection.empty"), 
					rm.getString("msg.prompt"), 
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		cancelClicked = false;
		if(dialog != null)
		{
			dialog.dispose();
			dialog = null;
		}
	}

	@Action
	public void cancel()
	{
		cancelClicked = true;
		if(dialog != null)
		{
			dialog.dispose();
			dialog = null;
		}
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnCommit;
    private javax.swing.JScrollPane tableScrollPane;
    private org.jdesktop.swingx.JXTreeTable userTable;
    // End of variables declaration//GEN-END:variables
}
