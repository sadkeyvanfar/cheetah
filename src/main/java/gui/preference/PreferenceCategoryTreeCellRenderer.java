package gui.preference;

import utils.Utils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;
import java.util.ResourceBundle;

/**
 * Created by Saeed on 10/30/2015.
 */
public class PreferenceCategoryTreeCellRenderer implements TreeCellRenderer {

    private JLabel leafRenderer;

    private Color textForeground;
    private Color textBackground;

    private ResourceBundle defaultPreferencesBundle = java.util.ResourceBundle.getBundle("defaultPreferences"); // NOI18N

    public PreferenceCategoryTreeCellRenderer() {
        leafRenderer = new JLabel();

        textForeground = UIManager.getColor("Tree.textForeground");
        textBackground = UIManager.getColor("Tree.textBackground");
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object nodeInfo = node.getUserObject();
        leafRenderer.setForeground(Color.blue); // selectionForeground

        leafRenderer.setIcon(Utils.createIcon("images/primo48/others/unknown.png"));

        if (selected) {
            leafRenderer.setForeground(Color.blue); // selectionForeground
        }
        else {
            leafRenderer.setForeground(textForeground);
            leafRenderer.setBackground(textBackground);
        }

        leafRenderer.setText(nodeInfo.toString());

        return leafRenderer;
    }
}
