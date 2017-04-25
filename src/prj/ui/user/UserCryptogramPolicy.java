/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NewJPanel.java
 *
 * Created on 2011-6-14, 9:52:56
 */
package com.hongxin.omc.ui.user;

import com.hongxin.omc.operation.Command;
import com.hongxin.omc.operation.OmcProcessor;
import com.hongxin.omc.operation.UserSession;
import com.hongxin.omc.protocol.CryptoGramPolicyInfo;
import com.hongxin.omc.protocol.NetAddressSegment;
import com.hongxin.omc.ui.util.FileDialogUtil;
import com.hongxin.omc.util.OmcConstants;
import com.hongxin.speed.core.ProcessData;
import com.hongxin.util.GUIUtils;
import java.awt.BorderLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

/**
 * 安全策略管理。
 *
 * @author dangzerong
 */
public class UserCryptogramPolicy extends javax.swing.JPanel
{
	/**
	 * 资源文件对象
	 */
	private ResourceMap rm;
	/**
	 * 窗体对象
	 */
	private JDialog dialog;
	/**
	 * 策略对象。
	 */
	private CryptoGramPolicyInfo policy;
	private byte[] datas = null;

	/**
	 * Creates new form UserCryptogramPolicy
	 */
	private UserCryptogramPolicy()
	{
		rm = Application.getInstance().getContext().getResourceMap(UserCryptogramPolicy.class);
		initComponents();
		// 获得安全策略配置
		getUserCryptogramPolicyData();
	}

	/**
	 * 创建唯一实例
	 *
	 * @param parent
	 * @param modal
	 * @return 窗体
	 */
	public static JDialog getInstance(java.awt.Frame parent, boolean modal)
	{
		UserCryptogramPolicy panel = new UserCryptogramPolicy();
		panel.dialog = new JDialog(parent, modal);
		panel.dialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		panel.dialog.setName(panel.getName());
		panel.dialog.setTitle(panel.rm.getString("form.title"));
		panel.dialog.setLayout(new BorderLayout());
		panel.dialog.add(panel, BorderLayout.CENTER);
		GUIUtils.addHideAction(panel.dialog);
		panel.dialog.pack();
		return panel.dialog;
	}

	/**
	 * 获取安全策略配置。
	 */
	private void getUserCryptogramPolicyData()
	{
		// 发送逻辑执行
		ProcessData out = OmcProcessor.process(Command.user, 
				Command.getUserCryptogramPolicy, null);
		// 成功
		if(out.getState() == OmcConstants.coes_success)
		{
			// 获得数据
			if(out.getData() != null)
			{
				// 赋值被对象
				policy = (CryptoGramPolicyInfo)out.getData();
				policy.copyTo(UserSession.getInstance().getUserInfo().getUserObject());
			}
		}
		else
		{
			// 失败，则使用默认安全策略
			// 弹出提示框信息错误信息
			JOptionPane.showMessageDialog(null,
					rm.getString("msg.getUserCryptogramPolicyData.fail"),
					rm.getString("msg.tip"),
					JOptionPane.ERROR_MESSAGE);
			policy = CryptoGramPolicyInfo.defaultPolicy();
		}
		setComplicacyData(policy);
	}

	/**
	 * 设置安全策略数据。
	 * @param policy 安全策略对象
	 */
	private void setComplicacyData(CryptoGramPolicyInfo policy)
	{
		// 用户名最小长度
		if(policy.getUserMinLength() < 0)
		{
			userMinLength.setValue(0);
		}
		else
		{
			userMinLength.setValue(policy.getUserMinLength());
		}
		if(policy.getUserMaxLength() < 0)
		{
			userMaxLength.setValue(0);
		}
		else
		{
			userMaxLength.setValue(policy.getUserMaxLength());
		}
		// 有效期
		if(policy.getPeriodOfValidity() < 0)
		{
			periodOfvalidity.setValue(0);
		}
		else
		{
			periodOfvalidity.setValue(policy.getPeriodOfValidity());
		}
		// 到期提醒
		if(policy.isAwaken())
		{
			enablAwaken.setSelected(true);
		}
		else
		{
			disableAwaken.setSelected(true);
		}
		// 提前提醒天数
		if(policy.getAwakenDays() < 0)
		{
			awakenDays.setValue(0);
		}
		else
		{
			awakenDays.setValue(policy.getAwakenDays());
		}
		// 最小长度
		if(policy.getMinLength() <= 6)
		{
			minLength.setValue(6);
		}
		else
		{
			minLength.setValue((Integer)policy.getMinLength());
		}
		// 同一字符禁止重复次数
		charRepeatLimit.setValue(policy.getCharRepeatLimit()); 
		// 连续输入口令错误锁定账号次数
		maxFaultRetry.setValue(policy.getMaxFaultRetry()); 
		// 安全最大长度
		maxLength.setValue(policy.getMaxLength()); 
		// 是否包含字符
		chkEnableChar.setSelected(policy.isContainChar());
		// 是否包含数字
		chkEnableNumber.setSelected(policy.isContainNumber());
		// 是否包含特殊字符
		chkEnableEspecialChar.setSelected(policy.isContainEspecialChar());
		// 是否允许和名称相同
		chkEnableSameAsName.setSelected(policy.isSameAsName());
		// 是否启用安全字典文件
		chkUsePasswordFile.setSelected(policy.isUsePasswordFile());
		// 首次登陆强制修改密码
		chkChangePassword.setSelected(policy.isChangePassword());
		// 安全字典文件
		if(policy.isUsePasswordFile())
		{
			btn_choose.setEnabled(true);
			tfFileName.setText(policy.getPasswordFile()); 
		}
		// 全局安全策略
		String policyText = policy.getFileTransMode();
		cbFileTransMode.setSelected(policyText != null && !policyText.isEmpty());
		if(cbFileTransMode.isSelected())
		{
			cmbFileTransMode.setSelectedItem(policyText);
		}
		policyText = policy.getConnectMode();
		cbConnectMode.setSelected(policyText != null && !policyText.isEmpty());
		if(cbConnectMode.isSelected())
		{
			String[] valueArray = policyText.split("\\|");
			for(String value : valueArray)
			{
				if(value.equalsIgnoreCase("tcp"))
				{
					cbTcp.setSelected(true);
				}
				else if(value.equalsIgnoreCase("udp"))
				{
					cbUdp.setSelected(true);
				}
			}
		}
		// 组网安全策略
		policyText = policy.getWhiteListForOmc();
		cbOmcWhiteList.setSelected(policyText != null && !policyText.isEmpty());
		if(cbOmcWhiteList.isSelected())
		{
			String[] valueArray = policyText.split("\\|");
			List<NetAddressSegment> listSegment = new ArrayList();
			for(String value : valueArray)
			{
				String[] segmentArray = value.split("~");
				NetAddressSegment segment = new NetAddressSegment();
				segment.setStartAddress(segmentArray[0]);
				segment.setEndAddress(segmentArray[1]);
				listSegment.add(segment);
			}
			omcWhiteList.setNetAddressSegment(listSegment);
		}
		policyText = policy.getWhiteListForElement();
		cbElementWhiteList.setSelected(policyText != null && !policyText.isEmpty());
		if(cbElementWhiteList.isSelected())
		{
			String[] valueArray = policyText.split("\\|");
			List<NetAddressSegment> listSegment = new ArrayList();
			for(String value : valueArray)
			{
				String[] segmentArray = value.split("~");
				NetAddressSegment segment = new NetAddressSegment();
				segment.setStartAddress(segmentArray[0]);
				segment.setEndAddress(segmentArray[1]);
				listSegment.add(segment);
			}
			elementWhiteList.setNetAddressSegment(listSegment);
		}
		// 用户自动锁定时长
		spnLockPeriod.setValue(policy.getLockPeriod());
		// 锁定用户登录提示信息
		tfLockHint.setText(policy.getLockHint());
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
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        buttonGroup1 = new javax.swing.ButtonGroup();
        jideSplitPane1 = new com.jidesoft.swing.JideSplitPane();
        jLabel3 = new javax.swing.JLabel();
        periodOfvalidity = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        enablAwaken = new javax.swing.JRadioButton();
        disableAwaken = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        minLength = new javax.swing.JSpinner();
        jLabel6 = new javax.swing.JLabel();
        chkEnableChar = new javax.swing.JCheckBox();
        chkEnableNumber = new javax.swing.JCheckBox();
        chkEnableEspecialChar = new javax.swing.JCheckBox();
        chkEnableSameAsName = new javax.swing.JCheckBox();
        btnOk = new javax.swing.JButton();
        btnQuit = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        maxLength = new javax.swing.JSpinner();
        jLabel7 = new javax.swing.JLabel();
        chkChangePassword = new javax.swing.JCheckBox();
        spnLockPeriod = new javax.swing.JSpinner();
        lblLockPeriodUnit = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        maxFaultRetry = new javax.swing.JSpinner();
        jLabel10 = new javax.swing.JLabel();
        charRepeatLimit = new javax.swing.JSpinner();
        chkUsePasswordFile = new javax.swing.JCheckBox();
        btn_choose = new javax.swing.JButton();
        tfFileName = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        separator1 = new org.jdesktop.swingx.JXTitledSeparator();
        separator2 = new org.jdesktop.swingx.JXTitledSeparator();
        cbFileTransMode = new javax.swing.JCheckBox();
        cmbFileTransMode = new javax.swing.JComboBox();
        cbConnectMode = new javax.swing.JCheckBox();
        cbTcp = new javax.swing.JCheckBox();
        cbUdp = new javax.swing.JCheckBox();
        separator3 = new org.jdesktop.swingx.JXTitledSeparator();
        omcWhiteList = new com.hongxin.omc.ui.common.NetAddressConfigPanel();
        cbOmcWhiteList = new javax.swing.JCheckBox();
        cbElementWhiteList = new javax.swing.JCheckBox();
        elementWhiteList = new com.hongxin.omc.ui.common.NetAddressConfigPanel();
        lblLockPeriod = new javax.swing.JLabel();
        lblLockPeriod1 = new javax.swing.JLabel();
        tfLockHint = new javax.swing.JTextField();
        separator4 = new org.jdesktop.swingx.JXTitledSeparator();
        jLabel9 = new javax.swing.JLabel();
        userMinLength = new javax.swing.JSpinner();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        userMaxLength = new javax.swing.JSpinner();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        awakenDays = new javax.swing.JSpinner();

        jideSplitPane1.setName("jideSplitPane1"); // NOI18N

        setName("Form_UserCryptogramPolicy_9"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(UserCryptogramPolicy.class);
        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        periodOfvalidity.setModel(new javax.swing.SpinnerNumberModel(0, 0, 365, 1));
        periodOfvalidity.setName("periodOfvalidity"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        buttonGroup1.add(enablAwaken);
        enablAwaken.setText(resourceMap.getString("enablAwaken.text")); // NOI18N
        enablAwaken.setBorder(null);
        enablAwaken.setName("enablAwaken"); // NOI18N

        buttonGroup1.add(disableAwaken);
        disableAwaken.setText(resourceMap.getString("disableAwaken.text")); // NOI18N
        disableAwaken.setBorder(null);
        disableAwaken.setName("disableAwaken"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        minLength.setModel(new javax.swing.SpinnerNumberModel(6, 6, 20, 1));
        minLength.setName("minLength"); // NOI18N
        minLength.setVerifyInputWhenFocusTarget(false);

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        chkEnableChar.setText(resourceMap.getString("chkEnableChar.text")); // NOI18N
        chkEnableChar.setBorder(null);
        chkEnableChar.setName("chkEnableChar"); // NOI18N

        chkEnableNumber.setText(resourceMap.getString("chkEnableNumber.text")); // NOI18N
        chkEnableNumber.setBorder(null);
        chkEnableNumber.setName("chkEnableNumber"); // NOI18N

        chkEnableEspecialChar.setText(resourceMap.getString("chkEnableEspecialChar.text")); // NOI18N
        chkEnableEspecialChar.setBorder(null);
        chkEnableEspecialChar.setName("chkEnableEspecialChar"); // NOI18N

        chkEnableSameAsName.setText(resourceMap.getString("chkEnableSameAsName.text")); // NOI18N
        chkEnableSameAsName.setBorder(null);
        chkEnableSameAsName.setName("chkEnableSameAsName"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(UserCryptogramPolicy.class, this);
        btnOk.setAction(actionMap.get("save")); // NOI18N
        btnOk.setText(resourceMap.getString("btnOk.text")); // NOI18N
        btnOk.setName("btnOk"); // NOI18N

        btnQuit.setAction(actionMap.get("exit")); // NOI18N
        btnQuit.setText(resourceMap.getString("btnQuit.text")); // NOI18N
        btnQuit.setName("btnQuit"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        maxLength.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(10), Integer.valueOf(6), null, Integer.valueOf(1)));
        maxLength.setName("maxLength"); // NOI18N

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        chkChangePassword.setText(resourceMap.getString("chkChangePassword.text")); // NOI18N
        chkChangePassword.setBorder(null);
        chkChangePassword.setName("chkChangePassword"); // NOI18N

        spnLockPeriod.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(1), null, Integer.valueOf(1)));
        spnLockPeriod.setName("spnLockPeriod"); // NOI18N

        lblLockPeriodUnit.setText(resourceMap.getString("lblLockPeriodUnit.text")); // NOI18N
        lblLockPeriodUnit.setName("lblLockPeriodUnit"); // NOI18N

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        maxFaultRetry.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(6), Integer.valueOf(0), null, Integer.valueOf(1)));
        maxFaultRetry.setName("maxFaultRetry"); // NOI18N

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        charRepeatLimit.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(0), Integer.valueOf(0), null, Integer.valueOf(1)));
        charRepeatLimit.setName("charRepeatLimit"); // NOI18N

        chkUsePasswordFile.setText(resourceMap.getString("chkUsePasswordFile.text")); // NOI18N
        chkUsePasswordFile.setBorder(null);
        chkUsePasswordFile.setName("chkUsePasswordFile"); // NOI18N
        chkUsePasswordFile.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                chkUsePasswordFileActionPerformed(evt);
            }
        });

        btn_choose.setAction(actionMap.get("chooseFile")); // NOI18N
        btn_choose.setText(resourceMap.getString("btn_choose.text")); // NOI18N
        btn_choose.setEnabled(false);
        btn_choose.setName("btn_choose"); // NOI18N

        tfFileName.setText(resourceMap.getString("tfFileName.text")); // NOI18N
        tfFileName.setEnabled(false);
        tfFileName.setName("tfFileName"); // NOI18N

        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N

        separator1.setTitle(resourceMap.getString("separator1.title")); // NOI18N
        separator1.setName("separator1"); // NOI18N

        separator2.setTitle(resourceMap.getString("separator2.title")); // NOI18N
        separator2.setName("separator2"); // NOI18N

        cbFileTransMode.setText(resourceMap.getString("cbFileTransMode.text")); // NOI18N
        cbFileTransMode.setName("cbFileTransMode"); // NOI18N

        cmbFileTransMode.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "FTP" }));
        cmbFileTransMode.setName("cmbFileTransMode"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cbFileTransMode, org.jdesktop.beansbinding.ELProperty.create("${selected}"), cmbFileTransMode, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        cbConnectMode.setText(resourceMap.getString("cbConnectMode.text")); // NOI18N
        cbConnectMode.setName("cbConnectMode"); // NOI18N

        cbTcp.setText(resourceMap.getString("cbTcp.text")); // NOI18N
        cbTcp.setName("cbTcp"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cbConnectMode, org.jdesktop.beansbinding.ELProperty.create("${selected}"), cbTcp, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        cbUdp.setText(resourceMap.getString("cbUdp.text")); // NOI18N
        cbUdp.setName("cbUdp"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cbConnectMode, org.jdesktop.beansbinding.ELProperty.create("${selected}"), cbUdp, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        separator3.setTitle(resourceMap.getString("separator3.title")); // NOI18N
        separator3.setName("separator3"); // NOI18N

        omcWhiteList.setName("omcWhiteList"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cbOmcWhiteList, org.jdesktop.beansbinding.ELProperty.create("${selected}"), omcWhiteList, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        cbOmcWhiteList.setText(resourceMap.getString("cbOmcWhiteList.text")); // NOI18N
        cbOmcWhiteList.setName("cbOmcWhiteList"); // NOI18N

        cbElementWhiteList.setText(resourceMap.getString("cbElementWhiteList.text")); // NOI18N
        cbElementWhiteList.setName("cbElementWhiteList"); // NOI18N

        elementWhiteList.setName("elementWhiteList"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, cbElementWhiteList, org.jdesktop.beansbinding.ELProperty.create("${selected}"), elementWhiteList, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        lblLockPeriod.setText(resourceMap.getString("lblLockPeriod.text")); // NOI18N
        lblLockPeriod.setName("lblLockPeriod"); // NOI18N

        lblLockPeriod1.setText(resourceMap.getString("lblLockPeriod1.text")); // NOI18N
        lblLockPeriod1.setName("lblLockPeriod1"); // NOI18N

        tfLockHint.setText(resourceMap.getString("tfLockHint.text")); // NOI18N
        tfLockHint.setName("tfLockHint"); // NOI18N

        separator4.setTitle(resourceMap.getString("separator4.title")); // NOI18N
        separator4.setName("separator4"); // NOI18N

        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        userMinLength.setModel(new javax.swing.SpinnerNumberModel(1, 1, 30, 1));
        userMinLength.setName("userMinLength"); // NOI18N
        userMinLength.setVerifyInputWhenFocusTarget(false);

        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N

        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N

        userMaxLength.setModel(new javax.swing.SpinnerNumberModel(1, 1, 30, 1));
        userMaxLength.setName("userMaxLength"); // NOI18N
        userMaxLength.setVerifyInputWhenFocusTarget(false);

        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N

        jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N

        jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N

        awakenDays.setModel(new javax.swing.SpinnerNumberModel(0, 0, 365, 1));
        awakenDays.setName("awakenDays"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(omcWhiteList, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addComponent(cbOmcWhiteList)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbElementWhiteList)
                            .addComponent(elementWhiteList, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addGap(10, 10, 10))
                    .addComponent(separator4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(separator3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(separator1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(separator2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(charRepeatLimit, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(userMinLength, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11)
                                .addGap(24, 24, 24)
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(userMaxLength, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel14))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(cbFileTransMode)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbFileTransMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(cbConnectMode)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cbTcp)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbUdp)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel5)
                                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(lblLockPeriod)
                                            .addComponent(jLabel15))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(awakenDays, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(minLength, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(periodOfvalidity, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel6)
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(jLabel4)
                                                        .addComponent(jLabel16))))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(enablAwaken)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(disableAwaken))
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(maxLength, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(spnLockPeriod, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel7)
                                                    .addComponent(lblLockPeriodUnit)))))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(maxFaultRetry, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel12)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tfFileName))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(lblLockPeriod1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tfLockHint))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(chkEnableChar)
                                            .addComponent(chkEnableNumber)
                                            .addComponent(chkEnableEspecialChar)
                                            .addComponent(chkEnableSameAsName)
                                            .addComponent(chkChangePassword)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(chkUsePasswordFile)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btn_choose)))
                                        .addGap(0, 18, Short.MAX_VALUE))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnOk)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnQuit)))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(separator4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(userMinLength, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(jLabel13)
                    .addComponent(userMaxLength, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(separator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(periodOfvalidity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkEnableChar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(enablAwaken)
                    .addComponent(disableAwaken)
                    .addComponent(jLabel5)
                    .addComponent(chkEnableNumber))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel15)
                        .addComponent(jLabel16)
                        .addComponent(awakenDays, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chkEnableEspecialChar, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(minLength, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(chkEnableSameAsName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel7)
                            .addComponent(chkChangePassword)
                            .addComponent(maxLength, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblLockPeriod)
                            .addComponent(tfLockHint, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblLockPeriod1)))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(spnLockPeriod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblLockPeriodUnit)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(maxFaultRetry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkUsePasswordFile)
                    .addComponent(btn_choose))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel12)
                        .addComponent(tfFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel10)
                        .addComponent(charRepeatLimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(separator2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbFileTransMode)
                    .addComponent(cmbFileTransMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbConnectMode)
                    .addComponent(cbTcp)
                    .addComponent(cbUdp))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(separator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbOmcWhiteList)
                    .addComponent(cbElementWhiteList))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(omcWhiteList, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                        .addGap(39, 39, 39))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(elementWhiteList, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnQuit)
                            .addComponent(btnOk))
                        .addContainerGap())))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {maxLength, minLength});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jLabel1, jLabel2});

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void chkUsePasswordFileActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_chkUsePasswordFileActionPerformed
    {//GEN-HEADEREND:event_chkUsePasswordFileActionPerformed
		this.btn_choose.setEnabled(chkUsePasswordFile.isSelected());
    }//GEN-LAST:event_chkUsePasswordFileActionPerformed

	/**
	 * 保存安全策略配置
	 */
	@Action
	public void save()
	{
		// 如果为NULL
		if(policy == null)
		{
			policy = new CryptoGramPolicyInfo();
		}
		// 安全有效期
		policy.setPeriodOfValidity((Integer)periodOfvalidity.getValue());
		// 到期提醒
		policy.setAwaken(enablAwaken.isSelected());
		policy.setAwakenDays((Integer)awakenDays.getValue());
		// 安全最小长度
		if(policy.getMinLength() <= 6)
		{
			policy.setMinLength(6);
		}
		// 密码最小长度大于最长长度，不合理
		if((Integer)minLength.getValue() > (Integer)maxLength.getValue())
		{
			JOptionPane.showMessageDialog(null,
					rm.getString("msg.maxLenLessThanMin"),
					rm.getString("msg.inputerror"),
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		// 用户名最小长度要大于0
		if((Integer)userMinLength.getValue() <= 0)
		{
			JOptionPane.showMessageDialog(null,
					rm.getString("msg.user.minLen.error"),
					rm.getString("msg.inputerror"),
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		// 用户名最大长度要大于0
		if((Integer)userMaxLength.getValue() <= 0)
		{
			JOptionPane.showMessageDialog(null,
					rm.getString("msg.user.maxLen.error"),
					rm.getString("msg.inputerror"),
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		// 用户名最小长度大于最长长度，不合理
		if((Integer)minLength.getValue() > (Integer)maxLength.getValue())
		{
			JOptionPane.showMessageDialog(null,
					rm.getString("msg.user.maxLenLessThanMin"),
					rm.getString("msg.inputerror"),
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		// 如果启用外部安全字典文件
		if(chkUsePasswordFile.isSelected() 
				&& (tfFileName.getText() == null || tfFileName.getText().trim().isEmpty()))
		{
			JOptionPane.showMessageDialog(null,
					rm.getString("msg.fileNameIsEmpty"),
					rm.getString("msg.tip"),
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		// 锁定提示信息长度检查
		if(tfLockHint.getText().trim().getBytes().length >= 100)
		{
			JOptionPane.showMessageDialog(null,
					rm.getString("msg.lock.hint.max", 100),
					rm.getString("msg.error"),
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		// 用户名最小长度
		policy.setUserMinLength((Integer)userMinLength.getValue());
		// 用户名最大长度
		policy.setUserMaxLength((Integer)userMaxLength.getValue());
		// 安全最小长度
		policy.setMinLength((Integer)minLength.getValue());
		// 安全最大长度
		policy.setMaxLength((Integer)maxLength.getValue());
		// 是否包含字符
		policy.setContainChar(chkEnableChar.isSelected());
		// 是否包含数字
		policy.setContainNumber(chkEnableNumber.isSelected());
		// 是否包含特殊字符
		policy.setContainEspecialChar(chkEnableEspecialChar.isSelected());
		// 是否允许与名称相同
		policy.setSameAsName(chkEnableSameAsName.isSelected());
		// 同一字符禁止重复次数
		policy.setCharRepeatLimit((Integer)charRepeatLimit.getValue());
		// 连续输入口令错误即锁定账号次数
		policy.setMaxFaultRetry((Integer)maxFaultRetry.getValue());
		// 是否启用外部安全字典文件
		policy.setUsePasswordFile(chkUsePasswordFile.isSelected());
		// 首次登陆强制修改密码
		policy.setChangePassword(chkChangePassword.isSelected());
		// 如果启用外部安全字典文件
		if(chkUsePasswordFile.isSelected())
		{
			// 设置安全字典文件
			policy.setPasswordFile(tfFileName.getText());
		}
		// 扩展安全策略
		// 全局安全策略
		if(cbFileTransMode.isSelected())
		{
			policy.setFileTransMode((String)cmbFileTransMode.getSelectedItem());
		}
		else
		{
			policy.setFileTransMode(null);
		}
		if(cbConnectMode.isSelected()
				&& (cbTcp.isSelected() || cbUdp.isSelected()))
		{
			StringBuilder sbd = new StringBuilder();
			if(cbTcp.isSelected())
			{
				sbd.append("TCP");
			}
			if(cbUdp.isSelected())
			{
				if(sbd.length() > 0)
				{
					sbd.append("|");
				}
				sbd.append("UDP");
			}
			policy.setConnectMode(sbd.toString());
		}
		else
		{
			policy.setConnectMode(null);
		}
		// 组网安全策略
		if(cbOmcWhiteList.isSelected())
		{
			StringBuilder sbd = new StringBuilder();
			List<NetAddressSegment> listSegment = omcWhiteList.getAllSegment();
			for(NetAddressSegment segment : listSegment)
			{
				if(sbd.length() > 0)
				{
					sbd.append("|");
				}
				sbd.append(segment.getStartAddress()).append("~").append(segment.getEndAddress());
			}
			policy.setWhiteListForOmc(sbd.toString());
		}
		else
		{
			policy.setWhiteListForOmc(null);
		}
		if(cbElementWhiteList.isSelected())
		{
			StringBuilder sbd = new StringBuilder();
			List<NetAddressSegment> listSegment = elementWhiteList.getAllSegment();
			for(NetAddressSegment segment : listSegment)
			{
				if(sbd.length() > 0)
				{
					sbd.append("|");
				}
				sbd.append(segment.getStartAddress()).append("~").append(segment.getEndAddress());
			}
			policy.setWhiteListForElement(sbd.toString());
		}
		else
		{
			policy.setWhiteListForElement(null);
		}
		// 用户自动锁定时长
		policy.setLockPeriod((Integer)spnLockPeriod.getValue());
		// 锁定用户登录提示信息
		policy.setLockHint(tfLockHint.getText().trim());
		// 参数列表
		List list = new ArrayList();
		// 安全策略对象
		list.add(policy);
		// 文件内容
		list.add(datas);
		// 组帧发送
		ProcessData out = OmcProcessor.process(Command.user, 
				Command.saveUserCryptogramPolicy, list);
		switch(out.getState())
		{
			// 失败
			case 0:
				JOptionPane.showMessageDialog(this,
						rm.getString("mb.save.fail"),
						rm.getString("msg.tip"),
						JOptionPane.ERROR_MESSAGE);
				break;
			// 成功
			case 1:
				JOptionPane.showMessageDialog(this,
						rm.getString("mb.save.success"),
						rm.getString("msg.tip"),
						JOptionPane.INFORMATION_MESSAGE);
				dialog.dispose();
				policy.copyTo(UserSession.getInstance().getUserInfo().getUserObject());
				break;
		}
	}

	/**
	 * 退出释放
	 */
	@Action
	public void exit()
	{
		dialog.dispose();
	}

	@Action
	public void chooseFile()
	{
		// 获取选择的文件
		File file = FileDialogUtil.showOpenDialog(this, false, new String[] {"txt"});
		if(file == null)
		{
			return;
		}
		// 文件名
		String fileName = file.getName();
		// 文件流
		FileInputStream fis = null;
		int available = 0;
		try
		{
			// 输入流
			fis = new FileInputStream(file);
			// 获取数据长度
			available = fis.available();
		}
		catch(Exception ex)
		{
			Logger.getLogger(UserCryptogramPolicy.class.getName()).
					log(Level.SEVERE, null, ex);
		}
		// 判读数据是否过大
		if(available > 512 * 1024)
		{
			JOptionPane.showMessageDialog(null,
						rm.getString("msg.fileToLarge"),
						rm.getString("msg.inputerror"),
						JOptionPane.ERROR_MESSAGE);
			tfFileName.setText("");
			datas = null;
			return;
		}
		// 显示文件名称
		tfFileName.setText(fileName); 
		// 初始化数据大小
		datas = new byte[available];
		try 
		{
			// 读取数据
			fis.read(datas);
		}
		catch(IOException ex)
		{
			Logger.getLogger(UserCryptogramPolicy.class.getName()).
					log(Level.SEVERE, null, ex);
		}
	}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner awakenDays;
    private javax.swing.JButton btnOk;
    private javax.swing.JButton btnQuit;
    private javax.swing.JButton btn_choose;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox cbConnectMode;
    private javax.swing.JCheckBox cbElementWhiteList;
    private javax.swing.JCheckBox cbFileTransMode;
    private javax.swing.JCheckBox cbOmcWhiteList;
    private javax.swing.JCheckBox cbTcp;
    private javax.swing.JCheckBox cbUdp;
    private javax.swing.JSpinner charRepeatLimit;
    private javax.swing.JCheckBox chkChangePassword;
    private javax.swing.JCheckBox chkEnableChar;
    private javax.swing.JCheckBox chkEnableEspecialChar;
    private javax.swing.JCheckBox chkEnableNumber;
    private javax.swing.JCheckBox chkEnableSameAsName;
    private javax.swing.JCheckBox chkUsePasswordFile;
    private javax.swing.JComboBox cmbFileTransMode;
    private javax.swing.JRadioButton disableAwaken;
    private com.hongxin.omc.ui.common.NetAddressConfigPanel elementWhiteList;
    private javax.swing.JRadioButton enablAwaken;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private com.jidesoft.swing.JideSplitPane jideSplitPane1;
    private javax.swing.JLabel lblLockPeriod;
    private javax.swing.JLabel lblLockPeriod1;
    private javax.swing.JLabel lblLockPeriodUnit;
    private javax.swing.JSpinner maxFaultRetry;
    private javax.swing.JSpinner maxLength;
    private javax.swing.JSpinner minLength;
    private com.hongxin.omc.ui.common.NetAddressConfigPanel omcWhiteList;
    private javax.swing.JSpinner periodOfvalidity;
    private org.jdesktop.swingx.JXTitledSeparator separator1;
    private org.jdesktop.swingx.JXTitledSeparator separator2;
    private org.jdesktop.swingx.JXTitledSeparator separator3;
    private org.jdesktop.swingx.JXTitledSeparator separator4;
    private javax.swing.JSpinner spnLockPeriod;
    private javax.swing.JTextField tfFileName;
    private javax.swing.JTextField tfLockHint;
    private javax.swing.JSpinner userMaxLength;
    private javax.swing.JSpinner userMinLength;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
