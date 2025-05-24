/*
 * Cheetah - A Free Fast Downloader
 *
 * Copyright © 2015 Saeed Kayvanfar
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package gui;

import enums.DownloadCategory;
import gui.listener.CategoryPanelListener;
import model.dto.PreferencesDirectoryCategoryDTO;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @author <a href="kayvanfar.sj@gmail.com">Saeed Kayvanfar</a> 9/10/2015
 */
class CategoryPanel extends JPanel {

    private JTree categoryTree;

    private List<PreferencesDirectoryCategoryDTO> preferencesDirectoryCategoryDTOs;

    private CategoryPanelListener categoryPanelListener;

    private final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("messages/messages"); // NOI18N

    public CategoryPanel(List<PreferencesDirectoryCategoryDTO> preferencesDirectoryCategoryDTOs) {

        this.preferencesDirectoryCategoryDTOs = preferencesDirectoryCategoryDTOs;

        categoryTree = new JTree(initTree(preferencesDirectoryCategoryDTOs));

        categoryTree.setBackground(new Color(238, 238, 244));
        categoryTree.setOpaque(true);

        categoryTree.setRootVisible(false);
        categoryTree.setShowsRootHandles(true);

        CategortyTreeCellRenderer categortyTreeCellRenderer = new CategortyTreeCellRenderer();
        categoryTree.setCellRenderer(categortyTreeCellRenderer);

        categoryTree.setEditable(false);

        categoryTree.addTreeSelectionListener(e -> {
            //Returns the last path element of the selection.
            //This method is useful only when the selection model allows a single selection.
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) categoryTree.getLastSelectedPathComponent();

            if (node == null)
                //Nothing is selected.
                return;

            Object nodeInfo = node.getUserObject();

            if (nodeInfo instanceof PreferencesDirectoryCategoryDTO) {
                PreferencesDirectoryCategoryDTO preferencesDirectoryCategoryDTO = (PreferencesDirectoryCategoryDTO) nodeInfo;
                categoryPanelListener.categoryNodeSelected(Arrays.asList(preferencesDirectoryCategoryDTO.getFileExtensions()),
                        DownloadCategory.valueOfByDesc(node.getParent().toString()));
            } else {
                categoryPanelListener.categoryNodeSelected(null,
                        DownloadCategory.valueOfByDesc(node.toString()));
            }
        });

        expandAllNodes(categoryTree, 0, categoryTree.getRowCount());

        setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(categoryTree);
        scrollPane.setMinimumSize(new Dimension(150, 400));
        add(scrollPane, BorderLayout.CENTER);

        Border innerBorder = BorderFactory.createTitledBorder(bundle.getString("categoryPanel.label"));
        Border outerBorder = BorderFactory.createEmptyBorder(5,5,5,5);
        setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));

    }

    private DefaultMutableTreeNode initTree(List<PreferencesDirectoryCategoryDTO> preferencesDirectoryCategoryDTOs) {

        // Must get data from database
        DefaultMutableTreeNode top = new DefaultMutableTreeNode();

        DefaultMutableTreeNode allDownloadsBranch = new DefaultMutableTreeNode(bundle.getString("categoryPanel.allDownloadsBranch"));

        for (PreferencesDirectoryCategoryDTO preferencesDirectoryCategoryDTO : preferencesDirectoryCategoryDTOs) {
            DefaultMutableTreeNode leaf = new DefaultMutableTreeNode(preferencesDirectoryCategoryDTO);
            allDownloadsBranch.add(leaf);
        }

        DefaultMutableTreeNode unfinishedBranch = new DefaultMutableTreeNode(bundle.getString("categoryPanel.unfinishedBranch"));
        for (PreferencesDirectoryCategoryDTO preferencesDirectoryCategoryDTO : preferencesDirectoryCategoryDTOs) {
            DefaultMutableTreeNode leaf = new DefaultMutableTreeNode(preferencesDirectoryCategoryDTO);
            unfinishedBranch.add(leaf);
        }

        DefaultMutableTreeNode finishedBranch = new DefaultMutableTreeNode(bundle.getString("categoryPanel.finishedBranch"));
        for (PreferencesDirectoryCategoryDTO preferencesDirectoryCategoryDTO : preferencesDirectoryCategoryDTOs) {
            DefaultMutableTreeNode leaf = new DefaultMutableTreeNode(preferencesDirectoryCategoryDTO);
            finishedBranch.add(leaf);
        }

        DefaultMutableTreeNode queuesBranch = new DefaultMutableTreeNode(bundle.getString("categoryPanel.queuesBranch"));

        DefaultMutableTreeNode mainDownloadQueueLeaf = new DefaultMutableTreeNode(bundle.getString("categoryPanel.mainDownloadQueueLeaf"));
        DefaultMutableTreeNode synchronizedQueueLeaf = new DefaultMutableTreeNode(bundle.getString("categoryPanel.synchronizedQueueLeaf"));

        queuesBranch.add(mainDownloadQueueLeaf);
        queuesBranch.add(synchronizedQueueLeaf);

        top.add(allDownloadsBranch);
        top.add(unfinishedBranch);
        top.add(finishedBranch);

        return top;
    }

    public void setTreeModel(List<PreferencesDirectoryCategoryDTO> preferencesDirectoryCategoryDTOs) {
        this.preferencesDirectoryCategoryDTOs = preferencesDirectoryCategoryDTOs;
        categoryTree.setModel(new DefaultTreeModel(initTree(preferencesDirectoryCategoryDTOs), false));
        expandAllNodes(categoryTree, 0, categoryTree.getRowCount());
    }

    public void setCategoryPanelListener(CategoryPanelListener categoryPanelListener) {
        Objects.requireNonNull(categoryPanelListener, "categoryPanelListener");
        this.categoryPanelListener = categoryPanelListener;
    }

    private void expandAllNodes(JTree tree, int startingIndex, int rowCount) {
        for (int i = startingIndex; i < rowCount; ++i) {
            tree.expandRow(i);
        }

        if(tree.getRowCount() != rowCount) {
            expandAllNodes(tree, rowCount, tree.getRowCount());
        }
    }
}
