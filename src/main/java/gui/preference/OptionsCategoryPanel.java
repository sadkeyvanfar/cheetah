package gui.preference;

import gui.listener.OptionsCategoryPanelListener;
import model.dto.OptionsCategoryDto;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.Objects;

/**
 * Created by Saeed on 9/22/2015.
 */
class OptionsCategoryPanel extends JPanel {

    // Logger
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private JTree categoryTree;

    private OptionsCategoryDto optionsCategoryDto;

    private OptionsCategoryPanelListener optionsCategoryPanelListener;

    public OptionsCategoryPanel(OptionsCategoryDto optionsCategoryDto) {
        this.optionsCategoryDto = optionsCategoryDto;
        categoryTree = new JTree(initTree());
        UIManager.put("Tree.rendererFillBackground", false);

        categoryTree.setBackground(optionsCategoryDto.getColor());
        categoryTree.setOpaque(true);

        PreferenceCategoryTreeCellRenderer preferenceCategoryTreeCellRenderer = new PreferenceCategoryTreeCellRenderer();
        categoryTree.setCellRenderer(preferenceCategoryTreeCellRenderer);

        categoryTree.setRootVisible(false);
        categoryTree.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        categoryTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                //Returns the last path element of the selection.
                //This method is useful only when the selection model allows a single selection.
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) categoryTree.getLastSelectedPathComponent();

                if (node == null)
                    //Nothing is selected.
                    return;

                String nodeName = (String) node.getUserObject();

                optionsCategoryPanelListener.nodeSelectedEventOccured(nodeName);////???????????????
            }
        });

        categoryTree.setEditable(false);

        setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(categoryTree);
        scrollPane.setMinimumSize(new Dimension(150, 400));
        add(scrollPane, BorderLayout.CENTER);

        Border innerBorder = BorderFactory.createTitledBorder(optionsCategoryDto.getCategoryName());
        Border outerBorder = BorderFactory.createEmptyBorder(5,5,5,5);
        setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));

    }

    public void setOptionsCategoryPanelListener(OptionsCategoryPanelListener optionsCategoryPanelListener) {
        Objects.requireNonNull(optionsCategoryPanelListener, "optionsCategoryPanelListener");
        this.optionsCategoryPanelListener = optionsCategoryPanelListener;
    }

    private DefaultMutableTreeNode initTree() {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(optionsCategoryDto.getCategoryName());

        optionsCategoryDto.getNodeNames().stream().forEach(s -> top.add(new DefaultMutableTreeNode(s)));

        return top;
    }
}
