package gui;

import enums.DownloadStatus;
import gui.listener.DownloadTablePanelListener;
import gui.model.DownloadAction;
import model.Download;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class DownloadTablePanel extends JPanel implements ActionListener {

    private final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("messages/messages"); // NOI18N

    private final JTable downloadTable;
    private final DownloadTableModel downloadsTableModel;

    private final JPopupMenu contextMenu;
    private JMenuItem openItem;
    private JMenuItem openFolderItem;
    private JMenuItem resumeItem;
    private JMenuItem pauseItem;
    private JMenuItem clearItem;
    private JMenuItem reJoinItem;
    private JMenuItem reDownloadItem;
    private JMenuItem moveToQueueItem;
    private JMenuItem removeFromQueueItem;
    private JMenuItem propertiesItem;

    private DownloadTablePanelListener downloadTablePanelListener;

    public DownloadTablePanel(DownloadTableModel model) {
        this.downloadsTableModel = model;
        this.downloadTable = new JTable(downloadsTableModel);
        this.contextMenu = new JPopupMenu();


                setLayout(new BorderLayout());
        initTable();
        initContextMenu();
        add(new JScrollPane(downloadTable), BorderLayout.CENTER);
    }

    private void initTable() {
        downloadTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        downloadTable.setRowHeight(24);
        downloadTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        downloadTable.getTableHeader().setReorderingAllowed(false);

        downloadTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                if (downloadTablePanelListener != null) {
                    downloadTablePanelListener.downloadSelected(getSelectedDownload());
                }
            }
        });

        downloadTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                int row = downloadTable.rowAtPoint(e.getPoint());
                if (row >= 0 && row < downloadTable.getRowCount()) {
                    downloadTable.getSelectionModel().setSelectionInterval(row, row);
                    if (e.getButton() == MouseEvent.BUTTON3) { // right click
                        contextMenu.show(downloadTable, e.getX(), e.getY());
                    } else if (e.getClickCount() == 2) { // double click
                        Download selectedDownload = getSelectedDownload();
                        if (selectedDownload != null) {
                            downloadTablePanelListener.downloadDoubleClicked(selectedDownload);
                        }
                    }
                }
            }
        });

        // Allow only one row at a time to be selected.
        downloadTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Set up ProgressBar as renderer for progress column.
        ProgressRenderer renderer = new ProgressRenderer(0, 100);
        renderer.setStringPainted(true); // show progress text
        downloadTable.setDefaultRenderer(JProgressBar.class, renderer);

        // Set table's row height large enough to fit JProgressBar.
        downloadTable.setRowHeight((int) renderer.getPreferredSize().getHeight());
        setColumnWidths();
    }

    private void initContextMenu() {
        openItem = new JMenuItem(bundle.getString("downloadPanel.openItem.name"));
        openItem.addActionListener(this);
        openFolderItem = new JMenuItem(bundle.getString("downloadPanel.openFolderItem.name"));
        openFolderItem.addActionListener(this);

        resumeItem = new JMenuItem(bundle.getString("downloadPanel.resumeItem.name"));
        resumeItem.addActionListener(this);
        pauseItem = new JMenuItem(bundle.getString("downloadPanel.pauseItem.name"));
        pauseItem.addActionListener(this);
        clearItem = new JMenuItem(bundle.getString("downloadPanel.clearItem.name"));
        clearItem.addActionListener(this);

        reJoinItem = new JMenuItem(bundle.getString("downloadPanel.reJoinItem.name"));
        reJoinItem.addActionListener(this);
        reDownloadItem = new JMenuItem(bundle.getString("downloadPanel.reDownloadItem.name"));
        reDownloadItem.addActionListener(this);

        moveToQueueItem = new JMenuItem(bundle.getString("downloadPanel.moveToQueueItem.name"));
        moveToQueueItem.addActionListener(this);
        removeFromQueueItem = new JMenuItem(bundle.getString("downloadPanel.removeFromQueueItem.name"));
        removeFromQueueItem.addActionListener(this);

        propertiesItem = new JMenuItem(bundle.getString("downloadPanel.propertiesItem.name"));
        propertiesItem.addActionListener(this);

        contextMenu.add(openItem);
        contextMenu.add(openFolderItem);
        contextMenu.add(new JPopupMenu.Separator());
        contextMenu.add(resumeItem);
        contextMenu.add(pauseItem);
        contextMenu.add(clearItem);
        contextMenu.add(new JPopupMenu.Separator());
        contextMenu.add(reJoinItem);
        contextMenu.add(reDownloadItem);
        contextMenu.add(new JPopupMenu.Separator());
        contextMenu.add(moveToQueueItem);
        contextMenu.add(removeFromQueueItem);
        contextMenu.add(new JPopupMenu.Separator());
        contextMenu.add(propertiesItem);

        setStateOfMenuItems();
    }


    private void setStateOfMenuItems() {
        resumeItem.setEnabled(false);
        pauseItem.setEnabled(false);
        clearItem.setEnabled(false);
        moveToQueueItem.setEnabled(false);
        removeFromQueueItem.setEnabled(false);
    }

    private void setColumnWidths(){
        downloadTable.getColumnModel().getColumn(0).setPreferredWidth(500);
        downloadTable.getColumnModel().getColumn(0).setMaxWidth(900);
        downloadTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        downloadTable.getColumnModel().getColumn(1).setMaxWidth(250);
        downloadTable.getColumnModel().getColumn(2).setPreferredWidth(250);
        downloadTable.getColumnModel().getColumn(2).setMaxWidth(500);
        downloadTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        downloadTable.getColumnModel().getColumn(3).setMaxWidth(200);
        downloadTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        downloadTable.getColumnModel().getColumn(4).setMaxWidth(150);

        downloadTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
    }

    public void setStateOfButtonsControl(boolean openState, boolean openFolderState, boolean pauseState, boolean resumeState, boolean clearState, boolean reJoinState, boolean reDownloadState, boolean propertiesState) {
        openItem.setEnabled(openState);
        openFolderItem.setEnabled(openFolderState);
        pauseItem.setEnabled(pauseState);
        resumeItem.setEnabled(resumeState);
        clearItem.setEnabled(clearState);
        reJoinItem.setEnabled(reJoinState);
        reDownloadItem.setEnabled(reDownloadState);
        propertiesItem.setEnabled(propertiesState);
    }

    public int getSelectedRow() {
        return downloadTable.getSelectedRow();
    }

    public Download getSelectedDownload() {
        int row = getSelectedRow();
        return row != -1 ? downloadsTableModel.getDownloadAt(row) : null;
    }

    public void setDownloadTablePanelListener(DownloadTablePanelListener downloadTablePanelListener) {
        this.downloadTablePanelListener = downloadTablePanelListener;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JMenuItem clicked= (JMenuItem) e.getSource();
        DownloadAction downloadAction = DownloadAction.PROPERTIES;
        if (clicked == openItem) {
            downloadAction = DownloadAction.OPEN_FILE;
        } else if (clicked == openFolderItem) {
            downloadAction = DownloadAction.OPEN_FOLDER;
        } else if (clicked == resumeItem) {
            downloadAction = DownloadAction.RESUME;
        }  else if (clicked == pauseItem) {
            downloadAction = DownloadAction.PAUSE;
        } else if (clicked == clearItem) {
            downloadAction = DownloadAction.CLEAR;
        } else if (clicked == reJoinItem) {
            downloadAction = DownloadAction.REJOIN_PARTS;
        } else if (clicked == reDownloadItem) {
            downloadAction = DownloadAction.REDOWNLOAD;
        } else if (clicked == moveToQueueItem) {
            downloadAction = DownloadAction.MOVE_TO_QUEUE;
        } else if (clicked == removeFromQueueItem) {
            downloadAction = DownloadAction.REMOVE_FROM_QUEUE;
        } else if (clicked == propertiesItem) {
            downloadAction = DownloadAction.PROPERTIES;
        }

        this.downloadTablePanelListener.onMenuAction(getSelectedDownload(), downloadAction);
    }

    public void clearDownload(Download selectedDownload) {
        if (selectedDownload != null) {
            downloadsTableModel.clearDownload(selectedDownload);
            setStateOfMenuItems();
        }
    }

    public List<Download> getDownloadsByStatus(DownloadStatus downloadStatus) {
        return downloadsTableModel.getDownloadsByStatus(downloadStatus);
    }

    public void clearDownloads(List<Download> selectedDownloads) {
        if (selectedDownloads != null && !selectedDownloads.isEmpty()) {
            downloadsTableModel.clearDownloads(selectedDownloads);
            setStateOfMenuItems();
        }
    }

    public void fireTableDataChanged() {
        downloadsTableModel.fireTableDataChanged();
        setStateOfMenuItems();
    }

    public void setDownloads(List<Download> selectedDownloads) {
        if (selectedDownloads != null && !selectedDownloads.isEmpty()) {
            downloadsTableModel.setDownloads(selectedDownloads);
            setStateOfMenuItems();
        }
    }
}
