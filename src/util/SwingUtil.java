/*
 * Copyright 2015 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */

package util;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

/**
 *
 * @author chenwei
 * Created on 2017-4-20, 15:46:15
 */
public class SwingUtil
{

	/** 
 * 隐藏表格中的某一列 
 * @param table  表格 
 * @param index  要隐藏的列 的索引
 */ 
public static void hideColumn(JTable table,int index){ 
    TableColumn tc= table.getColumnModel().getColumn(index); 
    tc.setMaxWidth(0); 
    tc.setPreferredWidth(0); 
    tc.setMinWidth(0); 
    tc.setWidth(0); 
    table.getTableHeader().getColumnModel().getColumn(index).setMaxWidth(0); 
    table.getTableHeader().getColumnModel().getColumn(index).setMinWidth(0); 
}
}
