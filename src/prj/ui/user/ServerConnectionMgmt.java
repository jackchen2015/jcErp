/*
 * Copyright 2013 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */

/*
 * ServerConnectionMgmt.java
 *
 * Created on 2014-5-13, 11:47:22
 */

package com.hongxin.omc.ui.user;

import com.hongxin.component.ComponentUtil;
import com.hongxin.component.ssl.SSLUtil;
import com.hongxin.omc.ui.util.BindingUtils;
import com.hongxin.omc.ui.validator.ElementNameValidator;
import com.hongxin.omc.ui.validator.IPv4ValueValidator;
import com.hongxin.omc.util.SysEnvConf;
import com.hongxin.saf.SingleFrameApplication;
import com.hongxin.util.GUIUtils;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.beansbinding.Validator;

/**
 * 服务器连接管理界面。
 * @author fanhuigang
 */
public class ServerConnectionMgmt extends javax.swing.JPanel
{
	private static final Logger logger =
			Logger.getLogger(ServerConnectionMgmt.class.getName());
	/**
	 * 字段分隔符。
	 */
	private static final String regexFieldSeparator = "\\$\\{";
	private static final String fieldSeparator = "${";
	private JDialog dialog;
	private boolean cancelClicked = true;
	private ResourceMap rm;
	
    /** Creates new form ServerConnectionMgmt */
    public ServerConnectionMgmt()
	{
        initComponents();
		initialize();
    }
	
	/**
	 * 初始化操作。
	 */
	private void initialize()
	{
		rm = Application.getInstance().getContext().getResourceMap(ServerConnectionMgmt.class);
		// 加载连接列表
		((ServerConnectionListModel)listConn.getModel()).initModel(acquireConnection());
	}
	
	/**
	 * 显示界面。
	 * @return 选择状态
	 */
	public boolean showDialog()
	{
		dialog = new JDialog(SingleFrameApplication.getInstance().getMainFrame(), true);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setName(getName());
		dialog.add(this);
		dialog.setTitle(rm.getString("form.title"));
		GUIUtils.addHideAction(dialog);
		dialog.getRootPane().setDefaultButton(btnClose);
		dialog.addWindowListener(new java.awt.event.WindowAdapter()
		{
			@Override
			public void windowClosing(java.awt.event.WindowEvent evt)
			{
				cancelClicked = true;
				if(dialog != null)
				{
					dialog.dispose();
					dialog = null;
				}
			}
		});
		dialog.pack();
		SingleFrameApplication.getInstance().show(dialog);
		return !cancelClicked;
	}
	
	/**
	 * 获取当前选择的连接。
	 * @return 连接对象或null
	 */
	public ServerConnection getSelectedConnection()
	{
		return (ServerConnection)listConn.getSelectedValue();
	}
	
	/**
	 * 服务器连接列表模型。
	 */
	private class ServerConnectionListModel extends AbstractListModel
	{
		/**
		 * 数据源。
		 */
		private List<ServerConnection> source = new ArrayList();
		
		@Override
		public int getSize()
		{
			return source.size();
		}

		@Override
		public Object getElementAt(int index)
		{
			return source.get(index);
		}
		
		/**
		 * 初始化模型。
		 * @param source 模型数据
		 */
		public void initModel(List<ServerConnection> source)
		{
			if(!this.source.isEmpty())
			{
				int size = this.source.size();
				this.source.clear();
				this.fireIntervalRemoved(this, 0, size - 1);
			}
			if(!source.isEmpty())
			{
				this.source.addAll(source);
				this.fireIntervalAdded(this, 0, this.source.size() - 1);
			}
		}
		
		/**
		 * 添加连接。
		 * @param conn 连接对象
		 * @return 新添加连接对应的索引
		 */
		public int addElement(ServerConnection conn)
		{
			// 检查名称是否重复
			int index = -1;
			for(int i = 0; i < source.size(); i++)
			{
				if(source.get(i).getName().equalsIgnoreCase(conn.getName()))
				{
					index = i;
					break;
				}
			}
			if(index == -1)
			{
				source.add(conn);
				fireIntervalAdded(this, source.size() - 1, source.size() - 1);
				return source.size() - 1;
			}
			else
			{
				source.set(index, conn);
				fireContentsChanged(this, index, index);
				return index;
			}
		}
		
		/**
		 * 删除连接。
		 * @param conn 连接对象
		 */
		public void removeElement(ServerConnection conn)
		{
			for(int i = 0; i < source.size(); i++)
			{
				if(source.get(i).getName().equalsIgnoreCase(conn.getName()))
				{
					fireIntervalRemoved(this, i, i);
					source.remove(i);
					break;
				}
			}
		}
	}
	
	/**
	 * 服务器连接渲染器。
	 */
	private class ServerConnectionRenderer extends DefaultListCellRenderer
	{
		@Override
		public Component getListCellRendererComponent(JList list,
			Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if(value instanceof ServerConnection)
			{
				ServerConnection conn = (ServerConnection)value;
				setText(String.format("%s@%s:%d", conn.getName(), conn.getAddress(), conn.getPort()));
			}
			return this;
		}
	}

	// <editor-fold defaultstate="collapsed" desc="http操作">
	/**
	 * 连接对象转换为字符串。
	 * @param conn 连接对象
	 * @return 连接对象对应的字符串
	 */
	private String connectionToString(ServerConnection conn)
	{
		return String.format("%s%s%s%s%d%s%s", conn.getName(), fieldSeparator, 
				conn.getAddress(), fieldSeparator, 
				conn.getPort(), fieldSeparator, 
				conn.getRemark());
	}
	
	/**
	 * 字符串转换为连接对象。
	 * @param string 字符串
	 * @return 连接对象
	 */
	private ServerConnection stringToConnection(String string)
	{
		String[] fieldArray = string.split(regexFieldSeparator);
		ServerConnection conn = new ServerConnection();
		conn.setName(fieldArray[0]);
		conn.setAddress(fieldArray[1]);
		conn.setPort(Integer.valueOf(fieldArray[2]));
		conn.setRemark(fieldArray[3]);
		return conn;
	}
	
	/**
	 * 查询所有连接。
	 * @return 连接列表 
	 */
	private List<ServerConnection> acquireConnection()
	{
		String url = SysEnvConf.getNameService();
		if(url == null || url.isEmpty())
		{
			return Collections.EMPTY_LIST;
		}
		// 查询连接列表
		PostMethod method = new PostMethod(url);
		NameValuePair[] parametersBody = new NameValuePair[]
		{
			new NameValuePair("command", "listName")
		};
		method.setRequestBody(parametersBody);
        method.setRequestHeader("Content-Type", 
				PostMethod.FORM_URL_ENCODED_CONTENT_TYPE + "; charset=utf-8");
		try
		{
			SSLUtil.addTrustedHttps();
			HttpClient httpClient = new HttpClient();
			if(httpClient.executeMethod(method) == HttpStatus.SC_OK)
			{
				// 响应结果为连接列表
				String result = method.getResponseBodyAsString();
				if(!result.isEmpty())
				{
					String[] rowArray = result.split(String.format("%s%s", regexFieldSeparator, regexFieldSeparator));
					List listCon = new ArrayList();
					for(String row : rowArray)
					{
						try
						{
							listCon.add(stringToConnection(row));
						}
						catch(Exception exp)
						{
							continue;
						}
					}
					return listCon;
				}
			}
		}
		catch(Exception exp)
		{
			logger.log(Level.WARNING, "failed to acquire connection from url: {0}", url);
		}
		finally
		{
			method.releaseConnection();
			SSLUtil.removeTrustedHttps();
		}
		return Collections.EMPTY_LIST;
	}
	
	/**
	 * 保存连接。
	 * @param conn 连接对象
	 * @return 保存结果
	 */
	private boolean saveConnection(ServerConnection conn)
	{
		String url = SysEnvConf.getNameService();
		if(url == null || url.isEmpty())
		{
			return false;
		}
		// 保存连接
		PostMethod method = new PostMethod(url);
		NameValuePair[] parametersBody = new NameValuePair[]
		{
			new NameValuePair("command", "saveName"),
			new NameValuePair("conn", connectionToString(conn))
		};
		method.setRequestBody(parametersBody);
        method.setRequestHeader("Content-Type", 
				PostMethod.FORM_URL_ENCODED_CONTENT_TYPE + "; charset=utf-8");
		try
		{
			SSLUtil.addTrustedHttps();
			HttpClient httpClient = new HttpClient();
			if(httpClient.executeMethod(method) == HttpStatus.SC_OK)
			{
				return true;
			}
		}
		catch(Exception exp)
		{
			logger.log(Level.WARNING, "failed to save connection to url: {0}", url);
		}
		finally
		{
			method.releaseConnection();
			SSLUtil.removeTrustedHttps();
		}
		return false;
	}
	
	/**
	 * 删除连接。
	 * @param conn 连接对象
	 * @return 操作结果
	 */
	private boolean removeConnection(ServerConnection conn)
	{
		String url = SysEnvConf.getNameService();
		if(url == null || url.isEmpty())
		{
			return false;
		}
		// 删除连接
		PostMethod method = new PostMethod(url);
		NameValuePair[] parametersBody = new NameValuePair[]
		{
			new NameValuePair("command", "removeName"),
			new NameValuePair("conn", connectionToString(conn))
		};
		method.setRequestBody(parametersBody);
        method.setRequestHeader("Content-Type", 
				PostMethod.FORM_URL_ENCODED_CONTENT_TYPE + "; charset=utf-8");
		try
		{
			SSLUtil.addTrustedHttps();
			HttpClient httpClient = new HttpClient();
			if(httpClient.executeMethod(method) == HttpStatus.SC_OK)
			{
				return true;
			}
		}
		catch(Exception exp)
		{
			logger.log(Level.WARNING, "failed to remove connection from url: {0}", url);
		}
		finally
		{
			method.releaseConnection();
			SSLUtil.removeTrustedHttps();
		}
		return false;
	}
	// </editor-fold>
	
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        listScrollPane = new javax.swing.JScrollPane();
        listConn = new javax.swing.JList();
        lblName = new javax.swing.JLabel();
        tfName = new javax.swing.JTextField();
        lblServer = new javax.swing.JLabel();
        tfServer = new javax.swing.JTextField();
        lblPort = new javax.swing.JLabel();
        spnPort = new javax.swing.JSpinner();
        lblRemark = new javax.swing.JLabel();
        remarkScrollPane = new javax.swing.JScrollPane();
        tfRemark = new javax.swing.JTextArea();
        toolBar = new javax.swing.JToolBar();
        btnNew = ComponentUtil.createToolBarButton();
        btnSave = ComponentUtil.createToolBarButton();
        btnCopy = ComponentUtil.createToolBarButton();
        btnRemove = ComponentUtil.createToolBarButton();
        btnClose = ComponentUtil.createToolBarButton();

        listScrollPane.setName("listScrollPane"); // NOI18N

        listConn.setModel(new ServerConnectionListModel());
        listConn.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listConn.setCellRenderer(new ServerConnectionRenderer());
        listConn.setName("listConn"); // NOI18N
        listConn.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                listConnMouseClicked(evt);
            }
        });
        listScrollPane.setViewportView(listConn);

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(ServerConnectionMgmt.class);
        lblName.setText(resourceMap.getString("lblName.text")); // NOI18N
        lblName.setName("lblName"); // NOI18N

        tfName.setName("tfName"); // NOI18N

        lblServer.setText(resourceMap.getString("lblServer.text")); // NOI18N
        lblServer.setName("lblServer"); // NOI18N

        tfServer.setName("tfServer"); // NOI18N

        lblPort.setText(resourceMap.getString("lblPort.text")); // NOI18N
        lblPort.setName("lblPort"); // NOI18N

        spnPort.setModel(new javax.swing.SpinnerNumberModel(1024, 1024, 65534, 1));
        spnPort.setName("spnPort"); // NOI18N

        lblRemark.setText(resourceMap.getString("lblRemark.text")); // NOI18N
        lblRemark.setName("lblRemark"); // NOI18N

        remarkScrollPane.setName("remarkScrollPane"); // NOI18N

        tfRemark.setColumns(20);
        tfRemark.setRows(5);
        tfRemark.setName("tfRemark"); // NOI18N
        remarkScrollPane.setViewportView(tfRemark);

        toolBar.setBorder(null);
        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        toolBar.setName("toolBar"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(ServerConnectionMgmt.class, this);
        btnNew.setAction(actionMap.get("create")); // NOI18N
        btnNew.setFocusable(false);
        btnNew.setName("btnNew"); // NOI18N
        toolBar.add(btnNew);

        btnSave.setAction(actionMap.get("save")); // NOI18N
        btnSave.setFocusable(false);
        btnSave.setName("btnSave"); // NOI18N
        toolBar.add(btnSave);

        btnCopy.setAction(actionMap.get("copy")); // NOI18N
        btnCopy.setFocusable(false);
        btnCopy.setName("btnCopy"); // NOI18N
        toolBar.add(btnCopy);

        btnRemove.setAction(actionMap.get("remove")); // NOI18N
        btnRemove.setFocusable(false);
        btnRemove.setName("btnRemove"); // NOI18N
        toolBar.add(btnRemove);

        btnClose.setAction(actionMap.get("close")); // NOI18N
        btnClose.setFocusable(false);
        btnClose.setName("btnClose"); // NOI18N
        toolBar.add(btnClose);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(listScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblName)
                            .addComponent(lblServer)
                            .addComponent(lblPort)
                            .addComponent(lblRemark))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfName)
                            .addComponent(tfServer)
                            .addComponent(remarkScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(spnPort, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblName)
                            .addComponent(tfName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfServer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblServer))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblPort)
                            .addComponent(spnPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblRemark)
                            .addComponent(remarkScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(listScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void listConnMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_listConnMouseClicked
    {//GEN-HEADEREND:event_listConnMouseClicked
        int index = listConn.locationToIndex(evt.getPoint());
		if(index != -1)
		{
			showConnectionInfo(index);
		}
    }//GEN-LAST:event_listConnMouseClicked
	
	/**
	 * 显示连接信息。
	 * @param index 连接索引
	 */
	private void showConnectionInfo(int index)
	{
		ServerConnection conn = (ServerConnection)listConn.getModel().getElementAt(index);
		tfName.setText(conn.getName());
		tfServer.setText(conn.getAddress());
		spnPort.setValue(conn.getPort());
		tfRemark.setText(conn.getRemark());
	}
	
	@Action
	public void close()
	{
		cancelClicked = false;
		if(dialog != null)
		{
			dialog.dispose();
			dialog = null;
		}
	}

	@Action
	public void remove()
	{
		ServerConnection conn = (ServerConnection)listConn.getSelectedValue();
		if(conn != null)
		{
			int option = JOptionPane.showConfirmDialog(null, 
					rm.getString("msg.remove.confirm"), 
					rm.getString("msg.confirm"), 
					JOptionPane.YES_NO_OPTION, 
					JOptionPane.QUESTION_MESSAGE);
			if(option != JOptionPane.YES_OPTION)
			{
				return;
			}
			// 删除连接
			if(removeConnection(conn))
			{
				// 清除选择状态
				listConn.getSelectionModel().clearSelection();
				((ServerConnectionListModel)listConn.getModel()).removeElement(conn);
			}
		}
	}

	@Action
	public void save()
	{
		// 检查字段数据
		StringBuilder sbd = new StringBuilder();
		// 名称
		ElementNameValidator nameValidator = new ElementNameValidator(false, 50);
		String name = tfName.getText().trim();
		Validator.Result result = nameValidator.validate(name);
		if(result != null)
		{
			if(sbd.length() > 0)
			{
				sbd.append("\n");
			}
			sbd.append(rm.getString("lblName.text"));
			sbd.append(":");
			sbd.append(BindingUtils.getValidationResult(result));
		}
		// 地址
		IPv4ValueValidator addressValidator = new IPv4ValueValidator(false);
		String address = tfServer.getText().trim();
		result = addressValidator.validate(address);
		if(result != null)
		{
			if(sbd.length() > 0)
			{
				sbd.append("\n");
			}
			sbd.append(rm.getString("lblServer.text"));
			sbd.append(":");
			sbd.append(rm.getString(result.getErrorCode().toString()));
		}
		// 备注
		String remark = tfRemark.getText().trim();
		if(remark.contains(fieldSeparator))
		{
			if(sbd.length() > 0)
			{
				sbd.append("\n");
			}
			sbd.append(rm.getString("lblRemark.text"));
			sbd.append(":");
			sbd.append(rm.getString("msg.remark.invalid"));
		}
		if(remark.getBytes().length >= 40)
		{
			if(sbd.length() > 0)
			{
				sbd.append("\n");
			}
			sbd.append(rm.getString("lblRemark.text"));
			sbd.append(":");
			sbd.append(rm.getString("msg.remark.overflow", 40));
		}
		// 显示错误消息
		if(sbd.length() > 0)
		{
			JOptionPane.showMessageDialog(null, 
					sbd.toString(), 
					rm.getString("msg.error"), 
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		// 执行保存操作
		try
		{
			spnPort.commitEdit();
			ServerConnection conn = new ServerConnection();
			conn.setName(name);
			conn.setAddress(address);
			conn.setPort((Integer)spnPort.getValue());
			conn.setRemark(remark);
			if(saveConnection(conn))
			{
				int index = ((ServerConnectionListModel)listConn.getModel()).addElement(conn);
				if(index != -1)
				{
					listConn.getSelectionModel().setSelectionInterval(index, index);
				}
			}
		}
		catch(Exception exp)
		{}
	}

	@Action
	public void create()
	{
		// 清除选择状态
		listConn.getSelectionModel().clearSelection();
		// 新建连接
		tfName.setText(rm.getString("msg.name.new"));
		tfServer.setText("");
		spnPort.setValue(10040);
		tfRemark.setText(rm.getString("msg.remark.new"));
	}

	@Action
	public void copy()
	{
		// 检查选择
		ServerConnection conn = (ServerConnection)listConn.getSelectedValue();
		if(conn != null)
		{
			// 清除选择状态
			listConn.getSelectionModel().clearSelection();
			// 复制连接
			tfName.setText(rm.getString("msg.name.copy", conn.getName()));
		}
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnCopy;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblPort;
    private javax.swing.JLabel lblRemark;
    private javax.swing.JLabel lblServer;
    private javax.swing.JList listConn;
    private javax.swing.JScrollPane listScrollPane;
    private javax.swing.JScrollPane remarkScrollPane;
    private javax.swing.JSpinner spnPort;
    private javax.swing.JTextField tfName;
    private javax.swing.JTextArea tfRemark;
    private javax.swing.JTextField tfServer;
    private javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables
}
