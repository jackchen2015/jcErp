/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prj.ui.mts;

import com.hongxin.saf.SingleFrameApplication;
import net.infonode.docking.DockingWindow;
import net.infonode.docking.DockingWindowAdapter;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import util.Constants;

/**
 *
 * @author Administrator
 */
public class CjxcPanel extends javax.swing.JPanel
{

	private ResourceMap resourceMap;
	/**
	 * 视图事件监听器。
	 */
	private CjxcViewListener viewListener;	
	/**
	 * Creates new form CjxcPanel
	 */
	public CjxcPanel()
	{
		initComponents();
		initialize();
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

        setName("Form"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

	/**
	 * 显示软件批处理视图。
	 */
	public static void showPanel()
	{
		net.infonode.docking.View view = SingleFrameApplication.getInstance().getView(Constants.VIEW_CJXC);
		if(view == null)
		{
			CjxcPanel sbv = new CjxcPanel();
			view = SingleFrameApplication.getInstance().addDynamicView(Constants.VIEW_CJXC,
					sbv.resourceMap.getString("dynamicview.cjxcPanel"),
					sbv.resourceMap.getIcon("image.cjxc"),
					sbv);
			view.addListener(sbv.viewListener);
		}
		else
		{
			view.restoreFocus();
		}
	}

	private void initialize()
	{
		resourceMap = Application.getInstance().getContext().getResourceMap(CjxcPanel.class);
	}
	
	private void clear()
	{
		
	}

	private class CjxcViewListener extends DockingWindowAdapter
	{
		@Override
		public void windowClosed(DockingWindow window)
		{
			// 清除资源
			clear();
			// 删除视图事件监听器
			window.removeListener(viewListener);
			viewListener = null;
		}
	}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
