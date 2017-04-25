/*  (swing1.1) */
package prj.ui.user;

import java.util.Enumeration;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @version 1.0 01/11/99
 */
public class TreeCheckNode extends DefaultMutableTreeNode
{
	public final static int SINGLE_SELECTION = 0;
	public final static int DIG_IN_SELECTION = 4;
	protected int selectionMode;
	protected boolean isSelected;

	public TreeCheckNode()
	{
		this(null);
	}

	public TreeCheckNode(Object userObject)
	{
		this(userObject, true, false);
	}

	public TreeCheckNode(Object userObject, boolean allowsChildren,
			boolean isSelected)
	{
		super(userObject, allowsChildren);
		this.isSelected = isSelected;
		setSelectionMode(DIG_IN_SELECTION);
	}

	public void setSelectionMode(int mode)
	{
		selectionMode = mode;
	}

	public int getSelectionMode()
	{
		return selectionMode;
	}

	public void setSelected(boolean isSelected)
	{
		this.isSelected = isSelected;

		if((selectionMode == DIG_IN_SELECTION) && (children != null))
		{
			Enumeration enum1 = children.elements();
			while(enum1.hasMoreElements())
			{
				TreeCheckNode node = (TreeCheckNode)enum1.nextElement();
				node.setSelected(isSelected);
			}
		}
	}

	public boolean isSelected()
	{
		return isSelected;
	}
	// If you want to change "isSelected" by CellEditor,
  /*
	public void setUserObject(Object obj) {
	if (obj instanceof Boolean) {
	setSelected(((Boolean)obj).booleanValue());
	} else {
	super.setUserObject(obj);
	}
	}
	 */
}


