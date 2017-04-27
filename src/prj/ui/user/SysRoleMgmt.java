/*
 * Copyright 2013 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */

/*
 * SysRoleMgmt.java
 *
 * Created on 2015-2-11, 8:27:43
 */

package prj.ui.user;

import com.hongxin.component.ComponentUtil;
import com.hongxin.component.export.TableModelRecordSet;
import com.hongxin.saf.SingleFrameApplication;
import com.hongxin.util.GUIUtils;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;
import prj.PrjApp;
import prj.ui.basic.DefaultDataExportTask;
import prj.user.po.Role;
import util.SQLiteCRUD;

/**
 * 角色管理界面。
 * @author fanhuigang
 */
public class SysRoleMgmt extends javax.swing.JPanel
{
	/**
	 * 界面操作状态。
	 */
	private boolean cancelClicked = true;

	private ResourceMap rm;
	private JDialog dialog;
	
    /** Creates new form SysRoleMgmt */
    public SysRoleMgmt()
	{
		rm = Application.getInstance().getContext().getResourceMap(SysRoleMgmt.class);
        initComponents();
		initialize();
    }
	
	/**
	 * 初始化界面。
	 */
	private void initialize()
	{
		// 加载角色列表
		initSysRole();
	}
	
	/**
	 * 初始化角色列表。
	 */
	private void initSysRole()
	{
		SQLiteCRUD sqlOpt = PrjApp.getApplication().getSQLiteCRUD();
		List<List> results = sqlOpt.select("user_role", new String[]{"id","roleName","roledesc"});
		
//		OmcProcessor.process(Command.user, Command.listSysRole, 
//		null, new ProcessCallback()
//		{
//			@Override
//			public void processCompleted(final ProcessData out)
//			{
//				if(out.getData() != null)
//				{
//					javax.swing.SwingUtilities.invokeLater(new Runnable()
//					{
//						@Override
//						public void run()
//						{
//							((SysRoleTableModel)sysRoleTable.getModel()).initModel((List)out.getData());
//						}
//					});
//				}
//			}	
//		});
		List<Role> roles = new ArrayList();
		for(List<String> re:results)
		{
			Role r = new Role();
			r.setId(Integer.parseInt(re.get(0)));
			r.setName(re.get(1));
			r.setDescription(re.get(2));
			roles.add(r);
		}
		((SysRoleTableModel)sysRoleTable.getModel()).initModel(roles);
	}
	
	/**
	 * 角色表格模型。
	 */
	private class SysRoleTableModel extends AbstractTableModel
	{
		/**
		 * 列集合。
		 */
		protected List<String> columns;
		/**
		 * 角色集合。
		 */
		private List<Role> listRole;
		
		public SysRoleTableModel()
		{
			columns = new ArrayList();
			columns.add(rm.getString("col.index"));
			columns.add(rm.getString("col.name"));
			columns.add(rm.getString("col.desc"));
			listRole = new ArrayList();
		}
		
		@Override
		public boolean isCellEditable(int row, int column)
		{
			return false;
		}

		@Override
		public int getColumnCount()
		{
			return columns.size();
		}

		@Override
		public String getColumnName(int column)
		{
			return columns.get(column);
		}
		
		@Override
		public int getRowCount()
		{
			return listRole.size();
		}

		@Override
		public Object getValueAt(int row, int col)
		{
			Role role = listRole.get(row);
			switch(col)
			{
				case 0:
					return row + 1;
				case 1:
					return role.getName();
				case 2:
					return role.getDescription();
				default:
					return null;
			}
		}
		
		/**
		 * 初始化模型数据。
		 * @param listRole 角色列表
		 */
		public void initModel(List<Role> listRole)
		{
			this.listRole.clear();
			this.listRole.addAll(listRole);
			this.fireTableDataChanged();
		}
		
		/**
		 * 清除数据。
		 */
		public void clear()
		{
			this.listRole.clear();
			this.fireTableDataChanged();
		}
		
		/**
		 * 获取指定位置的角色对象。
		 * @param modelIndex 模型索引
		 * @return 角色对象
		 */
		public Role getRoleAt(int modelIndex)
		{
			return listRole.get(modelIndex);
		}
		
		/**
		 * 添加角色。
		 * @param role 角色对象
		 */
		public void addRole(Role role)
		{
			if(listRole.indexOf(role) == -1)
			{
				listRole.add(role);
				fireTableRowsInserted(listRole.size() - 1, listRole.size() - 1);
			}
		}
		
		/**
		 * 更新角色。
		 * @param role 角色对象
		 */
		public void updateRole(Role role)
		{
			int index = listRole.indexOf(role);
			if(index != -1)
			{
				listRole.set(index, role);
				fireTableRowsUpdated(index, index);
			}
		}
		
		/**
		 * 删除角色。
		 * @param role 角色对象
		 */
		public void removeRole(Role role)
		{
			int index = listRole.indexOf(role);
			if(index != -1)
			{
				listRole.remove(index);
				fireTableRowsDeleted(index, index);
			}
		}
	}
	
	/**
	 * 显示界面。
	 * @param selectionMode 选择模式
	 */
	public boolean showDialog(boolean selectionMode)
	{
		// 选择功能仅在选择模式下可见
		btnSelect.setVisible(selectionMode);
		// 工具栏仅在非选择模式下可见
		toolBar.setVisible(!selectionMode);
		dialog = new JDialog(SingleFrameApplication.getInstance().getMainFrame(), true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setName(getName());
		dialog.add(this);
		dialog.setTitle(rm.getString("form.title"));
		GUIUtils.addHideAction(dialog);
		dialog.getRootPane().setDefaultButton(btnExit);
		dialog.addWindowListener(new java.awt.event.WindowAdapter()
		{
			@Override
			public void windowClosing(java.awt.event.WindowEvent evt)
			{
				exit();
			}
		});
		dialog.pack();
		SingleFrameApplication.getInstance().show(dialog);
		return !cancelClicked;
	}
	
	/**
	 * 获取选择的角色。
	 * @return 选择的角色对象
	 */
	public Role getSelectedRole()
	{
		return ((SysRoleTableModel)sysRoleTable.getModel()).getRoleAt(
				sysRoleTable.convertRowIndexToModel(sysRoleTable.getSelectedRow()));
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
        sysRoleTable = ComponentUtil.createTable(true, false);
        toolBar = new javax.swing.JToolBar();
        btnAdd = ComponentUtil.createToolBarButton();
        btnEdit = ComponentUtil.createToolBarButton();
        btnDelete = ComponentUtil.createToolBarButton();
        separator1 = new javax.swing.JToolBar.Separator();
        btnExport = ComponentUtil.createToolBarButton();
        btnExit = new javax.swing.JButton();
        btnSelect = new javax.swing.JButton();

        setName("Form_SysRoleMgmt"); // NOI18N

        tableScrollPane.setName("tableScrollPane"); // NOI18N

        sysRoleTable.setModel(new SysRoleTableModel());
        sysRoleTable.setName("sysRoleTable"); // NOI18N
        tableScrollPane.setViewportView(sysRoleTable);

        toolBar.setBorder(null);
        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        toolBar.setName("toolBar"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(prj.PrjApp.class).getContext().getActionMap(SysRoleMgmt.class, this);
        btnAdd.setAction(actionMap.get("addRole")); // NOI18N
        btnAdd.setFocusable(false);
        btnAdd.setName("btnAdd"); // NOI18N
        toolBar.add(btnAdd);

        btnEdit.setAction(actionMap.get("modifyRole")); // NOI18N
        btnEdit.setFocusable(false);
        btnEdit.setName("btnEdit"); // NOI18N
        toolBar.add(btnEdit);

        btnDelete.setAction(actionMap.get("removeRole")); // NOI18N
        btnDelete.setFocusable(false);
        btnDelete.setName("btnDelete"); // NOI18N
        toolBar.add(btnDelete);

        separator1.setName("separator1"); // NOI18N
        toolBar.add(separator1);

        btnExport.setAction(actionMap.get("export")); // NOI18N
        btnExport.setFocusable(false);
        btnExport.setName("btnExport"); // NOI18N
        toolBar.add(btnExport);

        btnExit.setAction(actionMap.get("exit")); // NOI18N
        btnExit.setName("btnExit"); // NOI18N

        btnSelect.setAction(actionMap.get("select")); // NOI18N
        btnSelect.setName("btnSelect"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(toolBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSelect)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnExit))
                    .addComponent(tableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(toolBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnExit)
                        .addComponent(btnSelect)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

	/**
	 * 销毁界面。
	 */
	private void dispose()
	{
		if(dialog != null)
		{
			dialog.dispose();
			dialog = null;
		}
	}
	
	/**
	 * 显示角色操作消息。
	 * @param role 角色对象
	 * @param state 操作状态
	 */
	private void showErrorMessage(Role role, int state)
	{
		String msg = null;
		switch(state)
		{
			case 1:
				msg = rm.getString("msg.name.repeat", role.getName());
				break;
			case 2:
				msg = rm.getString("msg.obj.inuse", role.getName());
				break;
		}
		if(msg != null)
		{
			JOptionPane.showMessageDialog(null, 
					msg, 
					rm.getString("msg.error"), 
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	@Action
	public void exit()
	{
		cancelClicked = true;
		dispose();
	}

	@Action
	public void addRole()
	{
		SysRoleEditor editor = new SysRoleEditor();
		if(editor.showDialog(null))
		{
			Role newRole = editor.getRole();
			// 添加角色
			
//			ProcessData out = OmcProcessor.process(Command.user, 
//					Command.addSysRole, newRole);
//			if(out.getState() == OmcConstants.coes_success)
//			{
//				// 添加成功
//				newRole.setId((Integer)out.getData());
//				((SysRoleTableModel)sysRoleTable.getModel()).addRole(newRole);
//				JOptionPane.showMessageDialog(null, 
//						rm.getString("msg.role.new.success"), 
//						rm.getString("msg.prompt"), 
//						JOptionPane.INFORMATION_MESSAGE);
//			}
//			else
//			{
//				// 添加失败
//				if(out.getState() != OmcConstants.coes_failure)
//				{
//					showErrorMessage(newRole, out.getState());
//				}
//				else
//				{
//					JOptionPane.showMessageDialog(null, 
//							rm.getString("msg.role.new.error"), 
//							rm.getString("msg.error"), 
//							JOptionPane.ERROR_MESSAGE);
//				}
//			}
		}
	}

	@Action
	public void modifyRole()
	{
		int row = sysRoleTable.getSelectedRow();
		if(row == -1)
		{
			return;
		}
		Role srcRole = ((SysRoleTableModel)sysRoleTable.getModel()).getRoleAt(sysRoleTable.convertRowIndexToModel(row));
		SysRoleEditor editor = new SysRoleEditor();
		if(editor.showDialog(srcRole.clone()))
		{
			Role newRole = editor.getRole();
			// 修改角色
//			ProcessData out = OmcProcessor.process(Command.user, 
//					Command.modifySysRole, newRole);
//			if(out.getState() == OmcConstants.coes_success)
//			{
//				// 修改成功
//				((SysRoleTableModel)sysRoleTable.getModel()).updateRole(newRole);
//				JOptionPane.showMessageDialog(null, 
//						rm.getString("msg.role.modify.success"), 
//						rm.getString("msg.prompt"), 
//						JOptionPane.INFORMATION_MESSAGE);
//			}
//			else
//			{
//				// 修改失败
//				if(out.getState() != OmcConstants.coes_failure)
//				{
//					showErrorMessage(newRole, out.getState());
//				}
//				else
//				{				
//					JOptionPane.showMessageDialog(null, 
//							rm.getString("msg.role.modify.error"), 
//							rm.getString("msg.error"), 
//							JOptionPane.ERROR_MESSAGE);
//				}
//			}
		}
	}

	@Action
	public void removeRole()
	{
		int row = sysRoleTable.getSelectedRow();
		if(row == -1)
		{
			return;
		}
		Role srcRole = ((SysRoleTableModel)sysRoleTable.getModel()).getRoleAt(sysRoleTable.convertRowIndexToModel(row));
		// 删除确认
		int option = JOptionPane.showConfirmDialog(null, 
				rm.getString("msg.role.remove.confirm", srcRole.getName()), 
				rm.getString("msg.confirm"), 
				JOptionPane.YES_NO_OPTION);
		if(option != JOptionPane.YES_OPTION)
		{
			return;
		}
		// 删除角色
//		ProcessData out = OmcProcessor.process(Command.user, 
//				Command.removeSysRole, srcRole.getId());
//		if(out.getState() == OmcConstants.coes_success)
//		{
//			// 删除成功
//			((SysRoleTableModel)sysRoleTable.getModel()).removeRole(srcRole);
//			JOptionPane.showMessageDialog(null, 
//					rm.getString("msg.role.remove.success"), 
//					rm.getString("msg.prompt"), 
//					JOptionPane.INFORMATION_MESSAGE);
//		}
//		else
//		{
//			// 删除失败
//			if(out.getState() != OmcConstants.coes_failure)
//			{
//				showErrorMessage(srcRole, out.getState());
//			}
//			else
//			{			
//				JOptionPane.showMessageDialog(null, 
//						rm.getString("msg.role.remove.error"), 
//						rm.getString("msg.error"), 
//						JOptionPane.ERROR_MESSAGE);
//			}
//		}
	}

	@Action
	public Task export()
	{
		// 检查列表数据
		if(sysRoleTable.getModel().getRowCount() == 0)
		{
			JOptionPane.showMessageDialog(null, 
					rm.getString("msg.export.empty"), 
					rm.getString("msg.prompt"), 
					JOptionPane.INFORMATION_MESSAGE);
			return null;
		}
		// 提取导出列
		List<String> listColumn = new ArrayList();
		for(int i = 0; i < sysRoleTable.getModel().getColumnCount(); i++)
		{
			listColumn.add(sysRoleTable.getModel().getColumnName(i));
		}
		TableModelRecordSet recordSet = new TableModelRecordSet(sysRoleTable.getModel());
		return new DefaultDataExportTask(listColumn, recordSet, null);
	}

	@Action
	public void select()
	{
		// 检查选择
		int row = sysRoleTable.getSelectedRow();
		if(row == -1)
		{
			JOptionPane.showMessageDialog(null, 
					rm.getString("msg.selection.empty"), 
					rm.getString("msg.prompt"), 
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		cancelClicked = false;
		dispose();
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnExport;
    private javax.swing.JButton btnSelect;
    private javax.swing.JToolBar.Separator separator1;
    private javax.swing.JTable sysRoleTable;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables
}
