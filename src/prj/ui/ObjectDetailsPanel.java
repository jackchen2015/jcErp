/*
 * Copyright 2015 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */

/*
 * ObjectDetailsPanel.java
 *
 * Created on 2017-4-24, 16:03:50
 */

package prj.ui;

import com.l2fprod.common.propertysheet.PropertySheetTable;

/**
 *
 * @author chenwei
 */
public class ObjectDetailsPanel extends javax.swing.JPanel
{

    /** Creates new form ObjectDetailsPanel */
    public ObjectDetailsPanel()
	{
        initComponents();
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
	 * 对象信息属性表格。
	 */
	private class ObjectDetailsTable extends PropertySheetTable
	{
		public ObjectDetailsTable()
		{
			setShowPropertyEditableState(false);
		}
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
