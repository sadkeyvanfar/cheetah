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
import gui.controller.DownloadManager;
import gui.download.DownloadAskDialog;
import gui.download.DownloadDialog;
import gui.listener.*;
import gui.model.DownloadAction;
import model.Download;
import model.DownloadRange;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import utils.FileUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DownloadPanel extends JPanel implements DownloadInfoListener, DownloadStatusListener, DownloadTablePanelListener {

    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private final Logger messageLogger = Logger.getLogger("message");

    private final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("messages/messages"); // NOI18N

    private DownloadManager downloadManager;

    // Currently selected download.
    private Download selectedDownload;

    private DownloadTablePanel downloadTablePanel;

    private List<DownloadDialog> downloadDialogs;

    private DownloadAskDialog downloadAskDialog;

    private List<String> fileExtensions;
    private DownloadCategory downloadCategory = DownloadCategory.ALL;

    // Flag for whether table selection is being cleared.
    private boolean clearing;

    private DownloadPanelListener downloadPanelListener;

    private JFrame parent;

    public List<Download> getDownloadList() {
        return downloadManager.getAllDownloads();
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

    public DownloadPanel(JFrame parent , DownloadManager downloadManager) {
        this.parent = parent;
        this.downloadManager = downloadManager;

        setLayout(new BorderLayout());

        downloadDialogs = new ArrayList<>();

        // Set up Downloads table.
        DownloadTableModel downloadsTableModel = new DownloadTableModel();
        downloadsTableModel.setDownloads(downloadManager.getAllDownloads()); //?
        this.downloadTablePanel = new DownloadTablePanel(downloadsTableModel);

        this.downloadTablePanel.setDownloadTablePanelListener(this);

        JScrollPane scrollPane = new JScrollPane(downloadTablePanel);
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        createDownloadDialogs(parent, downloadManager, downloadsTableModel);
    }

    private void createDownloadDialogs(JFrame parent, DownloadManager downloadManager, DownloadTableModel downloadsTableModel) {
        List<Download> downloadList = downloadManager.getAllDownloads();

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
    }

    private void calculateDownloaded(Download download) {
        int downloaded = 0;
        for (DownloadRange downloadRange : download.getDownloadRangeList()) {
            long rangeDownloaded = downloadRange.getDownloadRangeFile().length();
            downloaded += rangeDownloaded;
        }
        download.setDownloaded(downloaded);
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
        return downloadManager.getAllDownloads().size() + 1;
    }

    public void actionOpenFile() {
        downloadManager.openFile(selectedDownload.getDownloadPath() + File.separator + selectedDownload.getDownloadName());
    }

    public void actionOpenFolder() {
        downloadManager.openFile(selectedDownload.getDownloadPath().getPath());
    }

    // Pause the selected download.
    public void actionPause() {
        downloadManager.pauseDownload(selectedDownload);
    }

    // Resume the selected download.
    public void actionResume() {
      //  downloadManager.resumeDownload(selectedDownload);
        if (selectedDownload.getStatus() == DownloadStatus.CANCELLED || (selectedDownload.getStatus() == DownloadStatus.ERROR && selectedDownload.getDownloadRangeList().isEmpty())) {
            selectedDownload.removeDownloadInfo(this);
            deleteDownloadDialogByDownload(selectedDownload);

           // addDownload(selectedDownload);
            downloadManager.addDownload(selectedDownload);
        } else {
            downloadManager.resumeDownload(selectedDownload);
        }
    }

    // Cancel the selected download.
    public void actionPauseAll() {
        List<Download> downloadList = getDownloadList();
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
            downloadTablePanel.clearDownload(selectedDownload);
            if (downloadManager.getAllDownloads().contains(selectedDownload))
                downloadManager.remove(selectedDownload);
            clearing = false;

            DownloadDialog downloadDialog = getDownloadDialogByDownload(selectedDownload);
            downloadDialogs.remove(downloadDialog);
            downloadDialog.dispose();
            downloadDialog.removeDownloadInfoListener(this);

            downloadManager.deleteDownload(selectedDownload);

            try {
                FileUtils.forceDelete(new File(selectedDownload.getDownloadRangePath() + File.separator + selectedDownload.getDownloadName())); // todo must again
            } catch (IOException e) {
          //      e.printStackTrace();
            }

            selectedDownload = null;
        }
    }

    // Clear all completed downloads.
    public void actionClearAllCompleted() {
        int action = JOptionPane.showConfirmDialog(parent, "Do you realy want to delete all completed files?", "Confirm delete all", JOptionPane.OK_CANCEL_OPTION);
        if (action == JOptionPane.OK_OPTION) {
            List<Download> selectedDownloads = downloadTablePanel.getDownloadsByStatus(DownloadStatus.COMPLETE);

            clearing = true;
            downloadTablePanel.clearDownloads(selectedDownloads);
            clearing = false;

            try {
                for (Download download : selectedDownloads) {
                    if (selectedDownload == download)
                        selectedDownload = null;
                    DownloadDialog downloadDialog = getDownloadDialogByDownload(download);
                    downloadDialogs.remove(downloadDialog);
                    downloadDialog.removeDownloadInfoListener(this);
                    downloadDialog.dispose();
                    downloadManager.deleteDownload(download);
                    FileUtils.forceDelete(new File(download.getDownloadRangePath() + File.separator + download.getDownloadName())); // todo must again
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
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
              //  tableSelectionChanged();
            }
        }
    }

    public void actionProperties() {
        DownloadDialog downloadDialog = getDownloadDialogByDownload(selectedDownload);
        if (!downloadDialog.isVisible()) {
            downloadDialog.setVisible(true);
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
        downloadManager.saveDownload(download);
        downloadTablePanel.fireTableDataChanged();
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
        if (!downloadManager.getAllDownloads().contains(download)) {
            downloadManager.addDownload(download);
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
        for (Download download : downloadManager.getAllDownloads())
            for (String downloadPath : fileExtensions)
                if (FilenameUtils.getExtension(download.getDownloadName()).equals(downloadPath))
                    selectedDownloads.add(download);

        downloadTablePanel.setDownloads(selectedDownloads);
    }

    public void setDownloadsByDownloadPath(List<String> fileExtensions, DownloadCategory downloadCategory) { //todo use Sterategy pattern, have bad code
        this.fileExtensions = fileExtensions;
        this.downloadCategory = downloadCategory;
        List<Download> selectedDownloads = new ArrayList<>();
        for (Download download : downloadManager.getAllDownloads()) {
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

        downloadTablePanel.setDownloads(selectedDownloads);
    }

    @Override
    public void onMenuAction(Download download, DownloadAction action) {
        this.selectedDownload = download;

        switch (action) {
            case OPEN_FILE:
                actionOpenFile();
                break;
            case OPEN_FOLDER:
                actionOpenFolder();
                break;
            case RESUME:
                actionResume();
                break;
            case PAUSE:
                actionPause();
                break;
            case CLEAR:
                actionClear();
                break;
            case REJOIN_PARTS:
                actionReJoinFileParts();
                break;
            case REDOWNLOAD:
                actionReDownload();
                break;
            case MOVE_TO_QUEUE:
              //  downloadController.moveToQueue(download);
                break;
            case REMOVE_FROM_QUEUE:
              //  downloadController.removeFromQueue(download);
                break;
            case PROPERTIES:
                actionProperties();
                break;
            default:
                throw new IllegalArgumentException("Unknown action: " + action);
        }
    }

    @Override
    public void downloadSelected(Download download) {
        /* Unregister from receiving notifications
           from the last selected download. */
        if (selectedDownload != null)
            selectedDownload.deleteDownloadStatusListener(DownloadPanel.this);

        /* If not in the middle of clearing a download,
           set the selected download and register to
           receive notifications from it. */
        if (!clearing && downloadTablePanel.getSelectedRow() > -1) {
            selectedDownload = download;
            selectedDownload.addDownloadStatusListener(DownloadPanel.this);
            downloadPanelListener.stateChangedEventOccured(selectedDownload.getStatus());
        } else {
            downloadPanelListener.stateChangedEventOccured(null);
        }

        if (downloadPanelListener != null) {
            downloadPanelListener.downloadSelected(selectedDownload);
        }
    }

    @Override
    public void downloadDoubleClicked(Download download) {
        DownloadDialog downloadDialog = getDownloadDialogByDownload(selectedDownload);
        if (downloadDialog != null && !downloadDialog.isVisible()) {
            downloadDialog.setVisible(true);
        }
    }

    public void setStateOfButtonsControl(boolean b, boolean b1, boolean b2, boolean b3, boolean b4, boolean b5, boolean b6, boolean b7) {
        downloadTablePanel.setStateOfButtonsControl(b, b1, b2, b3, b4, b5, b6, b7);
    }
}
