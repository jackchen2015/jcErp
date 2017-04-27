/*
 * Copyright 2009 Hongxin Telecommunication Technologies Co, Ltd.,
 * Wuhan, Hubei, China. All rights reserved.
 */

package prj.ui.basic;

import com.hongxin.component.FileUtil;
import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

/**
 * 文件保存、加载工具类。
 * @author fanhuigang
 * Created on 2010-7-20, 10:10:06
 */
public class FileDialogUtil
{
	/**
	 * excel文件。
	 */
	public static final String FILE_XLS = "xls";
	public static final String DOT_FILE_XLS = ".xls";
	/**
	 * JPG图像文件。
	 */
	public static final String FILE_JPG = "jpg";
	public static final String DOT_FILE_JPG = ".jpg";
	/**
	 * PNG图像文件。
	 */
	public static final String FILE_PNG = "png";
	public static final String DOT_FILE_PNG = ".png";
	/**
	 * GIF图像文件。
	 */
	public static final String FILE_GIF = "gif";
	public static final String DOT_FILE_GIF = ".gif";
	/**
	 * HTML文件。
	 */
	public static final String FILE_HTML = "html";
	public static final String DOT_FILE_HTML = ".html";
	/**
	 * TRC信令跟踪文件。
	 */
	public static final String FILE_TRC = "sgnl";
	public static final String DOT_FILE_TRC = ".sgnl";
	/**
	 * DAT文件。
	 */
	public static final String FILE_DAT = "dat";
	public static final String DOT_FILE_DAT = ".dat";
	/**
	 * MML命令文件。
	 */
	public static final String FILE_CXT = "cxt";
	public static final String DOT_FILE_CXT = ".cxt";
	/**
	 * BIN文件。
	 */
	public static final String FILE_BIN = "bin";
	public static final String DOT_FILE_BIN = ".bin";
	/**
	 * IMG文件。
	 */
	public static final String FILE_IMG = "img";
	public static final String DOT_FILE_IMG = ".img";
	/**
	 * 文本文件。
	 */
	public static final String FILE_TXT = "txt";
	public static final String DOT_FILE_TXT = ".txt";
	/**
	 * PDF文件。
	 */
	public static final String FILE_PDF = "pdf";
	public static final String DOT_FILE_PDF = ".pdf";
	/**
	 * XML文件。
	 */
	public static final String FILE_XML = "xml";
	public static final String DOT_FILE_XML = ".xml";
	/**
	 * TCL文件。
	 */
	public static final String FILE_TCL = "tcl";
	public static final String DOT_FILE_TCL = ".tcl";
	/**
	 * ZIP文件。
	 */
	public static final String FILE_ZIP = "zip";
	public static final String DOT_FILE_ZIP = ".zip";
	/**
	 * DIV文件。
	 */
	public static final String FILE_DIV = "div";
	public static final String DOT_FILE_DIV = ".div";
	/**
	 * lic文件。
	 */
	public static final String FILE_LIC = "lic";
	public static final String DOT_FILE_LIC = ".lic";
	/**
	 * csv文件。
	 */
	public static final String FILE_CSV = "csv";
	public static final String DOT_FILE_CSV = ".csv";
	
	/**
	 * 打开文件保存对话框。
	 * @param parent parent组件或null
	 * @param remainAcceptAllFileFilter 是否保留接受所有文件类型过滤器
	 * @param extensions 文件扩展名或null，扩展名mime请在全局资源文件注册
	 * @return 目标文件或null
	 */
	public static File showSaveDialog(Component parent, 
			boolean remainAcceptAllFileFilter, 
			String[] extensions)
	{
		return FileUtil.showSaveDialog(parent, remainAcceptAllFileFilter, 
				extensions);
	}
	
	/**
	 * 打开文件保存对话框。
	 * @param parent parent组件或null
	 * @param remainAcceptAllFileFilter 是否保留接受所有文件类型过滤器
	 * @param extensions 文件扩展名或null，扩展名mime请在全局资源文件注册
	 * @param defaultFileName 缺省文件名称
	 * @return 目标文件或null
	 */
	public static File showSaveDialog(Component parent, 
			boolean remainAcceptAllFileFilter, 
			String[] extensions,
			String defaultFileName)
	{
		return FileUtil.showSaveDialog(parent, remainAcceptAllFileFilter, 
				extensions, defaultFileName);
	}

	/**
	 * 打开文件保存对话框。
	 * @param parent parent组件或null
	 * @param remainAcceptAllFileFilter 是否保留接受所有文件类型过滤器
	 * @param fileSelectionMode 文件选择模式，取值如下：
	 * <ul>
	 *	<li>{@link javax.swing.JFileChooser#FILES_ONLY}</li>
	 * 	<li>{@link javax.swing.JFileChooser#DIRECTORIES_ONLY}</li>
	 * 	<li>{@link javax.swing.JFileChooser#FILES_AND_DIRECTORIES}</li>
	 * </ul>
	 * @param extensions 文件扩展名或null，扩展名mime请在全局资源文件注册
	 * @param defaultFileName 缺省文件名称
	 * @param pcl 注册pcl到文件对话框
	 * @param accessory 文件预览组件
	 * @return 目标文件或null
	 */
	public static File showSaveDialog(final Component parent, 
			final boolean remainAcceptAllFileFilter, 
			final int fileSelectionMode, 
			final String[] extensions,
			final String defaultFileName,
			PropertyChangeListener pcl,
			JComponent accessory)
	{
		return FileUtil.showSaveDialog(parent, remainAcceptAllFileFilter, 
				null, fileSelectionMode, extensions, 
				defaultFileName, pcl, accessory);
	}

	/**
	 * 显示打开文件对话框。
	 * @param parent parent组件
	 * @param extensions 文件扩展名，扩展名mime请在全局资源文件注册
	 * @return 选择的文件或null
	 */
	public static File showOpenDialog(Component parent, 
			boolean remainAcceptAllFileFilter, 
			String[] extensions)
	{
		return FileUtil.showOpenDialog(parent, remainAcceptAllFileFilter, 
				extensions);
	}
	
	/**
	 * 显示打开文件对话框。
	 * @param parent parent组件
	 * @param extensions 文件扩展名，扩展名mime请在全局资源文件注册
	 * @param targetFileName 目标文件名称
	 * @return 选择的文件或null
	 */
	public static File showOpenDialog(Component parent, 
			boolean remainAcceptAllFileFilter, 
			String[] extensions,
			String targetFileName)
	{
		return FileUtil.showOpenDialog(parent, remainAcceptAllFileFilter, 
				null, JFileChooser.FILES_ONLY, extensions, targetFileName, null, null);
	}

	/**
	 * 显示打开文件对话框。
	 * @param parent parent组件或null
	 * @param fileSelectionMode 文件选择模式
	 * @param extensions 文件扩展名或null，扩展名mime请在全局资源文件注册
	 * @param targetFileName 目标文件名称
	 * @param pcl 注册pcl到文件对话框
	 * @param accessory 文件预览组件
	 * @return 选择的文件或null
	 */
	public static File showOpenDialog(final Component parent, 
			final boolean remainAcceptAllFileFilter, 
			final int fileSelectionMode, 
			final String[] extensions,
			final String targetFileName,
			PropertyChangeListener pcl,
			JComponent accessory)
	{
		return FileUtil.showOpenDialog(parent, remainAcceptAllFileFilter, 
				null, fileSelectionMode, extensions, targetFileName, pcl, accessory);
	}
}
