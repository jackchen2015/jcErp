/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package prj.ui.basic;

import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;

/**
 * 自定义PDF单元格
 * @author liming
 */
public class CnPdfCell extends Cell
{
	/**
	 * 构造CnPdfCell对象
	 * @param content 单元格文本内容
	 * @param rowspan 行间距
	 * @param colspan 列间距
	 * @throws Exception 异常对象
	 */
	public CnPdfCell(String content, int rowspan, int colspan) throws Exception
	{
		super(new Chunk(content != null ? content : "", getCnFont(false)));
		setRowspan(rowspan);
		setColspan(colspan);
		setHeader(false);
	}

	/**
	 * 构造CnPdfCell对象
	 * @param content 单元格文本内容
	 * @throws Exception  异常对象
	 */
	public CnPdfCell(String content) throws Exception
	{
		super(new Chunk(content != null ? content : "", getCnFont(false)));
		setHeader(false);
	}

	/**
	 * 构造CnPdfCell对象
	 * @param content 单元格文本内容
	 * @param isBold 是否粗体显示
	 * @throws Exception 异常对象
	 */
	public CnPdfCell(String content, boolean isBold) throws Exception
	{
		super(new Chunk(content != null ? content : "", getCnFont(isBold)));
		setHeader(false);
	}

	/**
	 * 获取字体对象,用于中文字体处理
	 * @param isBold 是否粗体显示
	 * @return 字体对象
	 * @throws Exception 异常对象
	 */
	private static Font getCnFont(boolean isBold) throws Exception
	{
		//创建一个用于中午字体处理的字体对象
		BaseFont baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
		//设置字体样式
		int fontStyle = Font.NORMAL;
		if(isBold)
		{
			fontStyle = Font.BOLD;
		}
		Font fontCn = new Font(baseFont, 12, fontStyle);
		return fontCn;
	}
}
