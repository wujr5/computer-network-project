package MinetUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Enumeration;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import Minet.Minet;

/**
 * 在线用户列表的构造类
 * @author jiarong
 *
 */

class CheckList {
  public static CheckableItem[] createData(String[] strs) { 
    int n = strs.length; 
    CheckableItem[] items = new CheckableItem[n]; 
    for (int i = 0; i < n; i++) { 
      items[i] = new CheckableItem(strs[i]);
    } 
    return items; 
  }
}

class CheckableItem { 
  private String str; 
  private boolean isSelected; 
  private Icon icon; 

  public CheckableItem(String str) { 
    this.str = str; 
    isSelected = false; 
  } 
  public void setSelected(boolean b) { 
    isSelected = b; 
  } 
  public boolean isSelected() { 
    return isSelected; 
  } 
  public String toString() { 
    return str; 
  } 
  public void setIcon(Icon icon) { 
    this.icon = icon; 
  } 
  public Icon getIcon() { 
    return icon; 
  } 
}

class CheckListRenderer extends CheckRenderer implements ListCellRenderer { 
  Icon commonIcon; 

  public CheckListRenderer() { 
    check.setBackground(UIManager.getColor("List.textBackground")); 
    label.setForeground(UIManager.getColor("List.textForeground")); 
    commonIcon = UIManager.getIcon("Tree.leafIcon"); 
  } 

  public Component getListCellRendererComponent(JList list, Object value, 
      int index, boolean isSelected, boolean hasFocus) { 
    setEnabled(list.isEnabled()); 
    check.setSelected(((CheckableItem) value).isSelected()); 
    label.setFont(list.getFont()); 
    label.setText(value.toString()); 
    label.setSelected(isSelected); 
    label.setFocus(hasFocus); 
    Icon icon = ((CheckableItem) value).getIcon(); 
    if (icon == null) { 
      icon = commonIcon; 
    } 
    label.setIcon(icon); 
    return this; 
  } 
} 

class CheckRenderer extends JPanel implements TreeCellRenderer { 
  
  private static final long serialVersionUID = 1L;
  
  protected JCheckBox check;
  protected TreeLabel label;

  public CheckRenderer() {
    setLayout(null); 
    add(check = new JCheckBox()); 
    add(label = new TreeLabel()); 
    check.setBackground(UIManager.getColor("Tree.textBackground")); 
    label.setForeground(UIManager.getColor("Tree.textForeground")); 
  }

  public Component getTreeCellRendererComponent(
      JTree tree, Object value, boolean isSelected, 
      boolean expanded, boolean leaf, int row, boolean hasFocus) {
    
    String stringValue = tree.convertValueToText(value, isSelected, 
        expanded, leaf, row, hasFocus);
    setEnabled(tree.isEnabled());
    check.setSelected(((CheckNode) value).isSelected());
    check.setText("wujiarong");
    label.setFont(tree.getFont()); 
    label.setText(stringValue); 
    label.setSelected(isSelected); 
    label.setFocus(hasFocus); 
    if (leaf) { 
      label.setIcon(UIManager.getIcon("Tree.leafIcon")); 
    } else if (expanded) { 
      label.setIcon(UIManager.getIcon("Tree.openIcon")); 
    } else { 
      label.setIcon(UIManager.getIcon("Tree.closedIcon")); 
    } 
    return this; 
  } 

  public Dimension getPreferredSize() { 
    Dimension d_check = check.getPreferredSize(); 
    Dimension d_label = label.getPreferredSize(); 
    return new Dimension(d_check.width + d_label.width, 
        (d_check.height < d_label.height ? d_label.height 
            : d_check.height)); 
  }

  public void doLayout() { 
    Dimension d_check = check.getPreferredSize(); 
    Dimension d_label = label.getPreferredSize(); 
    int y_check = 0; 
    int y_label = 0; 
    if (d_check.height < d_label.height) { 
      y_check = (d_label.height - d_check.height) / 2; 
    } else { 
      y_label = (d_check.height - d_label.height) / 2; 
    } 
    check.setLocation(0, y_check); 
    check.setBounds(0, y_check, d_check.width, d_check.height); 
    label.setLocation(d_check.width, y_label); 
    label.setBounds(d_check.width, y_label, d_label.width, d_label.height); 
  } 

  public void setBackground(Color color) { 
    if (color instanceof ColorUIResource) 
      color = null; 
    super.setBackground(color); 
  } 

  public class TreeLabel extends JLabel { 
    boolean isSelected; 
    boolean hasFocus; 
    public TreeLabel() {} 

    public void setBackground(Color color) { 
      if (color instanceof ColorUIResource) 
        color = null; 
      super.setBackground(color); 
    } 

    public void paint(Graphics g) { 
      String str; 
      if ((str = getText()) != null) { 
        if (0 < str.length()) { 
          if (isSelected) { 
            g.setColor(UIManager .getColor("Tree.selectionBackground")); 
          } else { 
            g.setColor(UIManager.getColor("Tree.textBackground")); 
          } 
          
          Dimension d = getPreferredSize(); 
          int imageOffset = 0; 
          Icon currentI = getIcon(); 
          if (currentI != null) { 
            imageOffset = currentI.getIconWidth() 
                + Math.max(0, getIconTextGap() - 1); 
          } 
          
          g.fillRect(imageOffset, 0, d.width - 1 - imageOffset, d.height); 
          
          if (hasFocus) { 
            g.setColor(UIManager.getColor("Tree.selectionBorderColor")); 
            g.drawRect(imageOffset, 0, d.width - 1 - imageOffset, d.height - 1); 
          }
        } 
      } 
      super.paint(g); 
    } 

    public Dimension getPreferredSize() { 
      Dimension retDimension = super.getPreferredSize(); 
      if (retDimension != null) { 
        retDimension = new Dimension(retDimension.width + 3, 
            retDimension.height); 
      } 
      return retDimension; 
    } 

    public void setSelected(boolean isSelected) { 
      this.isSelected = isSelected; 
    } 

    public void setFocus(boolean hasFocus) { 
      this.hasFocus = hasFocus; 
    } 
  } 
} 

class CheckNode extends DefaultMutableTreeNode { 
  private static final long serialVersionUID = 1L;
  
  public final static int SINGLE_SELECTION = 0; 
  public final static int DIG_IN_SELECTION = 4; 
  protected int selectionMode; 
  protected boolean isSelected; 

  public CheckNode() { 
    this(null); 
  } 

  public CheckNode(Object userObject) { 
    this(userObject, true, false); 
  } 

  public CheckNode(Object userObject, boolean allowsChildren, 
      boolean isSelected) { 
    super(userObject, allowsChildren); 
    this.isSelected = isSelected; 
    setSelectionMode(DIG_IN_SELECTION); 
  } 

  public void setSelectionMode(int mode) { 
    selectionMode = mode; 
  } 

  public int getSelectionMode() { 
    return selectionMode; 
  } 

  public void setSelected(boolean isSelected) { 
    this.isSelected = isSelected; 

    if ((selectionMode == DIG_IN_SELECTION) && (children != null)) { 
      Enumeration e = children.elements(); 
      while (e.hasMoreElements()) { 
        CheckNode node = (CheckNode) e.nextElement(); 
        node.setSelected(isSelected); 
      } 
    } 
  } 

  public boolean isSelected() { 
    return isSelected; 
  } 
}
