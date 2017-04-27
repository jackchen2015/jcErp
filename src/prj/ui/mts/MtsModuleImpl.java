/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prj.ui.mts;

/**
 *
 * @author Administrator
 */
public class MtsModuleImpl implements MtsModule
{
	@Override
	public void showCcfkDialog()
	{
		CcfkPanel.showPanel();
	}

	@Override
	public void showCjxcDialog()
	{
		CjxcPanel.showPanel();
	}

	@Override
	public void showCjcnDialog()
	{
		CnjxPanel.showPanel();
	}

	@Override
	public void showDeviceRateDialog()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void showStaffWageAnalysisDialog()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
}
