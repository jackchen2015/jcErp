/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prj.ui.mts;

/**
 *车间管理模块接口
 * @author Administrator
 */
public interface MtsModule
{
	//裁床发卡
	void showCcfkDialog();
	//车间现场监控
	void showCjxcDialog();
	//车间产能绩效
	void showCjcnDialog();
	//设备利用率
	void showDeviceRateDialog();
	//员工有效工时工资分析
	void showStaffWageAnalysisDialog();
}
