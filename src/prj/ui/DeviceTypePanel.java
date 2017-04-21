/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prj.ui;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import prj.PrjApp;
import util.Constants;
import util.SQLiteCRUD;
import util.SwingUtil;

/**
 *
 * @author Administrator
 */
public class DeviceTypePanel extends javax.swing.JPanel
{

	private JDialog dialog;
	
	private List<List> result;
	private int selId = -1;
	/**
	 * Creates new form DptPanel
	 */
	public DeviceTypePanel()
	{
		initComponents();
	}
	public void showDialog()
	{
		dialog = new JDialog(PrjApp.getApplication().getMainFrame(), true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setName(getName());
		dialog.add(this);
		dialog.setTitle("衣车设备种类");

		dialog.addWindowListener(new java.awt.event.WindowAdapter()
		{
			@Override
			public void windowClosing(java.awt.event.WindowEvent evt)
			{
				dialog.dispose();
			}
		});
		initTable();
		dialog.pack();
		dialog.setLocationRelativeTo(PrjApp.getApplication().getMainFrame());
		dialog.setVisible(true);
	}
	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        idKey = new javax.swing.JLabel();

        setName("Form_DeviceType"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {

            },
            new String []
            {
                "id", "编号", "设备名称"
            }
        )
        {
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        });
        jTable1.setName("jTable1"); // NOI18N
        jTable1.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(prj.PrjApp.class).getContext().getResourceMap(DeviceTypePanel.class);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jTextField1.setName("jTextField1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jTextField2.setName("jTextField2"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(prj.PrjApp.class).getContext().getActionMap(DeviceTypePanel.class, this);
        jButton1.setAction(actionMap.get("add")); // NOI18N
        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N

        jButton2.setAction(actionMap.get("modify")); // NOI18N
        jButton2.setText(resourceMap.getString("jButton2.text")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N

        jButton3.setAction(actionMap.get("delete")); // NOI18N
        jButton3.setText(resourceMap.getString("jButton3.text")); // NOI18N
        jButton3.setName("jButton3"); // NOI18N

        jButton4.setAction(actionMap.get("closeAction")); // NOI18N
        jButton4.setText(resourceMap.getString("jButton4.text")); // NOI18N
        jButton4.setName("jButton4"); // NOI18N

        idKey.setText(resourceMap.getString("idKey.text")); // NOI18N
        idKey.setName("idKey"); // NOI18N
        idKey.setVisible(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(idKey))
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3)
                    .addComponent(jButton4))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel1)
                                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel2)
                                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton3)
                                .addGap(18, 18, 18)
                                .addComponent(jButton4))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 17, Short.MAX_VALUE)
                                .addComponent(jButton1)
                                .addGap(18, 18, 18))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(idKey)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addComponent(jButton2)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jTable1MouseClicked
    {//GEN-HEADEREND:event_jTable1MouseClicked
        // TODO add your handling code here:
		if(javax.swing.SwingUtilities.isLeftMouseButton(evt) 
			&& evt.getClickCount() == 2)
		{
			selId = jTable1.getSelectedRow();
			int modelIndex = jTable1.convertRowIndexToModel(selId);
			List<String> rowd = result.get(modelIndex);
			idKey.setText(rowd.get(0));
			jTextField1.setText(rowd.get(1));
			jTextField2.setText(rowd.get(2));
		}
    }//GEN-LAST:event_jTable1MouseClicked

	@Action
	public void closeAction()
	{
		dialog.dispose();
	}

	@Action
	public void add()
	{
		SQLiteCRUD sqlOpt = PrjApp.getApplication().getSQLiteCRUD();
		String[] data = new String[]{jTextField1.getText(), jTextField2.getText()};
		boolean isSucc = sqlOpt.insert(Constants.CONF_DEVICETYPE, new String[]{"number", "deviceName"}, data);
		if(isSucc)
		{
			int maxId = sqlOpt.getMaxID(Constants.CONF_DEVICETYPE);
			data = new String[]{""+maxId, jTextField1.getText(), jTextField2.getText()};
			DefaultTableModel tm = (DefaultTableModel)jTable1.getModel();
			tm.addRow(data);
			result.add(Arrays.asList(data));
			selId = -1;
		}
	}

	@Action
	public void modify()
	{
		String ids = idKey.getText();
		if(selId == -1)
		{
			JOptionPane.showMessageDialog(this, "没有选择设备类型，双击选择需要修改的设备类型!");
			return;
		}
		String number = jTextField1.getText();
		String deviceName = jTextField2.getText();
		if(number.equals("")||deviceName.equals(""))
		{
			JOptionPane.showMessageDialog(this, "设备编号，设备名称不合法!");
			return;
		}
		SQLiteCRUD sqlOpt = PrjApp.getApplication().getSQLiteCRUD();
		boolean isSucc = sqlOpt.update(Constants.CONF_DEVICETYPE, idKey.getText(), "id", new String[]{"number", "deviceName"}, 
				new String[]{number, deviceName});
		if(isSucc)
		{
			String[] data = new String[]{ids, number, deviceName};
			DefaultTableModel tm = (DefaultTableModel)jTable1.getModel();
			int modelIndex = jTable1.convertRowIndexToModel(selId);
			tm.setValueAt(number, modelIndex, 1);
			tm.setValueAt(deviceName, modelIndex, 2);
			result.set(modelIndex, Arrays.asList(data));
		}		
	}

	@Action
	public void delete()
	{
		if(selId == -1)
		{
			JOptionPane.showMessageDialog(this, "没有选择设备类型，双击选择需要删除的设备类型!");
			return;
		}
		SQLiteCRUD sqlOpt = PrjApp.getApplication().getSQLiteCRUD();
		boolean isSucc = sqlOpt.delete(Constants.CONF_DEVICETYPE, "id", idKey.getText());
		if(isSucc)
		{
			DefaultTableModel tm = (DefaultTableModel)jTable1.getModel();
			int modelIndex = jTable1.convertRowIndexToModel(selId);
			tm.removeRow(modelIndex);
			result.remove(modelIndex);
		}
	}


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel idKey;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables

	private void initTable()
	{
		SQLiteCRUD sqlOpt = PrjApp.getApplication().getSQLiteCRUD();
		result = sqlOpt.select(Constants.CONF_DEVICETYPE, new String[]{"id", "number", "deviceName"});
		DefaultTableModel tm = (DefaultTableModel)jTable1.getModel();
		SwingUtil.hideColumn(jTable1, 0);
		for(List l:result)
		{
			tm.addRow(l.toArray());	
		}
	}
}
