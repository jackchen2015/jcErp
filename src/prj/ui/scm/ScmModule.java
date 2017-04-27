/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prj.ui.scm;

/**
 * 供应链管理
 * @author Administrator
 */
public interface ScmModule
{
	//显示采购管理
	void showPurchasDialog();
	//显示销售管理
	void showSaleDialog();
	//显示库存管理
	void showInventoryDialog();
	//显示库存成本核算
	void showInventoryCaseDialog();
}
