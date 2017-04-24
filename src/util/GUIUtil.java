/*
 * Copyright 2010 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */

package util;

import com.hongxin.component.ComponentUtil;
import com.hongxin.component.PainterUtil;
//import com.hongxin.omc.util.OmcConstants;
//import com.hongxin.omc.util.OmcUtil;
//import com.hongxin.omc.util.SysEnvConf;
import com.hongxin.saf.SingleFrameApplication;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.JFormattedTextField;
import javax.swing.UIManager;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;
import net.infonode.docking.internalutil.InternalDockingUtil;
import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.painter.Painter;

/**
 * 界面工具类。
 * @author fanhuigang
 * Created on 2011-3-14, 10:32:26
 */
public class GUIUtil
{
	/**
	 * 本地语言字符串对象比较器。
	 */
	public static final Comparator STRING_COMPARATOR = ComponentUtil.STRING_COMPARATOR;
	
	/**
	 * 获取指定类型的组件对象。
	 * @param uiClass 目标组件class
	 * @return 组件对象
	 */
	public static Component getUI(Class uiClass)
	{
		// 获取当前所有视图
		ArrayList<net.infonode.docking.View> listView = new ArrayList<net.infonode.docking.View>();
		InternalDockingUtil.getViews(SingleFrameApplication.getInstance().getDockingRootWindow(), listView);
		// 遍历所有视图，检查视图组件
		Component target = null;
		for(net.infonode.docking.View view : listView)
		{			
			target = view.getComponent();
			if(target.getClass().equals(uiClass))
			{
				return target;
			}
		}
		// 遍历所有视图，检查视图组件子组件
		for(net.infonode.docking.View view : listView)
		{
			target = getUI(uiClass, view.getComponent());
			if(target != null)
			{
				return target;
			}
		}
		return null;
	}

	/**
	 * 获取指定类型的组件对象。
	 * @param uiClass 目标组件class
	 * @param parent 父组件
	 * @return 组件对象
	 */
	public static Component getUI(Class uiClass, Component parent)
	{
		if(parent.getClass().equals(uiClass))
		{
			return parent;
		}
		Component target = null;
		if(parent instanceof Container)
		{
			Container container = (Container)parent;
			for(int i = 0; i < container.getComponentCount(); i++)
			{
				target = getUI(uiClass, container.getComponent(i));
				if(target != null)
				{
					return target;
				}
			}
		}
		return null;
	}
	
	/**
	 * 获取包含指定类型组件的所有视图。
	 * @param uiClass 目标组件class
	 * @return 视图集合
	 */
	public static List<net.infonode.docking.View> getView(Class uiClass)
	{
		// 获取当前所有视图
		ArrayList<net.infonode.docking.View> listView = new ArrayList<net.infonode.docking.View>();
		InternalDockingUtil.getViews(SingleFrameApplication.getInstance().getDockingRootWindow(), listView);
		List<net.infonode.docking.View> listTargetView = new ArrayList<net.infonode.docking.View>();
		// 遍历所有视图，检查视图组件
		for(net.infonode.docking.View view : listView)
		{
			if(view.getComponent().getClass().equals(uiClass)
					|| getUI(uiClass, view.getComponent()) != null)
			{
				listTargetView.add(view);
			}
		}
		
		return listTargetView;
	}
	
	/**
	 * 缺省条纹painter。
	 * @return 缺省条纹painter
	 */
	public static Painter defaultPinstripePainter()
	{
		return PainterUtil.defaultPinstripePainter();
	}
	
	/**
	 * 缺省条纹painter。
	 * @param color 指定条纹颜色或null，使用缺省颜色
	 * @return 缺省条纹painter
	 */
	public static Painter defaultPinstripePainter(Color color)
	{
		return PainterUtil.defaultPinstripePainter(color);
	}
	
	/**
	 * 缺省title背景painter。
	 * @return 缺省title背景painter
	 */	
	public static Painter defaultTitleBackgroundPainter()
	{
		return PainterUtil.defaultTitleBackgroundPainter();
	}
	
	/**
	 * 对指定的painter进行alpha装饰。
	 * @param painter 目标painter
	 * @param alpha alpha值0f~1f
	 * @return 装饰后的painter
	 */
	public static Painter alphaDecoratePainter(Painter painter, float alpha)
	{
		return PainterUtil.alphaDecoratePainter(painter, alpha);
	}
	
	/**
	 * 对指定的painter进行gloss装饰。
	 * @param painter 目标painter
	 * @param topPosition 是否位于top位置
	 * @return 装饰后的painter
	 */
	public static Painter glossDecoratePainter(Painter painter, boolean topPosition)
	{
		return PainterUtil.glossDecoratePainter(painter, topPosition);
	}
	
	/**
	 * 对指定的painter进行alpha和gloss装饰。
	 * @param painter 目标painter
	 * @param alpha alpha值0f~1f
	 * @param topPosition 是否位于top位置
	 * @return 装饰后的painter
	 */	
	public static Painter alphaGlossDecoratePainter(Painter painter, float alpha, boolean topPosition)
	{
		return PainterUtil.alphaGlossDecoratePainter(painter, alpha, topPosition);
	}
	
	/**
	 * 创建统一风格的表格组件。
	 * @return 表格组件
	 */
	public static JXTable createTable()
	{
		return ComponentUtil.createTable();
	}

	/**
	 * 创建统一风格的treetable组件。
	 * @return treetable组件
	 */	
	public static JXTreeTable createTreeTable()
	{
		return ComponentUtil.createTreeTable();
	}
	
	/**
	 * 为目标表格组件应用统一风格的配置。
	 */
	public static void configureTable(JXTable table)
	{
		ComponentUtil.configureTable(table);
	}
	
	/**
	 * 创建统一风格的列表组件。
	 * @return 列表组件
	 */	
	public static JXList createList()
	{
		return ComponentUtil.createList();
	}
	
	/**
	 * 为目标列表组件应用统一风格的配置。
	 */
	public static void configureList(JXList list)
	{
		ComponentUtil.configureList(list);
	}
	
	/**
	 * 创建统一风格的树组件。
	 * @return 树组件
	 */	
	public static JXTree createTree()
	{
		return ComponentUtil.createTree();
	}
	
	/**
	 * 为目标树组件应用统一风格的配置。
	 */
	public static void configureTree(JXTree tree)
	{
		ComponentUtil.configureTree(tree);
	}
	
	/**
	 * 创建统一风格的sheet组件。
	 * @return sheet组件
	 */		
	public static PropertySheetPanel createPropertySheet()
	{
		return ComponentUtil.createPropertySheet();
	}
	
	/**
	 * 为目标sheet组件应用统一风格的配置。
	 */
	public static void configurePropertySheet(PropertySheetPanel propertySheet)
	{
		ComponentUtil.configurePropertySheet(propertySheet);
	}
	
	/**
	 * 创建统一风格的标题面板组件。
	 * @return 标题面板组件
	 */
	public static JXTitledPanel createTitledPanel()
	{
		return ComponentUtil.createTitledPanel();
	}
	
	/**
	 * 为目标标题面板组件应用统一风格的配置。
	 * @param titledPanel 标题面板组件
	 */
	public static void configureTitledPanel(JXTitledPanel titledPanel)
	{
		ComponentUtil.configureTitledPanel(titledPanel);
	}
	
//	/**
//	 * 统一配置eid文本输入组件。
//	 * @param editor eid文本输入组件
//	 */
//	public static void configureEidTextField(JFormattedTextField editor)
//	{
//		try
//		{
//			DefaultFormatter formatter;
//			if(SysEnvConf.getEidFormat() == OmcConstants.eid_format_10)
//			{
//				NumberFormat format = NumberFormat.getNumberInstance();
//				format.setGroupingUsed(false);
//				formatter = new NumberFormatter(format);
//			}
//			else
//			{
//				formatter = new MaskFormatter("HHHHHHHH");
//				((MaskFormatter)formatter).setPlaceholderCharacter('0');
//			}
//			editor.setFormatterFactory(new DefaultFormatterFactory(formatter));
//		}
//		catch(Exception exp)
//		{}
//	}
	
//	/**
//	 * 将eid文本从显示格式转换为存储格式。
//	 * @param text 显示格式的eid文本
//	 * @param radix 目标eid格式
//	 * @return 转换后的eid
//	 */	
//	public static String convertViewEidToModel(String eid, int radix)
//	{
//		return OmcUtil.getEidText(eid, SysEnvConf.getEidFormat(), radix);
//	}
	
//	/**
//	 * 将eid文本从存储格式转换为显示格式。
//	 * @param text 存储格式的eid文本
//	 * @param radix 存储格式
//	 * @return 转换后的eid
//	 */	
//	public static String convertModelEidToView(String eid, int radix)
//	{
//		return OmcUtil.getEidText(eid, radix, SysEnvConf.getEidFormat());
//	}
	
	public static boolean isThemeSupported()
	{
		return UIManager.getLookAndFeel() instanceof com.incors.plaf.alloy.AlloyLookAndFeel;
	}
	
	public static boolean isCurrentTheme(String themeName)
	{
		String currentThemeName = "sys.laf.theme";
		return currentThemeName != null && currentThemeName.equalsIgnoreCase(themeName);
	}	
	
}
