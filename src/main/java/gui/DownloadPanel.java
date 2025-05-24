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

import comparator.FileNameComparator;
import enums.DownloadCategory;
import enums.DownloadStatus;
import gui.controller.DownloadController;
import gui.download.DownloadAskDialog;
import gui.download.DownloadDialog;
import gui.listener.*;
import model.Download;
import model.DownloadRange;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import utils.FileUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DownloadPanel extends JPanel implements DownloadInfoListener, DownloadStatusListener, ActionListener {

    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final Logger messageLogger = Logger.getLogger("message");

    private final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("messages/messages"); // NOI18N

    private DownloadController downloadController;

    private JTable downloadTable;

    private JPopupMenu popup;
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

    // download table's data model.
    private DownloadsTableModel downloadsTableModel;

    // Currently selected download.
    private Download selectedDownload;

    private List<DownloadDialog> downloadDialogs;

    private DownloadAskDialog downloadAskDialog;

    private List<String> fileExtensions;
    private DownloadCategory downloadCategory = DownloadCategory.ALL;

    // Flag for whether table selection is being cleared.
    private boolean clearing;

    private DownloadPanelListener downloadPanelListener;

    private JFrame parent;

    public List<Download> getDownloadList() {
        return downloadController.getAllDownloads();
    }

    public void addDownloadDialog(DownloadDialog downloadDialog) {
        if (downloadDialog == null)
            throw new NullPointerException();
        if (!downloadDialogs.contains(downloadDialog)) {
            downloadDialogs.add(downloadDialog);
            downloadDialog.setDownloadInfoListener(this);
        }
    }

    private void deleteDownloadDialog(DownloadDialog downloadDialog) {
        downloadDialogs.remove(downloadDialog);
        downloadDialog.removeDownloadInfoListener(this);
    }

    private DownloadDialog getDownloadDialogByDownload(Download download) {
        for (DownloadDialog downloadDialog : downloadDialogs)
            if (downloadDialog.getDownload().equals(download))
                return downloadDialog;
        return null;
    }

    private void deleteDownloadDialogByDownload(Download download) {
        DownloadDialog tempDownloadDialog = null;
        for (DownloadDialog downloadDialog : downloadDialogs)
            if (downloadDialog.getDownload().equals(download))
                tempDownloadDialog = downloadDialog;
        deleteDownloadDialog(tempDownloadDialog);
    }

    public DownloadPanel(JFrame parent , DownloadController downloadController) {
        this.parent = parent;
        this.downloadController = downloadController;

        setLayout(new BorderLayout());

        downloadDialogs = new ArrayList<>();

        // Set up Downloads table.
        downloadsTableModel = new DownloadsTableModel();
        downloadTable = new JTable(downloadsTableModel);
        popup = initPopupMenu();

        downloadTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { // todo check this
                tableSelectionChanged();
                if (downloadPanelListener != null) {
                    downloadPanelListener.downloadSelected(selectedDownload);
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

        downloadTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                int row = downloadTable.rowAtPoint(e.getPoint());
                if (row >= 0 && row < downloadTable.getRowCount()) {
                    downloadTable.getSelectionModel().setSelectionInterval(row, row);
                }
                DownloadDialog downloadDialog = getDownloadDialogByDownload(selectedDownload);
                if (e.getButton() == MouseEvent.BUTTON3) { // right click
                    popup.show(downloadTable, e.getX(), e.getY());
                } else if (e.getClickCount() == 2) {  // double click
                    if (downloadDialog != null && !downloadDialog.isVisible()) {
                        downloadDialog.setVisible(true);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(downloadTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        List<Download> downloadList = downloadController.getAllDownloads();

        DownloadDialog downloadDialog;
        for (Download download : downloadList) {
            calculateDownloaded(download);
            download.setDownloadInfoListener(this);
            download.addDownloadStatusListener(this);
            downloadDialog = new DownloadDialog(parent, download);
            downloadDialog.setDownloadInfoListener(this);
            downloadDialogs.add(downloadDialog);
            downloadsTableModel.addDownload(download);
            downloadDialog.setDownloadRanges(download.getDownloadRangeList());
        }
        setColumnWidths();
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

    private void calculateDownloaded(Download download) {
        int downloaded = 0;
        for (DownloadRange downloadRange : download.getDownloadRangeList()) {
            long rangeDownloaded = downloadRange.getDownloadRangeFile().length();
            downloaded += rangeDownloaded;
        }
        download.setDownloaded(downloaded);
    }

    private JPopupMenu initPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();

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

        popupMenu.add(openItem);
        popupMenu.add(openFolderItem);
        popupMenu.add(new JPopupMenu.Separator());
        popupMenu.add(resumeItem);
        popupMenu.add(pauseItem);
        popupMenu.add(clearItem);
        popupMenu.add(new JPopupMenu.Separator());
        popupMenu.add(reJoinItem);
        popupMenu.add(reDownloadItem);
        popupMenu.add(new JPopupMenu.Separator());
        popupMenu.add(moveToQueueItem);
        popupMenu.add(removeFromQueueItem);
        popupMenu.add(new JPopupMenu.Separator());
        popupMenu.add(propertiesItem);

        return popupMenu;
    }

    public void addDownload(final Download download) {
        download.setDownloadInfoListener(this);

        selectedDownload = download;

        downloadAskDialog = new DownloadAskDialog(parent);

        File downloadPath = new File(download.getDownloadPath() + File.separator + download.getDownloadName());
        File downloadRangePath = new File(download.getDownloadRangePath() + File.separator + download.getDownloadName());

        List<File> outPutfiles = new ArrayList<>();
        outPutfiles.add(downloadPath);
        outPutfiles.add(downloadRangePath);

        File path = FileUtil.outputFile(outPutfiles, new FileNameComparator());

        String downloadPathName = download.getDownloadPath() + File.separator + FileUtil.getFileName(path);

        downloadAskDialog.setInfo(download.getUrl().toString(), downloadPathName);
        downloadAskDialog.setDownloadAskDialogListener(new DownloadAskDialogListener() {
            @Override
            public void startDownloadEventOccured(String path) {
                File pathFile = new File(path);
                download.setDownloadPath(pathFile.getParentFile());
                download.setDownloadName(pathFile.getName());

                DownloadDialog downloadDialog = createDownloadDialog(download);

                downloadDialog.setVisible(true);

                download.createDownloadRanges();
                download.startTransferRateMonitor();
            }

            @Override
            public void cancelDownloadEventOccured() {
                selectedDownload = null;
            }

            @Override
            public void laterDownloadEventOccured() {
                download.setStatus(DownloadStatus.CANCELLED);
                downloadNeedSaved(download);
                createDownloadDialog(download);
            }
        });
        downloadAskDialog.setVisible(true);
        selectedDownload.resume();
    }

    public int getNextDownloadID() {
        return downloadController.getAllDownloads().size() + 1;
    }

    public void refresh() {
        downloadsTableModel.fireTableDataChanged();
    }

    public void actionOpenFile() {
        downloadController.openFile(selectedDownload.getDownloadPath() + File.separator + selectedDownload.getDownloadName());
    }

    public void actionOpenFolder() {
        downloadController.openFile(selectedDownload.getDownloadPath().getPath());
    }

    // Pause the selected download.
    public void actionPause() {
        selectedDownload.pause();
    }

    // Resume the selected download.
    public void actionResume() {
        if (selectedDownload.getStatus() == DownloadStatus.CANCELLED || (selectedDownload.getStatus() == DownloadStatus.ERROR && selectedDownload.getDownloadRangeList().isEmpty())) {
            selectedDownload.removeDownloadInfo(this);
            deleteDownloadDialogByDownload(selectedDownload);

            addDownload(selectedDownload);
        } else {
            selectedDownload.resume();
        }
    }

    // Cancel the selected download.
    public void actionPauseAll() {
        List<Download> downloadList = downloadsTableModel.getDownloadList();
        for (Download download : downloadList) {
            if (download.getStatus() == DownloadStatus.DOWNLOADING)
                download.pause();
        }
    }

    // Clear the selected download.
    public void actionClear() {
        int action = JOptionPane.showConfirmDialog(parent, "Do you realy want to delete selected file?", "Confirm delete", JOptionPane.OK_CANCEL_OPTION);
        if (action == JOptionPane.OK_OPTION) {
            if (selectedDownload == null) return;

            clearing = true;
            downloadsTableModel.clearDownload(selectedDownload);
            if (downloadController.getAllDownloads().contains(selectedDownload))
                downloadController.remove(selectedDownload);
            clearing = false;

            DownloadDialog downloadDialog = getDownloadDialogByDownload(selectedDownload);
            downloadDialogs.remove(downloadDialog);
            downloadDialog.dispose();
            downloadDialog.removeDownloadInfoListener(this);

            downloadController.deleteDownload(selectedDownload);

            try {
                FileUtils.forceDelete(new File(selectedDownload.getDownloadRangePath() + File.separator + selectedDownload.getDownloadName())); // todo must again
            } catch (IOException e) {
          //      e.printStackTrace();
            }

            selectedDownload = null;
            tableSelectionChanged();
        }
    }

    // Clear all completed downloads.
    public void actionClearAllCompleted() {
        int action = JOptionPane.showConfirmDialog(parent, "Do you realy want to delete all completed files?", "Confirm delete all", JOptionPane.OK_CANCEL_OPTION);
        if (action == JOptionPane.OK_OPTION) {
            List<Download> selectedDownloads = downloadsTableModel.getDownloadsByStatus(DownloadStatus.COMPLETE);

            clearing = true;
            downloadsTableModel.clearDownloads(selectedDownloads);
            clearing = false;

            try {
                for (Download download : selectedDownloads) {
                    if (selectedDownload == download)
                        selectedDownload = null;
                    DownloadDialog downloadDialog = getDownloadDialogByDownload(download);
                    downloadDialogs.remove(downloadDialog);
                    downloadDialog.removeDownloadInfoListener(this);
                    downloadDialog.dispose();
                    downloadController.deleteDownload(download);
                    FileUtils.forceDelete(new File(download.getDownloadRangePath() + File.separator + download.getDownloadName())); // todo must again
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            tableSelectionChanged();
        }
    }

    public void actionReJoinFileParts() {
        List<DownloadRange> downloadRangeList = selectedDownload.getDownloadRangeList();
        List<File> files = new ArrayList<>();
        for (DownloadRange downloadRange : downloadRangeList) {
            files.add(downloadRange.getDownloadRangeFile());
        }

        FileUtil.joinDownloadedParts(files, selectedDownload.getDownloadPath(), selectedDownload.getDownloadName());
        JOptionPane.showMessageDialog(parent, "Join parts completed.", "Rejoin", JOptionPane.INFORMATION_MESSAGE);
    }

    public void actionReDownload() {
        int action = JOptionPane.showConfirmDialog(parent, "Do you realy want to redownload the file?", "Confirm Redownload", JOptionPane.OK_CANCEL_OPTION);
        if (action == JOptionPane.OK_OPTION) {
            Download newDownload = selectedDownload;
            try {
                FileUtils.forceDelete(new File(selectedDownload.getDownloadRangePath() + File.separator + selectedDownload.getDownloadName())); // todo must again
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (newDownload.getStatus() == DownloadStatus.COMPLETE) {
                newDownload.resetData();
                newDownload.resume();
                tableSelectionChanged();
            }
        }
    }

    public void actionProperties() {
        DownloadDialog downloadDialog = getDownloadDialogByDownload(selectedDownload);
        if (!downloadDialog.isVisible()) {
            downloadDialog.setVisible(true);
        }
    }

    // Called when table row selection changes.
    private void tableSelectionChanged() {
        /* Unregister from receiving notifications
           from the last selected download. */
        if (selectedDownload != null)
            selectedDownload.deleteDownloadStatusListener(DownloadPanel.this);

        /* If not in the middle of clearing a download,
           set the selected download and register to
           receive notifications from it. */
        if (!clearing && downloadTable.getSelectedRow() > -1) {
            selectedDownload = downloadsTableModel.getDownload(downloadTable.getSelectedRow());
            selectedDownload.addDownloadStatusListener(DownloadPanel.this);
            downloadPanelListener.stateChangedEventOccured(selectedDownload.getStatus());
        } else {
            downloadPanelListener.stateChangedEventOccured(null);
        }
    }

    public void setDownloadPanelListener(DownloadPanelListener downloadPanelListener) {
        Objects.requireNonNull(downloadPanelListener, "downloadPanelListener");
        this.downloadPanelListener = downloadPanelListener;
    }

    @Override
    public void downloadStatusChanged(Download download) {
        // Update buttons if the selected download has changed.
        if (selectedDownload != null && selectedDownload.equals(download))
            downloadPanelListener.stateChangedEventOccured(selectedDownload.getStatus());
    }

    @Override
    public void newDownloadRangeEventOccured(DownloadRange downloadRange) {
        getDownloadDialogByDownload(selectedDownload).addDownloadRange(downloadRange);
    }

    @Override
    public void downloadNeedSaved(Download download) {
        downloadController.saveDownload(download);
        downloadsTableModel.fireTableDataChanged();
    }

    @Override
    public void newDownloadInfoGot(final Download download) {
        SwingUtilities.invokeLater(() -> {
            if (download.getStatus() == DownloadStatus.DOWNLOADING) {// todo add name to downloadAskDialog dialog
                messageLogger.info("New Download is ready to start: " + download.getDownloadName());
                String downloadPathName = download.getDownloadPath() + File.separator + download.getDownloadName();
                downloadAskDialog.setInfo(download.getUrl().toString(), downloadPathName, download.getFormattedSize(), download.isResumeCapability());
            } else { // When error arise
                logger.info("newDownloadInfoGot with error");
                messageLogger.info("Connection closed." + download.getDownloadName());
                downloadAskDialog.dispose();

                createDownloadDialog(download);

                JOptionPane.showMessageDialog(parent, "Connection closed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private DownloadDialog createDownloadDialog(Download download) {
        if (!downloadController.getAllDownloads().contains(download)) {
            downloadController.addDownload(download);
            setDownloadsByDownloadPath(fileExtensions, downloadCategory);
        }
        //**************************************************
        DownloadDialog downloadDialog = new DownloadDialog(parent, download);
        downloadDialog.setDownloadInfoListener(DownloadPanel.this);
        if (!downloadDialogs.contains(downloadDialog))
            downloadDialogs.add(downloadDialog);
        return downloadDialog;
    }

    public void setDownloadsByDownloadPath(List<String> fileExtensions) {
        List<Download> selectedDownloads = new ArrayList<>();
        for (Download download : downloadController.getAllDownloads())
            for (String downloadPath : fileExtensions)
                if (FilenameUtils.getExtension(download.getDownloadName()).equals(downloadPath))
                    selectedDownloads.add(download);

        downloadsTableModel.setDownloads(selectedDownloads);
    }

    public void setDownloadsByDownloadPath(List<String> fileExtensions, DownloadCategory downloadCategory) { //todo use Sterategy pattern, have bad code
        this.fileExtensions = fileExtensions;
        this.downloadCategory = downloadCategory;
        List<Download> selectedDownloads = new ArrayList<>();
        for (Download download : downloadController.getAllDownloads()) {
            if (fileExtensions != null) {
                for (String downloadPath : fileExtensions) {
                    switch (downloadCategory) {
                        case FINISHED:
                            if (FilenameUtils.getExtension(download.getDownloadName()).equals(downloadPath) && download.getStatus().equals(DownloadStatus.COMPLETE))
                                selectedDownloads.add(download);
                            break;
                        case UNFINISHED:
                            if (FilenameUtils.getExtension(download.getDownloadName()).equals(downloadPath) && !download.getStatus().equals(DownloadStatus.COMPLETE))
                                selectedDownloads.add(download);
                            break;
                        default:
                            if (FilenameUtils.getExtension(download.getDownloadName()).equals(downloadPath))
                                selectedDownloads.add(download);
                    }
                }
            } else {
                switch (downloadCategory) {
                    case FINISHED:
                        if (download.getStatus().equals(DownloadStatus.COMPLETE))
                            selectedDownloads.add(download);
                        break;
                    case UNFINISHED:
                        if (!download.getStatus().equals(DownloadStatus.COMPLETE))
                            selectedDownloads.add(download);
                        break;
                    default:
                        selectedDownloads.add(download);
                }
            }
        }

        downloadsTableModel.setDownloads(selectedDownloads);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JMenuItem clicked= (JMenuItem) e.getSource();

        if (clicked == openItem) {
            actionOpenFile();
        } else if (clicked == openFolderItem) {
            actionOpenFolder();
        } else if (clicked == resumeItem) {
            actionResume(); // todo may need  mainToolbar.setStateOfButtonsControl(false, false, false); // canceled
        }  else if (clicked == pauseItem) {
            actionPause();
        } else if (clicked == clearItem) {
            actionClear();
        } else if (clicked == reJoinItem) {
            actionReJoinFileParts();
        } else if (clicked == reDownloadItem) {
            actionReDownload();
        } else if (clicked == moveToQueueItem) {
    //        mainToolbarListener.preferencesEventOccured();
        } else if (clicked == removeFromQueueItem) {
    //        mainToolbarListener.preferencesEventOccured();
        } else if (clicked == propertiesItem) {
            actionProperties();
        }

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
}
